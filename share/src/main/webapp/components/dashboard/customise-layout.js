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
 * CustomiseLayout component.
 *
 * @namespace Alfresco
 * @class Alfresco.CustomiseLayout
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco.CustomiseLayout constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.CustomiseLayout} The new CustomiseLayout instance
    * @constructor
    */
   Alfresco.CustomiseLayout = function(htmlId)
   {
      Alfresco.CustomiseLayout.superclass.constructor.call(this, "Alfresco.CustomiseLayout", htmlId, ["button", "container", "datasource"]);
//      this.name = "Alfresco.CustomiseLayout";
//      this.id = htmlId;
//
//      // Register this component
//      Alfresco.util.ComponentManager.register(this);
//
//      // Load YUI Components
//      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource"], this.onComponentsLoaded, this);
//
      return this;
   };

//   Alfresco.CustomiseLayout.prototype =
   YAHOO.extend(Alfresco.CustomiseLayout, Alfresco.component.Base,
   {

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The current layout
          *
          * @property layouts
          * @type {object} {id: "", description: "", icon: ""}
          */
         currentLayout: {},

         /**
          * The avaiable layouts
          *
          * @property layouts
          * @type {object} {"layout.id":{templateId: "", description: "", icon: ""}}
          */
         layouts: {}
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.CustomiseLayout} returns 'this' for method chaining
       */
      setOptions: function CL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *       
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.CustomiseLayout} returns 'this' for method chaining
       */
      setMessages: function CL_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onReady: function CL_onReady()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }

         this.widgets.layoutLiElements = [];
         this.widgets.layoutUlElement = Dom.get(this.id +"-layout-ul");

         // Save reference to buttons so we can change label and such later
         this.widgets.changeButton = Alfresco.util.createYUIButton(this, "change-button", this.onChangeButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the select buttons for all layouts
         this.widgets.selectButtons = [];
         for (var layoutId in this.options.layouts)
         {
            var layoutLi = Dom.get(this.id +"-layout-li-" + layoutId);
            this.widgets.layoutLiElements[layoutId] = layoutLi;

            // Create the button and change layout when its clicked
            this.widgets.selectButtons[layoutId] = Alfresco.util.createYUIButton(this, "select-button-" + layoutId, function (event, button)
            {
               // Find out what layout that is chosen by looking at the clicked button's id
               var id = button.get("id");
               var selectedLayoutId = id.substring((this.id + "-select-button-").length);
               this.onSelectLayoutClick(selectedLayoutId);
            });

            // Add a listener to the image so we change layout when its clicked
            Event.addListener(this.id + "-select-img-" + layoutId, "click", function (event, obj)
            {
               obj.thisComponent.onSelectLayoutClick(obj.selectedLayoutId);
            },
            {
               selectedLayoutId: layoutId,
               thisComponent: this
            });

            // Remove layout/li-element from available layouts if its the current layout
            if (this.options.currentLayout.templateId == layoutId)
            {
               if (this.widgets.layoutUlElement)
               { 
                  this.widgets.layoutUlElement.removeChild(layoutLi);
               }
               else
               {
                  console.log("Element not available!");
               }
            }
         }
      },

      /**
       * Fired when the user clicks one of the select buttons or icons for a layout.
       * Changes the current layout to the selected layout and throws an global
       * event, that can be captured by other components, such as
       * Alfresco.CustomiseDashlets. 
       *
       * @method onSelectLayoutClick
       * @param selectedLayoutId {string} The id of the selected layout
       */
      onSelectLayoutClick: function CL_onSelectLayoutClick(selectedLayoutId)
      {
         // Get references to the divs that should be shown or hidden
         var layoutsDiv = Dom.get(this.id + "-layouts-div");
         var currentLayoutDiv = Dom.get(this.id + "-currentLayout-div");

         // Hide the div that displays the available layouts
         Dom.setStyle(layoutsDiv, "display", "none");
         for (var layoutId in this.options.layouts)
         {
            //var layoutLi = Dom.get(this.id + "-layout-li-" + layoutId);
            var layoutLi = this.widgets.layoutLiElements[layoutId];
            if (selectedLayoutId == layoutId)
            {
               // Set the current layout
               var selectedLayout = this.options.layouts[layoutId];
               this.options.currentLayout = selectedLayout;

               // Display the selected layout as the current one
               var descriptionSpan = Dom.get(this.id + "-currentLayoutDescription-span");
               descriptionSpan.innerHTML = selectedLayout.description;
               var iconImg = Dom.get(this.id + "-currentLayoutIcon-img");
               iconImg.src = selectedLayout.icon;

               // Send out event to let other component know that the layout has changed
               YAHOO.Bubbling.fire("onDashboardLayoutChanged",
               {
                  dashboardLayout: selectedLayout
               });
            }
         }

         /**
          * Remove all children from the availabla layout list. We could have just
          * used style="display:none" but that makes IE6 crash the layout in some combinations :-(
          */
         var lis = Dom.getChildren(this.widgets.layoutUlElement);
         for (var i = 0; i < lis.length; i++)
         {
            this.widgets.layoutUlElement.removeChild(lis[i]);
         }

         // Add the available layouts to the list
         for (var layoutId in this.options.layouts)
         {
            if (layoutId != selectedLayoutId)
            {
               this.widgets.layoutUlElement.appendChild(this.widgets.layoutLiElements[layoutId]);
            }
         }

         // Send out event to let other component know that the should show themselves
         YAHOO.Bubbling.fire("onDashboardLayoutsHidden", {});

         // Show the change layout button
         var changeButtonWrapperDiv = Dom.get(this.id + "-changeButtonWrapper-div");
         Dom.setStyle(changeButtonWrapperDiv, "display", "");
      },

      /**
       * Fired when the user clicks change layout button.
       * Shows the layout list.
       *
       * @method changeLayoutButton
       * @param event {object} an "click" event
       */
      onChangeButtonClick: function CL_onChangeButtonClick(event)
      {
         // Send out event to let other component know that the should hide themselves
         YAHOO.Bubbling.fire("onDashboardLayoutsDisplayed", {});

         // Hide the change button and show the layouts
         var changeButtonWrapperDiv = Dom.get(this.id + "-changeButtonWrapper-div");
         Dom.setStyle(changeButtonWrapperDiv, "display", "none");
         Alfresco.util.Anim.fadeIn(this.id + "-layouts-div");

      },

      /**
       * Fired when the user clicks cancel layout button.
       * Hides the layout list.
       *
       * @method onCancelButtonClick
       * @param event {object} an "click" event
       */
      onCancelButtonClick: function CL_onCancelButtonClick(event)
      {
         // Hide the available layouts-div
         var layoutsDiv = Dom.get(this.id + "-layouts-div");
         Dom.setStyle(layoutsDiv, "display", "none");

         // Send out event to let other component know that the should hide themselves
         YAHOO.Bubbling.fire("onDashboardLayoutsHidden", {});

         // Show the change button
         var changeButtonWrapperDiv = Dom.get(this.id + "-changeButtonWrapper-div");
         Dom.setStyle(changeButtonWrapperDiv, "display", "");
      }
   });
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.CustomiseLayout(null);
