package com.fisher.newaccountpoc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferMoneyDTO {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String remark;
    private String pin;
}
