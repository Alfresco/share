/**
 * CommentList component.
 * 
 * Displays a list of comments.
 * 
 * @namespace Alfresco
 * @class Alfresco.CommentList
 */
(function()
{
    
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom;

   /**
    * CommentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CommentList} The new Comment instance
    * @constructor
    */
   Alfresco.CommentList = function Alfresco_CommentList(htmlId)
   {
      Alfresco.CommentList.superclass.constructor.call(this, "Alfresco.CommentList", htmlId, ["editor", "paginator"]);
      
      /* Initialise prototype properties */
      this.editData = 
      {
         editDiv: null,
         viewDiv: null,
         row: -1,
         data: null,
         widgets: {}
      };
      this.busy = false;
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("setCommentedNode", this.onSetCommentedNode, this);
      YAHOO.Bubbling.on("refreshComments", this.onRefreshComments, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.CommentList, Alfresco.component.Base,
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
          * @default ""
          */
         containerId: "",
         
         /**
          * Node reference of the item to comment about
          */
         itemNodeRef: null,
         
         /**
          * Title of the item to comment about for activites service.
          */
         activityTitle: null,
         
         /**
          * Page for activities link.
          */
         activityPage: null,

         /**
          * Params for activities link.
          */
         activityPageParams: null,

         /**
          * Width to use for comment editor
          */
         width: 700,
         
         /**
          * Height to use for comment editor
          */
         height: 180,

         /**
          * Number of items per page
          *
          * @property pageSize
          * @type int
          */
         pageSize: 10
      },
      
      /**
       * Object containing data about the currently edited
       * comment.
       */
      editData: null,
      
      /**
       * Comments data
       */
      commentsData: null,
      
      /**
       * Tells whether an action is currently ongoing.
       * 
       * @property busy
       * @type boolean
       * @see _setBusy/_releaseBusy
       */
      busy: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function CommentList_onReady()
      {
         var me = this;
         // YUI Paginator definition
         var paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this.msg("pagination.template"),
            pageReportTemplate: this.msg("pagination.template.page-report"),
            previousPageLinkLabel : this.msg("pagination.previousPageLinkLabel"),
            nextPageLinkLabel     : this.msg("pagination.nextPageLinkLabel")
         });
         paginator.subscribe('changeRequest', this.onPaginatorChange, this, true);
         paginator.set('recordOffset', 0);
         paginator.set('totalRecords', 0);
         paginator.render();
         this.widgets.paginator = paginator;

         // Hook action events for the comments
         var fnActionHandlerDiv = function CommentList_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               //var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  var commentElem = Dom.getAncestorByClassName(owner, 'comment'),
                     index = parseInt(commentElem.id.substring((me.id + '-comment-view-').length), 10);

                  me[action].call(me, index);
                  args[1].stop = true;
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("blogcomment-action", fnActionHandlerDiv);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onCommentElementMouseEntered, this.onCommentElementMouseExited, this);
      },      

      /**
       * Called by the paginator when a user has clicked on next or prev.
       * Dispatches a call to the server and reloads the comment list.
       *
       * @method onPaginatorChange
       * @param state {object} An object describing the required page changing
       */
      onPaginatorChange : function CommentList_onPaginatorChange(state)
      {
         this._loadCommentsList(state.recordOffset);
      },
      
      /**
       * Called by another component to set the node for which comments should be displayed
       *
       * @method onSetCommentedNode
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (nodeRef, title, page, pageParams)
       */
      onSetCommentedNode: function CommentList_onSetCommentedNode(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.nodeRef !== null) && (obj.title !== null) && (obj.page !== null))
         {
            this.options.itemNodeRef = obj.nodeRef;
            this.options.activityTitle = obj.title;
            this.options.activityPage = obj.page;
            this.options.activityPageParams = obj.pageParams;
            this._loadCommentsList(0);
         }
      },
    
      /**
       * Forces the comments list to fresh by reloading the data from the server
       *
       * @method onRefreshComments
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (reason)
       */
      onRefreshComments: function CommentList_onRefreshComments(layer, args)
      {
         if (this.options.itemNodeRef && this.options.activityTitle)
         {
            var p = this.widgets.paginator,
               obj = args[1];

            // Find appropriate index
            var startIndex = 0,
               tr = p.getTotalRecords(),
               ps = this.options.pageSize;

            if (obj.reason == "deleted")
            {
               // Make sure we dont use n invalid startIndex now that one is removed
               var newTotalPages = Math.floor((tr - 1) / ps) + (((tr - 1) % ps) > 0 ? 1 : 0),
                  currentPage = p.getCurrentPage();

               if (newTotalPages < currentPage)
               {
                  // the deletion was done of the last comment in the current page
                  currentPage = currentPage > 1 ? currentPage - 1 : 1;
               }
               var record = p.getPageRecords(currentPage);
               startIndex = record ? record[0] : 0;
            }
            if (obj.reason == "created")
            {
               startIndex = Math.floor(tr/ps) * ps;
            }
            this._loadCommentsList(startIndex);
         }
      },
            
      /**
       * Loads the comments for the provided nodeRef and refreshes the UI
       *
       * @method _loadCommentsList
       * @protected
       * @param startIndex First comment to show
       */
      _loadCommentsList: function CommentList__loadCommentsList(startIndex)
      {
         // construct the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/node/{nodeRef}/comments",
         {
            nodeRef: this.options.itemNodeRef.replace(":/", "")
         });

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            dataObj:
            {
               startIndex: startIndex,
               pageSize: this.options.pageSize
            },
            successCallback:
            {
               fn: this.loadCommentsSuccess,
               scope: this
            },
            failureMessage: this.msg("message.loadComments.failure")
         });
         
      },

      /**
       * Load comments ajax request success handler.
       *
       * @method loadCommentsSuccess
       * @param response AJAX response object
       */
      loadCommentsSuccess: function CommentsList_loadCommentsSuccess(response)
      {
         // make sure any edit data is cleared
         this._hideEditView();
          
         var comments = response.json.items;

         // Get the elements to update
         var bodyDiv = Dom.get(this.id + "-body"),
            titleDiv = Dom.get(this.id + "-title"),
            commentDiv = Dom.get(this.id + "-comments");
         
         // temporarily hide the container node
         bodyDiv.setAttribute("style", "display:none");

         // update the list name
         if (comments.length > 0)
         {
            titleDiv.innerHTML =  Alfresco.util.message("label.comments", this.name,
            {
               "0": comments.length,
               "1": response.json.total
            });
         }
         else
         {
            titleDiv.innerHTML = Alfresco.util.message("label.noComments", this.name);
         }
         
         // Update the list elements
         var html = '', i, j;
         for (i = 0, j = comments.length; i < j; i++)
         {
            html += this.renderComment(i, comments[i]);
         }
         commentDiv.innerHTML = html;
         bodyDiv.removeAttribute("style");
         
         // init mouse over
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'comment', 'div');
         
         // keep a reference to the loaded data
         this.commentsData = comments;
         
         // inform the create comment component of whether the user can create a comment
         YAHOO.Bubbling.fire("setCanCreateComment",
         {
            canCreateComment: response.json.nodePermissions.create
         });

         this._updatePaginator(response.json.startIndex, response.json.total);
      },

      /**
       * Called by loadCommentsSuccess when it has rendered the comments.
       * Since this componenent listens for the event "setCommentedNode" that can be displayed
       * before this component has created its own widgets and paginator it must wait until the paginator
       * has been created and then update it.
       *
       * @method updatePaginator
       * @param page {int} The page of comments in the paging list that is displayed
       * @param total {int} The totla number of comments in the paging
       */
      _updatePaginator: function CommentList__updatePaginator(page, total)
      {
         if (this.widgets && this.widgets.paginator)
         {
            this.widgets.paginator.set('recordOffset', page);
            this.widgets.paginator.set('totalRecords', total);
         }
         else
         {
            YAHOO.lang.later(100, this, this._updatePaginator, [page, total]);
         }
      },

      /**
       * Edit comment action links handler.
       *
       * @method onEditComment
       * @param row {object} Comment to edit
       */
      onEditComment: function CommentList_onEditComment(row)
      {   
         this._loadForm(row, this.commentsData[row]);
      },

      /**
       * Delete comment action links handler.
       *
       * @method onDeleteComment
       * @param row {object} Comment to delete
       */
      onDeleteComment: function CommentList_onDeleteComment(row)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete"),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function CommentList_onDeleteComment_delete()
               {
                  this.destroy();
                  me._onDeleteCommentConfirm.call(me, row);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function CommentList_onDeleteComment_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete comment confirmed.
       *
       * @method _onDeleteCommentConfirm
       * @protected
       * @param row {object} Comment to delete
       */
      _onDeleteCommentConfirm: function CommentList__onDeleteCommentConfirm(row)
      {
         var data = this.commentsData[row];
         this._deleteComment(row, data);
      },

      // Action implementation
      
      /**
       * Implementation of the comment deletion action
       *
       * @method _deleteComment
       * @protected
       * @param row {object} Comment to delete
       * @param data {object} Comment data
       */
      _deleteComment: function CommentList__deleteComment(row, data)
      {
         // show busy message
         if (!this._setBusy(this.msg('message.wait')))
         {
            return;
         }
         
         // ajax request success handler
         var success = function CommentList__deleteComment_success(response)
         {
            // remove busy message
            this._releaseBusy();
            
            // reload the comments list
            YAHOO.Bubbling.fire("refreshComments",
            {
               reason: "deleted"
            });
         };
         
         // ajax request success handler
         var failure = function CommentList__deleteComment_failure(response)
         {
            // remove busy message
            this._releaseBusy();
         };

         // construct the request url to delete the comment
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/comment/node/{nodeRef}/?site={site}&itemTitle={itemTitle}&page={page}&pageParams={pageParams}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            nodeRef: data.nodeRef.replace(":/", ""),
            itemTitle: encodeURIComponent(this.options.activityTitle),
            page: encodeURIComponent(this.options.activityPage),
            pageParams: encodeURIComponent(YAHOO.lang.JSON.stringify(this.options.activityPageParams))
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successMessage: this.msg("message.delete.success"),
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureMessage: this.msg("message.delete.failure"),
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
      },


      // Form management

      /**
       * Loads the comment edit form
       *
       * @method _loadForm
       * @protected
       * @param row {object} Comment to edit
       * @param data {object} Comment data
       */
      _loadForm: function CommentList__loadForm(row, data)
      {
         // we always load the template through an ajax request
         var formId = this.id + "-edit-comment-" + row;
         
         // Load the UI template from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/blog/comments/edit-comment",
            dataObj:
            {
               htmlid: formId
            },
            successCallback:
            {
               fn: this.onFormLoaded,
               scope: this,
               obj:
               {
                  formId: formId,
                  row: row,
                  data: data
               }
            },
            failureMessage: this.msg("message.loadeditform.failure"),
            execScripts: true
         });
      },

      /**
       * Event callback when comment form has been loaded
       *
       * @method onFormLoaded
       * @param response {object} Server response from load form XHR request
       * @param obj {object} Comment scope (row, data, formId)
       */
      onFormLoaded: function CommentList_onFormLoaded(response, obj)
      {
         // get the data and formId of the loaded form
         var row = obj.row,
            data = obj.data,
            formId = obj.formId;
         
         // make sure no other forms are displayed
         this._hideEditView();
       
         // find the right divs to insert the html into
         var viewDiv = Dom.get(this.id + "-comment-view-" + row),
            editDiv = Dom.get(this.id + "-comment-edit-" + row);
         
         // insert the html
         editDiv.innerHTML = response.serverResponse.responseText;
         
         // insert current values into the form
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/comment/node/{nodeRef}",
         {
            nodeRef: data.nodeRef.replace(':/', '')
         });
         Dom.get(formId + "-form").setAttribute("action", actionUrl);
         Dom.get(formId + "-site").setAttribute("value", this.options.siteId);
         Dom.get(formId + "-container").setAttribute("value", this.options.containerId);
         Dom.get(formId + "-itemTitle").setAttribute("value", this.options.activityTitle);
         Dom.get(formId + "-page").setAttribute("value", this.options.activityPage);
         Dom.get(formId + "-pageParams").setAttribute("value", YAHOO.lang.JSON.stringify(this.options.activityPageParams));
         Dom.get(formId + "-content").value = data.content;
         
         // show the form and hide the view
         Dom.addClass(viewDiv, "hidden");
         Dom.removeClass(editDiv, "hidden");

         // store the edit data locally
         this.editData =
         {
            viewDiv: viewDiv,
            editDiv: editDiv,
            row: row,
            widgets : {},
            formId: formId
         };
             
         // and finally register the form handling
         this._registerEditCommentForm(row, data, formId);
      },

      /**
       * Registers the form with the html (that should be available in the page)
       * as well as the buttons that are part of the form.
       *
       * @method _registerEditCommentForm
       * @protected
       * @param row {object} Comment to edit
       * @param data {object} Comment data
       * @param formId {string} Form ID
       */
      _registerEditCommentForm: function CommentList__registerEditCommentForm(row, data, formId)
      {
         // register the okButton
         this.editData.widgets.okButton = new YAHOO.widget.Button(formId + "-submit",
         {
            type: "submit"
         });

         // register the cancel button
         this.editData.widgets.cancelButton = new YAHOO.widget.Button(formId + "-cancel");
         this.editData.widgets.cancelButton.subscribe("click", this.onEditFormCancelButtonClick, this, true);

         // instantiate the simple editor we use for the form
         this.editData.widgets.editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR, formId + '-content', this.options.editorConfig);
         this.editData.widgets.editor.addPageUnloadBehaviour(this.msg("message.unsavedChanges.comment"));
         this.editData.widgets.editor.render();

         // Add validation to the editor
         var keyUpIdentifier = (Alfresco.constants.HTML_EDITOR === 'YAHOO.widget.SimpleEditor') ? 'editorKeyUp' : 'onKeyUp';
         this.editData.widgets.editor.subscribe(keyUpIdentifier, function (e)
         {
            /**
             * Doing a form validation on every key stroke is process consuming, below we try to make sure we only do
             * a form validation if it's necessarry.
             * NOTE: Don't check for zero-length in commentsLength, due to HTML <br>, <span> tags, etc. possibly
             * being present. Only a "Select all" followed by delete will clean all tags, otherwise leftovers will
             * be there even if the form looks empty.
             */
            if (this.editData.widgets.editor.getContent().length < 20 || !this.editData.widgets.commentForm.isValid())
            {
               // Submit was disabled and something has been typed, validate and submit will be enabled
               this.editData.widgets.editor.save();
               this.editData.widgets.commentForm.validate();
            }
         }, this, true);

         // create the form that does the validation/submit
         var commentForm = new Alfresco.forms.Form(formId + "-form");
         this.editData.widgets.commentForm = commentForm;
         commentForm.addValidation(formId + "-content", Alfresco.forms.validation.mandatory, null);
         commentForm.setSubmitElements(this.editData.widgets.okButton);
         commentForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         commentForm.setAJAXSubmit(true,
         {
            successMessage: this.msg("message.savecomment.success"),
            successCallback:
            {
               fn: this.onEditFormSubmitSuccess,
               scope: this
            },
            failureMessage: this.msg("message.savecomment.failure"),
            failureCallback:
            {
               fn: this.onEditFormSubmitFailure,
               scope: this
            }
         });
         commentForm.setSubmitAsJSON(true);
         commentForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               this.editData.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.savecomment", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
               
               this.editData.widgets.editor.disable();
                
               //Put the HTML back into the text area
               this.editData.widgets.editor.save();
            },
            scope: this
         };
         commentForm.init();
      },
      
      /**
       * Edit form submit success handler
       *
       * @method onEditFormSubmitSuccess
       * @param response {object} Ajax response object
       */
      onEditFormSubmitSuccess: function CommentList_onEditFormSubmitSuccess(response)
      {
         this.editData.widgets.feedbackMessage.destroy();
          
         // the response contains the new data for the comment. Render the comment html
         // and insert it into the view element
         this.commentsData[this.editData.row] = response.json.item;
         this.editData.viewDiv.innerHTML = this.renderCommentView(this.editData.row, response.json.item);
            
         // hide the form and display an information message
         this._hideEditView();
      },
      
      /**
       * Edit form submit failure handler
       *
       * @method onEditFormSubmitFailure
       * @param response {object} Ajax response object
       */
      onEditFormSubmitFailure: function CommentList_onEditFormSubmitFailure(response)
      {
         this.editData.widgets.feedbackMessage.destroy();
         this.editData.widgets.editor.disable();
      },
      
      /**
       * Form cancel button click handler
       *
       * @method onEditFormCancelButtonClick
       */
      onEditFormCancelButtonClick: function CommentList_onEditFormCancelButtonClick()
      {
          this._hideEditView();
      },

      /**
       * Renders a comment element.
       * Each comment element consists of an edit and a view div.
       *
       * @method renderComment
       * @param index {int} Comment DOM index
       * @param data {object} Comment data
       */
      renderComment: function CommentList_renderComment(index, data)
      {
         // add a div for the comment edit form
         var html = '<div id="' + this.id + '-comment-edit-' + index + '" class="hidden"></div>';
         
         // output the view
         var rowClass = index % 2 === 0 ? "even" : "odd";
         html += '<div class="comment ' + rowClass + '" id="' + this.id + '-comment-view-' + index + '">';
         html += this.renderCommentView(index, data);
         html += '</div>';
         
         return html;
      },
      
      /**
       * Renders the content of the comment view div.
       *
       * @method renderCommentView
       * @param index {int} Comment DOM index
       * @param data {object} Comment data
       */
      renderCommentView: function CommentList_renderCommentView(index, data)
      {
         // actions
         var html = '<div class="nodeEdit">';
         if (data.permissions.edit)
         {
            html += '<div class="onEditComment"><a href="#" class="blogcomment-action">' + this.msg("action.edit") + '</a></div>';
         }
         if (data.permissions["delete"])
         {
            html += '<div class="onDeleteComment"><a href="#" class="blogcomment-action">' + this.msg("action.delete") + '</a></div>';
         }
         html += '</div>';
  
         // avatar image
         html += '<div class="authorPicture">' + Alfresco.util.people.generateUserAvatarImg(data.author) + '</div>';
  
         // comment info and content
         html += '<div class="nodeContent"><div class="userLink">' + Alfresco.util.people.generateUserLink(data.author);
         html += ' ' + this.msg("comment.said") + ':';
         if (data.isUpdated)
         {
            html += '<span class="theme-color-2 nodeStatus"> (' + this.msg("comment.updated") + ')</span>';
         }
         html += '</div>';
         html += '<div class="content yuieditor">' + data.content + '</div>';
         html += '</div>';

         // footer
         html += '<div class="commentFooter">';
         html += '<span class="nodeFooterBlock">';
         html += '<span class="nodeAttrLabel">' + this.msg("comment.postedOn") + ': ';
         html += Alfresco.util.formatDate(data.createdOn);
         html += '</span></span></div>';
         
         return html;
      },

      /**
       * Mouse enter event handler for list items
       *
       * @method onCommentElementMouseEntered
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (target)
       */
      onCommentElementMouseEntered: function CommentList_onCommentElementMouseEntered(layer, args)
      {
         // find out whether the user has actions, otherwise we won't show an overlay
         var id = args[1].target.id;
         var index = id.substring((this.id + '-comment-view-').length);
         var permissions = this.commentsData[index].permissions;
         if (! (permissions.edit || permissions["delete"]))
         {
            return;
         }
          
         var elem = args[1].target;
         Dom.addClass(elem, 'over');
      },
      
      /**
       * Mouse leave event handler for list items
       *
       * @method onCommentElementMouseExited
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (target)
       */
      onCommentElementMouseExited: function CommentList_onCommentElementMouseExited(layer, args)
      {
         var elem = args[1].target;
         Dom.removeClass(elem, 'over');
      },

   
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Makes sure that all forms get removed and if available the hidden content
       * elements displayed again.
       *
       * @method _hideEditView
       * @protected
       */
      _hideEditView: function CommentList__hideEditView()
      {
         if (this.editData.editDiv !== null)
         {
            // hide edit div and remove form
            Dom.addClass(this.editData.editDiv, "hidden");
            this.editData.editDiv.innerHTML = "";
            this.editData.editDiv = null;
         }
         if (this.editData.viewDiv !== null)
         {
            // display view div
            Dom.removeClass(this.editData.viewDiv, "hidden");
            this.editData.viewDiv = null;
         }
      },

      /**
       * Displays busyMessage popup if busy flag is currently unset
       * 
       * @method _setBusy
       * @protected
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function CommentList__setBusy(busyMessage)
      {
         if (this.busy)
         {
            return false;
         }
         this.busy = true;
         this.widgets.busyMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: busyMessage,
            spanClass: "wait",
            displayTime: 0
         });
         return true;
      },
      
      /**
       * Removes the busy message and marks the component as non-busy
       * 
       * @method _releaseBusy
       * @protected
       */
      _releaseBusy: function CommentList__releaseBusy()
      {
         if (this.busy)
         {
            this.widgets.busyMessage.destroy();
            this.busy = false;
            return true;
         }
         else
         {
            return false;
         }
      }
   });
})();
