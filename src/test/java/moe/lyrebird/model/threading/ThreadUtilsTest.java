package moe.lyrebird.model.threading;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Tristan on 04/03/2017.
 */
public class ThreadUtilsTest {
    @Test
    public void run() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        ThreadUtils.run(() -> ran.set(true));
        Thread.sleep(100);
        Assert.assertTrue(ran.get());
    }
    
    @Test
    public void runCallable() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Future<Boolean> oldValue = ThreadUtils.run(() -> ran.getAndSet(true));
        Assert.assertFalse(oldValue.get());
        Assert.assertTrue(ran.get());
    }
    
    @Test
    public void runLater() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        ThreadUtils.runlater(() -> ran.set(true), 500, MILLISECONDS);
        Thread.sleep(250);
        Assert.assertFalse(ran.get());
        Thread.sleep(400);
        Assert.assertTrue(ran.get());
    }
    
    @Test
    public void runLaterCallable() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Future<Boolean> oldValue = ThreadUtils.runLater(() -> ran.getAndSet(true), 500, MILLISECONDS);
        
        Thread.sleep(250);
        Assert.assertFalse(ran.get());
        Assert.assertFalse(oldValue.isDone());
        Thread.sleep(400);
        Assert.assertTrue(oldValue.isDone());
        Assert.assertFalse(oldValue.get());
        Assert.assertTrue(ran.get());
    }
    
}