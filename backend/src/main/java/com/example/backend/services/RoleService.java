package com.example.backend.services;


import com.example.backend.model.Role;

public interface RoleService {
    Role findByName(String name);
}
