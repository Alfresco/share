<#if field.value == "">
	<#assign displayValue=msg("form.control.novalue") />
<#else>
	<#assign displayValue=msg("form.control.novalue") />
</#if>

<script type="text/javascript">//<![CDATA[
    var cloudAuth;
    var cloudFolderPicker;
    
    function selectCloudFolder() {
    	cloudAuth.checkAuth();
    }
    
    (function()
    {
       // Create picker
       cloudFolderPicker = new Alfresco.module.DoclibCloudFolder("${fieldHtmlId}" + "-cloud-folder");
       
       // Set up handler for when the sync location has been chosen:
       YAHOO.Bubbling.on("folderSelected", function cloudSync_onCloudFolderSelected(event, args)
       {
           this.updateSyncOptions();
           var remoteTenantId = this.options.targetNetwork;
           var targetFolderNodeRef = args[1].selectedFolder.nodeRef;
           var targetFolderPath = args[1].selectedFolder.path;
           var targetFolderSite = Alfresco.util.encodeHTML(args[1].selectedFolder.siteTitle);
           var targetFolderSiteId = args[1].selectedFolder.siteId;
           console.log(args[1]);
                     
           // Update UI with selected site/tenant/folder
           Dom.get("${fieldHtmlId}-folder").innerHTML = "${msg("hybridworkflow.destination-select.folderPrefix")}" + targetFolderPath;
           Dom.get("${fieldHtmlId}-tenant").innerHTML = remoteTenantId;
           Dom.get("${fieldHtmlId}-site").innerHTML = targetFolderSite;
           
           Dom.get("${fieldHtmlId}").value = remoteTenantId + "|" + targetFolderNodeRef + "|" + targetFolderSiteId;
           
           YAHOO.Bubbling.fire("hybridWorklfowDestinationSelected", {
                  network: remoteTenantId,
                  site: targetFolderSite,
                  folder: targetFolderNodeRef,
                  folderPath: targetFolderPath
           });
           
           // inform the forms runtime that the control value has been updated (if field is mandatory)
           if (this.options.mandatory)
           {
              YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
           }
       }, cloudFolderPicker);
	   
	   cloudAuth = new Alfresco.module.CloudAuth("${fieldHtmlId}" + "cloudAuth");
	
	   cloudFolderPicker.setOptions(
	   {
	       title: cloudFolderPicker.msg('hybridworkflow.destination-select.title'),
	       showSyncOptions: false,
	       files: [],
	       <#if field.mandatory?? && field.mandatory>
	          mandatory: true
	       <#else>
   	       mandatory: false
	       </#if>
	   });
	
	   cloudAuth.setOptions(
	   {
	      authCallback: cloudFolderPicker.showDialog,
	      authCallbackContext: cloudFolderPicker
	   });
	   
	})();
	
	YAHOO.util.Event.onContentReady("${fieldHtmlId}", function(){
       	 Alfresco.util.createYUIButton({id: "${fieldHtmlId}"}, "select-button", selectCloudFolder);
    });
//]]></script>

<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${msg("hybridworkflow.destination.network")}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></span>
      <span class="viewmode-value" id="${fieldHtmlId}-tenant">${displayValue}</span>
      <br />
      <span class="viewmode-label">${msg("hybridworkflow.destination.site")}:</span>
      <span class="viewmode-value" id="${fieldHtmlId}-site">${displayValue}</span>
      <br />
      <span class="viewmode-label">${msg("hybridworkflow.destination.folder")}:</span>
      <span class="viewmode-value" id="${fieldHtmlId}-folder">${displayValue}</span>
      
      <br />
      <br />
      
      <button id="${fieldHtmlId}-select-button">${msg("hybridworkflow.destination.button")}</button>
	  <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${field.value?html}" />
   </div>
</div>