/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.surf.util;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * Implementation of the JSP BodyContent class. BodyContent extends
 * JspWriter to allow access to the underlying buffer.
 * 
 * The buffer can be cleared, converted to a string, or read through a Reader.
 * It also has the notion of an enclosed writer, which is in essence a parent
 * BodyContent.
 * 
 * Finally, it has a writeOut method which allows for efficiently writing its
 * contents to its parent (or another writer).
 * 
 * @author muzquiano
 */
public class FakeBodyContent extends BodyContent
{
    
    /** The DEFAUL t_ buffe r_ size. */
    static int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Instantiates a new fake body content.
     * 
     * @param encl
     *            the encl
     */
    public FakeBodyContent(JspWriter encl)
    {
        super(encl);
        this.enclosingWriter = encl;
        if (bufferSize == JspWriter.DEFAULT_BUFFER)
            bufferSize = DEFAULT_BUFFER_SIZE;
        else if (bufferSize == JspWriter.UNBOUNDED_BUFFER)
        {
            bufferSize = DEFAULT_BUFFER_SIZE;
            unbounded = true;
        }
        buffer = new char[bufferSize];
        index = 0;
    }

    /**
     * Write the String s to the buffer.
     * 
     * @param s
     *            the s
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void write(String s) throws IOException
    {
        // prevent NPEs
        if (s == null)
            s = "null";

        if (bufferSize == 0)
        {
            // write straight to the Writer contained in the parent JspWriter
            // not actually sure what good this does, but this case never happens anyways
            out.write(s);
        }
        else
        {
            if (s != null && s.length() == 0)
            {
                return;
            }

            // make sure there is enough room
            if (index + s.length() >= bufferSize)
            {
                growBuffer(index + s.length());
            }

            // copy the characters from s into the buffer
            s.getChars(0, s.length(), buffer, index);
            index += s.length();
        }
    }

    /**
     * Write the character represented by the integer i to the buffer.
     * 
     * @param i
     *            the i
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void write(int i) throws IOException
    {
        if (bufferSize == 0)
        {
            // write straight to the Writer contained in the parent JspWriter
            // not actually sure what good this does, but this case never happens anyways
            out.write(i);
        }
        else
        {
            // make sure there is enough room
            if (index >= bufferSize)
            {
                growBuffer(bufferSize + 1);
            }

            buffer[index++] = (char) i;
        }
    }

    /**
     * Write an array of characters to the buffer.
     * 
     * @param c
     *            the c
     * @param off
     *            the off
     * @param len
     *            the len
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void write(char c[], int off, int len) throws IOException
    {
        if (bufferSize == 0)
        {
            // write straight to the Writer contained in the parent JspWriter
            // not actually sure what good this does, but this case never happens anyways
            out.write(c, off, len);
        }
        else
        {
            int end = off + len;
            // make sure the offset and length parameters are valid
            if ((off < 0) || (off > c.length) || (len < 0) || (end > c.length) || ((end) < 0))
            {
                throw new IndexOutOfBoundsException();
            }
            else if (len == 0)
            {
                return;
            }

            // make sure there is enough space
            if (index + len >= bufferSize)
            {
                growBuffer(index + len);
            }
            System.arraycopy(c, off, buffer, index, len);
            index += len;
        }
    }

    /**
     * Ensure that at least minLength bytes are available in the buffer total.
     * 
     * @param minLength
     *            the min length
     */
    private void growBuffer(int minLength)
    {
        // grow by a factor of two, or minLength, whichever is greater
        int newLength = Math.max(minLength, bufferSize * 2);
        char newBuf[] = new char[newLength];
        System.arraycopy(buffer, 0, newBuf, 0, index);
        buffer = newBuf;
        bufferSize = newBuf.length;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(char)
     */
    public void print(char c) throws IOException
    {
        write((int) c);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(double)
     */
    public void print(double d) throws IOException
    {
        write(Double.toString(d));
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(boolean)
     */
    public void print(boolean b) throws IOException
    {
        write(new Boolean(b).toString());
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(long)
     */
    public void print(long l) throws IOException
    {
        write(Long.toString(l));
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(float)
     */
    public void print(float f) throws IOException
    {
        write(Float.toString(f));
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(int)
     */
    public void print(int i) throws IOException
    {
        write(Integer.toString(i));
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(java.lang.Object)
     */
    public void print(Object o) throws IOException
    {
        write(o.toString());
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(char[])
     */
    public void print(char c[]) throws IOException
    {
        write(c, 0, c.length);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#print(java.lang.String)
     */
    public void print(String s) throws IOException
    {
        write(s);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println()
     */
    public void println() throws IOException
    {
        newLine();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(java.lang.String)
     */
    public void println(String s) throws IOException
    {
        print(s);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(char)
     */
    public void println(char c) throws IOException
    {
        print(c);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(char[])
     */
    public void println(char c[]) throws IOException
    {
        print(c);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(long)
     */
    public void println(long l) throws IOException
    {
        print(l);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(int)
     */
    public void println(int i) throws IOException
    {
        print(i);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(double)
     */
    public void println(double d) throws IOException
    {
        print(d);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(float)
     */
    public void println(float f) throws IOException
    {
        print(f);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(boolean)
     */
    public void println(boolean b) throws IOException
    {
        print(b);
        println();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspWriter#println(java.lang.Object)
     */
    public void println(Object o) throws IOException
    {
        print(o);
        println();
    }

    /**
     * Close is a no-op in BodyContent.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void close() throws IOException
    {
    }

    /**
     * Flush is a no-op in BodyContent, since you have to explicitly write its
     * contents to the enclosing writer.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void flush() throws IOException
    {
    }

    /**
     * Return remaining size in the buffer. This shouldn't be a concern, since
     * this implementation always grows the buffer.
     * 
     * @return the remaining
     */
    public int getRemaining()
    {
        return bufferSize - index;
    }

    /**
     * Clear the contents of the buffer, unless it was flushed.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void clear() throws IOException
    {
        if (flushed)
            throw new IOException("Can't clear flushed buffer");
        clearBuffer();
    }

    /**
     * Clear the contents of the buffer.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void clearBuffer() throws IOException
    {
        if (bufferSize == 0)
            throw new IllegalStateException("No buffer set");
        index = 0;
    }

    /** The line separator. */
    static String lineSeparator = System.getProperty("line.separator");

    /**
     * Add a newline to the buffer.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void newLine() throws IOException
    {
        write(lineSeparator);
    }

    /**
     * Return the value of this BodyContent as a Reader. Note: this is after
     * evaluation!! There are no scriptlets, etc in this stream.
     * 
     * @return the value of this BodyContent as a Reader
     */
    public Reader getReader()
    {
        if (flushed)
            throw new IllegalStateException(
                    "The stream has already been flushed");
        return new CharArrayReader(buffer, 0, index);
    }

    /**
     * Return the value of the BodyContent as a String. Note: this is after
     * evaluation!! There are no scriptlets, etc in this stream.
     * 
     * @return the value of the BodyContent as a String
     */
    public String getString()
    {
        if (flushed)
            throw new IllegalStateException(
                    "The stream has already been flushed");
        return new String(buffer, 0, index);
    }

    /**
     * Write the contents of this BodyContent into a Writer. Subclasses are
     * likely to do interesting things with the implementation so some things
     * are extra efficient.
     * 
     * @param out
     *            The writer into which to place the contents of this body
     *            evaluation
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeOut(Writer out) throws IOException
    {
        if (flushed)
            throw new IllegalStateException(
                    "The stream has already been flushed");
        out.write(buffer, 0, index);
    }

    /**
     * Get the enclosing JspWriter.
     * 
     * @return the enclosing JspWriter passed at construction time
     */
    public JspWriter getEnclosingWriter()
    {
        return enclosingWriter;
    }

    /**
     * Sets the enclosing writer.
     * 
     * @param encl
     *            the new enclosing writer
     */
    protected void setEnclosingWriter(JspWriter encl)
    {
        this.enclosingWriter = encl;
    }

    /** private fields. */

    private JspWriter enclosingWriter;
    
    /** The out. */
    private PrintWriter out;
    
    /** The buffer. */
    private char buffer[];
    
    /** The index. */
    private int index;
    
    /** The flushed. */
    private boolean flushed = false;
    
    /** The unbounded. */
    public boolean unbounded = false;
}
