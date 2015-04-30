/*
 * Copyright ss(C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.AssertionFailedError;

import org.alfresco.wcm.client.impl.SectionFactoryCmisImpl;
import org.alfresco.wcm.client.impl.SectionFactoryWebscriptImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SectionFactoryTest extends BaseTest
{
    private final static Log log = LogFactory.getLog(SectionFactoryTest.class);

    public void testGetSections()
    {
        WebSite site = getWebSite();
        Section root = site.getRootSection();
        String rootId = root.getId();

        Section section = sectionFactory.getSectionFromPathSegments(rootId, new String[] { "news" });
        assertNotNull(section);
        // assertNotNull(section.getCollectionFolderId());

        Section bad = sectionFactory.getSectionFromPathSegments(rootId, new String[] { "news", "wooble" });
        assertNull(bad);

        Section exists2 = sectionFactory.getSection(section.getId());
        assertNotNull(exists2);
        // assertNotNull(exists2.getCollectionFolderId());

        log.debug(section.getProperties());
    }

    public void testConcurrentGetSections()
    {
        final WebSite site = getWebSite();
        final Map<String, List<String>> expectedSections = new TreeMap<String, List<String>>();

        if (SectionFactoryCmisImpl.class.isAssignableFrom(sectionFactory.getClass()))
        {
            ((SectionFactoryCmisImpl) sectionFactory).setSectionsRefreshAfter(20);
        }
        else if (SectionFactoryWebscriptImpl.class.isAssignableFrom(sectionFactory.getClass()))
        {
            ((SectionFactoryWebscriptImpl) sectionFactory).setSectionsRefreshAfter(20);
        }
        expectedSections.put("", Arrays.asList(new String[] { "blog", "contact", "news", "publications" }));
        expectedSections.put("/news", Arrays.asList(new String[] { "companies", "global", "markets" }));
        expectedSections.put("/blog", Arrays.asList(new String[] {}));
        expectedSections.put("/contact", Arrays.asList(new String[] {}));
        expectedSections.put("/publications", Arrays.asList(new String[] { "research-reports", "white-papers" }));
        expectedSections.put("/news/companies", Arrays.asList(new String[] {}));
        expectedSections.put("/news/global", Arrays.asList(new String[] {}));
        expectedSections.put("/news/markets", Arrays.asList(new String[] {}));
        expectedSections.put("/publications/research-reports", Arrays.asList(new String[] {}));
        expectedSections.put("/publications/white-papers", Arrays.asList(new String[] {}));

        final List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
        final List<Thread> errorThreads = Collections.synchronizedList(new ArrayList<Thread>());

        Runnable treeWalker = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //Run each thread for 5 mins
                    long timeToStop = System.currentTimeMillis() + 300000L;
                    while (timeToStop > System.currentTimeMillis())
                    {
                        Section section = site.getRootSection();
                        checkSection("", section);
                    }
                }
                catch(AssertionFailedError e)
                {
                    errorThreads.add(Thread.currentThread());
                    throw e;
                }
                finally
                {
                    threads.remove(Thread.currentThread());
                }
            }

            private void checkSection(String path, Section section)
            {
                List<Section> children = section.getSections();
                List<String> childNames = new ArrayList<String>(children.size());
                for (Section child : children)
                {
                    childNames.add(child.getName());
                }
                List<String> expectedChildren = expectedSections.get(path);
                assertNotNull(path, expectedChildren);
                assertEquals(path, expectedChildren.size(), childNames.size());
                childNames.removeAll(expectedChildren);
                assertEquals(path, 0, childNames.size());
                for (Section child : children)
                {
                    checkSection(path + "/" + child.getName(), child);
                }
            }
        };

        for (int i = 0; i < 30; ++i)
        {
            Thread thread = new Thread(treeWalker);
            threads.add(thread);
            thread.start();
        }

        System.out.print("Working");
        while (!threads.isEmpty())
        {
            if (!errorThreads.isEmpty())
            {
                fail("At least one thread has failed");
            }
            System.out.print(".");
            try
            {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e)
            {
            }
        }
        System.out.println();
        System.out.println("Finished");
    }

}
