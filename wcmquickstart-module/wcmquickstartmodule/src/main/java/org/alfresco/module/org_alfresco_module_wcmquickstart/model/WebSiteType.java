package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.namespace.QName;

/**
 * ws:website type behaviours.
 * 
 * @author Brian Remmington
 */
public class WebSiteType implements WebSiteModel
{
    private PolicyComponent policyComponent;
    private SiteHelper siteHelper;
    private NodeService nodeService;
    
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                WebSiteModel.TYPE_WEB_SITE, new JavaBehaviour(this, "onUpdatePropertiesEveryEvent"));
    }

    /**
     * On update properties behaviour, every event
     * 
     * @param nodeRef
     *            node reference
     * @param before
     *            before property values
     * @param after
     *            after property values
     */
    public void onUpdatePropertiesEveryEvent(NodeRef nodeRef, Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        // Only process this node if its name property has changed.
        String nameBefore = (String) before.get(ContentModel.PROP_NAME);
        String nameAfter = (String) after.get(ContentModel.PROP_NAME);
        if ((nameBefore != null && !nameBefore.equals(nameAfter)))
        {
            SiteInfo siteInfo = siteHelper.getRelevantShareSite(nodeRef);
            if (siteInfo != null)
            {
                NodeRef feedbackList = siteHelper.getFeedbackList(nameBefore, siteInfo.getShortName(), false);
                if (feedbackList != null)
                {
                    siteHelper.renameFeedbackList(feedbackList, nameAfter);
                }
            }
        }
    }
}
