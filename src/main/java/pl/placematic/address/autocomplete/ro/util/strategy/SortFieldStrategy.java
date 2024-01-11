package pl.placematic.address.autocomplete.ro.util.strategy;

import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.util.vo.sort.MunicipalityClassSortField;
import pl.placematic.address.autocomplete.ro.util.vo.sort.NoSortField;
import pl.placematic.address.autocomplete.ro.util.vo.sort.PopulationSegmentSortField;
import pl.placematic.address.autocomplete.ro.util.vo.sort.SortField;

@Service
public class SortFieldStrategy {

    public SortField decide(String sortBy) {
        if (sortBy != null) {
            switch (sortBy) {
                case "municipalityClass":
                    return new MunicipalityClassSortField();
                case "populationClass":
                    return new PopulationSegmentSortField();
            }
        }

        return new NoSortField();
    }
}
