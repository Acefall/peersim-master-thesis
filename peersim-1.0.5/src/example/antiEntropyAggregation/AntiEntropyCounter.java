package example.antiEntropyAggregation;

public class AntiEntropyCounter {
    private AntiEntropyIncrementer increments;
    private AntiEntropyIncrementer decrements;

    public AntiEntropyCounter(boolean leader){
        increments = new AntiEntropyIncrementer();
        decrements = new AntiEntropyIncrementer();
        if (leader){
            increments.setLeader();
            decrements.setLeader();
        }
    }

    public double getCount(){
        return increments.getIncrements()-decrements.getIncrements();
    }

    public void increment(int increment){
        increments.increment(increment);
    }

    public void decrement(int decrement){
        decrements.increment(decrement);
    }

    public void exchange(AntiEntropyCounter counter){
        increments.exchange(counter.increments);
        decrements.exchange(counter.decrements);
    }

    public double getLocalCount(){
        return increments.getLocalCount()-decrements.getLocalCount();
    }

    public AntiEntropyCounter clone(){
        AntiEntropyCounter counter = new AntiEntropyCounter(false);
        counter.increments = increments.clone();
        counter.decrements = decrements.clone();

        return counter;
    }
}
