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

package example.tsHistMean;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;


public class RandomUniformInitializer implements Control {

    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------

    /**
     * The upper bound of the values.
     *
     * @config
     */
    private static final String PAR_MAX = "max";

    /**
     * The lower bound of the values. Defaults to -max.
     *
     * @config
     */
    private static final String PAR_MIN = "min";

    /**
     * The protocol to operate on.
     *
     * @config
     */
    private static final String PAR_PROT = "protocol";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /** Maximum interval value,
     obtained from config property {@link #PAR_MAX}. */
    private final double max;

    /** Manimum interval value,
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
        System.out.println("Executing RandomUniformInitializer");
        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = min + (max - min) * CommonState.r.nextDouble();
            TsHistMean tsHistMean = (TsHistMean) Network.get(i).getProtocol(protocolID);
            tsHistMean.setInput(randomDouble);
        }
        return false;
    }

}
