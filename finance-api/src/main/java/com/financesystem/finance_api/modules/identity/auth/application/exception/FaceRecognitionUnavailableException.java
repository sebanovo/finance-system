package com.financesystem.finance_api.modules.identity.auth.application.exception;

public class FaceRecognitionUnavailableException extends RuntimeException {

    public FaceRecognitionUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
