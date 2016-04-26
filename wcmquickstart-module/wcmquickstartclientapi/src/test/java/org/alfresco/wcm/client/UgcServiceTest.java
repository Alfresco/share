package org.alfresco.wcm.client;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UgcServiceTest extends BaseTest 
{
	private final static Log log = LogFactory.getLog(UgcServiceTest.class);

    public void testPostFeedback()
    {
        WebSite site = getWebSite();
        
        Section root = site.getRootSection();
        String rootId = root.getId();
        
        Asset indexAsset = assetFactory.getSectionAsset(root.getId(), "index.html"); 
        assertEquals("index.html", indexAsset.getName());
        assertEquals(rootId, indexAsset.getContainingSection().getId());
        
        log.info("Article id = " + indexAsset.getId());
        
        Date beforePost = new Date();
        String feedbackId = site.getUgcService().postFeedback(indexAsset.getId(), "Brian", "brian@theworld", 
                "www.brian.com", UgcService.COMMENT_TYPE, null, "This is a fantastic article", new Random(System.currentTimeMillis()).nextInt(6));
        Date afterPost = new Date();
        
        long count = 0;
        VisitorFeedbackPage page = site.getUgcService().getFeedbackPage(indexAsset.getId(), 10, count);
        long totalSize = page.getTotalSize();
        boolean found = false;
        while (page.getSize() > 0)
        {
            List<VisitorFeedback> feedbackList = page.getFeedback();
            for (VisitorFeedback feedback : feedbackList)
            {
                ++count;
                if (!found)
                {
                    found = (feedback.getId().equals(feedbackId));
                    if (found)
                    {
                        Date feedbackTime = feedback.getPostTime();
                        assertNotNull(feedbackTime);
                        assertTrue(feedbackTime.after(beforePost) || feedbackTime.equals(beforePost));
                        assertTrue(feedbackTime.before(afterPost) || feedbackTime.equals(afterPost));
                    }
                }
            }
            page = site.getUgcService().getFeedbackPage(indexAsset.getId(), 10, count);
        }
        assertEquals(totalSize, count);
        assertTrue(found);
    }
    
    public void testFormId()
    {
        WebSite site = getWebSite();
        UgcService ugcService = site.getUgcService();
        
        assertFalse(ugcService.validateFormId("A random identifier"));
        
        String formId = ugcService.getFormId();
        assertTrue(ugcService.validateFormId(formId));
        assertFalse(ugcService.validateFormId(formId));
        
    }
    
	
}
