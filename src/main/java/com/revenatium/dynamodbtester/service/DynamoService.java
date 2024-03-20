package com.revenatium.dynamodbtester.service;

import java.net.URISyntaxException;

public interface DynamoService {

    Boolean itemExists(String tableName, String artist) throws URISyntaxException;

    void saveItem(String tableName, String artist, String albumTitle, String awards, String songTitle) throws URISyntaxException;

    String getItemAlbumTitle(String tableName, String artist) throws URISyntaxException;

    void deleteItem(String tableName, String artist) throws URISyntaxException;
}
