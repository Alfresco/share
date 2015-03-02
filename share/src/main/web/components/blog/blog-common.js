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

/**
 * This file defines functions used across different blog related components
 */

Alfresco.util.blog = {};


/**
 * Generate the REST url for a given blog post
 *
 * @method Alfresco.util.blog.generatePublishingRestURL
 * @param site {string} the site id
 * @param container {string} the container id
 * @param postId {string} the post id/name
 * @return a REST url for publishing the post
 */
Alfresco.util.blog.generatePublishingRestURL = function generatePublishingRestURL(site, container, postId)
{
   return YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}/publishing",
   {
      site: site,
      container: container,
      postId: postId
   });
};

/**
 * Generate a view url for a given site, container and blog post id.
 *
 * @param postId the id/name of the post
 * @return an url to access the post
 */
Alfresco.util.blog.generateBlogPostViewUrl =  function generateBlogPostViewUrl(site, container, postId)
{
   var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postview?postId={postId}&listViewLinkBack=true",
   {
      site: site,
      postId: postId
   });
   return url;
};

/**
 * Generates the status label text for a given blog post
 */
Alfresco.util.blog.generatePostStatusLabel = function generatePostStatusLabel(me, data)
{
   if (data.isDraft)
   {
      return "(" + me._msg("status.draft") + ")";
   }
   else if (data.isUpdated || data.isPublished)
   {
      var status = '';
      if (data.isUpdated)
      {
         status += "(" + me._msg("status.updated") + ") ";
      }

      if (data.isPublished)
      {
         if (data.outOfDate)
         {
            return status + "(" + me._msg("status.published.outofsync") + ")";
         }
         else
         {
            return status + "(" + me._msg("status.published") + ")";
         }
      }
      else
      {
         return status;
      }
   }
   else
   {
      // internally published, no status displayed
      return "";
   }
};

/**
 * Returns the html for the actions for a given blog post.
 * @param me the object that holds the _msg method used for i18n
 * @param data the blog post data
 * @param tagName the tag name to use for the actions. This will either be div or span, depending
 *                whether the actions are for the simple or detailed view.
 */
Alfresco.util.blog.generateBlogPostActions = function generateBlogPostActions(me, data, tagName)
{
   var desc = '';
   // begin actions
   desc += '<div class="nodeEdit">';
   if (data.permissions.edit)
   {
      desc += '<' + tagName + ' class="onEditBlogPost"><a href="#" class="blogpost-action-link-' + tagName + '"><span>' + me._msg("action.edit") + '</span></a></' + tagName + '>';
   }
   if (data.permissions['delete'])
   {
      desc += '<' + tagName + ' class="onDeleteBlogPost"><a href="#" class="blogpost-action-link-' + tagName + '"><span>' + me._msg("action.delete") + '</span></a></' + tagName + '>';
   }
   desc += '</div>';
   return desc;
};

