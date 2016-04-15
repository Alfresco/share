package org.alfresco.web.config.forms;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;forms&gt; (plural) elements. &lt;form&gt; (singular) elements have their own
 * reader class which is delegated to by this one.
 * 
 * @author Neil McErlean.
 * @see ConstraintHandlersElementReader
 * @see DefaultControlsElementReader
 * @see DependenciesElementReader
 * @see FormElementReader
 */
public class FormsElementReader implements ConfigElementReader
{
    public static final String ATTR_NAME_ID = "id";
    public static final String ELEMENT_FORMS = "forms";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element formsElement)
    {
        FormsConfigElement result = null;
        if (formsElement == null)
        {
            return null;
        }

        String name = formsElement.getName();
        if (!name.equals(ELEMENT_FORMS))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_FORMS
                    + " elements, the element passed was '" + name + "'");
        }

        result = new FormsConfigElement();
        
        // Go through each of the <form> tags under <forms>
        for (Object obj : formsElement.selectNodes("./form")) {
            Element formElement = (Element)obj;
            
            FormElementReader formReader = new FormElementReader();
            FormConfigElement form = (FormConfigElement)formReader.parse(formElement);

            if (form.getId() == null)
            {
                result.setDefaultForm(form);
            }
            else
            {
                result.addFormById(form, form.getId());
            }
        }
        
        // Go through each of the <default-controls> tags under <forms>
        for (Object obj : formsElement.selectNodes("./default-controls")) {
            Element defltCtrlsElement = (Element)obj;
            
            DefaultControlsElementReader defltCtrlsReader = new DefaultControlsElementReader();
            DefaultControlsConfigElement defltCtrlsCE = (DefaultControlsConfigElement)defltCtrlsReader.parse(defltCtrlsElement);

            result.setDefaultControls(defltCtrlsCE);
        }
        
        // Go through each of the <constraint-handlers> tags under <forms>
        for (Object obj : formsElement.selectNodes("./constraint-handlers")) {
            Element constraintHandlersElement = (Element)obj;
            
            ConstraintHandlersElementReader constraintHandlersReader = new ConstraintHandlersElementReader();
            ConstraintHandlersConfigElement constraintHandlersCE = (ConstraintHandlersConfigElement)constraintHandlersReader.parse(constraintHandlersElement);

            result.setConstraintHandlers(constraintHandlersCE);
        }

        // Go through each of the <dependencies> tags under <forms>
        for (Object obj : formsElement.selectNodes("./dependencies")) {
            Element depsElement = (Element)obj;
            
            DependenciesElementReader depsReader = new DependenciesElementReader();
            DependenciesConfigElement depsCE = (DependenciesConfigElement)depsReader.parse(depsElement);

            result.setDependencies(depsCE);
        }
        
        return result;
    }
}
