package pl.placematic.address.autocomplete.ro.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.AddressAutocompleteROApplication;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.util.vo.Approximation;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApproximationRangeFilter {

    private static final Logger logger = LogManager.getLogger(AddressAutocompleteROApplication.class);

    public List<Address> filter(List<Address> input, Approximation approximation) {
        if (approximation.getRangeFrom() == null || approximation.getRangeTo() == null) {
            return input;
        }
        List<Address> output = new ArrayList<>();
        for (Address address : input) {
            try {
                if (address.isRangeBuildingNumber()) {
                    ArrayList<Integer> range = address.getBuildingNumbersInRange();
                    Integer min = range.get(0);
                    Integer max = range.get(range.size() - 1);

                    if (
                            min >= approximation.getRangeFrom()
                                    && max <= approximation.getRangeTo()
                    ) {
                        output.add(address);
                    }
                    continue;
                }
                if (
                        address.getBuildingNumberInteger() >= approximation.getRangeFrom()
                                && address.getBuildingNumberInteger() <= approximation.getRangeTo()
                ) {
                    output.add(address);
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
        return output;
    }
}
