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
 * CollaborationTitle component
 *
 * The title component of a collaboration site
 *
 * @namespace Alfresco
 * @class Alfresco.CollaborationTitle
 */
(function()
{
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * CollaborationTitle constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.CollaborationTitle} The new DocumentList instance
    * @constructor
    */
   Alfresco.CollaborationTitle = function(htmlId)
   {
      Alfresco.CollaborationTitle.superclass.constructor.call(this, "Alfresco.CollaborationTitle", htmlId);

      return this;
   };

   YAHOO.extend(Alfresco.CollaborationTitle, Alfresco.component.Base,
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
          * The current user
          *
          * @property user
          * @type string
          */
         user: null,

         /**
          * The current site
          *
          * @property site
          * @type string
          */
         site: null,

         /**
          * The current site's title
          *
          * @property siteTitle
          * @type string
          */
         siteTitle: null,

         /**
          * Is user a member of the current site
          * @property userIsMember
          * @type string
          */
          userIsMember: null,

         /**
         * Is current site PRIVATE, PUBLIC or MODERATED
         * @property currentSiteVisibility
         * @type string
         */
         currentSiteVisibility: null,

         /**
         * Is requested site exists
         * @property siteExists
         * @type boolean
         */
         siteExists: false
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function CollaborationTitle_onReady()
      {
          // MNT-3053 fix. Report a user if site exists
          if (!this.options.siteExists)
          {
              Alfresco.util.PopupManager.displayPrompt(
                  {
                      title: this.msg("message.site-does-not-exist-title", this.options.site),
                      text: this.msg("message.site-does-not-exist"),
                      buttons: [
                          {
                              text: this.msg("button.ok"),
                              handler: function ()
                              {
                                  this.destroy();
                              },
                              isDefault: true
                          }
                      ]
                  });
          }
          else
          // MNT-9185 fix. Report a user if he doesn't have access rights to the site, except PUBLIC site.
          if (this.options.userIsMember == "false" && this.options.currentSiteVisibility !=="PUBLIC")
          {
              var errorMessage;
              switch (this.options.currentSiteVisibility)
              {
                  case "MODERATED":
                      errorMessage = this.msg("message.moderated-site");
                      break;
                  // By default setting up error message for private site.
                  default:
                      errorMessage = this.msg("message.private-site");
                      break;
              }
              Alfresco.util.PopupManager.displayPrompt(
                  {
                      title: this.msg("message.title-site-permission", this.options.site),
                      text: this.msg(errorMessage),
                      buttons: [
                          {
                              text: this.msg("button.ok"),
                              handler: function ()
                              {
                                  this.destroy();
                              },
                              isDefault: true
                          }
                      ]
                  });
          }
         // Add event listeners. We use Dom.get() so that Event doesn't add an onAvailable() listener for non-existent elements.
         Event.on(Dom.get(this.id + "-join-link"), "click", function(e)
         {
            this.joinSite();
            Event.stopEvent(e);
         }, this, true);

         Event.on(Dom.get(this.id + "-requestJoin-link"), "click", function(e)
         {
            this.requestJoinSite();
            Event.stopEvent(e);
         }, this, true);
         
         Event.on(Dom.get(this.id + "-become-manager-link"), "click", function(e)
         {
            this.becomeSiteManager();
            Event.stopEvent(e);
         }, this, true);

         // Create More menu
         this.widgets.more = new YAHOO.widget.Button(this.id + "-more",
         {
            type: "menu",
            menu: this.id + "-more-menu"
         });

         if (this.widgets.more.getMenu())
         {
            this.widgets.more.getMenu().subscribe("click", function(p_sType, p_aArgs)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  switch (menuItem.value)
                  {
                     case "editSite":
                        Alfresco.module.getEditSiteInstance().show(
                        {
                           shortName: this.options.site
                        });
                        break;

                     case "customiseSite":
                        window.location =  Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.site + "/customise-site";
                        break;

                     case "leaveSite":
                        var me = this;
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: me.msg("message.leave", me.options.siteTitle),
                           text: me.msg("message.leave-site-prompt",  me.options.siteTitle),
                           buttons:
                           [
                              {
                                 text: me.msg("button.ok"),
                                 handler: function leaveSite_onOk()
                                 {
                                    me.leaveSite();
                                    this.destroy();
                                 }
                              },
                              {
                                 text: me.msg("button.cancel"),
                                 handler: function leaveSite_onCancel()
                                 {
                                    this.destroy();
                                 },
                                 isDefault: true
                              }
                           ]
                        });
                        break;
                  }
               }
            }, this, true);
         }

         // Make the buttons and menus visible now that the dom has been enhanced
         Dom.removeClass(Selector.query(".links", this.id, true), "hidden");
      },

      /**
       * Called when the user clicks on the join site button
       *
       * @method joinSite
       */
      joinSite: function CollaborationTitle_joinSite()
      {
         // Call site service to join current user to current site
         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(this.options.site) + "/memberships",
            dataObj:
            {
               role: "SiteConsumer",
               person:
               {
                  userName: this.options.user
               }
            },
            successCallback:
            {
               fn: function CollaborationTitle__joinSiteSuccess()
               {
                  // Reload page to make sure all new actions on the current page are available to the user
                  window.location.reload();
               },
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("message.join-failure", this.options.user, this.options.siteTitle),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.joining", this.options.user, this.options.siteTitle),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Called when the user clicks on the request-to-join site button
       *
       * @method requestJoinSite
       */
      requestJoinSite: function CollaborationTitle_requestJoinSite()
      {
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(this.options.site) + "/invitations",
            dataObj:
            {
               invitationType: "MODERATED",
               inviteeUserName: this.options.user,
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
               obj: this.msg("message.request-join-failure", this.options.user, this.options.siteTitle),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.request-joining", this.options.user, this.options.siteTitle),
            spanClass: "wait",
            displayTime: 0
         });
      },
      
      /**
       * Called when the admin clicks on the -become-manager-link button
       *
       * @method becomeSiteManager
       */
      becomeSiteManager: function CollaborationTitle_becomeSiteManager()
      {
         // Call site service to remove user from this site
         Alfresco.util.Ajax.jsonPut(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(this.options.site) + "/memberships",
            method: "POST",
            dataObj:
            {
               role: "SiteManager",
               person:
               {
                  userName: this.options.user
               }
            },
            successCallback:
            {
               fn: this._becomeSiteManagerSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("message.become-manager-failure", this.options.user, this.options.siteTitle),
               scope: this
            }
         });
         
         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.become-manager", this.options.siteTitle),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Callback handler used when the current user (who should be a member of the admin group) has requested
       * to become a manager of the current site.
       * 
       * @method _becomeSiteManagerSuccess
       * @param response {object}
       */
      _becomeSiteManagerSuccess: function CollaborationTitle__becomeSiteManagerSuccess(response)
      {
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.destroy();
            this.widgets.feedbackMessage = null;
         }

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.success"),
            text: this.msg("message.become-manager-success"),
            buttons: [
            {
               text: this.msg("button.ok"),
               handler: function error_onOk()
               {
                  this.destroy();
                  // Redirect the user back to the site dashboard
                  window.location.reload(true);
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Callback handler used when the current user successfully has requested to join the current site
       *
       * @method _requestJoinSuccess
       * @param response {object}
       */
      _requestJoinSuccess: function CollaborationTitle__requestJoinSuccess(response)
      {
         if (this.widgets.feedbackMessage)
         {
            this.widgets.feedbackMessage.destroy();
            this.widgets.feedbackMessage = null;
         }

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.success"),
            text: this.msg("message.request-join-success", this.options.user, this.options.siteTitle),
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
       * Called when the user clicks on the leave site button
       *
       * @method leaveSite
       */
      leaveSite: function CollaborationTitle_leaveSite()
      {
         // Call site service to remove user from this site
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + encodeURIComponent(this.options.site) + "/memberships/" + encodeURIComponent(this.options.user),
            method: "DELETE",
            successCallback:
            {
               fn: function CollaborationTitle__leaveSiteSuccess()
               {
                  // Navigate to the default page as they are no longer a member of this site
                  window.location.href = Alfresco.constants.URL_CONTEXT;
               },
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: this.msg("message.leave-failure", this.options.user, this.options.siteTitle),
               scope: this
            }
         });

         // Let the user know something is happening
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.leaving", this.options.user, this.options.siteTitle),
            spanClass: "wait",
            displayTime: 0
         });
      },

      /**
       * Generic failure callback handler
       *
       * @method _failureCallback
       * @private
       * @param message {string} Display message
       */
      _failureCallback: function CollaborationTitle__failureCallback(obj, message)
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
      }
   });
})();