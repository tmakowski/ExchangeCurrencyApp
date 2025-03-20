package com.example.ciient.model;

import lombok.Data;

import java.util.List;

@Data
public class NbpResponse {
    private String table;
    private String currency;
    private String code;
    private List<Rate> rates;
}
