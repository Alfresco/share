package org.alfresco.web.config.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Visibility
{
    SHOW, HIDE;

    private static Log logger = LogFactory.getLog(Visibility.class);
    
    public static Visibility visibilityFromString(String visibilityString)
    {
        if (visibilityString.equalsIgnoreCase("show")) {
            return Visibility.SHOW;
        }
        else if (visibilityString.equalsIgnoreCase("hide"))
        {
            return Visibility.HIDE;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Illegal visibilityString: ")
                    .append(visibilityString);
                logger.debug(msg.toString());
            }
            return null;
        }
    }
}