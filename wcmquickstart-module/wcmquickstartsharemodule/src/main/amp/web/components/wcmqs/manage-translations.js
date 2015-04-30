/**
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
 
/**
 * Manage Translations component.
 * 
 * @namespace Alfresco
 * @class Alfresco.component.ManageTranslations
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * ManageTranslations constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.ManageTranslations} The new ManageTranslations instance
    * @constructor
    */
   Alfresco.component.ManageTranslations = function(htmlId)
   {
      return Alfresco.component.ManageTranslations.superclass.constructor.call(this, "Alfresco.component.ManageTranslations", htmlId);
   };

   /**
    * Mark as translations function.
    * 
    * @param {String} NodeRef of the node to be marked for translation
    * @param {String} locale Locale to mark translation as
    * @return {boolean} False to prevent further click processing
    * @static
    */
   Alfresco.component.ManageTranslations.markAsTranslation = function MT_markAsTranslation(nodeRef, locale)
   {
      Alfresco.util.Ajax.jsonRequest(
      {
         method: "post",
         url: Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.replace(":/", "") + "/formprocessor",
         dataObj:
         {
            "prop_ws_language": locale
         },
         successCallback:
         {
            fn: function()
            {
               window.location.reload();
            }
         }
      });

      return false;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.ManageTranslations, Alfresco.component.Base,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         var myColumnDefs =
         [
            { key: "lang", label: this.msg("label.language"), sortable: true },
            { key: "name", label: this.msg("label.name"), sortable: true },
            { key: "action", label: this.msg("label.action"), sortable: false }
         ];

         this.widgets.dataSource = new YAHOO.util.DataSource(YAHOO.util.Dom.get(this.id + "-languages"),
         {
            responseType: YAHOO.util.DataSource.TYPE_HTMLTABLE,
            responseSchema:
            {
               fields:
               [
                  { key: "lang" },
                  { key: "name" },
                  { key: "action" }
               ]
            }
         });

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-markup", myColumnDefs, this.widgets.dataSource);
      }
   }, true);
})();