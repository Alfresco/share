/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

package org.alfresco.previewer
{
   import flash.display.DisplayObject;
   import flash.display.Sprite;
   import flash.events.MouseEvent;
   
   /**
    * A sprite object that displays its children as pages in a document.
    *
    * @author Erik Winlof
    */
   public class Document extends Sprite
   {
      
      /**
       * The outer padding of the whpole document (top, left, right, bottom)
       */
      private var _padding:Number = 0;
      
      /**
       * The vertical gap/padding betwwen the children/pages.
       */
      private var _gap:Number = 0;
      
      /**
       * The width of the widest page/child.
       */
      private var _maximumPageWidth:Number = 0;
      
      /**
       * The height if the highest page/child.
       */
      private var _maximumPageHeight:Number = 0;
      
      /**
       * "start"-y-position for all the pages/children.
       */
      private var _pageStarts:Array = new Array();
      
      /**
       * "end"-y-position for all the pages/children.
       */
      private var _pageEnds:Array = new Array();
      
      /**
       * Constructor
       */
      public function Document()
      {
         super();
      }
      
      /**
       * Positions the children/pages based on the order they were added and the gap and padding.
       * Also updates the _maximumPageWidth, _maximumPageHeight, _pageStarts and _pageEnds properties.
       */
      private function redrawChildren():void
      {
         graphics.clear();
         _pageEnds = new Array();
         _pageStarts = new Array();
         var obj:DisplayObject;
         var top:Number = 0, w:Number = 0;
         top += _padding; // padding top
         w += _padding; // padding left
         for (var i:Number = 0; i < numChildren; i++)
         {
            // Position each child/page.
            obj = getChildAt(i);
            obj.x = _padding + ((_maximumPageWidth / 2) - (obj.width / 2));
            obj.y = top;
            _pageStarts.push(top);
            top += obj.height;
            _pageEnds.push(top);
            
            // Add gap as long as there are more pages after the current.
            if (i < numChildren - 1)
            {
               top += _gap;
            }
            
         }
         top += _padding; // padding bottom
         w += _maximumPageWidth; // widest page
         w += _padding; // padding right
         
         graphics.beginFill(0xFFCC00, 0); // alpha set to 0 so the yellow "padding" isn't "visible"
         graphics.drawRect(0, 0, w, top);
         graphics.endFill();
         
      }
      
      /**
       * Adds a Page as a child to the display list and treats it like a page in a document.
       *
       * @param child A page in the document.
       */
      override public function addChild(child:DisplayObject):DisplayObject
      {
         if (child is Page)
         {
            // Call super class addChild.
            super.addChild(child);
            
            // Set variables for access later
            _maximumPageWidth = child.width;
            _maximumPageHeight = child.height;
            
            // Return the child/page.
            return child;
         }
         else
         {
            throw Error("A child to a Document must be of type Child.");
         }
      }
      
      public function set pages(pages:Array):void
      {
         var p:Page;
         var widest:Number = 0;
         var highest:Number = 0;
         
         for (var i:int = 0; i < pages.length; i++)
         {
            p = pages[i];
            if (p is Page)
            {
               // Call super class addChild.
               super.addChild(p);
               
               // Save the width/height of the widest/highest page/child.
               widest = Math.max(p.width, widest);
               highest = Math.max(p.height, highest);
               
               // Add event listener for page clicks
               p.addEventListener(MouseEvent.CLICK, onPageClick);
            }
            else
            {
               throw Error("A child to a Document must be of type Child.");
            }
         }
         
         // Set variables for access later
         _maximumPageWidth = widest;
         _maximumPageHeight = highest;
         
         // Layout all the pages in the document.
         redrawChildren();
      }
      
      
      
      /**
       * Returns the padding used used for top, left, right and bottom.
       *
       * @return the padding used used for top, left, right and bottom
       */
      public function get padding():Number
      {
         return _padding;
      }
      
      /**
       * Sets the padding to be used for top, left, right and bottom.
       *
       * @param the padding to be used for top, left, right and bottom.
       */
      public function set padding(padding:Number):void
      {
         if (_padding != padding)
         {
            _padding = padding;
            
            // Make sure we layout the pages according to the new padding.
            redrawChildren();
         }
      }
      
      /**
       * Returns the gap between the children/pages in the document.
       *
       * @return the gap between the children/pages in the document.
       */
      public function get gap():Number
      {
         return _gap;
      }
      
      /**
       * Sets the gap to be used between the children/pages in the document.
       *
       * @param the gap to be used between the children/pages in the document.
       */
      public function set gap(gap:Number):void
      {
         if (_gap != gap)
         {
            _gap = gap;
            
            // Make sure we layout the pages according to the new gap.
            redrawChildren();
         }
      }
      
      /**
       * Returns the width of the document.
       *
       * @return the width of the document.
       */
      public function getDocWidth():Number
      {
         return width;
      }
      
      /**
       * Returns the height of the document.
       *
       * @return the height of the document.
       */
      public function getDocHeight():Number
      {
         return height; 
      }
      
      /**
       * Returns the width of the widest page/child.
       *
       * @return the width of the widest page/child.
       */
      public function getMaximumPageWidth():Number
      {
         return _maximumPageWidth;
      }
      
      /**
       * Returns the height of the widest page/child.
       *
       * @return the height of the widest page/child.
       */
      public function getMaximumPageHeight():Number
      {
         return _maximumPageHeight;
      }
      
      /**
       * Get the "start"-y-position for the page defined by pageIndex.
       *
       * @param pageIndex The index of the page (first page can be found on index 0)
       *
       * @return the "start"-y-position for the page
       */
      public function getPageStart(pageIndex:int):Number
      {
         return _pageStarts[pageIndex];
      }
      
      /**
       * Get the "end"-y-position for the page defined by pageIndex.
       *
       * @param pageIndex The index of the page (first page can be found on index 0)
       *
       * @return the "end"-y-position for the page
       */
      public function getPageEnd(pageIndex:int):Number
      {
         return _pageEnds[pageIndex];
      }
      
      /**
       * Returns the number of pages/children in the document.
       *
       * @return the number of pages/children in the document.
       */
      public function getNoOfPages():Number
      {
         return numChildren;
      }
      
      /**
       * Called when one of the pages is clicked.
       *
       * @param event Describes the click event on the page.
       */
      private function onPageClick(event:MouseEvent):void
      {
         var pageIndex:int = getChildIndex(event.currentTarget as DisplayObject);
         if (pageIndex != -1)
         {
            var de:DocumentEvent = new DocumentEvent(DocumentEvent.DOCUMENT_PAGE_CLICK);
            de.page = event.currentTarget as Page;
            de.pageIndex = pageIndex;
            dispatchEvent(de);
         }
      }
      
   }
}
