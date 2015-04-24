model.feedbackPage = webSite.ugcService.getFeedbackPage(context.properties.asset.id, 100, 0);

if (context.properties['report'] != null)
{
	webSite.ugcService.reportFeedback(context.properties['report']);	
}

