package com.fisher.newaccountpoc.controller;


import com.fisher.newaccountpoc.common.util.Result;
import com.fisher.newaccountpoc.common.util.ResultUtil;
import com.fisher.newaccountpoc.entity.Customer;
import com.fisher.newaccountpoc.entity.Teller;
import com.fisher.newaccountpoc.service.CustomerService;
import com.fisher.newaccountpoc.service.TellerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/backend/")
public class RegisterController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private TellerService tellerService;

    @PostMapping("register")
    @Operation(summary = "Person register to customer")
    public Result<String> register(@RequestBody Customer customer) {

        boolean result = customerService.registerCustomer(customer);
        return result ? ResultUtil.success("register success") : ResultUtil.fail("register failed");
    }

    @PostMapping("register/teller")
    @Operation(summary = "create teller")
    public Result<String> createTeller(@RequestBody Teller teller) {
        boolean result = tellerService.createTeller(teller);
        return result ? ResultUtil.success("register success") : ResultUtil.fail("register failed");
    }
}
