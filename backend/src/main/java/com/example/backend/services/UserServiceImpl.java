package com.example.backend.services;

import com.example.backend.exceptions.ConflictException;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.model.Cart;
import com.example.backend.model.Item;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.utils.RoleNames;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(RoleService roleService, UserRepository userRepository,
                           CartRepository cartRepository, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
             user = userRepository.findByUsername(username).orElseThrow();
        }
        catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(roleEntity -> {
           authorities.add(new SimpleGrantedAuthority("ROLE_" + roleEntity.getName()));
        });
        return authorities;
    }

    @Override
    public User addNewUser(User user) {
        if( userRepository.findByUsername(user.getUsername()).isPresent())
            throw new ConflictException("Username is already taken.");

        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new ConflictException("Email is already taken.");

        Cart cart = new Cart();
        cart.setItems(new HashSet<>());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByName(RoleNames.ROLE_USER));
        user.setRoles(roles);
        user.setItems(new HashSet<>());
        user.setOrders(new HashSet<>());
        user = userRepository.save(user);
        cart.setUser(user);
        user.setCart(cart);
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOne(int id) {
        try {
            return userRepository.findById(id).orElseThrow();
        }
        catch (NoSuchElementException e) {
            throw new ResourceNotFoundException( "No user of id " + id + " found.");
        }
    }

    @Override
    public User findOne(String username) {
        try {
            return userRepository.findByUsername(username).orElseThrow();
        }
        catch (NoSuchElementException e) {
            throw new ResourceNotFoundException( "No user of username " + username + " found.");
        }
    }

    @Override
    public User edit(int id, User data) {
        User target;
        try {
           target = userRepository.findById(id).orElseThrow();

        }
        catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("No user of id " + id + " found.");
        }

        if( userRepository.findByUsername(data.getUsername()).isPresent())
            throw new ConflictException("Username is already taken.");


        if(userRepository.findByEmail(data.getEmail()).isPresent())
            throw new ConflictException("Email is already taken.");

        target.setUsername(data.getUsername());
        target.setPassword(bCryptPasswordEncoder.encode(data.getPassword()));
        target.setEmail(data.getEmail());
        target.setFirstName(data.getFirstName());
        target.setLastName(data.getLastName());

        return userRepository.save(target);
    }

    @Override
    public void delete(int id) {
        User user;
        try {
             user = userRepository.findById(id).orElseThrow();
        }
        catch (Exception e){
            throw new ResourceNotFoundException("No user of id " + id + " found.");
        }
        userRepository.delete(user);


    }

    @Override
    public Set<Item> findAllFromCart(Integer id) {
        Cart cart;
        try {
            cart = cartRepository.findById(id).orElseThrow();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "No cart of id " + id + " found.");
        }
        return cart.getItems();
    }

    @Override
    public Set<Role> getUserRoles(Integer id) {
        User user;
        try {
            user = userRepository.findById(id).orElseThrow();
        }
        catch (Exception e){
            throw new ResourceNotFoundException("No user of id " + id + " found.");
        }
        return user.getRoles();
    }
}
