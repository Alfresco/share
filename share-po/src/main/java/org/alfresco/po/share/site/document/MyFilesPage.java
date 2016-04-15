package org.alfresco.po.share.site.document;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.RepositoryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author nshah
 * Dated: 27/03/2014
 * Represent Myfiles option from header bar of share. 
 */
public class MyFilesPage extends RepositoryPage {
    
    private static Log logger = LogFactory.getLog(DocumentLibraryPage.class);

    @SuppressWarnings("unchecked")
    @Override
    public MyFilesPage render(RenderTime timer) {
        logger.info("Logged in to :"+this);
        super.render(timer);
        return this;
}   
    @SuppressWarnings("unchecked")
    @Override
    public MyFilesPage render() {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
