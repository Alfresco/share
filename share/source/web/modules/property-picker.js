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
 * Property Picker.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.PropertyPicker
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   Alfresco.module.PropertyPicker = function(htmlId)
   {
      // Call super class constructor
      Alfresco.module.PropertyPicker.superclass.constructor.call(this, htmlId);

      // Merge options
      this.options = YAHOO.lang.merge(this.options, Alfresco.util.deepCopy(Alfresco.module.PropertyPicker.superclass.options,
      {
         copyFunctions: true
      }));

      // Override options to add an extra "Id" column
      this.options.dataTableColumnDefinitions.push(
      {
         key: "id",
         sortable: true
      });      

      // Re-register with our own name
      this.name = "Alfresco.module.PropertyPicker";
      Alfresco.util.ComponentManager.reregister(this);
      return this;
   };

   YAHOO.extend(Alfresco.module.PropertyPicker, Alfresco.module.DataPicker,
   {

      /**
       * Object container for initialization options
       */
      options:
      {

         /**
          * The extra template to get transient properties and i18n messages
          *
          * @property propertyPickerTemplateUrl
          * @type string
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/property-picker"
          */
         propertyPickerTemplateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/property-picker",

         /**
          * The transient properties to add to "special" properties.
          *
          * @property transientProperties
          * @type object
          * @default {}
          */
         transientProperties: {},

         /**
          * The classes (aspects & types) that are configured to be visible.
          *
          * @property classFilter
          * @type object
          * @default {}
          */
         classFilter: {
            aspects:
            {
               visible: []
            },
            types:
            {
               visible: []
            }
         },

         /**
          * Default property tab configuration.
          * Other tabs may be added by overriding setOptions() and adding a new tab like "properties"
          * to the tabs option.
          *
          * @property tabs
          * @type object
          * @default A single tab with all, standard, aspect & type properties
          */
         tabs: [
            {
               id: "properties",
               treeNodes: [
                  {
                     id: "all",
                     listItems:
                     {
                        url: "{url.proxy}api/properties",
                        dataModifier: function (listItemObjs, descriptorObj)
                        {
                           return this._addTransientProperties(listItemObjs);
                        },
                        id: "{item.name}",
                        type: "{item.dataType}",
                        label: "{item.title}",
                        title: "{item.name}"
                     }
                  },
                  {
                     id: "aspects",
                     treeNodes:
                     {
                        url: "{url.proxy}api/classes?cf=aspect",
                        id: "{node.name}",
                        title: "{node.name}",
                        label: "{node.title}",
                        listItems:
                        {
                           url: "{url.proxy}api/classes/{node.name}/properties",
                           dataModifier: function (listItemObjs, descriptorObj)
                           {
                              return this._addTransientProperties(listItemObjs);
                           },
                           id: "{item.name}",
                           type: "{item.dataType}",
                           label: "{item.title}",
                           title: "{item.name}"
                        }
                     }
                  },
                  {
                     id: "types",
                     treeNodes:
                     {
                        url: "{url.proxy}api/classes?cf=type",
                        id: "{node.name}",
                        title: "{node.name}",
                        label: "{node.title}",
                        listItems:
                        {
                           url: "{url.proxy}api/classes/{node.name}/properties",
                           dataModifier: function (listItemObjs, descriptorObj)
                           {
                              return this._addTransientProperties(listItemObjs);
                           },
                           id: "{item.name}",
                           type: "{item.dataType}",
                           label: "{item.title}",
                           title: "{item.name}"
                        }
                     }
                  }
               ],
               listItems: []
            }
         ]
      },

      /**
       * Event callback when superclass' dialog template has been loaded.
       *
       * @method onTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function RPP_onTemplateLoaded(response)
      {
         // Load the UI template, which will bring in additional i18n-messages from the server
         Alfresco.util.Ajax.request(
         {
            url: this.options.propertyPickerTemplateUrl,
            dataObj:
            {
               htmlid: this.id
            },
            successCallback:
            {
               fn: this.onPropertyPickerTemplateLoaded,
               obj: response,
               scope: this
            },
            failureMessage: this.msg("message.load.template.error", this.options.propertyPickerTemplateUrl),
            execScripts: true
         });
      },

      /**
       * Event callback when this class' template has been loaded
       *
       * @method onPropertyPickerTemplateLoaded
       * @override
       * @param response {object} Server response from load template XHR request
       */
      onPropertyPickerTemplateLoaded: function PP_onPropertyPickerTemplateLoaded(response, superClassResponse)
      {
         // Inject the template from the XHR request into a new DIV element and insert it when showDialog() is called
         var tmpEl = document.createElement("div");
         tmpEl.setAttribute("style", "display:none");
         tmpEl.innerHTML = response.serverResponse.responseText;         

         // Let the original template get rendered.
         Alfresco.module.PropertyPicker.superclass.onTemplateLoaded.call(this, superClassResponse);

      },

      /**
       * Makes sure only classes (aspects & types) that has been configured to be visible in share-config.xml
       * are displayed.
       *
       * @method _filterClasses
       * @param treeNodeObjs {array}
       * @param classFilter {array}
       * @return listItemObjs but with transient properties added if a property of type "d:content" was present
       */
      _filterClasses: function PP__filterClasses(treeNodeObjs, classFilter)
      {
         var filteredClasses = [];
         for (var i = 0, il = treeNodeObjs.length; i < il; i++)
         {
            if (Alfresco.util.arrayContains(classFilter, treeNodeObjs[i].name))
            {
               filteredClasses.push(treeNodeObjs[i]);
            }
         }
         return filteredClasses;
      },

      /**
       * Adds transient properties to listItemObjs if a property of type "d:content" was present.
       *
       * @method addTransientProperties
       * @param listItemObjs
       * @param descriptorObj
       * @return listItemObjs but with transient properties added if a property of type "d:content" was present
       */
      _addTransientProperties: function PP__addTransientProperties(listItemObjs, descriptorObj)
      {
         var foundTransientProperties = [],
            tps = this.options.transientProperties,
            addedTransientProperties = 0;
         for (var i = 0, il = listItemObjs.length; i < il; i++)
         {
            for (var name in tps)
            {
               if (tps.hasOwnProperty(name) && listItemObjs[i].dataType == name)
               {
                  foundTransientProperties.push({ type: name, index: i, properties: tps[name] });
               }
            }
         }

         for (i = 0, il = foundTransientProperties.length; i < il; i++)
         {
            var transientProperty = foundTransientProperties[i],
               tpil = transientProperty.properties.length,
               insertIndex = foundTransientProperties[i].index + addedTransientProperties,
               property = Alfresco.util.deepCopy(listItemObjs[insertIndex]),
               title = property.title ? property.title : property.name,
               newProperty,
               tpi;
            for (tpi = 0; tpi < tpil; tpi++)
            {
               listItemObjs.splice(insertIndex + tpi, 0, Alfresco.util.deepCopy(property));
            }
            for (tpi = 0; tpi < tpil; tpi++)
            {
               newProperty = listItemObjs[insertIndex + tpi + 1];
               newProperty.title = this.msg("label.transientProperty", transientProperty.properties[tpi].displayLabel, property.name);
               newProperty.name = property.name + ":" + transientProperty.properties[tpi].value;
            }
            addedTransientProperties += tpil;
         }
         return listItemObjs;
      },

      /**
       * Gets a message from this class or the superclass
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function PP_msg(messageId)
      {
         var result = Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1))
         if (result == messageId)
         {
            result = Alfresco.util.message(messageId, "Alfresco.module.DataPicker", Array.prototype.slice.call(arguments).slice(1));
         }
         return result;
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       * @protected
       */
      _showDialog: function PP__showDialog()
      {
         // Add class so we can override styles in css
         Dom.addClass(this.widgets.dialog.body.parentNode, "property-picker");

         // Show dialog as usual
         Alfresco.module.PropertyPicker.superclass._showDialog.call(this);
      }

   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.PropertyPicker("null");
})();

