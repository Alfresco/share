/**
 * BlogPostView component.
 *
 * Component to view a blog post
 *
 * @namespace Alfresco
 * @class Alfresco.BlogPostView
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
    * BlogPostView constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostView} The new Post instance
    * @constructor
    */
   Alfresco.BlogPostView = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.BlogPostView";
      this.id = htmlId;

      /* Initialise prototype properties */
      this.widgets = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["json", "connection", "event", "button", "menu"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);

      return this;
   };

   Alfresco.BlogPostView.prototype =
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
          * Id of the displayed blog post.
          */
         postId: ""
      },

      /**
       * Stores the data displayed by this component
       */
      blogPostData: null,

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
       * @see setBusy/releaseBusy
       */
      busy: false,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function BlogPostView_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.BlogPostView} returns 'this' for method chaining
       */
      setMessages: function BlogPostView_setMessages(obj)
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
      onComponentsLoaded: function BlogPostView_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function BlogPostView_onReady()
      {
         var me = this;

         // Hook action events.
         var fnActionHandlerDiv = function BlogPostView_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  me[action].call(me, me.blogPostData.name);
                  args[1].stop = true;
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("blogpost-action-link-div", fnActionHandlerDiv);

         // Hook tag clicks
         Alfresco.util.tags.registerTagActionHandler(this);

         // initialize the mouse over listener
         Alfresco.util.rollover.registerHandlerFunctions(this.id, this.onPostElementMouseEntered, this.onPostElementMouseExited, this);

         // load the post data
         this._loadBlogPostData();
      },

      /**
       * Loads the comments for the provided nodeRef and refreshes the ui
       */
      _loadBlogPostData: function BlogPostView__loadBlogPostData()
      {
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
            successCallback:
            {
               fn: this.loadBlogPostDataSuccess,
               scope: this
            },
            failureMessage: this._msg("message.loadpostdata.failure")
         });
      },

      /**
       * Success handler for a blog post request. Updates the UI using the blog post data
       * provided in the response object.
       *
       * @param response {object} the ajax request response
       */
      loadBlogPostDataSuccess: function BlogPostView_loadCommentsSuccess(response)
      {
         // store the returned data locally
         var data = response.json.item;
         this.blogPostData = data;

         // get the container div to insert the the post into
         var viewDiv = Dom.get(this.id + '-post-view-div');

         // render the blog post and insert it into the div
         var html = this.renderBlogPost(data);
         viewDiv.innerHTML = html;

         // attach the rollover listeners
         Alfresco.util.rollover.registerListenersByClassName(this.id, 'post', 'div');

         // inform interested comment components about the loaded blog post
         this.sendCommentedNodeEvent();
      },

      /**
       * Sends out a setCommentedNode bubble event.
       */
      sendCommentedNodeEvent: function BlogPostView_sendCommentedNodeEvent()
      {
         var eventData =
         {
            nodeRef: this.blogPostData.nodeRef,
            title: this.blogPostData.title,
            page: "blog-postview",
            pageParams:
            {
               postId: this.blogPostData.name
            }
         };
         YAHOO.Bubbling.fire("setCommentedNode", eventData);
      },

      /**
       * Renders the blog post given a blog post data object returned by the server.
       */
      renderBlogPost: function BlogPostView_renderBlogPost(data)
      {
         // preformat some values
         var postViewUrl = Alfresco.util.blog.generateBlogPostViewUrl(this.options.siteId, this.options.containerId, data.name);
         var statusLabel = Alfresco.util.blog.generatePostStatusLabel(this, data);
         //ALF-18527
         var authorLink = data.author.username;
         // firstName is a mandatory field for user
         if (typeof data.author.firstName != "undefined")
         {
            authorLink = Alfresco.util.people.generateUserLink(data.author);
         }

         var html = '<div id="' + this.id + '-postview" class="node post postview theme-bg-color-6 theme-border-3">';
         html += Alfresco.util.blog.generateBlogPostActions(this, data, 'div');

         // content
         html += '<div class="nodeContent">';
         html += '<div class="nodeTitle"><a href="' + postViewUrl + '">' + $html(data.title) + '</a> ';
         html += '<span class="theme-color-2 nodeStatus">' + statusLabel + '</span>';
         html += '</div>';

         html += '<div class="published">';
         if (!data.isDraft)
         {
            html += '<span class="nodeAttrLabel">' + this._msg("post.publishedOn") + ': </span>';
            html += '<span class="nodeAttrValue">' + Alfresco.util.formatDate(data.releasedOn) + '</span>';
            html += '<span class="separator">&nbsp;</span>';
         }

         html += '<span class="nodeAttrLabel">' + this._msg("post.author") + ': </span>';
         html += '<span class="nodeAttrValue">' + authorLink + '</span>';

         if (data.isPublished && data.postLink !== undefined && data.postLink.length > 0)
         {
            html += '<span class="separator">&nbsp;</span>';
            html += '<span class="nodeAttrLabel">' + this._msg("post.externalLink") + ': </span>';
            html += '<span class="nodeAttrValue"><a target="_blank" href="' + data.postLink + '">' + this._msg("post.clickHere") + '</a></span>';
         }

         html += '<span class="separator">&nbsp;</span>';
         html += '<span class="nodeAttrLabel tagLabel">' + this._msg("label.tags") + ': </span>';
         if (data.tags.length > 0)
         {
            for (var x=0; x < data.tags.length; x++)
            {
               if (x > 0)
               {
                  html += ', ';
               }
               html += Alfresco.util.tags.generateTagLink(this, data.tags[x]);
            }
         }
         else
         {
            html += '<span class="nodeAttrValue">' + this._msg("post.noTags") + '</span>';
         }
         html += '</div>';

         html += '<div class="content yuieditor">' + data.content + '</div>';
         html += '</div></div>';
         return html;
      },


      // Actions

      /**
       * Tag selected handler.
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function BlogPostView_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postlist?filterId={filterId}&filterOwner={filterOwner}&filterData={filterData}",
            {
               site: this.options.siteId,
               filterId: "tag",
               filterOwner: "Alfresco.BlogPostListTags",
               filterData: encodeURIComponent(obj.tagName)
            });
            window.location = url;
         }
      },

      /**
       * Loads the edit post form and displays it instead of the content
       * The div class should have the same name as the above function (onEditNode)
       */
      onEditBlogPost: function BlogPostView_onEditNode(postId)
      {
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postedit?postId={postId}",
         {
            site: this.options.siteId,
            postId: postId
         });
         window.location = url;
      },

      /**
       * Blog post deletion implementation
       *
       * @method onDeleteBlogPost
       * @param postId {string} the id of the blog post to delete
       */
      onDeleteBlogPost: function BlogPostView_onDeleteBlogPost(postId)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this._msg("message.confirm.delete.title"),
            text: this._msg("message.confirm.delete", $html(this.blogPostData.title)),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function BlogPostList_onDeleteBlogPost_delete()
               {
                  this.destroy();
                  me._deleteBlogPostConfirm.call(me, me.blogPostData.name);
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
       * Blog post deletion implementation
       *
       * @method _deleteBlogPostConfirm
       * @param postId {string} the id of the blog post to delete
       */
      _deleteBlogPostConfirm: function BlogPostView__deleteBlogPostConfirm(postId)
      {
         // show busy message
         if (! this._setBusy(this._msg('message.wait')))
         {
            return;
         }

         // ajax request success handler
         var onDeletedSuccess = function BlogPostList_onDeletedSuccess(response)
         {
            // remove busy message
            this._releaseBusy();

            // load the blog post list page
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postlist",
            {
               site: this.options.siteId
            });
            window.location = url;
         };

         // get the url to call
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}?page={page}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            postId: encodeURIComponent(postId),
            page: "blog-postlist"
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
       * Publishing of a blog post
       *
       * @method onPublishExternal
       * @param postId {string} the id of the blog post to publish
       */
      onPublishExternal: function BlogPostView_onPublishExternal(postId)
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

            // re-render the post
            this.loadBlogPostDataSuccess(response);
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
       * @method onUpdateExternal
       * @param postId {string} the id of the blog post to update
       */
      onUpdateExternal: function BlogPostView_onUpdateExternal(postId)
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

            // re-render the post
            this.loadBlogPostDataSuccess(response);
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
       * @method onUnpublishExternal
       * @param postId {string} the id of the blog post to update
       */
      onUnpublishExternal: function BlogPostView_onUnpublishExternal(postId)
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

            // re-render the post
            this.loadBlogPostDataSuccess(response);
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
               fn: function(response) { this._releaseBusy(); },
               scope: this
            }
         });
      },


      // mouse hover functionality

      /** Called when the mouse enters into a list item. */
      onPostElementMouseEntered: function BlogPostView_onListElementMouseEntered(layer, args)
      {
         // make sure the user sees at least one action, otherwise we won't highlight
         var permissions = this.blogPostData.permissions;
         if (!(permissions.edit || permissions["delete"]))
         {
            return;
         }

         Dom.addClass(args[1].target, 'over');
      },

      /** Called whenever the mouse exits a list item. */
      onPostElementMouseExited: function BlogPostView_onListElementMouseExited(layer, args)
      {
         Dom.removeClass(args[1].target, 'over');
      },


      /**
       * PRIVATE FUNCTIONS
       */

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
      _msg: function BlogPostView_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPostView", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
