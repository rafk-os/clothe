package com.example.backend.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Data
public class StatusForm {

    @Schema(example = "CLOSED")
    private String status;

    public StatusForm(){}

}
