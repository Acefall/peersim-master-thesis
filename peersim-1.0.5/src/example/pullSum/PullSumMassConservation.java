package example.pullSum;

import approximation.Approximation;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.IllegalParameterException;
import peersim.core.Node;
import java.util.List;
import java.util.Objects;

public class PullSumMassConservation extends PullSum implements CDProtocol, Approximation, PullProtocol {
    private final double keepRatio;
    private static final String PAR_KEEP_RATIO = "keepRatio";

    private final double limit;
    private static final String PAR_LIMIT = "limit";

    private String keepFunction = "";
    private static final String PAR_KEEP_FUNCTION = "keepFunction";

    public PullSumMassConservation(String name){
        super(name);
        keepRatio = Configuration.getDouble(name + "." + PAR_KEEP_RATIO, 0.5);
        limit = Configuration.getDouble(name + "." + PAR_LIMIT, 0.5);
        int keepFunctionId = Configuration.getInt(name + "." + PAR_KEEP_FUNCTION);
        if (keepFunctionId == 1) {
            keepFunction = "numCalls";
        } else if (keepFunctionId == 2) {
            keepFunction = "identity";
        } else if (keepFunctionId == 3) {
            keepFunction = "2PowerR";
        } else if (keepFunctionId == 4) {
            keepFunction = "giveWhatYouCan";
        } else if (keepFunctionId == 5) {
            keepFunction = "hardLimit";
        }
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        if (pullCalls.size() > 0) {
            double keepFactor = keepFactor(keepRatio, keepFunction, pullCalls.size(), getW());
            for (Message pullCall : pullCalls) {
                messagePassing.putOutboundMessage(new PullSumResponse(
                        node,
                        pullCall.getSender(),
                        protocolID,
                        ((1 - keepFactor)/(pullCalls.size())) * getS(),
                        ((1 - keepFactor)/(pullCalls.size())) * getW()));
            }
            setS(keepFactor * getS());
            setW(keepFactor * getW());
        }
    }

    public Object clone() {
        PullSumMassConservation pullSumMC = new PullSumMassConservation(name);
        pullSumMC.messagePassing = new MessagePassing();
        return pullSumMC;
    }

    private double keepFactor(double keepRatio, String keepFunction, int numberOfCalls, double weight){
        if (Objects.equals(keepFunction, "numCalls")) {
            return 1.0/(numberOfCalls+1);
        } else if (Objects.equals(keepFunction, "identity")) {
            return keepRatio;
        } else if (Objects.equals(keepFunction, "2PowerR")) {
            return 1.0 / Math.pow(2, keepRatio);
        } else if (Objects.equals(keepFunction, "giveWhatYouCan")) {
            if(weight <= 1){
                return 1-Math.pow(weight - limit, 2)/weight;
            }else{
                return 1-(weight-1)/weight;
            }

        }  else if (Objects.equals(keepFunction, "hardLimit")) {
            return Math.max(limit/weight, 1.0/(numberOfCalls+1));
        }
        else {
            throw new IllegalParameterException(name, "Unknown keep function '" + keepFunction + "'.");
        }
    }
}
