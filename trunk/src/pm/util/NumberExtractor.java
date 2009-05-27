package pm.util;

import java.util.Enumeration;

public class NumberExtractor implements Enumeration<Float> {

    private String line;
    private int st = 0;

    public NumberExtractor(String line) {
        this.line = line;
    }

    public boolean hasMoreElements() {
        if (line == null) return false;

        for (int i = st; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i)))
                return true;
        }
        return false;
    }

    public Float nextElement() {
        if (line == null) return null;

        int stNum = -1, enNum = -1;
        int len = line.length();

        for (; st < len; st++) {
            if (Character.isDigit(line.charAt(st))) {
                stNum = st;
                break;
            }
        }
        if (st == len) return null;

        for (; st < len; st++) {
            if (!Character.isDigit(line.charAt(st)) && line.charAt(st) != '.') {
                enNum = st;
                break;
            }
        }
        if (st == len) {
            enNum = st;
        }

        return Float.parseFloat(line.substring(stNum, enNum));
    }

    public int getIndex() {
        return st;
    }

    public boolean isPercentage() {
        if (line == null) return false;

        int len = line.length();

        for (int i = st; i < len; i++) {
            if (Character.isSpaceChar(line.charAt(i))) {
                continue;
            }
            if (line.charAt(i) == '%') {
                return true;
            } else {
                break;
            }
        }

        return false;
    }

}
