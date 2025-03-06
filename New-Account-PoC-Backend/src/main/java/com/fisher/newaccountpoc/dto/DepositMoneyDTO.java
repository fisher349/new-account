package com.fisher.newaccountpoc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositMoneyDTO {
    private String AccountNumber;
    private BigDecimal amount;
    private String initiatedBy;
    private String remark;

}
