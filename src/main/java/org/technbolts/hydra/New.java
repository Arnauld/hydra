package org.technbolts.hydra;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class New {
    
    public static <T> ConcurrentLinkedQueue<T> concurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<T> ();
    }
    
    public static <T> CopyOnWriteArrayList<T> copyOnWriteArrayList() {
        return new CopyOnWriteArrayList<T> ();
    }
    
    public static <T> AtomicReference<T> atomicReference() {
        return new AtomicReference<T>();
    }
    
    public static <T> AtomicReference<T> atomicReference(T value) {
        return new AtomicReference<T>(value);
    }
}
