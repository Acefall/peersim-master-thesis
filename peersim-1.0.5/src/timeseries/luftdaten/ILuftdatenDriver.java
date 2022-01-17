// Author: Alexander Weinmann uni@aweinmann.de
package timeseries.luftdaten;

import timeseries.Observation;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public interface ILuftdatenDriver {
    /**
     * Returns all data points available at time t
     */
    Map<String, Observation> valuesAt(LocalDateTime t, Duration validDuration, String sensorType, String environmentVariable);
}
