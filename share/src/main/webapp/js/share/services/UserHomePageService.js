/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
  /

/*
 * @module share/services/UserHomePageService
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/services/_UserServiceTopicMixin
 * @author Dave Draper
 * @author Ray Gauss II
 */
define(["dojo/_base/declare",
  "dojo/_base/lang",
  "dojo/dom",
  "dojo/dom-attr",
  "alfresco/core/Core",
  "alfresco/services/_UserServiceTopicMixin"],
  function(declare, lang, dom, domAttr, AlfCore, _UserServiceTopicMixin) {
    
  return declare([AlfCore, _UserServiceTopicMixin], {
     
     /**
      * Topic for setting the current page as the user home
      *
      * @instance
      * @type {string}
      * @default "ALF_SET_CURRENT_PAGE_AS_HOME"
      */
     TOPIC_SET_CURRENT_PAGE_AS_HOME: "ALF_SET_CURRENT_PAGE_AS_HOME",
     
     _lastSeenHomePageRequest: null,
     
     constructor: function share_services_UserHomePageService__constructor(args) {
        declare.safeMixin(this, args);
        lang.mixin(this, args);
        
        this.alfSubscribe(this.TOPIC_SET_CURRENT_PAGE_AS_HOME, lang.hitch(this, this.onSetCurrentPageAsHome));
        this.alfSubscribe(this.setUserHomePageTopic, lang.hitch(this, this.onSetHomePage));
        this.alfSubscribe(this.setUserHomePageSuccessTopic, lang.hitch(this, this.onSetHomePageSuccess));
        this.alfSubscribe(this.setUserHomePageFailureTopic, lang.hitch(this, this.onSetHomePageFailure));
      },
      
      /**
       * Grabs the current URL and publishes to the set home page topic
       * @instance
       * @param {object} publishPayload The message payload
       */
      onSetCurrentPageAsHome:  function share_services_UserHomePageService__onSetCurrentPageAsHome(publishPayload) {
         if (publishPayload && publishPayload.servletContext)
         {
            var currentPage = window.location.href;
            currentPage = currentPage.replace(window.location.origin + publishPayload.servletContext, "/page");
            this.alfPublish(this.setUserHomePageTopic, { homePage: currentPage });
         }
      },
      
      /**
       * Stores the last seen home page set request
       * @instance
       * @param {object} publishPayload The message payload
       */
      onSetHomePage:  function share_services_UserHomePageService__onSetHomePage(publishPayload) {
         if (publishPayload && publishPayload.homePage)
         {
            this._lastSeenHomePageRequest = publishPayload.homePage;
         }
      },
      
      /**
       * Changes the header home link on successfully setting the home page
       * @instance
       * @param {object} publishPayload The message payload
       */
      onSetHomePageSuccess:  function share_services_UserHomePageService__onSetHomePageSuccess(publishPayload) {
         if (this._lastSeenHomePageRequest)
         {
            var homeLinkParent = dom.byId("HEADER_HOME_text");
            if (homeLinkParent)
            {
               location.reload();
               // TODO Instead of reload, programmatically set menu item link, dijit intercepts via Javascript
               // domAttr.set(homeLinkParent.children[0], "href", this._lastSeenHomePageRequest);
            }
         }
      },
      
      /**
       * Clears the last seen home page set request on failure
       * @instance
       * @param {object} publishPayload The message payload
       */
      onSetHomePageFailure:  function share_services_UserHomePageService__onSetHomePageFailure(publishPayload) {
         this._lastSeenHomePageRequest = null;
      }
  });
});