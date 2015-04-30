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
