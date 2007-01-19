package edu.ou.utils;

/**
 * A Tuple is simply a pair of objects.  Tuples can be chained together to form
 * lisp-like lists or trees, or can simply hold objects for a convenient return type
 * for functions.
 */
public class Tuple<A, B> {
    private A head;
    private B tail;

    public Tuple(A head, B tail) {
        this.head = head;
        this.tail = tail;
    }

    /**
     * Set the first element of the tuple.
     * @param head The new first element of the tuple.
     */
    public void setHead(A head) {
        this.head = head;
    }

    /**
     * Get the first element of the tuple.
     * @return The first element of the tuple.
     */
    public A head() {
        return head;
    }

    /**
     * Set the second element of the tuple.
     * @param tail The new second element of the tuple.
     */
    public void setTail(B tail) {
        this.tail = tail;
    }

    /**
     * Get the second element of the tuple.
     * @return The second element of the tuple.
     */
    public B tail() {
        return tail;
    }

}
