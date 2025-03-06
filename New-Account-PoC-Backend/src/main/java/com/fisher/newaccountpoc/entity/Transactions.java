package com.fisher.newaccountpoc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Transactions {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String referenceId;
    private String fromAccount;
    private String toAccount;
    private String code;
    private String channel;
    private LocalDate transactionDate;
    private LocalTime transactionTime;
    private BigDecimal amount;
    private BigDecimal fromAccountBalance;
    private BigDecimal toAccountBalance;
    private String initiatedBy;
    private String status;
    private String remark;
}
