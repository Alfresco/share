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
 * Alfresco.WikiPage
 * 
 * @namespace Alfresco
 * @class Alfresco.WikiPage
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
    
   /**
    * WikiPage constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.WikiPage} The new Wiki instance
    * @constructor
    */
   Alfresco.WikiPage = function(htmlId)
   {
      Alfresco.WikiPage.superclass.constructor.call(this, "Alfresco.WikiPage", htmlId, ["button", "container", "connection", "editor", "tabview"]);
      this.selectedTags = [];
      this.parser = new Alfresco.WikiParser();
      return this;
   };

   YAHOO.extend(Alfresco.WikiPage, Alfresco.component.Base,
   {

      /**
       * Currently selected tags.
       *
       * @property selectedTags
       * @type array
       */
      selectedTags: null,
      
      /**
       * An instance of a Wiki parser for this page.
       * 
       * @property parser
       * @type Alfresco.WikiParser
       */
      parser: null,
      
      /**
       * Flag to indicate the user is forcing a save (newer version overwrite)
       * 
       * @property forceSave
       * @type bool
       * @default false
       */
      forceSave: false,
      
      /**
       * Saving page dialog popup
       * 
       * @property savingPagePopup
       * @type object
       * @default null
       */
      savingPagePopup: null,
      
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          * @default ""
          */
         siteId: "",

         /**
          * The posts title.
          *
          * @property pageTitle
          * @type string
          * @default ""
          */
         pageTitle: "",

         /**
          * The display mode
          *
          * @property mode
          * @type string
          * @default "view"
          */
         mode: "view",

         /**
          * Set to true if error exist
          *
          * @property error
          * @type boolean
          * @default false
          */
         error: false,

         /**
          * Tags for the wiki post.
          *
          * @property tags
          * @type array
          * @default []
          */
         tags: [],

         /**
          * Pages linked to from the wiki post.
          *
          * @property pages
          * @type array
          * @default []
          */
         pages: [],

         /**
          * Versions of this the post.
          *
          * @property versions
          * @type array
          * @default []
          */
         versions: [],

         /**
          * Permissions for the current user for the wiki post.
          *
          * @property permissions
          * @type array
          * @default []
          */
         permissions: {},

         /**
          * The current users locale
          *
          * @property locale
          * @type string
          * @default ""
          */
         locale: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WikiPage_onReady()
      {
         if (this.options.error)
         {
            // Site or container not found - deactivate controls
            YAHOO.Bubbling.fire("deactivateAllControls");
            return;
         }

         if (this.options.mode === "edit")
         {
            this._setupEditForm();
         }
         else if (this.options.mode === "details")
         {
            this._setupPageDetails();
         }
         
         //Append the Wiki page title to page title
         document.title += $html(" \u00BB " + this.options.pageTitle);

         // Content area
         var pageText = Dom.get(this.id + "-page");
         if (pageText)
         {
            this.parser.URL = this._getAbsolutePath();
            // Format any wiki markup
            pageText.innerHTML = this.parser.parse(pageText.innerHTML, this.options.pages);
         }
         
         // Fire permissions event to allow other components to update their UI accordingly
         YAHOO.Bubbling.fire("userAccess",
         {
            userAccess: this.options.permissions
         });
      },
      
      /**
       * Configure the page for "details" mode
       *
       * @method _setupPageDetails
       * @private
       */
      _setupPageDetails: function WikiPage__setupPageDetails()
      {
         var versions = this.options.versions;

         // Versioning drop down
         if (versions.length > 0)
         {
            this.widgets.versionSelect = Alfresco.util.createYUIButton(this, "selectVersion-button", this.onVersionSelectChange,
            {
               type: "menu",
               menu: "selectVersion-menu"
            });
         }

         // Listen on clicks for revert version icons
         var version, i, j, expandDiv, moreVersionInfoDiv;
         
         for (i = 0, j = versions.length; i < j; i++)
         {
            var revertSpan = Dom.get(this.id + "-revert-span-" + i);
            if (revertSpan)
            {
               Event.addListener(revertSpan, "click", function (event, obj)
               {
                  // Find the index of the version link by looking at its id
                  version = versions[obj.versionIndex];

                  // Find the version through the index and display the revert dialog for the version
                  Alfresco.module.getRevertWikiVersionInstance().show(
                  {
                     siteId: obj.siteId,
                     pageTitle: obj.pageTitle,
                     version: version.label,
                     versionId: version.versionId,
                     onRevertWikiVersionComplete:
                     {
                        fn: this.onRevertWikiVersionComplete,
                        scope: this
                     }
                  });
               },
               {
                  siteId: this.options.siteId,
                  pageTitle: this.options.pageTitle,
                  versionIndex: i
               }, this);
            }

            // Listen on clicks on the version - date row so we can expand and collapse it
            expandDiv = Dom.get(this.id + "-expand-div-" + i);
            moreVersionInfoDiv = Dom.get(this.id + "-moreVersionInfo-div-" + i);
            
            if (expandDiv)
            {
               Event.addListener(expandDiv, "click", function (event, obj)
               {
                  if (obj.moreVersionInfoDiv && Dom.hasClass(obj.expandDiv, "collapsed"))
                  {
                     Alfresco.util.Anim.fadeIn(obj.moreVersionInfoDiv);
                     Dom.removeClass(obj.expandDiv, "collapsed");
                     Dom.addClass(obj.expandDiv, "expanded");
                  }
                  else
                  {
                     Dom.setStyle(obj.moreVersionInfoDiv, "display", "none");
                     Dom.removeClass(obj.expandDiv, "expanded");
                     Dom.addClass(obj.expandDiv, "collapsed");
                  }
               },
               {
                  expandDiv: expandDiv,
                  moreVersionInfoDiv: moreVersionInfoDiv
               }, this);
            }

            // Format and display the createdDate
            Dom.get(this.id + "-createdDate-span-" + i).innerHTML =
               Alfresco.util.formatDate(Alfresco.thirdparty.fromISO8601(versions[i].createdDate), this.msg("date-format.longDate"));
         }
      },

      /**
       * Fired by the Revert Version component after a successful revert.
       *
       * @method onRevertWikiVersionComplete
       */
      onRevertWikiVersionComplete: function WikiPage_onRevertWikiVersionComplete()
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("message.revertComplete", this.name)
         });

         window.location.reload();
      },

      /**
       * Called via init if the page is in edit mode
       *
       * @method _setupEditForm
       */
      _setupEditForm: function WikiPage__setupEditForm()
      {
         var width = Dom.get(this.id + "-form").offsetWidth - 400;
         var height = YAHOO.env.ua.ie > 0 ? document.body.clientHeight : document.height;
         this.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.tagLibrary.setOptions(
         {
            siteId: this.options.siteId
         });
         if (this.options.tags.length > 0)
         {
            this.tagLibrary.setTags(this.options.tags);
         }
         
         // Form buttons
         var saveButton = new YAHOO.widget.Button(this.id + "-save-button",
         {
            type: "submit"
         });
         Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelSelect);
         
         // TinyMCE
         var me = this;
         this.pageEditor = Alfresco.util.createImageEditor(this.id + '-content',
         {
            height: 300,
            toolbar: "styleselect | bold italic | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | print preview fullscreen | alfresco-imagelibrary alfresco-linklibrary",
            extended_valid_elements : "style[type]",
            valid_children : "+body[style]",
            siteId: this.options.siteId,
            language: this.options.locale,
            init_instance_callback: function(o) {
               // must fire the "editorInitialized" as that is what the default init_instance_callback behaviour would do
               YAHOO.Bubbling.fire("editorInitialized", o);
               // save key behaviour - stop the outer document event and save the Wiki form
               me.pageEditor.addSaveKeyBehaviour(function(id, e) {
                  Event.stopEvent(e[1]);
                  saveButton.fireEvent('click', {
                     type: 'click'
                  });
               });
            }
         });
         this.pageEditor.addPageUnloadBehaviour(this.msg("message.unsavedChanges.wiki"));
         this.pageEditor.render();

         // create the form that does the validation/submit
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.setSubmitElements(saveButton);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageUpdated,
               scope: this
            },
            failureCallback:
            {
               fn: function(data, form)
               {
                  // Remove the Saving... popup
                  if (this.savingPagePopup)
                  {
                     this.savingPagePopup.destroy();
                     this.savingPagePopup = null;
                  }

                  // See if the error was a versino conflict
                  if (data.serverResponse.status == 409)
                  {
                     var me = this;
                     
                     // Version conflict, so let the user decide what to do
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        text: this.msg("message.confirm.newerVersion"),
                        buttons: [
                        {
                           text: this.msg("button.savechanges"),
                           handler: function Wiki_submit_forceSave()
                           {
                              // Set the "force save" flag and re-submit
                              this.destroy();
                              me.forceSave = true;
                              saveButton.fireEvent('click',
                              {
                                 type: 'click'
                              });
                           }
                        },
                        {
                           text: this.msg("button.cancel"),
                           handler: function Wiki_submit_cancel()
                           {
                              this.destroy();
                           },
                           isDefault: true
                        }]
                     });
                  }
                  else if (data.serverResponse.status == 401)
                  {
                     // Unauthenticated, which is probably due to a web-tier timeout or restart
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.sessionTimeout.title"),
                        text: this.msg("message.sessionTimeout.text")
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: data.json.message
                     });
                  }
               },
               scope: this,
               obj: form
            },
            noReloadOnAuthFailure: true
         });
       
         form.setSubmitAsJSON(true);
         form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         form.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // Display pop-up to indicate that the page is being saved
               this.savingPagePopup = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.saving", this.name)) + '</span>',
                  noEscape: true
               });
               
               // Put the HTML back into the text area
               this.pageEditor.save();
               // Update the tags set in the form
               this.tagLibrary.updateForm(this.id + "-form", "tags");
               
               // Avoid submitting the input field used for entering tags
               var tagInputElem = Dom.get(this.id + "-tag-input-field");
               if (tagInputElem)
               {
                  tagInputElem.disabled = true;
               }
            },
            scope: this
         };
         form.doBeforeAjaxRequest =
         {
            fn: function(config, obj)
            {
               if (this.forceSave)
               {
                  // Set the "force save" flag on the JSON request
                  this.forceSave = false;
                  config.dataObj["forceSave"] = true;
               }
               return true;
            },
            scope: this
         };
         
         this.tagLibrary.initialize(form);
         form.init();
         
         YAHOO.Bubbling.on("onTagLibraryTagsChanged", this.onTagLibraryTagsChanged, this);
      },
      
      /**
       * Called when tag library tags have been updated
       *
       * @method onTagLibraryTagsChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onTagLibraryTagsChanged: function WikiPage_onTagLibraryTagsChanged(layer, args)
      {
         this.selectedTags = args[1].tags;
      },

      /**
       * Called when the user selects a version in the version list
       *
       * @method onVersionSelectChange
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onVersionSelectChange: function WikiPage_onVersionSelectChange(sType, aArgs, p_obj)
      {
         var versionId = aArgs[1].value;
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/wiki/version/{site}/{title}/{version}",
         {
            site: this.options.siteId,
            title: encodeURIComponent(this.options.pageTitle),
            version: versionId
         });
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: actionUrl,
            successCallback:
            {
               fn: this.onVersionInfo,
               scope: this,
               obj:
               {
                  index: aArgs[1].index
               }
            },
            failureMessage: "Could not retrieve version information"
         });
      },

      /**
       * Called when the content for a new version has been loaded
       * (because of a user click in the version select menu).
       *
       * @method onVersionInfo
       * @param event {object} event from Alfresco.ajax.request
       * @param obj {object} contians the index of the selected version in the versions array
       */
      onVersionInfo: function WikiPage_onVersionInfo(event, obj)
      {        
         // Show the content
         var page = Dom.get(this.id + "-page");
         page.innerHTML = this.parser.parse(event.serverResponse.responseText, this.options.pages);

         // Update the version label in the header
         var versionHeaderSpan = Dom.get(this.id + "-version-header");
         if (versionHeaderSpan)
         {
            versionHeaderSpan.innerHTML = this.msg("label.shortVersion", this.name) + this.options.versions[obj.index].label;
         }

         // Update the label in the version select menu
         var label = this.options.versions[obj.index].label;
         if (obj.index == 0)
         {
            label += " (" + this.msg("label.latest") + ")";
         }
         this.widgets.versionSelect.set("label", label);
      },
      
      /**
       * Returns the absolute path (URL) to a wiki page, minus the title of the page.
       *
       * @method _getAbsolutePath
       */
      _getAbsolutePath: function WikiPage__getAbsolutePath()
      {
         return Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/" + Alfresco.constants.PAGEID + "?title=";
      },
      
      /*
       * Gets called when the user cancels an edit in progress.
       * Returns the user to the page view of a page.
       *
       * @method onCancelSelect
       * @param e {object} Event fired
       */
      onCancelSelect: function WikiPage_onCancelSelect(e)
      {
         this.pageEditor.clearDirtyFlag();
         this._redirect();
      },
      
      /*
       * Event handler that gets fired when a page is successfully updated.
       * This follows the "onSaveSelect" and "onRevert" event handlers.
       * 
       * @method onPageUpdated
       * @param e {object} Event fired
       */
      onPageUpdated: function WikiPage_onPageUpdated(e)
      {
         this.pageEditor.clearDirtyFlag();
         this._redirect();
      },
      
      /*
       * Redirect browser to current pageTitle page
       * 
       * @method _redirect
       */
      _redirect: function WikiPage__redirect()
      {
         var url = this._getAbsolutePath() + encodeURIComponent(this.options.pageTitle);
         window.location = url;   
      }

   });
})();
