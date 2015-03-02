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
 * DataLoader template.
 * 
 * @namespace Alfresco
 * @class Alfresco.DataLoader
 */
(function()
{
   /**
    * DataLoader constructor.
    * 
    * @return {Alfresco.DataLoader} The new DataLoader instance
    * @constructor
    */
   Alfresco.DataLoader = function DataLoader_constructor(htmlid)
   {
      return Alfresco.DataLoader.superclass.constructor.call(this, "Alfresco.DataLoader", htmlid, ["button"]);
   };
   
   YAHOO.extend(Alfresco.DataLoader, Alfresco.component.Base,
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
          * URL to resource to load data from
          * 
          * @property url
          * @type String
          * @mandatory
          */
         url: null,

         /**
          * Url to data to load
          *
          * @property url
          * @type String
          * @mandatory
          */
         eventName: null,

         /**
          * The object path in the response from where to get the vent value.
          * Example: "data" or "data.person.firstName"
          *
          * @property eventData
          * @type String
          * @default null (which implies that the whole response will be sent as the value)
          */
         eventData: null,

         /**
          * The failure message key that will be displayed if data wasn't able to be loaded
          *
          * @property failureMessageKey
          * @type String
          * @default
          */
         failureMessageKey: "message.failure.workflow",

         /**
		 
	 * The failure title key that will be displayed if data wasn't able to be loaded
          *
          * @property failureTitleKey
          * @type String
          * @default
          */
         failureTitleKey: "message.failure",

         /**
          * When set to true the proxy uri will prefix:ed to the value of the "url" option
          *
          * @property url
          * @type boolean
          * @default true
          */
         useProxy: true
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DataLoader_onReady()
      {
         var url = encodeURI(Alfresco.util.combinePaths(this.options.useProxy ? Alfresco.constants.PROXY_URI : "", this.options.url));

         Alfresco.util.Ajax.jsonGet(
         {
            method: "GET",
            url: url,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureCallback: 
            { 
               fn: this._getDataFailure, 
               scope: this 
            }
         });
      },
      
      /**
       * Success handler called when the web script returns successfully.
       * Will fire an event with data used by the components on the page.
       *
       * @method _getDataSuccess
       * @param response {object} The response object
       * @private
       */
      _getDataSuccess: function DataLoader__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            // Fire event with parent metadata
            var eventValue = response.json;
            if (this.options.eventData)
            {
               eventValue = Alfresco.util.findValueByDotNotation(eventValue, this.options.eventData, null);
            }
            if (eventValue)
            {
               YAHOO.Bubbling.fire(this.options.eventName, eventValue);
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: this.msg(this.options.failureTitleKey),
                  text: this.msg(this.options.failureMessageKey, Alfresco.constants.USERNAME),
                  modal: true
               });
            }
         }
      },

      /**
       * Failure handler called when the web script fails.
       * Will display an error message in a prompt dialog.
       *
       * @method _getDataFailure
       * @param response {object} The response object
       * @private
       */
      _getDataFailure: function DataLoader__getDataFailure(response)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg(this.options.failureTitleKey),
            text: this.msg(this.options.failureMessageKey, Alfresco.constants.USERNAME),
            modal: true
         });
      }
   });
})();