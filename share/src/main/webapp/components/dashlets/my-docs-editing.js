/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * Dashboard My Docs Editing component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.MyDocsEditing
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML
       $siteURL = Alfresco.util.siteURL,
       $profileURL = Alfresco.util.profileURL;
   
   /**
    * Dashboard MyDocsEditing constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocsEditing} The new component instance
    * @constructor
    */
   Alfresco.dashlet.MyDocsEditing = function MyDocsEditing_constructor(htmlId)
   {
      Alfresco.dashlet.MyDocsEditing.superclass.constructor.call(this, "Alfresco.dashlet.MyDocsEditing", htmlId);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.dashlet.MyDocsEditing, Alfresco.component.Base,
   {
      onReady: function onReady()
      {
         // Execute the request to retrieve the list of documents to display
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/documents/node/alfresco/company/home?filter=editingMe&max=3&sortField=cm:modified&sortAsc=false",
            successCallback:
            {
               fn: function(response)
               {
                  // construct each item markup from HTML template block
                  var elItems = Dom.get(this.id + "-documents"),
                      elTemplate = Dom.get(this.id + "-document-template"),
                      items = response.json.items,
                      imgerror = "onerror=\"this.src='" + Alfresco.constants.URL_CONTEXT + "res/components/images/filetypes/generic-file-32.png'\"";
                  
                  if (items.length !== 0)
                  {
                     // clone the template and perform a substitution to generate final markup
                     var htmlTemplate = unescape(elTemplate.innerHTML);
                     for (var i=0, j=items.length, clone, item; i<j; i++)
                     {
                        item = items[i];
                        
                        var linkURL = item.location.site ? $siteURL("dashboard", { site: item.location.site }) : $profileURL(item.lockedByUser, item.lockedBy),
                            fileExtIndex = item.fileName.lastIndexOf("."),
                            fileExt = fileExtIndex !== -1 ? item.fileName.substring(fileExtIndex + 1) : "generic",
                            editMsg = item.location.site ?
                              this.msg("details.editing-started-in-site", '<span class="relativeTime">' + Alfresco.util.relativeTime(item.modifiedOn) + '</span>', '<a class="theme-color-1 site-link" href="' + linkURL + '">' + $html(item.location.siteTitle) + '</a>') : 
                              this.msg("details.editing-started-by", '<span class="relativeTime">' + Alfresco.util.relativeTime(item.modifiedOn) + '</span>', '<a class="theme-color-1" href="' + linkURL + '">' + $html(item.lockedBy) + '</a>');
                        
                        var params = {
                           name: $html(item.displayName),
                           filename: encodeURIComponent(item.fileName),
                           fileExt: fileExt,
                           site: item.location.site ? ("/site/" + item.location.site) : "",
                           editingMessage: editMsg,
                           onerror: imgerror
                        };
                        
                        // clone the template and perform a substitution to generate final markup
                        clone = elTemplate.cloneNode(true);
                        clone.innerHTML = YAHOO.lang.substitute(htmlTemplate, params);
                        elItems.appendChild(clone);
                     }
                  }
                  else
                  {
                     elItems.innerHTML = '<div class="detail-list-item first-item"><span class="faded">' + this.msg("label.noItems") + '</span>';
                  }
                  
                  // remove the ajax wait spinner
                  Dom.addClass(this.id + "-documents-wait", "hidden");
                  
                  // show the containing element for the list
                  Dom.removeClass(elItems, "hidden");
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  // remove the ajax wait spinner
                  Dom.addClass(this.id + "-documents-wait", "hidden");
                  
                  // add the failure message inline
                  var elMessage = Dom.get(this.id + "-message");
                  elMessage.innerHTML += "<p>" + $html(response.json.message); + "</p>";
                  Dom.removeClass(elMessage, "hidden");
               },
               scope: this
            }
         });
         
         // Execute the request to retrieve the list of wiki pages, blog and forum posts to display
         if (Dom.get(this.id + "-content-wait") !== null)
         {
            Alfresco.util.Ajax.jsonRequest(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/dashlets/my-contents",
               successCallback:
               {
                  fn: function(response)
                  {
                     var items = response.json,
                         elTemplate = Dom.get(this.id + "-item-template");
                     this.renderContentItems(items["blogPosts"].items, "-blogposts", "blog-postview?postId=", "blogpost-32.png", elTemplate);
                     this.renderContentItems(items["wikiPages"].items, "-wikipages", "wiki-page?title=", "wikipage-32.png", elTemplate);
                     this.renderContentItems(items["forumPosts"].items, "-forumposts", "discussions-topicview?topicId=", "topicpost-32.png", elTemplate);
                     Dom.getElementsByClassName("relativeTime", "span", Dom.get(this.id + "-my-docs-dashlet"), function()
                     {
                        this.innerHTML = Alfresco.util.relativeTime(this.innerHTML);
                     });
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     // remove the ajax wait spinner
                     Dom.addClass(this.id + "-content-wait", "hidden");
                     
                     // add the failure message inline
                     var elMessage = Dom.get(this.id + "-message");
                     elMessage.innerHTML += "<p>" + $html(response.json.message); + "</p>";
                     Dom.removeClass(elMessage, "hidden");
                  },
                  scope: this
               }
            });
         }
      },
      
      /**
       * Render a list of content items - generally wiki, blogs or forum posts
       * 
       * @param {Object} items List of items to render
       * @param {string} itemId Prefix of the items target elements in the DOM
       * @param {string} urlPrefix Prefix for the browse URL generation for each item
       * @param {string} icon Icon image file name
       * @param {Object} elTemplate DOM element of the template node for each item replacement
       */
      renderContentItems: function renderContentItems(items, itemId, urlPrefix, icon, elTemplate)
      {
         // construct each item markup from HTML template block
         var elItems = Dom.get(this.id + itemId),
             iconURL = "components/images/" + icon;
         
         if (items.length !== 0)
         {
            // clone the template and perform a substitution to generate final markup
            var htmlTemplate = unescape(elTemplate.innerHTML);
            for (var i=0, j=items.length, clone, item; i<j; i++)
            {
               item = items[i];
               
               var siteURL = $siteURL("dashboard", { site: item.site.shortName }),
                   editMsg = this.msg("text.edited-on", '<span class="relativeTime">' + item.modifiedOn + '</span>', '<a class="theme-color-1 site-link" href="' + siteURL + '">' + $html(item.site.title) + '</a>');
               
               var params = {
                  name: $html(item.displayName),
                  icon: iconURL,
                  browseURL: $siteURL(urlPrefix, { site: item.site.shortName }) + encodeURIComponent(item.name),
                  editingMessage: editMsg
               };
               
               // clone the template and perform a substitution to generate final markup
               clone = elTemplate.cloneNode(true);
               clone.innerHTML = YAHOO.lang.substitute(htmlTemplate, params);
               elItems.appendChild(clone);
            }
         }
         else
         {
            elItems.innerHTML = '<div class="detail-list-item first-item"><span class="faded">' + this.msg("label.noItems") + '</span>';
         }
         
         // remove the ajax wait spinner
         Dom.addClass(this.id + "-content-wait", "hidden");
         
         // show the containing element for the list
         Dom.removeClass(elItems, "hidden");
      }
   });
})();