/**
 * CreateComment component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateComment
 * @extends Alfresco.component.Base
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom;
    
   /**
    * CreateComment constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CreateComment} The new CreateComment instance
    * @constructor
    */
   Alfresco.CreateComment = function Alfresco_CreateComment(htmlId)
   {
      Alfresco.CreateComment.superclass.constructor.call(this, "Alfresco.CreateComment", htmlId, ["event", "json", "editor"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("setCommentedNode", this.onSetCommentedNode, this);
      YAHOO.Bubbling.on("setCanCreateComment", this.onSetCanCreateComment, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.CreateComment, Alfresco.component.Base,
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
          * @default ""
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
          * Node reference of the item to comment about
          *
          * @property itemNodeRef
          * @type string
          * @default null
          */
         itemNodeRef: null,
         
         /**
          * Title of the item to comment about for activites service.
          *
          * @property activityTitle
          * @type string
          * @default null
          */
         activityTitle: null,
         
         /**
          * Page for activities link.
          *
          * @property activityPage
          * @type string
          * @default null
          */
         activityPage: null,

         /**
          * Params for activities link.
          *
          * @property activityPageParams
          * @type object
          * @default null
          */
         activityPageParams: null,

         /**
          * Width to use for comment editor
          *
          * @property width
          * @type int
          * @default 538
          */
         width: 538,
         
         /**
          * Height to use for comment editor
          *
          * @property height
          * @type int
          * @default 250
          */
         height: 250,
         
         /**
          * Tells whether the user can create comments.
          * The component is not displayed unless this field is true
          * 
          * @property canCreateComment
          * @type boolean
          * @default false
          */
         canCreateComment: false
      },

      /**
       * States whether the view has already been initialized
       *
       * @property initialized
       * @type boolean
       */
      initialized: false,

      /**
       * Called by a bubble event to set the node for which comments should be displayed
       *
       * @method onSetCommentedNode
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (nodeRef, title, page, pageParams)
       */
      onSetCommentedNode: function CreateComment_onSetCommentedNode(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.nodeRef !== null) && (obj.title !== null) && (obj.page !== null))
         {
            this.options.itemNodeRef = obj.nodeRef;
            this.options.activityTitle = obj.title;
            this.options.activityPage = obj.page;
            this.options.activityPageParams = obj.pageParams;
            this.initializeCreateCommentForm();
         }
      },
      
      /**
       * Called by a bubble event to set whether the user is allowed to comment.
       *
       * @method onSetCanCreateComment
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (canCreateComment)
       */
      onSetCanCreateComment: function CreateComment_onSetCanCreateComment(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.canCreateComment !== null))
         {
            this.options.canCreateComment = obj.canCreateComment;
            this.initializeCreateCommentForm();
         }
      },

      /**
       * Initializes the create comment form.
       *
       * @method initializeCreateCommentForm
       */
      initializeCreateCommentForm: function CreateComment_initializeCreateCommentForm()
      {
         // only continue if the user is allowed to create a comment
         if (!this.options.canCreateComment)
         {
            return;
         }

         // return if we have already been initialized
         if (this.initialized)
         {
            return;
         }
         this.initialized = true;

         // action url
         var form = Dom.get(this.id + '-form');
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/node/{nodeRef}/comments",
         {
            nodeRef: this.options.itemNodeRef.replace(':/', '')
         });
         form.setAttribute("action", actionUrl);

         // nodeRef
         Dom.get(this.id + '-nodeRef').setAttribute("value", this.options.itemNodeRef);
         
         if (this.options.siteId.length > 0)
         {
            // site
            Dom.get(this.id + '-site').setAttribute("value", this.options.siteId);

            // container
            Dom.get(this.id + '-container').setAttribute("value", this.options.containerId);
         }
         
         // itemTitle
         Dom.get(this.id + '-itemTitle').setAttribute("value", this.options.activityTitle);

         // page
         Dom.get(this.id + '-page').setAttribute("value", this.options.activityPage);

         // pageParams
         Dom.get(this.id + '-pageParams').setAttribute("value", YAHOO.lang.JSON.stringify(this.options.activityPageParams));

         // register the behaviour with the form and display it finally
         this.registerCreateCommentForm();
      },
      
      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       *
       * @method registerCreateCommentForm
       */
      registerCreateCommentForm: function CreateComment_registerCreateCommentForm()
      {
         // register the okButton
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "submit", null,
         {
            type: "submit",
            disabled: true
         });

         // instantiate the simple editor we use for the form
         this.widgets.editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR,this.id + '-content', this.options.editorConfig);
         this.widgets.editor.addPageUnloadBehaviour(this.msg("message.unsavedChanges.comment"));
         this.widgets.editor.render();
         this.widgets.editor.save();

         // Add validation to the rich text editor
         var keyUpIdentifier = (Alfresco.constants.HTML_EDITOR === 'YAHOO.widget.SimpleEditor') ? 'editorKeyUp' : 'onKeyUp';
         this.widgets.editor.subscribe(keyUpIdentifier, function (e)
         {
            /**
             * Doing a form validation on every key stroke is process consuming, below we try to make sure we only do
             * a form validation if it's necessarry.
             * NOTE: Don't check for zero-length in commentsLength, due to HTML <br>, <span> tags, etc. possibly
             * being present. Only a "Select all" followed by delete will clean all tags, otherwise leftovers will
             * be there even if the form looks empty.
             */
            if (this.widgets.editor.getContent().length < 20 || !this.widgets.commentForm.isValid())
            {
               // Submit was disabled and something has been typed, validate and submit will be enabled
               this.widgets.editor.save();
               this.widgets.commentForm.validate()
            }
         }, this, true);

         // create the form that does the validation/submit
         this.widgets.commentForm = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.commentForm.addValidation(this.id + "-content", this._validateTextContent, null);
         this.widgets.commentForm.setSubmitElements(this.widgets.okButton);
         this.widgets.commentForm.setAJAXSubmit(true,
         {
            successMessage: this.msg("message.createcomment.success"),
            successCallback:
            {
               fn: this.onCreateFormSubmitSuccess,
               scope: this
            },
            failureMessage: this.msg("message.createcomment.failure"),
            failureCallback:
            {
               fn: function()
               {
                  this.enableInputs();
               },
               scope: this
            }
         });

         this.widgets.commentForm.setSubmitAsJSON(true);
         this.widgets.commentForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // Put the HTML back into the text area
               this.widgets.editor.save();

               this.widgets.editor.disable();
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.creating", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            scope: this
         };
         this.widgets.commentForm.init();
         
         // show the form
         Dom.removeClass(this.id + '-form-container', 'hidden');
      },

      /**
       * Validate comment content
       *
       * @method _validateTextContent
       * @param field {object} The element representing the field the validation is for
       * @param args {object}
       * @param event {object} The event that caused this handler to be called, maybe null
       * @param form {object} The forms runtime class instance the field is being managed by
       * @param silent {boolean} Determines whether the user should be informed upon failure
       * @param message {string} Message to display when validation fails, maybe null
       */
      _validateTextContent: function CreateComment__validateTextContent(field, args, event, form, silent, message)
      {
         var stripHTML = /<\S[^><]*>/g;
         return YAHOO.lang.trim(field.value.replace(stripHTML, "")).length > 0;
      },

      /**
       * Success handler for the form submit ajax request
       *
       * @method onCreateFormSubmitSuccess
       * @param response {object} Ajax response object
       */
      onCreateFormSubmitSuccess: function CreateComment_onCreateFormSubmitSuccess(response)
      {
         // clear the content of the comment editor
         this.widgets.editor.clear();
         
         // reload the comments list
         YAHOO.Bubbling.fire("refreshComments",
         {
            reason: "created"
         });
         
         this.enableInputs();
      },

      /**
       * Reenables the inputs which got disabled as part of a comment submit
       *
       * @method enableInputs
       */
      enableInputs: function CreateComment_enableInputs()
      {
         this.widgets.feedbackMessage.destroy();
         this.widgets.editor.enable();
      }
   });
})();
