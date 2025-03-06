package com.fisher.newaccountpoc.service;


import com.fisher.newaccountpoc.dto.DepositMoneyDTO;
import com.fisher.newaccountpoc.dto.TransferMoneyDTO;
import com.fisher.newaccountpoc.entity.Transactions;

import java.util.List;

public interface TransactionService {
    boolean verifyPin(String fromAccountNumber, String pin);
    boolean transferMoney(TransferMoneyDTO transferMoneyDTO);
    boolean depositMoney(DepositMoneyDTO depositMoneyDTO);
}
