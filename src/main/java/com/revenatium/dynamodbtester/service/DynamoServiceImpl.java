package com.revenatium.dynamodbtester.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DynamoServiceImpl implements DynamoService {

    private DynamoDbClient getClient() throws URISyntaxException {
        DynamoDbClientBuilder dynamoClientBuilder = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create("dynamo_local"))
                .endpointOverride(new URI("http://localhost:8000"));

        return dynamoClientBuilder.build();
    }

    @Override
    public Boolean itemExists(String tableName, String artist) throws URISyntaxException {
        DynamoDbClient client = getClient();

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("Artist", AttributeValue.builder()
                .s(artist)
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        GetItemResponse item = client.getItem(request);
        client.close();

        return item.hasItem();
    }

    @Override
    public void saveItem(String tableName, String artist, String albumTitle, String awards, String songTitle) throws URISyntaxException {
        DynamoDbClient client = getClient();

        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("Artist", AttributeValue.builder().s(artist).build()); //key val
        itemValues.put("AlbumTitle", AttributeValue.builder().s(albumTitle).build());
        itemValues.put("Awards", AttributeValue.builder().s(awards).build());
        itemValues.put("SongTitle", AttributeValue.builder().s(songTitle).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

//        if (instance.managedLangs.size() > 0)
//                item.withStringSet("managedLangs", new HashSet<String>(Arrays.asList(instance.managedLangs)))
//         itemValues.put("managedLangs", AttributeValue.builder().ss(new HashSet<String>(Arrays.asList(instance.managedLangs))).build());

        try {
            PutItemResponse response = client.putItem(request);
            System.out.println(tableName + " was successfully updated. The request id is "
                    + response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            client.close();
        }
    }
    @Override
    public String getItemAlbumTitle(String tableName, String artist) throws URISyntaxException {

        DynamoDbClient client = getClient();

        // Set up an alias for the partition key name in case it's a reserved word.
        HashMap<String, String> attrNameAlias = new HashMap<String, String>();
        attrNameAlias.put("#Artist", "Artist");
//        attrNameAlias.put("#id", "externalSystem-externalHotelId");

        // Set up mapping of the partition name with the value.
        HashMap<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":Artist", AttributeValue.builder().s(artist).build());
//        attrValues.put(":id", getAccountSwitchId(system, hotelCode));

        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#Artist = :Artist")
       //        def querySpec = new QuerySpec().withKeyConditionExpression("#id = :id").withNameMap(nameMap)
//                .withValueMap(valueMap);
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();

        try {
            AtomicReference<String> albumTitle = new AtomicReference<>("");
            QueryResponse response = client.query(queryReq);
            response.items().forEach(item ->
                    albumTitle.set(item.get("AlbumTitle").s())
            );

            return albumTitle.get();
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        } finally {
            client.close();
        }

        return null;
    }

    public void deleteItem(String tableName, String artist) throws URISyntaxException {
        DynamoDbClient client = getClient();

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("Artist", AttributeValue.builder()
                .s(artist)
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(keyToGet)
                .build();

        try {
            client.deleteItem(deleteReq);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        } finally {
            client.close();
        }
    }
}
