<#macro form e>
<#escape x as x?xml>
               <field-visibility>
                  <#list e.properties as p>
                  <show id="${p.name}" <#if p.mode??>for-mode="${p.mode}"</#if> <#if p.force?? && p.force>force="true"</#if> />
                  </#list>
               </field-visibility>
               <appearance>
                  <#if e.sets??>
                  <#list e.sets as set>
                  <set <#if set.template??>template="${set.template}"</#if> appearance="${set.appearance}" <#if set.label??>label-id="${set.label}"</#if> id="${set.id}" />
                  </#list>
                  </#if>
                  <#if e.fields??>
                  <#list e.fields as field>
                  <field id="${field.id}" <#if field.set??>set="${field.set}"</#if> <#if field.readonly?? && field.readonly>read-only="true"</#if>>
                     <#if field.template?? || (field.params?? && field.params?size!=0)>
                     <control <#if field.template??>template="${field.template}"</#if>>
                        <#if field.params??>
                        <#list field.params?keys as param>
                        <control-param name="${param}">${field.params[param]}</control-param>
                        </#list>
                        </#if>
                     </control>
                     </#if>
                  </field>
                  </#list>
                  </#if>
               </appearance>
</#escape>
</#macro>
<#escape x as x?xml>
<module>
   <id>${moduleName}</id>
   <auto-deploy>true</auto-deploy>
   <configurations>
      
      <!-- Document Library config section -->
      <config evaluator="string-compare" condition="DocumentLibrary" replace="false">
         <#if aspects?size != 0>
         <aspects>
            <visible>
               <#list aspects as a>
               <aspect label="${a.title!a.name}" name="${a.name}" />
               </#list>
            </visible>
         </aspects>
         </#if>
         <#if subtypes?size != 0>
         <types>
            <#list subtypes as st>
            <type name="${st.name}">
               <#list st.subtypes as t>
               <subtype label="${t.title!t.name}" name="${t.name}" />
               </#list>
            </type>
            </#list>
         </types>
         </#if>
      </config>

      <!-- Form configuration section - type -->
      <#list types as t>
      <#if t.properties??>
      <config evaluator="node-type" condition="${t.name}">
         <forms>
            <form>
               <@form e=t/>
            </form>
            
            <!-- Document Library pop-up Edit Metadata form -->
            <form id="doclib-simple-metadata">
               <edit-form template="../documentlibrary/forms/doclib-simple-metadata.ftl" />
               <@form e=t/>
            </form>
            
            <!-- Document Library Inline Edit form -->
            <form id="doclib-inline-edit">
               <field-visibility>
                  <show id="cm:name" />
                  <show id="cm:content" force="true" />
                  <show id="cm:title" force="true" />
                  <show id="cm:description" force="true" />
               </field-visibility>
               <appearance>
                  <field id="cm:name">
                    <control>
                       <control-param name="maxLength">255</control-param>
                    </control>
                  </field>
                  <field id="cm:title">
                     <control template="/org/alfresco/components/form/controls/textfield.ftl" />
                  </field>
                  <field id="cm:content" label-id="">
                     <control>
                        <control-param name="editorAppearance">explorer</control-param>
                        <control-param name="forceEditor">true</control-param>
                     </control>
                  </field>
               </appearance>
            </form>
         </forms>
      </config>
      </#if>
      </#list>
      
      <!-- Form configuration section - aspect -->
      <#list aspects as a>
      <#if a.properties??>
      <config evaluator="aspect" condition="${a.name}">
         <forms>
            <form>
               <@form e=a/>
            </form>
         </forms>
      </config>
      </#if>
      </#list>
      
      <!-- Entity form designer configuration -->
      <config evaluator="string-compare" condition="FormDefinition">
      <#list entities as t>
         <form-definition id="${t.name}">${t.form}</form-definition>
      </#list>
      </config>

   </configurations>
</module>
</#escape>