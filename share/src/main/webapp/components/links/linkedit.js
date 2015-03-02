/**
 * LinkEdit component.
 * 
 * Component provides link creation/edit functionality.
 * 
 * @namespace Alfresco
 * @class Alfresco.LinkEdit
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
    * LinkEdit constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.LinkEdit} The new Link instance
    * @constructor
    */
   Alfresco.LinkEdit = function(htmlId)
   {
      return Alfresco.LinkEdit.superclass.constructor.call(this, "Alfresco.LinkEdit", htmlId, ["json", "button", "menu"]);
   };
   
   YAHOO.extend(Alfresco.LinkEdit, Alfresco.component.Base,
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "links"
          */
         containerId: "links",
         
         /**
          * True if the component should be in edit mode.
          */
         editMode: false,
         
         /**
          * Id of the link to edit. Only relevant if editMode is true
          */
         linkId: ""
      },

      /**
       * Stores the data of the currently edited link
       */
      linkData: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function LinkEdit_onReady()
      {
         if (this.options.editMode)
         {
            // load the link data prior to initializing the form
            this._loadLinkData();
         }
         else
         {
            // directly initialize the form
            this._initializeLinkForm();
         }
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadLinkData: function LinkEdit__loadLinkData()
      {
         // ajax request success handler
         var me = this;
         var loadLinkDataSuccess = function CommentsList_loadCommentsSuccess(response)
         {
            // set the link data
            var data = response.json.item;
            me.linkData = data;
            
            // now initialize the form, which will use the data we just loaded
            me._initializeLinkForm();
         };
         
         // construct the request url
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "/api/links/link/site/{site}/{container}/{linkId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            linkId: this.options.linkId
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "GET",
            responseContentType : "application/json",
            successCallback:
            {
               fn: loadLinkDataSuccess,
               scope: this
            },
            failureMessage: this.msg("message.loadlinkdata.failure")
         });
      },

      /**
       * Initializes the link form with create/edit dependent data.
       */
      _initializeLinkForm: function LinkEdit__initializeLinkForm()
      {
         // construct the actionUrl, which is different for creating/updating a link
         var actionUrl = "";
         if(this.options.editMode){
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/site/{site}/{container}/{linkId}",
            {
               site: this.options.siteId,
               container : this.options.containerId,
               linkId : this.options.linkId
            });
         }
         else
         {
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/links/site/{site}/{container}/posts",
            {
               site: this.options.siteId,
               container : this.options.containerId
            });
         }
         var form = Dom.get(this.id + '-form');
         form.setAttribute("action", actionUrl);

         // title
         var title = '';
         if (this.options.editMode)
         {
            title = this.linkData.title;
         }
         Dom.get(this.id + '-title').setAttribute("value", title);
         
         // description
         var description = '';
         if (this.options.editMode)
         {
            description = this.linkData.description;
         }
         Dom.get(this.id + '-description').value = description;

         // url
         var url = '';
         if (this.options.editMode)
         {
            url = $html(this.linkData.url);
         }
         Dom.get(this.id + '-url').value = url;

         // internal link
         var internal = false;
         if (this.options.editMode)
         {
            internal = this.linkData.internal;
         }
         Dom.get(this.id + '-internal').checked = internal;
         Dom.get(this.id + '-tag-input-field').tabIndex = 5;
         Dom.get(this.id + "-add-tag-button").tabIndex = 6;
         
         // register the behaviour with the form and display the form
         this._registerLinkForm();
      },

      /**
       * Registers the form logic
       */
      _registerLinkForm: function LinkEdit__registerLinkForm()
      {
         // initialize the tag library
         this.modules.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.modules.tagLibrary.setOptions(
         {
            siteId: this.options.siteId
         });
         
         // add the tags that are already set on the link
         if (this.options.editMode && this.linkData.tags.length > 0)
         {
            this.modules.tagLibrary.setTags(this.linkData.tags);
         }
         
         // create the Button
         var okButtonLabel = '';
         if (this.options.editMode)
         {
            okButtonLabel = this.msg('button.update');
         }
         else
         {
            okButtonLabel = this.msg('button.save');
         }
         this.widgets.okButton = new YAHOO.widget.Button(this.id + "-ok",
         {
            type: "submit",
            label: okButtonLabel
         });

         // cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onFormCancelButtonClick);

         // create the form that does the validation/submit
         this.widgets.linkForm = new Alfresco.forms.Form(this.id + "-form");
         // Title
         this.widgets.linkForm.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "keyup");
         this.widgets.linkForm.addValidation(this.id + "-title", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");
         // Description
         this.widgets.linkForm.addValidation(this.id + "-description", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");
         // URL
         this.widgets.linkForm.addValidation(this.id + "-url", Alfresco.forms.validation.mandatory, null, "blur");
         this.widgets.linkForm.addValidation(this.id + "-url", Alfresco.forms.validation.regexMatch,
         {
            pattern: /^(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/
         }, "keyup");
         this.widgets.linkForm.addValidation(this.id + "-url", Alfresco.forms.validation.length,
         {
            max: 2048,
            crop: true
         }, "blur");

         this.widgets.linkForm.setSubmitElements(this.widgets.okButton);
         this.widgets.linkForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this
            },
            failureMessage: this.msg("message.savelink.failure"),
            failureCallback:
            {
               fn: this.onFormSubmitFailure,
               scope: this
            }
         });
         if (this.options.editMode)
         {
             this.widgets.linkForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         }
         this.widgets.linkForm.setSubmitAsJSON(true);
         this.widgets.linkForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
                // disable ui elements
               this.widgets.cancelButton.set("disabled", true);
               
               // update the tags set in the form
               this.modules.tagLibrary.updateForm(this.id + "-form", "tags");
               
               // show a wait message
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message(this.msg("message.submitting")),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            scope: this
         };
         this.modules.tagLibrary.initialize(this.widgets.linkForm);
         this.widgets.linkForm.init();
         
         // finally display the form
         var containerElem = YAHOO.util.Dom.get(this.id + "-div");
         Dom.removeClass(containerElem, "hidden");
         Dom.get(this.id + "-title").focus();         
      },

      /**
       * Cancel button click handler
       */
      onFormCancelButtonClick: function LinkEdit_onFormCancelButtonClick(type, args)
      {
         // redirect to the page we came from
         history.go(-1);
      },
      
      /**
       * Form submit success handler
       */
      onFormSubmitSuccess: function LinkEdit_onFormSubmitSuccess(response)
      {
         // hide the wait message
         this.widgets.feedbackMessage.destroy();

         var linkId =  this.options.linkId;
         if (!this.options.editMode)
         {
            linkId = response.json.name;
         }

         Alfresco.util.PopupManager.displayMessage({text: this.msg("message.savelink.success")});
         this._loadLinkViewPage(linkId);
      },

      /**
       * Reenables the inputs which got disabled as part of a comment submit
       */
      onFormSubmitFailure: function LinkEdit_onFormSubmitFailure(response)
      {
         response.config.failureMessage = this.msg("message.savelink.failure");
         // enable the buttons
         this.widgets.cancelButton.set("disabled", false);
         
         // hide the wait message
         this.widgets.feedbackMessage.destroy();
      }, 
      
      /**
       * PRIVATE FUNCTIONS
       */
          
      /**
       * Loads the link view page
       */
      _loadLinkViewPage: function LinkEdit__loadLinkViewPage(linkId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/links-view?linkId={linkId}",
         {
            site: this.options.siteId,
            linkId: linkId
         });
         window.location = url;
      }
   });
})();
