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
 * A User picker that works against the Alfresco Cloud.
 * 
 * @namespace Alfresco
 * @class Alfresco.CloudObjectFinder
 */
(function()
{

   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   Alfresco.CloudObjectFinder = function Alfresco_CloudObjectFinder(htmlId, currentValueHtmlId)
   {
      Alfresco.CloudObjectFinder.superclass.constructor.call(this, htmlId, currentValueHtmlId);

      // Re-register with our own name
      this.name = "Alfresco.CloudObjectFinder";
      Alfresco.util.ComponentManager.reregister(this);

      if (htmlId != "null")
      {
         // Register handler so the right network can be used in the URL
         YAHOO.Bubbling.on("hybridWorklfowDestinationSelected", this._onNetworkSelected, this);
         
         // Register handler when multipleSelectMode should be altered
         YAHOO.Bubbling.on("multipleSelectModeChanged", this._onMultipleSelectModeChanged, this);
      }
      
      return this;
   };

   YAHOO.extend(Alfresco.CloudObjectFinder, Alfresco.ObjectFinder,
   {
      /**
       * Called when network is selected. Current selection will be cleared and
       * correct params used in cloud-datasource.
       */
      _onNetworkSelected: function CloudObjectFinder_onNetworkSelected(layer, args)
      {
         var destination = args[1];
         if(destination != null && destination.network != null) 
         {
            var newParams = "network=" + destination.network;
            
            // Check if network has changed from the previous value
            if(this.options.params != newParams)
            {
               // Update params used for API-calls, include network
               this.options.params = newParams;
               if(this.options.objectRenderer)
               {
                  // Also update object-renderer params to include network
                  this.options.objectRenderer.options.params = newParams;
               }
               
               // Make "select" button visible, if not already the case
               if(this.widgets.addButton != null)
               {
                  Alfresco.util.enableYUIButton(this.widgets.addButton);
               }
               
               // Remove all selection
               this.selectedItems = {};
               this.singleSelectedItem = null;
               
               YAHOO.Bubbling.fire("renderCurrentValue",
               {
                  eventGroup: this
               });
            }
         }
      },
      
      _onMultipleSelectModeChanged :function CloudObjectFinder_onMultipleSelectModeChanged(layer, args)
      {
         var newMultipleSelectMode = args[1];
         if(newMultipleSelectMode != null)
         {
            if(this.options.multipleSelectMode != newMultipleSelectMode)
            {
               // Remove all selected
               this.selectedItems = {};
               this.singleSelectedItem = null;
               
               YAHOO.Bubbling.fire("renderCurrentValue",
               {
                  eventGroup: this
               });
               
               // Disable button again, if needed
               if(this.options.params == null || this.options.params.indexOf("network") == -1)
               {
                  if(this.widgets.addButton != null)
                  {           
                     Alfresco.util.disableYUIButton(this.widgets.addButton);
                  }
               }
               
               this.options.multipleSelectMode = (newMultipleSelectMode == true);
               
               // Alter label if needed
               if(this.options.singleItemLabel && this.options.multipleItemsLabel)
               {
                  var newLabel = null;
                  if(newMultipleSelectMode == true)
                  {
                     newLabel = this.options.multipleItemsLabel;
                  }
                  else
                  {
                     newLabel = this.options.singleItemLabel;
                  }
                  
                  var parent = Dom.getAncestorByTagName(this.id, "div");
                  var lbl = Dom.getChildrenBy(parent, function(el){return el.tagName.toLowerCase()=='label';});
                  if(lbl[0] != null)
                  {
                     var requiredSpan = Dom.getChildrenBy(lbl[0], function(el){return el.tagName.toLowerCase()=='span';});
                     lbl[0].innerHTML = this.msg(newLabel) + ":";
                     
                     if(requiredSpan[0] != null)
                     {
                        lbl[0].appendChild(requiredSpan[0]);
                     }
                  }
               }
            }
         }
      },
      
      onReady: function CloudObjectFinder_onReady()
      {
         Alfresco.CloudObjectFinder.superclass.onReady.call(this);
         if(this.widgets.addButton != null)
         {           
            Alfresco.util.disableYUIButton(this.widgets.addButton);
         }
      },
      
      destroy: function ObjectFinder_destroy()
      {
         try
         {
            YAHOO.Bubbling.unsubscribe("hybridWorklfowDestinationSelected", this._onNetworkSelected, this);
            YAHOO.Bubbling.unsubscribe("multipleSelectModeChanged", this._onMultipleSelectModeChanged, this);
         }
         catch (e)
         {
            // Ignore
         }
         Alfresco.CloudObjectFinder.superclass.destroy.call(this);
      },
      
      _labelFilter: function CloudObjectFinder_tagfilter(element) {
         alert("Filterng label: " + element);
         return element.tagName=='label';
      },
      
      _spanFilter: function CloudObjectFinder_tagfilter(element) {
         alert("Filterng span: " + element);
         return element.tagName=='span';
      }
   });
   
})();