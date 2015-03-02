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
 * InlineEditMgr template.
 * 
 * @namespace Alfresco
 * @class Alfresco.InlineEditMgr
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
    * InlineEditMgr constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.InlineEditMgr} The new InlineEditMgr instance
    * @constructor
    */
   Alfresco.InlineEditMgr = function InlineEditMgr_constructor(htmlId)
   {
      Alfresco.InlineEditMgr.superclass.constructor.call(this, "Alfresco.InlineEditMgr", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      
      return this;
   };

   YAHOO.extend(Alfresco.InlineEditMgr, Alfresco.component.Base,
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
          * Current nodeRef.
          * 
          * @property nodeRef
          * @type Alfresco.util.NodeRef
          */
         nodeRef: null,

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
      onFormContentReady: function InlineEditMgr_onFormContentReady(layer, args)
      {
         // change the default 'Submit' label to be 'Save'
         var submitButton = args[1].buttons.submit;
         submitButton.set("label", this.msg("button.save"));
         
         // add a handler to the cancel button
         var cancelButton = args[1].buttons.cancel;
         cancelButton.addListener("click", this.onCancelButtonClick, null, this);
         
         // add actions for Activity post info
         this.modules.actions = new Alfresco.module.DoclibActions();
      },
      
      /**
       * Event handler called when the "beforeFormRuntimeInit" event is received
       */
      onBeforeFormRuntimeInit: function InlineEditMgr_onBeforeFormRuntimeInit(layer, args)
      {
         args[1].runtime.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onInlineEditSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onInlineEditFailure,
               scope: this
            }
         });
      },
      
      /**
       * Handler called when the metadata was updated successfully
       *
       * @method onInlineEditSuccess
       * @param response The response from the submission
       */
      onInlineEditSuccess: function InlineEditMgr_onInlineEditSuccess(response)
      {
         var siteId = this.options.siteId,
            data = 
            {
               fileName: response.config.dataObj.prop_cm_name,
               nodeRef: this.options.nodeRef
            };
         this.modules.actions.postActivity(siteId, "inline-edit", "document-details", data, this.bind(function(){this._navigateForward()}));
      },
      
      /**
       * Handler called when the metadata update operation failed
       *
       * @method onInlineEditFailure
       * @param response The response from the submission
       */
      onInlineEditFailure: function InlineEditMgr_onInlineEditFailure(response)
      {
         var errorMsg = this.msg("inline-edit-mgr.save.failed");
         if (response.json.message)
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
      onCancelButtonClick: function InlineEditMgr_onCancel(type, args)
      {
         this._navigateForward();
      },
      
      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function InlineEditMgr__navigateForward()
      {
         if (document.referrer)
         {
            /* Did we come from the document library? If so, then direct the user back there */
            if (document.referrer.match(/documentlibrary([?]|$)/) || 
                document.referrer.match(/repository([?]|$)/) ||
                document.referrer.match(/faceted-search([?]|$)/))
            {
               // go back to the referrer page
               history.go(-1);
            }
            else
            {
               document.location.href = document.referrer;
            }
         }
         else if (history.length > 1)
         {
            /**
             * The document.referrer wasn't available, either because there was no previous page (because the user
             * navigated directly to the page) or because the referrer header has been blocked.
             * So instead we'll use the browser history, unfortunately with increased chance of displaying stale data
             * since the page most likely won't be refreshed.
             */
            history.go(-1);
         }
         else
         {
            // go forward to the appropriate details page for the node
            window.location.href = $siteURL("document-details?nodeRef=" + new Alfresco.util.NodeRef(this.options.nodeRef).toString());
         }
      }
   });
})();
