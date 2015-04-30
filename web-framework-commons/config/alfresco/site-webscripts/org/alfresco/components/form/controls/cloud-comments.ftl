<div class="form-field">
   <div class="viewmode-field">
      <span class="viewmode-label">${field.label?html}:</span><br />
      <span class="viewmode-value">
         <ul>
         <#assign comments=multiValue.getUnescapedValues(field.value)>
         <#list comments as c>
            <#if c?length != 0>
               <li>
                  <#assign choice=c?substring(0,1)>
                  <#assign comment=c?substring(1)>
                  ${comment}
                  <#if choice == "1">
                     &nbsp;(${msg("hybridworkflow.review.approved")})
                  <#else>
                      &nbsp;(${msg("hybridworkflow.review.rejected")})
                  </#if>
               </li>
            </#if>
         </#list>
         </ul>
      </span>
   </div>
</div>