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
    * Event class describing events occurring inside a DocumentZoomDisplay component.
    *
    * @author Erik Winlof
    */
   public class DocumentZoomDisplayEvent extends Event
   {
      
      /**
       * Dispatched when the document specified by the url fails to load. 
       * 
       * Sets the following values: errorCode.
       */		
      public static const DOCUMENT_LOAD_ERROR:String = "documentLoadError";		
      
      
      /**
       * Dispatched when the content loaded is of an unexpected/unhandled type. 
       * 
       * Sets the following values: errorCode.
       */		
      public static const DOCUMENT_CONTENT_TYPE_ERROR:String = "documentContentTypeError";		
      
      /**
       * Dispatched when the snapPoints for the display area inside the document zoom display
       * is known or has changed. 
       * 
       * Sets the following values: fitToWidth, fitToHeight, fitToScreen and fitByContentType.
       */		
      public static const DOCUMENT_SNAP_POINTS_CHANGE:String = "documentSnapPointsChange";
      
      /**
       * Dispatched when the page property (the page being displayed in the top of the display area)
       * is changed inside the document zoom display.
       * 
       * Sets the following values: noOfPages, page, visiblePages.
       */
      public static const DOCUMENT_PAGE_SCOPE_CHANGE:String = "documentPageScopeChange";
      
      /**
       * Dispatched when the initiative to change document's scale is from the DocumentZoomDisplay itself. 
       * 
       * Sets the following values: documentScale.
       */
      public static const DOCUMENT_SCALE_CHANGE:String = "documentScaleChange";
      
      /**
       * Dispatched when the content of the document has been loaded and is displayed.
       *
       * Sets the following values: noOfPages, page, visiblePages.
       */
      public static const DOCUMENT_READY:String = "documentReady";
      
      /**
       * An error code representing the error that occured when trying to load the content from the url.
       */
      public var errorCode:String;
      
      /**
       * The scale to use to make sure the document's width fits inside the dispay area.
       */
      public var fitToWidth:Number;
      
      /**
       * The scale to use to make sure the document's height fits inside the dispay area.
       */
      public var fitToHeight:Number;
      
      /**
       * The scale to use to make sure the document's width and height fits inside the dispay area.
       */		
      public var fitToScreen:Number;
      
      /**
       * The scale to use to appropriatly display the document inside the dispay area.
       * Shall be set with contentType in mind, that small images NOT shall be upscaled to fit 
       * the display area, because that will make them look pixelated etc.
       */
      public var fitByContentType:Number;
      
      
      /**
       * The number of pages of the displayed document.
       */
      public var noOfPages:int;
      
      /** 
       * The page currenlty being displayed in the top of the display area.
       */
      public var page:int;		
      
      /** 
       * THe number of pages visible in the display area.
       */
      public var visiblePages:int;
      
      /** 
       * The new scale of the document
       */
      public var documentScale:Number;		
      
      
      /** 
       * Constructor
       */
      public function DocumentZoomDisplayEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
      {
         super(type, bubbles, cancelable);
      }
      
   }
}