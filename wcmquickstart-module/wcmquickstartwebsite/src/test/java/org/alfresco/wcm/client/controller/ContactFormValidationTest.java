package org.alfresco.wcm.client.controller;

import java.util.regex.Pattern;

import junit.framework.TestCase;


public class ContactFormValidationTest extends TestCase
{
    public void testEmailRegex()
    {
        Pattern regex = Pattern.compile
            ("^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.([a-z][a-z]+)|([0-9]{1,3}\\." +
            "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$");
        
        assertFalse(regex.matcher("!@#$%^&*()_+{}:\"|<>?name@mail.com").matches());
    }
}
