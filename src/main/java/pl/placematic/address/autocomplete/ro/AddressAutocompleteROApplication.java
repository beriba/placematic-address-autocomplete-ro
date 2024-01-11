package pl.placematic.address.autocomplete.ro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
public class AddressAutocompleteROApplication {

    public static void main(String[] args) {
        SpringApplication.run(AddressAutocompleteROApplication.class, args);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
