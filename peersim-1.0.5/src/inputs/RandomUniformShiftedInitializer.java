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
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Sets the input of every node independently sampled
 * from a specified interval but shifts it after sampling a fixed amount.
 * */
public class RandomUniformShiftedInitializer extends RandomUniformInitializer implements Control {
    /**
     * Parameter that defines the amount the values should be shifted
     */
    private static final String PAR_SHIFT = "shift";

    /**
     * The amount the values should be shifted.
     * */
    private final double shift;

    public RandomUniformShiftedInitializer(String prefix) {
        super(prefix);
        shift = Configuration.getDouble(prefix + "." + PAR_SHIFT);
    }

    public boolean execute() {
        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = shift + min + (max - min) * CommonState.r.nextDouble();
            AggregationProtocol aggregationProtocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            aggregationProtocol.setInput(randomDouble);
        }
        return false;
    }

}
