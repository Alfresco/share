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
 * Topic component.
 * Shows and allows to edit a topic.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsTopic
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
    * Topic constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TopicView} The new Topic instance
    * @constructor
    */
   Alfresco.DiscussionsTopic = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.DiscussionsTopic";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      this.modules = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datasource", "json", "connection", "event", "button", "menu", "editor"], this.onComponentsLoaded, this);
     
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
           
      YAHOO.Bubbling.on("addedNewReply", this.onAddedNewReply, this);
           
      return this;
   };
   
   Alfresco.DiscussionsTopic.prototype =
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
          * Id of the topic to display.
          * 
          * @property topicId
          * @type string
          */
         topicId: ""

      },
     
      /**
       * Holds the data displayed in this component.
       */
      topicData: null,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,
      
      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      tagId: null,
      
      /**
       * Tells whether an action is currently ongoing.
       * 
       * @property busy
       * @type boolean
       * @see _setBusy/_releaseBusy
       */
      busy: false,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsTopic_setOptions(obj)
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
      setMessages: function DiscussionsTopic_setMessages(obj)
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
      onComponentsLoaded: function DiscussionsTopic_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
  
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsTopic_onReady()
      {
         var me = this;
         
         // Hook action events.
         var fnActionHandlerDiv = function DiscussionsTopic_fnActionHandlerDiv(layer, args)
         {

            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, me.topicData.name);
                  args[1].stop = true;
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("topic-action-link-div", fnActionHandlerDiv);
         
         // register tag action handler, which will issue tagSelected bubble events.
         Alfresco.util.tags.registerTagActionHandler(this);
          
         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onTopicElementMouseEntered, this.onTopicElementMouseExited, this);
          
         // load the topic data
         this._loadTopicData();
      },
      
      
      /**
       * Loads the topic data and updates the ui.
       */
      _loadTopicData: function DiscussionsTopic__loadTopicData()
      {
         // ajax request success handler
         var loadTopicDataSuccess = function DiscussionsTopic__loadTopicData_loadTopicDataSuccess(response)
         {
            // set the loaded data
            var json = response.json;
            if (json)
            {
               this.topicData = json.item;
               
               // render the ui
               this.renderUI();
               
               // inform the comment components about the loaded post
               this._fireTopicDataChangedEvent();
            }
            else
            {
               var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/discussions-topiclist",
               {
                  site: this.options.siteId
               });
               window.location = url;
            }
         };
         
         // construct url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/forum/post/site/{site}/{container}/{topicId}",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            topicId: this.options.topicId
         });
         
         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            successCallback:
            {
               fn: loadTopicDataSuccess,
               scope: this
            },
            failureMessage: this._msg("message.loadtopicdata.failure")
         });
      },

      /**
       * Renders the UI with the data available in the component.
       */
      renderUI: function DiscussionsTopic_renderUI()
      {   
         // get the container div
         var viewDiv = Dom.get(this.id + '-topic-view-div');
         
         // render the topic and insert the resulting html
         var html = this.renderTopic(this.topicData);
         viewDiv.innerHTML = html;
         
         // attach the rollover listeners
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'topic', 'div');
      },
      
      /**
       * Renders the topic.
       * 
       * @param data {object} the data object containing the topic data
       * @return {string} html representing the data
       */
      renderTopic: function DiscussionsTopic_renderTopic(data)
      {
         var html = '';
          
         html += '<div id="' + this.id + '-topicview" class="node topic topicview">'
         
         // actions
         html += '<div class="nodeEdit">';
         if (data.permissions.reply)
         {
            html += '<div class="onAddReply"><a href="#" class="topic-action-link-div">' + this._msg("action.reply") + '</a></div>';   
         }
         if (data.permissions.edit)
         {
            html += '<div class="onEditTopic"><a href="#" class="topic-action-link-div">' + this._msg("action.edit") + '</a></div>';
         }
         if (data.permissions['delete'])
         {
            html += '<div class="onDeleteTopic"><a href="#" class="topic-action-link-div">' + this._msg("action.delete") + '</a></div>';
         }
         html += '</div>';
  
         // avatar
         html += '<div class="authorPicture">' + Alfresco.util.people.generateUserAvatarImg(data.author) + '</div>';

         // content
         html += '<div class="nodeContent">';
         html += '<div class="nodeTitle"><a href="' + Alfresco.util.discussions.getTopicViewPage(this.options.siteId, this.options.containerId, data.name) + '">' + $html(data.title) + '</a> ';
         if (data.isUpdated)
         {
            html += '<span class="theme-color-2 nodeStatus">(' + this._msg("post.updated") + ')</span>';
         }
         html += '</div>';
         
         html += '<div class="published">';
         html += '<span class="nodeAttrLabel">' + this._msg("post.createdOn") + ': </span>';
         html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.createdOn) + '</span>';
         html += '<span class="separator">&nbsp;</span>';
         html += '<span class="nodeAttrLabel">' + this._msg("post.author") + ': </span>';
         html += '<span class="nodeAttrValue">' + Alfresco.util.people.generateUserLink(data.author) + '</span>';
         html += '<br />';
         if (data.lastReplyBy)
         {
            html += '<span class="nodeAttrLabel">' + this._msg("post.lastReplyBy") + ': </span>';
            html += '<span class="nodeAttrValue">' + Alfresco.util.people.generateUserLink(data.lastReplyBy) + '</span>';                  
            html += '<span class="separator">&nbsp;</span>';
            html += '<span class="nodeAttrLabel">' + this._msg("post.lastReplyOn") + ': </span>';
            html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.lastReplyOn) + '</span>';
         }
         else
         {
            html += '<span class="nodeAttrLabel">' + this._msg("replies.label") + ': </span>';
            html += '<span class="nodeAttrValue">' + this._msg("replies.noReplies") + '</span>';                  
         }
         html += '</div>';
             
         html += '<div class="userLink">' + Alfresco.util.people.generateUserLink(data.author) + ' ' + this._msg("said") + ':</div>';
         html += '<div class="content yuieditor">' + data.content + '</div>';
         html += '</div>'
         // end view

         // begin footer
         html += '<div class="nodeFooter">';
         html += '<span class="nodeAttrLabel replyTo">' + this._msg("replies.label") + ': </span>';
         html += '<span class="nodeAttrValue">(' + data.totalReplyCount + ')</span>';
         html += '<span class="separator">&nbsp;</span>';
             
         html += '<span class="nodeAttrLabel tagLabel">' + this._msg("tags.label") +': </span>';
         if (data.tags.length > 0)
         {
            for (var x=0; x < data.tags.length; x++)
            {
               if (x > 0)
               {
                  html += ", ";
               }
               html += Alfresco.util.tags.generateTagLink(this, data.tags[x]);
            }
         }
         else
         {
            html += '<span class="nodeAttrValue">' + this._msg("tags.noTags") + '</span>';
         }
         html += '</div></div></div>';
          
         return html;
      },

      /**
       * Handler for add reply action link
       */
      onAddReply: function DiscussionsTopic_onAddReply(htmlId, ownerId, param)
      {
         YAHOO.Bubbling.fire('addReplyToPost',
         {
            postRef : this.topicData.nodeRef
         });
      },
     
      /**
       * Handler for edit topic action link
       */
      onEditTopic: function DiscussionsTopic_onEditTopic()
      {
         window.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/discussions-createtopic?topicId=" + this.options.topicId;
      },
     
      /**
       * Handler for delete topic action link
       */
      onDeleteTopic: function DiscussionsTopic_onDeleteTopic()
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this._msg("message.confirm.delete.title"),
            text: this._msg("message.confirm.delete", $html(this.topicData.title)),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DiscussionsTopic_onDeleteTopic_delete()
               {
                  this.destroy();
                  me._deleteTopicConfirm.call(me);
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function DiscussionsTopic_onDeleteTopic_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Delete topic implementation
       */
      _deleteTopicConfirm: function DiscussionsTopic__deleteTopicConfirm()
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }          
          
         // ajax request success handler
         var onDeleted = function onDeleted(response)
         {
            var listUrl = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/discussions-topiclist",
            {
               site: this.options.siteId
            });
            window.location = listUrl;
         };
         
         // construct the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/post/site/{site}/{container}/{topicId}?page=discussions-topicview",
         {
            site : this.options.siteId,
            container: this.options.containerId,
            topicId: encodeURIComponent(this.options.topicId)
         });
         
         // perform the ajax request to delete the topic
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successMessage: this._msg("message.delete.success"),
            successCallback:
            {
               fn: onDeleted,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure"),
            failureCallback:
            {
               fn: function(response)
               {
                  this._releaseBusy();
               },
               scope: this
            }
         });
      },
      
      /**
       * Tag selected handler
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function DiscussionsTopic_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            // construct the topic list url with initial active tag filter
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/discussions-topiclist?filterId={filterId}&filterOwner={filterOwner}&filterData={filterData}",
            {
               site: this.options.siteId,
               filterId: "tag",
               filterOwner: "Alfresco.TagFilter",
               filterData: encodeURIComponent(obj.tagName)
            });

            window.location = url;
         }
      },

      onAddedNewReply: function DiscussionsTopic_onAddedNewReply(layer, args)
      {
           this.topicData.totalReplyCount = args[1];
            
           // render the ui
           this.renderUI();
      },

      /**
       * Loads the edit form.
       */
      _loadEditForm: function DiscussionsTopic__loadEditForm()
      {  
         // Load the UI template from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/discussions/topic/edit-topic",
            dataObj:
            {
               htmlid: this.id + "-form"
            },
            successCallback:
            {
               fn: this.onEditFormLoaded,
               scope: this
            },
            failureMessage: this._msg("message.loadeditform.failure")
         });
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onFormLoaded
       * @param response {object} Server response from load template XHR request
       */
      onEditFormLoaded: function DiscussionsTopic_onEditFormLoaded(response)
      {
         // id to use for the form
         var formId = this.id + "-form";
          
         // use the already loaded data
         var data = this.topicData;
          
         // find the edit div to populate
         var editDiv = Dom.get(this.id + "-topic-edit-div");
         
         // insert the html
         editDiv.innerHTML = response.serverResponse.responseText;
         
         // insert current values into the form
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/post/site/{site}/{container}/{topicId}",
         {
            site: this.options.siteId,
            container : this.options.containerId,
            topicId: this.options.topicId
         });
         Dom.get(formId + "-form").setAttribute("action", actionUrl);
         Dom.get(formId + "-site").setAttribute("value", this.options.siteId);
         Dom.get(formId + "-container").setAttribute("value", this.options.containerId);
         Dom.get(formId + "-title").setAttribute("value", data.title);
         Dom.get(formId + "-content").value = data.content;

         // and finally register the form handling
         this._registerEditTopicForm(data, formId);
      },

      /**
       * Registers the form logic
       */
      _registerEditTopicForm: function DiscussionsTopic__registerEditTopicForm(data, formId)
      {
         // add the tags that are already set on the post
         if (this.modules.tagLibrary == undefined)
         {
            this.modules.tagLibrary = new Alfresco.module.TagLibrary(formId);
            this.modules.tagLibrary.setOptions(
            {
               siteId: this.options.siteId
            });
         }
         this.modules.tagLibrary.setTags(this.topicData.tags);
         
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
         this.widgets.cancelButton.subscribe("click", this.onEditFormCancelButtonClick, this, true);
         
         // instantiate the simple editor we use for the form
         this.widgets.editor = new YAHOO.widget.SimpleEditor(formId + '-content',
         {
             height: '180px',
             width: '700px',
             dompath: false, //Turns on the bar at the bottom
             animate: false, //Animates the opening, closing and moving of Editor windows
             toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg)
         });
         this.widgets.editor.addPageUnloadBehaviour(this._msg("message.unsavedChanges.reply"));
         this.widgets.editor.render();
         
         // create the form that does the validation/submit
         var editForm = new Alfresco.forms.Form(formId + "-form");
         editForm.setSubmitElements(this.widgets.okButton);
         editForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         editForm.setAJAXSubmit(true,
         {
            successMessage: this._msg("message.savetopic.success"),
            successCallback:
            {
               fn: this.onEditFormSubmitSuccess,
               scope: this
            },
            failureMessage: this._msg("message.savetopic.failure"),
            failureCallback:
            {
               fn: this.onEditFormSubmitFailure,
               scope: this
            }
         });
         editForm.setSubmitAsJSON(true);
         editForm.doBeforeFormSubmit =
         {
            fn: function(form, obj)
            {   
               // disable the buttons
               this.widgets.cancelButton.set("disabled", true);
               
               //Put the HTML back into the text area
               this.widgets.editor.saveHTML();
               
               // update the tags set in the form
               this.modules.tagLibrary.updateForm(formId + "-form", "tags");
               
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
         
         this.modules.tagLibrary.initialize(editForm);
         editForm.init();
         
         // show the form and hide the view
         this._showEditView();
         
         // TODO: disabled as it does not work correctly on IE. The focus is set
         // but hitting tab moves the focus to the focus to the address bar instead
         // of the editor
         // focus the title text field
         //Dom.get(formId + "-title").focus();
      },
      
      /**
       * Edit form submit success handler
       */
      onEditFormSubmitSuccess: function DiscussionsTopic_onEditFormSubmitSuccess(response, object)
      {
         // remove busy message
         this._releaseBusy();
         
         // the response contains the new data for the comment. Render the comment html
         // and insert it into the view element
         this.topicData = response.json.item;
         this.renderUI();
            
         // hide the form and show the ui
         this._hideEditView();
            
         // inform the replies object about the update
         this._fireTopicDataChangedEvent();
      },
      
      /**
       * Edit form submit failure handler
       */
      onEditFormSubmitFailure: function DiscussionsTopic_onEditFormSubmitFailure(response, object)
      {
         // remove busy message
         this._releaseBusy();
          
         // enable the buttons
         this.widgets.cancelButton.set("disabled", false);
      },
      
      /**
       * Edit form cancel button click handler
       */
      onEditFormCancelButtonClick: function(type, args)
      {
          this._hideEditView();
      },
      
      /**
       * Hides the form and displays the view
       */
      _hideEditView: function()
      {
         var editDiv = Dom.get(this.id + "-topic-edit-div");
         var viewDiv = Dom.get(this.id + "-topic-view-div");
         Dom.addClass(editDiv, "hidden");
         Dom.removeClass(viewDiv, "hidden");
         editDiv.innerHTML = '';
      },
      
      /**
       * Hides the view and displays the form
       */
      _showEditView: function()
      {
         var editDiv = Dom.get(this.id + "-topic-edit-div");
         var viewDiv = Dom.get(this.id + "-topic-view-div");
         Dom.addClass(viewDiv, "hidden");
         Dom.removeClass(editDiv, "hidden");
      },

      /**
       * Called when the mouse enters into the topic div
       */
      onTopicElementMouseEntered: function DiscussionsTopicList_onTopicElementMouseEntered(layer, args)
      {
         // make sure the user sees at least one action, otherwise we won't highlight
         var permissions = this.topicData.permissions;
         if (!(permissions.edit || permissions["delete"]))
         {
            return;
         } 
         
         Dom.addClass(args[1].target, 'over');
      },
     
      /**
       * Called whenever the mouse exits the topic div
       */
      onTopicElementMouseExited: function DiscussionsTopicList_onTopicElementMouseExited(layer, args)
      {
         Dom.removeClass(args[1].target, 'over');
      },

      /**
       * Fires a topic data changed bubble event
       */
      _fireTopicDataChangedEvent: function DiscussionsTopicList__fireTopicDataChangedEvent()
      {
         var eventData =
         {
            topicRef: this.topicData.nodeRef,
            topicTitle: this.topicData.title,
            topicId: this.topicData.name,
            topicTotalReplyCount: this.topicData.totalReplyCount
         };
         YAHOO.Bubbling.fire("topicDataChanged", eventData);
      },
      
      /**
       * Displays the provided busyMessage but only in case
       * the component isn't busy set.
       * 
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function DiscussionsTopic__setBusy(busyMessage)
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
       */
      _releaseBusy: function DiscussionsTopic__releaseBusy()
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
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DiscussionsTopic_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsTopic", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
