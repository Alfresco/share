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
 * CreateContentMgr template.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateContentMgr
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $siteURL = Alfresco.util.siteURL;

   /**
    * CreateContentMgr constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CreateContentMgr} The new CreateContentMgr instance
    * @constructor
    */
   Alfresco.CreateContentMgr = function CreateContentMgr_constructor(htmlId)
   {
      Alfresco.CreateContentMgr.superclass.constructor.call(this, "Alfresco.CreateContentMgr", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      
      return this;
   };

   YAHOO.extend(Alfresco.CreateContentMgr, Alfresco.component.Base,
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
          * Is the created type expected to be a container?
          * The manager needs to know whether the following page is document-details or folder-details.
          * 
          * @property isContainer
          * @type boolean
          * @default false
          */
         isContainer: false,
         
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: null
      },

      /**
       * Event handler called when the "formContentReady" event is received
       */
      onFormContentReady: function CreateContentMgr_onFormContentReady(layer, args)
      {
         // change the default 'Submit' label to be 'Save'
         var submitButton = args[1].buttons.submit;
         submitButton.set("label", this.msg("button.create"));
         
         // add a handler to the cancel button
         var cancelButton = args[1].buttons.cancel;
         cancelButton.addListener("click", this.onCancelButtonClick, null, this);
      },
      
      /**
       * Event handler called when the "beforeFormRuntimeInit" event is received
       */
      onBeforeFormRuntimeInit: function CreateContentMgr_onBeforeFormRuntimeInit(layer, args)
      {
         args[1].runtime.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateContentSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onCreateContentFailure,
               scope: this
            }
         });
      },
      
      /**
       * Handler called when the metadata was updated successfully
       *
       * @method onCreateContentSuccess
       * @param response The response from the submission
       */
      onCreateContentSuccess: function CreateContentMgr_onCreateContentSuccess(response)
      {
         var nodeRef = null;
         if (response.json && response.json.persistedObject)
         {
            // Grab the new nodeRef and pass it on to _navigateForward() to optionally use
            nodeRef = new Alfresco.util.NodeRef(response.json.persistedObject);
            
            // Activity post - documents only
            if (!this.options.isContainer)
            {
              Alfresco.Share.postActivity(this.options.siteId, "org.alfresco.documentlibrary.file-created", "{cm:name}", "document-details?nodeRef=" + nodeRef.toString(),
              {
                 appTool: "documentlibrary",
                 nodeRef: nodeRef.toString()
              }, this.bind(function() { this._navigateForward(nodeRef); }));
            }
         }
      },
      
      /**
       * Handler called when the metadata update operation failed
       * 
       * @method onCreateContentFailure
       * @param response The response from the submission
       */
      onCreateContentFailure: function CreateContentMgr_onCreateContentFailure(response)
      {
         var errorMsg = this.msg("create-content-mgr.create.failed");
         if (response.json && response.json.message)
         {
            errorMsg = errorMsg + ": " + response.json.message;
         }
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.failure"),
            text: errorMsg
         });
      },
      
      /**
       * Called when user clicks on the cancel button.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function CreateContentMgr_onCancel(type, args)
      {
         this._navigateForward();
      },
      
      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       * @param nodeRef {Alfresco.util.NodeRef} Optional: NodeRef of just-created content item
       */
      _navigateForward: function CreateContentMgr__navigateForward(nodeRef)
      {
         /* Have we been given a nodeRef from the Forms Service? */
         if (YAHOO.lang.isObject(nodeRef))
         {
            window.location.href = $siteURL((this.options.isContainer ? "folder" : "document") + "-details?nodeRef=" + nodeRef.toString());
         }
         else if (document.referrer)
         {
            /* Did we come from the document library? If so, then direct the user back there */
            if (document.referrer.match(/documentlibrary([?]|$)/) || document.referrer.match(/repository([?]|$)/))
            {
               // go back to the referrer page
               history.go(-1);
            }
            else
            {
               document.location.href = document.referrer;
            }
         }
         else if (this.options.siteId && this.options.siteId !== "")
         {
            // In a Site, so go back to the document library root
            window.location.href = $siteURL("documentlibrary");
         }
         else
         {
            window.location.href = Alfresco.constants.URL_CONTEXT;
         }
      }
   });
})();
