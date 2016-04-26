package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check a browser userAgent string against a supplied regular expression
 *
 * @author mikeh
 */
public class IsBrowserEvaluator extends BaseEvaluator
{
    private String regex;

    /**
     * Define the regular expression to test against
     *
     * @param regex
     */
    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (regex == null)
        {
            return false;
        }

        try
        {
            String userAgent = getHeader("user-agent");
            if (userAgent != null)
            {
                Pattern p = Pattern.compile(this.regex);
                Matcher m = p.matcher(userAgent);
                return m.find();
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return false;
    }
}
