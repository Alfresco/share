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
 * ManagePermissions template.
 * 
 * @namespace Alfresco.template
 * @class Alfresco.template.ManagePermissions
 */
(function()
{
   /**
    * ManagePermissions constructor.
    * 
    * @return {Alfresco.template.ManagePermissions} The new ManagePermissions instance
    * @constructor
    */
   Alfresco.template.ManagePermissions = function ManagePermissions_constructor()
   {
      return Alfresco.template.ManagePermissions.superclass.constructor.call(this, null, "Alfresco.template.ManagePermissions");
   };
   
   YAHOO.extend(Alfresco.template.ManagePermissions, Alfresco.component.Base,
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
      onComponentsLoaded: function ManagePermissions_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ManagePermissions_onReady()
      {
         var url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + this.options.nodeRef.uri;
         if (this.options.siteId == "")
         {
            // Repository mode
            url += "?libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());
         }
         Alfresco.util.Ajax.jsonGet(
         {
            url: url,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureMessage: "Failed to load data for permission details"
         });
      },
      
      /**
       * Success handler called when the AJAX call to the doclist web script returns successfully
       *
       * @response The response object
       */
      _getDataSuccess: function ManagePermissions__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            var nodeDetails = response.json.item;
            
            // Fire event to inform any listening components that the data is ready
            YAHOO.Bubbling.fire("nodeDetailsAvailable",
            {
               nodeDetails: nodeDetails,
               metadata: response.json.metadata
            });
         }
      }
   });
})();
