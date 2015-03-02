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
 * @module DocumentLibrary
 */

/**
 * Gallery view extension of DocumentListViewRenderer component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentListGalleryViewRenderer
 * @extends Alfresco.DocumentListViewRenderer
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Anim = YAHOO.util.Anim;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Preferences
    */
   var PREFERENCES_DOCLIST = "org.alfresco.share.documentList",
      PREF_GALLERY_COLUMNS = PREFERENCES_DOCLIST + ".galleryColumns",
      DND_CONTAINER_ID = 'ygddfdiv';
   
   // Other constants
   var DEFAULT_GALLERY_COLUMNS = 7, // Only used if option is undefined
      DETAIL_PANEL_WIDTH = '397px',
      DETAIL_PANEL_OFFSET = [-40, -20],
      DETAIL_PANEL_DEFAULT_TIMEOUT = 700,
      WINDOW_RESIZE_CHECK_TIME = 20,
      WINDOW_RESIZE_MIN_TIME = 100,
      ANIMATE_ON_WINDOW_RESIZE = false,
      ANIMATE_ON_COLUMN_CHANGE = true;
   
   /**
    * GalleryViewRenderer constructor.
    *
    * @param name {String} The name of the GalleryViewRenderer
    * @return {Alfresco.DocumentListGalleryViewRenderer} The new GalleryViewRenderer instance
    * @constructor
    */
   Alfresco.DocumentListGalleryViewRenderer = function(name, parentDocumentList, galleryColumns)
   {
      Alfresco.DocumentListGalleryViewRenderer.superclass.constructor.call(this, name, parentDocumentList);
      this.parentElementIdSuffix = "-gallery";
      this.parentElementEmptytIdSuffix = "-gallery-empty";
      this.rowClassName = "alf-gallery-item";
      this.infoPanelClassName = "alf-detail";
      this.metadataBannerViewName = "detailed";
      this.metadataLineViewName = "detailed";
      this.galleryColumns = galleryColumns;
      this.infoPanelPopupTimeout = DETAIL_PANEL_DEFAULT_TIMEOUT;
      this.windowResizeCheckTime = WINDOW_RESIZE_CHECK_TIME;
      this.windowResizeMinTime = WINDOW_RESIZE_MIN_TIME;
      this.documentList = null;
      return this;
   };

   /**
    * Extend from Alfresco.DocumentListViewRenderer
    */
   YAHOO.extend(Alfresco.DocumentListGalleryViewRenderer, Alfresco.DocumentListViewRenderer);
   
   /**
    * Generates a row item HTML ID from the given dataTable record
    *
    * @method getRowItemId
    * @param oRecord {object} data table record
    * @return {string} the row HTML item ID
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemId = function DL_GVR_getRowItemId(oRecord)
   {
      if (this.documentList != null && oRecord != null)
      {
         return this.documentList.id + '-gallery-item-' + oRecord.getId();
      }
   };
   
   /**
    * Gets the row item HTML element from the given dataTable record, checking and fixing any IDs changed by AJAX calls
    * to the dataTable as well
    *
    * @method getRowItem
    * @param oRecord {object} data table record
    * @param elCell {HTMLElement} the data table cell asking for the gallery item (optional)
    * @return {HTMLElement} the row item
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItem = function DL_GVR_getRowItem(oRecord, elCell)
   {
      if (this.documentList != null && oRecord != null)
      {
         var galleryItemId = this.getRowItemId(oRecord);
         // Yahoo.util.Dom.get does not work here for some reason
         var galleryItem = document.getElementById(galleryItemId);
         if (galleryItem === null && elCell != null)
         {
            // AJAX call must have updated the table, change our ID as well
            var rowElement = Dom.getAncestorByTagName(elCell, 'tr');
            var oldGalleryItemId = this.documentList.id + '-gallery-item-' + rowElement.id;
            galleryItem = document.getElementById(oldGalleryItemId);
            if (galleryItem !== null)
            {
               galleryItem.setAttribute('id', galleryItemId);
            }
         }
         return galleryItem;
      }
   };
   
   /**
    * Generates a row item select id from the given dataTable record
    *
    * @method getRowItemSelectId
    * @param oRecord {object} data table record
    * @return {string} the row item select control ID
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemSelectId = function DL_GVR_getRowItemSelectId(oRecord)
   {
      if (oRecord != null)
      {
         return 'checkbox-' + oRecord.getId() + '-gallery-item';
      }
   };
   
   /**
    * Gets the row item's detail popup panel element from the given row item
    *
    * @method getRowItemDetailElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item detail element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemDetailElement = function DL_GVR_getRowItemDetailElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemDetailElement = Dom.getChildren(rowItem)[1];
         // YUI may have added its container
         if (Dom.hasClass(galleryItemDetailElement, 'yui-panel-container'))
         {
            galleryItemDetailElement = Dom.getFirstChild(galleryItemDetailElement);
         }
         return galleryItemDetailElement;
      }
   };
   
   /**
    * Gets the row item's detail description element from the given row item
    *
    * @method getRowItemDetailDescriptionElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item detail description element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemDetailDescriptionElement = function DL_GVR_getRowItemDetailDescriptionElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemDetailDiv = this.getRowItemDetailElement(rowItem);
         return Dom.getChildren(Dom.getFirstChild(galleryItemDetailDiv))[3];
      }
   };
   
   /**
    * Gets the row item's detail thumbnail element from the given row item
    *
    * @method getRowItemDetailThumbnailElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item detail thumbnail element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemDetailThumbnailElement = function DL_GVR_getRowItemDetailThumbnailElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemDetailDiv = this.getRowItemDetailElement(rowItem);
         return Dom.getFirstChild(Dom.getFirstChild(galleryItemDetailDiv));
      }
   };
   
   /**
    * Gets the row item's thumbnail element from the given row item
    *
    * @method getRowItemThumbnailElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item thumbnail element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemThumbnailElement = function DL_GVR_getRowItemThumbnailElement(rowItem)
   {
      if (rowItem != null)
      {
         return Dom.getFirstChild(rowItem);
      }
   };
   
   /**
    * Gets the row item's header element from the given row item
    *
    * @method getRowItemHeaderElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item header element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemHeaderElement = function DL_GVR_getRowItemHeaderElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemThumbnailDiv = this.getRowItemThumbnailElement(rowItem);
         return Dom.getFirstChild(galleryItemThumbnailDiv);
      }
   };
   
   /**
    * Gets the row item's selection element from the given row item
    *
    * @method getRowItemSelectElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item selection element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemSelectElement = function DL_GVR_getRowItemSelectElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemHeaderElement = this.getRowItemHeaderElement(rowItem);
         return Dom.getFirstChild(galleryItemHeaderElement);
      }
   };
   
   /**
    * Gets the row item's label element from the given row item
    *
    * @method getRowItemLabelElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item label element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemLabelElement = function DL_GVR_getRowItemLabelElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemThumbnailDiv = this.getRowItemThumbnailElement(rowItem);
         return Dom.getChildren(galleryItemThumbnailDiv)[1];
      }
   };
   
   /**
    * Gets the row item's status element from the given row item
    *
    * @method getRowItemStatusElement
    * @param rowItem {HTMLElement} The row item object
    * @return {HTMLElement} the row item status element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemStatusElement = function DL_GVR_getRowItemStatusElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemDetailDiv = this.getRowItemDetailElement(rowItem);
         return Dom.getChildren(Dom.getFirstChild(galleryItemDetailDiv))[1];
      }
   };
   
   /**
    * Gets the row item's actions element from the given row item
    *
    * @method getRowItemActionsElement
    * @param rowItemscope.services.preferences.get(); {HTMLElement} The row item object
    * @return {HTMLElement} the row item actions element
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowItemActionsElement = function DL_GVR_getRowItemActionsElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemDetailDiv = this.getRowItemDetailElement(rowItem);
         return Dom.getChildren(Dom.getFirstChild(galleryItemDetailDiv))[2];
      }
   };
   
   // Override some of the standard ViewRenderer methods
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.getDataTableRecordIdFromRowElement = function DL_GVR_getDataTableRecordIdFromRowElement(scope, rowElement)
   {
      var elementId = Alfresco.DocumentListGalleryViewRenderer.superclass.getDataTableRecordIdFromRowElement.call(this, scope, rowElement);
      if (elementId != null)
      {
         return elementId.replace(scope.id + '-gallery-item-', '');
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowElementFromDataTableRecord = function DL_GVR_getRowElementFromDataTableRecord(scope, oRecord)
   {
      var galleryItemId = this.getRowItemId(oRecord);
      // Yahoo.util.Dom.get does not work here for some reason
      return document.getElementById(galleryItemId);
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.getRowSelectElementFromDataTableRecord = function DL_GVR_getRowSelectElementFromDataTableRecord(scope, oRecord)
   {
      var selectId = this.getRowItemSelectId(oRecord);
      return Dom.get(selectId);
   };
   
   /**
    * Sets up the gallery item hover delegates.
    *
    * @method setupItemHovers
    * @param scope {HTMLElement} The document list object
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.setupItemHovers = function DL_GVR_setupItemHovers(scope)
   {
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      
      // On mouseover show the select checkbox and detail pull down
      Event.delegate(container, 'mouseover', Alfresco.DocumentListGalleryViewRenderer.onMouseOverItem,
            'div.' + this.rowClassName, this);
      
      // On mouseout hide the select checkbox and detail pull down
      Event.delegate(container, 'mouseout', Alfresco.DocumentListGalleryViewRenderer.onMouseOutItem,
            'div.' + this.rowClassName, this);
   }
   
   /**
    * Tear down of the gallery item hover delegates.
    *
    * @method destroyItemHovers
    * @param scope {HTMLElement} The document list object
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.destroyItemHovers = function DL_GVR_destroyItemHovers(scope)
   {
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      
      Event.removeDelegate(container, 'mouseover', 
            Alfresco.DocumentListGalleryViewRenderer.onMouseOverItem);
      
      Event.removeDelegate(container, 'mouseout', 
            Alfresco.DocumentListGalleryViewRenderer.onMouseOutItem);
   }
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.setupRenderer = function DL_GVR_setupRenderer(scope)
   {
      Alfresco.DocumentListGalleryViewRenderer.superclass.setupRenderer.call(this, scope);
      
      this.documentList = scope;
      
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      
      this.galleryColumnsChangedEvent = new YAHOO.util.CustomEvent("galleryViewColumnsChangedCE");
      YAHOO.Bubbling.subscribe("galleryViewColumnsChanged", function(layer, args) {
         this.galleryColumnsChangedEvent.fire();
      }, this);
      
      this.setupItemHovers(scope);
      
      var viewRendererInstance = this;
      
      // On click of detail pull down show detail panel
      Event.delegate(container, 'click', function DL_GVR_infoPopup(event, matchedEl, container)
      {
         viewRendererInstance.onShowGalleryItemDetail(scope, viewRendererInstance, event, matchedEl, container);
      }, '.alf-show-detail', this);
      
      // On click of select checkbox
      Event.delegate(container, 'click', function DL_GVR_selectCheckboxClicked(event, matchedEl, container)
      {
         var eventTarget = Event.getTarget(event);
         scope.selectedFiles[eventTarget.value] = eventTarget.checked;
         YAHOO.Bubbling.fire("selectedFilesChanged");
      }, '.alf-select input', this);
      
      YAHOO.Bubbling.on("selectedFilesChanged", function(layer, args) {
         this.onSelectedFilesChanged(scope);
      }, this);
      
      
      this.galleryColumns = Alfresco.util.findValueByDotNotation(scope.services.preferences.get(), PREF_GALLERY_COLUMNS);
      if (!this.galleryColumns) 
      {
         this.galleryColumns = DEFAULT_GALLERY_COLUMNS;
      }
      
      this.setupGalleryColumnsSlider(scope, this);
   };
   
   /**
    * Override the standard dataTable since calls in Alfresco.DnD which are closely tied to it
    * at the moment.
    * TODO - Remove once https://issues.alfresco.com/jira/browse/ALF-15384 is implemented
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.overrideDataTable = function DL_GVR_overrideDataTable(scope)
   {
      var viewRendererInstance = this;
      if (scope.widgets.dataTable)
      {
         if (scope.widgets.dataTable.getRecord != scope.widgets.dataTable.getHiddenRecord)
         {
            // Save the normal getRecord method
            scope.widgets.dataTable.getVisibleRecord = scope.widgets.dataTable.getRecord;
            // Define a new method which also checks for hidden elements
            scope.widgets.dataTable.getHiddenRecord = function getHiddenRecord(row)
            {
               var oRecord = scope.widgets.dataTable.getVisibleRecord(row);
               if (oRecord == null)
               {
                  if (typeof row === "string")
                  {
                     oRecord = scope.widgets.dataTable.getVisibleRecord(row + '-hidden');
                  }
                  else if (row)
                  {
                     oRecord = scope.widgets.dataTable.getVisibleRecord(row.id + '-hidden');
                  }
               }
               return oRecord;
            }
            // Replace the getRecord function
            scope.widgets.dataTable.getRecord = scope.widgets.dataTable.getHiddenRecord;
         }
         if (scope.widgets.dataTable.getContainerEl != scope.widgets.dataTable.getGalleryContainerEl)
         {
            // Save the normal getContainerEl method
            scope.widgets.dataTable.origGetContainerEl = scope.widgets.dataTable.getContainerEl;
            // Define a new method which returns the gallery container
            scope.widgets.dataTable.getGalleryContainerEl = function getGalleryContainer()
            {
               return Dom.get(scope.id + viewRendererInstance.parentElementIdSuffix);
            }
            // Replace the getContainerEl method
            scope.widgets.dataTable.getContainerEl = scope.widgets.dataTable.getGalleryContainerEl;
         }
      }
   };
   
   /**
    * Restore the dataTable to its normal getContainerEl function
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.restoreDataTable = function DL_GVR_restoreDataTable(scope)
   {
      if (scope.widgets.dataTable.getContainerEl == scope.widgets.dataTable.getGalleryContainerEl)
      {
         // Restore the getContainerEl method
         scope.widgets.dataTable.getContainerEl = scope.widgets.dataTable.origGetContainerEl;
      }
   };
   
   /**
    * Render the gallery view using the existing dataTable
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.renderView = function DL_GVR_renderView(scope, sRequest, oResponse, oPayload)
   {
      this.overrideDataTable(scope);
      
      var viewRendererInstance = this;
      
      // Call the dataTable render to setup the dataTable.recordSet
      scope.widgets.dataTable.onDataReturnInitializeTable.call(
            scope.widgets.dataTable, sRequest, oResponse, oPayload);
      
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      var oRecordSet = scope.widgets.dataTable.getRecordSet();
      
      if (oRecordSet.getLength() == 0)
      {
         // No records, display the empty container and exit
         var emptyContainer = Dom.get(scope.id + this.parentElementEmptytIdSuffix);
         container.innerHTML = emptyContainer.innerHTML;
         Dom.getFirstChild(emptyContainer).innerHTML = '';
         scope.widgets.dataTable.fireEvent('tableMsgShowEvent', { html: container.innerHTML });
         Dom.removeClass(container, 'alf-gallery');
         Dom.removeClass(container, 'hidden');
         Dom.setStyle(container, 'height', 'auto');
         return;
      }
      
      // Clear the gallery div and hide while we build it
      container.innerHTML = '';
      Dom.setStyle(container, 'opacity', 0);
      Dom.addClass(container, 'alf-gallery');
      Dom.removeClass(container, 'hidden');
      
      var galleryItemTemplate = Dom.get(scope.id + '-gallery-item-template'),
         galleryItem = null;
      
      var oRecord, record, i, j;
      for (i = 0, j = oRecordSet.getLength(); i < j; i++)
      {
         oRecord = oRecordSet.getRecord(i);
         record = oRecord.getData();
         
         // Append a gallery item div
         var galleryItemId = this.getRowItemId(oRecord);
         galleryItem = galleryItemTemplate.cloneNode(true);
         Dom.removeClass(galleryItem, 'hidden');
         galleryItem.setAttribute('id', galleryItemId);
         container.appendChild(galleryItem);
         
         var galleryItemThumbnailDiv = this.getRowItemThumbnailElement(galleryItem);
         var galleryItemHeaderDiv = this.getRowItemHeaderElement(galleryItem);
         var galleryItemDetailDiv = this.getRowItemDetailElement(galleryItem);
         var galleryItemActionsDiv = this.getRowItemActionsElement(galleryItem);
         
         // Set the item header id
         galleryItemHeaderDiv.setAttribute('id', scope.id + '-item-header-' + oRecord.getId());
         
         // Suffix of the content actions div id must match the onEventHighlightRow target id
         galleryItemActionsDiv.setAttribute('id', scope.id + '-actions-' + galleryItemId);
         
         // Render the thumbnail within the gallery item
         this.renderCellThumbnail(
               scope,
               galleryItemThumbnailDiv, 
               oRecord, 
               galleryItem, 
               null,
               '');
         
         // Add the drag and drop
         var imgId = record.jsNode.nodeRef.nodeRef;
         var dnd = new Alfresco.DnD(imgId, scope);
         
         // Create a YUI Panel with a relative context of its associated galleryItem
         galleryItemDetailDiv.panel = new YAHOO.widget.Panel(galleryItemDetailDiv,
         { 
            visible:false, draggable:false, close:false, constraintoviewport: true, 
            underlay: 'none', width: DETAIL_PANEL_WIDTH,
            context: [galleryItem, 'tl', 'tl', [this.galleryColumnsChangedEvent], DETAIL_PANEL_OFFSET]
         });
      };
      
      scope.widgets.galleryColumnsSlider.initialize();
      
      this.currentResizeCallback = function(e)
      {
         viewRendererInstance.resizeView(scope, sRequest, oResponse, oPayload);
      };
      this.setupWindowResizeListener();
   };
   
   /**
    * Clear the gallery view container
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.destroyView = function DL_GVR_destroyView(scope, sRequest, oResponse, oPayload)
   {
      this.restoreDataTable(scope);
      Dom.get(scope.id + this.parentElementIdSuffix).innerHTML = '';
      Dom.addClass(Dom.get(scope.id + this.parentElementIdSuffix), 'hidden');
      Dom.addClass(Dom.get(scope.id + "-gallery-slider"), 'hidden');
      // Clear the columns class on drag and drop
      var dndContainer = Dom.get(DND_CONTAINER_ID);
      columnOptions = [3, 4, 7, 10];
      for ( var i = 0; i < columnOptions.length; i++) {
         Dom.removeClass(dndContainer, 'alf-gallery-columns-' + columnOptions[i]);
      }
      if (this.windowResizeCallback)
      {
         Event.removeListener(window, 'resize', this.windowResizeCallback);
      }
   };
   
   /**
    * Handler for a click on a thumbnail once in select mode
    *
    * @method onSelectModeImgClicked
    * @param event {Event} The click event
    * @param matchedEl {HTMLElement} the element that was clicked
    * @param container {HTMLElement} the parent container (the entire gallery)
    */
   Alfresco.DocumentListGalleryViewRenderer.onSelectModeImgClicked = function DL_GVR_onSelectModeImgClicked(event, matchedEl, container, scope)
   {
       var galleryItem = Dom.getAncestorByClassName(matchedEl, scope.rowClassName);
       var selectDiv = Dom.getElementsByClassName('alf-select', 'div', galleryItem)[0];
       var selectCheckbox = Dom.getFirstChild(selectDiv);
       selectCheckbox.checked = !selectCheckbox.checked;
       scope.documentList.selectedFiles[selectCheckbox.value] = selectCheckbox.checked;
       YAHOO.Bubbling.fire("selectedFilesChanged");
   };
   
   /**
    * Handler for mouse over a gallery item
    *
    * @method onMouseOverItem
    * @param event {Event} The click event
    * @param matchedEl {HTMLElement} the element that was clicked
    * @param container {Object} the parent container (the entire gallery)
    * @param viewRendererInstance {Object} the view renderer instance
    */
   Alfresco.DocumentListGalleryViewRenderer.onMouseOverItem = function DL_GVR_onMouseOverItem(event, matchedEl, container, viewRendererInstance)
   {
      Dom.addClass(matchedEl, 'alf-hover');
      viewRendererInstance.onEventHighlightRow(viewRendererInstance.documentList, event, matchedEl);
   };
   
   /**
    * Handler for mouse out of a gallery item
    *
    * @method onMouseOutItem
    * @param event {Event} The click event
    * @param matchedEl {HTMLElement} the element that was clicked
    * @param container {Object} the parent container (the entire gallery)
    * @param viewRendererInstance {Object} the view renderer instance
    */
   Alfresco.DocumentListGalleryViewRenderer.onMouseOutItem = function DL_GVR_onMouseOutItem(event, matchedEl, container, viewRendererInstance)
   {
      Dom.removeClass(matchedEl, 'alf-hover');
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.onActionShowMore = function DL_GVR_onActionShowMore(scope, record, elMore)
   {
      Alfresco.DocumentListGalleryViewRenderer.superclass.onActionShowMore.call(this, scope, record, elMore);
      var fnHideMoreActions = function DL_GVR_fnHideMoreActions()
      {
         if (scope.hideMoreActionsFn)
         {
            scope.hideMoreActionsFn.call(this);
         }
      };
      var elMoreActions = Dom.getNextSibling(elMore);
      Event.on(elMoreActions, "mouseleave", fnHideMoreActions, elMoreActions);
   };
   
   /**
    * Handler for selection of a gallery items
    *
    * @method onSelectedFilesChanged
    * @param scope {object} The DocumentList object
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.onSelectedFilesChanged = function DL_GVR_onSelectedFilesChanged(scope)
   {
      // Set all selected decorators
      var oRecordSet = scope.widgets.dataTable.getRecordSet(), oRecord, record, jsNode, nodeRef, galleryItem, i, j;
      var anySelected = false;
      for (i = 0, j = oRecordSet.getLength(); i < j; i++)
      {
         oRecord = oRecordSet.getRecord(i);
         jsNode = oRecord.getData("jsNode");
         nodeRef = jsNode.nodeRef;
         galleryItem = this.getRowElementFromDataTableRecord(scope, oRecord);
         isChecked = scope.selectedFiles[nodeRef];
         if (isChecked)
         {
            Dom.addClass(galleryItem, 'alf-selected');
            anySelected = true;
         }
         else
         {
            Dom.removeClass(galleryItem, 'alf-selected');
         }
      }
      
      // If any have been selected add indicator to the parent container
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      if (anySelected)
      {
         if (!Dom.hasClass(container, 'alf-selected'))
         {
            Dom.addClass(container, 'alf-selected');
         }
      }
      else
      {
         Dom.removeClass(container, 'alf-selected');
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.renderCellSelected = function DL_GVR_renderCellSelected(scope, elCell, oRecord, oColumn, oData)
   {
      var galleryItem = this.getRowItem(oRecord, elCell);
      // Check for null galleryItem due to ALF-15529
      if (galleryItem != null)
      {
         var galleryItemSelectDiv = this.getRowItemSelectElement(galleryItem);
         var jsNode = oRecord.getData("jsNode"),
             nodeRef = jsNode.nodeRef,
             name = oRecord.getData("displayName"),
             checkbox = document.createElement("input"),
             label = document.createElement("label");
         
         var checkboxId = this.getRowItemSelectId(oRecord);
         
         checkbox.id = checkboxId;
         checkbox.type = "checkbox";
         checkbox.name = "fileChecked";
         checkbox.value = nodeRef;
         checkbox.checked = scope.selectedFiles[nodeRef] ? true : false;
         
         label.id = "label_for_" + checkbox.id;
         label.style.fontSize="0em";
         label.innerHTML = (checkbox.checked ? scope.msg("checkbox.uncheck") : scope.msg("checkbox.check")) + " " + name;
         label.setAttribute("for", checkbox.id);
         galleryItemSelectDiv.innerHTML = '';
         galleryItemSelectDiv.appendChild(label);
         galleryItemSelectDiv.appendChild(checkbox);
         YAHOO.Bubbling.on("selectedFilesChanged", function(e)
         {
            if (Dom.get(label.id))
            {
              Dom.get(label.id).innerHTML = (scope.selectedFiles[nodeRef] ? scope.msg("checkbox.uncheck") : scope.msg("checkbox.check")) + " " + name;
            }
         });
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.getThumbnail = function DL_GVR_getThumbnail(scope, elCell, oRecord, oColumn, oData, imgIdSuffix, renditionName)
   {
      if (imgIdSuffix == null)
      {
         imgIdSuffix = "-hidden";
      }
      if (renditionName == null)
      {
         renditionName = "imgpreview";
      }
      
      var record = oRecord.getData(),
         node = record.jsNode,
         properties = node.properties,
         name = record.displayName,
         isContainer = node.isContainer,
         isLink = node.isLink,
         extn = name.substring(name.lastIndexOf(".")),
         imgId = node.nodeRef.nodeRef + imgIdSuffix, // DD added
         imgHtml;
      
      if (isContainer)
      {
         imgHtml = '<img id="' + imgId + '" class="alf-gallery-item-thumbnail-img" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-256.png" />';
      }
      else
      {
         imgHtml = '<img id="' + imgId + '" class="alf-gallery-item-thumbnail-img" src="' + Alfresco.DocumentList.generateThumbnailUrl(record, renditionName) + '" alt="' + $html(extn) + '" title="' + $html(name) + '" />';
      }
      return { id: imgId, html: imgHtml, isContainer: isContainer, isLink: isLink };
   };
   
   /**
    * Render a thumbnail for a given oRecord
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.renderCellThumbnail = function DL_GVR_renderCellThumbnail(scope, elCell, oRecord, oColumn, oData, imgIdSuffix, renditionName)
   {
      var containerTarget; // This will only get set if thumbnail represents a container
      
      var thumbnail = this.getThumbnail(scope, elCell, oRecord, oColumn, oData, imgIdSuffix, renditionName);
      var record = oRecord.getData();

      // Just add the data table thumbnail once
      if (!document.getElementById(thumbnail.id))
      {
         if (thumbnail.isContainer)
         {
            elCell.innerHTML += '<span class="folder">' + (thumbnail.isLink ? '<span class="link"></span>' : '') + 
                  (scope.dragAndDropEnabled ? '<span class="droppable"></span>' : '') + 
                  Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + thumbnail.html + '</a>';
            containerTarget = new YAHOO.util.DDTarget(thumbnail.id); // Make the folder a target
         }
         else
         {
            elCell.innerHTML += (thumbnail.isLink ? '<span class="link"></span>' : '') + 
                  Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + thumbnail.html + '</a>';
         }
         var thumbnailElement = document.getElementById(thumbnail.id);
         if (thumbnailElement)
         {
            var tempImg = new Image();
            tempImg.onload = function()
            {
               if(tempImg.width > tempImg.height)
               {
                  Dom.addClass(thumbnailElement, 'alf-landscape');
               }
            }
            tempImg.src = thumbnailElement.src;
         }
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.renderCellStatus = function DL_GVR_renderCellStatus(scope, elCell, oRecord, oColumn, oData)
   {
      Alfresco.DocumentListGalleryViewRenderer.superclass.renderCellStatus.call(this, scope, elCell, oRecord, oColumn, oData);
      var galleryItem = this.getRowItem(oRecord, elCell);
      // Check for null galleryItem due to ALF-15529
      if (galleryItem != null)
      {
         // Copy status
         var galleryItemStatusElement = this.getRowItemStatusElement(galleryItem).innerHTML = elCell.innerHTML;
         // Clear out the table cell so there's no conflicting HTML IDs
         elCell.innerHTML = '';
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.renderCellDescription = function DL_GVR_renderCellDescription(scope, elCell, oRecord, oColumn, oData)
   {
      Alfresco.DocumentListGalleryViewRenderer.superclass.renderCellDescription.call(this, scope, elCell, oRecord, oColumn, oData);
      var galleryItem = this.getRowItem(oRecord, elCell);
      // Check for null galleryItem due to ALF-15529
      if (galleryItem != null)
      {
         // Copy description
         var galleryItemDetailDescriptionElement = this.getRowItemDetailDescriptionElement(galleryItem).innerHTML = elCell.innerHTML;
         // Clear out the table cell so there's no conflicting HTML IDs
         elCell.innerHTML = '';
         // Add a simple display label
         this.getRowItemLabelElement(galleryItem).innerHTML = 
            Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord.getData()) + $html(oRecord.getData().displayName) + '</a>';
         var galleryItemDetailThumbnailElement = this.getRowItemDetailThumbnailElement(galleryItem);
         // Only set panel thumbnail if it's currently empty
         if (galleryItemDetailThumbnailElement.innerHTML == '')
         {
            var thumbnail = this.getThumbnail(scope, elCell, oRecord, oColumn, oData, '-detail');
            this.getRowItemDetailThumbnailElement(galleryItem).innerHTML = thumbnail.html;
         }
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype.onFileRenamed = function DL_GVR_onFileRenamed(scope, layer, args)
   {
      var obj = args[1];
      if (obj && (obj.file !== null))
      {
         var recordFound = scope._findRecordByParameter(obj.file.node.nodeRef, "nodeRef");
         if (recordFound !== null)
         {
            scope.widgets.dataTable.updateRow(recordFound, obj.file);
            var galleryItemId = this.getRowItemId(recordFound);
            Alfresco.util.Anim.pulse(galleryItemId);
         }
      }
   };
   
   /**
    * Does the work of actually resizing the view after resize events have stopped.  Extensions
    * will usually override this method rather than onWindowResize or checkWindowResizeTime.
    *
    * @method onWindowResize
    * @param scope {object} The DocumentList object
    * @param sRequest {string} Original request
    * @param oResponse {object} Response object
    * @param oPayload {MIXED} (optional) Additional argument(s)
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.resizeView = function DL_GVR_resizeView(scope, sRequest, oResponse, oPayload)
   {
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      var numColumns = scope.widgets.galleryColumnsSlider.getColumnValue();
      scope.widgets.galleryColumnsSlider.setGalleryViewColumns(container, numColumns, ANIMATE_ON_WINDOW_RESIZE);
   }
   
   /**
    * Timer check of resize to make sure resize has stopped, i.e. windowResizeMinTime has elapsed
    *
    * @method checkWindowResizeTime
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.checkWindowResizeTime = function DL_GVR_checkWindowResizeTime()
   {
      var viewRendererInstance = this;
      var now = Date.now();
      if (now - this.lastResize < viewRendererInstance.windowResizeMinTime) {
          this.resizeTimer = setTimeout(function()
          {
             viewRendererInstance.checkWindowResizeTime()
          }, viewRendererInstance.windowResizeCheckTime);
      } else {
          clearTimeout(this.resizeTimer);
          this.resizeTimer = this.lastResize = 0;
          // Only call resize if the width has actually changed
          if (this.currentViewportWidth != Dom.getViewportWidth())
          {
             this.currentResizeCallback();
             this.currentViewportWidth = Dom.getViewportWidth();
          }
      }
   };
   
   /**
    * Handler for window resize.  Uses a timer to only fire on stop of resize.
    *
    * @method onWindowResize
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.onWindowResize = function DL_GVR_onWindowResize()
   {
      var viewRendererInstance = this;
      this.lastResize = Date.now();
      this.resizeTimer = this.resizeTimer || 
         setTimeout(function()
         {
            viewRendererInstance.checkWindowResizeTime()
         }, viewRendererInstance.windowResizeCheckTime);
   };
   
   /**
    * Sets up the event listener for window resizing
    *
    * @method setupWindowResizeListener
    */
   Alfresco.DocumentListGalleryViewRenderer.prototype.setupWindowResizeListener = function DL_GVR_setupWindowResizeListener()
   {
      var isIOS = ( navigator.userAgent.match(/(iPad|iPhone|iPod)/i) ? true : false );
      if (!isIOS)
      {
         var viewRendererInstance = this;
         this.currentViewportWidth = Dom.getViewportWidth();
         if (viewRendererInstance.windowResizeCallback)
         {
            Event.removeListener(window, 'resize', viewRendererInstance.windowResizeCallback);
         }
         viewRendererInstance.windowResizeCallback = function(e)
         {
            viewRendererInstance.onWindowResize();
         };
         Event.addListener(window, "resize", viewRendererInstance.windowResizeCallback);
      }
   };
   
   Alfresco.DocumentListGalleryViewRenderer.prototype._setEmptyDataSourceMessage = function DL_GVR_setEmptyDataSourceMessage(scope, messageHtml)
   {
      var emptyContainer = Dom.get(scope.id + this.parentElementEmptytIdSuffix);
      Dom.getFirstChild(emptyContainer).innerHTML = messageHtml;
   }

   // Add some methods for the gallery columns slider
   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentListGalleryViewRenderer.prototype,
   {
      /**
       * Setup the number of columns slider
       *
       * @method setupGalleryColumnsSlider
       * @param scope {object} The DocumentList object
       * @param galleryViewRenderer {object} The DocumentListViewRenderer
       */
      setupGalleryColumnsSlider: function DL_GVR_setupGalleryColumnsSlider(scope, galleryViewRenderer)
      {
         var container = Dom.get(scope.id + this.parentElementIdSuffix);
         
         scope.widgets.galleryColumnsSlider = YAHOO.widget.Slider.getHorizSlider(
               scope.id + "-gallery-slider-bg", scope.id + "-gallery-slider-thumb", 0, 60, 20);
         
         scope.widgets.galleryColumnsSlider.animate = true;
         
         scope.widgets.galleryColumnsSlider.subscribe("change", function(offsetFromStart)
         {
            var numColumns = scope.widgets.galleryColumnsSlider.getColumnValue();
            scope.widgets.galleryColumnsSlider.setGalleryViewColumns(container, numColumns, true);
            scope.widgets.galleryColumnsSlider.setColumnsPreference(numColumns);
         });
         
         // Additional slider methods
         
         scope.widgets.galleryColumnsSlider.initialize = function()
         {
            Dom.removeClass(Dom.get(scope.id + "-gallery-slider"), 'hidden');
            var initialPixelValue = scope.widgets.galleryColumnsSlider.getPixelValue(galleryViewRenderer.galleryColumns);
            scope.widgets.galleryColumnsSlider.setValue(initialPixelValue, true, true, true);
            scope.widgets.galleryColumnsSlider.setGalleryItemDimensions(container, galleryViewRenderer.galleryColumns, true);
         };

         scope.widgets.galleryColumnsSlider.getColumnValue = function()
         { 
            switch(scope.widgets.galleryColumnsSlider.getValue())
            {
            case 0:
              return 10;
            case 20:
              return 7;
            case 40:
               return 4;
            case 60:
               return 3;
            }
         };
         scope.widgets.galleryColumnsSlider.getPixelValue = function(realValue)
         { 
            switch(realValue)
            {
            case 10:
               return 0;
            case 7:
               return 20;
            case 4:
               return 40;
            case 3:
               return 60;
            }
         };
         
         scope.widgets.galleryColumnsSlider.setGalleryItemDimensions = function _setGalleryItemDimensions(container, numColumns, animate)
         {
            if (numColumns == null) {
               numColumns = scope.widgets.galleryColumnsSlider.getColumnValue();
            }
            
            // Set calculated gallery container height
            var numItems = scope.widgets.dataTable.getRecordSet().getLength();
            var numRows = Math.ceil(numItems / numColumns);
            var galleryWidth = parseInt(Dom.getComputedStyle(container, 'width'));
            var galleryItemWidth = Math.floor(galleryWidth / numColumns) * 0.92; // account for margins
            var galleryHeight = galleryItemWidth * numRows * 1.13;
            
            Dom.setStyle(container, 'opacity', 0);
            Dom.setStyle(container, 'height', galleryHeight + 'px');
            
            // Clear then add the corresponding columns class to container and drag and drop
            var dndContainer = Dom.get(DND_CONTAINER_ID);
            columnOptions = [3, 4, 7, 10];
            for ( var i = 0; i < columnOptions.length; i++) {
               Dom.removeClass(container, 'alf-gallery-columns-' + columnOptions[i]);
               Dom.removeClass(dndContainer, 'alf-gallery-columns-' + columnOptions[i]);
            }
            Dom.addClass(container, 'alf-gallery-columns-' + numColumns);
            Dom.addClass(dndContainer, 'alf-gallery-columns-' + numColumns);
            
            // Set gallery items' height to calculated width for square
            var galleryItems = Dom.getChildren(container);
            Dom.batch(galleryItems, function(el) { Dom.setStyle(el, 'height', galleryItemWidth + 'px'); });
            
            if (animate)
            {
               if (!scope.widgets.galleryColumnsSlider.fadeInAnimation)
               {   
                  scope.widgets.galleryColumnsSlider.fadeInAnimation = new YAHOO.util.Anim(
                     container, { opacity: {from: 0, to: 1 } }, 0.4, YAHOO.util.Easing.easeOut);
               }
               scope.widgets.galleryColumnsSlider.fadeInAnimation.animate();
            }
            else
            {
               Dom.setStyle(container, 'opacity', 1);
            }
         }

         scope.widgets.galleryColumnsSlider.setGalleryViewColumns = function _setGalleryViewColumns(container, numColumns, animate)
         {
            scope.widgets.galleryColumnsSlider.setGalleryItemDimensions(container, numColumns, animate);
            YAHOO.Bubbling.fire("galleryViewColumnsChanged");
         };
         
         scope.widgets.galleryColumnsSlider.setColumnsPreference = function _setColumnsPreference(numColumns)
         {
            if (numColumns == null) {
               numColumns = scope.widgets.galleryColumnsSlider.getColumnValue();
            }
            galleryViewRenderer.galleryColumns = numColumns;
            scope.services.preferences.set(PREF_GALLERY_COLUMNS, numColumns);
         };
      },
      
      onShowGalleryItemDetail: function DL_GVR_onShowGalleryItemDetail(scope, galleryViewRenderer, event, matchedEl, container)
      {
         var galleryItem = Dom.getAncestorByClassName(matchedEl, galleryViewRenderer.rowClassName);
         var galleryItemDetailDiv = Dom.getElementsByClassName(galleryViewRenderer.infoPanelClassName, null, galleryItem)[0];
         
         Dom.setStyle(galleryItemDetailDiv, 'display', '');

          // MNT-10678 fix. Set the heigher z-index for galleryItemDetailDiv panel.
          // Make the panel in front of other elements. I.e. in filmstrip view make galleryItemDetailDiv panel in front
          // of the 'Nex' navigation button.
         var panelOldZIndex = galleryItemDetailDiv.panel.cfg.getProperty('zIndex'),
             panelNewZIndex = 3;
         galleryItemDetailDiv.panel.cfg.setProperty("zIndex", panelNewZIndex);

         galleryItemDetailDiv.panel.render();
         galleryItemDetailDiv.panel.show(galleryItemDetailDiv.panel);
         
         // Hide pop-up timer function
         var fnHideDetailPanel = function DL_GVR_fnHideDetailPanel()
         {
            galleryViewRenderer.onEventUnhighlightRow(scope, event, galleryItem);
            galleryItemDetailDiv.panel.hide(galleryItemDetailDiv.panel);
            Dom.setStyle(galleryItemDetailDiv, 'display', 'none');

            // set the previous z-index for galleryItemDetailDiv panel.
            galleryItemDetailDiv.panel.cfg.setProperty("zIndex", panelOldZIndex);
         };
         
         // Initial after-click hide timer - 4x the mouseOut timer delay
         if (galleryItemDetailDiv.hideTimerId)
         {
            window.clearTimeout(galleryItemDetailDiv.hideTimerId);
         }
         galleryItemDetailDiv.hideTimerId = window.setTimeout(fnHideDetailPanel, galleryViewRenderer.infoPanelPopupTimeout * 4);
         
         // Mouse over handler
         var onMouseOver = function DL_GVR__onMouseOver(e, obj)
         {
            // Clear any existing hide timer
            if (obj.hideTimerId)
            {
               window.clearTimeout(obj.hideTimerId);
               obj.hideTimerId = null;
            }
         };
         
         // Mouse out handler
         var onMouseOut = function DLSM_onMouseOut(e, obj)
         {
            var elTarget = Event.getTarget(e);
            var related = elTarget.relatedTarget;

            // In some cases we should ignore this mouseout event
            if ((related !== obj) && (!Dom.isAncestor(obj, related)))
            {
               if (obj.hideTimerId)
               {
                  window.clearTimeout(obj.hideTimerId);
               }
               obj.hideTimerId = window.setTimeout(fnHideDetailPanel, galleryViewRenderer.infoPanelPopupTimeout / 100);
            }
         };
         
         Event.on(galleryItemDetailDiv, "mouseover", onMouseOver, galleryItemDetailDiv);
         Event.on(galleryItemDetailDiv, "mouseout", onMouseOut, galleryItemDetailDiv);
      }
      
   });
   
})();

