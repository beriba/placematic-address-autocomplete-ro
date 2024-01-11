package pl.placematic.address.autocomplete.ro.util.vo;

public class Approximation {
    private boolean on;
    private Integer range;
    private Integer buildingNumber;

    public Approximation(boolean on, Integer range) {
        if (range == null) {
            range = 100000; // big value, but not too big. do not put Integer.MAX_VALUE here
        }
        this.on = on;
        this.range = range;
    }

    public boolean isOn() {
        return on;
    }

    public void setBuildingNumber(Integer buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public Integer getRangeFrom() {
        if (buildingNumber == null) {
            return null;
        }
        return buildingNumber - range;
    }

    public Integer getRangeTo() {
        if (buildingNumber == null) {
            return null;
        }
        return buildingNumber + range;
    }
}
