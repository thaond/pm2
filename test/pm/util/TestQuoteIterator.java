package pm.util;

import junit.framework.TestCase;

public class TestQuoteIterator extends TestCase {

    public void test() {
    }

    public void _testHasNext() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(1, 1, 2005), "ONGC");
        assertFalse(iterator.hasNext());
        iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(3, 1, 2005), "ONGC");
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    public void _testNext() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(1, 1, 2005), "ONGC");
        assertNull(iterator.next());
        iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(3, 1, 2005), "ONGC");
        assertNotNull(iterator.next());
        assertNull(iterator.next());
    }

    public void _testHasPrevious() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(1, 1, 2005), "ONGC");
        assertFalse(iterator.hasPrevious());
        iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(3, 1, 2005), "ONGC");
        assertFalse(iterator.hasPrevious());
        iterator.next();
        assertTrue(iterator.hasPrevious());
    }

    public void _testPrevious() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(1, 1, 2005), "ONGC");
        assertNull(iterator.previous());
        iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(3, 1, 2005), "ONGC");
        assertNull(iterator.previous());
        iterator.next();
        assertNotNull(iterator.previous());
    }

    public void _testLast() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(1, 1, 2005), "ONGC");
        assertNull(iterator.last());
        iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(5, 1, 2005), "ONGC");
        assertEquals(new PMDate(5, 1, 2005), iterator.last().getDate());
    }

    public void _testMark() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(1, 1, 2005), new PMDate(1, 1, 2005), "ONGC");
        assertNull(iterator.next());
        iterator.mark();
        iterator.reset();
        assertNull(iterator.next());
        iterator = new QuoteIterator(new PMDate(3, 1, 2005), new PMDate(5, 1, 2005), "ONGC");
        iterator.mark();
        iterator.next();
        iterator.next();
        iterator.reset();
        assertEquals(new PMDate(3, 1, 2005), iterator.next().getDate());
        iterator.mark();
        iterator.next();
        iterator.next();
        iterator.reset();
        assertEquals(new PMDate(4, 1, 2005), iterator.next().getDate());
    }

    public void _testMovePtr() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(3, 1, 2005), new PMDate(10, 1, 2005), "ONGC");
        iterator.next();
        iterator.next();
        assertTrue(iterator.movePtr(-2));
        assertEquals(new PMDate(3, 1, 2005), iterator.next().getDate());
        assertTrue(iterator.movePtr(1));
        assertEquals(new PMDate(5, 1, 2005), iterator.next().getDate());
        assertFalse(iterator.movePtr(-4));
        assertTrue(iterator.movePtr(2));
        assertEquals(new PMDate(10, 1, 2005), iterator.next().getDate());
        assertFalse(iterator.movePtr(1));
    }

    public void _testMovePtrToDate() {
        QuoteIterator iterator = new QuoteIterator(new PMDate(3, 1, 2005), new PMDate(10, 1, 2005), "ONGC");
        assertFalse(iterator.movePtrToDate(new PMDate(1, 1, 2005)));
        assertFalse(iterator.movePtrToDate(new PMDate(9, 1, 2005)));
        assertFalse(iterator.movePtrToDate(new PMDate(11, 1, 2005)));
        assertTrue(iterator.movePtrToDate(new PMDate(5, 1, 2005)));
        assertEquals(new PMDate(5, 1, 2005), iterator.next().getDate());
    }

}
