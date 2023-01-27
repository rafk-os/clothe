package com.example.backend.controllers;


import com.example.backend.model.Orders;
import com.example.backend.services.OrderService;
import com.example.backend.utils.ChargeRequest;
import com.example.backend.utils.ChargeResponse;
import com.example.backend.utils.StatusForm;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @Operation(
            operationId = "createOrder",
            responses = {
                    @ApiResponse(responseCode = "201", description = "A Order was created", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
            }
    )
    @PostMapping(value = "/orders", produces = { "application/json" }, consumes = { "application/json" })
    public ResponseEntity<Orders> createOrder(@Valid @RequestBody Orders order) {
        Orders result = orderService.place(order);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(
            operationId = "changeOrderStatus",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A Order status was changed", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "403", description = "User not authorized to change status."),
                    @ApiResponse(responseCode = "404", description = "Order not found.")
            }
    )
    @PutMapping(value = "/orders/{id}/status", produces = { "application/json" }, consumes = { "application/json" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Orders> changeOrderStatus(@PathVariable("id") Integer id, @Valid @RequestBody StatusForm status) {
        Orders result = orderService.changeStatus(id, orderService.checkAndChangeIntoStatus(status.getStatus()));

        return ResponseEntity.ok().body(result);
    }

    @Operation(
            operationId = "getOrder",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order of given id", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "Order not found.")
            }
    )
    @GetMapping(value = "/orders/{id}", produces = { "application/json" })
    public ResponseEntity<Orders> getOrder(@PathVariable("id") Integer id) {
        Orders order = orderService.find(id);
        return ResponseEntity.ok().body(order);
    }

    @Operation(
            operationId = "createOrder",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A Order was created"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
            }
    )
    @GetMapping(value = "/orders", produces = { "application/json" })
    public ResponseEntity<List<Orders>> getOrders() {
        List<Orders> result = orderService.getAll();

        return ResponseEntity.ok().body(result);
    }

    @Operation(
            operationId = "chargeOrder",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A Charge of order was created"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
            }
    )
    @PostMapping( value = "/orders/{id}/charge", produces = { "application/json" })
    public ResponseEntity<ChargeResponse> chargeOrder(HttpServletRequest request, @PathVariable("id") Integer id )
            throws StripeException {
        Charge charge = orderService.charge(new ChargeRequest(id, ChargeRequest.Currency.PLN,request.getHeader("token")));
        ChargeResponse response = new ChargeResponse(charge.getId(),charge.getStatus(),charge.getBalanceTransaction());
        return ResponseEntity.ok().body(response);
    }

}
