package 
{

	import com.yahoo.yui.YUIAdapter;
	
	import flash.display.Bitmap;
	import flash.display.DisplayObject;
	import flash.display.Loader;
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.external.ExternalInterface;
	import flash.net.URLRequest;
	import flash.text.TextField;
	
	import mx.core.MovieClipAsset;
   
  	/**  
	 * @author Erik Winlof / Alfresco
	 */
	[SWF(backgroundColor=0xCCCCCC, width="550", height="400")]
	
	public class SWFPlayer extends YUIAdapter {
		 
		public function SWFPlayer()
		{
			super();						
		    t("SWFPlayer-version: 0.1b");
		    
		    // To debug/run this from Flexbuilder uncomment line below
		    //test();		    
		} 

		private function test():void
		{
			init(true);
			//load("http://localhost:8080/share/proxy/alfresco/api/node/content/workspace/SpacesStore/d90b9295-0e9e-4241-894d-ff4685107f83?a=true&alf_ticket=TICKET_e2d2ec15744338d4ddedbba6634cb69f7add69a5", false);
			load("http://localhost:8080/AVM2.swf", false);
		}
		
		override protected function initializeComponent():void {
			super.initializeComponent();

		    t("Adding callbacks");		    
			ExternalInterface.addCallback("init", init);
			ExternalInterface.addCallback("load", load);
			ExternalInterface.addCallback("goToFrameNo", goToFrameNo);
			ExternalInterface.addCallback("goToFrameLabel", goToFrameLabel);
      	}
 
		/*******************************************************************
		 * VARIABLES
		 *****************************************************************/

		/**
		 * Preloader
		 */
		[Embed(source="preloader.swf")]		
		public var Preloader:Class;
		public var preloader:MovieClipAsset;

		/**
		 * Debug
		 */
		private var output:TextField = new TextField();

		/**
		 * Request objectes to load external movie clip
		 */
		private var request:URLRequest;
		private var loader:Loader;
		private var url:String;	
		private var doNavigation:Boolean;
		
		/**
		 * The loaded external content/swf
		 */		
		private var content:DisplayObject;
		private var padding:int = 0;				
		
		/**
		 * State information to pass back to the browser
		 */
		private var currentFrame:int = -1;
		  		
		  		 
		/*******************************************************************
		 * BROWSER CALLBACKS
		 *****************************************************************/

		/**
		 * Sets the debug mode and can be used by the browser to test if the flash movie has been loaded.
		 * 
		 * @param debug If true a textfield with debug info will be displayed
		 */
		public function init(debug:Boolean):void 
		{
			t("init: debug=" + debug);
         	if(debug)
         	{
	         	output.border = true; 
	         	output.width = stage.stageWidth;
	         	output.height = stage.stageHeight; 
	         	stage.addChild(output);         		
         	}
 	        	    
  		}

        /** 
	     * Loads and displays the flash movie specified by url.
	     * When the movie is loaded and ready to use a "swfReady" is dispatched.
	     * 
	     * @param url The url to swf to load and display
	     */
		public function load(url:String, doNavigation:Boolean=true):void 
		{
    	    if(content != null && stage.contains(content))  
    	    {
	    	    t("Remove old content");    	        	    
    	    	stage.removeChild(content);    	    	
    	    }      	        	    
    	    if(preloader == null )
    	    {
	    	    t("Create preloader");    	        	    
    	    	preloader = MovieClipAsset(new Preloader());
    	    }
    	    if(!stage.contains(preloader))
    	    {
	    	    t("Add preloader");    	        	    
    	    	center(preloader);
    	    	stage.addChild(preloader);
    	    }
			this.url = url;
			this.doNavigation = doNavigation;
			loadSwf(url);
		}
			
		/** 
	     * Makes the loaded content display a frame specified by a number
	     * 
	     * @param frameNo The frame to display
	     * @param scene The scene to display
	     * @param if true gotoAndPLay will be used, otherwise gotoAndStop
	     */
		public function goToFrameNo(frameNo:int, scene:String=null, play:Boolean=false):void 
		{
	        t("gotoFrame:" + frameNo + ":" + scene + " (" + play + ")");	
	        goTo(frameNo, scene, play);
        } 

		/** 
	     * Makes the loaded content display a frame specifiedd by a label
	     * 
	     * @param frameNo The frame to display
	     * @param scene The scene to display
	     * @param if true gotoAndPLay will be used, otherwise gotoAndStop
	     */
		public function goToFrameLabel(frameLabel:String, scene:String=null, play:Boolean=false):void 
		{
	        t("gotoLabel:" + frameLabel+ ":" + scene + " (" + play + ")");	
	        goTo(frameLabel, scene, play);
        }

		/*******************************************************************
		 * BROWSER EVENTS
		 *****************************************************************/

		/** 
	     * Creates an "loadedSwfError" event that is dispatched to the browser
	     * when the loading of the external content/swf has gone wrong.
	     * 
	     * @param code The error code
	     */
		public function loadedSwfError(code:String):void
		{
			t("loadedSwfError");	
			loader.contentLoaderInfo.removeEventListener(Event.COMPLETE, onMovieClipComplete);					
			loader.contentLoaderInfo.removeEventListener(IOErrorEvent.IO_ERROR, onError);
	        var newEvent:Object = new Object();
			newEvent.type = "loadedSwfError";
			newEvent.code = code;
        	super.dispatchEventToJavaScript(newEvent);			
		}		 
		
		/** 
	     * Creates an "loadedSwfReady" event that is dispatched to the browser when the
	     * external content/swf is added to the stage and ready to be controlled.
	     * 
	     * @param event
	     */
		public function loadedSwfReady (event:Event) : void 
		{
			t("loadedSwfReady");	
			stage.removeEventListener(Event.ADDED,loadedSwfReady);
			var newEvent:Object = new Object();
			newEvent.type = "loadedSwfReady";
			if(doNavigation && content is MovieClip)
			{
				var mc:MovieClip = content as MovieClip;
				newEvent.currentFrame = mc.currentFrame; 
				newEvent.totalFrames = mc.totalFrames; 
			}
			else
			{
				newEvent.currentFrame = 1; 
				newEvent.totalFrames = 1;
			}
			super.dispatchEventToJavaScript(newEvent);
		}

		/** 
	     * Creates an "loadedSwfOnFrame" event that is dispatched to the browser
	     * when a new frame has been entered in the external content/swf.
	     * 
	     * @param code The error code
	     */	
      	public function loadedSwfOnFrame (event:Event) : void 
      	{
			//t("loadedSwfOnFrame:" + currentFrame + "!=" +  mc.currentFrame);				
      		if(doNavigation && content is MovieClip)
      		{
      			var mc:MovieClip = content as MovieClip;      		
      			if(currentFrame != mc.currentFrame)
      			{      			      		
	      			currentFrame = mc.currentFrame;
	      			
					t("loadedSwfOnFrame");				
		        	var newEvent:Object = new Object();
		         	newEvent.type = "loadedSwfOnFrame";
					newEvent.currentFrame = mc.currentFrame; 
					newEvent.totalFrames = mc.totalFrames; 
		         	super.dispatchEventToJavaScript(newEvent);
		       }
	        }
      	}

		
		/*******************************************************************
		 * FLASH LOAD EVENT CALLBACKS
		 *****************************************************************/

		/** 
	     * Called by flash when the loading of the external swf has gone wrong.
	     * 
	     * @param event an error event
	     */ 
		public function onError(event:Event):void
		{
			loadedSwfError("error.server");
		}
 

		/** 
	     * Called by flash when the loading of the external swf is complete.
	     * Will handle the preloader, add the content to the stage and notify the browser.
	     * 
	     * @param event onCompleteEvent
	     */ 		
		private function onMovieClipComplete(event:Event):void 
		{						
    	    if(stage.contains(preloader))
    	    {
	    	    t("Remove preloader");    	        	    
				stage.removeChild(preloader);
    	    }
    	    if(loader.content is MovieClip)
    	    {    
    	    	t("Cast external content to a movie clip");	        	        	   
				var imc:MovieClip = MovieClip(loader.content);
				content = imc;				
				if(doNavigation)
				{ 
					imc.gotoAndStop(1);
				}
				currentFrame = -1;	
				stage.addEventListener(Event.ADDED, loadedSwfReady);				
				scale(imc); //, loader.contentLoaderInfo.width, loader.contentLoaderInfo.height);
				center(imc);
    	    	t("Add movie clip to stage as clip");
	    		stage.addChild(imc);	
    	    }
    	    else if(event.currentTarget.loader.content is flash.display.Bitmap)
    	    {    
    	    	t("Cast external content to a bitmap");	        	        	   
				var bm:Bitmap = Bitmap(loader.content);				
			    content = bm;
				scale(bm);
				center(bm);
				stage.addEventListener(Event.ADDED, loadedSwfReady);				
    	    	t("Add bitmap to stage");	        	        	   
	        	stage.addChild(bm); 
    	    }
    	    else if(event.target.actionScriptVersion == 2)
	    	{
    	    	content = loader.content;
				//scale(content);
				center(content);
    	    	stage.addChild(content);    	    		
    	    }
    	    else{
    	    	loadedSwfError("error.content");
    	    	t("Can't display url because loaded content is not a bitmap or movieclip.");
	    	    t("Description of loaded content:" + loader.content.toString());    	        	    
	    	    t("Actionscript version of loaded content:" + event.target.actionScriptVersion);    	        	    	    	    	    	    
    	    }
		}
			

		/*******************************************************************
		 * HELPER METHODS
		 *******************************************************************		
		
		/** 
	     * The actual load method.
	     * 
	     * @param url The url to swf to load and display
	     */ 
		private function loadSwf(url:String):void
		{
    	    t("Load:" + url);    	        	    
    	    request = new URLRequest(url);
			loader = new Loader(); 
			loader.load(request);
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, onMovieClipComplete);					
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, onError);						
		}
		
		/** 
	     * Scales the object to fit on the screen and respect the padding
	     * MovieClips are always scaled
	     * Bitmaps are only shrinken, not enlarged
	     * 
	     * @param obj The object to scale (the loaded content or the previewr)
	     */ 
		private function scale(obj:DisplayObject):void
		{
			var newObjWidth:Number = stage.stageWidth - (padding * 2);
			var scaleFactor:Number = newObjWidth / obj.width;
			var newObjHeight:Number = obj.height * scaleFactor;
			if(newObjHeight > (stage.stageHeight - (padding * 2)))
			{				
				newObjHeight = stage.stageHeight - (padding * 2);
				scaleFactor = newObjHeight / obj.height;
				newObjWidth = obj.width * scaleFactor;
			}			
			if(obj is Bitmap && (newObjWidth > obj.width || newObjHeight > obj.height))
			{
				// Do not scale since its better to watch the image in its orginigal size thaan to enlarge it
				return;
			}
			obj.width = newObjWidth;		
			obj.height = newObjHeight;		
		}
		
		/** 
	     * Centers the object on the screen
	     * 
	     * @param obj The object to scale (the loaded content or the previewr)
	     */
 		private function center(obj:DisplayObject):void
		{
			obj.x = (stage.stageWidth / 2) - (obj.width / 2);
			obj.y = (stage.stageHeight / 2) - (obj.height / 2);			
		}		

		/** 
	     * Goes to a specific frame and makes sure flash listeners are listening 
	     * so they can dispatch events to the browser.
	     * 
	     * @param obj The object to scale (the loaded content or the previewr)
	     */
		private function goTo(frame:Object, scene:String=null, play:Boolean=false):void 
		{
		    t("Do goTo?");  
			if(doNavigation && content is MovieClip)
      		{
      			t("Do goTo since its a movie clip");  
      			var mc:MovieClip = content as MovieClip;      		      		
		        if(!mc.hasEventListener(Event.ENTER_FRAME)) 
		        {
		        	mc.addEventListener(Event.ENTER_FRAME, loadedSwfOnFrame);
		        }
		        if(play)
		        {
			        mc.gotoAndPlay(frame, scene);	        	
		        }
		        else
		        {
			        mc.gotoAndStop(frame, scene);	        		        	
		        }
			}
        }
	      
      	/**
		 * Prints out debug info
		 * @param newText The debug info to print out
		 */
		private function t(newText:String):void {
			if(newText) output.text = newText + "\n" + output.text;
		}

	}

}

