/*
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
 * Archive and download component.
 *
 * Pops up a YUI panel and initiates the download archiving of one or more NodeRefs and monitors
 * the progress of that archiving. Once the archiving is complete it initiates the download.
 *
 * @namespace Alfresco.module
 * @class Alfresco.ArchiveAndDownload
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Element = YAHOO.util.Element,
       KeyListener = YAHOO.util.KeyListener,
       Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Returns the first registered instance of the Alfresco.ArchiveAndDownload widget.
    */
   Alfresco.getArchiveAndDownloadInstance = function()
   {
     var instanceId = "Alfresco.ArchiveAndDownload";
     var instance = Alfresco.util.ComponentManager.findFirst(instanceId);
     return instance;
   };
   
   /**
    * ArchiveAndDownload constructor.
    *
    * ArchiveAndDownload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.component.getArchiveAndDownloadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.ArchiveAndDownload} The new ArchiveAndDownload instance
    * @constructor
    * @private
    */
   Alfresco.ArchiveAndDownload = function(htmlId)
   {
      Alfresco.ArchiveAndDownload.superclass.constructor.call(this, "Alfresco.ArchiveAndDownload", htmlId, ["button", "container"]);
      return this;
   };

   YAHOO.extend(Alfresco.ArchiveAndDownload, Alfresco.component.Base,
   {
      /**
       * Contains the upload gui
       *
       * @property panel
       * @type YAHOO.widget.Dialog
       */
      panel: null,

      /**
       * The node URL (a nodeRef in the form {store_type}/{store_id}/{nodepath}) of the archive that is currently being 
       * generated. This will be set when on the successful response from the "archiveInitReqSuccess" function and
       * reset by the "_resetGUI" function.
       */
      _currentArchiveNodeURL : "",
      
      /**
       * The name to give to the archive currently being built. This will be set as the Folder filename when a single folder
       * is selected for download otherwise will just be "Archive.zip"
       */
      _currentArchiveName: "",
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function ArchiveAndDownload_onReady()
      {
         Dom.removeClass(this.id + "-dialog", "hidden");

         // Create the panel
         this.panel = Alfresco.util.createYUIPanel(this.id + "-dialog");

         // Hook close button
         this.panel.hideEvent.subscribe(this.onCancelButtonClick, null, this);

         // Create and save a reference to the buttons so we can alter them later
         this.widgets.cancelOkButton = Alfresco.util.createYUIButton(this, "cancelOk-button", this.onCancelButtonClick);

         // Register the ESC key to close the dialog
         this.widgets.escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: this.onCancelButtonClick,
            scope: this,
            correctScope: true
         });
      },

      /**
       * Updates the progress bar to reflect the current progress.
       * 
       * @method updateProgress
       * @param done The current number of bytes archived
       * @param total The total number of bytes required to be archived
       */
      updateProgress: function ArchiveAndDownload_updateProgress(json)
      {
         // Remove any commas from the number to prevent NaN errors
         var done = json.done.replace(/,/g, "");
         var total = json.total.replace(/,/g, "");
         var overallProgress = total != 0 ? (done / total) : 0;
         var overallLeft = (-300 + (overallProgress * 300));
         Dom.setStyle(this.id + "-aggregate-progress-span", "left", overallLeft + "px"); 
         Dom.get(this.id + "-file-count-span").innerHTML = this.msg("file.status", json.filesAdded, json.totalFiles);
      },
      
      /**
       * Called when an archiving progress update request succeeds. Determines the current status of the
       * archiving progress and either calls the "getArchivingProgress" function again if progress is not
       * complete or calls the "handleArchiveComplete" function when progress is returned as "DONE".
       * 
       * @method archiveProgressSuccess
       * @param response The response from XHR request.
       */
      archiveProgressSuccess: function ArchiveAndDownload_archiveProgressSuccess(response)
      {
         // Check the response data...
         if (response.json)
         {
            if (response.json.status == "PENDING")
            {
               // The archiving hasn't started yet...
               var _this = this;
               this._getProgressTimeout = window.setTimeout(function() {
                  _this.getArchivingProgress();
               }, 250);
            }
            else if (response.json.status == "IN_PROGRESS")
            {
               this.updateProgress(response.json);
               var _this = this;
               this._getProgressTimeout = window.setTimeout(function() {
                  _this.getArchivingProgress();
               }, 250);
               
            }
            else if (response.json.status == "DONE")
            {
               // The archiving is complete and the archive can now be downloaded...
               this.updateProgress(response.json);
               this.handleArchiveComplete();
            }
            else if (response.json.status == "MAX_CONTENT_SIZE_EXCEEDED")
            {
               // The file size is too large to be zipped up:
               Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.maxContentSizeExceeded", Alfresco.util.formatFileSize(response.json.done), Alfresco.util.formatFileSize(response.json.total, 2))
                  });
               this.panel.hide();
            }
            else if (response.json.status == "CANCELLED")
            {
               // Do nothing; the user has already cancelled it.
            }
            else
            {
               // Unknown or no status
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: this.msg("message.unknown.progress")
               });
               this.panel.hide();
            }
         }
      },
      
      /**
       * Called when the XHR call made by "getArchivingProgress" fails.
       * 
       * @method archiveProgressFailure
       * @param response The failure response returned by the XHR call.
       */
      archiveProgressFailure: function ArchiveAndDownload_archiveProgressFailure(response)
      {
         var _this = this,
            maxFailures = 6, // number of times a failure is retried.
            failureRetry = 5000, // milliseconds before trying.
            failures = response.config.failureCount || 0;

         if (failures < maxFailures)
         {
            // An error occurred getting the progress status....
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("message.archive.progress.failed")
            });

            // Wait a few seconds and then try again...
            this._getProgressTimeout = window.setTimeout(function()
            {
               _this.getArchivingProgress(++failures);
            }, failureRetry);
         } else
         {
            this.panel.hide();
            // Download Failed.
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("message.download.failed")
            });
         }
      },

      /**
       * Triggers a server side clean up of the download archive.
       *
       *
       * @method deleteDownload
       */
      deleteDownload: function ArchiveAndDownload_deleteDownload()
      {
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI + "api/internal/downloads/" + this._currentArchiveNodeURL
         });
      },

      /**
       * Called recursively after the initial archive request returns successfully until the response
       * indicates that the archiving process has completed.
       * 
       * @method getArchivingProgress
       */
      getArchivingProgress: function ArchiveAndDownload_getArchivingProgress(prevFailures)
      {
         if (this._currentArchiveNodeURL != null && this._currentArchiveNodeURL != "")
         {
            Alfresco.util.Ajax.jsonGet({
               url: Alfresco.constants.PROXY_URI + "api/internal/downloads/" + this._currentArchiveNodeURL + "/status",
               responseContentType : "application/json",
               successCallback:
               {
                  fn: this.archiveProgressSuccess,
                  scope: this
               },
               failureCallback:
               {
                  fn: this.archiveProgressFailure,
                  scope: this
               },
               failureCount: prevFailures
           });
         }
      },
      
      /**
       * Called when the initial request to generate an archive returns successfully. The response
       * will contain the new NodeRef of the archive being generated which should then be used
       * to request progress updates. The function is not called when the archive has been created,
       * but simply when the archiving process has begun.
       *  
       * @method archiveInitReqSuccess
       * @param response The success response from the XHR request.
       */
      archiveInitReqSuccess: function ArchiveAndDownload_archiveInitReqSuccess(response)
      {
         // Check the response object...
         if (response.json && response.json.nodeRef)
         {
            var nodeRef = Alfresco.util.NodeRef(response.json.nodeRef);
            this._currentArchiveNodeURL = nodeRef.storeType + "/" + nodeRef.storeId + "/" + nodeRef.id;
            this.getArchivingProgress();
         }
      },
      
      /**
       * Called when the initial request to generate an archive fails. Failure may occur for a number
       * of reasons including (but not limited to) the repository application not being available, 
       * invalid data in the request payload).
       * 
       * @method archiveInitReqFailure
       * @param response The failure response from the XHR request.
       */
      archiveInitReqFailure: function ArchiveAndDownload_archiveInitReqFailure(response)
      {
         // An error occurred getting the progress status....
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this.msg("message.archive.request.failed")
         });
         this.panel.hide();
      },
      
      /**
       * Makes and XHR request to initiate the archiving of one or more Nodes into a single downloadable
       * Node.
       * 
       * @method requestArchive
       * @param nodes The list of nodes to archived. This should be in the form:
       * [ { nodeRef: <nodeRef}, ... ]
       */
      requestArchive: function ArchiveAndDownload_requestArchive(nodes)
      {
         // Post the details of the nodeRefs to archive...
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/internal/downloads",
            responseContentType : "application/json",
            dataObj: nodes,
            successCallback:
            {
               fn: this.archiveInitReqSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.archiveInitReqFailure,
               scope: this
            }
         });
      },
      
      /**
       * Called when the archiving progress update requests indicate that the archive is ready to
       * be downloaded, it then sets the window location to the service URL for downloading the
       * archive node. 
       * 
       * @method handleArchiveComplete
       */
      handleArchiveComplete: function ArchiveAndDownload_requestArchive()
      {
         // Hide the panel and initiate the download...
         this.widgets.cancelOkButton.set("disabled", false);
         this.panel.hide();

         // Create an empty form and post it to a hidden ifram using GET to avoid confusing the browser to believe we
         // are leaving the current page (which would abort the currently running requests, i.e. deletion of the archive

         var form = document.createElement("form");
         form.method = "GET";
         form.action = Alfresco.constants.PROXY_URI + "api/node/content/" + this._currentArchiveNodeURL + "/" + Alfresco.util.encodeURIPath(this._currentArchiveName);
         document.body.appendChild(form);

         var d = form.ownerDocument;
         var iframe = d.createElement("iframe");
         iframe.style.display = "none";
         YAHOO.util.Dom.generateId(iframe, "downloadArchive");
         iframe.name = iframe.id;
         document.body.appendChild(iframe);

         // makes it possible to target the frame properly in IE.
         window.frames[iframe.name].name = iframe.name;

         form.target = iframe.name;
         form.submit();
      },
      
      /**
       * Makes the archive and download panel visible and launches the request to archive the nodes specified
       * in the supplied "config" argument.
       * 
       * @method show
       * @param config The configuration for the archive download. This object should should be in the following structure:
       * 
       * { nodesToArchive : [ { nodeRef: <nodeRef> }, ... ] } 
       */
      show: function ArchiveAndDownload_show(config)
      {
         if (config.nodesToArchive)
         {
            // Reset the dialog...
            this._resetGUI();

            // Enable the Esc key listener
            this.widgets.escapeListener.enable();
            this.panel.setFirstLastFocusable();
            this.panel.show();
            
            if (config.archiveName && config.archiveName != "")
            {
               this._currentArchiveName = config.archiveName + ".zip";
            }
            else
            {
               this._currentArchiveName = "Archive.zip";
            }
            
            // Kick off the request...
            this.requestArchive(config.nodesToArchive);
         }
         else
         {
            // Handle invalid data supplied to show function...
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("message.invalid.arguments")
            });
         }
      },

      /**
       * Reset GUI to start state
       *
       * @method _resetGUI
       * @private
       */
      _resetGUI: function ArchiveAndDownload__resetGUI()
      {
         // Reset references and the gui before showing it
         this.widgets.cancelOkButton.set("disabled", false);
         this._currentArchiveNodeURL = "";
         Dom.setStyle(this.id + "-aggregate-progress-span", "left", "-300px");
         Dom.get(this.id + "-file-count-span").innerHTML = "";
      },

      /**
       * Fired when the user clicks the cancel button.
       *
       * @method onCancelButtonClick
       */
      onCancelButtonClick: function ArchiveAndDownload_onCancelButtonClick()
      {
         // Remove the file from the server:
         this.deleteDownload();

         // Clear any pending timeout:
         window.clearTimeout(this._getProgressTimeout);

         // Hide the panel
         this.panel.hide();

         // Disable the Esc key listener
         this.widgets.escapeListener.disable();
      }
   });
})();
