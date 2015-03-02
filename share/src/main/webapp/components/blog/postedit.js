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
 * BlogPostEdit component.
 * 
 * Component provides blog post creation/edit functionality.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPostEdit
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * BlogPostEdit constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostEdit} The new Post instance
    * @constructor
    */
   Alfresco.BlogPostEdit = function(htmlId)
   {
      Alfresco.BlogPostEdit.superclass.constructor.call(this, "Alfresco.BlogPostEdit", htmlId, ["button", "menu", "json"]);
      
      // Initialise prototype properties
      this.blogPostData = null;
      this.performExternalPublish = false;
      
      return this;
   };

   YAHOO.extend(Alfresco.BlogPostEdit, Alfresco.component.Base,
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
          * @default "blog"
          */
         containerId: "blog",
         
         /**
          * True if the component should be in edit mode.
          *
          * @property editMode
          * @type boolean
          * @default: false
          */
         editMode: false,
         
         /**
          * Id of the post to edit. Only relevant if editMode is true
          *
          * @property postId
          * @type string
          * @default: ""
          */
         postId: ""
      },

      /**
       * Stores the data of the currently edited blog post
       */
      blogPostData: null,
        
      /**
       * If true, an external publish/update will be executed after the post has been
       * saved/updated.
       */
      performExternalPublish: null,
        
      /**
       * If true, the "message.publishExternal.failure" will be shown
       * True, if trying to publish externaly, but external blog is not configured
       */
      showNotConfiguredMessage: false,
	  
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPostEdit_onReady()
      {
         if (this.options.editMode)
         {
            // load the blog post data prior to initializing the form
            this._loadBlogPostData();
         }
         else
         {
            // directly initialize the form
            this._initializeBlogPostForm();
         }
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       *
       * @method _loadBlogPostData
       * @private
       */
      _loadBlogPostData: function BlogPostEdit__loadBlogPostData()
      {
         // ajax request success handler
         var me = this;
         var loadBlogPostDataSuccess = function BlogPostEdit__loadBlogPostData(response)
         {
            // set the blog data
            var data = response.json.item;
            me.blogPostData = data;
            
            // now initialize the form, which will use the data we just loaded
            me._initializeBlogPostForm();
         };
         
         // construct the request url
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/blog/post/site/{site}/{container}/{postId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            postId: this.options.postId
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "GET",
            responseContentType: "application/json",
            successCallback:
            {
               fn: loadBlogPostDataSuccess,
               scope: this
            },
            failureMessage: this.msg("message.loadpostdata.failure")
         });
      },

      /**
       * Initializes the blog post form with create/edit dependent data.
       *
       * @method _initializeBlogPostForm
       * @private
       */
      _initializeBlogPostForm: function BlogPostEdit__initializeBlogPostForm()
      {
         // construct the actionUrl, which is different for creating/updating a post
         var actionUrl, draft = true, title = "", content = "";
         if (this.options.editMode)
         {
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/node/{nodeRef}",
            {
               nodeRef: this.blogPostData.nodeRef.replace(":/", "")
            });
         }
         else
         {
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/site/{site}/{container}/posts",
            {
               site: this.options.siteId,
               container: this.options.containerId
            });
         }         
         Dom.get(this.id + "-form").setAttribute("action", actionUrl);

         // site and container
         Dom.get(this.id + "-site").setAttribute("value", this.options.siteId);
         Dom.get(this.id + "-container").setAttribute("value", this.options.containerId);
                  
         // draft
         if (this.options.editMode)
         {
            draft = this.blogPostData.isDraft;
         }
         Dom.get(this.id + "-draft").setAttribute("value", draft);
         
         // title
         if (this.options.editMode)
         {
            title = this.blogPostData.title;
         }
         Dom.get(this.id + "-title").setAttribute("value", title);
         
         // content
         if (this.options.editMode)
         {
            content = this.blogPostData.content;
         }
         Dom.get(this.id + "-content").value = content;
         
         // register the behaviour with the form and display the form
         this._registerBlogPostForm();
      },

      /**
       * Registers the form logic
       *
       * @method _registerBlogPostForm
       * @private
       */
      _registerBlogPostForm: function BlogPostEdit__registerBlogPostForm()
      {
         // initialize the tag library
         this.modules.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.modules.tagLibrary.setOptions(
         {
            siteId: this.options.siteId
         });
         
         // add the tags that are already set on the post
         if (this.options.editMode && this.blogPostData.tags.length > 0)
         {
            this.modules.tagLibrary.setTags(this.blogPostData.tags);
         }
         
         // create the Button
         this.widgets.saveButton = new YAHOO.widget.Button(this.id + "-save-button",
         {
            type: "submit",
            label: this.msg(this.options.editMode ? "action.update" : "action.saveAsDraft")
         });

         // publishing of a draft post button - only visible if post is a draft
         if (!this.options.editMode || this.blogPostData.isDraft)
         {
            this.widgets.publishButton = Alfresco.util.createYUIButton(this, "publish-button", this.onFormPublishButtonClick);
            Dom.removeClass(this.id + "-publish-button", "hidden");
         }

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onFormCancelButtonClick);

         // Instantiate the simple editor we use for the form
         this.widgets.editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR, this.id + "-content", this.options.editorConfig);
         this.widgets.editor.addPageUnloadBehaviour(this.msg("message.unsavedChanges.blog"));
         this.widgets.editor.render();

         // Create the form that does the validation/submit
         this.widgets.postForm = new Alfresco.forms.Form(this.id + "-form");

         // Title is mandatory
         this.widgets.postForm.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "blur");
         this.widgets.postForm.addValidation(this.id + "-title", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         if (this.widgets.publishButton)
         {
            this.widgets.postForm.setSubmitElements([this.widgets.saveButton, this.widgets.publishButton]);            
         }
         else
         {
            this.widgets.postForm.setSubmitElements([this.widgets.saveButton]);
         }
         this.widgets.postForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this
            },
            failureMessage: this.msg("message.savepost.failure"),
            failureCallback:
            {
               fn: this.onFormSubmitFailure,
               scope: this
            }
         });
         if (this.options.editMode)
         {
             this.widgets.postForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         }
         this.widgets.postForm.setSubmitAsJSON(true);
         this.widgets.postForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               //Put the HTML back into the text area
               this.widgets.editor.save();

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
         this.modules.tagLibrary.initialize(this.widgets.postForm);
         this.widgets.postForm.init();
         
         // finally display the form
         Dom.removeClass(this.id + "-div", "hidden");
         Dom.get(this.id + "-title").focus();
      },

      /**
       * Publish button click handler
       *
       * @method onFormPublishButtonClick
       */
      onFormPublishButtonClick: function BlogPostEdit_onFormPublishButtonClick(type, args)
      {
         // make sure we set the draft flag to false
         Dom.get(this.id + "-draft").setAttribute("value", false);
          
         // submit the form
         this.widgets.saveButton.fireEvent("click",
         {
            type: "click"
         });
      },
      
      /**
       * Cancel button click handler
       *
       * @method onFormCancelButtonClick
       */
      onFormCancelButtonClick: function BlogPostEdit_onFormCancelButtonClick(type, args)
      {
         // redirect to the page we came from
         history.go(-1);
      },
      
      /**
       * Form submit success handler
       *
       * @method onFormSubmitSuccess
       */
      onFormSubmitSuccess: function BlogPostEdit_onFormSubmitSuccess(response)
      {
         // hide the wait message
         this.widgets.feedbackMessage.destroy();
         
         // check whether we have to do an external publich
         if (this.performExternalPublish)
         {
            // show a new wait message
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: Alfresco.util.message(this.msg("message.postSavedNowPublish")),
               spanClass: "wait",
               displayTime: 0
            });
             
            this.showNotConfiguredMessage = !response.json.metadata.externalBlogConfig;
			 
            //var nodeRef = response.json.item.nodeRef;    
            var postId = response.json.item.name;
            if (response.json.item.isPublished)
            {
               // perform an update
               this.onUpdateExternal(postId);
            }
            else
            {
               // perform a publish
               this.onPublishExternal(postId);
            }
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.savepost.success")
            });
            this._loadPostViewPage(response.json.item.name);
         }
      },

      /**
       * Reenables the inputs which got disabled as part of a comment submit
       *
       * @method onFormSubmitFailure
       */
      onFormSubmitFailure: function BlogPostEdit_onFormSubmitFailure()
      {
         // enable the buttons
         this.widgets.cancelButton.set("disabled", false);
         
         // hide the wait message
         this.widgets.feedbackMessage.destroy();
      },

      /**
       * Publishes the blog post to an external blog.
       *
       * @method onPublishExternal
       */
      onPublishExternal: function BlogPostEdit_onPublishExternal(postId)
      {
         // publish request success handler
         var onPublished = function BlogPostEdit_onPublished(response)
         {
            this._loadPostViewPage(postId);
         };
         
         // publish request failure handler
         var onPublishFailed = function BlogPostEdit_onPublishFailed(response)
         {
            // let the user know that the publish failed, then redirect to the view page
            this.widgets.feedbackMessage.destroy();
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("message.publishExternal.failure"),
               buttons: [
               {
                  text: this.msg("button.ok"),
                  handler: function()
                  {
                     me._loadPostViewPage(postId);
                  },
                  isDefault: true
               }]
            });
            
         };
                  
         // get the url to call
         var url = Alfresco.util.blog.generatePublishingRestURL(this.options.siteId, this.options.containerId, postId);
		 
         var amsg = this.msg("message.savepost.success");
         if (this.showNotConfiguredMessage) 
         { 
            amsg = this.msg("message.publishExternal.failure"); 
         }
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            dataObj:
            {
               action : "publish"
            },
            successMessage: amsg,
            successCallback:
            {
               fn: onPublished,
               scope: this
            },
            failureCallback:
            {
               fn: onPublishFailed,
               scope: this
            }
         });
      },
      
      /**
       * Updates the external published blog post.
       *
       * @method onUpdateExternal
       */
      onUpdateExternal: function BlogPostEdit_onUpdateExternal(postId)
      {
         // update request success handler
         var onUpdated = function BlogPostEdit_onUpdated(response)
         {
            this._loadPostViewPage(postId);
         };
         
         // update request failure handler
         var onUpdateFailed = function BlogPostEdit_onUpdateFailed(response)
         {
            // let the user know that the publish failed, then redirect to the view page
            this.widgets.feedbackMessage.destroy();
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: this.msg("message.updateExternal.failure"),
               buttons: [
               {
                  text: this.msg("button.ok"),
                  handler: function()
                  {
                     me._loadPostViewPage(postId);
                  },
                  isDefault: true
               }]
            });
            
         };
         
         // get the url to call
         var url = Alfresco.util.blog.generatePublishingRestURL(this.options.siteId, this.options.containerId, postId);
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "POST",
            requestContentType : "application/json",
            responseContentType : "application/json",
            dataObj:
            {
               action : "update"
            },
            successMessage: this.msg("message.updateExternal.success"),
            successCallback:
            {
               fn: onUpdated,
               scope: this
            },
            failureCallback:
            {
               fn: onUpdateFailed,
               scope: this
            }
         });
      },
      
      /**
       * PRIVATE FUNCTIONS
       */
          
      /**
       * Loads the blog post view page
       *
       * @method _loadPostViewPage
       */
      _loadPostViewPage: function BlogPostEdit__loadPostViewPage(postId)
      {
         window.location = Alfresco.util.blog.generateBlogPostViewUrl(this.options.siteId, this.options.containerId, postId);
      }
   });
})();
