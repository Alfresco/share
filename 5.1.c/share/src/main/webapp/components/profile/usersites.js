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
 * User Sites component.
 * 
 * @namespace Alfresco
 * @class Alfresco.UserSites
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
      
   /**
    * UserSites constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserSites} The new UserSites instance
    * @constructor
    */
   Alfresco.UserSites = function(htmlId)
   {
      Alfresco.UserSites.superclass.constructor.call(this, "Alfresco.UserSites", htmlId, ["button"]);
      return this;
   }
   
   YAHOO.extend(Alfresco.UserSites, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UserSites_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         Event.addListener(this.id+"-sites", "click", this.changeSiteNotification, this, true);
         
      },
      
      changeSiteNotification: function UserSites_changeSiteNotification(event)
      {
         var element = event.target || event.srcElement;
         var action = element.name;
         if(element.id.indexOf(this.id+"_notification_") === 0)
         {
            var siteName = element.id.substring(element.id.lastIndexOf("_notification_")+14);
            var siteTitle = YAHOO.util.Dom.get(this.id+"_title_"+siteName).value;
            
            var url = Alfresco.constants.PROXY_URI + "api/activities/feed/control";
            var method, successMessage;
            var failureMessage;
            var dataObj = null;
            if(action == "enable")
            {
               method = Alfresco.util.Ajax.DELETE;
               url += "?s=" + siteName;
               successMessage = "message.delete.success";
               failureMessage = "message.delete.failure";
            }
            else
            {
               method = Alfresco.util.Ajax.POST;
               dataObj = {siteId: siteName};
               successMessage = "message.post.success";
               failureMessage = "message.post.failure";
            }
            // execute the request
            Alfresco.util.Ajax.request(
            {
               url: url,
               method: method,
               requestContentType : Alfresco.util.Ajax.JSON,
               dataObj: dataObj,
               failureMessage: Alfresco.util.message(failureMessage, this.name, siteTitle),
               successCallback:
               {
                  fn: this.onNotificationChange_success,
                  scope: this,
                  obj:
                  {
                     siteName: siteName,
                     siteTitle: siteTitle
                  }
               }
            });
         }
      },
      
      /**
       * Event callback on notification status change success
       *
       * @method onNotificationChange_success
       * @param response {object} Server response from notification change request
       * @param obj {object} Comment scope (siteName, siteTitle)
       */
      onNotificationChange_success: function UserSites_onNotificationChange_success(response, obj)
      {
         // get the data and formId of the loaded form
         var siteName = obj.siteName,
         siteTitle = obj.siteTitle;
         
         var button = YAHOO.util.Dom.get(this.id+"_notification_"+siteName);
      
         if(button.name == "enable")
         {
            button.innerHTML = Alfresco.util.encodeHTML(Alfresco.util.message("button.disable", this.name));
            button.name = "disable";
            button.title = Alfresco.util.encodeHTML(Alfresco.util.message("button.disable.tooltip", this.name, siteTitle));
         }
         else
         {
            button.innerHTML = Alfresco.util.encodeHTML(Alfresco.util.message("button.enable", this.name));
            button.name = "enable";
            button.title = Alfresco.util.encodeHTML(Alfresco.util.message("button.enable.tooltip", this.name, siteTitle));
         }
      },
      
      /**
       * Event callback on notification status change failure
       *
       * @method onNotificationChange_failure
       * @param response {object} Server response from notification change request
       * @param obj {object} Comment scope (siteName, siteTitle, method)
       */
      onNotificationChange_failure: function UserSites_onNotificationChange_failure(response, obj)
      {
         // get the data and formId of the loaded form
         var siteName = obj.siteName,
         siteTitle = obj.siteTitle,
         method = obj.method;
         var element = YAHOO.util.Dom.get(this.id+"_notification_"+siteName);
      
         if(method == Alfresco.util.Ajax.DELETE)
         {
            element.checked = false;
         }
         else if(method == Alfresco.util.Ajax.POST)
         {
            element.checked = true;
         }
      }
   });
})();