package pl.placematic.address.autocomplete.ro.util.vo.selector;

import lombok.Getter;

@Getter
public abstract class SelectorField {

    private String fieldName;

    public SelectorField() {
        this.fieldName = this.fieldName();
    }


    protected abstract String fieldName();
}
