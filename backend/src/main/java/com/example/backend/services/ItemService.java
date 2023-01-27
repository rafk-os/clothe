package com.example.backend.services;

import com.example.backend.model.Cart;
import com.example.backend.model.Item;

import java.util.List;

public interface ItemService {
    void delete(Integer id);
    Item add(Item item);
    Item find(Integer id);
    List<Item> findAll();
    Cart addToCart(Integer itemId);
    void removeFromCart( Integer itemId);
}
