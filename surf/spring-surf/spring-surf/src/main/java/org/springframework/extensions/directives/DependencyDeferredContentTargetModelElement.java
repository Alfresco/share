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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.extensions.surf.extensibility.DeferredContentSourceModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.impl.DefaultContentModelElement;

public abstract class DependencyDeferredContentTargetModelElement extends DefaultContentModelElement implements DeferredContentTargetModelElement
{
    public DependencyDeferredContentTargetModelElement(String id, String directiveName)
    {
        super(id, directiveName);
    }

    /**
     * <p>A {@link List} of the {@link DeferredContentSourceModelElement} instances that have registered to be included
     * in the output from this {@link DeferredContentTargetModelElement}. This list will be processed when the content
     * is flushed.</p>
     */
    private List<DeferredContentSourceModelElement> sourceElements = new ArrayList<DeferredContentSourceModelElement>();
    
    /**
     * @return The {@link List} of {@link DeferredContentSourceModelElement} instances registered with this
     * {@link DeferredContentTargetModelElement} instance.
     */
    public List<DeferredContentSourceModelElement> getSourceElements()
    {
        return sourceElements;
    }

    public void registerDeferredSourceElement(DeferredContentSourceModelElement sourceElement)
    {
        if (editMode == -1)
        {
            // If we're not in "edit" mode then new elements can just be added to the end of the list.
            this.sourceElements.add(sourceElement);
        }
        else
        {
            // If we are in "edit" mode then the new element should be added at the index defined by 
            // the editMode variable...
            this.sourceElements.add(this.editMode, sourceElement);
            
            // Whilst in edit mode we should increment the "editMode" variable so that subsequent
            // additions are added AFTER the one just added.
            this.editMode++;
        }
    }
    
    /**
     * <p>Indicates whether or not we're currently in "edit" mode. When in edit mode we will need to insert
     * dependencies at the index specified. When not in "edit" mode all dependencies will be appended to the
     * end of the list.</p>
     */
    private int editMode = -1;
    
    /**
     * <p>This {@link List} is used to keep track of the {@link DeferredContentSourceModelElement} instances currently
     * being edited (it could be more than one if a number of elements are contained within a parent element that is
     * being edited).</p>
     */
    private List<DeferredContentSourceModelElement> sourceElementsBeingEdited = new ArrayList<DeferredContentSourceModelElement>();
    
    /**
     * <p>Moves the {@link DeferredContentTargetModelElement} instance into "editMode". It checks that that the {@link DeferredContentSourceModelElement}
     * argument has previously registered for inclusion in the output and then sets the <code>editMode</code> variable to be the
     * correct index for inserting subsequent requests.</p>
     */
    public void enterEditMode(String mode, DeferredContentSourceModelElement sourceElement)
    {
        // Find the source element in the list and use it's index as a marker to indicate that
        // this is where to insert new dependencies.
        
        // If we're already in edit mode then we need to keep track of all the elements being edited.
        // This is important since we need to know if we're adding before the first or after the last.
        int indexOfEditedElement = this.sourceElements.indexOf(sourceElement);
        if (indexOfEditedElement == -1)
        {
            // The element that claims that it belongs to this list actually doesn't, so should just
            // ignore it...
        }
        else
        {
            // Add the element to the list of those being edited...
            this.sourceElementsBeingEdited.add(sourceElement);
            
            // We now need to set the index...
            if (mode == ExtensibilityDirective.ACTION_AFTER)
            {
                // If the edit action is "AFTER" then we need to set the "editMode" variable as the largest index...
                if (indexOfEditedElement >= this.editMode)
                {
                    this.editMode = indexOfEditedElement + 1;
                }
            }
            else
            {
                // ...for all other actions we need to set the "editMode" variable as the smallest index...
                if (this.editMode < indexOfEditedElement)
                {
                    this.editMode = indexOfEditedElement;
                }
            }
        }
    }

    /**
     * <p>Resets the <code>editMode</code> index to -1 (to indicate that we're out of "edit" mode) and clears the 
     * previously populated {@link List} of {@link DeferredContentSourceModelElement} instances.</p>
     */
    public void exitEditMode()
    {
        this.editMode = -1;
        this.sourceElementsBeingEdited.clear();
    }
    
    /**
     * <p>This {@link Set} is used to keep track of all the dependency resources that have been requested. It
     * is used to check that dependencies are not requested more than once.</p>
     */
    private Set<String> requestedDependencies = new HashSet<String>();
    
    /**
     * <p>Checks whether or not the supplied dependency has already been requested.</p>
     * 
     * @param dep The path to the dependency to check.
     * @return <code>true</code> if the dependency has already been requested and <code>false</code> otherwise.
     */
    public boolean dependencyAlreadyRequested(String dep)
    {
        return this.requestedDependencies.contains(dep);
    }
        
    /**
     * <p>This method can be used to indicate that the supplied dependency has been requested by other means.
     * This is provided to ensure that dependencies requested directly on the output stream (e.g. via the 
     * {@link JavaScriptDependencyDirective}, {@link CssDependencyDirective}, etc.)</p>
     * 
     * @param dep The path to the dependency to mark as requested.
     */
    public void markDependencyAsRequested(String dep)
    {
        this.requestedDependencies.add(dep);
    }
    
    /**
     * <p>This method must be implemented to return the mapping to the resource controller, e.g. "/share/res/"</p>
     * @return
     */
    protected abstract String getResourceControllerMapping();
    
    /**
     * <p>This method can be used to normalise supplied dependencies such that they are all referenced from the
     * same location and that there are no duplicate forward slashes.</p>
     * @param dependency
     * @return
     */
    protected String normaliseDependency(String dependency) {
        // Normalise the path...
        // If the path contains /<application-context>/res/ then remove it...
        if (dependency.toLowerCase().startsWith(DirectiveConstants.HTTP_PREFIX) || dependency.toLowerCase().startsWith(DirectiveConstants.HTTPS_PREFIX))
        {
            // Don't normalise explicit dependency requests
        }
        else
        {
            dependency = dependency.replace("//", "/");
            if (dependency.startsWith(this.getResourceControllerMapping()))
            {
                dependency = dependency.substring(this.getResourceControllerMapping().length());
            }
            else if (dependency.startsWith("/"))
            {
                dependency = dependency.substring(1);
            }
        }
        return dependency;
    }
}
