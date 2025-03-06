package com.fisher.newaccountpoc.controller;


import com.fisher.newaccountpoc.common.util.Result;
import com.fisher.newaccountpoc.common.util.ResultUtil;
import com.fisher.newaccountpoc.dto.DepositMoneyDTO;
import com.fisher.newaccountpoc.dto.TransferMoneyDTO;
import com.fisher.newaccountpoc.entity.Account;
import com.fisher.newaccountpoc.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/backend/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("deposit")
    @Operation(summary = "money deposit by teller")
    @PreAuthorize("hasRole('TELLER')")
    public Result<Account> deposit (@RequestBody DepositMoneyDTO depositMoneyDTO){
        if(transactionService.depositMoney(depositMoneyDTO)){
            return ResultUtil.success();
        }
        return ResultUtil.fail("deposit money error");
    }
    @PostMapping("transfer")
    @Operation(summary = "money transfer by customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<Account> transfer (@RequestBody TransferMoneyDTO transferMoneyDTO){
        if(transactionService.transferMoney(transferMoneyDTO)){
            return ResultUtil.success();
        }
        return ResultUtil.fail("transfer money error");
    }
}
