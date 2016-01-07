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
 * Drag and Drop Upload component.
 *
 * Pops up a YUI panel and initiates the upload and progress monitoring of
 * the files providing in the calling arguments.
 *
 * A multi file upload scenario could look like:
 *
 * var dndUpload = Alfresco.component.getDNDUploadInstance();
 * var multiUploadConfig =
 * {
 *    files: files,
 *    destination: destination,
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: flashUpload.MODE_MULTI_UPLOAD,
 * }
 * dndUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco.module
 * @class Alfresco.DNDUpload
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
    * DNDUpload constructor.
    *
    * DNDUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.component.getDNDUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.DNDUpload} The new DNDUpload instance
    * @constructor
    * @private
    */
   Alfresco.DNDUpload = function(htmlId)
   {
      Alfresco.DNDUpload.superclass.constructor.call(this, "Alfresco.DNDUpload", htmlId, ["button", "container", "datatable", "datasource"]);

      this.fileStore = {};
      this.addedFiles = {};
      this.defaultShowConfig =
      {
         files: [],
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
         suppressRefreshEvent: false,
         maximumFileSize: 0
      };
      this.suppliedConfig = {};
      this.showConfig = {};
      this.fileItemTemplates = {};

      // When the DNDUpload instance is first created we should determine the upload method that
      // the client will use. This is done via browser feature detection. If the browser supports
      // the FormData object then this will be used (as it allows data to be streamed without being
      // loaded into memory) otherwise the file will need to be loaded into the browser memory before
      // it can be uploaded to the server. This state can be queried by callers to the instance
      // using the "uploadMethod" attribute.
      if (typeof FormData !== "undefined")
      {
         this.uploadMethod = this.FORMDATA_UPLOAD;
      }
      else
      {
         this.uploadMethod = this.INMEMORY_UPLOAD;
      }

      return this;
   };

   YAHOO.extend(Alfresco.DNDUpload, Alfresco.component.Base,
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
       * The client supports FormData upload.
       *
       * @property FORMDATA_UPLOAD
       * @type int
       */
      FORMDATA_UPLOAD: 1,

      /**
       * The client requires in-memory upload.
       *
       * @property INMEMORY_UPLOAD
       * @type int
       */
      INMEMORY_UPLOAD: 2,

      /**
       * The method that will be used to perform file upload. This is determined via browser
       * feature detection and is set during singleton instantiation.
       *
       * @property uploadMethod
       * @type int
       */
      uploadMethod: 2,

      /**
       * The user is browsing and adding files to the file list
       *
       * @property STATE_BROWSING
       * @type int
       */
      STATE_BROWSING: 1,

      /**
       * File(s) is been added
       *
       * @property STATE_ADDED
       * @type int
       */
      STATE_ADDED: 2,

      /**
       * File(s) is being uploaded to the server
       *
       * @property STATE_UPLOADING
       * @type int
       */
      STATE_UPLOADING: 3,

      /**
       * All files are processed and have either failed or been successfully
       * uploaded to the server.
       *
       * @property STATE_FINISHED
       * @type int
       */
      STATE_FINISHED: 4,

      /**
       * File failed to upload.
       *
       * @property STATE_FAILURE
       * @type int
       */
      STATE_FAILURE: 5,

      /**
       * File was successfully STATE_SUCCESS.
       *
       * @property STATE_SUCCESS
       * @type int
       */
      STATE_SUCCESS: 6,

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
       *                                             // (state flow: STATE_BROWSING > STATE_ADDED > STATE_UPLOADING > STATE_SUCCESS or STATE_FAILURE)
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
       * The expected volume of data to be uploaded by all selected files.
       *
       * @property aggregateUploadTargetSize
       * @type int
       */
      aggregateUploadTargetSize: 0,

      /**
       * The current volume of data uploaded in the current operation.
       *
       * @property aggregateUploadCurrentSize
       * @type int
       */
      aggregateUploadCurrentSize: 0,

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
       * Contains the upload gui
       *
       * @property panel
       * @type YAHOO.widget.Dialog
       */
      panel: null,

      /**
       * Used to display the user selceted files and keep track of what files
       * that are selected and should be STATE_FINISHED.
       *
       * @property uploader
       * @type YAHOO.widget.DataTable
       */
      dataTable: null,

      /**
       * HTMLElement of type span that displays the dialog title.
       *
       * @property titleText
       * @type HTMLElement
       */
      titleText: null,

      /**
       * HTMLElement of type span that displays the total upload status
       *
       * @property statusText
       * @type HTMLElement
       */
      statusText: null,

      /**
       * HTMLElement of type span that displays the aggregate upload progress
       *
       * @property aggregateProgressText
       * @type HTMLElement
       */
      aggregateProgressText: null,

      /**
       * HTMLElement of type div that displays the aggregate upload data that is
       * is shown when uploads are in progress.
       *
       * @property aggregateDataWrapper
       * @type HTMLElement
       */
      aggregateDataWrapper: null,

      /**
       * HTMLElement of type input that is used for selecting files for uploading.
       * 
       * @property fileSelectionInput
       * @type HTMLElement
       */
      fileSelectionInput: null,
      
      /**
       * HTMLElement of type radio button for major or minor version
       *
       * @property description
       * @type HTMLElement
       */
      minorVersion: null,

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
       * Restricts the allowed maximum file size for a single file (in bytes).
       * 0 means there is no restriction.
       *
       * @property _maximumFileSizeLimit
       * @private
       * @type int
       * @default 0
       */
      _maximumFileSizeLimit: 0,

      /**
       * Sets te maximum allowed size for one file.
       *
       * @method setMaximumFileSizeLimit
       * @param maximumFileSizeLimit
       */
      setMaximumFileSizeLimit: function DNDUpload_setMaximumFileSizeLimit(maximumFileSizeLimit)
      {
         this._maximumFileSizeLimit = maximumFileSizeLimit;
      },

      /**
       * Returns the maximum allowed size for one file
       *
       * @method getMaximumFileSizeLimit
       */
      getMaximumFileSizeLimit: function DNDUpload_getInMemoryLimit()
      {
         return this._maximumFileSizeLimit;
      },

      /**
       * The maximum size of the sum of file sizes that be uploaded in a single operation when
       * operating in INMEMORY_UPLOAD mode.
       *
       * @property _inMemoryLimit
       * @private
       * @type int
       */
      _inMemoryLimit: 250000000,

      /**
       * Sets maximum size of the sum of file sizes that be uploaded in a single operation. This
       * limit only affects browsers operating in INMEMORY_UPLOAD mode.
       *
       * @method setInMemoryLimit
       * @param limit
       */
      setInMemoryLimit: function DNDUpload_setInMemoryLimit(limit)
      {
         if (isNaN(limit))
         {
            // If the user has overridden the default value and provided a non-numerical value
            // then we'll just leave the limit as the default.
            Alfresco.logger.warn("Non-numerical value set for \"in-memory-limit\" in share-documentlibrary.xml: ", limit);
            this._inMemoryLimit = 25000000;
         }
         else
         {
            this._inMemoryLimit = limit;
         }
      },

      /**
       * Returns the maximum size of the sum of file sizes that be uploaded in a single operation. This
       * limit only affects browsers operating in INMEMORY_UPLOAD mode.
       *
       * @method setInMemoryLimit
       * @param limit
       */
      getInMemoryLimit: function DNDUpload_getInMemoryLimit()
      {
         return this._inMemoryLimit;
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DNDUpload_onReady()
      {
         Dom.removeClass(this.id + "-dialog", "hidden");

         // Create the panel
         this.panel = Alfresco.util.createYUIPanel(this.id + "-dialog");

         // Hook close button
         this.panel.hideEvent.subscribe(this.onCancelOkButtonClick, null, this);

         // Save a reference to the file row template that is hidden inside the markup
         this.fileItemTemplates.left = Dom.get(this.id + "-left-div");
         this.fileItemTemplates.center = Dom.get(this.id + "-center-div");
         this.fileItemTemplates.right = Dom.get(this.id + "-right-div");

         // Create the YIU datatable object
         this._createEmptyDataTable();

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.titleText = Dom.get(this.id + "-title-span");
         this.statusText = Dom.get(this.id + "-status-span");
         this.aggregateProgressText = Dom.get(this.id + "-aggregate-status-span");
         this.aggregateDataWrapper = Dom.get(this.id + "-aggregate-data-wrapper");
         this.description = Dom.get(this.id + "-description-textarea");

         // Save reference to version radio so we can reset and get its value later
         this.minorVersion = Dom.get(this.id + "-minorVersion-radioButton");

         // Save a reference to the HTMLElement displaying version input so we can hide or show it
         this.versionSection = Dom.get(this.id + "-versionSection-div");

         // Create and save a reference to the buttons so we can alter them later
         this.widgets.cancelOkButton = Alfresco.util.createYUIButton(this, "cancelOk-button", this.onCancelOkButtonClick);
         this.widgets.uploadButton = Alfresco.util.createYUIButton(this, "upload-button", this.onUploadButtonClick, {additionalClass: "alf-primary-button"});
         this.widgets.fileSelectionOverlayButton = Alfresco.util.createYUIButton(this, "file-selection-button-overlay", this._doNothing, {additionalClass: "alf-primary-button"});
         Dom.addClass(this.widgets.fileSelectionOverlayButton._button, "dnd-file-selection-button-overlay");
         Dom.addClass(this.widgets.fileSelectionOverlayButton._button.parentNode, "dnd-file-selection-button-overlay-wrapper");

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
      },

      /**
       * Does nothing - used just for getting the YUI button to highlight as expected.
       * @method _doNothing
       * @param event {object} a file selection "change" event
       */
      _doNothing: function DNDUpload_doNothing()
      {
         // Do nothing
      },
      
      /**
       * Called when files are selected from the input element. 
       * 
       * @method onBrowseButtonClick
       * @param event {object} a file selection "change" event
       */
      onFileSelection: function DNDUpload_onFileSelection(evt)
      {
         var files = evt.target.files; // FileList object
         if (files != null)
         {
            this.showConfig.files = files;

            // This is done even if no files are selected to ensure the display is correct if the next upload is via drag and drop...
            if (this.dataTable != null)
            {
               // Check the data table has been set up (it might not be if this is the first invocation)...
               this.dataTable.set("height", "204px", true);
            }
            
            // Do some checks to ensure that we can proceed with some kind of upload
            var allZeroByteFiles = true;
            for (var i=0; i<files.length; i++)
               {
               if (files[i].size !== 0)
               {
                  allZeroByteFiles = false;
                  break;
               }
            }
            
            if (allZeroByteFiles)
            {
               // All the files selected are zero bytes in length - process the files anyway as this will
               // properly validate for errors but we won't change the appearance of the file selection.
               this.processFilesForUpload(this.showConfig.files);
            }
            else
            {
               if (this.suppliedConfig.mode == this.MODE_SINGLE_UPDATE)
               {
                  // If we're doing an update then we don't want to start the upload immediately as this 
                  // will not allow the user the opportunity to set the version update level or add a
                  // comment...
                  this.widgets.uploadButton.set("disabled", false);
                  Dom.removeClass(this.widgets.uploadButton, "hidden");
               }
               else
               {
                  Dom.removeClass(this.id + "-filelist-table", "hidden");
                  Dom.removeClass(this.id + "-aggregate-data-wrapper", "hidden");
                  Dom.addClass(this.id + "-file-selection-controls", "hidden");
                  this.processFilesForUpload(this.showConfig.files);
               }
            }
         }
         else
         {
            // No files selected, do nothing.
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
      show: function DNDUpload_show(config)
      {
         // Create an alias for this (it is required for the listener functions declared later)
         var _this = this;

         // Merge the supplied config with default config and check mandatory properties
         this.suppliedConfig = config;
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (!this.showConfig.uploadDirectory && !this.showConfig.updateNodeRef && !this.showConfig.destination && !this.showConfig.uploadURL)
         {
             throw new Error("An updateNodeRef, uploadDirectory, destination or uploadURL must be provided");
         }
         
         if (this.showConfig.uploadDirectory !== null && this.showConfig.uploadDirectory.length === 0)
         {
            this.showConfig.uploadDirectory = "/";
         }

         // Apply the config before it is shown
         this._resetGUI();

         // Apply the config before it is shown
         this._applyConfig();
         
         // If files is not defined then assume we need to select them...
         if (this.showConfig.files == null || this.showConfig.files.length == 0)
         {
            // Display the file select section of the dialog
            // Hide the file and progress information...
            Dom.removeClass(this.id + "-file-selection-controls", "hidden");
            Dom.addClass(this.id + "-filelist-table", "hidden");
            Dom.addClass(this.aggregateDataWrapper, "hidden");

            if (YAHOO.env.ua.ie > 9)
            {
               // Create a new file selection input element (to ensure old data is retained we will remove any old instance...
               if (this.fileSelectionInput && this.fileSelectionInput.parentNode)
               {
                  this.fileSelectionInputParent = this.fileSelectionInput.parentNode;
                  this.fileSelectionInput.parentNode.removeChild(this.fileSelectionInput); // Remove the old node...
               }
               else
               {
                  this.fileSelectionInputParent = this.widgets.fileSelectionOverlayButton._button.parentNode;
                  this.fileSelectionInputParent.removeChild(this.widgets.fileSelectionOverlayButton._button);
               }
               
               this.fileSelectionInput = document.createElement("input");
               Dom.setAttribute(this.fileSelectionInput, "type", "file");
               
               // Only set the multiple attribute on the input element if running in multi-file upload
               // (i.e. we don't want to allow multiple file selection when updating a file)
               if (this.suppliedConfig.mode !== this.MODE_SINGLE_UPLOAD && this.suppliedConfig.mode !== this.MODE_SINGLE_UPDATE)
               {
                  Dom.setAttribute(this.fileSelectionInput, "multiple", "");
               }
               Dom.setAttribute(this.fileSelectionInput, "name", "files[]");
               Dom.addClass(this.fileSelectionInput, "ie10-dnd-file-selection-button");
               Event.addListener(this.fileSelectionInput, "change", this.onFileSelection, this, true);

               // MNT-12948
               new KeyListener(this.fileSelectionInput,
               {
                  keys: KeyListener.KEY.ENTER
               },
               {
                  fn: function enter_key_pressed(obj)
                  {
                     this.fileSelectionInput.click();
                  },
                  scope: this,
                  correctScope: true
               }).enable();

               this.fileSelectionInputParent.appendChild(this.fileSelectionInput);
            }
            else
            {
               // Create a new file selection input element (to ensure old data is retained we will remove any old instance...
               if (this.fileSelectionInput && this.fileSelectionInput.parentNode)
               {
                  this.fileSelectionInput.parentNode.removeChild(this.fileSelectionInput); // Remove the old node...
               }
               
               this.fileSelectionInput = document.createElement("input");
               Dom.setAttribute(this.fileSelectionInput, "type", "file");

               // Only set the multiple attribute on the input element if running in multi-file upload
               // (i.e. we don't want to allow multiple file selection when updating a file)
               if (this.suppliedConfig.mode !== this.MODE_SINGLE_UPLOAD && this.suppliedConfig.mode !== this.MODE_SINGLE_UPDATE)
               {
                  Dom.setAttribute(this.fileSelectionInput, "multiple", "");
               }
               Dom.setAttribute(this.fileSelectionInput, "name", "files[]");
               Dom.addClass(this.fileSelectionInput, "dnd-file-selection-button");
               Event.addListener(this.fileSelectionInput, "change", this.onFileSelection, this, true);
               this.widgets.fileSelectionOverlayButton._button.parentNode.appendChild(this.fileSelectionInput);
            }
            
            // Enable the Esc key listener
            this.widgets.escapeListener.enable();
            this.widgets.enterListener = new KeyListener(this.widgets.fileSelectionOverlayButton._button,
            {
               keys: KeyListener.KEY.ENTER
            },
            {
               fn: function enter_key_pressed(obj)
               {
                  this.fileSelectionInput.click();
               },
               scope: this,
               correctScope: true
            });
            this.widgets.enterListener.enable();
            this.panel.setFirstLastFocusable();
            this.panel.show();
         }
         else
         {
            Dom.removeClass(this.id + "-filelist-table", "hidden");
            Dom.removeClass(this.aggregateDataWrapper, "hidden");
            Dom.addClass(this.id + "-file-selection-controls", "hidden");
            this.processFilesForUpload(this.showConfig.files);
         }
      },

      /**
       * Validates the file, i.e. name & size.
       *
       * @method _getFileValidationErrors
       * @param file
       * @return {null|string} String describing the error (html escaped) or null if file is valid
       */
      processFilesForUpload: function(_files)
      {
         
         // Start the upload...
         // Calculate the total expected upload size ahead of upload start to ensure
         // that a steady progress indication is presented...
         var aggregateSize = 0,
             message = null,
             messages = null,
             fileName;
         for (var i=0; i<_files.length; i++)
         {
            aggregateSize += _files[i].size;
            fileName = _files[i].name;

            // Validate file and collect errors
            message = this._getFileValidationErrors(_files[i]);
            if (message)
            {
                if (messages)
                {
                   messages += '<br/>' + message;
                }
                else
                {
                   messages = message;
                }
            }
         }
         this.aggregateUploadTargetSize = aggregateSize;
         
         // Recursively add files to the queue
         this._addFiles(0, _files.length, this);

         // Start uploads
         this._spawnUploads();

         // Display validation errors
         if (messages)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("header.error"),
               text: messages,
               noEscape: true
            });
         }
      },

      /**
       * Validates the file, i.e. name & size.
       *
       * @method _getFileValidationErrors
       * @param file
       * @return {null|string} String describing the error (html escaped) or null if file is valid
       */
      _getFileValidationErrors: function(file)
      {
         // Check if the file has size...
         var fileName = file.name;
         if (file.size === 0)
         {
            return this.msg("message.zeroByteFileSelected", $html(fileName));
         }
         else if (this._maximumFileSizeLimit > 0 && file.size > this._maximumFileSizeLimit)
         {
            return this.msg("message.maxFileFileSizeExceeded", $html(fileName), Alfresco.util.formatFileSize(file.size), Alfresco.util.formatFileSize(this._maximumFileSizeLimit));
         }
         else if (!Alfresco.forms.validation.nodeName({ id: 'file', value: fileName }, null, null, null, true))
         {
            return this.msg("message.illegalCharacters", $html(fileName));
         }

         // File is valid
         return null;
      },

      /**
       * A function that adds the file to the table, increments the file count and then recurses. Recursion is used instead of iteration
       * because the progress events do not contain enough information to link them back to the file they relate. The alternative
       * to recursion is to assign custom data to the XMLHttpRequest.upload object but the FireFox browser can randomly lose
       * this data and stall the upload. Using recursion ensures that the "fileId" variable is set correctly as the event listener
       * functions will only find the correct value in their immediate closure (using iteration it will always end up as the
       * last value)
       * 
       * @method _addFiles
       * @param i The index in the file to upload
       * @param max The count of files to upload (recursion stops when i is no longer less than max)
       * @param scope Should be set to the widget scope (i.e. this). 
       */
      _addFiles: function DNDUpload__addFiles(i, max, scope)
      {
         var uniqueFileToken;
         if (i < max)
         {
            var file = scope.showConfig.files[i];
            if (!this._getFileValidationErrors(file))
            {
               var fileId = "file" + i;
               try
               {
                  /**
                   * UPLOAD PROGRESS LISTENER
                   */
                  var progressListener = function DNDUpload_progressListener(e)
                  {
                    Alfresco.logger.debug("File upload progress update received", e);
                    if (e.lengthComputable)
                    {
                        try
                        {
                           var percentage = Math.round((e.loaded * 100) / e.total),
                               fileInfo = scope.fileStore[fileId];
                           fileInfo.progressPercentage.innerHTML = percentage + "%";
   
                           // Set progress position
                           var left = (-400 + ((percentage/100) * 400));
                           Dom.setStyle(fileInfo.progress, "left", left + "px");
                           scope._updateAggregateProgress(fileInfo, e.loaded);
   
                           // Save value of how much has been loaded for the next iteration
                           fileInfo.lastProgress = e.loaded;
                        }
                        catch(exception)
                        {
                           Alfresco.logger.error("The following error occurred processing an upload progress event: ", exception);
                        }
                    }
                    else
                    {
                        Alfresco.logger.debug("File upload progress not computable", e);
                    }
                 };
   
                 /**
                  * UPLOAD COMPLETION LISTENER
                  */
                 var successListener = function DNDUpload_successListener(e)
                 {
                    try
                    {
                       Alfresco.logger.debug("File upload completion notification received", e);
   
                       // The individual file has been transfered completely
                       // Now adjust the gui for the individual file row
                       var fileInfo = scope.fileStore[fileId];
                       if (fileInfo.request.readyState != 4)
                       {
                          // There is an occasional timing issue where the upload completion event fires before
                          // the readyState is correctly updated. This means that we can't check the upload actually
                          // completed successfully, if this occurs then we'll attach a function to the onreadystatechange
                          // extension point and things to catch up before we check everything was ok...
                          fileInfo.request.onreadystatechange = function DNDUpload_onreadystatechange()
                          {
                             if (fileInfo.request.readyState == 4)
                             {
                                scope._processUploadCompletion(fileInfo);
                             }
                          }
                       }
                       else
                       {
                          // If the request correctly indicates that the response has returned then we can process
                          // it to ensure that files have been uploaded correctly.
                          scope._processUploadCompletion(fileInfo);
                       }
                    }
                    catch(exception)
                    {
                       Alfresco.logger.error("The following error occurred processing an upload completion event: ", exception);
                    }
                  };
   
                  /**
                   * UPLOAD FAILURE LISTENER
                   */
                  var failureListener = function DNDUpload_failureListener(e)
                  {
                     try
                     {
                        var fileInfo = scope.fileStore[fileId];
   
                           // This sometimes gets called twice, make sure we only adjust the gui once
                           if (fileInfo.state !== scope.STATE_FAILURE)
                           {
                              scope._processUploadFailure(fileInfo, e.status);
                           }
                        }
                        catch(exception)
                     {
                        Alfresco.logger.error("The following error occurred processing an upload failure event: ", exception);
                     }
                  };
   
                  // Get the name of the file (note that we use ".name" and NOT ".fileName" which is non-standard and it's use 
                  // will break FireFox 7)...
                  var fileName = file.name;
               
                  // Add the event listener functions to the upload properties of the XMLHttpRequest object...
                  var request = new XMLHttpRequest();
                  
                  // Add the data to the upload property of XMLHttpRequest so that we can determine which file each
                  // progress update relates to (the event argument passed in the progress function does not contain
                  // file name details)...
                  request.upload._fileData = fileId;
                  request.upload.addEventListener("progress", progressListener, false);
                  request.upload.addEventListener("load", successListener, false);
                  request.upload.addEventListener("error", failureListener, false);
                  
                  // Construct the data that will be passed to the YUI DataTable to add a row...
                  data = {
                      id: fileId,
                      name: fileName,
                      size: scope.showConfig.files[i].size
                  };

                  // Get the nodeRef to update if available (this is required to perform version update)...
                  var updateNodeRef = null;
                  if (scope.suppliedConfig && scope.suppliedConfig.updateNodeRef)
                  {
                     updateNodeRef = scope.suppliedConfig.updateNodeRef;
                  }
                  
                  // Construct an object containing the data required for file upload...
                  var uploadData =
                  {
                     filedata: scope.showConfig.files[i],
                     filename: fileName,
                     destination: scope.showConfig.destination,
                     siteId: scope.showConfig.siteId,
                     containerId: scope.showConfig.containerId,
                     uploaddirectory: scope.showConfig.uploadDirectory,
                     majorVersion: !scope.minorVersion.checked,
                     updateNodeRef: updateNodeRef,
                     description: scope.description.value,
                     overwrite: scope.showConfig.overwrite,
                     thumbnails: scope.showConfig.thumbnails,
                     username: scope.showConfig.username
                  };
                  
                  // Add the upload data to the file store. It is important that we don't initiate the XMLHttpRequest
                  // send operation before the YUI DataTable has finished rendering because if the file being uploaded
                  // is small and the network is quick we could receive the progress/completion events before we're
                  // ready to handle them.
                  scope.fileStore[fileId] =
                  {
                     state: scope.STATE_ADDED,
                     fileName: fileName,
                     nodeRef: updateNodeRef,
                     uploadData: uploadData,
                     request: request
                  };

                  // Add file to file table
                  scope.dataTable.addRow(data);
                  scope.addedFiles[uniqueFileToken] = scope._getUniqueFileToken(data);

                  // Enable the Esc key listener
                  scope.widgets.escapeListener.enable();
                  scope.panel.setFirstLastFocusable();
                  scope.panel.show();
               }
               catch(exception)
               {
                  Alfresco.logger.error("DNDUpload_show: The following exception occurred processing a file to upload: ", exception);
               }
            }
            
            // If we've not hit the max, recurse info the function...
            scope._addFiles(i+1, max, scope);
         }
      },
      
      /**
       * Called from show when an upload complete event fires.
       *
       * @param fileInfo {object} An entry from the fileStore array contains the information about the file that has uploaded.
       * @method _processUploadCompletion
       * @private
       */
      _processUploadCompletion: function DND__processUploadCompletion(fileInfo)
      {
         if (fileInfo.request.status == "200")
         {
            var response = Alfresco.util.parseJSON(fileInfo.request.responseText);

            // update noderef and filename from response
            fileInfo.nodeRef = response.nodeRef;
            fileInfo.fileName = response.fileName;
            fileInfo.state = this.STATE_SUCCESS;

            // Add the label "Successful" after the filename, updating the fileName from the response
            Dom.addClass(fileInfo.progressStatusIncomplete, "hidden");
            Dom.removeClass(fileInfo.progressStatusComplete, "hidden");

            // Change the style of the progress bar
            Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
            Dom.addClass(fileInfo.progress, "fileupload-progressFinished-span");

            // Move the progress bar to "full" progress
            Dom.setStyle(fileInfo.progress, "left", 0 + "px");
            fileInfo.progressPercentage.innerHTML = "100%";
            this.noOfSuccessfulUploads++;
            this._updateAggregateProgress(fileInfo, fileInfo.uploadData.filedata.size);

            // Adjust the rest of the gui
            this._updateStatus();
            this._adjustGuiIfFinished();

            // Upload remaining files
            this._spawnUploads();
         }
         else
         {
            // Process the upload failure...
            this._processUploadFailure(fileInfo, fileInfo.request.status);
         }
      },

      /**
       * Called from show if an XMLHttpRequest.send() operation fails or completes but returns an HTTP status code of anything
       * other than 200.
       *
       * @param fileInfo {object} An entry from the fileStore array contains the information about the file that has failed to upload.
       * @method _processUploadFailure
       * @private
       */
      _processUploadFailure: function DND__processUploadFailure(fileInfo, status)
      {
         if (status === 401)
         {
            var redirect = fileInfo.request.getResponseHeader["Location"];
            if (redirect)
            {
               window.location.href = window.location.protocol + "//" + window.location.host + redirect;
               return;
            }
            else
            {
               window.location.reload(true);
               return;
            }
         }

         fileInfo.state = this.STATE_FAILURE;
         var errormsg = fileInfo.request.status+" "+fileInfo.request.statusText; //default
         
         try
         {
             errormsg = JSON.parse(fileInfo.request.responseText).message;
             errormsg = errormsg.substring(errormsg.indexOf(" ") + 1);
         }
         catch(exception)
         {
            Alfresco.logger.error("The following error occurred parsing the upload failure message for "+errormsg+": " + exception);
         }

         // Add the failure label to the filename & and as a title attribute
         var key = "label.failure." + status,
             msg = Alfresco.util.message(key, this.name);

         if (msg == key)
         {
            msg = Alfresco.util.message("label.failure", this.name, errormsg);
         }
         fileInfo.fileSizeInfo["innerHTML"] = fileInfo.fileSizeInfo["innerHTML"] + " (" + msg + ")";
         fileInfo.fileSizeInfo.setAttribute("title", msg);
         fileInfo.progressInfo.setAttribute("title", msg);
         fileInfo.progressInfo.parentElement.setAttribute("title", msg);

         // Hide the incomplete image and show the failed image...
         Dom.addClass(fileInfo.progressStatusIncomplete, "hidden");
         Dom.removeClass(fileInfo.progressStatusFailed, "hidden");
         
         // Change the style of the progress bar
         Dom.removeClass(fileInfo.progress, "fileupload-progressSuccess-span");
         Dom.addClass(fileInfo.progress, "fileupload-progressFailure-span");

         // Set the progress bar to "full" progress
         Dom.setStyle(fileInfo.progress, "left", 0 + "px");
         this._updateAggregateProgress(fileInfo, fileInfo.uploadData.filedata.size);

         // Adjust the rest of the gui
         this.noOfFailedUploads++;
         this._updateStatus();
         this._adjustGuiIfFinished();

         // Upload remaining files
         this._spawnUploads();
      },

      /**
       * Called whenever file upload progress notification is received. This calculates the overall
       * upload performed and sets the progress span accordingly.
       *
       * @method _updateAggregateProgress
       * @private
       */
      _updateAggregateProgress: function DNDUpload__updateAggregateProgress(fileInfo, loaded)
      {
         // Deduct the last loaded about from the overall loaded value, then add the full
         // file size. This can then be used to calculate the overall progress and set the
         // style of the progress bar...
         this.aggregateUploadCurrentSize -= fileInfo.lastProgress;
         this.aggregateUploadCurrentSize += loaded;
         var overallProgress = (this.aggregateUploadCurrentSize / this.aggregateUploadTargetSize);
         var overallLeft = (-620 + (overallProgress * 620));
         Dom.setStyle(this.id + "-aggregate-progress-span", "left", overallLeft + "px");
      },

      /**
       * Reset GUI to start state
       *
       * @method _resetGUI
       * @private
       */
      _resetGUI: function DNDUpload__resetGUI()
      {
         if (this.statusText == null)
         {
            this.onReady();
         }
         
         // Reset references and the gui before showing it
         this.state = this.STATE_UPLOADING; // We're going to start uploading as soon as the dialog is shown
         this.noOfFailedUploads = 0;
         this.noOfSuccessfulUploads = 0;
         this.statusText.innerHTML = "&nbsp;";
         this.description.value = "";
         this.minorVersion.checked = true;
         this.widgets.cancelOkButton.set("label", this.msg("button.cancel"));
         this.widgets.cancelOkButton.set("disabled", false);
         Dom.addClass(this.widgets.uploadButton, "hidden");
         this.widgets.uploadButton.set("label", this.msg("button.upload"));
         this.widgets.uploadButton.set("disabled", true);
         this.aggregateUploadTargetSize = 0;
         this.aggregateUploadCurrentSize = 0;
         Dom.setStyle(this.id + "-aggregate-progress-span", "left", "-620px");
         Dom.removeClass(this.aggregateDataWrapper, "hidden");
      },

      /**
       * Fired by YUI:s DataTable when the added row has been rendered to the data table list.
       *
       * @method onPostRenderEvent
       */
      onPostRenderEvent: function DNDUpload_onPostRenderEvent(e)
      {
         // Display the upload button since all files are rendered
         if (this.dataTable.getRecordSet().getLength() > 0)
         {
            this.panel.setFirstLastFocusable();
            this.panel.focusFirst();
         }
      },

      /**
       * Fired by YUI:s DataTable when a row has been added to the data table list.
       * This retrieves the previously stored file information (which includes
       * prepared XMLHttpRequest and FormData objects) updates the row's ui.
       *
       * @method onRowAddEvent
       * @param event {object} a DataTable "rowAdd" event
       */
      onRowAddEvent: function FlashUpload_onRowAddEvent(event)
      {
         try
         {
            var data = event.record.getData();
            var fileInfo = this.fileStore[data.id];

            // Initialise the lastProgress attribute, this will be updated each time a progress
            // event is processed and will be used to calculate the overall progress of *all* uploads...
            fileInfo.lastProgress = 0;

            this._updateAggregateStatus();
         }
         catch(exception)
            {
            Alfresco.logger.error("The following error occurred initiating upload: " + exception);
         }
      },

      /**
       * Find file(s) to start upload for
       *
       * @method _spawnUploads
       */
      _spawnUploads: function ()
      {
         var length = this.dataTable.getRecordSet().getLength();
         for (var i = 0; i < length; i++)
         {
            var record = this.dataTable.getRecordSet().getRecord(i);
            var fileId = record.getData("id");
            var fileInfo = this.fileStore[fileId];
            if (fileInfo.state == this.STATE_ADDED)
            {
               // Start upload
               this._startUpload(fileInfo);

               // For now only allow 1 upload at a time
               return;
            }
         }
      },

      /**
       * Starts the actual upload for a file
       *
       * @method _startUpload
       * @param fileInfo {object} Contains info about the file and its request.
       */
      _startUpload: function (fileInfo)
      {
         // Mark file as being uploaded
         fileInfo.state = this.STATE_UPLOADING;

         var url;
         if (this.showConfig.uploadURL === null)
         {
            url = Alfresco.constants.PROXY_URI + "api/upload";
         }
         else
         {
            url = Alfresco.constants.PROXY_URI + this.showConfig.uploadURL;
         }
         if (Alfresco.util.CSRFPolicy.isFilterEnabled())
         {
            url += "?" + Alfresco.util.CSRFPolicy.getParameter() + "=" + encodeURIComponent(Alfresco.util.CSRFPolicy.getToken());
         }

         if (this.uploadMethod === this.FORMDATA_UPLOAD)
         {
            // For Browsers that support it (currently FireFox 4), the FormData object is the best
            // object to use for file upload as it supports asynchronous multipart upload without
            // the need to read the entire object into memory.
            Alfresco.logger.debug("Using FormData for file upload");
            var formData = new FormData;
            formData.append("filedata", fileInfo.uploadData.filedata);
            formData.append("filename", fileInfo.uploadData.filename);
            formData.append("destination", fileInfo.uploadData.destination);
            formData.append("uploaddirectory", fileInfo.uploadData.uploaddirectory);
            formData.append("majorVersion", fileInfo.uploadData.majorVersion ? "true" : "false");
            formData.append("username", fileInfo.uploadData.username);
            formData.append("overwrite", fileInfo.uploadData.overwrite);
            formData.append("thumbnails", fileInfo.uploadData.thumbnails);
            
            
            if (fileInfo.uploadData.updateNodeRef)
            {
               formData.append("updateNodeRef", fileInfo.uploadData.updateNodeRef);
            }
            else
            {
               formData.append("siteId", fileInfo.uploadData.siteId);
               formData.append("containerId", fileInfo.uploadData.containerId);
            }

            if (fileInfo.uploadData.description)
            {
               formData.append("description", fileInfo.uploadData.description);
            }
            fileInfo.request.open("POST",  url, true);
            fileInfo.request.send(formData);
            fileInfo.request.onreadystatechange = function() {
               if (this.status === 401)
               {
                  var redirect = this.getResponseHeader["Location"];
                  if (redirect)
                  {
                     window.location.href = window.location.protocol + "//" + window.location.host + redirect;
                     return;
                  }
                  else
                  {
                     window.location.reload(true);
                     return;
                  }
               }
            };
         }
         else if (this.uploadMethod === this.INMEMORY_UPLOAD)
         {
            Alfresco.logger.debug("Using custom multipart upload");

            // PLEASE NOTE: Be *VERY* careful modifying the following code, this carefully constructs a multipart formatted request...
            var multipartBoundary = "----AlfrescoCustomMultipartBoundary" + (new Date).getTime();
            var rn = "\r\n";
            var customFormData = "--" + multipartBoundary;

               // Add the file parameter...
               customFormData += rn + "Content-Disposition: form-data; name=\"filedata\"; filename=\"" + unescape(encodeURIComponent(fileInfo.uploadData.filename)) + "\"";
               customFormData += rn + "Content-Type: image/png";
               customFormData += rn + rn + fileInfo.uploadData.filedata.getAsBinary() + rn + "--" + multipartBoundary; // Use of getAsBinary should be fine here - in-memory upload is only used pre FF4

               // Add the String parameters...
               customFormData += rn + "Content-Disposition: form-data; name=\"filename\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.filename)) + rn + "--" + multipartBoundary;
               customFormData += rn + "Content-Disposition: form-data; name=\"destination\"";
               if (fileInfo.uploadData.destination !== null)
               {
                  customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.destination)) + rn + "--" + multipartBoundary;
               }
               else
               {
                  customFormData += rn + rn + rn + "--" + multipartBoundary;
               }
               customFormData += rn + "Content-Disposition: form-data; name=\"siteId\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.siteId)) + rn + "--" + multipartBoundary;
               customFormData += rn + "Content-Disposition: form-data; name=\"containerId\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.containerId)) + rn + "--" + multipartBoundary;
               customFormData += rn + "Content-Disposition: form-data; name=\"uploaddirectory\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.uploaddirectory)) + rn + "--" + multipartBoundary + "--";
            customFormData += rn + "Content-Disposition: form-data; name=\"majorVersion\"";
            customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.majorVersion)) + rn + "--" + multipartBoundary + "--";
            if (fileInfo.uploadData.updateNodeRef)
            {
               customFormData += rn + "Content-Disposition: form-data; name=\"updateNodeRef\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.updateNodeRef)) + rn + "--" + multipartBoundary + "--";
            }
            if (fileInfo.uploadData.description)
            {
               customFormData += rn + "Content-Disposition: form-data; name=\"description\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.description)) + rn + "--" + multipartBoundary + "--";
            }
            if (fileInfo.uploadData.username)
            {
               customFormData += rn + "Content-Disposition: form-data; name=\"username\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.username)) + rn + "--" + multipartBoundary + "--";
            }
            if (fileInfo.uploadData.overwrite)
            {
               customFormData += rn + "Content-Disposition: form-data; name=\"overwrite\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.overwrite)) + rn + "--" + multipartBoundary + "--";
            }
            if (fileInfo.uploadData.thumbnails)
            {
               customFormData += rn + "Content-Disposition: form-data; name=\"thumbnails\"";
               customFormData += rn + rn + unescape(encodeURIComponent(fileInfo.uploadData.thumbnails)) + rn + "--" + multipartBoundary + "--";
            }
            
            fileInfo.request.open("POST",  url, true);
            fileInfo.request.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + multipartBoundary);
            fileInfo.request.sendAsBinary(customFormData);
         }
      },

      /**
       * Fired when the user clicks the upload button.
       * Starts the uploading and adjusts the gui.
       *
       * @method onBrowseButtonClick
       * @param event {object} a Button "click" event
       */
      onUploadButtonClick: function DNDUpload_onUploadButtonClick()
      {
         this.state = this.STATE_UPLOADING;
         Dom.removeClass(this.id + "-filelist-table", "hidden");
         Dom.removeClass(this.id + "-aggregate-data-wrapper", "hidden");
         Dom.addClass(this.id + "-file-selection-controls", "hidden");
         Dom.addClass(this.versionSection, "hidden");
         this.widgets.uploadButton.set("disabled", true);
         this.processFilesForUpload(this.showConfig.files);
      },
      
      /**
       * Fired when the user clicks the cancel/ok button.
       * The action taken depends on what state the uploader is in.
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
      onCancelOkButtonClick: function DNDUpload_onCancelOkButtonClick()
      {
         var message, i;
         if (this.state === this.STATE_UPLOADING)
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
         this.panel.hide();

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
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function DNDUpload__applyConfig()
      {
         // Generate the title based on number of files and destination
         var title, i18n;
         if (this.showConfig.mode === this.MODE_SINGLE_UPLOAD)
         {
            i18n = this.msg("header.singleUpload");
            this.titleText.innerHTML = i18n;
         }
         else if (this.showConfig.mode === this.MODE_MULTI_UPLOAD)
         {
            i18n = this.showConfig.files.length == 1 ? "header.multiUpload.singleFile" : "header.multiUpload";
            var location = this.showConfig.uploadDirectoryName == "" ? this.msg("label.documents") : this.showConfig.uploadDirectoryName;
            this.titleText.innerHTML = this.msg(i18n, '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-open-16.png" class="title-folder" />' + $html(location));
         }
         else if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            i18n = this.msg("header.singleUpdate");
            this.titleText.innerHTML = i18n;
         }

         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
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

            // Make the file list long
            this.dataTable.set("height", "204px", true);
         }
         else
         {
            // Hide the upload status label, only interesting for multiple files
            Dom.addClass(this.statusText, "hidden");

            // Make the file list short
            this.dataTable.set("height", "40px");
         }
      },

      /**
       * Helper function to create the data table and its cell formatter.
       *
       * @method _createEmptyDataTable
       * @private
       */
      _createEmptyDataTable: function DNDUpload__createEmptyDataTable()
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
            try
            {
               myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.left);
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload__createEmptyDataTable (formatLeftCell): The following error occurred: ", exception);
            }
         };

         /**
          * Responsible for rendering the center row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatCenterCell = function(el, oRecord, oColumn, oData)
         {
            try
            {
               myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.center);
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload__createEmptyDataTable (formatCenterCell): The following error occurred: ", exception);
            }
         };

         /**
          * Responsible for rendering the right row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         var formatRightCell = function(el, oRecord, oColumn, oData)
         {
            try
            {
               myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.right);
            }
            catch(exception)
            {
               Alfresco.logger.error("DNDUpload__createEmptyDataTable (formatRightCell): The following error occurred: ", exception);
            }
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
                fileId = record.id;

            // create an instance from the template and give it a uniqueue id.
            var cell = new Element(el);
            var templateInstance = template.cloneNode(true);
            templateInstance.setAttribute("id", templateInstance.getAttribute("id") + fileId);

            // Save references to elements that will be updated during upload.
            var progress = Dom.getElementsByClassName("fileupload-progressSuccess-span", "span", templateInstance);
            if (progress.length == 1)
            {
               this.fileStore[fileId].progress = progress[0];
            }
            var progressInfo = Dom.getElementsByClassName("fileupload-progressInfo-span", "span", templateInstance);
            if (progressInfo.length == 1)
            {
               // Display the file size in human readable format after the filename.
               var fileInfoStr = record.name;// + " (" + Alfresco.util.formatFileSize(record.size) + ")";
               templateInstance.setAttribute("title", fileInfoStr);

               // Display the file name and size.
               progressInfo = progressInfo[0];
               this.fileStore[fileId].progressInfo = progressInfo;
               this.fileStore[fileId].progressInfo.innerHTML = fileInfoStr;

               // Save the cell element
               this.fileStore[fileId].progressInfoCell = el;
            }

            var fileSize = Dom.getElementsByClassName("fileupload-filesize-span", "span", templateInstance);
            if (fileSize.length == 1)
            {
               // Display the file size in human readable format below the filename.
               var fileInfoStr = Alfresco.util.formatFileSize(record.size);
               fileSize = fileSize[0];
               this.fileStore[fileId].fileSizeInfo = fileSize;
               fileSize.innerHTML = fileInfoStr;
            }

            // Save a reference to the contentType dropdown so we can find each file's contentType before upload.
            var contentType = Dom.getElementsByClassName("fileupload-contentType-select", "select", templateInstance);
            if (contentType.length == 1)
            {
               this.fileStore[fileId].contentType = contentType[0];
            }
            else
            {
               contentType = Dom.getElementsByClassName("fileupload-contentType-input", "input", templateInstance);
               if (contentType.length == 1)
               {
                  this.fileStore[fileId].contentType = contentType[0];
               }
            }

            // Save references to elements that will be updated during upload.
            var progressPercentage = Dom.getElementsByClassName("fileupload-percentage-span", "span", templateInstance);
            if (progressPercentage.length == 1)
            {
               this.fileStore[fileId].progressPercentage = progressPercentage[0];
            }

            var progressStatus = Dom.getElementsByClassName("fileupload-status-img", "img", templateInstance);
            if (progressStatus.length == 3)
            {
               this.fileStore[fileId].progressStatusIncomplete = progressStatus[0];
               this.fileStore[fileId].progressStatusComplete = progressStatus[1];
               this.fileStore[fileId].progressStatusFailed = progressStatus[2];
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
         this.dataTable = new YAHOO.widget.DataTable(dataTableDiv, myColumnDefs, myDataSource,
         {
            scrollable: true,
            height: "100px", // must be set to something so it can be changed afterwards, when the showconfig options decides if its a sinlge or multi upload
            width: "620px",
            renderLoopSize: 1,
            MSG_EMPTY: this.msg("label.noFiles")
         });
         this.dataTable.subscribe("postRenderEvent", this.onPostRenderEvent, this, true);
         this.dataTable.subscribe("rowAddEvent", this.onRowAddEvent, this, true);
      },

      /**
       * Helper function to create a unique file token from the file data object
       *
       * @method _getUniqueFileToken
       * @param data {object} a file data object describing a file
       * @private
       */
      _getUniqueFileToken: function DNDUpload__getUniqueFileToken(data)
      {
         return data.name + ":" + data.size;
      },

      /**
       * Update the status label with the latest information about the upload progress
       *
       * @method _updateStatus
       * @private
       */
      _updateStatus: function DNDUpload__updateStatus()
      {
         if (this.noOfFailedUploads > 0)
         {
            this.statusText.innerHTML = YAHOO.lang.substitute(this.msg("label.uploadStatus.withFailures"),
            {
               "0" : this.noOfSuccessfulUploads,
               "1" : this.dataTable.getRecordSet().getLength(),
               "2" : this.noOfFailedUploads
            });
         }
         else
         {
            this.statusText.innerHTML = YAHOO.lang.substitute(this.msg("label.uploadStatus"),
            {
               "0" : this.noOfSuccessfulUploads,
               "1" : this.dataTable.getRecordSet().getLength()
            });
         }
      },

      /**
       * Update the aggregate status label with the latest information about the overall
       * upload progress
       *
       * @method _updateAggregateStatus
       * @private
       */
      _updateAggregateStatus: function DNDUpload__updateAggregateStatus()
      {
         this.aggregateProgressText.innerHTML = YAHOO.lang.substitute(this.msg("label.aggregateUploadStatus"),
         {
            "0" : this.dataTable.getRecordSet().getLength(),
            "1" : Alfresco.util.formatFileSize(this.aggregateUploadTargetSize)
         });
      },

      /**
       * Checks if all files are finished (successfully uploaded or failed)
       * and if so adjusts the gui.
       *
       * @method _adjustGuiIfFinished
       * @private
       */
      _adjustGuiIfFinished: function DNDUpload__adjustGuiIfFinished()
      {
         try
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
                        nodeRef: file.nodeRef
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
            Dom.addClass(this.aggregateDataWrapper, "hidden");
            Dom.addClass(this.widgets.uploadButton, "hidden");
            this.widgets.cancelOkButton.set("label", this.msg("button.ok"));
            this.widgets.cancelOkButton.focus();

            var callback = this.showConfig.onFileUploadComplete;
            if (callback && typeof callback.fn == "function")
            {
               // Call the onFileUploadComplete callback in the correct scope
               callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), objComplete, callback.obj);
            }
            
            if (objComplete.failed.length === 0)
            {
               this.onCancelOkButtonClick();
            }
         }
         catch(exception)
         {
            Alfresco.logger.error("_adjustGuiIfFinished", exception);
         }
      },

      /**
       * Cancels all uploads inside the flash movie.
       *
       * @method _cancelAllUploads
       * @private
       */
      _cancelAllUploads: function DNDUpload__cancelAllUploads()
      {
         // Cancel all uploads inside the flash movie
         var length = this.dataTable.getRecordSet().getLength();
         for (var i = 0; i < length; i++)
         {
            var record = this.dataTable.getRecordSet().getRecord(i);
            var fileId = record.getData("id");

            var fileInfo = this.fileStore[fileId];
            if (fileInfo.state === this.STATE_UPLOADING)
            {
                // We will only attempt an upload abort if the file is still being uploaded (there is
                // no point in aborting if the file has completed or failed)
                Alfresco.logger.debug("Aborting upload of file: " + fileInfo.fileName);
                fileInfo.request.abort();
            }
         }
      },

      /**
       * Remove all references to files inside the data table, flash movie
       * and the this class references.
        *
       * @method _clear
       * @private
       */
      _clear: function DNDUpload__clear()
      {
         /**
          * Remove all references to files inside the data table, flash movie
          * and this class's references.
          */
         var length = this.dataTable.getRecordSet().getLength();
         this.addedFiles = {};
         this.fileStore = {};
         this.dataTable.deleteRows(0, length);
      }
   });
})();
