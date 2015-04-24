<#if translationData??>
   <#assign id = args.htmlid?html>
   <script type="text/javascript">
      new Alfresco.component.ManageTranslations("${args.htmlid?js_string}").setMessages(${messages});
      fnMarkAsTranslation = Alfresco.component.ManageTranslations.markAsTranslation;
   </script>

   <div class="manage-translations">

   <#if !translationData.translationEnabled>
      <div class="status-banner theme-bg-color-2 theme-border-4">
         <span class="info">${msg("message.translations-not-enabled")}</span>
      <#if translationData.locale??>
         <span>&nbsp;<a href="#" onclick="return fnMarkAsTranslation('${nodeRef}', '${translationData.locale?js_string}')">
            ${msg("message.mark-translation", translationData.localeName?html)}
         </a></span>
      </#if>
      </div>
   </#if>

      <h2>${msg("header.translations")}</h2>
      <div id="${id}-markup">
         <table id="${id}-languages">
            <thead>
               <tr>
                  <th>${msg("label.language")}</th>
                  <th>${msg("label.name")}</th>
                  <th>${msg("label.action")}</th>
               </tr>
            </thead>
            <tbody>
   <#list translationData.locales as locale>
               <tr>
                  <td title="${locale.id?html}">${locale.name?html}</td>
      <#if translationData.translations[locale.id]??>
               <#assign translation = translationData.translations[locale.id]>
                  <td>${translation.name?html}</td>
                  <td><a href="inline-edit?nodeRef=${translation.nodeRef}">${msg("button.edit")}</a></td>
      <#else>
                  <td>${msg("label.not-applicable")}</td>
                  <td>
         <#assign createContentURL>create-content?mimeType=text/html&name=${translationData.name?url}&translationOf=${nodeRef}&language=${locale.id?url}&itemKind=type&itemId=${translationData.type?url}&isContainer=${translationData.isContainer?string}</#assign>
         <#if translationData.translationEnabled>
            <#if translationData.parents[locale.id]??>
               <#assign parent = translationData.parents[locale.id]>
               <#assign orphan = !parent.allPresent>
                     <a href='${createContentURL}&destination=${parent.nodeRef}&orphan=${orphan?string}'>${msg("button.create")}</a>
            <#else>
                     <a href='${createContentURL}&destination=${translationData.parentNodeRef}'>${msg("button.create")}</a>
            </#if>
         <#else>
                     <a href="#" onclick='fnMarkAsTranslation("${nodeRef}", "${locale.id?js_string}")'>${msg("message.mark-translation", locale.name?html)}</a>
         </#if>
      </#if>
                  </td>
               </tr>
   </#list>
            </tbody>
         </table>
      </div>
<#else>
      <h1>${msg("message.no-definitions")}</h1>
</#if>
   </div>
