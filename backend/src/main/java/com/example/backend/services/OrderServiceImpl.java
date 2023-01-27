package com.example.backend.services;

import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.model.Orders;
import com.example.backend.model.Status;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.utils.ChargeRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;
    private int amount;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Orders place(Orders order) {
        try {
            String username =  SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
            order.setUser(userRepository.findByUsername(username).orElseThrow());
            order.setStatus(Status.OPEN);
            order.setItems(new HashSet<>());
            order.getItems().addAll(order.getUser().getCart().getItems());
            order.getUser().getCart().getItems().clear();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "Please login again. ");
        }
        return orderRepository.save(order);
    }

    @Override
    public Orders changeStatus(Integer id, Status status) {
        Orders order;
        try {
            order =  orderRepository.findById(id).orElseThrow();
        }
        catch (NoSuchElementException e){
            throw new ResourceNotFoundException( "No order of id " + id + " found.");
        }
        order.setStatus(status);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Orders find(Integer id) {
        try {
            return orderRepository.findById(id).orElseThrow();
        }
        catch (NoSuchElementException e) {
            throw new ResourceNotFoundException( "No order of id " + id + " found.");
        }
    }

    @Override
    public Status checkAndChangeIntoStatus(String status) {
        Status newStatus;
        try {
            newStatus = Status.valueOf(status);
        }
        catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException( "No status of name " + status + " found.");
        }
        return newStatus;
    }

    public Charge charge(ChargeRequest chargeRequest)
            throws AuthenticationException, StripeException {
        Orders order;
        try {
        order = orderRepository.findById(chargeRequest.getId()).orElseThrow();
        }
        catch (NoSuchElementException e) {
            throw new ResourceNotFoundException( "No order of id " + chargeRequest.getId() + " found.");
        }
        Map<String, Object> chargeParams = new HashMap<>();
        amount = 0;
         order.getItems().forEach((item) -> {
             this.amount+=item.getPrice();
         });

        chargeParams.put("amount", (int)(amount * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", order.getDescription());
        chargeParams.put("source", chargeRequest.getToken());
        Charge charge = Charge.create(chargeParams);
        order.setStatus(Status.PREPARING);
        orderRepository.save(order);
        return charge;
    }

    @Override
    public List<Orders> getAll() {
        return orderRepository.findAll();
    }
}
