package pl.placematic.address.autocomplete.ro.util.vo.selector;

public class OneForEachCitySelectorField extends SelectorField {
    @Override
    protected String fieldName() {
        return "city_districted.keyword";
    }
}
