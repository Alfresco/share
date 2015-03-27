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
 * RuleConfigType.
 * 
 * @namespace Alfresco
 * @class Alfresco.RuleConfigType
 */
(function()
{

   Alfresco.RuleConfigType = function(htmlId)
   {
      Alfresco.RuleConfigType.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.RuleConfigType";
      Alfresco.util.ComponentManager.reregister(this);

      // Set default options
      //this.options.configWebscript = Alfresco.constants.URL_SERVICECONTEXT + "api/ruletypes";
      this.options.configWebscript = Alfresco.constants.PROXY_URI_RELATIVE + "api/sites";

      return this;
   };

   YAHOO.extend(Alfresco.RuleConfigType, Alfresco.RuleConfig,
   {
   });

})();