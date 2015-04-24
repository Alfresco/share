package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.util.Pair;

public interface TransferPathMapper
{

    public void addPathMapping(Path source, Path target);

    public void addPathMapping(Pair<Path,Path> mapping);

}
