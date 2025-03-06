package com.fisher.newaccountpoc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fisher.newaccountpoc.common.util.JwtUtils;
import com.fisher.newaccountpoc.dto.NewAccountDto;
import com.fisher.newaccountpoc.dto.StatementDTO;
import com.fisher.newaccountpoc.entity.Account;
import com.fisher.newaccountpoc.entity.Transactions;
import com.fisher.newaccountpoc.mapper.AccountMapper;
import com.fisher.newaccountpoc.mapper.TransactionsMapper;
import com.fisher.newaccountpoc.service.AccountService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private static final Set<String> generatedAccounts = new HashSet<>();
    private static final Random random = new Random();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TransactionsMapper transactionsMapper;


    @Override
    @PreAuthorize("hasRole('TELLER')")
    public boolean createAccount(NewAccountDto newAccountDto) {

        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setCitizenId(newAccountDto.getCitizenId());
        account.setThaiName(newAccountDto.getThaiName());
        account.setEnName(newAccountDto.getEnName());
        account.setCurrentBalance(BigDecimal.valueOf(0.00));
        account.setPinHash(passwordEncoder.encode(newAccountDto.getPin()));
        return this.save(account);
    }

    @Override
    public Account viewAccountInfo(String token) {
        Claims claims = jwtUtils.parseToken(token);
        String citizenId = claims.getSubject();
        QueryWrapper<Account> queryWrapper = Wrappers.query();
        queryWrapper.eq("citizen_id", citizenId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Account getAccountById(String account_number) {
        QueryWrapper<Account> queryWrapper = Wrappers.query();
        queryWrapper.eq("account_number", account_number);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<StatementDTO> getStatement(String citizenId, int year ,int month, String pin) {

        //get account number by citizenId
        QueryWrapper<Account> queryWrapper = Wrappers.query();
        queryWrapper.eq("citizen_id", citizenId);
        Account account = this.getOne(queryWrapper);

        //check pin
        if(!verifyPin(pin, account.getPinHash())){
            return null;
        }
        //get statement from transaction
        List<Transactions> transactionsList = getTransactions(account.getAccountNumber(), year, month);
        List<StatementDTO> statementDTOList = new ArrayList<>();
        for (Transactions transactions : transactionsList ){

            StatementDTO statementDTO = getStatementDTO(transactions, account);
            statementDTOList.add(statementDTO);
        }
        return statementDTOList;
    }

    private List<Transactions> getTransactions(String accountNumber, int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        QueryWrapper<Transactions> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("from_account", accountNumber).or().eq("to_account", accountNumber))
                .ge("transaction_date", startDate)
                .lt("transaction_date", endDate);

        return transactionsMapper.selectList(queryWrapper);
    }

    private static StatementDTO getStatementDTO(Transactions transactions, Account account) {
        StatementDTO statementDTO = new StatementDTO();
        statementDTO.setDate(transactions.getTransactionDate());
        statementDTO.setTime(transactions.getTransactionTime());
        statementDTO.setChannel(transactions.getChannel());
        statementDTO.setAmount(transactions.getAmount());
        if (Objects.equals(account.getAccountNumber(), transactions.getFromAccount())) {
            statementDTO.setType("Debit");
            statementDTO.setBalance(transactions.getFromAccountBalance());
        } else {
            statementDTO.setType("Credit");
            statementDTO.setBalance(transactions.getToAccountBalance());
        }
        statementDTO.setRemark(transactions.getRemark());
        return statementDTO;
    }

    public boolean verifyPin(String rawPin, String pinHash) {
        return passwordEncoder.matches(rawPin, pinHash);
    }

    public static String generateUniqueAccountNumber() {
        while (true) {
            StringBuilder accountNumber = new StringBuilder();
            // 生成 7 位数字的账号
            for (int i = 0; i < 7; i++) {
                accountNumber.append(random.nextInt(10));
            }
            String account = accountNumber.toString();
            if (!generatedAccounts.contains(account)) {
                // 如果账号未被使用，则添加到已生成账号集合中
                generatedAccounts.add(account);
                return account;
            }
        }
    }
}
