package org.alfresco.web.scripts;

import org.alfresco.web.evaluator.Evaluator;
import org.alfresco.web.resolver.doclib.DoclistActionGroupResolver;
import org.alfresco.web.resolver.doclib.DoclistDataUrlResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * @author ewinlof
 */
public class ResolverHelper extends BaseProcessorExtension implements ApplicationContextAware
{
    private static Log logger = LogFactory.getLog(ActionEvaluatorHelper.class);

    protected ApplicationContext applicationContext = null;

    /*
     * Set ApplicationContext
     *
     * @param applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /*
     * Returns a DoclistDataUrlResolver bean given it's bean reference
     *
     * @param resolverName     Bean reference of resolver
     * @return DoclistDataUrlResolver
     */
    public DoclistDataUrlResolver getDoclistDataUrlResolver(String resolverName)
    {
        try
        {
            DoclistDataUrlResolver resolver = (DoclistDataUrlResolver) applicationContext.getBean(resolverName);
            if (resolver == null)
            {
                logger.warn("Bean with id '" + resolverName + "' does not implement DoclistDataUrlResolver interface.");
                return null;
            }
            return resolver;
        }
        catch (Exception e)
        {
            logger.warn("DoclistDataUrlResolver '" + resolverName + "' not found.");
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception when trying to get doclistDataUrlResolver '" + resolverName + "':", e);
            }
        }
        return null;
    }

    /*
     * Returns a DoclistDataUrlResolver bean given it's bean reference
     *
     * @param resolverName     Bean reference of resolver
     * @return DoclistActionGroupResolver
     */
    public DoclistActionGroupResolver getDoclistActionGroupResolver(String resolverName)
    {
        try
        {
            DoclistActionGroupResolver resolver = (DoclistActionGroupResolver) applicationContext.getBean(resolverName);
            if (resolver == null)
            {
                logger.warn("Bean with id '" + resolverName + "' does not implement DoclistActionGroupResolver interface.");
                return null;
            }
            return resolver;
        }
        catch (Exception e)
        {
            logger.warn("DoclistActionGroupResolver '" + resolverName + "' not found.");
        }
        return null;
    }

}
