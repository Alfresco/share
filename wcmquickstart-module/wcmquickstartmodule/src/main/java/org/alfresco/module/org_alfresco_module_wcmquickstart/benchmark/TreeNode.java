/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

/**
 * @author  Nick Smith
 * @since  4.0
 */
public class TreeNode<T>
{
    private final T parent;
    private final T child;
    private final boolean isLeaf;
    /**
     * @param value
     * @param isLeaf
     */
    public TreeNode(T parent, T child, boolean isLeaf)
    {
        this.parent = parent;
        this.child = child;
        this.isLeaf = isLeaf;
    }
    
    /**
     * @return the value
     */
    public T getChild()
    {
        return child;
    }
    
    /**
     * @return the parent
     */
    public T getParent()
    {
        return parent;
    }
    
    /**
     * @return the isLeaf
     */
    public boolean isLeaf()
    {
        return isLeaf;
    }
    
    public boolean isRoot()
    {
        return parent == null;
    }
}
