package org.technbolts.hydra.task;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.technbolts.hydra.Threads;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fj.Unit;

public class TaskUsecaseEx01Test {
    
    private AtomicInteger counter;
    
    @BeforeMethod
    public void setUp () {
        counter = new AtomicInteger();
    }

    @Test
    public void simpleTask() throws InterruptedException {
        Task<Unit,Integer> task = new Task<Unit,Integer>() {
            public Integer execute(Unit unit) {
                Threads.sleep(4);
                return counter.incrementAndGet();
            }
        };
        task.start();
        Integer result = task.awaitTermination();
        assertThat(counter.get(), equalTo(1));
        assertThat(result, equalTo(1));
    }
    
    @Test(timeOut=100)
    public void simpleTask_continueWith() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        
        Task<Unit,Integer> task = new Task<Unit,Integer>() {
            public Integer execute(Unit unit) {
                Threads.sleep(4);
                return counter.incrementAndGet();
            }
        };
        task.continueWith(new Task<Integer,String> () {
            @Override
            public String execute(Integer parameter) {
                String ret = "Woot-" + parameter;
                latch.countDown();
                return ret;
            }
        });
        task.start();
        Integer result = task.awaitTermination();
        assertThat(counter.get(), equalTo(1));
        assertThat(result, equalTo(1));
        latch.await();
    }

}
