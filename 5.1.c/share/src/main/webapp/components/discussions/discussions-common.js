/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

Alfresco.util.discussions = {};

/**
 * Get the topic view page url for a topicId.
 * 
 * @method Alfresco.util.discussions.getTopicViewPage
 * @return {string} url pointing to the topic view page
 */
Alfresco.util.discussions.getTopicViewPage = function(site, container, topicId)
{
   return YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/discussions-topicview?topicId={topicId}&listViewLinkBack=true",
   {
      site: site,
      topicId: topicId
   });
}
