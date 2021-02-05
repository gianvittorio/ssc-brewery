package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {
    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    public Beer beerToDelete() {
        final Random random = new Random();

        return beerRepository.saveAndFlush(Beer.builder()
                .beerName("Delete Me Beer")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(Integer.toString(random.nextInt(999_999)))
                .build());
    }

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {
        @Test
        public void deleteBeerHttpBasic() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        public void deleteBeerHttpBasicUserRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        public void deleteBeerHttpBasicCustomerRole() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                    .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        public void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    public void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findBeersWithADMIN() throws Exception {
        mockMvc.perform(get("/api/v1/beer/").with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeersWithUSER() throws Exception {
        mockMvc.perform(get("/api/v1/beer/").with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeersWithCUSTOMER() throws Exception {
        mockMvc.perform(get("/api/v1/beer/").with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeersById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/" + beerToDelete().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findBeerByUpcADMIN() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036").with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeerByUpcUSER() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036").with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeerByUpcCUSTOMER() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036").with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk());
    }

    @Test
    public void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findBeerFormADMIN() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "").with(httpBasic("spring", "guru")))
                .andExpect(status().isOk());
    }
}
