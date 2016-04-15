package org.alfresco.web.scripts;

import org.alfresco.web.evaluator.Evaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * @author mikeh
 */
public class ActionEvaluatorHelper extends BaseProcessorExtension implements ApplicationContextAware
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
     * Returns an evaluator given it's bean reference
     * 
     * @param evaluatorName     Bean reference of evaluator
     * @return Evaluator
     */
    public Evaluator getEvaluator(String evaluatorName)
    {
        try
        {
            Evaluator evaluator = (Evaluator) applicationContext.getBean(evaluatorName);
            if (evaluator instanceof Evaluator == false)
            {
                logger.warn("Bean with id '" + evaluatorName + "' does not implement Evaluator interface.");
                return null;
            }
            return evaluator;
        }
        catch (Exception e)
        {
            logger.warn("Evaluator '" + evaluatorName + "' not found.");
            if (logger.isDebugEnabled())
            {
                logger.debug("Exception when trying to get evaluator '" + evaluatorName + "':", e);
            }
        }
        return null;
    }
}
