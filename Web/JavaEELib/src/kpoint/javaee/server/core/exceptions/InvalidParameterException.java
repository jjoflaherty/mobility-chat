/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.exceptions;

/**
 *
 * @author Steven
 */
public class InvalidParameterException extends Exception
{
    public InvalidParameterException() {}
    public InvalidParameterException(String message) {
        super(message);
    }
    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidParameterException(String method, String message) {
        this(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public InvalidParameterException(String method, String message, Throwable throwable) {
        this(ExceptionUtils.concatMethodAndMessage(method, message), throwable);
    }
}
