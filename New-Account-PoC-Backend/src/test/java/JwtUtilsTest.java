
import static org.junit.jupiter.api.Assertions.*;

import com.fisher.newaccountpoc.common.util.JwtUtils;
import com.fisher.newaccountpoc.dto.TokenUserDTO;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

public class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    // 测试生成 token 并解析后验证 token 中的主体和角色信息
    @Test
    public void testGenerateAndParseToken() {
        TokenUserDTO user = new TokenUserDTO();
        user.setUserName("testUser");
        user.setRoles("ROLE_CUSTOMER");

        String token = jwtUtils.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtUtils.parseToken(token);
        assertNotNull(claims);
        // 验证 token 中的 subject 是否与生成时一致
        assertEquals("testUser", claims.getSubject());
        // 验证 roles 是否包含期望的角色
        Object rolesClaim = claims.get("roles");
        assertNotNull(rolesClaim);
        String roles = (String) rolesClaim;
        assertTrue(roles.contains("ROLE_CUSTOMER"));
    }
    // 测试 validateToken 方法对有效 token 的判断
    @Test
    public void testValidateTokenWithValidToken() {
        TokenUserDTO user = new TokenUserDTO();
        user.setUserName("validUser");
        user.setRoles("ROLE_CUSTOMER");

        String token = jwtUtils.generateToken(user);
        assertTrue(jwtUtils.validateToken(token));
    }

    // 测试 validateToken 方法对无效 token 的判断
    @Test
    public void testValidateTokenWithInvalidToken() {
        // 随便传入一个无效 token 字符串
        String invalidToken = "invalid.token.string";
        assertFalse(jwtUtils.validateToken(invalidToken));

        // 传入空字符串或 null 时也应返回 false
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken(null));
    }
}




