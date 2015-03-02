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
 * TaskDetailsActions component.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.TaskDetailsActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector;

   /**
    * TaskDetailsActions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.TaskDetailsActions} The new TaskDetailsActions instance
    * @constructor
    */
   Alfresco.component.TaskDetailsActions = function TDA_constructor(htmlId)
   {
      Alfresco.component.TaskDetailsActions.superclass.constructor.call(this, "Alfresco.component.TaskDetailsActions", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("taskDetailedData", this.onTaskDetailsData, this);

      return this;
   };

   YAHOO.extend(Alfresco.component.TaskDetailsActions, Alfresco.component.Base,
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
          * Add referrer to the url if present
          *
          * @property referrer
          * @type String
          */
         referrer: null,

         /**
          * Adds nodeRef to the url if present
          *
          * @property nodeRef
          * @type String
          */
         nodeRef: null
      },

      /**
       * Event handler called when the "taskDetailedData" event is received
       *
       * @method: onTaskDetailsData
       */
      onTaskDetailsData: function TDA_onTaskDetailsData(layer, args)
      {
         var task = args[1],
               url = "task-edit?taskId=" + task.id;
         if (this.options.referrer)
         {
            url += "&referrer=" + this.options.referrer;
         }
         else if (this.options.nodeRef)
         {
            url += "&nodeRef=" + encodeURIComponent(this.options.nodeRef);
         }
         if (task.isEditable)
         {
            Alfresco.util.createYUIButton(this, "edit", function TDA_onMetadataRefresh_onEditClick()
            {
               window.location.href = Alfresco.util.siteURL(url);
            });
            Dom.removeClass(Selector.query(".actions", this.id), "hidden");
         }
      }
   });
})();
