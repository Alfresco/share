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
package org.springframework.extensions.surf.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;

/**
 * <p>
 * This class should be extended by any classes providing test cases for the Spring Surf FVT application. It is important that
 * this class does <b>not</b> extend <code>junit.framework.TestCase</code> otherwise JUnit 3 will be used and the annotations will
 * not be recognised. However, this does mean that all test methods must be annotated with the <code>Test</code> annotation</b>.
 * </p>
 * <p>
 * When making requests to the Jetty server the <code>makeHttpRequest</code> method should be used which will return a <code>WebTestArtifact</code> object which
 * can be used to retrieve the results of the request.
 * </p>
 *
 * @author David Draper
 */
public abstract class AbstractJettyTest
{
    /**
     * Use this method to make an HTTP request to the Jetty server running the FVT application. It will make a request to the specified resource using the
     * supplied HTTP method (typically "GET" or "POST"). A <code>WebTestArtifact</code> will be returned which can be used to obtain the requests response code
     * and response body. PLEASE NOTE: It is only necessary to specify the required resource relative to the context root, not the full URL. The prefix for the
     * URL (server, port and context root) is obtained from the field <code>_URL_PREFIX</code>
     *
     * @param resource The resource relative to the context root.
     * @param httpMethod The HTTP method to use when making the request.
     * @return A <code>WebTestArtifact</code> containing the results of the HTTP request.
     */
    protected WebTestArtifact makeHttpRequest(String resource, String httpMethod)
    {
        return new WebTestArtifact(resource, httpMethod);
    }

    /**
     * Use this method to make an HTTP GET request to the Jetty server running the FVT application. A <code>WebTestArtifact</code> will be returned which can be
     * used to obtain the requests response code and response body. PLEASE NOTE: It is only necessary to specify the required resource relative to the context
     * root, not the full URL. The prefix for the URL (server, port and context root) is obtained from the field <code>_URL_PREFIX</code>
     *
     * @param resource The resource relative to the context root.
     * @return A <code>WebTestArtifact</code> containing the results of the HTTP request.
     */
    protected WebTestArtifact makeHttpRequest(String resource)
    {
        return new WebTestArtifact(resource);
    }

    /**
     * This is a prefix to add to test messages so that they are more visible in the standard out. This has been broken out into a separate constant since
     * Strings are immutable and to save a new String being constructed for each method call.
     */
    private static final String _MESSAGE_PREFIX = ">>> ";

    /**
     * This method should be used when logging test messages. Currently it just defers to Sysout but in the future there may be a reason why we want to use an
     * alternative logging mechanism and this will save us needing to replace a load of Sysouts in test code.
     *
     * @param message The message to log.
     */
    protected void logTestMessage(String message)
    {
        System.out.println(_MESSAGE_PREFIX + message);
    }

    /**
     * A constant for "BeforeDefaultComponentChrome". This is the String that prefixes the <componentInclude> call in component chrome files. Changing this value
     * will effect the behaviour of the <code>findWrappedInDefaultComponentChrome</code> method.
     */
    public static final String _DEFAULT_COMPONENT_CHROME_PREFIX = "BeforeDefaultComponentChrome";

    /**
     * A constant for "AfterDefaultComponentChrome". This is the String that suffixes the <componentInclude> call in component chrome files. Changing this value
     * will effect the behaviour of the <code>findWrappedInDefaultComponentChrome</code> method.
     */
    public static final String _DEFAULT_COMPONENT_CHROME_SUFFIX = "AfterDefaultComponentChrome";

    /**
     * A constant for "BeforeDefaultRegionChrome". This is the String that suffixes the <regionInclude> call in region chrome files. Changing this value
     * will effect the behaviour of the <code>findWrappedInDefaultRegionChrome</code> method.
     */
    public static final String _DEFAULT_REGION_CHROME_PREFIX = "BeforeDefaultRegionChrome";

    /**
     * A constant for "AfterDefaultRegionChrome". This is the String that suffixes the <regionInclude> call in region chrome files. Changing this value
     * will effect the behaviour of the <code>findWrappedInDefaultRegionChrome</code> method.
     */
    public static final String _DEFAULT_REGION_CHROME_SUFFIX = "AfterDefaultRegionChrome";

    /**
     * <p>Convenience method for checking that a target String appears in the source between the default component chrome
     * prefix and suffix. Use this method when checking for content between default component chrome so that if the chrome
     * is changed it is not necessary to update test calls</p>
     *
     * @param target The String to search for in the source text (which will be embedded between the default component chrome prefix/suffix.
     * @param source The source text to search for the target in.
     * @return The remainder of the source text immediately after the location of the target and chrome.
     */
    protected String findWrappedInDefaultComponentChrome(String target, String source)
    {
        return findBetweenPrefixAndSuffix(target, source, _DEFAULT_COMPONENT_CHROME_PREFIX, _DEFAULT_COMPONENT_CHROME_SUFFIX);
    }

    /**
     * <p>Convenience method for checking that a target String appears in the source between the default region chrome
     * prefix and suffix. Use this method when checking for content between default region chrome so that if the chrome
     * is changed it is not necessary to update test calls</p>
     *
     * @param target The String to search for in the source text (which will be embedded between the default region chrome prefix/suffix.
     * @param source The source text to search for the target in.
     * @return The remainder of the source text immediately after the location of the target and chrome.
     */
    protected String findWrappedInDefaultRegionChrome(String target, String source)
    {
        return findBetweenPrefixAndSuffix(target, source, _DEFAULT_REGION_CHROME_PREFIX, _DEFAULT_REGION_CHROME_SUFFIX);
    }

    /**
     * <p>Method for finding a target String between a prefix and a suffix (for example chrome). If the target, prefix and suffix cannot
     * be found then this method will return an assertion error causing JUnit tests to fail. If it successfully finds the prefix, target and suffix
     * then it will return a String that is substring of the total match.</p>
     *
     * @param target The target String to find in the source.
     * @param source The source String to search for the target in.
     * @param prefix A String that must prefix the target String in the source.
     * @param suffix A String that must suffix the target String in the source.
     * @return The remainder of the source starting after the last character of the chrome suffix.
     */
    protected String findBetweenPrefixAndSuffix(String target, String source, String prefix, String suffix)
    {
        // Use RegEx to find all instances of the target (because there could be more than one!), then for each
        // match attempt to find the prefix before and suffix after it. RegEx is not being used to find the entire
        // sequence because it doesn't work from inside out so may match an earlier instance of the prefix.
        String matchingSubString = null;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(source);
        while(m.find())
        {
            String prefixSource = source.substring(0, m.start());
            int startingIndexOfPrefix = prefixSource.lastIndexOf(prefix);
            if (startingIndexOfPrefix == -1)
            {
                // If the prefix cannot be found then continue to the next loop (if there are more matches!)
                continue;
            }
            else
            {
                //int endingIndexOfTarget = startingIndexOfTarget + target.length();
                String suffixSource = source.substring(m.end());
                int startingIndexOfSuffix = suffixSource.indexOf(suffix);
                if (startingIndexOfSuffix == -1)
                {
                    // If the suffix cannot be found then continue to the next loop (if there are more matches!)
                    continue;
                }
                else
                {
                    // If the target, prefix and suffix have all been found then output the substring as the result...
                    matchingSubString = source.substring(startingIndexOfPrefix, m.end() + startingIndexOfSuffix + suffix.length());
                    break;
                }
            }
        }

        // If a match could not be found then throw an AssertionError to ensure that the unit test fails...
        if (matchingSubString == null)
        {
            throw new AssertionError("Could not find prefix, target and suffix: " + prefix + ", " + target + ", " + suffix);
        }
        return matchingSubString;
    }


    protected void checkResponse(String resource, String httpMethod, String expected)
    {
        for (int i = 0; i < 2; i++)
        {
            String response = requestResourceAssertingResponse(resource, httpMethod);
            Assert.assertEquals(expected, response);
        }
    }

    protected void checkResponseForContent(String resource, String httpMethod, String expected)
    {
        for (int i = 0; i < 2; i++)
        {
            String response = requestResourceAssertingResponse(resource, httpMethod);
            Assert.assertTrue(response.indexOf(expected) != -1);
        }
    }

    /**
     * Requests a resource from the application and checks the response to see that the supplied Strings are all present and occur in the order provided.
     *
     * @param resource The resource to request from the application
     * @param httpMethod The HTTP method to use
     * @param strings A list of Strings to search for in the response
     * @throws Exception
     */
    protected void checkResponseTextOrdering(String resource, String httpMethod, String... strings) throws AssertionError
    {
        for (int j = 0; j < 2; j++)
        {
            String response = requestResourceAssertingResponse(resource, httpMethod);
            assertOccurenceAndOrder(response, strings);
        }
    }

    /**
     * <p>Requests a resource from the embedded Jetty server provided by the instance of this class. The response code is checked
     * to ensure that 200 is returned and then the response is returned to the caller.</p>
     * @param resource The resource to request (only the resource is required as the URL and context root are already known)
     * @param httpMethod The HTTP method to use when requesting the resource.
     * @return The response returned from the request.
     */
    protected String requestResourceAssertingResponse(String resource, String httpMethod)
    {
        String response;

        logTestMessage("Requesting: " + resource);

        // Get the response from requesting the supplied resource...
        WebTestArtifact wta = makeHttpRequest(resource, httpMethod);

        // First of all lets check that the response code is correct...
        Assert.assertEquals(200, wta.getResponseCode());

        response = wta.getResponse();
        logTestMessage("Response: " + response);

        return response;
    }

    protected String findFirstOccurrenceAndReturnRemainder(String source, String target)
    {
        String remainder;
        int startingIndex = source.indexOf(target);
        if (startingIndex != -1)
        {
            remainder = source.substring(startingIndex, startingIndex + target.length());
        }
        else
        {
            throw new AssertionError("Could not find target: " + target);
        }
        return remainder;
    }


    /**
     * <p>Checks that the supplied source text contains all the target strings in the order provided. If any of the targets are not found (or if
     * they do not occur in the expected order) then an <code>AssertionError</code> will be thrown.</p>
     * @param source The String to search for the targets in.
     * @param targets The targets to find in the source.
     * @return Returns the substring that matches the start of the first target to the end of the last.
     */
    protected String assertOccurenceAndOrder(String source, String... targets)
    {
        String match = source;
        int startIndexOfFirstTarget = 0;
        int endIndexOfLastTarget = 0;

        // Filter the supplied targets to ensure that there are no nulls or empty Strings. This prevents NullPointerExceptions
        // and also accommodates default component and region chrome bing updated to have zero length prefixes and suffixes
        // (which is a very real possibility).
        List<String> filteredTargets = new ArrayList<String>();
        for (String currentTarget: targets)
        {
            if (currentTarget == null || currentTarget.equals(""))
            {
                // If the current String is either null or the empty String then don't add it to the list of filtered
                // Strings as it would only cause and exception or a problem!
            }
            else
            {
                // Providing that the current String is neither null nor the empty String then it can be safely added to the
                // list of filtered Strings...
                filteredTargets.add(currentTarget);
            }
        }

        if (filteredTargets.size() > 0)
        {
            // In order to be able to search for repeating Strings (and not just find the first occurrence
            // of the String each time) we need to discard all characters up until the end of the target String
            // but keep track of the number characters discarded. Declare variables to keep track of these!
            int totalCharsDiscarded = 0;

            // Check for the first String outside of the loop (as the logic requires the "previous" target to
            // already be established...
            String previousTarget = filteredTargets.get(0);
            int previousTargetIndex = source.indexOf(previousTarget);
            if (previousTargetIndex == -1)
            {
                // The very first target could not be found so throw an assertion error to cause the unit test to fail...
                throw new AssertionError("Could not find target: \"" + previousTarget + "\" in source: \"" + source + "\"");
            }

            // Take note of the starting index of the first result...
            startIndexOfFirstTarget = previousTargetIndex;

            // Remove everything from the response up until the end of the first target (taking note
            // of the number of characters to discard and adding them to the total discarded)...
            int charsToDiscard = previousTarget.length() + previousTargetIndex - totalCharsDiscarded;
            source = source.substring(charsToDiscard);
            totalCharsDiscarded += charsToDiscard;

            // Only bother going into the loop if we can find the first target!
            if (previousTargetIndex != -1)
            {
                for (int i = 1; i < filteredTargets.size(); i++)
                {
                    // Get the current String and the following String...
                    String currentTarget = filteredTargets.get(i);
                    logTestMessage("Checking that '" + previousTarget + "' comes before '" + currentTarget + "'");

                    int currentTargetIndex = source.indexOf(currentTarget);
                    if (currentTargetIndex == -1)
                    {
                        // Could not find the current target! Throw a new assertion error to cause the unit test to fail..
                        throw new AssertionError("Could not find target: \"" + currentTarget + "\" in source: \"" + source + "\"");
                    }
                    else
                    {
                        // Update the current index to compensate for the previous number of characters discarded so the indices
                        // of previous and current targets be accurately compared...
                        currentTargetIndex += totalCharsDiscarded;
                        if (previousTargetIndex > currentTargetIndex)
                        {
                            throw new AssertionError("Target: \"" + previousTarget + "\" occurs after target: \"" + currentTarget + "\"");
                        }
                        else
                        {
                            // Copy the current target data to the previous target data for the next loop...
                            previousTarget = currentTarget;
                            previousTargetIndex = currentTargetIndex;

                            // Remove everything from the response up until the end of the String that we have found (taking note
                            // of the number of characters to discard and adding them to the total discarded)...
                            charsToDiscard = previousTarget.length() + previousTargetIndex - totalCharsDiscarded;
                            source = source.substring(charsToDiscard);
                            totalCharsDiscarded += charsToDiscard;
                        }
                    }
                }

                // Take note of the end index of the final target...
                endIndexOfLastTarget = previousTargetIndex + previousTarget.length();
            }
            else
            {
                throw new AssertionError("Could not find String in response: " + previousTarget);
            }
        }
        else
        {
            logTestMessage("No targets provided!");
        }

        match = match.substring(startIndexOfFirstTarget, endIndexOfLastTarget);
        return match;
    }

    /**
     * <p>This can be used to make a multipart form POST submission and retrieve the response for further analyis.</p>
     * @param resource The location to post to (this only needs to be relative to the context root of the application)
     * @param stringKeyToValueMap A map of the String key/value pairs to be posted (these will become form arguments)
     * @param fileNameToLocationMap A map of the Files to post (the key will be the form argument)
     * @return The response from the HTTP POST.
     * @throws IOException
     */
    protected String getFilePostResponse(String resource,
                                         HashMap<String, String> stringKeyToValueMap,
                                         HashMap<String, String> fileNameToLocationMap) throws IOException
    {
        String response = null;

        // Check that all the file locations can be resolved to actual files and throw an AssertionError if any
        // files cannot be found. If the files are found, insert them into the new map to be passed to the
        // WebTestArtifact constructor as that's what it's expecting.
        HashMap<String, File> fileParts = new HashMap<String, File>();
        for (Entry<String, String> file: fileNameToLocationMap.entrySet())
        {
            File f = new File(file.getValue());
            if (f.exists())
            {
                fileParts.put(file.getKey(), f);
            }
            else
            {
                throw new AssertionError("Could not locate file: \"" + file.getValue() + "\" for posting");
            }
        }

        logTestMessage("Requesting: " + resource);
        WebTestArtifact wta = new WebTestArtifact(resource, stringKeyToValueMap, fileParts);

        // Get the response from requesting the supplied resource...
        // First of all lets check that the response code is correct...
        Assert.assertEquals(200, wta.getResponseCode());

        response = wta.getResponse();
        logTestMessage("Response: " + response);

        return response;
    }

    /**
     * A WebTestArtifact is an inner class intended to provide an easy way to generate and get results for HTTP requests. It has a private constructor so can
     * only be instantiated from the parent class - this should prevent unintended use. An instance should be created by providing a URL and an HTTP method and
     * the resulting object can then be used to obtain HTTP response codes and the response body. The reason that it is an inner class is so that it is
     * guaranteed that the server, port and context root information is correct for use with the test Jetty server.
     *
     * @author Dave Draper
     */
    public class WebTestArtifact
    {
        /**
         * This will be set to the response of making the connection.
         */
        private String response = null;

        /**
         * This will be set to the response returned from making the connection.
         */
        private int responseCode;

        /**
         * Creates a new HttpURLConnection object using the HTTP GET method by default.
         *
         * @param resource The resource to request. PLEASE NOTE: this is the suffix to the <code>_URL_PREFIX</code> defined in the outer class.
         */
        private WebTestArtifact(String resource)
        {
            this(resource, "GET");
        }

        /**
         * <p>This constructor can be used to generate an HTTP POST that simulates the multipart form encoding submission. A combination
         * of String and File parameters can be provided in maps (where the key will become the form attribute name).</p>
         *
         * @param resource The location to post to (this only needs to be relative to the context root of the application)
         * @param stringParts A map of the String key/value pairs to be posted (these will become form arguments)
         * @param fileParts A map of the Files to post (the key will be the form argument)
         * @throws IOException
         */
        private WebTestArtifact(String resource, HashMap<String, String> stringParts, HashMap<String, File> fileParts) throws IOException
        {
            // Set up the client with the URL to POST to...
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AbstractTestServerSetup._URL_PREFIX + resource);
            MultipartEntity reqEntity = new MultipartEntity();

            // Add all the File parts to be posted...
            for (Entry<String, File> f: fileParts.entrySet())
            {
                FileBody bin = new FileBody(f.getValue());
                reqEntity.addPart(f.getKey(), bin);
            }

            // Add all the String parts to be posted...
            for (Entry<String, String> s: stringParts.entrySet())
            {
                reqEntity.addPart(s.getKey(),  new StringBody(s.getValue()));
            }

            // Set the parts, execute the request and get the response...
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            // Set the response code...
            this.responseCode = response.getStatusLine().getStatusCode();

            // Build the response String...
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(resEntity.getContent()));
            String line = br.readLine();
            while (line != null)
            {
                sb.append(line);
                line = br.readLine();
            }
            this.response = sb.toString();
        }

        /**
         * Creates a new HttpURLConnection object using the supplied URL and HTTP method and connects to it.
         *
         * @param resource The resource to request. PLEASE NOTE: this is the suffix to the <code>_URL_PREFIX</code> defined in the outer class.
         * @param httpMethod The HTTP method to use for the connection.
         */
        private WebTestArtifact(String resource, String httpMethod)
        {
            HttpURLConnection connection = null;

            try
            {
                // Create the connection (using some sensible defaults)...
                URL url = new URL(AbstractTestServerSetup._URL_PREFIX + resource);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(httpMethod);
                connection.setDoOutput(true);
                connection.setReadTimeout(5000);
                connection.connect();

                // Read the response...
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }

                this.responseCode = connection.getResponseCode();
                this.response = sb.toString();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                // Make sure the connection is always cleanly taken down.\
                if (connection != null) connection.disconnect();
            }
        }

        /**
         * Returns the response code generated by making the connection to the requested URL.
         *
         * @return An HTTP response code (e.g. 404 for not found, 200 for OK, etc.
         */
        public int getResponseCode()
        {
            return this.responseCode;
        }

        /**
         * Returns the response generated by making the connection to the requested URL.
         *
         * @return A String containing the HTTP response.
         */
        public String getResponse()
        {
            return this.response;
        }
    }
}
