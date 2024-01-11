package pl.placematic.address.autocomplete.ro.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Zip {
    private String zip;
    private boolean approximated = false;

    public Zip(String zip) {
        this.zip = zip;
    }

    public Zip setApproximated() {
        this.approximated = true;

        return this;
    }
}
