/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po;

import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.getElementName;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.getGenericParameterClass;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.isHtmlElement;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.isHtmlElementList;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.isTypifiedElement;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.isTypifiedElementList;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.isWebElement;
import static ru.yandex.qatools.htmlelements.utils.HtmlElementUtils.isWebElementList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementFactory;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * Decorator that is a copy of Yandex's HtmlElementDecorator with some minor changes,
 * this is to allow access to WebDriver from {@link PageElement}.
 * @author Michael Suzuki
 *
 */
public class WebDriverAwareDecorator extends DefaultFieldDecorator
{
    protected WebDriver webDriver;
    public WebDriverAwareDecorator(ElementLocatorFactory locatorFactory) 
    {
        super(locatorFactory);
    }

    public WebDriverAwareDecorator(SearchContext searchContext)
    {
        this(new HtmlElementLocatorFactory(searchContext));
        if(searchContext instanceof WebDriver)
        {
            this.webDriver = (WebDriver)searchContext;
        }
    }

    public void setWebDriver(WebDriver webDriver) 
    {
        this.webDriver = webDriver;
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) 
    {
        Object result = null;

        if (!isDecoratableField(field)) 
        {
            return null;
        }

        ElementLocator locator = factory.createLocator(field);
        if (locator == null) 
        {
            return null;
        }
        String elementName = getElementName(field);
        if (isTypifiedElement(field)) 
        {
            @SuppressWarnings("unchecked")
            Class<TypifiedElement> typifiedElementClass = (Class<TypifiedElement>) field.getType();
            result = decorateTypifiedElement(typifiedElementClass, loader, locator, elementName);
        }
        else if (isHtmlElement(field)) 
        {
            @SuppressWarnings("unchecked")
            Class<HtmlElement> htmlElementClass = (Class<HtmlElement>) field.getType();
            result = decorateHtmlElement(htmlElementClass, loader, locator, elementName);
        }
        else if (isWebElement(field))
        {
            result = decorateWebElement(loader, locator, elementName);
        }
        else if (isTypifiedElementList(field)) 
        {
            @SuppressWarnings("unchecked")
            Class<TypifiedElement> typifiedElementClass = (Class<TypifiedElement>) getGenericParameterClass(field);
            result = decorateTypifiedElementList(typifiedElementClass, loader, locator, elementName);
        }
        else if (isHtmlElementList(field)) 
        {
            @SuppressWarnings("unchecked")
            Class<HtmlElement> htmlElementClass = (Class<HtmlElement>) getGenericParameterClass(field);
            result = decorateHtmlElementList(htmlElementClass, loader, locator, elementName);
        }
        else if (isWebElementList(field)) 
        {
            result = decorateWebElementList(loader, locator, elementName);
        }

        if (result instanceof WebDriverAware) 
        {
            ((WebDriverAware) result).setWebDriver(webDriver);
        }
        

        return result;
    }

    private boolean isDecoratableField(Field field) 
    {
        return isWebElement(field) && !field.getName().equals("wrappedElement")
                || isWebElementList(field)
                || isHtmlElement(field)
                || isHtmlElementList(field)
                || isTypifiedElement(field)
                || isTypifiedElementList(field);
    }

    private <T extends TypifiedElement> T decorateTypifiedElement(Class<T> elementClass, ClassLoader loader,
                                                                  ElementLocator locator, String elementName) 
    {
        // Create typified element and initialize it with WebElement proxy
        WebElement elementToWrap = HtmlElementFactory.createNamedProxyForWebElement(loader, locator, elementName);
        T typifiedElementInstance = HtmlElementFactory.createTypifiedElementInstance(elementClass, elementToWrap);
        typifiedElementInstance.setName(elementName);
        return typifiedElementInstance;
    }

    private <T extends HtmlElement> T decorateHtmlElement(Class<T> elementClass, ClassLoader loader,
                                                          ElementLocator locator, String elementName) 
    {
        // Create block and initialize it with WebElement proxy
        WebElement elementToWrap = HtmlElementFactory.createNamedProxyForWebElement(loader, locator, elementName);
        T htmlElementInstance = HtmlElementFactory.createHtmlElementInstance(elementClass);
        htmlElementInstance.setWrappedElement(elementToWrap);
        htmlElementInstance.setName(elementName);
        
        // ADD THIS: Recursively initialize elements of the block
        WebDriverAwareDecorator decoratorForSubFields = new WebDriverAwareDecorator(elementToWrap);
//        decoratorForSubFields.setObjectInitializer(this.objectInitializer);
        decoratorForSubFields.setWebDriver(this.webDriver);
        
        PageFactory.initElements(decoratorForSubFields, htmlElementInstance);
        return htmlElementInstance;
    }

    private WebElement decorateWebElement(ClassLoader loader, ElementLocator locator, String elementName) 
    {
        return HtmlElementFactory.createNamedProxyForWebElement(loader, locator, elementName);
    }

    private <T extends TypifiedElement> List<T> decorateTypifiedElementList(Class<T> elementClass, ClassLoader loader,
                                                                            ElementLocator locator, String listName) 
    {
        return HtmlElementFactory.createNamedProxyForTypifiedElementList(elementClass, loader, locator, listName);
    }

    @SuppressWarnings("unchecked")
    private <T extends HtmlElement> List<T> decorateHtmlElementList(Class<T> elementClass, ClassLoader loader,
                                                                    ElementLocator locator, String listName)
    {
        InvocationHandler handler = new WebDriverAwareListProxyHandler<T>(elementClass, locator, listName);
        return (List<T>) Proxy.newProxyInstance(loader, new Class[]{List.class}, handler);
    }

    private List<WebElement> decorateWebElementList(ClassLoader loader, ElementLocator locator, String listName) 
    {
        return HtmlElementFactory.createNamedProxyForWebElementList(loader, locator, listName);
    }

    private class WebDriverAwareListProxyHandler<T extends HtmlElement> implements InvocationHandler 
    {
        private final Class<T> htmlElementClass;
        private final ElementLocator locator;
        private final String name;

        public WebDriverAwareListProxyHandler(Class<T> htmlElementClass, ElementLocator locator, String name) 
        {
            this.htmlElementClass = htmlElementClass;
            this.locator = locator;
            this.name = name;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable 
        {
            if ("toString".equals(method.getName())) 
            {
                return name;
            }

            List<T> htmlElements = new LinkedList<T>();
            List<WebElement> elements = locator.findElements();
            int elementNumber = 0;
            for (WebElement element : elements)
            {
                T htmlElement = HtmlElementFactory.createHtmlElementInstance(htmlElementClass);
                htmlElement.setWrappedElement(element);
                String htmlElementName = String.format("%s [%d]", name, elementNumber);
                htmlElement.setName(htmlElementName);
                
                // HERE
                WebDriverAwareDecorator decorator = new WebDriverAwareDecorator(element);
                decorator.setWebDriver(webDriver);
                PageFactory.initElements(decorator, htmlElement);
                if (htmlElement instanceof WebDriverAware) 
                {
                    ((WebDriverAware) htmlElement).setWebDriver(webDriver);
                }
                
                htmlElements.add(htmlElement);
                elementNumber++;
            }

            try 
            {
                return method.invoke(htmlElements, objects);
            }
            catch (InvocationTargetException e) 
            {
                // Unwrap the underlying exception
                throw e.getCause();
            }
        }
    }
}