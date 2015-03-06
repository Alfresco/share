/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * This mock XHR server has been written to unit test the [AlfDocument]{@Link module:alfresco/documentlibrary/AlfDocument},
 * [AlfDocumentPreview]{@link module:alfresco/preview/AlfDocumentPreview} and the [Image Plugin]
 * {@link module:alfresco/preview/Image}.
 * 
 * @module aikauTesting/mockservices/PreviewMockXhr
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "aikauTesting/MockXhr",
        "dojo/text!./responseTemplates/previews/Image.json"], 
        function(declare, MockXhr, imageNode) {
   
   return declare([MockXhr], {

      // NOTE: Binary data is commented out for the moment because the binary data address needs to be consistent
      //       on both VM and local testing and localhost addresses will fail on VM.

      /**
       * Loads a JPEG image and converts it into an array.
       *
       * @instance
       */
      // loadBinaryData: function alfresco_testing_mockservices_PreviewMockXhr__loadBinaryData() {
      //    var oReq = new XMLHttpRequest();
      //    oReq.open("GET", "http://localhost:8089/aikau/res/js/aikau/testing/mockservices/responseTemplates/previews/Image.jpg", true);
      //    oReq.responseType = "arraybuffer";

      //    var _this = this;
      //    oReq.onload = function (oEvent) {
      //       _this.alfLog("log", "Binary data received");
      //       var arrayBuffer = oReq.response;
      //       if (arrayBuffer) {
      //          _this.imageByteArray = new Uint8Array(arrayBuffer);
      //          _this.waitForServer();
               
      //       }
      //    };
      //    oReq.send(null);
      // },

      // /**
      //  * Sets up the Sinon server to return the JPEG image when it is asked for a specific nodeRef
      //  *
      //  * @instance
      //  */
      // setupServerWithBinaryData: function alfresco_testing_mockservices_PreviewMockXhr__setupServerWithBinaryData() {
      //    this.alfLog("log", "Setting up server with binary data");
      //    this.server.respondWith("GET", 
      //                            /\/aikau\/proxy\/alfresco\/api\/node\/workspace\/SpacesStore\/62e6c83c-f239-4f85-b1e8-6ba0fd50fac4\/content\/thumbnails\/imgpreview\?(.*)/,
      //                            [200,
      //                            {"Content-Type":"image/jpeg"},
      //                            this.imageByteArray]);
      //    this.alfPublish("ALF_MOCK_XHR_SERVICE_READY", {});
      // },

      /**
       * This sets up the fake server with all the responses it should provide.
       *
       * @instance
       */
      setupServer: function alfresco_testing_mockservices_PreviewMockXhr__setupServer() {
         try
         {
            this.server.respondWith("GET",
                                    /\/aikau\/service\/components\/documentlibrary\/data\/node\/workspace\/SpacesStore\/62e6c83c-f239-4f85-b1e8-6ba0fd50fac4\?(.*)/,
                                    [200,
                                     {"Content-Type":"application/json;charset=UTF-8",
                                     "Content-Length":7962},
                                     imageNode]);
         }
         catch(e)
         {
            this.alfLog("error", "The following error occurred setting up the mock server", e);
         }
      }
   });
});
