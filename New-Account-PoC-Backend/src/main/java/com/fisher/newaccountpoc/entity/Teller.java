package com.fisher.newaccountpoc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Teller {
    @TableId(value = "employee_id")
    private String employeeId;
    private String display_name;
    private String passwordHash;
}
