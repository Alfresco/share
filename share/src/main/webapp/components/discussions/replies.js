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
 * Discussion TopicReplies component.
 * 
 * @namespace Alfresco
 * @class Alfresco.TopicReplies
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
    * TopicReplies constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TopicReplies} The new Reply instance
    * @constructor
    */
   Alfresco.TopicReplies = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.TopicReplies";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      this.editData =
      {
         formDiv : null,
         viewDiv : null
      };
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["dom", "event", "element"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("addReplyToPost", this.onAddReplyToPost, this);
      YAHOO.Bubbling.on("topicDataChanged", this.onTopicDataChanged, this);
            
      return this;
   };
   
   Alfresco.TopicReplies.prototype =
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
          * Current containerId.
          * 
          * @property containerId
          * @type string
          */
         containerId: "discussions",
         
         /**
          * Reference to the topic for which to display replies.
          */
         topicRef: "",
         
         /**
          * Id of the topic to display
          * Note: this is solely used for generating the activities feed
          */
         topicId: "",
         
         /**
          * Title of the topic for which replies are displayed
          * Note: this is solely used for generating the activities feed
          */
         topicTitle: ""
      },
      
      /**
       * Stores editing related data
       */
      editData : null,
      
      /**
       * Stores the displayed data
       */
      repliesData: null,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function TopicReplies_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setMessages: function TopicReplies_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function TopicReplies_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function TopicReplies_onReady()
      {   
         // Hook action events.
         var me = this;
         var fnActionHandlerDiv = function TopicReplies_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  var id = '';
                  id = owner.id;
                  var nodeRef = '';
                  nodeRef = id.substring((me.id + '-' + action + '-').length);
                  nodeRef = me.toNodeRef(nodeRef);
                  me[action].call(me, nodeRef);
                  args[1].stop = true;
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("reply-action-link", fnActionHandlerDiv);

         // Hook the show/hide link
         var fnShowHideChildrenHandler = function TopicReplies_fnShowHideChildrenHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "a");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  var id = '';
                  id = owner.id;
                  var nodeRef = '';
                  nodeRef = id.substring((me.id + '-' + action + '-').length);
                  nodeRef = me.toNodeRef(nodeRef);
                  me[action].call(me, nodeRef);
                  args[1].stop = true;
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("showHideChildren", fnShowHideChildrenHandler);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onReplyElementMouseEntered, this.onReplyElementMouseExited, this);
      },
      
      
      // Bubble event management
      
      /**
       * Tag selected handler (document details)
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onAddReplyToPost: function TopicReplies_addReplyToPost(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.postRef !== null))
         {
            this.onAddReply(obj.postRef);
         }
      },
      
      /**
       * onLoadReplies handler
       */
      onTopicDataChanged: function TopicReplies_onTopicDataChanged(layer, args)
      {
         var oldRef = this.options.topicRef;
         var obj = args[1];
         if (obj && (obj.topicRef !== null) && (obj.topicId !== null) && (obj.topicTitle !== null))
         {
            this.options.topicRef = obj.topicRef;
            this.options.topicId = obj.topicId;
            this.options.topicTitle = obj.topicTitle;
            this.options.topicTotalReplyCount = obj.topicTotalReplyCount;

            // load the data if not done so or if the topic has changed
            if (this.repliesData === null || (oldRef != this.repliesData.topicRef))
            {
               this._loadRepliesData();
            }
         }
      },
      
      /**
       * Loads the replies data and updates the ui.
       */      
      _loadRepliesData: function TopicReplies__loadRepliesData()
      {
         // ajax request success handler
         var loadRepliesDataSuccess = function TopicReplies_loadRepliesDataSuccess(response)
         {
            // set the loaded data
            var data = response.json.items;
            this.repliesData = data;
            
            // render the ui
            this.renderUI();
         };
         
         // construct the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/forum/post/site/{site}/{container}/{topicId}/replies?levels={levels}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            topicId: this.options.topicId,
            levels: 999
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: loadRepliesDataSuccess,
               scope: this
            },
            failureMessage: this._msg("message.loadreplies.failure")
         });
      },

      /**
       * Converts a html-id safe nodeRef to a real one.
       * 
       * @param safeRef {string} a nodeReference where the separators have been replaced by _
       * @return {string} a valid node reference
       */
      toNodeRef: function(safeRef)
      {
         return safeRef.replace(/_/, '://').replace(/_/, '/');
      },
      
      /**
       * Converts a node reference to a html-id safe string.
       * 
       * @param nodeRef {string} a nodeReference where the separators have been replaced by _
       * @return {string} a nodeRef string usable in html id values
       */
      toSafeRef: function(nodeRef)
      {
         return nodeRef.replace(':/', '').replace('/', '_').replace('/', '_');
      },

      /**
       * Renders the UI of the component
       */
      renderUI: function TopicReplies_renderUI()
      {
         // get the root element
         var rootDiv = Dom.get(this.id + '-replies-root');
         rootDiv.innerHTML = '';
         var elem = new Element(rootDiv);
         
         // add the reply form element
         var replyFormDiv = document.createElement("div");
         replyFormDiv.setAttribute("id", "reply-add-form-" + this.toSafeRef(this.options.topicRef));
         elem.appendChild(replyFormDiv);
         
         for (var x=0; x < this.repliesData.length; x++)
         {
            this.renderReply(rootDiv, this.repliesData[x], false);
         }
         
         // attach the rollover listeners
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'reply', 'div');  
         
         // finally show the root div
         Dom.removeClass(rootDiv, "hidden");
      },
      
      /**
       * Renders an individual reply element
       */
      renderReply: function TopicReplies_renderReply(parentDiv, data, highlight)
      {
         var replyDiv = document.createElement("div");
         
         // we first generate the general html for an element, this is the
         // edit and view divs, the child replies div including the add reply div.
         var safeRef = this.toSafeRef(data.nodeRef);
         var html = '';
         html += '<div class="reply" id="reply-' + safeRef + '">';
         html += '</div>';
         html += '<div id="reply-edit-form-' + safeRef + '" class="hidden"></div>';
         html += '<div id="reply-add-form-' + safeRef + '" class="indented hidden"></div>';
         html += '<div class="indented" id="replies-of-' + safeRef + '"></div>';
         replyDiv.innerHTML = html;
         parentDiv.appendChild(replyDiv);
         
         // render the reply content
         var viewElem = Dom.get('reply-' + safeRef);
         this.renderReplyView(viewElem, data);
         
         // render the children if they got already loaded
         if (data.children !== undefined)
         {
            var repliesElem = Dom.get('replies-of-' + safeRef);
            for (var x=0; x < data.children.length; x++)
            {
               this.renderReply(repliesElem, data.children[x], false);
            }
         }
         
         if (highlight)
         {
            Alfresco.util.Anim.pulse(viewElem);
            this._scrollToElement(viewElem);
         }
      },
      
      /**
       * Renders the view part of a reply element
       */
      renderReplyView: function TopicReplies_renderReplyView(div, data)
      {
         var safeRef = this.toSafeRef(data.nodeRef);
         var html = '';
                  
         // render the actions
         html += '<div class="nodeEdit">';
         if (data.permissions.reply)
         {
            html += '<div class="onAddReply" id="' + this.id + '-onAddReply-' + safeRef + '">';
            html += '<a href="#" class="reply-action-link">' + this._msg("action.reply") + '</a>';
            html += '</div>';
         }
        
         if (data.permissions.edit)
         {
            html += '<div class="onEditReply" id="' + this.id + '-onEditReply-' + safeRef + '">';
            html += '<a href="#" class="reply-action-link">' + this._msg("action.edit") + '</a>';
            html += '</div>';
         }
         html += '</div>';
          
         // avatar
         html += '<div class="authorPicture">' + Alfresco.util.people.generateUserAvatarImg(data.author) + '</div>';

         // content            
         html += '<div class="nodeContent">';
         html += '<div class="userLink">' + Alfresco.util.people.generateUserLink(data.author) + ' ' + this._msg("post.said") + ': ';
         if (data.isUpdated)
         {
            html += '<span class="theme-color-2 nodeStatus">(' + this._msg("post.updated") + ')</span>';
         }
         html += '</div>';
            
         html += '<div class="content yuieditor">' + data.content + '</div>';
         html += '</div>';
         
         // footer part
         html += '<div class="nodeFooter">';
         html += '<span class="nodeAttrLabel replyTo">' + this._msg("replies") + ': </span>';
         html += '<span class="nodeAttrValue">(' + (data.children !== undefined ? data.children.length : 0) + ') </span>';
         if (data.replyCount > 0)
         {
            html += '<span class="nodeAttrValue">';
            html += '<a href="#" class="showHideChildren" id="' + this.id + '-showHideChildren-' + safeRef + '">' + this._msg("replies.hide") + '</a>';
            html += '</span>';
         }
         html += '<span class="separator">&nbsp;</span>';
         html += '<span class="nodeAttrLabel">' + this._msg("post.postedOn") + ': ' + '</span>';
         html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.createdOn) + '</span>';
         html += '</div>';
         
         div.innerHTML = html;
      },

      /**
       * Re-renders the view UI for a reply element
       */
      rerenderReplyUI: function TopicReplies_rerenderReplyUI(nodeRef, highlight)
      {
         // Get the view element and the data and update the html
         var viewElem = Dom.get('reply-' + this.toSafeRef(nodeRef));
         var data = this.findReplyDataObject(nodeRef);
         this.renderReplyView(viewElem, data);
         
         if (highlight)
         {
            Alfresco.util.Anim.pulse(viewElem);
         }
      },
      
      
      // Actions handlers
      
      /**
       * Handler for the add reply action links.
       */
      onAddReply: function TopicReplies_onAddReply(nodeRef)
      {
         this._loadEditForm(nodeRef, false);
      },

      /**
       * Handler for the edit reply action links.
       */
      onEditReply: function TopicReplies_onEditReply(nodeRef)
      {
         this._loadEditForm(nodeRef, true);
      },
      
      /**
       * Handler for the show/hide replies toggle links
       */
      showHideChildren: function TopicReplies_showideChildren(nodeRef)
      {
         // get the replies element
         var repliesElem = Dom.get('replies-of-' + this.toSafeRef(nodeRef));
         if (Dom.hasClass(repliesElem, "hidden"))
         {
            this._showChildren(nodeRef);
         }
         else
         {
            this._hideChildren(nodeRef);
         }
      },

      /**
       * Loads the reply add or edit form.
       * 
       * @param nodeRef {string} the parent nodeRef to which a child should be added or the reply that should be edited
       * @param isEdit {boolean} if true nodeRef is edited, otherwise nodeRef is the parent of the new reply to be created
       */      
      _loadEditForm: function TopicReplies__loadEditForm(nodeRef, isEdit)
      {          
         // construct the id to use for the form elements
         var formId = this.id + this.toSafeRef(nodeRef) + (isEdit ? "-edit" : "-add");
         
         // execute ajax request to load the form
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/discussions/replies/reply-form",
            dataObj:
            {
               htmlid : formId
            },
            successCallback:
            {
               fn: this._onEditFormLoaded,
               scope: this,
               obj:
               {
                  isEdit: isEdit,
                  nodeRef: nodeRef,
                  formId: formId
               }
            },
            failureMessage: this._msg("message.loadeditform.failure")
         });
      },
      
      /**
       * Request success handler for the loadReplyEditForm ajax request
       */
      _onEditFormLoaded: function TopicReplies__onEditFormLoaded(response, obj)
      {
         // make sure no other forms are displayed
         this._hideOpenForms();
         
         // insert the form at the right location
         var safeRef = this.toSafeRef(obj.nodeRef);
         var formDiv = null;
         if (obj.isEdit)
         {
            formDiv = Dom.get('reply-edit-form-' + safeRef);
         }
         else
         {
            formDiv = Dom.get('reply-add-form-' + safeRef);
         }
         formDiv.innerHTML = response.serverResponse.responseText;
         
         // find the data object for nodeRef.
         // Note: this will be null in case of a reply to the topic itself.
         var data = this.findReplyDataObject(obj.nodeRef);
         
         // insert current values into the form
         var actionUrl = '';
         var formTitle = '';
         var content = '';
         var submitButtonLabel = '';
         var viewDiv = null;
         if (obj.isEdit)
         {
            viewDiv = Dom.get('reply-' + safeRef);
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/post/node/{nodeRef}",
            {
               nodeRef: obj.nodeRef.replace(':/', '')
            });
            formTitle = this._msg('form.updateTitle');
            submitButtonLabel = this._msg('action.update');
            content = data.content;
         }
         else
         {
            actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/post/node/{nodeRef}/replies",
            {
               nodeRef: obj.nodeRef.replace(':/', '')
            });
            
            // for root replies we don't have a parent data object and therefore can
            // tell whom to reply to
            if (data !== null)
            {
               formTitle = this._msg('form.replyToTitle', Alfresco.util.people.generateUserLink(data.author));
            }
            else
            {
               formTitle = this._msg('form.replyTitle');
            }
            submitButtonLabel = this._msg('action.create');
         }
         
         // set the values in the dom
         var formId = obj.formId;
         Dom.get(formId + "-form-title").innerHTML = formTitle;
         Dom.get(formId + "-form").setAttribute("action", actionUrl);
         Dom.get(formId + "-site").setAttribute("value", this.options.siteId);
         Dom.get(formId + "-container").setAttribute("value", this.options.containerId);
         Dom.get(formId + "-submit").setAttribute("value", submitButtonLabel);
         Dom.get(formId + "-content").value = content;
         
         // store edit related data.
         this.editData =
         {
            nodeRef: obj.nodeRef,
            isEdit: obj.isEdit,
            formId: formId,
            viewDiv: viewDiv,
            formDiv: formDiv
         };
         
         // register the form logic        
         this._registerEditForm(obj.nodeRef, formId, obj.isEdit);
      },
      
      /**
       * Registers the form logic
       */
      _registerEditForm: function TopicReplies__registerEditForm(nodeRef, formId, isEdit)
      {
         // register the okButton
         this.widgets.okButton = new YAHOO.widget.Button(formId + "-submit",
         {
            type: "submit"
         });
         
         // register the cancel button
         this.widgets.cancelButton = new YAHOO.widget.Button(formId + "-cancel",
         {
            type: "button"
         });
         this.widgets.cancelButton.subscribe("click", this.onFormCancelButtonClick, this, true);
         
         // Instantiate and render the simple editor we use for the form
         this.widgets.editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR, formId + '-content',this.options.editorConfig);
         this.widgets.editor.addPageUnloadBehaviour(this._msg("message.unsavedChanges.reply"));
         this.widgets.editor.render();
         
         // create the form that does the validation/submit
         this.widgets.form = new Alfresco.forms.Form(formId + "-form");
         var replyForm = this.widgets.form;
         replyForm.setSubmitElements(this.widgets.okButton);
         if (isEdit)
         {
            replyForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         }
         replyForm.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.savereply.success"),
            successCallback:
            {
               fn: this.onFormSubmitSuccess,
               scope: this,
               obj:
               {
                  nodeRef: nodeRef,
                  isEdit: isEdit
               }
            },
            failureMessage: this._msg("message.savereply.failure"),
            failureCallback:
            {
               fn: this.onFormSubmitFailure,
               scope: this
            }
         });
         replyForm.setSubmitAsJSON(true);
         replyForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {
               // disable buttons
               this.widgets.cancelButton.set("disabled", true);
         
               //Put the HTML back into the text area
               this.widgets.editor.save();
               
               // show a wait message
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message(this._msg("message.submitting")),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            scope: this
         };
         replyForm.init();
         
         // now show the form
         this._showForm();
         
         // finally scroll to the form
         this._scrollToElement(this.editData.formDiv);
      },
      
      /**
       * Form submit success handler
       */
      onFormSubmitSuccess: function TopicReplies_onFormSubmitSuccess(response, obj)
      {
         // remove wait message
         if (this.widgets.feedbackMessage.destroyWithAnimationsStop != undefined)
         {
            this.widgets.feedbackMessage.destroyWithAnimationsStop();
         }
         else
         {
            this.widgets.feedbackMessage.destroy();
         }
         
         var data, parentElem;
          
         // in case of an edit reply, simply update the data/ui
         if (obj.isEdit)
         {
            // update the data object for the reply
            data = this.findReplyDataObject(obj.nodeRef);
            YAHOO.lang.augmentObject(data, response.json.item, true);
            
            // rerender the ui
            this.rerenderReplyUI(data.nodeRef, true);
         }
         // in case of a create, add the new data and insert a new reply element
         else
         {
            // the logic here is slightly different for top level replies
            if (this.options.topicRef == obj.nodeRef)
            {
               // add the data object
               this.repliesData.push(response.json.item);
               
               // render the new reply
               parentElem = Dom.get(this.id + '-replies-root');
               this.renderReply(parentElem, response.json.item, true);
            }
            else
            {
               // add the data object
               data = this.findReplyDataObject(obj.nodeRef);
               // make sure the children array exists
               if (data.children === undefined)
               {
                  data.children = [];
               }
               data.children.push(response.json.item);
               
               // render the new reply
               parentElem = Dom.get('replies-of-' + this.toSafeRef(obj.nodeRef));
               this.renderReply(parentElem, response.json.item, true);
               
               // rerender the parent reply, which will update the reply count
               this.rerenderReplyUI(obj.nodeRef, false);
            }
            
            // make sure the rolover listener gets attached to the new element
            Alfresco.util.rollover.registerListenersByClassName(this.id, 'reply', 'div');

            // MNT-10636 fix. Replies count should be increased only when new reply is added and shouldn't be increased
            // when reply is updated.
            this.options.topicTotalReplyCount += 1;
            YAHOO.Bubbling.fire("addedNewReply", this.options.topicTotalReplyCount);
         }
         // finally hide the form / show the updated view in case of an edit
         this._hideOpenForms();
      },

      /**
       * Form submit failure handler
       */
      onFormSubmitFailure: function TopicReplies_onFormSubmitFailure(response, obj)
      {
         // enable buttons
         this.widgets.cancelButton.set("disabled", false);
         
         // hide message
         this.widgets.feedbackMessage.destroy();
      },

      /**
       * Edit form cancel button click handler
       */
      onFormCancelButtonClick: function TopicReplies_onFormCancelButtonClick(type, args)
      {
         this._hideOpenForms();
      },
      
      /**
       * Find the data object for a reply given its node reference.
       * 
       * @param nodeRef {string} the nodeRef of the reply to find the data for
       * @return {string} a reply data object or null if not found
       */
      findReplyDataObject: function TopicReplies_findReplyDataObject(nodeRef)
      {
         return this._findReplyDataObjectImpl(this.repliesData, nodeRef);
      },
      
      /**
       * Implementation of findReplyDataObject
       */
      _findReplyDataObjectImpl: function TopicReplies__findReplyDataObjectImpl(arr, nodeRef)
      {
         for (var  x=0; x < arr.length; x++)
         {
            // check the element
            if (arr[x].nodeRef == nodeRef)
            {
               return arr[x];
            }
            // check the children recursively
            else if (arr[x].children !== undefined)
            {
               var result = this._findReplyDataObjectImpl(arr[x].children, nodeRef);
               if (result !== null)
               {
                  return result;
               }
            }
         }
         return null;
      },
      
      /**
       * Shows the children of a reply
       * 
       * @param nodeRef the nodeRef of the reply for which children should be shown
       */
      _showChildren: function TopicReplies__showChildren(nodeRef)
      {
          // show the replies element
          var repliesElem = Dom.get('replies-of-' + this.toSafeRef(nodeRef));
          Dom.removeClass(repliesElem, "hidden");
          
          // the show/hide replies toggle link might not exist if there are no replies
          var linkElem = Dom.get(this.id + '-showHideChildren-' + this.toSafeRef(nodeRef));
          if (linkElem !== null)
          {
             linkElem.innerHTML = this._msg("replies.hide");
          }
      },
      
      /**
       * Hides the children of a reply
       * 
       * @param nodeRef the nodeRef of the reply for which children should be hidden
       */
      _hideChildren: function TopicReplies__hideChildren(nodeRef)
      {
          var repliesElem = Dom.get('replies-of-' + this.toSafeRef(nodeRef));
          Dom.addClass(repliesElem, "hidden");
          var linkElem = Dom.get(this.id + '-showHideChildren-' + this.toSafeRef(nodeRef));
          linkElem.innerHTML = this._msg("replies.show");
      },
      
      /**
       * Hides any open form, displays any hidden view element
       */
      _hideOpenForms: function TopicReplies__hideOpenForms()
      {
         if (this.editData.formDiv !== null)
         {
            Dom.addClass(this.editData.formDiv, "hidden");
            this.editData.formDiv.innerHTML = '';
            this.editData.formDiv = null;
         }
         if (this.editData.viewDiv !== null)
         {
            Dom.removeClass(this.editData.viewDiv, "hidden");
            this.editData.viewDiv = null;
         }
      },
      
      /**
       * Shows the already prepared form and hides the associated view element if any.
       */
      _showForm: function TopicReplies__showForm()
      {
         // hide the view element if any
         if (this.editData.viewDiv !== null)
         {
            Dom.addClass(this.editData.viewDiv, "hidden");
         }
         
         // show the form element
         Dom.removeClass(this.editData.formDiv, "hidden");
      },
      
      /**
       * Vertically scrolls the browser window to the passed element
       */
      _scrollToElement: function TopicReplies__scrollToElement(el)
      {
         var yPos = Dom.getY(el);
         if (YAHOO.env.ua.ie > 0)
         {
            yPos = yPos - (document.body.clientHeight / 3);
         }
         else
         {
            yPos = yPos - (window.innerHeight / 3);
         }
         window.scrollTo(0, yPos);
      },
      
      // mouse hover functionality
      
      /** Called when the mouse enters into a list item. */
      onReplyElementMouseEntered: function TopicReplies_onReplyElementMouseEntered(layer, args)
      {
         // only highlight if there are actions on the specific element
         var nodeRef = args[1].target.id.substring(('reply-').length);
         nodeRef = this.toNodeRef(nodeRef);
         
         var data = this.findReplyDataObject(nodeRef), permissions = data.permissions;
         if (!(permissions.edit || permissions.reply || permissions['delete']))
         {
            return;
         }
         
         Dom.addClass(args[1].target, 'over');
      },
      
      /** Called whenever the mouse exits a list item. */
      onReplyElementMouseExited: function TopicReplies_onReplyElementMouseExited(layer, args)
      {
         Dom.removeClass(args[1].target, 'over');
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function TopicReplies__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.TopicReplies", Array.prototype.slice.call(arguments).slice(1));
      }
      
   };
})();
