/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.alfresco.po.ElementState.VISIBLE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Locator By annotated with RenderWebElement can be rendered while calling the render on Page.
 *
 * @author Shan Nagarajan
 * @since  2.2
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface RenderWebElement 
{
    /**
     * Render the element based on the {@link ElementState}
     * The default value of {@link ElementState} is Visible, 
     * if  it have to render for other other {@link ElementState} user have to set attribute.
     * 
     * @return {@link ElementState} element state
     */
    ElementState state() default VISIBLE;
}