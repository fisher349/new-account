package com.fisher.newaccountpoc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    @TableId(value = "account_number")
    private String accountNumber;
    private String citizenId;
    private String thaiName;
    private String enName;
    private BigDecimal currentBalance;
    @Version
    private Integer version;
    private String pinHash;
}
