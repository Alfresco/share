/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.extensions.directives;

import org.springframework.extensions.surf.extensibility.DeferredContentSourceModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.impl.DefaultContentModelElement;

/**
 * <p>This {@link DeferredContentSourceModelElement} defines the common behaviour for adding CSS and JavaScript dependency requests
 * to the associated {@link DeferredContentTargetModelElement}.</p>
 * @author David Draper
 */
public class DependencyDeferredContentSourceModelElement extends DefaultContentModelElement implements DeferredContentSourceModelElement
{
    /**
     * <p>Indicates whether or not the element has been removed from the model that it was originally added to.</p>
     */
    private boolean removed = false;
    
    /**
     * <p>Updates the <code>removed</code> attribute to indicate that the element is no longer part of the model it 
     * was originally added to. By default there is no other cleanup action necessary.</p>
     */
    public void markAsRemoved()
    {
        this.removed = true;
    }
    
    /**
     * <p>Returns the value of the <code>removed</code> attribute.</p>
     * @return <code>true</code> if the element has been removed and <code>false</code> otherwise.
     */
    public boolean hasBeenRemoved()
    {
        return this.removed;
    }
    
    public DependencyDeferredContentSourceModelElement(String id,
                                                       String directiveName,
                                                       String dependency,
                                                       String group,
                                                       boolean aggregate,
                                                       DeferredContentTargetModelElement targetElement)
    {
        super(id, directiveName);
        this.group = group;
        this.targetElement = targetElement;
        this.dependency = dependency;
        this.aggregate = aggregate;
    }

    /**
     * <p>The dependency represented by this {@link DeferredContentSourceModelElement}</p>
     */
    private String dependency;
    
    /**
     * @return The dependency represented by this {@link DeferredContentSourceModelElement} 
     */
    public String getDependency()
    {
        return this.dependency;
    }

    /**
     * <p>The group to which this {@link DeferredContentSourceModelElement} belongs.</p>
     */
    private String group;
    
    /**
     * @return The name of the group that the dependency has been assigned to.
     */
    public String getGroup()
    {
        return group;
    }
    
    /**
     * Indicates where or not the dependency should be aggregated
     */
    private boolean aggregate = false;
    
    /**
     * @return Whether or not to aggregate the dependency 
     */
    public boolean isAggregate()
    {
        return aggregate;
    }

    /**
     * <p>The {@link DeferredContentTargetModelElement} instance that this {@link DeferredContentSourceModelElement} has
     * registered with.</p> 
     */
    private DeferredContentTargetModelElement targetElement;
    
    /**
     * <p>Returns the the {@link DeferredContentTargetModelElement} associated with this {@link DeferredContentSourceModelElement}</p>
     * 
     * @return
     */
    public DeferredContentTargetModelElement getTargetElement()
    {
        return this.targetElement;
    }
    
    /**
     * <p>Update the associated {@link DeferredContentTargetModelElement} that edit mode has been entered. If the
     * edit action is either to remove or replace the element then the element should be marked as having been 
     * removed.</p>
     */
    public void enterEditMode(String mode)
    {
        // Mark the element as removed if it has been either removed or replaced - this will ensure that the
        // deferred content element doesn't process it...
        if (mode == ExtensibilityDirective.ACTION_REMOVE || mode == ExtensibilityDirective.ACTION_REPLACE)
        {
            this.markAsRemoved();
        }
        
        // Update the deferred target that we're in edit mode...
        DeferredContentTargetModelElement targetElement = getTargetElement();
        if (targetElement != null)
        {
            targetElement.enterEditMode(mode, this);
        }
    }

    /**
     * <p>Update the associated {@link DeferredContentTargetModelElement} that edit mode has been
     * exited.</p>
     */
    public void exitEditMode()
    {
        // Update the deferred target that we're out of edit mode...
        DeferredContentTargetModelElement targetElement = getTargetElement();
        if (targetElement != null)
        {
            targetElement.exitEditMode();
        }
    }

    @Override
    public String toString()
    {
        return "DependencyDeferredContentSourceModelElement [dependency=" + dependency + ", group=" + group
                + ", targetElement=" + targetElement + "]";
    }
}
