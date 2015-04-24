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
package org.springframework.extensions.surf.test.extensibility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.springframework.extensions.surf.test.processor.JAXBTestReader;
import org.testng.annotations.Test;

public class ExtensibilityTest extends AbstractJettyTest
{
    public static final String EXTENSIBILITY_PAGE = "extensibility-page";
    public static final String EXTENSIBILITY_CONFIG_PAGE = "extensibility-config-page";
    public static final String MODULE_DEPLOY_POST = "service/modules/deploy";
    private static final HashMap<String, String> emptyMultiPartData = new HashMap<String, String>();
    
    /**
     * <p>Common method for checking the extensibility page.</p>
     * @param expectedResults
     * @throws JAXBException
     */
    private void checkExtensibilityPage(String[] expectedResults) throws JAXBException
    {
        WebTestArtifact wta = makeHttpRequest(EXTENSIBILITY_PAGE);
        Assert.assertEquals(200, wta.getResponseCode());
        List<String> results = JAXBTestReader.processResults(wta.getResponse()); 
        Assert.assertArrayEquals(expectedResults, results.toArray());
    }
    
    /**
     * <p>Common method for checking the extensibility configuration page - this page uses generic configuration
     * to set the content and is provided for testing configuration module extensions.</p>
     * @param expectedResults
     * @throws JAXBException
     */
    private void checkExtensibilityConfigPage(String[] expectedResults, String paramString) throws JAXBException
    {
        WebTestArtifact wta = makeHttpRequest(EXTENSIBILITY_CONFIG_PAGE + paramString);
        Assert.assertEquals(200, wta.getResponseCode());
        List<String> results = JAXBTestReader.processResults(wta.getResponse()); 
        Assert.assertArrayEquals(expectedResults, results.toArray());
    }
    
    /**
     * <p>Deploys a single module with no evaluator or override data.</p>
     * @param moduleName The name of the module to deploy.
     * @throws IOException
     */
    private void clearDeployedModules() throws IOException
    {
        HashMap<String, String> formData = new HashMap<String, String>();
        getFilePostResponse(MODULE_DEPLOY_POST, formData, emptyMultiPartData);
    }
    
    /**
     * <p>Deploys a single module with no evaluator or override data.</p>
     * @param moduleName The name of the module to deploy.
     * @throws IOException
     */
    private void deploySingleModule(String moduleName) throws IOException
    {
        HashMap<String, String> formData = new HashMap<String, String>();
        formData.put("evaluator", "{\"id\":\"\",\"requiredProps\":[]}");
        formData.put("deployedModules", "{\"id\":\"" + moduleName + "\",\"evaluatorProperties\":{}}");
        getFilePostResponse(MODULE_DEPLOY_POST, formData, emptyMultiPartData);
    }
    
    /**
     * <p>Deploys a single module but overrides the default evaluator and evaluator parameters</p>
     * @param moduleName
     * @param evaluator
     * @param evaluatorProperties
     * @throws IOException
     */
    private void deployModuleWithOverrides(String moduleName, String evaluator, Map<String, String> evaluatorProperties) throws IOException
    {
        HashMap<String, String> formData = new HashMap<String, String>();
        formData.put("evaluator", "{\"id\":\"\",\"requiredProps\":[]}");
        StringBuilder sb = new StringBuilder("\"evaluatorPropertyOverrides\" : {");
        
        Iterator<Entry<String, String>> i = evaluatorProperties.entrySet().iterator();
        while(i.hasNext())
        {
            Entry<String, String> entry = i.next();
            sb.append("\"");
            sb.append(entry.getKey());
            sb.append("\":\"");
            sb.append(entry.getValue());
            sb.append("\"");
            if (i.hasNext())
            {
                sb.append(",");
            }
        }
        sb.append("}");
        formData.put("deployedModules", "{\"id\":\"" + moduleName + "\",\"evaluatorOverrideId\":\"" + evaluator + "\"," + sb.toString() + ",\"evaluatorProperties\":{}}");
        getFilePostResponse(MODULE_DEPLOY_POST, formData, emptyMultiPartData);
    }
    
    /**
     * <p>This test checks the basic extensibility page output before any modules have been applied.</p>
     * @throws JAXBException
     * @throws IOException 
     */
    @Test
    public void testBasicOutput() throws JAXBException, IOException
    {
        clearDeployedModules();
        String[] expectedResults = {"base-WS1-head",
                                    "base-WS2-head",
                                    "base-WS3-head",
                                    "base-WS1-i18n-1",
                                    "base-WS1-i18n-2",
                                    "base-WS1-i18n-3",
                                    "base-inner1-markup",
                                    "base-template-content",
                                    "base-WS2-i18n-1",
                                    "base-WS2-i18n-2",
                                    "base-WS2-i18n-3",
                                    "base-WS3-i18n-1",
                                    "base-WS3-i18n-2",
                                    "base-WS3-i18n-3"};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test adds new markup directive content both before and after all the base markup directives.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testTemplateMarkupAdd() throws IOException, JAXBException
    {
        deploySingleModule("Add_Markup_To_Template");
        String[] expectedResults = {"ext-before-outer",
                                    "base-WS1-head",
                                    "base-WS2-head",
                                    "base-WS3-head",
                                    "base-WS1-i18n-1",
                                    "base-WS1-i18n-2",
                                    "base-WS1-i18n-3",
                                    "ext-before-inner1",
                                    "base-inner1-markup",
                                    "ext-after-inner1",
                                    "base-template-content",
                                    "ext-before-inner2",
                                    "base-WS2-i18n-1",
                                    "base-WS2-i18n-2",
                                    "base-WS2-i18n-3",
                                    "base-WS3-i18n-1",
                                    "base-WS3-i18n-2",
                                    "base-WS3-i18n-3",
                                    "ext-after-inner2",
                                    "ext-after-outer"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test removes the outer markup directive and therefore removes all content from the page.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testTemplateRemoveOuterMarkup() throws IOException, JAXBException
    {
        deploySingleModule("Remove_Outer_Markup");
        String[] expectedResults = {};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test removes the inner markup directive</p>
     * 
     * TODO: This test did not have the expected results - the two WebScripts that were removed still have there head output included !!!
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testTemplateRemoveInnerMarkup() throws IOException, JAXBException
    {
        deploySingleModule("Remove_Inner_Markup");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-template-content"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test replaces the outer markup directive.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testTemplateReplaceOuterMarkup() throws IOException, JAXBException
    {
        deploySingleModule("Replace_Outer_Markup");
        String[] expectedResults = {
                "ext-replaced-outer"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test replaces the inner markup directive.</p>
     * 
     * TODO: This test did not have the expected results...
     *     Firstly the head element from the 2 replaced WebScripts are still present
     *     Secondly the new region did not get bound (so no head added and not main content)
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testTemplateReplaceInnerMarkup() throws IOException, JAXBException
    {
        deploySingleModule("Replace_Inner_Markup");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "base-WS4-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "ext-replaced-inner",
                "base-template-content",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test adds new SubComponents before and after a SubComponent generated from a legacy Component configuration.
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testLegacyComponentAddSubComponents() throws IOException, JAXBException
    {
        deploySingleModule("Add_SubComponents_To_Legacy_Component");
        String[] expectedResults = {
                "base-WS4-head",
                "base-WS1-head",
                "base-WS5-head",
                "base-WS2-head",
                "base-WS3-head",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-WS5-i18n-1",
                "base-WS5-i18n-2",
                "base-WS5-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test removes the content rendered from legacy Component configuration.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testLegacyComponentRemove() throws IOException, JAXBException
    {
        deploySingleModule("Remove_Legacy_Component");
        String[] expectedResults = {
                "base-WS2-head",
                "base-WS3-head",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test replaces a SubComponent generated from legacy Component configuration.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testLegacyComponentReplace() throws IOException, JAXBException
    {
        deploySingleModule("Replace_Legacy_Component");
        String[] expectedResults = {
                "base-WS4-head",
                "base-WS2-head",
                "base-WS3-head",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test adds SubComponents around an existing SubComponent</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testSubComponentAdd() throws IOException, JAXBException
    {
        deploySingleModule("Add_SubComponents");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS4-head",
                "base-WS2-head",
                "base-WS5-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS5-i18n-1",
                "base-WS5-i18n-2",
                "base-WS5-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test removes the outer markup directive and therefore removes all content from the page.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testSubComponentRemove() throws IOException, JAXBException
    {
        deploySingleModule("Hide_SubComponent");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test removes the outer markup directive and therefore removes all content from the page.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testSubComponentReplace() throws IOException, JAXBException
    {
        deploySingleModule("Replace_SubComponent");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS4-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test overrides the default i18n properties for a WebScript</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testWebScriptI18nOverride() throws IOException, JAXBException
    {
        deploySingleModule("WebScript_I18n_Override");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "ext-WS1-i18n-1",
                "ext-WS1-i18n-2",
                "ext-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test overrides the default JavaScript controller for a WebScript. The new controller only adds a single
     * message key to the model for the FreeMarker template to use.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testWebScriptControllerOverride() throws IOException, JAXBException
    {
        deploySingleModule("WebScript_Controller_Override");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test overrides the default FreeMarker template for a WebScript to add new markup directive content.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testWebScriptTemplateAdd() throws IOException, JAXBException
    {
        deploySingleModule("WebScript_Template_Add");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "Before WS1 Messages",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "After WS1 Messages",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test overrides the default FreeMarker template for a WebScript to remove the base markup directive content.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testWebScriptTemplateRemove() throws IOException, JAXBException
    {
        deploySingleModule("WebScript_Template_Remove");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test overrides the default FreeMarker template for a WebScript to replace the base markup directive content.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testWebScriptTemplateReplace() throws IOException, JAXBException
    {
        deploySingleModule("WebScript_Template_Replace");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS2-head",
                "base-WS3-head",
                "Replaced WS1 Messages",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test overrides the default FreeMarker template for a WebScript to replace the base markup directive content.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testWebScriptHeadAdd() throws IOException, JAXBException
    {
        deploySingleModule("WebScript_Head_Add");
        String[] expectedResults = {
                "base-WS1-head",
                "Additional WS1 Head Content",
                "base-WS2-head","base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS2-i18n-1",
                "base-WS2-i18n-2",
                "base-WS2-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test checks that single evaluator in an evaluation works as expected. The first evaluation (using the always fail evaluator)
     * fails and the second evaluation (using the always succeed evaluator) passes and the appropriate URL and properties are overridden.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testOutOfBoxEvaluators() throws IOException, JAXBException
    {
        deploySingleModule("OutOfBox_Evaluators");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS4-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This is similar to the "testOutOfBoxEvaluators" except that it uses multiple evaluations to check that the AND logic of the
     * evaluations works.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testCombinedEvaluators() throws IOException, JAXBException
    {
        deploySingleModule("Combined_Evaluators");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS4-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This tests that parameters can be passed into evaluators.</p>
     * 
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testEvaluatorParams() throws IOException, JAXBException
    {
        deploySingleModule("Evaluators_With_Params");
        String[] expectedResults = {
                "base-WS1-head",
                "base-WS4-head",
                "base-WS3-head",
                "base-WS1-i18n-1",
                "base-WS1-i18n-2",
                "base-WS1-i18n-3",
                "base-inner1-markup",
                "base-template-content",
                "base-WS4-i18n-1",
                "base-WS4-i18n-2",
                "base-WS4-i18n-3",
                "base-WS3-i18n-1",
                "base-WS3-i18n-2",
                "base-WS3-i18n-3"
        };
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test applies a module that is guaranteed not to evaluate successfully and checks that the module is
     * not applied.</p>
     * @throws JAXBException
     * @throws IOException 
     */
    @Test
    public void testDefaultModuleEvaluationFailure() throws JAXBException, IOException
    {
        deploySingleModule("Always_Fail_By_Default_Evaluator");
        String[] expectedResults = {"base-WS1-head",
                                    "base-WS2-head",
                                    "base-WS3-head",
                                    "base-WS1-i18n-1",
                                    "base-WS1-i18n-2",
                                    "base-WS1-i18n-3",
                                    "base-inner1-markup",
                                    "base-template-content",
                                    "base-WS2-i18n-1",
                                    "base-WS2-i18n-2",
                                    "base-WS2-i18n-3",
                                    "base-WS3-i18n-1",
                                    "base-WS3-i18n-2",
                                    "base-WS3-i18n-3"};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test applies a module that is guaranteed to evaluate and checks that the module is applied.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testDefaultModuleEvaluationSuccess() throws IOException, JAXBException
    {
        deploySingleModule("Always_Pass_By_Default_Evaluator");
        String[] expectedResults = {};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test applies a module that is guaranteed not to evaluate successfully and checks that the module is
     * not applied.</p>
     * @throws JAXBException
     * @throws IOException 
     */
    @Test
    public void testDefaultModuleEvaluationParamFailure() throws JAXBException, IOException
    {
        deploySingleModule("Always_Fail_By_Parameter_Evaluator");
        String[] expectedResults = {"base-WS1-head",
                                    "base-WS2-head",
                                    "base-WS3-head",
                                    "base-WS1-i18n-1",
                                    "base-WS1-i18n-2",
                                    "base-WS1-i18n-3",
                                    "base-inner1-markup",
                                    "base-template-content",
                                    "base-WS2-i18n-1",
                                    "base-WS2-i18n-2",
                                    "base-WS2-i18n-3",
                                    "base-WS3-i18n-1",
                                    "base-WS3-i18n-2",
                                    "base-WS3-i18n-3"};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test applies a module that is guaranteed to evaluate and checks that the module is applied.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testDefaultModuleEvaluationParamSuccess() throws IOException, JAXBException
    {
        deploySingleModule("Always_Pass_By_Parameter_Evaluator");
        String[] expectedResults = {};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test applies a module that is guaranteed not to evaluate successfully and checks that the module is
     * not applied.</p>
     * @throws JAXBException
     * @throws IOException 
     */
    @Test
    public void testModuleOverrideEvaluationFailure() throws JAXBException, IOException
    {
        deployModuleWithOverrides("Always_Pass_By_Default_Evaluator", "always.fail.module.evaluator", new HashMap<String, String>());
        String[] expectedResults = {"base-WS1-head",
                                    "base-WS2-head",
                                    "base-WS3-head",
                                    "base-WS1-i18n-1",
                                    "base-WS1-i18n-2",
                                    "base-WS1-i18n-3",
                                    "base-inner1-markup",
                                    "base-template-content",
                                    "base-WS2-i18n-1",
                                    "base-WS2-i18n-2",
                                    "base-WS2-i18n-3",
                                    "base-WS3-i18n-1",
                                    "base-WS3-i18n-2",
                                    "base-WS3-i18n-3"};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This test applies a module that is guaranteed to evaluate and checks that the module is applied.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testModuleOverrideEvaluationSuccess() throws IOException, JAXBException
    {
        deployModuleWithOverrides("Always_Fail_By_Default_Evaluator", "always.pass.module.evaluator", new HashMap<String, String>());
        String[] expectedResults = {};
        checkExtensibilityPage(expectedResults);
    }
    
    /**
     * <p>This tests the configuration page without any modules applied.</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testBasicConfigurationPage() throws IOException, JAXBException
    {
        String[] expectedResults = {
             "template-static1",
             "template-static2",
             "controller-static-1",
             "controller-static-2",
             "ws-template-static1",
             "ws-template-static2",
             "ws-controller-static-1",
             "ws-controller-static-2"
        };
        checkExtensibilityConfigPage(expectedResults, "");
    }
    
    /**
     * <p>This test applies a module replaces all the configuration</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testConfigurationReplace() throws IOException, JAXBException
    {
        deploySingleModule("Replace_configuration");
        String[] expectedResults = {
             "template-dynamic1",
             "template-dynamic2",
             "controller-dynamic-1",
             "controller-dynamic-2",
             "ws-template-dynamic1",
             "ws-template-dynamic2",
             "ws-controller-dynamic-1",
             "ws-controller-dynamic-2"
        };
        checkExtensibilityConfigPage(expectedResults, "");
    }
    
    /**
     * <p>This test applies a module adds to the default configuration</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testConfigurationAdd() throws IOException, JAXBException
    {
        deploySingleModule("Add_configuration");
        String[] expectedResults = {
             "template-static1",
             "template-static2",
             "template-dynamic1",
             "template-dynamic2",
             "controller-static-1",
             "controller-static-2",
             "controller-dynamic-1",
             "controller-dynamic-2",
             "ws-template-static1",
             "ws-template-static2",
             "ws-template-dynamic1",
             "ws-template-dynamic2",
             "ws-controller-static-1",
             "ws-controller-static-2",
             "ws-controller-dynamic-1",
             "ws-controller-dynamic-2",
        };
        checkExtensibilityConfigPage(expectedResults, "");
    }
    
    /**
     * <p>This test applies a module that only replaces configuration if the URI contains certain request parameters. This
     * is to check that configuration isn't cached between requests - particularly for WebScripts</p>
     * @throws IOException
     * @throws JAXBException
     */
    @Test
    public void testConditionalConfigurationReplace() throws IOException, JAXBException
    {
        deploySingleModule("Conditionally_Replace_configuration");
        String[] expectedResults = {
                "template-static1",
                "template-static2",
                "controller-static-1",
                "controller-static-2",
                "ws-template-static1",
                "ws-template-static2",
                "ws-controller-static-1",
                "ws-controller-static-2"
           };
        checkExtensibilityConfigPage(expectedResults, "");
        expectedResults = new String[] {
                "template-dynamic1",
                "template-dynamic2",
                "controller-dynamic-1",
                "controller-dynamic-2",
                "ws-template-dynamic1",
                "ws-template-dynamic2",
                "ws-controller-dynamic-1",
                "ws-controller-dynamic-2"
           };
        checkExtensibilityConfigPage(expectedResults, "?param1=test1&param2=test2");
        expectedResults = new String[] {
                "template-static1",
                "template-static2",
                "controller-static-1",
                "controller-static-2",
                "ws-template-static1",
                "ws-template-static2",
                "ws-controller-static-1",
                "ws-controller-static-2"
           };
        checkExtensibilityConfigPage(expectedResults, "?param1=test1");
    }
}
