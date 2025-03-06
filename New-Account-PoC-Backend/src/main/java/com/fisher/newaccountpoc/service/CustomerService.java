package com.fisher.newaccountpoc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fisher.newaccountpoc.entity.Customer;

public interface CustomerService extends IService<Customer> {

    Customer findByCitizenId(String CitizenId);
    boolean validatePassword(String rawPassword, String encodedPassword);
    boolean registerCustomer(Customer customer);

}
