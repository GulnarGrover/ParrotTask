package com.parrotTest.ShowMetadataUtil;

import java.sql.Timestamp;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
/**
 * A modification of the Show class to the format that it should be saved to the DynamoDB table
 * @author Gulnar Grover
 *
 */
@DynamoDBTable(tableName="ddb-table-glt67gy")
public class DynamoDBShow {

	private Integer id;
	private Double rating;
	private String title;
	private Set<String> genres;
	private Set<String> akas;
	private String lang;
	private String timestamp;
	
	@DynamoDBHashKey(attributeName="title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@DynamoDBRangeKey(attributeName="timestamp")
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp() {
		this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	@DynamoDBAttribute(attributeName="Id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@DynamoDBAttribute(attributeName="Rating")
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	@DynamoDBAttribute(attributeName="Genres")
	public Set<String> getGenres() {
		return genres;
	}
	public void setGenres(Set<String> genres) {
		this.genres = genres;
	}
	@DynamoDBAttribute(attributeName="Akas")
	public Set<String> getAkas() {
		return akas;
	}
	public void setAkas(Set<String> akas) {
		this.akas = akas;
	}
	@DynamoDBAttribute(attributeName="Lang")
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}	
}
