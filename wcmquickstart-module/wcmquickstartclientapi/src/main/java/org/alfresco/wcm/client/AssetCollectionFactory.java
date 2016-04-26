package org.alfresco.wcm.client;

import java.util.Date;

public interface AssetCollectionFactory extends CollectionFactory
{
    Date getModifiedTimeOfAssetCollection(String assetCollectionId);
}
