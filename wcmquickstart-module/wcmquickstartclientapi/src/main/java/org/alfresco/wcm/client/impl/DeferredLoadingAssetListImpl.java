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
package org.alfresco.wcm.client.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;

public class DeferredLoadingAssetListImpl implements List<Asset>
{
    private AssetFactory assetFactory;
    private Collection<String> assetIds;
    private List<Asset> delegate;

    public DeferredLoadingAssetListImpl(Collection<String> assetIds, AssetFactory assetFactory)
    {
        super();
        this.assetIds = assetIds;
        this.assetFactory = assetFactory;
    }

    public boolean add(Asset e)
    {
        return getDelegate().add(e);
    }

    public void add(int index, Asset element)
    {
        getDelegate().add(index, element);
    }

    public boolean addAll(Collection<? extends Asset> c)
    {
        return getDelegate().addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Asset> c)
    {
        return getDelegate().addAll(index, c);
    }

    public void clear()
    {
        assetIds.clear();
        if (delegate != null)
        {
            delegate.clear();
        }
    }

    public boolean contains(Object o)
    {
        return getDelegate().contains(o);
    }

    public boolean containsAll(Collection<?> c)
    {
        return getDelegate().containsAll(c);
    }

    public boolean equals(Object o)
    {
        return getDelegate().equals(o);
    }

    public Asset get(int index)
    {
        return getDelegate().get(index);
    }

    public int hashCode()
    {
        return getDelegate().hashCode();
    }

    public int indexOf(Object o)
    {
        return getDelegate().indexOf(o);
    }

    public boolean isEmpty()
    {
        return assetIds.isEmpty();
    }

    public Iterator<Asset> iterator()
    {
        return getDelegate().iterator();
    }

    public int lastIndexOf(Object o)
    {
        return getDelegate().lastIndexOf(o);
    }

    public ListIterator<Asset> listIterator()
    {
        return getDelegate().listIterator();
    }

    public ListIterator<Asset> listIterator(int index)
    {
        return getDelegate().listIterator(index);
    }

    public Asset remove(int index)
    {
        return getDelegate().remove(index);
    }

    public boolean remove(Object o)
    {
        return getDelegate().remove(o);
    }

    public boolean removeAll(Collection<?> c)
    {
        return getDelegate().removeAll(c);
    }

    public boolean retainAll(Collection<?> c)
    {
        return getDelegate().retainAll(c);
    }

    public Asset set(int index, Asset element)
    {
        return getDelegate().set(index, element);
    }

    public int size()
    {
        return assetIds.size();
    }

    public List<Asset> subList(int fromIndex, int toIndex)
    {
        return getDelegate().subList(fromIndex, toIndex);
    }

    public Object[] toArray()
    {
        return getDelegate().toArray();
    }

    public <T> T[] toArray(T[] a)
    {
        return getDelegate().toArray(a);
    }

    private List<Asset> getDelegate()
    {
        if (delegate == null)
        {
            delegate = assetFactory.getAssetsById(assetIds, false);
        }
        return delegate;
    }
}
