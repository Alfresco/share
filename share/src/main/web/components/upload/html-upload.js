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
 * HtmlUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var htmlUpload = Alfresco.getHtmlUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: htmlUpload.MODE_MULTI_UPLOAD,
 * }
 * this.htmlUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco
 * @class Alfresco.HtmlUpload
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * HtmlUpload constructor.
    *
    * HtmlUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.getHtmlUploadInstance() instead.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.HtmlUpload} The new HtmlUpload instance
    * @constructor
    * @private
    */
   Alfresco.HtmlUpload = function(htmlId)
   {
      Alfresco.HtmlUpload.superclass.constructor.call(this, "Alfresco.HtmlUpload", htmlId, ["button", "container"]); 

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
         onFileUploadComplete: null,
         overwrite: false,
         thumbnails: null,
         uploadURL: null,
         username: null,
         suppressRefreshEvent: false,
         adobeFlashEnabled: true,
         maximumFileSize: 0
      };
      this.showConfig = {};

      return this;
   };

   YAHOO.extend(Alfresco.HtmlUpload, Alfresco.component.Base,
   {
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
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single & multi uploads and single updates.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig: null,

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property defaultShowConfig
       * @type object
       */
      showConfig: null,

      /**
       * HTMLElement of type div that displays the version input form.
       *
       * @property versionSection
       * @type HTMLElement
       */
      versionSection: null,

      /**
       * The name of the currently selected file (not including its full path).
       *  
       * @property _fileName
       * @type String
       */
      _fileName: null,

      /**
       * The size of the currently selected file. This will be null if a file has not been selected.
       *
       * @property _fileSize
       * @type number
       */
      _fileSize: null,

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
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function HtmlUpload_onReady()
      {
         var _this = this;
         
         Dom.removeClass(this.id + "-dialog", "hidden");

         // Create the panel
         this.widgets.panel = Alfresco.util.createYUIPanel(this.id + "-dialog");

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.titleText = Dom.get(this.id + "-title-span");
         this.widgets.singleUploadTip = Dom.get(this.id + "-singleUploadTip-span");
         this.widgets.singleUpdateTip = Dom.get(this.id + "-singleUpdateTip-span");

         // Save references to hidden fields so we can set them later
         this.widgets.filedata = Dom.get(this.id + "-filedata-file");
         this.widgets.filedata.contentEditable = false;
         this.widgets.siteId = Dom.get(this.id + "-siteId-hidden");
         this.widgets.containerId = Dom.get(this.id + "-containerId-hidden");
         this.widgets.destination = Dom.get(this.id + "-destination-hidden");
         this.widgets.username = Dom.get(this.id + "-username-hidden");
         this.widgets.updateNodeRef = Dom.get(this.id + "-updateNodeRef-hidden");
         this.widgets.uploadDirectory = Dom.get(this.id + "-uploadDirectory-hidden");
         this.widgets.overwrite = Dom.get(this.id + "-overwrite-hidden");
         this.widgets.thumbnails = Dom.get(this.id + "-thumbnails-hidden");

         // Save reference to version section elements so we can set its values later
         this.widgets.description = Dom.get(this.id + "-description-textarea");
         this.widgets.minorVersion = Dom.get(this.id + "-minorVersion-radioButton");
         this.widgets.versionSection = Dom.get(this.id + "-versionSection-div");

         // Create and save a reference to the buttons so we can alter them later
         this.widgets.uploadButton = Alfresco.util.createYUIButton(this, "upload-button", null,
         {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
         
         // Configure the forms runtime
         var form = new Alfresco.forms.Form(this.id + "-htmlupload-form");
         this.widgets.form = form;

         // Make sure we listen to the change event so we can store details about the file (name & size) and use it in validations below
         YAHOO.util.Event.addListener(this.id + "-filedata-file", "change", function()
         {
            if (this.files && this.files.length > 0)
            {
               _this._fileName = this.files[0].name;
               _this._fileSize = this.files[0].size;
            }
         });

         // Title is mandatory (will look for the inout elements value)
         form.addValidation(this.id + "-filedata-file", Alfresco.forms.validation.mandatory, null, "change", this.msg("Alfresco.forms.validation.mandatory.message"));

         // Internet Explorer does not support the "files" attribute for the input type file
         // so there is no point in adding validations
         if (YAHOO.env.ua.ie == 0)
         {
            // File name must work on all OS:s
            form.addValidation(this.id + "-filedata-file", function HtmlUpload_validateFileName(field, args)
            {
               return !YAHOO.lang.isString(_this._fileName) || Alfresco.forms.validation.nodeName({ id: field.id, value: _this._fileName }, args);
            }, null, "change", this.msg("message.illegalCharacters"));

            // Make sure file doesn't exceed maximum file size
            form.addValidation(this.id + "-filedata-file", function HtmlUpload_validateMaximumFileSize(field, args)
            {
               return args.maximumFileSizeLimit == 0 || !YAHOO.lang.isNumber(_this._fileSize) || _this._fileSize <= args.maximumFileSizeLimit;
            }, { maximumFileSizeLimit: this._maximumFileSizeLimit }, "change", this.msg("message.maxFileFileSizeExceeded"));

            // Make sure file isn't empty
            form.addValidation(this.id + "-filedata-file", function HtmlUpload_validateSize(field, args)
            {
               return !YAHOO.lang.isNumber(_this._fileSize) || _this._fileSize > 0;
            }, null, "change", this.msg("message.zeroByteFileSelected"));
         }

         // The ok button is the submit button, and it should be enabled when the form is ready
         form.setSubmitElements(this.widgets.uploadButton);
         form.doBeforeFormSubmit =
         {
            fn: function()
            {
               this.widgets.cancelButton.set("disabled", true);
               this.widgets.panel.hide();
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.uploading", this.name),
                  spanClass: "wait",
                  displayTime: 0,
                  effect: null
               });
            },
            obj: null,
            scope: this
         };

         // Submit as an ajax submit (not leave the page), in json format
         form.setAJAXSubmit(true, {});

         // We're in a popup, so need the tabbing fix
         form.applyTabFix();
         form.init();

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

         this.widgets.enterListener = new KeyListener(this.widgets.filedata,
         {
            keys: KeyListener.KEY.ENTER
         },
         {
            fn: function enter_key_pressed(obj)
            {
               this.widgets.filedata.click();
            },
            scope: this,
            correctScope: true
         });
         this.widgets.enterListener.enable();
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
       *    mode: {int},             // MODE_SINGLE_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},         // limits what kind of files the user can select in the OS file selector
       *    onFileUploadComplete: null, // Callback after upload
       *    overwrite: false         // If true and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to overwrite a versionable file with the existing name
       *                             // If false and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to append a number to the versionable filename to avoid
       *                             // an overwrite and a new version
       * }
       */
      show: function HtmlUpload_show(config)
      {
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.uploadDirectory === undefined && this.showConfig.updateNodeRef === undefined)
         {
             throw new Error("An updateNodeRef OR uploadDirectory must be provided");
         }
         if (this.showConfig.uploadDirectory !== null && this.showConfig.uploadDirectory.length === 0)
         {
            this.showConfig.uploadDirectory = "/";
         }
         
         // Enable the Esc key listener
         this.widgets.escapeListener.enable();

         this._showPanel();
      },
      
      /**
       * Hides the panel
       * 
       * @method hide
       */
      hide: function HtmlUpload_hide()
      {
         this.onCancelButtonClick();
      },
      
      /**
       * Called when a file has been successfully uploaded
       * Informs the user and reloads the doclib.
       *
       * @method onUploadSuccess
       */
      onUploadSuccess: function HtmlUpload_onUploadSuccess(response)
      {
         // Hide the current message display
         this.widgets.feedbackMessage.hide();

         // Tell the document list to refresh itself if present
         var fileName = response.fileName ? response.fileName : this.widgets.filedata.value;
         if (!this.showConfig.suppressRefreshEvent)
         {
            YAHOO.Bubbling.fire("metadataRefresh",
            {
               currentPath: this.showConfig.path,
               highlightFile: fileName
            });
         }
              
         // Todo see if the nodeRef can be added to the list
         var objComplete =
         {
            successful: [
            {
               nodeRef: response.nodeRef,
               fileName: fileName,
               response: response  // Added for CSV upload
            }]
         };

         var callback = this.showConfig.onFileUploadComplete;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onFileUploadComplete callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), objComplete, callback.obj);
         }
      },

      /**
       * Called when a file failed to be uploaded
       * Informs the user.
       *
       * @method onUploadFailure
       */
      onUploadFailure: function HtmlUpload_onUploadFailure(e)
      {
         // Hide the current message display
         this.widgets.feedbackMessage.hide();

         // Inform user that the upload failed
         var key = "message.failure." + e.status.code,
            text = Alfresco.util.message(key, this.name);
         if (text == key)
         {
            text = e.status.code ? e.status.code : Alfresco.util.message("message.failure", this.name);
         }
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: Alfresco.util.message("message.failure", this.name),
            text: text
         });
      },

      /**
       * Fired when the user clicks the cancel button.
       * Closes the panel.
       *
       * @method onCancelButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelButtonClick: function HtmlUpload_onCancelButtonClick()
      {
         // Disable the Esc key listener
         this.widgets.escapeListener.disable();

         // Hide the panel
         this.widgets.panel.hide();
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function HtmlUpload__applyConfig()
      {
         // Set the panel title
         var title;
         if (this.showConfig.mode === this.MODE_SINGLE_UPLOAD)
         {
            title = Alfresco.util.message("header.singleUpload", this.name);
         }
         else if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            title = Alfresco.util.message("header.singleUpdate", this.name);
         }
         this.widgets.titleText.innerHTML = title;

         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            var tip = Alfresco.util.message("label.singleUpdateTip", this.name,
            {
               "0": this.showConfig.updateFilename
            });
            this.widgets.singleUpdateTip.innerHTML = tip;

            // Display the version input form
            Dom.removeClass(this.widgets.versionSection, "hidden");
            var versions = (this.showConfig.updateVersion || "1.0").split("."),
               majorVersion = parseInt(versions[0], 10),
               minorVersion = parseInt(versions[1], 10);
            Dom.get(this.id + "-minorVersion").innerHTML = this.msg("label.minorVersion.more", majorVersion + "." + (1 + minorVersion));
            Dom.get(this.id + "-majorVersion").innerHTML = this.msg("label.majorVersion.more", (1 + majorVersion) + ".0");
         }
         else
         {
            // Hide the version input form
            Dom.addClass(this.widgets.versionSection, "hidden");
         }

         // Show the help label for single updates
         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            // Show the help label for single updates
            Dom.removeClass(this.widgets.singleUpdateTip, "hidden");
            Dom.addClass(this.widgets.singleUploadTip, "hidden");
         }
         else
         {
            // Only show the "Install Flash" message if Flash is enabled via config
            Dom.addClass(this.widgets.singleUploadTip, "hidden");
            if (this.showConfig.adobeFlashEnabled)
            {
               // Show the help label for single uploads
               Dom.removeClass(this.widgets.singleUploadTip, "hidden");
            }
            Dom.addClass(this.widgets.singleUpdateTip, "hidden");
         }

         this.widgets.cancelButton.set("disabled", false);
         this.widgets.filedata.value = null;

         // Set the forms action url
         var formEl = Dom.get(this.id + "-htmlupload-form");
         if (this.showConfig.uploadURL === null)
         {
            // The .html suffix is required - it is not possible to do a multipart post using an ajax call.
            // So it has to be a FORM submit, to make it feel like an ajax call a a hidden iframe is used.
            // Since the component still needs to be called when the upload is finished, the script returns
            // an html template with SCRIPT tags inside that which calls the component that triggered it.
            formEl.action = Alfresco.constants.PROXY_URI + "api/upload.html";
         }
         else
         {
            formEl.action = Alfresco.constants.PROXY_URI + this.showConfig.uploadURL;
         }

         // Set the hidden parameters
         this.widgets.siteId.value = this.showConfig.siteId;
         this.widgets.containerId.value = this.showConfig.containerId;
         this.widgets.destination.value = this.showConfig.destination;
         this.widgets.username.value = this.showConfig.username;
         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            this.widgets.updateNodeRef.value = this.showConfig.updateNodeRef;
            this.widgets.uploadDirectory.value = "";
            this.widgets.overwrite.value = "";
            this.widgets.thumbnails.value = "";
         }
         else
         {
            this.widgets.updateNodeRef.value = "";
            this.widgets.uploadDirectory.value = this.showConfig.uploadDirectory;
            this.widgets.overwrite.value = this.showConfig.overwrite;
            this.widgets.thumbnails.value = this.showConfig.thumbnails;
         }
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function HtmlUpload__showPanel()
      {
         // Reset references and the gui before showing it
         this.widgets.description.value = "";
         this.widgets.minorVersion.checked = true;

         // Apply the config before it is showed
         this._applyConfig();

         // Show the upload panel
         this.widgets.panel.show();
      }
   });
})();