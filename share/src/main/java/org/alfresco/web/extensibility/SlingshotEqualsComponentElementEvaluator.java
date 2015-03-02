package org.alfresco.web.extensibility;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 * <p>
 * Evaluator used to decide if a <{@code}sub-component> shall be bound in to a {@code<component>} and {@code<@region>}.
 * Returns true if all parameter values matches each other AND there are at least 2 parameters.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="equals.component.evaluator">
 *    <params>
 *       <referrer>{referrer}</referrer>
 *       <workflows>workflows</workflows>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if the resolved value of {referrer} equals the value of the {@code<workflows>} parameter ("workflows").
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotEqualsComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    /**
     * Returns true if all parameter values equal each other.
     *
     * @param context
     * @param params
     * @return true if 2 or more values are equal to each other.
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        if (params.isEmpty())
        {
            return false;
        }
        else if (params.size() < 2)
        {
            return false;
        }
        else
        {
            String firstValue = params.values().iterator().next();
            if (firstValue == null)
            {
                firstValue = "";
            }
            for (String value : params.values())
            {
                if (!firstValue.equals(value))
                {
                    return false;
                }
            }
            return true;
        }
    }
}
