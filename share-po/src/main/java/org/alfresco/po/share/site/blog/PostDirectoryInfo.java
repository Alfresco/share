package org.alfresco.po.share.site.blog;


/**
 * @author Sergey Kardash
 */
public interface PostDirectoryInfo
{
    /**
     * Click on Edit Post
     * 
     * @return EditPostForm
     */
    EditPostForm editPost();

    /**
     * Click on Delete Post
     * 
     * @return BlogPage
     */
    BlogPage deletePost();

    /**
     * Verify whether edit Post is displayed
     * 
     * @return boolean
     */
    boolean isEditPostDisplayed();

    /**
     * Verify whether delete Post is displayed
     * 
     * @return boolean
     */
    boolean isDeletePostDisplayed();
}
