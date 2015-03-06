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

package org.alfresco.core.ui
{
   import flash.events.Event;
   
   import mx.managers.CursorManager;
   import mx.managers.CursorManagerPriority;
   
   /**
    * Helper class for managing cursors.
    *
    * @author Erik Winlof
    */
   public class Cursors
   {
      
      /** 
       * Boolean that decides if curors shall be displayed or not.
       */ 
      public static var enabled:Boolean = true;
      
      /** 
       * Array of ids to all the current hand cursors that are in use.
       */ 
      private static var handCursorId:int = 0;
      
      /** 
       * Array of ids to all the current grab cursors that are in use.
       */ 
      private static var grabCursorId:int = 0;
      
      /**
       * Constructor
       */
      public function Cursors()
      {
      }		
      
      /**
       * The hand cursor image
       */
      [Embed(source="assets/cursor-hand.png")]
      private static var handCursor:Class;
      
      /**
       * The grab cursor image
       */
      [Embed(source="assets/cursor-grab.png")]
      private static var grabCursor:Class;
      
      /**
       * Show cursor as a hand.
       * 
       * @param event Any event that this method was set to listen for.
       */
      public static function showHandCursor(event:Event):void 
      {
         if (enabled && handCursorId == 0)
         {
            handCursorId = CursorManager.setCursor(handCursor);
            CursorManager.showCursor();
         }
      }
      
      /**
       * Hide the last hand cursor.
       * 
       * @param event Any event that this method was set to listen for.
       */
      public static function hideHandCursor(event:Event):void 
      {
         if (handCursorId != 0)
         {
            CursorManager.removeCursor(handCursorId);
            handCursorId = 0;
            CursorManager.showCursor();
         }
      }
      
      /**
       * Display cursor as a grabbing hand.
       * 
       * @param event Any event that this method was set to listen for.
       */
      public static function showGrabCursor(event:Event):void 
      {
         if (enabled && grabCursorId == 0)
         {
            grabCursorId = CursorManager.setCursor(grabCursor, CursorManagerPriority.HIGH);
            CursorManager.showCursor();
         }
      }
      
      /**
       * Hide the last grab cursor.
       * 
       * @param event Any event that this method was set to listen for.
       */	   	
      public static function hideGrabCursor(event:Event):void 
      {
         if (grabCursorId != 0)
         {
            CursorManager.removeCursor(grabCursorId);
            grabCursorId = 0;
            CursorManager.showCursor();
         }
      }
   }
}