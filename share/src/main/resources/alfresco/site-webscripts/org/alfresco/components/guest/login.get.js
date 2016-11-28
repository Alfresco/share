/**
 * Login component controller GET method
 */
function main()
{
   // Get the edition information to determine if this is an enterprise license
   // This allows us to set a class on the login form to control the button colour
   model.edition = context.properties["editionInfo"].edition;

   model.loginUrl = url.context + "/page/dologin";
   var successUrl = context.properties["alfRedirectUrl"];
   if (successUrl === null)
   {
      successUrl = url.context;
      if (args.alfRedirectUrl)
      {
         successUrl += Packages.org.springframework.extensions.surf.uri.UriUtils.relativeUri(args.alfRedirectUrl);
      }
   }
   successUrl = successUrl.replace("?error=true","");
   successUrl = successUrl.replace("&error=true","");
   model.successUrl = successUrl;
   model.failureUrl = successUrl + (successUrl.indexOf("?") != -1 ? "&" : "?") + "error=true";
   model.lastUsername = context.properties["alfLastUsername"];
   model.errorDisplay = (args.errorDisplay !== null ? args.errorDisplay : "container");
   model.error = (args.error === "true");

   var login = {
      id: "Login",
      name: "Alfresco.component.Login",
      options: {
         error: model.error,
         errorDisplay: model.errorDisplay,
         lastUsername: model.lastUsername,
         edition: model.edition
      }
   };
   model.widgets = [login];


   model.dependencies =
   [
      "components/header/header.js",
      "modules/about-share.js",
      "modules/create-site.js",
      "modules/header/sites.js",
      "components/dashlets/dynamic-welcome.js",
      "components/form/form.js",
      "components/form/date-picker.js",
      "components/form/period.js",
      "components/form/date.js",
      "components/form/rich-text.js",
      "components/object-finder/object-finder.js",
      "components/form/content.js",
      "components/form/workflow/activiti-transitions.js",
      "components/form/jmx/operations.js",
      "components/form/workflow/transitions.js",
      "components/dashlets/my-sites.js",
      "modules/delete-site.js",
      "components/dashlets/activities.js",
      "components/dashlets/my-documents.js",
      "components/header/header.css",
      "components/console/license.css",
      "modules/about-share.css",
      "modules/create-site.css",
      "modules/header/sites.css",
      "yui/calendar/assets/calendar.css",
      "components/dashlets/dynamic-welcome.css",
      "components/object-finder/object-finder.css",
      "components/dashlets/my-sites.css",
      "components/dashlets/my-profile.css",
      "modules/delete-site.css",
      "components/dashlets/activities.css",
      "components/dashlets/my-documents.css",
      "components/footer/footer.css",
      "components/title/collaboration-title.js",
      "modules/edit-site.js",
      "components/dashlets/docsummary.js",
      "modules/edit-site.css",
      "components/dashlets/colleagues.css",
      "components/dashlets/docsummary.css",
      "modules/documentlibrary/doclib-actions.js",
      "components/documentlibrary/actions.js",
      "modules/simple-dialog.js",
      "modules/documentlibrary/copy-move-to.js",
      "modules/documentlibrary/global-folder.js",
      "modules/documentlibrary/permissions.js",
      "components/people-finder/people-finder.js",
      "modules/documentlibrary/aspects.js",
      "components/documentlibrary/toolbar.js",
      "components/documentlibrary/tree.js",
      "components/documentlibrary/documentlist.js",
      "components/common/common-component-style-filter-chain.js",
      "components/tag-filter/tag-filter.js",
      "components/upload/dnd-upload.js",
      "components/upload/file-upload.js",
      "components/upload/html-upload.js",
      "modules/documentlibrary/global-folder.css",
      "components/documentlibrary/actions.css",
      "components/people-finder/people-finder.css",
      "modules/documentlibrary/permissions.css",
      "modules/documentlibrary/aspects.css",
      "components/documentlibrary/toolbar.css",
      "components/documentlibrary/documentlist.css",
      "components/documentlibrary/tree.css",
      "components/upload/dnd-upload.css",
      "components/upload/html-upload.css",
      "components/node-details/node-header.js",
      "components/preview/WebPreviewer.js",
      "components/preview/web-preview.js",
      "components/comments/comments-list.js",
      "components/document-details/document-links.js",
      "components/document-details/document-metadata.js",
      "components/document-details/document-workflows.js",
      "components/document-details/document-permissions.js",
      "modules/document-details/revert-version.js",
      "components/document-details/document-versions.js",
      "modules/document-details/historic-properties-viewer.js",
      "components/document-details/document-details-panel.css",
      "components/node-details/node-header.css",
      "components/preview/web-preview.css",
      "components/preview/WebPreviewer.css",
      "components/preview/WebPreviewerHTML.css",
      "components/comments/comments-list.css",
      "components/document-details/document-actions.css",
      "components/document-details/document-tags.css",
      "components/document-details/document-links.css",
      "components/document-details/document-metadata.css",
      "components/document-details/document-permissions.css",
      "components/document-details/document-workflows.css",
      "modules/document-details/revert-version.css",
      "modules/document-details/historic-properties-viewer.css"
   ];

   model.images =
   [
      "components/images/welcome-background.png",
      "components/images/user-16.png",
      "components/images/header/my-dashboard.png",
      "components/images/header/sites.png",
      "components/images/header/help.png",
      "components/images/feed-icon-16.png",
      "components/documentlibrary/images/simple-view-on-16.png",
      "components/documentlibrary/images/detailed-view-on-16.png",
      "components/documentlibrary/images/detailed-view-off-16.png",
      "components/images/search-16.png",
      "components/images/star-selected_16x16.png",
      "components/images/star-deselected_16x16.png",
      "components/images/lightbox/overlay.png",
      "components/images/filetypes/generic-file-16.png",
      "components/images/comment-16.png",
      "components/images/filetypes/generic-site-32.png",
      "components/documentlibrary/images/navbar-show-16.png",
      "components/documentlibrary/images/select-all-16.png",
      "components/documentlibrary/images/feed-icon-16.png",
      "components/documentlibrary/images/select-documents-16.png",
      "components/documentlibrary/images/select-folders-16.png",
      "components/documentlibrary/images/select-invert-16.png",
      "components/documentlibrary/images/select-none-16.png",
      "components/documentlibrary/images/folders-hide-16.png",
      "components/documentlibrary/images/sort-ascending-16.png",
      "components/documentlibrary/images/sort-descending-16.png",
      "components/documentlibrary/images/simple-view-off-16.png",
      "components/documentlibrary/images/folders-show-16.png",
      "components/documentlibrary/images/folder-new-16.png",
      "components/documentlibrary/images/upload-16.png",
      "components/documentlibrary/actions/document-move-to-16.png",
      "components/documentlibrary/actions/document-copy-to-16.png",
      "components/documentlibrary/actions/document-delete-16.png",
      "components/documentlibrary/actions/document-manage-permissions-16.png",
      "components/documentlibrary/images/folder-up-disabled-16.png",
      "components/documentlibrary/indicators/exif-16.png",
      "components/documentlibrary/images/folder-64.png",
      "components/images/drop-arrow-left-large.png",
      "components/images/drop-arrow-left-small.png",
      "components/images/like-16.png",
      "components/images/liked-16.png",
      "components/images/edit-16.png",
      "components/documentlibrary/actions/folder-view-details-16.png",
      "components/documentlibrary/actions/folder-edit-properties-16.png",
      "components/documentlibrary/images/plus-sign-16.png",
      "components/document-details/images/document-download-16.png",
      "components/documentlibrary/actions/document-view-content-16.png",
      "components/documentlibrary/actions/document-edit-properties-16.png",
      "components/documentlibrary/actions/document-assign-workflow-16.png",
      "components/documentlibrary/actions/document-upload-new-version-16.png",
      "components/documentlibrary/actions/document-edit-metadata-16.png",
      "components/documentlibrary/actions/document-edit-offline-16.png",
      "components/document-details/images/document-view-metadata-16.png",
      "components/document-details/images/revert-16.png"
   ];
}

main();