package org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Feedback processor handler interface.  Each processor handler deals with a 
 * different type of feedback.
 * 
 * @author Roy Wetherall
 */
public interface FeedbackProcessorHandler
{
    /**
     * Gets the type of feedback this handler deals with.
     * @return  String  feedback type.
     */
    String getFeedbackType();
    
    /**
     * Process given feedback node.
     * @param feedback  node reference to feedback
     */
    void processFeedback(NodeRef feedback);
    
    /**
     * Process feedback callback.  
     * <p>
     * Called once every time the feedback processor has completed
     * processing of all feedback nodes, providing opportunity to do some last minute processing.
     */
    void processorCallback();
}
