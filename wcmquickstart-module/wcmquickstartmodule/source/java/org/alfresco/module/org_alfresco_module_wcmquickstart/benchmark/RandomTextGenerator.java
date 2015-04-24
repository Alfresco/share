/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;


/**
 * 
 * @author Nick Smith
 * @since 4.0
 *
 */
public class RandomTextGenerator implements TextGenerator, InitializingBean
{
    /** Log */
    private static Log logger = LogFactory.getLog(TextGenerator.class);

    public static final String FILTER_NON_STANDARD_REGEX = "[^\\da-zA-Z\\s]";
    
    public final static int DEFAULT_LINE_WIDTH = 65;
    public final static int DEFAULT_TOTAL_CHARACTERS = 300;
    public final static int DEFAULT_NAME_SIZE = 15;

    private int prefixLength = 10;
    private String sourceTextLocation;

    private MarkovChain chain1;
    private MarkovChain chain2;

    public int generateHTMLFile(String outputFile, int minParagraphs, int maxParagraphs, int minParagraphLength, int maxParagraphLength, int lineWidth)
    {
        int totalLength = 0;
        int numberOfParagraphs = randomNumber (minParagraphs, maxParagraphs);
        try
        {
            File file =new File(outputFile);

            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file,false);
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n".getBytes());
            out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n".getBytes());
            out.write("<head>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n".getBytes());
            out.write(("<title>"+generateSentence(30,true)+"</title></head><body>\n").getBytes());
            for (int paragraph = 0; paragraph < numberOfParagraphs; paragraph++)
            {
                int paragraphLength = randomNumber (minParagraphLength, maxParagraphLength);
                String paraText = generateText (paragraphLength, lineWidth, false)+"\n";
                totalLength += paraText.length();
                out.write(("<p>"+paraText+"</p>\n").getBytes());
            }
            out.write("</body>\n</html>".getBytes());
            out.close();

            return totalLength;
        } 
        catch (FileNotFoundException e)
        {
            logger.error("Failed to generate HTML file",e);
            return 0;
        } 
        catch (IOException e)
        {
            logger.error("Failed to generate HTML file",e);
            return 0;
        }
    }

    /**
     * @param minParagraphs
     * @param maxParagraphs
     * @param minParagraphLength
     * @param maxParagraphLength
     * @param lineWidth
     * @return
     */
    public String generateHTMLString(int minParagraphs, int maxParagraphs, int minParagraphLength, int maxParagraphLength, int lineWidth)
    {
        int numberOfParagraphs = randomNumber (minParagraphs, maxParagraphs);
        StringBuffer sb = new StringBuffer();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n");
        sb.append("<head>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />\n");
        sb.append(("<title>"+generateSentence(30,true)+"</title></head><body>\n"));
        for (int paragraph = 0; paragraph < numberOfParagraphs; paragraph++)
        {
            int paragraphLength = randomNumber (minParagraphLength, maxParagraphLength);
            sb.append("<p>");
            sb.append(generateText (paragraphLength, lineWidth, false)+"\n");
            sb.append("</p>\n");
        }
        sb.append("</body>\n</html>");
        return sb.toString();      
    }    

    /**
     * @param minParagraphs
     * @param maxParagraphs
     * @param minParagraphLength
     * @param maxParagraphLength
     * @param lineWidth
     * @return
     */
    public String generateTextString(int minParagraphs, int maxParagraphs, int minParagraphLength, int maxParagraphLength, int lineWidth)
    {
        int numberOfParagraphs = randomNumber (minParagraphs, maxParagraphs);
        StringBuffer sb = new StringBuffer();

        for (int paragraph = 0; paragraph < numberOfParagraphs; paragraph++)
        {
            int paragraphLength = randomNumber (minParagraphLength, maxParagraphLength);
            sb.append(generateText (paragraphLength, lineWidth, false)+"\n\n");
        }
        return sb.toString();      
    }  
    
    /**
     * @param outputFile
     * @param minParagraphs
     * @param maxParagraphs
     * @param minParagraphLength
     * @param maxParagraphLength
     * @param lineWidth
     * @return
     */
    public int generateTextFile(String outputFile, int minParagraphs, int maxParagraphs, int minParagraphLength, int maxParagraphLength, int lineWidth)
    {
        int totalLength = 0;
        int numberOfParagraphs = randomNumber (minParagraphs, maxParagraphs);
        try
        {
            File file =new File(outputFile);

            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file,false);
            for (int paragraph = 0; paragraph < numberOfParagraphs; paragraph++)
            {
                int paragraphLength = randomNumber (minParagraphLength, maxParagraphLength);
                String paraText = generateText (paragraphLength, lineWidth, false)+"\n";
                totalLength += paraText.length();
                out.write(paraText.getBytes());
            }
            out.close();

            return totalLength;
        } 
        catch (FileNotFoundException e)
        {
            logger.error("Failed to generate text file",e);
            return 0;
        } 
        catch (IOException e)
        {
            logger.error("Failed to generate text file",e);
            return 0;
        }
    }

    /**
     * @param min
     * @param max
     * @return
     */
    private int randomNumber (int min, int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    /**
     * @return
     */
    public String generateSentence ()
    {
        return generateSentence(DEFAULT_LINE_WIDTH,true);
    }

    /**
     * @param totalCharacters
     * @return
     */
    public String generateSentence (int totalCharacters,boolean enforceFullWord)
    {
        return generateText (totalCharacters,totalCharacters,enforceFullWord).trim();
    } 

    public String generateName ()
    {
        return generateName(DEFAULT_NAME_SIZE,true);
    }

    /**
     * @param totalCharacters
     * @return
     */
    public String generateName (int totalCharacters,boolean enforceFullWord)
    {
        String text = generateText (totalCharacters,totalCharacters,enforceFullWord);
        text = filterNonStandardChars(text).trim();
        return flattenWhitespace(text);
    } 

    private String filterNonStandardChars(String s)
    {
        return s.replaceAll(FILTER_NON_STANDARD_REGEX,"");
    }

    private String flattenWhitespace(String s)
    {
        return s.replaceAll(" +", " ");
    }
    
    /**
     * @return
     */
    public String generateText ()
    {
        return generateText (DEFAULT_TOTAL_CHARACTERS,DEFAULT_LINE_WIDTH,true);
    }

    /**
     * @param totalCharacters
     * @param lineWidth
     * @return
     */
    public String generateText (int totalCharacters, int lineWidth, boolean enforceFullWord)
    {
        StringBuffer sb = new StringBuffer();

        Random random = new Random();
        CharQueue queue = new CharQueue(prefixLength);
        float weight = 0;

        queue.set(chain1.getBootstrapPrefix());
        sb.append(queue.toString());
        int width = 0;
        int c;

        do {
            String prefix = queue.toString();

            // get a character from each chain
            c = chain1.get(prefix, random);
            int c2 = -1;
            if (chain2 != null) {
                c2 = chain2.get(prefix, random);
            }
            if (c == -1 && c2 == -1) {
                break;
            }

            // choose one if we can
            if (chain2 != null) {
                if (c == -1) {
                    c = c2;
                } else if (c2 != -1 && random.nextFloat() < weight) {
                    c = c2;
                }
            }
            sb.append((char)c);
            queue.put((char)c);
            width++;

            // line wrap
            if (c == ' ' && width > lineWidth) {
                sb.append("\n");
                width = 0;
            }

            // go towards second markov chain
            weight += 1.0/totalCharacters;
        } while (weight < 1 || (/*c != '.'*/Character.isLetter(c) && enforceFullWord));

        sb.append("\n");

        String generatedText = sb.toString();

        return generatedText.substring(prefixLength+1,prefixLength+2).toUpperCase()+generatedText.substring(prefixLength+2);
    }

    private MarkovChain newMarkovChain() 
    {
        try
        {
            InputStream input = new ClassPathResource(sourceTextLocation).getInputStream();
            if(input == null)
            {
                throw new IllegalArgumentException("Source Text not found: " +sourceTextLocation);
            }
            return new MarkovChain(input, this.prefixLength);
        }
        catch(FileNotFoundException fnfe)
        {
            throw new AlfrescoRuntimeException("File could not be found: "+sourceTextLocation, fnfe);
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.chain1 = newMarkovChain();
        this.chain2 = newMarkovChain();
    }
    
    /**
     * @param prefixLength the prefixLength to set
     */
    public void setPrefixLength(int prefixLength)
    {
        this.prefixLength = prefixLength;
    }
    
    /**
     * @param sourceTextLocation the sourceTextLocation to set
     */
    public void setSourceTextLocation(String sourceTextLocation)
    {
        this.sourceTextLocation = sourceTextLocation;
    }
}
