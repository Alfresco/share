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
 * AboutShare module
 * 
 * A dialog for displaying information about Alfresco Share
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.AboutShare
 */
(function()
{
   var Dom = YAHOO.util.Dom;
   
   /**
    * AboutShare constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.AboutShare} The new AboutShare instance
    * @constructor
    */
   Alfresco.module.AboutShare = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.AboutShare already exists.");
      }

      Alfresco.module.AboutShare.superclass.constructor.call(this, "Alfresco.module.AboutShare", containerId, ["container", "connection", "json"]);

      return this;
   };

   YAHOO.extend(Alfresco.module.AboutShare, Alfresco.component.Base,
   {
      scrollpos: 0,
      
      /**
       * Shows the About Share dialog to the user.
       *
       * @method show
       */
      show: function AS_show()
      {
         if (this.widgets.panel)
         {
            this.widgets.panel.show();
         }
         else
         {
            // Load the gui from the server and let the templateLoaded() method handle the rest.
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/about-share",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load About Share template"
            });
         }
      },

      /**
       * Called when the AboutShare html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       * 
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function AS_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;
         
         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChild(containerDiv);
         this.widgets.panel = Alfresco.util.createYUIPanel(panelDiv, { draggable: false });

         if (YAHOO.env.ua.ie === 0 || YAHOO.env.ua.ie > 7)
         {
            Dom.setStyle(this.id + "-contributions", "display", "block");
            
            // begin the contributions scroller
            var me = this;
            setInterval(function AS_scroller()
            {
               var div = Dom.get(me.id + "-contributions");
               var pos = me.scrollpos++;
               if (pos > div.clientHeight)
               {
                  pos = me.scrollpos = 0;
               }
               div.style.top = "-" + pos + "px";
            },
            80);
         }
         
         this.widgets.panel.show();
      }
   });
})();

Alfresco.module.getAboutShareInstance = function()
{
   var instanceId = "alfresco-AboutShare-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.AboutShare(instanceId);
};