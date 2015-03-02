/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * DocumentListSimpleViewRenderer component which extends DocumentListViewRenderer
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentListSimpleViewRenderer
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * SimpleViewRenderer constructor.
    *
    * @param name {String} The name of the SimpleViewRenderer
    * @return {Alfresco.DocumentListSimpleViewRenderer} The new SimpleViewRenderer instance
    * @constructor
    */
   Alfresco.DocumentListSimpleViewRenderer = function(name, parentDocumentList)
   {
      Alfresco.DocumentListSimpleViewRenderer.superclass.constructor.call(this, name, parentDocumentList);
      this.actionsColumnWidth = 80;
      this.actionsSplitAtModifier = 0;
      return this;
   };
   
   /**
    * Extend from Alfresco.DocumentListViewRenderer
    */
   YAHOO.extend(Alfresco.DocumentListSimpleViewRenderer, Alfresco.DocumentListViewRenderer);
   
   /**
    * Override Alfresco.DocumentListViewRenderer.renderCellThumbnail with a simple icon and preview
    */
   Alfresco.DocumentListSimpleViewRenderer.prototype.renderCellThumbnail = function DL_SVR_renderCellThumbnail(scope, elCell, oRecord, oColumn, oData)
   {
      var record = oRecord.getData(),
         node = record.jsNode,
         properties = node.properties,
         name = record.displayName,
         isContainer = node.isContainer,
         isLink = node.isLink,
         extn = name.substring(name.lastIndexOf(".")),
         imgId = node.nodeRef.nodeRef; // DD added
      
      var containerTarget; // This will only get set if thumbnail represents a container
      
      oColumn.width = 40;
      Dom.setStyle(elCell, "width", oColumn.width + "px");
      Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

      if (isContainer)
      {
         elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + (scope.dragAndDropEnabled ? '<span class="droppable"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
         containerTarget = new YAHOO.util.DDTarget(imgId); // Make the folder a target
      }
      else
      {
         var id = scope.id + '-preview-' + oRecord.getId();
         var fileIcon = Alfresco.util.getFileIcon(name);
         if (fileIcon == "generic-file-32.png")
         {
            fileIcon = Alfresco.util.getFileIconByMimetype(node.mimetype);
         }
         elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + fileIcon + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
		 
         // Preview tooltip
         scope.previewTooltips.push(id);
      }
      var dnd = new Alfresco.DnD(imgId, scope);
   };
   
   /**
    * Override Alfresco.DocumentListViewRenderer.setupRenderer to setup preview tooltip
    */
   Alfresco.DocumentListSimpleViewRenderer.prototype.setupRenderer = function DL_SVR_setupRenderer(scope)
   {
      Dom.addClass(scope.id + this.buttonElementIdSuffix, this.buttonCssClass);
      // Tooltip for thumbnail in Simple View
      scope.widgets.previewTooltip = new YAHOO.widget.Tooltip(scope.id + "-previewTooltip",
      {
         width: "108px"
      });
      scope.widgets.previewTooltip.contextTriggerEvent.subscribe(function(type, args)
      {
         var context = args[0],
            oRecord = scope.widgets.dataTable.getRecord(context.id),
            record = oRecord.getData();

         this.cfg.setProperty("text", '<img src="' + Alfresco.DocumentList.generateThumbnailUrl(record) + '" />');
      });
   };

})();