/*
 * Created on 07-Mar-2005
 *
 */
package pm.util;

import java.io.*;

/**
 * @author thiyagu1
 */
public class FindReplace {

    /**
     * @param file
     * @param oldName
     * @param newName
     */
    public static void doFindReplace(File file, String oldName, String newName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            File tmpFile = new File(file.getAbsoluteFile() + ".tmp");
            PrintWriter pw = new PrintWriter(new FileWriter(tmpFile));
            String line;
            while ((line = br.readLine()) != null) {
                int st = 0;
                int en = line.length();
                StringBuffer sb = new StringBuffer();
                while (st < en) {
                    int en1 = line.indexOf(oldName, st);
                    if (en1 != -1) {
                        sb.append(line.substring(st, en1));
                        sb.append(newName);
                        st = en1 + oldName.length();
                    } else {
                        sb.append(line.substring(st, en));
                        st = en;
                    }
                }
                pw.println(sb.toString());
            }
            pw.close();
            br.close();
            file.delete();
            tmpFile.renameTo(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... str) {
        doFindReplace(new File("c:/test.txt"), "ab", "12345");
    }
}
