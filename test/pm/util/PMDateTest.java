package pm.util;

import junit.framework.TestCase;

public class PMDateTest extends TestCase {

    /*
      * Test method for 'pm.util.PMDate.getPreviousDate()'
      */
    public void testGetPreviousDate() {
        assertEquals(new PMDate(28, 2, 2005), new PMDate(1, 3, 2005).previous());
        assertEquals(new PMDate(1, 2, 2005), new PMDate(2, 2, 2005).previous());
        assertEquals(new PMDate(31, 12, 2004), new PMDate(1, 1, 2005).previous());
    }

    public void testGetNextDate() {
        assertEquals(new PMDate(1, 3, 2005), new PMDate(28, 2, 2005).next());
        assertEquals(new PMDate(2, 2, 2005), new PMDate(1, 2, 2005).next());
        assertEquals(new PMDate(1, 1, 2005), new PMDate(31, 12, 2004).next());
    }

    public void testIsWeekend() {
        assertTrue(new PMDate(31, 12, 2006).isWeekend());
        assertFalse(new PMDate(1, 1, 2007).isWeekend());
        assertFalse(new PMDate(2, 1, 2007).isWeekend());
        assertFalse(new PMDate(3, 1, 2007).isWeekend());
        assertFalse(new PMDate(4, 1, 2007).isWeekend());
        assertFalse(new PMDate(5, 1, 2007).isWeekend());

        assertTrue(new PMDate(1, 1, 2006).isWeekend());
        assertTrue(new PMDate(27, 1, 2007).isWeekend());
        assertTrue(new PMDate(28, 1, 2007).isWeekend());
    }

    public void testQuaterStartDate() {
        PMDate q1StartDate = new PMDate(1, 1, 2007);
        assertEquals(q1StartDate, new PMDate(1, 1, 2007).quaterStartDate());
        assertEquals(q1StartDate, new PMDate(2, 2, 2007).quaterStartDate());
        assertEquals(q1StartDate, new PMDate(31, 3, 2007).quaterStartDate());

        PMDate q2StartDate = new PMDate(1, 4, 2007);
        assertEquals(q2StartDate, new PMDate(1, 4, 2007).quaterStartDate());
        assertEquals(q2StartDate, new PMDate(2, 5, 2007).quaterStartDate());
        assertEquals(q2StartDate, new PMDate(15, 6, 2007).quaterStartDate());

        PMDate q3StartDate = new PMDate(1, 7, 2007);
        assertEquals(q3StartDate, new PMDate(15, 7, 2007).quaterStartDate());
        assertEquals(q3StartDate, new PMDate(12, 8, 2007).quaterStartDate());
        assertEquals(q3StartDate, new PMDate(1, 9, 2007).quaterStartDate());

        PMDate q4StartDate = new PMDate(1, 10, 2006);
        assertEquals(q4StartDate, new PMDate(1, 10, 2006).quaterStartDate());
        assertEquals(q4StartDate, new PMDate(21, 11, 2006).quaterStartDate());
        assertEquals(q4StartDate, new PMDate(31, 12, 2006).quaterStartDate());

    }

}
