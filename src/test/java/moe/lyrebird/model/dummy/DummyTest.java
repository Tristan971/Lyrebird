package moe.lyrebird.model.dummy;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Tristan on 22/02/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DummyTest {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void getDummy() throws Exception {
        Assertions.assertTrue(
                Dummy.getDummy(String.class) instanceof String,
                "Dummy was not cast !"
        );
    }
    
}