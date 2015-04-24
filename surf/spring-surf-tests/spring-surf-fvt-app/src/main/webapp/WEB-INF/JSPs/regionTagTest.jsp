<%@ page buffer="none" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="surf" uri="http://www.springframework.org/tags/surf" %>
<surf:region name="global_scope_jsp_component-region" />
<surf:region name="embed_this_region" scope="page" />
<surf:region name="embed_this_region" scope="template" />
<surf:region name="global_scope_jsp_component-region" chrome="region_jsp-chrome" />
<surf:region name="embed_this_region" scope="page" chrome="region_jsp-chrome" />
<surf:region name="embed_this_region" scope="template" chrome="region_jsp-chrome" />
<surf:region name="global_scope_jsp_component-region" chromeless="true"  />
<surf:region name="embed_this_region" scope="page" chromeless="true" />
<surf:region name="embed_this_region" scope="template" chromeless="true" />