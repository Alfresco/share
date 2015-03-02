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
 * Repository DocumentList TreeView component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocListCategories
 */
(function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * Records DocumentList TreeView constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocListCategories} The new DocListCategories instance
    * @constructor
    */
   Alfresco.DocListCategories = function DLT_constructor(htmlId)
   {
      Alfresco.DocListCategories.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.DocListCategories";
      Alfresco.util.ComponentManager.reregister(this);
      
      // Category filterId
      this.filterId = "category";
      
      // Register with Filter Manager
      Alfresco.util.FilterManager.register(this.name, this.filterId);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.DocListCategories, Alfresco.DocListTree,
   {
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function DLT__buildTreeNodeUrl(path)
       {
          var nodeRef = new Alfresco.util.NodeRef(this.options.nodeRef),
            uriTemplate ="slingshot/doclib/categorynode/node/" + $combine(encodeURI(nodeRef.uri), Alfresco.util.encodeURIPath(path));

          return  Alfresco.constants.PROXY_URI + uriTemplate + "?perms=false&children=" + this.options.evaluateChildFolders;
       }
   });
})();