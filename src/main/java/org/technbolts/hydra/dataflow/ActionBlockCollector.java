package org.technbolts.hydra.dataflow;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.technbolts.hydra.New;

public class ActionBlockCollector<T> extends ActionBlock<T> {

    private ConcurrentLinkedQueue<T> elements = New.concurrentLinkedQueue();
    
    public void execute(T message) {
        elements.add(message);
    }
    
    public ConcurrentLinkedQueue<T> getElements() {
        return elements;
    }
}
