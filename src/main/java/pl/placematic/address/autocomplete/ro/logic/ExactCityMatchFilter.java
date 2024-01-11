package pl.placematic.address.autocomplete.ro.logic;

import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.response.CityInterface;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExactCityMatchFilter {

    public <T extends CityInterface> List<T> filter(List<T> input, String city, String cityDistricted) {
        List<T> exactCityMatches = new ArrayList<>();
        boolean isExactMatch = false;
        for (T cityElement : input) {
            if (
                    cityElement.getCity().toUpperCase().equals(city.toUpperCase())
                    || cityElement.getCityDistricted().toUpperCase().equals(cityDistricted.toUpperCase())
                    || cityElement.getCityDistricted().toUpperCase().equals(city.toUpperCase())
                    || cityElement.getCity().toUpperCase().equals(cityDistricted.toUpperCase())
            ) {
                isExactMatch = true;
                exactCityMatches.add(cityElement);
            }
        }

        if (isExactMatch) {
            return exactCityMatches;
        }

        return input;
    }
}
