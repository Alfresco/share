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
 * CustomisePages component.
 *
 * @namespace Alfresco
 * @class Alfresco.CustomisePages
 */
(function()
{
   var Dom = YAHOO.util.Dom, 
       Event = YAHOO.util.Event,
       Selector = YAHOO.util.Selector;

   var $html = Alfresco.util.encodeHTML;   

   /**
    * Alfresco.CustomisePages constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.CustomisePages} The new CustomisePages instance
    * @constructor
    */
   Alfresco.CustomisePages = function(htmlId)
   {
      return Alfresco.CustomisePages.superclass.constructor.call(this, "Alfresco.CustomisePages", htmlId, ["button"]);
   };

   YAHOO.extend(Alfresco.CustomisePages, Alfresco.component.Base,
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
          * The id for the site who's pages are configured
          *
          * @property siteId
          * @type {string} The siteId
          */
         siteId: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onReady
       */
      onReady: function CP_onReady()
      {
         // Add drag n drop support
         this.widgets.dnd = new Alfresco.util.DragAndDrop(
         {
            draggables: [
               {
                  container: this.id + "-availablePages-ul",
                  groups: [ Alfresco.util.DragAndDrop.GROUP_MOVE ],
                  cssClass: "customise-pages-page-list-item",
                  vertical: false,
                  movesOnEnterKey: true,
                  callback:
                  {
                     fn: this.onAvailableDragAndDropAction,
                     scope: this
                  }
               },
               {
                  container: this.id + "-currentPages-ul",
                  groups: [ Alfresco.util.DragAndDrop.GROUP_MOVE, Alfresco.util.DragAndDrop.GROUP_DELETE ],
                  cssClass: "customise-pages-page-list-item",
                  vertical: false,
                  callback:
                  {
                     fn: this.onCurrentDragAndDropAction,
                     scope: this
                  }
               }
            ],
            targets: [
               {
                  container: this.id + "-availablePages-ul",
                  group: Alfresco.util.DragAndDrop.GROUP_MOVE
               },
               {
                  container: this.id + "-currentPages-ul",
                  group: Alfresco.util.DragAndDrop.GROUP_MOVE
               }
            ]
         });
         
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
      },

      /**
       * Called when a current page's rename anchor has been clicked
       *
       * @method onRenameClick
       * @param pageId
       * @param anchor
       */
      onRenameClick: function(pageId, anchor)
      {
         var li = Dom.get(this.id + "-page-" + pageId),
            sitePageTitleInputEl = Selector.query("input[name=sitePageTitle]", li, true),
            sitePageTitleH3El = Selector.query("h3.title", li, true),
            type = Selector.query("div.type", li, true).innerHTML;
         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("popup.rename.header", $html(type)),
            text: this.msg("popup.rename.label"),
            input: "text",
            value: $html(sitePageTitleInputEl.value),
            callback:
            {
               fn: this.onPageRenamed,
               obj: [ sitePageTitleInputEl, sitePageTitleH3El],
               scope: this
            }
         });
      },

      /**
       * Called when the page's name has been changed in the dialog
       *
       * @method onPageRenamed
       * @param sitePageTitle
       * @param elements
       */
      onPageRenamed: function(sitePageTitle, elements)
      {
         elements[0].value = sitePageTitle;
         elements[1].innerHTML = $html(sitePageTitle);
      },

      /**
       * Called when a dnd action has occured in the current pages lists.
       *
       * @param action
       * @param draggable
       * @param container
       */
      onAvailableDragAndDropAction: function(action, draggable, container)
      {
         // Reset site specific names in the hidden input fields
         this.resetPage(draggable);
      },

      /**
       * Called when a dnd action has occured in the current pages lists.
       *
       * @param action
       * @param draggable
       * @param container
       */
      onCurrentDragAndDropAction: function(action, draggable, container)
      {
         if (action == Alfresco.util.DragAndDrop.ACTION_DELETE)
         {
            // Move page/draggable to available pages
            this.removeCurrentPage(draggable);

            /**
             * Return false since we do NOT want the dnd container to go ahead and delete
             * the page/draggable we just moved to the available pages.
             */
            return false;
         }
      },

      /**
       * Called when a current page's remove anchor has been clicked
       *
       * @method onRemoveClick
       * @param pageId
       * @param anchor
       */
      onRemoveClick: function(pageId, anchor)
      {
         this.removeCurrentPage(Dom.get(this.id + "-page-" + pageId));
      },

      /**
       * Resets the site specific page info and moves the page to available pages
       *
       * @method removeCurrentPage
       * @param li
       */
      removeCurrentPage: function(li)
      {
         this.resetPage(li);
         Dom.get(this.id + "-availablePages-ul").appendChild(li);
      },

      /**
       * moves the page to available pages
       *
       * @method resetPage
       * @param li
       */
      resetPage: function(li)
      {
         var type = Selector.query("div.type", li, true).innerHTML;
         Selector.query("input[name=sitePageTitle]", li, true).value = "";
         Selector.query("h3.title", li, true).innerHTML = type;
      },

      /**
       * Fired when the user clicks the Save/Done button.
       * Saves the dashboard config and takes the user back to the dashboard page.
       *
       * @method onSaveButtonClick
       * @param event {object} a "click" event
       */
      onSaveButtonClick: function CD_onSaveButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.saveButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);

         var inputEls = Selector.query(".current-pages ul li input", this.id),
            inputEl,
            pages = [],
            page;
         for (var i = 0, il = inputEls.length; i < il; i++)
         {
            inputEl = inputEls[i];
            if (inputEl.name == "pageId")
            {
               page = {
                  pageId: inputEl.value
               };
               pages.push(page);
            }
            else if (inputEl.name == "sitePageTitle" && inputEl.value.length > 0)
            {
               page.sitePageTitle = inputEl.value;
            }
         }
         
         // Select theme option
         var themeId = Dom.get(this.id + "-theme-menu").value;
         
         // Execute the request and redirect the user to the dashboard on success
         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.POST,
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/site/customise-pages",
            dataObj:
            {
               siteId: this.options.siteId,
               pages: pages,
               themeId: themeId
            },
            successCallback:
            {
               fn: function()
               {
                  // Send the user to the newly configured dashboard
                  document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/dashboard";
               },
               scope: this
            },
            failureMessage: Alfresco.util.message("message.saveFailure", this.name),
            failureCallback:
            {
               fn: function()
               {
                  // Hide spinner
                  this.widgets.feedbackMessage.destroy();
                  
                  // Enable the buttons again
                  this.widgets.saveButton.set("disabled", false);
                  this.widgets.cancelButton.set("disabled", false);
               },
               scope: this
            }
         });

         // Display a spinning save message to the user 
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.saving", this.name),
            spanClass: "wait",
            displayTime: 0
         });

      },

      /**
       * Fired when the user clicks cancel layout button.
       * Takes the user to the sites dashboard
       *
       * @method onCancelButtonClick
       * @param event {object} an "click" event
       */
      onCancelButtonClick: function CP_onCancelButtonClick(event)
      {
         // Take the user back to the sites dashboard
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/dashboard";
      }

   });
})();
