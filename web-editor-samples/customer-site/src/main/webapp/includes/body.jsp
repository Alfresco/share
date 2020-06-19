<%--

    Copyright 2005 - 2020 Alfresco Software Limited.

    This file is part of the Alfresco software.
    If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
    Otherwise, the software is provided under the following open source license terms:

    Alfresco is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Alfresco is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Alfresco. If not, see <http://www.gnu.org/licenses/>.

--%>
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
   
   <div class="copyright">&copy; 2016 Customer, Inc. All Rights Reserved.</div>
   
   <awe:endTemplate />
</body>