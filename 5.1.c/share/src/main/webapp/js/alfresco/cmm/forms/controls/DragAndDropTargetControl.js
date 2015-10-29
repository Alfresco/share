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
 * This widget extends a [DragAndDropTargetControl]{@link module:alfresco/forms/controls/DragAndDropTargetControl} 
 * to provide clear functions.
 * 
 * @module cmm/forms/controls/DragAndDropTargetControl
 * @extends module:alfresco/forms/controls/DragAndDropTargetControl
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "alfresco/forms/controls/DragAndDropTargetControl",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/on",
        "dojo/keys",
        "dojo/dom-class",
        "dijit/registry"], 
        function(declare, DragAndDropTargetControl, lang, array, on, keys, domClass, Registry) {
   
   return declare([DragAndDropTargetControl], {

      /**
       * A topic which, when published, will request the nodes it's possible to have on the canvas, and 
       * which also returns those that already are.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      syncNodesTopic: "",

      /**
       * Node property to observe to see if syncing required.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      syncNodesWhiteListProp: "",
      
      /**
       * A list of nodes types which do not need to be considered in the properties sync process.
       * 
       * @instance
       * @type {string[]}
       * @default null
       */
      syncNodesWhiteList: null,

      /**
       * The topic upon which to listen for when an item selection is made.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      selectListenTopic: "",

      /**
       * The topic upon which to listen for when an item focus (and blur) happens.
       *
       * @instance
       * @type {string}
       * @default ""
       */
      focusListenTopic: "",

      /**
       * Hitch various functions if topics provided.
       * 
       * @instance
       */
      postCreate: function cmm_forms_controls_DragAndDropTargetControl__postCreate() {

         this.inherited(arguments);
         
         on(document, "keydown", lang.hitch(this, this.listenKeyDown));
         
         if(this.syncNodesTopic != "")
         {
            this.alfSubscribe(this.syncNodesTopic, lang.hitch(this, this.syncNodes), true);
         }

         if(this.selectListenTopic != "")
         {
            this.alfSubscribe(this.selectListenTopic, lang.hitch(this, this.listenSelection), true);
         }

         if(this.focusListenTopic != "")
         {
            this.alfSubscribe(this.focusListenTopic, lang.hitch(this, this.listenFocus), true);
         }

      },

      /**
       * This function receives a list of nodes that can be in the canvas, removes any that should not 
       * be there, and then returns the list of nodes that remain.
       * 
       * @instance
       */
      syncNodes: function cmm_forms_controls_DragAndDropTargetControl__syncNodes(paletteNodes) {

         // Get the canvas and existing widgets it contains
         var canvas = lang.getObject("wrappedWidget.previewTarget", false, this),
             nameRegex = /\[([^\]]+)\]/;
         
         if(canvas != null)
         {
            // Sync the canvas
            canvas.sync();

            // Get the existing canvas widgets
            var canvasWidgets = this._getCurrentCanvasWidgets(canvas);

            // Iterate over canvas widgets
            for(var i=0; i<canvasWidgets.length; ++i)
            {
               // Convert to a real widget
               var canvasObj = Registry.byId(canvasWidgets[i].id),
                   found = false,
                   canvasObjMatch = canvasObj.label.match(nameRegex),
                   canvasObjName = canvasObjMatch ? canvasObjMatch[1] : canvasObj.label;

               // Iterate over the palette items
               for(var j=0; j<paletteNodes.length; ++j)
               {
                  // Convert to a real widget
                  var paletteObj = Registry.byId(paletteNodes[j].firstChild.id)
                      paletteObjMatch = paletteObj.title.match(nameRegex),
                      paletteObjName = paletteObjMatch ? paletteObjMatch[1] : paletteObj.title;

                  // If the canvas widget name matches the palette widget name, they are a match
                  if(canvasObjName === paletteObjName)
                  {
                     found = true;
                     break;
                  }
               }
               
               // If an item in the canvas has not been found in the palette, delete it
               if(!found)
               {
                  canvasObj.onItemDelete();
               }
            }

            // Re-sync the canvas
            canvas.sync();
            
            // Send the remaining nodes on the canvas, back again
            canvasWidgets = this._getCurrentCanvasWidgets(canvas)
            this.alfPublish(this.syncNodesTopic + "_CALLBACK", canvasWidgets, true);
         }
      },

      /**
       * This function records the currently selected item, the previously selected item and colours the 
       * selected item.
       * 
       * @instance
       */
      listenSelection: function cmm_forms_controls_DragAndDropTargetControl__listenSelection(payload) {
         this._previousItem = this._selectedItem;
         this._selectedItem = null;

         // If there is a currently selected item remove its selection class
         if(this._previousItem)
         {
            var previous = Registry.byId(this._previousItem);
            if(previous)
            {
               domClass.remove(previous.domNode, "selected");
            }
         }

         // If the payload has an item, record it, find it and set its selection class
         if(payload.item)
         {
            this._selectedItem = payload.item;
            var selected = Registry.byId(this._selectedItem);
            if(selected)
            {
               domClass.add(selected.domNode, "selected");
            }
         }
      },

      /**
       * This function records the currently focused item or null if it has just blurred
       * 
       * @instance
       */
      listenFocus: function cmm_forms_controls_DragAndDropTargetControl__listenFocus(payload) {
         this._focusedItem = null;
         if(payload.item)
         {
            this._focusedItem = payload.item;
         }
      },

      /**
       * This function traps all key down events. If there is a currently selected item 
       * (this._selectedItem) and the key press indicates an up (UP_ARROW, LEFT_ARROW), down 
       * (DOWN_ARROW, RIGHT_ARROW) or delete (DELETE), it will be intercepted and done. If 
       * there is a currently focused item and the key press indicates selection (ENTER, 
       * NUMPAD_ENTER, SPACE) it will be clicked, which will have the effect of causing the 
       * selection process to take place.
       * 
       * @instance
       */
      listenKeyDown: function cmm_forms_controls_DragAndDropTargetControl__listenKeyDown(evt) {
         if(this._selectedItem)
         {
            var selected = Registry.byId(this._selectedItem);
            if(selected)
            {
               switch(evt.keyCode)
               {
                  case keys.UP_ARROW:
                  case keys.LEFT_ARROW:
                     selected.onItemUp(evt);
                     evt.preventDefault();
                     break;
      
                  case keys.DOWN_ARROW:
                  case keys.RIGHT_ARROW:
                     selected.onItemDown(evt);
                     evt.preventDefault();
                     break;
      
                  case keys.DELETE:
                     selected.onItemDelete(evt);
                     evt.preventDefault();
                     break;
               }
            }
         }
         
         if(this._focusedItem)
         {
            var focused = Registry.byId(this._focusedItem);
            if(focused)
            {
               switch(evt.keyCode)
               {
                  case keys.ENTER:
                  case keys.NUMPAD_ENTER:
                  case keys.SPACE:
                     focused.domNode.click();
                     evt.preventDefault();
                     break;

               }
            }
         }
      },

      /**
       * This function iterates over the items in the canvas, including into those items with contained 
       * targets, and builds an array of items which are then returned.
       * 
       * @instance
       */
      _getCurrentCanvasWidgets: function cmm_forms_controls_DragAndDropTargetControl___getCurrentCanvasWidgets(canvas) {
         
         // Get all the nodes on the canvas
         var nodes = canvas.map,
             widgets = [];

         // Iterate over the nodes on the canvas
         for (var node in nodes)
         {
            if (nodes.hasOwnProperty(node))
            {
               // Get the wrapper and properties of the node
               var nodeProps = nodes[node],
                   nodeType = lang.getObject(this.syncNodesWhiteListProp, false, nodeProps),
                   wrapper = Registry.byId(node);
   
               if(wrapper != null)
               {
                  // Add the node wrapper to the return list if it is not in the white list
                  if(this.syncNodesWhiteList.indexOf(nodeType) == -1)
                  { 
                     widgets.push(wrapper);
                  }
                  
                  // Iterate over the widgets contained in each node
                  if(wrapper.widgets && wrapper.widgets instanceof Array)
                  {
                     for(var i=0; i < wrapper.widgets.length; ++i)
                     {
                        // For each wrapped item dig in to get the nested target
                        var wrappedItem = wrapper.widgets[i];
                        if(lang.getObject("config.id", false, wrappedItem))
                        {
                           // This function only considers 1 level of nesting. This could be recursive.
                           var target = Registry.byId(wrappedItem.config.id);
                           if(target.previewTarget)
                           {
                              // Get all nodes for a nested target
                              var wrappedNodes = target.previewTarget.getAllNodes();
                              for(var j=0; j < wrappedNodes.length; ++j)
                              {
                                 
                                 // Get the nested node
                                 var nodeProps = wrappedNodes[j],
                                     nodeType = lang.getObject(this.syncNodesWhiteListProp, false, nodeProps);
   
                                 // Add the node to the return list if not in the white list
                                 if(this.syncNodesWhiteList.indexOf(nodeProps) == -1)
                                 {
                                    widgets.push(nodeProps);
                                 }
                              }
                              
                              // De-select any nodes in the nested target
                              target.previewTarget.selectNone();
                           }
                        }
                     }
                  }
               }
            }
         }

         return widgets;
      }

   });
});