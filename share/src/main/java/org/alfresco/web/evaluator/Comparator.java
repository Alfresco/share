
package org.alfresco.web.evaluator;

/**
 * Contract supported by all classes that provide a comparison service for the ValueEvaluator class.
 * <p>
 * The comparator is free to inject (via Spring config) whatever criteria are needed to decide on the outcome.
 *
 * @author mikeh
 */
public interface Comparator
{
    /**
     * Run the compare logic and return the result.
     *
     * @param nodeValue Object the node's value to compare
     * @return true for a successful result, false otherwise
     */
    public boolean compare(Object nodeValue);
}
