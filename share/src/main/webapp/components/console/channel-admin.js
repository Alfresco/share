/**
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * ConsoleChannels tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleChannels
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      parseURL = Alfresco.util.parseURL
   
   /**
    * ConsoleChannels constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleChannels} The new ConsoleChannels instance
    * @constructor
    */
   Alfresco.ConsoleChannels = function(htmlId)
   {
      this.name = "Alfresco.ConsoleChannels";
      Alfresco.ConsoleChannels.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "paginator", "json", "history"], this.onComponentsLoaded, this);
      
      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* File List Panel Handler */
      ListPanelHandler = function ListPanelHandler_constructor()
      {
         ListPanelHandler.superclass.constructor.call(this, "list");
      };
      
      YAHOO.extend(ListPanelHandler, Alfresco.ConsolePanelHandler, 
      {
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function onLoad()
         {
            
         }
      });
      new ListPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ConsoleChannels, Alfresco.ConsoleTool, 
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function ConsoleChannels_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Is the UI waiting for a callback from the Auth Scripts?
       *
       * @property isWaiting
       * @value {Object}
       *    state: {boolean} - are we waiting?
       *    callback: {string} - the callback URL.
       *    reauth:  {boolean} - is this a reauthentication request?
       * @method set - sets the state & callback
       * @method reset - resets the state.
       */
      isWaiting: 
      {
         state: false,
         callback: "",
         reauth: false,
         channelId: "",
         set: function consoleChannels_isWaiting_set(callback, channelId, reauth) 
         {
            this.state = true;
            this.callback = callback;
            this.channelId = channelId;
            if (reauth) 
            {
               this.reauth = reauth;
            }
         },
         reset: function consoleChannels_isWaiting_reset()
         {
            this.state = false;
            this.callback = this.channelId = "";
            this.reauth = false;
         }
      },
      
      /**
       * Handle to the window created during channel auth.
       *
       * @property authWindow
       */
      authWindow: null,

      options:
      {
         /**
          * Should we use the iframe for communicating with the popup Auth window?
          *
          * @property iframeComms
          */
         iframeComms: false,

         /**
          * Which remote site are we using for authentication callbacks?
          *
          * @property acceptMessagesFrom
          */
         acceptMessagesFrom: ""
      },

      /**
       * Contains details for instantiating Insitu editors
       * (array generated during cell rendering, editors instantiated after DOM written.)
       *
       * @property insituEditors
       * @type array
       * 
       */
      insituEditors: [],
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleChannels_onReady()
      {
         // Call super-class onReady() method
         Alfresco.ConsoleChannels.superclass.onReady.call(this);
         

         this.widgets.newChannelButton = new YAHOO.widget.Button(this.id + "-new-button",
         {
            type: "menu",
            menu: this.id + "-newChannel-menu",
            lazyloadmenu: false
         }); 

         // add event listeners
         Event.on(this.id + "-channelTypes", "click", this.onCreateChannel, this, true)
         Event.on(this.id + "-datatable", 'click', this.onChannelInteraction, this, true);
         // Set up list of channels as data table for ease of updating.
         this.widgets.channelDataTable = new Alfresco.util.DataTable(
         {
            dataSource: 
            {
               url: Alfresco.constants.PROXY_URI + "api/publishing/channels",
               doBeforeParseData: this.bind(function(oRequest, oFullResponse)
               {
                  // Channels List needs combining from list of publish and status update channels.
                  var channelList = [], publishChannels = oFullResponse.data.publishChannels, statusUpdateChannels = oFullResponse.data.statusUpdateChannels;
                  
                  channelList = publishChannels.concat(statusUpdateChannels)
                  return ({
                     "data": channelList
                  });
               })
            },
            dataTable: 
            {
               container: this.id + "-datatable",
               columnDefinitions: [
               {
                  key: "channel",
                  sortable: false,
                  formatter: this.bind(this.renderCellChannel)
               }],
               config: 
               {
                  MSG_EMPTY: this.msg("channelAdmin.noChannels")
               }
            }
         });
         
         // Set up Data Table event listeners
         var dt = this.widgets.channelDataTable.widgets.dataTable;
         
         dt.subscribe("cellClickEvent", this.onChannelInteraction, this, true); 
         dt.subscribe("renderEvent", this.onRenderEvent, this, true);

         // grab handle to iframe used for communicating with popups.
         this.widgets.iframe = Dom.get(this.id + "-iframe");

         // If we've got postMessage support, use it, through an iframe to facilitate communication with popups.
         // This will be false in IE<9
         if (typeof(window.postMessage) === "function")
         {
            this.options.iframeComms = true;
         }

      },
      
      /**
       *
       * Called by the Alfresco.util.dataTable method to render each channel into the dom.
       *
       * @method renderCellChannel
       */
      renderCellChannel: function consoleChannels_renderCellChannel(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = this.renderChannel(oRecord);
      },
      
      /**
       * Builds the HTML for existing channels
       *
       * @method renderChannel
       * @param {Object} channel
       */
      renderChannel: function consoleChannels_renderChannel(oRecord)
      {
         var channel = oRecord.getData(), 
            rel = ' rel="' + channel.id + '"',
            editNodeTooltip = $html(this.msg("channelAdmin.editNode.tooltip", channel.title)),
            deleteLink = '<a href="#" class="channelAction delete" ' + rel + ' title="' + this.msg("channelAdmin.delete.tooltip") + '">' + this.msg("channelAdmin.delete") + '</a>',
            permissionsLink= '<a href="#" class="channelAction permissions" ' + rel + ' title="' + this.msg("channelAdmin.permissions.tooltip") + '">' + this.msg("channelAdmin.permissions") + '</a>',
            reauth = '<a href="#" class="channelAction reauth" ' + rel + ' title="' + this.msg("channelAdmin.reauth.tooltip") + '">' + this.msg("channelAdmin.reauth") + '</a>',
            status = "authorised",
            title = this.msg("channelAdmin.authorised.tooltip"),
            image = '<a href="#" class="channelAction editNode"' + rel + ' title="' + editNodeTooltip + '"><img class="editNode" src="' + Alfresco.constants.PROXY_URI + channel.channelType.icon +  "/64" + '" title="' + editNodeTooltip + '"/></a> ',
            channelNameId = this.id + "-channelName-" + oRecord.getId(),
            html = "";
         
         // Has the channel authorisation failed?
         if (channel.authorised !== true ) 
         {
            status = "notAuthorised";
            title = this.msg("channelAdmin.notAuthorised.tooltip")
         }
         
         html += '<div class="channel ' + status + '" title="' + title + '" rel="' + channel.id + '">' + image + '<div class="channelName" id="' + channelNameId + '">' + $html(channel.name) + '</div><span class="channelActions">' + permissionsLink + reauth + deleteLink + '</span></div>'
         
         // Insitu editors
         this.insituEditors.push(
         {
            context: channelNameId,
            params:
            {
               type: "textBox",
               nodeRef: channel.id,
               name: "prop_cm_name",
               value: channel.name,
               validations: [
               {
                  type: Alfresco.forms.validation.nodeName,
                  when: "keyup",
                  message: this.msg("validation-hint.nodeName")
               },
               {
                  type: Alfresco.forms.validation.length,
                  args: { min: 1, max: 255, crop: true },
                  when: "keyup",
                  message: this.msg("validation-hint.length.min.max", 1, 255)
               }],
               title: this.msg("channelAdmin.insitu-edit.tooltip"),
               errorMessage: this.msg("channelAdmin.insitu-edit.failure")
            },
            callback:
            {
               fn: this.onChannelRename,
               scope: this,
               obj: channel
            }
         });
         
         return html;
      },
      
      /**
       *
       * Called when the new channel button is clicked
       *
       * @method onCreateChannel
       */
      onCreateChannel: function consoleChannels_onCreateChannel(event, args)
      {
         var channelEl = Event.getTarget(event),
            channelType = Dom.getAttribute(channelEl, "rel"),
            newChannelURL = Alfresco.constants.PROXY_URI + "api/publishing/channels", 
            channelName = this.newChannelName(YAHOO.lang.trim(channelEl.innerHTML)),
            params = "?channelType=" + channelType + "&channelName=" + channelName;

         Alfresco.util.Ajax.request(
         {
            url: newChannelURL + params,
            method: Alfresco.util.Ajax.POST,
            successCallback: 
            {
               fn: this.onCreateChannelSuccess,
               scope: this
            },
            failureCallback: 
            {
               fn: function consoleChannels_onCreateChannel_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("channelAdmin.createChannel.failure")
                  });
               },
               scope: this
            }
         });
      },
      
      /**
       * Called when the channel create API call returns successfully, this triggers the start of the authentication process.
       * 
       * @method onCreateChannelSuccess
       * @param {Object} response
       */
      onCreateChannelSuccess: function consoleChannels_onCreateChannelSuccess(response)
      {
         // Begin the authorisation process.
         this.authoriseChannel(response, false);
      },
      
      /**
       * 
       * Kicks off the authentication process and enables the listener.
       * 
       * @method authoriseChannel
       * @param {Object} authUrl
       */
      authoriseChannel: function consoleChannels_authoriseChannel(response, reauth)
      {
         // Parse the response and retrieve the URL
         var authUrl = response.json.data.authoriseUrl,
            callbackUrl = response.json.data.authCallbackUrl,
            channelId = response.json.data.channelId,
            redirectUrl = response.json.data.authRedirectUrl,
            isSameDomain = function consoleChannels_isSameDomain(url1, url2)
            {
               var url1Obj = parseURL(url1),
                  url2Obj = parseURL(url2);
               // protocol, hostname and port need to match (host contains port and hostname)
               return (url1Obj.protocol === url2Obj.protocol && url1Obj.host === url2Obj.host);
            };

         reauth = reauth || false;

         // If redirect domain is same as current domain open URL directly.
         if (isSameDomain(redirectUrl, window.location.href))
         {
            this.authWindow = window.open(authUrl);
         }
         // If we're not able to use iframe communication, we'll need to open the page directly.
         // It needs the callback URL because it'll need to manually redirect back there.
         else if (!this.options.iframeComms)
         {
            var authUrlObj = parseURL(authUrl);
               authUrlObj.queryParams.state = callbackUrl;
               authUrl = authUrlObj.getUrl();
            this.authWindow = window.open(authUrl);
         }
         // if the iframe and redirect domains match, use the iframe
         else if (isSameDomain(redirectUrl, this.options.acceptMessagesFrom))
         {

            this.widgets.iframe.contentWindow.postMessage(authUrl, "*");

            var me = this,
               receiveMessage = function consoleChannels_receiveMessage(event)
               {
                  // Check the message comes from the correct place
                  if (event.origin === me.options.acceptMessagesFrom)
                  {
                     // we're accepting the message, so set the location hash, which triggers the rest of the auth process.
                     window.location.hash = event.data;
                  }
               }

            // Listen for the message back from the iframe:
            Event.addListener(window, "message", receiveMessage, false);
         }
         // We'll hit here if the domain the user accessed Share on is wrong AND if they're using a modern browser.
         else
         {
            var error;

            // Clarify the exact error:
            if (!isSameDomain (redirectUrl, window.location.href))
            {
               var currentUrlObj = parseURL(window.location.href),
                  newUrlObj = parseURL(window.location.href),
                  redirectUrlObj = parseURL(redirectUrl);

               // Update new URL to point to expected domain
               newUrlObj.protocol = redirectUrlObj.protocol;
               newUrlObj.host = redirectUrlObj.host;

               // URL error: The server thinks it should be accessed via a different URL
               error = this.msg("channelAdmin.auth.failure.url", currentUrlObj.getDomain(), newUrlObj.getDomain(), newUrlObj.getUrl());
            }
            else
            {
               // Unsupported Browser
               error = this.msg("channelAdmin.auth.failure.browser");
            }

            // Display Error Message
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: error,
               noEscape: true
            });

            // reload channels
            this.refresh();
         }
         // Let the module know it's waiting for a callback.
         this.isWaiting.set(callbackUrl, channelId, reauth);
      },
      
      /**
       * 
       * Generates a new, unique, name for the channel
       * 
       * @method newChannelName
       * @property {string} channelType
       */
      newChannelName: function consoleChannels_newChannelName(channelType)
      {
         var elements = Dom.getElementsByClassName("channelName", "div", this.id),
            name = this.msg("channelAdmin.new-channel", channelType),
            unique = name,
            channelNames = [],
            increment = 0;
         
         // Build array of channel names.
         for (var i = 0; i < elements.length; i++) 
         {
            channelNames.push(elements[i].innerHTML.toLowerCase());
         }
         
         // Check the array against the current name, and increment until it doesn't exist.
         while (Alfresco.util.arrayContains(channelNames, unique.toLowerCase())) 
         {
            ++increment;
            unique = name + " " + increment; 
         }
         
         return unique;
      },
      
      /**
       * 
       * Called when one of the channel actions is triggered.
       * 
       * @method onChannelInteraction
       * @param {Object} o
       * @param {Object} args
       */
      onChannelInteraction: function consoleChannels_onChannelInteraction(o, args)
      {
         if (YAHOO.util.Selector.test(Event.getTarget(o.event), 'a.delete'))
         {
            this.onDeleteChannel(o.event, args);
         }
         else if (YAHOO.util.Selector.test(Event.getTarget(o.event), 'a.reauth'))
         {
            this.onReauthChannel(o.event, args);
         }
         else if (YAHOO.util.Selector.test(Event.getTarget(o.event), 'a.permissions'))
         {
            this.onPermissionsClick(o.event, args);
         }
         else if (YAHOO.util.Selector.test(Event.getTarget(o.event), '.editNode'))
         {
            this.onEditNode(o.event, args);
         }
      },

      /**
       * Triggered after the data table has been rendered
       * 
       * @method onRenderEvent
       * @param {Object} o
       * @param {Object} args
       */
      onRenderEvent: function consoleChannels_onRenderEvent(o, args)
      {
         // Register insitu editors
         var iEd;
         for (var i = 0, j = this.insituEditors.length; i < j; i++)
         {
            iEd = this.insituEditors[i];
            Alfresco.util.createInsituEditor(iEd.context, iEd.params, iEd.callback);
         }

      },
      
      /**
       * 
       * Triggered after the channel has been renamed
       * 
       * @method onChannelRename
       * @param {Object} o
       * @param {Object} args
       */
      onChannelRename: function consoleChannels_onChannelRename(o, args)
      {
         // insitu editor handles the form submission, so we just need to refresh the data.
         this.refresh();
      },
      
      /**
       * Triggers an API call to delete the channel.
       * 
       *  @method onDeleteChannel
       */
      onDeleteChannel: function consoleChannels_onDeleteChannel(event, args)
      {
         var me = this,
            nodeRef = Dom.getAttribute(Event.getTarget(event), "rel");
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: me.msg("channelAdmin.delete.title"),
            text: me.msg("channelAdmin.delete.confirm"),
            buttons: [
            {
               text: me.msg("button.ok"),
               handler: function()
               {
                  this.destroy();
                  
                  var deleteURL = Alfresco.constants.PROXY_URI + "api/publishing/channels/" + nodeRef.replace("://", "/")
         
                  Alfresco.util.Ajax.request(
                  {
                     url: deleteURL,
                     method: Alfresco.util.Ajax.DELETE,
                     successCallback: 
                     {
                        fn: me.onDeleteChannelSuccess,
                        scope: me
                     },
                     failureCallback: 
                     {
                        fn: function consoleChannels_onDeleteChannel_failure(response)
                        {
                           Alfresco.util.PopupManager.displayPrompt(
                           {
                              text: me.msg("channelAdmin.delete.failure")
                           });
                        },
                        scope: me
                     }
                  });
                  
               }
            },
            {
               text: me.msg("button.cancel"),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
         
      },
      
      /**
       * 
       * Called when the Ajax call to the Delete API succeeds. 
       * 
       * @method onDeleteChannelSuccess
       * @param {Object} response
       */
      onDeleteChannelSuccess: function consoleChannels_onDeleteChannelSuccess(response)
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("channelAdmin.delete.success")
         });
         
         // Reload Channels
         this.refresh();
      },
      
      /**
       * 
       * Called by the reauth channelAction
       * 
       * @method onReauthChannel
       * @param {Object} event
       * @param {Object} args
       */
      onReauthChannel: function consoleChannels_onReauthChannel(event, args)
      {
         var nodeRef = Dom.getAttribute(Event.getTarget(event), "rel"),
            url = Alfresco.constants.PROXY_URI + "api/publishing/channels/" + nodeRef.replace("://", "/") + "/reauthorise";
         
         // Call the Reauth API.
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: Alfresco.util.Ajax.POST,
            successCallback: 
            {
               fn: this.onReauthSuccess,
               scope: this
            },
            failureCallback: 
            {
               fn: function consoleChannels_onReauth_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("channelAdmin.auth.failure")
                  });
               },
               scope: this
            }
         });
      },
      
      /**
       * Called when the Reauthorise API call succeeds
       * 
       * Triggers the restart of the Authorisation process
       * 
       */
      onReauthSuccess: function consoleChannels_onReauthSuccess(response)
      {
         // Begin the authorisation process using that URL.
         this.authoriseChannel(response, true);
      },
      
      /**
       * 
       * Triggered when someone clicks on the permissions link
       * The fires up the manage permissions webscript and supplies the callback that initialises it.
       * 
       * @method onPermissionsClick
       * @param {Object} event
       * @param {Object} args
       */
      onPermissionsClick: function consoleChannels_onPermissionClick(event, args)
      {
         var nodeRef = Dom.getAttribute(Event.getTarget(event), "rel");
         
         // Load the Permissions GUI
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/manage-permissions/manage-permissions?nodeRef=" + nodeRef + "&htmlid=" + this.id,
            successCallback:
            {
               fn: this.onPermissionsTemplateLoaded,
               scope: this
            },
            failureMessage: Alfresco.util.message("channelAdmin.template.error", this.name),
            execScripts: true
         });
      },
      
      /**
       * 
       * Called on successful load of the permissions management HTML.
       * 
       * @method onPermissionsTemplateLoaded
       * @param {Object} response
       */
      onPermissionsTemplateLoaded: function consoleChannels_onPermissionsTemplateLoaded(response)
      {
         
         var permissionsEl = Dom.get(this.id + "-managepermissions"),
            permissionsContainerEl = Dom.get(this.id + "-body"),
            nodeRef = Alfresco.util.ComponentManager.findFirst("Alfresco.component.ManagePermissions").options.nodeRef;
         
         var url;
         if (nodeRef.uri)
         {
            url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + nodeRef.uri;
         }
         else
         {
            var nodeRefObj = Alfresco.util.NodeRef(nodeRef);
            url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + nodeRefObj.storeType + "/" + nodeRefObj.storeId + "/" + nodeRefObj.id;
         }

         // Write response to DOM and switch to permissions mode (hiding channels UI):
         permissionsEl.innerHTML = response.serverResponse.responseText;
         Dom.addClass(permissionsContainerEl, "managePermissions");

         Alfresco.util.Ajax.jsonGet(
         {
            url: url,
            successCallback: 
            { 
               fn: function consoleAdmin_getPermissionsDataSuccess(response)
               {
                  if (response.json !== undefined)
                  {
                     var nodeDetails = response.json.item;
                     
                     // Fire event to inform any listening components that the data is ready
                     YAHOO.Bubbling.fire("nodeDetailsAvailable",
                     {
                        nodeDetails: nodeDetails,
                        metadata: response.json.metadata
                     });
                  }
               }, 
               scope: this 
            },
            failureMessage: "Failed to load data for permission details"
         });
         
         // Override the default behaviour, ready for when the permissions page is done.
         Alfresco.component.ManagePermissions.prototype._navigateForward = function()
         {
            // reverse the displays, so the permissions Div is hidden and channels div is shown
            Dom.removeClass(permissionsContainerEl, "managePermissions");
            
            // empty the permissions container
            permissionsEl.innerHTML = "";
         }
         
      },
      
      /**
       * Overrides the onStateChanged to listen for token response
       *
       * @method onStateChanged
       */
      onStateChanged: function ConsoleChannels_onStateChanged()
      {
         // Get the hash, but remove the actual hash sign.
         var hash = window.location.hash.substring(1),
            url = this.isWaiting.callback;
         if (hash !== "" && this.isWaiting.state) 
         {
            if (hash !== "complete" && url !== "") 
            {
               this.onAuthCallback(hash, url);
            } 
            else 
            {
               this.onAuthComplete();   
            }
         }
      },
      
      /**
       * Triggers a call to the authorise Callback URL, submitting the received token.
       * 
       * @param {string} hash
       * @param {string} url
       */
      onAuthCallback: function consoleChannels_onAuthCallback(token, url)
      {
         // Ensure URL Callback is has the same hostname that we've currently got:
         // (server config files might have a different hostname to the one used in the browser, which breaks the same domain AJAX rules)
         localUrl = Alfresco.constants.PROXY_URI + url.split(Alfresco.constants.PROXY_URI_RELATIVE)[1]
         
         // token needs encoding.
         var params=token.split("&");
         for (var i = 0; i < params.length; i++) 
         {
            param = params[i].split("=");
            if (param[1] !== "undefined") 
            {
               param[1] = encodeURIComponent(param[1]);
            }
            params[i] = param.join("=");
         }
         token = params.join("&");
         // Submit to authoriseCallback URL
         Alfresco.util.Ajax.request(
         {
            url: localUrl + "?" + token,
            successCallback: 
            {
               fn: this.onAuthComplete,
               scope: this
            },
            failureCallback: 
            {
               fn: function consoleChannels_onAuthCallback_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("channelAdmin.auth.failure")
                  });
               },
               scope: this
            }
         }); 
      },
      
      /**
       * Called when a channel has been created and authentication has finished (regardless to outcome)
       *
       * @method onAuthComplete
       */
      onAuthComplete: function consoleChannels_onAuthComplete()
      {
         // If this wasn't a reauth call, let the user know a new channel has been created:
         if (!this.isWaiting.reauth) 
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("channelAdmin.createChannel.success")
            });
         }
         
         // We're no longer waiting for authentication to complete:
         this.isWaiting.reset();
            
         // reload channels
         this.refresh();
      },

      /**
       *
       * Loads the form to edit a channel node
       *
       * @method onEditNode
       */
      onEditNode: function consoleChannels_onEditNode(event, o)
      {
         var nodeRef = Dom.getAttribute(Event.getTarget(event), "rel") || Dom.getAttribute(Dom.getAncestorByTagName(Event.getTarget(event), "a"), "rel")

         if (nodeRef)
         {
            var elementId = this.id + "-editNode",
               editNode = new Alfresco.module.SimpleDialog(elementId),
               headerNode = document.createElement("div");

            headerNode.innerHTML = '<h2>' + this.msg("channelAdmin.editNode.header") + '</h2>';
            Dom.addClass(headerNode, "yui-g");

            var doBeforeDialogShow = function consoleChannels_onEditNode_doBeforeDialogShow(p_form, p_dialog)
               {
                  Dom.get(elementId + "-form-container_h").innerHTML = this.msg("channelAdmin.editNode.title");
                  Dom.insertBefore(headerNode, Dom.get(elementId + "-form-fields"));
               };

            editNode.setOptions(
            {
               width: "33em",
               templateUrl: YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",
               {
                  itemKind: "node",
                  itemId: nodeRef,
                  mode: "edit",
                  submitType: "json"
               }),
               actionUrl: null,
               destroyOnHide: true,
               doBeforeDialogShow:
               {
                  fn: doBeforeDialogShow,
                  scope: this
               },
               onSuccess:
               {
                  fn: function consoleChannels_onEditNode_success(response)
                  {
                     this.onEditNodeFinished();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("channelAdmin.editNode.success")
                     });
                  },
                  scope: this
               },
               onFailure:
               {
                  fn: function consoleChannels_onEditNode_failure(response)
                  {
                     if (response)
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("channelAdmin.editNode.failure")
                        });
                     }
                     else
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.failure")
                        });
                     }
                  },
                  scope: this
               }
            }).show();
         }
         event.preventDefault();
      },

      /**
       *
       * Editing the node has finished, reload the channels.
       *
       * @method onEditNodeFinished
       */
      onEditNodeFinished: function consoleChannels_onEditNodeFinished()
      {
         this.refresh();
      },

      /**
       * Refreshes the channels
       * 
       */
      refresh: function consoleChannels_refresh()
      {
         var dataTable = this.widgets.channelDataTable.widgets.dataTable;
         
         // Reset the hash.
         window.location.hash = "";
         
         // Remove the insitu editor references (these will be recreated when the table is rerendered)
         this.insituEditors = [];
         
         // Reload the dataTable.
         dataTable.getDataSource().sendRequest('', { success: dataTable.onDataReturnInitializeTable, scope: dataTable });
      },
      
      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function consoleChannels__showPanel()
      {
         // Enable the Esc key listener
         this.widgets.escapeListener.enable();
         
         // Show the panel
         this.widgets.panel.show();
      }
      
   });
})();
