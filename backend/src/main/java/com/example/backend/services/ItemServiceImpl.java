package com.example.backend.services;

import com.example.backend.exceptions.ConflictException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.model.Cart;
import com.example.backend.model.Item;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.ItemRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service("itemService")
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void delete(Integer id) {
        Item item;
        try {
            item = itemRepository.findById(id).orElseThrow();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "No item of id " + id + " found.");
        }
        for (Cart cart: item.getCarts())
            cart.getItems().remove(item);
        item.getCarts().clear();
        itemRepository.save(item);
        itemRepository.delete(item);

    }


    @Override
    public Item add(Item item) {
        try {
            String username =  SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
            item.setUser(userRepository.findByUsername(username).orElseThrow());
            item.setCarts(new HashSet<>());
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "Please login again. ");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item find(Integer id) {
        try {
            return itemRepository.findById(id).orElseThrow();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "No item of id " + id + " found.");
        }
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Cart addToCart( Integer itemId) {
        Integer cartId;
        try {
             cartId = userRepository.findByUsername( SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName()).orElseThrow().getId();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "Please login again. ");
        }
        Item item;
        Cart cart;
        try {
            item = itemRepository.findById(itemId).orElseThrow();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "No item of id " + itemId + " found.");
        }
        try {
            cart = cartRepository.findById(cartId).orElseThrow();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "No cart of id " + cartId + " found.");
        }
        cart.getItems().add(item);
        return cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(Integer itemId) {
        Integer cartId;
        try {
            cartId = userRepository.findByUsername( SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName()).orElseThrow().getId();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "Please login again. ");
        }
        Item item;
        Cart cart;
        try {
            item = itemRepository.findById(itemId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("No item of id " + itemId + " found.");
        }
        try {
            cart = cartRepository.findById(cartId).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("No cart of id " + cartId + " found.");
        }
        if (!cart.getItems().contains(item))
            throw new ConflictException("Item does not belong to this cart");

        item.getCarts().remove(cart);
        cart.getItems().remove(item);
        cartRepository.save(cart);
    }
}
