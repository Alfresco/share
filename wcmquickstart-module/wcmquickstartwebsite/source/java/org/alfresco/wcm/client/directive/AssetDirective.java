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
package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive to stream a repository asset's content to the HTTP
 * response. Usage: <@streamasset asset=xxx/> where xxx is a variable which
 * references an asset object
 * 
 * @author Chris Lack
 */
public class AssetDirective implements TemplateDirectiveModel
{
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException
    {
        if (params.size() != 1)
        {
            throw new TemplateModelException("asset directive expects one parameter of type Asset");
        }

        StringModel assetParam = (StringModel) params.get("asset");
        if (assetParam == null || !(assetParam.getWrappedObject() instanceof Asset))
        {
            throw new TemplateModelException("asset directive expects asset parameter with a value of class Asset");
        }
        Asset asset = (Asset) assetParam.getWrappedObject();

        // Get the assets content stream
        ContentStream stream = asset.getContentAsInputStream();

        // Write the content stream to the servlet out
        Writer out = env.getOut();
        stream.write(out);
    }

}
