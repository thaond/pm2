package pm.net;

import junit.framework.TestCase;
import org.htmlparser.util.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Thiyagu
 * @version $Id: NSEExternalTest.java,v 1.3 2007/12/30 15:17:59 tpalanis Exp $
 * @since 22-Oct-2007
 */
public class NSEExternalTest extends TestCase {

    public void testContent() throws ParserException, IOException {
        if (!HTTPHelper.isNetworkAvailable()) return;
        StringBuilder sb = new StringBuilder();
        StringReader reader = new NSE().content();
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        Pattern pattern = Pattern.compile(".*(MARKET OPEN)|(MARKET CLOSE)");
        Matcher matcher = pattern.matcher(sb.toString());
        assertTrue(matcher.find());
    }
}
