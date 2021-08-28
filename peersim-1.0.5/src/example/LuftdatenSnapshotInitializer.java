/*
 * Copyright (c) 2003-2005 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package example;

import example.AggregationProtocol;
import peersim.config.*;
import peersim.core.*;
import timeseries.IDataProvider;
import timeseries.IDataSource;
import timeseries.IncrementalDataProvider;
import timeseries.luftdaten.Luftdaten;
import timeseries.luftdaten.MongoDBDriver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;


public class LuftdatenSnapshotInitializer implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /**
     * Protocol identifier, obtained from config property {@link #PAR_PROT}.
     */
    private final int protocolID;

    private final IDataSource dataSource;
    private final IDataProvider dataProvider;

    public LuftdatenSnapshotInitializer(String prefix) {
        MongoDBDriver driver = new MongoDBDriver("localhost", 27017);
        dataSource = new Luftdaten(driver, "sds011", Network.size());
        dataProvider = new IncrementalDataProvider(dataSource, Network.size(), Duration.ofDays(1), Duration.ofMinutes(15), Double.NaN);


        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    /**
     * Initializes the nodes with the sensor data from luftdaten.
     *
     * @return false
     */
    public boolean execute() {
        System.out.println("Executing LuftdatenSnapshotInitializer");

        LocalDateTime start = LocalDateTime.of(2021, 2, 2, 0, 0, 0);
        HashMap<Integer, Double> latestMeasurements = dataProvider.nodeValuesAt(start);
        for (int i = 0; i < Network.size(); ++i) {
            AggregationProtocol aggregationProtocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            aggregationProtocol.setInput(latestMeasurements.get(i));
        }
        return false;
    }

}
