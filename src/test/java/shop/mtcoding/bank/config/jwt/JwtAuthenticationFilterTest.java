package shop.mtcoding.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserReqDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.save(newUser("jsh", "정성현"));
    }

    @Test
    public void successfulAuthentication_test() throws Exception {
        // given
        UserReqDto.LoginReqDto loginReqDto = new UserReqDto.LoginReqDto();
        loginReqDto.setUsername("jsh");
        loginReqDto.setPassword("1234");
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc.perform(post("/api/login").content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + jwtToken);

        // then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("jsh"));
    }

    @Test
    public void unsuccessfulAuthentication_test() throws Exception {
        // given
        UserReqDto.LoginReqDto loginReqDto = new UserReqDto.LoginReqDto();
        loginReqDto.setUsername("jsh");
        loginReqDto.setPassword("12345");
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mvc.perform(post("/api/login").content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + jwtToken);

        // then
        resultActions.andExpect(status().isUnauthorized());
    }
}
