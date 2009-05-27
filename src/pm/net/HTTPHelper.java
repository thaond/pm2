package pm.net;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import pm.util.AppConfig;

import java.io.*;
import java.net.*;
import java.security.Security;
import java.util.Map;

/*
 * Created on Aug 3, 2004
 *
 */
/**
 * @author thiyagu1
 */
public class HTTPHelper {
    private static Logger logger = Logger.getLogger(HTTPHelper.class);

    public Reader getData(String urls) {
        try {
            return getDataWithoutExpFilter(urls);
        } catch (Exception e) {
            logger.error(e, e);
            return null;
        }
    }

    public Reader getDataWithoutExpFilter(String urls) throws IOException {
        setProxy();
        URL url = new URL(urls);
        url.openConnection().connect();
        return new InputStreamReader(url.openConnection().getInputStream());
    }

    public InputStream getDataStream(String urls) {
        setProxy();
        try {
            URL url = new URL(urls);
            URLConnection conn = url.openConnection();
            conn.setDefaultUseCaches(false);
            conn.connect();
            return conn.getInputStream();
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace(System.out);
            return null;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static void setProxy() {
        java.util.Properties prop = System.getProperties();
        if (!AppConfig.proxyServer.Value.equals("")) {
            prop.put("proxySet", "true");
            prop.put("proxyHost", AppConfig.proxyServer.Value);
            prop.put("proxyPort", AppConfig.proxyPort.Value);
        }
    }

    public void setMyProxy() {
        java.util.Properties prop = System.getProperties();
        if (!AppConfig.proxyServer.Value.equals("")) {
            prop.put("proxySet", "true");
            prop.put("proxyHost", AppConfig.proxyServer.Value);
            prop.put("proxyPort", AppConfig.proxyPort.Value);
        }
    }

    public static boolean isExists(String urls) {
        setProxy();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) (new URL(urls)).openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getResponseCode() == 200;
        } catch (MalformedURLException e) {
            e.printStackTrace(System.out);
            return false;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                }
            }
        }
    }

    public URLConnection getConnection(String strURL) {
        URL url;
        try {
            setProxy();
            url = new URL(strURL);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(1000 * 60 * 2);
            conn.setReadTimeout(1000 * 60 * 2);
            return conn;
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }

    public static String encode(String stockCode) throws UnsupportedEncodingException {
        return URLEncoder.encode(stockCode, "UTF-8");
    }

    public static String decode(String string) throws UnsupportedEncodingException {
        return URLDecoder.decode(string, "UTF-8");
    }

    public Parser getParser(String url) throws ParserException {
        URLConnection conn = getConnection(url);
        Parser parser = conn != null ? new Parser(conn) : null;
        return parser;
    }

    public StringReader getHTMLContentReader(String url) throws ParserException {
        Parser parser = getParser(url);
        StringBean sb = new StringBean();
        sb.setLinks(false);
        parser.visitAllNodesWith(sb);
        StringReader reader = new StringReader(sb.getStrings());
        return reader;
    }

    public StringReader getHTMLContentReaderUsingPost(String url, String content) throws ParserException {
        try {
            BufferedReader br = new BufferedReader(getContentUsingPost(url, content, null));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            Parser parser = new Parser();
            parser.setInputHTML(sb.toString());
            StringBean stringBean = new StringBean();
            stringBean.setLinks(false);
            parser.visitAllNodesWith(stringBean);
            return new StringReader(stringBean.getStrings());
        } catch (ProtocolException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        return null;
    }

    public InputStreamReader getContentUsingPost(String url, String content, Map<String, String> headers) throws IOException {
        return new InputStreamReader(postData(url, content, headers).getInputStream());
    }

    public HttpURLConnection postData(String url, String content, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = getPostConnection(url);
        if (headers != null) addHeaders(conn, headers);
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(content);
        out.flush();
        out.close();
        return conn;
    }

    private void addHeaders(HttpURLConnection conn, Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            conn.addRequestProperty(header.getKey(), header.getValue());
        }
    }

    public HttpURLConnection getPostConnection(String url) throws IOException {
        setProxy();
        if (url.toLowerCase().startsWith("https")) setupHttps();

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(2000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
        conn.addRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        return conn;
    }

    private void setupHttps() {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        System.getProperties().put("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
    }

    public static boolean isNetworkAvailable() {
        boolean status = false;
        try {
            Socket socket = new Socket("www.google.com", 80);
            status = socket.isConnected();
            socket.close();
        } catch (IOException e) {
            //Do Nothing
        }
        return status;
    }

}
