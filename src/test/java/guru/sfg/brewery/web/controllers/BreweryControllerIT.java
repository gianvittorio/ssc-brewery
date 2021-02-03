package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerIT extends BaseIT {

    @Test
    public void listBreweriesWithCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries").with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void listBreweriesWithAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries").with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void listBreweriesWithUserRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void listBreweriesNotAuthorized() throws Exception {
        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getBreweriesJsonWithCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    public void getBreweriesJsonWithAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getBreweriesJsonWithUserRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getBreweriesJsonNotAuthenticated() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }
}
