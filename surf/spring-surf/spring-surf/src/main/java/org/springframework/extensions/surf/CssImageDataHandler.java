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
package org.springframework.extensions.surf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.Base64;

/**
 * <p>This class provides the capability to search through CSS resources and convert all URL references to be Base64 encoded data. This
 * can have performance benefits since as CSS files that include data rather than links will reduce the number of HTTP requests that the
 * browser needs to make. Processing CSS resources in this way will only be enabled if the Surf application configuration sets the 
 * property <{@code}generate-css-data-images> to <code>true</code> (however, is will only have an effect if the property 
 * {@code}use-checksum-dependencies> is also set to <code>true</code>.</p>
 * 
 * @author David Draper
 */
public class CssImageDataHandler
{
    private static final Log logger = LogFactory.getLog(DependencyHandler.class);
    
    /**
     * <p>This is the first part of the CSS data image text. This is the text that runs up until the MIME type of the image.</p>
     */
    public static final String DATA_PREFIX_PART1 = "data:image/";
    
    /**
     * <p>This is the second part of the CSS data image text. It runs from the MIME type to the Base64 encoded data.</p>
     */
    public static final String DATA_PREFIX_PART2 = ";base64,";
    
    /**
     * <p>This is the String used to target the start of image URL. <b>NOTE: At the moment this assumes URL only applies to "background-image"
     * which is technically not guaranteed in the specification.</b></p> 
     */
    public static final String URL_OPEN_TARGET_PATTERN = "url(";
    
    /**
     * <p>This is the String used to target the end of an image URL. The first occurrence of the closing bracket after the
     * opening bracket should mark the end of the URL because it is not valid as part of a URL.</p>
     */
    public static final String URL_CLOSE_TARGET_PATTERN = ")";
    
    /**
     * <p>Constant for the forward slash "/"</p>
     */
    public static final String FORWARD_SLASH = "/";
    
    /**
     * <p>Constant for the full stop "." (or period as it is known in the US). In this context it is used to indicate the current location
     * in paths.</p>
     */
    public static final String FULL_STOP = ".";
    
    /**
     * <p>Constant for double full stop ".." (or period as it is known in the US). In this context it is used to indicate the part folder of 
     * the current location.</p>
     */
    public static final String DOUBLE_FULL_STOP = "..";
    
    /**
     * <p>Constant for the double quote '"'.</p>
     */
    public static final String DOUBLE_QUOTES = "\"";
    
    /**
     * <p>Constant for the single quote "'"</p>
     */
    public static final String SINGLE_QUOTE = "'";
    
    public static final String DOUBLE_FULL_STOP_SLASH = DOUBLE_FULL_STOP + FORWARD_SLASH;
    
    /**
     * <p>This is the maximum size (in bytes) that an image is allowed to be encoded at. If an image is larger than this size
     * then it will be left as is. This is because the image takes longer to render from the encoding in the browser than it
     * does when rendered directly.</p> 
     */
    private Integer maximumImageSize; 
    
    /**
     * <p>Gets the maximum image size to allow to be encoded</p>
     * @return
     */
    public Integer getMaximumImageSize()
    {
        return maximumImageSize;
    }

    /**
     * <p>This setter is provided to allow the Spring application context to set the maximum size of image to encode (in bytes).</p>
     * 
     * @param maximumImageSize The maximum size of an image to encode (in bytes)
     */
    public void setMaximumImageSize(Integer maximumImageSize)
    {
        this.maximumImageSize = maximumImageSize;
    }

    /**
     * <p>A {@link DependencyHandler} is required for actually locating image resources for conversion. This variable should be set 
     * through the Spring application context configuration.</p>
     */
    private DependencyHandler dependencyHandler;
    
    public DependencyHandler getDependencyHandler()
    {
        return dependencyHandler;
    }
    
    /**
     * <p>Setter provided to allow the Spring application context to set the {@link DependencyHandler}.</p>
     * @param dependencyHandler
     */
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    
    /**
     * <p>A list of mimetypes (defined as filename extensions) that should be excluded from processing. For example, "css" files
     * and extensions used for font-faces are defined by default</p>
     */
    private List<String> excludeMimeTypes;
    
    public List<String> getExcludeMimeTypes()
    {
        return excludeMimeTypes;
    }

    public void setExcludeMimeTypes(List<String> excludeMimeTypes)
    {
        this.excludeMimeTypes = excludeMimeTypes;
    }

    /**
     * <p>It makes sense to exclude certain CSS files from processing because they may already use CSS sprites rather
     * than individual images. In this instance it would mean that the same image is reproduced repeatedly within the
     * CSS file whereas it would make more sense to leave it as a separate file that the browser can download and the
     * CSS file will reference parts of the sprite as normal.</p>
     */
    private List<String> excludeCssPaths;
    
    public List<String> getExcludeCssPaths()
    {
        return excludeCssPaths;
    }

    public void setExcludeCssPaths(List<String> excludeCssPaths)
    {
        this.excludeCssPaths = excludeCssPaths;
    }

    /**
     * <p>This represents the {@link List} of compiled {@link Pattern} instances generated from the {@link List} of Strings
     * defined in the Spring bean configuration. This {@link List} will be generated the first time that the <code>getExcludePatterns</code>
     * method is called (that method should be used instead of referencing the attribute directly).</p>
     */
    private List<Pattern> excludePatterns = null;
    protected synchronized List<Pattern> getExcludePatterns()
    {
        if (this.excludePatterns == null)
        {
            this.excludePatterns = new ArrayList<Pattern>();
            for (String excludePath: this.excludeCssPaths)
            {
                Pattern p = Pattern.compile(excludePath);
                this.excludePatterns.add(p);
            }
        }
        return this.excludePatterns;
    }
    
    /**
     * <p>Updates the supplied {@link StringBuilder} (which should contain the contents of a CSS file) so that
     * all image URLs are converted into Base64 encoded data. This method uses the {@link DependencyHandler} to 
     * load the image file and if the image cannot be loaded then it will not be encoded and will be left as 
     * the original URL.</p>
     *   
     * @param cssPath The path the CSS file.
     * @param cssContents The contents of the CSS file. 
     * @throws IOException
     */
    public void processCssImages(String cssPath, StringBuilder cssContents) throws IOException
    {
        boolean exclude = false;
        
        // Check whether or not the current CSS file should be excluded...
        for (Pattern excludePath: this.getExcludePatterns())
        {
            Matcher m = excludePath.matcher(cssPath);
            if (m.matches())
            {
                exclude = true;
                break;
            }
        }
        
        if (!exclude)
        {
            String pathPrefix = cssPath.substring(0, cssPath.lastIndexOf(FORWARD_SLASH));
            
            int index = cssContents.indexOf(URL_OPEN_TARGET_PATTERN);
            while (index != -1)
            {
                int matchingClose = cssContents.indexOf(URL_CLOSE_TARGET_PATTERN, index + URL_OPEN_TARGET_PATTERN.length());
                if (matchingClose == -1)
                {
                    // This would be a CSS error!
                    return;
                }
                else
                {
                    // Get the image source and trim any white space...
                    String imageSrc = cssContents.substring(index + URL_OPEN_TARGET_PATTERN.length(), matchingClose).trim();
                    
                    // Remove opening and closing quotes...
                    if (imageSrc.startsWith(DOUBLE_QUOTES) || imageSrc.startsWith(SINGLE_QUOTE))
                    {
                        imageSrc = imageSrc.substring(1);
                    }
                    if (imageSrc.endsWith(DOUBLE_QUOTES) || imageSrc.endsWith(SINGLE_QUOTE))
                    {
                        imageSrc = imageSrc.substring(0, imageSrc.length() -1);
                    }
                    
                    // Clear any pointless current location markers...
                    if (imageSrc.startsWith(FULL_STOP) && !imageSrc.startsWith(DOUBLE_FULL_STOP))
                    {
                        imageSrc = imageSrc.substring(1);
                    }
                    
                    // Check the MIME type of the image (this will need to be included in the CSS data information)...
                    String mimetype = null;
                    int extIndex = imageSrc.lastIndexOf(FULL_STOP);
                    if (extIndex != -1)
                    {
                        mimetype = imageSrc.substring(extIndex + 1);
                    }
                    
                    // Only proceed with the encoding if we have an extension to use as a MIME type...
                    if (mimetype != null && !this.excludeMimeTypes.contains(mimetype))
                    {
                        StringBuilder sb = new StringBuilder(pathPrefix);
                        if (!imageSrc.startsWith(FORWARD_SLASH))
                        {
                            sb.append(FORWARD_SLASH);
                        }
                        sb.append(imageSrc);
                        
                        // Get the encoded image...
                        String encodedImage = getEncodedImage(sb.toString());
                        if (encodedImage != null)
                        {
                            // Update the CSS source to replace the URL with the encoded image data... 
                            int offset = index + URL_OPEN_TARGET_PATTERN.length();
                            cssContents.delete(offset, matchingClose);               // Delete the original URL
                            offset = insert(cssContents, offset, DATA_PREFIX_PART1); // Add the first part of the prefix...
                            offset = insert(cssContents, offset, mimetype);          // Add the mimetype...
                            offset = insert(cssContents, offset, DATA_PREFIX_PART2); // Add the second part of the prefix...
                            offset = insert(cssContents, offset, encodedImage);      // Add the encoded image
                            
                            // Continue the search for the next image...
                            index = cssContents.indexOf(URL_OPEN_TARGET_PATTERN, offset);
                        }
                        else
                        {
                            index = cssContents.indexOf(URL_OPEN_TARGET_PATTERN, matchingClose);
                        }
                    }
                    else
                    {
                        // Since there was no mime type, leave the image as is
                        index = cssContents.indexOf(URL_OPEN_TARGET_PATTERN, matchingClose);
                    }
                }
            }
        }
    }
    
    /**
     * <p>Helper method for inserting content into a {@link StringBuilder} whilst maintaining a reference
     * to the offset that indicates the end of the inserted content.</p>
     * 
     * @param toUpdate The {@link StringBuilder} to update.
     * @param offset The current offset (e.g. where to insert the supplied String)
     * @param toInsert The String to insert.
     * @return A new offset that indicates the end of the inserted String within the {@link StringBuilder}.
     */
    public int insert(StringBuilder toUpdate, int offset, String toInsert)
    {
        toUpdate.insert(offset, toInsert);
        return offset + toInsert.length();
    }
    
    /**
     * <p>A lock for controlling access to the encoded image cache.</p>
     */
    private ReentrantReadWriteLock encodedImageCacheLock = new ReentrantReadWriteLock();
    
    /**
     * <p>A cache of previously encoded images. A single image might be used multiple times within one or more
     * CSS files so there is no point in repeatedly encoding it.</p>
     */
    private Map<String, String> encodedImageCache = new HashMap<String, String>();
    
    /**
     * <p>The empty String is used as the missing image sentinel. We cannot use null as this will indicate that
     * that the encoded image does not exist in the cache. However the empty String can be used as no image will
     * ever be encoded to the empty String as it MUST contain some data.</p>
     */
    public static final String MISSING_IMAGE_SENTINENEL = "";
    
    /**
     * <p>Gets the encoded image for the supplied path. This method will first check the private cache to see
     * if the image has previously been encoded and if it cannot be found then it will attempt to load the
     * image and encode it. If the image resource could not be loaded then this method will return <code>null</code></p>
     * 
     * @param path The path to the image to get the encoded String representation.
     * @return A Base64 encoded String representing the image or <code>null</code> if the image could not be loaded.
     */
    public String getEncodedImage(String path)
    {
        String encodedImage = null;
        
        // Check the cache to see if the image has previously been encoded...
        this.encodedImageCacheLock.readLock().lock();
        try
        {
            encodedImage = this.encodedImageCache.get(path);
        }
        finally
        {
            this.encodedImageCacheLock.readLock().unlock();
        }
        
        if (encodedImage == null)
        {
            this.encodedImageCacheLock.writeLock().lock();
            try
            {
                // check again as multiple threads could been waiting on the write lock
                encodedImage = this.encodedImageCache.get(path);
                if (encodedImage == null)
                {
                    // The image has not previously been encoded, load the image and encode it now...
                    InputStream in = this.dependencyHandler.getResourceInputStream(path);
                    if (in != null)
                    {
                        encodedImage = encodeImage(in);
                    }
                    
                    if (encodedImage != null)
                    {
                        // Store the encoded image in the cache...
                        this.encodedImageCache.put(path, encodedImage);
                    }
                    else if (in == null)
                    {
                        // If the image could not be loaded then store a sentinel in the cache
                        // so that we avoid trying to look it up again...
                        this.encodedImageCache.put(path, MISSING_IMAGE_SENTINENEL);
                    }
                }
            }
            catch (IOException e)
            {
                logger.error("The following error occurred attempting to Base64 encode \"" + path + "\": ", e);
            }
            finally
            {
                this.encodedImageCacheLock.writeLock().unlock();
            }
        }
        else if (encodedImage == MISSING_IMAGE_SENTINENEL)
        {
            // If the cache returned the sentinel object the switch it back to be null to 
            // indicate that the image could not be found...
            encodedImage = null;
        }
        return encodedImage;
    }
    
    /**
     * <p>Converts the image file in the supplied {@link InputStream} into a Base64 encoded String.</p>
     * 
     * @param in An {@link InputStream} of the image to convert.
     * @throws IOException
     */
    protected String encodeImage(final InputStream in) throws IOException
    {
        String encodedImage = null;
        
        // Set up a new OutputStream which is passed to the Base64 encoder so that the data 
        // copied from the InputStream to it will get encoded. It is important that we 
        // don't add line breaks into the output as this will prevent browsers from displaying
        // the images correctly. This technically breaks the strict encoding rules but is 
        // nevertheless required...
        int totalBytes = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        Base64.OutputStream base64Out = new Base64.OutputStream(baos, Base64.DONT_BREAK_LINES | Base64.ENCODE);
        try
        {
            int byteCount;
            final byte[] buffer = streamBuffer.get();
            while ((byteCount = in.read(buffer)) != -1)
            {
                totalBytes += byteCount;
                base64Out.write(buffer, 0, byteCount);
            }
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException ex) {}
            try
            {
                base64Out.close();
            }
            catch (IOException ex) {}
        }
        
        if (totalBytes > this.getMaximumImageSize())
        {
            // No action required - encodedImage will stay as null...
        }
        else
        {
            encodedImage = baos.toString();
        }
        
        return encodedImage;
    }
    
    /**
     * <p>Thread local stream byte buffer.</p> 
     */
    protected ThreadLocal<byte[]> streamBuffer = new ThreadLocal<byte[]>()
    {
        @Override
        protected byte[] initialValue()
        {
            return new byte[16384];
        }
    };
}
