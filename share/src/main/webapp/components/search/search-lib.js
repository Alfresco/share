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
 * Base component for various Search related components.
 * 
 * Provides helper functions to generate search related URLs, text and image thumbnails
 * 
 * @namespace Alfresco.component
 * @class Alfresco.component.SearchBase
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
       $msg = Alfresco.util.message;

   /**
    * Alfresco.component.SearchBase constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {object} The new instance
    * @constructor
    */
   Alfresco.component.SearchBase = function SearchBase_constructor(name, htmlId)
   {
      Alfresco.component.SearchBase.superclass.constructor.call(this, name, htmlId);

      return this;
   };

   YAHOO.extend(Alfresco.component.SearchBase, Alfresco.component.Base,
   {
      /**
       * Constructs the completed browse url for a record.
       * @param record {string} the record
       */
      getBrowseUrlForRecord: function(record)
      {
         var name = record.getData("name"),
            type = record.getData("type"),
            site = record.getData("site"),
            path = record.getData("path"),
            nodeRef = record.getData("nodeRef"),
            container = record.getData("container"),
            fromDate = record.getData("fromDate");

         return this.getBrowseUrl(name, type, site, path, nodeRef, container, fromDate);
      },

      /**
       * Constructs the completed browse url.
       * @param name {string} the name
       * @param type {string} the type
       * @param site {string} the site
       * @param path {string} the path
       * @param nodeRef {string} the nodeRef
       * @param container {string} the container
       * @param date{string} the  event date
       */
      getBrowseUrl: function(name, type, site, path, nodeRef, container, date)
      {
         var url = null;

         switch (type)
         {
            case "document":
            {
               url = "document-details?nodeRef=" + nodeRef;
               break;
            }

            case "folder":
            {
               if (path !== null)
               {
                  if (site)
                  {
                     url = "documentlibrary?path=" + encodeURIComponent(this.buildSpaceNamePath(path.split("/"), name));
                  }
                  else
                  {
                     url = "repository?path=" + encodeURIComponent(this.buildSpaceNamePath(path.split("/").slice(2), name));
                  }
               }
               break;
            }

            case "blogpost":
            {
               url = "blog-postview?postId=" + encodeURIComponent(name);
               break;
            }

            case "forumpost":
            {
               url = "discussions-topicview?topicId=" + encodeURIComponent(name);
               break;
            }

            case "calendarevent":
            {
               url = container + "?date=" + Alfresco.util.formatDate(date, "yyyy-mm-dd");
               break;
            }

            case "wikipage":
            {
               url = "wiki-page?title=" + encodeURIComponent(name);
               break;
            }

            case "link":
            {
               url = "links-view?linkId=" + encodeURIComponent(name);
               break;
            }

            case "datalist":
            case "datalistitem":
            {
               url = "data-lists?list=" + encodeURIComponent(name);
               break;
            }
         }

         if (url !== null)
         {
            // browse urls always go to a page. We assume that the url contains the page name and all
            // parameters. Add the absolute path and the optional site param
            if (site)
            {
               url = Alfresco.constants.URL_PAGECONTEXT + "site/" + site.shortName + "/" + url;
            }
            else
            {
               url = Alfresco.constants.URL_PAGECONTEXT + url;
            }
         }

         return (url !== null ? url : '#');
      },

      /**
       * Constructs the folder url for a record.
       * @param path {string} folder path
       * For a site relative item this can be empty (root of doclib) or any path - without a leading slash
       * For a repository item, this can never be empty - but will contain leading slash and Company Home root
       */
      getBrowseUrlForFolderPath: function (path, site)
      {
         var url = null;
         if (site)
         {
            url = Alfresco.constants.URL_PAGECONTEXT + "site/" + site.shortName + "/documentlibrary?path=" + encodeURIComponent('/' + path);
         }
         else
         {
            url = Alfresco.constants.URL_PAGECONTEXT + "repository?path=" + encodeURIComponent('/' + path.split('/').slice(2).join('/'));
         }
         return url;
      },

      buildSpaceNamePath: function(pathParts, name)
      {
         return (pathParts.length !== 0 ? ("/" + pathParts.join("/")) : "") + "/" + name;
      },

      buildTextForType: function(type)
      {
         var result = '';
         switch (type)
         {
            case "document":
            case "folder":
            case "blogpost":
            case "forumpost":
            case "calendarevent":
            case "wikipage":
            case "datalist":
            case "datalistitem":
            case "link":
               result += $msg("label." + type);
               break;

            default:
               result += $msg("label.unknown");
               break;
         }
         return result;
      },

      buildPath: function(type, path, site)
      {
         var result = '';
         if (type === "document" || type === "folder")
         {
            if (site)
            {
               if (!path)
               {
                  path = "";
               }
               result += '<div class="details">' + $msg("message.infolderpath") +
                  ': <a href="' + this.getBrowseUrlForFolderPath(path, site) + '">' + $html('/' + path) + '</a></div>';
            }
            else
            {
               if (path)
               {
                  result += '<div class="details">' + $msg("message.infolderpath") +
                     ': <a href="' + this.getBrowseUrlForFolderPath(path) + '">' + $html(path) + '</a></div>';
               }
            }
         }
         return result;
      },

      buildThumbnailUrl: function(type, nodeRef, modifiedOn)
      {
         var imageUrl = '';
         switch (type)
         {
            case "document":
               imageUrl = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + nodeRef.replace(":/", "");
               imageUrl += "/content/thumbnails/doclib?c=queue&ph=true&lastModified=" + Alfresco.util.encodeHTML(modifiedOn);
               break;
   
            case "folder":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/folder.png';
               break;
   
            case "blogpost":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/blog-post.png';
               break;
   
            case "forumpost":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/topic-post.png';
               break;
   
            case "calendarevent":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/calendar-event.png';
               break;
   
            case "wikipage":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/wiki-page.png';
               break;
   
            case "link":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/link.png';
               break;
   
            case "datalist":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/datalist.png';
               break;
   
            case "datalistitem":
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/datalistitem.png';
               break;
   
            default:
               imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/search/images/generic-result.png';
               break;
         }
         return imageUrl;
      },

      buildThumbnailHtml: function(record, height, width)
      {
         var moddate = record.getData("lastThumbnailModification") ? "doclib:" + record.getData("lastThumbnailModification") : record.getData("modifiedOn");
         var config = {
            displayName: record.getData("displayName"),
            type: record.getData("type"),
            name: record.getData("name"),
            nodeRef: record.getData("nodeRef"),
            site: record.getData("site"),
            path: record.getData("path"),
            container: record.getData("container"),
            modifiedOn: moddate,
            mimetype: record.getData("mimetype"),
            width: width,
            height: height
         };

         var html = this._buildThumbnailHtml(config);

         if (width || height)
         {
            return html;
         }

         if (config.type === "document")
         {
            var viewUrl = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + config.nodeRef.replace(":/", "") + "/" + encodeURIComponent(config.name),
                isImage = (config.mimetype && config.mimetype.match("^image/")),
                actions = '<div class="action-overlay">';
            if (!isImage)
            {
               actions += '<a href="' + viewUrl + '" target="_blank"><img title="' + $html($msg("label.viewinbrowser")) +
                       '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/search/images/view-in-browser-16.png" width="16" height="16"/></a>';
            }
            else
            {
               actions += '<a href="' + this.getBrowseUrlForRecord(record) + '"><img title="' + $html($msg("label.viewdetails")) +
                       '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/actions/document-view-details-16.png" width="16" height="16"/></a>';
            }
            actions += '<a href="' + viewUrl + '?a=true" style="padding-left:4px" target="_blank"><img title="' + $html($msg("label.download")) +
                    '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/search/images/download-16.png" width="16" height="16"/></a>';
            actions += '</div>';
            
            html = actions + html;
         }

         return html;
      },
      
      _buildThumbnailHtml: function(config)
      {
         var url, isImage = (config.mimetype && config.mimetype.match("^image/"));
         if (isImage)
         {
            url = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + config.nodeRef.replace(":/", "") + "/" + encodeURIComponent(config.name);
         }
         else
         {
            url = this.getBrowseUrl(config.name, config.type, config.site, config.path, config.nodeRef, config.container, config.modifiedOn);
         }
         
         var imageUrl = this.buildThumbnailUrl(config.type, config.nodeRef, config.modifiedOn),
             htmlName = $html(config.displayName);
         
         var html = '<span><a href="' + url + '"' + (isImage ? ' onclick="Alfresco.Lightbox.show(this);return false;"' : "") + '><img src="' + imageUrl + '" alt="' + htmlName + '" title="' + htmlName + '"' + (config.width ? ' width="' + config.width + '"' : "") + (config.height ? ' height="' + config.height + '"' : "") + '/></a></span>';
         
         return html;
      }
   });
})();