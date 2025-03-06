package com.fisher.newaccountpoc.controller;

import com.fisher.newaccountpoc.common.util.Result;
import com.fisher.newaccountpoc.common.util.ResultUtil;
import com.fisher.newaccountpoc.dto.NewAccountDto;
import com.fisher.newaccountpoc.dto.StatementDTO;
import com.fisher.newaccountpoc.entity.Account;
import com.fisher.newaccountpoc.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/v1/backend/Account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    private static final String BEARER_PREFIX = "Bearer ";


    @PostMapping("newAccount")
    @PreAuthorize("hasRole('TELLER')")
    @Operation(summary = "Teller create new account ")
    public Result<Account> newAccountImplement (@RequestBody NewAccountDto newAccountDto){
        return accountService.createAccount(newAccountDto) ? ResultUtil.success() : ResultUtil.fail("new Account failed");
    }

    @GetMapping("viewAccountInfo")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer view their account info")
    public Result<Account> viewAccountInfo (@RequestHeader("Authorization") String authorizationHeader){

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        return ResultUtil.success(accountService.viewAccountInfo(token));
    }

    @GetMapping("statement")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer view their statement for specified month")
    public Result<List<StatementDTO>> statement (@RequestParam String citizenId,
                                                 @RequestParam int year,
                                                 @RequestParam int month,
                                                 @RequestParam String pin){

        List<StatementDTO> statementDTOList = accountService.getStatement(citizenId,year,month,pin);
        if(statementDTOList == null) {
            return ResultUtil.fail("Pin not match");
        }

        return ResultUtil.success(statementDTOList);
    }

    @GetMapping("/getAccount")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "get account info by accountNumber")
    public Result<Account> getAccount(@RequestParam String accountNumber){
        return ResultUtil.success(accountService.getAccountById(accountNumber));
    }


}
