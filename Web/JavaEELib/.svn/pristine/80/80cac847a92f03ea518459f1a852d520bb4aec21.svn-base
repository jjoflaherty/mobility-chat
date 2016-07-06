/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.exceptions;

/**
 *
 * @author Steven
 */
public class OperationFailedException extends Exception
{
    public OperationFailedException() {}
    public OperationFailedException(String message) {
        super(message);
    }
    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    public OperationFailedException(Throwable cause) {
        super(cause);
    }

    public OperationFailedException(String method, String message) {
        this(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public OperationFailedException(String method, String message, Throwable throwable) {
        this(ExceptionUtils.concatMethodAndMessage(method, message), throwable);
    }
}
