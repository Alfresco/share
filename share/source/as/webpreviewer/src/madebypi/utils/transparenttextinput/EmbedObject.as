package madebypi.utils.transparenttextinput {
	
	import flash.external.ExternalInterface;
	import flash.events.EventDispatcher;
	import flash.system.Capabilities;
	
	/** Modified for AS3 (Based on EmbedObject version 1.2 (07/02/07) by astgtciv@analogcode.com)
	 *
	 * @author Mike Almond - MadeByPi
	 * @version AS3 1.1 02/2009
	 *
	 * The EmbedObject class allows you to retrieve information about the browser Embed Object for this swf.
	 */
	
	public class EmbedObject extends EventDispatcher {
		
		// The user has the option of specifing the id explicitly.
		static public const EMBED_OBJECT_ID_PARAM	:String = "_embedObjectId";
		static public const EVENT_PARAMS_AVAILABLE	:String = "EmbedObjectParamsAvailable";
		
		static private const SET_CALLBACK_NAME		:String = "asorg_setEmbedObjectProps";
		static private const SET_VARIABLE_NAME		:String = "asorg_EmbedObjectProps";
		static private const JS_AUTOGEN_ID			:String = "if (!elts[i].getAttribute('id')) {elts[i].setAttribute('id','asorgid_'+Math.floor(Math.random()*100000));}";
		
		static private const TAG_NAMES				:Array = ["embed", "object"];
		
		static private var INSTANCE					:EmbedObject = null;
		
		private var embedObjectProps				:Object;
		private var attemptedPropsRetrieval			:Boolean;
		
		public function EmbedObject(lock:Lock) {
			attemptedPropsRetrieval = false;
			initialize();
		}
		
		private function initialize():void {
			initializeExternalCallback();
		}
		
		private function initializeExternalCallback():void {
			ExternalInterface.addCallback(SET_CALLBACK_NAME, external_SetEmbedObjectProps);
		}
		
		static public function getInstance():EmbedObject {
			if (INSTANCE == null) { INSTANCE = new EmbedObject(new Lock()); }
			return INSTANCE;
		}
			
		/////////////////////////// Interface ////////////////////////////////
		/*
		* Returns an object with all the enumerable params of the embed object - i.e., params which are enumerable in
		* a javascript loop over the object's attributes. The code always tries to return param 'id' as part of the object,
		* whether it is considered enumerable by the browser or not.
		*
		* Knowledge of the id property in params allows execution of javascript from flash
		* via ExternalInterface that references this embedded object (using document.getElementById()).
		*
		* If it is not possible to get EmbedObjectParams, this function returns undefined.
		*/
		static public function getEnumerableParams():Object {
			if (!EmbedObject.getInstance().embedObjectProps) {
				EmbedObject.getInstance()._getEmbedObjectProps();
			}
			return EmbedObject.getInstance().embedObjectProps;
		}

		/*
		* Returns the value of an enumerable parameter "param" in the embed object in the page (<embed> or <object>).
		* This is a shortcut, calling this function is equivalent to calling getEnumerableParams()[param].
		* To get a non-enumerable (but readable) parameter, use getParamViaExternal.
		*/
		static public function getEnumerableParam(param:String):String {
			return String(getEnumerableParams()[param]);
		}
		
		/*
		* This is a js shortcut to getting a readable (!) EmbedObject param dynamically via ExternalInterface.
		*/
		static public function getParamViaExternal(param:String):Object {
			return EmbedObject.getInstance().executeJS("return document.getElementById('"+getEnumerableParam('id')+"').getAttribute('" + param + "');");
		}
		
		/*
		* This is a js shortcut to setting an attribute on the EmbedObject dynamically via ExternalInterface.
		*/
		static public function setParamViaExternal(param:String, value:String):void {
			EmbedObject.getInstance().executeJS("document.getElementById('"+getEnumerableParam('id')+"').setAttribute('" + param + "', '"+value+"');");
		}
		
		static public function get isInBrowser():Boolean {
			return (Capabilities.playerType == "PlugIn" || Capabilities.playerType == "ActiveX");
		}

		
		//////////////// Implementation /////////////////////////////////////////
		private function _getEmbedObjectProps():Object {
			if (!isInBrowser) { return null; }
			if (!attemptedPropsRetrieval) {
				retrieveEmbedObjectProps();
				attemptedPropsRetrieval = true;
			}
			return embedObjectProps;
		}
		
		private function retrieveEmbedObjectProps():void {
			var n		:int = TAG_NAMES.length;
			var tagName	:String;
			for (var i:int = 0; i < n; ++i) {
				tagName = String(TAG_NAMES[i]);
				retrieveEmbedObjectPropsForTagname(tagName);
				if (embedObjectProps) {
					// got the props, no need to continue
					break;
				}
			}
		}
		
		// this function executes id-retrieving javascript searching for a particular tag name
		// (it will be called for "object" and "embed")
		private function retrieveEmbedObjectPropsForTagname(tagName:String):void {
			// We iterate though all the tags with tagName, if the SET_ID_CALLBACK_NAME method is supported, we call it.
			var js:String =  "var elts = document.getElementsByTagName('" + tagName + "'); for (var i=0;i<elts.length;i++) {if(typeof elts[i]." + SET_CALLBACK_NAME + " != 'undefined') { " + JS_AUTOGEN_ID + " var props = {}; props.id = elts[i].getAttribute('id'); for (var x=0; x < elts[i].attributes.length; x++) { props[elts[i].attributes[x].nodeName] = elts[i].attributes[x].nodeValue;} elts[i]." + SET_CALLBACK_NAME + "(props); }}";
			executeJS(js);
		}
		
		// Executes a chunk of javascript code via ExternalInterface and returns the result
		private function executeJS(js:String):Object {
			return Object(ExternalInterface.call("function() {" + js + "}"));
		}

		private function external_SetEmbedObjectProps(props:Object):void {
			this.embedObjectProps = props;
		}
	}
}
internal class Lock { };