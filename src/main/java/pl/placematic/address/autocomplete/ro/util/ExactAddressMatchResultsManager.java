package pl.placematic.address.autocomplete.ro.util;

import pl.placematic.address.autocomplete.ro.elastic.Address;

import java.util.ArrayList;
import java.util.List;

public class ExactAddressMatchResultsManager {
    public List<Address> manage(Address exact, List<Address> list) {
        if (exact != null) {
            ArrayList<Address> res = new ArrayList<>(list);
            res.remove(exact);
            res.add(0, exact);
            return res;
        }

        return list;
    }

    public List<Address> manage(List<Address> exacts, List<Address> list) {
        if (exacts != null && !exacts.isEmpty()) {
            ArrayList<Address> res = new ArrayList<>(exacts);
            ArrayList<Address> listCopy = new ArrayList<>(list);
            listCopy.removeAll(exacts);
            res.addAll(listCopy);
            return res;
        }

        return list;
    }
}
