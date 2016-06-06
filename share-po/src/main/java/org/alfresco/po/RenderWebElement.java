package org.alfresco.po;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.alfresco.po.ElementState.VISIBLE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Locator By annotated with RenderWebElement can be rendered while calling the render on Page.
 *
 * @author Shan Nagarajan
 * @since  2.2
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface RenderWebElement 
{
    /**
     * Render the element based on the {@link ElementState}
     * The default value of {@link ElementState} is Visible, 
     * if  it have to render for other other {@link ElementState} user have to set attribute.
     * 
     * @return {@link ElementState} element state
     */
    ElementState state() default VISIBLE;
}