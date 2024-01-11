package pl.placematic.address.autocomplete.ro.util.vo.sort;

import lombok.Getter;

@Getter
public abstract class SortField {

    private String fieldName;

    public SortField() {
        this.fieldName = this.fieldName();
    }


    protected abstract String fieldName();
}
