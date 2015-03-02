/**
 * SWFPlayer class for the YUI SWFPlayer component.
 * Created by Alfresco to fit in the YUI library.
 *
 * @namespace YAHOO.widget
 * @class SWFPlayer
 * @uses YAHOO.widget.FlashAdapter
 * @constructor
 * @param containerId {HTMLElement} Container element for the Flash Player instance.
 */
YAHOO.widget.SWFPlayer = function(containerId, attributes)
{
   YAHOO.widget.SWFPlayer.superclass.constructor.call(this, YAHOO.widget.SWFPlayer.SWFURL, containerId, attributes);

   /**
    * Fires when if problems occur with the swf the user wanted to load.  
    *
    * @event swfError
    * @param event.type {String} The event type
    */
   this.createEvent("loadedSwfError");

	/**
	 * Fires when the swf the user wants to play has been loaded and is ready to use.
	 *
	 * @event swfReady
	 * @param event.type {String} The event type
	 * @param event.currentFrame {int} The index of the current frame
	 * @param event.totalFrames {int} The number of frames in the swf
	 */
	this.createEvent("loadedSwfReady");

	/**
	 * Fires when a frame has been entered.
	 *
	 * @event loadedSwfOnFrame
	 * @param event.type {String} The event type
	 * @param event.currentFrame {int} The index of the current frame
	 * @param event.totalFrames {int} The number of frames in the swf
	 */
	this.createEvent("loadedSwfOnFrame");
}

/**
 * Location of the Uploader SWF
 *
 * @property Chart.SWFURL
 * @private
 * @static
 * @final
 * @default "assets/uploader.swf"
 */
YAHOO.widget.SWFPlayer.SWFURL = "assets/SWFPlayer.swf";

YAHOO.extend(YAHOO.widget.SWFPlayer, YAHOO.widget.FlashAdapter,
{
   /**
    * Tests if the swfplayer is loaded.
    */
   init: function(debug)
   {
      try
      {
         this._swf.init(debug);
         return true;
      }
      catch(e)
      {
         return false;
      }
   },

   /**
    * Loads the swf that the SWFPlayer should play/control.
    *
    * @param swfUrl {string} the url to the swf that should be played.
    */
   load: function(swfUrl, doNavigate)
   {
      this._swf.load(swfUrl, typeof doNavigate == "boolean" ? doNavigate : true);
   },

   /**
    * Controls the loaded swf to display the frame specified by frameNo
    *
    * @param frameNo {int} The index of the frame to display
    * @param play {boolean} If true gotoAndPlay() will be used,
    *                       if false gotoAndStop() will be used
    */
	goToFrameNo: function(frameNo, scene, play)
	{
      this._swf.goToFrameNo(frameNo, scene ? scene : null, play ? true : false);
	},

   /**
    * Controls the loaded swf to display the frame specified by frameLabel
    *
    * @param frameNo {int} The index of the fram to display
    * @param play {boolean} If true gotoAndPlay() will be used,
    *                       if false gotoAndStop() will be used
    */
	goToFrameLabel: function(frameLabel, scene, play)
	{
		this._swf.goToFrameLabel(frameLabel, scene ? scene : null, play ? true : false);
	}

});
YAHOO.register("swfplayer", YAHOO.widget.SWFPlayer, {version: "2.6.0", build: "1321"});
