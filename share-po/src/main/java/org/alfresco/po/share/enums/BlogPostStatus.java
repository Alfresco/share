package org.alfresco.po.share.enums;

/**
 * Enum to include all blog posts' statuses
 *
 * @author Marina.Nenadovets
 */
public enum BlogPostStatus
{
        DRAFT("Draft"),
        UPDATED("Updated"),
        PUBLISHED ("Published"),
        OUT_OF_SYNC("Out of sync");

        public final String status;

        BlogPostStatus(String status)
        {
            this.status = status;
        }

        public String getPostStatus()
        {
            return status;
        }
 }
