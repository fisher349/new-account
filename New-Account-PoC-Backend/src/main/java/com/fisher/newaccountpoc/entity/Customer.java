package com.fisher.newaccountpoc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Customer {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String citizenId;
    private String thaiName;
    private String enName;
    private String passwordHash;
}
