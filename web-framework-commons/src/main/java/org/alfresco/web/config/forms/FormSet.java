package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a &lt;set&gt; element within a &lt;form&gt; element.
 * 
 * @author Neil McErlean.
 */
public class FormSet
{
    private final String setId;
    private final String parentId;
    private final String appearance;
    private final String label;
    private final String labelId;
    private final String template;
    
    /**
     * This field will be null for a 'root' set.
     */
    private FormSet parent;
    private List<FormSet> children = new ArrayList<FormSet>();
    
    public FormSet(String setId)
    {
        this(setId, null, null, null, null, null);
    }
    
    public FormSet(String setId, String parentId, String appearance,
             String label, String labelId)
   {
        this(setId, parentId, appearance, label, labelId, null);
   }
    
    public FormSet(String setId, String parentId, String appearance,
                   String label, String labelId, String template)
    {
        this.setId = setId;
        this.parentId = parentId;
        this.appearance = appearance;
        this.label = label;
        this.labelId = labelId;
        this.template = template;
    }
    public String getSetId()
    {
        return setId;
    }
    public String getParentId()
    {
        return parentId;
    }
    public String getAppearance()
    {
        return appearance;
    }

    public String getLabel()
    {
        return this.label;
    }
    
    public String getLabelId()
    {
        return this.labelId;
    }
    
    public String getTemplate()
    {
        return this.template;
    }
    
    public FormSet getParent()
    {
        return this.parent;
    }

    public FormSet[] getChildren()
    {
        return this.getChildrenAsList().toArray(new FormSet[0]);
    }

    public List<FormSet> getChildrenAsList()
    {
        return Collections.unmodifiableList(this.children);
    }
    
    void setParent(FormSet parentObject)
    {
        this.parent = parentObject;
    }
    
    void addChild(FormSet newChild)
    {
        this.children.add(newChild);
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("Set: id='").append(setId).append("' parentId='")
            .append(parentId).append("' appearance='").append(appearance)
            .append("' label='").append(label).append("' labelId='").append(labelId)
            .append("' template='").append(template).append("'");

        return result.toString();
    }
}