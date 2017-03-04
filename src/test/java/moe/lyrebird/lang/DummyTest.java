package moe.lyrebird.lang;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.Unsafe;

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
    
    private static class InstantiablePublicClass {
        public InstantiablePublicClass() {
        }
    }
    
    private static class InstantiablePrivateClass {
        private InstantiablePrivateClass() {
        }
    }
    
}