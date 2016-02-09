package org.opencb.biodata.models.variant.annotation.exceptions;

/**
 * Created by parce on 09/11/15.
 */
public class SOTermNotAvailableException extends RuntimeException {
    public SOTermNotAvailableException(String soName) {
        super("SO term " + soName + " not available in class ConsequenceTypeMappings");
    }
}
