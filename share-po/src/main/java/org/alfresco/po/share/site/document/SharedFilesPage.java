package org.alfresco.po.share.site.document;

import org.alfresco.po.RenderTime;

/**
 * @author nshah
 * Dated: 27/03/2014
 * This page represents Shared Files from HeaderBar. 
 *
 */
public class SharedFilesPage extends DocumentLibraryPage {



	/* (non-Javadoc)
	 * @see org.alfresco.po.Render#render(org.alfresco.po.RenderTime)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SharedFilesPage render(RenderTime timer)
	{
		
		super.render(timer);
		return this;
	}


	/* (non-Javadoc)
	 * @see org.alfresco.po.Render#render()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SharedFilesPage render() {
		
		return render(new RenderTime(maxPageLoadingTime));
	}

}
