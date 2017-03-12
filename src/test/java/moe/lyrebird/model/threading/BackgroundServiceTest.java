package moe.lyrebird.model.threading;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BackgroundServiceTest {
    @Autowired
    private BackgroundService backgroundService;
    
    @Test
    public void run() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        this.backgroundService.run(() -> ran.set(true));
        Thread.sleep(100);
        Assert.assertTrue(ran.get());
    }
    
    @Test
    public void runCallable() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Future<Boolean> oldValue = this.backgroundService.run(() -> ran.getAndSet(true));
        Assert.assertFalse(oldValue.get());
        Assert.assertTrue(ran.get());
    }
    
    @Test
    public void runLater() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        this.backgroundService.runlater(() -> ran.set(true), 500, MILLISECONDS);
        Thread.sleep(250);
        Assert.assertFalse(ran.get());
        Thread.sleep(400);
        Assert.assertTrue(ran.get());
    }
    
    @Test
    public void runLaterCallable() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Future<Boolean> oldValue = this.backgroundService.runLater(
                () -> ran.getAndSet(true), 500, MILLISECONDS
        );
        
        Thread.sleep(250);
        Assert.assertFalse(ran.get());
        Assert.assertFalse(oldValue.isDone());
        Thread.sleep(400);
        Assert.assertTrue(oldValue.isDone());
        Assert.assertFalse(oldValue.get());
        Assert.assertTrue(ran.get());
    }
    
}