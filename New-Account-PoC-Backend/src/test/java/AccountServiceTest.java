import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fisher.newaccountpoc.dto.NewAccountDto;
import com.fisher.newaccountpoc.dto.StatementDTO;
import com.fisher.newaccountpoc.entity.Account;
import com.fisher.newaccountpoc.entity.Transactions;
import com.fisher.newaccountpoc.mapper.TransactionsMapper;
import com.fisher.newaccountpoc.common.util.JwtUtils;
import com.fisher.newaccountpoc.service.impl.AccountServiceImpl;
import io.jsonwebtoken.Claims;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private TransactionsMapper transactionsMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用 spy 覆盖父类的 save 方法，使其不执行实际数据库操作
        AccountServiceImpl spyService = spy(accountService);
        doReturn(true).when(spyService).save(any(Account.class));
        accountService = spyService;
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testCreateAccountSuccess() {
        NewAccountDto dto = new NewAccountDto();
        dto.setCitizenId("1234567890123");
        dto.setThaiName("thai");
        dto.setEnName("Test");
        dto.setPin("1234");

        boolean result = accountService.createAccount(dto);
        assertTrue(result);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testViewAccountInfo() {
        String token = "dummyToken";
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("1234567890123");
        when(jwtUtils.parseToken(token)).thenReturn(mockClaims);

        Account account = new Account();
        account.setCitizenId("1234567890123");
        doReturn(account).when(accountService).getOne(any(QueryWrapper.class));

        Account result = accountService.viewAccountInfo(token);
        assertNotNull(result);
        assertEquals("1234567890123", result.getCitizenId());
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testGetAccountById() {
        Account account = new Account();
        account.setAccountNumber("0000001");
        doReturn(account).when(accountService).getOne(any(QueryWrapper.class));

        Account result = accountService.getAccountById("0000001");
        assertNotNull(result);
        assertEquals("0000001", result.getAccountNumber());
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testGetStatementSuccess() {
        // 构造账户并设置已加密的 PIN
        String rawPin = "1234";
        Account account = new Account();
        account.setCitizenId("1234567890123");
        account.setAccountNumber("0000001");
        String hashedPin = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(rawPin);
        account.setPinHash(hashedPin);

        // 模拟根据 citizenId 获取账户
        doReturn(account).when(accountService).getOne(any(QueryWrapper.class));

        // 构造一笔交易记录
        Transactions transaction = new Transactions();
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionTime(LocalTime.now());
        transaction.setChannel("online");
        transaction.setAmount(BigDecimal.TEN);
        transaction.setFromAccount("0000001");
        transaction.setToAccount("0000002");
        transaction.setFromAccountBalance(BigDecimal.valueOf(100));
        transaction.setToAccountBalance(BigDecimal.valueOf(200));
        transaction.setRemark("Test Remark");

        List<Transactions> transactionsList = Collections.singletonList(transaction);
        when(transactionsMapper.selectList(any(QueryWrapper.class))).thenReturn(transactionsList);

        List<StatementDTO> statements = accountService.getStatement("1234567890123", 2023, 1, rawPin);
        assertNotNull(statements);
        assertFalse(statements.isEmpty());
        StatementDTO stmt = statements.get(0);
        // 根据逻辑：如果 account.accountNumber 等于 transactions.fromAccount，则为 Debit
        assertEquals("Debit", stmt.getType());
        assertEquals(BigDecimal.TEN, stmt.getAmount());
    }
}