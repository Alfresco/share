package org.alfresco.po.share.systemsummary.directorymanagement;

/**
 * Object contains information about possible Auth Chains in alfresco.
 *
 * @author Aliaksei Boole
 */
public enum AuthType
{
    OPEN_LDAP("ldap", "OpenLDAP"),
    AD_LDAP("ldap-ad", "LDAP (Active Directory)"),
    PASSTHRU("passthru", "Passthru"),
    KERBEROS("kerberos", "Kerberos"),
    EXTERNAL("external", "External");

    AuthType(String value, String text)
    {
        this.value = value;
        this.text = text;
    }

    public final String value;
    public final String text;
}
