/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.util;

import org.apache.log4j.Logger;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;

/**
 *
 * @author Steven
 */
public abstract class ParameterValidation
{
    public static void stringNotNullNotEmpty(String string, String method, String param, Logger logger)
    throws InvalidParameterException
    {
        if ((string == null) || (string.trim().length() == 0))
        {
            StringBuilder description = new StringBuilder("Parameter null or empty ");
            description.append(method != null ? "METHOD = '" + method + "' " : "");
            description.append(param != null ? "PARAM = '" + param + "' " : "");

            if (logger != null)
              logger.warn(description);

            throw new InvalidParameterException(description.toString());
        }
    }

    public static void objectNotNull(Object obj, String method, String param, Logger logger)
    throws InvalidParameterException
    {
        if (obj == null)
        {
          StringBuilder description = new StringBuilder("Object references null ");
          description.append(method != null ? "METHOD = '" + method + "' " : "");
          description.append(param != null ? "PARAM = '" + param + "' " : "");

          if (logger != null)
            logger.warn(description);

          throw new InvalidParameterException(description.toString());
        }
    }

    public static void validatesTrue(boolean result, String method, String param, Logger logger)
    throws InvalidParameterException
    {
        if (!result)
        {
            StringBuilder description = new StringBuilder("Parameter evaluates to false ");
            description.append(method != null ? "METHOD = '" + method + "' " : "");
            description.append(param != null ? "PARAM = '" + param + "' " : "");

            if (logger != null)
              logger.warn(description);

            throw new InvalidParameterException(description.toString());
        }
    }

    public static void objectNotNullAndPositive(Integer obj, String method, String param, Logger logger)
    throws InvalidParameterException
    {
        if ((obj == null) || (obj.intValue() < 0))
        {
            StringBuilder description = new StringBuilder("Object references null or negative ");
            description.append(method != null ? "METHOD = '" + method + "' " : "");
            description.append(param != null ? "PARAM = '" + param + "' " : "");

            if (logger != null)
              logger.warn(description);

            throw new InvalidParameterException(description.toString());
        }
    }
    public static void objectNotNullAndPositive(Long obj, String method, String param, Logger logger)
    throws InvalidParameterException
    {
        if ((obj == null) || (obj.longValue() < 0))
        {
            StringBuilder description = new StringBuilder("Object references null or negative ");
            description.append(method != null ? "METHOD = '" + method + "' " : "");
            description.append(param != null ? "PARAM = '" + param + "' " : "");

            if (logger != null)
              logger.warn(description);

            throw new InvalidParameterException(description.toString());
        }
    }
}
