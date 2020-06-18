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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

public class AssetCollectionDefinition
{
    public static enum QueryType 
    {
        lucene("lucene"),
        alfrescoCmis("cmis-alfresco");
        
        private String engineName;
        private QueryType(String engineName)
        {
            this.engineName = engineName;
        }
        public String getEngineName()
        {
            return engineName;
        }
    };
    
    private String name;
    private String title;
    private QueryType queryType = QueryType.alfrescoCmis;
    private String query = null;
    private int maxResults = 0;
    private int queryIntervalMinutes = 1;
    
    public AssetCollectionDefinition()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public QueryType getQueryType()
    {
        return queryType;
    }

    public void setQueryType(QueryType searchType)
    {
        this.queryType = searchType;
    }

    public String getQuery()
    {
        return query;
    }

    /**
     * The query to use if this is a dynamic collection. Note that if placeholders are used then they may
     * either be of the form "${....}" or "%{....}".
     * @param searchQuery
     */
    public void setQuery(String searchQuery)
    {
        this.query = searchQuery == null ? null : searchQuery.trim().replaceAll("\\x25\\x7B", "\\${");
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    public int getQueryIntervalMinutes()
    {
        return queryIntervalMinutes;
    }

    public void setQueryIntervalMinutes(int queryIntervalMinutes)
    {
        this.queryIntervalMinutes = queryIntervalMinutes;
    }
    
}
