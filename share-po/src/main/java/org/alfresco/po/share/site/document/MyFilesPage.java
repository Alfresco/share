/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author nshah
 * Dated: 27/03/2014
 * Represent Myfiles option from header bar of share. 
 */
public class MyFilesPage extends RepositoryPage {
	
	private static Log logger = LogFactory.getLog(DocumentLibraryPage.class);
	
	public MyFilesPage(WebDrone drone) {
		super(drone);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public MyFilesPage render(RenderTime timer) {
		
		logger.info("Logged in to :"+this);
		super.render(timer);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MyFilesPage render(long time) {
		
		return render(new RenderTime(time));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MyFilesPage render() {
		
		return render(new RenderTime(maxPageLoadingTime));
	}

}
