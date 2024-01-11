package pl.placematic.address.autocomplete.ro.regression.v1_0;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuggestZipTest {

    private static final String ENDPOINT = "/1.0/suggest/zip";

    @Autowired
    private MockMvc mvc;

    @Test
    public void test_1_() throws Exception {

        mvc.perform(
                get(ENDPOINT)
                        .param("query", "")
                        .param("street", "ulica adi endrea")
                        .param("city", "novi sad")
                        .param("houseNumber", "56")
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].zip", is("21201")));
    }
}
