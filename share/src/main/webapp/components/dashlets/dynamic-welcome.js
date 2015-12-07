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

/*
 * Alfresco.dashlet.UserWelcome
 * Registers a event handler on the 'Remove Me' button to have the component remove itself.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.UserWelcome
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
   
   /**
    * Alfresco library aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * DynamicWelcome constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {DynamicWelcome} The new component instance
    * @constructor
    */
   Alfresco.dashlet.DynamicWelcome = function DynamicWelcome_constructor(htmlId, dashboardUrl, dashboardType, site, siteTitle,
         docsEdition)
   {
      Alfresco.dashlet.DynamicWelcome.superclass.constructor.call(this, "Alfresco.dashlet.DynamicWelcome", htmlId, ["button"]);

      this.name = "Alfresco.dashlet.DynamicWelcome";
      this.dashboardUrl = dashboardUrl;
      this.createSite = null;
      this.dashboardType = dashboardType;
      this.site = site;
      this.siteTitle = decodeURIComponent(siteTitle);
      this.docsEdition = docsEdition;

      this.services.preferences = new Alfresco.service.Preferences();
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.DynamicWelcome, Alfresco.component.Base,
   {
      site: "",
      dashboardType: "",
      dashboardUrl: "",
      closeDialog: null,
      docsEdition: "",

      /**
       * CreateSite module instance.
       *
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function DynamicWelcome_onReady()
      {
         // Listen on clicks
         this.widgets.hideButton = Alfresco.util.createYUIButton(this, "hide-button", this.onHideButtonClick);
         if (this.dashboardType == "user")
         {
            Event.addListener(this.id + "-get-started-panel-container", "click", function() {
               location.href = this.msg("welcome.user.clickable-content-link", this.docsEdition);
            }, this, true);
         }
         Event.addListener(this.id + "-createSite-button", "click", this.onCreateSiteLinkClick, this, true);
         Event.addListener(this.id + "-requestJoin-button", "click", this.onRequestJoinLinkClick, this, true);
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param p_event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function DynamicWelcome_onCreateSiteLinkClick(p_event)
      {
         // Create the CreateSite module if it doesn't exist
         if (this.createSite === null)
         {
            this.createSite = Alfresco.module.getCreateSiteInstance();
         }
         // and show it
         this.createSite.show();
         Event.stopEvent(p_event);
      },

            /**
       * Fired by YUI Link when the "Request join" label is clicked
       * @method onRequestJoinLinkClick
       * @param p_event {domEvent} DOM event
       */
      onRequestJoinLinkClick: function DynamicWelcome_onRequestJoinLinkClick(p_event)
      {
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(Alfresco.constants.SITE) + "/invitations",
            dataObj:
            {
               invitationType: "MODERATED",
               inviteeUserName: Alfresco.constants.USERNAME,
               inviteeComments: "",
               inviteeRoleName: "SiteConsumer"
            },
            successCallback:
            {
               fn: this._requestJoinSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("message.request-join-failure", Alfresco.constants.USERNAME, this.siteTitle),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.request-joining", Alfresco.constants.USERNAME, this.siteTitle),
            spanClass: "wait",
            displayTime: 0
         });
         Event.stopEvent(p_event);
      },

      /**
       * Callback handler used when the current user successfully has requested to join the current site
       *
       * @method _requestJoinSuccess
       * @param response {object}
       */
      _requestJoinSuccess: function DynamicWelcome_requestJoinSuccess(response)
      {
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.destroy();
            this.widgets.feedbackMessage = null;
         }

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.success"),
            text: this.msg("message.request-join-success", Alfresco.constants.USERNAME, this.siteTitle),
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function error_onOk()
               {
                  this.destroy();
                  // Redirect the user back to their dashboard
                  window.location = Alfresco.constants.URL_CONTEXT;
               },
               isDefault: true
            }]
         });
      },

      /**
       * Generic failure callback handler
       *
       * @method _failureCallback
       * @private
       * @param message {string} Display message
       */
      _failureCallback: function DynamicWelcome__failureCallback(obj, message)
      {
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.destroy();
            this.widgets.feedbackMessage = null;
         }

         if (message)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.failure"),
               text: message
            });
         }
      },

      /**
       * Fired by YUI Link when the "Close" label is clicked
       * @method onCloseLinkClick
       * @param event {domEvent} DOM event
       */
      onCloseConfirm: function DynamicWelcome_onCloseConfirm(event)
      {
         // Depending upon the dashboard type we need to handle the close request differently...
         // Each user has their own unique configuration for a dashboard so when a close request
         // is received we can simply update the configuration. However, there is only one configuration
         // per site so we cannot use this approach. Instead we'll set a user preference for the
         // current site that indicates that the user no longer wishes to see the welcome dashlet...
         if (this.dashboardType == "user")
         {
            // Do the request and send the user to the dashboard after wards
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashlets/dynamic-welcome",
               dataObj:
               {
                  dashboardUrl: this.dashboardUrl
               },
               successCallback:
               {
                  fn: function()
                  {
                     // Send the user to the newly configured dashboard
                     document.location.href = Alfresco.constants.URL_PAGECONTEXT + "" + this.dashboardUrl;
                  },
                  scope: this
               },
               failureMessage: this.msg("message.saveFailure"),
               failureCallback:
               {
                  fn: function()
                  {
                     // Hide spinner
                     this.widgets.feedbackMessage.destroy();
                  },
                  scope: this
               }
            });
         }
         else
         {
            // Use the preferences services to update the users preferences for this site...
            // replace the forward slash "/" and dot "." characters with dash "-"
            var updatedSite = this.site.substring(1).replace(/\/|\./g, "-");
            this.services.preferences.set("org.alfresco.share.siteWelcome." + updatedSite, false,
            {
               successCallback:
               {
                  fn: function DynamicWelcome_onCloseConfirm_successCallback()
                  {
                     document.location.reload(true);
                  }
               }
            });
         }
      },

      /**
       * Hide welcome dashlet click event handler
       *
       * @method onHideButtonClick
       * @param e {Object} Event arguments
       */
      onHideButtonClick: function DynamicWelcome_onHideButtonClick(e, args)
      {
         var _this = this;
         var messageText = this.msg(this.dashboardType + ".panel.delete.msg");
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg(this.dashboardType + ".panel.delete.header"),
            text: messageText,
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function()
               {
                  this.destroy();
                  _this.onCloseConfirm();
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }],
            noEscape: true
         });

         var elements = Dom.getElementsByClassName('yui-button', 'span', 'prompt');
         Dom.addClass(elements[0], 'alf-primary-button');

         Event.stopEvent(e);
      }
   });
})();