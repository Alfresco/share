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
 * FileUpload component.
 *
 * Checks if Flash is installed or not and uses either the FlashUpload or
 * HtmlUpload component.
 *
 * A multi file upload scenario could look like:
 *
 * var fileUpload = Alfresco.getFileUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: fileUpload.MODE_MULTI_UPLOAD,
 * }
 * this.fileUpload.show(multiUploadConfig);
 *
 * If flash is installed it would use the FlashUpload component in multi upload mode
 * If flash isn't installed it would use the HtmlUpload in single upload mode instead.
 *
 * @namespace Alfresco.component
 * @class Alfresco.FileUpload
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * FileUpload constructor.
    *
    * FileUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.getFileUploadInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.FileUpload} The new FileUpload instance
    * @constructor
    * @private
    */
   Alfresco.FileUpload = function(instanceId)
   {
      var instance = Alfresco.util.ComponentManager.get(instanceId);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.FileUpload already exists.");
      }

      Alfresco.FileUpload.superclass.constructor.call(this, "Alfresco.FileUpload", instanceId);

      return this;
   };

   YAHOO.extend(Alfresco.FileUpload, Alfresco.component.Base,
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
          * Adobe Flash enable/disable flag (overrides client-side detection)
          * 
          * @property adobeFlashEnabled
          * @type boolean
          * @default true
          */
         adobeFlashEnabled: false,

         /**
          * Class name of Flash Uploader
          *
          * @property flashUploader
          * @type string
          * @default "Alfresco.FlashUpload"
          */
         flashUploader: "Alfresco.FlashUpload",

         /**
          * Class name of HTML Uploader
          *
          * @property flashUploader
          * @type string
          * @default "Alfresco.HtmlUpload"
          */
         htmlUploader: "Alfresco.HtmlUpload",
         
         /**
          * Class name of Drag-and-drop Uploader - also supports HTML5 file selection upload
          * 
          * @property dndUploader
          * @type string
          * @default "Alfresco.DNDUpload"
          */
         dndUploader: "Alfresco.DNDUpload"
      },
      
      /**
       * The uploader instance
       *
       * @property uploader
       * @type Alfresco.FlashUpload or Alfresco.HtmlUpload
       */
      uploader: null,

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
      defaultShowConfig:
      {
         siteId: null,
         containerId: null,
         destination: null,
         uploadDirectory: null,
         updateNodeRef: null,
         updateFilename: null,
         mode: this.MODE_SINGLE_UPLOAD,
         filter: [],
         onFileUploadComplete: null,
         overwrite: false,
         thumbnails: null,
         htmlUploadURL: null,
         flashUploadURL: null,
         username: null
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property defaultShowConfig
       * @type object
       */
      showConfig: {},

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
       *    htmlUploadURL: null,     // Overrides default url to post the file to if the html version is used
       *    flashUploadURL: null,    // Overrides default url to post the files to if the flash version is used
       *    username: null           // If a file should be associated with a user
       * }
       */
      show: function FU_show(config)
      {
         // Only create a new instance the first time or if the user changed his mind using flash.
         if (this.uploader === null || (this.uploader.name == this.options.flashUploader && !this.options.adobeFlashEnabled))
         {
            // Determine minimum required Flash capability
            this.hasRequiredFlashPlayer = this.options.adobeFlashEnabled && !Alfresco.util.getVar("noflash") && Alfresco.util.hasRequiredFlashPlayer(9, 0, 45);

            /**
             * Due to a Flash Player bug (https://bugs.adobe.com/jira/browse/FP-1044) only IE browsers
             * pick up the session from the browser, therefore the flash uploader is passed the session id
             * using javascript when instantiated so uploads can pass authenticatication details in all browsers.
             * If the server has been configured to use "httponly" cookies it will not be possible to access the
             * jsessionid using javascript and we must therefore fallback to the normal uploader for all non IE browsers.
             */
            if (this.hasRequiredFlashPlayer && YAHOO.env.ua.ie == 0)
            {
               this.canAccessSession = (YAHOO.util.Cookie.get("JSESSIONID") || "").length > 0;
               this.hasRequiredFlashPlayer = this.canAccessSession;
            }

            // Check to see whether the browser supports the HTML5 file upload API...
            this.browserSupportsHTML5 = (window.File && window.FileList);
            
            // Create the appropriate uploader component
            var uploadType;
            if (this.browserSupportsHTML5)
            {
               uploadType = this.options.dndUploader;
            }
            else if (this.hasRequiredFlashPlayer)
            {
               uploadType = this.options.flashUploader;
            }
            else
            {
               uploadType = this.options.htmlUploader;
            }
            var uploadInstance = Alfresco.util.ComponentManager.findFirst(uploadType);
            
            
            if (uploadInstance)
            {
               this.uploader = uploadInstance;
            }
            else
            {
               throw new Error("No instance of uploader type '" + uploadType + "' exists.");
            }
         }

         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);

         // If flash isn't installed multi upload mode isn't supported
         if (!this.hasRequiredFlashPlayer && this.showConfig.mode == this.MODE_MULTI_UPLOAD)
         {
            if (this.browserSupportsHTML5)
            {
               // The browser supports HTML5 upload - so we're going to use that instead of Flash
            }
            else 
            {
               this.showConfig.mode = this.MODE_SINGLE_UPLOAD;
            }
         }

         if (this.hasRequiredFlashPlayer || this.browserSupportsHTML5)
         {
            this.showConfig.uploadURL = this.showConfig.flashUploadURL;
         }
         else
         {
            this.showConfig.uploadURL = this.showConfig.htmlUploadURL;
            this.showConfig.adobeFlashEnabled = this.options.adobeFlashEnabled && this.canAccessSession;
         }

         // MNT-11084 Full screen/window view: Actions works incorrectly;
         if (this.options.zIndex !== undefined && this.options.zIndex > 0 && (this.uploader.widgets.panel !== undefined || this.uploader.panel !== undefined))
         {
            var uploader = this.uploader.widgets.panel;
            if (this.uploader.panel !== undefined) 
            {
               var uploader = this.uploader.panel;
            }
            var index = this.options.zIndex + 2;
            var onBeforeShow = function () 
            {
               elements = Dom.getElementsByClassName("mask");
               if (elements.length > 0)
               {
                  Dom.setStyle(elements[0], "zIndex", index - 1);
               }

               Dom.setStyle(uploader.element, "zIndex", index);
               uploader.cfg.setProperty("zIndex", index, true);
            }
            uploader.beforeShowEvent.subscribe(onBeforeShow, uploader, true);
         }

         // Let the uploader instance show itself
         this.uploader.show(this.showConfig);
      },
      
      hide: function FU_hide()
      {
         if (this.uploader === null)
         {
            // If the uploader doesn't exist then there's nothing to hide!
         }
         else
         {
            this.uploader.hide();
         }
      }
   });
})();

Alfresco.getFileUploadInstance = function()
{
   var instanceId = "alfresco-fileupload-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.FileUpload(instanceId);
};

Alfresco.getDNDUploadProgressInstance = function()
{
  var instanceId = "Alfresco.DNDUpload";
  var instance = Alfresco.util.ComponentManager.findFirst(instanceId);
  return instance;
};