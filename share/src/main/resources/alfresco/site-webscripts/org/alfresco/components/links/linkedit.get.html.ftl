<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/modules/taglibrary/taglibrary.css" group="links"/>
   <@link href="${url.context}/res/components/links/linkedit.css" group="links"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/modules/taglibrary/taglibrary.js" group="links"/>
   <@script src="${url.context}/res/components/links/linkedit.js" group="links"/>
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="html">
   <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
   <@uniqueIdDiv>
      <div class="page-form-header">
      <#if page.url.args.linkId??>
         <h1>${msg("form.editLink")}</h1>
      <#else>
         <h1>${msg("form.createLink")}</h1>
      </#if>
         <hr />
      </div>
      
      <div id="${args.htmlid}-div" class="page-form-body hidden">
         <form id="${args.htmlid}-form" method="post" action="">
            <fieldset>          
               <input type="hidden" name="page" value="links-view"/>
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-title">${msg("label.title")}:</label>
                  </div>
                  <div class="yui-u">
                     <input class="wide" type="text" id="${args.htmlid}-title" name="title" value="" tabindex="0"/><span class="lbl dot">*</span>
                  </div>
               </div>
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-url">${msg("form.url")}:</label>
                  </div>
                  <div class="yui-u">
                     <input class="wide" id="${args.htmlid}-url" type="text" name="url" tabindex="0"/><span class="lbl dot">*</span>
                  </div>
               </div>
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-description">${msg("label.description")}:</label>
                  </div>
                  <div class="yui-u">
                     <textarea class="wide" id="${args.htmlid}-description" type="textarea" rows="5" name="description" tabindex="0"></textarea>
                  </div>
               </div>
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-internal">${msg("form.internal")}:</label>
                  </div>
                  <div class="yui-u">
                     <input id="${args.htmlid}-internal" type="checkbox" name="internal" tabindex="0"/>
                     <span class="help">${msg("form.internalDescription")}</span>
                  </div>
               </div>
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-tag-input-field">${msg("label.tags")}:</label>
                  </div>
                  <div class="yui-u">
                     <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
                  </div>
               </div>
      
               <div class="yui-gd">
                  <div class="yui-u first">&nbsp;</div>
                  <div class="yui-u">
                     <input type="submit" id="${args.htmlid}-ok" value="" tabindex="0"/>
                     <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0"/>
                  </div>
               </div>
            </fieldset>
         </form>
      </div>
   </@>
</@>