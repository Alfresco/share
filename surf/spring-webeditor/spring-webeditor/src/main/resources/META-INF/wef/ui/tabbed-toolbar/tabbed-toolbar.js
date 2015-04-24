/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

/**
 * @module WEF.ui 
 */
(function() 
{
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener,
       Selector = YAHOO.util.Selector,
       Element = YAHOO.util.Element,
       Bubbling = YAHOO.Bubbling,
       Cookie = YAHOO.util.Cookie,
       WebEditor = YAHOO.org.springframework.extensions.webeditor;

   YAHOO.namespace('org.springframework.extensions.webeditor.ui.tabbed-toolbar');

   /**
    * Tabbed Toolbar component constructor
    * @constructor
    * @class TabbedToolbar
    * @namespace WEF.ui
    * @extends WEF.Widget
    */
   WebEditor.ui.TabbedToolbar = function YAHOO_org_springframework_extensions_webeditor_ui_TabbedToolbar_constructor(config)
   {
      WebEditor.ui.TabbedToolbar.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };

   YAHOO.extend(WebEditor.ui.TabbedToolbar, WEF.Widget,
   {
      init: function WEF_UI_TabbedToolbar_init()
      {
         WebEditor.ui.TabbedToolbar.superclass.init.apply(this);

         this.initAttributes(this.config);
         var d = document.createElement('div');
         d.className = 'wef-tab-toolbar';
         this.element.get('element').appendChild(d);
         this.element = new Element(d);
         //create tabview
         this.widgets.tabview = new YAHOO.widget.TabView();
         
         this.widgets.tabview.appendTo(this.element);
         this.widgets.tabview.on('activeTabChange', this.onTabChange ,this, true);
         
         this.widgets.toolbars = [];

         if (this.config.toolbars)
         {
            for (var i = 0, len = this.config.toolbars.length; i < len; i++)
            {
               var tbConfig = this.config.toolbars[i];
               this.addToolbar(tbConfig.id, tbConfig);
            }
         }
      },

      render: function WEF_UI_TabbedToolbar_render()
      {
         var toolbars = this.widgets.toolbars;
         for (var i = 0, len = this.widgets.toolbars.length; i < len ;i++)
         {
            toolbars[i].init();
         }
      },
      
      /**
       * Adds a (tab) toolbar.
       * 
       * @param id {String} Id of toolbar
       * @param config {Object} Config of toolbar 
       */
      addToolbar: function WEF_UI_TabbedToolbar_addToolbar(id, config)
      {
         var toolbar, 
             tab  = new YAHOO.widget.Tab(
             {
                label: config.label,
                content: config.content || "",
                active: config.active  || false
             });
            
         tab.pluginOwner = config.pluginOwner || null;
         this.widgets.tabview.addTab(tab);
         
         var toolbarConfig = 
         {
            id:config.id+'-toolbar',
            name:config.id+'-toolbar',
            title:config.title,
            element: tab.get('contentEl'),
            buttons: { buttons: config.buttons || []}
         };

         
         toolbar = new WebEditor.ui.Toolbar(toolbarConfig);
         this.widgets.toolbars.push(toolbar);
         this.widgets.toolbars[id] = toolbar;
         if (this.widgets.toolbars.length>0)
         {
            this.widgets.tabview.set('activeTab',this.widgets.tabview.getTab(0));
            this.show();
         }
         return toolbar;
      },
      
      /**
       * Adds one or more buttons to specified id.
       * 
       * @param toolbarId {String} Id of toolbar
       * @param buttonConfig {Object} Config of button
       */
      addButtons : function WEF_UI_TabbedToolbar_addButtons(toolbarId, buttonConfig)
      {
         var toolbars  = this.widgets.toolbars;
         if (toolbars[toolbarId])
         {
            toolbars[toolbarId].addButtons(buttonConfig);
         }
      },

      /**
       * Retrieves a reference to the  toolbar with the given id
       * @param toolbarId {String} Id of existing toolbar
       * 
       * @return {WEF.ui.Toolbar} Reference to the specified toolbar 
       */
      getToolbar: function WEF_UI_TabbedToolbar_getToolbar(toolbarId)
      {
         return this.widgets.toolbars[toolbarId];
      },
      
      /**
       * Fires a *namespaced* bubbling event when a different tab has been clicked
       * @e tabChange 
       */
      onTabChange: function WEF_UI_TabbedToolbar_onTabChange(e)
      {
         Bubbling.fire(this.config.name + WEF.SEPARATOR + 'tabChange', e);
      },
      
      /**
       * Set orientation of tab component
       *  
       */
      setOrientation: function WEF_UI_TabbedToolbar_setOrientation(orientation)
      {
         this.widgets.tabview.set('orientation', orientation);
      }
   });
})();

WEF.register("org.springframework.extensions.webeditor.ui.tabbed-toolbar", YAHOO.org.springframework.extensions.webeditor.ui.TabbedToolbar, {version: "1.0", build: "1"}, YAHOO);