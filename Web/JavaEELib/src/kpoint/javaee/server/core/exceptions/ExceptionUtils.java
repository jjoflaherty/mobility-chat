/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.exceptions;

/**
 *
 * @author Steven
 */
public final class ExceptionUtils
{
    public static String concatMethodAndMessage(String method, String message) {
        return method + ": " + message;
    }
}
