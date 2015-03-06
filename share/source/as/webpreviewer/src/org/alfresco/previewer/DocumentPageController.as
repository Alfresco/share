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
   
   import com.wayne_dash_marsh.pseudothread.PseudoThread;
   
   import flash.display.MovieClip;
   import flash.events.TimerEvent;
   import flash.utils.Timer;
   
   /**
    * If a large movie clip is loaded with a lot of frames is loaded from the server
    * we can't create an instance for each frame. Instead we will create a smaller 
    * amount of instances of the movie clip and re use those instances and make sure 
    * they display the correct frame depending on which page that is showed.
    * 
    * This class is constantly told by the DocumentZoomDisplay class which page that
    * is the current page and how many pages that currenlty is visible.
    * This page will then take a movie clip instance from the pool, make sure it 
    * displays the frame corresponding to the page no then assign that movie clip to
    * the page so it's showed to the user.
    * 
    * NOTE ABOUT PERFORMANCE! 
    * 
    * The time it takes to change what frame a movie clip displays (by using 
    * goToAndPlay) varies a lot depending if the new frame is before or after the 
    * movie clips current frame. If a greater frame shall be displayed (after) it is
    * much faster than if lower frame (earlier) frame shall be displayed.  
    * 
    * The Flash Player runtime doesn't have thread support, which means that when a 
    * slow operatation is made, such as changing a bunch of movie clip instances 
    * frames from example values like 355 to 300 may freeze the ui in the browser.
    * 
    * Therefore imagine a situation where a user scrolls the document from the bottom
    * to the top for a document with 355 pages with the lowest zoom level. 
    * This class would get called for each time the page is changed. 
    * If this class would try to load all visible pages for each of those events
    * the browser would freeze on the first event and the scroll thumb would no longer
    * respond since the heavy goToAndPlay operations in the unthreaded runtime would 
    * freeze the flash ui.  
    * 
    * Therefore a couple of decisions has been taken to handle this:
    * 
    * a) When this class is told about a page change or a change of the number of visible
    *    pages it waits a number of milliseconds (defined by millisToWaitUntilAction)
    *    and sees if the values still are valid and hasn't been changed again, because
    *    if they have then it's not worth loading any pages because the user is not
    *    interested in those pages anymore.
    * 
    * b) When the class has decided that the the page loading shall start it loads
    *    one page at a time and after each load it checks if another page has been 
    *    requested.
    *
    * @author Erik Winlof
    */
   public class DocumentPageController extends Timer
   {
      /**
       * The number of milliseconds to wait until the page loading actually starts
       * to avoid unneccessarry loading of pages.
       */
      private var millisToWaitUntilAction:int = 100;
      
      /**
       * The number of milliseconds to wait until making the check if the time
       * has passed so the page loading can begin.
       */
      private var millisToWaitUntilActionTest:int = 50;
      
      /**
       * The number of visible pages that previously was asked to be made visible.
       */
      public var prevVisiblePages:int = 0;
      
      /**
       * The number of visible pages that is asked to be made visible.
       */		
      public var nextVisiblePages:int = 0;
      
      /**
       * The page that previously was asked to made visible.
       */		 
      public var prevPage:int = 0;
      
      /**
       * The page that is asked to be made visible. 
       */
      public var nextPage:int = 0;
      
      /**
       * The document to assign the loaded pages to.
       */
      public var doc:Document;
      
      /**
       * The last time the values for the current page or number of visible pages changed. 
       */
      private var lastMove:Date;
      
      /**
       * The class that imitates a thread and loads the pages 
       * (changes what frame the movie clip insteances display). 	
       */
      private var pageLoader:PageLoaderThread;
      
      /**
       * Constructor
       */
      public function DocumentPageController(doc:Document, dzd:DocumentZoomDisplay)
      {
         // Make sure timer doesn't repeat itself
         super(1, 0);
         
         // Save referecnce to the document and make sure we listen for changes in the display
         this.doc = doc;			
         dzd.addEventListener(DocumentZoomDisplayEvent.DOCUMENT_PAGE_SCOPE_CHANGE, onDocumentPageScopeChange);
         
         // Listen when this timer is started so we can decide if we shall load pages or not
         this.addEventListener(TimerEvent.TIMER, onTimer);
         
         // Create the thread that will do the page loading
         this.pageLoader = new PageLoaderThread(this);			
      }
      
      /**
       * This is called after a number of milliseconds to delay the decision if
       * page loading shall be done or not.
       */
      private function onTimer(event:TimerEvent):void
      {
         // Mark this timer as stopped so the running flag says false
         stop();
         
         // Check if the page or number of visible pages has changed since the timer was started
         if (this.prevPage != this.nextPage || this.prevVisiblePages != this.nextVisiblePages)
         {
            // Nothing has changed, have we waited long enough?
            if (new Date().time - this.lastMove.time > this.millisToWaitUntilAction)
            {	
               // Yes we have, the user has stopped the scrolling, paging or zooming
               this.prevPage = this.nextPage;
               this.prevVisiblePages = this.nextVisiblePages;
               
               // Start the page loading
               pageLoader.start();
            }
            else
            {
               // We have not waited long enough, wait a bit mor and see if anything changes
               start();	
            }
         }
         // Do nothing since the new page is the same as the previous one		
      }
      
      /**
       * Adds a movie clip instance that can be used in the internal page pool.
       * 
       * @param mc A movie clip instance to add to the page pool.
       */		
      public function addPageMovieClip(mc:MovieClip):void
      {
         pageLoader.addPageMovieClip(mc);		
      }
      
      /**
       * Returns the movie clip page pool.
       * 
       * @return An array of movie clips.
       */
      public function getPageMovieClips():Array
      {
         return pageLoader.getPageMovieClips();	
      }
      
      /**
       * The listener for changes in the document page display.
       * Will see if the pages that shall be displayed have changed, if they have
       * this class's timer availabilities will be used to postpone the decision if 
       * the pages shall be loaded, since the user might be scrolling to a new page
       * in a couple of milliseconds.
       * 
       * @param event Describes the new page and the new number of visible pages.
       */ 
      private function onDocumentPageScopeChange(event:DocumentZoomDisplayEvent):void
      {
         if (this.doc.getNoOfPages() > this.pageLoader.getPoolSize())
         {
            // Save the values from the event
            this.nextVisiblePages = event.visiblePages;
            this.nextPage = event.page;
            if (this.prevPage != this.nextPage || this.prevVisiblePages != this.nextVisiblePages)
            {
               /**
                * A new page or a new number of visible pages has been requested.
                * Make sure we reset the time we need to wait until the pages shall
                * be loaded.
                */ 				
               this.lastMove = new Date();
               if (!this.running)
               {				
                  // The timer is not active, start it  
                  this.delay = millisToWaitUntilActionTest;
                  start();
               }
            }
         }
         /*
         else
         {
         /**
         * Less pages than in the pool, so just set-up the correct page variables
         * and kick off the pseudo thread
         /
         this.nextVisiblePages = event.visiblePages;
         this.nextPage = event.page;
         this.prevVisiblePages = this.nextVisiblePages;
         this.prevPage = this.nextPage;
         this.pageLoader.start();
         }
         */
      }
      
   }
   
}

import com.wayne_dash_marsh.pseudothread.PseudoThread;
import org.alfresco.previewer.DocumentPageController;
import flash.display.MovieClip;
import org.alfresco.previewer.Page;

/**
 * This class handles the actual page loading.
 * It finds an unused movie clip instance, makes it display the
 * correct frame (page) and places it in the page in the document.
 * 
 * It loads one page/frame at a time and before it tries to load 
 * a new one it checks that the user hasn't requested a new page or 
 * that the time it has been given to load pages without stalling 
 * the ui hasn't been exceeded.
 * 
 * NOTE!
 * 
 * Since the Flash Player hasn't got thread support this class
 * extends the PseudoThread class. The pseudo thread will call
 * the work method where the actual loading will happen.
 * However to make the pseudo thread class work as expected 
 * the work method must honour the "active" member attribute
 * and only load pages as long as "active" is true.
 * When "active" is false it means that the pseudo thread thinks 
 * that the work method has taken too long time to process.
 * 
 * The work method uses the visiblePageIndex to know what movie clip to
 * load next.
 */
class PageLoaderThread extends PseudoThread
{
   /**
    * Reference to the page controller that contains info
    * about which page and how many pages that the user
    * would like to see.
    */ 
   private var dpc:DocumentPageController;
   
   /**
    * The pool of the movie clips that we can use to display as pages.
    */
   private var movieClipPool:Array = new Array();
   
   /**
    * Keeps track of which of the visible pages we currently are loading.
    */
   private var visiblePageIndex:int = 0;
   
   /**
    * The previous page the user asked for, if we compare it to the
    * new page the user is asking for we know if he is scrolling up or down.
    */
   private var prevPage:int = 0;
   
   /**
    * Keeps track of which movie clips in the pool that we have used
    * to display the current page and the one below.
    */
   private var usedMovieClipIndexes:Object = {};
   
   /**
    * Gets set when the loading actually starts.
    */
   private var loadDestinationFrame:int = 0;
   
   /**
    * The number of frames to load each time part of the movie is loaded.
    * The smaller value the more times the loader thread will listen for changes in the ui.
    * Just don't make it too small so too much time is spent checking for changes.
    */
   private var loadFrameStep:int = 12;
   
   /**
    * The pool index to the movie clip that is loading.
    */ 
   private var loadingMovieClipIndex:int = 0;
   
   /**
    * Constructor
    */
   public function PageLoaderThread(dpc:DocumentPageController) 
   { 
      this.dpc = dpc;
   } 
   
   /**
    * Add a movie clip to the movie clip pool.
    */
   public function addPageMovieClip(mc:MovieClip):void
   {
      movieClipPool.push(mc);
      loadAndDisplayPage(movieClipPool.length - 1, movieClipPool.length);		
   }
   
   /**
    * Returns the movie clip page pool.
    * 
    * @return An array of movie clips.
    */
   public function getPageMovieClips():Array
   {
      return movieClipPool;	
   }
   
   /**
    * Returns the number of movie clips in the pool.
    * 
    * @return the number of movie clips in the pool.
    */
   public function getPoolSize():int
   {
      return this.movieClipPool ? this.movieClipPool.length : 0;
   }
   
   /**
    * Called by the pseudo thread before work is called repeatedly 
    * until the thread is stopped.
    */
   protected override function initialize():void 
   {    	    	
      this.visiblePageIndex = 0;		
      this.usedMovieClipIndexes = {};
   } 
   
   /**
    * Called repeatedly by the pseudo thread until the thread is stopped.
    * This is where the loading is invoked.
    * Before a new page is loaded this method will check that the thread 
    * still is active, if it's not it will stop loading and wait for a 
    * new oppurtunity.
    * 
    * After loading all visible pages it will also try to load the page 
    * above the first visible page and the page after the last visible page.
    */ 
   protected override function work():void 
   {
      // Continue loading if it was interupted
      this.load();
      
      var mc:MovieClip;
      var j:int = 0;
      
      /**
       * This loop will load all visible pages (not the one before and after 
       * the visible pages). It will continue and load all visible pages as
       * long as the thred is active, in other word as long as the pseduo thread
       * think we have time ti load pages, and as long as the user hasn't 
       * requested a new page.
       */
      while (this.active 
         && this.visiblePageIndex < this.movieClipPool.length 
         && this.visiblePageIndex < this.dpc.nextVisiblePages
         && this.dpc.prevPage == this.dpc.nextPage 
         && this.dpc.prevVisiblePages == this.dpc.nextVisiblePages)
      {
         var loaded:Boolean = false;
         var savedPossibleFuturePagePoolIndex:int = -1;
         var page:int = this.dpc.nextPage + this.visiblePageIndex; 
         
         // First see if the page is loaded
         for (j = 0; j < this.movieClipPool.length; j++)
         {
            mc = this.movieClipPool[j];
            if (mc.currentFrame == (page))
            {				
               // The page was already loaded, make sure it's displayed.					  
               loadAndDisplayPage(j, page);					
               loaded = true;
               break;
            }
         }
         
         if (!loaded)
         {				
            // The page was not loaded, now find a free movie clip 
            for (j = 0; j < this.movieClipPool.length; j++)
            {
               mc = movieClipPool[j];
               
               /**
                * Try to find a movie clip that currently doesn't already 
                * display one of the other pages we will display later.
                */
               if (mc.currentFrame < this.dpc.nextPage - 1 
                  || mc.currentFrame > (this.dpc.nextPage + this.dpc.nextVisiblePages)						
                  || (this.dpc.nextVisiblePages > this.movieClipPool.length && mc.currentFrame > this.dpc.nextPage + this.visiblePageIndex))
               {
                  /**
                   * We found a movie clip that currently displays a frame we 
                   * are NOT interested in.
                   */
                  loadAndDisplayPage(j, page);
                  loaded = true;
                  break;
               }
               else if (mc.currentFrame == (this.dpc.nextPage - 1) || mc.currentFrame == (this.dpc.nextPage + this.dpc.nextVisiblePages))
               {
                  /**
                   * This clip can be used to display the page, but hopefully we 
                   * can save it since it already displays antther page that we 
                   * will need to display later.
                   */ 
                  savedPossibleFuturePagePoolIndex = j;
               }
            }
            
            if (!loaded && savedPossibleFuturePagePoolIndex != -1)
            {
               /**
                * We still haven't loaded the page, we will need to use the movie 
                * clip we hoped to be able to save for the page it currently displays.
                */					
               loadAndDisplayPage(savedPossibleFuturePagePoolIndex, page);
               savedPossibleFuturePagePoolIndex = -1;					
               loaded = true;
            }
            
         }
         
         // Increase the index to load so we don't try to this page again
         this.visiblePageIndex++;
      }
      
      /**
       * We come here when we have loaded all visible pages, when the movie clip pool is empty 
       * or when the thread has become inactive. 
       * If we have time and movie clips left load the clip before the first visible clip 
       * and the clip after the last visible clip. 
       */		 			
      if (this.visiblePageIndex < this.movieClipPool.length
         && this.dpc.prevPage == this.dpc.nextPage
         && this.dpc.prevVisiblePages == this.dpc.nextVisiblePages)
      {
         // Load as long as the thread is active and we have loaded all visible clips
         while (this.active 
            && (this.visiblePageIndex == this.dpc.nextVisiblePages 
               || this.visiblePageIndex == this.dpc.nextVisiblePages + 1))
         {				
            // Try to find a free clip...	
            for (j = 0; j < this.movieClipPool.length; j++)
            {
               // ... that hasn't been used
               if (!this.usedMovieClipIndexes[j])
               {
                  var preloadPage:int = 0;						
                  if (this.visiblePageIndex == this.dpc.nextVisiblePages)
                  {
                     /**
                      * First priority: decide which page depending if the user
                      * selected an earlier or later page than before
                      */
                     preloadPage = this.dpc.nextPage < this.prevPage ? this.dpc.nextPage - 1 : this.dpc.nextPage + this.dpc.nextVisiblePages;
                  }
                  else
                  {
                     /**
                      * Second priority: decide which page depending if the user
                      * selected an earlier or later page than before
                      */
                     preloadPage = this.dpc.nextPage < this.prevPage ? this.dpc.nextPage + this.dpc.nextVisiblePages : this.dpc.nextPage - 1 ;
                  }
                  loadAndDisplayPage(j, preloadPage);						
                  break;
               }
            }
            
            // Increase index so we don't try to load this page again
            this.visiblePageIndex++;
         }
      }
      
      /**
       * We come here when we have loaded ALL pages (both visisble and hidden) or when 
       * the thread has become inactive. Stop the thread if the user has requested a 
       * new page, the movie clip pool is empty or ALL pages are loaded.		 
       */
      if (this.dpc.prevPage != this.dpc.nextPage 
         || this.dpc.prevVisiblePages != this.dpc.nextVisiblePages 
         || this.visiblePageIndex >= this.movieClipPool.length 
         || this.visiblePageIndex >= this.dpc.nextVisiblePages + 2)
      {
         this.prevPage = this.dpc.nextPage;
         this.stop();
      }
      
   } 
   
   /**
    * Makes the movie clip display the frame specified by page and adds it to
    * the page in the document.
    * 
    * @param i The index in the movie clip pool for the movie clip that shall 
    * 		   display the frame specified by page
    * @param page The page number that shall display the movie clip 
    */
   private function loadAndDisplayPage(i:int, page:int):void
   {
      if (page < 1)
      {
         return;
      }
      
      // Remember the movie clip to use and the page to go to
      this.loadingMovieClipIndex = i;    	    	
      this.usedMovieClipIndexes[i] = true;
      this.loadDestinationFrame = page;
      
      // Start loading...	 			
      load();	
   }
   
   /**
    * Does the actual loading, in other words makes the chosen movie clip
    * in the pool display the frame/page.
    * 
    * If a movie clip's current frame is 300 and the frame to display is 250 it 
    * can take up to 2.5 seconds depending on how many frames exist.
    * 
    * Instead of making the clip display that directly and not be able to 
    * react to ui changes, such as scrolling or paging, it loads (moves the "frame cursor")
    * only parts of the frames at a time.
    */
   private function load():void 
   {
      // Find the movie clip to use
      var mc:MovieClip = this.movieClipPool[this.loadingMovieClipIndex];
      
      // Load as long as we have been given time from the thread
      while (this.loadDestinationFrame > 0 && this.active) // || mc.totalFrames < 11))
      {    		
         if (this.dpc.prevPage != this.dpc.nextPage 
            || this.dpc.prevVisiblePages != this.dpc.nextVisiblePages)
         {
            // The current page has changed, interrupt loading
            this.loadDestinationFrame = 0;
            return;
         }						
         else if (mc.currentFrame == this.loadDestinationFrame)
         {
            // We are finished loading, display the page
            var p:Page = this.dpc.doc.getChildAt(this.loadDestinationFrame - 1) as Page;			
            p.content = mc;
            this.loadDestinationFrame = 0;
            return;
         }
         else
         {				
            // Loading another part of the frames
            var nextFrame:int = mc.currentFrame + this.loadFrameStep;
            if (mc.currentFrame < this.loadDestinationFrame && this.loadDestinationFrame <= nextFrame)
            {
               // We are close to the destination frame, load it
               nextFrame = this.loadDestinationFrame;
            } 
            else if (nextFrame > mc.totalFrames)
            {
               // We have reached the end of the movie, start from the beginning
               nextFrame = 1;
            }				
            // Do the actual loading
            mc.gotoAndStop(nextFrame);	
         }
      }
   }
}
