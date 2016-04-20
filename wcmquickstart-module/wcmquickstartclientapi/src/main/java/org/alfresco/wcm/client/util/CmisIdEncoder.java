package org.alfresco.wcm.client.util;

/**
 * CMIS Url encoder. This interface is implemented by classes 
 * which translate between a CMIS object id and a valid url string
 * @author Chris Lack
 */
public interface CmisIdEncoder  
{
	/**
	 * Convert a CMIS object Id into a string which will be valid within a url.
	 * @param objectId CMIS object id
	 * @return String url portion
	 */
	String getUrlSafeString(String objectId);	
	
	/**
	 * Convert url valid encoding of a CMIS object Id back into the id.
	 * @param urlPortion String url portion
	 * @return objectId CMIS object id
	 */
	String getObjectId(String urlPortion);	
	
}
