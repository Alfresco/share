<@standalone>
   <@markup id="css" >
      <#include "form.css.ftl"/>
   </@>
   
   <@markup id="js">
      <#include "form.js.ftl"/>
   </@>
   
   <@markup id="widgets">
      <@createWidgets/>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#import "form.lib.ftl" as formLib />
         
         <#if error?exists>
            <div class="error">${error}</div>
         <#elseif form?exists>
            <#assign formId=args.htmlid?js_string?html + "-form">
            <#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>
            
            <#if form.viewTemplate?? && form.mode == "view">
               <#include "${form.viewTemplate}" />
            <#elseif form.editTemplate?? && form.mode == "edit">
               <#include "${form.editTemplate}" />
            <#elseif form.createTemplate?? && form.mode == "create">
               <#include "${form.createTemplate}" />
            <#else>
               <#if formUI == "true">
                  <@formLib.renderFormsRuntime formId=formId />
               </#if>
               
               <@formLib.renderFormContainer formId=formId>
                  <#list form.structure as item>
                     <#if item.kind == "set">
                        <#if item.children?size &gt; 0>
                           <@formLib.renderSet set=item />
                        </#if>
                     <#else>
                        <@formLib.renderField field=form.fields[item.id] />
                     </#if>
                  </#list>
               </@>
            </#if>
         <#else>
            <div class="form-container">${msg("form.not.present")}</div>
         </#if>
      </@>
   </@>
</@>
