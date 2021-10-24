package tech.amcg.llf.domain.exception;

public class LlfException extends Exception {

    public LlfException(String errorMessage) {
        super(errorMessage);
    }

    public LlfException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
