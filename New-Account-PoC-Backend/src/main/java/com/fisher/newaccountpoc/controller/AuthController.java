package com.fisher.newaccountpoc.controller;

import com.fisher.newaccountpoc.common.util.JwtUtils;
import com.fisher.newaccountpoc.common.util.Result;
import com.fisher.newaccountpoc.common.util.ResultUtil;
import com.fisher.newaccountpoc.dto.CustomerDTO;
import com.fisher.newaccountpoc.dto.TellerDTO;
import com.fisher.newaccountpoc.dto.TokenUserDTO;
import com.fisher.newaccountpoc.entity.Customer;
import com.fisher.newaccountpoc.entity.Teller;
import com.fisher.newaccountpoc.service.CustomerService;
import com.fisher.newaccountpoc.service.TellerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.fisher.newaccountpoc.common.util.Constants.ROLE_CUSTOMER;
import static com.fisher.newaccountpoc.common.util.Constants.ROLE_TELLER;

@Slf4j
@RestController
@RequestMapping("/v1/backend/auth/")
public class AuthController {

    @Autowired
    CustomerService customerService;
    @Autowired
    TellerService tellerService;
    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("login")
    @Operation(summary = "Customer Login")
    public Result<String> login (@RequestBody CustomerDTO customerDTO){

        //1.verify login info
        Customer customer = customerService.findByCitizenId(customerDTO.getCitizenId());
        if (customer == null || customerService.validatePassword(customerDTO.getPassword(), customer.getPasswordHash())) {
            //2.if failed return
            return ResultUtil.fail("invalid citizen id or password");
        }

        //3.if success generate token
        TokenUserDTO tokenUserDTO = new TokenUserDTO();
        tokenUserDTO.setUserName(customerDTO.getCitizenId());
        tokenUserDTO.setRoles(ROLE_CUSTOMER);
        String token = jwtUtils.generateToken(tokenUserDTO);
        return ResultUtil.success(token);
    }

    @PostMapping("tellerLogin")
    @Operation(summary = "Teller Login")
    public Result<String> tellerLogin (@RequestBody TellerDTO tellerDTO){

        Teller teller = tellerService.findById(tellerDTO.getEmployeeId());
        if (teller == null || tellerService.validatePassword(tellerDTO.getPassword(), teller.getPasswordHash())) {
            //2.if failed return
            return ResultUtil.fail("invalid id or password");
        }
        //3.if success generate token
        TokenUserDTO tokenUserDTO = new TokenUserDTO();
        tokenUserDTO.setUserName(tellerDTO.getEmployeeId());
        tokenUserDTO.setRoles(ROLE_TELLER);
        String token = jwtUtils.generateToken(tokenUserDTO);
        return ResultUtil.success(token);
    }
}
