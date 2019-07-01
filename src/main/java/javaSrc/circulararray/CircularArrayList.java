package javaSrc.circulararray;

import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E> {

    @Override
    public E get(int index) {



        negative stuff here


        return super.get(index % super.size());
    }
}
