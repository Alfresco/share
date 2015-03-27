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
 * Dashboard Image Summary component.
 * 
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.ImageSummary
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Event = YAHOO.util.Event,
       Dom = YAHOO.util.Dom;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
       $hasEventInterest = Alfresco.util.hasEventInterest;
   
   /**
    * Dashboard ImageSummary constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.ImageSummary} The new component instance
    * @constructor
    */
   Alfresco.dashlet.ImageSummary = function ImageSummary_constructor(htmlId)
   {
      Alfresco.dashlet.ImageSummary.superclass.constructor.call(this, "Alfresco.dashlet.ImageSummary", htmlId);
      
      Event.addListener(window, 'resize', this.resizeThumbnailList, this, true);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.dashlet.ImageSummary, Alfresco.component.Base,
   {
      /**
       * Calculated number of thumbnail items per row - don't perform full reflow unless actually required
       * 
       * @property itemsPerRow
       * @type integer
       */
      itemsPerRow: 0,
      
      /**
       * Calculated value of padding between thumbnail items - don't perform full reflow unless actually required
       * 
       * @property itemsPerRow
       * @type string
       */
      itemPadding: "",
      
      onReady: function onReady()
      {
         this.refreshImages();
         
         // listen for folderSelected event from the folder picker module
         YAHOO.Bubbling.on("folderSelected", function(layer, args)
         {
            if ($hasEventInterest(this.modules.folderPicker, args))
            {
               var selectedFolder = args[1].selectedFolder;
               if (selectedFolder !== null)
               {
                  if (selectedFolder.siteId === this.options.siteId)
                  {
                     this.options.siteFolderPath = selectedFolder.path;
                     
                     // Update component properties with the new value
                     Alfresco.util.Ajax.jsonRequest(
                     {
                        method: "POST",
                        url: Alfresco.constants.URL_SERVICECONTEXT + "modules/dashlet/config/" + this.options.componentId,
                        dataObj:
                        {
                           siteFolderPath: this.options.siteFolderPath
                        },
                        successCallback:
                        {
                           fn: function(response)
                           {
                              this.refreshImages();
                           },
                           scope: this
                        }
                     });
                  }
               }
            }
         }, this);
      },
      
      /**
       * Refresh the image list based on current site folder path
       */
      refreshImages: function refreshImages()
      {
         Dom.addClass(this.id + "-message", "hidden");
         var elImages = Dom.get(this.id + "-images");
         Dom.addClass(elImages, "hidden");
         elImages.innerHTML = "";
         Dom.removeClass(this.id + "-wait", "hidden");
         
         // Execute the request to retrieve the list of images to display
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI +
                 "slingshot/doclib/images/site/" + this.options.siteId + "/documentLibrary" +
                 (this.options.siteFolderPath ? encodeURI(this.options.siteFolderPath) : "") + "?max=250",
            successCallback:
            {
               fn: function(response)
               {
                  // construct each image preview markup from HTML template block
                  var elTemplate = Dom.get(this.id + "-item-template"),
                      items = response.json.items;
                  // clone the template and perform a substitution to generate final markup
                  var htmlTemplate = unescape(elTemplate.innerHTML);
                  for (var i=0, j=items.length, clone, item; i<j; i++)
                  {
                     item = items[i];
                     var params = {
                        nodeRef: item.nodeRef,
                        nodeRefUrl: item.nodeRef.replace(":/", ""),
                        name: encodeURIComponent(item.name),
                        title: $html(item.title),
                        modifier: this.msg("text.modified-by", $html(item.modifier)),
                        modified: Alfresco.util.formatDate(Alfresco.util.fromISO8601(item.modifiedOn))
                     };
                     // clone the template and perform a substitution to generate final markup
                     clone = elTemplate.cloneNode(true);
                     clone.innerHTML = YAHOO.lang.substitute(htmlTemplate, params);
                     elImages.appendChild(clone);
                  }
                  
                  // remove the ajax wait spinner
                  Dom.addClass(this.id + "-wait", "hidden");
                  
                  // perform initial resize to correctly set padding between items
                  this.itemsPerRow = 0;
                  this.itemPadding = "";
                  this.resizeThumbnailList(null);
                  
                  // show the containing element for the list of images
                  Dom.removeClass(elImages, "hidden");
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  // remove the ajax wait spinner
                  Dom.addClass(this.id + "-wait", "hidden");
                  
                  // show the failure message inline
                  var elMessage = Dom.get(this.id + "-message");
                  if (response.json)
                  {
                     elMessage.innerHTML = $html(response.json.message);
                  }
                  else
                  {
                     elMessage.innerHTML = $html(response.serverResponse.statusText);
                  }
                  Dom.removeClass(elMessage, "hidden");
               },
               scope: this
            }
         });
      },
      
      /**
       * Fired on window resize event.
       * 
       * @method resizeThumbnailList
       * @param e {object} the event source
       */
      resizeThumbnailList: function resizeThumbnailList(e)
      {
         // calculate number of thumbnails we can display across the dashlet width
         var listDiv = Dom.get(this.id + "-list");
         if (listDiv)
         {
             var clientWidth = listDiv.clientWidth - 16 - (e ? 0 : 16),
             count = Math.floor(clientWidth / 110),
             spacing = (((clientWidth % 110) / count / 2) + 3.5).toFixed(1);

             // handle minimum value - we never want to show less than one thumbnail column
             if (count === 0) count = 1;

             // reflow the thumbnail items as required
             if (count !== this.itemsPerRow || spacing !== this.itemPadding)
             {
                var items = Dom.getElementsByClassName("item", null, listDiv);
                for (var i=0, j=items.length, pad="0 "+spacing+"px 8px"; i<j; i++)
                {
                   if (spacing !== this.itemPadding)
                   {
                      Dom.setStyle(items[i], "padding", pad);
                   }
                   if (count !== this.itemsPerRow)
                   {
                      if (i % count === 0)
                      {
                         // initial item for the current row
                         Dom.addClass(items[i], "initial");
                      }
                      else
                      {
                         Dom.removeClass(items[i], "initial");
                      }
                   }
                }
                this.itemPadding = spacing;
                this.itemsPerRow = count;
             }
          }
      },
      
      /**
       * Event handler called when the Edit icon is clicked in the dashlet title bar
       */
      onConfigImageFolderClick: function onConfigImageFolderClick(e)
      {
         if (!this.modules.folderPicker)
         {
            this.modules.folderPicker = new Alfresco.module.DoclibGlobalFolder(this.id + "-rulesPicker");
         }
         
         this.modules.folderPicker.setOptions(
         {
            siteId: this.options.siteId,
            allowedViewModes: [ Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE ],
            path: this.options.siteFolderPath,
            title: this.msg("text.selectfolder", this.options.siteTitle)
         }).showDialog();
      }
   });
})();