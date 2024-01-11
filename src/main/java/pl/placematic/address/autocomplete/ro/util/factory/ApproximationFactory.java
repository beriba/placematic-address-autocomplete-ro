package pl.placematic.address.autocomplete.ro.util.factory;

import pl.placematic.address.autocomplete.ro.util.vo.Approximation;

public class ApproximationFactory {
    public Approximation create(String approximate, String approximationRange) {
        boolean approxOn = false;
        if (approximate.equals("true")) {
            approxOn = true;
        }

        Integer approxRange = null;
        if (approximationRange != null) {
            approxRange = Integer.parseInt(approximationRange);
        }

        return new Approximation(approxOn, approxRange);
    }
}
