/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.VisitorFeedbackType;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.StartWorkflowActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Comment feedback processor handler.
 * 
 * @author Roy Wetherall
 */
public class ContactFeedbackProcessorHandler extends FeedbackProcessorHandlerBase
{
    /** Logger */
    private static final Log log = LogFactory.getLog(ContactFeedbackProcessorHandler.class);
    
    /** Action service */
    private ActionService actionService;
    
    /** Person service */
    private PersonService personService;
    
    /** Context parser service */
    private ContextParserService contextParserService;
    
    /**
     * Set action service
     * @param actionService action service
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    /**
     * Set the person service
     * @param personService person service
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    /**
     * Set the context parser service
     * @param contextParserService  context parser service
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
        this.contextParserService = contextParserService;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback.FeedbackProcessorHandler#processFeedback(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public void processFeedback(NodeRef feedback)
    {
        // Create the action
        Action action = actionService.createAction(StartWorkflowActionExecuter.NAME);
        action.setParameterValue(StartWorkflowActionExecuter.PARAM_WORKFLOW_NAME, PROCESS_READ_CONTACT);
        
        // Get feedback configuration
        Map<String, String> feedbackConfig = getFeedbackConfiguration(feedback);
        String workflowUser = null;
        if (feedbackConfig != null)
        {
            workflowUser = feedbackConfig.get("feedbackAssignee." + VisitorFeedbackType.CONTACT_REQUEST_TYPE);
        }
        if (workflowUser == null)
        {
            workflowUser = AuthenticationUtil.getAdminUserName();
        }
        String visitorName = (String)nodeService.getProperty(feedback, WebSiteModel.PROP_VISITOR_NAME);
        String visitorEmail = (String)nodeService.getProperty(feedback, WebSiteModel.PROP_VISITOR_EMAIL);
        
        if (visitorName == null)
        {
            if (visitorEmail == null)
            {
                visitorName = I18NUtil.getMessage("wcmqs.contactRequest.unknownPerson");
            }
            else
            {
                int indexOfAt = visitorEmail.indexOf('@');
                visitorName = indexOfAt == -1 ? visitorEmail : visitorEmail.substring(0, indexOfAt);
            }
        }
        
        // Get the assignee
        NodeRef workflowPerson = personService.getPerson(workflowUser);
        action.setParameterValue("bpm:assignee", workflowPerson);
        action.setParameterValue("bpm:workflowDescription", I18NUtil.getMessage("wcmqs.contactRequest.workflowDescription", visitorName));
        
        if (log.isDebugEnabled() == true)
        {
            log.debug("Starting contact request workflow for node " + feedback.toString());
        }
        
        // Start the workflow
        actionService.executeAction(action, feedback);        
    }
    
    /**
     * Get the feeback configuration for the relevant web site
     * @param feedback              feedback node reference
     * @return Map<String, String>  feedback configuration
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getFeedbackConfiguration(NodeRef feedback)
    {
        Map<String, String> result = new TreeMap<String, String>();
        
        NodeRef relevantArticle = (NodeRef)nodeService.getProperty(feedback, PROP_RELEVANT_ASSET);
        if (relevantArticle != null)
        {
            NodeRef website = siteHelper.getRelevantWebSite(relevantArticle);
            if (website != null)
            {
                List<String> feedbackConfig = (List<String>)nodeService.getProperty(website, PROP_WEBSITE_CONFIG);
                if (feedbackConfig != null)
                {
                    for (String configValue : feedbackConfig)
                    {
                        String[] configValues = configValue.split("=");
                        if (configValues.length == 2)
                        {
                            result.put(configValues[0], 
                                       contextParserService.parse(website, configValues[1]));
                        }
                    }
                }
            }
        }
        
        return result;
    }
}
