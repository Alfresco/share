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
package org.alfresco.web.config.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.alfresco.encryptor.PublicPrivateKeyShareStringEncryptor;
import org.alfresco.web.site.servlet.config.AlfrescoEncryptionElementConvert;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.XMLConfigService;

public class EncryptionElementConverterTest {

	protected XMLConfigService configService;
	protected Config globalConfig;
	protected ConfigElement globalConstraintHandlers;
	private static PublicPrivateKeyShareStringEncryptor encryptor = new PublicPrivateKeyShareStringEncryptor();
	private static String path = null;

	@BeforeClass
	public static void beforeClass() throws Exception {
		setUpEncryptedKeys();
	}

	private static void setUpEncryptedKeys() {

		ClassLoader cl = EncryptionElementConverterTest.class.getClassLoader();
		URL uri = cl.getResource("alfresco/module/encryption");
		path = uri.getPath();
		
		encryptor.createKeyFiles(uri.getPath());
		encryptor.initPublic(uri.getPath());
		encryptor.initPrivate(uri.getPath());
	}

	@Test
	public void testBaseOperations() {
		String encryptedValue = encryptor.encrypt("123");
		assertEquals("123", encryptor.decrypt(encryptedValue));
	}

	@Test
	public void testEncryption() throws IOException {
		String encryptedSecret = encryptor.encrypt("secret");
		assertEquals("secret", encryptor.decrypt(encryptedSecret));
		PublicPrivateKeyShareStringEncryptor encryptorPrivateKey = new PublicPrivateKeyShareStringEncryptor();
		File file = new File(path);
		encryptorPrivateKey.initConfig(file.getAbsolutePath());
		assertEquals("secret", encryptorPrivateKey.decrypt(encryptedSecret));
	}

	@Test
	public void testEncryptionConverter() throws IOException {
		String encryptedValue = "ENC(" + encryptor.encrypt("secret") + ")";
		PublicPrivateKeyShareStringEncryptor encryptorPrivateKey = new PublicPrivateKeyShareStringEncryptor();
		File file = new File(path);
		encryptorPrivateKey.initConfig(file.getAbsolutePath());
		AlfrescoEncryptionElementConvert elementConveter = new AlfrescoEncryptionElementConvert(encryptorPrivateKey);
		Element elementEncrypted = DocumentHelper.createElement("password");
		elementEncrypted.addText(encryptedValue);
		try {
			elementConveter.parse(elementEncrypted);
		} catch (Exception e) {
			fail();
		}
	}

}
