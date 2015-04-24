/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var WebPreview = function() {
	
	function onWebPreviewerEvent(event)
	{
	    if (event.error)
	    {
	        // Inform the user about the failure
	        var message = "Error";
	        if (event.error.code)
	        {
	            message = messages["error." + event.error.code];
	        }
	        alert(message);
	    }
	}
	
	return {
		render : function (context, id, uri, name, messages) 
		{
			/*if (<check if flash player greater than 9.0.45 is installed >)
			{*/
				// Change the class of the containing div to be taller etc
		        var container = document.getElementById("web-preview-container");
		        container.className = "web-preview-content";

		        // Create flash web preview by using swfobject
		        var swfId = "WebPreviewer_" + id;
		        
		        var flashvarsObj = {"fileName" : name,
		        					"paging" : true,
		        					"url" : uri,
		        					"i18n_actualSize" : messages["preview.actualSize"],
		        					"i18n_fitPage" : messages["preview.fitPage"],
		        					"i18n_fitWidth" : messages["preview.fitWidth"],
		        					"i18n_fitHeight" : messages["preview.fitHeight"],
		        					"i18n_fullscreen" : messages["preview.fullscreen"],
		        					"i18n_fullwindow" : messages["preview.fullwindow"],
		        					"i18n_fullwindow_escape" : messages["preview.fullwindowEscape"],
		        					"i18n_page" : messages["preview.page"],
		        					"i18n_pageOf" : messages["preview.pageOf"],
		        					"show_fullscreen_button" : false,
		        					"show_fullwindow_button" : false};
		        
		        var parObj = {"allowScriptAccess" : "sameDomain", 
		        		      "allowFullScreen" : "true", 
		        		      "wmode" : "transparent"};
		        
		        swfobject.embedSWF(context+"/swf/preview/WebPreviewer.swf", 
		        				   "web-preview",
		        		           "100%", "100%", "9.0.45", null, flashvarsObj, parObj, null, onWebPreviewerEvent);	        
			//}
		}
	};
}();
