import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TwoferParserTest {
    @Test
    public void testTwoferParser() throws Exception{
        // TwoferParser sp = new TwoferParser();
        assertEquals(true, TwoferParser.parse("Twofer_2.java"));
    }
}
