package pl.placematic.address.autocomplete.ro.logic;

import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.response.Street;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExactStreetMatchDecorator {

    public <T extends Street> List<T> decorate(List<T> input, String street) {
        List<T> exactStreetMatches = new ArrayList<>();
        List<T> other = new ArrayList<>();
        String streetUpper = street.toUpperCase();
        String streetNorm = normalize(streetUpper);
        for (T streetElement : input) {
            String elem = streetElement.getStreet().toUpperCase();
            String elemNorm = normalize(elem);
            if (elemNorm.equals(streetNorm)) {
                exactStreetMatches.add(streetElement);
            } else {
                other.add(streetElement);
            }
        }

        other.addAll(0, exactStreetMatches);

        return other;
    }

    private String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
