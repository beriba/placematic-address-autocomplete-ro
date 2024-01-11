package pl.placematic.address.autocomplete.ro.algorithm;

import pl.placematic.address.autocomplete.ro.elastic.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressApproximationAlgorithm {
    public Address approximate(List<Address> addresses, String buildingNumberFull) {

        if (buildingNumberFull == null) {
            return null;
        }

        String buildingNumberRegex = ".*?(\\d+)[a-zA-Z]*.*";
        Integer inputBuildingNumberInteger = null;
        if (buildingNumberFull.matches(buildingNumberRegex)) {
            inputBuildingNumberInteger = Integer.parseInt(buildingNumberFull.replaceAll(buildingNumberRegex, "$1"));
        }

        List<Address> modifiableAddresses = new ArrayList<Address>(addresses);
        modifiableAddresses.sort((o1, o2) -> {
            return o1.getBuildingNumberInteger().compareTo(o2.getBuildingNumberInteger()) * -1; // -1 for descending order
        });

        if (modifiableAddresses.size() == 0) {
            return null;
        }

        if (inputBuildingNumberInteger == null) {
            return modifiableAddresses.get(modifiableAddresses.size() - 1); // return the lowest number
        }

        Integer inputBuildingNumberParity = inputBuildingNumberInteger % 2;
        Address addressApprox = null;

        if (modifiableAddresses.get(0).getBuildingNumberInteger() > inputBuildingNumberInteger) { // if highest number is greater than input
            if (modifiableAddresses.get(modifiableAddresses.size() - 1).getBuildingNumberInteger() < inputBuildingNumberInteger) { // if lowest number is lower than input
                // iterate to find closest
                Address closest = null;
                Integer closestDistance = Integer.MAX_VALUE;
                for (Address address : modifiableAddresses) {
                    if (address.getBuildingNumberInteger() % 2 == inputBuildingNumberParity) {
                        if (closest == null) {
                            closest = address;
                            closestDistance = Math.abs(address.getBuildingNumberInteger() - inputBuildingNumberInteger);
                            continue;
                        }
                        Integer currentDistance = Math.abs(address.getBuildingNumberInteger() - inputBuildingNumberInteger);
                        if (currentDistance < closestDistance) {
                            closest = address;
                            closestDistance = currentDistance;
                        }
                        if (closestDistance == 0) { //this is the closest possible distance
                            break;
                        }
                    }
                }
                addressApprox = closest;
            } else { // if lowest number is higher than input
                addressApprox = modifiableAddresses.get(modifiableAddresses.size() - 1);
            }
        } else { // if highest number is lower or equal than input
            addressApprox = modifiableAddresses.get(0);
        }


        if (addressApprox != null) {
            addressApprox.setBuildingNumberApproximated(addressApprox.getBuildingNumber());
            addressApprox.setBuildingNumber(buildingNumberFull);
            addressApprox.setWgs84AccuracyClassDescription("przypisano aproksymowany budynek");
        }

        return addressApprox;
    }
}
