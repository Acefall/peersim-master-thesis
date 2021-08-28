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

import approximation.SNApproximation;
import example.AggregationProtocol;
import peersim.config.*;
import peersim.core.*;


public class RandomUniformInitializer implements Control {
    /**
     * The upper bound of the values.
     */
    private static final String PAR_MAX = "max";

    /**
     * The lower bound of the values. Defaults to -max.
     */
    private static final String PAR_MIN = "min";

    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";


    /** Maximum interval value,
     obtained from config property {@link #PAR_MAX}. */
    private final double max;

    /** Minimum interval value,
     obtained from config property {@link #PAR_MIN}. */
    private final double min;

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public RandomUniformInitializer(String prefix) {
        max = Configuration.getDouble(prefix + "." + PAR_MAX);
        min = Configuration.getDouble(prefix + "." + PAR_MIN, -max);
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    public boolean execute() {
        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = min + (max - min) * CommonState.r.nextDouble();
            AggregationProtocol aggregationProtocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            aggregationProtocol.setInput(randomDouble);
        }
        return false;
    }

}
