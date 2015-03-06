package madebypi.utils.transparenttextinput {
	
	/** Modified for AS3 (Based on JSTextReader version 1.1 (08/01/08) by astgtciv@analogcode.com)
	 *
	 * @author Mike Almond - MadeByPi - mike.almond@madebypi.co.uk
	 * @version AS3 1.0 (02/2009)
	 *
	 * All you need to do to enable this tool is pass a refernce to the flash Stage to the init function
	 * JSTextReader.getInstance().init(theFlashStage);
	 *
	 * After that the class will listen into all Event.CHANGE events on the currently focussed input textfield
	 * and act accordingly if an input character needs changing.
	 *
	 * If you're listening to the Event.CHANGE event of a TextField this should not be affected,
	 * you'll get the event after this tool has checked the text.
	 */
	
	/**
	 * More info from the original AS2 version...
	 *
	 * The JSTextReader attempts to work around a bug that occurs with textinput in flash
	 * when the swf is embedded with the wmode=transparent parameter. This bug occurs both in
	 * Firefox and Internet Explorer, albeit manifesting itself differently.
	 *
	 * In Firefox, no matter what keyboard layout the user has, the swf receives keystrokes as if
	 * a US keyboard were used. In Internet Explorer, we get the "2 for 1" bug, where the true 4-byte unicode
	 * characters received by the swf are split into 2 2-byte characters.
	 *
	 * JSTextReader wraps itself around a textfield. It sets up a javascript keystroke listener
	 * corresponding to this swf object, and updates the textfield with the keystrokes from javascript
	 * since javascript continues to receive correct char data.
	 *
	 * When embedding the swf object into the html page, please make sure that 'allowScriptAccess' is set to 'always' or 'sameDomain',
	 * and that the embed object has a unique id for that page.
	*/
	
	import flash.display.Stage;
	
	import flash.system.Capabilities;
	import flash.external.ExternalInterface;
	
	import flash.text.TextField;
	import flash.text.TextFieldType;
	
	import flash.ui.Keyboard;
	import flash.utils.clearTimeout;
	import flash.utils.setTimeout;
	
	import flash.events.Event;
	import flash.events.FocusEvent;
	import flash.events.KeyboardEvent;
	
	import madebypi.utils.transparenttextinput.EmbedObject;
	
	public class JSTextReader {
		
		private static var INSTANCE			:JSTextReader 	= null;
		private static var JS_PREFIX		:String 		= null;
		private static var JS_INITIALISED	:Boolean 		= false;
		
		private var _initialised			:Boolean;
		private var _stage					:Stage;
		private var _tf						:TextField;
		private var _lastText				:String;
		private var _curText				:String;
		private var _waitForJSTimeoutId		:int;
		private var _ctrlDown				:Boolean;
		private var _vDown					:Boolean;
		
		public function JSTextReader(l:Lock) {
			_ctrlDown = _vDown = false;
			initJS();
		}
		
		public static function getInstance():JSTextReader {
			if (INSTANCE == null) { INSTANCE = new JSTextReader(new Lock()); }
			return INSTANCE;
		}
		
		public function init(stage:Stage):void {
			if(JS_INITIALISED){
				if (stage == null) { throw new ReferenceError("JSTextReader::init - stage reference was null"); }
				if (_stage == null && !_initialised) {
					_stage = stage;
					_stage.addEventListener(KeyboardEvent.KEY_DOWN, onKeyEvent, true, 0, false);
					_stage.addEventListener(KeyboardEvent.KEY_UP, onKeyEvent, true, 0, false);
					_stage.addEventListener(FocusEvent.FOCUS_IN, onFocusChanged, true, 0, false);
					_stage.addEventListener(FocusEvent.FOCUS_OUT, onFocusChanged, true, 0, false);
					_initialised = true;
				}
			}
		}
		
		private function onKeyEvent(e:KeyboardEvent):void {
			//detect ctrl-v paste operations
			_ctrlDown = e.ctrlKey;
			//set the state of the V key
			if (e.type == KeyboardEvent.KEY_DOWN) {
				if (e.keyCode == 86) { _vDown = true; }
			}else{
				if (e.keyCode == 86) { _vDown = false; }
			}
		}
		
		public function dispose():void {
			disable();
			
			_stage.removeEventListener(FocusEvent.FOCUS_IN, onFocusChanged);
			_stage.removeEventListener(FocusEvent.FOCUS_OUT, onFocusChanged);
			_stage.removeEventListener(KeyboardEvent.KEY_DOWN, onKeyEvent);
			_stage.removeEventListener(KeyboardEvent.KEY_UP, onKeyEvent);
			
			_stage    	 = null;
			_tf 		 = null;
			_lastText	 = null;
			_initialised = false;
			INSTANCE  	 = null;
		}
		
		private function disable():void {
			if (_tf != null) {
				_tf.removeEventListener(Event.CHANGE, onChanged);
				_tf = null;
			}
		}
		
		private function onFocusChanged(e:FocusEvent):void {
			if (e.type == FocusEvent.FOCUS_OUT) {
				disable();
			}else{
				var focus:TextField = _stage.focus as TextField;
				if (focus != null && focus.type == TextFieldType.INPUT && focus != _tf) {
					disable();
					_tf = focus;
					//give it a high priority so we get the event before anything else
					_tf.addEventListener(Event.CHANGE, onChanged, false, 10000, true);
					_lastText = _tf.text;
				}
			}
		}
		
		private function onChanged(e:Event):void {
			// clear the timeout if it exists
			clearTimeout(_waitForJSTimeoutId);
			// if it's not a paste - get the text from js
			if (!(_ctrlDown && _vDown)) {
				// stop the event propogating - we'll re-issue it after we've checked it against the js input
				e.stopImmediatePropagation();
				//store the new text and set the display to the last input (flash) text
				_curText = _tf.text;
				// wait for the JS
				_waitForJSTimeoutId = setTimeout(onTextChanged, 1);
			}else{
				_lastText = _tf.text;
			}
		}
		
		private function onTextChanged():void {
			
			var ignore	:Boolean = false;
			var insert	:String  = getKeyboardInputFromJS();
			
			// Conceptually, the text in the tf is broken into 3 parts:
			// Prefix (before the newly inserted text), insert (the newly inserted text) and suffix (after the insert)
			// If the caret was at the end of text, the suffix is empty.
			
			var caretIndex		:int = _tf.caretIndex;
			var numCharsInserted:int = _curText.length - _lastText.length;
			
			if (!insert && numCharsInserted == 0) {
				// js/flash glitch that happens when typing really fast
				ignore = true;
			} else if (numCharsInserted <= 0) {
				// Either this is backspace at work, or else some part of text was highlighted and then replaced
				// if the last char coming from JS is backspace, assume backspace (or delete)
				var lastJSChar:String = insert.substr(insert.length-1);
				if ((!insert) || (lastJSChar == "\u0008") || (lastJSChar == "\u002E")) {
					ignore = true; // nothing needs to be done, delete/backspace work by themselves :)
				} else {
					// Some part of text was highlighted and then replaced
					numCharsInserted = computeNumberOfDifferingChars();
				}
			} else if (isIE() && numCharsInserted == 1) {
				// If we are in IE, and it appears that there is 1 char entered,
				// we better computeNumberOfDifferingChars() - because this could be the scenario
				// where 1 char is highlighted and 1 keyboard key pressed to replace it, resulting in
				// 2 chars being entered (IE "1 Unicode char -> 2 chars" bug, see description below near computeNumberOfDifferingChars()),
				// but since the difference is exactly 1 char... we better recompute to be sure.
				// Note this case covers just the (rather rare) scenario when
				// 1. A true 4-byte unicode keyboard input is in effect (e.g., cyrillic)
				// 2. A single char is highlighted and then replaced by a keyboard stroke
				// However, if a european (not a true 4-byte unicode keyboard) input is in effect,
				// we are taking a processing hit since this function ends up executed every time a key
				// is pressed, and this function can be expensive for long strings.
				// If performance becomes a problem, we can enable another setting for to enable this feature
				// (disabled by default).
				numCharsInserted = computeNumberOfDifferingChars();
			}
			
			if (!ignore && insert != null) {
				insert = processKeyboardInput(insert, numCharsInserted);
				var prefix		:String = _curText.substr(0, caretIndex - numCharsInserted);
				var origTFInsert:String = _curText.substr(caretIndex - numCharsInserted, numCharsInserted);
				var suffix		:String = _curText.substr(caretIndex);
				
				// if there is no keyboard input from js, assume it's a paste and keep the original tf insert
				if (!insert) { insert = origTFInsert; }
				_tf.text = prefix + insert + suffix;
			}
			
			_lastText = _tf.text;
			
			// don't listen the change event we're about to dispatch
			_tf.removeEventListener(Event.CHANGE, onChanged);
			// this is for anything else that may be listening in...
			_tf.dispatchEvent(new Event(Event.CHANGE));
			// re-apply the change listener
			_tf.addEventListener(Event.CHANGE, onChanged, false, 1000, true);
		}
		
		
		// Detects how many chars are different in _curText from_lastText.
		// The assumption is that _curText differs from_lastText by a single
		// substring in the middle. We need to do this
		// because of the IE version of the wmode=transparent bug, the "1 Unicode char -> 2 chars" bug.
		// This bug results in Unicode chars with values > 255 (2 bytes) being broken up into 2 chars
		// 2 bytes each and ending up that way in the textfield - as 2 chars (only for wmode=transparent).
		// To determine how many chars were entered, we traverse_lastText and _curText from
		// front and then back, noting the first char that is different.
		// Note that this function could be expensive if the strings are long.
		// Also note that the number of differing chars here is computed based on _curText,
		// not of _lastText.
		private function computeNumberOfDifferingChars():int {
			var numCharsInserted:int;
			var leftInsertIndex	:int = -1;
			var rightInsertIndex:int = -1;
			var n				:int = _curText.length;
			
			for (var i:int = 0; i < n; ++i) {
				if (leftInsertIndex == -1) {
					if (_lastText.charAt(i) != _curText.charAt(i)) {
						leftInsertIndex = i;
					}
				}
				if (rightInsertIndex == -1) {
					if (_lastText.charAt(_lastText.length - i - 1) != _curText.charAt(n - i - 1)) {
						rightInsertIndex = n - i - 1;
					}
				}
				if ((leftInsertIndex != -1) && (rightInsertIndex != -1)) { break; }
			}
			
			if ((leftInsertIndex != -1) && (rightInsertIndex != -1)) {
				numCharsInserted = (rightInsertIndex - leftInsertIndex) + 1;
			} else { numCharsInserted = 0; }
			
			return numCharsInserted;
		}
		
		private static function executeJS(s:String, noFunc:Boolean = false):String {
			var js:String = s;
			if (!noFunc) { js = "function() {"+js+"}"; }
			return ExternalInterface.call(js);
		}

		private function getKeyboardInputFromJS():String {
			var keyboardInput:String = executeJS("return "+unq("popKeyboardInput")+"();");
			if (keyboardInput != null && keyboardInput.length > 0){
				var codes	:Array 	= keyboardInput.split(',');
				var n		:int 	= codes.length;
				
				keyboardInput = "";
				for (var i:int = 0; i < n; ++i) {
					keyboardInput += String.fromCharCode(int(codes[i]));
				}
			}
			return keyboardInput;
		}
		
		private function processKeyboardInput(keyboardInput:String, numChars:int):String {
			// we remove a number of unrenderable chars (if garbage ends up being entered into the text field,
			// figure out the charcode(s) responsible and add them to this list).
			var removable:Array = ["\u0008", // backspace
								   // "\u002E", // delete comes in as 46 (002E), but so does ".", so we can't remove it
								   "\u00C0" // some artifact that happens when keyboard layouts are being switched
								   ];
								
			var n:int = removable.length;
			for (var i:int = 0; i < n; ++i) {
				keyboardInput = keyboardInput.replace(new RegExp(removable[i], "g"), "");
			}
			
			// we keep only the last numChars characters (however many were entered into the actual TF)
			// ideally, the best would be to leave just one char here...
			// but it appears that at least in some situations flash is not fast enough and
			// javascript starts to accrue several chars
			keyboardInput = keyboardInput.substr(keyboardInput.length - numChars);
			return keyboardInput;
		}
		
		private static function isJSAvailable():Boolean {
			return (ExternalInterface.available && executeJS("return true;") == "true");
		}
		
		/*
		 * Returns true if JS successfully initialized, for all successive calls to this
		 * method as well (even though JS is attempted to be initialized only once).
		 */
		private static function initJS():Boolean {
			// Javascript needs to be initialized only once
			var s:String;
			if (JS_INITIALISED) { return true; }
			if (!isJSAvailable()) {
				s = "Javascript not available, failed to set up JSTextReader. Please make sure that 'allowScriptAccess' is set to 'always' or 'sameDomain' in the embed, and that the embed object has an id attribute unique for the page.";
				if(EmbedObject.isInBrowser){
					throw new Error(s);
				}else {
					trace("[JSTextReader] Not in a browser!\n" + s + "\n");
				}
				return false;
			} else if (!EmbedObject.getEnumerableParam("id")) {
				s = "Can't access the embed id, failed to set up JSTextReader.";
				if(EmbedObject.isInBrowser){
					throw new Error(s);
				}else {
					trace("[JSTextReader] Not in a browser!\n" + s + "\n");
				}
				return false;
			}
			
			// Note that unique func/var ids have to be used everywhere to allow for two or more concurrent
			// INSTANCEs of the swf's runningJSTextReader to be used on the same page.
			
			executeJS(unq("onKey") + " = function (e) {" +
						"if (!"+unq("keyboardInput")+") { "+unq("keyboardInput")+" = '';}" +
						"if ("+unq("popKeyboardInputTimeout")+") { clearTimeout("+unq("popKeyboardInputTimeout")+"); "+unq("popKeyboardInputTimeout")+"='';}" +
						"var evtobj = window.event? event : e;" +
						"var unicode = evtobj.charCode? evtobj.charCode : evtobj.keyCode;" +
						// "var actualkey = String.fromCharCode(unicode);" +
						unq("keyboardInput")+" += ("+unq("keyboardInput")+"?',':'')+unicode;" +
						// if flash didn't come for it soon, it's most likely garbage and we dont' want it.
						unq("popKeyboardInputTimeout") + " = setTimeout(" + unq("popKeyboardInput") + ", 1000);" +
					"}");
			executeJS(unq("popKeyboardInput") + " = function() {" +
						  "var ret="+unq("keyboardInput")+";" +
						  unq("keyboardInput") + "='';" +
						  "return ret;" +
					  "}");
			executeJS("var embedObject = document.getElementById('" + EmbedObject.getEnumerableParam("id") + "');" +
					  "embedObject.onkeypress = "+unq("onKey")+";"
					  );
			
			JS_INITIALISED = true;
			return true;
		}

		/*
		 * Debugging utility
		 */
		private function toCharCodes(s:String):String {
			var r:String = "[";
			var n:int = s.length;
			for (var i:int = 0; i < n; ++i) { r += s.charCodeAt(i) + " "; }
			return r + "]";
		}
	
		/*
		 * Are we running in IE?
		 */
		private static function isIE():Boolean { return Capabilities.playerType == 'ActiveX'; }
		
		/*
		 * Returns the name of the js var/func with a unique prefix.
		 * TODO: Using window.[] variables might not work in a situation in which the html
		 * surrounding the flash is in a frame with its src loaded from a domain different from
		 * the domain of the top frame page. Perhaps this needs to be more versatile.
		 */
		private static function unq(s:String):String {
			if (!JS_PREFIX) { JS_PREFIX = "_jstr_" + int(Math.random() * 10000) + "_"; }
			return "window." + JS_PREFIX + s;
		}
	}
}
internal class Lock { };