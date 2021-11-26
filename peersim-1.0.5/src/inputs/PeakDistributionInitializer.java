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
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Initialises the input of one node to a peak value. All other intputs are zero.
 * */
public class PeakDistributionInitializer implements Control {
    /**
     * The height of the peak
     */
    private static final String PAR_PEAK = "peak";

    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";


    /** The height of the peak,
     obtained from config property {@link #PAR_PEAK}. */
    private final double peak;


    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public PeakDistributionInitializer(String prefix) {
        peak = Configuration.getDouble(prefix + "." + PAR_PEAK);
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    public boolean execute() {
        for (int i = 0; i < Network.size(); ++i) {
            double input;
            if(i == 0){
                input = peak;
            }else{
                input = 0;
            }
            AggregationProtocol aggregationProtocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            aggregationProtocol.setInput(input);
        }
        return false;
    }

}
