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
 * RulesNone component.
 * When a folder has no rules this component lets the user create new rules
 * or link the folder to another folders rule set.
 *
 * @namespace Alfresco
 * @class Alfresco.RulesNone
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * FolderPath constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RulesNone} The new FolderPath instance
    * @constructor
    */
   Alfresco.RulesNone = function(htmlId)
   {
      Alfresco.RulesNone.superclass.constructor.call(this, "Alfresco.RulesNone", htmlId, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderRulesetDetailsAvailable", this.onFolderRulesetDetailsAvailable, this);

      return this;
   };

   YAHOO.extend(Alfresco.RulesNone, Alfresco.component.Base,
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
          * nodeRef of folder being viewed
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: null,

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RulesNone_onReady()
      {
         Event.addListener(this.id + "-linkToRuleSet", "click", this.onLinkToRuleSetClick, this, this);
      },

      /**
       * Event handler called when the "folderRulesetDetailsAvailable" event is received
       *
       * @method onFolderRulesetDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesetDetailsAvailable: function RulesLinked_onFolderRulesetDetailsAvailable(layer, args)
      {
         var ruleset = args[1].folderRulesetDetails;
         if (ruleset.inheritedRules && ruleset.inheritedRules.length > 0)
         {
            Dom.removeClass(this.id + "-inheritedRules", "hidden");  
         }
      },

      /**
       * Called when user clicks on the link to rules set link.
       *
       * @method onLinkToRuleSetClick
       * @param event
       * @param obj
       */
      onLinkToRuleSetClick: function RulesNone_onLinkToRuleSetClick(event, obj)
      {
         if (!this.modules.rulesPicker)
         {
            this.modules.rulesPicker = new Alfresco.module.RulesPicker(this.id + "-rulesPicker");
         }

         var allowedViewModes =
         [
            Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE
         ];
         if (this.options.repositoryBrowsing === true)
         {
            allowedViewModes.push(Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY, 
                                  Alfresco.module.DoclibGlobalFolder.VIEW_MODE_USERHOME, 
                                  Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SHARED);
         }
         
         this.modules.rulesPicker.setOptions(
         {
            mode: Alfresco.module.RulesPicker.MODE_LINK_TO,
            siteId: this.options.siteId,
            allowedViewModes: allowedViewModes,
            files:
            {
               displayName: this.folderDetails,
               nodeRef: this.options.nodeRef.toString()
            }
         }).showDialog();

         // Stop click event
         Event.stopEvent(event);
      }
   });
})();
