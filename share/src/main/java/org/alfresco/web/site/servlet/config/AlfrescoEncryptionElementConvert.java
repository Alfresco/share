/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
 */
package org.alfresco.web.site.servlet.config;

import java.util.Iterator;

import org.alfresco.encryptor.PublicPrivateKeyShareStringEncryptor;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jasypt.properties.PropertyValueEncryptionUtils;

public class AlfrescoEncryptionElementConvert {

	private final PublicPrivateKeyShareStringEncryptor stringEncryptor;

	public AlfrescoEncryptionElementConvert() {
		stringEncryptor = new PublicPrivateKeyShareStringEncryptor();
		stringEncryptor.init();
	}

	public AlfrescoEncryptionElementConvert(PublicPrivateKeyShareStringEncryptor stringEncryptor) {
		this.stringEncryptor = stringEncryptor;
	}

	public void parse(Element element) {
		if (element != null) {
			convertElement(element);
			// process any children there may be
			processChildren(element);
		}
	}

	protected void processChildren(Element element) {
		// get the list of children for the given element
		Iterator<Element> children = element.elementIterator();
		while (children.hasNext()) {
			Element child = children.next();
			convertElement(child);
			// recurse down the children
			processChildren(child);
		}
	}

	protected void convertElement(Element element) {
		if ((element.hasContent()) && (element.hasMixedContent() == false)) {
			String value = element.getTextTrim();
			if (value != null && value.length() > 0) {
				value = convertElementValue(value);
				element.setText(value);
			}
		}

		Iterator<Attribute> attrs = element.attributeIterator();
		while (attrs.hasNext()) {
			Attribute attr = attrs.next();
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			attrValue = convertElementValue(attrValue);

			element.addAttribute(attrName, attrValue);
		}

	}

	protected String convertElementValue(final String originalValue) {
		if (!PropertyValueEncryptionUtils.isEncryptedValue(originalValue)) {
			return originalValue;
		}
		if (this.stringEncryptor != null) {
			return PropertyValueEncryptionUtils.decrypt(originalValue, this.stringEncryptor);

		}
		return null;
	}

}
