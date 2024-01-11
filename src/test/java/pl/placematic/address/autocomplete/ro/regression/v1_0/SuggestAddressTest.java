package pl.placematic.address.autocomplete.ro.regression.v1_0;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SuggestAddressTest {

    private static final String ENDPOINT = "/1.0/suggest/address";

    @Autowired
    private MockMvc mvc;

    @Test
    public void test_1_() throws Exception {

        mvc.perform(
                        get(ENDPOINT)
                                .param("query", "endrea 56")
                                .param("outputSchema", "basic")
                                .param("size", "10")
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].street", is("Ulica Adi Endrea")));
    }

    @Test
    public void test_2_() throws Exception {

        mvc.perform(
                        get(ENDPOINT)
                                .param("query", "Jurija Gagarina 14 Beograd 11070")
                                .param("outputSchema", "basic")
                                .param("size", "10")
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].street", is("Ulica Jurija Gagarina")));
    }

    @Test
    public void test_2a() throws Exception {

        mvc.perform(
                        get(ENDPOINT)
                                .param("query", "Ulica Jurija Gagarina 14 Beograd 11070")
                                .param("outputSchema", "basic")
                                .param("size", "10")
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].street", is("Ulica Jurija Gagarina")));
    }

    @Test
    public void selector_test_1() throws Exception {
        ResultActions mock = mvc.perform(
                get(ENDPOINT)
                        .param("query", "Ulica Jurija Gagarina")
                        .param("outputSchema", "basic")
                        .param("size", "10")
                        .param("page", "0")
                        .param("sortBy", "municipalityClass")
                        .param("selector", "oneForEachCity")
        );

        mock
                .andExpect(status().is(200))
        ;

        String json = mock.andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, String>> map = mapper.readValue(json, List.class);

        List<String> checked = new ArrayList<>();
        int duplicates = 0;
        for (LinkedHashMap<String, String> address : map) {
            if (!checked.contains(address.get("cityDistricted"))) {
                checked.add(address.get("cityDistricted"));
            } else {
                duplicates++;
            }
        }

        assertEquals(0, duplicates);
    }

    @Test
    public void selector_test_1a() throws Exception {
        ResultActions mock = mvc.perform(
                get(ENDPOINT)
                        .param("query", "Ulica Jurija Gagarina")
                        .param("outputSchema", "basic")
                        .param("size", "10")
                        .param("page", "0")
                        .param("sortBy", "municipalityClass")
        );

        mock
                .andExpect(status().is(200))
        ;

        String json = mock.andReturn().getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, String>> map = mapper.readValue(json, List.class);

        List<String> checked = new ArrayList<>();
        int duplicates = 0;
        for (LinkedHashMap<String, String> address : map) {
            if (!checked.contains(address.get("cityDistricted"))) {
                checked.add(address.get("cityDistricted"));
            } else {
                duplicates++;
            }
        }

        assertTrue(duplicates > 0);
    }
}
