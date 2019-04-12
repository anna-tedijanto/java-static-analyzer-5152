package exercism_parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TwoferParserTest {
    @Test
    public void testOptimalFormat() throws Exception{
        assertEquals(true, OptimalFormat.parse(OptimalFormat.getContent("Twofer_2.java")));
    }
}
