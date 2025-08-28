package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);



    @Before
    public void setUp(){
        itemController  = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }
    @Test
    public void getItemsTest() {
        when(itemRepo.findAll()).thenReturn(new ArrayList<>());
        assertNotNull(itemController.getItems());
    }
    @Test
    public void getItemByIdTest() {
        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.ofNullable(null));
        assertNotNull(itemController.getItemById(1L));
    }
}
