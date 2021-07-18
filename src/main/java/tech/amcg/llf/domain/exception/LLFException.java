package tech.amcg.llf.domain.exception;

public class LLFException extends Exception {

    public LLFException(String errorMessage) {
        super(errorMessage);
    }

    public LLFException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
