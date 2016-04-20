package org.alfresco.wcm.client;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ResourceNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = -30710456153210458L;
    
    Set<String> ids = new TreeSet<String>();
    
    public ResourceNotFoundException(String id)
    {
        ids.add(id);
    }
    
    public ResourceNotFoundException(Collection<String> ids)
    {
        ids.addAll(ids);
    }

    public Set<String> getIds()
    {
        return ids;
    }
}
