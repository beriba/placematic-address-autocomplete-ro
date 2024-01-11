package pl.placematic.address.autocomplete.ro.logic;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import pl.placematic.address.autocomplete.ro.elastic.Address;

import java.util.ArrayList;
import java.util.List;

public abstract class FieldValueSelector {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public FieldValueSelector(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public List<Address> selectByValue(NativeSearchQueryBuilder builder, Pageable pageable) {
        List<Address> results = new ArrayList<>();

        builder.withPageable(new PageRequest(0, 2000));

        List<Address> resultsForSelector = elasticsearchTemplate.queryForList(builder.build(), Address.class);

        List<String> visited = new ArrayList<>();
        for (Address address : resultsForSelector) {
            if (!visited.contains(this.field(address))) {
                results.add(address);
                visited.add(this.field(address));
            }
        }
        return results.subList(
                Math.min(pageable.getPageNumber() * pageable.getPageSize(), results.size()),
                Math.min(((pageable.getPageNumber() + 1) * pageable.getPageSize()) - 1, results.size())
        );
    }

    protected abstract String field(Address address);
}
