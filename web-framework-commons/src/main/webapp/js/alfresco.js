/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

/**
 * YUI Library aliases
 * Deliberately named differently to the ones various components and modules use, to avoid unexpected behaviour.
 */
var YUIDom = YAHOO.util.Dom,
      YUIEvent = YAHOO.util.Event,
      YUISelector = YAHOO.util.Selector,
      YUIKeyListener = YAHOO.util.KeyListener;

/**
 * YUI Config
 * There are some bugs (see ALF-16878) in IE8's native stringify method, so we don't want to use it.
 */
if (YAHOO.env.ua.ie == 8)
{
   YAHOO.lang.JSON.useNativeStringify = false;
}

/**
 * Alfresco root namespace.
 *
 * @namespace Alfresco
 */
// Ensure Alfresco root object exists
if (typeof Alfresco == "undefined" || !Alfresco)
{
   var Alfresco = {};
}

/**
 * Alfresco top-level constants namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.constants
 */
Alfresco.constants = Alfresco.constants || {};

/**
 * Alfresco CSRF Policy.
 *
 * @namespace Alfresco.constants
 * @class Alfresco.constants.CSRF_POLICY
 */
Alfresco.constants.CSRF_POLICY = Alfresco.constants.CSRF_POLICY || {enabled: false};

/**
 * Alfresco IFrame Policy.
 *
 * @namespace Alfresco.constants
 * @class Alfresco.constants.IFRAME_POLICY
 */
Alfresco.constants.IFRAME_POLICY = Alfresco.constants.IFRAME_POLICY || {sameDomain: "allow", crossDomainUrls: ["*"]};

/**
 * Alfresco top-level template namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.template
 */
Alfresco.template = Alfresco.template || {};

/**
 * Alfresco top-level component namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.component
 */
Alfresco.component = Alfresco.component || {};

/**
 * Alfresco top-level dashlet namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.dashlet
 */
Alfresco.dashlet = Alfresco.dashlet || {};

/**
 * Alfresco top-level module namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.module
 */
Alfresco.module = Alfresco.module || {};

/**
 * Alfresco top-level util namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.util
 */
Alfresco.util = Alfresco.util || {};

/**
 * Alfresco top-level logger namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.logger
 */
Alfresco.logger = Alfresco.logger || {};

/**
 * Alfresco top-level service namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.service
 */
Alfresco.service = Alfresco.service || {};

/**
 * Alfresco top-level thirdparty namespace.
 * Used for importing third party javascript functions
 *
 * @namespace Alfresco
 * @class Alfresco.thirdparty
 */
Alfresco.thirdparty = Alfresco.thirdparty || {};

/**
 * Alfresco top-level widget namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.widget
 */
Alfresco.widget = Alfresco.widget || {};

/**
 * Alfresco top-level admin namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.Admin
 */
Alfresco.admin = Alfresco.admin || {};

/**
 * Alfresco top-level action namespace.
 * Used as a namespace for common functionality reused by multiple components.
 *
 * @namespace Alfresco
 * @class Alfresco.action
 */
Alfresco.action = Alfresco.action || {};

/**
 * Alfresco top-level doclib namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.doclib
 */
Alfresco.doclib = Alfresco.doclib ||
{
   MODE_SITE: 0,
   MODE_REPOSITORY: 1
};

/**
 * Alfresco top-level messages namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.messages
 */
Alfresco.messages = Alfresco.messages ||
{
   global: null,
   scope: {}
};

/**
 * Checks whether particular plugin for associated mimetype is installed
 *
 * @method Alfresco.util.isBrowserPluginInstalled
 */
Alfresco.util.isBrowserPluginInstalled = function(mimeType)
{
   return navigator.mimeTypes && navigator.mimeTypes[mimeType] && navigator.mimeTypes[mimeType].enabledPlugin;
};

/**
 * Checks whether Mac browser plugin for SharePoint is installed
 *
 * @method Alfresco.util.appendArrayToObject
 */
Alfresco.util.isSharePointPluginInstalled = function()
{
   var webkitPluginInstalled = Alfresco.util.isBrowserPluginInstalled("application/x-sharepoint-webkit");
   var npapiPluginInstalled = Alfresco.util.isBrowserPluginInstalled("application/x-sharepoint");
   if (YAHOO.env.ua.webkit && webkitPluginInstalled)
      return true;
   return npapiPluginInstalled != undefined;
};

/**
 * Check whether the location is valid for online editing in MS Office application
 *
 * @method Alfresco.util.validLocationForOnlineEdit
 * @param location {Object} (Required) Object describing the location of the file to edit
 * @param location.site {Object} Object describing the site the file is located in
 * @param location.site.name {String} The shortname of the site the file is located in
 * @param location.container {Object} Object describing the container the file is located in
 * @param location.container.name {String} The name of the container the files is located in
 * @param location.path {String} The path to the file inside the container
 * @param location.file {String} The name of the file to edit
 * @return {Boolean} Whether the location is valid for editing online
 */
Alfresco.util.validLocationForOnlineEdit = function(location)
{
   var pathToCheck = Alfresco.util.combinePaths(location.tenant ? location.tenant : "",
                                                location.site ? location.site.name : "",
                                                location.container ? location.container.name : "",
                                                location.path,
                                                location.file);
   var path = pathToCheck.split("/");
   
   for (i = 0, j = path.length; i < j; i++)
   {
      if ((/[~"#%&*:<>?\/\\{|}]/).test(path[i]))
      {
         return false;
      }
   }
   return true;
};

/**
 * Creates a url for online editing with sharepoint.
 *
 * @method Alfresco.util.onlineEditUrl
 * @param vtiServer {Object} (Required) Vti Server config object
 * @param vtiServer.protocol {String} (Optional) The protocol to use (Defaults to the current page's protocol, i.e. "http" or "https")
 * @param vtiServer.host {String}
 * @param vtiServer.port {Number}
 * @param vtiServer.contextPath {String}
 * @param location {Object} (Required) Object describing the location of the file to edit
 * @param location.site {Object} Object describing the site the file is located in
 * @param location.site.name {String} The shortname of the site the file is located in
 * @param location.container {Object} Object describing the container the file is located in
 * @param location.container.name {String} The name of the container the files is located in
 * @param location.path {String} The path to the file inside the container
 * @param location.file {String} The name of the file to edit
 * @return {String} The url to where the document can be edited online
 */
Alfresco.util.onlineEditUrl = function(vtiServer, location)
{
   // Thor: used by overridden JS to place the tenant domain into the URL.
   var tenant = location.tenant ? location.tenant : "";
   var onlineEditUrl = vtiServer.host + ":" + vtiServer.port + "/" +
      Alfresco.util.combinePaths(vtiServer.contextPath, tenant, (location.container && location.site) ? location.site.name : "", location.container ? encodeURIComponent(location.container.name) : "", encodeURIComponent(location.container ? location.path : location.path.substring(location.path.indexOf("/", 1))).replace(/%2F/g, "/"), encodeURIComponent(location.file));
   if (!(/^(http|https):\/\//).test(onlineEditUrl))
   {
      // Did they specify the protocol on the vti server bean?
      var protocol = vtiServer.protocol;
      if (protocol == null)
      {
         // If it's not set, assume it's the same as Share
         protocol = window.location.protocol;
         // Get it without the trailing colon, to match the vti property form
         protocol = protocol.substring(0, protocol.length-1);
      }

      // Build up the full HTTP / HTTPS URL
      onlineEditUrl = protocol + "://" + onlineEditUrl;
   }
   return onlineEditUrl;
};

/**
 * Creates a url for online editing with the AOS sharepoint implementation.
 *
 * @method Alfresco.util.onlineEditUrlAos
 * @param aos {Object} (Required) AOS Server config object
 * @param aos.baseUrl {String} (Required) The base URL where the AOS implementation is available
 * @param record {Object} (Required) Object describing the file to edit
 * @param record.webdav {String} The relative webdav path of the file
 * @return {String} The url to where the document can be edited online
 * @throws {Error}
 */
Alfresco.util.onlineEditUrlAos = function(aos, record)
{
   // sanity checks
   if (!Alfresco.util.isValueSet(aos) || !Alfresco.util.isValueSet(aos.baseUrl) || !Alfresco.util.isValueSet(record.webdavUrl) || (record.webdavUrl.substring(0,8) != '/webdav/') )
   {
      throw new Error("Alfresco.util.onlineEditUrlAos: Sanity checks failed.");
   }
   // obtain the path in the repository from the webdav URL
   var pathInRepository = record.webdavUrl.substring(7);
   // Construct a URL from the path in the repo
   var result = Alfresco.util.combinePaths(aos.baseUrl, pathInRepository);
   // Check if the length of the encoded path exceeds 256 characters. If so, use the nodeid instead of the path
   if (encodeURI(result).length > 256)
   {
      var filenameSepIdx = record.webdavUrl.lastIndexOf("/");
      if(filenameSepIdx < 0)
      {
         throw new Error("Alfresco.util.onlineEditUrlAos: Missing filename in webdav URL.");
      }
      var filename = record.webdavUrl.substring(filenameSepIdx + 1);
      result = Alfresco.util.combinePaths(aos.baseUrl, "_aos_nodeid", record.nodeRef.split("/").pop(), filename)
   }
   // If URL still exceeds 256 characters, we also need to replace the filename
   if(encodeURI(result).length > 256)
   {
      var fileextSepIdx = record.webdavUrl.lastIndexOf(".");
      result = Alfresco.util.combinePaths(aos.baseUrl, "_aos_nodeid", record.nodeRef.split("/").pop(), "Document" + (fileextSepIdx > 0 ? record.webdavUrl.substring(fileextSepIdx) : ''));
   }
   return result;
};

/**
 * Appends an array onto an object
 *
 * @method Alfresco.util.appendArrayToObject
 * @param obj {object} Object to be appended to
 * @param arr {array} Array to append/merge onto object
 * @param p_value {object} Optional: default value for property.
 * @return {object} The appended object
 * @static
 */
Alfresco.util.appendArrayToObject = function(obj, arr, p_value)
{
   var value = (p_value !== undefined ? p_value : true);

   if (YAHOO.lang.isObject(obj) && YAHOO.lang.isArray(arr))
   {
      for (var i = 0, ii = arr.length; i < ii; i++)
      {
         if (arr[i] !== undefined)
         {
            obj[arr[i]] = value;
         }
      }
   }
   return obj;
};

/**
 * Convert an array into an object
 *
 * @method Alfresco.util.arrayToObject
 * @param arr {array} Array to convert to object
 * @param p_value {object} Optional: default value for property.
 * @return {object} Object conversion of array
 * @static
 */
Alfresco.util.arrayToObject = function(arr, p_value)
{
   var obj = {},
         value = (p_value !== undefined ? p_value : true);

   if (YAHOO.lang.isArray(arr))
   {
      for (var i = 0, ii = arr.length; i < ii; i++)
      {
         if (arr[i] !== undefined)
         {
            obj[arr[i]] = value;
         }
      }
   }
   return obj;
};

/**
 * Copies the values in an object into a new instance.
 *
 * Note 1. This method ONLY copy values of type object, array, date, boolean, string or number.
 * Note 2. Functions are not copied.
 * Note 3. Objects with a constructor other than of type Object are still in the result but aren't copied.
 *         This means that objects of HTMLElements or window will be in the result but will not be copied and hence
 *         shared between p_obj and the returned copy og p_obj.
 *
 * @method Alfresco.util.deepCopy
 * @param p_obj {object|array|date|string|number|boolean} The object to copy
 * @param p_oInstructions {object} (Optional) Contains special non default copy instructions
 * @param p_oInstructions.copyFunctions {boolean} (Optional) false by default
 * @return {object|array|date|string|number|boolean} A new instance of the same type as o with the same values
 * @static
 */
Alfresco.util.deepCopy = function(p_oObj, p_oInstructions)
{
   if (!p_oObj)
   {
      return p_oObj;
   }
   if (!p_oInstructions)
   {
      p_oInstructions = {};
   }

   if (YAHOO.lang.isArray(p_oObj))
   {
      var arr = [];
      for (var i = 0, il = p_oObj.length, arrVal; i < il; i++)
      {
         arrVal = p_oObj[i];
         if (!YAHOO.lang.isFunction(arrVal) || p_oInstructions.copyFunctions == true)
         {
            arr.push(Alfresco.util.deepCopy(arrVal, p_oInstructions));
         }
      }
      return arr;
   }

   if (Alfresco.util.isDate(p_oObj))
   {
      return new Date(p_oObj.getTime());
   }

   if (YAHOO.lang.isString(p_oObj) || YAHOO.lang.isNumber(p_oObj) || YAHOO.lang.isBoolean(p_oObj))
   {
      return p_oObj;
   }

   if (YAHOO.lang.isObject(p_oObj))
   {
      if (p_oObj.toString() == "[object Object]")
      {
         var obj = {}, objVal;
         for (var name in p_oObj)
         {
            if (p_oObj.hasOwnProperty(name))
            {
               objVal = p_oObj[name];
               if (!YAHOO.lang.isFunction(objVal) || p_oInstructions.copyFunctions == true)
               {
                  obj[name] = Alfresco.util.deepCopy(objVal, p_oInstructions);
               }
            }
         }
         return obj;
      }
      else
      {
         // The object was
         return p_oObj;
      }
   }

   return null;
};

/**
 * Tests if o is of type date
 *
 * @method Alfresco.util.isDate
 * @param o {object} The object to test
 * @return {boolean} True if o is of type date
 * @static
 */
Alfresco.util.isDate = function(o)
{
   return o.constructor && o.constructor.toString().indexOf("Date") != -1;
};

/**
 * Returns true if obj matches all attributes and their values in pattern.
 * Attribute values in pattern may contain wildcards ("*").
 *
 * @method objectMatchesPattern
 * @param obj {object} The object to match pattern against
 * @param pattern {object} An object with attributes to match against obj
 * @return {boolean} true if obj matches pattern, false otherwise
 */
Alfresco.util.objectMatchesPattern = function(obj, pattern)
{
   for (var attrName in pattern)
   {
      if (pattern.hasOwnProperty(attrName) &&
            (!pattern.hasOwnProperty(attrName) || (obj[attrName] != pattern[attrName] && pattern[attrName] != "*")))
      {
         return false;
      }
   }
   return true;
};

/**
 * Create empty JavaScript object literal from dotted notation string
 * <pre>e.g. Alfresco.util.dotNotationToObject("org.alfresco.site") returns {"org":{"alfresco":{"site":{}}}}</pre>
 *
 * @method Alfresco.util.dotNotationToObject
 * @param str {string} an dotted notation string
 * @param value {object|string|number} an optional object to set the "deepest" object to
 * @return {object} An empty object literal, build from the dotted notation
 * @static
 */
Alfresco.util.dotNotationToObject = function(str, value)
{
   var object = {}, obj = object;
   if (typeof str === "string")
   {
      var properties = str.split("."), property, i, ii;
      for (i = 0, ii = properties.length - 1; i < ii; i++)
      {
         property = properties[i];
         obj[property] = {};
         obj = obj[property];
      }
      obj[properties[i]] = value !== undefined ? value : null;
   }
   return object;
};

/**
 * Returns an object literal's property value given a dotted notation string representing the property path
 *
 * @method Alfresco.util.findValueByDotNotation
 * @param obj {object} i.e. {org:{alfresco:{site:"share"}}}
 * @param propertyPath {string} i.e. "org.alfresco.site"
 * @param defaultValue {object} optional The value to return if there is no value for the propertyPath
 * @return {object} the value for the property specified by the string, in the example "share" would be returned
 * @static
 */
Alfresco.util.findValueByDotNotation = function(obj, propertyPath, defaultValue)
{
   var value = defaultValue ? defaultValue : null;
   if (propertyPath && obj)
   {
      var currObj = obj;
      var props = propertyPath.split(".");
      for (var i = 0; i < props.length; i++)
      {
         currObj = currObj[props[i]];
         if (typeof currObj == "undefined")
         {
            return value;
         }
      }
      return currObj;
   }
   return value;
};

/**
 * Substitutes placeholder dotted notation values in strings given an object containing those properties
 *
 * @method Alfresco.util.substituteDotNotation
 * @param str {string} string containing dot notated property placeholders
 * @param obj {object} JavaScript object
 * @return {string} String with populated placeholders
 */
Alfresco.util.substituteDotNotation = function(str, obj)
{
   return YAHOO.lang.substitute(str, {}, function substituteDotNotation_substitute(p_key, p_value, p_meta)
   {
      return Alfresco.util.findValueByDotNotation(obj, p_key);
   });
};

/**
 * Check if an array contains an object
 * @method Alfresco.util.arrayContains
 * @param arr {array} Array to convert to object
 * @param el {object} The element to be searched for in the array
 * @return {boolean} True if arr contains el
 * @static
 */
Alfresco.util.findInArray = function(arr, value, attr)
{
   var index = Alfresco.util.arrayIndex(arr, value, attr);
   return index !== -1 ? arr[index] : null;
};

/**
 * Check if an array contains an object
 * @method Alfresco.util.arrayContains
 * @param arr {array} Array to convert to object
 * @param el {object} The element to be searched for in the array
 * @return {boolean} True if arr contains el
 * @static
 */
Alfresco.util.arrayContains = function(arr, el)
{
   return Alfresco.util.arrayIndex(arr, el) !== -1;
};

/**
 * Removes element el from array arr
 *
 * @method Alfresco.util.arrayRemove
 * @param arr {array} Array to remove el from
 * @param el {object} The element to be removed
 * @return {boolean} The array now without the element
 * @static
 */
Alfresco.util.arrayRemove = function(arr, el)
{
   var i = Alfresco.util.arrayIndex(arr, el);
   while (i !== -1)
   {
      arr.splice(i, 1);
      i = Alfresco.util.arrayIndex(arr, el);
   }
   return arr;
};

/**
 * Finds the index of an object in an array
 *
 * @method Alfresco.util.arrayIndex
 * @param arr {array} Array to search in
 * @param value {object} The element to find the index for in the array
 * @param attr {string} (Optional) If provided, valu ewill be compared to an attribute inside the object, instead of compared to the object itself.
 * @return {integer} -1 if not found, other wise the index
 * @static
 */
Alfresco.util.arrayIndex = function(arr, value, attr)
{
   if (arr)
   {
      for (var i = 0, ii = arr.length; i < ii; i++)
      {
         if (attr)
         {
            if (arr[i] && arr[i][attr] == value)
            {
               return i;
            }
         }
         else if (arr[i] == value)
         {
            return i;
         }
      }
   }
   return -1;
};

/**
 * Asserts param contains a proper value
 * @method Alfresco.util.assertNotEmpty
 * @param param {object} Parameter to assert valid
 * @param message {string} Error message to throw on assertion failure
 * @static
 * @throws {Error}
 */
Alfresco.util.assertNotEmpty = function(param, message)
{
   if (typeof param == "undefined" || !param || param === "")
   {
      throw new Error(message);
   }
};

/**
 * Check a value is neither undefined nor null (returns false).
 * An empty string also returns false unless the allowEmptyString flag is set.
 * @method Alfresco.util.isValueSet
 * @param value {object} Parameter to check
 * @param allowEmptyString {boolean} Optional flag to indicate that empty strings are valid inputs.
 * @static
 * @return {boolean} Flag indicating whether the value is set or not.
 */
Alfresco.util.isValueSet = function(value, allowEmptyString)
{
   if (YAHOO.lang.isUndefined(value) || YAHOO.lang.isNull(value))
   {
      return false;
   }
   if (YAHOO.lang.isString(value) && value.length === 0 && !!allowEmptyString === false)
   {
      return false;
   }
   return true;
};

/**
 * Append multiple parts of a path, ensuring duplicate path separators are removed.
 * Leaves "://" patterns intact so URIs and nodeRefs are safe to pass through.
 *
 * @method Alfresco.util.combinePaths
 * @param path1 {string} First path
 * @param path2 {string} Second path
 * @param ...
 * @param pathN {string} Nth path
 * @return {string} A string containing the combined paths
 * @static
 */
Alfresco.util.combinePaths = function()
{
   var path = "", i, ii;
   for (i = 0, ii = arguments.length; i < ii; i++)
   {
      if (arguments[i] !== null)
      {
         path += arguments[i] + (arguments[i] !== "/" ? "/" : "");
      }
   }
   path = path.replace(/(^|[^:])\/{2,}/g, "$1/");

   // Remove trailing "/" if the last argument didn't end with one
   if (arguments.length > 0 && !(typeof arguments[arguments.length - 1] === "undefined") && arguments[arguments.length - 1].match(/(.)\/$/) === null)
   {
      path = path.replace(/(.)\/$/g, "$1");
   }
   return path;
};

/**
 * Constants for conversion between bytes, kilobytes, megabytes and gigabytes
 */
Alfresco.util.BYTES_KB = 1024;
Alfresco.util.BYTES_MB = 1048576;
Alfresco.util.BYTES_GB = 1073741824;

/**
 * Converts a file size in bytes to human readable form
 *
 * @method Alfresco.util.formatFileSize
 * @param fileSize {number} File size in bytes
 * @param decimalPlaces {int} number of decimal places
 * @return {string} The file size in a readable form, i.e 1.2mb
 * @static
 * @throws {Error}
 */
Alfresco.util.formatFileSize = function(fileSize, decimalPlaces)
{
   var decimalPlaces = decimalPlaces || 0;
   if (typeof fileSize == "string")
   {
      fileSize = fileSize.replace(/,/gi,"");
      fileSize = parseInt(fileSize, 10);
   }

   if (fileSize < Alfresco.util.BYTES_KB)
   {
      return fileSize + " " + Alfresco.util.message("size.bytes");
   }
   else if (fileSize < Alfresco.util.BYTES_MB)
   {
      fileSize = (fileSize / Alfresco.util.BYTES_KB).toFixed(decimalPlaces);
      return fileSize + " " + Alfresco.util.message("size.kilobytes");
   }
   else if (fileSize < Alfresco.util.BYTES_GB)
   {
      fileSize = (fileSize / Alfresco.util.BYTES_MB).toFixed(decimalPlaces);
      return fileSize + " " + Alfresco.util.message("size.megabytes");
   }
   else if (isNaN(fileSize))
   {
      // special case for missing content size
      return "0 " + Alfresco.util.message("size.bytes");
   }
   else
   {
      fileSize = (fileSize / Alfresco.util.BYTES_GB).toFixed(decimalPlaces);
      return fileSize + " " + Alfresco.util.message("size.gigabytes");
   }
};

/**
 * Given a filename, returns either a filetype icon or generic icon file stem
 *
 * @method Alfresco.util.getFileIcon
 * @param p_fileName {string} File to find icon for
 * @param p_fileType {string} Optional: Filetype to offer further hinting
 * @param p_iconSize {int} Icon size: 32
 * @return {string} The icon name, e.g. doc-file-32.png
 * @static
 */
Alfresco.util.getFileIcon = function(p_fileName, p_fileType, p_iconSize, p_fileParentType)
{
   // Mapping from extn to icon name for cm:content
   var extns =
   {
      "aep": "aep",
      "ai": "ai",
      "aiff": "aiff",
      "asf": "video",
      "asnd": "asnd",
      "asx": "video",
      "au": "audio",
      "avi": "video",
      "avx": "video",
      "bmp": "img",
      "css": "text",
      "divx": "video",
      "doc": "doc",
      "docx": "doc",
      "docm": "doc",
      "dotx": "doc",
      "dotm": "doc",
      "eml": "eml",
      "eps": "eps",
      "fla": "fla",
      "flv": "video",
      "fxp": "fxp",
      "gif": "img",
      "htm": "html",
      "html": "html",
      "indd": "indd",
      "jpeg": "img",
      "jpg": "img",
      "key": "key",
      "mkv": "video",
      "mov": "video",
      "movie": "video",
      "mp3": "mp3",
      "mp4": "video",
      "mpeg": "video",
      "mpeg2": "video",
      "mpv2": "video",
      "msg": "eml",
      "numbers": "numbers",
      "odg": "odg",
      "odp": "odp",
      "ods": "ods",
      "odt": "odt",
      "ogg": "video",
      "ogv": "video",
      "pages": "pages",
      "pdf": "pdf",
      "png": "img",
      "ppj": "ppj",
      "ppt": "ppt",
      "pptx": "ppt",
      "pptm": "ppt",
      "pps": "ppt",
      "ppsx": "ppt",
      "ppsm": "ppt",
      "pot": "ppt",
      "potx": "ppt",
      "potm": "ppt",
      "ppam": "ppt",
      "sldx": "ppt",
      "sldm": "ppt",
      "psd": "psd",
      "qt": "video",
      "rtf": "rtf",
      "snd": "audio",
      "spx": "audio",
      "svg": "img",
      "swf": "swf",
      "tif": "img",
      "tiff": "img",
      "txt": "text",
      "wav": "audio",
      "webm": "video",
      "wmv": "video",
      "xls": "xls",
      "xlsx": "xls",
      "xltx": "xls",
      "xlsm": "xls",
      "xltm": "xls",
      "xlam": "xls",
      "xlsb": "xls",
      "xml": "xml",
      "xvid": "video",
      "zip": "zip"
   };

   var prefix = "generic",
         fileType = typeof p_fileType === "string" ? p_fileType : "cm:content",
         fileParentType = typeof p_fileParentType === "string" ? p_fileParentType : null,
         iconSize = typeof p_iconSize === "number" ? p_iconSize : 32;

   // If type = cm:content, then use extn look-up
   var type = Alfresco.util.getFileIcon.types[fileType];
   if (type === "file")
   {
      var extn = p_fileName.substring(p_fileName.lastIndexOf(".") + 1).toLowerCase();
      if (extn in extns)
      {
         prefix = extns[extn];
      }
   }
   else if (typeof type == "undefined")
   {
      if (fileParentType !== null)
      {
         type = Alfresco.util.getFileIcon.types[fileParentType];
         if (typeof type == "undefined")
         {
            type = "file";
         }
      }
      else
      {
         type = "file";
      }
   }
   return prefix + "-" + type + "-" + iconSize + ".png";
};
Alfresco.util.getFileIconByMimetype = function(mimetype, p_iconSize)
{
   var extns = 
   {
      "text/css": "css",
      "application/vnd.ms-excel": "xls",
      "image/tiff": "tiff",
      "audio/x-aiff": "aiff",
      "application/vnd.ms-powerpoint": "ppt",
      "application/illustrator": "ai",
      "image/gif": "gif",
      "audio/mpeg": "mp3",
      "message/rfc822": "eml",
      "application/vnd.oasis.opendocument.graphics": "odg",
      "application/x-indesign": "indd",
      "application/rtf": "rtf",
      "audio/x-wav": "wav",
      "application/x-fla": "fla",
      "video/x-ms-wmv": "wmv",
      "application/msword": "doc",
      "video/x-msvideo": "avi",
      "video/mpeg2": "mpeg2",
      "video/x-flv": "flv",
      "application/x-shockwave-flash": "swf",
      "audio/vnd.adobe.soundbooth": "asnd",
      "image/svg+xml": "svg",
      "application/vnd.apple.pages": "pages",
      "text/plain": "txt",
      "video/quicktime": "mov",
      "image/bmp": "bmp",
      "video/x-m4v": "m4v",
      "application/pdf": "pdf",
      "application/vnd.adobe.aftereffects.project": "aep",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": "xlsx",
      "text/xml": "xml",
      "application/zip": "zip",
      "video/webm": "webm",
      "image/png": "png",
      "text/html": "html",
      "image/vnd.adobe.photoshop": "psd",
      "video/ogg": "ogv",
      "image/jpeg": "jpg",
      "application/x-zip": "fxp",
      "video/mp4": "mp4",
      "image/x-xbitmap": "xbm",
      "video/x-rad-screenplay": "avx",
      "video/x-sgi-movie": "movie",
      "audio/x-ms-wma": "wma",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document": "docx",
      "application/vnd.openxmlformats-officedocument.presentationml.presentation": "pptx",
      "application/vnd.oasis.opendocument.presentation": "odp",
      "video/x-ms-asf": "asf",
      "application/vnd.oasis.opendocument.spreadsheet": "ods",
      "application/vnd.oasis.opendocument.text": "odt",
      "application/vnd.apple.keynote": "key",
      "image/vnd.adobe.premiere": "ppj",
      "application/vnd.apple.numbers": "numbers",
      "application/eps": "eps",
      "audio/basic": "au"
   };

   var prefix = "generic",
   iconSize = typeof p_iconSize === "number" ? p_iconSize : 32;
   if (mimetype in extns)
   {
      prefix = extns[mimetype];
   }
   
   return prefix + "-file-" + iconSize + ".png";
};
Alfresco.util.getFileIcon.types =
{
   "{http://www.alfresco.org/model/content/1.0}cmobject": "file",
   "cm:cmobject": "file",
   "{http://www.alfresco.org/model/content/1.0}content": "file",
   "cm:content": "file",
   "{http://www.alfresco.org/model/content/1.0}thumbnail": "file",
   "cm:thumbnail": "file",
   "{http://www.alfresco.org/model/content/1.0}folder": "folder",
   "cm:folder": "folder",
   "{http://www.alfresco.org/model/content/1.0}category": "category",
   "cm:category": "category",
   "{http://www.alfresco.org/model/content/1.0}person": "user",
   "cm:person": "user",
   "{http://www.alfresco.org/model/content/1.0}authorityContainer": "group",
   "cm:authorityContainer": "group",
   "tag": "tag",
   "{http://www.alfresco.org/model/site/1.0}sites": "site",
   "st:sites": "site",
   "{http://www.alfresco.org/model/site/1.0}site": "site",
   "st:site": "site",
   "{http://www.alfresco.org/model/transfer/1.0}transferGroup": "server-group",
   "trx:transferGroup": "server-group",
   "{http://www.alfresco.org/model/transfer/1.0}transferTarget": "server",
   "trx:transferTarget": "server"
};

/**
 * Returns the extension from file url or path
 *
 * @method Alfresco.util.getFileExtension
 * @param filePath {string} File path from which to extract file extension
 * @return {string|null} File extension or null
 * @static
 */
Alfresco.util.getFileExtension = function(filePath)
{
   var match = (new String(filePath)).match(/^.*\.([^\.]*)$/);
   if (YAHOO.lang.isArray(match) && YAHOO.lang.isString(match[1]))
   {
      return match[1];
   }

   return null;
};


/**
 * Loads a webscript into a target element.
 *
 * @method loadWebscript
 * @param config
 * @param config.method {String} (Optional) Defaults to "GET"
 * @param config.url {String} The url to the webscript to load
 * @param config.properties {Object} An object literal with the webscript parameters
 * @param config.target {HTMLElement|String|null} The html element that will contain the new webscript.
 *        If null a div with the hidden class will be appended to the body.
 */
Alfresco.util.loadWebscript = function (config)
{
   // Help creating a target & htmlid if none has been provided
   var c = Alfresco.util.deepCopy(config);
   c.method = c.method || Alfresco.util.Ajax.GET;
   c.properties = c.properties || {};
   c.properties.htmlid = c.properties.htmlid || Alfresco.util.generateDomId();

   // Load the form for the specific workflow
   Alfresco.util.Ajax.request(
   {
      method: c.method,
      url: c.url,
      dataObj: c.properties,
      successCallback:
      {
         fn: function loadWebscript_successCallback(response, config)
         {
            // Split markup and script elements
            var result = Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText);

            // Create temporary element to insert html
            var wrapper = document.createElement("div");
            wrapper.innerHTML = result[0];
            if (!YAHOO.util.Selector.query("#" + c.properties.htmlid, wrapper, true))
            {
               wrapper.setAttribute("id", c.properties.htmlid);
            }
            var target = YAHOO.util.Dom.get(c.target);
            if (!target)
            {
               target = document.createElement("div");
               YAHOO.util.Dom.addClass(target, "hidden");
               document.body.appendChild(target);
            }
            target.appendChild(wrapper);

            // Run the js code from the webscript's <script> elements
            window.setTimeout(result[1], 0);
         },
         scope: this,
         obj: config
      },
      failureMessage: Alfresco.util.message("message.failure"),
      scope: this,
      execScripts: false
   });
};

/**
 *
 * @param password
 * @param config
 * @param config.username
 * @param config.minLength
 */
Alfresco.util.getPasswordStrength = function(password, config)
{
   // Merge default config with overrides
   var c = YAHOO.lang.merge({
      username: null,
      minLength: 0,
      minUpper: 0,
      minLower: 0,
      minNumeric: 0,
      minSymbols: 0
   }, config);

   // Note we are avoiding character classes to make algorithm i18n safe.
   var strength = 0,
      DIGITS = /\d/g,
      NODIGITS = /\D/g,
      SPECIAL_CHARS = /([!,@,#,$,%,^,&,*,?,_,~])/g,
      tmp;
   if ((!password || password.length < config.minLength) && config.minLength > 0)
   {
      // Its too short to be a valid password.
      return null;
   }
   // Count the number of upper and lower case characters
   var upper = 0, lower = 0, ch;
   for (var i = 0; i < password.length; i++)
   {
      ch = password.charAt(i);
      if (ch.toUpperCase() != ch.toLowerCase())
      {
         // Ok now we now it is an actual character (can't use regexp since they dont handle foreign characters)
         if (ch.toUpperCase() == ch)
         {
            upper++;
         }
         if (ch.toLowerCase() == ch)
         {
            lower++;
         }
      }
   }
   if (c.minUpper > upper || c.minLower > lower)
   {
      // A required minimum number of upper and/or lower case characters was not provided
      return null;
   }
   if (c.minNumeric > (password.match(DIGITS) || []).length)
   {
      // A required minimum number of numeric characters was not provided
      return null;
   }
   if (c.minSymbols > (password.match(SPECIAL_CHARS) || []).length)
   {
      // A required minimum number of special characters was not provided
      return null;
   }
   if (config.username && config.username.indexOf(password) != -1 || password.indexOf(config.username) != -1)
   {
      // The password matches the username, in other words its W.E.A.K.
      strength = 0;
   }
   else
   {
      // We have a password of at least 8 characters
      strength += 10;

      // Reward longer passwords
      if (password.length > (config.minLength + 2))
      {
         strength += 10;
      }
      if (password.length > (config.minLength + 4))
      {
         strength += 10;
      }
      if (password.length > (config.minLength + 6))
      {
         strength += 20;
      }

      // Reward the mix of numbers and non numbers
      if (password.match(DIGITS) && password.match(NODIGITS))
      {
         strength += 10;
      }
      // Reward having at least 3 numbers
      if ((password.match(DIGITS) || []).length > 2)
      {
         strength += 10;
      }

      // Reward mixing upper and lower case
      if (upper > 0 && lower > 0)
      {
         strength += 10;
         if (upper > 1 && lower > 1)
         {
            // Reward a spreaded mix
            strength += 10;
         }
      }

      // Reward special characters
      tmp = password.match(SPECIAL_CHARS);
      if (tmp)
      {
         strength += 10;
         if ((tmp || []).length > 3)
         {
            strength += 10;
         }
      }
   }

   var css = [
      { name: "weak",   interval: [0, 20] },
      { name: "medium", interval: [21, 30] },
      { name: "strong", interval: [31, 50] },
      { name: "best",   interval: [51, 110] }];
   for (var i = 0; i < css.length; i++)
   {
      if (strength >= css[i].interval[0] && strength <= css[i].interval[1])
      {
         return { name: css[i].name }
      }
   }
};

/**
 * Loads a webscript into a target element.
 *
 * @method loadWebscript
 * @param config
 * @param config.method {String} (Optional) Defaults to "GET"
 * @param config.url {String} The url to the webscript to load
 * @param config.properties {Object} An object literal with the webscript parameters
 * @param config.target {HTMLElement|String|null} The html element that will contain the new webscript.
 *        If null a div with the hidden class will be appended to the body.
 */
Alfresco.util.loadWebscript = function (config)
{
   // Help creating a target & htmlid if none has been provided
   var c = Alfresco.util.deepCopy(config);
   c.method = c.method || Alfresco.util.Ajax.GET;
   c.properties = c.properties || {};
   c.properties.htmlid = c.properties.htmlid || Alfresco.util.generateDomId();

   // Load the form for the specific workflow
   Alfresco.util.Ajax.request(
   {
      method: c.method,
      url: c.url,
      dataObj: c.properties,
      successCallback:
      {
         fn: function loadWebscript_successCallback(response, config)
         {
            // Split markup and script elements
            var result = Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText);

            // Create temporary element to insert html
            var wrapper = document.createElement("div");
            wrapper.innerHTML = result[0];
            if (!YAHOO.util.Selector.query("#" + c.properties.htmlid, wrapper, true))
            {
               wrapper.setAttribute("id", c.properties.htmlid);
            }
            var target = YAHOO.util.Dom.get(c.target);
            if (!target)
            {
               target = document.createElement("div");
               YAHOO.util.Dom.addClass(target, "hidden");
               document.body.appendChild(target);
            }
            target.appendChild(wrapper);

            // Run the js code from the webscript's <script> elements
            window.setTimeout(result[1], 0);
         },
         scope: this,
         obj: config
      },
      failureMessage: Alfresco.util.message("message.failure"),
      scope: this,
      execScripts: false
   });
};

/**
 * Returns the windows scroll position that later can be used for i.e. window.scrollTo.
 *
 * @method Alfresco.util.getScrollPosition
 * @return {Array} An array with the x & y position of the scrollbars
 * @static
 */
Alfresco.util.getScrollPosition = function()
{
   if (YAHOO.env.ua.ie > 0)
   {
      if (document.compatMode && document.compatMode != "BackCompat")
      {
         return [ document.documentElement.scrollLeft, document.documentElement.scrollTop ];
      }
      else
      {
         return [ document.body.scrollLeft, document.body.scrollTop ];
      }
   }
   else
   {
      return [ window.scrollX, window.scrollY ];
   }
};

/**
 * Formats a Freemarker datetime into more UI-friendly format
 *
 * @method Alfresco.util.formatDate
 * @param date {string|Date} Optional: Date as ISO8601 compatible string or JavaScript Date Object. Today used if missing.
 * @param mask {string} Optional: Mask to use to override default.
 * @return {string} Date formatted for UI
 * @static
 */
Alfresco.util.formatDate = function(date)
{
   if (YAHOO.lang.isString(date))
   {
      // if we've got a date as an ISO8601 string, convert to date Object before proceeding - otherwise pass it through
      var dateObj = Alfresco.util.fromISO8601(date);
      if (dateObj)
      {
         arguments[0] = dateObj;
      }
   }
   try
   {
      return Alfresco.thirdparty.dateFormat.apply(this, arguments);
   }
   catch(e)
   {
      return date;
   }
};

/**
 * Convert an ISO8601 date string into a JavaScript native Date object
 *
 * @method Alfresco.util.fromISO8601
 * @param date {string} ISO8601 formatted date string
 * @param ignoreTime {Bool} Optional. Ignores any time (and therefore timezone) components.
 * @return {Date|null} JavaScript native Date object
 * @static
 */
Alfresco.util.fromISO8601 = function(date, ignoreTime)
{
   // Added for MNT-9693 - just passes on the date component
   if (ignoreTime) {
      date = date.split('T')[0]
   };
   try
   {
      return Alfresco.thirdparty.fromISO8601.apply(this, arguments);
   }
   catch(e)
   {
      return null;
   }
};

/**
 *
 * Converts a user input time string into a date object.
 * Accepted inputs include: 11am, 11PM, 11:00, 23:00, 11:23 am, 3 p.m., 08:00, 1100, 11, 8, 23.
 * Only accepts hours and minutes, seconds are zeroed.
 *
 * @param timeString {String} - user input time
 * @return {Date}
 */

Alfresco.util.parseTime = function(timeString)
{
   var d = new Date(); // Today's date
   var time = timeString.toString().match(/^(\d{1,2})(?::?(\d\d))?\s*(a*)([p]?)\.*m?\.*$/i);

   // Exit early if we've not got a match, if the hours are greater than 24, or greater than 12 if AM/PM is specified, or minutes are larger than 59.
   if (time === null || !time[1] || time[1] > 24 || (time[1] > 12 && (time[3]||time[4])) || (time[2] && time[2] > 59)) return null;

   // Add 12?
   var add12 = false;

   // If we're PM:
   if (time[4])
   {
      add12 = true;
   }

   // if we've got EITHER AM or PM, the 12th hour behaves different:
   // 12am = 00:00 (which is the same as 24:00 if the date is ignored), 12pm = 12:00
   // if we don't have AM or PM, then default to 12 === noon (i.e. add nothing).
   if (time[1] == 12 && (time[3] || time[4]))
   {
      add12 = !add12;
   }

   d.setHours( parseInt(time[1], 10) + (add12 ? 12 : 0) );
   d.setMinutes( parseInt(time[2], 10) || 0 );
   d.setSeconds(0);
   return d;

}

/**
 * Convert a JavaScript native Date object into an ISO8601 date string
 *
 * @method Alfresco.util.toISO8601
 * @param date {Date} JavaScript native Date object
 * @return {string} ISO8601 formatted date string
 * @static
 */
Alfresco.util.toISO8601 = function(date)
{
   try
   {
      return Alfresco.thirdparty.toISO8601.apply(this, arguments);
   }
   catch(e)
   {
      return "";
   }
};

/**
 * Convert an JSON date exploded into an object literal into a JavaScript native Date object.
 * NOTE: Passed-in date will have month as zero-based.
 *
 * @method Alfresco.util.fromExplodedJSONDate
 * @param date {object} object literal of the following example format (UTC):
 * <pre>
 *    date =
 *    {
 *       year: 2009
 *       month: 4 // NOTE: zero-based
 *       date: 22
 *       hours: 14
 *       minutes: 27
 *       seconds: 42
 *       milliseconds: 390
 *    }
 * </pre>
 * @return {Date|null} JavaScript native Date object
 * @static
 */
Alfresco.util.fromExplodedJSONDate = function(date)
{
   try
   {
      var isoDate = YAHOO.lang.substitute("{year 4}-{month 2}-{date 2}T{hours 2}:{minutes 2}:{seconds 2}.{milliseconds 3}Z", date, function(p_key, p_value, p_meta)
      {
         if (p_key == "month")
         {
            ++p_value;
         }
         p_value = String(p_value);
         var length = parseInt(p_meta, 10) || 2;
         while (p_value.length < length)
         {
            p_value = "0" + p_value;
         }
         return p_value;
      });
      return Alfresco.thirdparty.fromISO8601.apply(this, [isoDate, Array.prototype.slice.call(arguments).slice(1)]);
   }
   catch(e)
   {
      return null;
   }
};

/**
 * Convert an object literal into a JavaScript native Date object into an JSON date exploded.
 * NOTE: Passed-in date will have month as zero-based.
 *
 * @method Alfresco.util.toExplodedJSONDate
 * @param date {Date} JavaScript Date object
 * @return {object}
 * <pre>
 *    date =
 *    {
 *       year: 2009
 *       month: 4 // NOTE: zero-based
 *       date: 22
 *       hours: 14
 *       minutes: 27
 *       seconds: 42
 *       milliseconds: 390
 *    }
 * </pre>
 * @static
 */
Alfresco.util.toExplodedJSONDate = function(date)
{
   return (
   {
      zone: "UTC",
      year: date.getFullYear(),
      month: date.getMonth(),
      date: date.getDate(),
      hours: date.getHours(),
      minutes: date.getMinutes(),
      seconds: date.getSeconds(),
      milliseconds: date.getMilliseconds()
   });
};

/**
 * Render relative dates on the client
 *
 * Converts all ISO8601 dates within the specified container to relative dates.
 * (indicated by <span class="relativeTime">{date.iso8601}</span>)
 *
 * @method Alfresco.util.renderRelativeTime
 * @param id {String} ID of HTML element containing
 *
 */
Alfresco.util.renderRelativeTime = function(id)
{
   YAHOO.util.Dom.getElementsByClassName("relativeTime", "span", id , function()
   {
      this.innerHTML = Alfresco.util.relativeTime(this.innerHTML);
   })
};

/**
 * Generate a relative time between two Date objects.
 *
 * @method Alfresco.util.relativeTime
 * @param from {Date|String} JavaScript Date object or ISO8601-formatted date string
 * @param to {Date|string} (Optional) JavaScript Date object or ISO8601-formatted date string, defaults to now if not supplied
 * @return {string} Relative time description
 * @static
 */
Alfresco.util.relativeTime = function(from, to)
{
   var originalFrom = from;

   if (YAHOO.lang.isString(from))
   {
      from = Alfresco.util.fromISO8601(from);
   }

   if (!(from instanceof Date))
   {
      return originalFrom;
   }

   if (YAHOO.lang.isUndefined(to))
   {
      to = new Date();
   }
   else if (YAHOO.lang.isString(to))
   {
      to = Alfresco.util.fromISO8601(to);
   }

   var $msg = Alfresco.util.message,
         seconds_ago = ((to - from) / 1000),
         minutes_ago = Math.floor(seconds_ago / 60),
         fnTime = function relativeTime_fnTime()
         {
            return "<span title='" + Alfresco.util.formatDate(from) + "'>" + $msg.apply(this, arguments) + "</span>";
         };

   if (minutes_ago <= 0)
   {
      return fnTime("relative.seconds", this, seconds_ago);
   }
   if (minutes_ago == 1)
   {
      return fnTime("relative.minute", this);
   }
   if (minutes_ago < 45)
   {
      return fnTime("relative.minutes", this, minutes_ago);
   }
   if (minutes_ago < 90)
   {
      return fnTime("relative.hour", this);
   }
   var hours_ago  = Math.round(minutes_ago / 60);
   if (minutes_ago < 1440)
   {
      return fnTime("relative.hours", this, hours_ago);
   }
   if (minutes_ago < 2880)
   {
      return fnTime("relative.day", this);
   }
   var days_ago  = Math.round(minutes_ago / 1440);
   if (minutes_ago < 43200)
   {
      return fnTime("relative.days", this, days_ago);
   }
   if (minutes_ago < 86400)
   {
      return fnTime("relative.month", this);
   }
   var months_ago  = Math.round(minutes_ago / 43200);
   if (minutes_ago < 525960)
   {
      return fnTime("relative.months", this, months_ago);
   }
   if (minutes_ago < 1051920)
   {
      return fnTime("relative.year", this);
   }
   var years_ago  = Math.round(minutes_ago / 525960);
   return fnTime("relative.years", this, years_ago);
};

/**
 * Converts a date to a more user friendly date
 *
 * @method Alfresco.util.relativeDate
 * @param date {Date|String} - the date being converted
 * @param format {string} - the date format
 * @param options {object} - overrides the default options.
 * @return {string} - the user friendly date
 */
Alfresco.util.relativeDate = function(date, format, options)
{
   var $msg = Alfresco.util.message;

   if (YAHOO.lang.isString(date))
   {
      date = Alfresco.util.fromISO8601(date);
   }

   if (YAHOO.lang.isObject(format))
   {
      options = format;
      format = $msg("date-format.default");
   }


   var now = new Date(),
         today = new Date(now.getTime()),
         dateFormat = Alfresco.thirdparty.dateFormat,
         dateMath = YAHOO.widget.DateMath,
         isoMask = Alfresco.thirdparty.dateFormat.masks.isoDate,
         isoDate = dateFormat(date, isoMask),
         isoToday = dateFormat(now, isoMask),
         isoYesterday = dateFormat(dateMath.add(now, dateMath.DAY, -1), isoMask),
         isoTomorrow = dateFormat(dateMath.add(now, dateMath.DAY, 1), isoMask),
         result = "",
         defaults =
         {
            // limit to just yesterday, today, tomorrow.
            limit: false
         };

   options = options || {}
   options = YAHOO.lang.augmentObject(options, defaults);

   // reset time on today (ISO dates already ignore time)
   today.setHours(0,0,0,0);

   switch(isoDate)
   {
      case isoToday:
         result = $msg("relative.today");
         break;
      case isoYesterday:
         result = $msg("relative.yesterday");
         break;
      case isoTomorrow:
         result = $msg("relative.tomorrow");
         break;
   }
   if (!options.limit && result === "")
   {
      var lastSunday = dateMath.add(now, dateMath.DAY, -now.getDay()),
            sundayBeforeLast = dateMath.add(lastSunday, dateMath.DAY, -7),
            nextSunday = dateMath.add(lastSunday, dateMath.DAY, +7),
            sundayAfterNext = dateMath.add(lastSunday, dateMath.DAY, +7);

      if (date < today && date > lastSunday)
      {
         // Earlier This week
         result = $msg("relative.earlierThisWeek");
      }
      else if (date < lastSunday && date > sundayBeforeLast)
      {
         // Last week
         result = $msg("relative.lastWeek");
      }
      else if (date > today && date < nextSunday)
      {
         // Later this week
         result = $msg("relative.laterThisWeek");
      }
      else if (date > nextSunday && date < sundayAfterNext)
      {
         // Next Week
         result = $msg("relative.nextWeek");
      }
      else if (options.olderDates && date < today)
      {
         result = options.olderDates;
      }
      else if (options.futureDates && date > today)
      {
         result = options.futureDates;
      }

   }
   if (result === "")
   {
      result = dateFormat(date, format)
   }
   return result;
};

/**
 * Pad a value with leading zeros to the specified length.
 *
 * @method Alfresco.util.pad
 * @param value {string|number} non null value to pad
 * @param value {number} length to pad out with leading zeros
 * @return {string} padded value as a string
 * @static
 */
Alfresco.util.pad = function(value, length)
{
   value = String(value);
   length = parseInt(length, 10) || 2;
   while (value.length < length)
   {
      value = "0" + value;
   }
   return value;
};

/**
 * Inserts the given string into the supplied text element at the current cursor position.
 *
 * @method Alfresco.util.insertAtCursor
 * @param el {object} The Dom text element to insert into
 * @param txt {string} The string to insert at current cursor position
 * @static
 */
Alfresco.util.insertAtCursor = function(el, txt)
{
   if (document.selection)
   {
      el.focus();
      document.selection.createRange().text = txt;
   }
   else if (el.selectionStart || el.selectionStart == '0')
   {
      el.value = el.value.substring(0, el.selectionStart) + txt + el.value.substring(el.selectionEnd, el.value.length);
   }
   else
   {
      el.value += txt;
   }
   el.focus();
};

/**
 * Selects text in the input field.
 *
 * @method selectText
 * @param elTextbox {HTMLElement} Text input box element in which to select text.
 * @param nStart {Number} Starting index of text string to select.
 * @param nEnd {Number} Ending index of text selection.
 */
Alfresco.util.selectText = function(elTextbox, nStart, nEnd)
{
   if (elTextbox.setSelectionRange)
   {
      elTextbox.setSelectionRange(nStart, nEnd);
   }
   else if (elTextbox.createTextRange)
   {
      // For IE
      var oTextRange = elTextbox.createTextRange();
      oTextRange.moveStart("character", nStart);
      oTextRange.moveEnd("character", nEnd-elTextbox.value.length);
      oTextRange.select();
   }
   else
   {
      elTextbox.select();
   }
};

/**
 * Checks if the element and all its parents are visible and displayed in the ui.
 *
 * @method Alfresco.util.isVisible
 * @param el {object} The Dom element to check visibility for
 * @return true if el and all its parents are displayed in ui
 * @static
 */
Alfresco.util.isVisible = function (el)
{
   try
   {
      while (el)
      {
         if (el.tagName)
         {
            if(el.tagName.toLowerCase() == "body")
            {
               return true;
            }
         }
         if (YUIDom.getStyle(el, "display") == "none" || YUIDom.getStyle(el, "visibility") == "hidden")
         {
            return false;
         }
         el = el.parentNode;
      }
   }
   catch(ex)
   {
      return false;
   }
   return true;
};

/**
 * Decodes an HTML-encoded string
 * Replaces &lt; &gt; and &amp; entities with their character equivalents
 *
 * @method Alfresco.util.decodeHTML
 * @param html {string} The string containing HTML entities
 * @return {string} Decoded string
 * @static
 */
Alfresco.util.decodeHTML = function(html)
{
   if (html === null)
   {
      return "";
   }
   return html.split("&lt;").join("<").split("&gt;").join(">").split("&amp;").join("&").split("&quot;").join('"');
};

/**
 * Encodes a potentially unsafe string with HTML entities
 * Replaces <pre><, >, &</pre> characters with their entity equivalents.
 * Based on the equivalent encodeHTML and unencodeHTML functions in Prototype.
 *
 * @method Alfresco.util.encodeHTML
 * @param text {string} The string to be encoded
 * @param justified {boolean} If true, don't render lines 2..n with an indent
 * @return {string} Safe HTML string
 * @static
 */
Alfresco.util.encodeHTML = function(text, justified)
{
   if (text === null || typeof text == "undefined")
   {
      return "";
   }

   var indent = justified === true ? "" : "&nbsp;&nbsp;&nbsp;";

   if (YAHOO.env.ua.ie > 0)
   {
      text = "" + text;
      return text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g, "<br />" + indent).replace(/"/g, "&quot;");
   }
   var me = arguments.callee;
   me.text.data = text;
   return me.div.innerHTML.replace(/\n/g, "<br />" + indent).replace(/"/g, "&quot;");
};
Alfresco.util.encodeHTML.div = document.createElement("div");
Alfresco.util.encodeHTML.text = document.createTextNode("");
Alfresco.util.encodeHTML.div.appendChild(Alfresco.util.encodeHTML.text);

/**
 * Encodes a folder path string for use in a REST URI.
 * First performs a encodeURI() pass so that the '/' character is maintained
 * as the path must be intact as URI elements. Then encodes further characters
 * on the path that would cause problems in URLs, such as '&', '=' and '#'.
 *
 * @method Alfresco.util.encodeURIPath
 * @param text {string} The string to be encoded
 * @return {string} Encoded path URI string.
 * @static
 */
Alfresco.util.encodeURIPath = function(text)
{
   return encodeURIComponent(text).replace(/%2F/g, "/");
};

/**
 * Scans a text string for links and injects HTML mark-up to activate them.
 * NOTE: If used in conjunction with encodeHTML, this function must be called last.
 *
 * @method Alfresco.util.activateLinks
 * @param text {string} The string potentially containing links
 * @return {string} String with links marked-up to make them active
 * @static
 */
Alfresco.util.activateLinks = function(text)
{
   var re = new RegExp(/((http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?\^=%&:;\/~\+#]*[\w\-\@?\^=%&\/~\+#])?)/g);
   text = text.replace(re, "<a href=\"$1\" target=\"_blank\">$1</a>");
   return text;
};

/**
 * Convert a plaintext Tweet into HTML with detected links parsed and "activated"
 *
 * @method Alfresco.util.tweetToHTML
 * @param text {string} The plaintext Tweet
 * @return {string} HTML string
 */
Alfresco.util.tweetToHTML = function(text)
{
   // URLs
   text = Alfresco.util.activateLinks(text);

   // User links
   var re = new RegExp(/(^|[^\w])@([\w]{1,})/g);
   text = text.replace(re, "$1<a href=\"http://twitter.com/$2\">@$2</a>");

   // Hash tags
   re = new RegExp(/#+([\w]{1,})/g);
   text = text.replace(re, "<a href=\"http://search.twitter.com/search?q=%23$1\">#$1</a>");

   return text;
};

/**
 * Tests a select element's options against "value" and
 * if there is a match that option is set to the selected index.
 *
 * @method Alfresco.util.setSelectedIndex
 * @param value {HTMLSelectElement} The select element to change the selectedIndex for
 * @param selectEl {string} The value to match agains the select elements option values
 * @return {string} The label/name of the seleceted option OR null if no option was found
 * @static
 */
Alfresco.util.setSelectedIndex = function(selectEl, value)
{
   for (var i = 0, l = selectEl.options.length; i < l; i++)
   {
      if (selectEl.options[i].value == value)
      {
         selectEl.selectedIndex = i;
         return selectEl.options[i].text;
      }
   }
   return null;
};

/**
 * Removes selectClass from all of selectEl's parent's child elements but adds it to selectEl.
 *
 * @method Alfresco.util.setSelectedClass
 * @param parentEl {HTMLElement} The elements to deselct
 * @param selectEl {HTMLElement} The element to select
 * @param selectClass {string} The css class to remove from unselected and add to the selected element
 * @static
 */
Alfresco.util.setSelectedClass = function(parentEl, selectEl, selectClass)
{
   var children = parentEl.childNodes,
         child = null;

   selectClass = selectClass ? selectClass : "selected";
   for (var i = 0, l = children.length; i < l; i++)
   {
      child = children[i];
      if (!selectEl || child.tagName == selectEl.tagName)
      {
         YUIDom.removeClass(child, selectClass);
         if (child === selectEl)
         {
            YUIDom.addClass(child, selectClass);
         }
      }
   }
};

/**
 * Helper function to listen for mouse click and keyboard enter events on an element.
 * Also makes sure that el has a tabindex so it can get focus
 *
 * @method useAsButton
 * @param el {String|HTMLElement} The element to listen to
 * @param callbackFn {function} The method to invoke on click or enter will get called with (event, obj)
 * @param callbackObj {Object} The object to pass into the callback
 * @param callbackScope {Object} (Optional) The scope to execute the callback in (if not supplied el is used)
 * @static
 */
Alfresco.util.useAsButton = function(el, callbackFn, callbackObj, callbackScope)
{
   YAHOO.util.Event.addListener(el, "click", callbackFn, callbackObj, callbackScope);
   if (YAHOO.lang.isString(el)) {
      el = YAHOO.util.Dom.get(el);
   }
   if(!el.getAttribute("tabindex")) {
      el.setAttribute("tabindex", "0");
   }
   var fn = callbackFn,
         obj = callbackObj,
         scope = callbackScope || el;
   var callback = function (type, arg)
   {
      fn.call(scope, type, obj || arg);
   };
   if (el.tagName != "A")
   {
      new YAHOO.util.KeyListener(el,
      {
         keys: [ YAHOO.util.KeyListener.KEY.ENTER ]
      }, callback).enable();
   }
};

/**
 * Returns a unique DOM ID for dynamically-created content. Optionally applies the new ID to an element.
 *
 * @method Alfresco.util.generateDomId
 * @param p_el {HTMLElement} Applies new ID to element
 * @param p_prefix {string} Optional prefix instead of "alf-id" default
 * @return {string} Dom Id guaranteed to be unique on the current page
 */
Alfresco.util.generateDomId = function(p_el, p_prefix)
{
   var domId, prefix = p_prefix || "alf-id";
   do
   {
      domId = prefix + Alfresco.util.generateDomId._nId++;
   } while (YUIDom.get(domId) !== null);

   Alfresco.util.setDomId(p_el, domId);

   return domId;
};
Alfresco.util.generateDomId._nId = 0;

/**
 * Sets the domId as the html dom id on el
 *
 * @method Alfresco.util.setDomId
 * @param p_el {HTMLElement} Applies new ID to element
 * @param p_domId {string} The dom id to apply
 */
Alfresco.util.setDomId = function(p_el, p_domId)
{
   if (p_el)
   {
      if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 8)
      {
         // MSIE 6 & 7-safe method
         if (p_el.attributes["id"])
         {
            p_el.attributes["id"].value = p_domId;
         }
         p_el.setAttribute("id", p_domId);
      }
      else
      {
         p_el.setAttribute("id", p_domId);
      }
   }
};

/**
 * Converts "rel" attributes on <a> tags to "target" attributes.
 * "target" isn't supported in XHTML, so we use "rel" as a placeholder and replace at runtime.
 *
 * @method relToTarget
 * @param rootNode {HTMLElement|String} An id or HTMLElement to start the query from
 */
Alfresco.util.relToTarget = function(p_rootNode)
{
   var elements = YUISelector.query("a[rel]", p_rootNode);
   for (var i = 0, ii = elements.length; i < ii; i++)
   {
      elements[i].setAttribute("target", elements[i].getAttribute("rel"));
   }
};

/**
 * Sets single or multiple DOM element innerHTML values.
 * Ensures target element exists in the DOM before attempting to set the label.
 *
 * @method populateHTML
 * @param p_label, p_label, ... {Array} Array with exactly two members:
 * <pre>
 *    [0] {String | HTMLElement} Accepts a string to use as an ID for getting a DOM reference, or an actual DOM reference.<br />
 *    [1] {String} HTML content to populate element if it exists in the DOM. HTML will NOT be escaped.
 * </pre>
 */
Alfresco.util.populateHTML = function()
{
   for (var i = 0, ii = arguments.length, el = null; i < ii; i++)
   {
      el = YUIDom.get(arguments[i][0]);
      if (el)
      {
         el.innerHTML = arguments[i][1];
      }
   }
};

/**
 * Checks whether the current browser supports a given CSS property.
 * The property should be passed in canonical form and without vendor prefix
 * e.g. "TextShadow"
 *
 * @method hasCssProperty
 * @param property {string} CSS property to test
 * @return {boolean} True if the browser appears to support the property
 */
Alfresco.util.hasCssProperty = function(property)
{
   if (Alfresco.util.hasCssProperty.div.style.hasOwnProperty(property))
   {
      return true;
   }

   // Ensure first character is uppercase
   property = property.replace(/^[a-z]/, function(val)
   {
      return val.toUpperCase();
   });

   var len = Alfresco.util.hasCssProperty.vendors.length;
   while(len--)
   {
      if (Alfresco.util.hasCssProperty.div.style.hasOwnProperty(vendors[len] + property))
      {
         return true;
      }
   }
   return false;
};
Alfresco.util.hasCssProperty.div = document.createElement("div");
Alfresco.util.hasCssProperty.vendors = ["Khtml", "O", "ms", "Moz", "Webkit"];

/**
 * Wrapper to create a YUI Button with common attributes.
 * All supplied object parameters are passed to the button constructor
 * e.g. Alfresco.util.createYUIButton(this, "OK", this.onOK, {type: "submit"});
 *
 * @method Alfresco.util.createYUIButton
 * @param p_scope {object} Component containing button; must have "id" parameter
 * @param p_name {string} Dom element ID of markup that button is created from {p_scope.id}-{name}
 * @param p_onclick {function} If supplied, registered with the button's click event
 * @param p_obj {object} Optional extra object parameters to pass to button constructor
 * @param p_oElement {string|HTMLElement} Optional and accepts a string to use as an ID for getting a DOM reference or an actual DOM reference
 * @return {YAHOO.widget.Button} New Button instance
 * @static
 */
Alfresco.util.createYUIButton = function(p_scope, p_name, p_onclick, p_obj, p_oElement)
{
   // Default button parameters
   var obj =
   {
      type: "button",
      disabled: false,
      usearia: true
   };

   // Any extra parameters?
   if (typeof p_obj == "object")
   {
      obj = YAHOO.lang.merge(obj, p_obj);
   }

   // Fix-up the menu element ID
   if ((obj.type == "menu") && (typeof obj.menu == "string"))
   {
      obj.menu = p_scope.id + "-" + obj.menu;
   }

   // Create the button
   var oElement = p_oElement ? p_oElement : p_scope.id + "-" + p_name,
         button = null;

   if (YUIDom.get(oElement) !== null)
   {
      button = new YAHOO.widget.Button(oElement, obj);

      if (typeof button == "object")
      {
         // Register the click listener if one was supplied
         if (typeof p_onclick == "function")
         {
            if (obj.type == "menu")
            {
               // Special case for a menu
               button.getMenu().subscribe("click", function (p_sType, p_aArgs, p_oObj)
               {
                  p_aArgs[p_aArgs.length] = p_name;
                  p_onclick.call(p_scope, p_sType, p_aArgs, p_oObj);
               }, p_scope, true);
               button.getMenu().subscribe("keydown", function (p_sType, p_aArgs, p_oObj)
                     {
                        if (p_aArgs[0].keyCode == YUIKeyListener.KEY.ENTER)
                        {
                           p_oObj.fn.call(p_oObj.scope, p_sType, p_aArgs);
                        }
                     },
                     {
                        scope: p_scope,
                        fn: p_onclick
                     });
            }
            else if (obj.type == "checkbox")
            {
               // Special case for a checkbox button
               button.on("checkedChange", p_onclick, button, p_scope);
            }
            else
            {
               button.on("click", p_onclick, button, p_scope);
            }
         }

         // Special case if htmlName was passed-in as an option
         if (typeof obj.htmlName != "undefined")
         {
            button.get("element").getElementsByTagName("button")[0].name = obj.htmlName;
         }
         
         // Adds button styling
         if (typeof obj.additionalClass == "string")
         {
            YUIDom.addClass(button._button.parentElement.parentElement, obj.additionalClass);
         }
      }
   }
   return button;
};

/**
 * Wrapper to disable a YUI Button, including link buttons.
 * Link buttons aren't disabled by YUI; see http://developer.yahoo.com/yui/button/#apiref
 *
 * @method Alfresco.util.disableYUIButton
 * @param p_button {YAHOO.widget.Button} Button instance
 * @static
 */
Alfresco.util.disableYUIButton = function(p_button)
{
   if (p_button && p_button.set && p_button.get)
   {
      p_button.set("disabled", true);
      if (p_button.get("type") == "link")
      {
         /**
          * Note the non-optimal use of a "private" variable, which is why it's tested before use.
          */
         p_button.set("href", "");
         if (p_button._button && p_button._button.setAttribute)
         {
            p_button._button.setAttribute("onclick", "return false;");
         }
         p_button.addStateCSSClasses("disabled");
         p_button.removeStateCSSClasses("hover");
         p_button.removeStateCSSClasses("active");
         p_button.removeStateCSSClasses("focus");
      }
   }
};

/**
 * Wrapper to (re)enable a YUI Button, including link buttons.
 * Link buttons aren't disabled by YUI; see http://developer.yahoo.com/yui/button/#apiref
 *
 * @method Alfresco.util.enableYUIButton
 * @param p_button {YAHOO.widget.Button} Button instance
 * @static
 */
Alfresco.util.enableYUIButton = function(p_button)
{
   if (p_button.set && p_button.get)
   {
      p_button.set("disabled", false);
      if (p_button.get("type") == "link")
      {
         /**
          * Note the non-optimal use of a "private" variable, which is why it's tested before use.
          */
         if (p_button._button && p_button._button.removeAttribute)
         {
            p_button._button.removeAttribute("onclick");
         }
         p_button.removeStateCSSClasses("disabled");
      }
   }
};

/**
 * Creates a "disclosure twister" UI control from existing mark-up.
 *
 * @method Alfresco.util.createTwister
 * @param p_controller {Element|string} Element (or DOM ID) of controller node
 * <pre>The code will search for the next sibling which will be used as the hideable panel, unless overridden below</pre>
 * @param p_filterName {string} Filter's name under which to save it's collapsed state via preferences
 * @param p_config {object} Optional additional configuration to override the defaults
 * <pre>
 *    panel {Element|string} Use this panel as the hideable element instead of the controller's first sibling
 * </pre>
 * @return {boolean} true = success
 */
Alfresco.util.createTwister = function(p_controller, p_filterName, p_config)
{
   var defaultConfig =
   {
      panel: null,
      CLASS_BASE: "alfresco-twister",
      CLASS_OPEN: "alfresco-twister-open",
      CLASS_CLOSED: "alfresco-twister-closed"
   };

   var elController, elControllerChildren, elPanel,
      config = YAHOO.lang.merge(defaultConfig, p_config || {});

   // Controller element
   elController = YUIDom.get(p_controller);
   if (elController === null)
   {
      return false;
   }

   // Controller element children
   elControllerChildren = YUIDom.getChildren(p_controller);

   // Panel element - next sibling or specified in configuration
   if (config.panel && YUIDom.get(config.panel))
   {
      elPanel = YUIDom.get(config.panel);
   }
   else
   {
      // Find the first sibling node
      elPanel = elController.nextSibling;
      while (elPanel.nodeType !== 1 && elPanel !== null)
      {
         elPanel = elPanel.nextSibling;
      }
   }
   if (elPanel === null)
   {
      return false;
   }

   // MNT-11316 fix, populate Alfresco.util.createTwister.collapsed if required 
   if (Alfresco.util.createTwister.collapsed === undefined)
   {
      var preferences = new Alfresco.service.Preferences();
      if (Alfresco.service.Preferences.COLLAPSED_TWISTERS)
      {
         Alfresco.util.createTwister.collapsed = Alfresco.util.findValueByDotNotation(preferences.get(), Alfresco.service.Preferences.COLLAPSED_TWISTERS) || "";
      }
      else
      {
         Alfresco.util.createTwister.collapsed = "";
      }
   }

   // See if panel should be collapsed via value stored in preferences
   var collapsedPrefs = Alfresco.util.arrayToObject(Alfresco.util.createTwister.collapsed.split(",")),
      isCollapsed = !!collapsedPrefs[p_filterName];

   // Initial State
   YUIDom.addClass(elController, config.CLASS_BASE);
   YUIDom.addClass(elController, isCollapsed ? config.CLASS_CLOSED : config.CLASS_OPEN);
   YUIDom.setStyle(elPanel, "display", isCollapsed ? "none" : "block");

   var twistFun = function(p_event, p_obj)
   {
      var type = p_event.type;
      if(type && type === "keypress")
      {
         var keyCode = p_event.keyCode;
         if(keyCode && keyCode !== YUIKeyListener.KEY.ENTER && keyCode !== YUIKeyListener.KEY.SPACE)
         {
            return;
         }
      }

      // Only expand/collapse if actual twister element is clicked (not for inner elements, i.e. twister actions)
      var isControllerOrChild = false;
      if(YUIEvent.getTarget(p_event) == elController)
      {
         isControllerOrChild = true;
      }
      for(var i=0; i < elControllerChildren.length; i++)
      {
         if(YUIEvent.getTarget(p_event) == elControllerChildren[i])
         {
            isControllerOrChild = true;
         }
      }

      if (isControllerOrChild)
      {
         // Update UI to new state
         var collapse = YUIDom.hasClass(p_obj.controller, config.CLASS_OPEN);
         if (collapse)
         {
            YUIDom.replaceClass(p_obj.controller, config.CLASS_OPEN, config.CLASS_CLOSED);
                  if (p_obj.filterName)
                  {
                      // Update local collapsed preferences : add item
                      Alfresco.util.createTwister.collapsed = !Alfresco.util.createTwister.collapsed ? 
                              p_obj.filterName : Alfresco.util.createTwister.collapsed.concat(",").concat(p_obj.filterName); 
                  }
         }
         else
         {
            YUIDom.replaceClass(p_obj.controller, config.CLASS_CLOSED, config.CLASS_OPEN);
                  if (p_obj.filterName)
                  {
                      // Update local collapsed preferences : remove item
                      var replaceRegExp = new RegExp(p_obj.filterName + ",|," + p_obj.filterName + "|" + p_obj.filterName);
                      Alfresco.util.createTwister.collapsed = Alfresco.util.createTwister.collapsed.replace(replaceRegExp, "");
                  }
         }
         YUIDom.setStyle(p_obj.panel, "display", collapse ? "none" : "block");

         if (p_obj.filterName)
         {
            // Save to preferences
            var fnPref = collapse ? "add" : "remove",
               preferences = new Alfresco.service.Preferences();
            preferences[fnPref].call(preferences, Alfresco.service.Preferences.COLLAPSED_TWISTERS, p_obj.filterName);
         }

         // Stop the event propogating any further (ie into the parent element)
         p_event.stopPropagation();
      }
      
   };

   var twistObj = 
   {
      controller: elController,
      panel: elPanel,
      filterName: p_filterName
   };

   var addListener = function(controller) {
      YUIEvent.addListener(controller, "click", twistFun, twistObj);
      YUIEvent.addListener(controller, "keypress", twistFun, twistObj);
   };

   // Add event listeners for the main control
   addListener(elController);

   // Add event listeners to children if found
   for(var i=0; i < elControllerChildren.length; i++)
   {
      addListener(elControllerChildren[i]);
   }

};

/**
 * Wrapper to create a YUI Panel with common attributes, as follows:
 * <pre>
 *   modal: true,
 *   constraintoviewport: true,
 *   draggable: true,
 *   fixedcenter: true,
 *   close: true,
 *   visible: false
 * </pre>
 * All supplied object parameters are passed to the panel constructor
 * e.g. Alfresco.util.createYUIPanel("myId", { draggable: false });
 *
 * @method Alfresco.util.createYUIPanel
 * @param p_el {string|HTMLElement} The element ID representing the Panel or the element representing the Panel
 * @param p_params {object} Optional extra/overridden object parameters to pass to Panel constructor
 * @param p_custom {object} Optional parameters to customise Panel creation:
 * <pre>
 *    render {boolean} By default the new Panel will be rendered to document.body. Set to false to prevent this.
 *    type {object} Use to override YAHOO.widget.Panel default type, e.g. YAHOO.widget.Dialog
 * </pre>
 * @return {YAHOO.widget.Dialog|flags.type} New Panel instance
 * @static
 */
Alfresco.util.createYUIPanel = function(p_el, p_params, p_custom)
{
   // Default constructor parameters
   var panel,
      params =
      {
         modal: true,
         constraintoviewport: true,
         draggable: true,
         fixedcenter: YAHOO.env.ua.mobile === null ? "contained" : false,
         close: true,
         visible: false,
         postmethod: "none", // Will make Dialogs not auto submit <form>s it finds in the dialog
         hideaftersubmit: false, // Will stop Dialogs from hiding themselves on submits
         fireHideShowEvents: true
      },
      custom =
      {
         render: true,
         type: YAHOO.widget.Dialog
      };

   // Any extra/overridden constructor parameters?
   if (typeof p_params == "object")
   {
      params = YAHOO.lang.merge(params, p_params);
   }
   // Any customisation?
   if (typeof p_custom == "object")
   {
      custom = YAHOO.lang.merge(custom, p_custom);
   }

   // Create and return the panel
   panel = new (custom.type)(p_el, params);

   // Patch YUI's broken bringToTop method. (Spotted while fixing ACE-4629)
   // bringToTop only considers YUI panels when calculating the highest z-index in use and ignores the specified z-index param.
   // If the user has specified a z-index, we want to use that if it's higher than the calculated one.
   if (params.zIndex)
   {
      panel._bringToTop = panel.bringToTop;
      panel.bringToTop = function () {
         var oldZindex = panel.cfg.getProperty("zindex");
         panel._bringToTop();
         var newZindex = panel.cfg.getProperty("zindex");
         // Highest z-index wins.
         if (newZindex < oldZindex) {
            panel.cfg.setProperty("zindex", oldZindex);
         }
      }
   }

   if (custom.render)
   {
      panel.render(document.body);
   }

   if (params.fireHideShowEvents)
   {
      // Let other components react to when a panel is shown or hidden
      panel.subscribe("show", function (p_event, p_args)
      {
         if (!params.fixedcenter)
         {
            panel.center();
         }
         YAHOO.Bubbling.fire("showPanel",
         {
               panel: this
         });
      });
      var hidePanel = function (p_event, p_args)
      {
         YAHOO.Bubbling.fire("hidePanel",
         {
            panel: this
         });
      }
      panel.subscribe("hide", hidePanel);
      panel.subscribe("destroy", hidePanel);
   }
   return panel;
};

/**
 * Wrapper to create a YUI Overlay with common attributes, as follows:
 * <pre>
 *   modal: false,
 *   draggable false,
 *   close: false,
 *   visible: false,
 *   iframe: false
 * </pre>
 *
 * All supplied object parameters are passed to the panel constructor
 * e.g. var overlay = Alfresco.util.createYUIOverlay(Dom.get(this.id),
 * {
 *    effect:
 *    {
 *       effect: YAHOO.widget.ContainerEffect.FADE,
 *       duration: 0.25
 *    }
 * }, { render: false });
 *
 * @method Alfresco.util.createYUIOverlay
 * @param p_el {string|HTMLElement} The element ID representing the Panel or the element representing the Panel
 * @param p_params {object} Optional extra/overridden object parameters to pass to Panel constructor:
 * @param p_custom {object} Optional parameters to customise Panel creation:
 * <pre>
 *    render {boolean} By default the new Overlay will be rendered to document.body. Set to false to prevent this.
 *    type {object} Use to override YAHOO.widget.Overlay default type
 * </pre>
 * @return {YAHOO.widget.Overlay|flags.type} New Overlay instance
 * @static
 */
Alfresco.util.createYUIOverlay = function(p_el, p_params, p_custom)
{
   // Default constructor parameters
   var overlay,
         params =
         {
            modal: false,
            draggable: false,
            close: false,
            visible: false,
            iframe: false,
            fireHideShowEvents: true
         },
         custom =
         {
            render: true,
            type: YAHOO.widget.Overlay
         };

   // Any extra/overridden constructor parameters?
   if (typeof p_params == "object")
   {
      params = YAHOO.lang.merge(params, p_params);
   }

   // Make sure we don't fix center in mobile devices
   if (params.fixedcenter)
   {
      params.fixedcenter = YAHOO.env.ua.mobile === null;
   }

   // Any customisation?
   if (typeof p_custom == "object")
   {
      custom = YAHOO.lang.merge(custom, p_custom);
   }

   // Create and return the overlay
   overlay = new (custom.type)(p_el, params);

   var PREVENT_SCROLLBAR_FIX = (YAHOO.widget.Module.prototype.platform === "mac" && 0 < YAHOO.env.ua.gecko);
   if (PREVENT_SCROLLBAR_FIX)
   {
      // Prevent Mac Firefox 3.x scrollbar bugfix from being applied by YUI
      overlay.hideEvent.unsubscribe(overlay.hideMacGeckoScrollbars, overlay, true);
      overlay.showEvent.unsubscribe(overlay.showMacGeckoScrollbars, overlay, true);
      overlay.hideMacGeckoScrollbars = function (){ Dom.replaceClass(this.element, "prevent-scrollbars", "hide-scrollbars"); },
      overlay.showMacGeckoScrollbars = function(){ Dom.replaceClass(this.element, "hide-scrollbars", "prevent-scrollbars"); };
   }

   if (custom.render)
   {
      overlay.render(document.body);
   }

   if (params.fireHideShowEvents)
   {
      // Let other components react to when a panel is shown or hidden
      overlay.showEvent.subscribe(function (p_event, p_args)
      {
         YAHOO.Bubbling.fire("showOverlay",
         {
            overlay: this
         });
      });
      var hideOverlay = function (p_event, p_args)
      {
         YAHOO.Bubbling.fire("hideOverlay",
         {
            overlay: this
         });
      };
      overlay.hideEvent.subscribe(hideOverlay);
      overlay.destroyEvent.subscribe(hideOverlay);
   }
   return overlay;
};


/**
 * Creates an "information balloon tooltip" UI control attached to a passed-in element.
 * This is similar to the standard balloon UI Control, but designed for better legibility of larger amounts of information
 *
 * @method Alfresco.util.createInfoBalloon
 * @param p_context {Element|string} Element (or DOM ID) to align the balloon to
 * @param p_params {object} Optional additional configuration to override the defaults
 * <pre>
 *    width {string} CSS width of the tooltip. Defaults to 30em
 * </pre>
 * @return {object|null} Balloon instance
 */
Alfresco.util.createInfoBalloon = function(p_context, p_params)
{
   p_params = YAHOO.lang.merge(
         {
            wrapperClass: "info-balloon",
            arrowClass: "info-balloon-arrow"
         }, p_params || {});
   return Alfresco.util.createBalloon(p_context, p_params);
},

/**
 * Creates a "balloon tooltip" UI control attached to a passed-in element.
 *
 * @method Alfresco.util.createBalloon
 * @param p_context {Element|string} Element (or DOM ID) to align the balloon to
 * @param p_params {object} Optional additional configuration to override the defaults
 * <pre>
 *    width {string} CSS width of the tooltip. Defaults to 30em
 * </pre>
 * @param p_autoDisplay {boolean} Hides
 * @return {object|null} Balloon instance
 */
Alfresco.util.createBalloon = function(p_context, p_params, showEvent, hideEvent)
{
   var elContext = YUIDom.get(p_context);
   if (YAHOO.lang.isNull(elContext))
   {
      return null;
   }

   p_params = YAHOO.lang.merge(
   {
      effectType: YAHOO.widget.ContainerEffect.FADE,
      effectDuration: 0.25,
      html: "",
      text: "",
      closeButton: true,
      /*width: "30em",*/
      wrapperClass: "balloon",
      arrowClass: "balloon-arrow"
   }, p_params || {});

   var balloon = new Alfresco.widget.Balloon(elContext, p_params);

   if (showEvent)
   {
      YAHOO.util.Event.addListener(p_context, showEvent, balloon.show, balloon, true);
   }
   if (hideEvent)
   {
      YAHOO.util.Event.addListener(p_context, hideEvent, balloon.hide, balloon, true);
   }

   return balloon;
};

(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event;

   /**
    * Alfresco library aliases
    */
   var $html = Alfresco.util.encodeHTML,
         PREVENT_SCROLLBAR_FIX = (YAHOO.widget.Module.prototype.platform === "mac" && 0 < YAHOO.env.ua.gecko);

   /**
    * Alfresco.widget.Balloon constructor.
    * Should not be created directly, but via the Alfresco.util.createBalloon static function.
    *
    * @param p_context {Element|string} Element (or DOM ID) to align the balloon to
    * @param p_params {object} Optional additional configuration to override the defaults
    * @return {Alfresco.widget.Balloon} The new Balloon instance
    * @constructor
    */
   Alfresco.widget.Balloon = function(p_context, p_params)
   {
      this.context = p_context;
      var balloon = new Alfresco.util.createYUIOverlay(Alfresco.util.generateDomId(), {
            context: [
               p_context,
               "bl",
               "tl",
               ["beforeShow", "windowResize", "windowScroll", "textResize"]
            ],
            constraintoviewport: true,
            visible: false,
            width: p_params.width || "auto",
            zIndex: 10,
            effect: {
               effect: p_params.effectType,
               duration: p_params.effectDuration
            },
            fireHideShowEvents: false
         });

      var wrapper = document.createElement("div"),
            arrow = document.createElement("div");

      Dom.addClass(wrapper, p_params.wrapperClass);
      Dom.addClass(arrow, p_params.arrowClass);

      if (p_params.closeButton)
      {
         var closeButton = document.createElement("div");
         closeButton.innerHTML = "x";
         Dom.addClass(closeButton, "closeButton");
         Event.addListener(closeButton, "click", this.hide, this, true);
         
         // Register the ESC key
         this.escapeListener = new YUIKeyListener(document,
         {
            keys: YUIKeyListener.KEY.ESCAPE
         },
         {
            fn: function(eventName, keyEvent)
            {
               this.hide();
            },
            scope: this,
            correctScope: true
         });
         this.escapeListener.enable();
         
         wrapper.appendChild(closeButton);
      }

      var content = document.createElement("div");
      Dom.addClass(content, "text");
      content.innerHTML = p_params.html || $html(p_params.text);
      wrapper.appendChild(content);
      wrapper.appendChild(arrow);

      balloon.setBody(wrapper);
      balloon.render(Dom.get("doc3"));

      this.balloon = balloon;
      this.content = content;

      this.onClose = new YAHOO.util.CustomEvent("close" , this);
      this.onShow = new YAHOO.util.CustomEvent("show" , this);

      // Register to enable hide all functionality.
      this.name = "Alfresco.widget.Balloon";
      this.id = this.balloon.id;
      Alfresco.util.ComponentManager.register(this);

      return this;
   };

   Alfresco.widget.Balloon.prototype =
   {
      /**
       * The element to position the balloon after
       *
       * @property context
       */
      context: null,

      /**
       * YAHOO.widget.Overlay instance
       *
       * @property balloon
       */
      balloon: null,

      /**
       * Element containing balloon's content
       *
       * @property content
       */
      content: null,

      /**
       * Hides the balloon
       *
       * @method hide
       */
      hide: function Balloon_hide()
      {
         this.balloon.hide();
         // Unregister here since this is the closest we've got to a destroy method.
         Alfresco.util.ComponentManager.unregister(this);
         this.onClose.fire();
      },

      /**
       * Shows the balloon
       *
       * @method show
       */
      show: function Balloon_show()
      {
         // MNT-10630 Sync Info Link is not working on Gallery View of document library page.
         // delete Alfresco.util.isVisible(this.context)
         if (this.content.innerHTML && this.content.innerHTML.length > 0)
         {
            // Reregister to ensure we're tracked.
            Alfresco.util.ComponentManager.reregister(this);
            this.balloon.show();
            this.balloon.bringToTop();

            // Hide Other Balloons
            this.hideOthers();

            this.onShow.fire();
         }
      },

      /**
       * Ensures that the current Balloon is the only one showing.
       * @method hideOthers
       */
      hideOthers: function Balloon_hideOthers()
      {
         var balloons = Alfresco.util.ComponentManager.find({ name: this.name});
         for (var i=0; i < balloons.length; i++)
         {
            if (balloons[i].id != this.id)
            {
               balloons[i].balloon.hide();
            }
         }
      },

      /**
       * Sets the HTML content of the balloon.
       *
       * @method html
       * @param content {String} Contents will be inserted as-is with no escaping.
       */
      html: function Balloon_html(content)
      {
         this.content.innerHTML = content;
         this.balloon.align();
      },

      /**
       * Sets the text content of the balloon.
       *
       * @method text
       * @param content {String} Contents will be inserted after being safely HTML-encoded.
       */
      text: function Balloon_text(content)
      {
         this.content.innerHTML = $html(content);
         this.balloon.align();
      }
   };
})();


(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Anim = YAHOO.util.Anim;
   
   /**
    * Alfresco library aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Alfresco.widget.FullScreen constructor.
    * Toggles full-screen mode for the given context element
    *
    * @param p_context {object} the HTML element ID to be made full-screen
    * @param p_params {object} the parameters
    * @return {Alfresco.widget.FullScreen} The new instance
    * @constructor
    */
   Alfresco.widget.FullScreen = function(p_context, p_params)
   {
      this.context = Dom.get(p_context);
      if (p_params)
      {
         this.params = YAHOO.lang.merge(Alfresco.util.deepCopy(this.params), p_params);
      }
      var fullScreenInstance = this;

      Event.addListener(document, "MSFullscreenChange", function()
      {
         fullScreenInstance.onFullScreenChange();
      });
      Event.addListener(document, "fullscreenchange", function()
      {
         fullScreenInstance.onFullScreenChange();
      });
      Event.addListener(document, "mozfullscreenchange", function()
      {
         fullScreenInstance.onFullScreenChange();
      });
      Event.addListener(document, "webkitfullscreenchange", function()
      {
         fullScreenInstance.onFullScreenChange();
      });

      return this;
   };
   
   Alfresco.widget.FullScreen.prototype =
   {
      /**
       * The element to make full screen
       *
       * @property context
       */
      context: null,
      
      /**
       * The current full screen mode
       *
       * @property context
       */
      isWindowOnly: true,
      
      /**
       * The parameters for full screen
       *
       * @property params
       */
      params:
      {
         pageContainerId: "Share"
      },
      
      /**
       * Toggles full-screen mode for the current context element
       *
       * @method toggleFullScreen
       */
      toggleFullScreen: function FullScreen_toggleFullScreen(isWindowOnly)
      {
         if (this.context != null)
         {
            if (!document.fullscreen && !document.mozFullScreen && !document.webkitFullScreen)
            {
               this.requestFullScreen(isWindowOnly);
            }
            else
            {
               this.cancelFullScreen();
            }
         }
      },
      
      /**
       * Enters full-screen mode for the current context element
       *
       * @method requestFullScreen
       */
      requestFullScreen: function FullScreen_requestFullScreen(isWindowOnly)
      {
         if (isWindowOnly != null)
         {
            this.isWindowOnly = isWindowOnly;
         }
         if (this.isWindowOnly)
         {
            this.toggleFullWindow();
            return;
         }
         var container = Dom.get(this.context);
         if (container.msRequestFullscreen || container.requestFullscreen || container.mozRequestFullScreen || container.webkitRequestFullScreen)
         {
            Dom.addClass(container, 'alf-fullscreen');
            Dom.addClass(container, 'alf-entering-true-fullscreen');
         }
         if (container.msRequestFullscreen)
         {
            container.msRequestFullscreen();
         }
         else if (container.requestFullscreen)
         {
            container.requestFullscreen();
         }
         else if (container.mozRequestFullScreen)
         {
            container.mozRequestFullScreen();
         }
         else if (container.webkitRequestFullScreen)
         {
            // TODO Safari bug doesn't support keyboard input
            if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1)
            {
               container.webkitRequestFullScreen();
            }
            else
            {
               container.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
            }
         }
         else
         {
            this.toggleFullWindow();
         }
      },
      
      /**
       * Exits full-screen mode for the current context element
       *
       * @method cancelFullScreen
       */
      cancelFullScreen: function FullScreen_cancelFullScreen()
      {
         if (this.isWindowOnly)
         {
            this.toggleFullWindow();
            return;
         }
         if (document.msExitFullscreen)
         {
            document.msExitFullscreen();
         }
         else if (document.exitFullscreen)
         {
            document.exitFullscreen();
         }
         else if (document.mozCancelFullScreen)
         {
            document.mozCancelFullScreen();
         }
         else if (document.webkitCancelFullScreen)
         {
            document.webkitCancelFullScreen();
         }
         else
         {
            this.toggleFullWindow();
         }
      },
      
      /**
       * Handles changes to the full screen mode
       *
       * @method onFullScreenChange
       */
      onFullScreenChange: function FullScreen_onFullScreenChange()
      {
         if (this.context != null)
         {
            var container = Dom.get(this.context);
            if (Dom.hasClass(container, 'alf-entering-true-fullscreen'))
            {
               Dom.removeClass(container, 'alf-entering-true-fullscreen');
               // Let resizing take place then add the true-fullscreen class
               setTimeout(function()
               {
                  Dom.addClass(container, 'alf-true-fullscreen');
               }, 1000);
            }
            else
            {
               if (Dom.hasClass(container, 'alf-true-fullscreen'))
               {
                  if (Dom.hasClass(container, 'alf-fullscreen'))
                  {
                     // Exiting true fullscreen complete
                     Dom.removeClass(container, 'alf-fullscreen');
                     Dom.removeClass(container, 'alf-true-fullscreen');
                     YAHOO.Bubbling.fire("fullScreenExitComplete",
                     {
                         scope: this.context,
                         eventGroup: this.context.id
                     });
                  }
               }
               else
               {
                  // We've probably been programatically called in fullwindow mode
                  if (!Dom.hasClass(container, 'alf-fullscreen'))
                  {
                     Dom.addClass(container, 'alf-fullscreen');
                     YAHOO.Bubbling.fire("fullScreenEnterComplete",
                     {
                         scope: this.context,
                         eventGroup: this.context.id
                     });
                  }
                  else
                  {
                     Dom.removeClass(container, 'alf-fullscreen');
                     YAHOO.Bubbling.fire("fullScreenExitComplete",
                     {
                         scope: this.context,
                         eventGroup: this.context.id
                     });
                  }
               }
            }
         }
      },
      
      /**
       * Toggles full-window mode for the current context element for browsers that don't support full-screen or
       * explicit setting of params.isWindowOnly=true.
       *
       * @method toggleFullWindow
       */
      toggleFullWindow: function FullScreen_toggleFullWindow()
      {
         if (this.context != null)
         {
            var pageContainer = Dom.get(this.params.pageContainerId);
            var container = Dom.get(this.context);
            if (!Dom.hasClass(pageContainer, 'alf-fullwindow'))
            {
               Dom.addClass(pageContainer, 'alf-fullwindow');
               var fullscreen = this;
               this.context.escKeyListener = function(e)
               {
                  if (e.keyCode == 27) {
                     fullscreen.toggleFullWindow();
                  }
               }
			   
               Event.addListener(document, "keydown", this.context.escKeyListener);
            }
            else
            {
               Dom.removeClass(pageContainer, 'alf-fullwindow');
               if (this.context.escKeyListener)
               {
                  Event.removeListener(document, "keydown", this.context.escKeyListener);
               }
            }
            this.onFullScreenChange();
         }
      }
   }
})();

(function()
{
/**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco.widget.PasswordStrengthMeter constructor.
    * Should not be created directly, but via the Alfresco.util.createBalloon static function.
    *
    * @param p_context {object}
    * @param p_params {object}
    * @return {Alfresco.widget.PasswordStrengthMeter} The new instance
    * @constructor
    */
   Alfresco.widget.PasswordStrengthMeter = function(p_context, p_params)
   {
      this.context = Dom.get(p_context);
      this.params = YAHOO.lang.merge(Alfresco.util.deepCopy(this.params), p_params);
      this._generateMarkup();
      return this;
   };

   Alfresco.widget.PasswordStrengthMeter.prototype =
   {
      context: null,

      params:
      {
         /**
          * The users username used to match passwords against.
          *
          * @property username
          * @type string
          */
         username: null,

         /**
          * Minimum length of a password
          *
          * @property minLength
          * @type int
          * @default 0
          */
         minLength: 0,

         /**
          * Minimum number of upper case characters required in passwords
          *
          * @property minPasswordUpper
          * @type int
          * @default 0
          */
         minUpper: 0,

         /**
          * Minimum number of lower case characters required in passwords
          *
          * @property minPasswordLower
          * @type int
          * @default 0
          */
         minLower: 0,

         /**
          * Minimum number of numeric characters required in passwords
          *
          * @property minPasswordNumeric
          * @type int
          * @default 0
          */
         minNumeric: 0,

         /**
          * Minimum number of non-alphanumeric characters required in passwords
          *
          * @property minPasswordSymbols
          * @type int
          * @default 0
          */
         minSymbols: 0
      },

      /**
       * Generate mark-up
       *
       * @method _generateMarkup
       * @protected
       */
      _generateMarkup: function PasswordStrengthMeter__generateMarkup()
      {
         Dom.addClass(this.context, "passwordStrengthMeter");

         var label = document.createElement("span");
         label.appendChild(document.createTextNode(Alfresco.util.message("password-strength-meter.label")));
         this.context.appendChild(label);

         var strengths = document.createElement("ul"),
            strength;
         for (var i = 0; i < 4; i++)
         {
            strength = document.createElement("li");
            Dom.addClass(strength, "passwordStrengthMeterStrength-" + (i + 1));
            strength.innerHTML = "&nbsp;";
            strengths.appendChild(strength);
         }
         this.context.appendChild(strengths);

         var clear = document.createElement("div");
         Dom.addClass(clear, "clear");
         this.context.appendChild(clear);
      },

      /**
       * Will update password strength meter based on the password parameter.
       *
       * @method setStrength
       * @param password {string} The password
       */
      setStrength: function AccountCompletion_setStrength(password)
      {
         // Get password & reset password strength meter
         password = YAHOO.lang.trim(password);

         var passwordStrengthEl = Selector.query("ul", this.context, true);
         passwordStrengthEl.setAttribute("class", "");

         // Display password strength if it is a password
         var strength = Alfresco.util.getPasswordStrength(password, {
            username: this.params.username,
            minLength: this.params.minLength,
            minUpper: this.params.minUpper,
            minLower: this.params.minLower,
            minNumeric: this.params.minNumeric,
            minSymbols: this.params.minSymbols
            
         });
         if (strength)
         {
            Dom.addClass(passwordStrengthEl, "passwordStrengthMeterStrength-" + strength.name);
         }
      }
   };

})();

(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco.widget.MultiSelectAutoComplete constructor.
    * Should not be created directly, but via the Alfresco.util.createBalloon static function.
    *
    * @param p_context {object}
    * @param p_params {object}
    * @return {Alfresco.widget.MultiSelectAutoComplete} The new instance
    * @constructor
    */
   Alfresco.widget.MultiSelectAutoComplete = function(p_context, p_params)
   {
      this.context = Dom.get(p_context);
      this.params = YAHOO.lang.merge(Alfresco.util.deepCopy(this.params), p_params);
      this.markupGenerated = false;
      this.hiddenInput = null;
      this.itemIds = null;
      this.newInput = null;
      this.newInputUsed = false;
      this.itemContainer = null;
      this.autoComplete = null;
      this.queuedPasteEvent = false;
      if(!this.context || !this.params.itemUrl)
      {
         throw new Error("Parameter p_context and p_params.itemUrl must be provided.");
      }

      this._generateMarkup();

      return this;
   };

   Alfresco.widget.MultiSelectAutoComplete.prototype =
   {
      context: null,

      params:
      {
         /**
          *
          */
         value: "",

         /**
          * The url to retrieve the auto complete data including a query substitute token to insert the actual query term.
          * I.e. "proxy/alfresco/api/people?filter={query}"
          *
          * @property itemUrl
          * @type string
          */
         itemUrl: null,


         /**
          * The path to the items array in the server response
          *
          * @property itemPath
          * @type string
          */
         itemPath: "data",

         /**
          *
          *
          * @property itemId
          * @type string
          */
         itemId: "id",

         /**
          * This is the field that will be displayed once an item is selected.
          * It will also be used in the suggested items menu as long as itemTemplate hasn't been defined.
          *
          * @property itemName
          * @type string
          */
         itemName: "name",

         /**
          * For more complex displays of the suggested items a template can be provided.
          * I.e. "<b>{firstName}</b> {lastName}"
          *
          * @property itemTemplate
          * @type string
          */
         itemTemplate: null,

         /**
          * Item ids that not will be displayed in the result
          *
          * @property forbiddenItemIds
          * @type array
          */
         forbiddenItemIds: [],

         /**
          * A form instance to add the validations to.
          *
          */
         form: null,

         /**
          * Decides how the selected values shall be stored.
          * "single" - stores them in 1 single hidden input element (with the selected items ids in a comma separated string)
          * "multiple" - stores them in multiple hidden input elements (one per selected value)
          *
          * @property formInputMode
          * @type string
          */
         formInputMode: "multiple",

         /**
          * The value of the hidden input elements name attribute, i.e. something like
          * "emails" when formInputMode is "single" or "emails[]" when formInputMode is "multiple".
          *
          * @property formInputName
          * @type string
          */
         formInputName: "-",

         /**
          * Standard callback object to add in custom behaviour when a new item is created.
          *
          * @property onItemInputCreate
          * @type object
          */
         onItemInputCreate: null,

         /**
          * Standard callback object to add in custom behaviour when escape is clicked.
          *
          * @property onItemInputEscape
          * @type object
          */
         onItemInputEscape: null,

         /**
          * The key codes that will make the typed word into a box.
          * By default just enter, but a comma could also be used by setting it to:
          * [YAHOO.util.KeyListener.KEY.ENTER, 44]
          *
          * @property delimiterKeyCodes
          * @type Array
          * @default []
          */
         delimiterKeyCodes: [ YAHOO.util.KeyListener.KEY.ENTER ],

         /**
          * When pasting in text to the control the usual delimiterKeyCodes act as delimiters against the pasted string.
          * However when the paste is done newline characters are transformed to space characters.
          * To make newline characters act as delimiters on paste set this to [ YAHOO.util.KeyListener.KEY.SPACE ]
          *
          * @property pasteDelimiterKeyCodes
          * @type Array
          * @default []
          */
         pasteDelimiterKeyCodes: [],

         /**
          * The number of characters that must be entered before sending a request for auto complete suggestions
          *
          * @property minQueryLength
          * @type int
          * @default 1
          */
         minQueryLength: 1
      },

      /**
       *
       */
      focus: function()
      {
         if (Alfresco.util.isVisible(this.newInput))
         {
            this.newInput.focus();
         }
      },

      /**
       * Generate mark-up
       *
       * @method _generateMarkup
       * @protected
       */
      _generateMarkup: function MultiSelectAutoComplete__generateMarkup()
      {
         // Reset the array of persisted item ids...
         this.itemIds = [];

         if (this.markupGenerated)
         {
            this._generateCurrentItemMarkup();
            return;
         }

         Dom.addClass(this.context, "input");

         var eAutoCompleteWrapper = document.createElement("span"),
            eAutoComplete = document.createElement("div");

         if (this.params.formInputMode == "single")
         {
            // Create a hidden input field - the value of this field is what will be used to update the
            this.hiddenInput = document.createElement("input");
            YUIDom.setAttribute(this.hiddenInput, "type", "hidden");
            YUIDom.setAttribute(this.hiddenInput, "name", this.params.formInputName);
         }

         // Create a new input field for entering new items (this will also allow the user to select items from
         // an auto-complete list...
         this.newInput = document.createElement("input");
         YUIDom.setAttribute(this.newInput, "type", "text");
         YUIDom.setAttribute(this.newInput, "tabindex", "0");

         // Add the new item input field and the auto-complete drop-down DIV element to the auto-complete wrapper
         eAutoCompleteWrapper.appendChild(this.newInput);
         eAutoCompleteWrapper.appendChild(eAutoComplete);

         // Create a new edit box (this contains all item spans, as well as the auto-complete enabled input field for
         // adding new items)...
         var editBox = document.createElement("div");
         YUIDom.addClass(editBox, "inlineItemEdit"); // This class should make the span look like a text input box
         this.itemContainer = document.createElement("span");
         editBox.appendChild(this.itemContainer);
         editBox.appendChild(eAutoCompleteWrapper); // Add the auto-complete wrapper (this contains the input field for typing items)

         // Add any previously applied items to the edit box, updating the array of applied item nodeRefs as we go...
         this._generateCurrentItemMarkup();

         // Add the main edit box to the form (all the items go in this box)
         this.context.appendChild(editBox);

         YUIDom.addClass(eAutoCompleteWrapper, "inlineItemEditAutoCompleteWrapper");
         YUIDom.addClass(eAutoComplete, "inlineItemEditAutoComplete");

         if (this.hiddenInput)
         {
            // Set the current items in the hidden field and add it to the dom...
            YUIDom.setAttribute(this.hiddenInput, "value", this.itemIds.toString());
            this.context.appendChild(this.hiddenInput);
         }


         /* ************************************************************************************
          *
          * This section of code deals with setting up the auto-complete widget for the new item
          * input field. We need to set up a data source for retrieving the existing items and
          * which we will need to filter on the client.
          *
          **************************************************************************************/
         var oDS = new YAHOO.util.XHRDataSource();
         oDS.connXhrMode = "cancelStaleRequests";
         oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
         oDS.maxCacheEntries = 10;
         // This schema indicates where to find the item name in the JSON response
         oDS.responseSchema =
         {
            /*fields : [this.params.itemName, this.params.itemId],*/
            resultsList : this.params.itemPath
         };
         this.autoComplete = new YAHOO.widget.AutoComplete(this.newInput, eAutoComplete, oDS);
         this.autoComplete.questionMark = false;     // Removes the question mark on the query string (this will be ignored anyway)
         var url = this.params.itemUrl;
         this.autoComplete.generateRequest = function(query)
         {
            return YAHOO.lang.substitute(url, { query: query });
         };
         this.autoComplete.applyLocalFilter = false;  // Filter the results on the client
         this.autoComplete.queryDelay = 0.2;           // Throttle requests sent
         this.autoComplete.animSpeed = 0.08;
         this.autoComplete.minQueryLength = this.params.minQueryLength;
         this.autoComplete.itemSelectEvent.subscribe(function(type, args)
         {
            // If the user clicks on an entry in the list then apply the selected item
            var itemName = args[2][1][this.params.itemName],
                itemId = args[2][1][this.params.itemId];
            this._applyItem(itemName, itemId, { type: "change" });
            if (YUIDom.isAncestor(this.itemContainer, this.newInput))
            {
               // We must have just finished editing a item, therefore we need to move
               // the auto-complete box out of the current items...
               YUIDom.insertAfter(this.newInput.parentNode, this.itemContainer);
            }
         }, this, true);
         // Update the result filter to remove any results that have already been used...
         this.autoComplete.dataReturnEvent.subscribe(function(type, args)
         {
            var results = args[2], currentItemId;
            for (i = 0, j = results.length; i < j; i++)
            {
               currentItemId = results[i][this.params.itemId];
               if (Alfresco.util.arrayContains(this.itemIds, currentItemId) || Alfresco.util.arrayContains(this.params.forbiddenItemIds, currentItemId))
               {
                  results.splice(i, 1); // Remove the result because it's already been used
                  i--;                  // Decrement the index because it's about to get incremented (this avoids skipping an entry)
                  j--;                  // Decrement the target length, because the arrays got shorter
               }
            }
         }, this, true);

         // User custom template if provided
         if (this.params.itemTemplate)
         {
            this.autoComplete.formatResult = Alfresco.util.bind(function(oResultData, sQuery, sResultMatch)
            {
               return YAHOO.lang.substitute(this.params.itemTemplate, oResultData[1], function(key, value)
               {
                  return Alfresco.util.encodeHTML(value);
               });
            }, this);
         }
         /* **************************************************************************************
          *
          * This section of code deals with handling enter keypresses in the new item input field.
          * We need to capture ENTER keypresses and prevent the form being submitted, but instead
          * make a request to create the item provided and then add it to the hidden variable that
          * will get submitted when the "Save" link is used.
          *
          ****************************************************************************************/
         var _this = this;
         Event.addListener(this.newInput, "keyup", function(e)
         {
            _this.newInputUsed = true;
            if (this.value.length > 0)
            {
               // Remove error indications since have started to type something
               Dom.removeClass(_this.itemContainer.parentNode, "mandatory");
               Dom.removeClass(_this.context, "invalid");

               if (_this.queuedPasteEvent)
               {
                  _this.queuedPasteEvent = false;
                  var inputValue = this.value,
                     c, code,
                     values = [],
                     value = '';

                  for(var i = 0; i < inputValue.length; i++)
                  {
                     c = inputValue.charAt(i);
                     code = c.charCodeAt(0);
                     if (Alfresco.util.arrayContains(_this.params.delimiterKeyCodes, code) || Alfresco.util.arrayContains(_this.params.pasteDelimiterKeyCodes, code))
                     {
                        value = YAHOO.lang.trim(value);
                        if (value.length > 0)
                        {
                           values.push(value);
                        }
                        value = '';
                     }
                     else
                     {
                        value += c;
                     }
                  }
                  value = YAHOO.lang.trim(value);
                  if (value.length > 0)
                  {
                     values.push(value);
                  }

                  this.value = "";
                  for (i = 0; i < values.length; i++)
                  {
                     value = YAHOO.lang.trim(values[i]);
                     _this._createItem(value, value, null);
                     YUIDom.insertAfter(_this.newInput.parentNode, _this.itemContainer);
                  }

                  YAHOO.lang.later(50, _this.newInput, function()
                  {
                     // Focus the input element for IE
                     this.focus();
                     _this._triggerFormValidation({ type: "keyup" }, _this.context);
                  });
               }
            }
         });
         Event.addListener(this.newInput, "paste", function(e)
         {
            _this.queuedPasteEvent = true;
         });
         Event.addListener(this.newInput, "keypress", function(e)
         {
            if (Alfresco.util.hasKeyCode(e, _this.params.delimiterKeyCodes) && this.value.length > 0)
            {
               Event.stopEvent(e); // Prevent the surrounding form from being submitted
               _this._createItem(this.value, this.value, { type: "change"});
               YUIDom.insertAfter(_this.newInput.parentNode, _this.itemContainer);
               YAHOO.lang.later(50, _this.newInput, function()
               {
                  // Focus the input element for IE
                  this.focus();
               });
            }
         });

         // This section of code handles deleting configured items through the use of the backspace key....
         Event.addListener(this.newInput, "keydown", function(e)
         {
            if (Alfresco.util.hasKeyCode(e, 8) && this.newInput.value.length == 0)
            {
               if (this._editingItemIndex >= 0)
               {
                  // If a item is being edited then we just need to remove the item and reset the input field
                  this.itemIds.splice(this._editingItemIndex, 1); // Remove the item, the item span has already been removed
                  YUIDom.insertAfter(this.newInput.parentNode, this.itemContainer); // Return the auto-complete elements to their correct position
                  this._hideFormErrorContainer();
               }
               else if (!this._itemPrimedForDelete && this.itemContainer.children.length > 0)
               {
                  this._itemPrimedForDelete = true;
                  var lastItem = YUIDom.getLastChild(this.itemContainer);
                  YUIDom.addClass(lastItem, "inlineItemEditItemPrimed");
                  YUIDom.addClass(lastItem.children[2], "hidden");
                  YUIDom.removeClass(lastItem.children[3], "hidden");
                  this._hideFormErrorContainer(lastItem);
               }
               else
               {
                  // The backspace key was used when there are no more characters to delete
                  // so we need to delete the last item...
                  if (this.itemIds.length > 0)
                  {
                     this.itemIds.pop();
                     if (this.hiddenInput)
                     {
                        YUIDom.setAttribute(this.hiddenInput, "value", this.itemIds.toString());
                     }
                     var itemEl = YUIDom.getLastChild(this.itemContainer);
                     this.itemContainer.removeChild(itemEl);
                     this._hideFormErrorContainer(itemEl);
                  }
                  this._itemPrimedForDelete = false; // If we've deleted a item then we're no longer primed for deletion...
               }
            }
            else if (this._itemPrimedForDelete == true)
            {
               // If any key other than backspace is pressed and the last item has been primed for deletion
               // then we should put it back to the normal state...
               this._itemPrimedForDelete = false;
               if (this.itemContainer.children.length > 0)
               {
                  var lastItem = YUIDom.getLastChild(this.itemContainer);
                  YUIDom.removeClass(lastItem, "inlineItemEditItemPrimed");
                  YUIDom.addClass(lastItem.children[3], "hidden");
                  YUIDom.removeClass(lastItem.children[2], "hidden");
               }
            }
         }, this, true);

         this.autoComplete.textboxBlurEvent.subscribe(function(type, args)
         {
            this.newInputUsed = true;
            var value = YAHOO.lang.trim(args[0].getInputEl().value);
            if (value.length > 0)
            {
               this._createItem(value, value, { type: "blur" });
            }
            else
            {
               YAHOO.lang.later(50, this, function()
               {
                  // Delay validation since blur event sometimes happens before other events that might change the value
                  this._triggerFormValidation({ type: "blur" }, this.context);
               });
            }
            YUIDom.insertAfter(this.newInput.parentNode, this.itemContainer);
         }, this, true);

         Event.addListener(this.newInput, "focus", function(type, args)
         {
            this._triggerFormValidation({ type: "focus" }, this.context);
         }, this, true);

         Event.addListener(editBox, "click", function(e)
         {
            YAHOO.lang.later(50, this, function()
            {
               // Select the input element for IE
               if (document.activeElement != this.newInput)
               {
                  this.newInput.select();
               }
            });
            Event.stopEvent(e);

         }, this, true);

         // Key Listener for [Escape] to cancel
         var escapeKeyListener = new KeyListener(this.newInput,
         {
            keys: [KeyListener.KEY.ESCAPE]
         },
         {
            fn: function(id, keyEvent)
            {
               Event.stopEvent(keyEvent[1]);
               this.newInput.value = "";
               if (this.params.onItemInputEscape && YAHOO.lang.isFunction(this.params.onItemInputEscape))
               {
                  this.params.onItemInputEscape.fn.call(this.params.onItemInputEscape.scope);
               }
            },
            scope: this,
            correctScope: true
         });
         escapeKeyListener.enable();

         // Connect to form if available
         if (this.params.form)
         {
            // Add our internal validator methods that will make sure all the validators in the formValidations param are used.
            this.params.form.addValidation(this.context, Alfresco.util.bind(this.isValid, this), {}, "blur", Alfresco.util.bind(this.getTitle, this), Alfresco.util.bind(this.getValidationConfig, this));
         }

         this.markupGenerated = true;
      },

      _itemIdValidations: {},
      _validations: [],

      /**
       * @method addValidation
       * @param validationHandler {function} Function to call to handle the actual validation
       * @param validationArgs {object} Optional object representing the arguments to pass to the validation handler function
       * @param message {string} Message to be displayed when validation fails
       */
      addValidation: function(validationHandler, validationArgs, message)
      {
         this._validations.push(
         {
            handler: validationHandler,
            args: validationArgs,
            message: message
         });
      },

      _triggerFormValidation: function(event, field)
      {
         if (this.params.form)
         {
            // So getTitle() will be correct since messages is called before isValid()
            this.isValid();

            // Call forms runtime so it can call our callbacks: getTitle(), isValid() & getValidationConfig()
            this.params.form.validate(event, field.id);
         }
      },

      _hideFormErrorContainer: function(field)
      {
         this._errorContainer = null;

         if (this.params.form)
         {
            if (field)
            {
               this.params.form.hideErrorContainer(field);
            }
            this.params.form.hideErrorContainer(this.context);
         }
      },

      isValid: function()
      {
         if (!this.newInputUsed)
         {
            // handle the mandatory case by ourselves
            if (this.params.mandatory && this.params.value.length == 0)
            {
               Dom.addClass(this.itemContainer.parentNode, "mandatory");
            }
            return true;
         }
         Dom.removeClass(this.itemContainer.parentNode, "mandatory");

         // First check all itemIds validation state
         var itemEls = YAHOO.util.Selector.query(".inlineItemEditItem", this.context),
            value;
         for (var i = 0; i < itemEls.length; i++)
         {
            value = YAHOO.util.Selector.query("input", itemEls[i], true).value;
            if (!(this._itemIdValidations[value] || 0) == (this._validations || []).length)
            {
               if (!Dom.get(this._errorContainer))
               {
                  this._errorContainer = itemEls[i]; // this.context;
               }
               Dom.addClass(this.itemContainer.parentNode.parentNode, "suppress-validation");
               return false;
            }
         }

         if (this.itemIds.length == 0 && this.params.mandatory)
         {
            Dom.removeClass(this.itemContainer.parentNode.parentNode, "suppress-validation");
            this._errorContainer = this.context;
            this._errorMessage = Alfresco.util.message("Alfresco.forms.validation.mandatory.message");
            return false;
         }

         this._errorMessage = null;
         this._errorContainer = null;
         return true;
      },

      getTitle: function()
      {
         return (this._errorMessage ? this._errorMessage : (this.params.title || ""));
      },

      getValidationConfig: function()
      {
         return {};
      },

      /**
       * Called after a new item has been added, will
       */
      _validateItem: function(itemValue, itemName, container)
      {
         var valid = true;
         if (YAHOO.lang.isArray(this._validations))
         {
            var v;
            for (var i = 0; i < this._validations.length; i++)
            {
               v = this._validations[i];

               // Call validator by faking the fieldId parameter pretending its a hidden input field
               var result = v.handler({ value: itemValue, type: "hidden" }, v.args, {}, this.params.form);
               if (YAHOO.lang.isBoolean(result) && result)
               {
                  // Validation was ok
                  this._itemIdValidations[itemValue] = (this._itemIdValidations[itemValue] || 0) + 1;
               }
               else
               {
                  // The value was invalid
                  valid = false;
                  this._errorContainer = container; //this.context;;
                  this._errorMessage = v.message;

                  if (YAHOO.lang.isNumber(result))
                  {
                     // The result is a transaction id waiting for server response
                     this._errorMessage = Alfresco.util.message("Alfresco.widget.MultiSelectAutoComplete.pending");
                  }
               }
            }
         }

         // We got here then all items have valid values
         return valid;
      },

      _createItem: function MultiSelectAutoComplete__createItem(itemName, itemId, triggerFormValidationEvent)
      {
         this._applyItem(itemName, itemId, triggerFormValidationEvent);

         if (YUIDom.isAncestor(this.itemContainer, this))
         {
            // We must have just finished editing a item, therefore we need to move
            // the auto-complete box out of the current items...
            YUIDom.insertAfter(this.newInput.parentNode, this.itemContainer);
         }

         if (this.params.onItemCreate && YAHOO.lang.isFunction(this.params.onItemCreate))
         {
            this.params.onItemCreate.fn.call(this.params.onItemCreate.scope, itemId);
         }
      },

      /**
       * Adds a new span that represents an applied item. This span contains an icon that can
       * be clicked on to remove the item.
       *
       * @method _addItem
       * @param itemName The name of the item
       * @param itemId The id of the item
       */
      _addItem: function MultiSelectAutoComplete__addItem(itemName, itemId)
      {
         var item = Alfresco.util.encodeHTML(itemName),
            invalidIcon = document.createElement("img"),
            span = document.createElement("span"),
            label = document.createElement("span"),
            hiddenItemInput = document.createElement("input"),
            removeIcon = document.createElement("img"),
            removeIconEdit = document.createElement("img");

         Alfresco.util.generateDomId(span);
         YUIDom.addClass(span, "inlineItemEditItem");
         //span.setAttribute("tabindex", "0");
         label.innerHTML = item;
         YUIDom.setAttribute(invalidIcon, "src", Alfresco.constants.URL_RESCONTEXT + "components/images/warning-16.png");
         YUIDom.setAttribute(removeIcon, "src", Alfresco.constants.URL_RESCONTEXT + "components/images/delete-item-off.png");
         YUIDom.setAttribute(removeIcon, "width", 16);
         YUIDom.setAttribute(removeIconEdit, "src", Alfresco.constants.URL_RESCONTEXT + "components/images/delete-item-on.png");
         YUIDom.setAttribute(removeIconEdit, "width", 16);
         YUIDom.addClass(removeIconEdit, "hidden");
         span.appendChild(invalidIcon);
         span.appendChild(label);
         span.appendChild(removeIcon);
         span.appendChild(removeIconEdit);

         var result = this._validateItem(itemId, itemName, span);
         if (YAHOO.lang.isBoolean(result))
         {
            if (result)
            {
               YUIDom.addClass(invalidIcon, "hidden");
            }
            else
            {
               Dom.addClass(span, "invalid");
            }
         }
         else if (YAHOO.lang.isNumber(result))
         {
            YUIDom.addClass(invalidIcon, "hidden");
         }

         if (this.params.formInputMode == "multiple")
         {
            YUIDom.setAttribute(hiddenItemInput, "type", "hidden");
            YUIDom.setAttribute(hiddenItemInput, "name", this.params.formInputName);
            YUIDom.setAttribute(hiddenItemInput, "value", itemId);
            span.appendChild(hiddenItemInput);
         }

         // Make sure we add the item in the right place...
         if (YUIDom.isAncestor(this.itemContainer, this.newInput))
         {
            // An existing item has been edited, so insert it before the input item...
            YUIDom.insertBefore(span, this.newInput.parentNode);
         }
         else
         {
            // Add the new item at the end of the list...
            this.itemContainer.appendChild(span);
         }
         this._editingItemIndex = -1; // If we've just added a item then we're not editing one


         // Function for deterining the index of a item...
         var findItemIndex = function InsituEditor_itemEditor_findItemIndex(itemSpan)
         {
            // Get the index of where the span ended up, needed to insert the nodeRef in the correct place...
            var spanIndex = 0,
                tmp = itemSpan;
            while((tmp = tmp.previousSibling) != null)
            {
               spanIndex++;
            }
            return spanIndex;
         };

         var _this = this;

         // Function for removing a itemId from the array of itemids...
         var removeItemId = function MultiSelectAutoComplete_removeItemId(itemId)
         {
            var index = Alfresco.util.arrayIndex(_this.itemIds, itemId);
            if (index != -1)
            {
               _this.itemIds.splice(index, 1);
            }
         };

         // Handler the user choosing to remove a item...
         Event.addListener(removeIcon, "click", function(e)
         {
            removeItemId(itemId);
            if (this.hiddenInput)
            {
               YUIDom.setAttribute(_this.hiddenInput, "value", _this.itemIds.toString());
            }
            _this.itemContainer.removeChild(span);

            // Make sure ballons are updated
            _this._hideFormErrorContainer(span);
         });

         // Handle the user choosing to edit a item...
         Event.addListener(label, "click", function(e)
         {
            // When the item label is clicked we need to make it editable. The new item box needs to
            // replace the item span and have it's value set to the item being edited.
            YUIDom.insertBefore(_this.newInput.parentNode, span);
            _this.itemContainer.removeChild(span);
            _this.newInput.value = itemName;

            removeItemId(itemId);
            _this._editingItemIndex = findItemIndex(span); // Set the index of the span being edited.

            _this._hideFormErrorContainer(span);

            _this.newInput.select();

            Event.stopEvent(e);
         });

         return findItemIndex(span);
      },

      /**
       * Applies a item to the document being edited. This will add a new span to represent
       * the applied item, update the the overall hidden input field that will be submitted
       * and reset the new item input field.
       *
       * @method _applyItem
       * @param itemName The name of the item
       * @param itemId The ItemId of the item
       */
      _applyItem: function MultiSelectAutoComplete__applyItem(itemName, itemId, triggerFormValidationEvent)
      {
         var index = this._addItem(itemName, itemId);
         this.newInput.value = "";

         // Add the itemId of the item into the hidden value field...
         this.itemIds.splice(index, 0, itemId);
         if (this.hiddenInput)
         {
            YUIDom.setAttribute(this.hiddenInput, "value", this.itemIds.toString());
         }

         // Ensure that auto-complete drop-down is hidden...
         this.autoComplete.collapseContainer();

         // Make sure balloons are updated
         if (triggerFormValidationEvent)
         {
            this._triggerFormValidation(triggerFormValidationEvent, this.context);
         }
      },

      /**
       * Generates the markup that displays the currently applied items. This function
       * can be called once the main markup has been created to allow only item changes
       * to be rendered.
       *
       * @method _generateCurrentItemMarkup
       */
      _generateCurrentItemMarkup: function MultiSelectAutoComplete__generateCurrentItemMarkup()
      {
         // Clear any previously created span items...
         if (this.itemContainer.hasChildNodes())
         {
             while (this.itemContainer.childNodes.length >= 1)
             {
                this.itemContainer.removeChild( this.itemContainer.firstChild );
             }
         }

         // Add any previously applied items to the edit box, updating the array of applied item itemIds as we go...
         this.params.value = this.params.value || [];
         // Need to check that the value param has been set because if the node did not have
         // a cm:itemgable option then it would be undefined.
         for (i = 0, j = this.params.value.length; i < j; i++)
         {
            this._addItem(this.params.value[i][this.params.itemName], this.params.value[i][this.params.itemId]);
            this.itemIds.push(this.params.value[i][this.params.itemId]);
         }
      }

   };

})();


/**
 * Create Insitu Editor
 *
 * @method Alfresco.util.createInsituEditor
 * @param p_context {HTMLElement} DOM node controlling editor visibility
 * @param p_params {Object} Instance configuration parameters
 * @param p_callback {Object} Callback function after successful edit operation
 * @return {Object|null} New instance of an insitu editor
 * @static
 */
Alfresco.util.createInsituEditor = function(p_context, p_params, p_callback)
{
   var elContext = YUIDom.get(p_context);
   if (YAHOO.lang.isNull(elContext))
   {
      return false;
   }

   p_params = YAHOO.lang.merge(
         {
            showDelay: 600,
            hideDelay: 600,
            autoDismissDelay: 0,
            container: null,
            context: elContext,
            callback: p_callback,
            error: null,
            disabled: false,
            type: "textBox",
            nodeRef: null,
            name: null,
            value: "",
            title: null
         }, p_params || {});

   if (Alfresco.widget.InsituEditor[p_params.type])
   {
      return (new Alfresco.widget.InsituEditor[p_params.type](p_params));
   }

   return null;
};

(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event,
         Element = YAHOO.util.Element,
         KeyListener = YAHOO.util.KeyListener;

   Alfresco.widget.InsituEditorIcon = function(p_editor, p_params)
   {
      this.editor = p_editor;
      this.params = YAHOO.lang.merge({}, p_params);
      this.disabled = p_params.disabled;

      this.editIcon = document.createElement("span");
      this.editIcon.title = Alfresco.util.encodeHTML(p_params.title);
      Dom.addClass(this.editIcon, "insitu-edit");

      if (this.params.container !== null)
      {
         Dom.get(this.params.container).appendChild(this.editIcon);
      }
      else
      {
         this.params.context.parentNode.insertBefore(this.editIcon, this.params.context);
      }

      Event.on(this.params.context, "mouseover", this.onContextMouseOver, this);
      Event.on(this.params.context, "mouseout", this.onContextMouseOut, this);
      Event.on(this.editIcon, "mouseover", this.onContextMouseOver, this);
      Event.on(this.editIcon, "mouseout", this.onContextMouseOut, this);
   };

   Alfresco.widget.InsituEditorIcon.prototype =
   {
      /**
       * Flag to disable the editor
       *
       * @property disabled
       */
      disabled: null,

      /**
       * Fade the editor icon out
       *
       * @method _fadeOut
       * @param p_element {HTMLElement} The icon element
       * @protected
       */
      _fadeOut: function InsituEditorIcon__fadeOut(p_element)
      {
         var fade = new YAHOO.util.Anim(p_element,
               {
                  opacity:
                  {
                     to: 0
                  }
               }, 0.2);

         fade.onComplete.subscribe(function(e, data, obj)
         {
            Event.removeListener(obj.editIcon, "click");
            Dom.setStyle(p_element, "visibility", "hidden");
            Dom.setStyle(p_element, "opacity", 0);
         }, this);

         fade.animate();
      },

      /**
       * Fade the editor icon in
       *
       * @method _fadeIn
       * @param p_element {HTMLElement} The icon element
       * @protected
       */
      _fadeIn: function InsituEditorIcon__fadeIn(p_element)
      {
         var fade = new YAHOO.util.Anim(p_element,
               {
                  opacity:
                  {
                     to: 1
                  }
               }, 0.2);

         fade.onComplete.subscribe(function(e, data, obj)
         {
            Dom.setStyle(p_element, "opacity", 1);
            Event.removeListener(obj.editIcon, "click");
            Event.on(obj.editIcon, "click", obj.onIconClick, obj);
         }, this);

         Dom.setStyle(p_element, "visibility", "visible");
         fade.animate();
      },

      /**
       * The default event handler fired when the user mouses over the context element.
       *
       * @method onContextMouseOver
       * @param {DOMEvent} e The current DOM event
       * @param {Object} obj The object argument
       */
      onContextMouseOver: function InsituEditorIcon_onContextMouseOver(e, obj)
      {
         if (obj.disabled)
         {
            return;
         }

         // Stop the icon from being hidden (set on last mouseout)
         if (obj.hideProcId)
         {
            clearTimeout(obj.hideProcId);
            obj.hideProcId = null;
         }

         obj.showProcId = obj.doIconShow(e, this);
      },

      /**
       * The default event handler fired when the user mouses out of the context element.
       *
       * @method onContextMouseOut
       * @param {DOMEvent} e The current DOM event
       * @param {Object} obj The object argument
       */
      onContextMouseOut: function InsituEditorIcon_onContextMouseOut(e, obj)
      {
         if (obj.disabled)
         {
            return;
         }

         if (obj.showProcId)
         {
            clearTimeout(obj.showProcId);
            obj.showProcId = null;
         }

         if (obj.hideProcId)
         {
            clearTimeout(obj.hideProcId);
            obj.hideProcId = null;
         }

         obj.hideProcId = setTimeout(function()
         {
            obj._fadeOut(obj.editIcon);
         }, obj.params.hideDelay);
      },

      /**
       * The default event handler fired when the user clicks the icon element.
       *
       * @method onIconClick
       * @param {DOMEvent} e The current DOM event
       * @param {Object} obj The object argument
       */
      onIconClick: function InsituEditorIcon_onIconClick(e, obj)
      {
         if (obj.disabled)
         {
            return;
         }

         Alfresco.logger.debug("onIconClick", e);
         obj.editor.doShow();
         Event.stopEvent(e);
      },

      /**
       * Processes the showing of the edit icon
       *
       * @method doIconShow
       * @param {DOMEvent} e The current DOM event
       * @param {HTMLElement} context The current context element
       * @return {Number} The process ID of the timeout function associated with doIconShow
       */
      doIconShow: function InsituEditorIcon_doIconShow(e, context)
      {
         var me = this;

         return window.setTimeout(function()
         {
            me._fadeIn(me.editIcon);
            me.hideProcId = me.doHide();
         }, me.params.showDelay);
      },

      /**
       * Sets the timeout for the auto-dismiss delay
       *
       * @method doHide
       */
      doHide: function InsituEditorIcon_doHide()
      {
         if (this.params.autoDismissDelay < 1)
         {
            return null;
         }

         var me = this;

         return window.setTimeout(function()
         {
            me._fadeOut(me.editIcon);
         }, me.params.autoDismissDelay);
      }
   };

   /**
    * Insitu Editor components.
    *
    * @namespace Alfresco.widget
    * @class Alfresco.widget.InsituEditor
    */
   Alfresco.widget.InsituEditor = {};

   /**
    * Insitu Editor base class, from which editor implementations should be extended.
    *
    * @namespace Alfresco.widget.InsituEditor
    * @class Alfresco.widget.InsituEditor.base
    */
   Alfresco.widget.InsituEditor.base = function(p_params)
   {
      this.params = YAHOO.lang.merge({}, p_params);

      var nodeRef = new Alfresco.util.NodeRef(this.params.nodeRef),
            elEditForm = new Element(document.createElement("form"),
                  {
                     id: Alfresco.util.generateDomId(),
                     method: "post",
                     action: Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/formprocessor"
                  });

      this.elEditForm = elEditForm;
      this.editForm = elEditForm.get("element");

      // Form definition
      this.form = new Alfresco.forms.Form(this.editForm);
      this.form.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onPersistSuccess,
                  scope: this
               },
               failureCallback:
               {
                  fn: this.onPersistFailure,
                  scope: this
               }
            });
      this.form.setSubmitAsJSON(true);

      elEditForm.addClass("insitu-edit");
      elEditForm.on("submit", function(e)
      {
         Event.stopEvent(e);
      });

      // Create editor icon instance
      this.editorIcon = new Alfresco.widget.InsituEditorIcon(this, p_params);

      this.params.context.parentNode.insertBefore(this.editForm, this.params.context);

      return this;
   };

   Alfresco.widget.InsituEditor.base.prototype =
   {
      /**
       * Configuration parameters.
       *
       * @property params
       * @type object
       */
      params: null,

      /**
       * YAHOO.util.Element representing <form> node.
       *
       * @property elEditForm
       * @type YAHOO.util.Element
       */
      elEditForm: null,

      /**
       * <form> DOM element.
       *
       * @property elEditForm
       * @type HTMLElement
       */
      editForm: null,

      /**
       * Forms Runtime instance.
       *
       * @property form
       * @type Alfresco.forms.Form
       */
      form: null,

      /**
       * Generic helper method for invoking a Alfresco.util.Ajax.request() from a responseConfig object
       *
       * @method _jsonCall
       * @param method {string} The method for the XMLHttpRequest
       * @param url {string} The url for the XMLHttpRequest
       * @param dataObj {object} An object that will be transformed to a json string and put in the request body
       * @param responseConfig.successCallback {object} A success callback object
       * @param responseConfig.successMessage {string} A success message
       * @param responseConfig.failureCallback {object} A failure callback object
       * @param responseConfig.failureMessage {string} A failure message
       * @private
       */
      _jsonCall: function InsituEditor_base__jsonCall(method, url, dataObj, responseConfig)
      {
         responseConfig = responseConfig || {};
         Alfresco.util.Ajax.jsonRequest(
               {
                  method: method,
                  url: url,
                  dataObj: dataObj,
                  successCallback: responseConfig.successCallback,
                  successMessage: responseConfig.successMessage,
                  failureCallback: responseConfig.failureCallback,
                  failureMessage: responseConfig.failureMessage,
                  noReloadOnAuthFailure: responseConfig.noReloadOnAuthFailure || false
               });
      },

      /**
       * Abstract function
       *
       * @method doShow
       */
      doShow: function InsituEditor_base_doShow()
      {
         Alfresco.logger.debug("Alfresco.widget.InsituEditor", "Abstract implementation 'doShow()' not overridden");
      },

      /**
       * Abstract function
       *
       * @method doHide
       * @param restoreUI {boolean} Whether to restore the UI or rely on the caller to do it
       */
      doHide: function InsituEditor_base_doHide(restoreUI)
      {
         Alfresco.logger.debug("Alfresco.widget.InsituEditor", "Abstract implementation 'doHide()' not overridden");
      },

      /**
       * Successful property persistence handler
       *
       * @method onPersistSuccess
       * @param response {Object} Server response object literal
       */
      onPersistSuccess: function InsituEditor_base_onPersistSuccess(response)
      {
         var restoreUI = true;
         if (this.params.callback.fn)
         {
            restoreUI = this.params.callback.fn.call(this.params.callback.scope || this, response, this.params.callback.obj);
         }
         this.doHide(restoreUI);
      },

      /**
       * Failure property persistence handler
       *
       * @method onPersistFailure
       * @param response {Object} Server response object literal
       */
      onPersistFailure: function InsituEditor_base_onPersistFailure(response)
      {
         // Allow the callee to handle the error
         if (this.params.error && this.params.error.fn)
         {
            this.params.error.fn.call(this.params.error.scope || this, response, this.params.error.obj);
         }
      }
   };

   /**
    * Alfresco.widget.InsituEditor.textBox constructor.
    *
    * @param p_params {Object} Instance configuration parameters
    * @return {Alfresco.widget.InsituEditor.textBox} The new textBox editor instance
    * @constructor
    */
   Alfresco.widget.InsituEditor.textBox = function(p_params)
   {
      Alfresco.widget.InsituEditor.textBox.superclass.constructor.call(this, p_params);

      this.balloon = null;
      this.suppressInputBoxFocus = false;
      this.contextStyle = null;
      this.keyListener = null;
      this.markupGenerated = false;

      return this;
   };

   /*
    * Alfresco.widget.InsituEditor.textBox
    *
    *  <form>
    *     <input type="text" value="digital photograph record.jpg">
    *     <a href="#" style="font-size: 13px; margin-left: 0.5em; padding-top: 0px; padding-right: 0.5em; padding-bottom: 0px; padding-left: 0.5em; ">Save</a>
    *     <a href="#" style="font-size: 13px; padding-top: 0px; padding-right: 0.5em; padding-bottom: 0px; padding-left: 0.5em; ">Cancel</a>
    *  </form>
    */
   YAHOO.extend(Alfresco.widget.InsituEditor.textBox, Alfresco.widget.InsituEditor.base,
         {
            /**
             * Balloon UI instance used for error reporting
             *
             * @property balloon
             * @type object
             */
            balloon: null,

            /**
             * Flag to prevent setting focus on the input text box
             *
             * @property suppressInputBoxFocus
             * @type boolean
             */
            suppressInputBoxFocus: null,

            /**
             * Save context elements style CSS property so it can be restored correctly
             *
             * @property contextStyle
             * @type string
             */
            contextStyle: null,

            /**
             * Key Listener for [Escape] to cancel
             *
             * @property keyListener
             * @type YAHOO.util.KeyListener
             */
            keyListener: null,

            /**
             * Flag tracking whether markup has been generated
             *
             * @property markupGenerated
             * @type boolean
             */
            markupGenerated: null,

            /**
             * Show the editor
             *
             * @method doShow
             * @override
             */
            doShow: function InsituEditor_textBox_doShow()
            {
               this._generateMarkup();
               if (this.contextStyle === null)
               {
                  this.contextStyle = Dom.getStyle(this.params.context, "display");
               }
               Dom.setStyle(this.params.context, "display", "none");
               Dom.setStyle(this.editForm, "display", "inline");
               this.keyListener.enable();
               if (typeof this.params.fnSelect === "function")
               {
                  this.params.fnSelect(this.inputBox, this.params.value);
               }
               else
               {
                  this.inputBox.select();
               }
            },

            /**
             * Hide the editor
             *
             * @method doHide
             * @param restoreUI {boolean} Whether to restore the UI or rely on the caller to do it
             * @override
             */
            doHide: function InsituEditor_textBox_doHide(restoreUI)
            {
               this.balloon.hide();
               this.keyListener.disable();
               if (restoreUI)
               {
                  Dom.setStyle(this.editForm, "display", "none");
                  Dom.setStyle(this.params.context, "display", this.contextStyle);
               }
            },

            /**
             * Failure property persistence handler
             *
             * @override
             * @method onPersistFailure
             * @param response {Object} Server response object literal
             */
            onPersistFailure: function InsituEditor_textBox_onPersistFailure(response)
            {
               Alfresco.widget.InsituEditor.textBox.superclass.onPersistFailure.call(this, response);
               this.balloon.text(this.params.errorMessage);
               this.balloon.show();
            },

            /**
             * Generate mark-up
             *
             * @method _generateMarkup
             * @protected
             */
            _generateMarkup: function InsituEditor_textBox__generateMarkup()
            {
               if (this.markupGenerated)
               {
                  return;
               }

               // Generate input box markup
               var eInput = new Element(document.createElement("input"),
                           {
                              type: "text",
                              name: this.params.name,
                              value: this.params.value
                           }),
                     eSave = new Element(document.createElement("a"),
                           {
                              href: "#",
                              innerHTML: Alfresco.util.message("button.save")
                           }),
                     eCancel = new Element(document.createElement("a"),
                           {
                              href: "#",
                              innerHTML: Alfresco.util.message("button.cancel")
                           });

               this.elEditForm.appendChild(eInput);
               this.elEditForm.appendChild(eSave);
               this.elEditForm.appendChild(eCancel);

               eInput.on("blur", function(e)
               {
                  if (this.balloon)
                  {
                     this.suppressInputBoxFocus = true;
                     this.balloon.hide();
                  }
                  this.suppressInputBoxFocus = false;
               }, this, true);

               eSave.on("click", function(e)
               {
                  this.form._submitInvoked(e);
               }, this, true);

               eCancel.on("click", function(e)
               {
                  Event.stopEvent(e);
                  this.inputBox.value = this.params.value;
                  this.doHide(true);
                  this.form.validate();
                  this.form.hideErrorContainer();
               }, this, true);

               this.inputBox = eInput.get("element");

               // Key Listener for [Escape] to cancel
               this.keyListener = new KeyListener(this.inputBox,
                     {
                        keys: [KeyListener.KEY.ESCAPE]
                     },
                     {
                        fn: function(id, keyEvent)
                        {
                           Event.stopEvent(keyEvent[1]);
                           this.inputBox.value = this.params.value;
                           this.doHide(true);
                        },
                        scope: this,
                        correctScope: true
                     });

               // Balloon UI for errors
               this.balloon = Alfresco.util.createBalloon(this.inputBox);
               this.balloon.onClose.subscribe(function(e)
               {
                  try
                  {
                     if (!this.suppressInputBoxFocus)
                     {
                        this.inputBox.focus();
                     }
                  }
                  catch (e)
                  {
                  }
               }, this, true);

               // Register validation handlers
               var vals = this.params.validations;
               for (var i = 0, ii = vals.length; i < ii; i++)
               {
                  this.form.addValidation(this.inputBox, vals[i].type, vals[i].args, vals[i].when, vals[i].message);
               }
               this.form.addValidation(this.inputBox, Alfresco.forms.validation.length,
               {
                  max: 255,
                  crop: true
               }, "keyup");

               // Initialise the form
               this.form.init();

               this.markupGenerated = true;
            }
         });

   /**
    * Alfresco.widget.InsituEditor.tagEditor constructor.
    *
    * @param p_params {Object} Instance configuration parameters
    * @return {Alfresco.widget.InsituEditor.tagEditor} The new tagEditor instance
    * @constructor
    */
   Alfresco.widget.InsituEditor.tagEditor = function(p_params)
   {
      Alfresco.widget.InsituEditor.tagEditor.superclass.constructor.call(this, p_params);

      this.balloon = null;
      this.suppressInputBoxFocus = false;
      this.contextStyle = null;
      this.keyListener = null;
      this.markupGenerated = false;

      return this;
   };

   /*
    * Alfresco.widget.InsituEditor.tagEditor
    */
   YAHOO.extend(Alfresco.widget.InsituEditor.tagEditor, Alfresco.widget.InsituEditor.base,
         {
            /**
             * Balloon UI instance used for error reporting
             *
             * @property balloon
             * @type object
             */
            balloon: null,

            /**
             * Flag to prevent setting focus on the input text box
             *
             * @property suppressInputBoxFocus
             * @type boolean
             */
            suppressInputBoxFocus: null,

            /**
             * Save context elements style CSS property so it can be restored correctly
             *
             * @property contextStyle
             * @type string
             */
            contextStyle: null,

            /**
             * Key Listener for [Escape] to cancel
             *
             * @property keyListener
             * @type YAHOO.util.KeyListener
             */
            keyListener: null,

            /**
             * A list of the tag node references. This is the data that will be submitted
             * when the Insitu Editor is saved.
             *
             * @property tagRefs
             * @type array
             */
            tagRefs: null,

            /**
             * A hidden input element that contains the only data used by the form when submitted.
             * The value is updated from the contents of the tagRefs property.
             *
             * @property hiddenInput
             * @type HTMLElement
             */
            hiddenInput: null,

            /**
             * An input element used for typing in new tags. Also used as part of a YUI auto-complete
             * widget to allow selection from previously created tags.
             *
             * @property newTagInput
             * @type HTMLElement
             */
            newTagInput: null,

            /**
             * A span element that contains a span element for each tag already
             * applied to the document.
             *
             * @property currentTags
             * @type HTMLElement
             */
            currentTags: null,

            /**
             * Flag tracking whether markup has been generated
             *
             * @property markupGenerated
             * @type boolean
             */
            markupGenerated: null,

            /**
             * The YUI auto-complete widget used for entering new tags.
             *
             * @property tagAutoComplete
             * @type YAHOO.widget.AutoComplete
             */
            tagAutoComplete: null,

            /**
             * The index of the tag that is currently being edited. This will be
             * set to -1 if a tag isn't currently being edited.
             *
             * @property _editingTagIndex
             * @type int
             */
            _editingTagIndex: -1,

            /**
             * Indicates that a tag is primted for deletion. This occurs when the backspace key is used
             * when there are no characters entered in the newTagInput field. A further use of the backspace
             * key will result in the tag being deleted.
             *
             * @property _tagPrimedForDelete
             * @type boolean
             */
            _tagPrimedForDelete: false,

            /**
             * Show the editor
             *
             * @method doShow
             * @override
             */
            doShow: function InsituEditor_tagEditor_doShow()
            {
               this._generateMarkup();
               if (this.contextStyle === null)
               {
                  this.contextStyle = Dom.getStyle(this.params.context, "display");
               }
               Dom.setStyle(this.params.context, "display", "none");
               Dom.setStyle(this.editForm, "display", "inline");
               this.keyListener.enable();
               this.inputBox.select();
            },

            /**
             * Hide the editor
             *
             * @method doHide
             * @param restoreUI {boolean} Whether to restore the UI or rely on the caller to do it
             * @override
             */
            doHide: function InsituEditor_tagEditor_doHide(restoreUI)
            {
               this.balloon.hide();
               this.keyListener.disable();
               if (restoreUI)
               {
                  Dom.setStyle(this.editForm, "display", "none");
                  Dom.setStyle(this.params.context, "display", this.contextStyle);
               }
            },

            /**
             * Successful property persistence handler
             *
             * @method onPersistSuccess
             * @param response {Object} Server response object literal
             */
            onPersistSuccess: function InsituEditor_base_onPersistSuccess(response)
            {
               var restoreUI = true;
               if (this.params.callback.fn)
               {
                  restoreUI = this.params.callback.fn.call(this.params.callback.scope || this, response, this.params.callback.obj);
               }
               this.doHide(restoreUI);

               // Fire an event to cause the tags to refresh. A short-timeout is used to compensate for requests coming back too
               // tag requests being completed before server side persistence is complete. Tests without this timeout have shown
               // that tag updates get missed more often than not.
               window.setTimeout(function() { YAHOO.Bubbling.fire("tagRefresh") }, 1000);
            },

            /**
             * Failure property persistence handler
             *
             * @override
             * @method onPersistFailure
             * @param response {Object} Server response object literal
             */
            onPersistFailure: function InsituEditor_tagEditor_onPersistFailure(response)
            {
               Alfresco.widget.InsituEditor.textBox.superclass.onPersistFailure.call(this, response);
               this.balloon.text(this.params.errorMessage);
               this.balloon.show();
            },

            /**
             * Adds a new span that represents an applied tag. This span contains an icon that can
             * be clicked on to remove the tag.
             *
             * @method _addTag
             * @param value The name of the tag
             * @param nodeRef The nodeRef of the tag
             */
            _addTag: function InsituEditor_tagEditor__addTag(value, nodeRef)
            {
               tag = Alfresco.util.encodeHTML(value);
               var span = document.createElement("span");
               YUIDom.addClass(span, "inlineTagEditTag");
               var label = document.createElement("span");
               label.innerHTML = tag;
               var removeIcon = document.createElement("img"),
                  removeIconEdit = document.createElement("img");
               YUIDom.setAttribute(removeIcon, "src", Alfresco.constants.URL_RESCONTEXT + "components/images/delete-item-off.png");
               YUIDom.setAttribute(removeIcon, "width", 16);
               YUIDom.setAttribute(removeIconEdit, "src", Alfresco.constants.URL_RESCONTEXT + "components/images/delete-item-on.png");
               YUIDom.setAttribute(removeIconEdit, "width", 16);
               YUIDom.addClass(removeIconEdit, "hidden");
               span.appendChild(label);
               span.appendChild(removeIcon);
               span.appendChild(removeIconEdit);

               // Make sure we add the tag in the right place...
               if (YUIDom.isAncestor(this.currentTags, this.newTagInput))
               {
                  // An existing tag has been edited, so insert it before the input tag...
                  YUIDom.insertBefore(span, this.newTagInput.parentNode);
               }
               else
               {
                  // Add the new tag at the end of the list...
                  this.currentTags.appendChild(span);
               }
               this._editingTagIndex = -1; // If we've just added a tag then we're not editing one


               // Function for deterining the index of a tag...
               var findTagIndex = function InsituEditor_tagEditor_findTagIndex(tagSpan)
               {
                  // Get the index of where the span ended up, needed to insert the nodeRef in the correct place...
                  var spanIndex = 0,
                        tmp = tagSpan;
                  while((tmp = tmp.previousSibling) != null)
                  {
                     spanIndex++;
                  }
                  return spanIndex;
               }

               var _this = this;

               // Function for removing a nodeRef from the array of refs...
               var removeNodeRef = function InsituEditor_tagEditor_removeNodeRef(nodeRef)
               {
                  var index = Alfresco.util.arrayIndex(_this.tagRefs, nodeRef);
                  if (index != -1)
                  {
                     _this.tagRefs.splice(index, 1);
                  }
               }

               // Handler the user choosing to remove a tag...
               Event.addListener(removeIcon, "click", function(e)
               {
                  removeNodeRef(nodeRef);
                  YUIDom.setAttribute(_this.hiddenInput, "value", _this.tagRefs.toString());
                  _this.currentTags.removeChild(span);
               });

               // Handle the user choosing to edit a tag...
               Event.addListener(label, "click", function(e)
               {
                  // When the tag label is clicked we need to make it editable. The new tag box needs to
                  // replace the tag span and have it's value set to the tag being edited.
                  YUIDom.insertBefore(_this.newTagInput.parentNode, span);
                  _this.currentTags.removeChild(span);
                  _this.newTagInput.value = value;
                  removeNodeRef(nodeRef);
                  _this._editingTagIndex = findTagIndex(span); // Set the index of the span being edited.
               });

               return findTagIndex(span);
            },

            /**
             * Applies a tag to the document being edited. This will add a new span to represent
             * the applied tag, update the the overall hidden input field that will be submitted
             * and reset the new tag input field.
             *
             * @method _applyTag
             * @param value The name of the tag
             * @param nodeRef The nodeRef of the tag
             */
            _applyTag: function InsituEditor_tagEditor__applyTag(tagName, nodeRef)
            {
               var index = this._addTag(tagName, nodeRef);
               this.newTagInput.value = "";

               // Add the nodeRef of the tag into the hidden value field...
               this.tagRefs.splice(index, 0, nodeRef);
               YUIDom.setAttribute(this.hiddenInput, "value", this.tagRefs.toString());

               // Ensure that auto-complete drop-down is hidden...
               this.tagAutoComplete.collapseContainer();
            },

            /**
             * Generates the markup that displays the currently applied tags. This function
             * can be called once the main markup has been created to allow only tag changes
             * to be rendered.
             *
             * @method _generateCurrentTagMarkup
             */
            _generateCurrentTagMarkup: function InsituEditor_tagEditor__generateCurrentTagMarkup()
            {
               // Clear any previously created span tags...
               if (this.currentTags.hasChildNodes())
               {
                  while (this.currentTags.childNodes.length >= 1)
                  {
                     this.currentTags.removeChild( this.currentTags.firstChild );
                  }
               }

               // Add any previously applied tags to the edit box, updating the array of applied tag nodeRefs as we go...
               if (this.params.value != undefined)
               {
                  // Need to check that the value param has been set because if the node did not have
                  // a cm:taggable option then it would be undefined.
                  for (i = 0, j = this.params.value.length; i < j; i++)
                  {
                     this._addTag(this.params.value[i].name, this.params.value[i].nodeRef);
                     this.tagRefs.push(this.params.value[i].nodeRef);
                  }
               }
            },

            /**
             * Generate mark-up
             *
             * @method _generateMarkup
             * @protected
             */
            _generateMarkup: function InsituEditor_tagEditor__generateMarkup()
            {
               // Reset the array of persisted tag nodeRefs...
               this.tagRefs = [];

               if (this.markupGenerated)
               {
                  this._generateCurrentTagMarkup();
                  return;
               }

               var eAutoCompleteWrapper = document.createElement("span"),
                     eAutoComplete = document.createElement("div"),
                     eSave = new Element(document.createElement("a"),
                           {
                              // ALF-19091 fixing.
                              href: window.location.hash,
                              innerHTML: Alfresco.util.message("button.save")
                           }),
                     eCancel = new Element(document.createElement("a"),
                           {
                              href: "#",
                              innerHTML: Alfresco.util.message("button.cancel")
                           });

               // Create a hidden input field - the value of this field is what will be used to update the
               // cm:taggable property of the document when the "Save" button is clicked.
               this.hiddenInput = document.createElement("input");
               YUIDom.setAttribute(this.hiddenInput, "type", "hidden");
               YUIDom.setAttribute(this.hiddenInput, "name", this.params.name);

               // Create a new input field for entering new tags (this will also allow the user to select tags from
               // an auto-complete list...
               this.newTagInput = document.createElement("input");
               YUIDom.setAttribute(this.newTagInput, "type", "text");

               // Add the new tag input field and the auto-complete drop-down DIV element to the auto-complete wrapper
               eAutoCompleteWrapper.appendChild(this.newTagInput);
               eAutoCompleteWrapper.appendChild(eAutoComplete);

               // Create a new edit box (this contains all tag spans, as well as the auto-complete enabled input field for
               // adding new tags)...
               var editBox = document.createElement("div");
               YUIDom.addClass(editBox, "inlineTagEdit"); // This class should make the span look like a text input box
               this.currentTags = document.createElement("span");
               editBox.appendChild(this.currentTags);
               editBox.appendChild(eAutoCompleteWrapper); // Add the auto-complete wrapper (this contains the input field for typing tags)

               // Add any previously applied tags to the edit box, updating the array of applied tag nodeRefs as we go...
               this._generateCurrentTagMarkup();

               // Set the current tags in the hidden field...
               YUIDom.setAttribute(this.hiddenInput, "value", this.tagRefs.toString());

               // Add the main edit box to the form (all the tags go in this box)
               this.elEditForm.appendChild(editBox);

               YUIDom.addClass(eAutoCompleteWrapper, "inlineTagEditAutoCompleteWrapper");
               YUIDom.addClass(eAutoComplete, "inlineTagEditAutoComplete");
               this.elEditForm.appendChild(this.hiddenInput);
               this.elEditForm.appendChild(eSave);
               this.elEditForm.appendChild(eCancel);

               /* ************************************************************************************
                *
                * This section of code deals with setting up the auto-complete widget for the new tag
                * input field. We need to set up a data source for retrieving the existing tags and
                * which we will need to filter on the server.
                *
                **************************************************************************************/
               var oDS = new YAHOO.util.XHRDataSource(Alfresco.constants.PROXY_URI + "api/forms/picker/category/workspace/SpacesStore/tag:tag-root/children?selectableType=cm:category&size=100&aspect=cm:taggable&searchTerm=");
               oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
               oDS.connXhrMode = "cancelStaleRequests";
               // This schema indicates where to find the tag name in the JSON response
               oDS.responseSchema =
               {
                  resultsList : "data.items",
                  fields : ["name", "nodeRef"]
               };
               this.tagAutoComplete = new YAHOO.widget.AutoComplete(this.newTagInput, eAutoComplete, oDS);
               // force using 'searchTerm' parameter
               this.tagAutoComplete.generateRequest = function(sQuery)
               {
                  return sQuery;
               };
               this.tagAutoComplete.queryDelay = 0.1           // Throttle requests sent
               this.tagAutoComplete.animSpeed = 0.08;
               this.tagAutoComplete.itemSelectEvent.subscribe(function(type, args)
               {
                  // If the user clicks on an entry in the list then apply the selected tag
                  var tagName = args[2][0],
                        nodeRef = args[2][1];
                  this._applyTag(tagName, nodeRef);
                  if (YUIDom.isAncestor(this.currentTags, this.newTagInput))
                  {
                     // We must have just finished editing a tag, therefore we need to move
                     // the auto-complete box out of the current tags...
                     YUIDom.insertAfter(this.newTagInput.parentNode, this.currentTags);
                  }
               }, this, true);
               // Update the result filter to remove any results that have already been used...
               this.tagAutoComplete.dataReturnEvent.subscribe(function(type, args)
               {
                  var results = args[2];
                  for (i = 0, j = results.length; i < j; i++)
                  {
                     var currNodeRef = results[i].nodeRef;
                     var index = Alfresco.util.arrayIndex(this.tagRefs, currNodeRef);
                     if (index != -1)
                     {
                        results.splice(i, 1); // Remove the result because it's already been used
                        i--;                  // Decrement the index because it's about to get incremented (this avoids skipping an entry)
                        j--;                  // Decrement the target length, because the arrays got shorter
                     }
                  }
               }, this, true);


               /* **************************************************************************************
                *
                * This section of code deals with handling enter keypresses in the new tag input field.
                * We need to capture ENTER keypresses and prevent the form being submitted, but instead
                * make a request to create the tag provided and then add it to the hidden variable that
                * will get submitted when the "Save" link is used.
                *
                ****************************************************************************************/
               var _this = this;
               Event.addListener(this.newTagInput, "keypress", function(e)
               {
                  if (e.keyCode == 13 && this.value.length > 0)
                  {
                     Event.stopEvent(e); // Prevent the surrounding form from being submitted
                     _this._createTag(this.value, false, e);
                  }
               });

               // This section of code handles deleting configured tags through the use of the backspacce key....
               var _this = this;
               Event.addListener(this.newTagInput, "keydown", function(e)
               {
                  if (e.keyCode == 8 && this.newTagInput.value.length == 0)
                  {
                     if (this._editingTagIndex != -1)
                     {
                        // If a tag is being edited then we just need to remove the tag and reset the input field
                        this.tagRefs.splice(this._editingTagIndex, 1); // Remove the nodeRef, the tag span has already been removed
                        YUIDom.insertAfter(this.newTagInput.parentNode, this.currentTags); // Return the auto-complete elements to their correct position
                     }
                     else if (!this._tagPrimedForDelete && this.currentTags.children.length > 0)
                     {
                        this._tagPrimedForDelete = true;
                        var lastTag = YUIDom.getLastChild(this.currentTags);
                        YUIDom.addClass(lastTag, "inlineTagEditTagPrimed");
                        YUIDom.addClass(lastTag.children[1], "hidden");
                        YUIDom.removeClass(lastTag.children[2], "hidden");
                     }
                     else
                     {
                        // The backspace key was used when there are no more characters to delete
                        // so we need to delete the last tag...
                        if (this.tagRefs.length > 0)
                        {
                           this.tagRefs.pop();
                           YUIDom.setAttribute(this.hiddenInput, "value", this.tagRefs.toString());
                           this.currentTags.removeChild(YUIDom.getLastChild(this.currentTags));
                        }
                        this._tagPrimedForDelete = false; // If we've deleted a tag then we're no longer primed for deletion...
                     }
                  }
                  else if (this._tagPrimedForDelete == true)
                  {
                     // If any key other than backspace is pressed and the last tag has been primed for deletion
                     // then we should put it back to the normal state...
                     this._tagPrimedForDelete = false;
                     if (this.currentTags.children.length > 0)
                     {
                        var lastTag = YUIDom.getLastChild(this.currentTags);
                        YUIDom.removeClass(lastTag, "inlineTagEditTagPrimed");
                        YUIDom.addClass(lastTag.children[2], "hidden");
                        YUIDom.removeClass(lastTag.children[1], "hidden");
                     }
                  }
               }, this, true);

               Event.addListener(editBox, "click", function(e)
               {
                  this.newTagInput.select();
               }, this, true);

               Event.addListener(this.newTagInput, "blur", function(e)
               {
                  if (this.balloon)
                  {
                     this.suppressInputBoxFocus = true;
                     this.balloon.hide();
                  }
                  this.suppressInputBoxFocus = false;
               }, this, true);

               eSave.on("click", function(e)
               {
                  // Check to see if any characters need to be converted into a tag...
                  if (this.newTagInput.value.length > 0)
                  {
                     this._createTag(this.newTagInput.value, true, e);
                  }
                  else
                  {
                     this.form._submitInvoked(e);
                  }

               }, this, true);

               eCancel.on("click", function(e)
               {
                  Event.stopEvent(e);
                  this.inputBox.value = "";
                  this.doHide(true);
               }, this, true);

               this.inputBox = this.newTagInput;

               // Key Listener for [Escape] to cancel
               this.keyListener = new KeyListener(this.inputBox,
                     {
                        keys: [KeyListener.KEY.ESCAPE]
                     },
                     {
                        fn: function(id, keyEvent)
                        {
                           Event.stopEvent(keyEvent[1]);
                           this.inputBox.value = "";
                           this.doHide(true);
                        },
                        scope: this,
                        correctScope: true
                     });

               // Balloon UI for errors
               this.balloon = Alfresco.util.createBalloon(editBox);
               this.balloon.onClose.subscribe(function(e)
               {
                  try
                  {
                     if (!this.suppressInputBoxFocus)
                     {
                        this.inputBox.focus();
                     }
                  }
                  catch (e)
                  {
                  }
               }, this, true);

               // Register validation handlers
               var vals = this.params.validations;
               for (var i = 0, ii = vals.length; i < ii; i++)
               {
                  this.form.addValidation(this.inputBox, vals[i].type, vals[i].args, vals[i].when, vals[i].message);
               }

               // Initialise the form
               this.form.init();
               this.markupGenerated = true;
            },

            /**
             * Creates and applies a tag. If the tag already exists then it will just be re-used.
             *
             * @method _createTag
             * @param value The value of the tag to create/apply
             * @param submitOnSuccess Whether or not to submit the form on a success callback
             * @param eventToStop An event to stop if necessary (this will be completed by the form submit action)
             */
            _createTag: function InsituEditor_tagEditor__createTag(value, submitOnSuccess, eventToStop)
            {
               if (eventToStop)
               {
                  Event.stopEvent(eventToStop);
               }
               var dataObj = { name : value },
                     successCallback =
                     {
                        fn: function(response)
                        {
                           // The tag was successfully created, add it before the new tag entry field
                           // and reset the entry field...
                           this._applyTag(value, response.json.nodeRef);
                           if (YUIDom.isAncestor(this.currentTags, this))
                           {
                              // We must have just finished editing a tag, therefore we need to move
                              // the auto-complete box out of the current tags...
                              YUIDom.insertAfter(this.parentNode, this.currentTags);
                           }

                           if (submitOnSuccess)
                           {
                              this.form._submitInvoked(eventToStop);
                           }

                        },
                        scope: this
                     },
                     failureCallback =
                     {
                        fn: function(response)
                        {
                           // The tag was not created for some reason. There's no need to
                           // do any additional action because the validation balloon will
                           // still be shown.
                        },
                        scope: this
                     };

               // Post a request to create a new tag. This will succeed even if the tag already
               // exists, it will just give us a handy reference to the nodeRef for the tag
               Alfresco.util.Ajax.jsonRequest(
                     {
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "api/tag/workspace/SpacesStore",
                        dataObj: dataObj,
                        successCallback: successCallback,
                        failureCallback: failureCallback
                     });
            }
         });
})();

/**
 * Find an event target's class name, ignoring YUI classes.
 *
 * @method Alfresco.util.findEventClass
 * @param p_eventTarget {object} Event target from Event class
 * @param p_tagName {string} Optional tag if 'span' needs to be overridden
 * @return {string|null} Class name or null
 * @static
 */
Alfresco.util.findEventClass = function(p_eventTarget, p_tagName)
{
   if (!p_eventTarget)
      return null;

   var src = p_eventTarget.element;
   var tagName = (p_tagName || "span").toLowerCase();

   // Walk down until specified tag found and not a yui class
   while ((src !== null) && ((src.tagName.toLowerCase() != tagName) || (src.className.indexOf("yui") === 0)))
   {
      src = src.firstChild;
   }

   // Found the target element?
   if (src === null)
   {
      return null;
   }

   return src.className;
};

/**
 * Determines whether a Bubbling event should be ignored or not
 *
 * @method Alfresco.util.hasEventInterest
 * @param p_instance {object} Instance checking for event interest
 * @param p_args {object} Bubbling event args
 * @return {boolean} false to ignore event
 */
Alfresco.util.hasEventInterest = function(p_eventGroup, p_args)
{
   var obj = p_args[1],
         sourceGroup = "source",
         targetGroup = "target",
         hasInterest = false;

   if (obj)
   {
      // Was this a defaultAction event?
      if (obj.action === "navigate")
      {
         obj.eventGroup = obj.anchor.rel;
      }

      if (obj.eventGroup && p_eventGroup)
      {
         sourceGroup = (typeof obj.eventGroup == "string") ? obj.eventGroup : obj.eventGroup.eventGroup;
         targetGroup = (typeof p_eventGroup == "string") ? p_eventGroup : p_eventGroup.eventGroup;

         hasInterest = (sourceGroup == targetGroup);
      }
   }
   return hasInterest;
};

/**
 * Check if flash is installed.
 * Returns true if a flash player of the required version is installed
 *
 * @method Alfresco.util.isFlashInstalled
 * @param reqMajorVer {int}
 * @param reqMinorVer {int}
 * @param reqRevision {int}
 * @return {boolean} Returns true if a flash player of the required version is installed
 * @static
 */
Alfresco.util.hasRequiredFlashPlayer = function(reqMajorVer, reqMinorVer, reqRevision)
{
   if (typeof DetectFlashVer == "function")
   {
      return DetectFlashVer(reqMajorVer, reqMinorVer, reqRevision);
   }
   return false;
};

/**
 * Add a component's messages to the central message store.
 *
 * @method Alfresco.util.addMessages
 * @param p_obj {object} Object literal containing messages in the correct locale
 * @param p_messageScope {string} Message scope to add these to, e.g. componentId
 * @return {boolean} true if messages added
 * @throws {Error}
 * @static
 */
Alfresco.util.addMessages = function(p_obj, p_messageScope)
{
   if (p_messageScope === undefined)
   {
      throw new Error("messageScope must be defined");
   }
   else if (p_messageScope == "global")
   {
      throw new Error("messageScope cannot be 'global'");
   }
   else
   {
      Alfresco.messages.scope[p_messageScope] = YAHOO.lang.merge(Alfresco.messages.scope[p_messageScope] || {}, p_obj);
      return true;
   }
   // for completeness...
   return false;
};

/**
 * Copy one namespace's messages to another's.
 *
 * @method Alfresco.util.copyMessages
 * @param p_source {string} Source namespace
 * @param p_destination {string} Destination namespace. Will be overwritten with source's messages
 * @throws {Error}
 * @static
 */
Alfresco.util.copyMessages = function(p_source, p_destination)
{
   if (p_source === undefined)
   {
      throw new Error("Source must be defined");
   }
   else if (Alfresco.messages.scope[p_source] === undefined)
   {
      throw new Error("Source namespace doesn't exist");
   }
   else if (p_destination === undefined)
   {
      throw new Error("Destination must be defined");
   }
   else if (p_destination == "global")
   {
      throw new Error("Destination cannot be 'global'");
   }
   else
   {
      Alfresco.messages.scope[p_destination] = YAHOO.lang.merge({}, Alfresco.messages.scope[p_source]);
   }
};

/**
 * Resolve a messageId into a message.
 * If a messageScope is supplied, that container will be searched first, followed by the "global" message scope.
 * Note: Implementation follows single-quote quirks of server implementations whereby I18N messages containing
 *       one or more tokens must use two single-quotes in order to display one.
 *       See: http://download.oracle.com/javase/1.5.0/docs/api/java/text/MessageFormat.html
 *
 * @method Alfresco.util.message
 * @param p_messageId {string} Message id to resolve
 * @param p_messageScope {string} Message scope, e.g. componentId
 * @param multiple-values {string} Values to replace tokens with
 * @return {string} The localized message string or the messageId if not found
 * @throws {Error}
 * @static
 */
Alfresco.util.message = function(p_messageId, p_messageScope)
{
   var msg = p_messageId;

   if (typeof p_messageId != "string")
   {
      throw new Error("Missing or invalid argument: messageId");
   }

   var globalMsg = Alfresco.messages.global[p_messageId];
   if (typeof globalMsg == "string")
   {
      msg = globalMsg;
   }

   if ((typeof p_messageScope == "string") && (typeof Alfresco.messages.scope[p_messageScope] == "object"))
   {
      var scopeMsg = Alfresco.messages.scope[p_messageScope][p_messageId];
      if (typeof scopeMsg == "string")
      {
         msg = scopeMsg;
      }
   }

   // Search/replace tokens
   var tokens = [];
   if ((arguments.length == 3) && (typeof arguments[2] == "object"))
   {
      tokens = arguments[2];
   }
   else
   {
      tokens = Array.prototype.slice.call(arguments).slice(2);
   }

   // Emulate server-side I18NUtils implementation
   if (YAHOO.lang.isArray(tokens) && tokens.length > 0)
   {
      msg = msg.replace(/''/g, "'");
   }
   msg = YAHOO.lang.substitute(msg, tokens);

   return msg;
};

/**
 * Helper method to set the required i18n properties on a YUI Calendar Widget
 * see: http://developer.yahoo.com/yui/docs/YAHOO.widget.Calendar.html#config_MY_LABEL_MONTH_POSITION
 * for what each property does
 *
 * @method Alfresco.util.calI18nParams
 * @param oCal {object} a YAHOO.widget.Calendar object
 * @static
 */
Alfresco.util.calI18nParams = function(oCal)
{
   var $msg = Alfresco.util.message;

   oCal.cfg.setProperty("MONTHS_SHORT", $msg("months.short").split(","));
   oCal.cfg.setProperty("MONTHS_LONG", $msg("months.long").split(","));
   oCal.cfg.setProperty("WEEKDAYS_1CHAR", $msg("days.initial").split(","));
   oCal.cfg.setProperty("WEEKDAYS_SHORT", $msg("days.short").split(","));
   oCal.cfg.setProperty("WEEKDAYS_MEDIUM", $msg("days.medium").split(","));
   oCal.cfg.setProperty("WEEKDAYS_LONG", $msg("days.long").split(","));
   oCal.cfg.setProperty("START_WEEKDAY", $msg("calendar.widget_config.start_weekday"))

   var monthPos = $msg("calendar.widget_config.my_label_month_position");
   if (monthPos.length !== 0)
   {
      oCal.cfg.setProperty("MY_LABEL_MONTH_POSITION", parseInt(monthPos));
   }

   var monthSuffix = $msg("calendar.widget_config.my_label_month_suffix");
   if (monthSuffix.length !== 0)
   {
      oCal.cfg.setProperty("MY_LABEL_MONTH_SUFFIX", monthSuffix);
   }

   var yearPos = $msg("calendar.widget_config.my_label_year_position");
   if (yearPos.length !== 0)
   {
      oCal.cfg.setProperty("MY_LABEL_YEAR_POSITION", parseInt(yearPos));
   }

   oCal.cfg.setProperty("MY_LABEL_YEAR_SUFFIX", $msg("calendar.widget_config.my_label_year_suffix"));
};

/**
 * Fixes the hidden caret problem in Firefox 2.x.
 * Assumes <input> or <textarea> elements are wrapped in a <div class="yui-u"></div>
 *
 * @method Alfresco.util.caretFix
 * @param p_formElement {element|string} Form element to fix input boxes within
 * @static
 */
Alfresco.util.caretFix = function(p_formElement)
{
   if (YAHOO.env.ua.gecko === 1.8)
   {
      if (typeof p_formElement == "string")
      {
         p_formElement = YUIDom.get(p_formElement);
      }
      var nodes = YUISelector.query(".yui-u", p_formElement);
      for (var x = 0; x < nodes.length; x++)
      {
         var elem = nodes[x];
         YUIDom.addClass(elem, "caret-fix");
      }
   }
};

/**
 * Remove the fixes for the hidden caret problem in Firefox 2.x.
 * Should be called before hiding a form for re-use.
 *
 * @method Alfresco.util.undoCaretFix
 * @param p_formElement {element|string} Form element to undo fixes within
 * @static
 */
Alfresco.util.undoCaretFix = function(p_formElement)
{
   if (YAHOO.env.ua.gecko === 1.8)
   {
      if (typeof p_formElement == "string")
      {
         p_formElement = YUIDom.get(p_formElement);
      }
      var nodes = YUISelector.query(".caret-fix", p_formElement);
      for (var x = 0; x < nodes.length; x++)
      {
         var elem = nodes[x];
         YUIDom.removeClass(elem, "caret-fix");
      }
   }
};

/**
 * Submits a form programatically, handling the
 * various browser nuances.
 *
 * @method Alfresco.util.submitForm
 * @param form The form to submit
 * @static
 */
Alfresco.util.submitForm = function(form)
{
   var UA = YAHOO.env.ua;
   var submitTheForm = false;

   if (form !== null)
   {
      if (UA.ie && (UA.ie < 9))
      {
         // IE up to v9
         submitTheForm = form.fireEvent("onsubmit");
      }
      else
      {
         // Gecko, Opera, and Safari
         var event = document.createEvent("HTMLEvents");
         event.initEvent("submit", true, true);
         submitTheForm = form.dispatchEvent(event);
      }

      if ((UA.ie || UA.webkit) && submitTheForm)
      {
         // for IE and webkit firing the event doesn't submit
         // the form so we have to do it manually (if the 
         // submission was not cancelled)
         form.submit();
      }
   }
};

/**
 * Parses a string to a json object and returns it.
 * If str contains invalid json code that is displayed using displayPrompt().
 *
 * @method Alfresco.util.parseJSON
 * @param jsonStr {string} The JSON string to be parsed
 * @param displayError {boolean} Set true to display a message informing about bad JSON syntax
 * @return {object} The object representing the JSON string
 * @static
 */
Alfresco.util.parseJSON = function(jsonStr, displayError)
{
   try
   {
      return YAHOO.lang.JSON.parse(jsonStr);
   }
   catch (error)
   {
      if (displayError)
      {
         Alfresco.util.PopupManager.displayPrompt(
               {
                  title: "Failure",
                  text: "Can't parse response as json: '" + jsonStr + "'"
               });
      }
   }
   return null;
};

/**
 * Returns a populated URI template, given a TemplateId and an object literal
 * containing the tokens to be substituted.
 * Understands when the application is hosted in a Portal environment.
 *
 * @method Alfresco.util.uriTemplate
 * @param templateId {string} URI TemplateId from web-framework configuration
 * @param obj {object} The object literal containing the token values to substitute
 * @param absolute {boolean} Whether the URL should include the protocol and host
 * @return {string|null} The populated URI or null if templateId not found
 * @static
 */
Alfresco.util.uriTemplate = function(templateId, obj, absolute)
{
   // Check we know about the templateId
   if (!(templateId in Alfresco.constants.URI_TEMPLATES))
   {
      return null;
   }

   return Alfresco.util.renderUriTemplate(Alfresco.constants.URI_TEMPLATES[templateId], obj, absolute);
};

/**
 * Returns a populated URI template, given the URI template and an object literal
 * containing the tokens to be substituted.
 * Understands when the application is hosted in a Portal environment.
 *
 * @method Alfresco.util.renderUriTemplate
 * @param template {string} URI Template to be populated
 * @param obj {object} The object literal containing the token values to substitute
 * @param absolute {boolean} Whether the URL should include the protocol and host
 * @return {string|null} The populated URI or null if templateId not found
 * @static
 */
Alfresco.util.renderUriTemplate = function(template, obj, absolute)
{
   // If a site page was requested but no {siteid} given, then use the current site or remove the missing parameter
   if (template.indexOf("{site}") !== -1)
   {
      if (obj.hasOwnProperty("site"))
      {
         // A site parameter was given - is it valid?
         if (!Alfresco.util.isValueSet(obj.site) && (Alfresco.constants.PAGECONTEXT.length == 0))
         {
            // Not valid - remove site part of template
            template = template.replace("/site/{site}", "");
         }
         else if (Alfresco.constants.PAGECONTEXT.length > 0)
         {
            template = template.replace("/site/{site}", "/context/" + Alfresco.constants.PAGECONTEXT);
         }
      }
      else
      {
         if (Alfresco.constants.SITE.length > 0)
         {
            // We're currently in a Site, so generate an in-Site link
            obj.site = Alfresco.constants.SITE;
         }
         else if (Alfresco.constants.PAGECONTEXT.length > 0)
         {
            template = template.replace("/site/{site}", "/context/" + Alfresco.constants.PAGECONTEXT);
         }
         else
         {
            // No current Site context, so remove from the template
            template = template.replace("/site/{site}", "");
         }
      }
   }

   var uri = template,
         regExp = /^(http|https):\/\//;

   /**
    * NOTE: YAHOO.lang.substitute is currently somewhat broken in YUI 2.9.0
    * Specifically, strings are no longer recursively substituted, even with the new "recurse"
    * flag set to "true". See http://yuilibrary.com/projects/yui2/ticket/2529100
    */
   while (uri !== (uri = YAHOO.lang.substitute(uri, obj))){}

   if (!regExp.test(uri))
   {
      // Page context required
      uri = Alfresco.util.combinePaths(Alfresco.constants.URL_PAGECONTEXT, uri);
   }

   // Portlet scriptUrl mapping required?
   if (Alfresco.constants.PORTLET)
   {
      // Remove the context prefix
      if (uri.indexOf(Alfresco.constants.URL_CONTEXT) === 0)
      {
         uri = Alfresco.util.combinePaths("/", uri.substring(Alfresco.constants.URL_CONTEXT.length));
      }

      uri = Alfresco.constants.PORTLET_URL.replace("$$scriptUrl$$", encodeURIComponent(decodeURIComponent(uri.replace(/%25/g, "%2525").replace(/%26/g, "%252526"))));
   }

   // Absolute URI needs current protocol and host
   if (absolute && (uri.indexOf(location.protocol + "//") !== 0))
   {
      // Don't use combinePaths in case the PORTLET_URL encoding is fragile
      if (uri.substring(0, 1) !== "/")
      {
         uri = "/" + uri;
      }
      uri = location.protocol + "//" + location.host + uri;
   }

   return uri;
};

/**
 * Returns a URL to a site page.
 * If no Site ID is supplied, generates a link to the non-site page.
 *
 * @method Alfresco.util.siteURL
 * @param pageURI {string} Page ID and and QueryString parameters the page might need, e.g.
 * <pre>
 *    "folder-details?nodeRef=" + nodeRef
 * </pre>
 * @param obj {object} The object literal containing the token values to substitute within the template
 * @param absolute {boolean} Whether the URL should include the protocol and host
 * @return {string} The populated URL
 * @static
 */
Alfresco.util.siteURL = function(pageURI, obj, absolute)
{
   return Alfresco.util.uriTemplate("sitepage", YAHOO.lang.merge(obj || {},
         {
            pageid: pageURI
         }), absolute);
};

/**
 * Returns a URL to a user profile page.
 *
 * @method Alfresco.util.profileURL
 * @param userName {string} User ID
 * @return {string} The populated URL
 * @static
 */
Alfresco.util.profileURL = function(userName)
{
   return Alfresco.util.uriTemplate("userprofilepage",
         {
            userid: userName
         });
};

/**
 * Parses a URL string into an object
 *
 * @param URL {string} The URL to convert
 * @return urlObject {object} object literal containing the URL properties.
 */
Alfresco.util.parseURL = function(url)
{
   var a = document.createElement("a"),
         _sanitizedPathname = function(pathname)
         {
            // pathname MUST include leading slash (IE<9: this code is for you).
            var prepend = (pathname.substring(0,1) === "/")? "" : "/";
            return prepend + pathname;
         };

   a.href = url;

   var urlObject = {
      // protocol includes trailing colon.
      protocol: a.protocol,
      hostname: a.hostname,
      port: a.port,
      // host = hostname:port
      host: a.host,
      pathname: _sanitizedPathname(a.pathname),
      // search and hash include question mark and hash symbol respectively
      search: a.search,
      hash: a.hash,
      queryParams: Alfresco.util.getQueryStringParameters(url),
      getUrl: function()
      {
         return this.getDomain() + this.pathname + Alfresco.util.toQueryString(this.queryParams) + this.hash;
      },
      getDomain: function()
      {
         return this.protocol + "//" + this.host;
      }
   }
   return urlObject;
};

/**
 * Navigates to a url
 *
 * @method Alfresco.util.navigateTo
 * @param uri {string} THe uri to navigate to
 * @param method {string} (Optional) Default is "GET"
 * @param parameters {string|object}
 * @static
 */
Alfresco.util.navigateTo = function(uri, method, parameters)
{
   method = method ? method.toUpperCase() : "GET";
   if (method == "GET")
   {
      window.location.href = uri;
   }
   else
   {
      var form = document.createElement("form");
      form.method = method;
      form.action = uri;
      if (method == "POST")
      {
         var input;
         for (var name in parameters)
         {
            if (parameters.hasOwnProperty(name))
            {
               value = parameters[name];
               if (value)
               {
                  input = document.createElement("input");
                  input.setAttribute("name", name);
                  input.setAttribute("type", "hidden");
                  input.value = value;
                  form.appendChild(input);
               }
            }
         }
      }
      document.body.appendChild(form);
      form.submit();
   }
};

/**
 * Generates a link to a site dashboard page
 *
 * @method Alfresco.util.siteDashboardLink
 * @param siteId {string} Site short name
 * @param siteTitle {string} Site display name. "siteId" used if this param is empty or not supplied
 * @param linkAttr {string} Optional attributes to add to the <a> tag, e.g. "class"
 * @param disableLink {boolean} Optional attribute instructing the link to be disabled
 *                             (ie returning a span element rather than an a href element)
 * @return {string} The populated HTML Link
 * @static
 */
Alfresco.util.siteDashboardLink = function(siteId, siteTitle, linkAttr, disableLink)
{
   if (!YAHOO.lang.isString(siteId) || siteId.length === 0)
   {
      return "";
   }

   var html = Alfresco.util.encodeHTML(YAHOO.lang.isString(siteTitle) && siteTitle.length > 0 ? siteTitle : siteId),
         template = Alfresco.constants.URI_TEMPLATES["sitedashboardpage"],
         uri = "";

   // If the "sitedashboardpage" template doesn't exist or is empty, or we're in portlet mode we'll just return the site's title || id
   if (disableLink || YAHOO.lang.isUndefined(template) || template.length === 0 || Alfresco.constants.PORTLET)
   {
      return '<span>' + html + '</span>';
   }

   // Generate the link
   uri = Alfresco.util.uriTemplate("sitedashboardpage",
         {
            site: siteId
         });

   return '<a href="' + uri + '" ' + (linkAttr || "") + '>' + html + '</a>';
};

/**
 * Generates a link to the user profile page unless the "userprofilepage" uritemplate has
 * been removed or emptied in share-config-custom.xml
 * If no fullName is supplied, the userName is displayed instead.
 *
 * @method Alfresco.util.userProfileLink
 * @param userName {string} User Name
 * @param fullName {string} Full display name. "userName" used if this param is empty or not supplied
 * @param linkAttr {string} Optional attributes to add to the <a> tag, e.g. "class"
 * @param disableLink {boolean} Optional attribute instructing the link to be disabled
 *                             (ie returning a span element rather than an a href element)
 * @return {string} The populated HTML Link
 * @static
 */
Alfresco.util.userProfileLink = function(userName, fullName, linkAttr, disableLink)
{
   if (!YAHOO.lang.isString(userName) || userName.length === 0)
   {
      return "";
   }

   var html = Alfresco.util.encodeHTML(YAHOO.lang.isString(fullName) && fullName.length > 0 ? fullName : userName),
         template = Alfresco.constants.URI_TEMPLATES["userprofilepage"],
         uri = "";

   // If the "userprofilepage" template doesn't exist or is empty, or we're in portlet mode we'll just return the user's fullName || userName
   if (disableLink || YAHOO.lang.isUndefined(template) || template.length === 0 || Alfresco.constants.PORTLET)
   {
      return '<span>' + html + '</span>';
   }

   // Generate the link
   uri = Alfresco.util.uriTemplate("userprofilepage",
         {
            userid: userName
         });

   return '<a href="' + uri + '" ' + (linkAttr || "") + '>' + html + '</a>';
};

/**
 * Returns a URL to the content represented by the passed-in nodeRef
 *
 * @method Alfresco.util.contentURL
 * @param nodeRef {string} Standard Alfresco nodeRef
 * @param name {string} Filename to download
 * @param attach {boolean} If true, browser should prompt the user to "Open or Save?", rather than display inline
 * @return {string} The URL to the content
 * @static
 */
Alfresco.util.contentURL = function(nodeRef, name, attach)
{
   return Alfresco.constants.PROXY_URI + "api/node/content/" + nodeRef.replace(":/", "") + "/" + name + (attach ? "?a=true" : "");
};

/**
 * Returns the value of the specified query string parameter.
 *
 * @method getQueryStringParameter
 * @param {string} paramName Name of the parameter we want to look up.
 * @param {string} queryString Optional URL to look at. If not specified,
 *     this method uses the URL in the address bar.
 * @return {string} The value of the specified parameter, or null.
 * @static
 */
Alfresco.util.getQueryStringParameter = function(paramName, url)
{
   var params = this.getQueryStringParameters(url);

   if (paramName in params)
   {
      return params[paramName];
   }

   return null;
};

/**
 * Returns the query string parameters as an object literal.
 * Parameters appearing more than once are returned an an array.
 * This method has been extracted from the YUI Browser History Manager.
 * It can be used here without the overhead of the History JavaScript include.
 *
 * @method getQueryStringParameters
 * @param queryString {string} Optional URL to look at. If not specified,
 *     this method uses the URL in the address bar.
 * @return {object} Object literal containing QueryString parameters as name/value pairs
 * @static
 */
Alfresco.util.getQueryStringParameters = function(url)
{
   var i, len, idx, queryString, params, tokens, name, value, objParams;

   url = url || window.location.href;

   idx = url.indexOf("?");
   queryString = idx >= 0 ? url.substr(idx + 1) : url;

   // Remove the hash if any
   idx = queryString.lastIndexOf("#");
   queryString = idx >= 0 ? queryString.substr(0, idx) : queryString;

   params = queryString.split("&");

   objParams = {};

   for (i = 0, len = params.length; i < len; i++)
   {
      tokens = params[i].split("=");
      if (tokens.length >= 2)
      {
         name = tokens[0];
         value = decodeURIComponent(tokens[1]);
         switch (typeof objParams[name])
         {
            case "undefined":
               objParams[name] = value;
               break;

            case "string":
               objParams[name] = [objParams[name]].concat(value);
               break;

            case "object":
               objParams[name] = objParams[name].concat(value);
               break;
         }
      }
   }

   return objParams;
};

/**
 * Turns an object literal into a valid queryString.
 * Format of the object is as returned from the getQueryStringParameters() function.
 *
 * @method toQueryString
 * @param params {object} Object literal containing QueryString parameters as name/value pairs
 * @return {string} QueryString-formatted string
 * @static
 */
Alfresco.util.toQueryString = function(p_params)
{
   var qs = "?", name, value, val;
   for (name in p_params)
   {
      if (p_params.hasOwnProperty(name))
      {
         value = p_params[name];
         if (typeof value == "object")
         {
            for (val in value)
            {
               if (value.hasOwnProperty(val))
               {
                  qs += encodeURIComponent(name) + "=" + encodeURIComponent(value[val]) + "&";
               }
            }
         }
         else if (typeof value == "string")
         {
            qs += encodeURIComponent(name) + "=" + encodeURIComponent(value) + "&";
         }
      }
   }

   // Return the string after removing the last character
   return qs.substring(0, qs.length - 1);
};

/**
 * Retrieves a JavaScript session variable.
 * Variables are scoped to the current "location.host"
 *
 * @method getVar
 * @param name {string} Variable name
 * @param default {object} Default value to return if not set
 * @return {object|null} Variable value or default if provided (null otherwise)
 * @static
 */
Alfresco.util.getVar = function(p_name, p_default)
{
   var returnValue = typeof p_default != "undefined" ? p_default : null;

   try
   {
      if (window.name !== "" && YAHOO.lang.JSON.isValid(window.name))
      {
         var allVars = YAHOO.lang.JSON.parse(window.name),
               scopedVars = allVars[location.host],
               value = null;

         if (typeof scopedVars == "object")
         {
            value = scopedVars[p_name];
            if (typeof value !== "undefined" && value !== null)
            {
               returnValue = value;
            }
         }
      }
   }
   catch (e)
   {
      Alfresco.logger.error("Alfresco.util.getVar()", p_name, p_default, e);
   }

   return returnValue;
};

/**
 * Sets a JavaScript session variable.
 * The variables are stored in window.name, so live for as long as the browser window does.
 * Variables are scoped to the current "location.host"
 *
 * @method setVar
 * @param name {string} Variable name
 * @param value {object} Value to set
 * @return {boolean} True for successful set
 * @static
 */
Alfresco.util.setVar = function(p_name, p_value)
{
   var success = true;

   try
   {
      var allVars = {};

      if (window.name !== "" && YAHOO.lang.JSON.isValid(window.name))
      {
         allVars = YAHOO.lang.JSON.parse(window.name);
      }

      if (typeof allVars[location.host] == "undefined")
      {
         allVars[location.host] = {};
      }
      allVars[location.host][p_name] = p_value;

      window.name = YAHOO.lang.JSON.stringify(allVars);
   }
   catch (e)
   {
      Alfresco.logger.error("Alfresco.util.setVar()", p_name, p_value, e);
      success = false;
   }
   return success;
};


/**
 * Takes a string and splits it up to valid tags by using whitespace as separators.
 * Multi-word tags are supported by "quoting the phrase".
 * e.g. the string
 * <pre>hello*world "this is alfresco"</pre>
 * would result in  tags: "hello*world" and "this is alfresco".
 *
 * @method getTags
 * @param str {string} a string containing tags
 * @return {array} of valid tags
 * @static
 */
Alfresco.util.getTags = function(str)
{
   var match = null,
         tags = [],
         found = {},
         regexp = new RegExp(/([^"^\s]+)\s*|"([^"]+)"\s*/g),
         tag;

   while (match = regexp.exec(str))
   {
      tag = match[1] || match[2];
      if (found[tag] === undefined)
      {
         found[tag] = true;
         tags.push(tag);
      }
   }
   return tags;
};

/**
 * The YUI Bubbling Library augments callback objects with its own built-in fields.
 * This function strips those out, so the remainder of the object is "clean"
 *
 * @method cleanBubblingObject
 * @param callbackObj {object} Object literal as passed to the event handler
 * @return {object} Object stripped of Bubbling Library fields
 * @static
 */
Alfresco.util.cleanBubblingObject = function(callbackObj)
{
   // See Bubbling Library, fire() function. These fields are correct for v2.1.
   var augmented =
         {
            action: true,
            flagged: true,
            decrepitate: true,
            stop: true
         },
         cleanObj = {};

   for (var index in callbackObj)
   {
      if (callbackObj.hasOwnProperty(index) && augmented[index] !== true)
      {
         cleanObj[index] = callbackObj[index];
      }
   }
   return cleanObj;
};

/**
 * Bind a function to a specific context.
 *
 * @method bind
 * @param fn {function} Function to be bound.
 * @param context {object} Context to bind function to.
 * @param arguments {object} Optional arguments to prepend to arguments passed-in when function is actually called
 * @return {function} Function wrapper.
 * @static
 */
Alfresco.util.bind = function(fn, context)
{
   if (!YAHOO.lang.isObject(context))
   {
      return fn;
   }
   var args = Array.prototype.slice.call(arguments).slice(2);
   return (function()
   {
      return fn.apply(context, args.concat(Array.prototype.slice.call(arguments)));
   });
};

/**
 * Autocomplete key filter
 * Whether or not key is functional or should be ignored. Note that the right
 * arrow key is NOT an ignored key since it triggers queries for certain intl
 * charsets.
 * From YAHOO.widget.AutoComplete.prototype._isIgnoreKey()
 *
 * @method isAutocompleteIgnoreKey
 * @param nKeycode {Number} Code of key pressed.
 * @return {Boolean} True if key should be ignored, false otherwise.
 */
Alfresco.util.isAutocompleteIgnoreKey = function(nKeyCode)
{
   if (
         (nKeyCode == 9) || (nKeyCode == 13) || // tab, enter
               (nKeyCode == 16) || (nKeyCode == 17) || // shift, ctl
               (nKeyCode >= 18 && nKeyCode <= 20) || // alt, pause/break,caps lock
               (nKeyCode == 27) || // esc
               (nKeyCode >= 33 && nKeyCode <= 35) || // page up,page down,end
               (nKeyCode >= 36 && nKeyCode <= 40) || // home,left,up, right, down
               (nKeyCode >= 44 && nKeyCode <= 45) || // print screen,insert
               (nKeyCode == 229) // Bug 2041973: Korean XP fires 2 keyup events, the key and 229
         )
   {
      return true;
   }
   return false;
};

/**
 * Helps checking the event's keyCode in all browsers (FF uses charCode)
 *
 * @param event {Object}
 * @param keyCode {int|Array} An int describing the keyCode or an array of keyCode integers
 */
Alfresco.util.hasKeyCode = function (event, keyCode)
{
   var code = event.charCode ? event.charCode : event.keyCode;
   if (YAHOO.lang.isArray(keyCode))
   {
      return Alfresco.util.arrayContains(keyCode, code);
   }
   return keyCode == code;
};


/**
 * Wrapper for helping components specify their YUI components.
 * @class Alfresco.util.YUILoaderHelper
 */
Alfresco.util.YUILoaderHelper = function()
{
   /**
    * The YUILoader single instance which will load all the dependencies
    * @property yuiLoader
    * @type YAHOO.util.YUILoader
    */
   var yuiLoader = null;

   /**
    * Array to store callbacks from all component registrants
    * @property callbacks
    * @type Array
    */
   var callbacks = [];

   /**
    * Flag to indicate whether the initial YUILoader has completed
    * @property initialLoaderComplete
    * @type boolean
    */
   var initialLoaderComplete = false;

   /**
    * Create YUILoader
    * @function createYUILoader
    * @private
    */
   var createYUILoader = function YUILoaderHelper_createYUILoader()
   {
      if (yuiLoader === null)
      {
         yuiLoader = new YAHOO.util.YUILoader(
               {
                  base: Alfresco.constants.URL_RESCONTEXT + "yui/",
                  filter: Alfresco.constants.DEBUG ? "DEBUG" : {
                     'searchExp': "-min\\.js",
                     'replaceStr': "-min.js?v=" +YAHOO.VERSION
                  },
                  loadOptional: false,
                  skin: {},
                  onSuccess: Alfresco.util.YUILoaderHelper.onLoaderComplete,
                  onFailure: function(event)
                  {
                     alert("load failed:" + event);
                  },
                  scope: this
               });
         // Add Alfresco YUI components to YUI loader

         // SWFPlayer
         yuiLoader.addModule(
               {
                  name: "swfplayer",
                  type: "js",
                  path: "swfplayer/swfplayer.js", //can use a path instead, extending base path
                  varName: "SWFPlayer",
                  requires: ['uploader'] // The FlashAdapter class is located in uploader.js
               });

         // ColumnBrowser - js
         yuiLoader.addModule(
               {
                  name: "columnbrowser",
                  type: "js",
                  path: "columnbrowser/columnbrowser.js", //can use a path instead, extending base path
                  varName: "ColumnBrowser",
                  requires: ['json', 'carousel', 'paginator'],
                  skinnable: true
               });
      }
   };

   return (
   {
      /**
       * Main entrypoint for components wishing to load a YUI component
       * @method require
       * @param p_aComponents {Array} List of required YUI components. See YUILoader documentation for valid names
       * @param p_oCallback {function} Callback function invoked when all required YUI components have been loaded
       * @param p_oScope {object} Scope for callback function
       */
      require: function YLH_require(p_aComponents, p_oCallback, p_oScope)
      {
         createYUILoader();
         if (p_aComponents.length > 0)
         {
            /* Have all the YUI components the caller requires been registered? */
            var isRegistered = true;
            for (var i = 0; i < p_aComponents.length; i++)
            {
               if (YAHOO.env.getVersion(p_aComponents[i]) === null)
               {
                  isRegistered = false;
                  break;
               }
            }
            if (isRegistered && (p_oCallback !== null))
            {
               YAHOO.lang.later(10, p_oScope, p_oCallback);
            }
            else
            {
               /* Add to the list of components to be loaded */
               yuiLoader.require(p_aComponents);

               /* Store the callback function and scope for later */
               callbacks.push(
                     {
                        required: Alfresco.util.arrayToObject(p_aComponents),
                        fn: p_oCallback,
                        scope: (typeof p_oScope != "undefined" ? p_oScope : window)
                     });
            }
         }
         else if (p_oCallback !== null)
         {
            p_oCallback.call(typeof p_oScope != "undefined" ? p_oScope : window);
         }
      },

      /**
       * Called by template once all component dependencies have been registered. Should be just before the </body> closing tag.
       * @method loadComponents
       * @param p_pageLoad {Boolean} Whether the function is being called from the page footer, or elsewhere. Only the footer is allowed to use "true" here.
       */
      loadComponents: function YLH_loadComponents(p_pageLoad)
      {
         createYUILoader();
         if (initialLoaderComplete || p_pageLoad === true)
         {
            if (yuiLoader !== null)
            {
               yuiLoader.insert(null, "js");
            }
         }
      },

      /**
       * Callback from YUILoader once all required YUI componentshave been loaded by the browser.
       * @method onLoaderComplete
       */
      onLoaderComplete: function YLH_onLoaderComplete()
      {
         for (var i = 0; i < callbacks.length; i++)
         {
            if (callbacks[i].fn)
            {
               callbacks[i].fn.call(callbacks[i].scope);
            }
         }
         callbacks = [];
         initialLoaderComplete = true;
      }
   });
}();


/**
 * Keeps track of Alfresco components on a page. Components should register() upon creation to be compliant.
 * @class Alfresco.util.ComponentManager
 */
Alfresco.util.ComponentManager = function()
{
   /**
    * Array of registered components.
    *
    * @property components
    * @type Array
    */
   var components = [];

   return (
   {
      /**
       * Main entrypoint for components wishing to register themselves with the ComponentManager
       * @method register
       * @param p_aComponent {object} Component instance to be registered
       */
      register: function CM_register(p_oComponent)
      {
         if (p_oComponent.id !== "null" && components.hasOwnProperty(p_oComponent.id))
         {
            var purge = components[p_oComponent.id];
            if (purge.name === p_oComponent.name)
            {
               if (typeof purge.destroy  === "function")
               {
                  purge.destroy();
               }
               this.unregister(components[p_oComponent.id]);
            }
         }
         components.push(p_oComponent);
         components[p_oComponent.id] = p_oComponent;
      },

      /**
       * Unregister a component from the ComponentManager
       * @method unregister
       * @param p_aComponent {object} Component instance to be unregistered
       */
      unregister: function CM_unregister(p_oComponent)
      {
         for (var i = 0; i < components.length; i++) // Do not optimize
         {
            if (components[i] == p_oComponent)
            {
               components.splice(i, 1);
               delete components[p_oComponent.id];
               break;
            }
         }
      },

      /**
       * Re-register a component with the ComponentManager
       * Component ID cannot be updated via this function, use separate unregister(), register() calls instead.
       * @method reregister
       * @param p_aComponent {object} Component instance to be unregistered, then registered again
       */
      reregister: function CM_reregister(p_oComponent)
      {
         this.unregister(p_oComponent);
         this.register(p_oComponent);
      },

      /**
       * Allows components to find other registered components by name, id or both
       * e.g. find({name: "Alfresco.DocumentLibrary"})
       * @method find
       * @param p_oParams {object} List of paramters to search by
       * @return {Array} Array of components found in the search
       */
      find: function CM_find(p_oParams)
      {
         var found = [];
         var bMatch, component;

         for (var i = 0, j = components.length; i < j; i++)
         {
            component = components[i];
            bMatch = true;
            for (var key in p_oParams)
            {
               if (p_oParams[key] != component[key])
               {
                  bMatch = false;
               }
            }
            if (bMatch)
            {
               found.push(component);
            }
         }
         return found;
      },

      /**
       * Allows components to find first registered components by name only
       * e.g. findFirst("Alfresco.DocumentLibrary")
       * @method findFirst
       * @param p_sName {string} Name of registered component to search on
       * @return {object|null} Component found in the search
       */
      findFirst: function CM_findFirst(p_sName)
      {
         var found = Alfresco.util.ComponentManager.find(
               {
                  name: p_sName
               });

         return (typeof found[0] == "object" ? found[0] : null);
      },

      /**
       * Get component by Id
       * e.g. get("global_x002e_header-sites-menu")
       * @method get
       * @param p_sId {string} Id of registered component to return
       * @return {object|null} Component with given Id
       */
      get: function CM_get(p_sId)
      {
         return (components[p_sId] || null);
      },

      /**
       * List all registered components
       *
       * @method list
       * @return {array} array of components
       */
      list: function CM_list()
      {
         return components;
      }
   });
}();

/**
 * Provides a common interface for displaying popups in various forms
 *
 * @class Alfresco.util.PopupManager
 */
Alfresco.util.PopupManager = function()
{
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   return (
   {

      /**
       * The html zIndex startvalue that will be incremented for each popup
       * that is displayed to make sure the popup is visible to the user.
       *
       * @property zIndex
       * @type int
       */
      zIndex: 15,

      /**
       * The default config for the displaying messages, can be overriden
       * when calling displayMessage()
       *
       * @property defaultDisplayMessageConfig
       * @type object
       */
      defaultDisplayMessageConfig:
      {
         title: null,
         text: null,
         spanClass: "message",
         displayTime: 2.5,
         effect: YAHOO.widget.ContainerEffect.FADE,
         effectDuration: 0.5,
         visible: false,
         noEscape: false
      },

      /**
       * Intended usage: To quickly assure the user that the expected happened.
       *
       * Displays a message as a popup on the screen.
       * In default mode it fades, is visible for half a second and then fades out.
       *
       * @method displayMessage
       * @param config {object}
       * The config object is in the form of:
       * {
       *    text: {string},         // The message text to display, mandatory
       *    spanClass: {string},    // The class of the span wrapping the text
       *    effect: {YAHOO.widget.ContainerEffect}, // the effect to use when shpwing and hiding the message,
       *                                            // default is YAHOO.widget.ContainerEffect.FADE
       *    effectDuration: {int},  // time in seconds that the effect should be played, default is 0.5
       *    displayTime: {int},     // time in seconds that the message will be displayed, default 2.5
       *    modal: {true}           // if the message should modal (the background overlayed with a gray transparent layer), default is false
       * }
       * @param parent {HTMLElement} (optional) Parent element in which to render prompt. Defaults to document.body if not provided
       */
      displayMessage: function PopupManager_displayMessage(config, parent)
      {
         var parent = parent || document.body;
         // Merge the users config with the default config and check mandatory properties
         var c = YAHOO.lang.merge(this.defaultDisplayMessageConfig, config);
         if (c.text === undefined)
         {
            throw new Error("Property text in userConfig must be set");
         }
         var dialogConfig =
         {
            modal: false,
            visible: c.visible,
            close: false,
            draggable: false,
            effect:
            {
               effect: c.effect,
               duration: c.effectDuration
            },
            zIndex: (c.zIndex == undefined ? 0 : c.zIndex) + this.zIndex++
         };
         // IE browsers don't deserve fading, as they can't handle it properly
         if (c.effect === null || YAHOO.env.ua.ie > 0)
         {
            delete dialogConfig.effect;
         }
         // Construct the YUI Dialog that will display the message
         var message = new YAHOO.widget.Dialog("message", dialogConfig);

         // This method allows to stop animanions before destroy to avoid NPE
         message.destroyWithAnimationsStop = function()
         {
            if (message._fadingIn || message._fadingOut)
            {
               if(message._cachedEffects && message._cachedEffects.length > 0)
               {
                  for(var i = 0; i < message._cachedEffects.length; i++)
                  {
                     var effect = message._cachedEffects[i];
                     if (effect.animIn)
                        effect.animIn.stop();
                     if (effect.animOut)
                        effect.animOut.stop();
                  }
               }
            }
            message.destroy();
         }

         // Set the message that should be displayed
         var bd =  "<span class='" + c.spanClass + "'>" + (c.noEscape ? c.text : $html(c.text)) + "</span>";
         message.setBody(bd);

         /**
          * Add it to the dom, center it, schedule the fade out of the message
          * and show it.
          */
         message.render(parent);
         message.center();
         // Need to schedule a fade-out?
         if (c.displayTime > 0)
         {
            message.subscribe("show", this._delayPopupHide,
                  {
                     popup: message,
                     displayTime: (c.displayTime * 1000)
                  }, true);
         }
         message.show();

         return message;
      },

      /**
       * Gets called after the message has been displayed as long as it was
       * configured.
       * Hides the message from the user.
       *
       * @method _delayPopupHide
       */
      _delayPopupHide: function PopupManager__delayPopupHide()
      {
         YAHOO.lang.later(this.displayTime, this, function()
         {
            this.popup.destroy();
         });
      },

      /**
       * The default config for displaying "prompt" messages, can be overriden
       * when calling displayPrompt()
       *
       * @property defaultDisplayPromptConfig
       * @type object
       */
      defaultDisplayPromptConfig:
      {
         title: null,
         text: null,
         icon: null,
         close: false,
         constraintoviewport: true,
         draggable: true,
         effect: null,
         effectDuration: 0.5,
         modal: true,
         visible: false,
         noEscape: false,
         buttons: [
            {
               text: null, // Too early to localize at this time, do it when called instead
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }]
      },

      /**
       * Intended usage: To inform the user that something unexpected happened
       * OR that ask the user if if an action should be performed.
       *
       * Displays a message as a popup on the screen with a button to make sure
       * the user responds to the prompt.
       *
       * In default mode it shows with an OK button that needs clicking to get closed.
       *
       * @method displayPrompt
       * @param config {object}
       * The config object is in the form of:
       * {
       *    title: {string},       // the title of the dialog, default is null
       *    text: {string},        // the text to display for the user, mandatory
       *    icon: null,            // the icon to display next to the text, default is null
       *    effect: {YAHOO.widget.ContainerEffect}, // the effect to use when showing and hiding the prompt, default is null
       *    effectDuration: {int}, // the time in seconds that the effect should run, default is 0.5
       *    modal: {boolean},      // if a grey transparent overlay should be displayed in the background
       *    close: {boolean},      // if a close icon should be displayed in the right upper corner, default is false
       *    buttons: []            // an array of button configs as described by YUI's SimpleDialog, default is a single OK button
       *    noEscape: {boolean}    // indicates the the message has already been escaped (e.g. to display HTML-based messages)
       * }
       * @param parent {HTMLElement} (optional) Parent element in which to render prompt. Defaults to document.body if not provided
       */
      displayPrompt: function PopupManager_displayPrompt(config, parent)
      {
         var parent = parent || document.body;
         if (this.defaultDisplayPromptConfig.buttons[0].text === null)
         {
            /**
             * This default value could not be set at instantion time since the
             * localized messages weren't present at that time
             */
            this.defaultDisplayPromptConfig.buttons[0].text = Alfresco.util.message("button.ok", this.name);
         }
         // Merge users config and the default config and check manadatory properties
         var c = YAHOO.lang.merge(this.defaultDisplayPromptConfig, config);
         if (c.text === undefined)
         {
            throw new Error("Property text in userConfig must be set");
         }

         // Create the SimpleDialog that will display the text
         var prompt = new YAHOO.widget.SimpleDialog("prompt",
               {
                  close: c.close,
                  constraintoviewport: c.constraintoviewport,
                  draggable: c.draggable,
                  effect: c.effect,
                  modal: c.modal,
                  visible: c.visible,
                  zIndex: c.zIndex + this.zIndex++
               });

         // Show the title if it exists
         if (c.title)
         {
            prompt.setHeader($html(c.title));
         }

         // Show the prompt text
         prompt.setBody(c.noEscape ? c.text : $html(c.text));

         // Show the icon if it exists
         if (c.icon)
         {
            prompt.cfg.setProperty("icon", c.icon);
         }

         // Add the buttons to the dialog
         if (c.buttons)
         {
            prompt.cfg.queueProperty("buttons", c.buttons);
         }

         // Add the dialog to the dom, center it and show it.
         prompt.render(parent);
         prompt.center();

         // MNT-11084 Full screen/window view: Actions works incorrectly; 
         if (c.zIndex !== undefined && c.zIndex > 0)
         {
            var index = c.zIndex + this.zIndex;
            var onBeforeShow = function () 
            {
               element = Dom.get("prompt_mask");
               if (element !== undefined)
               {
                  Dom.setStyle(element, "zIndex", index - 1 );
               }

               Dom.setStyle(prompt.element, "zIndex", index);
               prompt.cfg.setProperty("zIndex", index, true);
            }
            prompt.beforeShowEvent.subscribe(onBeforeShow, prompt, true);
         }

         prompt.show();
      },

      /**
       * The default config for the getting user input, can be overriden
       * when calling getUserInput()
       *
       * @property defaultGetUserInputConfig
       * @type object
       */
      defaultGetUserInputConfig:
      {
         title: null,
         text: null,
         input: "textarea",
         value: "",
         icon: null,
         close: true,
         constraintoviewport: true,
         draggable: true,
         effect: null,
         effectDuration: 0.5,
         modal: true,
         visible: false,
         initialShow: true,
         noEscape: true,
         html: null,
         callback: null,
         buttons: [
            {
               text: null, // OK button. Too early to localize at this time, do it when called instead
               handler: null,
               isDefault: true
            },
            {
               text: null, // Cancel button. Too early to localize at this time, do it when called instead
               handler: function()
               {
                  this.destroy();
               }
            }]
      },

      /**
       * Intended usage: To ask the user for a simple text input, similar to JavaScript's prompt() function.
       *
       * @method getUserInput
       * @param config {object}
       * The config object is in the form of:
       * {
       *    title: {string},       // the title of the dialog, default is null
       *    text: {string},        // optional label next to input box
       *    value: {string},       // optional default value to populate textbox with
       *    callback: {object}     // Object literal specifying function callback to receive user input. Only called if default button config used.
       *                           // fn: function, obj: optional pass-thru object, scope: callback scope
       *    icon: null,            // the icon to display next to the text, default is null
       *    effect: {YAHOO.widget.ContainerEffect}, // the effect to use when showing and hiding the prompt, default is null
       *    effectDuration: {int}, // the time in seconds that the effect should run, default is 0.5
       *    modal: {boolean},      // if a grey transparent overlay should be displayed in the background
       *    initialShow {boolean}  // whether to call show() automatically on the panel
       *    close: {boolean},      // if a close icon should be displayed in the right upper corner, default is true
       *    buttons: []            // an array of button configs as described by YUI's SimpleDialog, default is a single OK button
       *    okButtonText: {string} // Allows just the label of the OK button to be overridden
       *    noEscape: {boolean}    // indicates the the text property has already been escaped (e.g. to display HTML-based messages)
       *    html: {string},        // optional override for function-generated HTML <input> field. Note however that you must supply your own
       *                           //    button handlers in this case in order to get the user's input from the Dom.
       * }
       * @return {YAHOO.widget.SimpleDialog} The dialog widget
       */
      getUserInput: function PopupManager_getUserInput(config)
      {
         if (this.defaultGetUserInputConfig.buttons[0].text === null)
         {
            /**
             * This default value could not be set at instantion time since the
             * localized messages weren't present at that time
             */
            this.defaultGetUserInputConfig.buttons[0].text = Alfresco.util.message("button.ok", this.name);
         }
         if (this.defaultGetUserInputConfig.buttons[1].text === null)
         {
            this.defaultGetUserInputConfig.buttons[1].text = Alfresco.util.message("button.cancel", this.name);
         }

         // Merge users config and the default config and check manadatory properties
         var c = YAHOO.lang.merge(this.defaultGetUserInputConfig, config);

         // Create the SimpleDialog that will display the text
         var prompt = new YAHOO.widget.SimpleDialog("userInput",
               {
                  close: c.close,
                  constraintoviewport: c.constraintoviewport,
                  draggable: c.draggable,
                  effect: c.effect,
                  modal: c.modal,
                  visible: c.visible,
                  zIndex: this.zIndex++
               });

         // Show the title if it exists
         if (c.title)
         {
            prompt.setHeader($html(c.title));
         }

         // Generate the HTML mark-up if not overridden
         var html = c.html,
               id = Alfresco.util.generateDomId();

         if (html === null)
         {
            html = "";
            if (c.text)
            {
               html += '<label for="' + id + '">' + (c.noEscape ? c.text : $html(c.text)) + '</label><br/>';
            }
            if (c.input == "textarea")
            {
               html += '<textarea id="' + id + '" tabindex="0">' + c.value + '</textarea>';
            }
            else if (c.input == "text")
            {
               html += '<input id="' + id + '" tabindex="0" type="text" value="' + c.value + '"/>';
            }
         }
         prompt.setBody(html);

         // Show the icon if it exists
         if (c.icon)
         {
            prompt.cfg.setProperty("icon", c.icon);
         }

         // Add the buttons to the dialog
         if (c.buttons)
         {
            if (c.okButtonText)
            {
               // Override OK button label
               c.buttons[0].text = c.okButtonText;
            }

            // Default handler if no custom button passed-in
            if (typeof config.buttons == "undefined" || typeof config.buttons[0] == "undefined")
            {
               // OK button click handler
               c.buttons[0].handler = {
                  fn: function(event, obj)
                  {
                     // Grab the input, destroy the pop-up, then callback with the value
                     var value = null;
                     if (YUIDom.get(obj.id))
                     {
                        var inputEl = YUIDom.get(obj.id);
                        value = YAHOO.lang.trim(inputEl.value || inputEl.text);
                     }
                     this.destroy();
                     if (obj.callback.fn)
                     {
                        obj.callback.fn.call(obj.callback.scope || window, value, obj.callback.obj);
                     }
                  },
                  obj:
                  {
                     id: id,
                     callback: c.callback
                  }
               };
            }
            prompt.cfg.queueProperty("buttons", c.buttons);
         }

         // Add the dialog to the dom, center it and show it (unless flagged not to).
         prompt.render(document.body);

         // Make sure ok button only is enabled  if textfield contains content
         if (prompt.getButtons().length > 0)
         {
            // Make sure button only is disabled if textinput has a proper value
            var okButton = prompt.getButtons()[0];
            YAHOO.util.Event.addListener(id, "keyup", function(event, okButton)
            {
               okButton.set("disabled", YAHOO.lang.trim(this.value || this.text || "").length == 0);
            }, okButton);
            okButton.set("disabled", YAHOO.lang.trim(c.value).length == 0)
         }

         // Center and display
         prompt.center();
         if (c.initialShow)
         {
            prompt.show();
         }

         // If a default value was given, set the selectionStart and selectionEnd properties
         if (c.value !== "")
         {
            YUIDom.get(id).selectionStart = 0;
            YUIDom.get(id).selectionEnd = c.value.length;
         }

         // Register the ESC key to close the panel
         var escapeListener = new YAHOO.util.KeyListener(document,
               {
                  keys: YAHOO.util.KeyListener.KEY.ESCAPE
               },
               {
                  fn: function(id, keyEvent)
                  {
                     this.destroy();
                  },
                  scope: prompt,
                  correctScope: true
               });
         escapeListener.enable();

         if (YUIDom.get(id))
         {
            YUIDom.get(id).focus();
         }

         return prompt;
      },

      /**
       * Displays a form inside a dialog
       *
       * @method displayForm
       * @param config
       * @param config.title {String} The dialog title
       * @param config.properties {Object} An object literal with the form properties
       * @param config.success {Object} A callback object literal used on form success
       * @param config.successMessage {String} A submit success message
       * @param config.failure {Object} A callback object literal used on form failure
       * @param config.failureMessage {String} A submit failure message
       */
      displayForm: function PopupManager_displayForm(config)
      {
         // Use the htmlid to make sure we respond to events from the correct form instance
         var htmlid = config.properties.htmlid || Alfresco.util.generateDomId();
         config.properties.htmlid = htmlid;

         // Display form webscript in a dialog
         var panel = this.displayWebscript(
               {
                  title: config.title,
                  url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                  properties: YAHOO.lang.merge(
                        {
                           submitType: "json",
                           showCaption: false,
                           formUI: true,
                           showCancelButton: true
                        }, config.properties)
               });

         // Adjust form to work in dialog
         YAHOO.Bubbling.on("formContentReady", function PopupManager_displayForm_onFormContentReady(layer, args)
               {
                  if (Alfresco.util.hasEventInterest(htmlid + "-form", args))
                  {
                     // Change the default 'Submit' label to be 'Ok'
                     var submitButton = args[1].buttons.submit;
                     submitButton.set("label", Alfresco.util.message("label.ok"));

                     // Close dialog when cancel button is clicked
                     var cancelButton = args[1].buttons.cancel;
                     if (cancelButton)
                     {
                        cancelButton.addListener("click", this.panel.destroy, this.panel, true);
                     }
                  }
               },
               {
                  panel: panel,
                  config: config
               });

         // When form is submitted make sure we hide the dialog after a successful submit and display a message when it fails.
         YAHOO.Bubbling.on("beforeFormRuntimeInit", function PopupManager_displayForm_onBeforeFormRuntimeInit(layer, args)
               {
                  if (Alfresco.util.hasEventInterest(htmlid + "-form", args))
                  {
                     args[1].runtime.setAJAXSubmit(true,
                           {
                              successCallback:
                              {
                                 fn: function PopupMananger_displayForm_formSuccess(response)
                                 {
                                    this.panel.destroy();
                                    if (this.config.success && YAHOO.lang.isFunction(this.config.success.fn))
                                    {
                                       this.config.success.fn.call(this.config.success.scope || {}, response, this.config.success.obj)
                                    }
                                 },
                                 scope: this
                              },
                              successMessage: this.config.successMessage,
                              failureCallback: this.config.success,
                              failureMessage: this.config.failureMessage
                           });
                  }
               },
               {
                  panel: panel,
                  config: config
               });
      },

      /**
       * Displays a webscript inside a dialog
       *
       * @method displayWebscript
       * @param config
       * @param config.title {String} The dialog title
       * @param config.method {String} (Optional) Defaults to "GET"
       * @param config.url {String} THe url to the webscript to load
       * @param config.properties {Object} An object literal with the webscript parameters
       */
      displayWebscript: function PopupManager_displayWebscript(config)
      {
         // Help creating a htmlid if none has been provided
         config.properties.htmlid = config.properties.htmlid || Alfresco.util.generateDomId();

         var p = new YAHOO.widget.Dialog(config.properties.htmlid + "-panel",
               {
                  visible:false,
                  modal: true,
                  constraintoviewport: true,
                  fixedcenter: "contained",
                  postmethod: "none"
               });

         // Load the form for the specific workflow
         Alfresco.util.Ajax.request(
               {
                  method: config.method || Alfresco.util.Ajax.GET,
                  url: config.url,
                  dataObj: config.properties,
                  successCallback:
                  {
                     fn: function PopupManager_displayWebscript_successCallback(response, config)
                     {
                        // Instantiate a Panel from script
                  if (config.title)
                  {
                        p.setHeader(config.title);
                  }
                        p.setBody(response.serverResponse.responseText);
                        p.render(document.body);
                        p.show();
                     },
                     scope: this,
                     obj: config
                  },
                  failureMessage: Alfresco.util.message("message.failure"),
                  scope: this,
                  execScripts: true
               });
         return p;
      }
   });
}();


/**
 * Keeps track of multiple filters on a page. Filters should register() upon creation to be compliant.
 * @class Alfresco.util.FilterManager
 */
Alfresco.util.FilterManager = function()
{
   /**
    * Array of registered filters.
    *
    * @property filters
    * @type Array
    */
   var filters = [];

   return (
   {
      /**
       * Main entrypoint for filters wishing to register themselves with the FilterManager
       * @method register
       * @param p_filterOwner {string} Name of the owner registering this filter. Used when owning exclusive filters.
       * @param p_filterIds {string|Array} Single or multiple filterIds this filter owns
       */
      register: function FM_register(p_filterOwner, p_filterIds)
      {
         var i, ii, filterId;

         if (typeof p_filterIds == "string")
         {
            p_filterIds = [p_filterIds];
         }

         for (i = 0, ii = p_filterIds.length; i < ii; i++)
         {
            filterId = p_filterIds[i];
            filters.push(
                  {
                     filterOwner: p_filterOwner,
                     filterId: filterId
                  });
            filters[filterId] = p_filterOwner;
         }
      },

      /**
       * Get filterOwner by filterId
       *
       * @method getOwner
       * @param p_filterId {string} FilterId
       * @return {string|null} filterOwner that has registered for the given filterId
       */
      getOwner: function FM_getOwner(p_filterId)
      {
         return (filters[p_filterId] || null);
      }
   });
}();

/**
 * Helper class for getting the CSRF token and the name of the request header or param to set it to.
 *
 * @class Alfresco.util.CSRFPolicy
 */
Alfresco.util.CSRFPolicy = function()
{
   var properties = Alfresco.constants.CSRF_POLICY.properties || {};
   function resolve(str)
   {
      return YAHOO.lang.substitute(str, properties);
   }
   return {

      /**
       * Use this method and check if the CSRF filter is enabled before trying to set the CSRF header or parameter.
       * Will be disabled if the filter contains no rules.
       *
       * @return {*}
       */
      isFilterEnabled: function()
      {
         return Alfresco.constants.CSRF_POLICY.enabled;
      },

      /**
       * Returns the name of the request header to put the token in when sending XMLHttpRequests.
       *
       * @method getHeader
       * @return {String} The name of the request header to put the token in.
       */
      getHeader: function()
      {
         return resolve(Alfresco.constants.CSRF_POLICY.header);
      },

      /**
       * Returns the name of the request parameter to put the token in when sending multipart form uploads.
       *
       * @method getParameter
       * @return {String} The name of the request header to put the token in.
       */
      getParameter: function()
      {
         return resolve(Alfresco.constants.CSRF_POLICY.parameter);
      },

      /**
       * Returns the name of the cookie that holds the value of the token.
       *
       * @method getCookie
       * @return {String} The name of the request header to put the token in.
       */
      getCookie: function()
      {
         return resolve(Alfresco.constants.CSRF_POLICY.cookie);
      },

      /**
       * Returns the token.
       *
       * Note! Make sure to use this method just before a request is made against the server since it might have been
       * updated in another browser tab or window.
       *
       * @method getToken
       * @return {String} The name of the request header to put the token in.
       */
      getToken: function()
      {
         var token = null;
         var cookieName = this.getCookie();
         if (cookieName)
         {
            token = YAHOO.util.Cookie.get(cookieName);
            if (token)
            {
               // remove quotes to support Jetty app-server - bug where it quotes a valid cookie value see ALF-18823
               token = token.replace(/"/g, '');
            }
         }
         return token;
      }
   };
}();

/**
 * Helper class for deciding if a url shall be allowed to be included as an iframe inside Share.
 *
 * @class Alfresco.util.IFramePolicy
 */
Alfresco.util.IFramePolicy = function()
{
   return {

      /**
       * Use this method and check if the url has been configured to be allowed to be included as an <iframe> inside Share.
       *
       * @return {boolean} True if the url is allowed.
       */
      isUrlAllowed: function(url)
      {
         // Is it a local url?
         if (url.indexOf(window.location.protocol + "//" + window.location.host) == 0)
         {
            return Alfresco.constants.IFRAME_POLICY.sameDomain == "allow";
         }

         var crossDomainUrls = Alfresco.constants.IFRAME_POLICY.crossDomainUrls;
         for (var i = 0; i < crossDomainUrls.length; i++)
         {
            if (crossDomainUrls[i] == "*" || url.indexOf(crossDomainUrls[i]) == 0)
            {
               return true;
            }
         }
         return false;
      }
   };
}();


/**
 * Helper class for submitting data to serverthat wraps a
 * YAHOO.util.Connect.asyncRequest call.
 *
 * The request methid provides default behaviour for displaying messages on
 * success and error events and simplifies json handling with encoding and decoding.
 *
 * @class Alfresco.util.Ajax
 */
Alfresco.util.Ajax = function()
{
   // Since we mix FORM & JSON request we must make sure our Content-type headers aren't overriden
   //YAHOO.util.Connect.setDefaultPostHeader(false);
   //YAHOO.util.Connect.setDefaultXhrHeader(false);

   return {

      /**
       * Constant for contentType of type standard XHR form request
       *
       * @property FORM
       * @type string
       */
      FORM: "application/x-www-form-urlencoded",

      /**
       * Constant for contentType of type json
       *
       * @property JSON
       * @type string
       */
      JSON: "application/json",

      /**
       * Constant for method of type GET
       *
       * @property GET
       * @type string
       */
      GET: "GET",

      /**
       * Constant for method of type POST
       *
       * @property POST
       * @type string
       */
      POST: "POST",

      /**
       * Constant for method of type PUT
       *
       * @property PUT
       * @type string
       */
      PUT: "PUT",

      /**
       * Constant for method of type DELETE
       *
       * @property DELETE
       * @type string
       */
      DELETE: "DELETE",

      /**
       * The default request config used by method request()
       *
       * @property defaultRequestConfig
       * @type object
       */
      defaultRequestConfig:
      {
         method: "GET",        // GET, POST, PUT or DELETE
         url: null,            // Must be set by user
         dataObj: null,        // Will be encoded to parameters (key1=value1&key2=value2)
         // or a json string if contentType is set to JSON
         dataStr: null,        // Will be used in the request body, could be a already created parameter or json string
         // Will be overriden by the encoding result from dataObj if dataObj is provided
         dataForm: null,       // A form object or id that contains the data to be sent with request
         requestContentType: null,    // Set to JSON if json should be used
         responseContentType: null,    // Set to JSON if json should be used
         successCallback: null,// Object literal representing callback upon successful operation
         successMessage: null, // Will be displayed by Alfresco.util.PopupManager.displayMessage if no success handler is provided
         failureCallback: null,// Object literal representing callback upon failed operation
         failureMessage: null,  // Will be displayed by Alfresco.util.displayPrompt if no failure handler is provided
         execScripts: false,    // Whether embedded <script> tags will be executed within the successful response
         noReloadOnAuthFailure: false, // Default to reloading the page on HTTP 401 response, which will redirect through the login page
         object: null           // An object that can be passed to be used by the success or failure handlers
      },

      /**
       * Wraps a YAHOO.util.Connect.asyncRequest call and provides some default
       * behaviour for displaying error or success messages, uri encoding and
       * json encoding and decoding.
       *
       * JSON
       *
       * If requestContentType is JSON, config.dataObj (if available) is encoded
       * to a json string and set in the request body.
       *
       * If a json string already has been created by the application it should
       * be passed in as the config.dataStr which will be put in the rewuest body.
       *
       * If responseContentType is JSON the server response is decoded to a
       * json object and set in the "json" attribute in the response object
       * which is passed to the succes or failure callback.
       *
       * PARAMETERS
       *
       * If requestContentType is null, config.dataObj (if available) is encoded
       * to a normal parameter string which is added to the url if method is
       * GET or DELETE and to the request body if method is POST or PUT.
       *
       * FORMS
       * A form can also be passed it and submitted just as desccribed in the
       * YUI documentation.
       *
       * SUCCESS
       *
       * If the request is successful successCallback.fn is called.
       * If successCallback.fn isn't provided successMessage is displayed.
       * If successMessage isn't provided nothing happens.
       *
       * FAILURE
       *
       * If the request fails failureCallback.fn is called.
       * If failureCallback.fn isn't displayed failureMessage is displayed.
       * If failureMessage isn't provided the "best error message as possible"
       * from the server response is displayed.
       *
       * CALLBACKS
       *
       * The success or failure handlers can expect a response object of the
       * following form (they will be called in the scope defined by config.scope)
       *
       * {
       *   config: {object},         // The config object passed in to the request,
       *   serverResponse: {object}, // The response provided by YUI
       *   json: {object}            // The serverResponse parsed and ready as an object
       * }
       *
       * @method request
       * @param config {object} Description of the request that should be made
       * The config object has the following form:
       * {
       *    method: {string}               // GET, POST, PUT or DELETE, default is GET
       *    url: {string},                 // the url to send the request to, mandatory
       *    dataObj: {object},             // Will be encoded to parameters (key1=value1&key2=value2) or a json string if requestContentType is set to JSON
       *    dataStr: {string},             // the request body, will be overriden by the encoding result from dataObj if dataObj is provided
       *    dataForm: {HTMLElement},       // A form object or id that contains the data to be sent with request
       *    requestContentType: {string},  // Set to JSON if json should be used
       *    responseContentType: {string}, // Set to JSON if json should be used
       *    successCallback: {object},     // Callback for successful request, should have the following form: {fn: successHandler, scope: scopeForSuccessHandler}
       *    successMessage: {string},      // Will be displayed using Alfresco.util.PopupManager.displayMessage if successCallback isn't provided
       *    failureCallback: {object},     // Callback for failed request, should have the following form: {fn: failureHandler, scope: scopeForFailureHandler}
       *    failureMessage: {string},      // Will be displayed by Alfresco.util.displayPrompt if no failureCallback isn't provided
       *    execScripts: {boolean},        // Whether embedded <script> tags will be executed within the successful response
       *    noReloadOnAuthFailure: {boolean}, // Set to TRUE to prevent an automatic page refresh on HTTP 401 response
       *    object: {object}               // An object that can be passed to be used by the success or failure handlers
       * }
       */
      request: function(config)
      {
         // Merge the user config with the default config and check for mandatory parameters
         var c = YAHOO.lang.merge(this.defaultRequestConfig, config);
         Alfresco.util.assertNotEmpty(c.url, "Parameter 'url' can NOT be null");
         Alfresco.util.assertNotEmpty(c.method, "Parameter 'method' can NOT be null");

         // If a contentType is provided set it in the header
         if (c.requestContentType)
         {
            YAHOO.util.Connect.setDefaultPostHeader(c.requestContentType);
            YAHOO.util.Connect.setDefaultXhrHeader(c.requestContentType);
            YAHOO.util.Connect.initHeader("Content-Type", c.requestContentType);
         }
         else
         {
            YAHOO.util.Connect.setDefaultPostHeader(this.FORM);
            YAHOO.util.Connect.setDefaultXhrHeader(this.FORM);
            YAHOO.util.Connect.initHeader("Content-Type", this.FORM)
         }

         // CSRF token
         if (Alfresco.util.CSRFPolicy.isFilterEnabled())
         {
            YAHOO.util.Connect.initHeader(Alfresco.util.CSRFPolicy.getHeader(), Alfresco.util.CSRFPolicy.getToken());
         }

         // Encode dataObj depending on request method and contentType.
         // Note: GET requests are always queryString encoded.
         if (c.requestContentType === this.JSON)
         {
            if (c.method.toUpperCase() === this.GET)
            {
               if (c.dataObj)
               {
                  // Encode the dataObj and put it in the url
                  c.url += (c.url.indexOf("?") == -1 ? "?" : "&") + this.jsonToParamString(c.dataObj, true);
               }
            }
            else if (c.method.toUpperCase() !== this.DELETE)
            {
               // If json is used encode the dataObj parameter and put it in the body
               c.dataStr = YAHOO.lang.JSON.stringify(c.dataObj || {});
            }
         }
         else
         {
            if (c.dataObj)
            {
               // Normal URL parameters
               if (c.method.toUpperCase() === this.GET)
               {
                  // Encode the dataObj and put it in the url
                  c.url += (c.url.indexOf("?") == -1 ? "?" : "&") + this.jsonToParamString(c.dataObj, true);
               }
               else if (c.method.toUpperCase() !== this.DELETE)
               {
                  // Encode the dataObj and put it in the body
                  c.dataStr = this.jsonToParamString(c.dataObj, true);
               }
            }
         }

         if (c.dataForm !== null)
         {
            // Set the form on the connection manager
            YAHOO.util.Connect.setForm(c.dataForm);
         }

         /**
          * The private "inner" callback that will handle json and displaying
          * of messages and prompts
          */
         var callback =
         {
            success: this._successHandler,
            failure: this._failureHandler,
            scope: this,
            argument:
            {
               config: config
            }
         };

         // Do we need to tunnel the HTTP method if the client can't support it (Adobe AIR)
         if (YAHOO.env.ua.air !== 0)
         {
            // Check for unsupported HTTP methods
            if (c.method.toUpperCase() == "PUT" || c.method.toUpperCase() == "DELETE")
            {
               // Check we're not tunnelling already
               var alfMethod = Alfresco.util.getQueryStringParameter("alf_method", c.url);
               if (alfMethod === null)
               {
                  c.url += (c.url.indexOf("?") == -1 ? "?" : "&") + "alf_method=" + c.method;
                  c.method = this.POST;
               }
            }
         }

         // Add a noCache parameter to the URL to ensure that XHR requests are always made to the
         // server. This is added to tackle a specific problem in IE where 304 responses are assumed
         // for XHR requests. This has intentionally been conditionally added just for IE
         if (YAHOO.env.ua.ie > 0)
         {
            c.url += (c.url.indexOf("?") == -1 ? "?" : "&") + "noCache=" + new Date().getTime();
         }

         // Make the request
         YAHOO.util.Connect.asyncRequest (c.method, c.url, callback, c.dataStr);
      },

      /**
       * Helper function for pure json requests, where both the request and
       * response are using json. Will result in a call to request() with
       * requestContentType and responseContentType set to JSON.
       *
       * @method request
       * @param config {object} Description of the request that should be made
       */
      jsonRequest: function(config)
      {
         config.requestContentType = this.JSON;
         config.responseContentType = this.JSON;
         this.request(config);
      },

      /**
       * Helper function for pure json requests, where both the request and
       * response are using json. Will result in a call to request() with
       * responseContentType set to JSON and method set to GET.
       *
       * @method request
       * @param config {object} Description of the request that should be made
       */
      jsonGet: function(config)
      {
         config.method = this.GET;
         this.jsonRequest(config);
      },

      /**
       * Helper function for pure json requests, where both the request and
       * response are using json. Will result in a call to request() with
       * requestContentType and responseContentType set to JSON and method set to POST.
       *
       * @method request
       * @param config {object} Description of the request that should be made
       */
      jsonPost: function(config)
      {
         config.method = this.POST;
         this.jsonRequest(config);
      },

      /**
       * Helper function for pure json requests, where both the request and
       * response are using json. Will result in a call to request() with
       * requestContentType and responseContentType set to JSON and method set to PUT.
       *
       * @method request
       * @param config {object} Description of the request that should be made
       */
      jsonPut: function(config)
      {
         config.method = this.PUT;
         this.jsonRequest(config);
      },

      /**
       * Helper function for pure json requests, where both the request and
       * response are using json. Will result in a call to request() with
       * responseContentType set to JSON and method set to DELETE.
       *
       * @method request
       * @param config {object} Description of the request that should be made
       */
      jsonDelete: function(config)
      {
         config.method = this.DELETE;
         this.jsonRequest(config);
      },

      /**
       * Takes an object and creates a decoded URL parameter string of it.
       * Note! Does not contain a '?' character in the beginning.
       *
       * @method request
       * @param obj
       * @param encode   indicates whether the parameter values should be encoded or not
       * @private
       */
      jsonToParamString: function(obj, encode)
      {
         var params = "", first = true, attr;

         for (attr in obj)
         {
            if (obj.hasOwnProperty(attr))
            {
               if (first)
               {
                  first = false;
               }
               else
               {
                  params += "&";
               }

               // Make sure no user input destroys the url 
               if (encode)
               {
                  params += encodeURIComponent(attr) + "=" + encodeURIComponent(obj[attr]);
               }
               else
               {
                  params += attr + "=" + obj[attr];
               }
            }
         }
         return params;
      },

      /**
       * Handles successful request triggered by the request() method.
       * If execScripts was requested, retrieve and execute the script(s).
       * Otherwise, fall through to the _successHandlerPostExec function immediately.
       *
       * @method request
       * @param serverResponse
       * @private
       */
      _successHandler: function(serverResponse)
      {
         // Get the config that was used in the request() method
         var config = serverResponse.argument.config;

         // Need to execute embedded "<script>" tags?
         if (config.execScripts)
         {
            var result = this.sanitizeMarkup(serverResponse.responseText);

            // Set the responseText to only contain script cleaned markup
            serverResponse.responseText = result[0];

            // Use setTimeout to execute the script. Note scope will always be "window"
            var scripts = result[1];
            if (YAHOO.lang.trim(scripts).length > 0)
            {
               window.setTimeout(scripts, 0);
               // Delay-call the PostExec function to continue response processing after the setTimeout above
               YAHOO.lang.later(0, this, this._successHandlerPostExec, serverResponse);
            }
            else
            {
               this._successHandlerPostExec(serverResponse);
            }
         }
         else
         {
            this._successHandlerPostExec(serverResponse);
         }
      },

      /**
       * Parses the markup and returns an array with clean markup (without script elements) and the actual script code
       *
       * @param markup
       * @return An array with cleaned markup at index 0 and the script code from the script leemnts at index 1
       */
      sanitizeMarkup: function(markup)
      {
         var scripts = [];
         var script = null;
         var regexp = /<script[^>]*>([\s\S]*?)<\/script>/gi;
         while ((script = regexp.exec(markup)))
         {
            scripts.push(script[1]);
         }
         scripts = scripts.join("\n");

         // Remove the script from the responseText so it doesn't get executed twice
         return [markup.replace(regexp, ""), scripts];
      },

      /**
       * Follow-up handler after successful request triggered by the request() method.
       * If execScripts was requested, this function continues after the scripts have been run.
       * If the responseContentType was set to json the response is decoded
       * for easy access to the success callback.
       * If no success callback is provided the successMessage is displayed
       * using Alfresco.util.PopupManager.displayMessage().
       * If no successMessage is provided nothing happens.
       *
       * @method request
       * @param serverResponse
       * @private
       */
      _successHandlerPostExec: function(serverResponse)
      {
         // Get the config that was used in the request() method
         var config = serverResponse.argument.config;

         if (config.execScripts)
         {
            /**
             * Scripts in the loaded template have now been executed, if they required a missing YUI module, that module
             * will now have been placed on the yuiLoader queue. To make sure the modules on the queue gets loaded
             * we need to tell YUILoaderHelper to start loading the modules by calling loadComponents().
             *
             * Note! The caller of the external scripts shall NOT expect the brought in components to have got their
             * onReady methods called if they required yui modules that wasn't previously loaded.
             * A safer approach is to let the brought in components fire a "custom ready event" at the end of their
             * "onReady" method, that the caller can listen to.
             */
            Alfresco.util.YUILoaderHelper.loadComponents();
         }

         var callback = config.successCallback;
         if (callback && typeof callback.fn == "function")
         {
            var contentType = serverResponse.getResponseHeader["Content-Type"] ||
                  serverResponse.getResponseHeader["content-type"] ||
                  config.responseContentType;
            // User provided a custom successHandler
            var json = null;

            if (/^\s*application\/json/.test(contentType))
            {
               // Decode the response since it should be json
               json = Alfresco.util.parseJSON(serverResponse.responseText);
            }

            // Call the success callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this),
                  {
                     config: config,
                     json: json,
                     serverResponse: serverResponse
                  }, callback.obj);
         }
         if (config.successMessage)
         {
            /**
             * User provided successMessage.
             */
            Alfresco.util.PopupManager.displayMessage(
                  {
                     text: config.successMessage
                  });
         }
      },

      /**
       * Handles failed request triggered by the request() method.
       * If the responseContentType was set to json the response is decoded
       * for easy access to the failure callback.
       * If no failure callback is provided the failureMessage is displayed
       * using Alfresco.util.PopupManager.displayPrompt().
       * If no failureMessage is provided "the best available server response"
       * is displayed using Alfresco.util.PopupManager.displayPrompt().
       *
       * @method request
       * @param serverResponse
       * @private
       */
      _failureHandler: function(serverResponse)
      {
         // Get the config that was used in the request() method
         var config = serverResponse.argument.config;

         // Our session has likely timed-out, so refresh to offer the login page
         var contentType = serverResponse.getResponseHeader["Content-Type"] ||
               serverResponse.getResponseHeader["content-type"] ||
               config.responseContentType;

         if ((serverResponse.status == 401 || (serverResponse.status == 302 && (/(text\/html)/).test(contentType)))
               && !config.noReloadOnAuthFailure)
         {
            var redirect = serverResponse.getResponseHeader["Location"];
            if (redirect)
            {
               window.location.href = window.location.protocol + "//" + window.location.host + redirect;
               return;
            }
            else
            {
               window.location.reload(true);
               return;
            }
         }

         // Invoke the callback
         var callback = config.failureCallback, json = null;

         if ((callback && typeof callback.fn == "function") || (config.failureMessage))
         {
            if (callback && typeof callback.fn == "function")
            {
               // If the caller has defined an error message display that instead of displaying message about bad json syntax
               var displayBadJsonResult = true;
               if (config.failureMessage || config.failureCallback)
               {
                  displayBadJsonResult = false;
               }

               // User provided a custom failureHandler
               if (config.responseContentType === "application/json")
               {
                  json = Alfresco.util.parseJSON(serverResponse.responseText, displayBadJsonResult);
               }
               callback.fn.call((typeof callback.scope == "object" ? callback.scope : this),
                     {
                        config: config,
                        json: json,
                        serverResponse: serverResponse
                     }, callback.obj);
            }
            if (config.failureMessage)
            {
               /**
                * User did not provide a custom failureHandler, instead display
                * the failureMessage if it exists
                */
               Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: Alfresco.util.message("message.failure", this.name),
                        text: config.failureMessage
                     });
            }
         }
         else
         {
            /**
             * User did not provide any failure info at all, display as good
             * info as possible from the server response.
             */
            if (config.responseContentType == "application/json")
            {
               json = Alfresco.util.parseJSON(serverResponse.responseText);
               if (json != null)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: json.status.name,
                     text: json.message
                  });
               }
            }
            else if (serverResponse.statusText)
            {
               Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: Alfresco.util.message("message.failure", this.name),
                        text: serverResponse.statusText
                     });
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: Alfresco.util.message("message.failure", this.name),
                        text: "Error sending data to server."
                     });
            }
         }
      }

   };
}();

/**
 * Helper class for setting the user mouse cursor and making sure its used the
 * same way in the whole application.
 *
 * Use setCursor with the predefined state constants to set the cursor.
 * Each constant has a css selector in base.css where it can be styled
 * differently if needed.
 *
 * @class Alfresco.util.Cursor
 */
Alfresco.util.Cursor = function()
{
   return (
   {
      /**
       * Show cursor in state to indicate that the current element is draggable.
       * Styled through css selector ".draggable" in base.css
       *
       * @property DRAGGABLE
       * @type string
       */
      DRAGGABLE: "dnd-draggable",

      /**
       * Show cursor in state to indicate that the current element is dragged.
       * Styled through css selector ".drag" in base.css
       *
       * @property DRAG
       * @type string
       */
      DRAG: "dnd-drag",

      /**
       * Show cursor in state to indicate that the element dragged over IS a valid drop point.
       * Styled through css selector ".dropValid" in base.css
       *
       * @property DROP_VALID
       * @type string
       */
      DROP_VALID: "dnd-dropValid",

      /**
       * Show cursor in state to indicate that the element dragged over is NOT a valid drop point.
       * Styled through css selector ".dropInvalid" in base.css
       *
       * @property DROP_INVALID
       * @type string
       */
      DROP_INVALID: "dnd-dropInvalid",

      /**
       * @method setCursorState
       * @param el {HTMLElement} Object that is dragged and who's style affects the cursor
       * @param cursor {string} Predifined constant from Alfresco.util.CURSOR_XXX
       */
      setCursorState: function(el, cursorState)
      {
         var allStates = [this.DRAGGABLE, this.DRAG, this.DROP_VALID, this.DROP_INVALID];
         for (var i = 0; i < allStates.length; i++)
         {
            var cs = allStates[i];
            if (cs === cursorState)
            {
               YUIDom.addClass(el, cursorState);
            }
            else
            {
               YUIDom.removeClass(el, cs);
            }
         }
      }
   });
}();

/**
 * Transition methods that handles browser limitations.
 *
 * @class Alfresco.util.Anim
 */
Alfresco.util.Anim = function()
{
   return (
   {
      /**
       * The default attributes for a fadeIn or fadeOut call.
       *
       * @property fadeAttributes
       * @type {object} An object literal of the following form:
       * {
       *    adjustDisplay: true, // Will handle style attribute "display" in
       *                         // the appropriate way depending on if its
       *                         // fadeIn or fadeOut, default is true.
       *    callback: null,      // A function that will get called after the fade
       *    scope: this,         // The scope the callback function will get called in
       *    period: 0.5          // Period over which animation occurs (seconds)
       */
      fadeAttributes:
      {
         adjustDisplay: true,
         callback: null,
         scope: this,
         period: 0.5
      },

      /**
       * Displays an object with opacity 0, increases the opacity during
       * 0.5 seconds for browsers supporting opcaity.
       *
       * (IE does not support opacity)
       *
       * @method fadeIn
       * @param el {HTMLElement} element to fade in
       * @param attributes
       */
      fadeIn: function A_fadeIn(el, attributes)
      {
         return this._fade(el, true, attributes);
      },

      /**
       * Displays an object with opacity 1, decreases the opacity during
       * 0.5 seconds for browsers supporting opacity and finally hides it.
       *
       * (IE does not support opacity)
       *
       * @method fadeOut
       * @param el {HTMLElement} element to fade out
       * @param attributes
       */
      fadeOut: function A_fadeOut(el, attributes)
      {
         return this._fade(el, false, attributes);
      },

      /**
       * @method _fade
       * @param el {HTMLElement} element to fade in
       * @param fadeIn {boolean} true if fadeIn false if fadeOut
       * @param attributes
       */
      _fade: function A__fade(el, fadeIn, attributes)
      {
         el = YUIDom.get(el);
         // No manadatory elements in attributes, avoid null checks below though
         attributes = YAHOO.lang.merge(this.fadeAttributes, attributes ? attributes : {});
         var adjustDisplay = attributes.adjustDisplay;

         // todo test against functionality instead of browser
         var supportsOpacity = YAHOO.env.ua.ie === 0;

         // Prepare el before fade
         if (supportsOpacity)
         {
            YUIDom.setStyle(el, "opacity", fadeIn ? 0 : 1);
         }

         // Show the element, transparent if opacity supported,
         // otherwise its visible and the "fade in" is finished
         if (supportsOpacity)
         {
            YUIDom.setStyle(el, "visibility", "visible");
         }
         else
         {
            YUIDom.setStyle(el, "visibility", fadeIn ? "visible" : "hidden");
         }

         // Make sure element is displayed
         if (adjustDisplay && YUIDom.getStyle(el, "display") === "none")
         {
            YUIDom.setStyle(el, "display", "block");
         }

         // Put variables in scope so they can be used in the callback below
         var fn = attributes.callback,
               scope = attributes.scope,
               myEl = el;

         if (supportsOpacity)
         {
            // Do the fade (from value/opacity has already been set above)
            var fade = new YAHOO.util.Anim(el,
                  {
                     opacity:
                     {
                        to: fadeIn ? 1 : 0
                     }
                  }, attributes.period);

            fade.onComplete.subscribe(function(e)
            {
               if (!fadeIn && adjustDisplay)
               {
                  // Hide element from Dom if its a fadeOut
                  YUIDom.setStyle(myEl, "display", "none");
               }
               if (fn)
               {
                  // Call custom callback
                  fn.call(scope ? scope : this);
               }
            });
            fade.animate();
         }
         else
         {
            if (!fadeIn && adjustDisplay)
            {
               // Hide element from Dom if its a fadeOut
               YUIDom.setStyle(myEl, "display", "none");
            }
            if (fn)
            {
               // Call custom callback
               fn.call(scope ? scope : this);
            }
         }
      },

      /**
       * Default attributes for a pulse call.
       *
       * @property pulseAttributes
       * @type {object} An object literal containing:
       *    callback: {object} Function definition for callback on complete. {fn, scope, obj}
       *    inColor: {string} Optional colour for the pulse (default is #ffff80)
       *    outColor: {string} Optional colour to fade back to (default is original element backgroundColor)
       *    inDuration: {int} Optional time for "in" animation (default 0.2s)
       *    outDuration: {int} Optional time for "out" animation (default 1.2s)
       *    clearOnComplete: {boolean} Set to clear the backgroundColor style on pulse complete (default true)
       */
      pulseAttributes:
      {
         callback: null,
         inColor: "#ffff80",
         inDuration: 0.2,
         outDuration: 1.2,
         clearOnComplete: true
      },

      /**
       * Pulses the background colo(u)r of an HTMLELement
       *
       * @method pulse
       * @param el {HTMLElement|string} element to fade out
       * @param attributes {object} Object literal containing optional custom values
       */
      pulse: function A_pulse(p_el, p_attributes)
      {
         // Shortcut return if animation library not loaded
         if (!YAHOO.util.ColorAnim)
         {
            return;
         }

         var el = YUIDom.get(p_el);
         if (el)
         {
            // Set outColor to existing backgroundColor
            // Fix for ALF-12308 Stack specific: Script error when reply on a topic
            // ColorAnim.parseColor() returns null for rgba(0, 0, 0, 0).
            // Also IE and FF return "transparent" which cannot be parsed as well.
            //debugger;
            var rgbaRegexp = /^rgba\((\d+), (\d+), (\d+), (\d+)\)$/i,
                  transparent = /^transparent$/i,
                  outColor = YUIDom.getStyle(el, "backgroundColor");
            if (rgbaRegexp.test(outColor))
            {
               var rgba = rgbaRegexp.exec(outColor);
               // Check wether it's black
               if (rgba[1] == 0 && rgba[2] == 0 && rgba[3] == 0 && rgba[4] == 0)
                  outColor = "#fff";
               else
                  outColor = "rgb(" + rgba[1] + ", " + rgba[2] + ", " + rgba[3] + ")";
            }
            else if (transparent.test(outColor))
            {
               outColor = "#fff";
            }

            var attr = YAHOO.lang.merge(this.pulseAttributes,
                  {
                     outColor: outColor
                  });
            if (typeof p_attributes == "object")
            {
               attr = YAHOO.lang.merge(attr, p_attributes);
            }

            // The "in" animation class
            var animIn = new YAHOO.util.ColorAnim(el,
                  {
                     backgroundColor:
                     {
                        to: attr.inColor
                     }
                  }, attr.inDuration);

            // The "out" animation class
            var animOut = new YAHOO.util.ColorAnim(el,
                  {
                     backgroundColor:
                     {
                        to: attr.outColor
                     }
                  }, attr.outDuration);

            // onComplete functions
            animIn.onComplete.subscribe(function A_aI_onComplete()
            {
               animOut.animate();
            });

            animOut.onComplete.subscribe(function A_aO_onComplete()
            {
               if (attr.clearOnComplete)
               {
                  YUIDom.setStyle(el, "backgroundColor", "");
               }
               if (attr.callback && (typeof attr.callback.fn == "function"))
               {
                  attr.callback.fn.call(attr.callback.scope || this, attr.callback.obj);
               }
            });

            // Kick off the pulse animation
            animIn.animate();
         }
      }
   });
}();

/**
 * Helper class for managing nodeRefs.
 * Provides helper properties for obtaining various parts and formats of nodeRefs.
 * <pre>
 *    nodeRef: return nodeRef as passed-in
 *    storeType, storeId, id: return individual nodeRef parts
 *    uri: return nodeRef in "uri" format, i.e. without ":/" between storeType and storeId
 * </pre>
 *
 * @class Alfresco.util.NodeRef
 */
(function()
{
   Alfresco.util.NodeRef = function(nodeRef)
   {
      try
      {
         var uri = nodeRef.replace(":/", ""),
               arr = uri.split("/");

         return (
         {
            nodeRef: nodeRef,
            storeType: arr[0],
            storeId: arr[1],
            id: arr[2],
            uri: uri,
            toString: function()
            {
               return nodeRef;
            }
         });
      }
      catch (e)
      {
         e.message = "Invalid nodeRef: " + nodeRef;
         throw e;
      }
   };
})();

/**
 * Short QName property names, used by Alfresco.util.Node class
 */
Alfresco.constants = YAHOO.lang.merge(Alfresco.constants || {},
      {
         /* Content model */
         PROP_NAME: "cm:name",
         PROP_TITLE: "cm:title",
         PROP_DESCRIPTION: "cm:description",
         PROP_CREATED: "cm:created",
         PROP_CREATOR: "cm:creator",
         PROP_MODIFIER: "cm:modifier",
         PROP_MODIFIED: "cm:modified",
         PROP_CATEGORIES: "cm:categories",
         PROP_TAGGABLE: "cm:taggable",

         /* Google Docs model */
         PROP_GOOGLEDOC_URL: "gd:url"
      });

/**
 * Helper class for managing Nodes.
 * Provides convenience functions for accessing node properties, aspects, etc.
 * Requires a JSON structure representing a Node, as returned from appUtils.toJSON()
 * <pre>
 *    node: return node as passed-in, or parsed from JSON string
 *    toJSON: return JSON string representing the node
 *    storeType, storeId, id: return individual nodeRef parts
 *    uri: return nodeRef in "uri" format, i.e. without ":/" between storeType and storeId
 * </pre>
 *
 * @class Alfresco.util.Node
 * @param node JSON string or object representing the node
 */
(function()
{
   Alfresco.util.Node = function(p_node)
   {
      if (YAHOO.lang.isUndefined(p_node) || p_node == null)
      {
         return null;
      }

      var node = YAHOO.lang.isString(p_node) ? YAHOO.lang.JSON.parse(p_node) : p_node,
            nodeJSON = YAHOO.lang.isString(p_node) ? p_node : YAHOO.lang.JSON.stringify(p_node);

      var nodeRef = new Alfresco.util.NodeRef(node.nodeRef),
            properties = node.properties || {},
            aspects = node.aspects || [],
            permissions = node.permissions || {},
            aspectsObj = null,
            tagsArray = null,
            tagsObj = null,
            categoriesArray = null;
      categoriesObj = null;

      /**
       * Populates the properties object literal with all "cm:" properties for easy access.
       * Therefore description can be accessed either as node.properties[Alfresco.constants.PROP_DESCRIPTION]
       * or, more simply, as node.properties.description.
       *
       * For all properties, the ":" character is replaced with an underscore.
       * These can then be accessed as, for example, node.properties.gd_googleUrl
       */
      for (var index in properties)
      {
         if (properties.hasOwnProperty(index))
         {
            if (index.indexOf("cm:") === 0)
            {
               properties[index.substring(3)] = properties[index];
            }
            properties[index.replace(/:/g, "_")] = properties[index];
         }
      };

      /**
       * Private functions
       */

      var getTags = function Node_getTags()
      {
         if (tagsArray === null)
         {
            tagsArray = [];

            var prop_taggable = node.properties[Alfresco.constants.PROP_TAGGABLE] || [];

            for (var i = 0, ii = prop_taggable.length; i < ii; i++)
            {
               tagsArray.push(prop_taggable[i].name);
            }
         }
         return tagsArray;
      };

      var getCategories = function Node_getCategories()
      {
         if (categoriesArray === null)
         {
            categoriesArray = [];

            var prop_categories = node.properties[Alfresco.constants.PROP_CATEGORIES] || [];

            for (var i = 0, ii = prop_categories.length; i < ii; i++)
            {
               categoriesArray.push([prop_categories[i].name, prop_categories[i].path]);
            }
         }
         return categoriesArray;
      };

      return (
      {
         /* Retrieve original object */
         getNode: function()
         {
            return node;
         },
         toJSON: function()
         {
            return YAHOO.lang.JSON.stringify(node);
         },

         /* Set nodeRef - doesn't requery node properties. Used when generating new page urls */
         setNodeRef: function(p_nodeRef)
         {
            this.nodeRef = new Alfresco.util.NodeRef(p_nodeRef);
         },

         /* Core node properties */
         nodeRef: nodeRef,
         type: node.type,
         isContainer: node.isContainer,
         isLink: node.isLink,
         isLocked: node.isLocked,
         linkedNode: new Alfresco.util.Node(node.linkedNode),

         /* Content Nodes */
         contentURL: node.contentURL,
         mimetype: node.mimetype,
         mimetypeDisplayName: node.mimetypeDisplayName,
         size: node.size,

         /* Properties */
         properties: properties,
         hasProperty: function(property)
         {
            return properties.hasOwnProperty(property);
         },

         /* Aspects */
         aspects: aspects,
         hasAspect: function(aspect)
         {
            if (aspectsObj === null)
            {
               aspectsObj = Alfresco.util.arrayToObject(this.aspects);
            }
            return aspectsObj.hasOwnProperty(aspect);
         },

         /* Permissions */
         permissions: permissions,
         hasPermission: function(permission)
         {
            return permissions.user[permission];
         },

         /* Tags */
         tags: getTags(),
         hasTag: function(tag)
         {
            if (tagsObj === null)
            {
               tagsObj = Alfresco.util.arrayToObject(this.tags);
            }
            return tagsObj.hasOwnProperty(tag);
         },

         /* Categories */
         categories: getCategories()
      });
   };
})();

/**
 * Utility class to defer a function until all supplied conditions have been met via fulfil() call(s).
 * A callback is only invoked once.
 * <pre>
 *    conditions: {Object|String[]} Object literal containing boolean properties (use to preset conditions if required), or an array of strings representing condition names
 *    callback: {Object} Callback function of the form { fn: function, scope: callback scope, obj: optional pass-thru object }
 * </pre>
 *
 * @class Alfresco.util.Deferred
 */
(function()
{
   Alfresco.util.Deferred = function(p_conditions, p_callback)
   {
      /**
       * Expired flag. Ensures callback is only invoked once.
       */
      var expired = false;

      /**
       * Deferred function callback
       */
      var callback = p_callback;

      /**
       * Condition flags
       */
      var conditions = {};

      if (YAHOO.lang.isArray(p_conditions))
      {
         conditions = Alfresco.util.arrayToObject(p_conditions, false);
      }
      else
      {
         YAHOO.lang.augmentObject(conditions, p_conditions, true);
      }

      /**
       * Checks all conditions are fulfilled and invokes callback if they are.
       *
       * @method conditionCheck
       * @private
       */
      var conditionCheck = function Deferred_conditionCheck()
      {
         for (var index in conditions)
         {
            if (conditions.hasOwnProperty(index))
            {
               if (conditions[index] !== true)
               {
                  return;
               }
            }
         }

         // All conditions are fulfilled if we got here
         if (YAHOO.lang.isFunction(callback.fn))
         {
            expired = true;
            callback.fn.call(callback.scope || window, callback.obj);
         }
      };

      return (
      {
         /**
          * Fulfils a condition and subsequently check for all conditions being fulfilled.
          *
          * @method fulfil
          * @param name {String} The name of the condition to fulfil.
          */
         fulfil: function Deferred_fulfil(p_name)
         {
            if (!expired && conditions.hasOwnProperty(p_name))
            {
               conditions[p_name] = true;
               conditionCheck();
               return true;
            }
            return false;
         },

         /**
          * Immediately expires the deferral.
          *
          * @method expire
          */
         expire: function Deferred_expire()
         {
            expired = true;
         }
      });
   };
})();

/**
 * Logging makes use of the log4javascript framework.
 *
 * Original code:
 *    Author: Tim Down <tim@log4javascript.org>
 *    Version: 1.4.1
 *    Edition: log4javascript
 *    Build date: 24 March 2009
 *    Website: http://log4javascript.org
 */
if (typeof log4javascript != "undefined")
{
   /**
    * Initial state on page load:
    *    Always show if AUTOLOGGING flag is true
    *    Show if AUTOLOGGING flag is false, but have enabled logger in this session
    */
   Alfresco.logger = log4javascript.getDefaultLogger();
   if (Alfresco.constants.AUTOLOGGING || Alfresco.util.getQueryStringParameter("log") == "on")
   {
      Alfresco.logger.info("Alfresco LOGGING enabled.");
   }
   else
   {
      if (Alfresco.util.getVar("logging", false))
      {
         Alfresco.logger.info("Alfresco LOGGING re-enabled.");
      }
      else
      {
         log4javascript.setEnabled(false);
      }
   }

   // Hook log window unload event
   Alfresco.logger.getEffectiveAppenders()[0].addEventListener("unload", function()
   {
      log4javascript.setEnabled(false);
      Alfresco.util.setVar("logging", false);
   });

   // Hook key sequence to enable log window
   if (window.addEventListener)
   {
      var sequence = [],
            logSequence = [17, 17, 16, 16], // Ctrl, Ctrl, Shift, Shift
            logSequenceLen = logSequence.length,
            logSequenceStr = logSequence.toString();

      document.addEventListener("keydown", function(e)
      {
         sequence.push(e.keyCode);
         while (sequence.length > logSequenceLen)
         {
            sequence.shift();
         }
         if (sequence.toString().indexOf(logSequenceStr) >= 0)
         {
            sequence = [];
            if (log4javascript.isEnabled())
            {
               log4javascript.setEnabled(false);
               Alfresco.logger.getEffectiveAppenders()[0].hide();
               Alfresco.util.setVar("logging", false);
            }
            else
            {
               log4javascript.setEnabled(true);
               Alfresco.logger.getEffectiveAppenders()[0].show();
               Alfresco.util.setVar("logging", true);
            }
         }
      }, true);
   }
}
else
{
   Alfresco.logger =
   {
      trace: function() {},
      debug: function() {},
      info: function() {},
      warn: function() {},
      error: function() {},
      fatal: function() {},
      isDebugEnabled: function()
      {
         return false;
      }
   };
};


/**
 * Format a date object to a user-specified mask
 * Modified to retrieve i18n strings from Alfresco.messages
 *
 * Original code:
 *    Date Format 1.1
 *    (c) 2007 Steven Levithan <stevenlevithan.com>
 *    MIT license
 *    With code by Scott Trenda (Z and o flags, and enhanced brevity)
 *
 * http://blog.stevenlevithan.com/archives/date-time-format
 *
 * @method Alfresco.thirdparty.dateFormat
 * @return {string}
 * @static
 */
Alfresco.thirdparty.dateFormat = function()
{
   /*** dateFormat
    Accepts a date, a mask, or a date and a mask.
    Returns a formatted version of the given date.
    The date defaults to the current date/time.
    The mask defaults ``"ddd mmm d yyyy HH:MM:ss"``.
    */
   var dateFormat = function()
   {
      var   token        = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloZ]|"[^"]*"|'[^']*'/g,
            timezone     = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
            timezoneClip = /[^-+\dA-Z]/g,
            pad = function (value, length)
            {
               value = String(value);
               length = parseInt(length, 10) || 2;
               while (value.length < length)
               {
                  value = "0" + value;
               }
               return value;
            };

      // Regexes and supporting functions are cached through closure
      return function (date, mask)
      {
         // Treat the first argument as a mask if it doesn't contain any numbers
         if (arguments.length == 1 &&
               (typeof date == "string" || date instanceof String) &&
               !/\d/.test(date))
         {
            mask = date;
            date = undefined;
         }

         if (typeof date == "string")
         {
            date = date.replace(".", "");
         }

         date = date ? new Date(date) : new Date();
         if (isNaN(date))
         {
            throw "invalid date";
         }

         mask = String(this.masks[mask] || mask || this.masks["default"]);

         var d = date.getDate(),
               D = date.getDay(),
               m = date.getMonth(),
               y = date.getFullYear(),
               H = date.getHours(),
               M = date.getMinutes(),
               s = date.getSeconds(),
               L = date.getMilliseconds(),
               o = date.getTimezoneOffset(),
               flags =
               {
                  d:    d,
                  dd:   pad(d),
                  ddd:  this.i18n.dayNames[D],
                  dddd: this.i18n.dayNames[D + 7],
                  m:    m + 1,
                  mm:   pad(m + 1),
                  mmm:  this.i18n.monthNames[m],
                  mmmm: this.i18n.monthNames[m + 12],
                  yy:   String(y).slice(2),
                  yyyy: y,
                  h:    H % 12 || 12,
                  hh:   pad(H % 12 || 12),
                  H:    H,
                  HH:   pad(H),
                  M:    M,
                  MM:   pad(M),
                  s:    s,
                  ss:   pad(s),
                  l:    pad(L, 3),
                  L:    pad(L > 99 ? Math.round(L / 10) : L),
                  t:    H < 12 ? this.TIME_AM.charAt(0) : this.TIME_PM.charAt(0),
                  tt:   H < 12 ? this.TIME_AM : this.TIME_PM,
                  T:    H < 12 ? this.TIME_AM.charAt(0).toUpperCase() : this.TIME_PM.charAt(0).toUpperCase(),
                  TT:   H < 12 ? this.TIME_AM.toUpperCase() : this.TIME_PM.toUpperCase(),
                  Z:    (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
                  o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4)
               };

         return mask.replace(token, function ($0)
         {
            return ($0 in flags) ? flags[$0] : $0.slice(1, $0.length - 1);
         });
      };
   }();

   /**
    * Alfresco wrapper: delegate to wrapped code
    */
   return dateFormat.apply(arguments.callee, arguments);
};
Alfresco.thirdparty.dateFormat.DAY_NAMES = (Alfresco.util.message("days.medium") + "," + Alfresco.util.message("days.long")).split(",");
Alfresco.thirdparty.dateFormat.MONTH_NAMES = (Alfresco.util.message("months.short") + "," + Alfresco.util.message("months.long")).split(",");
Alfresco.thirdparty.dateFormat.TIME_AM = Alfresco.util.message("date-format.am");
Alfresco.thirdparty.dateFormat.TIME_PM = Alfresco.util.message("date-format.pm");
Alfresco.thirdparty.dateFormat.masks =
{
   "default":       Alfresco.util.message("date-format.default"),
   defaultDateOnly: Alfresco.util.message("date-format.defaultDateOnly"),
   shortDate:       Alfresco.util.message("date-format.shortDate"),
   mediumDate:      Alfresco.util.message("date-format.mediumDate"),
   longDate:        Alfresco.util.message("date-format.longDate"),
   fullDate:        Alfresco.util.message("date-format.fullDate"),
   shortTime:       Alfresco.util.message("date-format.shortTime"),
   mediumTime:      Alfresco.util.message("date-format.mediumTime"),
   longTime:        Alfresco.util.message("date-format.longTime"),
   isoDate:         "yyyy-mm-dd",
   isoTime:         "HH:MM:ss",
   isoDateTime:     "yyyy-mm-dd'T'HH:MM:ss",
   isoFullDateTime: "yyyy-mm-dd'T'HH:MM:ss.lo"
};
Alfresco.thirdparty.dateFormat.i18n =
{
   dayNames: Alfresco.thirdparty.dateFormat.DAY_NAMES,
   monthNames: Alfresco.thirdparty.dateFormat.MONTH_NAMES
};


/**
 * Converts an ISO8601-formatted date into a JavaScript native Date object
 *
 * Original code:
 *    dojo.date.stamp.fromISOString
 *    Copyright (c) 2005-2008, The Dojo Foundation
 *    All rights reserved.
 *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
 *
 * @method Alfresco.thirdparty.fromISO8601
 * @param formattedString {string} ISO8601-formatted date string
 * @return {Date|null}
 * @static
 */
Alfresco.thirdparty.fromISO8601 = function()
{
   var fromISOString = function()
   {
      //   summary:
      //      Returns a Date object given a string formatted according to a subset of the ISO-8601 standard.
      //
      //   description:
      //      Accepts a string formatted according to a profile of ISO8601 as defined by
      //      [RFC3339](http://www.ietf.org/rfc/rfc3339.txt), except that partial input is allowed.
      //      Can also process dates as specified [by the W3C](http://www.w3.org/TR/NOTE-datetime)
      //      The following combinations are valid:
      //
      //         * dates only
      //         |   * yyyy
      //         |   * yyyy-MM
      //         |   * yyyy-MM-dd
      //          * times only, with an optional time zone appended
      //         |   * THH:mm
      //         |   * THH:mm:ss
      //         |   * THH:mm:ss.SSS
      //          * and "datetimes" which could be any combination of the above
      //
      //      timezones may be specified as Z (for UTC) or +/- followed by a time expression HH:mm
      //      Assumes the local time zone if not specified.  Does not validate.  Improperly formatted
      //      input may return null.  Arguments which are out of bounds will be handled
      //      by the Date constructor (e.g. January 32nd typically gets resolved to February 1st)
      //      Only years between 100 and 9999 are supported.
      //
      //   formattedString:
      //      A string such as 2005-06-30T08:05:00-07:00 or 2005-06-30 or T08:05:00

      // Modified to parse ISO822 style timezone offsets: (0500 instead of 05:00), which are still valid but not previous supported
      var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):?(\d{2}))|Z)?)?$/;

      return function(formattedString)
      {
         var match = isoRegExp.exec(formattedString);
         var result = null;

         if (match)
         {
            match.shift();
            if (match[1]){match[1]--;} // Javascript Date months are 0-based
            if (match[6]){match[6] *= 1000;} // Javascript Date expects fractional seconds as milliseconds

            result = new Date(match[0]||1970, match[1]||0, match[2]||1, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

            var offset = 0;
            var zoneSign = match[7] && match[7].charAt(0);
            if (zoneSign != 'Z')
            {
               offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
               if (zoneSign != '-')
               {
                  offset *= -1;
               }
            }
            if (zoneSign)
            {
               offset -= result.getTimezoneOffset();
            }
            if (offset)
            {
               result.setTime(result.getTime() + offset * 60000);
            }
         }

         return result; // Date or null
      };
   }();

   return fromISOString.apply(arguments.callee, arguments);
};

/**
 * Converts a JavaScript native Date object into a ISO8601-formatted string
 *
 * Original code:
 *    dojo.date.stamp.toISOString
 *    Copyright (c) 2005-2008, The Dojo Foundation
 *    All rights reserved.
 *    BSD license (http://trac.dojotoolkit.org/browser/dojo/trunk/LICENSE)
 *
 * @method Alfresco.thirdparty.toISO8601
 * @param dateObject {Date} JavaScript Date object
 * @param options {object} Optional conversion options
 *    zulu = true|false
 *    selector = "time|date"
 *    milliseconds = true|false
 * @return {string}
 * @static
 */
Alfresco.thirdparty.toISO8601 = function()
{
   var toISOString = function()
   {
      //   summary:
      //      Format a Date object as a string according a subset of the ISO-8601 standard
      //
      //   description:
      //      When options.selector is omitted, output follows [RFC3339](http://www.ietf.org/rfc/rfc3339.txt)
      //      The local time zone is included as an offset from GMT, except when selector=='time' (time without a date)
      //      Does not check bounds.  Only years between 100 and 9999 are supported.
      //
      //   dateObject:
      //      A Date object
      var _ = function(n){ return (n < 10) ? "0" + n : n; };

      return function(dateObject, options)
      {
         options = options || {};
         var formattedDate = [];
         var getter = options.zulu ? "getUTC" : "get";
         var date = "";
         if (options.selector != "time")
         {
            var year = dateObject[getter+"FullYear"]();
            date = ["0000".substr((year+"").length)+year, _(dateObject[getter+"Month"]()+1), _(dateObject[getter+"Date"]())].join('-');
         }
         formattedDate.push(date);
         if (options.selector != "date")
         {
            var time = [_(dateObject[getter+"Hours"]()), _(dateObject[getter+"Minutes"]()), _(dateObject[getter+"Seconds"]())].join(':');
            var millis = dateObject[getter+"Milliseconds"]();
            if (options.milliseconds === undefined || options.milliseconds)
            {
               time += "."+ (millis < 100 ? "0" : "") + _(millis);
            }
            if (options.zulu)
            {
               time += "Z";
            }
            else if (options.selector != "time")
            {
               var timezoneOffset = dateObject.getTimezoneOffset();
               var absOffset = Math.abs(timezoneOffset);
               time += (timezoneOffset > 0 ? "-" : "+") +
                     _(Math.floor(absOffset/60)) + ":" + _(absOffset%60);
            }
            formattedDate.push(time);
         }
         return formattedDate.join('T'); // String
      };
   }();

   return toISOString.apply(arguments.callee, arguments);
};


/**
 * Alfresco BaseService.
 *
 * @namespace Alfresco.service
 * @class Alfresco.service.BaseService
 */

/**
 * BaseService constructor.
 *
 * @return {Alfresco.service.BaseService} The new Alfresco.service.BaseService instance
 * @constructor
 */
Alfresco.service.BaseService = function BaseService_constructor()
{
   return this;
};

Alfresco.service.BaseService.prototype =
{
   /**
    * Generic helper method for invoking a Alfresco.util.Ajax.request() from a responseConfig object
    *
    * @method _jsonCall
    * @param method {string} The method for the XMLHttpRequest
    * @param url {string} The url for the XMLHttpRequest
    * @param dataObj {object} An object that will be transformed to a json string and put in the request body
    * @param responseConfig.successCallback {object} A success callback object
    * @param responseConfig.successMessage {string} A success message
    * @param responseConfig.failureCallback {object} A failure callback object
    * @param responseConfig.failureMessage {string} A failure message
    * @private
    */
   _jsonCall: function BaseService__jsonCall(method, url, dataObj, responseConfig)
   {
      responseConfig = responseConfig || {};
      Alfresco.util.Ajax.jsonRequest(
            {
               method: method,
               url: url,
               dataObj: dataObj,
               successCallback: responseConfig.successCallback,
               successMessage: responseConfig.successMessage,
               failureCallback: responseConfig.failureCallback,
               failureMessage: responseConfig.failureMessage,
               noReloadOnAuthFailure: responseConfig.noReloadOnAuthFailure || false
            });
   }
};

/**
 * Alfresco Preferences.
 *
 * @namespace Alfresco.service
 * @class Alfresco.service.Preferences
 */
(function()
{
   /**
    * Preferences constructor.
    *
    * @return {Alfresco.service.Preferences} The new Alfresco.service.Preferences instance
    * @constructor
    */
   Alfresco.service.Preferences = function Preferences_constructor()
   {
      Alfresco.service.Preferences.superclass.constructor.call(this);
      return this;
   };

   YAHOO.extend(Alfresco.service.Preferences, Alfresco.service.BaseService,
   {
      _prefs: null,

      /**
       * Gets a user specific property
       *
       * @method url
       * @return {string} The base url to the preference webscripts
       * @private
       */
      _url: function Preferences_url()
      {
         return Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(Alfresco.constants.USERNAME) + "/preferences";
      },

      /**
       * Get the complete copy of the currently cached user preferences. This value is set once per page load - a component
       * should retrieve this once onReady() but not assume the state is correct after that. The component should maintain
       * its own local copies of values as modified by set(), add() or remote() operations until the next page refresh.
       *
       * @method get
       */
      get: function Preferences_get()
      {
         if (this._prefs === null)
         {
            this._prefs = YAHOO.lang.JSON.parse(Alfresco.constants.USERPREFERENCES);
         }
         return this._prefs;
      },

      /**
       * Requests a fresh copy of a user specific property
       *
       * @method request
       * @param name {string} The name of the property to get, or null or no param for all
       * @param responseConfig {object} A config object with only success and failure callbacks and messages
       */
      request: function Preferences_request(name, responseConfig)
      {
         this._jsonCall(Alfresco.util.Ajax.GET, this._url() + (name ? "?pf=" + name : ""), null, responseConfig);
      },

      /**
       * Sets a user specific property
       *
       * @method set
       * @param name {string} The name of the property to set
       * @param value {object} The value of the property to set
       * @param responseConfig {object} A config object with only success and failure callbacks and messages
       */
      set: function Preferences_set(name, value, responseConfig)
      {
         var preference = Alfresco.util.dotNotationToObject(name, value);
         this._jsonCall(Alfresco.util.Ajax.POST, this._url(), preference, responseConfig);
      },

      /**
       * Updates a set of preferences
       *
       * @method update
       * @param value {object} The preferences to set
       * @param responseConfig {object} A config object with only success and failure callbacks and messages
       */
      update: function Preferences_set(preference, responseConfig)
      {
         this._jsonCall(Alfresco.util.Ajax.POST, this._url(), preference, responseConfig);
      },

      /**
       * Adds a value to a user specific property that is treated as a multi value.
       * Since arrays aren't supported in the webscript we store multiple values using a comma separated string.
       *
       * @method add
       * @param name {string} The name of the property to set
       * @param value {object} The value of the property to set
       * @param responseConfig {object} A config object with only success and failure callbacks and messages
       */
      add: function Preferences_add(name, value, responseConfig)
      {
         var n = name, v = value;
         var rc = responseConfig ? responseConfig : {};
         var originalSuccessCallback = rc.successCallback;
         rc.successCallback =
         {
            fn: function (response, obj)
            {
               // Make sure the original succes callback is used
               rc.successCallback = originalSuccessCallback;

               // Get the value for the preference name
               var preferences = Alfresco.util.dotNotationToObject(n, null);
               preferences = YAHOO.lang.merge(preferences, response.json);
               var values = Alfresco.util.findValueByDotNotation(preferences, n);

               // Parse string to array, add the value and convert to string again
               if (typeof values == "string" || values === null)
               {
                  var arrValues = values ? values.split(",") : [];
                  arrValues.push(v);

                  // Save preference with the new value
                  this.set(name, arrValues.join(","), rc);
               }
            },
            scope: this
         };
         this.request(name, rc);
      },

      favouriteDocumentOrFolder: function Preferences_favouriteDocumentOrFolder(node, responseConfig)
      {
         var name = node.isContainer ? "org.alfresco.share.folders.favourites" : "org.alfresco.share.documents.favourites";
         var n = "org.alfresco";
         var v = node.nodeRef;
         var rc = responseConfig ? responseConfig : {};
         var originalSuccessCallback = rc.successCallback;
         rc.successCallback =
         {
            fn: function (response, obj)
            {
               // Make sure the original succes callback is used
               rc.successCallback = originalSuccessCallback;

               // Get the value for the preference name
               var preferences = Alfresco.util.dotNotationToObject(name, null);
               preferences = YAHOO.lang.merge(preferences, response.json);
               var values = Alfresco.util.findValueByDotNotation(preferences, name);

               // Parse string to array, add the value and convert to string again
               if (typeof values == "string" || values === null)
               {
                  var arrValues = values ? values.split(",") : [];
                  arrValues.push(v);

                  if(node.isContainer)
                  {
                     var value =
                     {
                        org :
                        {
                           alfresco :
                           {
                              ext :
                              {
                                 folders :
                                 {
                                    favourites :
                                    {
                                    }
                                 }
                              },
                              share :
                              {
                                 folders :
                                 {
                                    favourites : arrValues.join(",")
                                 }
                              }
                           }
                        }
                     };
                     value.org.alfresco.ext.folders.favourites[v] =
                     {
                        createdAt: new Date()
                     };
                      
                     // Save preference with the new value
                     this.update(value, rc);
                  }
                  else
                  {
                     var value =
                     {
                        org :
                        {
                           alfresco :
                           {
                              ext :
                              {
                                 documents :
                                 {
                                    favourites :
                                    {
                                    }
                                 }
                              },
                              share :
                              {
                                 documents :
                                 {
                                    favourites : arrValues.join(",")
                                 }
                              }
                           }
                        }
                     };
                     value.org.alfresco.ext.documents.favourites[v] =
                     {
                        createdAt: new Date()
                     };

                     // Save preference with the new value
                     this.update(value, rc);
                  }
               }
            },
            scope: this
         };
         this.request(name, rc);
      },

      unFavouriteDocumentOrFolder: function Preferences_unFavouriteDocumentOrFolder(node, responseConfig)
      {
         var name = node.isContainer ? "org.alfresco.share.folders.favourites" : "org.alfresco.share.documents.favourites";
         var n = "org.alfresco";
         var v = node.nodeRef;
         var rc = responseConfig ? responseConfig : {};
         var originalSuccessCallback = rc.successCallback;
         rc.successCallback =
         {
            fn: function (response, obj)
            {
               // Make sure the original succes callback is used
               rc.successCallback = originalSuccessCallback;

               // Get the value for the preference name
               var preferences = Alfresco.util.dotNotationToObject(name, null);
               preferences = YAHOO.lang.merge(preferences, response.json);
               var values = Alfresco.util.findValueByDotNotation(preferences, name);

               // Parse string to array, add the value and convert to string again
               if (typeof values == "string")
               {
                  var arrValues = values ? values.split(",") : [];
                  arrValues = Alfresco.util.arrayRemove(arrValues, v);

                  if(node.isContainer)
                  {
                     var value =
                     {
                        org :
                        {
                            alfresco :
                            {
                                ext :
                                {
                                   folders :
                                   {
                                      favourites :
                                      {
                                      }
                                   }
                                },
                                share :
                                {
                                   folders :
                                   {
                                      favourites : arrValues.join(",")
                                   }
                                }
                            }
                        }
                     };
                     value.org.alfresco.ext.folders.favourites[v] =
                     {
                        createdAt: null
                     };
                      
                     // Save preference with the new value
                     this.update(value, rc);
                  }
                  else
                  {
                     var value =
                     {
                        org :
                        {
                            alfresco :
                            {
                                ext :
                                {
                                   documents :
                                   {
                                      favourites :
                                      {
                                      }
                                   }
                                },
                                share :
                                {
                                   documents :
                                   {
                                      favourites : arrValues.join(",")
                                   }
                                }
                             }
                         }
                     };
                     value.org.alfresco.ext.documents.favourites[v] =
                     {
                        createdAt: null
                     };

                     // Save preference with the new value
                     this.update(value, rc);
                  }
               }
            },
            scope: this
         };
         this.request(name, rc);
      },

      favouriteSite: function Preferences_favouriteSite(siteId, responseConfig)
      {
         var value =
         {
            org :
            {
               alfresco :
               {
                  ext :
                  {
                     sites :
                     {
                        favourites :
                        {
                        }
                     }
                  },
                  share :
                  {
                     sites :
                     {
                        favourites :
                        {
                        }
                     }
                  }
               }
            }
         };
         value.org.alfresco.share.sites.favourites[siteId] = true;
         value.org.alfresco.ext.sites.favourites[siteId] =
         {
            createdAt: new Date()
         };
              
         // Save preference with the new value
         this.update(value, responseConfig);
      },

      unFavouriteSite: function Preferences_unFavouriteSite(siteId, responseConfig)
      {
         var value =
         {
            org :
            {
               alfresco :
               {
                  ext :
                  {
                     sites :
                     {
                        favourites :
                        {
                        }
                     }
                  },
                  share :
                  {
                     sites :
                     {
                        favourites :
                        {
                        }
                     }
                  }
               }
            }
         };
         value.org.alfresco.share.sites.favourites[siteId] = false;
         value.org.alfresco.ext.sites.favourites[siteId] =
         {
            createdAt: null
         };
              
         // Save preference with the new value
         this.update(value, responseConfig);
      },
      
      /**
       * Removes a value from a user specific property that is treated as a multi value.
       * Since arrays aren't supported in the webscript we store multiple values using a comma separated string.
       *
       * @method remove
       * @param name {string} The name of the property to set
       * @param value {object} The value of the property to set
       * @param responseConfig {object} A config object with only success and failure callbacks and messages
       */
      remove: function Preferences_remove(name, value, responseConfig)
      {
         var n = name, v = value;
         var rc = responseConfig ? responseConfig : {};
         var originalSuccessCallback = rc.successCallback;
         rc.successCallback =
         {
            fn: function (response, obj)
            {
               // Make sure the original succes callback is used
               rc.successCallback = originalSuccessCallback;

               // Get the value for the preference name
               var preferences = Alfresco.util.dotNotationToObject(n, null);
               preferences = YAHOO.lang.merge(preferences, response.json);
               var values = Alfresco.util.findValueByDotNotation(preferences, n);

               // Parse string to array, remove the value and convert to string again
               if (typeof values == "string")
               {
                  var arrValues = values ? values.split(",") : [];
                  arrValues = Alfresco.util.arrayRemove(arrValues, v);

                  // Save preference without value
                  this.set(name, arrValues.join(","), rc);
               }
            },
            scope: this
         };
         this.request(name, rc);
      },

      /**
       *
       * @param dashlet
       * @param name
       */
      getDashletId: function Preferences_getDashletId(dashlet, name)
      {
         var siteId = (dashlet.options.siteId && dashlet.options.siteId != "") ? "." + dashlet.options.siteId : "",
            regionId = (dashlet.options.regionId && dashlet.options.regionId != "") ? "." + dashlet.options.regionId : "",
            constantPrefix = "org.alfresco.share.";

         return constantPrefix + name + ".dashlet" + regionId + siteId;
      }
   });
})();

/**
 * Alfresco Ratings Service.
 *
 * @namespace Alfresco.service
 * @class Alfresco.service.Ratings
 */
(function()
{
   /**
    * Ratings Service constructor.
    *
    * @param ratingScheme {string} Rating Scheme
    * @return {Alfresco.service.Ratings} The new Alfresco.service.Ratings instance
    * @constructor
    */
   Alfresco.service.Ratings = function Ratings_constructor(ratingScheme)
   {
      if (typeof ratingScheme === "undefined" || ratingScheme === null)
      {
         throw new Error("Mandatory ratingScheme parameter is missing.");
      }
      Alfresco.service.Ratings.superclass.constructor.call(this);

      this._ratingScheme = ratingScheme;
      return this;
   };

   /**
    * Augment prototype with out-of-the-box rating scheme constants
    */
   YAHOO.lang.augmentObject(Alfresco.service.Ratings,
         {
            LIKES: "likesRatingScheme",
            FIVE_STAR: "fiveStarRatingScheme"
         });

   YAHOO.extend(Alfresco.service.Ratings, Alfresco.service.BaseService,
         {
            /**
             * Rating scheme
             * @param _ratingScheme
             * @protected
             */
            _ratingScheme: null,

            /**
             * Gets a user specific property
             *
             * @method url
             * @param node {NodeRef} Node reference
             * @return {string} The base url to the ratings webscripts
             * @protected
             */
            _url: function Ratings_url(nodeRef)
            {
               return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/ratings";
            },

            /**
             * Requests a list of ratings for the specified NodeRef
             *
             * @method request
             * @param node {NodeRef} Node reference
             * @param responseConfig {object} A config object with only success and failure callbacks and messages
             */
            request: function Ratings_request(node, responseConfig)
            {
               this._jsonCall(Alfresco.util.Ajax.GET, this._url(node), null, responseConfig);
            },

            /**
             * Sets a user rating on the specified NodeRef
             *
             * @method set
             * @param node {NodeRef} Node reference
             * @param rating {number} The rating score to set
             * @param responseConfig {object} A config object with only success and failure callbacks and messages
             */
            set: function Ratings_set(node, rating, responseConfig)
            {
               this._jsonCall(Alfresco.util.Ajax.POST, this._url(node),
                     {
                        rating: rating,
                        ratingScheme: this._ratingScheme
                     }, responseConfig);
            },

            /**
             * Removes a user's rating from a node.
             *
             * @method remove
             * @param node {NodeRef} Node reference
             * @param responseConfig {object} A config object with only success and failure callbacks and messages
             */
            remove: function Ratings_remove(node, responseConfig)
            {
               this._jsonCall(Alfresco.util.Ajax.DELETE, this._url(node) + "/" + encodeURIComponent(this._ratingScheme), null, responseConfig);
            }
         });
})();

/**
 * Alfresco QuickShare  Service.
 *
 * @namespace Alfresco.service
 * @class Alfresco.service.QuickShare
 */
(function()
{
   /**
    * Ratings Service constructor.
    *
    * @return {Alfresco.service.QuickShare} The new Alfresco.service.QuickShare  instance
    * @constructor
    */
   Alfresco.service.QuickShare = function QuickShare_constructor()
   {
      return Alfresco.service.QuickShare.superclass.constructor.call(this);
   };

   YAHOO.extend(Alfresco.service.QuickShare, Alfresco.service.BaseService,
         {
            /**
             * Helper method for creating urls
             *
             * @method _url
             * @param action Part of the url
             * @param id {string} NodeRef or sharedId
             * @return {string} The base url to the ratings webscripts
             * @protected
             */
            _url: function QuickShare_url(action, id)
            {
               return Alfresco.constants.PROXY_URI + "api/internal/shared/" + action + "/" + id;
            },

            /**
             * Sets a user rating on the specified NodeRef
             *
             * @method set
             * @param node {NodeRef|string} Node reference
             * @param responseConfig {object} A config object with only success and failure callbacks and messages
             */
            share: function QuickShare_set(node, responseConfig)
            {
               this._jsonCall(Alfresco.util.Ajax.POST, this._url("share", (node + "").replace(":/", "")), {}, responseConfig);
            },

            /**
             * Stop the node from being share.
             *
             * @method unshare
             * @param sharedId {string} Id to the share to remove
             * @param responseConfig {object} A config object with only success and failure callbacks and messages
             */
            unshare: function QuickShare_remove(sharedId, responseConfig)
            {
               this._jsonCall(Alfresco.util.Ajax.DELETE, this._url("unshare", sharedId), null, responseConfig);
            }
         });
})();

/**
 * Manager object for managing adapters for HTML editors.
 *
 */
Alfresco.util.RichEditorManager = (function()
{
   var editors = [];
   return (
   {
      /**
       * Store a reference to a specified editor
       *
       * @method addEditor
       * @param editorName {string} Name of html editor to use, including namespace eg YAHOO.widget.SimpleEditor
       * @param editor {object} reference to editor
       */
      addEditor: function (editorName, editor)
      {
         editors[editorName] = editor;
      },

      /**
       * Retrieve a previously added editor
       *
       * @method getEditor
       * @param editorName {string}  name of editor to retrieve
       * @return {object} Returns a reference to specified editor.
       */
      getEditor: function (editorName)
      {
         if (editors[editorName])
         {
            return editors[editorName];
         }
         return null;
      }
   });
})();

/**
 * @module RichEditor
 * Factory object for creating instances of adapters around
 * specified editor implementations. Eg tinyMCE/YUI Editor. Also augments
 * created editor with YAHOO.util.EventProvider.
 * Editor can be initialized instantly by passing in 'id' and 'config' parameters or later
 * on by calling init() method.
 *
 * Fires editorInitialized event when html editor is initialized.
 *
 * @param editorName {String} Name of editor to use. Must be same as one registered with
 * Alfresco.util.RichEditManager.
 * @param id {String} Optional Id of textarea to turn into rich text editor
 * @param config {String} Optional config object literal to use to configure editor.
 */
Alfresco.util.RichEditor = function(editorName,id,config)
{
   var editor = Alfresco.util.RichEditorManager.getEditor(editorName);
   if (editor)
   {
      var ed = new editor();

      YAHOO.lang.augmentObject(ed,
            {
               unsubscribe: function()
               {
               },

               subscribe : function(event, fn, scope)
               {
                  var edtr = ed.getEditor();
                  //yui custom events
                  if (edtr.subscribe)
                  {
                     edtr.subscribe(event, fn, scope, true);
                  }
                  else if (edtr[event])
                  {
                     edtr[event].add(function()
                     {
                        fn.apply(scope,arguments);
                     });
                  }
                  YAHOO.Bubbling.on(event, fn, scope);
               },

               on: function(event, fn, scope)
               {
                  YAHOO.Bubbling.on(event, fn, scope);
               }
            });

      if (id && config)
      {
         // Check we can support the requested language
         if (config.language)
         {
            var langs = Alfresco.constants.TINY_MCE_SUPPORTED_LOCALES.split(","),
               lang = "en",
               configLangOnly = config.language.substring(0,2).toLowerCase();

            for (var i = 0, j = langs.length; i < j; i++)
            {
               // This checks the full local string (e.g. "en_US")
               if (langs[i] === config.language)
               {
                  lang = config.language;
                  break;
               }
               // If the above doesn't match, then perform less strict match (e.g. "en") for ACE-3502.
               else if (langs[i] === configLangOnly)
               {
                  lang = configLangOnly;
                  break;
               }
            }
            config.language = lang;
         }

         // MNT-11113
         config.theme_advanced_resize_horizontal = false;

         ed.init(id,config);
      }
      return ed;
   }
   return null;
};

/**
 * YUI DataTable rendering loop size constant
 */
Alfresco.util.RENDERLOOPSIZE = 25;


/**
 * The Alfresco.component.Base class provides core component functions
 * and is intended to be extended by other UI components, rather than
 * instantiated on it's own.
 */
(function()
{
   /**
    * Alfresco.component.Base constructor.
    *
    * @param name {String} The name of the component
    * @param id {String} he DOM ID of the parent element
    * @param components {Array} Optional: Array of required YAHOO
    * @return {object} The new instance
    * @constructor
    */
   Alfresco.component.Base = function Alfresco_component_Base(name, id, components)
   {
      // Mandatory properties
      this.name = (typeof name == "undefined" || name === null) ? "Alfresco.component.Base" : name;
      this.id = (typeof id == "undefined" || id === null) ? Alfresco.util.generateDomId() : id;

      // Initialise default prototype properties
      this.options = Alfresco.util.deepCopy(this.options);
      this.widgets = {};
      this.modules = {};
      this.services = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components if req'd
      if (YAHOO.lang.isArray(components))
      {
         Alfresco.util.YUILoaderHelper.require(components, this.onComponentsLoaded, this);
      }
      else
      {
         this.onComponentsLoaded();
      }

      return this;
   };

   Alfresco.component.Base.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       * @default {}
       */
      options: {},

      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       * @default null
       */
      widgets: null,

      /**
       * Object container for storing module instances.
       *
       * @property modules
       * @type object
       * @default null
       */
      modules: null,

      /**
       * Object container for storing service instances.
       *
       * @property services
       * @type object
       */
      services: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {object} returns 'this' for method chaining
       */
      setOptions: function Base_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {object} returns 'this' for method chaining
       */
      setMessages: function Base_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function Base_onComponentsLoaded()
      {
         if (this.id !== "null")
         {
            YUIEvent.onContentReady(this.id, this.onReadyWrapper, this, true);
         }
      },

      /**
       * Destroy method - destroy widgets, dereference modules & services
       *
       * @method destroy
       */
      destroy: function Base_destroy()
      {
         var index, purge;

         // Destroy widgets
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               try
               {
                  purge = this.widgets[index];
                  if (typeof purge.destroy == "function")
                  {
                     purge.destroy();
                  }
               }
               catch (e)
               {
                  // Ignore
               }

               delete this.widgets[index];
            }
         }

         // Modules
         for (index in this.modules)
         {
            if (this.modules.hasOwnProperty(index))
            {
               delete this.modules[index];
            }
         }

         // Services
         for (index in this.services)
         {
            if (this.services.hasOwnProperty(index))
            {
               delete this.services[index];
            }
         }
      },

      /**
       * Calls the onReady method and adds default event handling to correctly marked-up anchor tags
       *
       * @method onReadyWrapper
       */
      onReadyWrapper: function Base_onReadyWrapper()
      {
         // Call component's onReady method
         if (this.onReady && this.onReady.call)
         {
            this.onReady.call(this);
         }
         this.createYUIButtons();
         this.attachLinkClickListeners();
      },

      /**
       * Will be called after the components onready method adn will search for buttons declared as alfresco-buttons.
       * Convert these to yui push buttons and make them either fire an event or call a method on the component.
       * The created yui buttons will be placed inside the components "widgets" attribute with the method as the key.
       *
       * Examples #1: Will call the client side component onRenameClick method with the nodeRef as the first parameter and the button itself as the second:
       * <button type="alfresco-button" name=".onRenameClick" value="${nodeRef}">${msg("button.rename")}</button>
       *
       * Exmaple #2: Will make the client side component fire an event name "metaDataRefresh" with the nodeRef as the value:
       * <button type="alfresco-button" name="@metaDataRefresh" value="${nodeRef}">${msg("button.metaDataRefresh")}</button>
       *
       * The callback handler specification is:
       *    callbackFunctionName: function(value, yuiButton) { ... }
       * e.g.
       *    onRenameClick: function(nodeRef, button) { ... }
       *
       * The name of the button will also be added as a css class for styling:
       * .onDownloadDocumentClick button {
       *    background: transparent url(rename-16.png) no-repeat scroll 12px 4px;
       *    padding-left: 32px;
       *
       * @method createYUIButtons
       */
      createYUIButtons: function Base_createYUIButtons()
      {
         var buttons = YUISelector.query("button.alfresco-button", this.id),
               button,
               yuiButton,
               name,
               value;

         for (var i = 0, il = buttons.length; i < il; i++)
         {
            button = buttons[i];
            name = button.getAttribute("name");
            if (name)
            {
               yuiButton = Alfresco.util.createYUIButton(this, null, function(n, v)
               {
                  var name = n, value = v;
                  return function()
                  {
                     this.invokeAction(name, value, this.widgets[name])
                  };
               }(name, button.getAttribute("value")), {}, buttons[i]);
               YAHOO.util.Dom.addClass(yuiButton.get("element"), name.substring(1));
               this.widgets[name.substring(1)] = yuiButton;
            }
         }
      },

      /**
       * Add default anchor callbacks for this component based on its id so they are instance specific.
       * <a href="#" name="{.callback/@event name}" class="{componentId}" rel="{callback parameter/event value}">{label}</a>
       *
       * Examples #1: Will call the client side component onRenameClick method with the nodeRef as the first parameter and the link as the second:
       * <a href="#" name".onRenameClick" class="${args.htmlid?js_string}" rel="${nodeRef}">${msg("link.rename")}</a>
       *
       * Exmaple #2: Will make the client side component fire an event name "metaDataRefresh" with the nodeRef as the value:
       * <a href="#" name"@metaDataRefresh" class="${args.htmlid?js_string}" rel="${nodeRef}">${msg("link.metaDataRefresh")}</a>
       *
       * The callback handler specification is:
       *    {callback function name}: function(p_parameter, p_anchor) { ... }
       * e.g.
       *    onRenameClick: function(pageId, anchor) { ... }
       *
       * @method attachLinkClickListeners
       */
      attachLinkClickListeners: function Base_attachLinkClickListeners()
      {
         var fnActionHandler = function Base_attachLinkClickListeners_fnActionHandler(layer, args)
         {
            var anchor = args[1].anchor,
               name = anchor.getAttribute("name") || anchor.name,
                  rel = anchor.getAttribute("rel");

            if (this.invokeAction(name, rel, anchor))
            {
               YUIEvent.preventDefault(args[1]);
            }
            return true;
         };

         // Force the action to be applied (3rd parameter) to allow for component refresh use cases
         YAHOO.Bubbling.addDefaultAction(this.id, this.bind(fnActionHandler), true);
      },

      /**
       * Helper method to invoke a method or fire an event based on the action.
       *
       * @method invokeAction
       * @param action {string} If it starts with "." it will be treated as a method,
       *                        if starts with "@" it will be treated as an event.
       * @param value {String} .
       * @param el {HTMLElement|YAHOO.util.Button} The element/widget that was clicked/triggered the action.
       */
      invokeAction: function Base_invokeAction(action, value, el)
      {
         var method = action.match(/^\.[\w]+$/) ? action.substring(1) : null,
               event = action.match(/^\@[\w]+$/) ? action.substring(1) : null;
         if (YAHOO.lang.isFunction(this[method]))
         {
            this[method].call(this, value, el);
            return true;
         }
         else if (event)
         {
            this.fire(event, value);
            return true;
         }
      },

      /**
       * Gets a custom message
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       */
      msg: function Base_msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      },

      /**
       * Asserts a method always is called with this component's scope
       *
       * @method bind
       * @param method {function} The function to bind to the scope of this component
       * @return {function} The function bound to a different scope
       */
      bind: function Base_bind(method)
      {
         return Alfresco.util.bind(method, this);
      },

      /**
       * Fires an event
       *
       * @method fire
       * @param event {String} The event name
       * @param value {Object} The event value
       */
      fire: function Base_fire(event, value)
      {
         value.eventGroup = this;
         YAHOO.Bubbling.fire(event, value)
      },

      /**
       * Call this to refresh/reload the component.
       * Note that this component's javascript instance will be unregistered and replaced by a new.
       *
       * @method refresh
       * @param webscript {String} The url to this component
       */
      refresh: function Base_refresh(webscript)
      {
         var url = Alfresco.util.combinePaths(Alfresco.constants.URL_SERVICECONTEXT, YAHOO.lang.substitute(webscript, this.options, function(p_key, p_value, p_meta)
         {
            return typeof p_value === "boolean" ? p_value.toString() : p_value;
         }));
         url += (url.indexOf("?") == -1 ? "?" : "&") + "htmlid=" + this.id;
         Alfresco.util.Ajax.request(
               {
                  url: url,
                  successCallback:
                  {
                     fn: this.onComponentLoaded,
                     obj: this,
                     scope: this
                  },
                  scope: this
               });
      },

      /**
       * Called when this component has been refreshed and the new markup has been loaded.
       * Will replace the old markup with the new and unregister the old component instance.
       *
       * @method onComponentLoaded
       * @param response
       */
      onComponentLoaded: function Base_onComponentLoaded(response)
      {
         // Clean new markup from scripts so it doesn't instantiate the new component instance yet
         var result = Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText);

         // Replace the old markup with the new
         YAHOO.util.Dom.get(this.id).innerHTML = result[0];

         // Remove this instance of this component before a new one is created
         Alfresco.util.ComponentManager.unregister(this);
         window.setTimeout(result[1], 0);
      }
   };
})();

/**
 * The Alfresco.component.BaseFilter class provides core functions for a "twister-style" filter.
 * It can be extended by other UI filters, or simply instantiated on it's own.
 */
(function()
{
   /**
    * Alfresco.component.BaseFilter constructor.
    *
    * @param name {String} The name of the component
    * @param id {String} he DOM ID of the parent element
    * @param components {Array} Optional: Array of required YAHOO
    * @return {object} The new instance
    * @constructor
    */
   Alfresco.component.BaseFilter = function(name, id, components)
   {
      Alfresco.component.BaseFilter.superclass.constructor.apply(this, arguments);

      this.filterName = this.name.substring(this.name.lastIndexOf(".") + 1);
      this.controlsDeactivated = false;
      this.uniqueEventKey = Alfresco.util.generateDomId(null, "filter");
      this.SELECTED_CLASS = "selected";

      // Decoupled event listeners
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("hideFilter", this.onHideFilter, this);

      return this;
   };

   YAHOO.extend(Alfresco.component.BaseFilter, Alfresco.component.Base,
         {
            /**
             * Filter name, automatically generated from component name.
             *
             * @property filterName
             * @type {string}
             */
            filterName: null,

            /**
             * Selected filter.
             *
             * @property selectedFilter
             * @type {element}
             */
            selectedFilter: null,

            /**
             * Flag to indicate whether all controls are deactivated or not.
             *
             * @property controlsDeactivated
             * @type {boolean}
             */
            controlsDeactivated: null,

            /**
             * Unique event key used to hook DOM clicks on filters
             *
             * @property uniqueEventKey
             * @type {string}
             */
            uniqueEventKey: null,

            /**
       * The css class to apply on the selected filter
       *
       * @property SELECTED_CLASS
       * @type {string}
       * @default "selected"
       */
      SELECTED_CLASS: "selected",

      /**
       * The css class to to apply on disabled filter
       *
       * @property DISABLED_CLASS
       * @type {string}
       * @default "disabled"
       */
      DISABLED_CLASS: "disabled",

      /**
       * Css class to use for selected filter
       *
       * @method setSelectedClass
       * @param p_sSelectedClass {string} Css class to use for selected filter
       */
      setSelectedClass: function BaseFilter_setSelectedClass(p_sSelectedClass)
      {
         this.SELECTED_CLASS = p_sSelectedClass;
      },

      /**
       * Css class to use for disabled filter
       *
       * @method setDisabledClass
       * @param p_sDisabledClass {string} Css class to use for disabled filter
       */
      setDisabledClass: function BaseFilter_setSelectedClass(p_sDisabledClass)
      {
         this.DISABLED_CLASS = p_sDisabledClass;
      },

      /**
             * Set the filterId(s) this filter will be owning.
             *
             * @method setFilterIds
             * @param p_aFilterIds {array} Array of filterIds this filter will be owning
             */
            setFilterIds: function BaseFilter_setFilterIds(p_aFilterIds)
            {
               // Register the filter
               Alfresco.util.FilterManager.register(this.name, p_aFilterIds);
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Component initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function BaseFilter_onReady()
            {
               var me = this,
                     headers = YUISelector.query("h2", this.id);

               if (YAHOO.lang.isArray(headers))
               {
                  // Create twister from the first H2 tag found by the query
                  Alfresco.util.createTwister(headers[0], this.filterName);
               }

               // Add the unique event key into the filter link nodes
               var filterLinks = YUISelector.query("li a", this.id);
               for (var i = 0, ii = filterLinks.length; i < ii; i++)
               {
                  YUIDom.addClass(filterLinks[i], this.uniqueEventKey);
               }

               // ...and attach a global listener to the unique event key
               YAHOO.Bubbling.addDefaultAction(this.uniqueEventKey, function BaseFilter_filterAction(layer, args)
               {
                  var anchor = args[1].anchor,
                        owner = YAHOO.Bubbling.getOwnerByTagName(anchor, "span");

                  if ((owner !== null) && !me.controlsDeactivated)
                  {
                     var href = anchor.getAttribute("href", 2);
                     // Check the filter isn't a link (yes wiki, we're all looking at you)
                     // Note: IE6 (and IE7 for DHTML operations) just doesn't get it, even with the second parameter on getAttribute()
                     if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 8)
                     {
                        var d = href.length - 1;
                        if (d < 0 || href.lastIndexOf("#") != d)
                        {
                           return false;
                        }
                     }
                     else if (anchor.getAttribute("href", 2).length > 1)
                     {
                        return false;
                     }

                     var filterId = owner.className,
                           filterData = anchor.rel,
                           filterObj =
                           {
                              filterOwner: me.name,
                              filterId: filterId
                           };

                     if (Alfresco.util.isValueSet(filterData))
                     {
                        filterObj.filterData = filterData;
                     }

                     YAHOO.Bubbling.fire("changeFilter", filterObj);

                     // If a function has been provided which corresponds to the filter Id, then call it
                     if (typeof me[filterId] == "function")
                     {
                        me[filterId].call(me);
                     }

                     args[1].stop = true;
                  }

                  return true;
               });
            },

            /**
             * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
             * Disconnected event handlers for inter-component event notification
             */

            onHideFilter: function()
            {
               var filters = YUISelector.query("a", this.id);
               for (var i = 0, ii = filters.length; i < ii; i++)
               {
                  YUIDom.addClass(filters[i], "hidden");
               }
            },

            /**
             * Fired when the currently active filter has changed
             *
             * @method onFilterChanged
             * @param layer {string} the event source
             * @param args {object} arguments object
             */
            onFilterChanged: function BaseFilter_onFilterChanged(layer, args)
            {
               var obj = Alfresco.util.cleanBubblingObject(args[1]),
                     filterFound = false;

               if ((obj !== null) && (obj.filterId !== null))
               {
                  obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

                  if (obj.filterOwner == this.name)
                  {
                     // Remove the old highlight, as it might no longer be correct
                     if (this.selectedFilter !== null)
                     {
                  YUIDom.removeClass(this.selectedFilter, this.SELECTED_CLASS);
                     }

                     // Need to find the selectedFilter element, from the current filterId
                     var candidates = YUISelector.query("." + obj.filterId, this.id);
                     if (candidates.length == 1)
                     {
                        // This component now owns the active filter
                        this.selectedFilter = candidates[0].parentNode;
                  YUIDom.addClass(this.selectedFilter, this.SELECTED_CLASS);
                        filterFound = true;
                     }
                     else if (candidates.length > 1)
                     {
                        if (obj.filterData.indexOf("]") !== -1 || obj.filterData.indexOf(",") !== -1)
                        {
                           /**
                            * Special case handling, as YUI Selector doesn't work with "]" or "," within an attribute
                            * See http://yuilibrary.com/projects/yui2/ticket/1978321 ("]" issue)
                            * and comment in selector-debug.js ("," issue)
                            */
                           for (var i = 0, ii = candidates.length; i < ii; i++)
                           {
                              if (candidates[i].firstChild.rel == obj.filterData)
                              {
                                 // This component now owns the active filter
                                 this.selectedFilter = candidates[i].parentNode;
                           YUIDom.addClass(this.selectedFilter, this.SELECTED_CLASS);
                                 filterFound = true;
                              }
                           }
                        }
                        else
                        {
                     candidates = YUISelector.query("a[rel=\"" + obj.filterData.replace("'", "\'") + "\"]", this.id);
                           if (candidates.length == 1)
                           {
                              // This component now owns the active filter
                              this.selectedFilter = candidates[0].parentNode.parentNode;
                        YUIDom.addClass(this.selectedFilter, this.SELECTED_CLASS);
                              filterFound = true;
                           }
                        }
                     }

                     if (!filterFound)
                     {
                        // Optional per-filter "filterId not found" handling
                        this.handleFilterIdNotFound(obj);
                     }
                  }
                  else
                  {
                     // Currently filtering by something other than this component
                     if (this.selectedFilter !== null)
                     {
                  YUIDom.removeClass(this.selectedFilter, this.SELECTED_CLASS);
                     }
                  }
               }
            },

            /**
             * Called if this filter is the owner, but the filterId could be found in the DOM
             *
             * @method handleFilterIdNotFound
             * @param filter {object} New filter trying to be set
             */
            handleFilterIdNotFound: function BaseFilter_handleFilterIdNotFound(filter)
            {
               // Default handling: no implementation
            },

            /**
             * Deactivate All Controls event handler
             *
             * @method onDeactivateAllControls
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onDeactivateAllControls: function BaseFilter_onDeactivateAllControls(layer, args)
            {
               this.controlsDeactivated = true;
               var filters = YUISelector.query("a." + this.uniqueEventKey, this.id);
               for (var i = 0, ii = filters.length; i < ii; i++)
               {
                  YUIDom.addClass(filters[i], this.DISABLED_CLASS);
               }
            }
         });
})();

/**
 * FormManager component.
 *
 * Component that helps the management on a form on a page.
 *
 * Set options to customise:
 *  - the label of the submit button
 *  - the failure message if form submission failed
 *  - the page that the user shall visit after the form was submitted or cancelled
 *
 * ...or override it's onFormContentReady method to perform additional customisation.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.FormManager
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * FormManager constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.FormManager} The new FormManager instance
    * @constructor
    */
   Alfresco.component.FormManager = function FormManager_constructor(htmlId, components)
   {
      Alfresco.component.FormManager.superclass.constructor.call(this, "Alfresco.component.FormManager", htmlId, components);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);

      return this;
   };

   YAHOO.extend(Alfresco.component.FormManager, Alfresco.component.Base,
         {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:
            {
               /**
                * Failure message key.
                *
                * @property failureMessageKey
                * @type string
                */
               failureMessageKey: "message.failure",

               /**
                * Failure message key.
                *
                * @property submitButtonMessageKey
                * @type string
                */
               submitButtonMessageKey: "button.save",

               /**
                * The url to always take the user to if the form was submitted even if another url was visited just before
                * the form.
                *
                * @property submitUrl
                * @type string
                */
               submitUrl: null,

               /**
                * The url to always take the user to if the form was cancelled even if another url was visited just before
                * the form.
                *
                * @property cancelUrl
                * @type string
                */
               cancelUrl: null,

               /**
                * The url to dispatch the user to if no submit or cancel url was provided and the page visited just before
                * the form couldn't be resolved.
                *
                * @property defaultUrl
                * @type string
                */
               defaultUrl: null
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function FormManager_onReady()
            {

            },

            /**
             * Event handler called when the "formContentReady" event is received
             */
            onFormContentReady: function FormManager_onFormContentReady(layer, args)
            {
               // change the default 'Submit' label to be 'Save'
               var submitButton = args[1].buttons.submit;
               submitButton.set("label", this.msg(this.options.submitButtonMessageKey));

               // add a handler to the cancel button
               var cancelButton = args[1].buttons.cancel;
               if (cancelButton)
               {
                  cancelButton.addListener("click", this.onCancelButtonClick, null, this);
               }
            },

            /**
             * Event handler called when the "beforeFormRuntimeInit" event is received
             */
            onBeforeFormRuntimeInit: function FormManager_onBeforeFormRuntimeInit(layer, args)
            {
               args[1].runtime.setAJAXSubmit(true,
                     {
                        successCallback:
                        {
                           fn: this.onFormSubmitSuccess,
                           scope: this
                        },
                        failureCallback:
                        {
                           fn: this.onFormSubmitFailure,
                           scope: this
                        }
                     });
            },

            /**
             * Handler called when the form was submitted successfully
             *
             * @method onFormSubmitSuccess
             * @param response The response from the submission
             */
            onFormSubmitSuccess: function FormManager_onFormSubmitSuccess(response)
            {
               this.navigateForward(true);
            },

            /**
             * Handler called when the form submitoperation failed
             *
             * @method onMetadataUpdateFailure
             * @param response The response from the submission
             */
            onFormSubmitFailure: function FormManager_onFormSubmitFailure(response)
            {
               var failureMsg = null;
               if (response.json && response.json.message && response.json.message.indexOf("Failed to persist field 'prop_cm_name'") !== -1)
               {
                    failureMsg = this.msg("message.details.failure.name");
               }
               else if (response.json && response.json.message && response.json.message.indexOf("PropertyValueSizeIsMoreMaxLengthException") !== -1)
               {
                    failureMsg = this.msg("message.details.failure.more.max.length");
               }
               else if (response.json && response.json.message && response.json.message.indexOf("org.alfresco.error.AlfrescoRuntimeException") == 0)
               {  
                  var split =  response.json.message.split(/(?:org.alfresco.error.AlfrescoRuntimeException:\s*\d*\s*)/ig);
                  if (split && split[1])
                  {
                        failureMsg = split[1];
                  }
               }
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: this.msg(this.options.failureMessageKey),
                  text: failureMsg ? failureMsg : (response.json && response.json.message ? response.json.message : this.msg("message.details.failure"))
               });
            },

            /**
             * Called when user clicks on the cancel button.
             *
             * @method onCancelButtonClick
             * @param type
             * @param args
             */
            onCancelButtonClick: function FormManager_onCancel(type, args)
            {
               this.navigateForward(false);
            },

            /**
             * Override this method to and return true for urls that matches pages on your site that is considered to be
             * pages that use ajax states (places a "#" to add runtime values to the browser location bar).
             * The browser will do a history.go(-1) for these pages so that the these pages gets a chance of loading the state
             * the page had before this form was visited.
             *
             * @method pageUsesAjaxState
             * @param url
             * @return {boolean} True if the url is recognised as a page that uses ajax states (adds values after "#" on the url)
             */
            pageUsesAjaxState: function FormManager_pageUsesAjaxState(url)
            {
               return false;
            },

            /**
             * MNT-11418
             * Override this method to and return true  if Crome rewrites "document.referrer" property at
             * page reloading via code: document.location.reload().
             * This function tries to detect this behavior on "tasks-edit" page
             *
             * @method isCromeRedirectToTheSamePage
             * @param url
             * @return {boolean} True if the url is the same as current, it is a "tasks-edit" page and browser is Chrome
             */
            isCromeRedirectToTheSamePage: function FormManager_isCromeRedirectToTheSamePage(url)
            {
               return false;
            },

            /**
             * Override this method to make the user visit this url if no preferred urls was given for a form and
             * there was no page visited before the user came to the form page.
             *
             * @method getSiteDefaultUrl
             * @return {string} The url to make the user visit if no other alternatives have been found
             */
            getSiteDefaultUrl: function FormManager_getSiteDefaultUrl()
            {
               return null;
            },

            /**
             * Decides to which page we shall navigate after form submission or cancellation
             *
             * @method navigateForward
             * @param submitted {boolean} True if the form was submitted successfully
             */
            navigateForward: function FormManager_navigateForward(submitted)
            {
               if(submitted && this.options.submitUrl)
               {
                  /**
                   * The form was submitted and this form wants the user to always be taken to a specific page after this
                   * form has been submitted.
                   */
                  document.location.href = this.options.submitUrl;
               }
               else if(!submitted && this.options.cancelUrl)
               {
                  /**
                   * The form was cancelled and this form wants the user to always be taken to a specific page after this
                   * form has been cancelled.
                   */
                  document.location.href = this.options.cancelUrl;
               }
               else if (document.referrer)
               {
                  // No specific urls have been provided to navigate to, lets look at the previous page url if present
                  if (this.pageUsesAjaxState(document.referrer))
                  {
                     /**
                      * The previous page is considered to be a page that uses ajax states (appends "#" on the url)
                      * Since everything after the "#" isn't included in the value of document.referrer we must use
                      * history.go(-1) so the page can restore to its previous ajax state.
                      */
                     history.go(-1);
                  }
                  else if (history.state && history.state.previous_url && this.isCromeRedirectToTheSamePage(document.referrer))
                  {
                     document.location.href = history.state.previous_url;
                  }
                  else if (document.location.href == document.referrer)
                  {
                     if (this.options.defaultUrl)
                     {
                        /**
                         * Now we know that there was no previous page, lets use the default url that was provided for this form
                         */
                        document.location.href = this.options.defaultUrl;
                     }
                     else
                     {
                        // What a sad form, fallback to use the sites default url if provided, otherwise assume the context will work
                        document.location.href = this.getSiteDefaultUrl() || Alfresco.constants.URL_CONTEXT;
                     }
                  }
                  else
                  {
                     /**
                      * The page is not considered be a page that uses ajax states, which means we have the complete url in
                      * the value of document.referrer, therefore we navigate to it since that increases the chance of the
                      * data being refreshed on the page if it the data is displayed in the html code
                      * rather than coming from a http request.
                      */
                     document.location.href = document.referrer;
                  }
               }
               else if (history.length > 1)
               {
                  /**
                   * The document.referrer wasn't available, either because there was no previous page (because the user
                   * navigated directly to the page) or because the referrer header has been blocked.
                   * So instead we'll use the browser history, unfortunately with increased chance of displaying stale data
                   * since the page most likely won't be refreshed.
                   */
                  history.go(-1);
               }
               else if (this.options.defaultUrl)
               {
                  /**
                   * Now we know that there was no previous page, lets use the default url that was provided for this form
                   */
                  document.location.href = this.options.defaultUrl;
               }
               else
               {
                  // What a sad form, fallback to use the sites default url if provided, otherwise assume the context will work
                  document.location.href = this.getSiteDefaultUrl() || Alfresco.constants.URL_CONTEXT;
               }
            }
         });
})();

/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

/**
 * Alfresco.util.DataTable is a helper class for easily creating a YUI DataTable with the following (optionally) added
 * functionality that uses a lot of default values to to minimize configuration:
 *
 * - DataTable
 *   * Sets default alfresco empty, loading and error messages
 *   * Auto sets the width for columns that has width set in columnDefinition
 *   * (Sorting support is not yet implemented)
 * - Paginator with:
 *   * Uses Alfresco default pagination templates
 *   * Urls created with default alfresco paging parameters
 *   * PayLoad parsing form the response
 * - Browser History Manager
 *   * Creates hidden element (and iframe for IE) automatically
 *   * Loads state form url and fires from "filter" state and uses "paging" state to create paginated datasource url
 * - Filters
 *   - Listens for "changeFilter" events, puts them as browser history state and reloads datatable when the state has changed
 *   - Asks callback (dataSource.filterResolver) for url parameters that represent the current filter to create the datasource url
 *
 * To see an example of a DataTable:
 *  - with filters, pagination and browser history state is created, visit task-list.js
 *  - without filters and browser history state BUT with a paginator only displaying the no of results, visit my-tasks.js
 *
 * Note!
 * - ALL usual YUI DataTable, DataSource & Paginator constructor arguments may be provided to override the default ones set by YUI and this component.
 * - If a WebScript REST API call doesn't use skipCount & maxItems in its urls:
 *   * Pass in a url resolver function to "dataSource.pagingResolver"
 *   * Inform the paginator how the payload shall be parsed by setting values in "dataSource.config.responseSchema.metaFields"
 *   * Note that only "index based" pagination is supported currently not "page based".
 * - If no paginator shall be created, simply omit it.
 * - If you want to instruct the datatable to load something without pagination use load(url)
 *
 * @namespace Alfresco
 * @class Alfresco.util.DataTable
 */
(function()
{

   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event;

   /**
    *
    * @param c The config object               {Object} (Required)
    *
    * @param c.dataSource                      {Object} (Required)
    * @param c.dataSource.url                  {String} (Required) 1st argument in YAHOO.util.DataSource constructor
    * @param c.dataSource.config               {Object} (Required) 2nd argument in YAHOO.util.DataSource constructor
    * @param c.dataSource.config.responseType                                     {String} (Optional) Default: YAHOO.util.DataSource.TYPE_JSON
    * @param c.dataSource.config.responseSchema                                   {Object} (Optional)
    * @param c.dataSource.config.responseSchema.resultsList                       {String} (Optional) Default "data"
    * @param c.dataSource.config.responseSchema.fields                            {Array}  (Optional) Default null
    * @param c.dataSource.config.responseSchema.metaFields                        {Object} (Optional)
    * @param c.dataSource.config.responseSchema.metaFields.paginationRecordOffset {String} (Optional) Default: "paging.skipCount"
    * @param c.dataSource.config.responseSchema.metaFields.paginationRowsPerPage  {String} (Optional) Default: "paging.maxItems"
    * @param c.dataSource.config.responseSchema.metaFields.totalRecords           {String} (Optional) Default: "paging.totalItems"
    * @param c.dataSource.doBeforeParseData     {Function} (Optional) Will be called by the datasource before the response is parsed
    * @param c.dataSource.defaultFilter         {Object}   (Optional) If no filter is present this is the one that will get fired
    * @param c.dataSource.initialParameters     {String}   (Optional) Initial parameters to append to request url
    * @param c.dataSource.pagingResolver        {function} (Optional) Default: this._defaultPagingResolver;
    * @param c.dataSource.filterResolver        {function} (Optional) Will be called, if present, to resolve a "filter" object to url parameters to add to the datasource url.
    *
    * @param c.dataTable                        {Object} (Required)
    * @param c.dataTable.container              {String|HTMLElement} (Required) 1st argument in YAHOO.widget.DataTable constructor
    * @param c.dataTable.columnDefinitions      {Array}              (Required) 2nd argument in YAHOO.widget.DataTable constructor
    * @param c.dataTable.config                 {Object}  (Optional)
    * @param c.dataTable.config.dynamicData     {boolean} (Optional) Default: true,
    * @param c.dataTable.config.initialLoad     {boolean} (Optional) Default: false
    * @param c.dataTable.config.MSG_EMPTY       {String}  (Optional) Default: Alfresco.util.message("message.datatable.empty")
    * @param c.dataTable.config.MSG_ERROR       {String}  (Optional) Default: Alfresco.util.message("message.datatable.error")
    * @param c.dataTable.config.MSG_LOADING     {String}  (Optional) Default: Alfresco.util.message("message.datatable.loading")
    * @param c.dataTable.config.className       {String}  (Optional) Default: "alfresco-datatable" Default class will hide column headers and cell borders
    *
    * @param c.paginator                              {Object}  (Optional)
    * @param c.paginator.history                      {Boolean} (Optional) Default: true. Will make the paginator use the browser history
    * @param c.paginator.hide                         {Boolean} (Optional) Default: true. Will make the paginator hide if there is not enough results to motivate paging
    * @param c.paginator.config                       {Object}  (Optional) 1st argument in YAHOO.widget.Paginator constructor
    * @param c.paginator.config.containers            {Array}   (Required if c.paginator is used) Array of elements to create paginators in
    * @param c.paginator.config.rowsPerPage           {int}     (Optional) Default: 10
    * @param c.paginator.config.recordOffset          {int}     (Optional) Default: 0
    * @param c.paginator.config.template              {String}  (Optional) Default: Alfresco.util.message("pagination.template")
    * @param c.paginator.config.pageReportTemplate    {String}  (Optional) Default: Alfresco.util.message("pagination.template.page-report")
    * @param c.paginator.config.previousPageLinkLabel {String}  (Optional) Default: Alfresco.util.message("pagination.previousPageLinkLabel")
    * @param c.paginator.config.nextPageLinkLabel     {String}  (Optional) Default: Alfresco.util.message("pagination.nextPageLinkLabel")
    * @return {Alfresco.util.DataTable} DataTable with all yui widgets accessible in its "widget" property
    */
   Alfresco.util.DataTable = function(c)
   {
      // Check mandatory config attributes
      if (!c.dataSource ||
            !YAHOO.lang.isString(c.dataSource.url) ||
            !c.dataTable.container ||
            !YAHOO.lang.isArray(c.dataTable.columnDefinitions))
      {
         throw new Error("Mandatory config parameter is missing or of wrong type!");
      }

      // Merge default data source config
      c.dataSource.pagingResolver = c.dataSource.pagingResolver || this._defaultPagingResolver;
      c.dataSource.defaultFilter = c.dataSource.defaultFilter || null;
      c.dataSource.config = c.dataSource.config || {};
      c.dataSource.config.connXhrMode = c.dataSource.config.connXhrMode || "cancelStaleRequests";
      c.dataSource.config.responseType = c.dataSource.config.responseType || YAHOO.util.DataSource.TYPE_JSON;
      c.dataSource.config.responseSchema = c.dataSource.config.responseSchema || {};
      if (!YAHOO.lang.isString(c.dataSource.config.responseSchema.resultsList))
      {
         c.dataSource.config.responseSchema.resultsList = "data";
      }
      c.dataSource.config.responseSchema.fields = c.dataSource.config.responseSchema.fields || null;
      c.dataSource.config.responseSchema.metaFields = YAHOO.lang.merge(
            {
               paginationRecordOffset: "paging.skipCount",
               paginationRowsPerPage:"paging.maxItems",
               totalRecords: "paging.totalItems"
            }, c.dataSource.config.responseSchema.metaFields || {});

      // Merge default data table config
      c.dataTable.config = YAHOO.lang.merge(
            {
               dynamicData: true,
               initialLoad: false,
               MSG_EMPTY: Alfresco.util.message("message.datatable.empty"),
               MSG_ERROR: Alfresco.util.message("message.datatable.error"),
               MSG_LOADING: Alfresco.util.message("message.datatable.loading"),
               className: "alfresco-datatable"
            }, c.dataTable.config || {});

      // Merge default paginator config
      if (c.paginator)
      {
         c.paginator.history = YAHOO.lang.isBoolean(c.paginator.history) ? c.paginator.history : true;
         c.paginator.hide = YAHOO.lang.isBoolean(c.paginator.hide) ? c.paginator.hide : true;
         c.paginator.config = YAHOO.lang.merge(
               {
                  rowsPerPage: 10,
                  recordOffset: 0,
                  template: Alfresco.util.message("pagination.template"),
                  pageReportTemplate: Alfresco.util.message("pagination.template.page-report"),
                  previousPageLinkLabel: Alfresco.util.message("pagination.previousPageLinkLabel"),
                  nextPageLinkLabel: Alfresco.util.message("pagination.nextPageLinkLabel")
               }, c.paginator.config || {});
         if (!c.paginator.config.containers)
         {
            throw new Error("Mandatory paginator config parameter is missing!");
         }
      }
      this.config = c;

      // Instance variables
      this.formatters = {};
      this.widgets = {};

      // Initialise all widgets
      this._init();

      // Return instance
      return this;
   };

   Alfresco.util.DataTable.prototype =
   {
      /**
       * The config passed in to the constructor
       *
       * @param parameters
       * @type Object
       */
      config: null,

      /**
       * Name space to save all yui widgets in
       *
       * @param widgets
       * @type Object
       */
      widgets: null,

      /**
       * Place to save all data table formatters so we can call them later
       *
       * @param formatters
       * @type Object
       */
      formatters: null,

      /**
       * The current filter
       *
       * @param currentFilter
       * @type Object
       */
      currentFilter: {},

      /**
       * The current number of items to skip
       *
       * @param currentSkipCount
       * @type int
       */
      currentSkipCount: null,

      /**
       * The current number of maximum values to display
       *
       * @param currentMaxItems
       * @type int
       */
      currentMaxItems: null,

      /**
       * The current sort column/key
       *
       * @param currentSortKey
       * @type String
       */
      currentSortKey: null,

      /**
       * The current sort direction
       *
       * @param currentDir
       * @type String
       */
      currentDir: null,

      /**
       * Returns a DataTable record by ID or RecordSet position.
       *
       * @public
       * @param oRecord {HTMLElement|Number|String} DOM reference to a TR element (or child of a TR element), RecordSet position index, or Record ID.
       * @return {YAHOO.widget.Record} Record object
       */
      getRecord: function DataTable_getRecord(oRecord)
      {
         return this.widgets.dataTable.getRecord(oRecord);
      },

      /**
       * Returns the data associated to the row.
       *
       * @public
       * @param oRecord {HTMLElement|Number|String} DOM reference to a TR element (or child of a TR element), RecordSet position index, or Record ID.
       * @return The data asscoiated to the row.
       */
      getData: function DataTable_getData(oRecord)
      {
         return this.getRecord(oRecord).getData();
      },

      /**
       * Returns the DataTable instance
       *
       * @public
       * @return The YUI DataTable instance.
       */
      getDataTable: function DataTable_getDataTable()
      {
         return this.widgets.dataTable;
      },

      /**
       * Searches the current recordSet for a record with the given parameter value
       *
       * @public
       * @method findDataByParameter
       * @param p_parameter {string} Parameter to look for the value in
       * @param p_value {string} Value to find
       * @return The data asscoiated to the row.
       */
      findDataByParameter: function DataTable_findDataByParameter(p_parameter, p_value)
      {
         var record = this.findRecordByParameter(p_parameter, p_value);
         if (record !== null)
         {
            return record.getData();
         }
         return null;
      },

      /**
       * Searches the current recordSet for a record with the given parameter value
       *
       * @public
       * @method findDataByParameter
       * @param p_parameter {string} Parameter to look for the value in
       * @param p_value {string} Value to find
       * @return The data asscoiated to the row.
       */
      findRecordByParameter: function DataTable_findRecordByParameter(p_parameter, p_value)
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
               data, i, j;

         for (i = 0, j = recordSet.getLength(); i < j; i++)
         {
            record = recordSet.getRecord(i);
            if (record.getData(p_parameter) === p_value)
            {
               return record;
            }
         }
         return null;
      },

      /**
       * Reloads the datatable with the current filter & pagination
       *
       * @public
       * @method loadDataTable
       */
      reloadDataTable: function DataTable_reloadDataTable()
      {
         this.loadDataTable();
      },

      /**
       * Loads the datatable
       *
       * @public
       * @method loadDataTable
       * @param parameters {String} (Optional) The url parameters to add to the datasource url
       */
      loadDataTable: function DataTable_loadDataTable(parameters)
      {
         var me = this,
               baseParameters = this.createUrlParameters(),
               delimiter = (this.config.dataSource.url + baseParameters).indexOf("?") > -1 ? "&" : "?",
               url = baseParameters + (parameters ? delimiter + parameters : "");

         this.widgets.dataSource.sendRequest(url,
               {
                  success: function DataTable_loadDataTable_success(oRequest, oResponse, oPayload)
                  {
                     me.lastResultCount = oResponse.results.length;
                     
                     // Will end up making the doBeforeLoadData being called
                     me.widgets.dataTable.onDataReturnSetRows(oRequest, oResponse, oPayload);
                     
                     var filter = me.currentFilter;
                     if (!filter.filterId)
                     {
                        filter = me.config.dataSource.defaultFilter;
                     }
                     if (filter && filter.filterId)
                     {
                        YAHOO.Bubbling.fire("filterChanged", filter);
                     }
                  },
                  failure: this.widgets.dataTable.onDataReturnSetRows,
                  scope: this.widgets.dataTable,
                  argument: {}
               });
      },

      /**
       * The default function to create the url parameters to apend to the datasource url.
       * Will call the "pagingResolver" if a paginator is present and a "filterResolver" if the funciton is present
       * and merge the returned urls. Override this method if the url that shall be added is so dynamic that its format
       * depends on more than the filter and pagination parameters.
       *
       * @method createUrlParameters
       */
      createUrlParameters: function DataTable_createUrlParameters()
      {
         if (this.widgets.paginator)
         {
            var pagingParams = null;
            if ((this.currentSkipCount || this.currentMaxItems || this.currentSortKey || this.currentDir) && YAHOO.lang.isFunction(this.config.dataSource.pagingResolver))
            {
               pagingParams = this.config.dataSource.pagingResolver(this.currentSkipCount, this.currentMaxItems, this.currentSortKey, this.currentDir);
            }
         }
         var filterParams = null;
         if (this.currentFilter && YAHOO.lang.isFunction(this.config.dataSource.filterResolver))
         {
            filterParams = this.config.dataSource.filterResolver(this.currentFilter);
         }
         var delimiters = ["?", "&"];
         if (this.config.dataSource.url.indexOf("?") > -1)
         {
            delimiters = ["&", "&"];
         }
         return (pagingParams ? delimiters.shift() + pagingParams : "") + (filterParams ? delimiters.shift() + filterParams : "");
      },


      /**
       * PRIVATE METHODS
       */

      /**
       * Creates all YUI widgets from config
       *
       * @method _init
       * @private
       */
      _init: function DataTable__init()
      {
         // Reference to self used by inline functions
         var me = this,
               History = YAHOO.util.History;

         // Create DataSource
         this.widgets.dataSource = new YAHOO.util.DataSource(this.config.dataSource.url, this.config.dataSource.config);
         this.widgets.dataSource.doBeforeParseData = this.config.dataSource.doBeforeParseData || this.widgets.dataSource.doBeforeParseData;

         // Create Paginator
         if (this.config.paginator)
         {
            this.widgets.paginator = new YAHOO.widget.Paginator(this.config.paginator.config);
            this.config.dataTable.config.paginator = this.widgets.paginator;
         }

         // Help formatters setting their width automatically (if a width has been provided)
         var columnDefinitions = this.config.dataTable.columnDefinitions;
         for (var i = 0, il = columnDefinitions.length; i <il; i++)
         {
            if (YAHOO.lang.isFunction(columnDefinitions[i].formatter))
            {
               // Save original formatter
               this.formatters[i] = columnDefinitions[i].formatter;
               columnDefinitions[i].formatter = function(el, oRecord, oColumn, oData)
               {
                  // Apply widths on each cell so it actually works as given in the column definitions
                  if (oColumn.width)
                  {
                     YAHOO.util.Dom.setStyle(el, "width", oColumn.width + (YAHOO.lang.isNumber(oColumn.width) ? "px" : ""));
                     YAHOO.util.Dom.setStyle(el.parentNode, "width", oColumn.width + (YAHOO.lang.isNumber(oColumn.width) ? "px" : ""));
                  }
                  me.formatters[oColumn.getIndex()].call(this, el, oRecord, oColumn, oData);
               };
            }
         }

         // Create data table and show loading message while page is being rendered
         var originalCssClasses = (Dom.get(this.config.dataTable.container).getAttribute("class") || "").split(/\s/);
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.config.dataTable.container, columnDefinitions, this.widgets.dataSource, this.config.dataTable.config);
         this.widgets.dataTable.showTableMessage(this.config.dataTable.config.MSG_LOADING, YAHOO.widget.DataTable.CLASS_LOADING);
         for (i = 0, il = originalCssClasses.length; i <  il; i++)
         {
            Dom.addClass(this.widgets.dataTable.get("element"), originalCssClasses[i])
         }

         // Add animation to row delete
         this.widgets.dataTable._deleteTrEl = function DataTable__deleteTrEl(row)
         {
            var scope = this,
                  trEl = this.getTrEl(row);

            var fadeAnim = new YAHOO.util.ColorAnim(trEl,
                  {
                     opacity:
                     {
                        to: 0
                     }
                  }, 0.25);
            fadeAnim.onComplete.subscribe(function()
            {
               YAHOO.widget.DataTable.prototype._deleteTrEl.call(scope, row);
            });
            fadeAnim.animate();
         };

         // Enable row highlighting (and making it possible to display hidden column content such as actions usin css)
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);

         var loadData = function DataTable__init_loadData()
         {
            if (me.currentFilter.filterId)
            {
               me.loadDataTable();
            }
            else if (me.config.dataSource.defaultFilter)
            {
               // Since there was no filter in the url we set the configured default as start filter
               me.currentFilter = me.config.dataSource.defaultFilter;
               me.loadDataTable();
            }
            else if (YAHOO.lang.isString(me.config.dataSource.initialParameters))
            {
               // Since there is no filter in url or set as default we use the configured default url parameters
               me.loadDataTable(me.config.dataSource.initialParameters);
            }
            else
            {
               me.loadDataTable();
            }
         };

         var handlePagination = function DataTable__init_handlePagination(paginatorStateObj)
         {
            if (me.config.paginator && me.config.paginator.history)
            {
               var pagingState = me.getPagingState(paginatorStateObj.recordOffset, paginatorStateObj.rowsPerPage);
               if (pagingState)
               {
                  History.multiNavigate(
                        {
                           "paging": pagingState
                        });
               }
            }
            else
            {
               me.setPagingState(me.getPagingState(paginatorStateObj.recordOffset, paginatorStateObj.rowsPerPage));
               me.loadDataTable();
            }
         };

         // First we must unhook the built-in mechanism and then we hook up our custom function
         if (this.widgets.paginator)
         {
            this.widgets.paginator.unsubscribe("changeRequest", this.widgets.dataTable.onPaginatorChangeRequest);
            this.widgets.paginator.subscribe("changeRequest", handlePagination, this.widgets.dataTable, true);
         }

         // Update payload data on the fly for tight integration with latest values from server
         this.widgets.dataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload)
         {
            if (me.config.paginator)
            {
               var meta = oResponse.meta || {};
               oPayload.totalRecords = YAHOO.lang.isNumber(meta.totalRecords) ? meta.totalRecords : oPayload.totalRecords;
               oPayload.pagination =
               {
                  rowsPerPage: YAHOO.lang.isNumber(meta.paginationRowsPerPage) ? meta.paginationRowsPerPage : me.config.paginator.config.rowsPerPage,
                  recordOffset: YAHOO.lang.isNumber(meta.paginationRecordOffset) ? meta.paginationRecordOffset : me.config.paginator.config.recordOffset
               };
               if (me.config.paginator.hide)
               {
                  if (oPayload.totalRecords <= oPayload.pagination.rowsPerPage)
                  {
                     Dom.addClass(me.widgets.paginator.get("containers"), "hidden");
                  }
                  else
                  {
                     Dom.removeClass(me.widgets.paginator.get("containers"), "hidden");
                  }
               }
            }
            /*
             if (meta.sortKey)
             {
             oPayload.sortedBy =
             {
             key: meta.sortKey || null,
             dir: (meta.sortDir) ? "yui-dt-" + meta.sortDir : "yui-dt-asc" // Convert from server value to DataTable format
             };
             }
             */
            return true;
         };

         var onNewHistoryPagingState = function DataTable__init_onNewHistoryPagingState(pagingState)
         {
            me.setPagingState(pagingState);
            if (History.getBookmarkedState("filter") == null || History.getBookmarkedState("filter") == History.getCurrentState("filter"))
            {
               me.loadDataTable();
            }
         };

         var onNewHistoryFilterState = function DataTable__init_onNewHistoryFilterState(filterState)
         {
            me.setFilterState(filterState);
            me.loadDataTable();
         };

         /**
          * When a filter has changed we save the filter and add it to the browser history state.
          * When the new state has been added the _loadDataTableFromHistory will be called that will reload the data table
          * which when the response successfully comes back will fire the "filterChanged" event to make it possible for
          * the filter ui to update itself.
          */
         YAHOO.Bubbling.on("changeFilter", function DT_onChangeFilter(layer, args)
         {
            if (me.config.paginator && me.config.paginator.history)
            {
               var filterState = args[1] ? me.getFilterState(args[1]) : null;
               if (filterState)
               {
                  History.multiNavigate(
                        {
                           "paging": "|",
                           "filter": filterState
                        });
               }
            }
            else
            {
               // Use state history methods to set the current filter values before reloading the datatable
               me.setFilterState(me.getFilterState(args[1]));
               me.loadDataTable();
            }
         }, this);

         if (this.config.paginator)
         {
            var pc = this.config.paginator.config;

            if (this.config.paginator.history)
            {
               // Register the module with states taken either from the url (or from the default values if not present)
               History.register("paging", History.getBookmarkedState("paging") || this.getPagingState(pc.recordOffset, pc.rowsPerPage), onNewHistoryPagingState);
               History.register("filter", History.getBookmarkedState("filter") || this.getFilterState(this.config.dataSource.defaultFilter), onNewHistoryFilterState);

               var onHistoryManagerReady = function DataTable_init_onHistoryManagerReady()
               {
                  // Current state after BHM is initialized is the source of truth for what state to render
                  me.setPagingState(History.getCurrentState("paging"));
                  me.setFilterState(History.getCurrentState("filter"));

                  // There was no specific filter arguments in the url from a previous visit on the page
                  if (me.widgets.paginator)
                  {
                     // Set default values for pagination since they weren't provided in the url
                     me.currentSkipCount = me.currentSkipCount || me.config.paginator.config.recordOffset;
                     me.currentMaxItems = me.currentMaxItems || me.config.paginator.config.rowsPerPage;
                     // todo set sorting default attributes if not present
                  }
                  loadData();
               };

               // Initialize the Browser History Manager.
               History.onReady(onHistoryManagerReady, {}, this);
               try
               {
                  // Create the html elements needed for history management
                  var historyMarkup = '';
                  if (YAHOO.env.ua.ie > 0 && !Dom.get("yui-history-iframe"))
                  {
                     historyMarkup += '<iframe id="yui-history-iframe" src="' + Alfresco.constants.URL_RESCONTEXT + 'yui/history/assets/blank.html" style="display: none;"></iframe>';
                  }
                  if (!Dom.get("yui-history-field"))
                  {
                     historyMarkup +='<input id="yui-history-field" type="hidden" />';
                  }
                  if (historyMarkup.length > 0)
                  {
                     var historyManagementEl = document.createElement("div");
                     historyManagementEl.innerHTML = historyMarkup;
                     document.body.appendChild(historyManagementEl);
                  }
                  History.initialize("yui-history-field", "yui-history-iframe");
               }
               catch(e)
               {
                  Alfresco.logger.error(this.name + ": Couldn't initialize HistoryManager.", e);
                  onHistoryManagerReady();
               }
            }
            else
            {
               // No history manager being used - set default values for pagination
               this.currentSkipCount = this.currentSkipCount || pc.recordOffset;
               this.currentMaxItems = this.currentMaxItems || pc.rowsPerPage;
               loadData();
            }
         }
         else
         {
            loadData();
         }
      },

      /**
       * The default function to create url pagination parameters using "skipCount" and "maxItems".
       * If these parameters doesn't mathc th url of the REST call override this method by passing in a
       * similar function to the "dataSource.pagingResolver" config element to the YAHOO.util.DataTable constructor.
       *
       * @method _defaultPagingResolver
       * @param currentSkipCount
       * @param currentMaxItems
       * @private
       */
      _defaultPagingResolver: function DataTable__defaultPagingResolver(currentSkipCount, currentMaxItems)
      {
         return "skipCount=" + currentSkipCount + "&" + "maxItems=" + currentMaxItems;
      },


      /**
       * HELPER METHODS FOR ENCODING/DECODING BROWSER HISTORY STATE
       */

      /**
       * Returns the current paging values encoded as a browser history bookmark state string.
       *
       * @method getPagingState
       */
      getPagingState: function DataTable_getPagingState(skipCount, maxItems)
      {
         // TODO for sorting: + "|" + this.currentSortKey + "|" + this.currentDir;
         return YAHOO.lang.isNumber(skipCount) ? (skipCount + (YAHOO.lang.isNumber(maxItems) ? "|" + maxItems : "")) : "";
      },

      /**
       * Takes a browser history bookmark state string and decodes it to set the current paging values.
       *
       * @method setPagingState
       * @param pagingState
       */
      setPagingState: function DataTable_setPagingState(pagingState)
      {
         // Check against, "|"-index of "0" and not "-1" since "-1" means filterId is empty
         var paging = (pagingState && pagingState.indexOf("|") > 0) ? pagingState.split("|") : [];
         this.currentSkipCount = paging.length > 0 ? parseInt(paging[0]) : this.config.paginator.config.recordOffset;
         this.currentMaxItems = paging.length > 1 ? parseInt(paging[1]) : this.config.paginator.config.rowsPerPage;
         // todo for sorting variables
      },

      /**
       * Returns the current filter values encoded as a browser history bookmark state string.
       *
       * @method getFilterState
       */
      getFilterState: function DataTable_getFilterState(filter)
      {
         if (filter)
         {
            return filter.filterId ? (filter.filterId + (filter.filterData ? ("|" + filter.filterData) : "")) : "";
         }
         return "";
      },

      /**
       * Takes a browser history bookmark state string and decodes it to set the current filter values.
       *
       * @method setFilterState
       * @param filterState
       */
      setFilterState: function DataTable_setFilterState(filterState)
      {
         var filter = {};
         if (filterState.indexOf("|") > 0)
         {
            var filterTokens = filterState.split("|");
            filter.filterId = filterTokens[0];
            filter.filterData = filterTokens.slice(1).join("|"); // Make sure '|' characters in the data value remains
         }
         else
         {
            filter.filterId = filterState.length > 0 ? filterState : undefined;
         }
         this.currentFilter = filter || {};
      }

   };
})();

/**
 * Adds a class if it doesn't exist, and removes it if it does
 *
 * @method Alfresco.util.toggleClass
 * @param element {HTML Element} The element to change the class of.
 * @param className {String} Name of the class to look for and remove.
 */
Alfresco.util.toggleClass = function(element, className)
{
   if (YUIDom.hasClass(element, className))
   {
      YUIDom.removeClass(element, className);
   }
   else
   {
      YUIDom.addClass(element, className);
   }
};

/**
 * Returns trimmed value. This method uses YAHOO.lang.trim + remove IDEOGRAPHIC SPACE (\u3000)
 *
 * @method Alfresco.util.trim
 * @param value {string} String value which we trim
 * @return {string} trimmed value
 * @static
 */
Alfresco.util.trim = function(value)
{
   var s = YAHOO.lang.trim(value);
   return s ? s.replace(/(^[\s\u3000]+|[\s\u3000]+$)/g, '') : s;
};

/**
 * Generates a URL to retrieve a thumbnail based on the node and thumbnail name supplied.
 *
 * @method generateThumbnailUrl
 */
Alfresco.util.generateThumbnailUrl = function(jsNode, thumbnailName)
{
   var url,
         nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef;;
   if (jsNode.properties["cm:lastThumbnailModification"])
   {
      var thumbnailModData = jsNode.properties["cm:lastThumbnailModification"];
      for (var i = 0; i < thumbnailModData.length; i++)
      {
         if (thumbnailModData[i].indexOf(thumbnailName) != -1)
         {
            url = Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/" + thumbnailName + "?c=queue&ph=true&lastModified=" + thumbnailModData[i];
            break;
         }
      }
   }
   if (YAHOO.lang.isUndefined(url) || YAHOO.lang.isNull(url))
   {
      // This has been updated to include a lastModified request parameter to allow the thumbnail to be cached. Because thumbnails
      // that are genuine previews are "taken care of" by updates to the "cm:lastThumbnailModification" property then there are more
      // benefits to allowing non-preview types to be cached. File association thumbnails change infrequently and there is no
      // major problem with them not updating if they do - the benefits of not revalidating them from performance are far greater...
      url = Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/" + thumbnailName + "?c=queue&ph=true&lastModified=1";
   }
   return url;
};

Alfresco.util.getCalendarControlConfiguration = function()
{
   var navConfig =
   {
      strings :
      {
         month : Alfresco.util.message("calendar.widget_control_config.label_month"),
         year : Alfresco.util.message("calendar.widget_control_config.label_year"),
         submit : Alfresco.util.message("button.ok"),
         cancel : Alfresco.util.message("button.cancel"),
         invalidYear : Alfresco.util.message("calendar.widget_control_config.label_invalid_year")
      },
      monthFormat : YAHOO.widget.Calendar.SHORT,
      initialFocus : "year"
   };
   return navConfig;
};

/**
 * Aspect filter implementation. Returns true if the node attribute has all the aspects enumerated in {filter.match}. 
 * For filters examples see ["CommonComponentStyle"]["component-style"] or ["SuppressComponent"]["component-config"] configurations from share-document-library-config.xml.
 * 
 * @param node {object}
 * @param filter {object}
 * @returns {Boolean} - true if the node attribute has all the aspects enumerated in filter.match, false otherwise.
 */
Alfresco.util.matchAspect = function(node, filter)
{
   var match = true;
   if (filter.match && filter.match.length != null)
   {
      for (var j = 0; j < filter.match.length; j++)
      {
         var aspect = filter.match[j];
         if (!node.aspects || node.aspects.indexOf(aspect) == -1)
         {
            match = false;
            break;
         }
      }
   }
   else
   {
      match = false;
   }
   return match;
};

/**
 * Returns true if filterType is accepted, false otherwise. Currently only aspect filters accepted. 
 * @param filterType
 * @returns {Boolean} - true if filterType is accepted, false otherwise.
 */
Alfresco.util.accepted = function(filterType)
{
   return (filterType == "aspect");
};

/**
 * Filter implementation. Currently only aspect filter supported implemented by function Alfresco.util.matchAspect, 
 * but other filter types can be added and implemented( e.g. filter by type, filter by one property name,....) and here we'll hook the implementations for each filter type.
 * @param node {object}
 * @param filter {object}
 * @returns {Boolean} - true if filter matches, false otherwise.
 */
Alfresco.util.match = function(node, filter)
{
   if (filter.name == "aspect")
   {
      return Alfresco.util.matchAspect(node, filter);
   }
   return false;
};

/**
 * Gets true if any of {supressConfig} filters are matching, or false otherwise.
 * 
 * This function is used for suppressing Social components {favorites, likes and comments}. Currently only used for folders in Smart Folders context.
 * 
 * @returns {Boolean} - true if any of {supressConfig} filters are matching, or false otherwise.
 */
Alfresco.util.isSuppressed = function(node, supressConfig)
{
   var suppress = false;
   if (supressConfig && supressConfig.length != null)
   {
      for (var i = 0; i < supressConfig.length; i++)
      {
         var component = supressConfig[i];
         var filter = component.filter;
         if (!Alfresco.util.accepted(filter.name))
         {
            continue;
         }
         var match = Alfresco.util.match(node, filter);
         if (match == true)
         {
            suppress = true;
            break;
         }
      }
   }
   return suppress;
};
