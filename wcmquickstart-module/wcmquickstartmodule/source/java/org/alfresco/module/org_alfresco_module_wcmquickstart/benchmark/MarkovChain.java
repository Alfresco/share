/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is based on the work done by Lawrence Kesteloot. 
 * 
 * Provides a Markov chain for characters.  For each set of prefix strings,
 * keeps track of possible next characters and the probability
 * of going to each.
 */
public class MarkovChain 
{
    private static Log logger = LogFactory.getLog(MarkovChain.class);

    private Map<String,Chain> chainMap;
    private String bootstrapPrefix;

    /**
     * Creates a chain based on the Reader with a prefix of
     * length "length".  Reads the entire input stream and
     * creates the Markov chain.
     * 
     * @param in
     * @param length
     * @throws java.io.IOException
     */
    public MarkovChain(InputStream in, int length) throws java.io.IOException 
    {
        chainMap = new HashMap<String,Chain>();

        CharQueue queue = new CharQueue(length);
        int c;
        for (int i = 0; i < length; i++) 
        {
            c = in.read();
            if (c == -1) 
            {
                logger.error("Input is too short");
                return;
            }
            queue.put((char)c);
        }

        bootstrapPrefix = queue.toString();

        // for collapsing whitespace
        boolean wasWhitespace = false;

        while ((c = in.read()) != -1) 
        {
            if (Character.isWhitespace((char)c)) 
            {
                if (wasWhitespace) 
                {
                    // collapse continuous whitespace
                    continue;
                }
                c = ' ';
                wasWhitespace = true;
            } 
            else
            {
                wasWhitespace = false;
            }

            String prefix = queue.toString();

            Chain chain = (Chain)chainMap.get(prefix);
            if (chain == null) 
            {
                chain = new Chain(prefix);
                chainMap.put(prefix, chain);
            }
            chain.add((char)c);
            queue.put((char)c);
        }
    }

    /**
     * Returns the first "length" characters that were read.
     */
    public String getBootstrapPrefix() 
    {
        return bootstrapPrefix;
    }

    /**
     * Returns the next character to print given the prefix.
     * Returns -1 when there are no possible next characters.
     * 
     * @param prefix
     * @param random
     * @return
     */
    public int get(String prefix, Random random) 
    {
        Chain chain = (Chain)chainMap.get(prefix);
        if (chain == null) 
        {
            return -1;
        }
        int index = random.nextInt(chain.getTotal());
        return chain.get(index);
    }

    /**
     * Prints the contents of the Markov graph.
     */
    public void dump() 
    {
        Set<String> keys = chainMap.keySet();
        for (String key : keys)
        {
            Chain chain = (Chain)chainMap.get(key);
            chain.dump();
        }
    }

    /**
     * List of possible next characters and their probabilities.
     */
    public static class Chain 
    {
        private String prefix;
        private int total;
        private List<Link> list;

        /**
         * @param prefix
         */
        public Chain(String prefix) 
        {
            this.prefix = prefix;
            total = 0;
            list = new LinkedList<Link>();
        }

        /**
         * @return
         */
        public String getPrefix() 
        {
            return prefix;
        }

        /**
         * @return
         */
        public int getTotal() 
        {
            return total;
        }

        /**
         * @param index
         * @return
         */
        public char get(int index) 
        {
            Iterator<Link> i = list.iterator();
            while (i.hasNext()) 
            {
                Link link = (Link)i.next();
                int count = link.getCount();

                if (index < count) 
                {
                    return link.getChar();
                }
                index -= count;
            }

            // weird
            return '@';
        }

        /**
         * @param c
         */
        public void add(char c) 
        {
            Iterator<Link> i = list.iterator();
            boolean found = false;

            while (i.hasNext()) 
            {
                Link link = (Link)i.next();

                if (c == link.getChar()) 
                {
                    link.increment();
                    found = true;
                    break;
                }
            }

            if (!found) 
            {
                Link link = new Link(c);
                list.add(link);
            }

            total++;
        }

        /**
         * 
         */
        public void dump() 
        {
            logger.info(prefix + ": (" + total + ")");
            Iterator<Link> i = list.iterator();
            while (i.hasNext()) 
            {
                Link link = (Link)i.next();
                logger.info("    " + link.getChar() + " (" +
                        link.getCount() + ")");
            }
        }

        /**
         * Possible next character and the number of times we've
         * seen it.
         */
        private static class Link 
        {
            private char c;
            private int count;

            public Link(char c) 
            {
                this.c = c;
                count = 1;
            }

            public void increment() 
            {
                count++;
            }

            public int getCount() 
            {
                return count;
            }

            public char getChar() 
            {
                return c;
            }
        }
    }
}

