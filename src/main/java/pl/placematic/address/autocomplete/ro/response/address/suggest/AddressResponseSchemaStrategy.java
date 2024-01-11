package pl.placematic.address.autocomplete.ro.response.address.suggest;

import pl.placematic.address.autocomplete.ro.exception.UnsupportedSchemaException;

public class AddressResponseSchemaStrategy {

    private String outputSchema;

    public AddressResponseSchemaStrategy(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    public AddressResponseAdapterInterface decide() throws UnsupportedSchemaException {
        switch (outputSchema) {
            case "basic":
                return new BasicAdapter();
            case "extended":
                return new ExtendedAdapter();
            case "full":
                return new FullAdapter();
        }

        throw new UnsupportedSchemaException();
    }
}
