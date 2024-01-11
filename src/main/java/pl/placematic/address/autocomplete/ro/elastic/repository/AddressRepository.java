package pl.placematic.address.autocomplete.ro.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.placematic.address.autocomplete.ro.elastic.Address;

public interface AddressRepository extends ElasticsearchRepository<Address, String>, AddressRepositoryCustom {
}
