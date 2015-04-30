   <div id="awe-login" class="login-panel">
      <div class="hd">
         <div class="login-logo"></div>
      </div>
      <form id="${args.htmlid}-form" accept-charset="UTF-8" method="post" action="${url.context}/page/dologin">
         <div class="bd">
         <div class="form-fields">
         <fieldset>
            <div class="form-field">
               <label id="${args.htmlid}-txt-username" for="${args.htmlid}-username">${msg('label.username')}</label>
            </div>
            <div class="form-field">
               <input type="text" id="${args.htmlid}-username" name="username" class="awe-login-username" maxlength="255" style="width:200px" value="<#if lastUsername??>${lastUsername?html}</#if>" />
            </div>
            <div class="form-field">
               <label id="${args.htmlid}-txt-password" for="${args.htmlid}-password">${msg('label.password')}</label>
            </div>
            <div class="form-field">
               <input type="password" id="${args.htmlid}-password" name="password" class="awe-login-password" maxlength="255" style="width:200px"/>
            </div>
          </div>
         <input type="hidden" id="success" name="success" value="${successUrl!'metadata'}"/>
         <input type="hidden" name="failure" value="<#assign link>${url.context}/page/type/login</#assign>${link?html}?error=true"/>
         </fieldset>
         <div class="ft">
            <div class="form-field">
               <span id="${args.htmlid}-btn-login" class="yui-button yui-push-button">
                  <span class="first-child">
                       <input type="submit" class="login-button" value="${msg('label.login')}"/>
                  </span>
               </span>
            </div>
         </div>
         </div>
      </form>
      </div>
   </div>
<script>//<![CDATA[
Alfresco.util.addMessages(${messages}, "org.alfresco.awe.ui.LoginPanel");
//]]></script>
