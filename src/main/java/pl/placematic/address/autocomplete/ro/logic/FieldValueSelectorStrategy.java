package pl.placematic.address.autocomplete.ro.logic;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

@Component
public class FieldValueSelectorStrategy {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public FieldValueSelectorStrategy(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public FieldValueSelector choose(String fieldName) {
        switch (fieldName) {
            case "city_districted":
            case "city_districted.keyword":
                return new CityDistrictedFieldValueSelector(elasticsearchTemplate);
        }

        throw new RuntimeException("Field not supported for selector");
    }
}
