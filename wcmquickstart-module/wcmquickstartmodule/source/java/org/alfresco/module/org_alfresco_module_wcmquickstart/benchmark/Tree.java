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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Nick Smith
 * @since 4.0
 *
 */
public class Tree<T> implements Iterable<TreeNode<T>>
{
    private final Map<T, List<T>> links = new TreeMap<T, List<T>>();
    private T root;
    
    public Tree(T root)
    {
        this.root = root;
        links.put(root, new LinkedList<T>());
    }
    
    public void appendChild(T parent, T child)
    {
        List<T> children = getChildrenToAddTo(parent, child);
        children.add(child);
    }
    
    public void insertChild(T parent, T child, int index)
    {
        List<T> children = getChildrenToAddTo(parent, child);
        children.add(index, child);
    }

    private List<T> getChildrenToAddTo(T parent, T child)
    {
        if(links.containsKey(parent)==false)
        {
            throw new IllegalArgumentException("The parent: " +parent + " does not exist!");
        }
        List<T> children = links.get(parent);
        links.put(child, new LinkedList<T>());
        return children;
    }
    
    public List<T> getChildren(T parent)
    {
        return links.get(parent);
    }

    public T getRoot()
    {
        return root;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<TreeNode<T>> iterator()
    {
        return new TreeIterator();
    }
    
    private class TreeIterator implements Iterator<TreeNode<T>>
    {
        private final List<TreeNode<T>> nodes = new LinkedList<TreeNode<T>>();
        private int index = 0;
        
        public TreeIterator()
        {
            addChildNode(null, root);
        }

        private void addChildNode(T parent, T child)
        {
            List<T> grandChildren = links.get(root);
            TreeNode<T> node = new TreeNode<T>(parent, child, grandChildren.isEmpty());
            nodes.add(node);
            for (T grandChild : grandChildren)
            {
                addChildNode(child, grandChild);
            }
        }

        /**
        * {@inheritDoc}
        */
        public boolean hasNext()
        {
            return index < nodes.size()-1;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public TreeNode<T> next()
        {
            if(hasNext()==false)
            {
                throw new IllegalStateException();
            }
            TreeNode<T> next = nodes.get(index-1);
            index++;
            return next;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot remove items from a Tree!");
        }
        
    }
    
}
