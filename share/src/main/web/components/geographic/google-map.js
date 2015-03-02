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
 * Google Map component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.GoogleMap
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * GoogleMap constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.GoogleMap} The new GoogleMap instance
    * @constructor
    */
   Alfresco.component.GoogleMap = function(htmlId)
   {
      return Alfresco.component.GoogleMap.superclass.constructor.call(this, "Alfresco.component.GoogleMap", htmlId);
   };

   YAHOO.extend(Alfresco.component.GoogleMap, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * JSON representation of document details
          *
          * @property documentDetails
          * @type object
          */
         documentDetails: null
      },

      /**
       * The data for the document
       * 
       * @property recordData
       * @type object
       */
      recordData: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function GoogleMap_onReady()
      {
         var h = Dom.getXY("alf-ft")[1] - Dom.getXY(this.id + "-map")[1] - 50;
         Dom.setStyle(this.id + "-map", "height", h + "px");

         // Asset data 
         this.recordData = this.options.documentDetails.item;
         this.recordData.jsNode = new Alfresco.util.Node(this.recordData.node);

         // Async load the Google Maps API. Need to do this, as it breaks the YUI Loader otherwise
         var script = document.createElement("script");
         script.type = "text/javascript";
         script.src = window.location.protocol + "//maps.google.com/maps/api/js?sensor=false&callback=Alfresco.component.GoogleMap.Callback";
         document.body.appendChild(script);
      },

      /**
       * Event handler called when the Google Maps API has loaded
       *
       * @method onGoogleAPIReady
       */
      onGoogleAPIReady: function GoogleMap_onGoogleAPIReady()
      {
         // Get node's geo location info
         var properties = this.recordData.node.properties,
            latLong = new google.maps.LatLng(properties["cm:latitude"], properties["cm:longitude"]),
            mapOptions =
            {
               zoom: 16,
               center: latLong,
               mapTypeId: google.maps.MapTypeId.HYBRID
            };

         // Generate map centered on geo location
         var map = new google.maps.Map(Dom.get(this.id + "-map"), mapOptions),
            marker = new google.maps.Marker(
            {
               position: latLong,
               map: map,
               title: this.recordData.displayName
            });

         // Generate info window, showing EXIF panel if relevant
         var infoWindow = new google.maps.InfoWindow(
         {
            content: Dom.get(this.id + "-info")
         });
         google.maps.event.addListener(marker, "click", function()
         {
            infoWindow.open(map, marker);
         });

         if (this.recordData.jsNode.hasAspect("exif:exif"))
         {
            Dom.removeClass(this.id + "-exif", "hidden");

            // Pan the map a little to allow room for the EXIF data
            map.panBy(0, -200);
         }

         // Auto-open the info window
         infoWindow.open(map, marker);
      }
   });
})();

Alfresco.component.GoogleMap.Callback = function()
{
   var googleMap = Alfresco.util.ComponentManager.findFirst("Alfresco.component.GoogleMap");
   if (googleMap)
   {
      googleMap.onGoogleAPIReady();
   }
}