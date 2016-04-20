package org.alfresco.po.share.user;

/**
 * All Languages displayed in Language Settings drop down
 * 
 * @author Ranjith Manyam
 * @since v1.9.0
 */
public enum Language
{
    ENGLISH_US("en_US", "english.properties"),
    FRENCH("fr_FR", "french.properties"),
    DEUTSCHE("de_DE", "deutsche.properties"),
    SPANISH("es_ES", "spanish.properties"),
    ITALIAN("it_IT", "italian.properties"),
    JAPANESE("ja_JA", "japanese.properties");

    private String value;
    private String propertyFileName;

    private Language(String value, String propertyFileName)
    {
        this.value = value;
        this.propertyFileName = propertyFileName;
    }

    public String getLanguageValue()
    {
        return value;
    }

    public String getLanguagePropertyFileName()
    {
        return propertyFileName;
    }

    /**
     * Returns the Language from string value.
     * 
     * @param value - string value of enum eg - "en_US"
     * @return Language
     */
    public static Language getLanguageFromValue(String value)
    {
        for (Language status : Language.values())
        {
            if (value.equalsIgnoreCase(status.value))
            {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Language : " + value);
    }
}
