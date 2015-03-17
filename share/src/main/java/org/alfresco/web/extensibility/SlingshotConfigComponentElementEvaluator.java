package org.alfresco.web.extensibility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

import java.util.Map;

/**
 * <p>
 * Evaluator used to decide if a {@code<sub-component>} shall be bound in to a {@code<component>} and {@code<@region>}.
 * </p>
 *
 * <p>
 * Finds the value of the config element in share-config.xml (or other deployed xxx-config.xml files)
 * specificed by the {@code<element>} parameter.
 * </p>
 *
 * <p>
 * If no additional parameters has been provided it will simply test if the value equals "true".
 * If the value instead shall match a different value that value can be specified using a regexp inside the
 * {@code<match>} parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="config.component.evaluator">
 *     <params>
 *         <element>DocumentDetails/document-details/display-web-preview</element>
 *     </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if we are inside a site with an id of "marketing" or "engineering".
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotConfigComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    private static Log logger = LogFactory.getLog(SlingshotConfigComponentElementEvaluator.class);

    // Evaluator parameters
    public static final String ELEMENT = "element";
    public static final String MATCH = "match";

    protected SlingshotEvaluatorUtil util = null;
    protected ConfigService configService = null;


    /**
     * Sets the evaluator util.
     *
     * @param slingshotExtensibilityUtil the evaluator util
     */
    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    /**
     * Sets the config service.
     *
     * @param configService the new config service
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * Decides if we are inside a site or not.
     *
     * @param context
     * @param params
     * @return true if we are in a site and its id matches the {@code<sites>} param (defaults to ".*")
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        String element = util.getEvaluatorParam(params, ELEMENT, null);
        if (element != null)
        {
            String token = null;
            String value = null;
            Config config = null;
            ConfigElement configElement = null;
            String[] tokens = element.split("/");
            int i = 0;
            for (; i < tokens.length; i++)
            {
                token = tokens[i];
                if (!token.isEmpty())
                {
                    if (i == 0)
                    {
                        config = configService.getConfig(token);
                    }
                    else if (i == 1 && config != null)
                    {
                        value = config.getConfigElementValue(token);
                        configElement = config.getConfigElement(token);
                    }
                    else if (i >= 2 && configElement != null)
                    {
                        value = configElement.getChildValue(token);
                        configElement = configElement.getChild(token);
                    }
                }
            }
            if (value != null && i == tokens.length)
            {
                String match = util.getEvaluatorParam(params, MATCH, null);
                if (match != null)
                {
                    return match.matches(value);
                }

                // If no specific parameter instructions was provided just test if the value returns true
                return value.equalsIgnoreCase("true");
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Could not find value for <element>" + element + "</element>");
                }
            }
        }

        // No value was found
        return false;
    }

}
