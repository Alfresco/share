package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Freemarker directive to output a string, truncating it at a certain number of
 * characters. The truncation is done between words. Usage: <@truncate value=xxx
 * chars=nnn/> where xxx is a variable which references string and nnn is the
 * number of characters.
 * 
 * @author Chris Lack
 */
public class TruncateDirective implements TemplateDirectiveModel
{
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException
    {
        if (params.size() != 2)
            return;

        SimpleScalar valueParam = (SimpleScalar) params.get("value");
        SimpleNumber charsParam = (SimpleNumber) params.get("chars");

        if (valueParam != null && charsParam != null)
        {
            // Get the text and chars values
            String text = valueParam.getAsString();
            int chars = charsParam.getAsNumber().intValue();

            // Truncate the string if needed
            if (text.length() > chars)
            {
                BreakIterator bi = BreakIterator.getWordInstance();
                bi.setText(text);
                int firstBefore = bi.preceding(chars);
                text = text.substring(0, firstBefore) + "...";
            }
            env.getOut().write(text);
        }
    }
}
