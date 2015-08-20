/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * Alfresco.WikiToolbar
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   var $msg = function(){};

   /**
   * WikiToolbar constructor.
   * 
   * @param {String} htmlId The HTML id of the parent element
   * @return {Alfresco.WikiToolbar} The new WikiToolbar instance
   * @constructor
   */
   Alfresco.WikiToolbar = function(containerId)
   {
      this.name = "Alfresco.WikiToolbar";
      this.id = containerId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      // Initialise prototype properties
      this.widgets = {};
      this.popups = {};

      // Decoupled event listeners
      YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);

      return this;
   };

   Alfresco.WikiToolbar.prototype =
   {

      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * Sets the current site for this component.
          *
          * @property siteId
          * @type string
          */
         siteId: null,

         /**
          * The title of the current page.
          *
          * @property title
          * @type string
          */
         title: null,

         /**
          * Indicating if back link is used
          *
          * @property showBackLink
          * @type string
          */
         showBackLink: false,

         /**
          * To tell if the wiki page exists in the repo yet.
          *
          * @property statusCode
          * @type boolean
          */
         exists: true
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Object container for storing pop-up window instances.
       * 
       * @property popups
       * @type object
       */
      popups: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.WikiToolbar} returns 'this' for method chaining
       */
      setOptions: function WikiToolbar_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.WikiToolbar} returns 'this' for method chaining
       */
      setMessages: function WikiToolbar_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         $msg = this._msg;
         return this;
      },
         
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function WikiToolbar_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WikiToolbar_onReady()
      {
         // Register buttons with YUI
         this.widgets.newPageButton = Alfresco.util.createYUIButton(this, "create-button", this.onNewPageClick,
         {
            disabled: true,
            value: "create"
         });
         this.widgets.deletePageButton = Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick,
         {
            disabled: true,
            value: "delete"
         });
         this.widgets.renamePageButton = Alfresco.util.createYUIButton(this, "rename-button", this.onRenameClick,
         {
            disabled: true,
            value: "edit"
         });
         this.widgets.rssFeedButton = Alfresco.util.createYUIButton(this, "rssFeed-button", null,
         {
            type: "link"
         });
         if (this.widgets.rssFeedButton !== null)
         {
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/wiki/rss?site={site}",
                {
                    site: encodeURIComponent(this.options.siteId)
                });
            this.widgets.rssFeedButton.set("href", url);
         }
         
         // Create the rename panel
         var renamePanel = Dom.get(this.id + "-renamepanel"),
            clonedRenamePanel = renamePanel.cloneNode(true);
         
         renamePanel.parentNode.removeChild(renamePanel);
         
         this.popups.renamePanel = Alfresco.util.createYUIPanel(clonedRenamePanel,
         {
            width: "320px"
         });
         
         var renameSaveButton = Alfresco.util.createYUIButton(this, "rename-save-button", null,
         {
            type: "submit"
         });
         
         var renameForm = new Alfresco.forms.Form(this.id + "-renamePageForm");
         renameForm.addValidation(this.id + "-renameTo", Alfresco.forms.validation.mandatory, null, "blur");
         renameForm.addValidation(this.id + "-renameTo", Alfresco.forms.validation.wikiTitle, null, "keyup");
         renameForm.addValidation(this.id + "-renameTo", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup", this._msg("Alfresco.forms.validation.length.message"));
         
         renameForm.setSubmitElements(renameSaveButton);
         renameForm.ajaxSubmitMethod = Alfresco.util.Ajax.POST;
         renameForm.doBeforeAjaxRequest =
         {
            fn: function WikiToolbar_doBeforeAjaxRequest(config, obj)
            {
               var pageTitle = config.dataObj.name;
               config.dataObj.name = pageTitle.replace(/\s+/g, "_");
               return true;
            },
            scope: this
         };
         renameForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageRenamed,
               scope: this
            },
            failureCallback:
            {
               fn: this.onPageRenameFailed,
               scope: this
            }
         });        
         renameForm.setSubmitAsJSON(true);
         renameForm.applyTabFix();
         renameForm.init();
         
         // Listen for when an event has been updated
         YAHOO.Bubbling.on("deletePage", this.onDeletePage, this);
      },

      /**
       * User Access event handler
       *
       * @method onUserAccess
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onUserAccess: function WikiToolbar_onUserAccess(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.userAccess !== null))
         {
            var widget, widgetPermissions, index;
            for (index in this.widgets)
            {
               if (this.widgets.hasOwnProperty(index))
               {
                  widget = this.widgets[index];
                  if (widget.get("srcelement").className != "no-access-check" && (!(widget._button != null && widget._button.className == "no-access-check")))					  				  
                  {
                     widget.set("disabled", false);
                     if (typeof widget.get("value") == "string")
                     {
                        widgetPermissions = widget.get("value").split(",");
                        for (var i = 0, ii = widgetPermissions.length; i < ii; i++)
                        {
                           if (!obj.userAccess[widgetPermissions[i]])
                           {
                              widget.set("disabled", true);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function WikiToolbar_onDeactivateAllControls(layer, args)
      {
         var index, widget, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      /**
       * Dispatches the browser to the create wiki page
       *
       * @method onNewPageClick
       * @param e {object} DomEvent
       */
      onNewPageClick: function WikiToolbar_onNewPageClick(e)
      {
         var url = Alfresco.constants.URL_PAGECONTEXT + "site/" + encodeURIComponent(this.options.siteId) + "/wiki-create";
         if (!this.options.showBackLink)
         {
            url += "?listViewLinkBack=true";
         }
         window.location.href = url;
      },

      /**
       * Kicks off a page delete confirmation dialog.
       * Fired when a delete link is clicked - 
       * primarily the "delete" link on the listing page.
       *
       * @method onDeletePage
       * @param e {object} DomEvent
       */      
      onDeletePage: function WikiToolbar_onDeletePage(e, args)
      {
         var title = args[1].title;
         if (title)
         {
            this.options.title = title;
            this._deletePage();
         }
      },
      
      _deletePage: function WikiToolbar__deletePage()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this._msg("panel.confirm.header"),
            text: this._msg("panel.confirm.delete-msg"),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function()
               {
                  this.destroy();
                  me.onConfirm();
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
         
         var elements = Dom.getElementsByClassName('yui-button', 'span', 'prompt');
         Dom.addClass(elements[0], 'alf-primary-button');
      },
      
      /**
       * Fired when the user confirms that they want to delete a page. 
       * Kicks off a DELETE request to the Alfresco repo to remove an event.
       *
       * @method onConfirm
       * @param e {object} DomEvent
       */
      onConfirm: function WikiToolbar_onConfirm(e)
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "slingshot/wiki/page/" + encodeURIComponent(this.options.siteId) + "/" + Alfresco.util.encodeURIPath(this.options.title) + "?page=wiki",
            successCallback:
            {
               fn: this.onPageDeleted,
               scope: this
            },
            failureMessage: this._msg("load.fail")
         });
      },
      
      /**
       * Callback handler then gets invoked when a page is 
       * successfully deleted.
       * 
       * @method onPageDeleted
       * @param e {object} DomEvent
       */
      onPageDeleted: function WikiToolbar_onPageDeleted(e)
      {
         // Redirect to the wiki landing page
         var landingPage = Alfresco.constants.URL_PAGECONTEXT + "site/" + encodeURIComponent(this.options.siteId) + "/wiki";
         if (window.location.pathname == landingPage)
         {
            window.location.reload(true);
         }
         else
         {
            window.location = landingPage;
         }
      },
      
      /**
       * Event handler for the rename button in the toolbar.
       * Pops up the rename dialog.
       *
       * @method onRenameClick
       * @param e {object} DomEvent
       */
      onRenameClick: function WikiToolbar_onRenameClick(e)
      {
         this.popups.renamePanel.show();

         // Clear the text field any previously entered values
         var newNameField = Dom.get(this.id + "-renameTo");
         newNameField.value = this.options.title.replace(/_/g, " ");
         
         // Fix Firefox caret issue
         var formElement = Dom.get(this.id + "-renamePageForm");
         Alfresco.util.caretFix(formElement);

         // Register the ESC key to close the dialog
         if (!this.escapeListener)
         {
            this.escapeListener = new YAHOO.util.KeyListener(document,
            {
               keys: YAHOO.util.KeyListener.KEY.ESCAPE
            },
            {
               fn: function WikiToolbar_onRename_escapeListener(id, keyEvent)
               {
                  // Undo Firefox caret issue
                  Alfresco.util.undoCaretFix(formElement);
                  this.popups.renamePanel.hide();
               },
               scope: this,
               correctScope: true
            });
         }
         this.escapeListener.enable();

         // Set focus to fileName input
         newNameField.focus();
      },

      /**
       * Gets called when a page is successfully renamed.
       * Sets the window location to the URL of the new page.
       *
       * @method onPageRenamed
       * @param e {object} DomEvent
       */      
      onPageRenamed: function WikiToolbar_onPageRenamed(e)
      {
         var response = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (response)
         {
            if (!YAHOO.lang.isUndefined(response.title))
            {
               // Change the location bar
               window.location = Alfresco.constants.URL_PAGECONTEXT + "site/" + encodeURIComponent(this.options.siteId) + "/wiki-page?title=" + encodeURIComponent(response.title);
            } 
            else
            {
               // A problem occurred
               var errorMsg = "Rename failed: ";
               if (!YAHOO.lang.isUndefined(response.error))
               {
                  errorMsg += response.error;
               }
               else
               {
                  errorMsg += "Unknown error occurred."
               }
               
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: errorMsg
               });
            }
         }
      },

      /**
       * Gets called when a page rename failed.
       *
       * @method onPageRenameFailed
       * @param e {object} DomEvent
       * @param e {string} Attempted page title
       */      
      onPageRenameFailed: function WikiToolbar_onPageRenameFailed(e, pageTitle)
      {
         var me = this;

         if (e.serverResponse.status === 404 && !this.options.exists)
         {
            // We tried to rename a page that doesn't yet exist, just forward the user to the new page
            // which also won't exist but display itself like it does.
            var url = Alfresco.constants.URL_PAGECONTEXT + "site/" + encodeURIComponent(this.options.siteId) + "/wiki-page?title=" + encodeURIComponent(e.config.dataObj.name);
            if (!this.options.showBackLink)
            {
               url += "?listViewLinkBack=true";
            }
            window.location.href = url;
         }
         else
         {
            var defaultKey = "rename.failure",
               statusKey = defaultKey + "." + e.serverResponse.status,
               text = $msg(statusKey, pageTitle);
            if (statusKey == text)
            {
               text = $msg(defaultKey, pageTitle)
            }
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: $msg("rename.failure.title"),
               text: text,
               buttons: [
                  {
                     text: $msg("button.ok"),
                     handler: function()
                     {
                        this.destroy();
                        Dom.get(me.id + "-renameTo").focus();
                     },
                     isDefault: true
                  }]
            });
         }
      },
      
      /**
       * Event handler for the delete button in the toolbar.
       * Pops up the delete confirmation dialog.
       *
       * @method onDeleteClick
       * @param e {object} DomEvent
       */
      onDeleteClick: function WikiToolbar_onDeleteClick(e)
      {
         this._deletePage();
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function WikiToolbar__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.WikiToolbar", Array.prototype.slice.call(arguments).slice(1));
      }
   };

})();   
