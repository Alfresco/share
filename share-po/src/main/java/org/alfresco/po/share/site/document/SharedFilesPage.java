/**
 * 
 */
package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author nshah
 * Dated: 27/03/2014
 * This page represents Shared Files from HeaderBar. 
 *
 */
public class SharedFilesPage extends DocumentLibraryPage {

	private static Log logger = LogFactory.getLog(DocumentLibraryPage.class);
	/**
	 * @param drone
	 * @param subfolderName
	 */
	public SharedFilesPage(WebDrone drone, String subfolderName) {	   
		super(drone, subfolderName);
		logger.info("enter into class : "+ this);
	}

	/**
	 * @param drone
	 */
	public SharedFilesPage(WebDrone drone) {
		super(drone);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param drone
	 * @param contentName
	 * @param hasTag
	 */
	public SharedFilesPage(WebDrone drone, String contentName, String hasTag) {
		super(drone, contentName, hasTag);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SharedFilesPage render(RenderTime timer) {
		
		super.render(timer);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.webdrone.Render#render(long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SharedFilesPage render(long time) {
		
		return render(new RenderTime(time));
	}

	/* (non-Javadoc)
	 * @see org.alfresco.webdrone.Render#render()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SharedFilesPage render() {
		
		return render(new RenderTime(maxPageLoadingTime));
	}

}
