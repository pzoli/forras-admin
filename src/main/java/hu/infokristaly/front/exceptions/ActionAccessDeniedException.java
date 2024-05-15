package hu.infokristaly.front.exceptions;

public class ActionAccessDeniedException extends Exception {

    private static final long serialVersionUID = 8796244271030277347L;

    public ActionAccessDeniedException(String message) {
        super(message);
    }
}
