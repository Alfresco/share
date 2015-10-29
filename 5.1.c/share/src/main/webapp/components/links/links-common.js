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
 * This file defines functions used across different links related components
 */

Alfresco.util.links = {};

/**
 * Returns the html for the actions for a given links.
 * @param me the object that holds the _msg method used for i18n
 * @param data the links data
 * @param tagName the tag name to use for the actions. This will either be div or span, depending
 *                whether the actions are for the simple or detailed view.
 */
Alfresco.util.links.generateLinksActions = function generateLinksActions(me, data, tagName)
{
   var desc = '<div class="nodeEdit">';

   if (data.permissions["edit"])
   {
      desc += '<' + tagName + ' class="onEditLink"><a href="#" class="link-action-link-' + tagName + '">' + me.msg("action.edit") + '</a></' + tagName + '>';
   }
   if (data.permissions["delete"])
   {
      desc += '<' + tagName + ' class="onDeleteLink"><a href="#" class="link-action-link-' + tagName + '">' + me.msg("action.delete") + '</a></' + tagName + '>';
   }
   desc += '</div>';

   return desc;
};

