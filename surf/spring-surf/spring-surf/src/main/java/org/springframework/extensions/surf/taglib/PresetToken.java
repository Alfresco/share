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
package org.springframework.extensions.surf.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>This class provides the "presetToken" custom JSP tag that can only be used as a child tag of the
 * "constructPreset" custom JSP tag. Multiple instances the tag can be used to define token map entries
 * to apply when constructing a Spring Surf preset. It should be used as follows:</p>
 * <pre>
 * <{@code}constructPreset preset="">
 *     <{@code}presetToken key="someKey" value="someValue"><{@code}/presetToken>
 *     <{@code}presetToken key="anotherKey" value="anotherValue"><{@code}/presetToken>
 * <{@code}/constructPreset>
 * </pre>
 *
 * @author David Draper
 */
public class PresetToken extends TagSupport
{
    private static final long serialVersionUID = 2223221597076988546L;

   /**
    * <p>This should be a substitution point in the preset being constructed. For example, if the the
    * preset contains a substitution point <code>${pageid}</code> and the <code>key</code> is set to
    * <code>pageid</code> then that substitution point (and all other with the same name) will be replaced
    * with whatever is set as the <code>value</code> attribute.</p>
    */
    private String key;

    /**
     * <p>This is required to set the <code>key</code> attribute when the custom tag is used.</p>
     * @param key
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * <p>This should be set to the value used to replace all substitution points with the value set in the
     * <code>key</code> attribute.<p>
     */
    private String value;

    /**
     * <p>This is required to set the <code>value</code> attribute when the custom tag is used.</p>
     * @param key
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * <p>Checks that the tag has been used as a direct child of a <code>ConstructPreset</code> tag
     * and calls its <code>addToken</code> method with the <code>key</code> and <code>value</code>
     * attributes that have been provided.</p>
     *
     * @throws JspException If the parent of the tag is not a <code>ConstructPreset</code> tag.
     */
    @Override
    public int doStartTag() throws JspException
    {
        Tag parent = getParent();
        if (parent != null && parent instanceof ConstructPreset)
        {
            ConstructPreset preset = (ConstructPreset) parent;
            preset.addToken(key, value);
        }
        else
        {
            throw new JspException("PresetToken tag must be child of ConstructPreset tag");
        }

        return super.doStartTag();
    }
}
