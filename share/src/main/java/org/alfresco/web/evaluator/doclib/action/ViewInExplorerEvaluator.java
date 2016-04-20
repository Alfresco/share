package org.alfresco.web.evaluator.doclib.action;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;

/**
 * Evaluates whether a repositoryUrl config value has been set and that we're not in portlet mode
 *
 * @author mikeh
 */
public class ViewInExplorerEvaluator extends BaseEvaluator
{
    private static final String CONFIG_CONDITION_DOCUMENTLIBRARY = "DocumentLibrary";
    private static final String CONFIG_ELEMENT_REPOSITORY_URL = "repository-url";

    private ConfigService configService;

    /**
     * Config Service setter
     *
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return (getConfigValue(CONFIG_CONDITION_DOCUMENTLIBRARY, CONFIG_ELEMENT_REPOSITORY_URL) != null && !getIsPortlet());
    }

    /**
     * Retrieve config value
     *
     * @param condition Config section
     * @param elementName Element within section
     * @return Object || null
     */
    protected Object getConfigValue(String condition, String elementName)
    {
        Config config = configService.getConfig(condition);
        if (config == null)
        {
            return null;
        }

        return config.getConfigElementValue(elementName);
    }
}
