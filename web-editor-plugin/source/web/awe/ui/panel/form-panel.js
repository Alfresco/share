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

(function()
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener,
       Selector = YAHOO.util.Selector,
       Bubbling = YAHOO.Bubbling;

   YAHOO.namespace('org.alfresco.awe.ui.FormPanel');

   /**
    * FormPanel constructor.
    *
    * @param containerId {string} A unique id for this component
    * @return {YAHOO.org.alfresco.awe.ui.FormPanel} The new DocumentList instance
    * @constructor
    */
   YAHOO.org.alfresco.awe.ui.FormPanel = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of AWE.component.FormPanel already exists.");
      }

      YAHOO.org.alfresco.awe.ui.FormPanel.superclass.constructor.call(this, "org.alfresco.awe.ui.FormPanel", containerId, ["button", "container", "connection", "selector", "json"]);

      return this;
   };

   YAHOO.extend(YAHOO.org.alfresco.awe.ui.FormPanel, YAHOO.org.alfresco.awe.ui.Panel,
   {
      /**
       * Shows the dialog
       *
       * @method show
       */
      show: function AweFormPanel_show()
      {
         if (this.widgets.panel)
         {
            /**
             * The panel gui has been showed before and its gui has already
             * been loaded and created
             */
            this._showPanel();
            YAHOO.org.springframework.extensions.webeditor.module.Ribbon.resizeRibbon();
         }
         else
         {
            /**
             * Load the gui from the server and let the templateLoaded() method
             * handle the rest.
             */
            if (this.options.formUri)
            {
               Alfresco.util.Ajax.request(
               {
                  url: this.options.formUri,
                  noReloadOnAuthFailure: true,
                  dataObj:
                  {
                     htmlid: this.id+'-'+this.options.formName,
                     showCancelButton:'true'
                  },

                  successCallback:
                  {
                     fn: this.onTemplateLoaded,
                     scope: this
                  },

                  failureCallback:
                  {
                     fn: function(args)
                     {
                        if (args.serverResponse.status == 401)
                        {
                           YAHOO.org.springframework.extensions.webeditor.PluginRegistry.getInstance('awe').login(
                           {
                              fn: function AWE_FormPanel_ReloadAfterLogin()
                              {
                                 YAHOO.org.springframework.extensions.webeditor.PluginRegistry.getInstance('awe').loadForm(
                                 {
                                    id: this.options.domContentId,
                                    formId: this.options.formId,
                                    nodeRef: this.options.nodeRef,
                                    title: this.options.title,
                                    nested: this.options.nested,
                                    redirectUrl: this.options.redirectUrl
                                 });
                              },
                              scope: this
                           });
                        }
                     },
                     scope: this
                  },
                  execScripts: true
               });
            }
         }
      },

      /**
       * Called when the FormPanel html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function AweFormPanel_onTemplateLoaded(response)
      {
         if (this.widgets.panel)
         {
            this.widgets.panel.destroy();
            this.widgets.panel = null;
         }

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChildBy(containerDiv, function(el) { return el.nodeName.toLowerCase() == 'div';});
         this.widgets.panel = new YAHOO.widget.Panel(panelDiv, 
         {
            width: "auto",
            modal: true,
            constraintoviewport: true,
            draggable: true,
            fixedcenter: "contained",
            close: true,
            visible: false,
            underlay:'none'
         });
         this.widgets.panel.setHeader(this.options.title);
         this.widgets.panel.render(Dom.get(this.id));
         
         // subscribe to the close (cross) icon close event
         var fnHideEventHandler = function AweFormPanel_fnHideEventHandler(p_sType, p_aArgs)
         {
            if(p_aArgs[0][0] == "visible" && p_aArgs[0][1] == false)
            {
               this.destroy();
               YAHOO.org.springframework.extensions.webeditor.module.Ribbon.resizeRibbon();
            }
         }
         this.widgets.panel.cfg.configChangedEvent.subscribe(fnHideEventHandler, this, true);

         YAHOO.Bubbling.on('beforeFormRuntimeInit', function(e, args) 
         {
            var form = args[1].runtime;
            var formComponent = args[1].component;
            formComponent.buttons.cancel.subscribe('click', this.onCancelButtonClick, this, true);
            // set up UI update
            form.doBeforeFormSubmit =
            {
               fn: function()
               {
                  // Unhook close button
                  this.widgets.panel.cfg.configChangedEvent.unsubscribe(fnHideEventHandler, null, this);
                  this.widgets.panel.hide();
                  YAHOO.org.springframework.extensions.webeditor.module.Ribbon.resizeRibbon();
                  
                  this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message("message.saving"),
                     spanClass: "wait",
                     displayTime: 0,
                     effect: null
                  },
                  Dom.get('wef'));
               },
               obj:null,
               scope:this
            };
         },
         this);

         // Show the panel
         this._showPanel();
         YAHOO.org.springframework.extensions.webeditor.module.Ribbon.resizeRibbon();
      },

      /**
       * Called when user clicks on the cancel button.
       * Closes the FormPanel panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function AweFormPanel_onCancelButtonClick(type, args)
      {
         this.hide();
         YAHOO.org.springframework.extensions.webeditor.module.Ribbon.resizeRibbon();
      },

      onUpdateContentUI: function AweFormPanel_onUpdateContentUI(args)
      {
         var contentElem = Dom.get(this.options.domContentId);
         var container  = document.createElement('div');
         container.innerHTML = args.serverResponse.responseText;

         // remove childnodes of src content sparing edit link
         var contentChildren = contentElem.childNodes;
         for (var i = contentChildren.length - 1; i >= 0; i--)
         {
            var el = contentChildren[i];
            if (!Selector.test(el, 'span.wef-edit'))
            {
               contentElem.removeChild(el);
            }
         }

         // add to dom
         contentChildren = container.childNodes;
         for (var i = contentChildren.length - 1; i >= 0; i--)
         {
            contentElem.insertBefore(contentChildren[i], Dom.getFirstChild(contentElem));
         }
      }
   });
})();

WEF.register("org.alfresco.awe.ui.form-panel", YAHOO.org.alfresco.awe.ui.FormPanel, {version: "1.0", build: "1"}, YAHOO);