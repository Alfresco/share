package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Mode
{
    VIEW, EDIT, CREATE;
    
    private static Log logger = LogFactory.getLog(Mode.class);
    
    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }

    public static Mode modeFromString(String modeString)
    {
        if ("create".equalsIgnoreCase(modeString)) {
            return Mode.CREATE;
        }
        else if ("edit".equalsIgnoreCase(modeString))
        {
            return Mode.EDIT;
        }
        else if ("view".equalsIgnoreCase(modeString))
        {
            return Mode.VIEW;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Illegal modeString: " + modeString);
            }
            return null;
        }
    }
    
    public static List<Mode> modesFromString(String commaSeparatedModesString)
    {
        if (commaSeparatedModesString == null)
        {
            return Collections.emptyList();
        }
        List<Mode> result = new ArrayList<Mode>();
        StringTokenizer st = new StringTokenizer(commaSeparatedModesString, ",");
        while (st.hasMoreTokens())
        {
            String nextToken = st.nextToken().trim();
            Mode nextMode = Mode.modeFromString(nextToken);
            result.add(nextMode);
        }
        return result;
    }
}