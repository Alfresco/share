/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * Common Component Filter Chain.
 * 
 * This filter has the purpose to return: 
 *   -a resource path string (e.g. "components/documentlibrary/images/smart-folder-16.png") 
 *    which combined with Alfresco.constants.URL_RESCONTEXT will return an icon resource url; 
 *   -a resource style ( e.g. icon-smart); 
 * based on share-documentlibrary-config.xml configuration.
 * 
 * An example of component configuration: 
 * <component-style> 
 *              {
 *                  "browse":{
 *                      "folder":[
 *                          {
 *                              "filter":{
 *                                  "name":"aspect",
 *                                  "match":[
 *                                      "smf:smartFolder"
 *                                  ]
 *                              },
 *                              "style":{
 *                                  "css":"icon-smart",
 *                                  "icons":{
 *                                      "16x16":{
 *                                          "icon":"components/documentlibrary/images/smart-folder-16.png"
 *                                      },
 *                                      "32x32":{
 *                                          "icon":"components/documentlibrary/images/smart-folder-32.png"
 *                                      },
 *                                      "48x48":{
 *                                          "icon":"components/documentlibrary/images/smart-folder-48.png"
 *                                      },
 *                                      "64x64":{
 *                                          "icon":"components/documentlibrary/images/smart-folder-64.png"
 *                                      },
 *                                      "256x256":{
 *                                          "icon":"components/documentlibrary/images/smart-folder-256.png"
 *                                      }
 *                                  }
 *                              }
 *                          }
 *                      ],
 *                      "content":[
 *                          {
 *                              "filter":{
 *                                  "name":"aspect",
 *                                  "match":[
 *                                      "smf:smartFolderChild"
 *                                  ]
 *                              },
 *                              "style":{
 *                                  "css":"icon-smart-file",
 *                                  "icons":{
 *                                      "16x16":{
 *                                          "icon":"components/documentlibrary/images/smart-file-16.png"
 *                                      },
 *                                      "32x32":{
 *                                          "icon":"components/documentlibrary/images/smart-file-32.png"
 *                                      },
 *                                      "48x48":{
 *                                          "icon":"components/documentlibrary/images/smart-file-48.png"
 *                                      },
 *                                      "64x64":{
 *                                          "icon":"components/documentlibrary/images/smart-file-64.png"
 *                                      },
 *                                      "256x256":{
 *                                          "icon":"components/documentlibrary/images/smart-file-256.png"
 *                                      }
 *                                  }
 *                              }
 *                          }
 *                      ]
 *                  }
 *              }
 *     </component-style>
 * 
 * Currently we have only aspect filters implemented since it was needed for Smart Folders Visual indicators (filter.name == "aspect"), 
 * but the implementation offers the possibility to introduce other filters (filter.name == "type" or any other filters). 
 * @namespace Alfresco
 * @author sdinuta
 */
(function()
{
   /**
    * Abstract common component filter constructor.
    * @param node {object}
    * @param commonComponentConfig {object} - common component configuration from share-documentlibrary-config.xml
    */
   Alfresco.CommonComponentFilterChain = function(node, commonComponentConfig)
   {
      this.node = node;
      this.commonComponentConfig = commonComponentConfig;
      return this;
   };

   Alfresco.CommonComponentFilterChain.prototype =
   {
      /**
       * Returns true if filterType is accepted, false otherwise. Currently only aspect filters accepted. 
       * @param filterType
       * @returns {Boolean} - true if filterType is accepted, false otherwise.
       */
      accepted : function CC_FC__accepted(filterType)
      {
         return (filterType == "aspect");
      },
      /**
       * Filter implementation. Currently only aspect filter supported, but other filter types can be added and implemented.
       * @param filter {object}
       * @returns {Boolean} - true if filter matches, false otherwise.
       */
      match : function CC_FC__match(filter)
      {
         if (filter.name == "aspect")
         {
            return this.matchAspect(filter);
         }
         return false;
      },
      /**
       * Aspect filter implementation. Returns true if the node attribute has all the aspects enumerated in filter.match.
       * See sample configuration from the top.
       * @param filter {object}
       * @returns {Boolean} - true if the node attribute has all the aspects enumerated in filter.match, false otherwise.
       */
      matchAspect : function CC_FC__matchAspect(filter)
      {
         var match = true;
         if (filter.match && filter.match.length != null)
         {
            for (var j = 0; j < filter.match.length; j++)
            {
               var aspect = filter.match[j];
               if (!this.node.aspects || this.node.aspects.indexOf(aspect) == -1)
               {
                  match = false;
                  break;
               }
            }
         }
         else
         {
            match = false;
         }
         return match;
      },
      /**
       * Abstract implementation. Implemented in subclasses.
       * @param component {object}
       * @returns
       */
      getResource : function CC_FC__getResource(component)
      {
         return null;
      },
      /**
       * Gets icon resource path string or resource style specified in the style configuration that corresponds
       * with matching filter (see sample configuration from file header comment),
       * or defaultValue parameter if there are no matching filters.
       * @param defaultValue -default icon resource path string or default style 
       * @returns icon resource path string or resource style specified in the style configuration that corresponds
       *    with matching filter, or defaultValue specified if there are no matching filters.
       */
      filterResource : function CC_FC__filterResource(defaultValue)
      {
         var defaultResourceValue = defaultValue;
         if (this.commonComponentConfig && this.commonComponentConfig.length != null)
         {
            for (var i = 0; i < this.commonComponentConfig.length; i++)
            {
               var component = this.commonComponentConfig[i];
               var filter = component.filter;
               if (!this.accepted(filter.name))
               {
                  continue;
               }
               var match = this.match(filter);
               if (match == true)
               {
                  defaultResourceValue = this.getResource(component);
                  break;
               }
            }
         }
         return defaultResourceValue;
      }
   };

   /**
    * CommonComponentIconFilterChain constructor.
    * @param node {object}
    * @param commonComponentConfig {object} - common component configuration from share-documentlibrary-config.xml
    * @param defaultIcon string - default resource icon path string
    * @param iconSize String - (values of form: "16x16", "32x32"..)
    */
   Alfresco.CommonComponentIconFilterChain = function(node, commonComponentConfig, defaultIcon, iconSize)
   {
      Alfresco.CommonComponentIconFilterChain.superclass.constructor.call(this, node, commonComponentConfig);
      this.node = node;
      this.commonComponentConfig = commonComponentConfig;
      this.defaultIcon = defaultIcon;
      this.iconSize = iconSize;
      return this;
   };

   YAHOO.extend(Alfresco.CommonComponentIconFilterChain, Alfresco.CommonComponentFilterChain);

   /**
    * Gets icon resource path string specified in the {style} configuration that corresponds with matching filter 
    * for specified by {iconSize} attribute.
    * See sample configuration from header file comment. As an example for {browse.folder} component configuration 
    * and for {iconSize} "32x32" the result will be "components/documentlibrary/images/smart-folder-32.png" 
    * @param component
    * @returns
    */
   Alfresco.CommonComponentIconFilterChain.prototype.getResource = function CC_I_FC__getResource(component)
   {
      return component.style.icons[this.iconSize].icon;
   };
   /**
    * Gets icon resource path string specified in the {style} configuration that corresponds with matching filter, 
    * of {defaultIcon} if there are no matching filters.
    * @returns
    */
   Alfresco.CommonComponentIconFilterChain.prototype.createIconResourceName = function CC_I_FC__createIconResourceName()
   {
      var iconStr = this.filterResource(this.defaultIcon);
      return iconStr;
   };

   /**
    * CommonComponentStyleFilterChain constructor.
    * @param node {object}
    * @param commonComponentConfig {object} - common component configuration from share-documentlibrary-config.xml
    */
   Alfresco.CommonComponentStyleFilterChain = function(node, commonComponentConfig)
   {
      Alfresco.CommonComponentStyleFilterChain.superclass.constructor.call(this, node, commonComponentConfig);
      this.node = node;
      this.commonComponentConfig = commonComponentConfig;
      return this;
   };
   YAHOO.extend(Alfresco.CommonComponentStyleFilterChain, Alfresco.CommonComponentFilterChain);

   /**
    * Gets resource style specified in the {style} configuration that corresponds with matching filter.
    * See sample configuration from header file comment. As an example for {browse.content} component configuration 
    * the result will be "icon-smart-file".
    * @param component
    * @returns
    */
   Alfresco.CommonComponentStyleFilterChain.prototype.getResource = function CC_S_FC__getResource(component)
   {
      return component.style.css;
   };
   /**
    * Gets resource style specified in the {style} configuration that corresponds with matching filter, or null if there are no matching filters.
    * @returns resource style specified in the {style} configuration that corresponds with matching filter, or null if there are no matching filters.
    */
   Alfresco.CommonComponentStyleFilterChain.prototype.createCustomStyle = function CC_S_FC__createCustomStyle()
   {
      var style = this.filterResource(null);
      return style;
   };
})();