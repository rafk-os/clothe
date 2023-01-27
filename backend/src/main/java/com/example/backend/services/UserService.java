package com.example.backend.services;

import com.example.backend.model.Item;
import com.example.backend.model.Role;
import com.example.backend.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User addNewUser(User user);
    List<User> findAll();
    User findOne(int id);
    User findOne(String username);
    User edit(int id, User data);
    void delete(int id);
    Set<Item> findAllFromCart(Integer id);
    Set<Role> getUserRoles(Integer id);
}
