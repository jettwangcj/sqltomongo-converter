package cn.org.wangchangjiu.sqltomongo.core.exception;

public class NotSupportFunctionException extends RuntimeException {

    public NotSupportFunctionException() {
        super();
    }

    public NotSupportFunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportFunctionException(String message) {
        super(message);
    }

    public NotSupportFunctionException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
