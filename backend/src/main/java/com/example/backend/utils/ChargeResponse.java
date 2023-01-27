package com.example.backend.utils;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChargeResponse {

    String chargeId;
    String chargeStatus;
    String balanceTransaction;
}
