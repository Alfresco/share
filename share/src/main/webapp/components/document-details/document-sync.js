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
 * DocumentSync
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentSync
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      substitute = YAHOO.lang.substitute;

   /**
    * DocumentSync constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentSync} The new DocumentSync instance
    * @constructor
    */
   Alfresco.DocumentSync = function DocumentSync_constructor(htmlId)
   {
      Alfresco.DocumentSync.superclass.constructor.call(this, "Alfresco.DocumentSync", htmlId);

      // Decoupled event listeners
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);

      return this;
   };

   /**
    * Extend Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentSync, Alfresco.component.Base);

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentSync, Alfresco.doclib.Actions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentSync.prototype,
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
          * The nodeRefs to load the form for.
          *
          * @property nodeRef
          * @type string
          * @required
          */
         nodeRef: null,

         /**
          * The current site (if any)
          *
          * @property site
          * @type string
          */
         site: null,

         /**
          * JSON representation of document details
          *
          * @property documentDetails
          * @type object
          */
         documentDetails: null
      },

      /**
       * Helper method for building the html code for the sync buttons
       * 
       * @method _getSyncActionButtons
       * @return {string} The html code for the sync buttons
       */
      _getSyncActionButtons: function DocumentSync__getSyncActionButtons()
      {
         var html = "";
         var actions = this.options.documentDetails.item.actions;
         if (Alfresco.util.findInArray(actions, "document-cloud-sync", "id"))
         {
            html += '<a href="#" class="document-sync-link" title="' + this.msg("label.document.cloud-sync") + '">&nbsp;</a>';
         }
         if (Alfresco.util.findInArray(actions, "document-cloud-unsync", "id"))
         {
            html += '<a href="#" class="document-unsync-link" title="' + this.msg("label.document.cloud-unsync") + '">&nbsp;</a>';
         }
         if (Alfresco.util.findInArray(actions, "document-request-sync", "id"))
         {
            html += '<a href="#" class="document-requestsync-link" title="' + this.msg("label.document.cloud-request-sync") + '">&nbsp;</a>';
         }
         return html;
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DocumentSync_onReady()
      {
         if (Alfresco.util.arrayContains(this.options.documentDetails.item.node.aspects, "sync:syncSetMemberNode"))
         {
            // Load sync info
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/doclib2/node/"  + this.options.nodeRef.replace('://', '/'),
               successCallback:
               {
                  fn: this.onSyncInfoLoaded,
                  scope: this
               },
               failureMessage: this.msg("message.failure")
            });
         }
         else
         {
            Dom.get(this.id + "-formContainer").innerHTML = this.msg("content.not.synced");
         }

         Dom.get(this.id + "-heading").innerHTML = substitute(Dom.get(this.id + "-heading").innerHTML,
         {
            syncActionButtons: this._getSyncActionButtons() 
         });

         var twisterACtionDiv = Dom.get(this.id + "-document-sync-twister-actions");
         Dom.removeClass(twisterACtionDiv, "hidden");

         var root = this.id + "-heading";
         var record = this._buildRecord();

         Event.on(Dom.getElementsByClassName("document-sync-link", "a", root), "click", function syncToCloudClick(event)
         {
            Event.preventDefault(event);
            this.onActionCloudSync(record);
         }, {}, this);

         Event.on(Dom.getElementsByClassName("document-unsync-link", "a", root), "click", function unsyncFromCloudClick(event)
         {
            Event.preventDefault(event);
            this.onActionCloudUnsync(record);
         }, {}, this);

         Event.on(Dom.getElementsByClassName("document-requestsync-link", "a", root), "click", function requestSyncClick(event)
         {
            Event.preventDefault(event);
            this.onActionCloudSyncRequest(record);
         }, {}, this);
      },

      /**
       * Helper method for building the "record"
       * 
       * @method _buildRecord
       * @return {object} The "record" object
       */
      _buildRecord: function DocumentSync__buildRecord()
      {
         var record = this.options.documentDetails.item;
         record.jsNode = new Alfresco.util.Node(record.node);
         return record;
      },

      /**
       * Called when the sync info has been loaded.
       * Will insert the sync info in the Dom.
       *
       * @method onSyncInfoLoaded
       * @param response {Object}
       */
      onSyncInfoLoaded: function DocumentSync_onSyncInfoLoaded(response)
      {
         var me = this;
         var configOptions =
         {
            showTitle: false,
            showRequestSyncButton: false,
            showUnsyncButton: false,
            showMoreInfoLink: false
         };

         Alfresco.util.getSyncStatus(this, this._buildRecord(), response.json, configOptions, function(callbackResult)
         {
            if (callbackResult != null)
            {
               var formId = me.id + "-formContainer";
               var formEl = Dom.get(formId);
               formEl.innerHTML = callbackResult.html;

               Alfresco.util.syncClickOnShowDetailsLinkEvent(me, formId);
               Alfresco.util.syncClickOnHideLinkEvent(me, formId);
               Alfresco.util.syncClickOnTransientErrorShowDetailsLinkEvent(me, formId);
               Alfresco.util.syncClickOnTransientErrorHideLinkEvent(me, formId);
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: me.msg("message.failure")
               });
            }
         });
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       * @param layer {object} Event fired
       * @param args {array} Event parameters
       * @param scope {object} The DocumentSync object
       */
      doRefresh: function DocumentSync_doRefresh(layer, args, scope)
      {
         var twisterACtionDiv = Dom.get(this.id + "-document-sync-twister-actions");
         Dom.addClass(twisterACtionDiv, "hidden");

         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         if (scope.options.documentDetails.item.jsNode.isContainer)
         {
            this.refresh('components/folder-details/folder-sync?nodeRef={nodeRef}' + (this.options.site ? '&site={site}' : ''));
         }
         else
         {
            this.refresh('components/document-details/document-sync?nodeRef={nodeRef}' + (this.options.site ? '&site={site}' : ''));
         }
      }
   }, true);
})();