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
 * Filmstrip view extension of DocumentListViewRenderer component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentListFilmstripViewRenderer
 * @extends Alfresco.DocumentListGalleryViewRenderer
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
   
   var DND_CONTAINER_ID = 'ygddfdiv',
      DETAIL_PANEL_WIDTH = '377px',
      DETAIL_PANEL_OFFSET = [-42, 25],
      NAV_ITEM_WIDTH = 120,
      NAV_ITEM_PADDING_X = 4,
      NAV_PADDING_X = 52,
      NAV_HEIGHT = 112,
      HEADER_HEIGHT = 50,
      SHARE_DOCLIB_HEADER_FOOTER_HEIGHT = 315,
      FILMSTRIP_WINDOW_RESIZE_CHECK_TIME = 50,
      FILMSTRIP_WINDOW_RESIZE_MIN_TIME = 200,
      CAROUSEL_CONTENT_MIN_HEIGHT = 372,
      WEB_PREVIEWER_HEIGHT,
       itemHeaderHeight,
      MIMETYPE_PREFIX_IMAGE = "image/";
   
   /**
    * FilmstripViewRenderer constructor.
    *
    * @param name {String} The name of the FilmstripViewRenderer
    * @return {Alfresco.DocumentListFilmstripViewRenderer} The new FilmstripViewRenderer instance
    * @constructor
    */
   Alfresco.DocumentListFilmstripViewRenderer = function(name, parentDocumentList)
   {
      Alfresco.DocumentListFilmstripViewRenderer.superclass.constructor.call(this, name, parentDocumentList);
      this.parentElementIdSuffix = "-filmstrip";
      this.windowResizeCheckTime = FILMSTRIP_WINDOW_RESIZE_CHECK_TIME;
      this.windowResizeMinTime = FILMSTRIP_WINDOW_RESIZE_MIN_TIME;
      return this;
   };

   /**
    * Extend from Alfresco.DocumentListViewRenderer
    */
   YAHOO.extend(Alfresco.DocumentListFilmstripViewRenderer, Alfresco.DocumentListGalleryViewRenderer);
   
   /**
    * Generates a filmstrip item nav id from the given dataTable record
    *
    * @method getFilmstripNavItemId
    * @param scope {object} The DocumentList object
    * @param oRecord {object} data table record
    * @return {string} the filmstrip nav item id
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripNavItemId = function DL_FVR_getFilmstripNavItemId(oRecord)
   {
      if (this.documentList != null && oRecord != null)
      {
         return this.documentList.id + '-filmstrip-nav-item-' + oRecord.getId();
      }
   };
   
   /**
    * Gets an existing filmstrip item nav from the given dataTable record
    *
    * @method getFilmstripNavItem
    * @param scope {object} The DocumentList object
    * @param oRecord {object} data table record
    * @param elCell {HTMLElement} the data table cell asking for the gallery item (optional)
    * @return {object} the filmstrip nav item
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripNavItem = function DL_FVR_getFilmstripNavItem(oRecord, elCell)
   {
      if (this.documentList != null && oRecord != null)
      {
         var filmstripNavItemId = this.getFilmstripNavItemId(oRecord);
         var filmstripNavItem = Dom.get(filmstripNavItemId);
         if (filmstripNavItem === null && elCell != null)
         {
            // AJAX call must have updated the table, change our ID as well
            var rowElement = Dom.getAncestorByTagName(elCell, 'tr');
            var oldFilmstripNavItemId = this.documentList.id + '-filmstrip-nav-item-' + rowElement.id;
            filmstripNavItem = document.getElementById(oldFilmstripNavItemId);
            if (filmstripNavItem !== null)
            {
               filmstripNavItem.setAttribute('id', filmstripNavItemId);
            }
         }
         return filmstripNavItem;
      }
   };
   
   /**
    * Gets the filmstrip nav item's thumbnail element from the given filmstrip nav item
    *
    * @method getFilmstripNavItemThumbnailElement
    * @param filmstripNavItem {HTMLElement} The filmstrip nav item object
    * @return {HTMLElement} the filmstrip nav item thumbnail element
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripNavItemThumbnailElement = function DL_FVR_getFilmstripNavItemThumbnailElement(filmstripNavItem)
   {
      if (filmstripNavItem != null)
      {
         return Dom.getFirstChild(filmstripNavItem);
      }
   };
   
   /**
    * Gets the filmstrip nav item's label element from the given filmstrip nav item
    *
    * @method getFilmstripNavItemLabelElement
    * @param filmstripNavItem {HTMLElement} The filmstrip nav item object
    * @return {HTMLElement} the filmstrip nav item label element
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripNavItemLabelElement = function DL_FVR_getFilmstripNavItemLabelElement(filmstripNavItem)
   {
      if (filmstripNavItem != null)
      {
         var filmstripNavItemThumbnailDiv = this.getFilmstripNavItemThumbnailElement(filmstripNavItem);
         return Dom.getChildren(filmstripNavItemThumbnailDiv)[0];
      }
   };
   
   /**
    * Gets the filmstrip carousel element ID
    *
    * @method getFilmstripCarouselContainerId
    * @param scope {Object} The document list object
    * @return {String} the filmstrip carousel element ID
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripCarouselContainerId = function DL_FVR_getFilmstripCarouselContainerId(scope)
   {
      if (scope != null)
      {
         return scope.id + '-filmstrip-carousel';
      }
   };
   
   /**
    * Gets the filmstrip nav element
    *
    * @method getFilmstripNavElement
    * @param scope {Object} The document list object
    * @return {HTMLElement} the filmstrip nav element
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripNavElement = function DL_FVR_getFilmstripNavElement(scope)
   {
      if (scope != null)
      {
         return Dom.get(scope.id + '-filmstrip-nav');
      }
   };
   
   /**
    * Gets the filmstrip nav carousel element ID
    *
    * @method getFilmstripNavCarouselContainerId
    * @param scope {Object} The document list object
    * @return {String} the filmstrip nav carousel element ID
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getFilmstripNavCarouselContainerId = function DL_FVR_getFilmstripNavCarouselContainerId(scope)
   {
      if (scope != null)
      {
         return scope.id + '-filmstrip-nav-carousel';
      }
   };
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.getRowItemLabelElement
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getRowItemLabelElement = function DL_FVR_getRowItemLabelElement(rowItem)
   {
      if (rowItem != null)
      {
         var filmstripItemHeaderDiv = this.getRowItemHeaderElement(rowItem);
         return Dom.getChildren(filmstripItemHeaderDiv)[2];
      }
   };
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.getRowItemActionsElement
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getRowItemActionsElement = function DL_FVR_getRowItemActionsElement(rowItem)
   {
      if (rowItem)
      {
         var galleryItemDetailDiv = this.getRowItemDetailElement(rowItem);
         return Dom.getChildren(Dom.getFirstChild(galleryItemDetailDiv))[1];
      }
   };
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.getRowItemStatusElement
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getRowItemStatusElement = function DL_FVR_getRowItemStatusElement(rowItem)
   {
      if (rowItem != null)
      {
         var galleryItemDetailDiv = this.getRowItemDetailElement(rowItem);
         return Dom.getChildren(Dom.getFirstChild(galleryItemDetailDiv))[0];
      }
   };
   
   /**
    * Gets the filmstrip item's info button element from the given filmstrip item
    *
    * @method getRowItemInfoButtonElement
    * @param filmstripItem {HTMLElement} The filmstrip item object
    * @return {HTMLElement} the filmstrip item info button element
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.getRowItemInfoButtonElement = function DL_FVR_getRowItemInfoButtonElement(rowItem)
   {
      if (rowItem != null)
      {
         var filmstripItemHeaderDiv = this.getRowItemHeaderElement(rowItem);
         return Dom.getChildren(filmstripItemHeaderDiv)[1];
      }
   };
   
   /**
    * Handler for click of a filmstrip content nav item
    *
    * @method onClickFilmstripContentNavItem
    * @param scope {Object} The document list object
    * @param index {Number} The index of the nav item
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.onClickFilmstripContentNavItem = function DL_FVR_onClickFilmstripContentNavItem(scope, index)
   {
      if (scope != null && index != null)
      {
         scope.widgets.filmstripCarousel.scrollTo(index, false);
      }
   };
   
   /**
    * Handler for new filmstrip main content item
    *
    * @method onFilmstripMainContentChanged
    * @param scope {Object} The document list object
    * @param index {Number} The index of the nav item
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.onFilmstripMainContentChanged = function DL_FVR_onFilmstripMainContentChanged(scope, index)
   {
      if (scope != null)
      {
         this.destroyWebPreview(scope, index);
         this.renderWebPreview(scope, index);
         var pageNum = scope.widgets.filmstripNavCarousel.getPageForItem(index) - 1;
         var currentPage = scope.widgets.filmstripNavCarousel.get('currentPage');
         if (pageNum != currentPage)
         {
            var firstOnPage = scope.widgets.filmstripNavCarousel.getFirstVisibleOnPage(pageNum + 1);
            scope.widgets.filmstripNavCarousel.scrollTo(firstOnPage, true);
         }
      }
   };
   
   /**
    * Handler for hiding header and content nav elements
    *
    * @method onToggleHeaderAndNav
    * @param scope {Object} The document list object
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.onToggleHeaderAndNav = function DL_FVR_onToggleHeaderAndNav(scope)
   {
      if (scope != null)
      {
         var viewRendererInstance = this;
         var container = Dom.get(scope.id + this.parentElementIdSuffix);
         var currentPage = scope.widgets.filmstripCarousel.get('currentPage');
         var filmstripItem = Dom.getFirstChild(scope.widgets.filmstripCarousel.getElementForItem(currentPage));
         var header = this.getRowItemHeaderElement(filmstripItem);
         var filmstripNav = this.getFilmstripNavElement(scope);
         var headerAnim, navAnim;
         if (!Dom.hasClass(container, 'alf-filmstrip-content-only'))
         {
            navAnim = new Anim(filmstripNav, { bottom: {to: -NAV_HEIGHT} }, 0.2);
            headerAnim = new Anim(header, { top: {to: -HEADER_HEIGHT} }, 0.2);
            headerAnim.onComplete.subscribe(function() { 
               Dom.addClass(container, 'alf-filmstrip-content-only');
            });
         }
         else
         {
            Dom.setStyle(header, 'top', '-' + HEADER_HEIGHT + 'px');
            Dom.removeClass(container, 'alf-filmstrip-content-only');
            navAnim = new Anim(filmstripNav, { bottom: {to: 0} }, 0.2);
            headerAnim = new Anim(header, { top: {to: 0} }, 0.2);
            headerAnim.onComplete.subscribe(function() { 
               // Manually set all other headers
               var i, j, otherFilmstripItemHeader, otherFilmstripItems = scope.widgets.filmstripCarousel.getElementForItems();
               for (i = 0, j = otherFilmstripItems.length; i < j; i++)
               {
                  otherFilmstripItemHeader = viewRendererInstance.getRowItemHeaderElement(Dom.getFirstChild(otherFilmstripItems[i]));
                  Dom.setStyle(otherFilmstripItemHeader, 'top', '0px');
               }
            });
         }
         headerAnim.animate();
         navAnim.animate();
      }
   };
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.setupRenderer
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.setupRenderer = function DL_FVR_setupRenderer(scope)
   {
      Dom.addClass(scope.id + this.buttonElementIdSuffix, this.buttonCssClass);
      
      this.documentList = scope;
      
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      
      var viewRendererInstance = this;
      
      // TODO slide in and out of header and content nav here
      
      // On mouseover show the select checkbox and detail pull down
      Event.delegate(container, 'mouseover', function DL_GVR_onGalleryItemMouseOver(event, matchedEl, container)
      {
//         Dom.addClass(matchedEl, 'alf-hover');
         viewRendererInstance.onEventHighlightRow(scope, event, matchedEl);
      }, 'div.' + this.rowClassName, this);
      
      // On mouseout hide the select checkbox and detail pull down
//      Event.delegate(container, 'mouseout', function DL_GVR_onGalleryItemMouseOut(event, matchedEl, container)
//      {
//         Dom.removeClass(matchedEl, 'alf-hover');
//      }, 'div.' + this.rowClassName, this);
      
      // On click of detail pull down show detail panel
      Event.delegate(container, 'click', function DL_GVR_infoPopup(event, matchedEl, container)
      {
         viewRendererInstance.onShowGalleryItemDetail(scope, viewRendererInstance, event, matchedEl, container);
      }, '.alf-show-detail', this);
      
      // On click of handle toggle header and nav
      Event.addListener(Dom.get(scope.id + '-filmstrip-nav-handle'), 'click', function(e)
      {
         viewRendererInstance.onToggleHeaderAndNav(scope);
      });
      
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
      YAHOO.Bubbling.on('webPreviewSetupComplete', function(layer, args)
      {
         var galleryItemDivs = Dom.getElementsByClassName('alf-gallery-item', 'div');
         if (galleryItemDivs != null && galleryItemDivs.length > 0)
         {
            var webPreviewDivs = Dom.getElementsByClassName('WebPreviewer', 'div');
            if (webPreviewDivs != null && webPreviewDivs.length > 0)
            {
               var previewHeight = parseInt(galleryItemDivs[0].style.height) - 46;
               webPreviewDivs[0].style.height = previewHeight + 'px';
            }
         }
         var headerHeight = getHeaderHeight();
         setImagePreviewMargin(headerHeight);
      }, this);
   };

   function setImagePreviewMargin(itemHeaderHeight)
   {
      var imagePreviewElements = Dom.getElementsByClassName('previewer Image');

      if (imagePreviewElements.length == 0)
      {
         imagePreviewElements = Dom.getElementsByClassName('previewer WebPreviewer Image');

         if (imagePreviewElements.length > 0)
         {
            imagePreviewElements[0].getElementsByTagName("img")[0].style.marginTop = itemHeaderHeight + "px";
         }
      }
      else
      {
         imagePreviewElements[0].style.marginTop = itemHeaderHeight + "px";
      }
   }

   function renderImagePreview()
   {
      var itemHeaderHeight = getHeaderHeight();
      // MNT-10678 fix. Set CAROUSEL_CONTENT_MIN_HEIGHT according web flash preview height.
      var webPreviewersRealDivs = Dom.getElementsByClassName('real', 'div');

      if (webPreviewersRealDivs.length !== 0)
      {
         // WEB_PREVIEWER_HEIGHT = webPreviewersRealDivs[0].offsetHeight;
         CAROUSEL_CONTENT_MIN_HEIGHT = WEB_PREVIEWER_HEIGHT + itemHeaderHeight;
      }

      setImagePreviewMargin(itemHeaderHeight);
   }

   function getHeaderHeight()
   {
      // Get the div for preview header
      var elements = Dom.getElementsByClassName('alf-header');
      var previewHeaderElement;
      //itemHeaderHeight;
      for (var i = 0; i < elements.length; i++)
      {
         if (elements[i].id.indexOf('-item-header-yui-') > 0)
         {
            previewHeaderElement = elements[i];
            break;
         }
      }
      return previewHeaderElement.offsetHeight;
   }

   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.destroyView
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.destroyView = function DL_GVR_destroyView(scope, sRequest, oResponse, oPayload)
   {
      this.restoreDataTable(scope);
      var filmstripCarousel = Dom.get(this.getFilmstripCarouselContainerId(scope));
      if (filmstripCarousel != null)
      {
         Dom.get(this.getFilmstripCarouselContainerId(scope)).innerHTML = '';
         Dom.get(this.getFilmstripNavCarouselContainerId(scope)).innerHTML = '';
      }
      this.destroyWebPreview(scope);
      Dom.addClass(Dom.get(scope.id + this.parentElementIdSuffix), 'hidden');
      Dom.addClass(Dom.get(scope.id + this.parentElementEmptytIdSuffix), 'hidden');
      var dndContainer = Dom.get(DND_CONTAINER_ID);
      Dom.removeClass(dndContainer, 'alf-filmstrip-dragging');
      if (this.windowResizeCallback)
      {
         Event.removeListener(window, 'resize', this.windowResizeCallback);
      }
   };
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.renderView
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.renderView = function DL_FVR_renderView(scope, sRequest, oResponse, oPayload)
   {
      this.overrideDataTable(scope);
      
      var viewRendererInstance = this;
      
      // Call the dataTable render to setup the dataTable.recordSet
      scope.widgets.dataTable.onDataReturnInitializeTable.call(
            scope.widgets.dataTable, sRequest, oResponse, oPayload);
      
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      var oRecordSet = scope.widgets.dataTable.getRecordSet();
      
      var emptyContainer = Dom.get(scope.id + this.parentElementEmptytIdSuffix);
      if (oRecordSet.getLength() == 0)
      {
         // No records, display the empty container and exit
         Dom.removeClass(emptyContainer, 'hidden');
         Dom.setStyle(emptyContainer, 'height', 'auto');
         return;
      }
      Dom.addClass(emptyContainer, 'hidden');
      
      // Hide the container while we build it
      Dom.setStyle(container, 'opacity', 0);
      
      var filmstripMainContent = Dom.get(scope.id + '-filmstrip-main-content');
      var carouselContainerId = this.getFilmstripCarouselContainerId(scope);
      var carouselContainer = Dom.get(carouselContainerId);
      carouselContainer.innerHTML = '';
      var carouselList = document.createElement('ol');
      carouselContainer.appendChild(carouselList);
      
      var filmstripNav = Dom.get(scope.id + '-filmstrip-nav');
      
      var navCarouselContainerId = this.getFilmstripNavCarouselContainerId(scope);
      var navCarouselContainer = Dom.get(navCarouselContainerId);
      navCarouselContainer.innerHTML = '';
      var navCarouselList = document.createElement('ol');
      navCarouselContainer.appendChild(navCarouselList);
      
      Dom.addClass(container, 'alf-gallery');
      Dom.addClass(container, 'alf-filmstrip');
      Dom.removeClass(container, 'hidden');
      
      var containerWidth = parseInt(Dom.getComputedStyle(container, 'width'));
      // Set a 3:2 aspect ratio if we have room
      var itemHeight = Math.floor((2 / 3) * containerWidth);
      var maxItemHeight = Dom.getViewportHeight() - SHARE_DOCLIB_HEADER_FOOTER_HEIGHT;
      if (Dom.hasClass(Dom.get(scope.id), 'alf-fullscreen'))
      {
         maxItemHeight = Dom.getViewportHeight();
      }
      if ((maxItemHeight >= 0) && (itemHeight > maxItemHeight))
      {
         itemHeight = maxItemHeight;
      }

      var filmstripItemTemplate = Dom.get(scope.id + '-filmstrip-item-template'),
         filmstripNavItemTemplate = Dom.get(scope.id + '-filmstrip-nav-item-template'),
         filmstripItem = null,
         filmstripNavItem = null,
         filmstripListItem = null,
         filmstripNavListItem = null;
      
      var oRecord, record, i, j;
      for (i = 0, j = oRecordSet.getLength(); i < j; i++)
      {
         oRecord = oRecordSet.getRecord(i);
         record = oRecord.getData();
         
         filmstripListItem = document.createElement('li');
         carouselList.appendChild(filmstripListItem);
         
         filmstripNavListItem = document.createElement('li');
         navCarouselList.appendChild(filmstripNavListItem);
         
         // Append a filmstrip item div
         var filmstripItemId = this.getRowItemId(oRecord);
         filmstripItem = filmstripItemTemplate.cloneNode(true);
         Dom.removeClass(filmstripItem, 'hidden');
         filmstripItem.setAttribute('id', filmstripItemId);
         filmstripItem.style.width = containerWidth + 'px';
         filmstripItem.style.height = itemHeight + 'px';
         filmstripListItem.appendChild(filmstripItem);
         
         // Append a filmstrip item nav div
         var filmstripNavItemId = this.getFilmstripNavItemId(oRecord);
         filmstripNavItem = filmstripNavItemTemplate.cloneNode(true);
         Dom.removeClass(filmstripNavItem, 'hidden');
         filmstripNavItem.setAttribute('id', filmstripNavItemId);
         filmstripNavItem.style.width = NAV_ITEM_WIDTH + 'px';
         Event.addListener(filmstripNavItem, 'click', function(i)
               { return function(e)
                  { YAHOO.Bubbling.fire('filmstripNavItemClicked', { index: i }) };
               }(i), this, true);
         filmstripNavListItem.appendChild(filmstripNavItem);
         
         var galleryItemThumbnailDiv = this.getRowItemThumbnailElement(filmstripItem);
         var galleryItemHeaderDiv = this.getRowItemHeaderElement(filmstripItem);
         var galleryItemDetailDiv = this.getRowItemDetailElement(filmstripItem);
         var galleryItemActionsDiv = this.getRowItemActionsElement(filmstripItem);
         
         // Set the item header id
         galleryItemHeaderDiv.setAttribute('id', scope.id + '-item-header-' + oRecord.getId());
         
         // Suffix of the content actions div id must match the onEventHighlightRow target id
         galleryItemActionsDiv.setAttribute('id', scope.id + '-actions-' + filmstripItemId);
         
         // Only render the web-preview for the first filmstrip item and images
         if (i == 0 || (record.node.mimetype != null && 
               record.node.mimetype.substring(0, MIMETYPE_PREFIX_IMAGE.length) == MIMETYPE_PREFIX_IMAGE))
         {
            this.destroyWebPreview(scope, i);
            this.renderWebPreview(scope, i);
         }
         
         var filmstripNavItemThumbnailDiv = this.getFilmstripNavItemThumbnailElement(filmstripNavItem);
         
         // Render the thumbnail for the filmstrip nav item
         this.renderCellThumbnail(
               scope,
               filmstripNavItemThumbnailDiv, 
               oRecord, 
               filmstripItem, 
               null,
               '',
               'doclib');
         
         // TODO - YUI carousel does not like variable widths
//         var filmstripNavItemWidth = parseInt(Dom.getComputedStyle(filmstripNavItemThumbnailDiv, 'width'));
//         filmstripItem.style.width = filmstripNavItemWidth;
         
         // Add the drag and drop
         var imgId = record.jsNode.nodeRef.nodeRef;
         var dnd = new Alfresco.DnD(imgId, scope);
         
         var galleryItemInfoButtonDiv = this.getRowItemInfoButtonElement(filmstripItem);
         // Create a YUI Panel with a relative context of its associated galleryItem
         galleryItemDetailDiv.panel = new YAHOO.widget.Panel(galleryItemDetailDiv,
         { 
            visible:false, draggable:false, close:false, constraintoviewport: true, 
            underlay: 'none', width: DETAIL_PANEL_WIDTH,
            context: [filmstripItem, 'tr', 'tr', null, DETAIL_PANEL_OFFSET]
         });
      };
      
      scope.widgets.filmstripCarousel = new YAHOO.widget.Carousel(this.getFilmstripCarouselContainerId(scope), {
         animation: { speed: 0.2 },
         numVisible: 1,
         navigation:
         {
            prev: scope.id + '-filmstrip-nav-main-previous',
            next: scope.id + '-filmstrip-nav-main-next'
         }
      });
      scope.widgets.filmstripCarousel.render();
      scope.widgets.filmstripCarousel.show();
      scope.widgets.filmstripCarousel.focus();
      
      var numNavItemsVisible = Math.floor((containerWidth - (NAV_PADDING_X * 2)) / (NAV_ITEM_WIDTH + NAV_ITEM_PADDING_X));
      scope.widgets.filmstripNavCarousel = new YAHOO.widget.Carousel(this.getFilmstripNavCarouselContainerId(scope), {
         animation: { speed: 0.5 },
         numVisible: numNavItemsVisible,
         navigation:
         {
            prev: scope.id + '-filmstrip-nav-previous',
            next: scope.id + '-filmstrip-nav-next'
         }
      });
      scope.widgets.filmstripNavCarousel.render();
      scope.widgets.filmstripNavCarousel.show();
      
      // Add the content navigation handling
      YAHOO.Bubbling.on('filmstripNavItemClicked', function(layer, args)
      {
         this.onClickFilmstripContentNavItem(scope, args[1].index);
      }, this);
      scope.widgets.filmstripCarousel.on('pageChange', function (ev)
      {
         viewRendererInstance.onFilmstripMainContentChanged(scope, ev);
      });
      
      var dndContainer = Dom.get(DND_CONTAINER_ID);
      Dom.addClass(dndContainer, 'alf-filmstrip-dragging');
      
      this.currentResizeCallback = function(e)
      {
         viewRendererInstance.resizeView(scope, sRequest, oResponse, oPayload);
      };
      this.setupWindowResizeListener();

      renderImagePreview();

      // MNT-10678 fix. Fix for small screen resolutions. Setting up the minimal height for Carousel Content div.
      var carouselContentDivs = Dom.getElementsByClassName('yui-carousel-element');
      // set the carousel height
      carouselContentDivs[0].style.minHeight = CAROUSEL_CONTENT_MIN_HEIGHT + "px";

      var galeryItems = Dom.getElementsByClassName('alf-gallery-item');

      for (var i = 0; i < galeryItems.length; i++)
      {
         if (galeryItems[i].className.indexOf('hidden') < 0)
         {
            galeryItems[i].style.minHeight = CAROUSEL_CONTENT_MIN_HEIGHT + "px";
         }
      }

      // hide scrol panel for the carousel element when it's hidden.
      var filmstripNavDiv = Dom.get(scope.id+ '-filmstrip');
      filmstripNavDiv.style.overflow = "hidden";

      var fadeIn = new YAHOO.util.Anim(container, {
                                                     opacity:
                                                     {
                                                        from: 0,
                                                        to: 1
                                                     }
                                                  }, 0.4, YAHOO.util.Easing.easeOut);
      fadeIn.animate();
   };

   Alfresco.DocumentListFilmstripViewRenderer.prototype.renderWebPreview = function DL_FVR_renderWebPreview(scope, index)
   {
      var containerTarget; // This will only get set if thumbnail represents a container
      
      var oRecordSet = scope.widgets.dataTable.getRecordSet();
      
      if (oRecordSet.getLength() > 0)
      {
         var oRecord = oRecordSet.getRecord(index);
         var record = oRecord.getData();
         var filmstripItemId = this.getRowItemId(oRecord);
         var filmstripItem = document.getElementById(filmstripItemId);
         var galleryItemThumbnailDiv = this.getRowItemThumbnailElement(filmstripItem);

         WEB_PREVIEWER_HEIGHT = galleryItemThumbnailDiv.offsetHeight;

         var thumbnail = this.getThumbnail(
               scope, galleryItemThumbnailDiv, oRecord, null, null, '-filmstrip-main-content');
         
         if (thumbnail.isContainer)
         {   
            if (!document.getElementById(thumbnail.id))
            {
               galleryItemThumbnailDiv.innerHTML += '<span class="folder">' + (thumbnail.isLink ? '<span class="link"></span>' : '') + 
                  Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + thumbnail.html + '</a>';
            }
         }
         else
         {
            // Image web preview may still be present so only render if web-preview doesn't exist
            var galleryItemThumbnailDivChildren = Dom.getElementsByClassName('web-preview', 'div', galleryItemThumbnailDiv);
            if (galleryItemThumbnailDivChildren == null || galleryItemThumbnailDivChildren.length == 0)
            {
               // Render the web-preview for the filmstrip item
               Alfresco.util.loadWebscript(
               {
                  url: Alfresco.constants.URL_SERVICECONTEXT + "/components/preview/web-preview",
                  properties: { nodeRef: record.nodeRef },
                  target: galleryItemThumbnailDiv
               });
            }
         }
      }
   };
   
   Alfresco.DocumentListFilmstripViewRenderer.prototype.destroyWebPreview = function DL_FVR_destroyWebPreview(scope, index)
   {
      var previewRealDivs = Dom.getElementsByClassName('real', 'div');
      if (previewRealDivs != null)
      {
         for (i = 0, j = previewRealDivs.length; i < j; i++)
         {
            if (Dom.hasClass(previewRealDivs[i], 'web-preview'))
            {
               previewRealDivs[i].parentNode.removeChild(previewRealDivs[i]);
            }
         }
      }
      var previewDivs = Dom.getElementsByClassName('WebPreviewer', 'div');
      if (previewDivs != null)
      {
         for (i = 0, j = previewDivs.length; i < j; i++)
         {
            if (Dom.hasClass(previewDivs[i], 'previewer'))
            {
               previewDivs[i].parentNode.parentNode.parentNode.removeChild(previewDivs[i].parentNode.parentNode);
            }
         }
      }
   }
   
   
   Alfresco.DocumentListFilmstripViewRenderer.prototype.resizeView = function DL_FVR_resizeView(scope, sRequest, oResponse, oPayload)
   {
      var container = Dom.get(scope.id + this.parentElementIdSuffix);
      // Ignore if true full screen resizing due to conflicting firing times
      if (!Dom.hasClass(container, 'alf-true-fullscreen'))
      {
         var currentPage = scope.widgets.filmstripCarousel.get('currentPage');
         this.renderView(scope, sRequest, oResponse, oPayload);
         // Wait for carousel to render
         setTimeout(function()
         {
            scope.widgets.filmstripCarousel.scrollTo(currentPage, false);
         }, 50);
      }
   }
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.onSelectedFilesChanged
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.onSelectedFilesChanged = function DL_FVR_onSelectedFilesChanged(scope)
   {
      // Set all selected decorators
      var oRecordSet = scope.widgets.dataTable.getRecordSet(), oRecord, record, jsNode, nodeRef, filmstripNavItem, i, j;
      var anySelected = false;
      for (i = 0, j = oRecordSet.getLength(); i < j; i++)
      {
         oRecord = oRecordSet.getRecord(i);
         jsNode = oRecord.getData("jsNode");
         nodeRef = jsNode.nodeRef;
         filmstripNavItem = this.getFilmstripNavItem(oRecord);
         var isChecked = scope.selectedFiles[nodeRef];
         if (isChecked)
         {
            Dom.addClass(filmstripNavItem, 'alf-selected');
            anySelected = true;
         }
         else
         {
            Dom.removeClass(filmstripNavItem, 'alf-selected');
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
   }
   
   /**
    * Override to prevent rendering of the navigation link
    * 
    * @see Alfresco.DocumentListFilmstripViewRenderer.renderCellThumbnail
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.renderCellThumbnail = function DL_FVR_renderCellThumbnail(scope, elCell, oRecord, oColumn, oData, imgIdSuffix, renditionName)
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
                  (scope.dragAndDropEnabled ? '<span class="droppable"></span>' : '') + thumbnail.html;
            containerTarget = new YAHOO.util.DDTarget(thumbnail.id); // Make the folder a target
         }
         else
         {
            elCell.innerHTML += (thumbnail.isLink ? '<span class="link"></span>' : '') + thumbnail.html;
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
   
   /**
    * @see Alfresco.DocumentListGalleryViewRenderer.renderCellDescription
    */
   Alfresco.DocumentListFilmstripViewRenderer.prototype.renderCellDescription = function DL_FVR_renderCellDescription(scope, elCell, oRecord, oColumn, oData)
   {
      Alfresco.DocumentListGalleryViewRenderer.superclass.renderCellDescription.call(this, scope, elCell, oRecord, oColumn, oData);
      var filmstripItem = this.getRowItem(oRecord, elCell);
      // Check for null galleryItem due to ALF-15529
      if (filmstripItem !== null)
      {
         // Copy description
         var filmstripItemDetailDescriptionElement = this.getRowItemDetailDescriptionElement(filmstripItem).innerHTML = elCell.innerHTML;
         // Clear out the table cell so there's no conflicting HTML IDs
         elCell.innerHTML = '';
         // Add a simple display label
         this.getRowItemLabelElement(filmstripItem).innerHTML = $html(oRecord.getData().displayName);
      }
      // Add a simple display label for the nav item
      var filmstripNavItem = this.getFilmstripNavItem(oRecord, elCell);
      if (filmstripNavItem !== null)
      {
         this.getFilmstripNavItemLabelElement(filmstripNavItem).innerHTML = $html(oRecord.getData().displayName);
      }
   };

   
})();

