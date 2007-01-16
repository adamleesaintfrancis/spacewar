package edu.ou.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {
    
    private final E[] array;
    private int index;
    private final int endIndex;
    
    public ArrayIterator(E[] array, int index, int length) {
        assert(index + length < array.length);
        this.array = array;
        this.index = index;
        this.endIndex = index + length;
    }

    public boolean hasNext() {
        return this.index < this.endIndex;
    }
    
    public E next() {
        if (this.index >= this.endIndex)
            throw new NoSuchElementException();
        
        return this.array[this.index++];
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
