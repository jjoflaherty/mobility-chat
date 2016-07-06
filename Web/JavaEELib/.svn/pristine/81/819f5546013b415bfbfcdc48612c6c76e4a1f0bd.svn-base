/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.exceptions;

/**
 *
 * @author Steven
 */
public class EntityNotFoundException extends Exception
{
    public EntityNotFoundException() {}
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public EntityNotFoundException(String method, String message) {
        this(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public EntityNotFoundException(String method, String message, Throwable throwable) {
        this(ExceptionUtils.concatMethodAndMessage(method, message), throwable);
    }
}
