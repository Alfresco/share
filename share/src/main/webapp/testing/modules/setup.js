var alfTicket = getLoginTicket();

var URLS = {
	errorSearch: "/share/proxy/alfresco/slingshot/node/search?q=PATH%3A%22%2F%22&lang=email&store=workspace%3A%2F%2FSpacesStore&maxResults=100&alf_ticket=" + alfTicket,
	errorDocumentDetails: "/share/page/document-details",
	errorAuthorityFinder: "/share/service/components/people-finder/authority-finder?htmlid=FOOOO%A)F?alf_ticket=" + alfTicket
};