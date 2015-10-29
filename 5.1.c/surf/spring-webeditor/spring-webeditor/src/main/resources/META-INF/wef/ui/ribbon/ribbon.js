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
 * Ribbon component
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

   YAHOO.namespace('org.springframework.extensions.webeditor.ui.ribbon');

   /**
    * Ribbon component constructor
    * @constructor
    * @class Ribbon
    * @namespace WEF.ui
    * @extends WEF.Widget
    */
   WebEditor.ui.Ribbon = function(config)
   {
      WebEditor.ui.Ribbon.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };

   YAHOO.extend(WebEditor.ui.Ribbon, WEF.Widget,
   {
      /**
       * Initialises managed attributes and subscribes to WEF--afterRender event
       * @method init 
       */
      init: function WEF_UI_Ribbon_init()
      {
         WebEditor.ui.Ribbon.superclass.init.apply(this);
         // render ribbon after WEF is rendered
         Bubbling.on('WEF'+WEF.SEPARATOR+'afterRender', this.render);
         this.widgets.toolbars = [];
         this.widgets.activePlugin = null;
         this.initAttributes(this.config);
      },

      /**
       * Set up 'position' attribute as a managed attribute. Default value is 'top'
       * and valid values are 'top', 'left' and 'right'. 
       */
      initAttributes: function WEF_UI_Ribbon_init_attributes(attr)
      {

         this.setAttributeConfig('position', 
         {
            value: WebEditor.ui.Ribbon.POSITION_TOP,
            validator: function WEF_UI_Ribbon_position_attribute_validator(value)
            {
               return ('top,left,right'.indexOf(value) != -1);
            }
         });

         this.on('positionChange', this.onPositionChangeEvent);
      },

      /**
       * Renders ribbon and also subscribes to the window resize event to resize
       * the ribbon accordingly
       *
       */
      render: function WEF_UI_Ribbon_render()
      {
         this.renderRibbon();
         this.widgets.ribbon.show();
         Event.addListener(window, "resize", function WEF_UI_Ribbon_onResize()
         {
            this.set('position', this.get('position'));
            this.resizeRibbon();
         }, this, true);
      },

      /**
       * Renders ribbon
       *
       */
      renderRibbon: function WEF_UI_Ribbon_renderRibbon()
      {
         if (!Dom.get(this.config.id))
         {
            Dom.get('wef').innerHTML+='<div id="wef-ribbon-container" class="wef-ribbon-container"><div id="wef-ribbon" class="wef-ribbon wef-hide" role="toolbar"><div id="wef-ribbonHeader" class="hd"><h6>'+this.getMessage('wef.ribbon-title')+'</h6></div><div id="wef-ribbonBody" class="bd"><div id="wef-toolbar-container" class="wef-hide"></div></div><div id="wef-ribbonFooter" class="ft"><div id="wef-toolbar-secondary-container"></div></div></div></div>';
         }

         var panelConfig = 
         {
            visible: false,
            draggable: false,
            underlay: 'none',
            close: false
         };

         var ribbon = this.widgets.ribbon = new YAHOO.widget.Panel(this.config.id, panelConfig);
         //Override hide method as container aria plugin also hides panel when a menu is closed using the ESC key
         ribbon.hide = function() {};
         
         var header = this.widgets.ribbonHeader = new Element(ribbon.header);
         var body = this.widgets.ribbonBody = new Element(ribbon.body);
         var footer = this.widgets.ribbonFooter = new Element(ribbon.footer);
         var container = this.widgets.ribbonContainer = new Element(ribbon.element.parentNode);
         container.addClass('wef-ribbon-orientation-' + this.get('position'));
         Dom.addClass([ribbon.header, ribbon.body, ribbon.footer], 'wef-ribbon-module');
         // set correct width  
         YAHOO.lang.later(0, this, this.resizeRibbon);

         ribbon.render();
         
         var name = 'WEF-'+WebEditor.ui.Ribbon.PRIMARY_TOOLBAR+'Toolbar';
         this.addToolbar(
            WebEditor.ui.Ribbon.PRIMARY_TOOLBAR,
            {
               id:  WebEditor.ui.Ribbon.PRIMARY_TOOLBAR,
               name: name,
               element: 'wef-toolbar-container'
            },
            WebEditor.ui.TabbedToolbar
         );
         //subscribe to tabchange event 
         Bubbling.on(name + WebEditor.SEPARATOR + 'tabChange', this.onTabChange);
         
         name = 'WEF-Ribbon'+WebEditor.ui.Ribbon.SECONDARY_TOOLBAR+'Toolbar';

         //add secondary toolbar         
         this.addToolbar(
            WebEditor.ui.Ribbon.SECONDARY_TOOLBAR,
            {
               id:  WebEditor.ui.Ribbon.SECONDARY_TOOLBAR,
               name: name,
               title: this.getMessage('wef.toolbar-secondary'),
               element: 'wef-toolbar-secondary-container',
               buttons:
               {
                  buttons: function()
                     {
                        //note only non-quirks and ie 7+ can orient vertically
                        //so only add orientation menu for non-quirks and non ie6
                        var secToolbarButtons = [
                           {
                              type: 'push',
                              label: this.getMessage('wef.ribbon-help-label'),
                              title: this.getMessage('wef.ribbon-help-label'),                        
                              value: '',
                              id: this.config.name + WebEditor.SEPARATOR + 'help',
                              icon: true
                           } 
                        ];
                        if (YAHOO.env.ua.ie != 6 && document.compatMode != 'BackCompat')
                        {
                           secToolbarButtons = [{
                              type: 'menu',
                              label: this.getMessage('wef.ribbon-orientation-label'),
                              value: name+ WebEditor.SEPARATOR + 'ribbon-placement',
                              id: this.config.name + WebEditor.SEPARATOR + 'ribbon-placement',
                              icon: true,
                              menu: 
                              [
                                 {
                                    text: this.getMessage('wef.ribbon-orientation-menu-top-label'),
                                    value: WebEditor.ui.Ribbon.POSITION_TOP
                                 }, 
                                 {
                                    text: this.getMessage('wef.ribbon-orientation-menu-left-label'),
                                    value: WebEditor.ui.Ribbon.POSITION_LEFT
                                 }, 
                                 {
                                    text: this.getMessage('wef.ribbon-orientation-menu-right-label'),
                                    value: WebEditor.ui.Ribbon.POSITION_RIGHT
                                 }
                              ]
                           }].concat(secToolbarButtons);
                        }
                        return secToolbarButtons;
                     }.apply(this)
               }
            },
            WebEditor.ui.Toolbar);
         //get ribbon position from cookie if available otherwise reset to initial config value
         this.set('position', WebEditor.getCookieValue(this.config.name,'ribbon-position') || this.get('position'));

         //quirks mode (hack to show secondary toolbar in correct place)
         if (document.compatMode=='BackCompat' && YAHOO.env.ua.ie && (this.get('position') == WebEditor.ui.Ribbon.POSITION_TOP))
         {

            YAHOO.lang.later(100, this, function() {             
               //need to replace this with a better fix a not future proof
               // if (YAHOO.env.ua.ie == 6)
               // {
                  this.widgets.ribbonFooter.setStyle('width', '16em');
                  this.widgets.ribbonFooter.setStyle('overflow', 'auto');                  
               // }
               // else
               // {
               //    // this.widgets.ribbonFooter.setStyle('width', '29em');
               //    this.widgets.ribbonFooter.setStyle('overflow', 'visible');
               // }
            });
         }
         // Refresh any attributes here
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'ribbon-placementClick', this.onRibbonPlacementClick, this, true);
         Bubbling.on(this.config.name + WebEditor.SEPARATOR + 'helpClick', this.onHelpClick, this, true);
         
      },
      
      /**
       * Resizes ribbon
       * @private
       *  
       */
      resizeRibbon: function WEF_UI_Ribbon_resizeRibbon()
      {
         if (this.get('position') === WebEditor.ui.Ribbon.POSITION_TOP)
         {
            var newWidth = Dom.getRegion(this.widgets.ribbonContainer).width- parseInt(Dom.getStyle(this.widgets.ribbonHeader,'width'),10);
            if (!YAHOO.env.ua.ie)
            {
              newWidth+=2;
            }
            else if (YAHOO.env.ua.ie>7)
            {
              newWidth+=3;
            }
            else if (YAHOO.env.ua.ie<7)
            {
              newWidth-=7;
            }
            //resize if quirks mode
            if ((YAHOO.env.ua.ie>7 && (document.compatMode=='BackCompat')))
            {
              newWidth-=9;
            }
            else if ((YAHOO.env.ua.ie==7 && (document.compatMode=='BackCompat')))
            {
              newWidth-=6;
            }

            this.widgets.ribbonBody.setStyle('width', newWidth+'px');
         }
         else
         {
            this.widgets.ribbonBody.setStyle('width', '132px');
         }

         Dom.setStyle(this.widgets.ribbonBody.getElementsByClassName('yui-content', 'div')[0], 'height', this.widgets.ribbonBody.getStyle('height'));
      },
      
      /**
       * Retrieves a reference to the  toolbar with the given id
       * @param toolbar {String} Id of existing toolbar
       * 
       * @return {WEF.ui.Toolbar} Reference to the specified toolbar 
       */
      getToolbar: function WEF_UI_Ribbon_getToolbar(toolbarId)
      {
         if (toolbarId === WebEditor.ui.Ribbon.PRIMARY_TOOLBAR | toolbarId === WebEditor.ui.Ribbon.SECONDARY_TOOLBAR)
         {
            return this.widgets.toolbars[toolbarId];
         }
         else 
         {
            return this.widgets.toolbars[WebEditor.ui.Ribbon.PRIMARY_TOOLBAR].getToolbar(toolbarId);
         } 
      },

      /**
       * Adds toolbar to ribbon, either as a new tab or as a toolbar within a tab
       *
       * @param id {String} Id on toolbar
       * @param config {Object} Configuration for object
       * @param toolbarType {WEF.ui.Toolbar|WEF.ui.Tabbed-Toolbar} The type of toolbar to create.
       * @return {WEF.ui.Toolbar|WEF.ui.Tabbed-Toolbar} Reference to the newly created toolbar
       *
       */
      addToolbar: function WEF_UI_Ribbon_addToolbar(id, config, toolbarType)
      {
         var tbar = null;  
         
         if (!toolbarType)
         {
            throw new Error('Unable to add toolbar of specified type');
         }

         //add primary/secondary toolbars
         if (id === WebEditor.ui.Ribbon.PRIMARY_TOOLBAR | id === WebEditor.ui.Ribbon.SECONDARY_TOOLBAR)
         {
            if(!this.widgets.toolbars[id])
            {
               tbar = new toolbarType(config);      
            }   
         }
         else //add toolbars as tabs of tabbed toolbars.
         {
            tbar = this.widgets.toolbars[WebEditor.ui.Ribbon.PRIMARY_TOOLBAR].addToolbar(id, config);
         }

         this.widgets.toolbars.push(tbar);
         this.widgets.toolbars[id] = tbar;
         tbar.init();   
         tbar.render();

         return tbar;
      },

      /**
       * Add button to specified button group and specified toolbar
       *
       * @param id {String} The name of the toolbar to add the button to.
       * @param buttonConfig {Object} An object literal for the button config
       *
       * @return {Boolean} Success/failure of addition
       */
      addButtons: function WEF_UI_Ribbon_addButton(id, buttonConfig)
      {
         if (!this.widgets.toolbars[id])
         {
            throw new Error('Toolbar ' + id + ' not found');
            return false;   
         }
         else
         {
            return this.widgets.toolbars[id].addButtons(buttonConfig);
         }
      },

      /**
       * Handler for when ribbon orientation is changed
       * 
       * @param e {String} Name of event
       * @param args {Object} Event arguments 
       */
      onRibbonPlacementClick: function WEF_UI_Ribbon_onRibbonPlacementClick(e, args)
      {
         this.set('position', args[1]);
      },

      /**
       * Handler for when help button is clicked
       * 
       * @param e {String} Name of event
       * @param args {Object} Event arguments 
       */
      onHelpClick: function WEF_UI_Ribbon_onHelpClick(e, args)
      {
         if (this.widgets.activePlugin && this.widgets.activePlugin.onHelp)
         {
            this.widgets.activePlugin.onHelp();
         }
      },

      /**
       * Change handler for position attribute. Moves the ribbon around
       * the screen adjusting the margin certain elements appropiately
       *
       * @param e {Object} Object literal describing previous and new value of attribute
       */
      onPositionChangeEvent: function WEF_UI_Ribbon_onPositionChangeEvent(e)
      {
         var container = this.widgets.ribbonContainer, bodyEl = document.getElementsByTagName('body')[0];

         // if position has changed, change class
         if (e.prevValue !== e.newValue)
         {
            container.removeClass(('wef-ribbon-orientation-' + e.prevValue));
            container.addClass('wef-ribbon-orientation-' + e.newValue);
         }
         //fix for quirks mode
         if (document.compatMode=='BackCompat'  && YAHOO.env.ua.ie)
         {
            if (e.newValue === WebEditor.ui.Ribbon.POSITION_TOP)
            {
               this.widgets.ribbonContainer.setStyle('width', Dom.getViewportWidth()+'px');
               // this.widgets.ribbonFooter.setStyle('width', '29em');
            }            
            bodyEl.style.overflowX='hidden';            
         }

         if (e.newValue === WebEditor.ui.Ribbon.POSITION_TOP && !this._originalBodyMarginTop) 
         {
            // save original margin as any position changes to ribbon *might*
            // change the margins value
            this._originalBodyMarginTop = Dom.getStyle(bodyEl, 'margin-top');

            // offset body element by height of ribbon if position is at top.
            Dom.setStyle(bodyEl, 'margin-top', parseInt(Dom.getStyle(bodyEl, 'margin-top'), 10) + 
                  (parseInt(Dom.getStyle(this.config.id, 'height'), 10)) * 1.5 + 'px');

            // reset any padding (left or right) on body
            this._originalBodyMarginLeft = this._originalBodyMarginRight = null;
            Dom.setStyle(bodyEl, 'margin-left', this._originalBodyMarginLeft);
            Dom.setStyle(bodyEl, 'margin-right', this._originalBodyMarginRight);
            this.widgets.ribbonContainer.setStyle('margin-right', 0);
            this.widgets.ribbonContainer.setStyle('margin-left', 0);
            //rest height as this would have been set when orientation is at left or right
            this.widgets.ribbonBody.setStyle('height', 'auto');
         }
         else if (e.newValue !== WebEditor.ui.Ribbon.POSITION_TOP) 
         {
            // reset body margin-top
            if (this._originalBodyMarginTop!=null)
            {
               Dom.setStyle(bodyEl, 'margin-top', this._originalBodyMarginTop);               
            }

            this._originalBodyMarginTop = null;
            // resize toolbar to viewport height minus header and footer heights
            this.widgets.ribbonBody.setStyle('height', (Dom.getViewportHeight() - 
                  (this.widgets.ribbonHeader.get('offsetHeight') +
                  this.widgets.ribbonFooter.get('offsetHeight')) ) + 'px');

            // offset body element by width of ribbon if position is not at top.
            if (e.newValue === WebEditor.ui.Ribbon.POSITION_RIGHT && !this._originalBodyMarginRight)
            {
               // save original margin right
               this._originalBodyMarginRight = Dom.getStyle(bodyEl, 'margin-right');
               Dom.setStyle(bodyEl, 'margin-right', parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'), 10) + 'px');
               // set negative margin
               this.widgets.ribbonContainer.setStyle('margin-right', 0 - parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'), 10) + 'px');
               // reset
               this.widgets.ribbonContainer.setStyle('margin-left', 0);

               Dom.setStyle(bodyEl, 'margin-left', this._originalBodyMarginLeft);
               this._originalBodyMarginLeft = null;
            }

            if (e.newValue === WebEditor.ui.Ribbon.POSITION_LEFT && !this._originalBodyMarginLeft)
            {
               // save original margin left
               this._originalBodyMarginLeft = Dom.getStyle(bodyEl, 'margin-left');

               Dom.setStyle(bodyEl, 'margin-left',    parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'), 10)+ 'px');

               // set negative margin
               this.widgets.ribbonContainer.setStyle('margin-left', 0 - parseInt(Dom.getStyle(this.widgets.ribbonContainer.getElementsByTagName('div')[0], 'width'), 10) + 'px');
               

               // reset
               this.widgets.ribbonContainer.setStyle('margin-right', 0);
               Dom.setStyle(bodyEl, 'margin-right', this._originalBodyMarginRight);
               this._originalBodyMarginRight = null;
            }
         }
         if (this.widgets.toolbars[WebEditor.ui.Ribbon.PRIMARY_TOOLBAR])
         {
            this.widgets.toolbars[WebEditor.ui.Ribbon.PRIMARY_TOOLBAR].setOrientation(e.newValue);            
         }
         WEF.setCookieValue(this.config.name,'ribbon-position', e.newValue);
         this.resizeRibbon();
      },
      
      /**
       * Handler for when tab is changed. Defaults current plugin and sets
       * the active plugin to be the owner of the clicked tab
       * 
       * @param e {String} Name of event
       * @param args {Object} Event arguments 
       */
      onTabChange: function WEF_UI_Ribbon_onTabChange(e,args)
      {
         var prevValue = args[1].prevValue,
             newValue =args[1].newValue,
             ribbonObj = WebEditor.module.Ribbon;
         
         if (ribbonObj.widgets.activePlugin !== newValue.pluginOwner)
         {
            
            if (prevValue.pluginOwner && prevValue.pluginOwner.deactivate)
            {
               prevValue.pluginOwner.deactivate();
            }
            
            if (newValue.pluginOwner && newValue.pluginOwner.activate)
            {
               ribbonObj.widgets.activePlugin = newValue.pluginOwner;
               ribbonObj.widgets.activePlugin.activate();
            }            
         }
      }
   });
   
   /**
    * Value of ribbon position constant for left orientation
    * @property POSITION_LEFT
    * @final
    * @type string
    *  
    */
   WebEditor.ui.Ribbon.POSITION_LEFT = 'left';

   /**
    * Value of ribbon position constant for right orientation
    * @property POSITION_RIGHT
    * @final
    * @type string
    *  
    */
   WebEditor.ui.Ribbon.POSITION_RIGHT = 'right';

   /**
    * Value of ribbon position constant for top orientation
    * @property POSITION_TOP
    * @final
    * @type string
    *  
    */
   WebEditor.ui.Ribbon.POSITION_TOP = 'top';

   /**
    * Value of id for primary toolbar
    * @property PRIMARY_TOOLBAR
    * @final
    * @type string
    *  
    */
   WebEditor.ui.Ribbon.PRIMARY_TOOLBAR = 'primary';

   /**
    * Value of id for secondary toolbar
    * @property SECONDARY_TOOLBAR
    * @final
    * @type string
    *  
    */
   WebEditor.ui.Ribbon.SECONDARY_TOOLBAR = 'secondary';
})();

WEF.register("org.springframework.extensions.webeditor.ui.ribbon", YAHOO.org.springframework.extensions.webeditor.ui.Ribbon, {version: "1.0", build: "1"}, YAHOO);