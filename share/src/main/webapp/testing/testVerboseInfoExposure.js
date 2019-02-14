/**
 * MNT-20195 (LM-190214): test when error happened on webscript, will error code number being displayed.
 * Also test when no error code thrown from exception, will proper error message being displayed.
 */

function isHTML(string)
{
   string = string && string.trim();
   return (string && string.indexOf("<") == 0 && string.lastIndexOf(">") == string.length - 1);
}

function isJSON(string)
{
   string = string && string.trim();
   return (string && string.indexOf("{") == 0 && string.lastIndexOf("}") == string.length - 1);
}

function isXML(string)
{
   string = string && string.trim();
   return (string && string.indexOf("<?xml") == 0);
}

// get error log number
function getErrorLogNumber(content)
{
   var logNumber = null;
   if (isXML(content))
   {
      logNumber = getErrorLogNumberFromXML(content);
   }
   else if (isHTML(content))
   {
      logNumber = getErrorLogNumberFromHTML(content);
   }
   else if (isJSON(content))
   {
      logNumber = getErrorLogNumberFromJSON(content);
   }
   
   return logNumber;
}

function getErrorLogNumberFromJSON(content)
{
   var response = JSON.parse(content);
   return response.errorLogNumber;
}

function getErrorLogNumberFromXML(htmlString)
{
   var logNumber = null;
   
   var xml = $.parseXML(htmlString);
   $(xml)
      .find('errorLogNumber')
      .each(function(index, element)
      {
         logNumber = element.innerHTML;
         // break the loop
         return false;
      });
   
   return logNumber;
}

function getErrorLogNumberFromHTML(htmlString)
{
   var logNumber = null;
   
   var finder = (htmlString.indexOf("<td><b>Error Log Number") != -1) ? $(htmlString).find("table:contains('Error Log Number')") : $(htmlString).find("div:contains('Error Log Number')");
   finder
      .each(function(index, element)
      {
         var innerText = element.innerText;
         var content = innerText.split("\n");
         for (var i = 0, l = content.length; i < l; i++)
         {
            var text = content[i];
            if (text && text.indexOf("Error Log Number") != -1)
            {
               text = text.trim();
               logNumber = text.substring(text.length - 8);
               return false;
            }
         }
      });
   
   return logNumber;
}

function isValidNumber(number)
{
   return (number && !isNaN(number));
}

/**************************************** QUNIT TEST start ****************************************/

function testVerboseInfoExposure()
{
   var searchUrl = "/share/proxy/alfresco/slingshot/node/search?q=PATH%3A%22%2F%22&lang=email&store=workspace%3A%2F%2FSpacesStore&maxResults=100";
   var docDetailsUrl = "/share/page/document-details";
   var authFinderUrl = "/share/service/components/people-finder/authority-finder?htmlid=FOOOO%A)F";
   
   /**
    * Test if error message is displayed in FTL when unable to retrieve log number from exception nessage.
    * - url: "/share/proxy/alfresco/slingshot/node/search?q=PATH%3A%22%2F%22&lang=email&store=workspace%3A%2F%2FSpacesStore&maxResults=100&alf_ticket=" + alfTicket,
    */
   QUnit.test("Test if error message is displayed in status template when no error log number returned in exception. (Test url = " + searchUrl + ")", function(assert)
   {
      var data = $.ajax(
      {
         async: false,
         url: searchUrl,
         type: "GET",
         contentType: "application/json"
      });
      
      var logNumber = null;
      var message = null;
      if (data && data.responseText)
      {
         var response = JSON.parse(data.responseText);
         logNumber = response.errorLogNumber;
         message = response.message;
      }
      
      assert.ok(!isValidNumber(logNumber), "No error log number get: [" + logNumber + "]");
      assert.ok(message, "Error message: " + message);
   });
   
   /**
    * Test if log number is displayed in Share FTL (i.e: html.status.ftl)
    * - url: "/share/page/document-details"
    * - url: "/share/service/components/people-finder/authority-finder?htmlid=FOOOO%A)F"
    */
   QUnit.test("Test if error log number can be displayed in correctly in Share status template (i.e: html.status.ftl).", function(assert)
   {
      var urls = [docDetailsUrl, authFinderUrl];
      for (var i = 0, l = urls.length; i < l; i++)
      {
         var url = urls[i];
         var data = $.ajax(
         {
            async: false,
            url: url,
            type: "GET"
         });
         
         if (data && data.responseText)
         {
            logNumber = getErrorLogNumberFromHTML(data.responseText);
         }
         
         assert.ok(isValidNumber(logNumber), "Error log number: " + logNumber + " (Test url = " + url + ")");
      }
   });
}