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
 * FolderRules template.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderRules
 */
(function()
{

   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * FolderRules constructor.
    * 
    * @return {Alfresco.FolderRules} The new FolderRules instance
    * @constructor
    */
   Alfresco.FolderRules = function FolderRules_constructor()
   {
      Alfresco.FolderRules.superclass.constructor.call(this, "Alfresco.FolderRules", null, ["button"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("rulesCopiedFrom", this.onRulesCopiedFrom, this);
      YAHOO.Bubbling.on("rulesLinkedTo", this.onRulesLinkedTo, this);
      YAHOO.Bubbling.on("folderRulesetDetailsAvailable", this.onFolderRulesetDetailsAvailable, this);
      YAHOO.Bubbling.on("folderRulesDetailsChanged", this.onFolderRulesDetailsChanged, this);
      YAHOO.Bubbling.on("inheritChange", this.onInheritChange, this);

      return this;
   };
   
   YAHOO.extend(Alfresco.FolderRules, Alfresco.component.Base,
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
          * @type Alfresco.util.NodeRef
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
          * Current folder name.
          *
          * @property folderName
          * @type string
          */
         folderName: "",

         /**
          * Path to current folder.
          *
          * @property pathToFolder
          * @type string
          */
         pathToFolder: "",

         /**
          * Local and inherited rules  and info about rules that link to this folder or are linked form this folder.
          *
          * @property ruleset
          * @type Array
          */
         ruleset: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @override
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function FolderRules_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function FolderRules_onReady()
      {
         // Save references to Dom elements
         this.widgets.inheritedRulesList = Dom.get("inherited-rules-container");

         // Fire event to inform any listening components that the data is ready
         YAHOO.Bubbling.fire("folderDetailsAvailable",
         {
            folderDetails:
            {
               nodeRef: this.options.nodeRef.toString(),
               location:
               {
                  path: this.options.folder.path,
                  file: this.options.folder.name
               },
               fileName: this.options.folder.name,
               type: "folder"
            }
         });

         if (this.options.linkedToFolder)
         {
            // Fire event to inform any listening components that the data is ready
            YAHOO.Bubbling.fire("linkedToFolderDetailsAvailable",
            {
               linkedToFolder: this.options.linkedToFolder
            });
         }

         // Fire event to inform any listening components that the data is ready
         this._fireFolderRulesetDetailsAvailable();

         // Display inherited rules
         this._displayInheritedRules();
      },

      /**
       * Event handler called when the "onRulesCopiedFrom" event is received
       *
       * @method onRulesCopiedFrom
       * @param layer
       * @param args
       */
      onRulesCopiedFrom: function RulesHeader_onRulesCopiedFrom(layer, args)
      {
         // Refresh page since new components needs to be rendered
         document.location.reload();
      },

      /**
       * Event handler called when the "onRulesLinkedTo" event is received
       *
       * @method onRulesLinkedTo
       * @param layer
       * @param args
       */
      onRulesLinkedTo: function RulesHeader_onRulesLinkedTo(layer, args)
      {
         // Refresh page since new components needs to be rendered
         document.location.reload();
      },
      
      /**
       * Event handler called when the "folderRulesetDetailsAvailable" event is received
       *
       * @method onfolderRulesetDetailsAvailable
       * @param layer
       * @param args
       */
      onFolderRulesetDetailsAvailable: function RulesHeader_onFolderRulesetDetailsAvailable(layer, args)
      {
         var folderRulesetData = args[1].folderRulesetDetails;

         if(this.options.ruleset.linkedToRuleSet != folderRulesetData.linkedToRuleSet ||
            !this.options.ruleset.rules && folderRulesetData.rules ||
            this.options.ruleset.rules && !folderRulesetData.rules)
         {
            // Refresh page since new components needs to be rendered
            document.location.reload();
         }
      },


      /**
       * Checks if any rules are inherited and then hides or show them
       *
       * @method _displayInheritedRules
       */
      _displayInheritedRules: function RulesHeader__displayInheritedRules(layer, args)
      {
         // Check if there are inherited rules
         var inheritedRules = this.options.ruleset ? this.options.ruleset.inheritedRules : null;
         if (inheritedRules && inheritedRules.length > 0)
         {
            // Found an inherited rule make sure the component is displayed
            Dom.removeClass(this.widgets.inheritedRulesList, "hidden");
         }
         else
         {
            // Found no inherited rules make sure the component is hidden
            Dom.addClass(this.widgets.inheritedRulesList, "hidden");            
         }
      },

      /**
       *
       * Tiggered when the Rules Header component shows a change in
       *
       * @method onInheritChange
       */
      onInheritChange: function RulesHeader_onInheritChange(layer, args)
      {
         // Reload page so appropriate components will be displayed in stead of the current ones
         window.location.reload();
      },

      /**
       * Event called when another component has changed the details of a rule on a folder
       * which requires ui to reload itself.
       *
       * @method onFolderRulesDetailsChanged
       * @param layer
       * @param args
       * @private
       */
      onFolderRulesDetailsChanged: function RulesHeader_onFolderRulesDetailsChanged(layer, args)
      {
         // Load rule information form server
         var prevNoOfRules = this.options.ruleset && this.options.ruleset.rules ? this.options.ruleset.rules.length : 0;
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI_RELATIVE + "api/node/" + this.options.nodeRef.uri + "/ruleset",
            successCallback:
            {
               fn: function(response, p_prevNoOfRules)
               {
                  if (response.json)
                  {
                     this.options.ruleset = response.json.data;
                     this._fireFolderRulesetDetailsAvailable();
                     var newNoOfRules = this.options.ruleset && this.options.ruleset.rules ? this.options.ruleset.rules.length : 0;
                     if ((newNoOfRules == 0 && p_prevNoOfRules != 0) ||
                         (newNoOfRules != 0 && p_prevNoOfRules == 0))
                     {
                        // Reload page so appropriate components will be displayed in stead of the current ones
                        window.location.reload();
                     }
                  }
               },
               obj: prevNoOfRules,
               scope: this
            },
            failureMessage:this.msg("message.getRuleFailure", this.name)
         });
      },

      /**
       * @method _fireFolderRulesetDetailsAvailable
       * @private
       */
      _fireFolderRulesetDetailsAvailable: function RulesHeader__fireFolderRulesetDetailsAvailable()
      {
         YAHOO.Bubbling.fire("folderRulesetDetailsAvailable",
         {
            folderRulesetDetails: this.options.ruleset
         });
      }

   });
})();
