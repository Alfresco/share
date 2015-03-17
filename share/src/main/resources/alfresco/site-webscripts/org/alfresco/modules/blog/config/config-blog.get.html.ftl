<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="config-blog">
   <div class="hd">${msg("header.configBlog")}</div>
   <div class="bd">
      <form id="${el}-form"
            action="${url.context}/proxy/alfresco/api/blog/site/${args.siteId?html}/${args.containerId?html}?alf_method=PUT"
            method="POST">
            
         <div class="yui-g">
            <h2>${msg("section.type")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.type")}</div>
            <div class="yui-u caret-fix">
               <select id="${el}-blogType" name="blogType" tabindex="0">
                  <option value=""></option>
                  <option value="wordpress">wordpress</option>
                  <option value="typepad">typepad</option>
               </select>
            </div>
         </div>

         <div class="yui-g">
            <h2>${msg("section.info")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${el}-blogid">${msg("label.id")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${el}-blogid" type="text" name="blogId" tabindex="0" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${el}-title">${msg("label.name")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${el}-title" type="text" name="blogName" tabindex="0" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${el}-description">${msg("label.description")}:</label></div>
            <div class="yui-u caret-fix">
               <textarea id="${el}-description" name="blogDescription" rows="3" tabindex="0" value=""></textarea>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${el}-url">${msg("label.url")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${el}-url" type="text" name="blogUrl" tabindex="0" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${el}-username">${msg("label.username")}:</label></div>
            <div class="yui-u">
               <input id="${el}-username" type="text" name="username" tabindex="0" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${el}-password">${msg("label.password")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${el}-password" type="password" name="password" tabindex="0" value="" />
            </div>
         </div>
            
         <div class="bdft">
            <input type="submit" id="${el}-ok" value="${msg('button.ok')}" tabindex="0"/>
            <input type="submit" id="${el}-cancel" value="${msg('button.cancel')}" tabindex="0"/>
         </div>

      </form>

   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.ConfigBlog");
//]]></script>