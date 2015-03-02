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
 * Document Library and Document Details Actions Utility Methods
 *
 * @namespace Alfresco.util
 */

(function()
{
   /**
    * YUI Library aliases
    */
   var Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $combine = Alfresco.util.combinePaths,
      $relativeTime = Alfresco.util.relativeTime;

   YAHOO.lang.augmentObject(Alfresco.util,
   {
      /**
       *
       * getSyncStatus: Retrieves the sync status data and template from the server,
       *    parses and completes it and then sends the result to the specified callback method
       *
       * @method Alfresco.util.getSyncStatus
       * @name getSyncStatus
       * @param scope
       * @param record
       * @param nodeDetails
       * @param configOptions
       * @param callback
       */
      getSyncStatus: function actionsUtil_getSyncStatus(scope, record, nodeDetails, configOptions, callback)
      {
         // Fetch content for Sync Status:
         Alfresco.util.Ajax.request(
         {
            url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "cloud/cloud-sync-status") + "?htmlid=" + scope.id +  "&nodeRef=" + record.nodeRef,
            dataObj:
            {
               nodeDetails: nodeDetails
            },
            successCallback:
            {
               fn: function onCloudSyncIndicatorAction_success(response)
               {
                  // This code wants to:
                  // Show "Pending" if no syncTime or if syncRequested
                  // Show "Sync Attempted {errorTime} by {user|"You"}" and "Last successful synced {syncTime by {user|"You"}" if sync:failed.
                  // Show "Synced {syncTime} by {user|"You"}"

                  // Fetch Status info from node
                  var node = new Alfresco.util.Node(response.config.dataObj.nodeDetails.item.node),
                     properties = node.properties,
                     syncTime = properties.sync_syncTime,
                     syncOwner = properties.sync_syncOwner,
                     syncRequested = (properties.sync_syncRequested === "true"),
                     title = configOptions.showTitle ? '<h2>' + scope.msg("sync.status.title") + '</h2>' : "",
                     statusMsg = scope.msg("sync.status.pending", null, null, originalDocument),
                     syncOwnerDisplay = scope.msg("label.you"),
                     hasFailed = node.hasAspect("sync:failed"),
                     hasTransientError = node.hasAspect("sync:transientError"),
                     isWorkingCopy = node.hasAspect("cm:workingcopy"),
                     msgPrefix = "sync.status.",
                     originalDocument = "";

                  if (isWorkingCopy)
                  {
                     msgPrefix += "copy.";
                     originalDocument = "<a href='document-details?nodeRef=" + record.workingCopy.sourceNodeRef + "'>" + scope.msg("sync.original-document") + "</a>";
                  }

                  if (syncOwner !== Alfresco.constants.USERNAME)
                  {
                     // Extract syncOwnerFull name from data attr in response
                     var syncOwnerMatch = response.serverResponse.responseText.match(/data-sync-owner-fullname="(.*?)"/),
                     // checks the match worked before we use it & we want the result from the first capture group
                        syncOwnerFullName = (syncOwnerMatch && syncOwnerMatch[1])? syncOwnerMatch[1] : syncOwner;

                     syncOwnerDisplay = Alfresco.util.userProfileLink(syncOwner, syncOwnerFullName);
                  }

                  // Build Status String:
                  // If it failed, show that it did:
                  if (hasFailed && !syncRequested)
                  {
                     var errorTimeDisplay = $relativeTime(properties.sync_errorTime.iso8601);
                     statusMsg = scope.msg(msgPrefix + "failed", errorTimeDisplay, syncOwnerDisplay, originalDocument);
                  }

                  // If it has a sync time it has succeeded at least once in the past.
                  if (syncTime && !syncRequested)
                  {
                     var syncTimeDisplay = $relativeTime(syncTime.iso8601);

                     // Assume most recent attempt was successful if no error flag
                     if (hasFailed)
                     {
                        statusMsg +="<br />" + scope.msg(msgPrefix + "last-attempt", syncTimeDisplay, syncOwnerDisplay, originalDocument);
                     } else
                     {
                        statusMsg = scope.msg(msgPrefix + "synced", syncTimeDisplay, syncOwnerDisplay, originalDocument);
                     }
                  }

                  // Sort out code for the error banner (if required)
                  var syncFailed = "hidden",
                     transientError = "hidden",
                     errorCode = "",
                     errorDetails = "",
                     transientErrorCode = "",
                     transientErrorDetails = "";

                  if (hasFailed)
                  {
                     syncFailed = "visible";
                     errorCode = scope.msg(properties["sync:errorCode"]);
                     errorDetails = properties["sync:errorDetails"];
                  }

                  if (hasTransientError)
                  {
                     transientError = "visible";
                     transientErrorCode = scope.msg(properties["sync:transientErrorCode"]);
                     transientErrorDetails = properties["sync:transientErrorDetails"] || scope.msg("sync.status.transient-error.default-details");
                  }

                  var actions = record.actions;

                  // Should show request sync button
                  var showRequestSyncButton = false,
                  requestSyncButtonClass = "hidden";
                  if (configOptions.showRequestSyncButton && Alfresco.util.findInArray(actions, "document-request-sync", "id"))
                  {
                     showRequestSyncButton = true;
                     requestSyncButtonClass = "visible";
                  }

                  // Should show unsync button
                  var showUnsyncButton = false,
                     unsyncButtonClass = "hidden";
                  if (configOptions.showUnsyncButton && Alfresco.util.findInArray(actions, "document-cloud-unsync", "id"))
                  {
                     showUnsyncButton = true;
                     unsyncButtonClass = "visible";
                  }

                  // Don't show the more info link
                  var showMoreInfoLink = "";

                  var html = YAHOO.lang.substitute(response.serverResponse.responseText,
                  {
                     title: title,
                     status: statusMsg,
                     syncFailed: syncFailed,
                     errorCode: errorCode,
                     errorDetails: errorDetails,
                     transientError: transientError,
                     transientErrorCode: transientErrorCode,
                     transientErrorDetails: transientErrorDetails,
                     requestSyncButtonClass: requestSyncButtonClass,
                     unsyncButtonClass: unsyncButtonClass,
                     showMoreInfoLink: showMoreInfoLink
                  });

                  callback({
                     html: html,
                     showRequestSyncButton: showRequestSyncButton,
                     showUnsyncButton: showUnsyncButton
                  });

               },
               scope: scope
            },
            failureCallback:
            {
               fn: function onCloudSyncIndicatorAction_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: scope.msg("sync.unable.diplay.status")
                  });

                  callback(null);
               },
               scope: scope
            }
         });
      },

      /**
       *
       * syncClickOnShowDetailsLinkEvent: Triggered when the show details link is activated in the Last Sync Failed banner
       *
       * @method Alfresco.util.syncClickOnShowDetailsLinkEvent
       * @name syncClickOnShowDetailsLinkEvent
       * @param scope
       * @param root
       */
      syncClickOnShowDetailsLinkEvent: function actionsUtil_syncClickOnShowDetailsLinkEvent(scope, root)
      {
         Event.on(Dom.getElementsByClassName("cloud-sync-details-failed-show-link", "a", root), "click", function showDetailsLinkClick(event)
         {
            Event.preventDefault(event);
            if (!Dom.hasClass(Dom.getElementsByClassName("cloud-sync-error-detailed-transient"), "hidden")[0])
            {
               Dom.addClass(Dom.getElementsByClassName("cloud-sync-error-detailed-transient"), "hidden");
               Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-failed-show-link-transient"), "hidden");
               Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link-transient"), "hidden");
            }

            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-error-detailed"), "hidden");
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-info"), "hidden");
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-failed-show-link"), "hidden");
            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link"), "hidden");
         }, {}, scope);
      },

      /**
      *
      * syncClickOnTransientErrorShowDetailsLinkEvent: Triggered when the show details link (for transient error) is activated in the Last Sync Failed banner
      *
      * @method Alfresco.util.syncClickOnTransientErrorShowDetailsLinkEvent
      * @name syncClickOnTransientErrorShowDetailsLinkEvent
      * @param scope
      * @param root
      */
      syncClickOnTransientErrorShowDetailsLinkEvent: function actionsUtil_syncClickOnTransientErrorShowDetailsLinkEvent(scope, root)
      {
         Event.on(Dom.getElementsByClassName("cloud-sync-details-failed-show-link-transient", "a", root), "click", function showDetailsLinkClick(event)
         {
            Event.preventDefault(event);
            if (!Dom.hasClass(Dom.getElementsByClassName("cloud-sync-error-detailed"), "hidden")[0])
            {
               Dom.addClass(Dom.getElementsByClassName("cloud-sync-error-detailed"), "hidden");
               Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-failed-show-link"), "hidden");
               Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link"), "hidden");
            }

            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-error-detailed-transient"), "hidden");
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-info"), "hidden");
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-failed-show-link-transient"), "hidden");
            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link-transient"), "hidden");
         }, {}, scope);
      },

      /**
       *
       * syncClickOnHideLinkEvent: Triggered when the hide details link is activated in the Last Sync Failed banner
       *
       * @method Alfresco.util.syncClickOnHideLinkEvent
       * @name syncClickOnHideLinkEvent
       * @param scope
       * @param root
       */
      syncClickOnHideLinkEvent: function actionUtil_syncClickOnHideLinkEvent(scope, root)
      {
         Event.on(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link", "a", root), "click", function hideLinkClick(event)
         {
            Event.preventDefault(event);
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-error-detailed"), "hidden");
            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-info"), "hidden");
            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-failed-show-link"), "hidden");
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link"), "hidden");
         }, {}, scope);
      },

      /**
      *
      * syncClickOnTransientErrorHideLinkEvent: Triggered when the hide details link (for transient error) is activated in the Last Sync Failed banner
      *
      * @method Alfresco.util.syncClickOnTransientErrorHideLinkEvent
      * @name syncClickOnTransientErrorHideLinkEvent
      * @param scope
      * @param root
      */
      syncClickOnTransientErrorHideLinkEvent: function actionUtil_syncClickOnTransientErrorHideLinkEvent(scope, root)
      {
         Event.on(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link-transient", "a", root), "click", function hideLinkClick(event)
         {
            Event.preventDefault(event);
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-error-detailed-transient"), "hidden");
            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-info"), "hidden");
            Dom.removeClass(Dom.getElementsByClassName("cloud-sync-details-failed-show-link-transient"), "hidden");
            Dom.addClass(Dom.getElementsByClassName("cloud-sync-details-failed-hide-link-transient"), "hidden");
         }, {}, scope);
      }
   }, true);
})();