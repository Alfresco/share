<body>
   
   <h1>
      <customer:property nodeRef="<%=mainTextNodeRef%>" property="cm:title" />
      <awe:markContent id="<%=mainTextNodeRef%>" title="Edit Press Release" />
   </h1>
   
   <h3>
      <customer:property nodeRef="<%=mainTextNodeRef%>" property="cm:description" />
   </h3>
   
   <div class="content">
      <customer:content nodeRef="<%=mainTextNodeRef%>" />
   </div>
   
   <div class="trailer-text">
      <awe:markContent id="<%=subTextNodeRef%>" formId="description" title="Edit Trailing Text" nestedMarker="true" />
      <customer:property nodeRef="<%=subTextNodeRef%>" property="cm:description" />
   </div>
   
   <div class="links">
      <customer:content nodeRef="<%=subTextNodeRef%>" />
   </div>
   <awe:markContent id="<%=subTextNodeRef%>" title="Edit Links" />
   
   <div class="copyright">&copy; 2015 Customer, Inc. All Rights Reserved.</div>
   
   <awe:endTemplate />
</body>