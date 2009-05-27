package pm.util;

import junit.framework.TestCase;

/**
 * Date: Dec 19, 2006
 * Time: 9:05:36 PM
 */
public class HelperTest extends TestCase {


    public void testGetRoundedOffValue() {
        assertEquals(19.5f, Helper.getRoundedOffValue(19.495f, 2));
        assertEquals(19.5f, Helper.getRoundedOffValue(19.499f, 2));
        assertEquals(19.49f, Helper.getRoundedOffValue(19.492f, 2));
        assertEquals(19.49f, Helper.getRoundedOffValue(19.494f, 2));

    }
}
