package com.example.backend.services;

import com.example.backend.model.Orders;
import com.example.backend.model.Status;
import com.example.backend.utils.ChargeRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

public interface OrderService {
    Orders place(Orders order);

    Orders changeStatus(Integer id, Status status);

    Orders find(Integer id);

    Status checkAndChangeIntoStatus(String status);

    List<Orders> getAll();

   Charge charge(ChargeRequest chargeRequest)  throws AuthenticationException, StripeException;
}
