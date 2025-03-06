package com.fisher.newaccountpoc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fisher.newaccountpoc.dto.DepositMoneyDTO;
import com.fisher.newaccountpoc.dto.TransferMoneyDTO;
import com.fisher.newaccountpoc.entity.Account;
import com.fisher.newaccountpoc.entity.Transactions;
import com.fisher.newaccountpoc.mapper.AccountMapper;
import com.fisher.newaccountpoc.mapper.TransactionsMapper;
import com.fisher.newaccountpoc.service.AccountService;
import com.fisher.newaccountpoc.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionsMapper, Transactions> implements TransactionService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private TransactionsMapper transactionsMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public boolean transferMoney(TransferMoneyDTO transferMoneyDTO) {
        String referenceId = UUID.randomUUID().toString();

        // check Pin
        if(!verifyPin(transferMoneyDTO.getFromAccountNumber(), transferMoneyDTO.getPin())){
            return false;
        }
        // check whether transaction exist.
        if (isTransactionExists(referenceId)) {
            return false;
        }

        // get from/to account
        Account fromAccount = accountService.getAccountById(transferMoneyDTO.getFromAccountNumber());
        Account toAccount = accountService.getAccountById(transferMoneyDTO.getToAccountNumber());

        if (fromAccount == null || toAccount == null || fromAccount.getCurrentBalance().compareTo(transferMoneyDTO.getAmount()) < 0) {
            return false;
        }

        // pre withhold money
        if (!preWithholdMoney(fromAccount, referenceId, transferMoneyDTO)) {
            return false;
        }

        try {
            // update balance
            fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(transferMoneyDTO.getAmount()));
            toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(transferMoneyDTO.getAmount()));

            // update balance if version match
            if (accountMapper.updateById(fromAccount) == 0 || accountMapper.updateById(toAccount) == 0) {
                cancelTransaction(referenceId);
                return false;
            }

            // confirm
            confirmTransaction(referenceId, fromAccount.getCurrentBalance(), toAccount.getCurrentBalance());
            return true;
        } catch (Exception e) {
            // if exception rollback money
            cancelTransaction(referenceId);
            return false;
        }
    }

    @Override
    public boolean depositMoney(DepositMoneyDTO depositMoneyDTO) {
        String referenceId = UUID.randomUUID().toString();

        // check whether transaction exist.
        if (isTransactionExists(referenceId)) {
            return false;
        }

        // get account
        Account account = accountService.getAccountById(depositMoneyDTO.getAccountNumber());

        if (account == null) {
            return false;
        }

        // pre withhold money
        if (!preWithholdMoney(account, referenceId, depositMoneyDTO)) {
            return false;
        }

        try {
            // update balance
            account.setCurrentBalance(account.getCurrentBalance().add(depositMoneyDTO.getAmount()));

            // update balance if version match
            if (accountMapper.updateById(account) == 0) {
                cancelTransaction(referenceId);
                return false;
            }

            // confirm
            confirmTransaction(referenceId, account.getCurrentBalance());
            return true;
        } catch (Exception e) {
            // if exception rollback money
            cancelTransaction(referenceId);
            return false;
        }
    }

    public boolean verifyPin(String fromAccountNumber, String pinHash) {
        return passwordEncoder.matches(pinHash, accountService.getAccountById(fromAccountNumber).getPinHash());
    }

    private boolean isTransactionExists(String referenceId) {
        LambdaQueryWrapper<Transactions> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Transactions::getReferenceId, referenceId);
        return transactionsMapper.selectCount(queryWrapper) > 0;
    }

    private boolean preWithholdMoney(Account account, String referenceId, TransferMoneyDTO transferMoneyDTO) {
        Transactions transaction = new Transactions();
        transaction.setReferenceId(referenceId);
        transaction.setFromAccount(account.getAccountNumber());
        transaction.setToAccount(transferMoneyDTO.getToAccountNumber());
        transaction.setCode("A0");
        transaction.setChannel("online");
        transaction.setAmount(transferMoneyDTO.getAmount());
        transaction.setFromAccountBalance(BigDecimal.valueOf(0.00));
        transaction.setToAccountBalance(BigDecimal.valueOf(0.00));
        transaction.setInitiatedBy(account.getThaiName());
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionTime(LocalTime.now());
        transaction.setStatus("PRE_WITHHELD");
        transaction.setRemark(transferMoneyDTO.getRemark());
        return transactionsMapper.insert(transaction) > 0;
    }

    private boolean preWithholdMoney(Account account, String referenceId, DepositMoneyDTO depositMoneyDTO) {
        Transactions transaction = new Transactions();
        transaction.setReferenceId(referenceId);
        transaction.setToAccount(account.getAccountNumber());
        transaction.setCode("A1");
        transaction.setChannel("Teller");
        transaction.setAmount(depositMoneyDTO.getAmount());
        transaction.setFromAccountBalance(BigDecimal.valueOf(0.00));
        transaction.setInitiatedBy(depositMoneyDTO.getInitiatedBy());
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionTime(LocalTime.now());
        transaction.setStatus("PRE_WITHHELD");
        transaction.setRemark(depositMoneyDTO.getRemark());
        return transactionsMapper.insert(transaction) > 0;
    }

    private void confirmTransaction(String referenceId, BigDecimal fromAccountBalance, BigDecimal toAccountBalance) {
        Transactions transaction = getTransactionByReferenceId(referenceId);
        if (transaction != null && "PRE_WITHHELD".equals(transaction.getStatus())) {
            transaction.setStatus("SUCCESS");
            transaction.setFromAccountBalance(fromAccountBalance);
            transaction.setToAccountBalance(toAccountBalance);
            transactionsMapper.updateById(transaction);
        }
    }
    private void confirmTransaction(String referenceId, BigDecimal toAccountBalance) {
        Transactions transaction = getTransactionByReferenceId(referenceId);
        if (transaction != null && "PRE_WITHHELD".equals(transaction.getStatus())) {
            transaction.setStatus("SUCCESS");
            transaction.setToAccountBalance(toAccountBalance);
            transactionsMapper.updateById(transaction);
        }
    }

    private void cancelTransaction(String referenceId) {
        Transactions transaction = getTransactionByReferenceId(referenceId);
        if (transaction != null && "PRE_WITHHELD".equals(transaction.getStatus())) {
            Account account = accountMapper.selectById(transaction.getFromAccount());
            account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount()));
            accountMapper.updateById(account);

            transaction.setStatus("CANCELLED");
            transactionsMapper.updateById(transaction);
        }
    }

    private Transactions getTransactionByReferenceId(String referenceId){
        LambdaQueryWrapper<Transactions> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Transactions::getReferenceId, referenceId);
        return transactionsMapper.selectOne(queryWrapper);
    }

}
