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
 * FolderDetails template.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderDetails
 */
(function()
{
   /**
    * FolderDetails constructor.
    * 
    * @return {Alfresco.FolderDetails} The new FolderDetails instance
    * @constructor
    */
   Alfresco.FolderDetails = function FolderDetails_constructor()
   {
      Alfresco.FolderDetails.superclass.constructor.call(this, null, "Alfresco.FolderDetails");
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("metadataRefresh", this.onReady, this);
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.onReady, this);
      YAHOO.Bubbling.on("filesMoved", this.onReady, this);
            
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderDetails, Alfresco.component.Base,
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
          * nodeRef of folder being viewed
          * 
          * @property nodeRef
          * @type string
          */
         nodeRef: null,
         
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Root node if Repository-based library
          * 
          * @property rootNode
          * @type Alfresco.util.NodeRef
          */
         rootNode: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @override
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function FolderDetails_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function FolderDetails_onReady()
      {
         var url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + this.options.nodeRef.uri;
         if (this.options.siteId == "")
         {
            // Repository mode
            url += "?libraryRoot=" + encodeURIComponent((this.options.rootNode || "").toString());
         }

         Alfresco.util.Ajax.jsonGet(
         {
            url: url,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureMessage: "Failed to load data for folder details"
         });
      },
      
      /**
       * Success handler called when the AJAX call to the doclist web script returns successfully
       *
       * @response The response object
       */
      _getDataSuccess: function FolderDetails__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            var folderDetails = response.json.item;
            
            // Fire event to inform any listening components that the data is ready
            YAHOO.Bubbling.fire("folderDetailsAvailable",
            {
               folderDetails: folderDetails,
               metadata: response.json.metadata
            });
            
            // Fire event to show comments for folder
            YAHOO.Bubbling.fire("setCommentedNode",
            { 
               nodeRef: folderDetails.nodeRef,
               title: folderDetails.displayName,
               page: "folder-details",
               pageParams:
               {
                  nodeRef: this.options.nodeRef.toString()
               }
            });
         }
      }
   });
})();
