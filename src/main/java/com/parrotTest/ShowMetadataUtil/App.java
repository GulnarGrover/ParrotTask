package com.parrotTest.ShowMetadataUtil;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



/**
 * Main class to run parrotTest. Loads a list of show titles from a file, splits that list into a list of lists of size equal to the 
 * number of parrallel streams set in the properties file. Then uses parallel streams to get meta data for the titles from tvmaze and uses 
 * the aws sdk for java to store show details to a dynamo db table. 
 * @author Gulnar Grover
 *
 */

public class App 
{	//Logger to be used by all classes
	static Logger logger = Logger.getGlobal();
	//Level for logger
	static String loggerLevel = "WARNING";
	//aws credentials
	static String awsCredentialsPath = null;
	static String awsAccessKey = null;
	static String awsSecretKey = null;

	//Path where to store the log file
	static String logFilePath = null;
	//number of parallel streams to process titles
	static Integer numParallelStream = null;
	//File path for the titles file
	

	/**
	 * The main method of the App class. It takes an optional argument for the path to the awsCredentials.properties file else looks for it at root of project. The configurations are provided through a property file 
	 * placed in the root of the project.
	 * @param args
	 */
	public static void main( String[] args ) 
	{
		if(args.length > 0){
			awsCredentialsPath = args[0];			
		}
		else{
			awsCredentialsPath = "AwsCredentials.properties";
		}
		//call method to load config properties and credentials for aws
		loadProperties();
		//call method to configure logger to write to specified file
		configureLogger();

		//List to store the titles read from file
		List<String> titlesList = null;
		try{
			InputStream titles = App.class.getClassLoader().getResourceAsStream("titles.txt");	
			titlesList = IOUtils.readLines(titles, "UTF-8");
		} catch (IOException e) {
			logger.severe("Unable to read lines from the titles file");
		}
		logger.info("Number of titles to process - " + titlesList.size());
		System.out.println("Number of titles to process - " + titlesList.size());
		
		//call the breakList method to get a list of lists of titles
		List<List<String>> listOfTitleLists = breakList(titlesList, numParallelStream);
		
		TitleProcessor processor = new TitleProcessor();
		DButil dbutil = new DButil(awsAccessKey, awsSecretKey);
		
		//number of lists of titles that make 5% of the total number of lists
		int fivePercent = titlesList.size()/(20*numParallelStream);
		int c = 0;		
		System.out.println("Starting processing. Each '#' represents ~5% progress");
		for (List<String> showList : listOfTitleLists) {
			if(++c % fivePercent == 0){
				System.out.print('#');
			}
			//the process title method returns a show which is then put on the db by using dbUtil
			showList.parallelStream().forEach(t -> dbutil.putItem((processor.processTitle(t))));			
		}
		System.out.println(" Processing complete.");
	}
	/**
	 * Takes a list of show titles and returns a list of lists of titles of size specified by the number of parallel streams set 
	 * in the config file
	 * @param titlesList
	 * @param numParrallelStreams
	 * @return
	 */
	static List<List<String>> breakList(List<String> titlesList, int numParrallelStreams){
		List<List<String>> listOfTitleLists = new ArrayList<List<String>>();
		List<String> tempList = new ArrayList<String>();
		for(int i = 0; i<titlesList.size(); i++){
			tempList.add(titlesList.get(i)); 
			if((i+1)%numParrallelStreams == 0){
				listOfTitleLists.add(tempList);
				tempList = new ArrayList<String>();
			}
		}
		if(titlesList.size()%numParrallelStreams != 0){
			listOfTitleLists.add(tempList);
		}
		return listOfTitleLists;
	}

	/**
	 * Loads config properties from config.properties file and loads credentials from AwsCredentials.properties file
	 */
	static void loadProperties(){
		Properties prop = new Properties();
		InputStream input = null;

		//block to load config properties
		try {			
			input = App.class.getClassLoader().getResourceAsStream("config.properties");	
			prop.load(input);
			//load properties from config file
			logFilePath = prop.getProperty("logFilePath");
			
			numParallelStream = Integer.parseInt(prop.getProperty("numParallelStream"));
			//check that number of streams is not less than 1 else set to default of 5
			if(numParallelStream < 1){
				numParallelStream = 5;
				Logger.getGlobal().config("Number of streams below 1 - using default of 5");
			}
			if(prop.containsKey(loggerLevel)){
				loggerLevel = prop.getProperty("loggerLevel");
			}
		} catch (IOException ex) {
			Logger.getGlobal().warning("Error loading properties file. Using default values.");
		} catch (NumberFormatException ex){
			numParallelStream = 5;
			Logger.getGlobal().config("Number of streams below 1 - using default of 5");
		} finally {
			if(logFilePath == null){
				logFilePath = "log.txt";
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.getGlobal().warning("Error closing input stream");
				}
			}
		}
		//block to load aws credentials
		try {			
			input = new FileInputStream(awsCredentialsPath);	
			prop.load(input);
			//load properties from awsCredentials file
			awsAccessKey = prop.getProperty("accessKey");
			awsSecretKey = prop.getProperty("secretKey");

		} catch (IOException ex) {
			Logger.getGlobal().severe("Error loading credentials file.");
		} finally {
			if(awsAccessKey == null || awsSecretKey == null){
				Logger.getGlobal().severe("Error loading credentials for aws.");
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.getGlobal().warning("Error closing input stream");
				}
			}
		}
	}
	/**
	 * Configures the logger print to path specified in the config file and not print to console
	 */
	static void configureLogger(){

		FileHandler fh;  

		try {  

			// This block configures the logger with handler and formatter  
			fh = new FileHandler(logFilePath);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.parse(loggerLevel));
		} catch (SecurityException e) {  
			logger.warning("Security violation while configuring logger");  
		} catch (IOException e) {  
			logger.warning("Unable to write log to the path specified");  
		}  
	}
}
