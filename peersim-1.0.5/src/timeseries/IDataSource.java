// Author: Alexander Weinmann uni@aweinmann.de

package timeseries;

import java.time.*;
import java.util.Map;

/**
 * Provides a mapping from sensor ids to observations.
 */

public interface IDataSource {
    /**
     * Returns all data points available at time t
     * @return
     */
	Map<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration);
}
