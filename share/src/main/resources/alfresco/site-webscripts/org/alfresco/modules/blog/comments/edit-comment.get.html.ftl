<#assign el=args.htmlid?html>
<div class="editCommentForm">
   <div class="commentFormTitle">
      ${msg("editComment")}
   </div>
   <div class="editComment">
      <form id="${el}-form" method="POST" action="">
         <input type="hidden" id="${el}-site" name="site" value="" />
         <input type="hidden" id="${el}-container" name="container" value="" />
         <input type="hidden" id="${el}-itemTitle" name="itemTitle" value="" />
         <input type="hidden" id="${el}-page" name="page" value="" />
         <input type="hidden" id="${el}-pageParams" name="pageParams" value="" />
         
         <textarea id="${el}-content" rows="8" cols="80" name="content"></textarea>
         
         <div class="commentFormAction">
            <input type="submit" id="${el}-submit"  value="${msg('action.update')}" />
            <input type="reset"  id="${el}-cancel" value="${msg('action.cancel')}" />
         </div>
      </form>
   </div>
</div>