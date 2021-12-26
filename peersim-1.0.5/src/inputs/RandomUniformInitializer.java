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

package inputs;

import protocols.AggregationProtocol;
import peersim.config.*;
import peersim.core.*;

import java.util.Random;

/**
 * Sets the input of every node to a value independently sampled from a specified interval.
 * */
public class RandomUniformInitializer implements Control {
    /**
     * Parameter that defines the upper bound of the values.
     */
    private static final String PAR_MAX = "max";

    /**
     * Parameter that defines the lower bound of the values
     */
    private static final String PAR_MIN = "min";

    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    private static final String PAR_SEED = "seed";

    /** Maximum interval value,
     obtained from config property {@link #PAR_MAX}. */
    protected final double max;

    /** Minimum interval value,
     obtained from config property {@link #PAR_MIN}. */
    protected final double min;

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}.
     * Defaults to -max. */
    protected final int protocolID;

    private Random random =  new Random();

    public RandomUniformInitializer(String prefix) {
        max = Configuration.getDouble(prefix + "." + PAR_MAX);
        min = Configuration.getDouble(prefix + "." + PAR_MIN, -max);
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
        random.setSeed(Configuration.getInt(prefix + "." + PAR_SEED));
    }

    public boolean execute() {
        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = min + (max - min) * random.nextDouble();
            AggregationProtocol aggregationProtocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            aggregationProtocol.setInput(randomDouble);
        }
        return false;
    }

}
