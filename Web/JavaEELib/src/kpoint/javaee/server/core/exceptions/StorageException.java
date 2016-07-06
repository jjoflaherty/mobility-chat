/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.exceptions;

/**
 *
 * @author Steven
 */
public class StorageException extends Exception
{
    public StorageException() {}
    public StorageException(String message) {
        super(message);
    }
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
    public StorageException(Throwable cause) {
        super(cause);
    }

    public StorageException(String method, String message) {
        this(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public StorageException(String method, String message, Throwable throwable) {
        this(ExceptionUtils.concatMethodAndMessage(method, message), throwable);
    }
}
