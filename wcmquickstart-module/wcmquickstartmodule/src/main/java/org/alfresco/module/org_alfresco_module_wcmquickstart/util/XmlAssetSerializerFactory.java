package org.alfresco.module.org_alfresco_module_wcmquickstart.util;

import org.alfresco.service.namespace.NamespaceService;

public class XmlAssetSerializerFactory implements AssetSerializerFactory
{
    private NamespaceService namespaceService;
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    @Override
    public AssetSerializer getAssetSerializer()
    {
        AssetSerializerXmlImpl serializer = new AssetSerializerXmlImpl();
        serializer.setNamespaceService(namespaceService);
        return serializer;
    }

}
