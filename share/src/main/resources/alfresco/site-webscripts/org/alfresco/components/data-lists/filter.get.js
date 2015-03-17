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
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Actions
   var myConfig = new XML(config.script),
      filters = [],
      filterIds = [];
   
   for each(var xmlFilter in myConfig..filter)
   {
      filters.push(
      {
         id: xmlFilter.@id.toString(),
         data: xmlFilter.@data.toString(),
         label: xmlFilter.@label.toString()
      });
      
      filterIds.push(xmlFilter.@id.toString());
   }
   
   model.filters = filters;
   model.filterIds = filterIds;
   
   // Widget instantiation metadata...
   var filter = {
      id : "BaseFilter", 
      name : "Alfresco.component.BaseFilter",
      assignTo : "filter",
      initArgs : ["'Alfresco.DataListFilter'","\"" + args.htmlid + "\""],
      useMessages : false
   };
   
   model.widgets = [filter];
}

main();


