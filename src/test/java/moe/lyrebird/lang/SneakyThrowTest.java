package moe.lyrebird.lang;

import javaslang.control.Try;
import moe.lyrebird.Lombok;
import moe.lyrebird.lang.SneakyThrow.ThrowingSupplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SneakyThrowTest {
    private static final ThrowingSupplier<?> throwingSupplier = () -> {
        throw new Exception();
    };
    
    private static final ThrowingSupplier<?> nonThrowingSupplier = () -> {
        return 0;
    };
    
    @Test(expected = Exception.class)
    public void unchecked() throws Exception {
        SneakyThrow.unchecked(throwingSupplier);
    }
    
    @Test
    public void uncheckedWithException() throws Exception {
        final Try<?> tryFailing = SneakyThrow.tryUnchecked(throwingSupplier);
        Assert.assertFalse(tryFailing.isSuccess());
    }
    
    @Test
    public void uncheckedWithoutException() throws Exception {
        final Try<?> successfulTry = SneakyThrow.tryUnchecked(nonThrowingSupplier);
        Assert.assertTrue(successfulTry.isSuccess());
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(SneakyThrow.class);
    }
}