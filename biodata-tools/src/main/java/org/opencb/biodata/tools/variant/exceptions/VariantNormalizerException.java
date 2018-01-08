package org.opencb.biodata.tools.variant.exceptions;

/**
 * Created by fjlopez on 24/10/17.
 */
public class VariantNormalizerException extends IllegalArgumentException {
    public VariantNormalizerException(String message) {
        super(message);
    }

    public VariantNormalizerException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariantNormalizerException(Throwable cause) {
        super(cause);
    }
}
