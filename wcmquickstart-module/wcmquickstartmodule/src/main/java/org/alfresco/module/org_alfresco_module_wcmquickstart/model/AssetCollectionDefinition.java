/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
