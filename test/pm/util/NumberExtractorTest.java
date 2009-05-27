package pm.util;

import junit.framework.TestCase;

public class NumberExtractorTest extends TestCase {

    /*
      * Test method for 'pm.util.NumberExtractor.hasMoreElements()'
      */
    public void testHasMoreElements() {
        NumberExtractor extractor = new NumberExtractor("ABCD");
        assertFalse(extractor.hasMoreElements());

        extractor = new NumberExtractor(null);
        assertFalse(extractor.hasMoreElements());

        extractor = new NumberExtractor("");
        assertFalse(extractor.hasMoreElements());

        extractor = new NumberExtractor("1DF");
        assertTrue(extractor.hasMoreElements());

        extractor = new NumberExtractor("A1DF");
        assertTrue(extractor.hasMoreElements());
        assertTrue(extractor.hasMoreElements());

        extractor = new NumberExtractor("A1.1D2.1F");
        assertTrue(extractor.hasMoreElements());
        extractor.nextElement();
        assertTrue(extractor.hasMoreElements());
        extractor.nextElement();
        assertFalse(extractor.hasMoreElements());

    }

    /*
      * Test method for 'pm.util.NumberExtractor.nextElement()'
      */
    public void testNextElement() {
        NumberExtractor extractor = new NumberExtractor("ABCD");
        assertNull(extractor.nextElement());

        extractor = new NumberExtractor(null);
        assertNull(extractor.nextElement());

        extractor = new NumberExtractor("ABCD");
        assertNull(extractor.nextElement());

        extractor = new NumberExtractor("1A B 2.1C 4.0 D");
        assertEquals(1f, extractor.nextElement());
        assertEquals(2.1f, extractor.nextElement());
        assertEquals(4f, extractor.nextElement());
    }

    public void testIsPercentage() throws Exception {
        NumberExtractor extractor = new NumberExtractor("ABCD");
        assertFalse(extractor.isPercentage());

        extractor = new NumberExtractor("123%");
        extractor.nextElement();
        assertTrue(extractor.isPercentage());

        extractor = new NumberExtractor("123  %");
        extractor.nextElement();
        assertTrue(extractor.isPercentage());

        extractor = new NumberExtractor("123A %");
        extractor.nextElement();
        assertFalse(extractor.isPercentage());

        extractor = new NumberExtractor("123");
        extractor.nextElement();
        assertFalse(extractor.isPercentage());

        extractor = new NumberExtractor(null);
        assertFalse(extractor.isPercentage());
    }


}
