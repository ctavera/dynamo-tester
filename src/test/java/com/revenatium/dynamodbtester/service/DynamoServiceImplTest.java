package com.revenatium.dynamodbtester.service;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DynamoServiceImplTest {

    @Autowired
    DynamoService dynamoService;

    @Test
    void testCRUD() throws URISyntaxException {

        dynamoService.saveItem("Music", "Korn", "See You On The Other Side", "1000", "Comming Undone");
        Assertions.assertTrue(dynamoService.itemExists("Music", "Korn"));
        Assertions.assertEquals("See You On The Other Side", dynamoService.getItemAlbumTitle("Music", "Korn"));
        dynamoService.deleteItem("Music", "Korn");
        Assertions.assertFalse(dynamoService.itemExists("Music", "Korn"));
    }

}