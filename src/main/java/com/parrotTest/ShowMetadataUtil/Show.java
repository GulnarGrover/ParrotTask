package com.parrotTest.ShowMetadataUtil;

import java.util.HashSet;
import java.util.Set;
/**
 * Class to represent a show with the properties that are required to be saved to the DynamoDB table
 * @author Gulnar Grover
 *
 */
public class Show {
	/**
	 * Represents the rating for the show which is retrieved as an object from the json returned by get request to tvMaze
	 * Only stores the average property from the object
	 * @author gulnar
	 *
	 */
	class Rating{
		protected Double average;

		public Rating(){
		}
		public Double getAverage() {

			return average;
		}

		public void setAverage(Double average) {
			this.average = average;
		}	

	}
	/**
	 * Represents the aka for the show which is retrieved as an object from the json returned by get request to tvMaze.
	 * Only stores the name property of the object
	 * @author gulnar
	 *
	 */
	class Aka{
		//Alternate name for the show
		protected String name;

		public Aka(){
		}
		//getter and setter for name of aka
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}	

	}
	//name of the show
	private String name;
	//show id
	private Integer id;
	//show rating saved as an object with property average
	private Rating rating;
	//Genres of show stored as a set
	private	Set<String> genres;
	//Array of akas with property name 
	private Aka[] akas;
	//the language the show is in
	private String language;
	//default constructor for the show object
	public Show(){

	}
	
	public String getTitle() {
		return name;
	}

	public void setTitle(String title) {
		this.name = title;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * getter for rating object
	 * if no rating object is present for the show
	 *  creates a new rating object with average property set to null
	 *  This is to prevent nullPointer exception when getting the rating for the show
	 * @return Rating
	 */
	public Rating getRating() {
		if(this.rating != null){
			return rating;
		}
		else{
			this.rating = new Rating();
			this.rating.average = null;
			return rating;
		}
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}
	/**
	 * getter for the genres of the show
	 * returns a set containing the genres as strings
	 * If no genres are present returns null rather than empty set 
	 * This is to make it compatible with dynamoDB no empty set policy
	 * @return Set<String>
	 */
	public Set<String> getGenres() {
		if(genres != null && genres.size() > 0){
			return genres;
		}
		else{
			return null;
		}
	}

	public void setGenres(Set<String> genres) {
		this.genres = genres;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String lang) {
		this.language = lang;
	}
	public Aka[] getAkas() {
		return akas;
	}
	/**
	 * Method to get the names of the akas as a set of strings
	 * Iterates over the akas and stores the names to a set
	 * If no akas present returns null
	 * This is to make it compatible with dynamoDB no empty set policy
	 * @return Set<String>
	 */
	public Set<String> getAkasAsSet(){
		if(this.akas != null &&  this.akas.length > 0 ){
			Set<String> akaSet = new HashSet<String>();		
			for (Aka aka : this.akas) {
				akaSet.add(aka.name);
			}

			return akaSet;
		}
		else return null;

	}
	public void setAkas(Aka[] akas) {
		this.akas = akas;
	}
	/**
	 * Transforms the show object to a DynamoDB show object
	 * which is compatible with the dynamoDB table made for this project
	 * @return DynamoDBShow
	 */
	public DynamoDBShow transformtoDBShow(){
		DynamoDBShow dbShow = new DynamoDBShow();
		
		dbShow.setTitle(name);
		
		dbShow.setId(id);
		
		dbShow.setAkas(this.getAkasAsSet());
		
		dbShow.setGenres(this.getGenres());
		
		dbShow.setRating(this.getRating().average);
		
		dbShow.setLang(language);
		
		return dbShow;
	}
}
