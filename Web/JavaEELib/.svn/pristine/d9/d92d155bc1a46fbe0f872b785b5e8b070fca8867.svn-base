/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.util;

import org.apache.log4j.Logger;
import kpoint.javaee.server.core.exceptions.ExceptionUtils;

/**
 *
 * @author Steven
 */
public class MethodLogger
{
    private Logger logger;

    public MethodLogger(Logger logger) {
        this.logger = logger;
    }

    public void debug(String method, String message) {
        this.logger.debug(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public void debug(String method, String message, Throwable t) {
        this.logger.debug(concatMethodMessageAndCause(method, message, t));
    }

    public void warn(String method, String message) {
        this.logger.warn(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public void warn(String method, String message, Throwable t) {
        this.logger.warn(concatMethodMessageAndCause(method, message, t));
    }

    public void error(String method, String message) {
        this.logger.error(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public void error(String method, String message, Throwable t) {
        this.logger.error(concatMethodMessageAndCause(method, message, t));
    }

    public void fatal(String method, String message) {
        this.logger.fatal(ExceptionUtils.concatMethodAndMessage(method, message));
    }
    public void fatal(String method, String message, Throwable t) {
        this.logger.fatal(concatMethodMessageAndCause(method, message, t));
    }

    private static String concatMethodMessageAndCause(String method, String message, Throwable t) {
        return ExceptionUtils.concatMethodAndMessage(method, message) + ", cause: " + t.getMessage();
    }
}
