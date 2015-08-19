<#assign id=args.htmlid?html>
<div id="${id}-configDialog">
   <div class="hd">${msg("label.header")}</div>
   <div class="bd">
      <form id="${id}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${id}-title">${msg("label.title")}:</label></div>
            <div class="yui-u" >
               <input type="text" name="title" id="${id}-title" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${id}-text">${msg("label.text")}:</label></div>
            <div class="yui-u" >
               <textarea name="text" id="${id}-text" rows="8" cols="80"></textarea>
            </div>
         </div>
         <div class="bdft">
            <input type="submit" id="${id}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${id}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>