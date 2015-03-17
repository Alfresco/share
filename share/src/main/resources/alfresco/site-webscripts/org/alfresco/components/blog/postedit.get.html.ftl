<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/taglibrary/taglibrary.css" group="blog"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/modules/taglibrary/taglibrary.js" group="blog"/>
   <@script type="text/javascript" src="${url.context}/res/components/blog/postedit.js" group="blog"/>
</@>

<@markup id="widgets">
   <@createWidgets group="blog"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="page-form-header">
      <#if page.url.args.postId??>
         <h1>${msg("editPost")}</h1>
      <#else>
         <h1>${msg("createPost")}</h1>
      </#if>
         <hr/>
      </div>
      <div id="${args.htmlid}-div" class="page-form-body hidden">
         <form id="${args.htmlid}-form" method="post" action="">
            <fieldset>
               <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
               <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
               <input type="hidden" id="${args.htmlid}-page" name="page" value="blog-postview" />
               <input type="hidden" id="${args.htmlid}-draft" name="draft" value=""/>
      
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-title">${msg("label.title")}:</label>
                  </div>
                  <div class="yui-u">
                     <input class="wide" type="text" id="${args.htmlid}-title" name="title" value="" />&nbsp;*
                  </div>
               </div>
      
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${args.htmlid}-content">${msg("text")}:</label>
                  </div>
                  <div class="yui-u">
                     <textarea rows="8" id="${args.htmlid}-content" name="content" cols="180" class="yuieditor"></textarea> 
                  </div>
               </div>
      
               <div class="yui-gd">
                  <div class="yui-u first">
                     <label for="${htmlid}-tag-input-field">${msg("label.tags")}:</label>
                  </div>
                  <div class="yui-u">
                     <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
                     <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
                  </div>
               </div>
      
               <div class="yui-gd">
                  <div class="yui-u first">&nbsp;</div>
                  <div class="yui-u">
                     <input type="submit" id="${args.htmlid}-save-button" value="" />
                     <input type="button" id="${args.htmlid}-publish-button" value="${msg('action.publish')}" class="hidden" />
                     <input type="reset" id="${args.htmlid}-cancel-button" value="${msg('action.cancel')}" />
                  </div>
               </div>
            </fieldset>
         </form>
      </div>
   </@>
</@>