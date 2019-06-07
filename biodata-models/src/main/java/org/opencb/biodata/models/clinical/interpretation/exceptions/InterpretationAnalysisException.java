package org.opencb.biodata.models.clinical.interpretation.exceptions;

public class InterpretationAnalysisException extends Exception {

    public InterpretationAnalysisException() {}

    public InterpretationAnalysisException(String message) {
        super(message);
    }

    public InterpretationAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterpretationAnalysisException(Throwable cause) {
        super(cause);
    }

    public InterpretationAnalysisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

