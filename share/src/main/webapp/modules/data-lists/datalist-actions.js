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
 * Actions module for Data Lists
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DataListActions
 */
(function()
{
   Alfresco.module.DataListActions = function()
   {
      this.name = "Alfresco.module.DataListActions";
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["json"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.module.DataListActions.prototype =
   {
      /**
       * Flag indicating whether module is ready to be used.
       * Flag is set when all YUI component dependencies have loaded.
       * 
       * @property isReady
       * @type boolean
       */
      isReady: false,

      /**
       * Object literal for default AJAX request configuration
       *
       * @property defaultConfig
       * @type object
       */
      defaultConfig:
      {
         method: "POST",
         urlStem: Alfresco.constants.PROXY_URI + "slingshot/datalists/action/",
         dataObj: null,
         successCallback: null,
         successMessage: null,
         failureCallback: null,
         failureMessage: null,
         object: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLA_onComponentsLoaded()
      {
         this.isReady = true;
      },
      
      /**
       * Make AJAX request to data webscript
       *
       * @method _runAction
       * @private
       * @return {boolean} false: module not ready for use
       */
      _runAction: function DLA__runAction(config, obj)
      {
         // Check components loaded
         if (!this.isReady)
         {
            return false;
         }

         // Merge-in any supplied object
         if (typeof obj == "object")
         {
            config = YAHOO.lang.merge(config, obj);
         }
         
         if (config.method == Alfresco.util.Ajax.DELETE)
         {
            if (config.dataObj !== null)
            {
               // Change this request into a POST with the alf_method override
               config.method = Alfresco.util.Ajax.POST;
               if (config.url.indexOf("alf_method") < 1)
               {
                  config.url += (config.url.indexOf("?") < 0 ? "?" : "&") + "alf_method=delete";
               }
               Alfresco.util.Ajax.jsonRequest(config);
            }
            else
            {
               Alfresco.util.Ajax.request(config);
            }
         }
         else
         {
            Alfresco.util.Ajax.jsonRequest(config);
         }
      },
      
      
      /**
       * ACTION: Generic action.
       * Generic DataList action based on passed-in parameters
       *
       * @method genericAction
       * @param action.success.event.name {string} Bubbling event to fire on success
       * @param action.success.event.obj {object} Bubbling event success parameter object
       * @param action.success.message {string} Timed message to display on success
       * @param action.success.callback.fn {object} Callback function to call on success.
       * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
       * @param action.success.callback.scope {object} Success callback function scope
       * @param action.success.callback.obj {object} Success callback function object passed to callback
       * @param action.failure.event.name {string} Bubbling event to fire on failure
       * @param action.failure.event.obj {object} Bubbling event failure parameter object
       * @param action.failure.message {string} Timed message to display on failure
       * @param action.failure.callback.fn {object} Callback function to call on failure.
       * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
       * @param action.failure.callback.scope {object} Failure callback function scope
       * @param action.failure.callback.obj {object} Failure callback function object passed to callback
       * @param action.webscript.stem {string} optional webscript URL stem
       * <pre>default: Alfresco.constants.PROXY_URI + "slingshot/datalists/action/"</pre>
       * @param action.webscript.name {string} data webscript URL name
       * @param action.webscript.method {string} HTTP method to call the data webscript on
       * @param action.webscript.queryString {string} Optional queryString to append to the webscript URL
       * @param action.webscript.params.nodeRef {string} nodeRef of target item
       * @param action.wait.message {string} if set, show a Please wait-style message during the operation
       * @param action.config {object} optional additional request configuration overrides
       * @return {boolean} false: module not ready
       */
      genericAction: function DataListActions_genericAction(action)
      {
         var path = "",
            success = action.success,
            failure = action.failure,
            webscript = action.webscript,
            params = action.params ? action.params : action.webscript.params,
            overrideConfig = action.config,
            wait = action.wait,
            config = null;

         var fnCallback = function DataListActions_genericAction_callback(data, obj, innerCall)
         {
            // Did the operation succeed?
            if (!data.json.overallSuccess && !innerCall)
            {
               data.config.failureCallback.fn.call(undefined, data, data.config.failureCallback.obj, true);
               return;
            } 
            // Check for notification event
            if (obj)
            {
               // Event(s) specified?
               if (obj.event && obj.event.name)
               {
                  YAHOO.Bubbling.fire(obj.event.name, obj.event.obj);
               }
               if (YAHOO.lang.isArray(obj.events))
               {
                  for (var i = 0, ii = obj.events.length; i < ii; i++)
                  {
                     YAHOO.Bubbling.fire(obj.events[i].name, obj.events[i].obj);
                  }
               }
               
               // Please wait pop-up active?
               if (obj.popup)
               {
                  obj.popup.destroy();
               }
               // Message?
               if (obj.message)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: obj.message
                  });
               }
               // Callback function specified?
               if (obj.callback && obj.callback.fn)
               {
                  obj.callback.fn.call((typeof obj.callback.scope == "object" ? obj.callback.scope : this),
                  {
                     config: data.config,
                     json: data.json,
                     serverResponse: data.serverResponse
                  }, obj.callback.obj);
               }
            }
         }
         
         // Please Wait... message pop-up?
         if (wait && wait.message)
         {
            if (typeof success != "object")
            {
               success = {};
            }
            if (typeof failure != "object")
            {
               failure = {};
            }
            
            success.popup = Alfresco.util.PopupManager.displayMessage(
            {
               modal: true,
               displayTime: 0,
               text: wait.message,
               effect: null
            });
            failure.popup = success.popup;
         }

         var url;
         if (webscript.stem)
         {
            url = webscript.stem + webscript.name;
         }
         else
         {
            url = this.defaultConfig.urlStem + webscript.name;
         }
         
         if (params)
         {
            url = YAHOO.lang.substitute(url, params);
            config = params;
         }
         if (webscript.queryString)
         {
            url += "?" + webscript.queryString;
         }
                  
         var config = YAHOO.lang.merge(this.defaultConfig,
         {
            successCallback:
            {
               fn: fnCallback,
               scope: this,
               obj: success,
               objFailure: failure
            },
            successMessage: null,
            failureCallback:
            {
               fn: fnCallback,
               scope: this,
               obj: failure
            },
            failureMessage: null,
            url: url,
            method: webscript.method,
            responseContentType: Alfresco.util.Ajax.JSON,
            object: config
         });

         return this._runAction(config, overrideConfig);
      }
   };

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DataListActions();
})();
