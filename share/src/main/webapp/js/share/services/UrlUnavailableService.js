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
 * @module share/services/UrlUnavailableService
 * @extends module:alfresco/core/Core
 * @author Ray Gauss II
 */
define(["dojo/_base/declare",
  "dojo/_base/lang",
  "alfresco/core/Core",
  "alfresco/core/CoreXhr",
  "service/constants/Default"],
  function(declare, lang, AlfCore, AlfXhr, AlfConstants) {
    
  return declare([AlfCore, AlfXhr], {
     constructor: function share_services_UrlUnavailableService__constructor(args) {
        declare.safeMixin(this, args);
        lang.mixin(this, args);
        
        if (args.httpStatusCode) {
           var me = this;
           me.handleError(args);
        }
      },
      
      /**
       * @instance
       * @param {object} error The passed in error object
       */
      handleError:  function share_services_UrlUnavailableService__handleError(error) {
         // TODO different handling for different error.httpStatusCode?
         var url = AlfConstants.URL_CONTEXT + "error500.jsp";
         this.serviceXhr({url : url,
            method: "GET",
            successCallback: this._errorPageLoaded,
            failureCallback: this._errorPageLoadedFailed,
            callbackScope: this});
      },
      
      /**
       * @instance
       * @param {object} response The response from the request
       */
      _errorPageLoaded: function share_services_UrlUnavailableService__errorPageLoaded(response) {
         document.open();
         document.write(response);
         document.close();
      },
      
      /**
       * @instance
       * @param {object} response The response from the request
       */
      _errorPageLoadedFailed: function share_services_UrlUnavailableService__errorPageLoadedFailed(response) {
         this.alfLog("error", "Could not load error page", response);
      }
  });
});