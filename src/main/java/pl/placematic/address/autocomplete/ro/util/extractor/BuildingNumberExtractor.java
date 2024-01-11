package pl.placematic.address.autocomplete.ro.util.extractor;

import org.springframework.stereotype.Service;

@Service
public class BuildingNumberExtractor {
    public Integer extractAsInt(String string) {
        string = this.dropZip(string);
        String buildingNumber = string.replaceAll("^[^\\d]*(\\d+)[A-Za-z/]*.*$", "$1");
        try {
            return Integer.parseInt(buildingNumber);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String extractAsString(String string) {
        string = this.dropZip(string);
        String buildingNumber = string.replaceAll("^[^\\d]*(\\d+[A-Za-z]*)[/]*.*$", "$1");
        if (string.equals(buildingNumber)) {
            try {
                Integer.parseInt(buildingNumber);
                return buildingNumber;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return buildingNumber;
    }

    public String extractFullWithLetters(String string) {
        return string.replaceAll("^[^\\d]*(\\d+[A-Za-z]*(?:\\/(\\d+)*)*).*$", "$1");
    }

    private String dropZip(String string)
    {
        return string.replaceAll("(\\b)\\d{2}-\\d{0,3}(\\b)", "$1$2");
    }
}
