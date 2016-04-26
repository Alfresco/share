package org.alfresco.wcm.client.util;

public class QuerySanitizer
{
    private static QuerySanitizer instance = new QuerySanitizer();
    
    protected QuerySanitizer()
    {
    }
    
    public static String sanitize(String text)
    {
        return instance.sanitizeImpl(text);
    }
    
    /**
     * Overridable sanitization method
     * @param text String
     * @return String
     */
    protected String sanitizeImpl(String text)
    {
        return text == null ? null : text.replaceAll("[\"'%?*()$^<>/{}\\[\\]#~@.,|\\\\+!:;&`¬=]", " ");
    }
    
    /**
     * Inject a new implementation if desired. Create a subclass of this class, override the sanitizeImpl operation,
     * and inject an instance of it using this operation. QuerySanitizer.sanitize will then be routed to your object.
     * @param sanitizer QuerySanitizer
     */
    public static void setSanitizer(QuerySanitizer sanitizer)
    {
        instance = sanitizer;
    }
}
