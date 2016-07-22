<@markup id="html">
   <#if redirectUrl??>
       <script type="text/javascript">
            document.location.href = "${redirectUrl}";
       </script>
   </#if>
</@>