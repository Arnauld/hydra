package org.technbolts.hydra.dataflow;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.technbolts.hydra.Objects.s;
import static org.technbolts.hydra.dataflow.BufferBlock.newBufferBlock;
import static org.technbolts.hydra.dataflow.Dataflow.post;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UsecaseEx01Test {
    
    private ActionBlockCollector<String> collect;
    
    @BeforeMethod
    public void setUp () {
        collect = Dataflow.collectorActionBlock();
    }

    @Test
    public void usecase_bufferAndAction () {
        BufferBlock<String> bufferBlock = new BufferBlock<String>();
        bufferBlock.post("Hello");
        bufferBlock.post("every body");
        bufferBlock.linkTo(collect, false);
        
        ConcurrentLinkedQueue<String> collected = collect.getElements();
        assertThat(collected.size(), equalTo(2));
        assertThat(collected, hasItems("Hello", "every body"));
        collected.clear();
        
        bufferBlock.post("What's uppppp?");
        assertThat(collected, hasItems("What's uppppp?"));
    }
    
    @Test
    public void usecase_buffersJoinAndAction () {
        JoinBlock<String, String, String> join = new JoinBlock<String, String, String>() {
            @Override
            protected String assemble(String in1, String in2) {
                return in1 + " " + in2;
            }
        };
        BufferBlock<String> bufferBlock1 = newBufferBlock();
        BufferBlock<String> bufferBlock2 = newBufferBlock();
        bufferBlock1.linkTo(join.asTarget1(), false);
        bufferBlock2.linkTo(join.asTarget2(), false);
        
        join.linkTo(collect, false);
        
        post(bufferBlock1, "Hello", "Hi", "hola", "guten Tag");
        post(bufferBlock2, "Travis", "Carmen", "Pacman");
        
        ConcurrentLinkedQueue<String> collected = collect.getElements();
        assertThat(collected.size(), equalTo(3));
        assertThat(collected, hasItems("Hello Travis", "Hi Carmen", "hola Pacman"));
        collected.clear();
        
        bufferBlock2.post("Dude");
        assertThat(collected, hasItems("guten Tag Dude"));
    }
    
    @Test
    public void usecase_bufferTransformationAndAction () {
        BufferBlock<Integer> bufferBlock = newBufferBlock();
        TransformBlock<Integer, String> trans = new TransformBlock<Integer, String>() {
            @Override
            protected String transform(Integer message) {
                return Integer.toHexString(message);
            }
        };
        bufferBlock.linkTo(trans, false);
        trans.linkTo(collect, false);
        post(bufferBlock, 1, 2, 3, 15, 16);
        
        ConcurrentLinkedQueue<String> collected = collect.getElements();
        assertThat(collected.size(), equalTo(5));
        assertThat(collected, hasItems("1", "2", "3", "f", "10"));
    }
    
    @Test
    public void usecase_bufferBroadcastAndAction () {
        ActionBlockCollector<String> collect2 = Dataflow.collectorActionBlock();

        BroadcastBlock<String> broadcast = new BroadcastBlock<String>();
        BufferBlock<String> bufferBlock = newBufferBlock();
        bufferBlock.linkTo(broadcast, false);
        broadcast.linkTo(collect, false);
        broadcast.linkTo(collect2, false);
        post(bufferBlock, "a", "E", "i", "O");
        
        ConcurrentLinkedQueue<String> collected = collect.getElements();
        assertThat(collected.size(), equalTo(4));
        assertThat(collected, hasItems("a", "E", "i", "O"));
        
        ConcurrentLinkedQueue<String> collected2 = collect2.getElements();
        assertThat(collected2.size(), equalTo(4));
        assertThat(collected2, hasItems("a", "E", "i", "O"));
    }
    
    @Test
    public void usecase_bufferBatchAndAction () {
        ActionBlockCollector<String[]> arrayCollector = Dataflow.collectorActionBlock();
        
        BatchBlock<String> batchBlock = new BatchBlock<String>(3, String.class); 
        BufferBlock<String> bufferBlock = newBufferBlock();
        bufferBlock.linkTo(batchBlock, false);
        batchBlock.linkTo(arrayCollector, false);
        post(bufferBlock, "a", "E", "i", "O", "u", "y", "o");
        
        ConcurrentLinkedQueue<String[]> elements = arrayCollector.getElements();
        assertThat(elements.size(), equalTo(2));
        assertThat(elements, hasItems(s("a", "E", "i"), s("O", "u", "y")));
    }
}
