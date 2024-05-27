package proyecto.dh.Security;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import proyecto.dh.resources.auth.service.UserDetailsService;
import proyecto.dh.resources.users.controller.AdminController;
import proyecto.dh.resources.users.controller.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminController.class, UserController.class})
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    public void whenUserWithUserRoleAccessUserEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/user/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenUserWithUserRoleAccessAdminEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenUserWithAdminRoleAccessAdminEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenUserWithAdminRoleAccessUserEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/user/dashboard"))
                .andExpect(status().isOk());
    }
}
