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
 * LinksView component.
 *
 * Component to view a link
 *
 * @namespace Alfresco
 * @class Alfresco.LinksView
 * @extends Alfresco.component.Base
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
   var $html = Alfresco.util.encodeHTML,
      $links = Alfresco.util.activateLinks;

   /**
    * LinksView constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.LinksView} The new LinksView instance
    * @constructor
    */
   Alfresco.LinksView = function(htmlId)
   {
      Alfresco.LinksView.superclass.constructor.call(this, "Alfresco.LinksView", htmlId, ["json", "connection", "event", "button", "menu"]);

      this.tagId =
      {
         id: 0,
         tags: {}
      };

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);

      return this;
   };

   YAHOO.extend(Alfresco.LinksView, Alfresco.component.Base,
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
          * Current siteId.
          *
          * @property siteId
          * @type string
          * @default ""
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "links"
          */
         containerId: "links",

         /**
          * Id of the displayed link.
          * 
          * @property linkId
          * @type string
          * @default ""
          */
         linkId: ""
      },

      /**
       * Stores the data displayed by this component
       */
      linksData: null,

      /**
       * Object literal used to generate unique tag ids
       *
       * @property tagId
       * @type object
       */
      tagId: null,

      /**
       * Tells whether an action is currently ongoing.
       *
       * @property busy
       * @type boolean
       * @see setBusy/releaseBusy
       */
      busy: false,
      
      PROTOCOL_STR_DELIM : /((.*):\/\/(.*))/,
      PORT_STR_DELIM : /^((.*):(\d{1,})((\/.*){0,}))/,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function LinksView_onReady()
      {
         var me = this;

         // Hook action events.
         var fnActionHandlerDiv = function LinksView_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, me.linksData.name);
                  args[1].stop = true;
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("link-action-link-div", fnActionHandlerDiv);

         // Hook tag clicks
         Alfresco.util.tags.registerTagActionHandler(this);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onLinkElementMouseEntered, this.onLinkElementMouseExited, this);

         // load the link data
         this._loadLinksData();
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadLinksData: function LinksView__loadLinksData()
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/link/site/{site}/{container}/{linkId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            linkId: this.options.linkId
         });

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: this.loadLinksDataSuccess,
               scope: this
            },
            failureMessage: this.msg("message.loadlinkdata.failure")
         });
      },

      /**
       * Success handler for a link request. Updates the UI using the link data
       * provided in the response object.
       *
       * @param response {object} the ajax request response
       */
      loadLinksDataSuccess: function LinksView_loadLinksDataSuccess(response)
      { 
         // store the returned data locally
         var data = response.json.item;
         this.linksData = data;
         

         // get the container div 
         var viewDiv = Dom.get(this.id + '-link-view-div');

         // render the link and insert it into the div
         var html = this.renderLinks(data);
         viewDiv.innerHTML = html;

         // attach the rollover listeners
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'link', 'div');

         // inform interested comment components about the loaded link
         this.sendCommentedNodeEvent();
      },

      /**
       * Sends out a setCommentedNode bubble event.
       */
      sendCommentedNodeEvent: function LinksView_sendCommentedNodeEvent()
      {
         var eventData =
         {
            nodeRef: this.linksData.nodeRef,
            title: this.linksData.title,
            page: "links-view",
            pageParams:
            {
               linkId: this.linksData.name
            }
         };
         YAHOO.Bubbling.fire("setCommentedNode", eventData);
      },
       
      /**
       * Renders the links.
       */
      renderLinks: function LinksView_renderLinks(data)
      {
         var me = this;
         // preformat some values
         var linksViewUrl = me.generateLinksViewUrl(this.options.siteId, this.options.containerId, data.name);
         var authorLink = Alfresco.util.people.generateUserLink(data.author);

         var html = '';
         html += '<div id="' + this.id + '-linksview" class="node linksview theme-bg-2">';
         html += Alfresco.util.links.generateLinksActions(this, data, 'div');
         
         var needHttpPrefix = function(userUrl)
         {
            // check for "://" in URI
            if (me.PROTOCOL_STR_DELIM.test(userUrl))
            {
               return false;
            }
            
            // check for digits after ":"
            if (me.PORT_STR_DELIM.test(userUrl))
            {
               return true;
            }
            
            // URI with port was filtered in previous block. Therefore URI with ":" no need "http" prefix
            if (userUrl.indexOf(":")> -1)
            {
               return false;
            }
            
            // default value
            return true;
         };

         // Prepare url attribute
         var href = (needHttpPrefix(data.url) ? 'http://' : '') + data.url.replace(/"/g, encodeURIComponent('"'));

         // Link details
         html += '<div class="nodeContent">';
         html += '<div class="nodeTitle"><a href="' + linksViewUrl + '">' + $html(data.title) + '</a></div>';

         html += '<div class="nodeURL">';
         html += '<span class="nodeAttrLabel">' + this.msg("link.url") + ": </span><a " + (data.internal ? '' : 'target="_blank" class="external"') + ' href="' + href + '">' + $html(data.url) + "</a>";
         html += '</div>';

         html += '<div class="detail">';
         html += '<span class="nodeAttrLabel">' + this.msg("link.createdOn") + ': </span>';
         html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.createdOn) + '</span>';
         html += '<span class="separator">&nbsp;</span>';
         html += '<span class="nodeAttrLabel">' + this.msg("link.createdBy") + ': </span>';
         html += '<span class="nodeAttrValue">' + authorLink + '</span>';
         html += '</div>';

         html += '<div class="detail">';
         html += '<span class="nodeAttrLabel">' + this.msg("label.description") + ": </span>";
         html += '<span class="nodeAttrValue">' + $links($html(data.description)) + '</span>';
         html += '</div>';
         
         html += '<div class="nodeFooter">';
         html += '<span class="nodeAttrLabel tagLabel">' + this.msg("label.tags") + ': </span>';
         if (data.tags.length > 0)
         {
            for (var x=0; x < data.tags.length; x++)
            {
               if (x > 0)
               {
                  html += ', ';
               }
               html += Alfresco.util.tags.generateTagLink(this, data.tags[x]);
            }
         }
         else
         {
            html += '<span class="nodeAttrValue">' + this.msg("link.noTags") + '</span>';
         }
         html += '</div>';

         html += '</div>';

         return html;
      },

      /**
       * Generate a view url for a given site, link id.
       *
       * @param linkId the id/name of the link
       * @return an url to access the link
       */
      generateLinksViewUrl: function LinksView_generateLinksViewUrl(site, container, linkId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links-view?linkId={linkId}",
         {
            site: site,
            linkId: linkId
         });
         return url;
      },

      // Actions

      /**
       * Tag selected handler.
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function LinksView_onTagSelected(layer, args)
      { 
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links?filterId={filterId}&filterOwner={filterOwner}&filterData={filterData}",
            {
               site: this.options.siteId,
               filterId: "tag",
               filterOwner: "Alfresco.TagFilter",
               filterData: obj.tagName
            });
            window.location = url;
         }
      },

      /**
       * Link deletion implementation
       *
       * @method onDeleteLink
       * @param linkId {string} the id of the link to delete
       */
      onDeleteLink: function LinksView_onDeleteLink(linkId)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete", $html(this.linksData.title)),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function LinksView_onDeleteLink_delete()
               {
                  this.destroy();
                  me._deleteLinkConfirm.call(me, me.linksData.name);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function LinksView_onDeleteLink_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Link deletion implementation
       *
       * @method _deleteLinkConfirm
       * @param linkId {string} the id of the link to delete
       */
      _deleteLinkConfirm: function LinksView__deleteLinkConfirm(linkId)
      {
         // show busy message
         if (! this._setBusy(this.msg('message.wait')))
         {
            return;
         }

         // ajax request success handler
         var onDeletedSuccess = function LinksView_onDeletedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // load the link list page
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links",
            {
               site: this.options.siteId
            });
            window.location = url;
         };

         // get the url to call
         
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/delete/site/{site}/{container}",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            successMessage: this.msg("message.delete.success"),
            successCallback:
            {
               fn: onDeletedSuccess,
               scope: this
            },
            failureMessage: this.msg("message.delete.failure"),
            failureCallback:
            {
               fn: function(response) { this._releaseBusy(); },
               scope: this
            },
            dataObj :
            {
               items : [linkId]
            }
         });
      },

      /**
       * Loads the edit link form and displays it instead of the content
       */
      onEditLink: function LinksView_onEditNode(linkId)
      {  
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links-linkedit?linkId={linkId}",
         {
            site: this.options.siteId,
            linkId: linkId
         });
         window.location = url;
      },

      // mouse hover functionality

      /** Called when the mouse enters into a list item. */
      onLinkElementMouseEntered: function LinksView_onLinkElementMouseEntered(layer, args)
      {
         // make sure the user sees at least one action, otherwise we won't highlight
         var permissions = this.linksData.permissions;
         if (! (permissions["edit"] || permissions["delete"]))
         {
            return;
         }

         var elem = args[1].target;
         YAHOO.util.Dom.addClass(elem, 'over');
      },

      /** Called whenever the mouse exits a list item. */
      onLinkElementMouseExited: function LinksView_onLinkElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         YAHOO.util.Dom.removeClass(elem, 'over');
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Displays the provided busyMessage but only in case
       * the component isn't busy set.
       *
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function LinksView__setBusy(busyMessage)
      {
         if (this.busy)
         {
            return false;
         }
         this.busy = true;
         this.widgets.busyMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: busyMessage,
            spanClass: "wait",
            displayTime: 0
         });
         return true;
      },

      /**
       * Removes the busy message and marks the component as non-busy
       */
      _releaseBusy: function LinksView__releaseBusy()
      {
         if (this.busy)
         {
            this.widgets.busyMessage.destroy();
            this.busy = false;
            return true;
         }
         else
         {
            return false;
         }
      }
   });
})();