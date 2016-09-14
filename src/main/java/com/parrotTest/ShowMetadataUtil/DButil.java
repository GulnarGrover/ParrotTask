package com.parrotTest.ShowMetadataUtil;

import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * Class used to interact with AWS DynamoDB using the aws sdk for java.
 * @author Gulnar Grover
 *
 */
public class DButil {

	protected AmazonDynamoDB dynamoDB;
	/**
	 * Default constructor for DButil class. Takes the awsAccessKey and awsSecretKey as parameters.
	 * Creates a new AmazonDynamoDBClient using the credentials provided 
	 * @param awsAccessKey
	 * @param awsSecretKey
	 */
	public DButil(String awsAccessKey, String awsSecretKey){
		AWSCredentials awsCredentials = new AWSCredentials() {

			@Override
			public String getAWSSecretKey() {
				return awsSecretKey;
			}

			@Override
			public String getAWSAccessKeyId() {
				return awsAccessKey;
			}
		};
		dynamoDB = new AmazonDynamoDBClient(awsCredentials);
	}
	/**
	 * Method to put items into the dynamoDB table.
	 * Takes a show as parameter, transforms to a DBShow and saves it to table using DynamoDBMapper class
	 * @param show
	 */
	public void putItem(Show show){
		try{

			DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);

			DynamoDBShow item = show.transformtoDBShow();
			item.setTimestamp();
			mapper.save(item);	

		}
		catch (Exception ex){			
			Logger.getGlobal().warning("Exception while putting " + show.getTitle());

		}
	}
}
