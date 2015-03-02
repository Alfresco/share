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
 * Email form module.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.EmailForm
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $hasEventInterest = Alfresco.util.hasEventInterest;

   Alfresco.module.EmailForm = function(htmlId)
   {
      Alfresco.module.EmailForm.superclass.constructor.call(this, "Alfresco.module.EmailForm", htmlId, ["button", "container", "connection"]);

      // Instance variables
      this.components = {};

      // Decoupled event listeners
      if (htmlId != "null")
      {
         // Make sure we listen for events when the user selects an authority
         YAHOO.Bubbling.on("itemSelected", this.onRecipientsSelected, this);
      }

      return this;
   };

   YAHOO.extend(Alfresco.module.EmailForm, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Template URL
          *
          * @property templateUrl
          * @type string
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/email-form"
          */
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/email-form"
      },

      /**
       * Object container for storing component instances.
       *
       * @property components
       * @type object
       * @default null
       */
      components: null,

      /**
       * Container element for template in DOM.
       *
       * @property containerDiv
       * @type HTMLElement
       */
      containerDiv: null,

      /**
       * The selected Recipients usernames
       *
       * @property recipients
       * @type Array
       */
      recipients: [],

      /**
       * A cache for the selected Recipients displayNames, using username as key and the displayName as value.
       *
       * @property recipients
       * @type {object}
       */
      recipientsCache: {},

      /**
       * Main entry point
       * 
       * @method showDialog
       * @param emailFormConfig {object} Data to fill the form with
       *        emailFormConfig.recipients {array} Array of the recipients usernames to send the email to
       *        emailFormConfig.subject {string) The emails subject
       *        emailFormConfig.message {string} The message of the email
       *        emailFormConfig.template {string} The template for the email
       */
      showDialog: function EF_showDialog(emailFormConfig)
      {
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: this.options.templateUrl,
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  obj: emailFormConfig,
                  scope: this
               },
               failureMessage: "Could not load template:" + this.options.templateUrl,
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog(emailFormConfig);
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       * @param emailFormConfig {object} Data to fill the form with
       */
      onTemplateLoaded: function EF_onTemplateLoaded(response, emailFormConfig)
      {
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);

         // Create and render the YUI dialog
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv);

         // Buttons & menues (note: Ok button's click will be handled in forms onBeforeAjaxSubmit)
         this.widgets.selectRecipientsButton = Alfresco.util.createYUIButton(this, "selectRecipients-button", this.onSelectRecipientsClick,
         {
            disabled: true
         });
         this.widgets.useTemplateMenu = Alfresco.util.createYUIButton(this, "useTemplate-menu", this.onUseTemplateMenuSelect,
         {
            type: "menu",
            menu: "useTemplate-options",
            lazyloadmenu: false
         });
         this.widgets.discardTemplateButton = Alfresco.util.createYUIButton(this, "discardTemplate-button", this.onDiscardTemplateButtonClick);
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);

         // Configure the forms runtime
         var form = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.form = form;

         /**
          * Comment is mandatory (note either message or template is required
          * but when a template is selected it will fill the message textare as well.
          * The validation for at least one recipient will be conencted to the subject field since
          * the recipient values are in hidden fields.
          */
         form.addValidation(this.id + "-recipients", this.mandatoryRecipients,
         {
            recipientsContainerEl: Dom.get(this.id + "-recipients")   
         }, "keyup", null, { validationType: "mandatory" });
         form.addValidation(this.id + "-subject", Alfresco.forms.validation.mandatory, null, "keyup");
         form.addValidation(this.id + "-message", Alfresco.forms.validation.mandatory, null, "keyup");

         // The ok button is the submit button, and it should be enabled when the form is ready
         form.setSubmitElements(this.widgets.okButton);

         // Add remove recipient click handler
         var me = this;
         var fnActionHandlerDiv = function TagLibrary_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               owner.parentNode.removeChild(owner);
               me.widgets.form.validate();
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("email-recipient-action", fnActionHandlerDiv);

         // Stop the form from being submitted and fire and event from the collected information
         form.doBeforeAjaxRequest =
         {
            fn: function(p_config, p_obj)
            {
               var dataobj = p_config.dataObj;

               // Fire event so other component know
               var options =
               {
                  recipients: dataobj.recipients,
                  subject: dataobj.subject
               };
               if (dataobj.template && dataobj.template.length > 0)
               {
                  options.template = dataobj.template;
               }
               else
               {
                  options.message = dataobj.message;
               }
               YAHOO.Bubbling.fire("emailFormCompleted",
               {
                  options: options,
                  eventGroup: this
               });

               // Close dialog
               this.widgets.dialog.hide();

               // Return false so the form isn't submitted
               return false;
            },
            obj: null,
            scope: this
         };

         // We're in a popup, so need the tabbing fix
         form.applyTabFix();
         form.init();

         // Register the ESC key to close the dialog
         YAHOO.util.Event.addListener(document, "keydown", 
            function(e)
            {
               if(e.keyCode == 27)
               {
                  this.onCancelClick();
               }
            },
         this, true);

         // Load in the Authority Finder component from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-finder",
            dataObj:
            {
               htmlid: this.id + "-authority-finder"
            },
            successCallback:
            {
               fn: this.onAuthorityFinderLoaded,
               scope: this
            },
            failureMessage: this.msg("message.authorityfinderfail"),
            execScripts: true
         });

         // Show the dialog
         this._showDialog(emailFormConfig);
      },

      /**
       * Called when the authority finder template has been loaded.
       * Creates a dialog and inserts the authority finder for choosing groups and users to add.
       *
       * @method onAuthorityFinderLoaded
       * @param response The server response
       */
      onAuthorityFinderLoaded: function EF_onAuthorityFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         Dom.get(this.id + "-authority-finder").innerHTML = response.serverResponse.responseText;

         this.widgets.authorityPicker = Alfresco.util.createYUIPanel(this.id + "-authority-picker");

         // Find the Authority Finder by container ID
         this.components.authorityFinder = Alfresco.util.ComponentManager.get(this.id + "-authority-finder");

         // Set the correct options for our use
         this.components.authorityFinder.setOptions(
         {
            viewMode: Alfresco.AuthorityFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true,
            minSearchTermLength: 3
         });
         var hidePickerListener = function(e)
         {
         if(!e) var e = window.event;

            if(e.keyCode == 27)
            {
               // Close dialog
               this.hide();
               //e.cancelBubble is supported by IE - this will kill the bubbling process.
               e.cancelBubble = true;
               e.returnValue = false;

               //e.stopPropagation for other browsers
               if ( e.stopPropagation ) e.stopPropagation();
               if ( e.preventDefault ) e.preventDefault();
            }
         };
         this.widgets.authorityPicker.hidePickerListener = hidePickerListener;
         this.widgets.authorityPicker.beforeShowEvent.subscribe(
            function()
            {
               YAHOO.util.Event.addListener(this.element, "keydown", this.hidePickerListener, this, true);
            }
         );
         this.widgets.authorityPicker.beforeHideEvent.subscribe(
            function()
            {
               YAHOO.util.Event.removeListener(this.element, "keydown", this.hidePickerListener);
            }
         );
         this.widgets.selectRecipientsButton.set("disabled", false);
      },


      /**
       * Mandatory validation handler, tests that there is at least one recpient.
       *
       * @method mandatory
       * @param field {object} Will be the subject field since the recipient fields are hidden
       * @param args {object} Not used
       * @param event {object} The event that caused this handler to be called, maybe null
       * @param form {object} The forms runtime class instance the field is being managed by
       * @param silent {boolean} Determines whether the user should be informed upon failure
       * @param message {string} Message to display when validation fails, maybe null
       * @static
       */
      mandatoryRecipients: function mandatory(field, args, event, form, silent, message)
      {
         return Selector.query("input[type=hidden]", args.recipientsContainerEl).length > 0; 
      },

      /**
       * Internal show dialog function
       *
       * @method _showDialog
       * @param emailFormConfig {object} Data to fill the form with
       */
      _showDialog: function EF__showDialog(emailFormConfig)
      {
         // Show form data
         emailFormConfig = emailFormConfig ? emailFormConfig : {};

         // Recipients
         Dom.get(this.id + "-recipients").innerHTML = "";
         this.recipients = YAHOO.lang.isArray(emailFormConfig.recipients) ? emailFormConfig.recipients : [];
         this._renderRecipients(this.recipients);

         // Subject
         Dom.get(this.id + "-subject").value = emailFormConfig.subject ? emailFormConfig.subject : "";

         // Message & Template
         var messageEl = Dom.get(this.id + "-message");
         messageEl.value = emailFormConfig.message && emailFormConfig.message.length > 0 ? emailFormConfig.message : "";
         if (emailFormConfig.template)
         {
            var menuItems = this.widgets.useTemplateMenu.getMenu().getItems();
            for (var i = 0, il = menuItems.length; i < il; i++)
            {
               if (menuItems[i].value == emailFormConfig.template)
               {
                  this.widgets.useTemplateMenu.set("label", menuItems[i].cfg.getProperty("text"));
               }
            }
            this._loadTemplate(emailFormConfig.template);
         }
         else
         {
            messageEl.removeAttribute("readonly");
            Dom.get(this.id + "-template").value = "";
         }

         // Enable the ok button
         this.widgets.okButton.set("disabled", false);

         // Show the dialog
         this.widgets.dialog.show();
      },

      /**
       * @method _renderRecipients
       * @param recipientIds {array} Ids of the rest of the recpients to load
       */
      _renderRecipients: function EF__renderRecipients(recipientIds)
      {
         if (recipientIds.length > 0)
         {
            var recipientId = recipientIds[recipientIds.length - 1];
            if (this.recipientsCache[recipientId])
            {
               this._renderRecipient(recipientId);
               recipientIds.pop();
               this._renderRecipients(recipientIds);
            }
            else if (recipientId.indexOf("GROUP_") == 0)
            {
               if (recipientId === "GROUP_EVERYONE")
               {
                  this.recipientsCache[recipientId] = "EVERYONE";
                  this._renderRecipients(recipientIds);
               }
               else
               {
                  Alfresco.util.Ajax.jsonGet(
                  {
                     url: Alfresco.constants.PROXY_URI_RELATIVE + "api/groups/" + recipientId.substring(6),
                     successCallback:
                     {
                        fn: function (p_oResponse, p_oObj)
                        {
                           var group = p_oResponse.json.data;
                           this.recipientsCache[recipientId] = group.displayName;
                           this._renderRecipients(p_oObj.recipientIds);
                        },
                        obj:
                        {
                           recipientIds: recipientIds
                        },
                        scope: this
                     }
                  });
               }
            }
            else
            {
               Alfresco.util.Ajax.jsonGet(
               {
                  url: Alfresco.constants.PROXY_URI_RELATIVE + "api/people/" + encodeURIComponent(recipientId),
                  successCallback:
                  {
                     fn: function (p_oResponse, p_oObj)
                     {
                        var person = p_oResponse.json;
                        this.recipientsCache[recipientId] = person.firstName + (person.lastName ? " " + person.lastName : "");
                        this._renderRecipients(p_oObj.recipientIds);
                     },
                     obj:
                     {
                        recipientIds: recipientIds
                     },
                     scope: this
                  }
               });
            }
         }
      },


      /**
       * @method _renderRecipient
       * @param recipientId  {string} The username/groupid of the recipient to render
       */
      _renderRecipient: function EF__renderRecipient(recipientId)
      {
         var recipientsEl = Dom.get(this.id + "-recipients"),
            recipientEl = document.createElement("li");
         Dom.addClass(recipientEl, recipientId.indexOf("GROUP_") == 0 ? "group" : "user");
         recipientEl.innerHTML = '<a href="#" class="email-recipient-action">' +
                                 '<input type="hidden" name="recipients[]" value="' + recipientId + '"/>' +
                                 '<span>' + $html(this.recipientsCache[recipientId]) + '</span>' +
                                 '<span class="remove">&nbsp;</span>' +
                                 '</a>';
         recipientsEl.appendChild(recipientEl);
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Select destination button event handler
       *
       * @method onSelectRecipientsClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSelectRecipientsClick: function EF_onSelectRecipientsClick(e, p_obj)
      {
         this.components.authorityFinder.clearResults();
         this.widgets.authorityPicker.show();
      },

      /**
       * Recipients selected in recipients dialog
       *
       * @method onReciepientsSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onRecipientsSelected: function EF_onReciepientsSelected(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this.components.authorityFinder, args))
         {
            var recipient = args[1];
            if (recipient !== null)
            {
               var i = 0;
               for (var il = this.recipients.length; i <il; i++)
               {
                  if (this.recipients[i] == recipient)
                  {
                     // Recipient was already added
                     break;
                  }
               }
               if (i == il)
               {
                  // Its a new recipient
                  this.recipients.push(recipient.itemName);
                  this.recipientsCache[recipient.itemName] = recipient.displayName;
               }
               this._renderRecipient(recipient.itemName);
               this.widgets.form.validate();
            }
            // Close dialog
            this.widgets.authorityPicker.hide();
         }
      },

      /**
       * Called when an option in the Run Rules menu has been called.
       *
       * @method onUseTemplateMenuSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onUseTemplateMenuSelect: function EF_onUseTemplateMenuSelect(sType, aArgs, p_obj)
      {
         // Get template so it can be displayed
         this.widgets.useTemplateMenu.set("label", aArgs[1].cfg.getProperty("text"));
         this._loadTemplate(aArgs[1].value);
      },

      /**
       * Loads a template and displays it
       *
       * @method _loadTemplate
       * @param template {string} The template to load
       */
      _loadTemplate: function EF__loadTemplate(template)
      {
         // Get template so it can be displayed
         var messageEl = Dom.get(this.id + "-message"),
            templateEl = Dom.get(this.id + "-template");
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/node/" + template.replace("://", "/") + "/content",
            successCallback:
            {
               fn: function(response)
               {
                  templateEl.value = template;
                  messageEl.value = response.serverResponse.responseText;
                  messageEl.setAttribute("readonly", true);
                  this.widgets.form.validate();
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.getTemplate-failure", template)
                  });
                  templateEl.value = "";
                  messageEl.value = "";
                  messageEl.removeAttribute("readonly");
                  this.widgets.form.validate();
               },
               scope: this
            }
         });
      },
      
      /**
       * Select destination button event handler
       *
       * @method onDiscardTemplateButtonClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onDiscardTemplateButtonClick: function EF_onDiscardTemplateButtonClick(e, p_obj)
      {
         var messageEl = Dom.get(this.id + "-message");
         messageEl.value = "";
         messageEl.removeAttribute("readonly");
         Dom.get(this.id + "-template").value = "";
         this.widgets.form.validate();
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancelClick: function EF_onCancelClick(e, p_obj)
      {
         this.widgets.dialog.hide();
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.EmailForm("null");
})();
