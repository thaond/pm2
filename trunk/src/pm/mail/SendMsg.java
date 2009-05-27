package pm.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/*
 * Created on Aug 3, 2004
 *
 */
/**
 * @author thiyagu1
 */
public class SendMsg {

    private static final String PASSWORD = "alerts";
    private static final String USERNAME = "PMAlerts";

    public static void send(String to, String from, String subject,
                            String msgText, String host, boolean debug) {

//		sendNonSSL(to, from, subject, msgText, host, debug);
        sendSSL(to, from, subject, msgText, host, debug);
    }

    private static void sendNonSSL(String to, String from, String subject, String msgText, String host, boolean debug) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        if (debug) {
            props.put("mail.debug", host);
        }
        Session session = Session.getInstance(props, null);
        session.setDebug(debug);

        try {
            // create a message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            StringTokenizer stk = new StringTokenizer(to, ";");
            InternetAddress[] address = new InternetAddress[stk.countTokens()];

            for (int i = stk.countTokens() - 1; i >= 0; i--) {
                String token = stk.nextToken();
                address[i] = new InternetAddress(token);
            }
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            // If the desired charset is known, you can use
            // setText(text, charset)
            msg.setText(msgText);

            Transport.send(msg);
        } catch (MessagingException mex) {
            System.out.println("\n--Exception handling in msgsendsample.java");

            mex.printStackTrace();
            System.out.println();
            Exception ex = mex;
            do {
                if (ex instanceof SendFailedException) {
                    SendFailedException sfex = (SendFailedException) ex;
                    Address[] invalid = sfex.getInvalidAddresses();
                    if (invalid != null) {
                        System.out.println("    ** Invalid Addresses");
                        for (int i = 0; i < invalid.length; i++) {
                            System.out.println("         " + invalid[i]);
                        }
                    }
                    Address[] validUnsent = sfex.getValidUnsentAddresses();
                    if (validUnsent != null) {
                        System.out.println("    ** ValidUnsent Addresses");
                        for (int i = 0; i < validUnsent.length; i++) {
                            System.out.println("         " + validUnsent[i]);
                        }
                    }
                    Address[] validSent = sfex.getValidSentAddresses();
                    if (validSent != null) {
                        System.out.println("    ** ValidSent Addresses");
                        for (int i = 0; i < validSent.length; i++) {
                            System.out.println("         " + validSent[i]);
                        }
                    }
                }
                System.out.println();
                if (ex instanceof MessagingException) {
                    ex = ((MessagingException) ex).getNextException();
                } else {
                    ex = null;
                }
            } while (ex != null);
        }
    }

    public static void sendSSL(String to, String from, String subject,
                               String msgText, String host, boolean debug) {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        Properties props = System.getProperties();

        props.put("mail.smtps.host", host);
        props.put("mail.transport.protocol", "smtps");

        props.put("mail.smtps.auth", "true");

        Authenticator auth = new SmtpAuthenticator(USERNAME, PASSWORD);

        props.put("mail.smtps.starttls.enable", "true");

        Session session = Session.getDefaultInstance(props, auth);
        session.setProtocolForAddress("rfc822", "smtps");
        try {
            // create a message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            StringTokenizer stk = new StringTokenizer(to, ";");
            InternetAddress[] address = new InternetAddress[stk.countTokens()];

            for (int i = stk.countTokens() - 1; i >= 0; i--) {
                String token = stk.nextToken();
                address[i] = new InternetAddress(token);
            }
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(msgText);
            msg.saveChanges();
            Transport transport = session.getTransport();
            transport.connect(host, USERNAME, PASSWORD);
            transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();

        }
    }

    public static void main(String[] str) {
        send("thiyagu@gmail.com", "PMAlerts@gmail.com", "subject", "msgText", "smtp.gmail.com", false);
    }

}

class SmtpAuthenticator extends Authenticator {
    private PasswordAuthentication password_auth;

    public SmtpAuthenticator(String smtp_user, String smtp_password) {
        password_auth = new PasswordAuthentication(smtp_user, smtp_password);
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return password_auth;
    }
}

