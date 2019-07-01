package javaSrc.circulararray;

import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E> {

    @Override
    public E get(int index) {
        int number = index % (size() - 1);
        if (number < 0) {
            return super.get(size() + number);
        } else {
            return super.get(number);
        }
    }
}
