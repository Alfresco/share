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

package org.springframework.extensions.webscripts;

import java.util.List;

import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * @author Kevin Roast
 * @author Will Abson
 * 
 * @see org.springframework.extensions.webscripts.MessageMethod
 * 
 * Custom FreeMarker Template language method.
 * <p>
 * Returns an I18N message resolved for the current locale and specified message ID.
 * <p>
 * Usage: message(String id)
 */
@ScriptClass 
(
        help="Returns an I18N message resolved for the current locale and specified message ID.\n\nUsage: message(String id)",
        code="${message(\"templates.doc_info.name\")}",
        types=
        {
                ScriptClassType.TemplateAPI
        }
)
public class FreemarkerI18NMessageMethod implements TemplateMethodModelEx
{
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        String result = "";
        int argSize = args.size();
        
        if (argSize != 0)
        {
            String id = "";
            Object arg0 = args.get(0);
            if (arg0 instanceof TemplateScalarModel)
            {
                id = ((TemplateScalarModel)arg0).getAsString();
            }
            
            if (id != null)
            {
                if (argSize == 1)
                {
                    // shortcut for no additional msg params
                    result = I18NUtil.getMessage(id);
                }
                else
                {
                    Object[] params = new Object[argSize - 1];
                    for (int i = 0; i < argSize-1; i++)
                    {
                        // ignore first passed-in arg which is the msg id
                        Object arg = args.get(i + 1);
                        if (arg instanceof TemplateScalarModel)
                        {
                            params[i] = ((TemplateScalarModel)arg).getAsString();
                        }
                        else if (arg instanceof TemplateNumberModel)
                        {
                            params[i] = ((TemplateNumberModel)arg).getAsNumber();
                        }
                        else if (arg instanceof TemplateDateModel)
                        {
                            params[i] = ((TemplateDateModel)arg).getAsDate();
                        }
                        else
                        {
                            params[i] = "";
                        }
                    }
                    result = I18NUtil.getMessage(id, params);
                }
            }
        }
        
        return result;
    }
}