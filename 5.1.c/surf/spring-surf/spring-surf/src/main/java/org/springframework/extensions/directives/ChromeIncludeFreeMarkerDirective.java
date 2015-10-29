/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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
package org.springframework.extensions.directives;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluation;
import org.springframework.extensions.surf.extensibility.impl.ExtensibilityDebugData;
import org.springframework.extensions.surf.extensibility.impl.ExtensibilityDebugData.Data;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.render.RenderService.SubComponentData;
import org.springframework.extensions.surf.types.Chrome;
import org.springframework.extensions.surf.types.SubComponent.RenderData;

import freemarker.core.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>A FreeMarker directive that uses a <code>PresentationService</code> to render regions or
 * component included in chrome.</p>
 * 
 * @author David Draper
 */
public class ChromeIncludeFreeMarkerDirective extends RenderServiceFreeMarkerDirective
{
    /**
     * <p>Instantiates a new <code>ChromeIncludeFreeMarkerDirective</code>. The <code>RenderService</code> will be 
     * used by calling its <code>renderChromeInclude</code> method to generate the output. The directive name is only needed
     * for generating useful exception messages to assist debugging problems but an effort should be made to set it 
     * correctly</p>
     * 
     * @param directiveName The name of the directive represented by the instance of this class.
     * @param context A <code>RequestContext</code> required as an argument to the <code>RenderService</code> method.
     * @param renderService A <code>RenderService</code> used to generate the output of the directive by calling 
     * its <code>renderRegion</code> method.
     */
    public ChromeIncludeFreeMarkerDirective(String directiveName, RequestContext context, ModelObject object, RenderService renderService)
    {
        super(directiveName, context, object, renderService);  
    }
    
    /**
     * <p>This method is declared by the <code>TemplateDirectiveModel</code> interface that the abstract
     * superclass <code>AbstractFreeMarkerDirective</code> implements. It retrieves any properties supplied
     * when invoking the directive and passes them onto the <code>PresentationService.renderChromeInclude</code>
     * method.
     */
    public void execute(Environment env, 
                        @SuppressWarnings("rawtypes") Map params, 
                        TemplateModel[] loopVars, 
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {        
        try
        {
            RequestContext context = getRequestContext();
            getRenderService().renderChromeInclude(context, getObject());
            
            // Collect SurfBug data if enabled - this is done AFTER the nested content of the Chrome
            // has been processed so that all the raw extensibility/rendering data will have been collected
            // and stored in the context... 
            if (Boolean.parseBoolean((String) context.getValue(WebFrameworkConstants.RENDER_DATA_SURFBUG_ENABLED)))
            {
                postProcessSurfBugData(context, env);
            }
        }
        catch (RequestDispatchException e)
        {
            throw new TemplateException(e, env);
        }
    }
    
    /**
     * <p>Updates the current model with any raw debug data that was added to the {@link RequestContext} whilst
     * processing the nested {@link Chrome} contents. This data will then be used by SurfBug to create a table
     * of all the debug data.</p>
     *  
     * @param context The current {@link RequestContext}
     * @param env The current FreeMarker {@link Environment}
     */
    private void postProcessSurfBugData(RequestContext context, Environment env)
    {
        // Get the data about any extensibility directives that were used processing the sub-component...
        ExtensibilityModel extModel = context.getCurrentExtensibilityModel();
        if (extModel != null && extModel.getChildDebugData() != null)
        {
            ExtensibilityDebugData data = extModel.getChildDebugData();
            HashMap<String, HashMap<String, HashMap<String, String>>> directiveToIdToDataMap = new HashMap<String, HashMap<String,HashMap<String,String>>>();
            for (String directive: data.getDirectives())
            {
                HashMap<String, HashMap<String, String>> idToDataMap = new HashMap<String, HashMap<String,String>>();
                directiveToIdToDataMap.put(directive, idToDataMap);
                for (Data currData: data.getDirectiveData(directive))
                {
                    HashMap<String, String> directiveData = new HashMap<String, String>();
                    directiveData.put("id", currData.getId());
                    directiveData.put("directive", currData.getDirective());
                    
                    StringBuilder sources = new StringBuilder();
                    for (String source: currData.getPaths())
                    {
                        sources.append(source);
                        sources.append(", ");
                    }
                    if (sources.length()>0)
                    {
                        sources.delete(sources.lastIndexOf(","), sources.length()-1);
                    }
                    directiveData.put("sources", sources.toString());
                    idToDataMap.put(currData.getId(), directiveData);
                }
            }
            TemplateModel model = new SimpleHash(directiveToIdToDataMap);
            env.setVariable("renderedExtensibilityArtefacts", model);
        }
        
        // Get the SubComponent rendering data...
        // This is the information that illustrates which module evaluations, etc. ultimately determined
        // the SubComponent contents...
        if (WebFrameworkConstants.RENDER_SUB_COMPONENT.equals(context.getValue(WebFrameworkConstants.RENDER_TYPE)))
        {
            SubComponentData subComponentData = (SubComponentData) context.getValue(WebFrameworkConstants.RENDER_DATA_SUB_COMPONENT);
            RenderData renderData = getRequestContext().getSubComponentDebugData(subComponentData.getSubComponent().getId());
            
            HashMap<String, Object> renderDataMap = new HashMap<String, Object>();
            if (renderData != null)
            {
                renderDataMap.put("Evaluated URI", renderData.getUri());
                SubComponentEvaluation evaluation = renderData.getEvaluation();
                if (evaluation != null)
                {
                    renderDataMap.put("Evaluated By", evaluation.getId());
                }
                else
                {
                    renderDataMap.put("Evaluated By", "");
                }
            }
            TemplateModel subCompRenderData = new SimpleHash(renderDataMap);
            env.setVariable("subCompRenderData", subCompRenderData);
            if (renderData != null)
            {
                TemplateModel  subCompRenderProperties = new SimpleHash(renderData.getProperties());
                env.setVariable("subCompEvaluatedProps", subCompRenderProperties);
            }
        }
    }
}
