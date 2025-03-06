package com.fisher.newaccountpoc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fisher.newaccountpoc.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
