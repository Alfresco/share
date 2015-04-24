var successUrl = context.properties["alfRedirectUrl"];
if (successUrl == null)
{
	successUrl = page.url.context;
}
model.successUrl = successUrl;