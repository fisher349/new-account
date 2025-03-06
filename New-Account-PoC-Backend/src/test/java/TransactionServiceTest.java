import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fisher.newaccountpoc.dto.DepositMoneyDTO;
import com.fisher.newaccountpoc.dto.TransferMoneyDTO;
import com.fisher.newaccountpoc.entity.Account;
import com.fisher.newaccountpoc.entity.Transactions;
import com.fisher.newaccountpoc.mapper.AccountMapper;
import com.fisher.newaccountpoc.mapper.TransactionsMapper;
import com.fisher.newaccountpoc.service.AccountService;
import java.math.BigDecimal;

import com.fisher.newaccountpoc.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionsMapper transactionsMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testTransferMoneySuccess() {
        TransferMoneyDTO dto = new TransferMoneyDTO();
        dto.setFromAccountNumber("0000001");
        dto.setToAccountNumber("0000002");
        dto.setAmount(BigDecimal.TEN);
        dto.setPin("1234");
        dto.setRemark("Test Transfer");

        // 构造转出账户，余额足够且 PIN 正确
        Account fromAccount = new Account();
        fromAccount.setAccountNumber("0000001");
        fromAccount.setCurrentBalance(BigDecimal.valueOf(100));
        fromAccount.setPinHash(passwordEncoder.encode("1234"));

        Account toAccount = new Account();
        toAccount.setAccountNumber("0000002");
        toAccount.setCurrentBalance(BigDecimal.valueOf(50));

        when(accountService.getAccountById("0000001")).thenReturn(fromAccount);
        when(accountService.getAccountById("0000002")).thenReturn(toAccount);

        // 模拟事务不存在
        when(transactionsMapper.selectCount(any())).thenReturn(0L);
        // 模拟预扣款插入成功
        when(transactionsMapper.insert(any(Transactions.class))).thenReturn(1);
        // 模拟账户更新成功
        when(accountMapper.updateById(fromAccount)).thenReturn(1);
        when(accountMapper.updateById(toAccount)).thenReturn(1);
        // 模拟确认事务更新
        when(transactionsMapper.updateById(any(Transactions.class))).thenReturn(1);

        boolean result = transactionService.transferMoney(dto);
        assertTrue(result);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testTransferMoneyFailureDueToInsufficientBalance() {
        TransferMoneyDTO dto = new TransferMoneyDTO();
        dto.setFromAccountNumber("0000001");
        dto.setToAccountNumber("0000002");
        dto.setAmount(BigDecimal.valueOf(200)); // 余额不足
        dto.setPin("1234");
        dto.setRemark("Test Transfer");

        Account fromAccount = new Account();
        fromAccount.setAccountNumber("0000001");
        fromAccount.setCurrentBalance(BigDecimal.valueOf(100));
        fromAccount.setPinHash(passwordEncoder.encode("1234"));

        Account toAccount = new Account();
        toAccount.setAccountNumber("0000002");
        toAccount.setCurrentBalance(BigDecimal.valueOf(50));

        when(accountService.getAccountById("0000001")).thenReturn(fromAccount);
        when(accountService.getAccountById("0000002")).thenReturn(toAccount);
        when(transactionsMapper.selectCount(any())).thenReturn(0L);

        boolean result = transactionService.transferMoney(dto);
        assertFalse(result);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testDepositMoneySuccess() {
        DepositMoneyDTO dto = new DepositMoneyDTO();
        dto.setAccountNumber("0000001");
        dto.setAmount(BigDecimal.valueOf(50));
        dto.setInitiatedBy("Test Teller");
        dto.setRemark("Deposit Test");

        Account account = new Account();
        account.setAccountNumber("0000001");
        account.setCurrentBalance(BigDecimal.valueOf(100));

        when(accountService.getAccountById("0000001")).thenReturn(account);
        when(transactionsMapper.selectCount(any())).thenReturn(0L);
        when(transactionsMapper.insert(any(Transactions.class))).thenReturn(1);
        when(accountMapper.updateById(account)).thenReturn(1);
        when(transactionsMapper.updateById(any(Transactions.class))).thenReturn(1);

        boolean result = transactionService.depositMoney(dto);
        assertTrue(result);
        // 检查账户余额是否正确增加
        assertEquals(BigDecimal.valueOf(150), account.getCurrentBalance());
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testDepositMoneyFailureNonexistentAccount() {
        DepositMoneyDTO dto = new DepositMoneyDTO();
        dto.setAccountNumber("0000003");
        dto.setAmount(BigDecimal.valueOf(50));
        dto.setInitiatedBy("Test Teller");
        dto.setRemark("Deposit Test");

        when(accountService.getAccountById("0000003")).thenReturn(null);

        boolean result = transactionService.depositMoney(dto);
        assertFalse(result);
    }
}