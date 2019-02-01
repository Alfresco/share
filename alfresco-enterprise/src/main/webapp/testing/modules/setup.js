var alfTicket = getLoginTicket();

var URLS = {
	errorSearch: "/alfresco/service/slingshot/node/search?q=PATH%3A%22%2F%22&lang=email&store=workspace%3A%2F%2FSpacesStore&maxResults=100&alf_ticket=" + alfTicket,
	errorRatings: "/alfresco/service/api/node/workspace/SpacesStore/337d6116-307f-4fec-893a-f68961555d95/ratings?alf_ticket=" + alfTicket
};