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
   import flash.events.Event;
   import flash.events.MouseEvent;
   import flash.text.TextField;
   import flash.text.TextFormat;
   
   /**
    * Adds padding to the display object inside but the most important
    * feature is that it provides the possibility to use mouse events
    * such as mouse over, click, mouse out on the wrapped display object.
    *
    * I.e. The content loaded though a Loader and found in loader.content
    * (typed as DisplayObject) does NOT provide mouse events even if the actual
    * content inisde is say a MovieClip (which in the api says it
    * dispatches mouse events). So if an event listener that listen for
    * mouse events is attached to a loaded movie clip nothing happens; no errors
    * and no events.
    *
    * By wrapping the loaded content inside a Prite we can listen for events on
    * the sprite instead.
    *
    * @author Erik Winlof
    */
   public class Page extends Sprite
   {
      
      /**
       * The wiheightdth if the page (excl border).
       * Since mc not will be present all the time this value will tell it's height.
       */
      public var contentHeight:Number = 0;
      
      /**
       * The width if the page (excl border).
       * Since mc not will be present all the time this value will tell it's width.
       */
      public var contentWidth:Number = 0;
      
      /**
       * The wrapped display object.
       */
      private var _content:DisplayObject;
      
      /**
       * True if page should appear as interactive
       */
      private var _interactive:Boolean = false;
      
      /**
       * True if page should appear as a placeholder with background color and a text
       */
      private var _placeHolder:Boolean = false;
      
      /**
       * Textfild to display the text in center if page is a place holder
       */
      private var _text:TextField;
      
      /**
       * The border color to display if border is larger than 0.
       */
      public var borderColor:uint;
      
      /**
       * The border around mc; top, left, right and bottom.
       */
      private var borderThickness:Number;
      
      /**
       * The color to display if mc isn't present and page is a place holder.
       */
      public var defaultPageColor:uint;
      
      /**
       * Constructor
       */
      public function Page(dpw:Number, dph:Number, placeHolder:Boolean=false, text:String="", dpc:uint=0x00FFFFFF, bc:uint=0x0054B9F8, bt:Number=15)
      {
         super();
         
         // Set size of component
         contentWidth = dpw;
         contentHeight = dph;
         
         this._placeHolder = placeHolder;
         /**
          *  Display a text representation of the page that is visible when
          *  the page isn't loaded.
          */
         
         if (this._placeHolder)
         {
            // The style of the text
            var format:TextFormat = new TextFormat();
            format.font = "Verdana";
            format.color = 0xCCCCCC;
            format.size = 500;
            
            // The actual text
            _text = new TextField();
            _text.defaultTextFormat = format;
            _text.text = text;
            _text.width = _text.textWidth
            _text.height = _text.textHeight
            
            // Add and position the text
            addChild(_text);
            _text.x = bt + (dpw / 2) - (_text.textWidth / 2);
            _text.y = bt + (dph / 2) - (_text.textHeight / 2);
            
            /**
             * Make sure we notice if the mouse is over so we can listen
             * for clicks and paint the border.
             */
            addEventListener(Event.REMOVED, onRemoveChild);
            
            /**
             * Make sure we notice if the mouse is over so we can listen
             * for clicks and paint the border.
             */
            addEventListener(MouseEvent.MOUSE_OVER, onPageMouseOver);
         }
         
         // Color of the page
         defaultPageColor = dpc;
         borderColor = bc;
         borderThickness = bt;
         
         // Paint the page
         redrawChild();
      }
      
      /**
       * Returns true if the page appears as interactive and dispatches events
       *
       * @return true if the page appears as interactive and dispatches events
       */
      public function get interactive():Boolean
      {
         return _interactive;
      }
      
      /**
       * Set to true if the page shall appear as interactive and dispatches events
       *
       * @param interactive true if the page shall appear as interactive and dispatches events
       */
      public function set interactive(interactive:Boolean):void
      {
         if (_interactive != interactive)
         {
            _interactive = interactive;
            useHandCursor = interactive;
            buttonMode = interactive;
         }
      }
      
      /**
       * Positions the children with regards to padding.
       */
      private function redrawChild():void
      {
         // Fill background with transparent graphics so the Pages dimensions include the border
         doFillBackground(0);
      }
      
      /**
       * Called when a movie clip from the pool has been removed in favour for another page.
       *
       * @param event A description of the remove event
       */
      public function onRemoveChild(event:Event):void
      {
         if (event.target == _content)
         {
            // Content was removed (since it was needed in another Page), lets display the page number instead
            addChild(_text);
         }
      }
      
      /**
       * Called when the user moves the mouse over a page.
       *
       * @param event A description of the mouse over event
       */
      public function onPageMouseOver(event:Event):void
      {
         doFillBackground(1);
         addEventListener(MouseEvent.CLICK, onPageMouseClick);
         addEventListener(MouseEvent.MOUSE_OUT, onPageMouseOut);
      }
      
      /**
       * Called when the user moves the mouse out from a page.
       *
       * @param event A description of the mouse out event
       */
      public function onPageMouseOut(event:Event):void
      {
         doFillBackground(0);
         removeEventListener(MouseEvent.CLICK, onPageMouseClick);
      }
      
      /**
       * Called when the user clicks a page.
       *
       * @param event A description of the mouse click
       */
      public function onPageMouseClick(event:Event):void
      {
         // Create a fully colored border round the object
         doFillBackground(1);
         
         // Remove click event listener
         removeEventListener(MouseEvent.CLICK, onPageMouseClick);
      }
      
      public function set content(content:DisplayObject):void
      {
         // Remove old child if any
         var prevIndex:int = _content && contains(_content) ? getChildIndex(_content) : -1;
         if (prevIndex > -1)
         {
            removeChildAt(prevIndex);
         }
         
         // Hide the page number
         if (_text != null)
         {
            removeChild(_text);
         }
         
         // Add new child
         _content = content;
         super.addChild(_content);
         _content.x = borderThickness;
         _content.y = borderThickness;
      }
      
      private function doFillBackground(alpha:Number):void
      {
         graphics.clear();
         
         graphics.beginFill(borderColor, _interactive ? alpha : 0);
         graphics.drawRect(0, 0, contentWidth + borderThickness * 2, contentHeight + borderThickness * 2);
         graphics.endFill();
         
         if (this._placeHolder)
         {
            graphics.beginFill(defaultPageColor, 1);
            graphics.drawRect(borderThickness, borderThickness, contentWidth, contentHeight);
            graphics.endFill();
         }
      }
      
   }
   
}