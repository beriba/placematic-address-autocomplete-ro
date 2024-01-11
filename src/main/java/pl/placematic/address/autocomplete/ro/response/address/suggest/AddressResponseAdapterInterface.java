package pl.placematic.address.autocomplete.ro.response.address.suggest;

import pl.placematic.address.autocomplete.ro.response.ResponseInterface;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.util.vo.Location;

public interface AddressResponseAdapterInterface {
    Iterable<ResponseInterface> adapt(Iterable<Address> addresses);

    AddressResponseAdapterInterface setQueryPoint(Location queryPoint);
}
