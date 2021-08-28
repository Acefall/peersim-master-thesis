package example.antiEntropyAggregation;

public class AntiEntropyIncrementer {
    private double a;

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getLocalCount() {
        return localCount;
    }

    public void setLocalCount(double localCount) {
        this.localCount = localCount;
    }

    private double localCount;

    public AntiEntropyIncrementer(){
        setA(0);
        setLocalCount(1);
    }

    public void setLeader(){
        setA(1);
    }

    public void increment(int increment){
        a = a * localCount /(localCount +increment);
        localCount = localCount + increment;
    }

    public double getIncrements(){
        if(a == 0){
            return 0;
        }
        return 1/a;
        //return Math.round(1/a);
    }

    public void exchange(AntiEntropyIncrementer incrementer){
        double a = (getLocalCount()*getA() + incrementer.getLocalCount()*incrementer.getA())/(getLocalCount()+incrementer.getLocalCount());
        setA(a);
    }

    public AntiEntropyIncrementer clone(){
        AntiEntropyIncrementer incrementer = new AntiEntropyIncrementer();
        incrementer.setA(getA());
        incrementer.setLocalCount(getLocalCount());
        return incrementer;
    }


}
