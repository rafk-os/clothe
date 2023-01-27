package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public enum Status {
    OPEN, CLOSED, DELIVERING, CANCELED,PREPARING
}