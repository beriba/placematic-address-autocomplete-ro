package pl.placematic.address.autocomplete.ro.regression.v1_0;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuggestHouseNumberTest {

    private static final String ENDPOINT = "/1.0/suggest/housenumber";

    @Autowired
    private MockMvc mvc;

    @Test
    public void test_1_() throws Exception {

        mvc.perform(
                get(ENDPOINT)
                        .param("query", "56")
                        .param("street", "ulica adi endrea")
                        .param("cityDistricted", "novi sad")
                        .param("outputSchema", "basic")
                        .param("size", "10")
                        .param("approximate", "true")
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
