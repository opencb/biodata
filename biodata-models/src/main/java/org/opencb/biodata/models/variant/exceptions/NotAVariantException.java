package org.opencb.biodata.models.variant.exceptions;

/**
 * Created by fjlopez on 10/04/15.
 */
public class NotAVariantException extends RuntimeException {

    /**
     * Constructs an instance of <code>NotAVariantException</code>
     * for a field, and with the specified detail message.
     */
    public NotAVariantException() {
        super();
    }

    /**
     * Constructs an instance of <code>NotAVariantException</code>
     * for a field, and with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NotAVariantException(String msg) {
        super(msg);
    }

}
