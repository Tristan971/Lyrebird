package moe.lyrebird.lang.collections;

import moe.lyrebird.Lombok;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tristan on 04/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MapUtilsTest {
    private static final String testKey = "testKey";
    private static final String testValue = "testValue";
    private static final Map<String, String> testMap = new HashMap<>();
    
    @Before
    public void fillMap() {
        testMap.put(testKey, testValue);
    }
    
    @Test
    public void entryFor() throws Exception {
        final Map.Entry<String, String> testEntry = MapUtils.entryFor(testKey, testMap);
        Assert.assertEquals(testEntry.getKey(), testKey);
        Assert.assertEquals(testEntry.getValue(), testValue);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullKeyCheck() throws Exception {
        MapUtils.entryFor(null, testMap);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullMapCheck() throws Exception {
        MapUtils.entryFor(testKey, null);
    }
    
    @Test(expected = InvocationTargetException.class)
    public void utilityClassTest() throws Exception {
        Lombok.utilityClassTest(MapUtils.class);
    }
}