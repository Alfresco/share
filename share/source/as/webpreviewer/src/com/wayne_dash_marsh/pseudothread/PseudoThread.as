/*
Copyright (c) 2007 Wayne Marsh

Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, 
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to the following 
conditions:

The above copyright notice and this permission notice shall be included in all copies 
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.wayne_dash_marsh.pseudothread
{
	import flash.display.MovieClip;
	import flash.events.EventDispatcher;
	import flash.utils.getTimer;
	import flash.events.Event;

	public class PseudoThread extends EventDispatcher
	{
		private var msAllowedPerFrame:int;
		private var frameEnterTime:int;
		
		private var frameListener:MovieClip;
		
		private var paused:Boolean, started:Boolean;
		
		/**
		 * Creates (but doesn't start) a PseudoThread object.
		 * 
		 * @param	msAllowedPerFrame	the milliseconds that a thread should be active for each frame
		 */
		public function PseudoThread(msAllowedPerFrame:int = 40) 
		{
			this.msAllowedPerFrame = msAllowedPerFrame;
			
			this.paused = this.started = false;
			
			// Set up movie clip to listen to ENTER_FRAME events
			this.frameListener = new MovieClip;
		}
		
		/**
		 * Starts the thread's execution.
		 * 
		 * @return	true if the thread state has been altered (i.e. false if already running)
		 */
		public final function start():Boolean
		{
			var changed:Boolean = false;
			
			if (!this.started)
			{
				this.setupListeners();
				
				this.started = true;
				
				changed = true;
				
				this.initialize();
			}
			
			return changed;
		}
		
		/**
		 * Stop's the thread's execution.
		 * 
		 * @return	true if the thread state has been altered
		 */
		public final function stop():Boolean
		{
			var changed:Boolean = false;
			
			if (this.started)
			{
				this.removeListeners()
				
				this.started = false;
				
				changed = true;
				
				this.kill();
			}
			
			return changed;
		}
		
		/**
		 * Pauses the thread's execution.
		 * 
		 * @return	true if the thread state has been altered
		 */
		public final function pause():Boolean
		{
			var changed:Boolean = false;
			
			if (this.started)
			{
				if (!this.paused)
				{
					this.paused = true;
					
					changed = true;
				}
			}
			
			return changed;
		}
		
		/**
		 * Unpauses the thread's execution.
		 * 
		 * @return	true if the thread state has been altered
		 */
		public final function unpause():Boolean
		{
			var changed:Boolean = false;
			
			if (this.started)
			{
				if (this.paused)
				{
					this.paused = false;
					
					changed = true;
				}
			}
			
			return changed;
		}
		
		/**
		 * Called each frame. Inheriting classes should override this method to provide their own thread body. Threads should behave well by watching for the active member to see when it is time to yield.
		 * An example function body is
		 * do
		 * {
		 * 		// Calculation
		 * } while (this.active)
		 * 
		 * @see	#active()
		 */
		protected function work():void
		{
			this.finish();
		}
		
		/**
		 * Called automatically when the thread is started. Inheriting classes should override this method to provide thread initialization.
		 */
		protected function initialize():void
		{	
		}
		
		/**
		 * Called automatically when the thread is stopped. Inheriting classes should override this method to provide thread cleanup.
		 */
		protected function kill():void
		{
		}
		
		/**
		 * Utility method called by an inheriting class to end the thread execution. This method automatically launches a PseudoThreadFinishEvent.
		 */
		protected function finish():void
		{
			this.removeListeners();
			this.started = false;
			
			this.dispatchEvent(new PseudoThreadFinishEvent(this));
		}
		
		/**
		 * Thread bodies (#work() methods) should check this property to see when it should stop calculating.
		 * 
		 * @return	true if the thread should continue, false if it should yield until the next frame
		 */
		protected final function get active():Boolean
		{
			var isActive:Boolean = false;
			
			if (0 != this.msAllowedPerFrame)
			{
				if (this.started)
				{
					if (!this.paused)
					{
						var timeActive:int = getTimer() - this.frameEnterTime;
						isActive = timeActive < this.msAllowedPerFrame;
					}
				}
			}
			else
			{
				isActive = true;
			}
						
			return isActive;
		}
		
		private function onEnterFrame(e:Event):void
		{
			if (!this.paused)
			{
				this.frameEnterTime = getTimer();
				
				work();
			}
		}
		
		private function setupListeners():void
		{
			this.frameListener.addEventListener(Event.ENTER_FRAME, onEnterFrame, false, 0, true);
		}
		
		private function removeListeners():void
		{
			this.frameListener.removeEventListener(Event.ENTER_FRAME, onEnterFrame, false);
		}
	}
	
}
