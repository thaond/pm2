package pm.util;

import junit.framework.TestCase;

import java.util.Calendar;

public class TestPMDate extends TestCase {

    /*
      * Class under test for void PMDate(Calendar)
      */
    public final void testPMDateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(2004, 1, 11);
        PMDate date = new PMDate(cal);
        assertEquals(2004, date.getYear());
        assertEquals(2, date.getMonth());
        assertEquals(11, date.getDate());
    }

    public final void testGetCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(2004, 1, 11);
        PMDate date = new PMDate(cal);
        Calendar newCal = date.getCalendar();
        assertEquals(2004, newCal.get(Calendar.YEAR));
        assertEquals(1, newCal.get(Calendar.MONTH));
        assertEquals(11, newCal.get(Calendar.DATE));
    }

}
