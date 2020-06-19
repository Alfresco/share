/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.wcm.client.util.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.alfresco.wcm.client.impl.WebScriptCaller;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class AlfrescoTicketCmisAuthenticationProviderTest
{
    AlfrescoTicketCmisAuthenticationProvider authProvider;
    
    @Before
    public void setUp() throws Exception
    {
        WebScriptCaller mockedWebscriptCaller = mock(WebScriptCaller.class);
        when(mockedWebscriptCaller.getTicket(any(String.class), any(String.class))).thenAnswer(new Answer<String>() {
            private int ticketNum = 1;
            
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable
            {
                return "ticket" + ticketNum++;
            }
        });

        BindingSession mockedSession = mock(BindingSession.class);
        when(mockedSession.get(SessionParameter.USER)).thenReturn("admin");
        when(mockedSession.get(SessionParameter.PASSWORD)).thenReturn("admin");
        
        authProvider = new AlfrescoTicketCmisAuthenticationProvider();
        authProvider.setWebscriptCaller(mockedWebscriptCaller);
        authProvider.setSession(mockedSession);
        authProvider.setRefetchTicketDelay(2000L);
    }

    @Test
    public void testGetPassword()
    {
        
        Set<String> replies = new TreeSet<String>();
        for (int i = 0; i < 1000; ++i)
        {
            replies.add(authProvider.getPassword());
        }
        Assert.assertEquals(1, replies.size());
        for (String reply : replies)
        {
            Assert.assertTrue(reply.startsWith("ticket"));
        }
    }
    
    @Test
    public void testGetPasswordWithErrors() throws InterruptedException
    {
        
        Set<String> replies = new TreeSet<String>();
        long stopTime = System.currentTimeMillis() + 3000L;
        while (stopTime > System.currentTimeMillis())
        {
            replies.add(authProvider.getPassword());
            authProvider.putResponseHeaders(null, 401, null);
            Thread.sleep(10);
        }
        Assert.assertEquals(2, replies.size());
        for (String reply : replies)
        {
            Assert.assertTrue(reply.startsWith("ticket"));
        }
    }

    @Test
    public void testMultithreadedGetPasswordWithErrors() throws InterruptedException
    {
        
        final Set<String> replies = Collections.synchronizedSet(new TreeSet<String>());
        final long stopTime = System.currentTimeMillis() + 3000L;
        final List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>(20));
        
        for (int i = 0; i < 20; ++i)
        {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run()
                {
                    while (stopTime > System.currentTimeMillis())
                    {
                        replies.add(authProvider.getPassword());
                        authProvider.putResponseHeaders(null, 401, null);
                        try
                        {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    threads.remove(Thread.currentThread());
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (int i=0; !threads.isEmpty(); i++)
        {
            if (i >= 600) // give up after approx 5 mins - hung the build
            {
                fail("Not all threads ("+threads.size()+") are still to finish.");
            }
            Thread.sleep(500);
        }
        
        Assert.assertEquals(2, replies.size());
        for (String reply : replies)
        {
            Assert.assertTrue(reply.startsWith("ticket"));
        }
    }
}
