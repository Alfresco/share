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
	import flash.events.Event;

	public class PseudoThreadFinishEvent extends Event
	{
		public static const FINISHED:String = "PseudoThreadFinishEvent_FINISHED";
		
		private var launcher:PseudoThread;
		
		/**
		 * Creates the event object, alerting a listener of a milestone in a PseudoThread's life.
		 * 
		 * @param	launcher	the PseudoThread object that caused the event to be launched
		 */
		public function PseudoThreadFinishEvent(launcher:PseudoThread) 
		{
			super(FINISHED);
			
			this.launcher = launcher;
		}
		
		public override function clone():Event
		{
			return new PseudoThreadFinishEvent(this.thread);
		}
		
		public function get thread():PseudoThread
		{
			return this.launcher;
		}
	}
	
}
