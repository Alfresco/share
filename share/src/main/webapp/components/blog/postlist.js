/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * BlogPostList component.
 *
 * @namespace Alfresco
 * @class Alfresco.BlogPostList
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
    * BlogPostList constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostList} The new BlogPostList instance
    * @constructor
    */
   Alfresco.BlogPostList = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.BlogPostList";
      this.id = htmlId;

      /* Initialise prototype properties */
      this.widgets = {};
      this.currentFilter = {};
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
      YAHOO.Bubbling.on("blogConfigChanged", this.onBlogConfigChanged, this);
      YAHOO.Bubbling.on("changeFilter", this.onChangeFilter, this);
      YAHOO.Bubbling.on("blogpostlistRefresh", this.onBlogPostListRefresh, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);

      return this;
   };

   Alfresco.BlogPostList.prototype =
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
          * Initially used filter name and id.
          */
         initialFilter: {},

         /**
          * Number of items per page
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
          * Maximum length of post to show in list view
          *
          * @property maxContentLength
          * @type int
          * @default 512
          */
         maxContentLength: 512
      },

      /**
       * Current filter to filter blog post list.
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
       * Offset of first record on page
       *
       * @property recordOffset
       * @type int
       * @default 0
       */
      recordOffset: 0,

      /**
       * Total number of posts in the current view (across all pages)
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
      setOptions: function BlogPostList_setOptions(obj)
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
      setMessages: function BlogPostList_setMessages(obj)
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
      onComponentsLoaded: function BlogPostList_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPostList_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // Simple view button
         this.widgets.simpleView = Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView);

         // called by the paginator on state changes
         var handlePagination = function BlogPostList_handlePagination(state, dt)
         {
            //me.currentPage = state.page;
            me._updateBlogPostList(
            {
               page: state.page
            });
         };

         // YUI Paginator definition
         this.widgets.paginator = new YAHOO.widget.Paginator(
         {
            containers: [this.id + "-paginator"],
            rowsPerPage: this.options.pageSize,
            initialPage: 1,
            template: this._msg("pagination.template"),
            pageReportTemplate: this._msg("pagination.template.page-report"),
            previousPageLinkLabel: this._msg("pagination.previousPageLinkLabel"),
            nextPageLinkLabel: this._msg("pagination.nextPageLinkLabel")
         });

         this.widgets.paginator.subscribe("changeRequest", handlePagination);

         // Hook action events for details view
         var fnActionHandlerDiv = function BlogPostList_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.className] == "function")
               {
                  args[1].stop = true;
                  me[owner.className].call(me, args[1].target.offsetParent, owner);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("blogpost-action-link-div", fnActionHandlerDiv);

         // Hook action events for simple view
         var fnActionHandlerSpan = function BlogPostList_fnActionHandlerSpan(layer, args)
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
         };
         YAHOO.Bubbling.addDefaultAction("blogpost-action-link-span", fnActionHandlerSpan);

         // DataSource definition
         var uriBlogPostList = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/blog/site/{site}/{container}/posts",
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         this.widgets.dataSource = new YAHOO.util.DataSource(uriBlogPostList,
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
                  metadata: "metadata",
                  totalRecordsUpper: 'totalRecordsUpper'
               }
            }
         });

         /**
          * Blog post element. We only have a list and not an acutal table, there is therefore
          * only one column renderer
          *
          * @method renderBlogPost
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderBlogPost = function BlogPostList_renderBlogPost(elCell, oRecord, oColumn, oData)
         {
            // hide the parent temporarily as we first insert the structure and then the content
            // to avoid problems caused by broken xhtml
            Dom.addClass(elCell, 'hidden');

            // fetch the data and pregenerate some values
            var data = oRecord.getData();
            var postViewUrl = Alfresco.util.blog.generateBlogPostViewUrl(me.options.siteId, me.options.containerId, data.name);
            var statusLabel = Alfresco.util.blog.generatePostStatusLabel(me, data);
            //ALF-18527
            var authorLink = data.author.username;
            // firstName is a mandatory field for user
            if (typeof data.author.firstName != "undefined")
            {
               authorLink = Alfresco.util.people.generateUserLink(data.author);
            }

            var html = "";
            // detailed view
            if (!me.options.simpleView)
            {
               html += '<div class="node post">';

               // actions
               html += Alfresco.util.blog.generateBlogPostActions(me, data, 'div');

               // begin view
               html += '<div class="nodeContent">';
               html += '<span class="nodeTitle"><a href="' + postViewUrl + '">' + $html(data.title) + '</a> ';
               html += '<span class="theme-color-2 nodeStatus">' + statusLabel + '</span></span>';
               html += '<div class="published">';
               if (!data.isDraft)
               {
                  html += '<span class="nodeAttrLabel">' + me._msg("post.publishedOn") + ': </span>';
                  html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.releasedOn) + '</span>';
                  html += '<span class="separator">&nbsp;</span>';
               }
               html += '<span class="nodeAttrLabel">' + me._msg("post.author") + ': </span>';
               html += '<span class="nodeAttrValue">' + authorLink + '</span>';
               if (data.isPublished && data.postLink && data.postLink.length > 0)
               {
                  html += '<span class="separator">&nbsp;</span>';
                  html += '<span class="nodeAttrLabel">' + me._msg("post.externalLink") + ': </span>';
                  html += '<span class="nodeAttrValue"><a target="_blank" href="' + data.postLink + '">' + me._msg("post.clickHere") + '</a></span>';
               }
               html += '</div>';
               html += '<div class="content yuieditor"></div>';
               html += '</div>';
               // end view

               html += '</div>';

               // begin footer
               html += '<div class="nodeFooter">';
               html += '<span class="nodeAttrLabel replyTo">' + me._msg("post.replies") + ': </span>';
               html += '<span class="nodeAttrValue">(' + data.commentCount + ')</span>';
               html += '<span class="separator">&nbsp;</span>';
               html += '<span class="nodeAttrValue"><a href="' + postViewUrl + '">' + me._msg("post.read") + '</a></span>';
               html += '<span class="separator">&nbsp;</span>';

               html += '<span class="nodeAttrLabel tagLabel">' + me._msg("label.tags") +': </span>';
               if (data.tags.length > 0)
               {
                  for (var x=0; x < data.tags.length; x++)
                  {
                     if (x > 0)
                     {
                         html += ', ';
                     }
                     html += Alfresco.util.tags.generateTagLink(me, data.tags[x]);
                  }
               }
               else
               {
                  html += '<span class="nodeAttrValue">' + me._msg("post.noTags") + '</span>';
               }
               html += '</div></div>';
               // end
            }

            // simple view
            else
            {
               // add a class to the parent div so that we can add a separator line in the simple view
               Dom.addClass(elCell, 'row-separator');

               html += '<div class="node post simple">';

               // begin actions
               html += Alfresco.util.blog.generateBlogPostActions(me, data, 'span');

               // begin view
               html += '<div class="nodeContent">';
               html += '<span class="nodeTitle"><a href="' + postViewUrl + '">' + $html(data.title) + '</a> ';
               html += '<span class="theme-color-2 nodeStatus">' + statusLabel + '</span></span>';
               html += '<div class="published">';
               if (!data.isDraft)
               {
                  html += '<span class="nodeAttrLabel">' + me._msg("post.publishedOn") + ': </span>';
                  html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.releasedOn) + '</span>';
                  html += '<span class="separator">&nbsp;</span>';
               }
               html += '<span class="nodeAttrLabel">' + me._msg("post.author") + ': </span>';
               html += '<span class="nodeAttrValue">' + authorLink + '</span>';
               if (data.isPublished && data.postLink && data.postLink.length > 0)
               {
                  html += '<span class="separator">&nbsp;</span>';
                  html += '<span class="nodeAttrLabel">' + me._msg("post.externalLink") + ': </span>';
                  html += '<span class="nodeAttrValue"><a target="_blank" href="' + data.postLink + '">' + me._msg("post.clickHere") + '</a></span>';
               }
               html += '</div>';
               html += '</div>';
               html += '</div>';
            }

            // assign html
            elCell.innerHTML = html;

            // finally add the content. We do this here to avoid a broken page layout, as
            // data.content isn't valid xhtml.
            if (!me.options.simpleView)
            {
               var contentElem = Dom.getElementsByClassName("content", "div", elCell);
               if (contentElem.length == 1)
               {
                  contentElem[0].innerHTML = data.content;
               }
            }

            // now show the element
            Dom.removeClass(elCell, 'hidden');
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "blogposts", label: "BlogPosts", sortable: false, formatter: renderBlogPost
         }];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-postlist", columnDefinitions, this.widgets.dataSource,
         {
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this._msg("message.loading")
         });

         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function DL_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            // Save totalRecords for Paginator update later
            me.recordOffset = oResponse.meta.recordOffset;
            me.totalRecords = oResponse.meta.totalRecords;
            me.totalRecordsUpper = oResponse.meta.totalRecordsUpper;

            oPayload = oPayload || {};
            oPayload.recordOffset = oResponse.meta.recordOffset;
            oPayload.totalRecords = oResponse.meta.totalRecords;
            return oPayload;
         }

         // Prevent the DataTable from updating the Paginator widget
         this.widgets.dataTable.doBeforePaginatorChange = function DL_doBeforePaginatorChange(oPaginatorState)
         {
            return false;
         }

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            // Update the paginator if it's been created
            this.widgets.paginator.setState(
            {
               recordOffset: this.recordOffset,
               totalRecords: this.totalRecords
            });
            if (this.totalRecordsUpper && this.totalRecordsUpper == true)
            {
               this.widgets.paginator.set("pageReportTemplate", this._msg("pagination.template.page-report.more"));
            }
            else
            {
               this.widgets.paginator.set("pageReportTemplate", this._msg("pagination.template.page-report"));
            }
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
         this.widgets.dataTable.doBeforeLoadData = function BlogPostList_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.set("MSG_ERROR", response.message);
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
         };

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);

         // Load the new blog posts by default
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "new",
            filterOwner: "Alfresco.BlogPostListFilter",
            filterData: null
         }, this.options.initialFilter);
         YAHOO.Bubbling.fire("changeFilter", filterObj);
      },

      // Actions

      /**
       * Action handler for the simple view toggle button
       *
       * @method onSimpleView
       */
      onSimpleView: function BlogPostList_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("label", this._msg(this.options.simpleView ? "header.detailList" : "header.simpleList"));

         // refresh the list
         YAHOO.Bubbling.fire("blogpostlistRefresh");
         Event.preventDefault(e);
      },

      /**
       * Handler for the view blog post action links
       *
       * @method onViewBlogPost
       * @param row {object} DataTable row representing post to be actioned
       */
      onViewBlogPost: function BlogPostList_onViewNode(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         window.location = this._generatePostViewUrl(record.getData('name'));
      },

      /**
       * Handler for the edit blog post action links
       *
       * @method onEditBlogPost
       * @param row {object} DataTable row representing post to be actioned
       */
      onEditBlogPost: function BlogPostList_onEditBlogPost(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postedit?postId={postId}",
         {
            site: this.options.siteId,
            postId: record.getData('name')
         });
         window.location = url;
      },

      /**
       * Handler for the delete blog post action links
       *
       * @method onDeleteBlogPost
       * @param row {object} DataTable row representing post to be actioned
       */
      onDeleteBlogPost: function BlogPostList_onDeleteBlogPost(row)
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
               handler: function BlogPostList_onDeleteBlogPost_delete()
               {
                  this.destroy();
                  me._deleteBlogPostConfirm.call(me, record.getData('name'));
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function BlogPostList_onDeleteBlogPost_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Handler for the publish external action links
       *
       * @method onPublishExternal
       * @param row {object} DataTable row representing post to be actioned
       */
      onPublishExternal: function BlogPostList_onPublishExternal(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._publishExternal(record.getData('name'));
      },

      /**
       * Handler for the update external action links
       *
       * @method onUpdateExternal
       * @param row {object} DataTable row representing post to be actioned
       */
      onUpdateExternal: function BlogPostList_onUpdateExternal(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._updateExternal(record.getData('name'));
      },

      /**
       * Handler for the unpublish external action links
       *
       * @method onUnpublishExternal
       * @param row {object} DataTable row representing post to be actioned
       */
      onUnpublishExternal: function BlogPostList_onUnpublishExternal(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         this._unpublishExternal(record.getData('name'));
      },

      /**
       * Tag selected handler
       *
       * @method onTagSelected
       */
      onTagSelected: function BlogPostList_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var filterObj =
            {
               filterId: obj.tagName,
               filterOwner: "Alfresco.BlogPostListTags",
               filterData: null
            };
            YAHOO.Bubbling.fire("changeFilter", filterObj);
         }
      },

      /**
       * On blog config changed h handler
       *
       * @method onTagSelected
       */
      onBlogConfigChanged: function BlogPostList_onBlogConfigChanged(layer, args)
      {
         // refresh the list
         this._updateBlogPostList();
      },

      // Actions implementation

      /**
       * Blog post deletion implementation
       *
       * @method _deleteBlogPostConfirm
       * @param postId {string} the id of the blog post to delete
       */
      _deleteBlogPostConfirm: function BlogPostList__deleteBlogPostConfirm(postId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // ajax request success handler
         var onDeletedSuccess = function BlogPostList_deleteBlogPostConfirm_onDeletedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // reload the table data
            this._updateBlogPostList();
         };

         // get the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}?page=blog-postlist",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: encodeURIComponent(postId)
         });

         // execute ajax request
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "DELETE",
            responseContentType : "application/json",
            successMessage: this._msg("message.delete.success"),
            successCallback:
            {
               fn: onDeletedSuccess,
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
       * Publishing of a blog post implementation
       *
       * @method _publishExternal
       * @param postId {string} the id of the blog post to publish
       */
      _publishExternal: function BlogPostList__publishExternal(postId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // ajax call success handler
         var onPublishedSuccess = function BlogPostList_onPublishedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // reload the table data
            this._updateBlogPostList();
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
               action : "publish"
            },
            successMessage: this._msg("message.publishExternal.success"),
            successCallback:
            {
               fn: onPublishedSuccess,
               scope: this
            },
            failureMessage: this._msg("message.publishExternal.failure"),
            failureCallback:
            {
               fn: function(response) { this._releaseBusy(); },
               scope: this
            }
         });
      },


      /**
       * Updating of an external published blog post implementation
       *
       * @method _updateExternal
       * @param postId {string} the id of the blog post to update
       */
      _updateExternal: function BlogPostList__updateExternal(postId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // ajax request success handler
         var onUpdatedSuccess = function BlogPostList_onUpdatedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // reload the table data
            this._updateBlogPostList();
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
            successMessage: this._msg("message.updateExternal.success"),
            successCallback:
            {
               fn: onUpdatedSuccess,
               scope: this
            },
            failureMessage: this._msg("message.updateExternal.failure"),
            failureCallback:
            {
               fn: function(response) { this._releaseBusy(); },
               scope: this
            }
         });
      },


      /**
       * Unpublishing of an external published blog post implementation
       *
       * @method _unpublishExternal
       * @param postId {string} the id of the blog post to update
       */
      _unpublishExternal: function BlogPostList__onUnpublishExternal(postId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // ajax request success handler
         var onUnpublishedSuccess = function BlogPostList_onUnpublishedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // reload the table data
            this._updateBlogPostList();
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
               action : "unpublish"
            },
            successMessage: this._msg("message.unpublishExternal.success"),
            successCallback:
            {
               fn: onUnpublishedSuccess,
               scope: this
            },
            failureMessage: this._msg("message.unpublishExternal.failure"),
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


      // row highlighting

      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function BlogPostList_onEventHighlightRow(oArgs)
      {
         // only highlight if we got actions to show
         var record = this.widgets.dataTable.getRecord(oArgs.target.id);
         // This event triggers for empty lists too, so record may be null.
         if (record)
         {
            var permissions = record.getData('permissions');
            if (!(permissions.edit || permissions["delete"]))
            {
               return;
            }

            var elem = Dom.getElementsByClassName('post', null, oArgs.target, null);
            Dom.addClass(elem, 'over');
         }
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function BlogPostList_onEventUnhighlightRow(oArgs)
      {
         var elem = Dom.getElementsByClassName('post', null, oArgs.target, null);
         Dom.removeClass(elem, 'over');
      },


      /**
       * BlogPostList Change filter event handler
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function BlogPostList_onChangeFilter(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            this.currentFilter =
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            };
            this._updateBlogPostList(
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
      onDeactivateAllControls: function BlogPostList_onDeactivateAllControls(layer, args)
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
       * Updates the list title considering the current active filter.
       */
      updateListTitle: function BlogPostList_updateListTitle()
      {
         var elem = Dom.get(this.id + '-listtitle');
         var title = this._msg("title.postlist");

         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;
         if (filterOwner == "Alfresco.BlogPostListFilter")
         {
            if (filterId == "all")
            {
                title = this._msg("title.allposts");
            }
            if (filterId == "new")
            {
               title = this._msg("title.newposts");
            }
            else if (filterId == "mydrafts")
            {
               title = this._msg("title.mydrafts");
            }
            else if (filterId == "mypublished")
            {
               title = this._msg("title.mypublished");
            }
            else if (filterId == "publishedext")
            {
               title = this._msg("title.publishedext");
            }
         }
         else if (filterOwner == "Alfresco.BlogPostListTags")
         {
            title = this._msg("title.bytag", $html(filterData));
         }
         else if (filterOwner == "Alfresco.BlogPostListArchive" && filterId == "bymonth")
         {
            var date = new Date(filterData.year, filterData.month, 1);
            var formattedDate = Alfresco.util.formatDate(date, this._msg("date-format.monthYear"));
            title = this._msg("title.bymonth", formattedDate);
         }

         elem.innerHTML = title;
      },


      /**
       * BlogPostList Refresh Required event handler
       *
       * @method onBlogPostListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onBlogPostListRefresh: function BlogPostList_onBlogPostListRefresh(layer, args)
      {
         this._updateBlogPostList();
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
      _msg: function BlogPostList_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPostList", Array.prototype.slice.call(arguments).slice(1));
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function BlogPostList__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.BlogPostList"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.BlogPostList"));
      },

      /**
       * Updates blog post list by calling data webscript with current site and filter information
       *
       * @method _updateBlogPostList
       */
      _updateBlogPostList: function BlogPostList__updateBlogPostList(p_obj)
      {
         // show busy message
         /*if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }*/

         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

         // ajax request success handler
         var successHandler = function BlogPostList__updateBlogPostList_successHandler(sRequest, oResponse, oPayload)
         {
            // remove busy message
            //this._releaseBusy();

            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            this.updateListTitle();
         };

         // ajax request failure handler
         var failureHandler = function BlogPostList__updateBlogPostList_failureHandler(sRequest, oResponse)
         {
            // remove busy message
            //this._releaseBusy();

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
         };

         // get the url to call
         this.widgets.dataSource.sendRequest(this._buildBlogPostListParams(p_obj || {}),
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
      _buildBlogPostListParams: function BlogPostList__buildDocListParams(p_obj)
      {
         var params =
         {
            contentLength: this.options.maxContentLength,
            fromDate: null,
            toDate: null,
            tag: null,
            page: this.widgets.paginator.getCurrentPage() || "1",
            pageSize: this.widgets.paginator.getRowsPerPage()
         };

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            params = YAHOO.lang.merge(params, p_obj);
         }

         // calculate the startIndex param
         params.startIndex = (params.page-1) * params.pageSize;

         // check what url to call and with what parameters
         var filterOwner = this.currentFilter.filterOwner;
         var filterId = this.currentFilter.filterId;
         var filterData = this.currentFilter.filterData;

         // check whether we got a filter or not
         var url = "",
            urlExt = "",
            paramName;

         if (filterOwner == "Alfresco.BlogPostListFilter")
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
            else if (filterId == "mydrafts")
            {
                url = "/mydrafts";
            }
            else if (filterId == "mypublished")
            {
                url = "/mypublished";
            }
            else if (filterId == "publishedext")
            {
                url = "/publishedext";
            }
         }
         else if (filterOwner == "Alfresco.TagFilter")
         {
            params.tag = encodeURIComponent(filterData);
         }
         else if (filterOwner == "Alfresco.BlogPostListArchive" && filterId == "bymonth")
         {
            var fromDate = new Date(filterData.year, filterData.month, 1);
            var toDate = new Date(filterData.year, filterData.month + 1, 1);
            toDate = new Date(toDate.getTime() - 1);
            params.fromDate = fromDate.getTime();
            params.toDate = toDate.getTime();
         }
         else if (filterOwner == "Alfresco.BlogPostListTags" && filterId != null)
         {
            urlExt += "&" + filterId + "=" + encodeURIComponent(filterData);
         }

         // build the url extension
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
