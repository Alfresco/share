package org.alfresco.wcm.client.impl;

import java.util.Collections;
import java.util.List;

import org.alfresco.wcm.client.VisitorFeedback;
import org.alfresco.wcm.client.VisitorFeedbackPage;

public class VisitorFeedbackPageImpl implements VisitorFeedbackPage
{
    private List<VisitorFeedback> feedback;
    private long startingIndex;
    private long totalSize;

    public VisitorFeedbackPageImpl(List<VisitorFeedback> feedback, long startingIndex, long totalSize)
    {
        this.feedback = Collections.unmodifiableList(feedback);
        this.startingIndex = startingIndex;
        this.totalSize = totalSize;
    }

    @Override
    public List<VisitorFeedback> getFeedback()
    {
        return feedback;
    }

    @Override
    public int getSize()
    {
        return feedback.size();
    }

    @Override
    public long getStartingIndex()
    {
        return startingIndex;
    }

    @Override
    public long getTotalSize()
    {
        return totalSize;
    }

}
