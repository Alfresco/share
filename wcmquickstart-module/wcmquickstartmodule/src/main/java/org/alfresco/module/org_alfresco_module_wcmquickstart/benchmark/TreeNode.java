
package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

/**
 * @author  Nick Smith
 * @since  4.0
 */
public class TreeNode<T>
{
    private final T parent;
    private final T child;
    private final boolean isLeaf;

    /**
     * @param parent T
     * @param child T
     * @param isLeaf boolean
     */
    public TreeNode(T parent, T child, boolean isLeaf)
    {
        this.parent = parent;
        this.child = child;
        this.isLeaf = isLeaf;
    }
    
    /**
     * @return the value
     */
    public T getChild()
    {
        return child;
    }
    
    /**
     * @return the parent
     */
    public T getParent()
    {
        return parent;
    }
    
    /**
     * @return the isLeaf
     */
    public boolean isLeaf()
    {
        return isLeaf;
    }
    
    public boolean isRoot()
    {
        return parent == null;
    }
}
