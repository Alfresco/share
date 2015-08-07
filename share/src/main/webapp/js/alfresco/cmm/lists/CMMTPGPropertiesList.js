/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * This is the CMM listing for Type properties or Property Group properties.
 *
 * @module cmm/lists/CMMTPGPropertiesList
 * @extends cmm/lists/CMMTPGList
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "cmm/lists/CMMTPGList", 
        "dojo/_base/lang",
        "cmm/CMMConstants",
        "dijit/registry"], 
        function(declare, CMMTPGList, lang, CMMConstants, Registry) {
   
   return declare([CMMTPGList], {

      /**
       * A topic which, when published, will send the nodes it's possible to have on the canvas, and 
       * which also listens for a response with those that already are.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      syncNodesTopic: "",
      
      /**
       * If [useHash]{@link module:alfresco/lists/AlfHashList#useHash} has been set to true
       * then this function will be called whenever the browser hash fragment is modified. It will update
       * the attributes of this instance with the values provided in the fragment.
       * 
       * @instance
       * @param {object} payload
       */
      onHashChanged: function alfresco_cmm_lists_CMMTPGPropertiesList__onHashChanged(payload) {

         // Only update if the payload contains one of the variables we care about
         if (this.doHashVarUpdate(payload))
         {
            lang.mixin(this, payload);
            
            // the Properties list can either use a propertygroup or a type variable
            // they are mutually exclusive and we need to ensure the opposing variable is cleared when one is set
            
            if (payload.propertygroup) this.type = null;
            if (payload.type) this.propertygroup = null;
            
            this.updateLoadDataPayload({
               name: this.model,
               type: this.type,
               propertygroup: this.propertygroup
            });
            
            // Fire off an event to update labels that care about the type or property group name
            this.alfPublish(CMMConstants.UPDATE_TPG_HEADING, {
               label: this.model + " - " + (this.type || this.propertygroup)
            });
            
            // Load data
            this.loadData();
         }
      },

      /**
       * @instance
       */
      onDataLoadSuccess: function alfresco_cmm_lists_CMMTPGPropertiesList__onDataLoadSuccess(payload) {
         this.inherited(arguments);

         if(this.syncNodesTopic != "")
         {
            // Subscribe the callback
            this._syncCallBackHandle = this.alfSubscribe(this.syncNodesTopic + "_CALLBACK", lang.hitch(this, this.syncNodes), true);

            // Get the nodes in the palette and publish out on the syncNodesTopic
            var palette = lang.getObject("viewMap.Abstract.docListRenderer.sourceTarget", false, this);
            if(palette != null)
            {
               var paletteNodes = palette.getAllNodes();
               this.alfPublish(this.syncNodesTopic, paletteNodes, true);
            }

         }
      },

      /**
       * @instance
       */
      syncNodes: function alfresco_cmm_lists_CMMTPGPropertiesList__syncNodes(canvasNodes) {

         // Unsubscribe the callback
         this.alfUnsubscribe(this._syncCallBackHandle);
         
         var palette = lang.getObject("viewMap.Abstract.docListRenderer.sourceTarget", false, this);
         if(palette != null)
         {
            var paletteNodes = palette.getAllNodes();
            
            // Iterate over palette widgets
            for(var i=0; i<paletteNodes.length; ++i)
            {
               // Convert to a real widget with the registry
               var paletteNodeObj = Registry.byId(paletteNodes[i].firstChild.id),
                   found = false;

               // Iterate over the canvas items
               for(var j=0; j<canvasNodes.length; ++j)
               {
                  // Convert the canvas node to a real widget
                  var canvasNodeObj = Registry.byId(canvasNodes[j].id);

                  // If the canvas widget label matches the title of the palette item, they are a match
                  if(canvasNodeObj.label === paletteNodeObj.title)
                  {
                     found = true;
                     break;
                  }
               }
               
               // If an item in the palette has been found in the canvas, destroy it
               if(found)
               {
                  paletteNodeObj.destroyRecursive();
               }
            }
         }
      }

   });
});