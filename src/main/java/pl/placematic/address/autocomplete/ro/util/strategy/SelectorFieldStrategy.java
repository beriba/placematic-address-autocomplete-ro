package pl.placematic.address.autocomplete.ro.util.strategy;

import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.util.vo.selector.NoSelectorField;
import pl.placematic.address.autocomplete.ro.util.vo.selector.OneForEachCitySelectorField;
import pl.placematic.address.autocomplete.ro.util.vo.selector.SelectorField;

@Service
public class SelectorFieldStrategy {

    public SelectorField decide(String selectBy) {
        if (selectBy != null) {
            switch (selectBy) {
                case "oneForEachCity":
                    return new OneForEachCitySelectorField();
            }
        }

        return new NoSelectorField();
    }
}
