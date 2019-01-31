package org.apache.log4j;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.owasp.esapi.ESAPI;
/**
 * Custom Log Pattern Layout to neutralize logs
 * 
 * MNT-20199 Improper Output Neutralization for Logs CWE ID 117
 * Creator: aioobe (https://stackoverflow.com/questions/30912182/how-to-resolve-cwe-117-issue )
 * LM_2019-01-30
 * */

public class NewLinePatternLayout extends PatternLayout {

	public NewLinePatternLayout() { }

    public NewLinePatternLayout(String pattern) {
        super(pattern);
    }
    
    public String format(LoggingEvent event) {
        String original = super.format(event);

        // ensure no CRLF injection into logs for forging records
        String clean = original.replace('\n', '_').replace('\r', '_');
        if (ESAPI.securityConfiguration().getLogEncodingRequired()) {
        	//Encode data for use in HTML using HTML entity encoding
            clean = ESAPI.encoder().encodeForHTML(clean);
        }
        //insert new line for better readability of the logs
        StringBuilder sb = new StringBuilder(clean + "\n");

        return sb.toString();
    }
}