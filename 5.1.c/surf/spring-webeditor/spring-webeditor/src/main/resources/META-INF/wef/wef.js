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
 * The core WEF module
 * @module WEF 
 * @namespace WEF
 */
(function()
{
   var Dom = YAHOO.util.Dom,
       Element = YAHOO.util.Element,
       Cookie = YAHOO.util.Cookie;

   if (typeof WEF == "undefined" || !WEF)
   {
      throw new Error('WEF not found');   
   }

   var Bubbling = YAHOO.Bubbling;

   YAHOO.namespace('org.springframework.extensions.webeditor');

   /**
    * Provides AOP style functionality (before,after,around)
    * 
    * @class Do
    * @namespace WEF
    */
   WEF.Do = function() 
   {
      var aAspects = 
      {
         /**
          * Decorates method wth supplied function. Stores a reference to the
          * original function on the new function object 
          *  
          * @param {Object} oTarget Object that contains method to override
          * @param {String} sMethod Name of function to override
          * @param {Function} fAdvice Function to run before specified object method
          * 
          * @return {Function} function with before advice applied
          */
         before: function WEF_before(oTarget,sMethodName,fn) 
         {
            var fOrigMethod = oTarget[sMethodName];
            oTarget[sMethodName] = function()
            {
               fn.apply(oTarget, arguments);
               return fOrigMethod.apply(oTarget, arguments);
            };
            oTarget[sMethodName]['fOrigMethod_before'] = fOrigMethod;
         },

         /**
          * Decorates method wth supplied function. Stores a reference to the
          * original function on the new function object 
          *  
          * @param {Object} oTarget Object that contains method to override
          * @param {String} sMethod Name of function to override
          * @param {Function} fAdvice Function to run before specified object method
          * 
          * @return {Function} function with after advice applied
          */         
         after: function WEF_after(oTarget,sMethodName,fn)
         {
            var fOrigMethod = oTarget[sMethodName];
            oTarget[sMethodName] = function () 
            {
               var rv = fOrigMethod.apply(oTarget, arguments);
               return fn.apply(oTarget, [rv]);
            };
            oTarget[sMethodName]['fOrigMethod_after'] = fOrigMethod;
         },

         /**
          * Decorates method wth supplied function. Stores a reference to the
          * original function on the new function object 
          *  
          * @param {Object} oTarget Object that contains method to override
          * @param {String} sMethod Name of function to override
          * @param {Array} aFn Array of functions to run before and after 
          * specified object method
          * 
          * @return {Function} function with around advice applied
          */
         around: function WEF_around(oTarget,sMethodName,aFn)
         {
            var fOrigMethod = oTarget[sMethodName];
            oTarget[sMethodName] = function() 
            {
               if (aFn && aFn.length==2) 
               {
                  // before
                  aFn[0].apply(oTarget, arguments);
                  // original
                  var rv = fOrigMethod.apply(oTarget, arguments);
                  // after
                  return aFn[1].apply(oTarget, [rv]);
               }
               else 
               {
                  return fOrigMethod.apply(oTarget, arguments);
               }
            };
            if (aFn && aFn.length==2) 
            {
               oTarget[sMethodName]['fOrigMethod_around'] = fOrigMethod;
            }
         }
      };

      /**
       * Advises specified method with the specified aspect and advice
       * 
       * @method advise
       * @private
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sAspect Name of aspect to advise
       * @param {String} sMethod Name of function to override
       * @param {Function} fAdvice Function to run before specified object method
       * 
       * @return {Function} function with advice applied
       */
      var advise = function(oTarget,sAspect,sMethod,fAdvice) 
      {
         if (oTarget && sAspect && sMethod && fAdvice && aAspects[sAspect]) 
         {
            // decorate specified method
            aAspects[sAspect](oTarget,sMethod,fAdvice);
         }

         return oTarget;
      };

      /**
       * Decorates supplied object method with supplied function so that the 
       * function is run before the object method.
       * 
       * @method before
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {Function} fAdvice Function to run before specified object method
       * 
       * @return {Function} function with before advice applied
       */
      var before = function WEF_Do_before(oTarget, sMethod, fAdvice)
      {
         return advise(oTarget,WEF.Do.BEFORE,sMethod,fAdvice);
      };

      /**
       * Decorates supplied object method with supplied function so that the 
       * function is run after the object method.
       * 
       * @method after
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {Function} fAdvice Function to run after specified object method
       * 
       * @return {Function} function with after advice applied
       */
      var after = function WEF_Do_after(oTarget,sMethod,fAdvice)
      {
         return advise(oTarget,WEF.Do.AFTER,sMethod,fAdvice);
      };

      /**
       * Decorates supplied object method with supplied functions so that the 
       * functions are runn before and after the object method.
       * 
       * @method around
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {Array} fAdvice Array of functions, first of which is the before
       * function and the second is the after function
       *
       * @return {Function} function with around advice applied
       */
      var around = function WEF_Do_around(oTarget,sMethod,aAdvices)
      {
         return advise(oTarget,WEF.Do.AROUND,sMethod,aAdvices);
      };

      /**
       * Unbinds (removes) the advice given to the specified object method
       * 
       * @method unbind
       * 
       * @param {Object} oTarget Object that contains method to override
       * @param {String} sMethod Name of function to override
       * @param {String} type Name of advice to unbind ('before', 'after' or
       * 'around')
       * 
       */
      var unbind = function WEF_Do_unbind(oTarget, sMethod, type)
      {
         var resolvedName = 'fOrigMethod_'+type;
         if (oTarget[sMethod] && oTarget[sMethod][resolvedName])
         {
            oTarget[sMethod] = oTarget[sMethod][resolvedName];
         }
      };

      return {
         before: before,
         after : after,
         around: around,
         unbind: unbind
      };
   }();
   
   /**
    * Name of before advice
    * @property BEFORE 
    * @final 
    * @type string
    */
   WEF.Do.BEFORE = 'before';

   /**
    * Name of after advice
    * @property AFTER
    * @final 
    * @type string
    */
   WEF.Do.AFTER  = 'after';

   /**
    * Name of around advice
    * @property AROUND 
    * @final 
    * @type string
    */
   WEF.Do.AROUND = 'around';

   /**
    * value of separator used to send namespaced events
    * @property SEPARATOR 
    * @final 
    * @type string
    */
   WEF.SEPARATOR = '--';

   /**
    * DEBUG constant
    * @property DEBUG 
    * @final 
    * @type string
    */
   WEF.DEBUG = 'DEBUG';

   /**
    * Fully qualified name of before event
    * @property BEFORE_EVENT 
    * @final 
    * @type string
    */
   WEF.BEFORE_EVENT = WEF.SEPARATOR + WEF.Do.BEFORE;

   /**
    * Fully qualified name of after event
    * @property AFTER_EVENT 
    * @final 
    * @type string
    */
   WEF.AFTER_EVENT =  WEF.SEPARATOR + WEF.Do.AFTER;

   /**
    * Sets value of specified sub cookie
    * 
    * @namespace WEF.setCookieValue
    * @method setCookieValue
    * 
    * @param name {String} Name of specified sub cookie to set
    * @param value Value of specified sub-cookie
    * 
    * @return {String}
    */
   WEF.setCookieValue = function WEF_setCookieValue(rootName, name, value)
   {
      var data = Cookie.getSubs(rootName) || {};
      data[name] = value;
      return Cookie.setSubs(rootName, data);
   };

   /**
    * Returns value of specified sub-cookie
    * 
    * @namespace WEF.getCookieValue
    * @method getCookieValue
    * 
    * @param name {String} (Optional) Name of specified sub cookie to retrieve. If no name specified then returns full cookie
    * @return {String} Value of specified sub-cookie or full cookie if no name specified.
    */
   WEF.getCookieValue = function WEF_UI_getCookieValue(rootName, name)
   {
      return (YAHOO.lang.isUndefined(name)) ? Cookie.getSubs(rootName) : Cookie.getSub(rootName, name);
   };

   /**
    * Base object of all WEF components. Automatically fires before and after
    * Bubbling events for init() and destroy().
    * @class Base
    * @namespace WEF
    * @uses YAHOO.util.AttributeProvider
    * @constructor
    * 
    * @param {Object} config Configuration object. If config contains a field
    * called setUpCustomEvents (Array) which point to object methods then those
    * methods are automatically given an around aspect which fires a before and 
    * after event for that method. The actual event name is namespaced eg
    * 'objName::beforeInit'. 'name' is a required property of the config parameter. 
    * 
    */
   WEF.Base = function WEF_Base(config)
   {
      if (config)
      {
         this.config = config;
         this.config.setUpCustomEvents = this.config.setUpCustomEvents || [];
         
         function _setupEvent(mth) 
         {
            if (!this[mth])
            {
               return;
            }

            var capitalizedMthName = mth.slice(0,1).toUpperCase() + mth.slice(1),
                beforeMthdName = WEF.BEFORE_EVENT + capitalizedMthName,
                afterMthdName = WEF.AFTER_EVENT + capitalizedMthName;

            WEF.Do.around(this,mth,
            [
               // before
               function()
               {
                  if (WEF.get('debugMode'))
                  {
                     Bubbling.fire(WEF.DEBUG + beforeMthdName,
                     {
                        name: this.config.name,
                        obj: this
                     });
                  }
                  Bubbling.fire(this.config.name + beforeMthdName,
                  {
                     name: this.config.name,
                     obj: this
                  });
                  
                  return this;
               },

               // after
               function()
               {
                  if (WEF.get('debugMode'))
                  {
                     Bubbling.fire(WEF.DEBUG + afterMthdName,
                     {
                        name: this.config.name,
                        obj: this
                     });
                  }
                  Bubbling.fire(this.config.name + afterMthdName,
                  {
                     name: this.config.name,
                     obj: this
                  });
                  return this;
               }
            ]);
         }

         var evts = (['init','destroy']).concat(this.config.setUpCustomEvents);

         for (var i = 0, len = evts.length; i < len; i++)
         {
            _setupEvent.apply(this,[evts[i]]);
         }
      }

      return this;
   };
   
   /**
    * Bubbling (namespaced) event fired before init method is called
    * @event beforeInit 
    */
    
   /**
    * Bubbling (namespaced) event fired after init method is called
    * @event afterInit 
    */
    
   /**
    * Bubbling (namespaced) event fired before destroy method is called
    * @event beforeDestroy 
    */
    
   /**
    * Bubbling (namespaced) event fired after destroy method is called
    * @event afterDestroy 
    */
   WEF.Base.prototype = 
   {
      /**
       * Initialises object and fires a beforeInit and afterInit event
       * 
       * @return {WEF.Base}
       */
      init: function init()
      {
         return this;
      },

      /**
       * Destroys object and fires a beforeDestroy and afterDestroy event
       * 
       * @return {WEF.Base}
       */      
      destroy: function destroy()
      {         
         return this;
      },

      /**
       * Add i18n messages to the global message store.
       * 
       * @param {String} name Name of key to use for message. Actual name is
       *                      stored with the name of the component as a prefix
       * @param {String} msg  Message value
       */
      setMessages: function(name, msg)
      {
         var container;
         if (window['Alfresco'])
         {
            container = Alfresco.messages.global;
         }
         else if (window['SpringSurf'])
         {
            container = SpringSurf.messages.global;
         }
         else 
         {
            return;
         }
         container[name] = msg;
      },

      /**
       * Retrieves i18n message
       * 
       * @param {String} name Key of message. Fully qualified name is prefixed 
       *                      with name of component
       */
      getMessage: function(name)
      {
         var container = {};
             
         if (window['Alfresco'])
         {
            container = Alfresco.messages.global;
         }
         else if (window['SpringSurf'])
         {
            container = SpringSurf.messages.global;
         }
         
         return container[name] || name; 
      }
   };

   YAHOO.augment(WEF.Base, YAHOO.util.AttributeProvider);

   /**
    * The Plugin object constructor. Automatically fires before and after
    * Bubbling events for activate() and deactivate() in additional to those
    * fired by WEF.Base.
    * 
    * @class WEF.Plugin
    * @namespace WEF
    * @constructor
    * @extends WEF.Base
    * @uses YAHOO.util.AttributeProvider 
    */
   WEF.Plugin = function WEF_Plugin(config)
   {
      config.setUpCustomEvents = (['render','activate','deactivate']).concat(config.setUpCustomEvents || []);
      WEF.Plugin.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));
   };
   
   /**
    * Bubbling (namespaced) event fired before activate method is called
    * @event beforeActivate
    */
    
   /**
    * Bubbling (namespaced) event fired after activate method is called
    * @event afterActivate 
    */
    
   /**
    * Bubbling (namespaced) event fired before deactivate method is called
    * @event beforeDeactivate
    */
    
   /**
    * Bubbling (namespaced) event fired after deactivate method is called
    * @event afterDeactivate 
    */
   YAHOO.extend(WEF.Plugin, WEF.Base, 
   {
      /**
       * Activates plugin and fires a beforeActivate and afterActivate event
       * 
       * @return {WEF.PLugin}
       */
      activate: function activate()
      {
         return this;
      },

      /**
       * Deactivates object and fires a beforeDeactivate and afterDeActivate event
       * 
       * @return {WEF.Plugin}
       */
      deactivate: function deactivate()
      {
         return this;
      },

      /**
       * Renders object and fires a beforeRender and afterRender event
       * 
       * @return {WEF.Widget}
       */
      render: function WEF_Widget_render() 
      {
         return this;
      },
      
      /**
       * Container for any service instances
       */
      services: {},

      /**
       * Container for widget instances
       */
      widgets: {}
   });

   /**
    * The App widget constructor. Automatically fires before and after
    * Bubbling events for render(), show() and hide(), in additional to those
    * fired by WEF.Base.
    * 
    * @class WEF.Widget
    * @namespace WEF
    * @constructor
    * @extends WEF.Plugin
    * @uses YAHOO.util.AttributeProvider
    */  
   
   WEF.Widget = function WEF_Widget(config)
   {
      config.setUpCustomEvents = (['show','hide']).concat(config.setUpCustomEvents || []);
      WEF.Widget.superclass.constructor.apply(this, Array.prototype.slice.call(arguments));

      this.services = {};
      this.widgets = {};
   };
   /**
    * Bubbling (namespaced) event fired before render method is called
    * @event beforeRender
    */
    
   /**
    * Bubbling (namespaced) event fired after render method is called
    * @event afterRender
    */
    
   /**
    * Bubbling (namespaced) event fired before show method is called
    * @event beforeShow
    */
    
   /**
    * Bubbling (namespaced) event fired after show method is called
    * @event afterShow 
    */
      /**
    * Bubbling (namespaced) event fired before hide method is called
    * @event beforeHide
    */
    
   /**
    * Bubbling (namespaced) event fired after hide method is called
    * @event afterHide
    */ 
   YAHOO.extend(WEF.Widget, WEF.Plugin, 
   {
      /**
       * Initialises widget and sets up element property to be an instance of
       * YAHOO.util.Element
       *  
       */
      init: function init()
      {
         //if no element on config then widget must assign one at some point.
         if (this.config.element)
         {
            this.element = new Element(this.config.element);
         }
      },
      /**
       * Initialises managed attributes. Should be overridden
       * @method initAttributes
       *  
       */
      initAttributes: function initAttributes()
      {
      },

      /**
       * Shows widget and fires a beforeShow and afterShow event
       * 
       * @return {WEF.Widget}
       */
      show : function WEF_Widget_show()
      {
         Dom.addClass(this.config.element, 'wef-show');
         Dom.removeClass(this.config.element, 'wef-hide');
         return this;
      },

      /**
       * Hides widget fires a beforeHide and afterHide event
       * 
       * @return {WEF.Widget}
       */
      hide: function WEF_Widget_hide()
      {
         Dom.addClass(this.config.id, 'wef-hide');
         Dom.removeClass(this.config.id, 'wef-show');
         return this;
      }
   });

   /**
    * The App object constructor
    * 
    * @class WEF.App
    * @constructor
    * @extends WEF.Plugin
    * @namespace WEF
    * @uses YAHOO.util.AttributeProvider 
    */
   WEF.App = function WEF_App(config)
   {
      WEF.App.superclass.constructor.apply(this, arguments);
   };

   YAHOO.extend(WEF.App, WEF.Plugin);

   YAHOO.org.springframework.extensions.webeditor = WEF;
})();

WEF.register("org.springframework.extensions.webeditor", YAHOO.org.springframework.extensions.webeditor, {version: "1.0", build: "1"}, YAHOO);