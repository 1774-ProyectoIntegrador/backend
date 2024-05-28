package proyecto.dh.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import proyecto.dh.exceptions.GlobalException;
import proyecto.dh.resources.auth.controller.AuthController;
import proyecto.dh.resources.auth.service.AuthService;
import proyecto.dh.resources.auth.util.JwtUtil;
import proyecto.dh.resources.users.dto.RegisterRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalException())
                .build();
    }

    @Test
    void testRegisterNewUser() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setEmail("newuser123@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setUserName("newuser");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"newuser123@example.com\",\"password\":\"password123\",\"userName\":\"newuser\",\"firstName\":\"New\",\"lastName\":\"User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("User registered successfully")));
    }


}
