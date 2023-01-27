package com.example.backend.utils;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {

    public enum Currency {
        EUR, USD, PLN;
    }

    @NotEmpty(message = "Please provide a order id")
    private int id;
    private Currency currency;
    @NotEmpty(message = "Please provide a stripeToken")
    private String token;
}