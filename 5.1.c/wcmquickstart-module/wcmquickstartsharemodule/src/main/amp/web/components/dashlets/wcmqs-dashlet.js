/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * Dashboard Records Management component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.RMA
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Dashboard WCMQS constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.WCMQS} The new component instance
    * @constructor
    */
   Alfresco.dashlet.WCMQS = function WCMQS_constructor(htmlId)
   {
      return Alfresco.dashlet.WCMQS.superclass.constructor.call(this, "Alfresco.dashlet.WCMQS", htmlId);
   };
   
   YAHOO.extend(Alfresco.dashlet.WCMQS, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function WCMQS_onReady()
      {
         var me = this;
         
         this.widgets.feedbackMessage = null;
         
         // setup link events
         Event.on(this.id + "-load-data-link", "click", this.onLoadTestData, null, this);
      },
      
      /**
       * Load Test Data link click event handler
       * 
       * @method onLoadTestData
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onLoadTestData: function WCMQS_onLoadTestData(e, args)
      {
         Event.stopEvent(e);
         
         if (this.widgets.feedbackMessage === null)
         {
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.importing"),
               spanClass: "wait",
               displayTime: 0
            });
            
            // Get the import id that's been selected
            var importid = Dom.get(this.id + "-load-data-options").value;
            
            // call repo-tier to perform test data import
            Alfresco.util.Ajax.request(
            {
               method: Alfresco.util.Ajax.GET,
               url: Alfresco.constants.PROXY_URI + "api/loadwebsitedata?site=" + this.options.siteId + "&importid=" + importid,
               successCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.import-ok")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function()
                  {
                     this.widgets.feedbackMessage.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.import-fail")
                     });
                     
                     // reset feedback message - to allow another action if required
                     this.widgets.feedbackMessage = null;
                  },
                  scope: this
               }
            });
         }
      }
   });
})();