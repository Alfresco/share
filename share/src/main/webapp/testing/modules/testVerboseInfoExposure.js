/**
 * MNT-20195 (Bug-2766): test if error log number is displayed when available, else show error message.
 */

function isHTML(string) {
	string = string && string.trim();
	return (string && string.indexOf("<") == 0 && string.lastIndexOf(">") == string.length - 1);
}

function isJSON(string) {
	string = string && string.trim();
	return (string && string.indexOf("{") == 0 && string.lastIndexOf("}") == string.length - 1);
}

function isXML(string) {
	string = string && string.trim();
	return (string && string.indexOf("<?xml") == 0);
}

// get error log number
function getErrorLogNumber(content) {
	var logNumber = null;
	
	if (isXML(content)) {
		logNumber = getErrorLogNumberFromXML(content);
		
	} else if (isHTML(content)) {
		logNumber = getErrorLogNumberFromHTML(content);
		
	} else if (isJSON(content)) {
		logNumber = getErrorLogNumberFromJSON(content);
	}
	
	return logNumber;
}

function getErrorLogNumberFromJSON(content) {
	var response = JSON.parse(content);
	return response.errorLogNumber;
}

function getErrorLogNumberFromXML(htmlString) {
	var logNumber = null;
	
	var xml = $.parseXML(htmlString);
	$(xml)
		.find('errorLogNumber')
		.each(function(index, element) {
			logNumber = element.innerHTML;
			// break the loop
			return false;
		});
	
	return logNumber;
}

function getErrorLogNumberFromHTML(htmlString) {
	var logNumber = null;
	
	var finder = (htmlString.indexOf("<td><b>Error Log Number") != -1) ? $(htmlString).find("table:contains('Error Log Number')") : $(htmlString).find("div:contains('Error Log Number')")
	
	finder
		.each(function(index, element) {
			var innerText = element.innerText;
			var content = innerText.split("\n");
			for (var i = 0, l = content.length; i < l; i++) {
				var text = content[i];
				if (text && text.indexOf("Error Log Number") != -1) {
					text = text.trim();
					logNumber = text.substring(text.length - 8);
					return false;
				}
			}
		});
	
	return logNumber;
}

function isValidNumber(number) {
	return (number && !isNaN(number));
}

function testVerboseInfoExposure() {
	/**
	 * Test if error message is displayed in FTL when unable to retrieve log number from exception nessage.
	 * - url: "/share/proxy/alfresco/slingshot/node/search?q=PATH%3A%22%2F%22&lang=email&store=workspace%3A%2F%2FSpacesStore&maxResults=100&alf_ticket=" + alfTicket,
	 */
	test("Test if error message is displayed in status template when no error log number returned in exception. (Test url = " + URLS.errorSearch + ")", function() {
		stop();
		
		var data = $.ajax({
			async: false,
			url: URLS.errorSearch,
			type: "GET",
			contentType: "application/json"
		});
		
		var logNumber = null;
		var message = null;
		if (data && data.responseText) {
			var response = JSON.parse(data.responseText);
			logNumber = response.errorLogNumber;
			message = response.message;
		}
		
		ok(!isValidNumber(logNumber), "No error log number get: [" + logNumber + "]");
		ok(message, "Error message: " + message);
		start();
	});
	
	/**
	 * Test if log number is displayed in Share FTL (i.e: html.status.ftl)
	 * - url: "/share/page/document-details"
	 * - url: "/share/service/components/people-finder/authority-finder?htmlid=FOOOO%A)F"
	 */
	test("Test if error log number can be displayed in correctly in Share status template (i.e: html.status.ftl).", function() {
		stop();
		
		var urls = [URLS.errorDocumentDetails, URLS.errorAuthorityFinder];
		for (var i = 0, l = urls.length; i < l; i++) {
			var url = urls[i];
			var data = $.ajax({
				async: false,
				url: url,
				type: "GET"
			});
			
			if (data && data.responseText) {
				logNumber = getErrorLogNumberFromHTML(data.responseText);
			}
			
			ok(isValidNumber(logNumber), "Error log number: " + logNumber + " (Test url = " + url + ")");
		}
		
		start();
	});
}