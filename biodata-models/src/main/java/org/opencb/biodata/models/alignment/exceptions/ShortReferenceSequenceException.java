package org.opencb.biodata.models.alignment.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 5/7/14
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShortReferenceSequenceException extends Exception {

    public ShortReferenceSequenceException() {
    }

    public ShortReferenceSequenceException(String message) {
        super(message);
    }

    public ShortReferenceSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShortReferenceSequenceException(Throwable cause) {
        super(cause);
    }

    public ShortReferenceSequenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
