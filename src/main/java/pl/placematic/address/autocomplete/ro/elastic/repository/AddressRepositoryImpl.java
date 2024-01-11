package pl.placematic.address.autocomplete.ro.elastic.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import pl.placematic.address.autocomplete.ro.AddressAutocompleteROApplication;
import pl.placematic.address.autocomplete.ro.data.CityCentroids;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.logic.ExactHouseNumberMatchFinder;
import pl.placematic.address.autocomplete.ro.logic.FieldValueSelectorStrategy;
import pl.placematic.address.autocomplete.ro.query.BuildingNumberQuery;
import pl.placematic.address.autocomplete.ro.response.City;
import pl.placematic.address.autocomplete.ro.response.Street;
import pl.placematic.address.autocomplete.ro.response.Zip;
import pl.placematic.address.autocomplete.ro.singlestring.Query;
import pl.placematic.address.autocomplete.ro.util.ExactAddressMatchResultsManager;
import pl.placematic.address.autocomplete.ro.util.extractor.StreetPrefixExtractor;
import pl.placematic.address.autocomplete.ro.util.normalizer.*;

import java.util.ArrayList;
import java.util.List;

public class AddressRepositoryImpl implements AddressRepositoryCustom {

    private static final Logger logger = LogManager.getLogger(AddressAutocompleteROApplication.class);

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ExactHouseNumberMatchFinder exactHouseNumberMatchFinder;

    @Value("${elasticsearch.index}")
    private String index;

    private CityNormalizer cityNormalizer = new CityNormalizer();

    @Autowired
    CityCentroids cityCentroids;

    @Override
    public List<Address> findSingleString(Query singlestringQuery, Pageable pageable) {
        String query = singlestringQuery.getQuery();

        query = normalizeSingleString(query);

//        query = this.addStreetPrefix(singlestringQuery, query);
//        query = this.addZip(singlestringQuery, query);

        return this.executeSingleString(query, pageable, singlestringQuery);
    }

    @Override
    public List<Address> findSingleStringWithoutBuildingNumber(Query singlestringQuery, Pageable pageable) {
        String query = singlestringQuery.getQuery();

        query = query.replaceAll("\\d+(?:[a-zA-Z]+)*", "");
        query = normalizeSingleString(query);
        query = query.replaceAll("\\s[*]", "");

//        query = this.addStreetPrefix(singlestringQuery, query);

        return this.executeSingleString(query, pageable, singlestringQuery);
    }

//    private String addStreetPrefix(Query singlestringQuery, String query) {
//        if (singlestringQuery.getStreetPrefix() != null) {
//            return "street_prefix:" + singlestringQuery.getStreetPrefixNormalized() + " " + query;
//        }
//
//        return query;
//    }

//    private String addZip(Query singlestringQuery, String query) {
//        if (singlestringQuery.getZip() != null) {
//            String zipValue = singlestringQuery.getZip();
//            if (zipValue.length() < 6) {
//                zipValue += "*";
//            }
//            return "zip.keyword:" + zipValue + " " + query;
//        }
//
//        return query;
//    }

    private String normalizeSingleString(String query) {
        query = query.replaceAll("\\s+", " ").trim();
        query = query.replaceAll("[.-]", " ");
        query = QueryParser.escape(query);
        query = query.replaceAll("[, ]", "* ") + "*";
        query = query.replaceAll("\\s[*]", "");
        query = query.replaceAll("^[*]$", "");

        return query;
    }

    private List<Address> executeSingleString(String query, Pageable pageable, Query singlestringQuery) {
        String sortByField = null;
        if (singlestringQuery.getSortBy() != null) {
            sortByField = singlestringQuery.getSortBy().getFieldName();
        }
        logger.info(query);

        NativeSearchQueryBuilder builder = this.singlestringBuilder(query)
                .withPageable(pageable);

        if (sortByField != null) {
            builder.withSort(SortBuilders.fieldSort(sortByField + ".keyword"));
        }

        List<Address> results;

        if (singlestringQuery.getSelectBy() != null && singlestringQuery.getSelectBy().getFieldName() != null) {
            results = new FieldValueSelectorStrategy(elasticsearchTemplate).choose(singlestringQuery.getSelectBy().getFieldName()).selectByValue(builder, pageable);
        } else {
            SearchQuery searchQuery = builder.build();
            results = elasticsearchTemplate.queryForList(searchQuery, Address.class);
        }

        List<Address> exacts = exactHouseNumberMatchFinder.findAll(results, singlestringQuery);
        if (exacts.isEmpty() && !results.isEmpty() && singlestringQuery.hasBuildingNumber()) {
            NativeSearchQueryBuilder exactMatchQueryBuilder = this.singlestringBuilder(query + " building_number:" + QueryParser.escape(singlestringQuery.getBuildingNumberFull()));
            if (sortByField != null) {
                exactMatchQueryBuilder.withSort(SortBuilders.fieldSort(sortByField + ".keyword"));
            }
            SearchQuery exactMatchSearchQuery = exactMatchQueryBuilder.build();
            List<Address> exactMatchResults = elasticsearchTemplate.queryForList(exactMatchSearchQuery, Address.class);
            exacts = exactHouseNumberMatchFinder.findAll(exactMatchResults, singlestringQuery);
        }

        return new ExactAddressMatchResultsManager().manage(exacts, results);
    }

    private NativeSearchQueryBuilder singlestringBuilder(String query) {
        return new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.queryStringQuery(query)
                                .field("city_districted", 5)
                                .field("city", 5)
                                .field("district", 5)
                                .field("district_alternative", 5)
                                .field("street.keyword", 20)
                                .field("street")
                                .field("zip", 10)
                                .field("building_number", 3)
                                .defaultOperator(Operator.AND)
                );
    }

    @Override
    public List<City> findByCity(String cityQuery, String municipalityQuery, String voivodeshipQuery) {

        cityQuery = cityNormalizer.normalize(QueryParser.escape(cityQuery));
        cityQuery = "(city:" + cityQuery + "* OR city_districted:" + cityQuery + "*)";

        String query = cityQuery;

        if (!municipalityQuery.isEmpty()) {
            query += " municipality:" + municipalityQuery.replaceAll("[-]", " ") + "*";
        }
        if (!voivodeshipQuery.isEmpty()) {
            query += " voivodeship:" + voivodeshipQuery.replaceAll("[-]", " ") + "*";
        }

        logger.info(query);

        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query)
                .defaultOperator(Operator.AND);

        SearchResponse sr = elasticsearchTemplate.getClient().prepareSearch()
                .setIndices(index)
                .setQuery(queryBuilder)
                .addAggregation(
                        AggregationBuilders.terms("city_unique.keyword").field("city_unique.keyword")
                                .subAggregation(AggregationBuilders.terms("city_districted.keyword").field("city_districted.keyword"))
                                .subAggregation(AggregationBuilders.terms("city.keyword").field("city.keyword"))
                                .subAggregation(AggregationBuilders.terms("voivodeship.keyword").field("voivodeship.keyword"))
                                .subAggregation(AggregationBuilders.terms("municipality.keyword").field("municipality.keyword"))
                )
                .execute().actionGet();

        Terms cityTerms = sr.getAggregations().get("city_unique.keyword");

        List<City> cities = new ArrayList<>();
        for (Terms.Bucket bucket : cityTerms.getBuckets()) {
            String cityUnique = (String) bucket.getKey();
            String cityName = (String) ((Terms) bucket.getAggregations().get("city.keyword")).getBuckets().get(0).getKey();
            String cityDistricted = (String) ((Terms) bucket.getAggregations().get("city_districted.keyword")).getBuckets().get(0).getKey();
            String voivodeship = (String) ((Terms) bucket.getAggregations().get("voivodeship.keyword")).getBuckets().get(0).getKey();
            String municipality = (String) ((Terms) bucket.getAggregations().get("municipality.keyword")).getBuckets().get(0).getKey();
            City cityResponse = new City();
            cityResponse.setCity(cityName);
            cityResponse.setCityDistricted(cityDistricted);
            cityResponse.setState(voivodeship);
            cityResponse.setMunicipality(municipality);
            cityResponse.setLocation(cityCentroids.getCentroid(cityUnique));
            cities.add(cityResponse);
        }

        return cities;
    }

    @Override
    public List<Street> findStreets(
            String streetQuery,
            String cityQuery,
            String municipalityQuery,
            String voivodeshipQuery,
            String streetPrefix
    ) {

        String streetEscapedQuery = QueryParser.escape(new StreetNormalizer().normalize(streetQuery));
        StreetPrefixNormalizer streetPrefixNormalizer = new StreetPrefixNormalizer();
        if (streetPrefix.isEmpty()) {
            streetPrefix = new StreetPrefixExtractor().extract(streetQuery);
            if (streetPrefix == null) {
                streetPrefix = "";
            }
        }
        if (!streetPrefix.isEmpty()) {
            streetEscapedQuery = streetPrefixNormalizer.dropPrefix(streetEscapedQuery);
        }
        String query = "(street:" + streetEscapedQuery + "* OR street_teryt:" + streetEscapedQuery + "*)";

        cityQuery = cityNormalizer.normalize(QueryParser.escape(cityQuery));
        query += " (city:" + cityQuery + "* OR city_districted:" + cityQuery + "*)";

//        if (!streetPrefix.isEmpty()) {
//            streetPrefix = new StreetPrefixNormalizer().normalize(streetPrefix);
//            query += " street_prefix:" + QueryParser.escape(streetPrefix) + "*";
//        }

        if (!municipalityQuery.isEmpty()) {
            query += " municipality:" + municipalityQuery + "*";
        }
        if (!voivodeshipQuery.isEmpty()) {
            query += " voivodeship:" + voivodeshipQuery + "*";
        }

        logger.info(query);

        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query)
                .defaultOperator(Operator.AND);

        SearchResponse sr = elasticsearchTemplate.getClient().prepareSearch()
                .setIndices(index)
                .setQuery(queryBuilder)
                .addAggregation(
                        AggregationBuilders.terms("street.keyword").field("street.keyword")
                                .subAggregation(AggregationBuilders.terms("city.keyword").field("city.keyword")
                                                .subAggregation(AggregationBuilders.terms("city_districted.keyword").field("city_districted.keyword"))
                                                .subAggregation(AggregationBuilders.terms("voivodeship.keyword").field("voivodeship.keyword"))
                                                .subAggregation(AggregationBuilders.terms("municipality.keyword").field("municipality.keyword"))
                                                .subAggregation(AggregationBuilders.terms("street_prefix.keyword").field("street_prefix.keyword"))
//                                        .subAggregation(AggregationBuilders.terms("street.keyword").field("street.keyword"))
                                )
                )
                .execute().actionGet();

        Terms streetTerms = sr.getAggregations().get("street.keyword");

        List<Street> streets = new ArrayList<>();
        if (streetTerms.getBuckets().size() == 0) {
            return streets;
        }

        for (Terms.Bucket bucket : streetTerms.getBuckets()) {
            String streetName = (String) bucket.getKey();

            for (StringTerms.Bucket streetBucket : ((StringTerms) ((Terms) bucket.getAggregations().asList().get(0))).getBuckets()) {
                String cityName = (String) streetBucket.getKey();
                String cityDistricted = (String) ((Terms) streetBucket.getAggregations().get("city_districted.keyword")).getBuckets().get(0).getKey();
                String voivodeship = (String) ((Terms) streetBucket.getAggregations().get("voivodeship.keyword")).getBuckets().get(0).getKey();
                String municipality = (String) ((Terms) streetBucket.getAggregations().get("municipality.keyword")).getBuckets().get(0).getKey();
                String prefix = (String) ((Terms) streetBucket.getAggregations().get("street_prefix.keyword")).getBuckets().get(0).getKey();

                Street streetResponse = new Street(voivodeship, municipality, cityName, cityDistricted, prefix, streetName, streetName);
                streets.add(streetResponse);
            }
        }

        return streets;
    }

    @Override
    public List<Zip> findZips(
            String zipQuery,
            String streetQuery,
            String buildingNumberQuery,
            String cityQuery,
            String cityDistrictedQuery,
            String municipalityQuery,
            String voivodeshipQuery,
            String streetPrefix,
            boolean streetExactMatchOnly
    ) {
        String streetQueryEscaped = QueryParser.escape(streetQuery);
        String query = "zip:" + QueryParser.escape(zipQuery) + "*";
        if (streetExactMatchOnly) {
            query += " street_exact:\"" + streetQueryEscaped + "\"";
        } else {
            query += " street:(" + streetQueryEscaped + "*)";
        }

        if (!buildingNumberQuery.isEmpty()) {
            buildingNumberQuery = new BuildingNumberNormalizer().normalize(buildingNumberQuery);
            query += " building_number.keyword:/" + QueryParser.escape(buildingNumberQuery) + "/";
        }

        if (!cityQuery.isEmpty()) {
            query += " city:" + cityNormalizer.normalize(QueryParser.escape(cityQuery)) + "*";
        }

        if (!cityDistrictedQuery.isEmpty()) {
            query += " city_districted:" + cityNormalizer.normalize(QueryParser.escape(cityDistrictedQuery)) + "*";
        }

        if (!municipalityQuery.isEmpty()) {
            query += " municipality:" + QueryParser.escape(municipalityQuery) + "*";
        }
        if (!voivodeshipQuery.isEmpty()) {
            query += " voivodeship:" + QueryParser.escape(voivodeshipQuery) + "*";
        }
//        if (streetPrefix != null && !streetPrefix.trim().isEmpty()) {
//            query += " street_prefix:" + new StreetPrefixNormalizer().normalize(streetPrefix) + " ";
//        }

        logger.info(query);

        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query)
                .defaultOperator(Operator.AND);

        SearchResponse sr = elasticsearchTemplate.getClient().prepareSearch()
                .setIndices(index)
                .setQuery(queryBuilder)
                .addAggregation(
                        AggregationBuilders.terms("zip.keyword").field("zip.keyword")
                )
                .execute().actionGet();

        Terms zipTerms = sr.getAggregations().get("zip.keyword");

        List<Zip> zips = new ArrayList<>();
        if (zipTerms.getBuckets().size() == 0) {
            return zips;
        }

        for (Terms.Bucket zipBucket : zipTerms.getBuckets()) {
            Zip zipResponse = new Zip((String) zipBucket.getKey());
            zips.add(zipResponse);
            //refactor (tworzenie innego obiektu zip + adapter)
        }

        return zips;
    }

    @Override
    public List<Address> findBuildingNumbers(BuildingNumberQuery buildingNumberQuery) {

        String buildingNumber = buildingNumberQuery.getBuildingNumberNormalized();
        String streetQueryEscapedNormalized = QueryParser.escape(buildingNumberQuery.getStreetNormalized());
        String streetQueryEscaped = QueryParser.escape(buildingNumberQuery.getStreetQuery());
        String streetQuery = QueryParser.escape(buildingNumberQuery.getStreetQuery());
        String streetPrefix = buildingNumberQuery.getStreetPrefix();
        String cityDistrictedQuery = buildingNumberQuery.getCityDistrictedQuery();
        String zipQuery = buildingNumberQuery.getZipQuery();
        String municipalityQuery = buildingNumberQuery.getMunicipalityQuery();
        String voivodeshipQuery = buildingNumberQuery.getVoivodeshipQuery();

        if (streetPrefix.isEmpty()) {
            streetPrefix = new StreetPrefixExtractor().extract(streetQuery);
            if (streetPrefix == null) {
                streetPrefix = "";
            }
        }
//        if (!streetPrefix.isEmpty()) {
//            streetQueryEscaped = new StreetPrefixNormalizer().dropPrefix(streetQueryEscaped);
//        }

        //TODO do osobnej metody
        String[] streetTokens = streetQueryEscapedNormalized.split(" ");
        String[] streetQueryParts = new String[2];
        String[] streetFields = new String[2];
        streetFields[0] = "street";
        int i = 0;
        for (String streetField : streetFields) {
            String streetFieldQuery = "(";
            for (String streetToken : streetTokens) {
                streetFieldQuery += streetField + ":" + streetToken + "* ";
            }
            streetFieldQuery = streetFieldQuery.trim() + ")";
            streetQueryParts[i] = streetFieldQuery;
            i++;
        }
        //

        String query = "";
        if (buildingNumberQuery.isExactMatchOnly()) {
            query += "building_number.keyword:/" + QueryParser.escape(buildingNumber) + "[^0-9]*/";
        } else {
            query += "building_number.keyword:" + QueryParser.escape(buildingNumber) + "*";
        }

        String[] streetEscapedTokens = streetQueryEscapedNormalized.split(" ");

        if (buildingNumberQuery.isStreetExactMatchOnly()) {
//            query += " (street_exact:\"" + streetQueryEscaped + "\")";
            query += " (street_exact:\"" + streetQueryEscaped + "\" OR street_exact.keyword:\"" + streetQueryEscaped + " utca\")";
        } else {
            query += " (" + String.join(" OR ", streetQueryParts) + ")";
            String streetQueryString = String.join("* AND ", streetEscapedTokens);
            query += " (street:(" + streetQueryString + "*))";
        }

        query += " (city_districted:\"" + cityNormalizer.normalize(QueryParser.escape(cityDistrictedQuery)) + "\"";
        query += " OR city:\"" + cityNormalizer.normalize(QueryParser.escape(cityDistrictedQuery)) + "\")";

//        if (!streetPrefix.isEmpty()) {
//            streetPrefix = new StreetPrefixNormalizer().normalize(streetPrefix);
//            query += " street_prefix:" + QueryParser.escape(streetPrefix) + "*";
//        }

        if (!zipQuery.isEmpty()) {
            String zip = QueryParser.escape(zipQuery).replaceAll("\\\\-", "-");
            query += " (zip:" + zip + "* OR zip.keyword:" + zip + "*)";
        }

        if (!municipalityQuery.isEmpty()) {
            query += " municipality:" + QueryParser.escape(municipalityQuery) + "*";
        }
        if (!voivodeshipQuery.isEmpty()) {
            query += " voivodeship:" + QueryParser.escape(voivodeshipQuery) + "*";
        }

        logger.info(query);

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.queryStringQuery(query)
                                .defaultOperator(Operator.AND)
                )
                .withPageable(buildingNumberQuery.getPageable());

        builder.withSort(SortBuilders.fieldSort("building_number_sort.keyword"));

        SearchQuery searchQuery = builder.build();

        List<Address> addresses = elasticsearchTemplate.queryForList(searchQuery, Address.class);

        List<Address> exact = exactHouseNumberMatchFinder.findByBuildingNumber(addresses, buildingNumber);
        if (buildingNumberQuery.isExactMatchOnly() && exact == null) {
            return new ArrayList<Address>();
        }

        return new ExactAddressMatchResultsManager().manage(exact, addresses);
    }

    @Override
    public List<Address> findWithoutBuildingNumber(
            BuildingNumberQuery buildingNumberQuery,
            Pageable pageable
    ) {
        String streetQueryEscaped = QueryParser.escape(buildingNumberQuery.getStreetNormalized());
        String streetPrefix = buildingNumberQuery.getStreetPrefix();
        String cityDistrictedQuery = buildingNumberQuery.getCityDistrictedQuery();
        String zipQuery = buildingNumberQuery.getZipQuery();
        String municipalityQuery = buildingNumberQuery.getMunicipalityQuery();
        String voivodeshipQuery = buildingNumberQuery.getVoivodeshipQuery();

        String query = "(street:" + streetQueryEscaped + "* OR street_teryt:" + streetQueryEscaped + "*)";


        if (buildingNumberQuery.isStreetExactMatchOnly()) {
            query += " (street_exact:\"" + streetQueryEscaped + "\")";
        } else {
            String[] streetEscapedTokens = streetQueryEscaped.split(" ");
            String streetQueryString = String.join("* AND ", streetEscapedTokens);
            query += " (street:(" + streetQueryString + "*))";
        }

        query += " (city_districted:" + cityNormalizer.normalize(QueryParser.escape(cityDistrictedQuery)) + "*";
        query += " OR city:" + cityNormalizer.normalize(QueryParser.escape(cityDistrictedQuery)) + "*)";

//        if (!streetPrefix.isEmpty()) {
//            streetPrefix = new StreetPrefixNormalizer().normalize(streetPrefix);
//            query += " street_prefix:" + QueryParser.escape(streetPrefix) + "*";
//        }

        if (!zipQuery.isEmpty()) {
            query += " zip:" + QueryParser.escape(zipQuery) + "*";
        }

        if (!municipalityQuery.isEmpty()) {
            query += " municipality:" + QueryParser.escape(municipalityQuery) + "*";
        }
        if (!voivodeshipQuery.isEmpty()) {
            query += " voivodeship:" + QueryParser.escape(voivodeshipQuery) + "*";
        }

        logger.info(query);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.queryStringQuery(query)
                                .defaultOperator(Operator.AND)
                )
                .withPageable(pageable)
                .build();

        return elasticsearchTemplate.queryForList(searchQuery, Address.class);
    }

    @Override
    public List<Address> findByLocation(String latitude, String longitude, Pageable pageable) {

        Double lat = Double.parseDouble(latitude);
        Double lon = Double.parseDouble(longitude);
        GeoPoint point = new GeoPoint(lat, lon);

        GeoDistanceQueryBuilder geoDistanceQueryBuilder = QueryBuilders.geoDistanceQuery("wgs84_location")
                .ignoreUnmapped(true)
                .geoDistance(GeoDistance.PLANE)
                .point(point)
                .distance(1, DistanceUnit.KILOMETERS);
        geoDistanceQueryBuilder.setValidationMethod(GeoValidationMethod.IGNORE_MALFORMED);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(geoDistanceQueryBuilder)
                .withSort(SortBuilders.geoDistanceSort("wgs84_location", point))
                .withPageable(pageable)
                .build();

        return elasticsearchTemplate.queryForList(searchQuery, Address.class);

    }

}
