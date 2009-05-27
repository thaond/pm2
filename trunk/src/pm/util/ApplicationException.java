/*
 * Created on 14-Feb-2005
 *
 */
package pm.util;

/**
 * @author thiyagu1
 */
public class ApplicationException extends RuntimeException {

    /**
     *
     */
    public ApplicationException() {
        super();
    }

    /**
     * @param message
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
