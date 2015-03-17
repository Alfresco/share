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
 * DiscussionsTopicList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsTopicList
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
    * DiscussionsTopicList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DiscussionsTopicList} The new DiscussionsTopicList instance
    * @constructor
    */
   Alfresco.DiscussionsTopicList = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.DiscussionsTopicList";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.currentFilter = {};
      this.widgets = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "dom", "datasource", "datatable", "paginator", "event", "element"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
      YAHOO.Bubbling.on("changeFilter", this.onChangeFilter, this);
      YAHOO.Bubbling.on("topiclistRefresh", this.onDiscussionsTopicListRefresh, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      
      return this;
   }
   
   Alfresco.DiscussionsTopicList.prototype =
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
          * Initially used filter name and id.
          */
         initialFilter: {},

         /**
          * Number of items displayed per page
          * 
          * @property pageSize
          * @type int
          */
         pageSize: 10,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property simpleView
          * @type boolean
          */
         simpleView: false,
         
         /**
          * Maximum length of topic to show in list view
          *
          * @property maxContentLength
          * @type int
          * @default 512
          */
         maxContentLength: 512
      },
      
      /**
       * Current filter to filter topic list.
       * 
       * @property currentFilter
       * @type object
       */
      currentFilter: null,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets : null,

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
       * Offset of first record on page
       * 
       * @property recordOffset
       * @type int
       * @default 0
       */
      recordOffset: 0,

      /**
      * Total number of topics in the current view (across all pages)
       * 
       * @property totalRecords
       * @type int
       * @default 0
       */
      totalRecords: 0,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function DiscussionsTopicList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setMessages: function DiscussionsTopicList_setMessages(obj)
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
      onComponentsLoaded: function DiscussionsTopicList_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsTopicList_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // Simple view button
         this.widgets.simpleView = Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView);
         
         // Called by the paginator on state changes
         var handlePagination = function DiscussionsTopicList_handlePagination(state, dt)
         {
            me.widgets.paginator.setState(state);
            me._updateDiscussionsTopicList(
            {
               page: state.page
            });
         }
         
         // YUI Paginator definition
         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this._msg("pagination.template"),
            pageReportTemplate: this._msg("pagination.template.page-report"),
            previousPageLinkLabel : this._msg("pagination.previousPageLinkLabel"),
            nextPageLinkLabel     : this._msg("pagination.nextPageLinkLabel")
         });
         this.widgets.paginator.subscribe("changeRequest", handlePagination, this);

         // Hook action events for details view
         var fnActionHandlerDiv = function DiscussionsTopicList_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, target.offsetParent, owner);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("topic-action-link-div", fnActionHandlerDiv);
         
         // Hook action events for simple view
         var fnActionHandlerSpan = function DiscussionsTopicList_fnActionHandlerSpan(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, target.offsetParent, owner);
                  args[1].stop = true;
               }
            }

            return true;
         }
         YAHOO.Bubbling.addDefaultAction("topic-action-link-span", fnActionHandlerSpan);
         
         // register tag action handler, which will issue tagSelected bubble events.
         Alfresco.util.tags.registerTagActionHandler(this);

         // DataSource definition
         var uriDiscussionsTopicList = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/forum/site/{site}/{container}/posts",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDiscussionsTopicList,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
               resultsList: "items",
               metaFields:
               {
                  recordOffset: "startIndex",
                  totalRecords: "total",
                  forumPermissions: "forumPermissions"
               }
            }
         });
         
         var generateTopicActions = function(me, data, tagName)
         {
            var html = '';
            // begin actions
            html += '<div class="nodeEdit">';
            html += '<' + tagName + ' class="onViewTopic"><a href="#" class="topic-action-link-' + tagName + '">' + me._msg("action.view") + '</a></' + tagName + '>';   
            if (data.permissions.edit)
            {
               html += '<' + tagName + ' class="onEditTopic"><a href="#" class="topic-action-link-' + tagName + '">' + me._msg("action.edit") + '</a></' + tagName + '>';
            }
            if (data.permissions['delete'])
            {
               html += '<' + tagName + ' class="onDeleteTopic"><a href="#" class="topic-action-link-' + tagName + '">' + me._msg("action.delete") + '</a></' + tagName + '>';
            }
            html += '</div>';
            return html;
         };        
         
         /**
          * Topic element renderer. We use the data table as a one-column table, this renderer
          * thus renders the complete element.
          *
          * @method renderTopic
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderTopic = function DiscussionsTopicList_renderTopic(elCell, oRecord, oColumn, oData)
         {
            // hide the parent temporarily as we first insert the structure and then the content
            // to avoid problems caused by broken xhtml
            Dom.addClass(elCell, 'hidden');
             
            // precalculate some values
            var data = oRecord.getData();
            var topicViewUrl = Alfresco.util.discussions.getTopicViewPage(me.options.siteId, me.options.containerId, data.name);
            var authorLink = Alfresco.util.people.generateUserLink(data.author);
            
            var html = "";
            
            // detailed view
            if (!me.options.simpleView)
            {
               html += '<div class="node topic">';

               // actions
               html += generateTopicActions(me, data, 'div');
   
               // begin view
               html += '<div class="nodeContent">';
               html += '<span class="nodeTitle"><a href="' + topicViewUrl + '">' + $html(data.title) + '</a> ';
               if (data.isUpdated)
               {
                  html += '<span class="theme-color-2 nodeStatus">(' + me._msg("post.updated") + ')</span>';
               }
               html += '</span>';
               html += '<div class="published">';
               html += '<span class="nodeAttrLabel">' + me._msg("post.createdOn") + ': </span>';
               html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.createdOn) + '</span>';
               html += '<span class="separator">&nbsp;</span>';
               html += '<span class="nodeAttrLabel">' + me._msg("post.author") + ': </span>';
               html += '<span class="nodeAttrValue">' + authorLink + '</span>';
               html += '<br />';
               if (data.lastReplyBy)
               {
                  html += '<span class="nodeAttrLabel">' + me._msg("post.lastReplyBy") + ': </span>';
                  html += '<span class="nodeAttrValue">' + Alfresco.util.people.generateUserLink(data.lastReplyBy) + '</span>';                  
                  html += '<span class="separator">&nbsp;</span>';
                  html += '<span class="nodeAttrLabel">' + me._msg("post.lastReplyOn") + ': </span>';
                  html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.lastReplyOn) + '</span>';
               }
               else
               {
                  html += '<span class="nodeAttrLabel">' + me._msg("replies.label") + ': </span>';
                  html += '<span class="nodeAttrValue">' + me._msg("replies.noReplies") + '</span>';                  
               }
               html += '</div>';
               
               html += '<div class="userLink">' + authorLink + ' ' + me._msg("said") + ':</div>';
               html += '<div class="content yuieditor"></div>';
               html += '</div>'
               // end view

               html += '</div>';

               // begin footer
               html += '<div class="nodeFooter">';
               html += '<span class="nodeAttrLabel replyTo">' + me._msg("replies.label") + ': </span>';
               html += '<span class="nodeAttrValue">(' + data.totalReplyCount + ')</span>';
               html += '<span class="separator">&nbsp;</span>';
               
               html += '<span class="nodeAttrValue"><a href="' + topicViewUrl + '">' + me._msg("action.read") + '</a></span>';
               html += '<span class="separator">&nbsp;</span>';
               
               html += '<span class="nodeAttrLabel tagLabel">' + me._msg("label.tags") +': </span>';
               if (data.tags.length > 0)
               {
                  for (var x=0; x < data.tags.length; x++)
                  {
                     if (x > 0)
                     {
                        html += ", ";
                     }
                     html += Alfresco.util.tags.generateTagLink(me, data.tags[x]);
                  }
               }
               else
               {
                  html += '<span class="nodeAttrValue">' + me._msg("tags.noTags") + '</span>';
               }
               html += '</div></div>';
               // end
            }
            
            // simple view
            else
            {
               // add a class to the parent div so that we can add a separator line in the simple view
               Dom.addClass(elCell, 'row-separator');
                
               html += '<div class="node topic simple">';
               
               // begin actions
               html += generateTopicActions(me, data, 'span');
   
               // begin view
               html += '<div class="nodeContent">';
               html += '<span class="nodeTitle"><a href="' + topicViewUrl + '">' + $html(data.title) + '</a> ';
               if (data.isUpdated)
               {
                  html += '<span class="theme-color-2 nodeStatus">(' + me._msg("post.updated") + ')</span>';
               }
               html += '<div class="published">';
               html += '<span class="nodeAttrLabel">' + me._msg("post.createdOn") + ': </span>';
               html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.createdOn) + '</span>';
               html += '<span class="separator">&nbsp;</span>';
               html += '<span class="nodeAttrLabel">' + me._msg("post.author") + ': </span>';
               html += '<span class="nodeAttrValue">' + authorLink + '</span>';
               html += '</div>';
               html += '</div>';
               html += '</div>';
            }
             
            // assign html        
            elCell.innerHTML = html;
            
            // finally add the content. We do this here to avoid a broken page layout, as
            // data.content isn't valid xhtml.
            if (! me.options.simpleView)
            {
               var contentElem = Dom.getElementsByClassName("content", "div", elCell);
               if (contentElem.length == 1)
               {
                  contentElem[0].innerHTML = data.content;
               }
            }
            
            // now show the element
            Dom.removeClass(elCell, 'hidden');
         }

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "topics", label: "Topics", sortable: false, formatter: renderTopic
         }];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-topiclist", columnDefinitions, this.widgets.dataSource,
         {
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this._msg("message.loading")
         });
         
         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function DL_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            // Save metadata for Paginator update later
            me.recordOffset = oResponse.meta.recordOffset;
            me.totalRecords = oResponse.meta.totalRecords;

            oPayload = oPayload || {};
            oPayload.recordOffset = oResponse.meta.recordOffset;
            oPayload.totalRecords = oResponse.meta.totalRecords;
            return oPayload;
         }

         // Prevent the DataTable from generating a second data request
         this.widgets.dataTable.doBeforePaginatorChange = function DL_doBeforePaginatorChange(oPaginatorState)
         {
            return false;
         }

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            this.widgets.paginator.setState(
            {
               recordOffset: this.recordOffset,
               totalRecords: this.totalRecords
            });
            this.widgets.paginator.render();
         }, this, true);

         // Custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

         // Hook tableMsgShowEvent to clear out fixed-pixel width on <table> element (breaks resizer)
         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function DiscussionsTopicList_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results && !me.options.usePagination)
            {
               this.renderLoopSize = Alfresco.util.RENDERLOOPSIZE;
            }

            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // issue a filterChanged bubble event to load the list and to
         // update the other components on the page
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "new",
            filterOwner: "Alfresco.DiscussionsTopicListFilter",
            filterData: null
         }, this.options.initialFilter);
         YAHOO.Bubbling.fire("changeFilter", filterObj);
      },
      
      /**
       * Action handler for the simple view toggle button
       */
      onSimpleView: function DiscussionsTopicList_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("label", this._msg(this.options.simpleView ? "header.detailList" : "header.simpleList"));

         // update the list
         YAHOO.Bubbling.fire("topiclistRefresh");
         Event.preventDefault(e);
      },
      
      /**
       * Handler for the view topic action links
       *
       * @method onActionDelete
       * @param row {object} DataTable row representing file to be actioned
       */
      onViewTopic: function DiscussionsTopicList_onViewTopic(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         window.location = Alfresco.util.discussions.getTopicViewPage(this.options.siteId, this.options.containerId, record.getData('name'));
      },

      /**
       * Handler for the edit topic action links
       */
      onEditTopic: function DiscussionsTopicList_onEditTopic(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/discussions-createtopic?topicId={topicId}",
         {
            site: this.options.siteId,
            topicId: record.getData('name')
         });
         window.location = url;                  
      },
      
      /**
       * Tag selected handler (document details)
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function DiscussionsTopicList_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var filterObj =
            {
               filterId: obj.tagName,
               filterOwner: "Alfresco.TagFilter",
               filterData: null
            };
            YAHOO.Bubbling.fire("changeFilter", filterObj);
         }
      },
      
      /**
       * Handler for the delete topic action links.
       */
      onDeleteTopic: function DiscussionsTopicList_onDeleteTopic(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this._msg("message.confirm.delete.title"),
            text: this._msg("message.confirm.delete", $html(record.getData('title'))),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DiscussionsTopicList_onDeleteTopic_delete()
               {
                  this.destroy();
                  me._deleteTopicConfirm.call(me, record.getData('name'));
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function DiscussionsTopicList_onDeleteTopic_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Delete a topic.
       * 
       * @param topicId {string} the id of the topic to delete
       */
      _deleteTopicConfirm: function DiscussionsTopicList__deleteTopicConfirm(topicId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }
          
         // ajax request success handler
         var onDeleted = function DiscussionsTopicList__deleteTopic_onDeleted(response)
         {
            // remove busy message
            this._releaseBusy();
            
            // reload the table
            this._updateDiscussionsTopicList();
         };
          
         // construct the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/forum/post/site/{site}/{container}/{topicId}?page=discussions-topicview",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            topicId: encodeURIComponent(topicId)
         });
         
         // execute the request
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
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function DiscussionsTopicList_onEventHighlightRow(oArgs)
      {
         // only highlight if we got actions to show
         var record = this.widgets.dataTable.getRecord(oArgs.target.id);
         if (record)
         {
            var permissions = record.getData('permissions');
            if (!(permissions.edit || permissions["delete"]))
            {
               return;
            }
         }
         var elem = Dom.getElementsByClassName('topic', null, oArgs.target, null);
         Dom.addClass(elem, 'over');
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function DiscussionsTopicList_onEventUnhighlightRow(oArgs)
      {
         var elem = Dom.getElementsByClassName('topic', null, oArgs.target, null);
         Dom.removeClass(elem, 'over');
      },
      
      /**
       * DiscussionsTopicList View change filter event handler
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function DiscussionsTopicList_onChangeFilter(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            // Should be a filterId in the arguments
            this.currentFilter =
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            };
            this._updateDiscussionsTopicList(
            {
               page: 1
            });
            YAHOO.Bubbling.fire("filterChanged", this.currentFilter);
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DiscussionsTopicList_onDeactivateAllControls(layer, args)
      {
         var index, widget, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },
      
      /**
       * Update the list title.
       */
      updateListTitle: function DiscussionsTopicList_updateListTitle()
      {
         var elem = Dom.get(this.id + '-listtitle'),
            title = this._msg("title.generic"),
            filterOwner = this.currentFilter.filterOwner,
            filterId = this.currentFilter.filterId,
            filterData = this.currentFilter.filterData;
         
         if (filterOwner == "Alfresco.TopicListFilter")
         {
            if (filterId == "new")
            {
                title = this._msg("title.newtopics");
            }
            if (filterId == "hot")
            {
               title = this._msg("title.hottopics");
            }
            else if (filterId == "all")
            {
               title = this._msg("title.alltopics");
            }
            else if (filterId == "mine")
            {
               title = this._msg("title.mytopics");
            }
         }
         else if (filterOwner == "Alfresco.TagFilter")
         {
            title = this._msg("title.bytag", $html(filterData));
         }
         
         elem.innerHTML = title;
      },

      /**
       * DiscussionsTopicList Refresh Required event handler
       *
       * @method onDiscussionsTopicListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDiscussionsTopicListRefresh: function DiscussionsTopicList_onDiscussionsTopicListRefresh(layer, args)
      {
         this._updateDiscussionsTopicList({});
      },

      /**
       * Displays the provided busyMessage but only in case
       * the component isn't busy set.
       * 
       * @return true if the busy state was set, false if the component is already busy
       */
      _setBusy: function BlogPostList__setBusy(busyMessage)
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
      _releaseBusy: function BlogPostList__releaseBusy()
      {
         if (this.busy)
         {
            if (this.widgets.busyMessage.destroyWithAnimationsStop != undefined)
            {
               this.widgets.busyMessage.destroyWithAnimationsStop();
            }
            else
            {
               this.widgets.busyMessage.destroy();
            }
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
      _msg: function DiscussionsTopicList_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DiscussionsTopicList", Array.prototype.slice.call(arguments).slice(1));
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function DiscussionsTopicList__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.DiscussionsTopicList"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.DiscussionsTopicList"));
      },
      
      /**
       * Updates topic list by calling data webscript with current site and filter information
       *
       * @method _updateDiscussionsTopicList
       */
      _updateDiscussionsTopicList: function DiscussionsTopicList__updateDiscussionsTopicList(p_obj)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         var successHandler = function DiscussionsTopicList__updateDiscussionsTopicList_successHandler(sRequest, oResponse, oPayload)
         {
            //this.currentPath = successPath;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.updateListTitle();
         }
         
         var failureHandler = function DiscussionsTopicList__updateDiscussionsTopicList_failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     // Site or container not found - deactivate controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         }
         
         // get the url to call
         this.widgets.dataSource.sendRequest(this._buildParams(p_obj || {}),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },
      
      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       */
      _buildParams: function DiscussionsTopicList__buildParams(p_obj)
      {
         var params =
         {
            contentLength: this.options.maxContentLength,
            tag: null,
            page: this.widgets.paginator.getCurrentPage() || "1",
            pageSize: this.widgets.paginator.getRowsPerPage()
         }
         
         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            params = YAHOO.lang.merge(params, p_obj);
         }

         // add the pageSize param
         params.startIndex = (params.page-1) * params.pageSize;

         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;       
         
         // check whether we got a filter or not
         var url = "";
         if (filterOwner == "Alfresco.TopicListFilter")
         {
            // latest only
            if (filterId == "all")
            {
                url = "";
            }
            if (filterId == "new")
            {
                url = "/new";
            }
            else if (filterId == "hot")
            {
                url = "/hot"
            }
            else if (filterId == "mine")
            {
                url = "/myposts"
            }
         }
         else if (filterOwner == "Alfresco.TagFilter")
         {
            params.tag = encodeURIComponent(filterData);
         }
         
         // build the url extension
         var urlExt = "";
         for (paramName in params)
         {
            if (params[paramName] !== null)
            {
               urlExt += "&" + paramName + "=" + encodeURIComponent(params[paramName]);
            }
         }
         if (urlExt.length > 0)
         {
            urlExt = urlExt.substring(1);
         }
         return url + "?" + urlExt;
      }
   };
})();
