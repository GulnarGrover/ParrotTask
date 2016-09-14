package com.parrotTest.ShowMetadataUtil;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.parrotTest.ShowMetadataUtil.Show.Aka;
/**
 * Class to be used to process show titles and retrieve the meta data where available
 * @author Gulnar Grover
 *
 */
public class TitleProcessor {

	//base url to request meta data for show title
	static String baseUrl = "http://api.tvmaze.com/singlesearch/shows?q=";
	//generic url to get akas for a show using the id
	static String akasURl = "http://api.tvmaze.com/shows/ID/akas";
	//default constructor 
	public TitleProcessor(){
	}
	/**
	 * Method to process show titles. 
	 * Takes a show title as a string, retrieves meta data from tvMaze and returns a show object with the available properties
	 * from the metadata
	 * @param title
	 * @return Show
	 */
	public Show processTitle(String title){

		Logger.getGlobal().info("Processing title - " + title);		
		Show show = new Show();
		show.setTitle(title);
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
		//Create url to get metadata for title
		String formattedTitle = title.replaceAll(" ", "%20");
		String urlMetaData = baseUrl.concat(formattedTitle);   	
		
		client = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(urlMetaData);
		
		
			response = client.execute(httpget);
			//store response as a json string
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			//if no data is returned for the show, set the name of the show to the title from the file
			if(json == null || json.equals("")){
				show.setTitle(title);
				throw new ShowNotFoundException();
			}
			else{
				Gson gson = new Gson();
				//parse the json to a Show object
				show = gson.fromJson(json, Show.class);				
				show.setTitle(title);
				//Create url to get the akas for the show
				String urlAkasData = akasURl.replace("ID", show.getId().toString()); 
				HttpGet httpget2 = new HttpGet(urlAkasData);
				response = client.execute(httpget2);
				String json2 = EntityUtils.toString(response.getEntity(), "UTF-8");
				//parse the json to an array of akas 
				Aka[] akas = gson.fromJson(json2, Aka[].class);
				//if any akas retrieved for the show
				if(akas.length > 0){
					show.setAkas(akas);
				}
			}
		} catch (ClientProtocolException e1) {
			Logger.getGlobal().warning("Unable to complete get request for - " + title);
		} catch (ParseException e) {
			Logger.getGlobal().warning("Error parsing json for " + title);
		} catch (IOException e) {
			Logger.getGlobal().warning("IO exception while processing - " + title);
		} catch (ShowNotFoundException e) {
			Logger.getGlobal().warning("Meta data not found for - " + title);
		}
		finally{			
			try {
				client.close();
				response.close();
			} catch (IOException e) {
				Logger.getGlobal().warning("Unable to close response");
			}
		}        
		return show;

	}
}
