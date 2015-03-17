<#assign el=args.htmlid?html>
<div id="${el}-configDialog">
   <div class="hd">${msg("label.title")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-webviewTitle">${msg("label.linkTitle")}</label>:</div>
            <div class="yui-u"><input id="${el}-webviewTitle" type="text" name="webviewTitle" tabindex="0" value="${(webviewTitle!"")?html}" maxlength="256"/>&nbsp;</div>
            <div class="yui-u first"><label for="${el}-url">${msg("label.url")}</label>:</div>
            <div class="yui-u"><input id="${el}-url" type="text" name="url" tabindex="0" value="${(uri!"")?html}"/>&nbsp;*</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>