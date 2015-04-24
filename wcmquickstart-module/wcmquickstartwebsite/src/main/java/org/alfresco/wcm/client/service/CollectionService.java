/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.service;

import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.CollectionFactory;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

public class CollectionService extends BaseProcessorExtension implements CollectionFactory
{
    private CollectionFactory collectionFactory;

	public void setCollectionFactory(CollectionFactory collectionFactory) {
		this.collectionFactory = collectionFactory;
	}

	@Override
	public AssetCollection getCollection(String sectionId, String collectionName) {
		return collectionFactory.getCollection(sectionId, collectionName);
	}

	@Override
	public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults) {
		return collectionFactory.getCollection(sectionId, collectionName, resultsToSkip, maxResults);
	}

}
