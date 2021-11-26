// Author: Alexander Weinmann uni@aweinmann.de
package protocols.approximation;

import protocols.AggregationProtocol;

/**
 * Approximator that uses two values s and n to approximate the mean.
 * This class is extended by a group of aggregation algorithms.
 */
public abstract class SNApproximation extends AggregationProtocol implements Approximation {
    private double s;
    private double n;

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    /**
     * Compute the protocols.approximation by S/N.
     *
     * @return S/N
     */
    @Override
    public double getApproximation() {
        return getS() / getN();
    }

}
