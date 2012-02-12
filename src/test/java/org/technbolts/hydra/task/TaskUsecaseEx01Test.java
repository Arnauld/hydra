package org.technbolts.hydra.task;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.technbolts.hydra.New;
import org.technbolts.hydra.Threads;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fj.Effect;
import fj.F;
import fj.F2;
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
    
    @Test(timeOut=300)
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
    
    @Test(timeOut=300)
    public void simpleTask_continueWithMultiple() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String[]> ref = New.atomicReference();
        
        TaskEffectAdapter<Unit,Integer> task = Tasks.newTask(new F<Unit,Integer> () {
            public Integer f(Unit unit) {
                return counter.incrementAndGet();
            }
        });
        task.continueWith(new F<Integer, String> () {
            public String f(Integer count) {
                return "Woot-" + count;
            }
        }).continueWith(new F<String,String[]> () {
            public String[] f(String parameter) {
                return new String[] { parameter };
            }
        }).resultCallback(wrapAsEffect(ref))
          .continueWith(new F<String[], Unit>() {
              @Override
            public Unit f(String[] a) {
              latch.countDown();
                return null;
            }
          });
        task.start();
        Integer result = task.awaitTermination();
        assertThat(counter.get(), equalTo(1));
        assertThat(result, equalTo(1));
        latch.await();
        assertThat(ref.get(), notNullValue());
        assertThat(ref.get(), equalTo(new String[] { "Woot-1" }));
    }
    
    @Test(timeOut=3000)
    public void simpleTask_waitChildCompletion() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> ref = New.atomicReference();
        
        TaskEffectAdapter<Unit,Integer> task = Tasks.newTask("ParentTask", new F2<TaskEffectAdapter<Unit,Integer>, Unit,Integer> () {
            public Integer f(TaskEffectAdapter<Unit,Integer> task, Unit unit) {
                task.childTask("ChildTask", new F<Integer, String> () {
                    public String f(Integer count) {
                        return "Woot-" + count;
                    }
                }).resultCallback(wrapAsEffect(ref))
                  .continueWith("Unlatch", new F<String, Unit>() {
                      @Override
                    public Unit f(String a) {
                        latch.countDown();
                        return null;
                    }
                  });
                return counter.incrementAndGet();
            }
        });
        task.start();
        Integer result = task.awaitTermination();
        assertThat(counter.get(), equalTo(1));
        assertThat(result, equalTo(1));
        latch.await();
        assertThat(ref.get(), notNullValue());
        assertThat(ref.get(), equalTo("Woot-1"));
    }
    
    public static <T> TaskResultCallback<T> wrap(final AtomicReference<T> ref) {
        return new TaskResultCallback<T>() {
            public void onResult(T result) {
                if(!ref.compareAndSet(null, result)) {
                    throw new RuntimeException("Result already set!");
                }
            }
        };
    }
    
    public static <T> Effect<T> wrapAsEffect(final AtomicReference<T> ref) {
        return new Effect<T>() {
            public void e(T result) {
                if(!ref.compareAndSet(null, result)) {
                    throw new RuntimeException("Result already set to <" + ref.get() + ">");
                }
            }
        };
    }


}
