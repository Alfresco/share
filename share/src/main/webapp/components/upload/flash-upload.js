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
 * FlashUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var flashUpload = Alfresco.component.getFlashUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: flashUpload.MODE_MULTI_UPLOAD,
 * }
 * this.flashUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco.module
 * @class Alfresco.FlashUpload
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * FlashUpload constructor.
    *
    * FlashUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.component.getFlashUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.FlashUpload} The new FlashUpload instance
    * @constructor
    * @private
    */
   Alfresco.FlashUpload = function(htmlId)
   {
      Alfresco.FlashUpload.superclass.constructor.call(this, "Alfresco.FlashUpload", htmlId, ["button", "container", "datatable", "datasource", "cookie", "uploader"]);

      this.swf = Alfresco.constants.URL_CONTEXT + "res/yui/uploader/assets/uploader.swf";
      this.hasRequiredFlashPlayer = Alfresco.util.hasRequiredFlashPlayer(9, 0, 45);
      
      this.fileStore = {};
      this.addedFiles = {};
      this.defaultShowConfig =
      {
         siteId: null,
         containerId: null,
         destination: null,
         uploadDirectory: null,
         updateNodeRef: null,
         updateFilename: null,
         updateVersion: "1.0",
         mode: this.MODE_SINGLE_UPLOAD,
         filter: [],
         onFileUploadComplete: null,
         overwrite: false,
         thumbnails: null,
         uploadURL: null,
         username: null,
         suppressRefreshEvent: false
      };
      this.suppliedConfig = {};
      this.showConfig = {};
      this.fileItemTemplates = {};
      
      return this;
   };

   YAHOO.extend(Alfresco.FlashUpload, Alfresco.component.Base,
   {
      /**
       * The flash move will dispatch the contentReady event twice,
       * make sure we only react on it twice.
       *
       * @property contentReady
       * @type boolean
       */
      contentReady: false,
      
      /**
       * The user is browsing and adding files to the file list
       *
       * @property STATE_BROWSING
       * @type int
       */
      STATE_BROWSING: 1,

      /**
       * File(s) is being uploaded to the server
       *
       * @property STATE_UPLOADING
       * @type int
       */
      STATE_UPLOADING: 2,

      /**
       * All files are processed and have either failed or been successfully
       * uploaded to the server.
       *
       * @property STATE_FINISHED
       * @type int
       */
      STATE_FINISHED: 3,
      
      /**
       * File failed to upload.
       *
       * @property STATE_FAILURE
       * @type int
       */
      STATE_FAILURE: 4,

      /**
       * File was successfully STATE_SUCCESS.
       *
       * @property STATE_SUCCESS
       * @type int
       */
      STATE_SUCCESS: 5,

      /**
       * The filename contains characters that are illegal (for Windows at least)
       * 
       * @property STATE_ILLEGAL_FILENAME
       * @type int
       */
      STATE_ILLEGAL_FILENAME: 6,
      
       /**
       * The state of which the uploader currently is, where the flow is.
       * STATE_BROWSING > STATE_UPLOADING > STATE_FINISHED
       *
       * @property state
       * @type int
       */
      state: 1,

      /**
       * Stores references and state for each file that is in the file list.
       * The fileId parameter from the YAHOO.widget.Uploader is used as the key
       * and the value is an object that stores the state and references.
       *
       * @property fileStore
       * @type object Used as a hash table with fileId as key and an object
       *       literal as the value.
       *       The object literal is of the form:
       *       {
       *          contentType: {HTMLElement},        // select, hidden input or null (holds the chosen contentType for the file).
       *          fileButton: {YAHOO.widget.Button}, // Will be disabled on success or STATE_FAILURE
       *          state: {int},                      // Keeps track if the individual file has been successfully uploaded or failed
       *                                             // (state flow: STATE_BROWSING > STATE_UPLOADING > STATE_SUCCESS or STATE_FAILURE)
       *          progress: {HTMLElement},           // span that is the "progress bar" which is moved during progress
       *          progressInfo: {HTMLElement},       // span that displays the filename and the state
       *          progressPercentage: {HTMLElement}, // span that displays the upload percentage for the individual file
       *          fileName: {string},                // filename
       *          nodeRef: {string}                  // nodeRef if the file has been uploaded successfully
       *       }
       */
      fileStore: null,

      /**
       * The number of successful uploads since upload was clicked.
       *
       * @property noOfSuccessfulUploads
       * @type int
       */
      noOfSuccessfulUploads: 0,

      /**
       * The number of failed uploads since upload was clicked.
       *
       * @property noOfFailedUploads
       * @type int
       */
      noOfFailedUploads: 0,

      /**
       * Remembers what files that how been added to the file list since
       * the show method was called.
       *
       * @property addedFiles
       * @type object
       */
      addedFiles: null,

      /**
       * Shows uploader in single upload mode.
       *
       * @property MODE_SINGLE_UPLOAD
       * @static
       * @type int
       */
      MODE_SINGLE_UPLOAD: 1,

      /**
       * Shows uploader in single update mode.
       *
       * @property MODE_SINGLE_UPDATE
       * @static
       * @type int
       */
      MODE_SINGLE_UPDATE: 2,

      /**
       * Shows uploader in multi upload mode.
       *
       * @property MODE_MULTI_UPLOAD
       * @static
       * @type int
       */
      MODE_MULTI_UPLOAD: 3,
      
      /**
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single & multi uploads and single updates.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig: null,

      /**
       * The config passed in to the show method.
       *
       * @property suppliedConfig
       * @type object
       */
      suppliedConfig: null,

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property showConfig
       * @type object
       */
      showConfig: null,

      /**
       * YUI class that controls the .swf to open the browser dialog window
       * and transfers the files.
       *
       * @property uploader
       * @type YAHOO.widget.Uploader
       */
      uploader: null,

      /**
       * A property that is set to true after the loaded swf movie has dispatched its swfReady/contentReady event
       *
       * @property uploader Ready
       * @type boolean
       */
      uploaderReady: false,

      /**
       * HTMLElement of type span that displays the dialog title.
       *
       * @property titleText
       * @type HTMLElement
       */
      titleText: null,

      /**
       * HTMLElement of type span that displays help text for multi uploads.
       *
       * @property multiUploadTip
       * @type HTMLElement
       */
      multiUploadTip: null,

      /**
       * HTMLElement of type span that displays help text for single updates.
       *
       * @property singleUpdateTip
       * @type HTMLElement
       */
      singleUpdateTip: null,

      /**
       * HTMLElement of type span that displays the total upload status
       *
       * @property statusText
       * @type HTMLElement
       */
      statusText: null,

      /**
       * HTMLElement of type radio button for major or minor version 
       *
       * @property description
       * @type HTMLElement
       */
      minorVersion: null,
      
      majorVersion: null,

      /**
       * HTMLElement of type textarea for version comment
       *
       * @property description
       * @type HTMLElement
       */
      description: null,

      /**
       * HTMLElement of type div that displays the version input form.
       *
       * @property versionSection
       * @type HTMLElement
       */
      versionSection: null,

      /**
       * HTMLElements of type div that is used to to display a column in a
       * row in the file table list. It is loaded dynamically from the server
       * and then cloned for each row and column in the file list.
       * The fileItemTemplates has the following form:
       * {
       *    left:   HTMLElement to display the left column
       *    center: HTMLElement to display the center column
       *    right:  HTMLElement to display the right column
       * }
       *
       * @property fileItemTemplates
       * @type HTMLElement
       */
      fileItemTemplates: null,

      /**
       * Backs up the page title to protect against Flash changing it when a file is selected.
       * 
       * @property _iePageTitleBackup
       * @type String
       */
      _iePageTitleBackup: null,
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function FlashUpload_onReady()
      {
         // Backup the page title...
         this._iePageTitleBackup = document.title;
         
         // Tell the YUI class where the swf is
         YAHOO.widget.Uploader.SWFURL = this.swf;

         Dom.removeClass(this.id + "-dialog", "hidden");

         // Create the panel
         this.widgets.panel = Alfresco.util.createYUIPanel(this.id + "-dialog");

         // Hook close button
         this.widgets.panel.hideEvent.subscribe(this.onCancelOkButtonClick, null, this);

         /**
          * Mac Gecko bugfix for javascript losing connection to the flash uploader movie.
          * To avoid a mac gecko bug where scrollbars are displayed even if the "scrolled"
          * content isn't visible, YUI toggled the "overflow" style property between "hidden"
          * and "visible", this unfortnately also caused the flash movie (which is a child
          * of the toggled element) to get re-instantiated.
          *
          * (https://bugzilla.mozilla.org/show_bug.cgi?id=187435)
          */
         if (this.widgets.panel.platform == "mac" && YAHOO.env.ua.gecko)
         {
            /**
             * Remove the already added event listeners that toggles
             * the "overflow" style property for the dialog wrapper.
             */
            var Config = YAHOO.util.Config,
                  p = this.widgets.panel;

            if (Config.alreadySubscribed(p.showEvent, p.showMacGeckoScrollbars, p))
            {
               p.showEvent.unsubscribe(p.showMacGeckoScrollbars, p);
            }
            if (Config.alreadySubscribed(p.hideEvent, p.hideMacGeckoScrollbars, p))
            {
            	 p.hideEvent.unsubscribe(p.hideMacGeckoScrollbars, p);
            }

            // Add a class for special bug fix css classes
            Dom.addClass(p.element, "reinstantiated-fix");
         }

         // Save a reference to the file row template that is hidden inside the markup
         this.fileItemTemplates.left = Dom.get(this.id + "-left-div");
         this.fileItemTemplates.center = Dom.get(this.id + "-center-div");
         this.fileItemTemplates.right = Dom.get(this.id + "-right-div");

         // Create the YIU datatable object
         this._createEmptyDataTable();

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.titleText = Dom.get(this.id + "-title-span");
         this.multiUploadTip = Dom.get(this.id + "-multiUploadTip-span");
         this.singleUpdateTip = Dom.get(this.id + "-singleUpdateTip-span");
         this.statusText = Dom.get(this.id + "-status-span");
         this.description = Dom.get(this.id + "-description-textarea");

         // Save reference to version radio so we can reset and get its value later
         this.minorVersion = Dom.get(this.id + "-minorVersion-radioButton");

         this.majorVersion = Dom.get(this.id + "-majorVersion-radioButton");

         // Save a reference to the HTMLElement displaying version input so we can hide or show it
         this.versionSection = Dom.get(this.id + "-versionSection-div");

         // Create and save a reference to the uploadButton so we can alter it later
         this.widgets.uploadButton = Alfresco.util.createYUIButton(this, "upload-button", this.onUploadButtonClick);

         // Create and save a reference to the cancelOkButton so we can alter it later
         this.widgets.cancelOkButton = Alfresco.util.createYUIButton(this, "cancelOk-button", this.onCancelOkButtonClick);

         // Create and save a reference to the uploader so we can call it later
         this.uploader = new YAHOO.widget.Uploader(this.id + "-flashuploader-div", Alfresco.constants.URL_RESCONTEXT + "themes/" + Alfresco.constants.THEME + "/images/upload-button-sprite.png", true);
         this.uploader.subscribe("fileSelect", this.onFileSelect, this, true);
         this.uploader.subscribe("uploadComplete",this.onUploadComplete, this, true);
         this.uploader.subscribe("uploadProgress",this.onUploadProgress, this, true);
         this.uploader.subscribe("uploadStart",this.onUploadStart, this, true);
         this.uploader.subscribe("uploadCancel",this.onUploadCancel, this, true);
         this.uploader.subscribe("uploadCompleteData",this.onUploadCompleteData, this, true);
         this.uploader.subscribe("uploadError",this.onUploadError, this, true);
         this.uploader.subscribe("contentReady", this.onContentReady, this, true);

         // Register the ESC key to close the dialog
         this.widgets.escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: this.onCancelOkButtonClick,
            scope: this,
            correctScope: true
         });
         
         if (this.uploader._swf && this.uploader._swf.tagName.toLowerCase() == "embed")
         {
            this.widgets.uploaderListener = new KeyListener(this.uploader._swf,
            {
               keys: KeyListener.KEY.TAB
            },
            {
               fn: function change_swf_focus_on_first_element(obj)
               {
                  this.widgets.panel.setFirstLastFocusable();
                  this.widgets.panel.firstElement.focus();
               },
               scope: this,
               correctScope: true
            });
         }

         // MNT-12892 fix, dinamically calculate width of upload label
         // from the flash-upload.css file browse div has following styles: margin-left: 10px; width: 38px; margin-right: 10px;
         var label = Dom.getElementsByClassName("label", "div")[0];
         label.parentNode.style.width = (10 + 38 + 10 + 1) + label.offsetWidth + "px";

         YAHOO.lang.later(this, 2000, function()
         {
            Dom.addClass(this.id + "-flashuploader-div", "hidden");
         });
      },

      /**
       * Called when the "wrapping" SWFPlayer-flash movie is loaded
       *
       * @method onContentReady
       */
      onContentReady: function FlashUpload_onContentReady(event)
      {
         this.uploader.enable();
         this.uploader.setAllowMultipleFiles(this.showConfig.mode === this.MODE_MULTI_UPLOAD);
         if (this.showConfig.filter)
         {
            this.uploader.setFileFilters(this.showConfig.filter);
         }
      },

      /**
       * Show can be called multiple times and will display the uploader dialog
       * in different ways depending on the config parameter.      
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config object is in the form of:
       * {
       *    siteId: {string},        // site to upload file(s) to
       *    containerId: {string},   // container to upload file(s) to (i.e. a doclib id)
       *    destination: {string},   // destination nodeRef to upload to if not using site & container
       *    uploadPath: {string},    // directory path inside the component to where the uploaded file(s) should be save
       *    updateNodeRef: {string}, // nodeRef to the document that should be updated
       *    updateFilename: {string},// The name of the file that should be updated, used to display the tip
       *    mode: {int},             // MODE_SINGLE_UPLOAD, MODE_MULTI_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},         // limits what kind of files the user can select in the OS file selector
       *    onFileUploadComplete: null, // Callback after upload
       *    overwrite: false         // If true and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to overwrite a versionable file with the existing name
       *                             // If false and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to append a number to the versionable filename to avoid
       *                             // an overwrite and a new version
       * }
       */
      show: function FlashUpload_show(config)
      {
         if (!this.hasRequiredFlashPlayer)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("label.noFlash")
            });
         }
         
         // Merge the supplied config with default config and check mandatory properties
         this.suppliedConfig = config;
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.uploadDirectory === undefined && this.showConfig.updateNodeRef === undefined)
         {
             throw new Error("An updateNodeRef OR uploadDirectory must be provided");
         }
         if (this.showConfig.uploadDirectory !== null && this.showConfig.uploadDirectory.length === 0)
         {
            this.showConfig.uploadDirectory = "/";
         } 

         var swfWrapper = this.id + "-flashuploader-div";
         Dom.removeClass(swfWrapper, "hidden");

         // Apply the config before it is shown
         this._resetGUI();

         // Apply the config before it is shown
         this._applyConfig();

         // Enable the Esc key listener
         this.widgets.escapeListener.enable();

         // Remove the a elements to make tabbing work as expeceted
         var swfObjectCreatedElements = Dom.getChildren(this.id + "-flashuploader-div"),
            el,
            tagName;
         for (var i = 0, il = swfObjectCreatedElements.length; i < il; i++)
         {
            el = swfObjectCreatedElements[i];
            tagName = el.tagName.toLowerCase();
            if (tagName == "a")
            {
               el.parentNode.removeChild(el);
            }
         }
         this.widgets.panel.setFirstLastFocusable();

         if (this.uploader._swf && this.uploader._swf.tagName.toLowerCase() == "embed")
         {
            this.widgets.panel.firstElement = this.uploader._swf;
            this.widgets.lastElListener = new KeyListener(this.widgets.panel.lastElement,
         {
            keys: KeyListener.KEY.TAB
         },
         {
            fn: function change_last_element_focus_on_swf(obj)
            {
               this.widgets.panel.firstElement = this.uploader._swf;
            },
               scope: this,
               correctScope: true
            });
            this.widgets.lastElListener.enable();
            this.widgets.uploaderListener.enable();
         }

         // Show the upload panel
         this.widgets.panel.show();

         // Need to resize FF in Ubuntu so the button appears
         if (navigator.userAgent && navigator.userAgent.indexOf("Ubuntu") != -1 &&
            YAHOO.env.ua.gecko > 1 && !Dom.hasClass(swfWrapper, "button-fix"))
         {
            Dom.addClass(swfWrapper, "button-fix");
         }
         
         // Reset the page title if IE because the file select will have changed it...
         if (YAHOO.env.ua.ie)
         {
            document.title = this._iePageTitleBackup;
         }

      },

      /**
       * Hides the panel
       * 
       * @method hide
       */
      hide: function FlashUpload_hide()
      {
         this.onCancelOkButtonClick();
      },
      
      /**
       * Reset GUI to start state
       *
       * @method _resetGUI
       * @private
       */
      _resetGUI: function FlashUpload__resetGUI()
      {
         // Reset references and the gui before showing it
         if (this.statusText == null)
         {
            this.onReady();
         }
         
         this.state = this.STATE_BROWSING;
         this.noOfFailedUploads = 0;
         this.noOfSuccessfulUploads = 0;
         this.statusText.innerHTML = "&nbsp;";
         this.description.value = "";
         this.minorVersion.checked = true;
         this.minorVersion.disabled = false;
         this.majorVersion.disabled = false;
         this.description.disabled = false;
         this.widgets.uploadButton.set("label", this.msg("button.upload"));
         this.widgets.uploadButton.set("disabled", true);
         Dom.removeClass(this.id + "-upload-button", "hidden");
         this.widgets.cancelOkButton.set("label", this.msg("button.cancel"));
         this.widgets.cancelOkButton.set("disabled", false);
      },

      /**
       * Fired by YUI:s DataTable when the added row has been rendered to the data table list.
       *
       * @method onPostRenderEvent       
       */
      onPostRenderEvent: function FlashUpload_onPostRenderEvent()
      {
         // Check whether or not any of the files added are invalid...
         for (var id in this.fileStore)
         {
            var fileInfo = this.fileStore[id];
            if (fileInfo != null && fileInfo.state == this.STATE_ILLEGAL_FILENAME)
            {
               // Add the failure label to the filename & and as a title attribute
               var msg = this.msg("label.illegalChars");
               fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] + " " + msg;
               fileInfo.progressInfo.setAttribute("title", msg);
               fileInfo.progressInfoCell.setAttribute("title", msg);

               // Change the style of the progress bar
               Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
               Dom.addClass(fileInfo.progress, "fileupload-progressFailure-span");

               // Set the progress bar to "full" progress
               Dom.setStyle(fileInfo.progress, "left", 0 + "px");
               
               // Switch the state to a general failure message to ensure that we don't
               // keep processing this same error.
               fileInfo.state = this.STATE_FAILURE;
               
               // Log the failure and update the status message...
               this.noOfFailedUploads++;
               this._updateStatus();
            }
         }
         
         // If there are currently no failures then clear any status message that may have been
         // previously set by adding a file with a name containing illegal characters...
         if (this.noOfFailedUploads == 0)
         {
            this._clearStatus();
         }
         
         // Display the upload button since all files are rendered
         var numRecords = this.widgets.dataTable.getRecordSet().getLength();
         if (numRecords > 0 && numRecords > this.noOfFailedUploads)
         {
            this.widgets.uploadButton.set("disabled", false);
            this.widgets.panel.setFirstLastFocusable();
            this.widgets.panel.focusFirst();
         }
         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE && this.widgets.panel.cfg.getProperty("visible"))
         {
            if (this.widgets.dataTable.getRecordSet().getLength() === 0)
            {
               this.uploader.enable();
            }
            else
            {
               this.uploader.disable();
            }
         }
      },

      /**
       * Fired by YUI:s DataTable when a row has been deleted to the data table list.
       * Keeps track of added files.
       *
       * @method onRowDeleteEvent
       * @param event {object} a DataTable "rowDelete" event
       */
      onRowDeleteEvent: function FlashUpload_onRowDeleteEvent(event)
      {
         // Update tabbing and focus
         this.widgets.panel.setFirstLastFocusable();
         this.widgets.panel.focusFirst();         
      },

      /**
       * Fired by YIUs Uploader when the user has selected one or more files
       * from the OS:s file dialog window.
       * Adds file that hasn't been selected before to the gui and adjusts the gui.
       *
       * @method onFileSelect
       * @param event {object} an Uploader "fileSelect" event
       */
      onFileSelect: function FlashUpload_onFileSelect(event)
      {
         // Reset the page title if IE because the file select will have changed it...
         if (YAHOO.env.ua.ie)
         {
            document.title = this._iePageTitleBackup;
         }
         
         // Disable upload button until all files have been rendered and added
         this.widgets.uploadButton.set("disabled", true);

         // For each time the user select new files, all the previous selected
         // files also are included in the event.fileList. Make sure we only
         // add files to the table that haven's been added before.
         var newFiles = [],
            data,
            uniqueFileToken;
         for (var i in event.fileList)
         {
            data = YAHOO.widget.DataTable._cloneObject(event.fileList[i]);
            uniqueFileToken = this._getUniqueFileToken(data);
            if (!this.addedFiles[uniqueFileToken])
            {
               if (data.size === 0 && !this.addedFiles[uniqueFileToken])
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.zeroByteFileSelected", data.name)
                  });
               }
               else
               {
                  // Add file to file table
                  newFiles.push(data);
               }
               // Since the flash movie allows the user to select one file several
               // times we need to keep track of the selected files by our selves
               this.addedFiles[uniqueFileToken] = uniqueFileToken;
            }
         }
         // Add all files to table
         this.widgets.dataTable.addRows(newFiles, 0);
      },

      /**
       * Fired by YIU:s Uploader when transfer has been start for one of the files.
       * Adjusts the gui.
       *
       * @method onUploadStart
       * @param event {object} an Uploader "uploadStart" event
       */
      onUploadStart: function FlashUpload_onUploadStart(event)
      {
         // Get the reference to the files gui components
         var fileInfo = this.fileStore[event.id];

         // Hide the contentType drop down if it wasn't hidden already
         if (fileInfo.contentType)
         {
            Dom.addClass(fileInfo.contentType, "hidden");
         }

         // Show the progress percentage if it wasn't visible already
         fileInfo.progressPercentage.innerHTML = "0%";
         Dom.removeClass(fileInfo.progressPercentage, "hidden");

         // Make sure we know we are in upload state
         fileInfo.state = this.STATE_UPLOADING;
      },

      /**
       * Fired by YIU:s Uploader during the transfer for one of the files.
       * Adjusts the gui and its progress bars.
       *
       * @method onUploadComplete
       * @param event {object} an Uploader "uploadProgress" event
       */
      onUploadProgress: function FlashUpload_onUploadProgress(event)
      {
         var flashId = event.id;
         var fileInfo = this.fileStore[flashId];

         // Set percentage
         var percentage = event.bytesLoaded / event.bytesTotal;
         fileInfo.progressPercentage.innerHTML = Math.round(percentage * 100) + "%";

         // Set progress position
         var left = (-400 + (percentage * 400));
         Dom.setStyle(fileInfo.progress, "left", left + "px");
      },

      /**
       * Fired by YIU:s Uploader when transfer is complete for one of the files.
       *
       * @method onUploadComplete
       * @param event {object} an Uploader "uploadComplete" event
       */
      onUploadComplete: function FlashUpload_onUploadComplete(event)
      {
         /**
          * Actions taken on a completed upload is handled by the
          * onUploadCompleteData() method instead.
          */
      },

      /**
       * Fired by YIU:s Uploader when transfer is completed for a file.
       * A difference compared to the onUploadComplete() method is that
       * the response body is available in the event.
       * Adjusts the gui and calls for another file to upload if the upload
       * was succesful.
       *
       * @method onUploadCompleteData
       * @param event {object} an Uploader "uploadCompleteData" event
       */
      onUploadCompleteData: function FlashUpload_onUploadCompleteData(event)
      {
         // The individual file has been transfered completely
         // Now adjust the gui for the individual file row
         var fileInfo = this.fileStore[event.id];
         fileInfo.state = this.STATE_SUCCESS;
         fileInfo.fileButton.set("disabled", true);

         // Extract the nodeRef and (possibly changed) fileName from the JSON response
         var oldFileName = fileInfo.fileName;
         var json = Alfresco.util.parseJSON(event.data);
         if (json)
         {
            fileInfo.nodeRef = json.nodeRef;
            fileInfo.fileName = json.fileName;
            fileInfo.rawJson = json;  // Added for CSV upload
         }

         // Add the label "Successful" after the fileSize
         fileInfo.fileSizeInfo["innerHTML"] = fileInfo.fileSizeInfo["innerHTML"] + " (" + this.msg("label.success") + ")";

         // Change the style of the progress bar
         Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
         Dom.addClass(fileInfo.progress, "fileupload-progressFinished-span");

         // Move the progress bar to "full" progress
         Dom.setStyle(fileInfo.progress, "left", 0 + "px");
         fileInfo.progressPercentage.innerHTML = "100%";
         this.noOfSuccessfulUploads++;

         // Adjust the rest of the gui
         this._updateStatus();
         this._uploadFromQueue(1);
         this._adjustGuiIfFinished();
      },

      /**
       * Fired by YIU:s Uploader when transfer has been cancelled for one of the files.
       * Doesn't do anything.
       *
       * @method onUploadCancel
       * @param event {object} an Uploader "uploadCancel" event
       */
      onUploadCancel: function FlashUpload_onUploadCancel(event)
      {
         // The gui has already been adjusted in the function that caused the cancel
      },

      /**
       * Fired by YIU:s Uploader when transfer failed for one of the files.
       * Adjusts the gui and calls for another file to upload.
       *
       * @method onUploadError
       * @param event {object} an Uploader "uploadError" event
       */
      onUploadError: function FlashUpload_onUploadError(event)
      {
         var fileInfo = this.fileStore[event.id];

         // This sometimes gets called twice, make sure we only adjust the gui once
         if (fileInfo.state !== this.STATE_FAILURE)
         {
            fileInfo.state = this.STATE_FAILURE;

            // NOTE: ideally pass through JSON error response text from Flash upload adaptor 
            //       - there is currently no known way to do this due to Flash bugs, see:
            //       http://yuilibrary.com/projects/yui3/ticket/2529848 
            //       http://stackoverflow.com/questions/4483004/getting-http-500-response-body-with-flex 

            // Add the failure label to the filename & and as a title attribute
            var key = "label.failure." + event.status,
               msg = Alfresco.util.message(key, this.name);
            if (msg == key)
            {
               msg = Alfresco.util.message("label.failure", this.name);
            }
            fileInfo.fileSizeInfo["innerHTML"] = fileInfo.fileSizeInfo["innerHTML"] + " (" + msg + ")";
            fileInfo.progressInfo.setAttribute("title", msg);
            fileInfo.progressInfoCell.setAttribute("title", msg);

            // Change the style of the progress bar
            Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
            Dom.addClass(fileInfo.progress, "fileupload-progressFailure-span");

            // Set the progress bar to "full" progress
            Dom.setStyle(fileInfo.progress, "left", 0 + "px");

            // Disable the remove button
            fileInfo.fileButton.set("disabled", true);

            // Adjust the rest of the gui
            this.noOfFailedUploads++;
            this._updateStatus();
            this._uploadFromQueue(1);
            this._adjustGuiIfFinished();
         }
      },

      /**
       * Called by an anonymous function which that redirects the call to here
       * when the user clicks the file remove button.
       * Removes the file and cancels it if it was being uploaded
       *
       * @method _onFileButtonClickHandler
       * @param flashId {string} an id matching the flash movies fileId
       * @param recordId {int} an id matching a record in the data tables data source
       */
      _onFileButtonClickHandler: function FlashUpload__onFileButtonClickHandler(flashId, recordId)
      {
         /**
          * The file button has been clicked to remove a file.
          * Remove the file from the datatable and all references to it.
          */         
         var r = this.widgets.dataTable.getRecordSet().getRecord(recordId);
         this.addedFiles[this._getUniqueFileToken(r.getData())] = null;
         this.widgets.dataTable.deleteRow(r);
         if (this.fileStore[flashId].state == this.STATE_FAILURE)
         {
            // It's only possible to a remove a file that is in a failure state if it was already
            // guaranteed to fail before uploading began (i.e. if the filename contained illegal
            // characters). Because we are removing it we can decrement the number of failures.
            this.noOfFailedUploads--;
            this._updateStatus();
         }
         
         this.fileStore[flashId] = null;
         
         if (this.state === this.STATE_BROWSING)
         {
            // Remove the file from the flash movies memory
            this.uploader.removeFile(flashId);
            if (this.widgets.dataTable.getRecordSet().getLength() === 0)
            {
               // If it was the last file, disable the gui since no files exist.
               this.widgets.uploadButton.set("disabled", true);
               this.uploader.enable();
            }
         }
         else if (this.state === this.STATE_UPLOADING)
         {
            // Cancel the ongoing upload for the file in the flash movie
            this.uploader.cancel(flashId);

            // Continue to upload documents from the queue
            this._uploadFromQueue(1);

            // Update the rest of the gui
            this._updateStatus();
            this._adjustGuiIfFinished();
         }
      },

      /**
       * Fired when the user clicks the cancel/ok button.
       * The action taken depends on what state the uploader is in.
       * In STATE_BROWSING  - Closes the panel.
       * In STATE_UPLOADING - Cancels current uploads,
       *                      informs the user about how many that were uploaded,
       *                      tells the documentlist to update itself
       *                      and closes the panel.
       * In STATE_FINISHED  - Tells the documentlist to update itself
       *                      and closes the panel.
       *
       * @method onBrowseButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelOkButtonClick: function FlashUpload_onCancelOkButtonClick()
      {
         var message, i;
         if (this.state === this.STATE_BROWSING)
         {     
            // Do nothing (but close the panel, which happens below)
         }
         else if (this.state === this.STATE_UPLOADING)
         {
            this._cancelAllUploads();

            // Inform the user if any files were uploaded before the rest was cancelled
            var noOfUploadedFiles = 0;
            for (i in this.fileStore)
            {
               if (this.fileStore[i] && this.fileStore[i].state === this.STATE_SUCCESS)
               {
                  noOfUploadedFiles++;
               }
            }
            if (noOfUploadedFiles > 0)
            {
               message = YAHOO.lang.substitute(this.msg("message.cancelStatus"),
               {
                  "0": noOfUploadedFiles
               });
            }

            if (!this.showConfig.suppressRefreshEvent)
            {
               // Tell the document list to refresh itself if present
               YAHOO.Bubbling.fire("metadataRefresh",
               {
                  currentPath: this.showConfig.path
               });
            }
         }
         else if (this.state === this.STATE_FINISHED)
         {
            // Tell the document list to refresh itself if present and to
            // highlight the uploaded file (if multi upload was used display the first file)
            var fileName = null, f;
            for (i in this.fileStore)
            {
               f = this.fileStore[i];
               if (f && f.state === this.STATE_SUCCESS)
               {
                  fileName = f.fileName;
                  break;
               }
            }
            if (!this.showConfig.suppressRefreshEvent)
            {
               if (fileName)
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     currentPath: this.showConfig.path,
                     highlightFile: fileName
                  });
               }
               else
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     currentPath: this.showConfig.path
                  });
               }
            }
         }

         // Remove all files and references for this upload "session"
         this._clear();

         // Hide the panel
         this.widgets.panel.hide();

         // Hide the Flash movie
         Dom.addClass(this.id + "-flashuploader-div", "hidden");
                  
         // Disable the Esc key listener
         this.widgets.escapeListener.disable();

         // Inform the user if any files were uploaded before the rest was cancelled
         if (message)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: message
            });
         }
      },

      /**
       * Fired when the user clicks the upload button.
       * Starts the uploading and adjusts the gui.
       *
       * @method onBrowseButtonClick
       * @param event {object} a Button "click" event
       */
      onUploadButtonClick: function FlashUpload_onUploadButtonClick()
      {
         if (this.state === this.STATE_BROWSING)
         {
            // Change the stat to uploading state and adjust the gui
            var length = this.widgets.dataTable.getRecordSet().getLength();
            if (length > 0)
            {
               this.state = this.STATE_UPLOADING;
               this.widgets.uploadButton.set("disabled", true);
               this.minorVersion.disabled = true;
               this.majorVersion.disabled = true;
               this.description.disabled = true;
               this.uploader.disable();
               this._updateStatus();
            }
            // And start uploading from the queue
            this._uploadFromQueue(2);
         }
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function FlashUpload__applyConfig()
      {
         // Set the panel title
         var title;
         if (this.showConfig.mode === this.MODE_SINGLE_UPLOAD)
         {
            title = this.msg("header.singleUpload");
         }
         else if (this.showConfig.mode === this.MODE_MULTI_UPLOAD)
         {
            title = this.msg("header.multiUpload");
         }
         else if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            title = this.msg("header.singleUpdate");
         }
         this.titleText.innerHTML = title;

         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            this.singleUpdateTip.innerHTML = YAHOO.lang.substitute(this.msg("label.singleUpdateTip"),
            {
               "0": this.showConfig.updateFilename
            });

            // Display the version input form
            Dom.removeClass(this.versionSection, "hidden");
            var versions = (this.showConfig.updateVersion || "1.0").split("."),
               majorVersion = parseInt(versions[0], 10),
               minorVersion = parseInt(versions[1], 10);
            Dom.get(this.id + "-minorVersion").innerHTML = this.msg("label.minorVersion.more", majorVersion + "." + (1 + minorVersion));
            Dom.get(this.id + "-majorVersion").innerHTML = this.msg("label.majorVersion.more", (1 + majorVersion) + ".0");
         }
         else
         {
            // Hide the version input form
            Dom.addClass(this.versionSection, "hidden");
         }

         if (this.showConfig.mode === this.MODE_MULTI_UPLOAD)
         {
            // Show the upload status label, only interesting for multiple files
            Dom.removeClass(this.statusText, "hidden");

            // Show the help label for how to select multiple files
            Dom.removeClass(this.multiUploadTip, "hidden");

            // Hide the help label for other modes
            Dom.addClass(this.singleUpdateTip, "hidden");

            // Make the file list long
            this.widgets.dataTable.set("height", "204px", true);
         }
         else
         {
            // Hide the upload status label, only interesting for multiple files
            Dom.addClass(this.statusText, "hidden");

            // Hide the help label for how to select multiple files
            Dom.addClass(this.multiUploadTip, "hidden");

            // Show the help label for single updates
            if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
            {
               // Show the help label for single updates
               Dom.removeClass(this.singleUpdateTip, "hidden");
            }
            else
            {
               // Hide the help label for single updates
               Dom.addClass(this.singleUpdateTip, "hidden");
            }

            // Make the file list short
            this.widgets.dataTable.set("height", "40px");
         }

         // Check if flash player existed or if the no flash message is displayed
         var uploaderDiv = Dom.get(this.id + "-flashuploader-div");
         var p = Dom.getFirstChild(uploaderDiv);
         if (p && p.tagName.toLowerCase() == "p")
         {
            // Flash isn't installed, make sure the no flash error message is displayed
            Dom.setStyle(uploaderDiv, "height", "30px");
            Dom.setStyle(uploaderDiv, "height", "200px");
         }
         else
         {
            this._applyUploaderConfig(
            {
               multiSelect: this.showConfig.mode === this.MODE_MULTI_UPLOAD,
               filter: this.showConfig.filter
            }, 0);

         }
      },

      /**
       * Function to try to apply configuration to Flash movie.
       *
       * @method _applyUploaderConfig
       * @param obj {Object} Object literal containing configuration
       * @param attempt {int} Counter for retry attempts
       * @private
       */
      _applyUploaderConfig: function (obj, attempt)
      {
         try
         {
            this.uploader.enable();
            this.uploader.setAllowMultipleFiles(obj.multiSelect);
            this.uploader.setFileFilters(obj.filter);
         }
         catch(e)
         {
            if (attempt == 7)
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: this.msg("message.flashError.title"),
                  text: this.msg("message.flashError.message"),
                  buttons: [
                  {
                     text: Alfresco.util.message("button.ok"),
                     handler:
                     {
                        fn: function _applyUploaderConfig_onOk(e, p_obj)
                        {
                           this.destroy();
                           p_obj.widgets.panel.destroy();
                           var fileUpload = p_obj._disableFlashUploader();
                           if (fileUpload)
                           {
                              fileUpload.show(p_obj.suppliedConfig);
                           }
                        },
                        obj: this
                     },
                     isDefault: true
                  },
                  {
                     text: Alfresco.util.message("button.refreshPage"),
                     handler: function _applyUploaderConfig_onRefreshPage()
                     {
                        window.location.reload(true);
                     }
                  }]
               });
            }
            else
            {
               YAHOO.lang.later(100, this, this._applyUploaderConfig, [obj, ++attempt]);
            }
         }
      },
      
      /**
       * Disables Flash uploader if an error is detected.
       * Possibly a temporary workaround for bugs in SWFObject v1.5
       *
       * @method _disableFlashUploader
       */
      _disableFlashUploader: function FlashUpload__disableFlashUploader()
      {
         var fileUpload = Alfresco.util.ComponentManager.findFirst("Alfresco.FileUpload");
         if (fileUpload)
         {
            fileUpload.options.adobeFlashEnabled = false;
         }
         return fileUpload;
      },

      /**
       * Helper function to create the data table and its cell formatter.
       *
       * @method _createEmptyDataTable
       * @private
       */
      _createEmptyDataTable: function FlashUpload__createEmptyDataTable()
      {
         /**
          * Save a reference of 'this' so that the formatter below can use it
          * later (since the formatter method gets called with another scope
          * than 'this').
          */
         var myThis = this;

         /**
          * Responsible for rendering the left row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatLeftCell = function(el, oRecord, oColumn, oData)
         {
            myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.left);
         };

         /**
          * Responsible for rendering the center row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatCenterCell = function(el, oRecord, oColumn, oData)
         {
            myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.center);
         };

         /**
          * Responsible for rendering the right row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatRightCell = function(el, oRecord, oColumn, oData)
         {
            myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.right);
         };

         /**
          * Takes a left, center or right column template and looks for expected
          * html components and vcreates yui objects or saves references to
          * them so they can be updated during the upload progress.
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          * @param template the template to display in the column
          */
         this._formatCellElements = function(el, oRecord, template)
         {
            var record = oRecord.getData(),
               flashId = record.id;
            
            // Set the state for this file(/row) if it hasn't been set
            if (!this.fileStore[flashId])
            {
               var defaultState = this.STATE_BROWSING;
               
               // ALF-6697
               // Although the users operating system might be Unix based it is necessary that we only allow
               // file names that are supported by the lowest common denominator (Windows)
               var mockField = 
               {
                  id: flashId,
                  value: record.name
               };
               if (!Alfresco.forms.validation.nodeName(mockField, {}, null, null, true, null))
               {
                  defaultState = this.STATE_ILLEGAL_FILENAME;
               }
               else
               {
                  // No action required. Default state already set.
               }

               // Add the item to the file store...
               this.fileStore[flashId] =
               {
                  state: defaultState,
                  fileName: record.name,
                  nodeRef: null
               };
            }

            // create an instance from the template and give it a uniqueue id.
            var cell = new Element(el);
            var templateInstance = template.cloneNode(true);
            templateInstance.setAttribute("id", templateInstance.getAttribute("id") + flashId);

            // Save references to elements that will be updated during upload.
            var progress = Dom.getElementsByClassName("fileupload-progressSuccess-span", "span", templateInstance);
            if (progress.length == 1)
            {
               this.fileStore[flashId].progress = progress[0];
            }
            var progressInfo = Dom.getElementsByClassName("fileupload-progressInfo-span", "span", templateInstance);
            if (progressInfo.length == 1)
            {
               // Display the file size in human readable format after the filename.
               var fileInfoStr = record.name;
               templateInstance.setAttribute("title", fileInfoStr);

               // Display the file name and size.
               progressInfo = progressInfo[0];
               this.fileStore[flashId].progressInfo = progressInfo;
               this.fileStore[flashId].progressInfo.innerHTML = $html(fileInfoStr);

               // Save the cell element
               this.fileStore[flashId].progressInfoCell = el;
            }

            var fileSize = Dom.getElementsByClassName("fileupload-filesize-span", "span", templateInstance);
            if (fileSize.length == 1)
            {
               // Display the file size in human readable format below the filename.
               var fileInfoStr = Alfresco.util.formatFileSize(record.size);
               fileSize = fileSize[0];
               this.fileStore[flashId].fileSizeInfo = fileSize;
               fileSize.innerHTML = fileInfoStr;
            }

            // Save a reference to the contentType dropdown so we can find each file's contentType before upload.            
            var contentType = Dom.getElementsByClassName("fileupload-contentType-select", "select", templateInstance);
            if (contentType.length == 1)
            {
               this.fileStore[flashId].contentType = contentType[0];
            }
            else
            {
               contentType = Dom.getElementsByClassName("fileupload-contentType-input", "input", templateInstance);
               if(contentType.length == 1)
               {
                  this.fileStore[flashId].contentType = contentType[0];
               }
            }

            // Save references to elements that will be updated during upload.
            var progressPercentage = Dom.getElementsByClassName("fileupload-percentage-span", "span", templateInstance);
            if (progressPercentage.length == 1)
            {
               this.fileStore[flashId].progressPercentage = progressPercentage[0];
            }

            // Create a yui button for the fileButton.
            var fButton = Dom.getElementsByClassName("fileupload-file-button", "button", templateInstance);
            if (fButton.length == 1)
            {
               var fileButton = new YAHOO.widget.Button(fButton[0],
               {
                  type: "button",
                  disabled: false
               });
               fileButton.subscribe("click", function()
               {
                  this._onFileButtonClickHandler(flashId, oRecord.getId());
               }, this, true);
               this.fileStore[flashId].fileButton = fileButton;               
            }

            // Insert the templateInstance to the column.
            cell.appendChild(templateInstance);            
         };

         // Definition of the data table column
         var myColumnDefs = [
            { key: "id", className:"col-left", resizable: false, formatter: formatLeftCell },
            { key: "name", className:"col-center", resizable: false, formatter: formatCenterCell },
            { key: "created", className:"col-right", resizable: false, formatter: formatRightCell }
         ];

         // The data tables underlying data source.
         var myDataSource = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         /**
          * Create the data table.
          * Set the properties even if they will get changed in applyConfig
          * afterwards, if not set here they will not be changed later.
          */
         YAHOO.widget.DataTable._bStylesheetFallback = !!YAHOO.env.ua.ie;
         var dataTableDiv = Dom.get(this.id + "-filelist-table");
         this.widgets.dataTable = new YAHOO.widget.DataTable(dataTableDiv, myColumnDefs, myDataSource,
         {
            scrollable: true,            
            height: "100px", // must be set to something so it can be changed afterwards, when the showconfig options decides if its a sinlge or multi upload
            width: "620px",
            renderLoopSize: 1,
            MSG_EMPTY: this.msg("label.noFiles")
         });
         this.widgets.dataTable.subscribe("postRenderEvent", this.onPostRenderEvent, this, true);
         this.widgets.dataTable.subscribe("rowDeleteEvent", this.onRowDeleteEvent, this, true);
      },

      /**
       * Helper function to create a unique file token from the file data object
       *
       * @method _getUniqueFileToken
       * @param data {object} a file data object describing a file
       * @private
       */
      _getUniqueFileToken: function FlashUpload__getUniqueFileToken(data)
      {
         return data.name + ":" + data.size + ":" + data.cDate + ":" + data.mDate;
      },

      /**
       * Update the status label with the latest information about the upload progress
       *
       * @method _updateStatus
       * @private
       */
      _updateStatus: function FlashUpload__updateStatus()
      {
         // Update the status label with the latest information about the upload progress
         this.statusText.innerHTML = YAHOO.lang.substitute(this.msg("label.uploadStatus"),
         {
            "0" : this.noOfSuccessfulUploads,
            "1" : this.widgets.dataTable.getRecordSet().getLength(),
            "2" : this.noOfFailedUploads
         });
      },
      
      /**
       * Remove the last status message
       *
       * @method _updateStatus
       * @private
       */
      _clearStatus: function FlashUpload__clearStatus()
      {
         // Update the status label with the latest information about the upload progress
         this.statusText.innerHTML = "";
      },

      /**
       * Checks if all files are finished (successfully uploaded or failed)
       * and if so adjusts the gui.
       *
       * @method _adjustGuiIfFinished
       * @private
       */
      _adjustGuiIfFinished: function FlashUpload__adjustGuiIfFinished()
      {
         var objComplete =
         {
            successful: [],
            failed: []
         };
         var file = null;
         
         // Go into finished state if all files are finished: successful or failures
         for (var i in this.fileStore)
         {
            file = this.fileStore[i];
            if (file)
            {
               if (file.state == this.STATE_SUCCESS)
               {
                  // Push successful file
                  objComplete.successful.push(
                  {
                     fileName: file.fileName,
                     nodeRef: file.nodeRef,
                     response: file.rawJson
                  });
               }
               else if (file.state == this.STATE_FAILURE)
               {
                  // Push failed file
                  objComplete.failed.push(
                  {
                     fileName: file.fileName
                  });
               }
               else
               {
                  return;
               }
            }
         }
         this.state = this.STATE_FINISHED;
         if(!this.showConfig.suppressRefreshEvent)
         {
            this.widgets.cancelOkButton.set("label", this.msg("button.ok"));
            this.widgets.cancelOkButton.focus();
         }
         else
         {
            this.widgets.cancelOkButton.set("disabled", true);
         }
         this.widgets.uploadButton.set("disabled", true);
         Dom.addClass(this.id + "-upload-button", "hidden");
         
         var callback = this.showConfig.onFileUploadComplete;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onFileUploadComplete callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), objComplete, callback.obj);
         }
      },

      /**
       * Starts to upload as many files as specified by noOfUploadsToStart
       * as long as there are files left to upload.
       *
       * @method _uploadFromQueue
       * @param noOfUploadsToStart
       * @private
       */
      _uploadFromQueue: function FlashUpload__uploadFromQueue(noOfUploadsToStart)
      {
         // generate upload POST url
         var url;
         if (this.showConfig.uploadURL === null)
         {
            url = Alfresco.constants.PROXY_URI + "api/upload";
         }
         else
         {
            url = Alfresco.constants.PROXY_URI + this.showConfig.uploadURL;
         }
         
         // Flash does not correctly bind to the session cookies during POST
         // so we manually patch the jsessionid directly onto the URL instead
         // also include the CSRF token to pass the CSRF token filter
         url += ";jsessionid=" + YAHOO.util.Cookie.get("JSESSIONID") + "?lang=" + Alfresco.constants.JS_LOCALE;

         // Pass the CSRF token if the CSRF token filter is enabled
         if (Alfresco.util.CSRFPolicy.isFilterEnabled())
         {
            url += "&" + Alfresco.util.CSRFPolicy.getParameter() + "=" + encodeURIComponent(Alfresco.util.CSRFPolicy.getToken());
         }

         // Find files to upload
         var startedUploads = 0,
            length = this.widgets.dataTable.getRecordSet().getLength(),
            record, flashId, fileInfo, attributes;
         
         for (var i = 0; i < length && startedUploads < noOfUploadsToStart; i++)
         {
            record = this.widgets.dataTable.getRecordSet().getRecord(i);
            flashId = record.getData("id");
            fileInfo = this.fileStore[flashId];
            if (fileInfo.state === this.STATE_BROWSING)
            {
               // Upload has NOT been started for this file, start it now
               fileInfo.state = this.STATE_UPLOADING;
               
               attributes =
               {
                  username: this.showConfig.username
               };

               // Site or Non-site (Repository) mode
               if (this.showConfig.siteId !== null)
               {
                  attributes.siteId = this.showConfig.siteId;
                  attributes.containerId = this.showConfig.containerId;
               }
               else if (this.showConfig.destination !== null)
               {
                  attributes.destination = this.showConfig.destination
               }
               
               if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
               {         
                  attributes.updateNodeRef = this.showConfig.updateNodeRef;
                  attributes.majorVersion = !this.minorVersion.checked;
                  attributes.description = this.description.value;
               }
               else
               {
                  if (this.showConfig.uploadDirectory !== null)
                  {
                     attributes.uploadDirectory = this.showConfig.uploadDirectory;
                  }
                  if (fileInfo.contentType)
                  {
                     if (fileInfo.contentType.tagName.toLowerCase() == "select")
                     {
                        attributes.contentType = fileInfo.contentType.options[fileInfo.contentType.selectedIndex].value;
                     }
                     else
                     {
                        attributes.contentType = fileInfo.contentType.value;
                     }
                  }
                  attributes.overwrite = this.showConfig.overwrite;
                  if (this.showConfig.thumbnails)
                  {
                     attributes.thumbnails = this.showConfig.thumbnails;
                  }
               }
               this.uploader.upload(flashId, url, "POST", attributes, "filedata");
               startedUploads++;
            }
         }
      },

      /**
       * Cancels all uploads inside the flash movie.
       *
       * @method _cancelAllUploads
       * @private
       */
      _cancelAllUploads: function FlashUpload__cancelAllUploads()
      {
         // Cancel all uploads inside the flash movie
         var length = this.widgets.dataTable.getRecordSet().getLength();
         for (var i = 0; i < length; i++)
         {
            var record = this.widgets.dataTable.getRecordSet().getRecord(i);
            var flashId = record.getData("id");
            this.uploader.cancel(flashId);
         }
      },

      /**
       * Remove all references to files inside the data table, flash movie
       * and the this class references.
        *
       * @method _clear
       * @private
       */
      _clear: function FlashUpload__clear()
      {
         /**
          * Remove all references to files inside the data table, flash movie
          * and this class's references.
          */
         var length = this.widgets.dataTable.getRecordSet().getLength();
         this.addedFiles = {};
         this.fileStore = {};
         this.widgets.dataTable.deleteRows(0, length);
         this.uploader.clearFileList();
      }
   });
})();
