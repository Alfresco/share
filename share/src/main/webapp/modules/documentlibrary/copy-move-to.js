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
 * Document Library "Copy- and Move-To" module for Document Library.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibCopyMoveTo
 */
(function()
{
   Alfresco.module.DoclibCopyMoveTo = function(htmlId)
   {
      Alfresco.module.DoclibCopyMoveTo.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.module.DoclibCopyMoveTo";
      var DLGF = Alfresco.module.DoclibGlobalFolder;

      Alfresco.util.ComponentManager.reregister(this);

      this.options = YAHOO.lang.merge(this.options,
      {
         allowedViewModes:
         [
            DLGF.VIEW_MODE_SITE,
            DLGF.VIEW_MODE_RECENT_SITES,
            DLGF.VIEW_MODE_FAVOURITE_SITES,
            DLGF.VIEW_MODE_SHARED,
            DLGF.VIEW_MODE_REPOSITORY,
            DLGF.VIEW_MODE_USERHOME
         ],
         extendedTemplateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/copy-move-to"
      });

      return this;
   };

   YAHOO.extend(Alfresco.module.DoclibCopyMoveTo, Alfresco.module.DoclibGlobalFolder,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.DoclibMoveTo} returns 'this' for method chaining
       */
      setOptions: function DLCMT_setOptions(obj)
      {
         var myOptions = {};

         if (typeof obj.mode !== "undefined")
         {
            var dataWebScripts =
            {
               copy: "copy-to",
               move: "move-to"
            };
            if (typeof dataWebScripts[obj.mode] == "undefined")
            {
               throw new Error("Alfresco.module.CopyMoveTo: Invalid mode '" + obj.mode + "'");
            }
            myOptions.dataWebScript = dataWebScripts[obj.mode];
         }

         myOptions.viewMode = Alfresco.module.DoclibGlobalFolder.VIEW_MODE_RECENT_SITES; // Always default to recent sites view.
         // Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();

         return Alfresco.module.DoclibCopyMoveTo.superclass.setOptions.call(this, YAHOO.lang.merge(myOptions, obj));
      },

      /**
       * Event callback when superclass' dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLCMT_onTemplateLoaded(response)
      {
         // Load the UI template, which only will bring in new i18n-messages, from the server
         Alfresco.util.Ajax.request(
         {
            url: this.options.extendedTemplateUrl,
            dataObj:
            {
               htmlid: this.id
            },
            successCallback:
            {
               fn: this.onExtendedTemplateLoaded,
               obj: response,
               scope: this
            },
            failureMessage: "Could not load 'copy-move-to' template:" + this.options.extendedTemplateUrl,
            execScripts: true
         });
      },

      /**
       * Event callback when this class' template has been loaded
       *
       * @method onExtendedTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onExtendedTemplateLoaded: function DLCMT_onExtendedTemplateLoaded(response, superClassResponse)
      {
         // Now that we have loaded this components i18n messages let the original template get rendered.
         Alfresco.module.DoclibCopyMoveTo.superclass.onTemplateLoaded.call(this, superClassResponse);
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function DLCMT_onOK(e, p_obj)
      {
         var files, multipleFiles = [], params, i, j,
            eventSuffix =
            {
               copy: "Copied",
               move: "Moved"
            };

         // Single/multi files into array of nodeRefs
         if (YAHOO.lang.isArray(this.options.files))
         {
            files = this.options.files;
         }
         else
         {
            files = [this.options.files];
         }
         for (i = 0, j = files.length; i < j; i++)
         {
            multipleFiles.push(files[i].node.nodeRef);
         }

         // Success callback function
         var fnSuccess = function DLCMT__onOK_success(p_data)
         {
            var result,
               successCount = p_data.json.successCount,
               failureCount = p_data.json.failureCount;

            this.widgets.dialog.hide();

            // Did the operation succeed?
            if (!p_data.json.overallSuccess)
            {
               //MNT-7514 Uninformational error message on move when file name conflicts
               var message = "message.failure";
               for (var i = 0, j = p_data.json.totalResults; i < j; i++)
               {
                  result = p_data.json.results[i];

                  if (!result.success && result.fileExist)
                  {
                     if ("folder" == result.type)
                     {
                        message = "message.exists.failure.folder";
                     }
                     else
                     {
                        message = "message.exists.failure.file";
                     }
                  }
               }

               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg(message)
               });

               return;
            }

            YAHOO.Bubbling.fire("files" + eventSuffix[this.options.mode],
            {
               destination: this.currentPath,
               successCount: successCount,
               failureCount: failureCount
            });

            for (var i = 0, j = p_data.json.totalResults; i < j; i++)
            {
               result = p_data.json.results[i];

               if (result.success)
               {
                  YAHOO.Bubbling.fire((result.type == "folder" ? "folder" : "file") + eventSuffix[this.options.mode],
                  {
                     multiple: true,
                     nodeRef: result.nodeRef,
                     destination: this.currentPath
                  });
               }
            }
            // ALF-18501 - Redirect on successful moves of documents within the details view.
            if (this.options.mode == "move" &&
                window.location.pathname.lastIndexOf("document-details") === (window.location.pathname.length - "document-details".length))
            {
               // By reloading the page, the node-header will detect that the node is located in a different
               // site and cause a redirect. The down-side to this is that it causes two page loads but this
               // is most likely quite an edge case and it does ensure that we're re-using a consistent code path
               window.location.reload();
            }
            else
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.success", successCount)
               });
               YAHOO.Bubbling.fire("metadataRefresh");
            }
         };

         // Failure callback function
         var fnFailure = function DLCMT__onOK_failure(p_data)
         {
            this.widgets.dialog.hide();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.failure")
            });
         };

         // Construct webscript URI based on current viewMode
         var webscriptName = this.options.dataWebScript + "/node/{nodeRef}",
            nodeRef = new Alfresco.util.NodeRef(this.selectedNode.data.nodeRef);

         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this
               }
            },
            failure:
            {
               callback:
               {
                  fn: fnFailure,
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: webscriptName,
               params:
               {
                  nodeRef: nodeRef.uri
               }
            },
            wait:
            {
               message: this.msg("message.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleFiles,
		  parentId: this.options.parentId
               }
            }
         });

         this.widgets.okButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);
      },

      /**
       * Gets a custom message depending on current view mode
       * and use superclasses
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function DLCMT_msg(messageId)
      {
         var result = Alfresco.util.message.call(this, this.options.mode + "." + messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
         if (result ==  (this.options.mode + "." + messageId))
         {
            result = Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1))
         }
         if (result == messageId)
         {
            result = Alfresco.util.message(messageId, "Alfresco.module.DoclibGlobalFolder", Array.prototype.slice.call(arguments).slice(1));
         }
         return result;
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Internal show dialog function
       * @method _showDialog
       * @override
       */
      _showDialog: function DLCMT__showDialog()
      {
         this.widgets.okButton.set("label", this.msg("button"));
         return Alfresco.module.DoclibCopyMoveTo.superclass._showDialog.apply(this, arguments);
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DoclibCopyMoveTo("null");
})();