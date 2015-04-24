package org.springframework.extensions.directives;

import java.io.IOException;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.AbstractFreeMarkerDirective;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.ComponentImpl;
import org.springframework.extensions.surf.util.XMLUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * The purpose of this directive is to circumvent the requirement to create and persist Components as a means of executing
 * Aikau pages defined by a single WebScript. Previously the solution had been to use the {@link CreateComponentDirective} directive
 * prior to using the {@link RegionDirective}. However, this resulted in calls to persist the Component and were ultimately unnecessary.
 * This solution simply requires that a new {@link Component} is created on demand and not persisted.
 * 
 * @author Dave Draper
 */
public class AutoComponentRegionDirective extends AbstractFreeMarkerDirective
{
    public AutoComponentRegionDirective(String directiveName)
    {
        super(directiveName);
    }
    
    /**
     * The name to reference this directive by in FreeMarker templates.
     */
    public static final String DIRECTIVE_NAME = "autoComponentRegion";

    /**
     * A {@link RequestContext} is required to be passed in the call to the {@link RenderService.renderComponent}
     * method.
     */
    private RequestContext context = null;
    
    /**
     * Set the {@link RequestContext} required for rendering the {@link Component}.
     * 
     * @param context
     */
    public void setRequestContext(RequestContext context)
    {
        this.context = context;
    }
    
    /**
     * A {@link RenderService} is required to render the {@link Component} that is created.
     */
    private RenderService renderService = null;
    
    /**
     * Set the {@link RenderService} required to render the {@link Component}
     * 
     * @param renderService
     */
    public void setRenderService(RenderService renderService)
    {
        this.renderService = renderService;
    }
    
    /**
     * Creates a new {@link Component} and sets it to use the supplied WebScript URI and is then immediately rendered
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, 
            Map params, 
            TemplateModel[] loopVars, 
            TemplateDirectiveBody body) throws TemplateException, IOException
    {
        if (this.context.isPassiveMode())
        {
            // No action required for passive mode
        }
        else
        {
            String objectId = "tmpComponent";
            String objectTypeId = "component";
            String xml = "<" + objectTypeId + "></" + objectTypeId + ">";
            try
            {
                Document document = XMLUtil.parse(xml);
                ModelPersisterInfo info = new ModelPersisterInfo("tmp", "tmp", false);
                Component component = new ComponentImpl(objectId, info, document);
                String uri = getStringProperty(params, "uri", true);
                component.setURI(uri);
                component.setURL(uri);
                renderService.renderComponent(context, RenderFocus.BODY, component, null, true);
            }
            catch (DocumentException e)
            {
                throw new TemplateException("Failed to parse generated XML: " + objectId, env);
            }
        }
    }
}
