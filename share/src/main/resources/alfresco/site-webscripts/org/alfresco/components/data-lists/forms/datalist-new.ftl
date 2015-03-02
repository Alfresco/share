<#assign id=args.htmlid?html>
<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>

<div id="${id}-dialog">
   <div id="${id}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">

      <div id="${formId}-container" class="form-container">

         <div class="yui-g">
            <h2 id="${id}-dialogHeader">${msg("header")}</h2>
         </div>

         <#if form.showCaption?exists && form.showCaption>
            <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
         </#if>
      
         <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
   
         <#if form.destination??>
            <input id="${formId}-destination" name="alf_destination" type="hidden" value="${form.destination?html}" />
         </#if>
   
            <div id="${formId}-fields" class="form-fields">

               <div class="form-field">
                  <label for="${id}-itemType-field" id="${id}-dataListItemType">${msg("label.item-type")}</label>
                  <div id="${id}-itemTypesContainer" class="item-types"></div>
                  <input type="hidden" value="" name="prop_dl_dataListItemType" id="${id}-dataListItemType-field">
               </div>

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