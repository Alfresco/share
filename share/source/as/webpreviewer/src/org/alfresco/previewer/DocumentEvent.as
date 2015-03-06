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
   import flash.events.Event;
   
   /**
    * Event class describing events occurring inside a Page.
    *
    * @author Erik Winlof
    */
   public class DocumentEvent extends Event
   {
      
      /**
       * Dispatched when a apge in the document was clicked.
       * 
       * Sets values: page and pageNo
       */		
      public static const DOCUMENT_PAGE_CLICK:String = "documentPageClick";		
      
      /**
       * The page in the document that something happenened to.
       */
      public var page:Page;
      
      /**
       * The page index of the page in the document that something happenened to.
       */
      public var pageIndex:Number;
      
      /**
       * Constructor
       */
      public function DocumentEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
      {
         super(type, bubbles, cancelable);
      }
   }
}


