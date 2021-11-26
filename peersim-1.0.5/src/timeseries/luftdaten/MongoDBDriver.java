// Author: Alexander Weinmann uni@aweinmann.de
package timeseries.luftdaten;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import peersim.config.Configuration;
import timeseries.Observation;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.ascending;

import java.time.*;
import java.util.*;

/**
 * Driver that provides P1 measurements from a MongoDB for a certain time interval.
 */

public class MongoDBDriver implements ILuftdatenDriver {
    private static final String PAR_HOST = "host";
    private static final String PAR_PORT = "port";


    private final MongoClient client;
    private final MongoDatabase database;

    public MongoDBDriver(String host, int port) {
        client = new MongoClient("localhost", 27017);
        database = client.getDatabase("luftdaten");
    }

    public MongoDBDriver(String name) {
        client = new MongoClient(
                Configuration.getString(name + "." + PAR_HOST, "localhost"),
                Configuration.getInt(name + "." + PAR_PORT, 27017)
        );
        database = client.getDatabase("luftdaten");
    }

    /**
     * Returns a mapping from sensor ids to measurements.
     * For each sensor that had a measurement between t-validPeriod and t
     * it returns the latest measurement for that sensor.
     *
     * @param t             upper limit of time interval
     * @param validDuration length of the time interval
     * @param sensorType    has to be equal to the name of the collection
     * @return
     */

    public HashMap<String, Observation> valuesAt(LocalDateTime t, Duration validDuration, String sensorType, String environmentVariable) {
        var collection = database.getCollection(sensorType);

        // Select documents in this time interval
        Bson match = match(Filters.and(
                Filters.gte("timestamp", t.minus(validDuration)),
                Filters.lt("timestamp", t)
        ));

        // Get the last observation from each sensor_id
        Bson group = group("$sensor_id", Accumulators.last("last_observation", "$$ROOT"));

        // Sort ids ascending
        Bson sort = sort(ascending("sensor_id"));

        // Replace the root document with the last observation
        Bson replaceRoot = replaceRoot("$last_observation");

        // Only return relevant fields
        Bson project = project(Projections.include(Arrays.asList(
                "sensor_id", "timestamp", environmentVariable
        )));

        // Run the actual query
        AggregateIterable<Document> resultSet = collection.aggregate(Arrays.asList(
                match, group, sort, replaceRoot
        )).allowDiskUse(true);

        HashMap<String, Observation> measurements = new HashMap<String, Observation>();

        for (Document doc : resultSet) {
            Double measurement;
            try {
                Object measurementObject = doc.get(environmentVariable);

                if(measurementObject instanceof Double){
                    // Is Double
                    measurement = (Double) measurementObject;
                } else if(measurementObject instanceof Integer){
                    // Is Integer
                    measurement = ((Integer) measurementObject).doubleValue();
                }else{
                    // Try anyways
                    measurement = (Double )doc.get(environmentVariable);
                }

                if (measurement == null) {
                    throw new Exception("Measurement is null");
                }
            } catch (Exception e) {
                System.err.println("Failed to convert measurement to double. Measurement was: " + doc.get(environmentVariable));
                measurement = Double.NaN;
            }

            LocalDateTime tObs;
            try {
                Date timestamp = (Date) doc.get("timestamp");
                tObs = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("UTC"));
            } catch (Exception e) {
                System.err.println("Failed to parse timestamp");
                throw e;
            }

            measurements.put(doc.get("sensor_id").toString(), new Observation(tObs, measurement));
        }

        return measurements;
    }
}
