<#assign el=args.htmlid?html>
<div id="${el}-form-title" class="replyTitle"></div>
<div class="editReplyForm">
   <div id="${el}-replyto" class="replyTo hidden"></div>
   
   <div class="editReply">
      <form id="${el}-form" name="replyForm" method="POST" action="">
         <div>
            <input type="hidden" id="${el}-site"name="site" value="" />
            <input type="hidden" id="${el}-container"name="container" value="" />
            <input type="hidden" id="${el}-page" name="page" value="discussions-topicview" />
            <textarea id="${el}-content" rows="8" cols="80" name="content" class="yuieditor"></textarea>
            <div class="nodeFormAction">
               <input type="submit" id="${el}-submit" />
               <input type="reset"  id="${el}-cancel"  value="${msg('action.cancel')}" />
            </div>
         </div>
      </form>
   </div>
</div>