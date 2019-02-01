function getLoginTicket(){

	try {
		var response = testLogin();
		
		if(response.status != 200) {
			throw "invalid username or password";
		}
		
		var ticket = response.responseXML.childNodes[0].textContent;
		
		
		if (!ticket) {
			throw "invalid ticket returned";	
		}

		test("Standard login", function() {
			stop();
			ok(response.status == 200, "Expected 200 received : " + response.status);
			ok(ticket, "Got a ticket back " + ticket);
			start();	
		});	

		return ticket;	
	} catch (e) {
		alert(e);
	}
}

var DEFAULT_USER_NAME = "admin"; var DEFAULT_PASSWORD = "admin";
/* IMPORTANT: 
 * IF YOU HAVE CHANGED THE USER NAME AND PASSWORD FOR YOUR OWN USER IN YOUR LOCAL MACHINE,
 * DO NOT COMMIT THIS IN SVN.
 * some one will go after you, if you break the hudson test
 * */
function testLogin(){
	var response = $.ajax({
		async: false,
		url: "/alfresco/service/api/login", 
		data: ({
			u: DEFAULT_USER_NAME,
			pw: DEFAULT_PASSWORD
		}),
		type: "GET",
		dataType: "xml",		
	});
		
	return response;
}
