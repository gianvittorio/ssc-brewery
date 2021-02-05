package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerIT extends BaseIT {
    @DisplayName("List Customers")
    @Nested
    class ListCustomers {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAdminCustomer")
        public void testListCustomerAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/customers").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }

        @Test
        public void testListCustomerNOTAUTH() throws Exception {
            mockMvc.perform(get("/customers").with(httpBasic("user", "password")))
                    .andExpect(status().isOk());
        }

        @Test
        public void testListCustomerNOTLOGGEDIN() throws Exception {
            mockMvc.perform(get("/customers"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Add customers")
    @Nested
    class AddCustomers {

        @Rollback
        @Test
        public void processCreationForm() throws Exception {
            mockMvc.perform(post("/customers/new")
                    .with(csrf())
                    .param("customerName", "Foo Customer")
                    .with(httpBasic("spring", "guru")))
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        public void processCreationFormNOTAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(post("/customers/new")
                    .param("customerName", "Foo Customer2")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        public void processCreationFormNOAUTH() throws Exception {
            mockMvc.perform(post("/customers/new")
                    .with(csrf())
                    .param("customerName", "Foo Customer2"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
