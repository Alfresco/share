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
 * AddedUsersList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AddedUsersList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * AddedUsersList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AddedUsersList} The new AddedUsersList instance
    * @constructor
    */
    Alfresco.AddedUsersList = function(htmlId)
    {
      return Alfresco.AddedUsersList.superclass.constructor.call(this, "Alfresco.AddedUsersList", htmlId);
    };

    YAHOO.extend(Alfresco.AddedUsersList, Alfresco.component.Base,
    {
       /**
        * Fired by YUI when parent element is available for scripting.
        * Component initialisation, including instantiation of YUI widgets and event listener binding.
        *
        * @method onReady
        */
       onReady: function AddedUsersList_onReady()
       {
          var parentDiv = Dom.getElementsByClassName("added-users-list-bar", "div", "bd")[0];
          var sinviteDiv = Dom.getElementsByClassName("sinvite", "div", "bd")[0];
          var inviteButton = sinviteDiv.getElementsByTagName("button")[0];
          inviteButton.innerHTML = this.msg("added-users-list.add-button-text");
          parentDiv.appendChild(sinviteDiv.firstElementChild);
       }
   });
})();
