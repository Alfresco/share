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
 * A Folder picker that works against the Alfresco Cloud.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibCloudFolder
 */
(function()
{

   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   Alfresco.module.DoclibCloudFolder = function(htmlId)
   {
      Alfresco.module.DoclibCloudFolder.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.module.DoclibCloudFolder";
      Alfresco.util.ComponentManager.reregister(this);

      this.options = YAHOO.lang.merge(this.options,
      {
         allowedViewModes:
         [
            Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE
         ],
         targetNetwork: "-default-",
         targetUserid: '',
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/cloud-folder",
         sitesAPITemplate: Alfresco.constants.PROXY_URI + "cloud/people/{userid}/sites?network={network}",
         containersAPITemplate: Alfresco.constants.PROXY_URI + "cloud/doclib/containers/?network={network}",
         treeNodeAPITemplate: "cloud/doclib/treenode/site/{site}/{container}{path}?children={evaluateChildFoldersSite}&max={maximumFolderCountSite}&network={network}",
         templateFailMessage: Alfresco.util.message("message.sync.unavailable"),
         syncOptions: {},
         showSyncOptions: true,
         mode: 'sync'
      });

      this.updateAPIURLs();

      if (htmlId != "null")
      {
         try 
         {
            YAHOO.Bubbling.unsubscribe("networkSelected", null, this);
            YAHOO.Bubbling.unsubscribe("authDetailsAvailable", null, this);
         }
         catch(err)
         {
            /*ignore, because error is thrown when event isn't registred*/
         };

         YAHOO.Bubbling.on("networkSelected", this._populateSitePicker, this);
         YAHOO.Bubbling.on("authDetailsAvailable", this.onAuthDetailsAvailable, this);
      }

      return this;
   };

   YAHOO.extend(Alfresco.module.DoclibCloudFolder, Alfresco.module.DoclibGlobalFolder,
   {
      /**
       * Overrides DLGF_onTemplateLoaded to add new functionality
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function cloudFolder_onTemplateLoaded(response)
      {
         if (response.serverResponse.status === 204)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("sync.message.no.active.network")
            });
         }
         else
         {
            Alfresco.module.DoclibCloudFolder.superclass.onTemplateLoaded.call(this, response);
         }
      },
      
      /**
       * Helper method for finding if the selection includes a folder
       *
       * @method _selectionIncludesFolder
       * @private
       */
      _selectionIncludesFolder: function cloudFolder__includesSelectionFolder()
      {
         var files = this.options.files;
         if (YAHOO.lang.isArray(files))
         {
            for (var i in files)
            {
               if (files[i].jsNode.isContainer)
               {
                  return true;
               }
            }
            return false;
         }
         else
         {
            return files.jsNode.isContainer;
         }
      },
      
      /**
       * Overrides DLGF__beforeShowDialog to add new functionality
       *
       * @method _beforeShowDialog
       * @private
       */
      _beforeShowDialog: function cloudFolder__beforeShowDialog()
      {
         Alfresco.module.DoclibCloudFolder.superclass._beforeShowDialog.call(this);
         
         if(!this.options.showSyncOptions)
         {
            var optionDiv = Dom.getElementsByClassName("cloud-options", "div", this.id + "-wrapper");
            Dom.addClass(optionDiv[0], "hidden");
         }
         
         this.widgets.optionInputs = Dom.getElementsByClassName("cloudSyncOption", "input", this.id + "-wrapper");
         var optionInputs = this.widgets.optionInputs;
         for (var i in optionInputs)
         {        
            if (!this._selectionIncludesFolder() && optionInputs[i].id === "includeSubFolders")
            {
               Dom.addClass(Dom.get(optionInputs[i].id + "-label"), "hidden");
               break;
            }
            else if (Dom.hasClass(Dom.get(optionInputs[i].id + "-label"), "hidden"))
            {
               Dom.removeClass(Dom.get(optionInputs[i].id + "-label"), "hidden");
               break;
            }
         }
      },

      /**
       *
       * Overrides DLGF__showDialog to add new functionality
       *
       * @method _showDialog
       * @private
       */
      _showDialog: function cloudFolder__showDialog()
      {
         Alfresco.module.DoclibCloudFolder.superclass._showDialog.call(this);

         if (!this.widgets.networkButtons) // Only run this once.
         {
            this.widgets.networkButtons = new YAHOO.widget.ButtonGroup(this.id + "-networkGroup");
            this.widgets.networkButtons.on("checkedButtonChange", this.onNetworkSelect, this.widgets.networkButtons, this);
         }

         this.onNetworkSelect(null, this.widgets.networkButtons);

         var cloudCreateFolderButtonEl = Dom.getElementsByClassName("cloud-path-add-folder", "div");

         // Add click listener to create folder button, but ensure it only registers once (See ACE-4791).
         // Remove fails silently if listener isn't registered.
         Event.removeListener(cloudCreateFolderButtonEl, "click");
         Event.on(cloudCreateFolderButtonEl, "click", function onCreateFolder(event) {
            Event.preventDefault(event);
            this.createFolderInTheCloud();
         }, {}, this);
      },

      createFolderInTheCloud: function cloudFolder_createFolderInTheCloud()
      {
         // If an instance of the dialog (for creating a folder in the cloud) does not exist create one
         if (!this.widgets.createFolderInTheCloudDialog)
         {
            // Build template url
            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
            {
               itemKind: "type",
               itemId: "cm:folder",
               mode: "create",
               submitType: "json",
               formId: "doclib-common"
            });
            
            // Intercept before dialog show
            var doBeforeDialogShow = function cloudFolder_onNewFolder_doBeforeDialogShow(p_form, p_dialog)
            {
               Dom.get(p_dialog.id + "-dialogTitle").innerHTML = this.msg("sync.new-folder.in-the-cloud.title");
               Dom.get(p_dialog.id + "-dialogHeader").innerHTML = this.msg("sync.new-folder.in-the-cloud.header");
            };

            // Intercept before form submit. The cancel button won't be disabled, because an instance of the dialog won't be created each time
            var doBeforeFormSubmit = function cloudFolder_doBeforeFormSubmit(args)
            {
               this.widgets.cancelButton.set("disabled", false);
            };

            var doAfterDialogHide = function cloudFolder_doAfterDialogHide(args)
            {
               delete this.widgets.createFolderInTheCloudDialog;
            };
            
            // Intercept before making ajax request
            var doBeforeAjaxRequest = function cloudFolder_doBeforeAjaxRequest(args)
            {
               // The destination where the folder should be created
               var destination = (this.selectedNode.data.nodeRef).replace(":/", "");

               // The network, in which the folder should be created
               var network = "";
               var networkButtons = this.widgets.networkButtons.getButtons();
               for (var i in networkButtons)
               {
                  if (networkButtons[i].get("checked"))
                  {
                     network = networkButtons[i]._button.innerHTML;
                     break;
                  }
               }

               // The URL for creating the folder
               var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "cloud/node/folder/" + "{destination}" + "?network=" + "{network}",
               {
                  destination: destination,
                  network: network
               });

               // The data object, which contains the needed information
               var dataObj =
               {
                  name: args.dataObj.prop_cm_name,
                  title: args.dataObj.prop_cm_title,
                  description: args.dataObj.prop_cm_description
               }

               Alfresco.util.Ajax.jsonPost(
               {
                  url: url,
                  dataObj: dataObj,
                  successCallback:
                  {
                     fn: function cloudFolder_createFolderRequest_success(response)
                     {
                        this.widgets.createFolderInTheCloudDialog.hide();
                        
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("sync.new-folder.creation.success"),
                           displayTime: 0.5
                        });

                        var pathSeparator = "/";
                        // Split the selected path
                        var paths = this.selectedNode.data.path.split(pathSeparator);
                        // Remove the empty element(s) from the array
                        paths = Alfresco.util.arrayRemove(paths, "");
                        // Add the new folder to the paths
                        paths.push(response.config.dataObj.name);

                        var expandPath = "";
                        this.pathsToExpand = [];
                        for (var i = 0, j = paths.length; i < j; i++)
                        {
                           // Push the path onto the list of paths to be expanded
                           expandPath += pathSeparator + paths[i];
                           this.pathsToExpand.push(expandPath);
                        }

                        // Fire the siteChanged event to refresh the list of folders
                        YAHOO.Bubbling.fire("siteChanged",
                        {
                           site: this.options.siteId,
                           siteTitle: this.options.siteTitle,
                           eventGroup: this,
                           scrollTo: true
                        });
                     },
                     scope: this
                  },
                  failureCallback:
                  {
                     fn: function cloudFolder_createFolderRequest_failure(response)
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("sync.new-folder.creation.failure")
                        });
                        this.widgets.createFolderInTheCloudDialog.widgets.okButton.set("disabled", false);
                     },
                     scope: this
                  }
               });
            };
            
            // Create an instance
            this.widgets.createFolderInTheCloudDialog = new Alfresco.module.SimpleDialog(this.id + "-createFolderInTheCloud");
   
            // Set the options
            this.widgets.createFolderInTheCloudDialog.setOptions(
            {
               width: "33em",
               templateUrl: templateUrl,
               actionUrl: null,
               clearForm: true,
               destroyOnHide: false,
               doBeforeFormSubmit:
               {
                  fn: doBeforeFormSubmit,
                  scope: this
               },
               doBeforeDialogShow:
               {
                  fn: doBeforeDialogShow,
                  scope: this
               },
               doBeforeAjaxRequest:
               {
                  fn: doBeforeAjaxRequest,
                  scope: this
               },
               doAfterDialogHide: 
               {
                  fn: doAfterDialogHide,
                  scope: this
               }
            });
         }

         // Show the dialog for creating a folder in the cloud
         this.widgets.createFolderInTheCloudDialog.show();
      },

      /**
       * Overrides setViewMode
       * @param event
       * @param args
       */
      setViewMode: function cloudFolder_setViewMode(viewMode)
      {
         this.options.viewMode = viewMode;
         Dom.removeClass(this.id + "-wrapper", "repository-mode");
      },

      /**
       *
       * Triggered when the user selects a network.
       *
       * @method onNetworkSelect
       */
      onNetworkSelect: function cloudFolder_onNetworkSelect(event, args)
      {
         // Update Sites API
         this.options.targetNetwork = args.get("checkedButton").get("name");
         this.updateAPIURLs();
         this.options.siteId = null;
         YAHOO.Bubbling.fire("networkSelected");
      },

      updateAPIURLs: function updateAPIURLs()
      {
         var substitutionOptions =
         {
            network: this.options.targetNetwork,
            userid: this.options.targetUserid
         };

         this.options.sitesAPI = YAHOO.lang.substitute(this.options.sitesAPITemplate, substitutionOptions);
         // TODO: validate if global-folder.js should use sitesAPI instead of peopleAPI
         this.options.peopleAPI = YAHOO.lang.substitute(this.options.sitesAPITemplate, substitutionOptions);
         this.options.containersAPI = YAHOO.lang.substitute(this.options.containersAPITemplate, substitutionOptions)
         this.options.siteTreeContainerTypes =
         {
            "cm:folder":
            {
               uri: YAHOO.lang.substitute(this.options.treeNodeAPITemplate, substitutionOptions)
            }
         };
      },

      updateSyncOptions: function updateSyncOptions()
      {
         for (var i = 0; i < this.widgets.optionInputs.length; i++)
         {
            // Read the state and property name for each option
            var property = Dom.getAttribute(this.widgets.optionInputs[i], "value"),
               state = Dom.getAttribute(this.widgets.optionInputs[i], "checked");

            // Set option.
            this.options.syncOptions[property] = state;
         }
      },

      /**
       * Overrides onExpandComplete
       *
       * Fired by YUI TreeView when a node has finished expanding
       * @method onExpandComplete
       * @param oNode {YAHOO.widget.Node} the node recently expanded
       */
      onExpandComplete: function cloudFolder_onExpandComplete(oNode)
      {
         var addFolder = Dom.getElementsByClassName("cloud-path-add-folder", "div")[0];  
         if (oNode.data.userAccess.create)
         {
	        addFolder.style.display = 'block';
         }
         else
         {
            addFolder.style.display = 'none';
         }
         
         if (this.pathsToExpand != null)
         {
            var node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
            if (node != null)
            {
               node.expand();
               this._updateSelectedNode(node);
            }
         }
      },

      /**
       * Called once the Auth details have been returned
       *
       * @method onRemoveListItem
       * @param event {string} Event fired
       * @param args {object} Event parameters
       */
      onAuthDetailsAvailable: function cloudFolder_onAuthDetailsAvailable(event, args)
      {
         this.options.targetUserid = args[1].authDetails.username;
      }

   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DoclibCloudFolder("null");
})();