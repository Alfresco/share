var errors = [];
var failed = false;

var type = context.properties['feedbackType'];
var successPage = context.properties['successPage'];
var name = context.properties['visitorName'];
var email = context.properties['visitorEmail'];
var website = context.properties['visitorWebsite'];
var subject = context.properties['feedbackSubject'];
var comment = context.properties['feedbackComment'];
var formId = context.properties['formId'];

// Validate the form
if (name == null || name.length == 0)
{
	failed = true;
	errors["visitorName"] = "comments.write.null.feedback.visitorName";
}
else
{
	var txt=new RegExp("^[-a-z0-9 ]+$","ig");
	if ( ! txt.test(name))
	{
		failed = true;
		errors["visitorName"] = "comments.write.invalid.feedback.visitorName";
	}	
}

if (email == null || email.length == 0)
{
	failed = true;
	errors["visitorEmail"] = "comments.write.null.feedback.visitorEmail";
}
else
{
	var txt=new RegExp("^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.([a-z][a-z]+)|([0-9]{1,3}\\." +
			"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$","ig");
	if ( ! txt.test(email))
	{
		failed = true;
		errors["visitorEmail"] = "comments.write.invalid.feedback.visitorEmail";
	}	
}

if (comment == null || comment.length == 0)
{
	failed = true;
	errors["comment"] = "comments.write.null.feedback.comment";
}

model.formId = formId;

if (failed)
{
	model.errors = errors;
	model.visitorName = name;
	model.visitorEmail = email;
	model.visitorWebsite = website;
	model.feedbackSubject = subject;
	model.feedbackComment = comment;
}
else 
{
	// Check that the form id has not already been submitted, ie prevent re-post via browser refresh.
	if (webSite.ugcService.validateFormId(formId))
	{	
		// Post the feedback
		var assetId;
		if (asset != null)
		{
			assetId = asset.id;
		}
		else
		{
			assetId = webSite.id;
		}
		webSite.ugcService.postFeedback(assetId, name, email, website, type, subject, comment, 0);
	}
	// Get the thankyou asset from the repository to render
	model.successAsset = webSite.getAssetByPath(section.path+args.successAsset);
}

