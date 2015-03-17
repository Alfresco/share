package org.alfresco.po.share.site.blog;


/**
 * @author Sergey Kardash
 */
public interface PostDirectoryInfo
{
    /**
     * Click on Edit Post
     * 
     * @return
     */
    EditPostForm editPost();

    /**
     * Click on Delete Post
     * 
     * @return
     */
    BlogPage deletePost();

    /**
     * Verify whether edit Post is displayed
     * 
     * @return
     */
    boolean isEditPostDisplayed();

    /**
     * Verify whether delete Post is displayed
     * 
     * @return
     */
    boolean isDeletePostDisplayed();
}
