import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fisher.newaccountpoc.entity.Teller;
import com.fisher.newaccountpoc.mapper.TellerMapper;
import com.fisher.newaccountpoc.service.impl.TellerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TellerServiceTest {

    @InjectMocks
    private TellerServiceImpl tellerService;

    @Mock
    private TellerMapper tellerMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用 spy 覆盖 getOne 方法，避免实际数据库查询
        TellerServiceImpl spyService = spy(tellerService);
        doReturn(new Teller()).when(spyService).getOne(any(QueryWrapper.class));
        tellerService = spyService;
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testFindById() {
        Teller teller = new Teller();
        teller.setEmployeeId("T123");
        doReturn(teller).when(tellerService).getOne(any(QueryWrapper.class));

        Teller result = tellerService.findById("T123");
        assertNotNull(result);
        assertEquals("T123", result.getEmployeeId());
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testValidatePassword() {
        String rawPassword = "password";
        // 使用 BCrypt 生成加密后的密码
        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(rawPassword);
        // 若密码匹配，则 matches 返回 true，但本方法返回 !true，即 false
        boolean result = tellerService.validatePassword(rawPassword, encodedPassword);
        assertFalse(result);

        // 若密码不匹配，则返回 !false，即 true
        result = tellerService.validatePassword("wrong", encodedPassword);
        assertTrue(result);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testCreateTeller() {
        Teller teller = new Teller();
        teller.setEmployeeId("T123");
        teller.setPasswordHash("password"); // 传入明文密码

        doReturn(true).when(tellerService).save(any(Teller.class));

        boolean result = tellerService.createTeller(teller);
        assertTrue(result);
        // 确认密码已经被加密（与明文不相等）
        assertNotEquals("password", teller.getPasswordHash());
    }
}
