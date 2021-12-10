package de.viadee.vpw.analyzer.service;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -7504642655585707286L;

    private final Exception wrappedException;

    ServiceException(final Exception wrappedException) {
        super();
        this.wrappedException = wrappedException;
    }

    public ServiceException(final String message) {
        super(message);
        this.wrappedException = null;
    }

    public ServiceException(final Exception wrappedException, final String message) {
        super(message);
        this.wrappedException = wrappedException;
    }

    public Exception getWrappedException() {
        return wrappedException;
    }

    @Override
    public String toString() {
        return "ServiceException [wrappedException=" + wrappedException + ", message=" + getMessage() + "]";
    }
}
