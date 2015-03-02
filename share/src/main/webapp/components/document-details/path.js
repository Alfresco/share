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
 * Document and Folder path component.
 * 
 * @namespace Alfresco
 * @class Alfresco.component.Path
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
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL,
      $userProfile = Alfresco.util.userProfileLink;
   
   /**
    * Path constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.Path} The new Path instance
    * @constructor
    */
   Alfresco.component.Path = function(htmlId)
   {
      Alfresco.component.Path.superclass.constructor.call(this, "Alfresco.component.Path", htmlId);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      YAHOO.Bubbling.on("nodeDetailsAvailable", this.onNodeDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.component.Path, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Root page to create links to.
          * 
          * @property rootPage
          * @type string
          * @default "documentlibrary"
          */
         rootPage: "documentlibrary",

         /**
          * Root label ID. The I18N property of the document library root container.
          * 
          * @property rootLabelId
          * @type string
          * @default "path.documents"
          */
         rootLabelId: "path.documents",

         /**
          * Flag indicating whether to show folder icon type.
          *
          * @property showIconType
          * @type boolean
          * @default: true
          */
         showIconType: true
      },
      
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       *
       * @method: onDocumentDetailsAvailable
       */
      onDocumentDetailsAvailable: function Path_onDocumentDetailsAvailable(layer, args)
      {
         var docData = args[1].documentDetails,
            pathHtml,
            rootLink = this.options.rootPage,
            pathUrl = "",
            folders = [],
            href;

         var path = docData.location.path;

         // Document Library root node
         if (path.length < 2)
         {
            pathHtml = '<span class="path-link"><a href="' + $siteURL(rootLink + "?file=" + encodeURIComponent(docData.fileName)) + '">' + this.msg(this.options.rootLabelId) + '</a></span>';
         }
         else
         {
            pathHtml = '<span class="path-link"><a href="' + $siteURL(rootLink) + '">' + this.msg(this.options.rootLabelId) + '</a></span>';
            folders = path.substring(1, path.length).split("/");

            if (folders.length > 0)
            {
               pathHtml += '<span class="separator"> &gt; </span>';
            }
            
            for (var x = 0, y = folders.length; x < y; x++)
            {
               pathUrl += "/" + folders[x];
               href = rootLink + (y - x < 2 ? "?file=" + encodeURIComponent(docData.fileName) + "&path=" : "?path=") + encodeURIComponent(pathUrl);
               pathHtml += '<span class="path-link folder"><a href="' + $siteURL(href) + '">' + $html(folders[x]) + '</a></span>';

               if (y - x > 1)
               {
                  pathHtml += '<span class="separator"> &gt; </span>';
               }
            }
         }
         
         Dom.setStyle(this.id + "-defaultPath", "display", "none");
         Dom.get(this.id + "-path").innerHTML = pathHtml;

         Dom.addClass(this.id + "-status", "hidden");
         
         if (docData.custom && (docData.custom.isWorkingCopy || docData.custom.hasWorkingCopy))
         {
            var bannerMsg, bannerStatus;
            
            if (docData.lockedByUser && docData.lockedByUser !== "")
            {
               var lockedByLink = $userProfile(docData.lockedByUser, docData.lockedBy, 'class="theme-color-1"');

               /* Working Copy handling */
               if (docData.lockedByUser === Alfresco.constants.USERNAME)
               {
                  // Locked / Working Copy handling
                  bannerStatus = docData.actionSet === "lockOwner" ? "lock-owner" : "editing";
                  bannerMsg = this.msg("banner." + bannerStatus);
               }
               else 
               {
                  bannerStatus = "locked";
                  bannerMsg = this.msg("banner.locked", lockedByLink);
               }
            }
            
            if (bannerMsg)
            {
               Dom.get(this.id + "-status").innerHTML = '<span class="' + $html(bannerStatus) + '">' + bannerMsg + '</span>';
               Dom.removeClass(this.id + "-status", "hidden");
            }
            
            YAHOO.Bubbling.fire("recalculatePreviewLayout");
         }
      },

      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       *
       * @method: onFolderDetailsAvailable
       */
      onFolderDetailsAvailable: function Path_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails,
            pathHtml = "",
            rootLink = this.options.rootPage,
            pathUrl = "/",
            folders = [],
            href;
         
         var path = folderData.location.path;
         
         // Document Library root node
         if (path == "/" && folderData.location.file.length > 0)
         {
            pathHtml = '<span class="path-link"><a href="' + $siteURL(rootLink + "?file=" + encodeURIComponent(folderData.fileName)) + '">' + this.msg(this.options.rootLabelId) + '</a></span>';
         }
         else
         {
            pathHtml = '<span class="path-link"><a href="' + $siteURL(rootLink) + '">' + this.msg(this.options.rootLabelId) + '</a></span>';
         }

         path = $combine(path, folderData.location.file);
         if (path.length > 1)
         {
            folders = path.substring(1, path.length).split("/");
            pathHtml += '<span class="separator"> &gt; </span>';

            for (var x = 0, y = folders.length; x < y; x++)
            {
               pathUrl += folders[x];
               href = rootLink + (y - x == 2 ? "?file=" + encodeURIComponent(folderData.fileName) + "&path=" : "?path=") + encodeURIComponent(pathUrl);
               pathHtml += '<span class="path-link ' + (y - x == 1 ? "self" : "folder") + '"><a href="' + $siteURL(href) + '">' + $html(folders[x]) + '</a></span>';

               if (y - x > 1)
               {
                  pathHtml += '<span class="separator"> &gt; </span>';
                  pathUrl += "/";
               }
            }
         }
         
         Dom.setStyle(this.id + "-defaultPath", "display", "none");
         Dom.get(this.id + "-path").innerHTML = pathHtml;

         if (this.options.showIconType)
         {
            Alfresco.util.populateHTML(
               [this.id + "-iconType", YAHOO.lang.substitute('<img src="{iconContext}{icon}-48.png" width="48" height="48" /><span class="type">{type}</span>',
                  {
                     iconContext: Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/images/",
                     icon: folderData.type,
                     type: this.msg("type." + folderData.type, $html(folderData.displayName))
                  })]
            );
         }
      },

      /**
       * Event handler called when the "nodeDetailsAvailable" event is received
       *
       * @method: onNodeDetailsAvailable
       */
      onNodeDetailsAvailable: function Path_onNodeDetailsAvailable(layer, args)
      {
         var nodeData = args[1].nodeDetails,
            newArgs = args;
         
         if (nodeData.isFolder)
         {
            newArgs[1].folderDetails = nodeData;
            this.onFolderDetailsAvailable(layer, newArgs);
         }
         else
         {
            newArgs[1].documentDetails = nodeData;
            this.onDocumentDetailsAvailable(layer, newArgs);
         }
      }
   });
})();