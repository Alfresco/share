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
   
   /**
    *  SpriteZoomDisplayContext is a helper class to SpriteXoomDisplay.
    *  It describes the current state of the zoomable sprite inside the displays vieable area.
    *
    *  @author Erik Winlof
    */
   public class SpriteZoomDisplayContext
   {
      
      /**
       * True if sprite is wider than the display area.
       */
      public var overflowX:Boolean;
      
      /**
       * True if sprite is higher than the display area.
       */
      public var overflowY:Boolean;
      
      /**
       * The width of the display area exluding the vertical scrollbar.
       */
      public var screenWidth:Number;
      
      /**
       * The height of the display area excluding the horizontal srollbar.
       */
      public var screenHeight:Number;
      
      /**
       * The width of the display area including any vertical scrollbar.
       */
      public var screenWidthIncl:Number;
      
      /**
       * The height of the display area including any horizontal scrollbar.
       */
      public var screenHeightIncl:Number;
      
      /** 
       * Constructor
       */ 
      public function SpriteZoomDisplayContext()
      {				
      }
      
   }
}