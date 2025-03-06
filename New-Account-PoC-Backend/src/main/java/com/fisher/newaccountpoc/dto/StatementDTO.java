package com.fisher.newaccountpoc.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class StatementDTO {
    private LocalDate date;
    private LocalTime time;
    private String type;
    private String channel;
    private BigDecimal amount;
    private BigDecimal balance;
    private String remark;
}
