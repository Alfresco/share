package org.alfresco.po;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.exception.PageRenderTimeException;


/**
 * An Object to measure time left for the operation to complete.
 * Every operation takes the time left to complete and deducts
 * the time spent from the total time.
 * 
 * The RenderTime is used to determine if different elements 
 * on the HTML page have completed in a timely manner. 
 * Each page object has a render method that check if the page has rendered.
 * 
 * The check takes the total time limit and for every element it check it passes
 * the time left for it before it runs out of time.
 * 
 * 
 * @author Michael Suzuki
 * @since 1.0
 *
 */
public class RenderTime
{
    private long maxWait;
    private long start;
    private long passed;
    private TimeUnit unit;

    public RenderTime(final long maxWait, TimeUnit unit)
    {
        this.maxWait = TimeUnit.NANOSECONDS.convert(maxWait, unit);
        this.unit = unit;
    }
    
    /**
     * RenderTime constructor that take maxWait time in milliseconds.
     * @param maxWait long wait in millisecond
     */
    public RenderTime(final long maxWait)
    {
        unit = TimeUnit.MILLISECONDS;
        this.maxWait = TimeUnit.NANOSECONDS.convert(maxWait, TimeUnit.MILLISECONDS);
    }
    /**
     *  The time left in nanoseconds.
     *  @return long time left
     */
    public long timeLeft()
    {
        long time = maxWait - passed;
        return unit.convert(time, TimeUnit.NANOSECONDS);
    }

    public void start()
    {
        this.start = System.nanoTime();
    }

    public void end()
    {
        end("");
    }
    
    public void end(String message)
    {
        long duration = System.nanoTime() - start;
        passed += duration;
        if(passed > maxWait)
        {
            throw new PageRenderTimeException("Exceeded the max wait time" + maxWait + " " + message);
        }
    }
    
    public String toString()
    {
        return String.format("The max time %d in nanon and time left in %s: %d", 
                maxWait, unit.name(),
                timeLeft());
    }
}
