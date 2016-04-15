package org.alfresco.po;
/**
 * WebDriver properties interface, every page object library
 * that uses {@link WebDriver} get element by id will require to
 * load a properties file with all the ids from the page object.
 * 
 * 
 * @author Michael Suzuki
 * @since 1.9
 *
 */
public interface POProperties
{
    /**
     * Gets the HTML element id value for the given key
     * from the loaded properties file.
     * 
     * @param key String HTML element id
     * @return String value of key
     */
    String getElement(final String key);
    /**
     * Get the product version.
     * Alfresco Share 4.2 will return Enterprise4.2 enum
     * @param <T> version type
     * @return {@link Version} version 
     */
    <T extends Version> T getVersion();
}
