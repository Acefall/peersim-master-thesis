package protocols.sampling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CircularBuffer<T> {
    private final List<T> elements;
    private final int size;
    private int recent = -1;
    private int oldest = -1;

    public CircularBuffer(int size){
        this.size = size;
        elements = new ArrayList<>(size);
    }

    public void add(T element){
        recent = (recent + 1) % size;
        elements.set(recent, element);

        if(oldest == -1){
            oldest = 0;
        }else if(recent == oldest){
            oldest = (oldest + 1) % size;
        }
    }

    public int count(){
        if(recent == -1){
            return 0;
        }

        if(recent >= oldest){
            return recent - oldest + 1;
        }else{
            return size;
        }
    }

    public Iterator<T> iterator() {
        return new CircularBufferIterator<>(this);
    }

    public class CircularBufferIterator<T> implements Iterator<T> {
        private int currentIndex = -1;
        private final CircularBuffer<T> circularBuffer;

        public CircularBufferIterator(CircularBuffer<T> circularBuffer){
            this.circularBuffer = circularBuffer;
        }

        @Override
        public boolean hasNext() {
            if(circularBuffer.count() == 0){
                return false;
            } else if(currentIndex == -1){
                return true;
            }

            return currentIndex != oldest;
        }

        @Override
        public T next() {
            if(count() == 0){
                return null;
            }

            if(currentIndex == -1){
                currentIndex = recent;
            }else {
                currentIndex = Math.floorMod(currentIndex - 1, circularBuffer.size);
            }
            return circularBuffer.elements.get(currentIndex);
        }
    }
}



