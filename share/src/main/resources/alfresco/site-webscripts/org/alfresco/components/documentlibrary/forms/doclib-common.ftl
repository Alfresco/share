<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>

<#assign el=args.htmlid?html>
<div id="${el}-dialog">
   <div id="${el}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">

      <div id="${formId}-container" class="form-container">

         <div class="yui-g">
            <h2 id="${el}-dialogHeader">${msg("header")}</h2>
         </div>
   
         <#if form.showCaption?exists && form.showCaption>
            <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
         </#if>
      
         <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl?html}">
   
         <#if form.destination??>
            <input id="${formId}-destination" name="alf_destination" type="hidden" value="${form.destination?html}" />
         </#if>
   
            <div id="${formId}-fields" class="form-fields">
               <#list form.structure as item>
                  <#if item.kind == "set">
                     <@formLib.renderSet set=item />
                  <#else>
                     <@formLib.renderField field=form.fields[item.id] />
                  </#if>
               </#list>
            </div>

            <div class="bdft">
               <input id="${formId}-submit" type="submit" value="${msg("form.button.submit.label")}" />
               &nbsp;<input id="${formId}-cancel" type="button" value="${msg("form.button.cancel.label")}" />
            </div>
      
         </form>

      </div>
   </div>
</div>