package org.alfresco.po.share.enums;

/**
 * @author Marina.Nenadovets
 */
public enum CloudSyncStatus
{
        SYNCED("Synced"),
        ATTEMPTED("Sync attempted"),
        PENDING("Pending");

        private String value;

        CloudSyncStatus(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
}
