/**
 * Returns a template config param or the passed default if not found.
 * This method first checks in template.properties
 */
function getTemplateParam(paramName, defaultValue)
{
   if (template.properties[paramName] != undefined)
   {
      return template.properties[paramName];
   }
   else
   {
      return defaultValue;
   }
}


/** Gets a param from a page request. */
function getPageUrlParam(paramName, defaultValue)
{
   if (page.url.args[paramName] != undefined)
   {
      return page.url.args[paramName];
   }
   else
   {
      return defaultValue;
   }
}

/** Gets a param from a webscript request. */
function getRequestParam(paramName, defaultValue)
{
   if (args[paramName] != undefined)
   {
      return args[paramName];
   }
   else
   {
      return defaultValue;
   }
}

/**
 * Adds a request parameter to a url
 * @return the new url
 */
function addParamToUrl(theUrl, paramName, paramValue)
{
   if (theUrl.indexOf('?') > -1)
   {
      return theUrl += "&" + paramName + "=" + paramValue;
   }
   else
   {
      return theUrl += "?" + paramName + "=" + paramValue;
   }
}

/** Adds a path element to a url.
 * E.g. if the url is /abc/def and elem is ghi the returned url would be
 * /abc/def/ghi. On the other hand, if elem is null, the input url would be
 * returned
 */
function addUrlPathElement(theUrl, elem)
{
   if (elem != undefined && elem.length > 0)
   {
      theUrl += "/" + elem;
   }
   return theUrl;
}

/** 
 * Copies all properties from one javascript object to another.
 */
function copyDataToObject(data, target)
{
   for (n in data)
   {
      target[n] = data[n];
   }
}

/** 
 * Copies all properties from the passed javascript object to the model.
 */
function applyDataToModel(data)
{
   copyDataToObject(data, model);
}


/**
 * POST call
 */
function doPostCall(theUrl, paramsJSON)
{
   var connector = remote.connect("alfresco");
   var result = connector.post(theUrl, paramsJSON, "application/json");
   if (result.status == status.STATUS_OK)
   {
      return JSON.parse(result.response);
   }
   else
   {
      status.code = result.status;
      status.message = result.response;
   }
}


/**
 * PUT call
 */
function doPutCall(theUrl, paramsJSON)
{
   var connector = remote.connect("alfresco");
   var result = connector.put(theUrl, paramsJSON, "application/json");
   if (result.status == status.STATUS_OK)
   {
      return JSON.parse(result.response);
   }
   else
   {
      status.code = result.status;
      status.message = result.response;
   }
}


/**
 * GET call
 */
function doGetCall(theUrl)
{
   var connector = remote.connect("alfresco");
   var result = connector.get(theUrl);
   if (result.status == status.STATUS_OK)
   {
      return JSON.parse(result.response);
   }
   else
   {
      status.code = result.status;
      status.message = result.response;
   }
}
