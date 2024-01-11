package pl.placematic.address.autocomplete.ro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;

@Named
public class IndexConfig {

    @Value("${elasticsearch.index}")
    private String indexName;

    @Bean
    public String indexName() {
        return indexName;
    }
}
