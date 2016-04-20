package org.alfresco.wcm.client;

import java.util.List;

public interface VisitorFeedbackPage
{
    long getStartingIndex();
    int getSize();
    long getTotalSize();
    List<VisitorFeedback> getFeedback();
}
