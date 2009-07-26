package pm.vo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pm.util.PMDate;

public class FinYearTest {
    @Test
    public void testStartDate() throws Exception {
        FinYear finYear = new FinYear(2009);
        assertEquals(new PMDate(1, 4, 2009), finYear.startDate());
    }
}
