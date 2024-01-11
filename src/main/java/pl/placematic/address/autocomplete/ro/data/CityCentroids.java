package pl.placematic.address.autocomplete.ro.data;

import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.util.vo.Location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class CityCentroids {

    private Map<String, String> data;

    public Location getCentroid(String cityUnique) {
        String locationString = data().get(cityUnique.toLowerCase());
        if (locationString == null) {
            return null;
        }
        String[] split = locationString.split("\t");
        return new Location(
                split[0],
                split[1]
        );
    }

    private Map<String, String> data() {
        if (data != null) {
            return data;
        }

        Map<String, String> map = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("static/centroids.tsv")));

        String sCurrentLine;
        try {
            while ((sCurrentLine = reader.readLine()) != null) {
                String[] split = sCurrentLine.split("\t");
                map.put(split[0], split[1] + "\t" + split[2]);
            }
        } catch (Exception ex) {

        }

        data = map;

        return map;
    }
}
