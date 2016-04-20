package org.alfresco.wcm.client;

/** 
 * Path represents a uri as a resource name and array of path segments.
 * @author Chris Lack
 */
public interface Path
{
	/**
	 * Get the path without the filename, split into individual segments.
	 * eg from /news/world/index.html this will return {"news","world"}.
	 * @return String[] array of path segments.
	 */
	String[] getPathSegments();

	/**
	 * Get the filename from a path.
	 * eg from /news/world/index.html this will return "index.html".
	 * @return String resource name
	 */
	String getResourceName();
}
