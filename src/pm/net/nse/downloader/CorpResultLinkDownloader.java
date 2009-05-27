package pm.net.nse.downloader;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import pm.net.HTTPHelper;
import pm.net.nse.AbstractStockDownloadManager;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class CorpResultLinkDownloader extends AbstractHTMLDownloader {

    private static final String baseURL = "http://www.nse-india.com/marketinfo/companyinfo/eod/corp_res.jsp?symbol=";

    private Vector<String> linkList = new Vector<String>();

    protected String stockCode;

    public CorpResultLinkDownloader(AbstractStockDownloadManager manager,
                                    String stockCode) {
        super(manager);
        this.stockCode = stockCode;
    }

    @Override
    protected void performTask() {
        if (logger.isInfoEnabled()) logger.info("Starting result link download for " + stockCode);
        NodeList list = null;
        NodeFilter filter = new NodeClassFilter(LinkTag.class);
        try {
            Parser parser = getParser();
            if (parser != null) {
                list = parser.extractAllNodesThatMatch(filter);
                for (Node node : list.toNodeArray()) {
                    String linkStr = ((LinkTag) node).getLink();
                    if (linkStr.indexOf("results.jsp") != -1) linkList.add(linkStr);
                }
                if (logger.isInfoEnabled()) logger.info("Completed downloading result link for " + stockCode);
            } else {
                error = true;
            }
        } catch (ParserException e) {
            logger.error(e, e);
            error = true;
        }
    }

    Parser getParser() throws ParserException {
        return new HTTPHelper().getParser(this.getURL());
    }

    public String getURL() {
        try {
            return baseURL + HTTPHelper.encode(stockCode);
        } catch (UnsupportedEncodingException e) {
            logger.error(e, e);
            error = true;
            return null;
        }
    }

    public Vector<String> getLinkList() {
        return linkList;
    }

    public String getStockCode() {
        return stockCode;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((stockCode == null) ? 0 : stockCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CorpResultLinkDownloader other = (CorpResultLinkDownloader) obj;
        if (stockCode == null) {
            if (other.stockCode != null)
                return false;
        } else if (!stockCode.equals(other.stockCode))
            return false;
        return true;
    }
}
