package pm.net;

import org.htmlparser.util.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class NSE {

    private static String url = "http://www.nse-india.com/homepage.htm";

    public boolean isMarketOpen() {
        try {
            StringReader reader = content();
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("MARKET OPEN")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return false;
    }

    StringReader content() throws ParserException {
        return new HTTPHelper().getHTMLContentReader(url);
    }
}
