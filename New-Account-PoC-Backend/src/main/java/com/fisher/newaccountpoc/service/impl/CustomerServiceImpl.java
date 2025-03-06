package com.fisher.newaccountpoc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fisher.newaccountpoc.entity.Customer;
import com.fisher.newaccountpoc.mapper.CustomerMapper;
import com.fisher.newaccountpoc.service.CustomerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Customer findByCitizenId(String citizenId) {

        QueryWrapper<Customer> queryWrapper = Wrappers.query();
        queryWrapper.eq("citizen_id", citizenId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean registerCustomer(Customer customer) {
        customer.setPasswordHash(passwordEncoder.encode(customer.getPasswordHash()));
        return this.save(customer);
    }
}
