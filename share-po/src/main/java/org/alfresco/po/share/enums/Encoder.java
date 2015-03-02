package org.alfresco.po.share.enums;

import org.alfresco.webdrone.WebDroneUtil;

/**
 * Enum to contain all the possible Encoders
 * 
 * @author Meenal Bhave
 */
public enum Encoder
{
    ENCODER_HTML("Html"),
    ENCODER_JAVASCRIPT("JavaScript"),
    ENCODER_NOENCODER("No");

    private String name;

    private Encoder(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Gets appropriate Encoder type.
     * 
     * @param name
     * @return {@link Encoder}
     */
    public static Encoder getEncoder(String name)
    {
        WebDroneUtil.checkMandotaryParam("name", name);

        if (name.equalsIgnoreCase((ENCODER_HTML.getName())))
        {
            return ENCODER_HTML;
        }
        else if (name.equalsIgnoreCase((ENCODER_JAVASCRIPT.getName())))
        {
            return ENCODER_JAVASCRIPT;
        }
        else if (name.equalsIgnoreCase((ENCODER_NOENCODER.getName())))
        {
            return ENCODER_NOENCODER;
        }

        throw new IllegalArgumentException("Not able to find the Encoder for given type: " + name);
    }

}