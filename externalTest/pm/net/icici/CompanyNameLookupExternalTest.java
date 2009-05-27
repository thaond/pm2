package pm.net.icici;

import junit.framework.TestCase;

public class CompanyNameLookupExternalTest extends TestCase {

    public void testLookup() {
        assertEquals("Reliance Industries Ltd.", new CompanyNameLookup("RELIND").lookup());
    }

}
