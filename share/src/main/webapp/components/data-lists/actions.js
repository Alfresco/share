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
 * Data Lists Actions module
 * 
 * @namespace Alfresco.service
 * @class Alfresco.service.DataListActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Bubbling = YAHOO.Bubbling;

   /**
    * Alfresco.service.DataListActions implementation
    */
   Alfresco.service.DataListActions = {};
   Alfresco.service.DataListActions.prototype =
   {
      /**
       * Delete item(s).
       *
       * @method onActionDelete
       * @param items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
       */
      onActionDelete: function DataListActions_onActionDelete(p_items)
      {
         var me = this,
            items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
         
         var fnActionDeleteConfirm = function DataListActions__onActionDelete_confirm(items)
         {
            var nodeRefs = [];
            for (var i = 0, ii = items.length; i < ii; i++)
            {
               nodeRefs.push(items[i].nodeRef);
            }

            this.modules.actions.genericAction(
            {
               success:
               {
                  event:
                  {
                     name: "dataItemsDeleted",
                     obj:
                     {
                        items: items
                     }
                  },
                  message: this.msg("message.delete.success", items.length)
               },
               failure:
               {
                  message: this.msg("message.delete.failure")
               },
               webscript:
               {
                  method: Alfresco.util.Ajax.DELETE,
                  name: "items"
               },
               config:
               {
                  requestContentType: Alfresco.util.Ajax.JSON,
                  dataObj:
                  {
                     nodeRefs: nodeRefs
                  }
               }
            });
         };

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title", items.length),
            text: this.msg("message.confirm.delete.description", items.length),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function DataListActions__onActionDelete_delete()
               {
                  this.destroy();
                  fnActionDeleteConfirm.call(me, items);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function DataListActions__onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Duplicate item(s).
       *
       * @method onActionDuplicate
       * @param items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
       */
      onActionDuplicate: function DataListActions_onActionDuplicate(p_items)
      {
         var me = this,
            items = YAHOO.lang.isArray(p_items) ? p_items : [p_items],
            destinationNodeRef = new Alfresco.util.NodeRef(this.modules.dataGrid.datalistMeta.nodeRef),
            nodeRefs = [];

         for (var i = 0, ii = items.length; i < ii; i++)
         {
            nodeRefs.push(items[i].nodeRef);
         }

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "dataItemsDuplicated",
                  obj:
                  {
                     items: items
                  }
               },
               message: this.msg("message.duplicate.success", items.length)
            },
            failure:
            {
               message: this.msg("message.duplicate.failure")
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "duplicate/node/" + destinationNodeRef.uri
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: nodeRefs
               }
            }
         });
      }
   };
})();