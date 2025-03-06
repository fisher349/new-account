package com.fisher.newaccountpoc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fisher.newaccountpoc.dto.NewAccountDto;
import com.fisher.newaccountpoc.dto.StatementDTO;
import com.fisher.newaccountpoc.entity.Account;

import java.util.List;

public interface AccountService extends IService<Account> {

    boolean createAccount(NewAccountDto newAccountDto);
    Account viewAccountInfo(String token);
    Account getAccountById(String account_number);
    List<StatementDTO> getStatement(String citizenId, int year ,int month, String pin);
}
