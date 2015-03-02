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
 * GenericFormTool tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.GenericFormTool
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * GenericFormTool constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.GenericFormTool} The new GenericFormTool instance
    * @constructor
    */
   Alfresco.GenericFormTool = function(htmlId)
   {
      this.name = "Alfresco.GenericFormTool";
      Alfresco.GenericFormTool.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "history"], this.onComponentsLoaded, this);

      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* View Panel Handler */
      ViewPanelHandler = function ViewPanelHandler_constructor()
      {
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };
      
      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onBeforeShow: function onBeforeShow()
         {
            // Load the form for the specific workflow
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
               dataObj:
               {
                  htmlid: parent.id + "-" + parent.options.mode + "-" + Alfresco.util.generateDomId(),
                  itemKind: parent.options.itemKind,
                  itemId: parent.options.itemId,
                  mode: "view",
                  formUI: false,
                  err: parent.options.errorKey || ""
               },
               successCallback:
               {
                  fn: function GenericFormTool_onLoad_onFormLoaded(response)
                  {
                     var formEl = Dom.get(parent.id + "-" + this.id + "-form");
                     formEl.innerHTML = response.serverResponse.responseText;
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.failure"),
               scope: this,
               execScripts: true
            });
         }
      });
      new ViewPanelHandler();

      /* Edit Panel Handler */
      EditPanelHandler = function EditPanelHandler_constructor()
      {
         EditPanelHandler.superclass.constructor.call(this, "edit");
         YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
         YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      };

      YAHOO.extend(EditPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onBeforeShow: function onBeforeShow()
         {
            // Load the form for the specific workflow
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
               dataObj:
               {
                  htmlid: parent.id + "-" + parent.options.mode + "-" + Alfresco.util.generateDomId(),
                  itemKind: parent.options.itemKind,
                  itemId: parent.options.itemId,
                  mode: "edit",
                  formUI: true,
                  submitType: "json",
                  showCaption: true,
                  showCancelButton: parent.options.showCancelButton,
                  showSubmitButton: parent.options.showSubmitButton
               },
               successCallback:
               {
                  fn: function GenericFormTool_onLoad_onFormLoaded(response)
                  {
                     var formEl = Dom.get(parent.id + "-" + this.id + "-form");
                     formEl.innerHTML = response.serverResponse.responseText;
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.failure"),
               scope: this,
               execScripts: true
            });
         },

         onFormContentReady: function FormManager_onFormContentReady(layer, args)
         {
            if (args[1].buttons.submit)
            {
               args[1].buttons.submit.set("label", parent.msg("button.save"));
            }
            if (args[1].buttons.cancel)
            {
               args[1].buttons.cancel.addListener("click", parent.showViewPanel, parent, true);
            }
         },

         onBeforeFormRuntimeInit: function FormManager_onBeforeFormRuntimeInit(layer, args)
         {
            args[1].runtime.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: function()
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.save.success")
                     });
                     this.showViewPanel();
                  },
                  scope: parent
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"), 
                        text: (response.json && response.json.message ? response.json.message : this.msg("message.save.failure"))
                     });
                  },
                  scope: parent
               }
            });
         }
      });
      new EditPanelHandler();

      return this;
   };
   
   YAHOO.extend(Alfresco.GenericFormTool, Alfresco.ConsoleTool,
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
          * Item kind
          * 
          * @property itemKind
          * @type string
          */
         itemKind: null,
         
         /**
          * Item id
          *
          * @property itemId
          * @type string
          */
         itemId: null,

         /**
          * Error key
          *
          * @property errorKey
          * @type string
          */         
         errorKey: null,


         showCancelButton: true,

         showSubmitButton: true
      },

      showViewPanel: function()
      {
         this.refreshUIState({"panel": "view"});
      },

      showEditPanel: function()
      {
         this.refreshUIState({"panel": "edit"});
      }

   });
})();