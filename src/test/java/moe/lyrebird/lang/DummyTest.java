package moe.lyrebird.lang;

import moe.lyrebird.Lombok;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.Unsafe;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DummyTest {
    
    @Test
    public void simpleTypeTest() {
        Assert.assertEquals(Dummy.getDummy(String.class).getClass(), String.class);
    }
    
    @Test
    public void publicClassConstructor() {
        Assert.assertEquals(Dummy.getDummy(InstantiablePublicClass.class).getClass(), InstantiablePublicClass.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void privateClassConstructor() {
        Assert.assertEquals(Dummy.getDummy(InstantiablePrivateClass.class).getClass(), InstantiablePrivateClass.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void noCastForPrivateClasses() throws Exception {
        Assert.assertEquals(Dummy.getDummy(Unsafe.class).getClass(), Unsafe.class);
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(Dummy.class);
    }
    
    private static class InstantiablePublicClass {
        public InstantiablePublicClass() {
        }
    }
    
    private static class InstantiablePrivateClass {
        private InstantiablePrivateClass() {
        }
    }
    
}