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
   import flash.display.Sprite;
   import flash.events.Event;
   import flash.events.MouseEvent;
   import flash.geom.Rectangle;
   
   import mx.controls.HScrollBar;
   import mx.controls.VScrollBar;
   import mx.core.UIComponent;
   import mx.events.ResizeEvent;
   import mx.events.ScrollEvent;		
   
   /**
    * <p>SpriteZoomDisplay is a zoomable user interface (ZUI) that makes it possible
    * to zoom into and out of a sprite, drag it and scroll it.</p>
    * 
    * <p>The main work in this class is done in the updateDisplayList method.
    * Almost all other functions sets properties of the new visual appaerance of the 
    * sprite object or scrollbars and make sure that updateDisplayList is called where
    * the display objects actually are moved.</p>
    *
    * @author Erik Winlof
    */
   public class SpriteZoomDisplay extends UIComponent
   {
      
      /* PUBLIC ACCESSIBLE PROPERTIES */
      
      /**
       * Decides where the sprite shall be vertically positioned by default 
       * if the sprite's height is smaller then the display area's height.
       * 
       * Possible values are "center" (default) and "top"
       */
      public var verticalDefaultPosition:String = "center";
      
      /** 
       * True if the sprite shall be draggable inside the display area.
       * Default is true
       */
      public var draggingEnabled:Boolean = true;
      
      /* LOCAL / PRIVATE PROPERTIES */
      
      /**
       * The horizontal scrollbar 
       */
      protected var hsb:HScrollBar;
      
      /**
       * Set to true if the horizontal scrollbar has been moved and 
       * the sprite shall be updated in updateDisplayList
       */ 
      protected var hsbUpdated:Boolean;
      
      /**
       * The vertical scrollbar
       */				
      protected var vsb:VScrollBar;
      
      /**
       * Set to true if the vertical scrollbar has been moved and 
       * the sprite shall be updated in updateDisplayList
       */ 
      protected var vsbUpdated:Boolean;
      
      /**
       * A reference to the sprite that is zoomed, and moved
       */
      protected var _sprite:Sprite;
      
      /**
       * The original/unscaled witdh of the sprite
       */
      protected var spriteOrgWidth:Number;
      
      /**
       * The original/unscaled height of the sprite
       */
      protected var spriteOrgHeight:Number;
      
      /**
       * The previous scale of the sprite
       */
      protected var spritePrevZoom:Number = 1;
      
      /**
       * The fortcoming scale of the sprite
       */
      protected var spriteNewZoom:Number = 1;
      
      /**
       * The fortcoming x position of the sprite.
       * Normally the move is done directly in the changing method such as moveSprite
       * but if a move shall be made after a zoom this can be used.
       * Make sure to set spriteNewXChange to true to make the move.
       */
      protected var spriteNewX:Number = NaN;
      protected var spriteNewXChanged:Boolean = false;
      
      /**
       * The fortcoming y position of the sprite.
       * Normally the move is done directly in the changing method such as moveSprite
       * but if a move shall be made after a zoom this can be used.
       * Make sure to set spriteNewYChange to true to make the move.
       */
      protected var spriteNewY:Number = NaN;
      protected var spriteNewYChanged:Boolean = false;
      
      /**
       * Set to true if the sprite has been moved/dragged and  
       * the scrollbars shall be updated in updateDisplayList
       */ 				
      protected var spritePositionChanged:Boolean = false;
      
      /**
       * Set to true if the sprite hasn't been positioned in the display area before
       * but shall be default positioned in updateDisplayList
       */ 
      protected var defaultPosition:Boolean = false;
      
      /**
       * Set to true if the display area has changed and 
       * the sprite shall be appropriatly positoned in updateDisplayList
       */ 				
      protected var displayResized:Boolean = false;
      
      /**
       * The original screen size on a resize event
       */
      protected var prevScreenHeight:Number = -1;
      protected var prevScreenWidth:Number = -1;
      
      /**
       * Constructor
       */
      public function SpriteZoomDisplay()
      {
         super();
      }
      
      /**
       * Sets the sprite object that should be zoomed and moved.
       *
       * @param sprite THe sprite object to zoom and move. 
       */
      public function set sprite(sprite:Sprite):void
      {
         // Remove old sprites
         var prevSpriteIndex:int = _sprite ? getChildIndex(_sprite) : -1;
         if (prevSpriteIndex > -1)
         {
            removeChildAt(prevSpriteIndex);
         }
         
         // And set new one			
         _sprite = sprite;
         
         if (_sprite)
         {
            // Make sure we have the sprite original dimensions
            spriteOrgWidth = _sprite.width;
            spriteOrgHeight = _sprite.height;
            spritePrevZoom = 1;
            spriteNewZoom = 1;	
            
            // Add drag n drop event listeners		
            sprite.addEventListener(MouseEvent.MOUSE_DOWN, startSpriteDrag);
            
            // Show mouse cursor as hand if	dragging is enabled	
            sprite.addEventListener(MouseEvent.ROLL_OVER, function (event:Event):void
            {				
               if (draggingEnabled)
               {
                  Cursors.showHandCursor(event);
               }
            });
            sprite.addEventListener(MouseEvent.ROLL_OUT, function (event:Event):void
            {				
               if (draggingEnabled)
               {
                  Cursors.hideHandCursor(event);
               }
            });
            
            // Add sprite to child   			
            addChildAt(sprite, 0);
            
            // Make sure sprite is displayed in default position
            defaultPosition = true;
            invalidateDisplayList();
         }				
      }
      
      /**
       * The sprite object that is zoomed and moved.
       *
       * @return The sprite object that is zoomed and moved 
       */
      public function get sprite():Sprite
      {
         return _sprite;
      }
      
      
      /**
       * Makes sure the sprite object is zoomed in or out.
       *
       * @param value The scale to display the zoomable sprite object in. 
       */		
      public function set zoom(value:Number):void
      {			
         if (value > 0)
         {	        	        		      
            /** 
             * Make sure we update the appaerance of the sprite object, 
             * since the actual zooming/scaling is done in updateDisplayList
             */   					
            spriteNewZoom = value;	        	
            invalidateDisplayList();
         }        	
      }
      
      /**
       * Returns the current scale of the zoomable sprite object.
       *
       * @return The current scale of the zoomable sprite object. 
       */		
      public function get zoom():Number
      {
         return spritePrevZoom;	
      }
      
      /**
       * Moves the movable sprite object.
       *
       * @param x The new x position of the moveable sprite object.
       * @param y The new y position of the moveable sprite object. 
       */
      public function moveSprite(x:Number, y:Number):void
      {
         // Make sure we don't move the sprite out of the visible area
         _sprite.y = Math.max(y, this.height - _sprite.height);
         _sprite.x = x; 
         
         // Make sure the scrollbars are updated in updateDisplayList    
         spritePositionChanged = true;    			
         invalidateDisplayList();			
      }
      
      
      /**
       * Creates the scrollbars so the sprite can be scrolled  
       */
      override protected function createChildren():void 
      {			
         // Call super class createChildren
         super.createChildren();
         
         // Create and add horizontal scrollbar to display list
         if (!hsb)
         {	            					    
            hsb = new HScrollBar();
            hsb.visible = false;	
            hsb.enabled = true;	    	
            hsb.height = 16;
            addChild(hsb);
            
            // Listen for user interaction with the scroll bar
            hsb.addEventListener(ScrollEvent.SCROLL, onHorizontalScroll);	    		    		    	
         }
         
         //  Create and add vertical scrollbar to display list
         if (!vsb)
         {
            vsb = new VScrollBar();
            vsb.visible = false;
            vsb.enabled = true;
            vsb.width = 16;
            addChild(vsb);		    
            
            // Listen for user interaction with the scroll bar
            vsb.addEventListener(ScrollEvent.SCROLL, onVerticalScroll);		    	
         }
         
         /**
          * Listen for resize events so we can position the sprite in an 
          * appropriate position after a resize, i.e. a full screen 
          */
         this.addEventListener(ResizeEvent.RESIZE, onResize);
         
         // Listen for mouse wheel so we can do vertical scrolling 
         this.addEventListener(MouseEvent.MOUSE_WHEEL, onMouseWheel);
      }
      
      /**
       * Called when a user has interacted with the horizontal scrollbar.
       * Make sure the sprite is moved accordingly.
       *
       * @param event Event describing the user scrollbar interaction 
       */
      private function onHorizontalScroll(event:ScrollEvent):void
      {			
         // Make sure sprite is moved in relation to the new scrollbar position
         hsbUpdated = true;
         invalidateDisplayList();			
      }		
      
      /**
       * Called when a user has interacted with the vertical scrollbar.
       * Make sure the sprite is moved accordingly.
       *
       * @param event Event describing the user scrollbar interaction. 
       */
      private function onVerticalScroll(event:ScrollEvent):void
      {	
         // Make sure sprite is moved in relation to the new scrollbar position		
         vsbUpdated = true;
         invalidateDisplayList();			
      }		
      
      
      /**
       * Called when the user moves the mouse wheel.
       * This will not get called on Mac in a browser if not the 3rd party class 
       * ExternalMouseWheelSupport is activated.
       */
      private function onMouseWheel(event:MouseEvent):void 
      {				
         // Move the document up or down in the display area
         adjustVerticalScrollbarPosition(vsb.scrollPosition - event.delta * (vsb.lineScrollSize / 3));        				 
      }
      
      /**
       * Adjusts the scrollbar position and makes sure it doesn's scroll out of bounds.
       * 
       * @param newScrollbarPosition The new y-position for the scroll thumb of the vertical scrollbar.
       */
      protected function adjustVerticalScrollbarPosition(newScrollPosition:Number):void
      {
         // Make sure scrolling doesn't go out of bounds
         newScrollPosition = Math.max(newScrollPosition, 0);
         newScrollPosition = Math.min(newScrollPosition, vsb.maxScrollPosition);
         
         if (newScrollPosition != vsb.scrollPosition)
         {
            // scroll position has changed make sure we force the sprite to be positioned 				 
            vsb.scrollPosition = newScrollPosition;
            vsbUpdated = true;
            invalidateDisplayList();
         }
      }	
      
      /**
       * Called when the user performs a mouse down operation on the sprite or one of its children.
       * Makes the sprite draggable inside an area based on the sprite size and the visible screen.
       * 
       * @param event Event describing the user mouse interaction. 
       */	
      private function startSpriteDrag(mouseDown:Event):void
      {
         if (draggingEnabled)
         {				
            Cursors.showGrabCursor(mouseDown);
            _sprite.startDrag(false, getSpriteDragRectangle());
            // Swap mouse listeners for in-drag interest ones
            _sprite.removeEventListener(MouseEvent.MOUSE_DOWN, startSpriteDrag);
            stage.addEventListener(MouseEvent.MOUSE_UP, stopSpriteDrag, true);
            _sprite.addEventListener(MouseEvent.MOUSE_MOVE, onSpriteMouseMove);							
         }
      }
      
      /**
       * Called when the user drags the sprite.
       * Makes sure the scrollbar positions are updated.
       * 
       * @param event Event describing the user mouse interaction. 
       */
      private function onSpriteMouseMove(event:MouseEvent):void
      {			
         // Make sure scrollbars positions gets updated
         spritePositionChanged = true;			
         invalidateDisplayList();
      }
      
      /**
       * Called when the user releases the mouse after during a drag operation.
       * 
       * @param event Event describing the user mouse interaction. 
       */
      private function stopSpriteDrag(mouseUp:Event):void
      {
         // Make sure the sprite isnt dragged anymore
         _sprite.stopDrag();
         Cursors.hideGrabCursor(mouseUp);
         
         // Remove in-drag mouse event listeners
         _sprite.removeEventListener(MouseEvent.MOUSE_MOVE, onSpriteMouseMove);
         stage.removeEventListener(MouseEvent.MOUSE_UP, stopSpriteDrag);
         _sprite.addEventListener(MouseEvent.MOUSE_DOWN, startSpriteDrag);
      } 
      
      /**
       * Calculates the area that the sprite can be dragged inside depending on 
       * the sprites current size and the display area and if scrollbars are used or not.
       * 
       * @return A rectablge describing the are inside which the sprite can be dragged. 
       */
      private function getSpriteDragRectangle():Rectangle
      {
         // Get info about hwo the display area looks based on the sprite's size.
         var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplayContext();
         
         // Define the sprites draggable area based on if scrollbars are used or not.
         return new Rectangle(
            ctx.overflowX ? (ctx.screenWidth - _sprite.width) : _sprite.x,
            ctx.overflowY ? ctx.screenHeight - _sprite.height : _sprite.y,
            ctx.overflowX ? ctx.screenWidth + ((_sprite.width - ctx.screenWidth) * 2) - _sprite.width : 0,
            ctx.overflowY ? ctx.screenHeight + ((_sprite.height - ctx.screenHeight) * 2) - _sprite.height : 0 
         );						
      }	
      
      /**
       * Called when this component is resized, in other words when the 
       * display area for the sprite changes. 
       * Makes sure the sprite is position is updated.
       * 
       * @param event Event describing the resizing of the display area. 
       */
      private function onResize(event:ResizeEvent):void
      {
         /**
          * Draw a transparent background so we can receive mouse scroll events
          * even when the zoomable sprite object is smaller than the display area.
          */
         graphics.clear();
         graphics.beginFill(0xFFCC00, 0);
         graphics.drawRect(0, 0, this.width, this.height);
         graphics.endFill();
         
         // Save the current screen size
         this.prevScreenHeight = (event.oldHeight == 0 ? this.height : event.oldHeight);
         this.prevScreenWidth = (event.oldWidth == 0 ? this.width : event.oldWidth);
         
         // Make sure the sprite is updated to the new display area
         displayResized = true;
         invalidateDisplayList();				
      }	
      
      /**
       * Helper method that creates an object that describes the dimensions  
       * of the display area and if scrollbars are used or not.
       * 
       * @param w The width of the sprite to base the context on (default is the sprites current width)
       * @param h The height of the sprite to base the context on (default is the sprites current height) 
       */
      public function getZoomSpriteDisplayContext(w:Number=-1, h:Number=-1):SpriteZoomDisplayContext
      {			
         var ctx:SpriteZoomDisplayContext = new SpriteZoomDisplayContext();
         
         // If no "virtual" values are supplied use the sprites current values
         h = Math.floor(h != -1 ? h : _sprite.height);
         w = Math.floor(w != -1 ? w : _sprite.width);
         
         // Let's first assume no scrollbars are needed
         ctx.screenWidth = this.width;
         ctx.screenHeight = this.height;
         ctx.screenWidthIncl = this.width;
         ctx.screenHeightIncl = this.height;
         
         // Is the child bigger than a screen area without scrollbars?
         ctx.overflowX = w > ctx.screenWidth;
         ctx.overflowY = h > ctx.screenHeight;			
         if (ctx.overflowY)
         {
            // A horizontal scrollbar was needed so screen width becomes smaller
            ctx.screenWidth = this.width - vsb.width;
         }
         if (ctx.overflowX)
         {
            // A vertical scrollbar was needed so screen height becomes smaller        	
            ctx.screenHeight = this.height - hsb.height;
         }
         
         // Re test again since we know the real display area
         ctx.overflowX = w > ctx.screenWidth;
         ctx.overflowY = h > ctx.screenHeight;			
         
         return ctx;
      }	   	   
      
      /* UPDATE DISPLAY AREA METHODS */
      
      /**
       * Called by the FLEX framework when this component needs to redraw itself.
       * I.e. because the sprite has been moved because of dragging or scrolling, 
       * or because it was zoomed etc.
       * 
       * Will redraw different display objects depending on what flags that has been set
       * in the methods that called invalidateDisplayList to make this method get called.
       *
       * @param unscaledWidth The unscaled width of this component
       * @param unscaledHeight The unscaled height of this component 
       */				
      override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void 
      {
         // Call super class updateDisplayList
         super.updateDisplayList(unscaledWidth, unscaledHeight);    
         
         if (_sprite)
         {		       	
            if (defaultPosition)
            {
               // Make sure it doesnt get's updated twice
               defaultPosition = false;
               
               // Call the actual default position method
               doDefaultPosition();
               
               // Make sure scrollbars adjust
               spritePositionChanged = true;								
            } 
            
            if (displayResized)
            {
               // Make sure it doesnt get's updated twice
               displayResized = false;
               
               // Call the actual resize position method
               doResizePosition();
               
               // Make sure scrollbars adjust
               spritePositionChanged = true;
            }
            
            if (spriteNewZoom != spritePrevZoom)
            {		         
               // scale/zoom has changed, call the zoom method
               doZoom();
            }
            
            if (spriteNewXChanged)
            {		         
               // A new x position for the sprite has been requested
               sprite.x = spriteNewX;	
               spriteNewXChanged = false;
               
               //Make sure scrollbars are updated
               spritePositionChanged = true;				
            }
            
            if (spriteNewYChanged)
            {		         
               // A new y position for the sprite has been requested
               sprite.y = spriteNewY;		
               spriteNewYChanged = false;
               
               //Make sure scrollbars are updated
               spritePositionChanged = true;			
            }
            
            if (spriteNewZoom != spritePrevZoom || spritePositionChanged)
            {   
               // Make sure we son't update scrollbars more than once
               spritePositionChanged = false;
               
               // Make sure scrollbars are updated         		
               doUpdateScrollbarsBasedOnSpritePosition();
            }
            
            if (vsbUpdated || hsbUpdated)
            {     
               // Call method to move sprite because of scrollbar changes
               doUpdateSpritePositionBasedOnScrollbarPosition();            		
            }
            
            if (spriteNewZoom != spritePrevZoom)
            {
               // Make sure we don't update display because of zoom/scale more than once
               spritePrevZoom = spriteNewZoom;
            }
            
         }		
         
      }
      
      private function doDefaultPosition():void
      {        	
         // Scale content
         _sprite.scaleX = spriteNewZoom;
         _sprite.scaleY = spriteNewZoom;
         
         // Get screen context after updating objects dimensions 
         var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplayContext();
         
         if (this.verticalDefaultPosition == "top")
         {
            // Place object on top of screen
            _sprite.y = 0;
         }
         else
         {
            // Center object vertically 
            _sprite.y = (ctx.screenHeight / 2) - (_sprite.height / 2)	            			            		            	
         }
         
         // Center the object horizontally on the screen
         _sprite.x = (ctx.screenWidth / 2) - (_sprite.width / 2);										
      }
      
      
      /**
       * Helper method called from updateDisplayList to position and size
       * sprite approppriately after the disply area has changed.
       */	        
      private function doResizePosition():void
      {
         // Center object to the middle and simply keep the old y position
         var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplayContext();
         
         _sprite.x = (ctx.screenWidth - _sprite.width) / 2;
         
         // Make sure content outside screen area is hidden
         this.scrollRect = new Rectangle(0, 0, this.width, this.height);	            	
      }
      
      /**
       * Helper method called from updateDisplayList to scale/zoom sprite
       * and also change its position since its dimension has changed.
       */	        
      private function doZoom():void
      {
         // Remember the sprites dimension before the scale
         var prevZoomWidth:Number = _sprite.width;
         var prevZoomHeight:Number = _sprite.height;
         
         // Adjust size (this will have immidiate effect on the width and height properties)
         _sprite.scaleX = spriteNewZoom;
         _sprite.scaleY = spriteNewZoom;
         
         var intWidth:int = new int(_sprite.width).valueOf();
         var intHeight:int = new int(_sprite.height).valueOf();
         
         // Get screen context after updating objects dimensions
         var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplayContext();
         
         // Find the new x position after the zoom 	 	        	
         var newX:Number;										
         if (Math.floor(_sprite.width) <= ctx.screenWidth)
         {
            // object is smaller than the screen area, simply center it.
            newX = (ctx.screenWidth / 2) - (_sprite.width / 2);						
         }
         else
         {					
            /**
             *  Object is larger than the screen area, move the object to left/right 
             *  depending on how much it was enlarged/shrinked
             */
            newX = _sprite.x + ((prevZoomWidth - _sprite.width) / 2);
            
            // Make sure object doesn't go out of bounds if it is shrinked...
            if (spriteNewZoom < spritePrevZoom)
            {	
               /**
                *  Make sure the objects right edge is aligned to the
                *  screens right edge or placed further right than the screens right edge.							  
                */				
               newX = Math.max(newX, ctx.screenWidth - _sprite.width);
               /**
                *  Make sure the objects left edge is aligned to the screens left edge
                *  or placed further left than the screens left edge ...							  
                */ 
               newX = Math.min(newX, 0);
            }				
         }
         // We have the new position now make sure its used
         _sprite.x = newX;	
         
         // Find the new y position after zoom							        																	
         var newY:Number;										
         if (Math.floor(_sprite.height) <= ctx.screenHeight)
         {
            // object is smaller than the screen area, simply center it.
            newY = (ctx.screenHeight / 2) - (_sprite.height / 2);						
         }
         else
         {
            // Make sure it feels like we zoom in and out of the center of the screen
            if (this.prevScreenHeight != ctx.screenHeightIncl)
            {
               newY = Math.round(_sprite.y * (spriteNewZoom / spritePrevZoom));
            }
            else
            {
               var currHeightCenterOffset:Number = _sprite.y - (ctx.screenHeight / 2);
               var futureHeightCenterOffset:Number = currHeightCenterOffset * (spriteNewZoom / spritePrevZoom);
               newY = Math.round(_sprite.y + futureHeightCenterOffset - currHeightCenterOffset);
            }
            
            // but also make sure object doesn't go out of bounds
            if (spriteNewZoom < spritePrevZoom)
            {					
               newY = Math.min(Math.max(newY, ctx.screenHeight - _sprite.height), 0);
            }
         }
         // We have the new position now make sure its used	
         _sprite.y = newY;	
      }
      
      /**
       * Helper method called from updateDisplayList to make sure the scrollbars
       * are displayed as expected after the sprite has been moved or scaled/zoomed.
       */
      private function doUpdateScrollbarsBasedOnSpritePosition():void
      {
         var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplayContext();
         
         // Adjust horizontal scrollbar	            	            	
         if (ctx.overflowX)
         {
            // Show the scrollbar since the sprite's height is larger than the display area's height
            hsb.y = this.height - hsb.height;
            hsb.width = ctx.screenWidth;
            hsb.visible = true;
            
            /**
             * Make sure the scollbar's scrollThumb is as wide as a page and 
             * positioned in relation to the sprite and the display area.
             */  
            hsb.setScrollProperties(Math.round(ctx.screenWidth), 0, Math.round(_sprite.width - ctx.screenWidth));
            hsb.lineScrollSize = Math.round(ctx.screenWidth) / 20;
            hsb.scrollPosition = Math.round(-1 * _sprite.x);	            		
         }
         else
         {
            // Sprite's height fits inside the display area so no scrollbar is needed
            hsb.visible = false;
         }	   					
         
         // Adjust vertical scrollbar	            	
         if (ctx.overflowY)
         {
            // Show the scrollbar since the sprite's width is larger than the display area's width        		
            vsb.x = this.width - vsb.width;
            vsb.height = ctx.screenHeight;
            vsb.visible = true;
            
            /**
             * Make sure the scollbar's scrollThumb is as high as a page and 
             * positioned in relation to the sprite and the display area.
             */           			            			            		
            vsb.setScrollProperties(Math.round(ctx.screenHeight), 0, Math.round(_sprite.height - ctx.screenHeight));
            vsb.lineScrollSize = Math.round(ctx.screenHeight) / 20;	            		
            vsb.scrollPosition = Math.round(-1 * _sprite.y);            		
         }	 
         else
         {
            // Sprite's width fits inside the display area so no scrollbar is needed
            vsb.visible = false;
         }	           	
      }  
      
      /**
       * Helper method called from updateDisplayList to make sure the sprite
       * is displayed as expected after the scrollbars have changed.
       */
      private function doUpdateSpritePositionBasedOnScrollbarPosition():void
      {
         var ctx:SpriteZoomDisplayContext = getZoomSpriteDisplayContext();
         
         if (ctx.overflowY && vsbUpdated)
         {
            /**
             * Vertical scrollbar is in use and has been moved, 
             * position sprite related to the new scrollbar position
             */ 
            vsbUpdated = false;
            var newZoomChildY:Number = -1 * (vsb.scrollPosition / (vsb.maxScrollPosition)) * (_sprite.height - ctx.screenHeight);
            _sprite.y = newZoomChildY;
         }
         
         if (ctx.overflowX && hsbUpdated)
         {
            /**
             * Horizontal scrollbar is in use and has been moved, 
             * position sprite related to the new scrollbar position
             */ 
            hsbUpdated = false;
            var newZoomChildX:Number = -1 * (hsb.scrollPosition / (hsb.maxScrollPosition)) * (_sprite.width - ctx.screenWidth);
            _sprite.x = newZoomChildX; 
         }
      }
      
   }
   
}

