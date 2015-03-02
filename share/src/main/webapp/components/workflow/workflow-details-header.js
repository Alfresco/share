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
 * WorkflowDetailsHeader component.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.WorkflowDetailsHeader
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * WorkflowDetailsHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.WorkflowDetailsHeader} The new WorkflowDetailsHeader instance
    * @constructor
    */
   Alfresco.component.WorkflowDetailsHeader = function WorkflowDetailsHeader_constructor(htmlId)
   {
      Alfresco.component.WorkflowDetailsHeader.superclass.constructor.call(this, "Alfresco.component.WorkflowDetailsHeader", htmlId, ["button", "container", "datasource", "datatable"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("workflowDetailedData", this.onWorkflowDetailedData, this);

      return this;
   };

   YAHOO.extend(Alfresco.component.WorkflowDetailsHeader, Alfresco.component.Base,
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
          * The taskId for the task related to the workflow, if any.
          *
          * @property taskId
          * @type String
          * @default null
          */
         taskId: null
      },
      
      /**
       * Event handler called when the "onWorkflowDetailedData" event is received
       *
       * @method: onWorkflowDetailedData
       */
      onWorkflowDetailedData: function TDH_onWorkflowDetailedData(layer, args)
      {
         // Display workflow description
         var workflow = args[1],
            title = null,
            taskId = this.options.taskId || workflow.startTaskInstanceId;
         if (taskId)
         {
            // There was a specific task related to the task in the url, lets use its message
            var task, message, type;
            for (var i = 0, il = workflow.tasks.length; i < il; i++)
            {
               task = workflow.tasks[i];
               if (task.id == taskId)
               {
                  message = task.properties.bpm_description;
                  type = task.title;
               }
            }
            if (message && message != type)
            {
               title = this.msg("label.message", $html(message), $html(type));
            }
            else
            {
               title = this.msg("label.noMessage", $html(type));
            }
         }
         else
         {
            title = this.msg("label.noMessageAndNoTask");
         }
         Selector.query("h1 span", this.id, true).innerHTML = title;
      }

   });

})();
