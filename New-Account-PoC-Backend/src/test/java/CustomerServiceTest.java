
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fisher.newaccountpoc.entity.Customer;
import com.fisher.newaccountpoc.mapper.CustomerMapper;
import com.fisher.newaccountpoc.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerMapper customerMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用 spy 覆盖 getOne 方法，避免实际数据库查询
        CustomerServiceImpl spyService = Mockito.spy(customerService);
        Customer dummyCustomer = new Customer();
        dummyCustomer.setCitizenId("1234567890123");
        doReturn(dummyCustomer).when(spyService).getOne(any(QueryWrapper.class));
        customerService = spyService;
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testFindByCitizenId() {
        Customer result = customerService.findByCitizenId("1234567890123");
        assertNotNull(result);
        assertEquals("1234567890123", result.getCitizenId());
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testValidatePassword() {
        // 使用 BCryptPasswordEncoder 生成加密后的密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encodedPassword = encoder.encode(rawPassword);

        // 当密码匹配时，passwordEncoder.matches 返回 true，因此 validatePassword 返回 !true，即 false
        boolean result = customerService.validatePassword(rawPassword, encodedPassword);
        assertFalse(result);

        // 当密码不匹配时，则返回 !false，即 true
        result = customerService.validatePassword("wrongPassword", encodedPassword);
        assertTrue(result);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testRegisterCustomer() {
        Customer customer = new Customer();
        customer.setCitizenId("1234567890123");
        customer.setPasswordHash("password"); // 明文密码

        doReturn(true).when(customerService).save(any(Customer.class));

        boolean result = customerService.registerCustomer(customer);
        assertTrue(result);
        // 确认注册后密码已被加密（与明文不相等）
        assertNotEquals("password", customer.getPasswordHash());
    }
}
