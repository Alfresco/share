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
 * Document Library Actions module
 *
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.Actions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $siteURL = Alfresco.util.siteURL,
      $isValueSet = Alfresco.util.isValueSet;

   /**
    * Cloud folder picker dialog.
    * This will be defined globally, because the sync actions are available in the actions panel as well as in the
    * sync panel. And clicking those actions from different panels creates different panels in different contexts.
    */
   var cloudFolderPicker;

   /**
    * Alfresco.doclib.Actions implementation
    */
   Alfresco.doclib.Actions = {};
   Alfresco.doclib.Actions.prototype =
   {
      /**
       * Current actions view type: set by owning class to "browse" or "details".
       *
       * @property actionsView
       * @type string
       */
      actionsView: null,

      /**
       * Register a Document Library action via Bubbling event
       *
       * @method onRegisterAction
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (actionName, fn)
       */
      onRegisterAction: function dlA_onRegisterAction(layer, args)
      {
         var obj = args[1];
         if (obj && $isValueSet(obj.actionName) && $isValueSet(obj.fn))
         {
            this.registerAction(obj.actionName, obj.fn);
         }
         else
         {
            Alfresco.logger.error("DL_onRegisterAction: Custom action registion invalid: " + obj);
         }
      },

      /**
       * Register a Document Library action
       *
       * @method registerAction
       * @param actionName {string} Action name
       * @param fn {function} Handler function
       * @return {boolean} Success status of registration
       */
      registerAction: function DL_registerAction(actionName, fn)
      {
         if ($isValueSet(actionName) && $isValueSet(fn))
         {
            this.constructor.prototype[actionName] = fn;
            return true;
         }
         return false;
      },

      /**
       * Renders a single action for a given record.
       * Callers should then use
       * <pre>
       *    YAHOO.lang.substitute(actionHTML, this.getActionUrls(record))
       * </pre>
       * on the final concatenated HTML for multiple actions to populate placeholder URLs.
       *
       * @method renderAction
       * @param p_action {object} Object literal representing the node
       * @param p_record {string} Optional siteId override for site-based locations
       * @return {string} HTML containing action markup
       */
      renderAction: function dlA_renderAction(p_action, p_record)
      {
         var urlContext = Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/actions/",
           iconStyle = 'style="background-image:url(' + urlContext + '{icon}-16.png)" ',
           actionTypeMarkup =
           {
              "link": '<div class="{id}"><a title="{label}" class="simple-link" href="{href}" ' + iconStyle + '{target}><span>{label}</span></a></div>',
              "pagelink": '<div class="{id}"><a title="{label}" class="simple-link" href="{pageUrl}" ' + iconStyle + '><span>{label}</span></a></div>',
              "javascript": '<div class="{id}" id="{jsfunction}"><a title="{label}" class="action-link" href="#"' + iconStyle + '><span>{label}</span></a></div>'
           };

         // Store quick look-up for client-side actions
         p_record.actionParams[p_action.id] = p_action.params;

         var markupParams =
         {
            "id": p_action.id,
            "icon": p_action.icon,
            "label": $html(Alfresco.util.substituteDotNotation(this.msg(p_action.label), p_record))
         };

         // Parameter substitution for each action type
         if (p_action.type === "link")
         {
            if (p_action.params.href)
            {
               markupParams.href = Alfresco.util.substituteDotNotation(p_action.params.href, p_record);
               markupParams.target = p_action.params.target ? "target=\"" + p_action.params.target + "\"" : "";
            }
            else
            {
               Alfresco.logger.warn("Action configuration error: Missing 'href' parameter for actionId: ", p_action.id);
            }
         }
         else if (p_action.type === "pagelink")
         {
            if (p_action.params.page)
            {
               markupParams.pageUrl = Alfresco.util.substituteDotNotation(p_action.params.page, p_record);

               /**
                * If the page starts with a "{" character we're going to assume it's a placeholder variable
                * that will be resolved by the getActionsUrls() function. In which case, we do not want to
                * use the $siteURL() function here as that will result in a double-prefix.
                */
               if (p_action.params.page.charAt(0) !== "{")
               {
                  var recordSiteName = $isValueSet(p_record.location.site) ? p_record.location.site.name : null;
                  markupParams.pageUrl = $siteURL(markupParams.pageUrl,
                  {
                     site: recordSiteName
                  });
               }
            }
            else
            {
               Alfresco.logger.warn("Action configuration error: Missing 'page' parameter for actionId: ", p_action.id);
            }
         }
         else if (p_action.type === "javascript")
         {
            if (p_action.params["function"])
            {
               markupParams.jsfunction = p_action.params["function"];
            }
            else
            {
               Alfresco.logger.warn("Action configuration error: Missing 'function' parameter for actionId: ", p_action.id);
            }
         }

         return YAHOO.lang.substitute(actionTypeMarkup[p_action.type], markupParams);
      },

      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param recordData {object} Object literal representing the node
       * @param siteId {string} Optional siteId override for site-based locations
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function dlA_getActionUrls(record, siteId)
      {
         var jsNode = record.jsNode,
            nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef,
            nodeRef = jsNode.isLink && !$isValueSet(nodeRef) ? "invalidlink" : nodeRef,
            strNodeRef = nodeRef.toString(),
            nodeRefUri = nodeRef.uri,
            contentUrl = jsNode.contentURL,
            workingCopy = record.workingCopy || {},
            recordSiteId = $isValueSet(record.location.site) ? record.location.site.name : null,
            fnPageURL = Alfresco.util.bind(function(page)
            {
               return Alfresco.util.siteURL(page,
               {
                  site: YAHOO.lang.isString(siteId) ? siteId : recordSiteId
               });
            }, this),
            actionUrls =
            {
               downloadUrl: $combine(Alfresco.constants.PROXY_URI, contentUrl) + "?a=true" + "\" target=\"_blank",
               viewUrl:  $combine(Alfresco.constants.PROXY_URI, contentUrl) + "\" target=\"_blank",
               documentDetailsUrl: fnPageURL("document-details?nodeRef=" + strNodeRef),
               folderDetailsUrl: fnPageURL("folder-details?nodeRef=" + strNodeRef),
               editMetadataUrl: fnPageURL("edit-metadata?nodeRef=" + strNodeRef),
               inlineEditUrl: fnPageURL("inline-edit?nodeRef=" + strNodeRef),
               managePermissionsUrl: fnPageURL("manage-permissions?nodeRef=" + strNodeRef),
               manageTranslationsUrl: fnPageURL("manage-translations?nodeRef=" + strNodeRef),
               workingCopyUrl: fnPageURL("document-details?nodeRef=" + (workingCopy.workingCopyNodeRef || strNodeRef)),
               workingCopySourceUrl: fnPageURL("document-details?nodeRef=" + (workingCopy.sourceNodeRef || strNodeRef)),
               cloudViewUrl: $combine(Alfresco.constants.URL_SERVICECONTEXT, "cloud/cloudUrl?nodeRef=" +strNodeRef)
            };

         actionUrls.sourceRepositoryUrl = this.viewInSourceRepositoryURL(record, actionUrls) + "\" target=\"_blank";

         return actionUrls;
      },


      /**
       * Helper for actions of type "javascript" to get the node's action descriptor with params resolved (unless resolve is set to false).
       *
       * @method getAction
       * @param record {object} Object literal representing one file or folder to be actioned
       * @param owner {HTMLElement} The action html element
       * @param resolve {Boolean} (Optional) Set to false if the action param's {} shouldn't get resolved
       */
      getAction: function dlA_getAction(record, owner, resolve)
      {
         var actionId = owner.className,
            action = Alfresco.util.findInArray(record.actions, actionId, "id") || {};

         if (resolve === false)
         {
            // Return action without resolved parameters
            return action;
         }
         else
         {
            // Resolve action's parameters before returning them
            action = Alfresco.util.deepCopy(action);
            var params = action.params || {};
            for (var key in params)
            {
               params[key] = YAHOO.lang.substitute(params[key], record, function getActionParams_substitute(p_key, p_value, p_meta)
               {
                  return Alfresco.util.findValueByDotNotation(record, p_key);
               });
            }
            return action;
         }
      },

      /**
       * Tries to get a common parent nodeRef for an action that requires one.
       *
       * @method getParentNodeRef
       * @param record {object} Object literal representing one file or folder to be actioned
       * @return {string|null} Parent nodeRef or null
       */
      getParentNodeRef: function dlA_getParentNodeRef(record)
      {
         var nodeRef = null;

         if (YAHOO.lang.isArray(record))
         {
            try
            {
               nodeRef = this.doclistMetadata.parent.nodeRef;
            }
            catch (e)
            {
               nodeRef = null;
            }

            if (nodeRef === null)
            {
               for (var i = 1, j = record.length, sameParent = true; i < j && sameParent; i++)
               {
                  sameParent = (record[i].parent.nodeRef == record[i - 1].parent.nodeRef)
               }

               nodeRef = sameParent ? record[0].parent.nodeRef : this.doclistMetadata.container;
            }
         }
         else
         {
            nodeRef = record.parent.nodeRef;
         }

         return nodeRef;
      },

      /**
       * Record metadata.
       *
       * @override
       * @method onActionDetails
       * @param record {object} Object literal representing one file or folder to be actioned
       */
      onActionDetails: function dlA_onActionDetails(record)
      {
         var scope = this,
            nodeRef = record.nodeRef,
            jsNode = record.jsNode;

         // Intercept before dialog show
         var doBeforeDialogShow = function dlA_onActionDetails_doBeforeDialogShow(p_form, p_dialog)
         {
            // Dialog title
            var fileSpan = '<span class="light">' + $html(record.displayName) + '</span>';

            Alfresco.util.populateHTML(
               [ p_dialog.id + "-dialogTitle", scope.msg("edit-details.title", fileSpan) ]
            );

            // Edit metadata link button
            this.widgets.editMetadata = Alfresco.util.createYUIButton(p_dialog, "editMetadata", null,
            {
               type: "link",
               label: scope.msg("edit-details.label.edit-metadata"),
               href: $siteURL("edit-metadata?nodeRef=" + nodeRef)
            });
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "node",
            itemId: nodeRef,
            mode: "edit",
            submitType: "json",
            formId: "doclib-simple-metadata"
         });

         // Using Forms Service, so always create new instance
         var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails-" + Alfresco.util.generateDomId());

         editDetails.setOptions(
         {
            width: "auto",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function dlA_onActionDetails_success(response)
               {
                  // Reload the node's metadata
                  var webscriptPath = "components/documentlibrary/data";
                  if ($isValueSet(this.options.siteId))
                  {
                     webscriptPath += "/site/" + encodeURIComponent(this.options.siteId)
                  }
                  Alfresco.util.Ajax.request(
                  {
                     url: $combine(Alfresco.constants.URL_SERVICECONTEXT, webscriptPath, "/node/", jsNode.nodeRef.uri) + "?view=" + this.actionsView,
                     successCallback:
                     {
                        fn: function dlA_onActionDetails_refreshSuccess(response)
                        {
                           var record = response.json.item
                           record.jsNode = new Alfresco.util.Node(response.json.item.node);

                           // Fire "renamed" event
                           YAHOO.Bubbling.fire(record.node.isContainer ? "folderRenamed" : "fileRenamed",
                           {
                              file: record
                           });

                           // Fire "tagRefresh" event
                           YAHOO.Bubbling.fire("tagRefresh");

                           // Display success message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.details.success")
                           });

                           // Refresh the document list...
                           this._updateDocList.call(this);
                        },
                        scope: this
                     },
                     failureCallback:
                     {
                        fn: function dlA_onActionDetails_refreshFailure(response)
                        {
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.details.failure")
                           });
                        },
                        scope: this
                     }
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function dLA_onActionDetails_failure(response)
               {
                  var failureMsg = this.msg("message.details.failure");
                  if (response.json && response.json.message.indexOf("Failed to persist field 'prop_cm_name'") !== -1)
                  {
                     failureMsg = this.msg("message.details.failure.name");
                  }
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: failureMsg
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Locate record.
       *
       * @method onActionLocate
       * @param record {object} Object literal representing one file or folder to be actioned
       */
      onActionLocate: function dlA_onActionLocate(record)
      {
         var jsNode = record.jsNode,
            path = record.location.path,
            file,
            recordSiteName = $isValueSet(record.location.site) ? record.location.site.name : null;
         if (jsNode.isLink)
         {
             file = $isValueSet(jsNode.linkedNode.properties) ? jsNode.linkedNode.properties.name : null;
             Alfresco.util.PopupManager.displayMessage(
             {
                text: this.msg("message.actions.failure.locate")
             });
         }
         else
         {
             file = record.displayName;
         }

         if ($isValueSet(this.options.siteId) && recordSiteName !== this.options.siteId)
         {
            window.location = $siteURL((recordSiteName === null ? "repository" : "documentlibrary") + "?file=" + encodeURIComponent(file) + "&path=" + encodeURIComponent(path),
            {
               site: recordSiteName
            });
         }
         else
         {
            this.options.highlightFile = file;

            // Change active filter to path
            YAHOO.Bubbling.fire("changeFilter",
            {
               filterId: "path",
               filterData: path
            });
         }
      },

      /**
       * Delete record.
       *
       * @method onActionDelete
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionDelete: function dlA_onActionDelete(record)
      {
         var me = this,
            jsNode = record.jsNode,
            content = jsNode.isContainer ? "folder" : "document",
            displayName = record.displayName,
            isCloud = (this.options.syncMode === "CLOUD"),
            zIndex = 0;

         var displayPromptText = this.msg("message.confirm.delete", displayName);
         if (jsNode.hasAspect("sync:syncSetMemberNode"))
         {
            if (isCloud)
            {
        	   if (jsNode.hasAspect("sync:deleteOnPrem"))
               {
          	      displayPromptText += this.msg("actions.synced.cloud." + content + ".delete.on.prem", displayName);
               }
               else
               {
          	      displayPromptText += this.msg("actions.synced.cloud." + content + ".delete", displayName);
               }
            }
            else
            {
                displayPromptText += this.msg("actions.synced." + content + ".delete", displayName);
            }
         }

         if (this.fullscreen !== undefined && ( this.fullscreen.isWindowOnly || Dom.hasClass(this.id, 'alf-fullscreen')))
         {
            zIndex = 1000;
         }

         //MNT-11084 : Full screen/window view: Actions works incorrectly;
         var parent = undefined;
         var container = Dom.get(this.id);
         var ua = navigator.userAgent.toLowerCase();
         if ((ua.indexOf('gecko') != -1 || ua.indexOf('safari')!=-1) && ua.indexOf('chrome')==-1)
         {
            parent = container;
         }
		 
         var buttons =
         [
            {
               text: this.msg("button.delete"),
               handler: function dlA_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, record);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function dlA_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }
         ];
			
         if (jsNode.hasAspect("sync:syncSetMemberNode"))
         {
            displayPromptText += this.msg("actions.synced.remove-sync");
            buttons.unshift({
               text: this.msg("button.unsync"),
               handler: function dlA_onActionCloudUnsync_unsync()
               {
                  var requestDeleteRemote = isCloud ? false : Dom.getAttribute("requestDeleteRemote", "checked");
                    
                  try
                  {
                     Alfresco.util.Ajax.request(
                     {
                        url: Alfresco.constants.PROXY_URI + "enterprise/sync/syncsetmembers/" + record.jsNode.nodeRef.uri + "?requestDeleteRemote=" + requestDeleteRemote,
                        method: Alfresco.util.Ajax.DELETE,
                        successCallback:{
                           fn: function cloudSync_onCloudUnsync_success()
                           {
                              YAHOO.Bubbling.fire("metadataRefresh");
                              Alfresco.util.PopupManager.displayMessage(
                              {
                                 text: me.msg("message.unsync.success")
                              })
                           },
                           scope: me
                        },
                        failureMessage: me.msg("message.unsync.failure")
                     });
                  }
                  catch (e) {}
               }
            });
         }
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("actions." + content + ".delete"),
            text: displayPromptText,
            noEscape: true,
            buttons: buttons
         });
      },

      /**
       * Delete record confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param record {object} Object literal representing the file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function dlA__onActionDeleteConfirm(record)
      {
         var jsNode = record.jsNode,
            path = record.location.path,
            fileName = record.location.file,
            filePath = $combine(path, fileName),
            displayName = record.displayName,
            nodeRef = jsNode.nodeRef,
            parentNodeRef = this.getParentNodeRef(record);

         this.modules.actions.genericAction(
         {
            success:
            {
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: jsNode.isContainer ? "folder-deleted" : "file-deleted",
                  page: "documentlibrary",
                  activityData:
                  {
                     fileName: fileName,
                     path: path,
                     nodeRef: nodeRef.toString(),
                     parentNodeRef: parentNodeRef.toString()
                  }
               },
               event:
               {
                  name: jsNode.isContainer ? "folderDeleted" : "fileDeleted",
                  obj:
                  {
                     path: filePath
                  }
               },
               message: this.msg("message.delete.success", displayName),
               callback:
               {
                  fn: function successDeleteCallback(response, obj)
                  {
                      if (this.totalRecords)
                      {
                          this.totalRecords -= response.json.successCount;
                      }
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this.msg("message.delete.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "file/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            },
            wait:
            {
               message: this.msg("message.multiple-delete.please-wait")
            }
         });
      },

      /**
       * Edit Offline.
       * NOTE: Placeholder only, clients MUST implement their own editOffline action
       *
       * @method onActionEditOffline
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionEditOffline: function dlA_onActionEditOffline(record)
      {
         Alfresco.logger.error("onActionEditOffline", "Abstract implementation not overridden");
      },

      /**
       * Valid online edit mimetypes, mapped to application ProgID.
       * Currently allowed are Microsoft Office 2003 and 2007 mimetypes for Excel, PowerPoint and Word only
       *
       * @property onlineEditMimetypes
       * @type object
       */
      onlineEditMimetypes:
      {
         "application/msword": "Word.Document",
         "application/vnd.openxmlformats-officedocument.wordprocessingml.document": "Word.Document",
         "application/vnd.ms-word.document.macroenabled.12": "Word.Document",
         "application/vnd.openxmlformats-officedocument.wordprocessingml.template": "Word.Document",
         "application/vnd.ms-word.template.macroenabled.12": "Word.Document",

         "application/vnd.ms-powerpoint": "PowerPoint.Slide",
         "application/vnd.openxmlformats-officedocument.presentationml.presentation": "PowerPoint.Slide",
         "application/vnd.ms-powerpoint.presentation.macroenabled.12": "PowerPoint.Slide",
         "application/vnd.openxmlformats-officedocument.presentationml.slideshow": "PowerPoint.Slide",
         "application/vnd.ms-powerpoint.slideshow.macroenabled.12": "PowerPoint.Slide",
         "application/vnd.openxmlformats-officedocument.presentationml.template": "PowerPoint.Slide",
         "application/vnd.ms-powerpoint.template.macroenabled.12": "PowerPoint.Slide",
         "application/vnd.ms-powerpoint.addin.macroenabled.12": "PowerPoint.Slide",
         "application/vnd.openxmlformats-officedocument.presentationml.slide": "PowerPoint.Slide",
         "application/vnd.ms-powerpoint.slide.macroEnabled.12": "PowerPoint.Slide",

         "application/vnd.ms-excel": "Excel.Sheet",
         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": "Excel.Sheet",
         "application/vnd.openxmlformats-officedocument.spreadsheetml.template": "Excel.Sheet",
         "application/vnd.ms-excel.sheet.macroenabled.12": "Excel.Sheet",
         "application/vnd.ms-excel.template.macroenabled.12": "Excel.Sheet",
         "application/vnd.ms-excel.addin.macroenabled.12": "Excel.Sheet",
         "application/vnd.ms-excel.sheet.binary.macroenabled.12": "Excel.Sheet",
         "application/vnd.visio": "Visio.Drawing"
      },

      /**
       * Edit Online.
       *
       * @method onActionEditOnline
       * @param record {object} Object literal representing file or folder to be actioned
       */
      onActionEditOnline: function dlA_onActionEditOnline(record)
      {
         //MNT-8609 Edit online fails for files which URL is too long
         if (!$isValueSet(record.onlineEditUrl))
         {
            record.onlineEditUrl = Alfresco.util.onlineEditUrl(this.doclistMetadata.custom.vtiServer, record.location);
         }


         // Check if either the URL's length or the encoded URL's length is greater than 260 (see MNT-13279):
         if (record.onlineEditUrl.length > 260 || (encodeURI(record.onlineEditUrl)).length > 260)
         {
            //Try to use alternate edit online URL: http://{host}:{port}/{context}/_IDX_SITE_{site_uuid}/_IDX_NODE_{document_uuid}/{document_name}
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.PROXY_URI+"/api/sites/" + record.location.site.name,
               successCallback:
               {
                  fn: function(response)
                  {
                     var siteUUID = response.json.node.split("/").pop();
                     var docUUID = record.nodeRef.split("/").pop();
                     record.onlineEditUrl = record.onlineEditUrl.split(record.location.site.name)[0] + "_IDX_SITE_" + siteUUID + "/_IDX_NODE_" + docUUID + "/" + record.displayName;
                     if (record.onlineEditUrl.length > 260)
                     {
                        var ext = record.displayName.split(".").pop();
                        var recordName = record.displayName.split(".")[0];
                        var exceed = record.onlineEditUrl.length - 260;
                        record.onlineEditUrl = record.onlineEditUrl.replace(record.displayName, recordName.substring(0, recordName.length - exceed - 1) + "." + ext);
                     }
                     this.actionEditOnlineInternal(record);
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     this.actionEditOnlineInternal(record);
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.actionEditOnlineInternal(record);
         }
      },

      actionEditOnlineInternal: function dlA_onActionEditOnline(record)
      {
         if (record.onlineEditUrl.length > 260)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.edit-online.office.path.failure")
            });
         }
         else if (this._launchOnlineEditor(record))
         {
            YAHOO.Bubbling.fire("metadataRefresh");
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.edit-online.office.failure")
            });
         }
      },

      /**
       * Opens the appropriate Microsoft Office application for online editing.
       * Supports: Microsoft Office 2003, 2007 & 2010.
       *
       * @method Alfresco.util.sharePointOpenDocument
       * @param record {object} Object literal representing file or folder to be actioned
       * @return {boolean} True if the action was completed successfully, false otherwise.
       */
      _launchOnlineEditor: function dlA__launchOnlineEditor(record)
      {
         var controlProgID = "SharePoint.OpenDocuments",
            jsNode = record.jsNode,
            loc = record.location,
            mimetype = jsNode.mimetype,
            appProgID = null,
            activeXControl = null,
            extensionMap =
            {
               doc: "application/msword",
               docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
               docm: "application/vnd.ms-word.document.macroenabled.12",
               dotx: "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
               dotm: "application/vnd.ms-word.template.macroenabled.12",

               ppt: "application/vnd.ms-powerpoint",
               pptx: "application/vnd.openxmlformats-officedocument.presentationml.presentation",
               pptm: "application/vnd.ms-powerpoint.presentation.macroenabled.12",
               ppsx: "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
               ppsm: "application/vnd.ms-powerpoint.slideshow.macroenabled.12",
               potx: "application/vnd.openxmlformats-officedocument.presentationml.template",
               potm: "application/vnd.ms-powerpoint.template.macroenabled.12",
               ppam: "application/vnd.ms-powerpoint.addin.macroenabled.12",
               sldx: "application/vnd.openxmlformats-officedocument.presentationml.slide",
               sldm: "application/vnd.ms-powerpoint.slide.macroEnabled.12",

               xls: "application/vnd.ms-excel",
               xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
               xltx: "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
               xlsm: "application/vnd.ms-excel.sheet.macroenabled.12",
               xltm: "application/vnd.ms-excel.template.macroenabled.12",
               xlam: "application/vnd.ms-excel.addin.macroenabled.12",
               xlsb: "application/vnd.ms-excel.sheet.binary.macroenabled.12"
            };

         if (!Alfresco.util.validLocationForOnlineEdit(loc))
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("actions.editOnline.invalid", loc.file)
            });
            return true;
         }

         // Try to resolve the record to an application ProgID; by mimetype first, then file extension.
         if (this.onlineEditMimetypes.hasOwnProperty(mimetype))
         {
            appProgID = this.onlineEditMimetypes[mimetype];
         }
         else
         {
            var extn = Alfresco.util.getFileExtension(record.location.file);
            if (extn !== null)
            {
               extn = extn.toLowerCase();
               if (extensionMap.hasOwnProperty(extn))
               {
                  mimetype = extensionMap[extn];
                  if (this.onlineEditMimetypes.hasOwnProperty(mimetype))
                  {
                     appProgID = this.onlineEditMimetypes[mimetype];
                  }
               }
            }
         }

         if (appProgID !== null)
         {
            // Ensure we have the record's onlineEditUrl populated
            if (!$isValueSet(record.onlineEditUrl))
            {
               record.onlineEditUrl = Alfresco.util.onlineEditUrl(this.doclistMetadata.custom.vtiServer, loc);
            }

            if (YAHOO.env.ua.ie > 0)
            {
               return this._launchOnlineEditorIE(controlProgID, record, appProgID);
            }

            if ((YAHOO.env.ua.chrome > 0) && !Alfresco.util.isSharePointPluginInstalled())
            {
               var extn = Alfresco.util.getFileExtension(loc.file);
               if (null !== extn)
               {
                  var protocolHandler = this.getProtocolForFileExtension(extn.toLowerCase());
                  return this._launchOnlineEditorChrome(protocolHandler, record.onlineEditUrl);
               }
            }

            if (Alfresco.util.isSharePointPluginInstalled())
            {
               return this._launchOnlineEditorPlugin(record, appProgID);
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: this.msg("actions.editOnline.failure", loc.file)
               });
               return false;
            }
         }

         // No success in launching application via ActiveX control; launch the WebDAV URL anyway
         return window.open(record.onlineEditUrl, "_blank");
      },

      _launchOnlineEditorChrome: function dlA__launchOnlineEditorChrome(protocolHandler, url)
      {
          var protocolUrl = protocolHandler + ':ofe%7Cu%7C' + url;
          var protocolHandlerPresent = false;

          var input = document.createElement('input');
          var inputTop = document.body.scrollTop + 10;
          input.setAttribute('style', 'z-index: 1000; background-color: rgba(0, 0, 0, 0); border: none; outline: none; position: absolute; left: 10px; top: '+inputTop+'px;');
          document.getElementsByTagName("body")[0].appendChild(input);
          input.focus();
          input.onblur = function() {
              protocolHandlerPresent = true;
          };
          location.href = protocolUrl;
          setTimeout(function()
          {
              input.onblur = null;
              input.remove();
              if(!protocolHandlerPresent)
              {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                      text: this.msg('message.edit-online.supported_office_version_required')
                  });
              }
          }, 500);
      },

      getProtocolForFileExtension: function(fileExtension)
      {
         var msProtocolNames =
         {
            'doc'  : 'ms-word',
            'docx' : 'ms-word',
            'docm' : 'ms-word',
            'dot'  : 'ms-word',
            'dotx' : 'ms-word',
            'dotm' : 'ms-word',
            'xls'  : 'ms-excel',
            'xlsx' : 'ms-excel',
            'xlsb' : 'ms-excel',
            'xlsm' : 'ms-excel',
            'xlt'  : 'ms-excel',
            'xltx' : 'ms-excel',
            'xltm' : 'ms-excel',
            'xlsm' : 'ms-excel',
            'ppt'  : 'ms-powerpoint',
            'pptx' : 'ms-powerpoint',
            'pot'  : 'ms-powerpoint',
            'potx' : 'ms-powerpoint',
            'potm' : 'ms-powerpoint',
            'pptm' : 'ms-powerpoint',
            'potm' : 'ms-powerpoint',
            'pps'  : 'ms-powerpoint',
            'ppsx' : 'ms-powerpoint',
            'ppam' : 'ms-powerpoint',
            'ppsm' : 'ms-powerpoint',
            'sldx' : 'ms-powerpoint',
            'sldm' : 'ms-powerpoint',
         };
         return msProtocolNames[fileExtension];
      },

      /**
       * Opens the appropriate Microsoft Office application for online editing.
       * Supports: Microsoft Office 2003, 2007 & 2010.
       *
       * @method Alfresco.util.sharePointOpenDocument
       * @param record {object} Object literal representing file or folder to be actioned
       * @return {boolean} True if the action was completed successfully, false otherwise.
       */
      _launchOnlineEditorIE: function dlA__launchOnlineEditorIE(controlProgID, record, appProgID)
      {
         // Try each version of the SharePoint control in turn, newest first
         try
         {
            if (appProgID === "Visio.Drawing")
               throw ("Visio should be invoked using activeXControl.EditDocument2.");
            activeXControl = new ActiveXObject(controlProgID + ".3");
            return activeXControl.EditDocument3(window, record.onlineEditUrl, true, appProgID);
         }
         catch(e)
         {
            try
            {
               activeXControl = new ActiveXObject(controlProgID + ".2");
               return activeXControl.EditDocument2(window, record.onlineEditUrl, appProgID);
            }
            catch(e1)
            {
               try
               {
                  activeXControl = new ActiveXObject(controlProgID + ".1");
                  return activeXControl.EditDocument(record.onlineEditUrl, appProgID);
               }
               catch(e2)
               {
                  // Do nothing
               }
            }
         }
         return false;
      },

      /**
       * Opens the appropriate Microsoft Office application for online editing.
       * Supports: Microsoft Office 2010 & 2011 for Mac.
       *
       * @method Alfresco.util.sharePointOpenDocument
       * @param record {object} Object literal representing file or folder to be actioned
       * @return {boolean} True if the action was completed successfully, false otherwise.
       */
      _launchOnlineEditorPlugin: function dlA__launchOnlineEditorPlugin(record, appProgID)
      {
         var plugin = document.getElementById("SharePointPlugin");
         if (plugin == null && Alfresco.util.isSharePointPluginInstalled())
         {
            var pluginMimeType = null;
            if (YAHOO.env.ua.webkit && Alfresco.util.isBrowserPluginInstalled("application/x-sharepoint-webkit"))
               pluginMimeType = "application/x-sharepoint-webkit";
            else
               pluginMimeType = "application/x-sharepoint";
            var pluginNode = document.createElement("object");
            pluginNode.id = "SharePointPlugin";
            pluginNode.type = pluginMimeType;
            pluginNode.width = 0;
            pluginNode.height = 0;
            pluginNode.style.setProperty("visibility", "hidden", "");
            document.body.appendChild(pluginNode);
            plugin = document.getElementById("SharePointPlugin");

            if (!plugin)
            {
               return false;
            }
         }

         try
         {
            if (appProgID === "Visio.Drawing")
               throw ("Visio should be invoked using activeXControl.EditDocument2.");
            return plugin.EditDocument3(window, record.onlineEditUrl, true, appProgID);
         }
         catch(e)
         {
            try
            {
               return plugin.EditDocument2(window, record.onlineEditUrl, appProgID);
            }
            catch(e1)
            {
               try
               {
                  return plugin.EditDocument(record.onlineEditUrl, appProgID);
               }
               catch(e2)
               {
                  return false;
               }
            }
         }
      },

      /**
       * Edit Online with AOS.
       *
       * @method onActionEditOnlineAos
       * @param record {object} Object literal representing file or folder to be actioned
       */
      onActionEditOnlineAos: function dlA_onActionEditOnlineAos(record)
      {
         if (!$isValueSet(record.onlineEditUrlAos))
         {
            record.onlineEditUrlAos = Alfresco.util.onlineEditUrlAos(this.doclistMetadata.custom.aos, record);
         }

         var fileExtension = Alfresco.util.getFileExtension(record.location.file);
         var protocolHandler = this.getProtocolForFileExtension(fileExtension);

         if(protocolHandler === undefined)
         {
            Alfresco.logger.error("onActionEditOnlineAos", "No protocol handler available for file extension.");
            return;
         }

         var officeLauncher = new EmbeddedOfficeLauncher();

         if(officeLauncher.isIOS())
         {
            this._aos_launchOfficeOnIos(officeLauncher, protocolHandler, record.onlineEditUrlAos);
            return;
         }

         // detect if we are on a supported operating system
         if(!officeLauncher.isWin() && !officeLauncher.isMac())
         {
             Alfresco.util.PopupManager.displayMessage(
             {
                text: this.msg('message.edit-online-aos.no_supported_environment')
             });
             return;
         }

         // if we have a working PlugIn (ActiveX or NPAPI), use it. Otherwise we use the protocol handler (e.g. Chrome w/o PlugIn)
         if(officeLauncher.isAvailable())
         {
             this._aos_launchOfficeByPlugin(officeLauncher, record.onlineEditUrlAos);
         }
         else
         {
             this._aos_tryToLaunchOfficeByMsProtocolHandler(officeLauncher, protocolHandler, record.onlineEditUrlAos);
         }

         return;
      },

      _aos_launchOfficeByPlugin: function dlA__aos_launchOfficeByPlugin(officeLauncher, url)
      {
         var checker, dlg;
         var isNotIE = (officeLauncher.isFirefox() || officeLauncher.isChrome() || officeLauncher.isSafari());
         if (!officeLauncher.EditDocument(url))
         {
            // check if the Plug-In has been blocked
            if (officeLauncher.isControlNotActivated() && isNotIE)
            {
               checker = window.setInterval(function()
               {
                  if (officeLauncher.isControlActivated())
                  {
                     window.clearInterval(checker);
                     dlg.destroy();
                     window.setTimeout(function()
                     {
                        if (!officeLauncher.EditDocument(url))
                        {
                           if (officeLauncher.getLastControlResult() !== -2)
                           {
                              var errorDetails = officeLauncher.getLastControlResult() !== false ? ' (Error code: ' + officeLauncher.getLastControlResult() + ')' : '';
                              Alfresco.util.PopupManager.displayMessage(
                              {
                                          text: this.msg('message.edit-online-aos.starting_office_failed') + errorDetails
                              });
                           }
                        }
                        else
                        {
                           YAHOO.Bubbling.fire("metadataRefresh");
                        }
                     }, 50);
                  }
               }, 250);
               var dlg = new YAHOO.widget.SimpleDialog('prompt',
               {
                        close: false,
                        constraintoviewport: true,
                        draggable: false,
                        effect: null,
                        modal: true,
                        visible: true,
                        zIndex: 9999
               });
               var dlgMessageKey = 'message.edit-online-aos.plugin_blocked.body.firefox';
               if(officeLauncher.isFirefox())
               {
                   dlgMessageKey = 'message.edit-online-aos.plugin_blocked.body.firefox';
               }
               else if(officeLauncher.isChrome())
               {
                   dlgMessageKey = 'message.edit-online-aos.plugin_blocked.body.chrome';
               }
               else if(officeLauncher.isSafari())
               {
                   dlgMessageKey = 'message.edit-online-aos.plugin_blocked.body.safari';
               }
               dlg.setHeader(this.msg('message.edit-online-aos.plugin_blocked.caption'));
               dlg.setBody(this.msg(dlgMessageKey));
               dlg.cfg.queueProperty('buttons', [ {
                     text: this.msg('message.edit-online-aos.plugin_blocked.button_dismiss'),
                     handler: function() {
                        window.clearInterval(checker);
                        this.destroy();
                     },
                     isDefault: true
               }]);
               dlg.render(document.body);
               dlg.center();
               dlg.show();
            }
            else
            {
               if (officeLauncher.getLastControlResult() !== -2)
               {
                  // error message only required if user did not cancel (result === -2)
                  var errorDetails = officeLauncher.getLastControlResult() !== false ? ' (Error code: ' + officeLauncher.getLastControlResult() + ')' : '';
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg('message.edit-online-aos.starting_office_failed') + errorDetails
                  });
               }
            }
         }
         else
         {
            YAHOO.Bubbling.fire("metadataRefresh");
         }
      },

      _aos_tryToLaunchOfficeByMsProtocolHandler: function dlA__aos_tryToLaunchOfficeByMsProtocolHandler(officeLauncher, protocolHandler, url)
      {
          var protocolUrl = protocolHandler + ':ofe%7Cu%7C' + url;
          var protocolHandlerPresent = false;

          var input = document.createElement('input');
          var inputTop = document.body.scrollTop + 10;
          input.setAttribute('style', 'z-index: 1000; background-color: rgba(0, 0, 0, 0); border: none; outline: none; position: absolute; left: 10px; top: '+inputTop+'px;');
          document.getElementsByTagName("body")[0].appendChild(input);
          input.focus();
          input.onblur = function() {
              protocolHandlerPresent = true;
          };
          location.href = protocolUrl;
          setTimeout(function()
          {
              input.onblur = null;
              input.remove();
              if(!protocolHandlerPresent)
              {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                      text: this.msg('message.edit-online-aos.supported_office_version_required')
                  });
              }
          }, 500);
      },

      _aos_launchOfficeOnIos: function dlA__aos_launchOfficeOnIos(officeLauncher, protocolHandler, url)
      {
         var protocolUrl = protocolHandler + ':ofe%7Cu%7C' + officeLauncher.encodeUrl(url);
         var iframe = document.createElement('iframe');
         iframe.setAttribute('style', 'display: none; height: 0; width: 0;');
         document.getElementsByTagName('body')[0].appendChild(iframe);
         iframe.src = protocolUrl;
      },

      getProtocolForFileExtension: function(fileExtension)
      {
         var msProtocolNames =
         {
            'doc'  : 'ms-word',
            'docx' : 'ms-word',
            'docm' : 'ms-word',
            'dot'  : 'ms-word',
            'dotx' : 'ms-word',
            'dotm' : 'ms-word',
            'xls'  : 'ms-excel',
            'xlsx' : 'ms-excel',
            'xlsb' : 'ms-excel',
            'xlsm' : 'ms-excel',
            'xlt'  : 'ms-excel',
            'xltx' : 'ms-excel',
            'xltm' : 'ms-excel',
            'xlsm' : 'ms-excel',
            'ppt'  : 'ms-powerpoint',
            'pptx' : 'ms-powerpoint',
            'pot'  : 'ms-powerpoint',
            'potx' : 'ms-powerpoint',
            'potm' : 'ms-powerpoint',
            'pptm' : 'ms-powerpoint',
            'potm' : 'ms-powerpoint',
            'pps'  : 'ms-powerpoint',
            'ppsx' : 'ms-powerpoint',
            'ppam' : 'ms-powerpoint',
            'ppsm' : 'ms-powerpoint',
            'sldx' : 'ms-powerpoint',
            'sldm' : 'ms-powerpoint',
         };
         return msProtocolNames[fileExtension];
      },

      /**
       * Simple Repo Action.
       *
       * Accepts the following <param> declarations from the <action> config:
       *
       * action - The name of  the repo action (i.e. extract-metadata)
       * success - The name of the callback function
       * successMessage - The msg key to use when the repo action succeded (i.e. message.extract-metadata.success)
       * failure - The name of the callback function
       * failureMessage - The msg key to use when the repo action failed (i.e. message.extract-metadata.failure)
       * * - All remaining parameters will be treated as repo action parameters
       *
       * Example:
       * <action id="addAspectExample" type="javascript">
       *    <param name="function">onActionSimpleRepoAction</param>
       *    <param name="action">add-features</param>
       *    <param name="aspect-name">rd:status</param>
       *    <param name="successMessage">addAspectExample.success</param>
       *    <param name="failureMessage">addAspectExample.failure</param>
       * </action>
       *
       * @method onActionSimpleRepoAction
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionSimpleRepoAction: function dlA_onActionSimpleRepoAction(record, owner)
      {
         //ACE-2470 : Clone: Clicking multiple times the simple Workflow approval menu item gives unexpected results.
         if (owner.title.indexOf("_deactivated") == -1)
         {
         // Get action params
         var params = this.getAction(record, owner).params,
            displayName = record.displayName,
            namedParams = ["function", "action", "success", "successMessage", "failure", "failureMessage"],
            repoActionParams = {};

         for (var name in params)
         {
            if (params.hasOwnProperty(name) && !Alfresco.util.arrayContains(namedParams, name))
            {
               repoActionParams[name] = params[name];
            }
         }

         //Deactivate action
         var ownerTitle = owner.title;
         owner.title = owner.title + "_deactivated";

         // Prepare genericAction config
         var config =
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh",
                  obj: record
               }
            },
            failure:
            {
               message: this.msg(params.failureMessage, displayName),
               fn: function showAction()
               {
                  owner.title = ownerTitle;
               },
               scope: this
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               stem: Alfresco.constants.PROXY_URI + "api/",
               name: "actionQueue"
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  actionedUponNode: record.nodeRef,
                  actionDefinitionName: params.action,
                  parameterValues: repoActionParams
               }
            }
         };

         // Add configured success callbacks and messages if provided
         if (YAHOO.lang.isFunction(this[params.success]))
         {
            config.success.callback =
            {
               fn: this[params.success],
               obj: record,
               scope: this
            };
         }
         if (params.successMessage)
         {
            config.success.message = this.msg(params.successMessage, displayName);
         }

         // Acd configured failure callback and message if provided
         if (YAHOO.lang.isFunction(this[params.failure]))
         {
            config.failure.callback =
            {
               fn: this[params.failure],
               obj: record,
               scope: this
            };
         }
         if (params.failureMessage)
         {
            config.failure.message = this.msg(params.failureMessage, displayName);
         }

         // Execute the repo action
         this.modules.actions.genericAction(config);
         }
      },

      /**
       * Form Dialog Action.
       *
       * Accepts <param name=""></param> declarations in share config xml for the following names:
       * success - The name of the callback function
       * successMessage - The msg key to use when the repo action succeded (i.e. message.extract-metadata.success)
       * failure - The name of the callback function
       * failureMessage - The msg key to use when the repo action failed (i.e. message.extract-metadata.failure)
       * ...and any other parameter mathing the properties for GET /service/components/form webscript
       * i.e itemid, itemkind, mode etc...
       *
       * @method onActionFormDialog
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionFormDialog: function dlA_onActionFormDialog(record, owner)
      {
         var config = this.generateConfigForFormDialogAction(record, owner);

         // Finally display form as dialog
         Alfresco.util.PopupManager.displayForm(config);
      },
      
      /**
       * Form Dialog Action with disabling submit buttons.
       *
       * Accepts <param name=""></param> declarations in share config xml for the following names:
       * success - The name of the callback function
       * successMessage - The msg key to use when the repo action succeded (i.e. message.extract-metadata.success)
       * failure - The name of the callback function
       * failureMessage - The msg key to use when the repo action failed (i.e. message.extract-metadata.failure)
       * ...and any other parameter mathing the properties for GET /service/components/form webscript
       * i.e itemid, itemkind, mode etc...
       *
       * @method onActionFormDialog
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionFormDialogWithSubmitDisable: function dlA_onActionFormDialogWithSubmitDisable(record, owner)
      {
         var config = this.generateConfigForFormDialogAction(record, owner);
         
         config.properties.disableSubmitButton = true;

         // Finally display form as dialog
         Alfresco.util.PopupManager.displayForm(config);
      },

      generateConfigForFormDialogAction: function dlA_generateConfigForFormDialogAction(record, owner)
      {
         // Get action & params and start create the config for displayForm
         var action = this.getAction(record, owner),
            params = action.params,
            config =
            {
               title: this.msg(action.label)
            },
            displayName = record.displayName;

         // Make sure we don't pass the function as a form parameter
         delete params["function"];

         // Add configured success callback
         var success = params["success"];
         delete params["success"];
         config.success =
         {
            fn: function(response, obj)
            {
               // Invoke callback if configured and available
               if (YAHOO.lang.isFunction(this[success]))
               {
                  this[success].call(this, response, obj);
               }

               // Fire metadataRefresh so other components may update themselves
               YAHOO.Bubbling.fire("metadataRefresh", obj);
            },
            obj: record,
            scope: this
         };

         // Add configure success message
         if (params.successMessage)
         {
            config.successMessage = this.msg(params.successMessage, displayName);
            delete params["successMessage"];
         }

         // Add configured failure callback
         if (YAHOO.lang.isFunction(this[params.failure]))
         {
            config.failure =
            {
               fn: this[params.failure],
               obj: record,
               scope: this
            };
            delete params["failure"];
         }
         // Add configure success message
         if (params.failureMessage)
         {
            config.failureMessage = this.msg(params.failureMessage, displayName);
            delete params["failureMessage"];
         }

         // Use the remaining properties as form properties
         config.properties = params;

         return config;
      },

      /**
       * Upload new version.
       *
       * @method onActionUploadNewVersion
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionUploadNewVersion: function dlA_onActionUploadNewVersion(record)
      {
         var jsNode = record.jsNode,
            displayName = record.displayName,
            nodeRef = jsNode.nodeRef,
            version = record.version;

         if (!this.fileUpload)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }

         // Show uploader for multiple files
         var description = this.msg("label.filter-description", displayName),
            extensions = "*";

         if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
         {
            // Only add a filtering extension if filename contains a name and a suffix
            extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
         }

         if (record.workingCopy && record.workingCopy.workingCopyVersion)
         {
            version = record.workingCopy.workingCopyVersion;
         }

         var zIndex = 0;
         if (this.fullscreen !== undefined && ( this.fullscreen.isWindowOnly || Dom.hasClass(this.id, 'alf-fullscreen')))
         {
            zIndex = 1000;
         }

         var singleUpdateConfig =
         {
            updateNodeRef: nodeRef.toString(),
            updateFilename: displayName,
            updateVersion: version,
            overwrite: true,
            filter: [
            {
               description: description,
               extensions: extensions
            }],
            mode: this.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadComplete,
               scope: this
            }
         };

         this.fileUpload.options.zIndex = zIndex;

         if ($isValueSet(this.options.siteId))
         {
            singleUpdateConfig.siteId = this.options.siteId;
            singleUpdateConfig.containerId = this.options.containerId;
         }
         this.fileUpload.show(singleUpdateConfig);
      },

      /**
       * Handles creating activity events after file upload completion
       *
       * @method _uploadComplete
       * @protected
       * @param complete {object} Object literal containing details of successful and failed uploads
       * @param uploadType {String} Either "added" or "updated" depending on the file action
       */
      _uploadComplete: function dlA__uploadComplete(complete, uploadType)
      {
         var success = complete.successful.length, activityData, file;
         if (success > 0)
         {
            if (success < (this.options.groupActivitiesAt || 5))
            {
               // Below cutoff for grouping Activities into one
               for (var i = 0; i < success; i++)
               {
                  file = complete.successful[i];
                  activityData =
                  {
                     fileName: file.fileName,
                     nodeRef: file.nodeRef
                  };
                  this.modules.actions.postActivity(this.options.siteId, "file-" + uploadType, "document-details", activityData);
               }
            }
            else
            {
               // grouped into one message
               activityData =
               {
                  fileCount: success,
                  path: this.currentPath,
                  parentNodeRef: this.doclistMetadata.parent.nodeRef
               };
               this.modules.actions.postActivity(this.options.siteId, "files-" + uploadType, "documentlibrary", activityData);
            }
         }
      },

      /**
       * Called from the uploader component after one or more files have been uploaded.
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function dlA_onFileUploadComplete(complete)
      {
         this._uploadComplete(complete, "added");
      },

      /**
       * Called from the uploader component after one or more files have been updated.
       *
       * @method onNewVersionUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadComplete: function dlA_onNewVersionUploadComplete(complete)
      {
         this._uploadComplete(complete, "updated");
      },

      /**
       * Cancel editing.
       *
       * @method onActionCancelEditing
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCancelEditing: function dlA_onActionCancelEditing(record)
      {
         var displayName = record.displayName;

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg("message.edit-cancel.success", displayName)
            },
            failure:
            {
               message: this.msg("message.edit-cancel.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "cancel-checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: record.jsNode.nodeRef.uri
               }
            }
         });

         YAHOO.Bubbling.fire("editingCanceled",
         {
            record: record
         });
      },
	  
     /**
       * Unlock document
       *
       * @method onActionUnlockDocument
       * @param record {object} 
       */
      onActionUnlockDocument: function dlA_onActionUnlockDocument(record)
      {
         var displayName = record.displayName;

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg("message.unlock-document.success", displayName)
            },
            failure:
            {
               message: this.msg("message.unlock-document.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "unlock-document/node/{nodeRef}",
               params:
               {
                  nodeRef: record.jsNode.nodeRef.uri
               }
            }
         });
      },

      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCopyTo: function dlA_onActionCopyTo(record)
      {
         this._copyMoveTo("copy", record);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionMoveTo: function dlA_onActionMoveTo(record)
      {
         this._copyMoveTo("move", record);
      },

      /**
       * Copy/Move To implementation.
       *
       * @method _copyMoveTo
       * @param mode {String} Operation mode: copy|move
       * @param record {object} Object literal representing the file or folder to be actioned
       * @private
       */
      _copyMoveTo: function dlA__copyMoveTo(mode, record)
      {
         // Check mode is an allowed one
         if (!mode in
            {
               copy: true,
               move: true
            })
         {
            throw new Error("'" + mode + "' is not a valid Copy/Move to mode.");
         }

         if (!this.modules.copyMoveTo)
         {
            this.modules.copyMoveTo = new Alfresco.module.DoclibCopyMoveTo(this.id + "-copyMoveTo");
         }

         var DLGF = Alfresco.module.DoclibGlobalFolder;

         var allowedViewModes =
         [
            DLGF.VIEW_MODE_RECENT_SITES,
            DLGF.VIEW_MODE_FAVOURITE_SITES,
            DLGF.VIEW_MODE_SITE,
            DLGF.VIEW_MODE_SHARED
         ];

         if (this.options.repositoryBrowsing === true)
         {
            allowedViewModes.push(DLGF.VIEW_MODE_REPOSITORY);
         }

         allowedViewModes.push(DLGF.VIEW_MODE_USERHOME)

         var zIndex = 0;
         if (this.fullscreen !== undefined && ( this.fullscreen.isWindowOnly || Dom.hasClass(this.id, 'alf-fullscreen')))
         {
            zIndex = 1000;
         }

         var repoPath = record[0] ? record[0].location.repoPath : record.location.repoPath;
         this.modules.copyMoveTo.setOptions(
         {
            allowedViewModes: allowedViewModes,
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.options.repositoryBrowsing ? repoPath : this.currentPath,
            files: record,
            /* Fix for MNT-12432. Do not overwrite this.modules.copyMoveTo.options.rootNode option if repoBrowsing is enabled. Could cause Repository tab view inconsistency */
            rootNode: this.options.repositoryBrowsing ? this.modules.copyMoveTo.options.rootNode : this.options.rootNode,
            parentId: this.getParentNodeRef(record),
            zIndex: zIndex
         }).showDialog();
      },

      /**
       * Assign workflow.
       *
       * @method onActionAssignWorkflow
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionAssignWorkflow: function dlA_onActionAssignWorkflow(record)
      {
         var nodeRefs = "",
            destination = this.getParentNodeRef(record);

         if (YAHOO.lang.isArray(record))
         {
            for (var i = 0, il = record.length; i < il; i++)
            {
               nodeRefs += (i === 0 ? "" : ",") + record[i].nodeRef;
            }
         }
         else
         {
            nodeRefs = record.nodeRef;
         }
         var postBody =
         {
            selectedItems: nodeRefs
         };
         if (destination)
         {
            postBody.destination = destination;
         }
         Alfresco.util.navigateTo($siteURL("start-workflow"), "POST", postBody);
      },

      /**
       * Set permissions on a single document or folder.
       *
       * @method onActionManagePermissions
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionManagePermissions: function dlA_onActionManagePermissions(record)
      {
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-permissions");
         }

         this.modules.permissions.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: record
         }).showDialog();
      },

      /**
       * Manage aspects.
       *
       * @method onActionManageAspects
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionManageAspects: function dlA_onActionManageAspects(record)
      {
         if (!this.modules.aspects)
         {
            this.modules.aspects = new Alfresco.module.DoclibAspects(this.id + "-aspects");
         }

         this.modules.aspects.setOptions(
         {
            file: record
         }).show();
      },

      /**
       * Change Type
       *
       * @method onActionChangeType
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionChangeType: function dlA_onActionChangeType(record)
      {
         var jsNode = record.jsNode,
            currentType = jsNode.type,
            displayName = record.displayName,
            actionUrl = Alfresco.constants.PROXY_URI + $combine("slingshot/doclib/type/node", jsNode.nodeRef.uri);

         var doSetupFormsValidation = function dlA_oACT_doSetupFormsValidation(p_form)
         {
            // Validation
            p_form.addValidation(this.id + "-changeType-type", function fnValidateType(field, args, event, form, silent, message)
            {
               return field.options[field.selectedIndex].value !== "-";
            }, null, "change", null, { validationType: "mandatory" });
         };

         // Always create a new instance
         this.modules.changeType = new Alfresco.module.SimpleDialog(this.id + "-changeType").setOptions(
         {
            width: "30em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/change-type?currentType=" + encodeURIComponent(currentType),
            actionUrl: actionUrl,
            doSetupFormsValidation:
            {
               fn: doSetupFormsValidation,
               scope: this
            },
            firstFocus: this.id + "-changeType-type",
            onSuccess:
            {
               fn: function dlA_onActionChangeType_success(response)
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     highlightFile: displayName
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.change-type.success", displayName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function dlA_onActionChangeType_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.change-type.failure", displayName)
                  });
               },
               scope: this
            }
         });
         this.modules.changeType.show();
      },

      /**
       * View in source Repository URL helper
       *
       * @method viewInSourceRepositoryURL
       * @param record {object} Object literal representing the file or folder to be actioned
       * @param actionUrls {object} Action urls for this record
       */
      viewInSourceRepositoryURL: function dlA_viewInSourceRepositoryURL(record, actionUrls)
      {
         var node = record.node,
            repoId = record.location.repositoryId,
            urlMapping = this.options.replicationUrlMapping,
            siteUrl;

         if (!repoId || !urlMapping || !urlMapping[repoId])
         {
            return "#";
         }

         // Generate a URL to the relevant details page
         siteUrl = node.isContainer ? actionUrls.folderDetailsUrl : actionUrls.documentDetailsUrl;
         // Strip off this webapp's context as the mapped one might be different
         siteUrl = siteUrl.substring(Alfresco.constants.URL_CONTEXT.length);

         return $combine(urlMapping[repoId], "/", siteUrl);
      },

      /**
       * CLOUD SYNC
       */

      /**
       * Create Sync
       * loads folder picker populated with networks, sites and folders from The Cloud.
       *
       * @method onActionCloudSync
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCloudSync: function dlA_onActionCloudSync(record)
      {
         // Instantiate Cloud Folder Picker & Cloud Auth Dialogue if they don't exist
         if (!cloudFolderPicker)
         {
            cloudFolderPicker = new Alfresco.module.DoclibCloudFolder(this.id + "-cloud-folder");

            var me = this;

            // Set up handler for when the sync location has been chosen:
            YAHOO.Bubbling.on("folderSelected", function cloudSync_onCloudFolderSelected(event, args)
            {
               this.updateSyncOptions();

               Alfresco.util.Ajax.jsonPost(
               {
                  url: Alfresco.constants.PROXY_URI + "enterprise/sync/syncsetdefinitions",
                  dataObj: YAHOO.lang.merge(this.options.syncOptions,
                  {
                     memberNodeRefs: me.getMemberNodeRefs(this.options.files),
                     remoteTenantId: this.options.targetNetwork,
                     targetFolderNodeRef: args[1].selectedFolder.nodeRef
                  }),
                  successCallback: {
                     fn: function cloudSync_onCloudFolderSelectedSuccess()
                     {
                        YAHOO.Bubbling.fire("metadataRefresh");
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.sync.success")
                        });
                     },
                     scope: this
                  },
                  failureMessage: this.msg("message.sync.failure")
               })
            }, cloudFolderPicker);
         }
         else
         {
            var optionInputs = cloudFolderPicker.widgets.optionInputs;
            if (optionInputs)
            {
               for (var i = 0; i < optionInputs.length; i++)
               {
                  optionInputs[i].checked = optionInputs[i].defaultChecked;
               }
            }
         }

         if(!this.modules.cloudAuth)
         {
            this.modules.cloudAuth = new Alfresco.module.CloudAuth(this.id + "cloudAuth");
         }

         cloudFolderPicker.setOptions(
         {
            files: record
         });

         this.modules.cloudAuth.setOptions(
         {
            authCallback: cloudFolderPicker.showDialog,
            authCallbackContext: cloudFolderPicker
         }).checkAuth();
      },

      /**
       * Remove Sync
       * loads folder picker populated with networks, sites and folders from The Cloud.
       *
       * @method onActionCloudUnsync
       * @param record {object} Object literal representing the file or folder to be actioned
       */
      onActionCloudUnsync: function dlA_onActionCloudUnsync(record)
      {
         var me = this,
            content = record.jsNode.isContainer ? "folder" : "document",
            displayName = record.displayName,
            isCloud = (this.options.syncMode === "CLOUD"),
            deleteRemoteFile = isCloud ? "" : '<div><input type="checkbox" id="requestDeleteRemote" class="requestDeleteRemote-checkBox"><span class="requestDeleteRemote-text">' + this.msg("sync.remove." + content + ".from.cloud", displayName) + '</span></div>';

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("actions." + content + ".cloud-unsync"),
            noEscape: true,
            text: this.msg("message.unsync.confirm", displayName) + deleteRemoteFile,
            buttons: [
            {
               text: this.msg("button.unsync"),
               handler: function dlA_onActionCloudUnsync_unsync()
               {
                  var requestDeleteRemote = isCloud ? false : Dom.getAttribute("requestDeleteRemote", "checked");
                  this.destroy();
                  Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + "enterprise/sync/syncsetmembers/" + record.jsNode.nodeRef.uri + "?requestDeleteRemote=" + requestDeleteRemote,
                     method: Alfresco.util.Ajax.DELETE,
                     successCallback: {
                        fn: function cloudSync_onCloudUnsync_success()
                        {
                           YAHOO.Bubbling.fire("metadataRefresh");
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: me.msg("message.unsync.success")
                           })
                        },
                        scope: me
                     },
                     failureMessage: me.msg("message.unsync.failure")
                  });
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function dlA_onActionCloudUnsync_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Triggered when the Cloud Sync Icon is clicked
       * Shows the status and location in cloud.
       *
       * @method onCloudSyncIndicatorAction
       * @param record {object} Object literal representing the file or folder to be actioned
       * @param target {HTML DOM Element} HTML Element that was the target of the initial action.
       */
      onCloudSyncIndicatorAction: function dlA_onCloudSyncIndicatorAction(record, target)
      {
         var balloon = new Alfresco.util.createInfoBalloon(this.widgets.dataTable.getTrEl(target),
         {
            text: this.msg("label.loading"),
            width: "455px"
         });

         // Show Balloon with initial message:
         balloon.show();

         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/doclib2/node/"  + record.nodeRef.replace('://', '/'),
            successCallback:
            {
               fn: function onCloudSyncGettingNodeDetailsAction_success(response)
               {
                  var me = this,
                     configOptions =
                  {
                     showTitle: true,
                     showRequestSyncButton: true,
                     showUnsyncButton: true,
                     showMoreInfoLink: true
                  };

                  Alfresco.util.getSyncStatus(this, record, response.json, configOptions, function(callbackResult)
                  {
                     if (callbackResult != null)
                     {
                        // Render Error Banner
                        balloon.html(callbackResult.html);

                        balloon.requestsync = Alfresco.util.createYUIButton(me, "button-requestsyn", function()
                        {
                           me.onActionCloudSyncRequest(record);
                           balloon.hide();
                        },
                        {
                           id: me.id
                        });
                        if (!callbackResult.showRequestSyncButton && balloon.requestsync != null)
                        {
                           balloon.requestsync.setStyle('display', 'none');
                        }

                        balloon.unsync = Alfresco.util.createYUIButton(me, "button-unsync", function()
                        {
                           me.onActionCloudUnsync(record);
                           balloon.hide();
                        },
                        {
                           id: me.id
                        });
                        if (!callbackResult.showUnsyncButton && balloon.unsync != null)
                        {
                           balloon.unsync.setStyle('display', 'none');
                        }

                        var root = balloon.content;
                        Alfresco.util.syncClickOnShowDetailsLinkEvent(me, root);
                        Alfresco.util.syncClickOnHideLinkEvent(me, root);
                        Alfresco.util.syncClickOnTransientErrorShowDetailsLinkEvent(me, root);
                        Alfresco.util.syncClickOnTransientErrorHideLinkEvent(me, root);
                     }
                     else
                     {
                        balloon.hide();
                     }
                  });
               },
               scope: this
            },
            failureCallback:
            {
               fn: function onCloudSyncGettingNodeDetailsAction_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("sync.unable.get.details")
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Request Sync
       *
       * @method onActionCloudSyncRequest
       * @param record {object} Object literal representing the file or folder to be actioned
       * @param target {HTML DOM Element} HTML Element that was the target of the initial action.
       */
      onActionCloudSyncRequest: function dlA_onActionCloudSyncRequest(record, target)
      {
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "enterprise/sync/syncrequest",
            dataObj:
            {
               memberNodeRefs: this.getMemberNodeRefs(record)
            },
            successCallback: {
               fn: function cloudSync_onActionCloudSyncRequest_success()
               {
                  YAHOO.Bubbling.fire("metadataRefresh");
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.request.sync.success")
                  })
               },
               scope: this
            },
            failureMessage: this.msg("message.request.sync.failure")
         })
      },

      /**
       * Helper method for getting the MemberNodeRefs from an object
       *
       * @method getMemberNodeRefs
       * @param record {object} Object literal representing one file or folder to be actioned
       * @return {object} An array of MemberNodeRefs
       */
      getMemberNodeRefs: function dlA_onGetMemberNodeRefs(record)
      {
         var memberNodeRefs = new Array();
         if (YAHOO.lang.isArray(record))
         {
            for (var i in record)
            {
               memberNodeRefs.push(record[i].nodeRef);
            }
         }
         else
         {
            memberNodeRefs.push(record.nodeRef);
         }
         return memberNodeRefs;
      },

      /**
       * Triggered when the Cloud Sync Failed Icon is clicked
       * Shows the status and location in cloud.
       *
       * @method onCloudSyncFailedIndicatorAction
       * @param record {object} Object literal representing the file or folder to be actioned
       * @param target {HTML DOM Element} HTML Element that was the target of the initial action.
       */
      onCloudSyncFailedIndicatorAction: function dlA_onCloudSyncFailedIndicatorAction(record, target)
      {
         this.onCloudSyncIndicatorAction(record, target);
      },

      /**
       * Triggered when the Cloud Indirect Sync Icon is clicked
       * Shows the status and location in cloud.
       *
       * @method onCloudIndirectSyncIndicatorAction
       * @param record {object} Object literal representing the file or folder to be actioned
       * @param target {HTML DOM Element} HTML Element that was the target of the initial action.
       */
      onCloudIndirectSyncIndicatorAction: function dlA_onCloudIndirectSyncIndicatorAction(record, target)
      {
         this.onCloudSyncIndicatorAction(record, target);
      },
      onCloudIndirectSyncFailedIndicatorAction: function dlA_onCloudIndirectSyncFailedIndicatorAction(record, target)
      {
         this.onCloudSyncIndicatorAction(record, target);
      },

      /**
       * Triggers the archiving and download of a single folders contents
       *
       * @method onActionFolderDownload
       * @param record {object} Object literal representing the folder to be actioned
       */
      onActionFolderDownload: function dlA_onActionFolderDownload(record) {

         var downloadDialog = Alfresco.getArchiveAndDownloadInstance(),
             config = { nodesToArchive: [{"nodeRef": record.nodeRef}],
                        archiveName: record.fileName };
         downloadDialog.show(config);
      },

      /**
       * Triggers the archiving and download of the currently selected documents/folders.
       *
       * @method onActionDownload
       * @param record {array} The list of selected records.
       */
      onActionDownload: function dla_onActionDownload(record) {
         var downloadDialog = Alfresco.getArchiveAndDownloadInstance(),
             config = { nodesToArchive: [] };

         if (record.length == 1)
         {
            config.nodesToArchive.push({"nodeRef": record[0].nodeRef});
            config.archiveName = record[0].fileName;
         }
         else
         {
            for (var i=0; i<record.length; i++)
            {
               config.nodesToArchive.push({"nodeRef": record[i].nodeRef})
            }
         }
         downloadDialog.show(config);
      }
   };
})();