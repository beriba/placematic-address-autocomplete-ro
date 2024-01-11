package pl.placematic.address.autocomplete.ro.logic;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import pl.placematic.address.autocomplete.ro.elastic.Address;

public class CityDistrictedFieldValueSelector extends FieldValueSelector {
    public CityDistrictedFieldValueSelector(ElasticsearchTemplate elasticsearchTemplate) {
        super(elasticsearchTemplate);
    }

    @Override
    protected String field(Address address) {
        return address.getCityDistricted();
    }
}
