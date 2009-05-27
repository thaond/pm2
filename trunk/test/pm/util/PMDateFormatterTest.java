package pm.util;

import junit.framework.TestCase;

import java.util.Calendar;

public class PMDateFormatterTest extends TestCase {

    public final void testParseDD_MMM_YYYY() throws ApplicationException {
        PMDate date;
        date = PMDateFormatter.parseDD_Mmm_YYYY("10-Jan-2004");
        assertEquals(10, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(2004, date.getYear());

        date = PMDateFormatter.parseDD_Mmm_YYYY("1-Jan-2004");
        assertEquals(1, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(2004, date.getYear());

        try {
            date = PMDateFormatter.parseDD_Mmm_YYYY("1Jan-2004");
            assertTrue(false);
        } catch (ApplicationException e) {
        }
    }

    public final void testParseDD_MMM_YY() throws ApplicationException {
        PMDate date;
        date = PMDateFormatter.parseDD_Mmm_YY("10-Jan-04");
        assertEquals(10, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(2004, date.getYear());

        date = PMDateFormatter.parseDD_Mmm_YY("10-Jan-99");
        assertEquals(10, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(1999, date.getYear());

        date = PMDateFormatter.parseDD_Mmm_YY("1-Jan-99");
        assertEquals(1, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(1999, date.getYear());

        try {
            date = PMDateFormatter.parseDD_Mmm_YY("1-Jan99");
            assertFalse(true);
        } catch (ApplicationException e) {
        }
    }

    public final void testParseYYYYMMDD() throws ApplicationException {
        PMDate date;
        date = PMDateFormatter.parseYYYYMMDD("20040110");
        assertEquals(10, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(2004, date.getYear());

        try {
            date = PMDateFormatter.parseYYYYMMDD("2004a110");
            assertFalse(true);
        } catch (ApplicationException e) {
        }
    }

    public final void testParseYYYY_MM_DD() throws ApplicationException {
        PMDate date;
        date = PMDateFormatter.parseYYYY_MM_DD("2004-01-10");
        assertEquals(10, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(2004, date.getYear());

        try {
            date = PMDateFormatter.parseYYYY_MM_DD("2004a110");
            assertFalse(true);
        } catch (ApplicationException e) {
        }
    }

    public final void testParseDD_MM_YYYY() throws ApplicationException {
        PMDate date = PMDateFormatter.parseDD_MM_YYYY("10/01/2004");
        assertEquals(10, date.getDate());
        assertEquals(1, date.getMonth());
        assertEquals(2004, date.getYear());

        try {
            date = PMDateFormatter.parseDD_MM_YYYY("2004a110");
            assertFalse(true);
        } catch (ApplicationException e) {
        }
    }

    public final void testFormatDD_MMM_YY() {
        PMDate date = new PMDate(10, 01, 2004);
        String str = PMDateFormatter.formatDD_MMM_YY(date);
        assertEquals("10-Jan-4", str);
    }

    public final void testFormatYYYYMMDD() {
        PMDate date = new PMDate(10, 01, 2004);
        String str = PMDateFormatter.formatYYYYMMDD(date);
        assertEquals("20040110", str);
        str = PMDateFormatter.formatYYYYMMDD(new PMDate(1, 1, 2004));
        assertEquals("20040101", str);
        str = PMDateFormatter.formatYYYYMMDD(new PMDate(1, 10, 2004));
        assertEquals("20041001", str);
        str = PMDateFormatter.formatYYYYMMDD(new PMDate(10, 10, 1990));
        assertEquals("19901010", str);
    }

    public final void testFormatDDMMYYYY() {
        PMDate date = new PMDate(10, 01, 2004);
        String str = PMDateFormatter.formatDDMMYYYY(date);
        assertEquals("10012004", str);
        str = PMDateFormatter.formatDDMMYYYY(new PMDate(1, 1, 2004));
        assertEquals("01012004", str);
        str = PMDateFormatter.formatDDMMYYYY(new PMDate(1, 10, 2004));
        assertEquals("01102004", str);
        str = PMDateFormatter.formatDDMMYYYY(new PMDate(10, 10, 1990));
        assertEquals("10101990", str);
    }

    public final void testDisplayFormat() {
        String str = PMDateFormatter.displayFormat(new PMDate(10, 01, 2004));
        assertEquals("10/01/2004", str);
        str = PMDateFormatter.displayFormat(new PMDate(1, 1, 2004));
        assertEquals("01/01/2004", str);
        str = PMDateFormatter.displayFormat(new PMDate(1, 10, 2004));
        assertEquals("01/10/2004", str);
        str = PMDateFormatter.displayFormat(new PMDate(10, 10, 1990));
        assertEquals("10/10/1990", str);
    }

    public final void testParseMMMspDD() throws ApplicationException {
        PMDate date;
        date = PMDateFormatter.parseMMMspDD("Mar 16");
        assertEquals(16, date.getDate());
        assertEquals(3, date.getMonth());
        assertEquals(Calendar.getInstance().get(Calendar.YEAR), date.getYear());

        date = PMDateFormatter.parseMMMspDD(" Apr 1");
        assertEquals(1, date.getDate());
        assertEquals(4, date.getMonth());
        assertEquals(Calendar.getInstance().get(Calendar.YEAR), date.getYear());

        date = PMDateFormatter.parseMMMspDD("Apr 1 2004");
        assertEquals(1, date.getDate());
        assertEquals(4, date.getMonth());
        assertEquals(2004, date.getYear());
        try {
            date = PMDateFormatter.parseMMMspDD("2004a110");
            assertFalse(true);
        } catch (ApplicationException e) {
        }

    }

}
