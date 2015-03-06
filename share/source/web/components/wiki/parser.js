/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

/**
 * Wiki markup parser. 
 * Very simple parser that converts a subset of wiki markup to HTML.
 * 
 * @namespace Alfresco
 * @class Alfresco.WikiParser
 */
(function()
{
   /**
    * WikiParser constructor.
    * 
    * @return {Alfresco.WikiParser} The new parser instance
    * @constructor
    */
   Alfresco.WikiParser = function()
   {
      return this;
   };

   Alfresco.WikiParser.prototype =
   {
      /**
       * The url to use when rewriting links.
       * 
       * @property URL
       * @type String
       */
      URL: null,
      
      /**
       * Renders wiki markup.
       *
       * @method parse
       * @param test {String} The text to render
       */
      parse: function WikiParser_parse(text, pages)
      {
         pages = pages == null ? [] : pages;
         text = this._renderLinks(text, pages);
         return text;
      },
      
      /**
       * Looks for instance of [[ ]] in the text and replaces
       * them as appropriate.
       * 
       * @method _renderLinks
       * @private
       * @param s {String} The text to render
       * @param pages {Array} The existing pages on the current site
       */
      _renderLinks: function WikiParser__renderLinks(s, pages)
      {
         if (typeof s == "string")
         {
            var result = s.split("[["), text = s;
         
            if (result.length > 1)
            {
               var re = /^([^\|\]]+)(?:\|([^\]]+))?\]\]/;
               var uri, i, ii, str, matches, page, exists, anchor;
               text = result[0];
            
               for (i = 1, ii = result.length; i < ii; i++)
               {
                  str = result[i];
                  if (re.test(str))
                  {
                     matches = re.exec(str);
                     // ALF-20817
                     anchor = matches[1].split("#");
                     if (anchor[1])
                     {
                        matches[1] = anchor[0];
                     }
                     else if (matches[2])
                     {
                        anchor = matches[2].split("#");
                        if (anchor[1])
                        {
                           matches[2] = anchor[0];
                        }
                     }
					 
                     // Replace " " character in page URL with "_"
                     page = matches[1].replace(/\s+/g, "_");
                     exists = Alfresco.util.arrayContains(pages, page);
                     uri = '<a href="' + this.URL + encodeURIComponent(Alfresco.util.decodeHTML(anchor[1] ? page + "#" + anchor[1] : page)) + '" class="' + (exists ? 'theme-color-1' : 'wiki-missing-page') + '">';
                     uri += (matches[2] ? matches[2] : anchor[1] ? matches[1] + "#" + anchor[1] : matches[1]);
                     uri += '</a>';
                     
                     text += uri.replace("%23", "#");
                     text += str.substring(matches[0].length);
                  }
               }
            }   
            return text;
         }
         return s;
      }
   };
})();