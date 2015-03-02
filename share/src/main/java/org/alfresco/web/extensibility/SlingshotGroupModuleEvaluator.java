package org.alfresco.web.extensibility;

import java.util.List;
import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;

public class SlingshotGroupModuleEvaluator implements ExtensionModuleEvaluator
{
    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    /**
     * Checks to see whether or not the current user satisfies the group membership requirements
     * specified.
     */
    @Override
    public boolean applyModule(RequestContext context, Map<String, String> evaluationProperties)
    {
        boolean memberOfAllGroups = getRelationship(context, evaluationProperties);
        List<String> groups = util.getGroups(evaluationProperties.get(SlingshotGroupComponentElementEvaluator.GROUPS));
        boolean isMember = util.isMemberOfGroups(context, groups, memberOfAllGroups);
        boolean negate = getNegation(context, evaluationProperties);
        boolean apply = (isMember && !negate) || (!isMember && negate);
        return apply;
    }

    /**
     * Checks for a request for to negate the ruling. The default is false.
     * @param context
     * @param evaluationProperties
     * @return
     */
    protected boolean getNegation(RequestContext context, Map<String, String> evaluationProperties)
    {
        String negateParam = evaluationProperties.get(SlingshotGroupComponentElementEvaluator.NEGATE);
        return (negateParam != null && negateParam.trim().equalsIgnoreCase(Boolean.TRUE.toString()));
    }
    
    /**
     * Gets the logical relationship between all the groups to test for membership of. By default
     * this boils down to a straight choice between "AND" (must be a member of ALL groups) and "OR"
     * (only needs to be a member of one group)
     *
     * @param context
     * @param evaluationProperties
     * @return
     */
    protected boolean getRelationship(RequestContext context, Map<String, String> evaluationProperties)
    {
        String relationParam = evaluationProperties.get(SlingshotGroupComponentElementEvaluator.RELATION);
        return (relationParam != null && relationParam.trim().equalsIgnoreCase(SlingshotGroupComponentElementEvaluator.AND));
    }

    @Override
    public String[] getRequiredProperties()
    {
        String[] props = { SlingshotGroupComponentElementEvaluator.GROUPS, SlingshotGroupComponentElementEvaluator.RELATION, SlingshotGroupComponentElementEvaluator.NEGATE };
        return props;
    }

}
