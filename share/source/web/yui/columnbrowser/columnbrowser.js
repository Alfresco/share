/*
 * Copyright (c) 2007, David A. Lindquist <david.lindquist@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

YAHOO.namespace('extension');

/**
 * Note! This class is based upon David Lindqvist's ColumNav component.
 * Major changes that has occured after David's work are:
 * - Name has changed to ColumnBrowser and code more YUI like
 * - Code has been refactored to use the official YUI 2.7 Carousel component
 * - A breadcrumb has been added out of the box with navigation support
 * - Headers and footers have been added for each column
 * - Handlers for custom json-data can be plugged in
 * - XML and dom datasource has currently been removed
 */

/**
 * The ColumnBrowser enables browsing in a Mac OSX finder way.
 * When going in to deep hierarchies a breadcrumb can be easily navigate back and forward.
 * Currently the user of the widget MUST tranform the server reponse to a columnInfo object.
 *
 * @module carousel
 * @requires yahoo, dom, event, element, carousel
 * @optional animation
 * @namespace YAHOO.extension
 * @title ColumnBrowser Widget
 * @beta
 */
(function () {

   var WidgetName; // forward declaration

   /**
    * The ColumnBrowser widget.
    *
    * @class ColumnBrowser
    * @constructor
    * @param el {HTMLElement | String} The HTML element that represents the
    * the container that houses the ColumnBrowser.
    * @param cfg {Object} (optional) The configuration values
    */
   YAHOO.extension.ColumnBrowser = function(el, cfg)
   {
      YAHOO.extension.ColumnBrowser.superclass.constructor.call(this, el, cfg);
   };

   /*
    * Private variables of the Carousel component
    */

   /* Some abbreviations to avoid lengthy typing and lookups. */
   var ColumnBrowser = YAHOO.extension.ColumnBrowser,
       Carousel      = YAHOO.widget.Carousel,
       Dom           = YAHOO.util.Dom,
       Event         = YAHOO.util.Event,
       Con           = YAHOO.util.Connect,
       JS            = YAHOO.lang;

   /**
    * The widget name.
    * @private
    * @static
    */
   WidgetName = "ColumnBrowser";
   
   /**
    * The internal table of ColumnBrowser instances.
    * @private
    * @static
    */
   var instances = {},


   /*
    * Custom events of the ColumnBrowser component
    */

   /**
    * @event afterScroll
    * @description Fires when the user clicks one of the items in the column.
    * See
    * <a href="YAHOO.util.Element.html#addListener">Element.addListener</a>
    * for more information on listening for this event.
    * @type YAHOO.util.CustomEvent
    */
   itemSelectEvent = "itemSelect";


   /*
    * Private helper functions used by the Carousel component
    */

   /**
    * Create an element, set its class name and optionally install the element
    * to its parent.
    * @method createElement
    * @param el {String} The element to be created
    * @param attrs {Object} Configuration of parent, class and id attributes.
    * If the content is specified, it is inserted after creation of the
    * element. The content can also be an HTML element in which case it would
    * be appended as a child node of the created element.
    * @private
    */
   function createElement(el, attrs) {
       var newEl = document.createElement(el);

       attrs = attrs || {};
       if (attrs.className) {
           Dom.addClass(newEl, attrs.className);
       }

       if (attrs.parent) {
           attrs.parent.appendChild(newEl);
       }

       if (attrs.id) {
           newEl.setAttribute("id", attrs.id);
       }

       if (attrs.content) {
           if (attrs.content.nodeName) {
               newEl.appendChild(attrs.content);
           } else {
               newEl.innerHTML = attrs.content;
           }
       }

       return newEl;
   }


   /*
    * Static members and methods of the ColumnBrowser component
    */

   /**
    * Return the appropriate ColumnBrowser object based on the id associated with
    * the ColumnBrowser element or false if none match.
    * @method getById
    * @public
    * @static
    */
   ColumnBrowser.getById = function (id) {
       return instances[id] ? instances[id].object : false;
   };


   YAHOO.extend(ColumnBrowser, YAHOO.util.Element, {

      /*
       * Internal variables used within the Carousel component
       */

      /**
       * Easy and fast access to the element id
       *
       * @property _id
       * @private
       */
      _id: null,

      /**
       * Easy and fast access to the carousel that houses the columns
       *
       * @property _carousel
       * @private
       */
      _carousel: null,

      /**
       * Easy and fast access to the root element
       *
       * @property _el
       * @private
       */
      _el: null,

      /**
       * Checks if the carousel is moving its items around.
       *
       * @property _isMoving
       * @private
       */
      _isMoving: false,

      /**
       * The HttpRequest object.
       *
       * @property _request
       * @private
       */
      _request: null,

      /**
       * The urls that was supplied by each selected item in each column (unpaginated)
       *
       * @property _urlPathUnPaginated
       * @private
       */
      _urlPathUnPaginated: [],

      /**
       * The path of urls (possibly paginated) used to create the current columns based on the urls given by each item & pagination.
       *
       * @property _urlPath
       * @private
       */
      _urlPath: [],

      /**
       * The urls that was requested by the user of this component by calling load.
       *
       * @property _loadUrls
       * @private
       */
      _loadUrls: null,

      /**
       * The flag that tells this component to check for consistency or not in the urls
       * that was requested by the user of this component by calling load.
       *
       * @property _checkUrlsConsistency
       * @private
       */
      _checkLoadUrlsConsistency: null,

      /*
       * CSS classes used by the ColumnBrowser component
       */

      CLASSES: {

          /**
           * The class name of the ColumnBrowser element.
           *
           * @property COLUMNBROWSER
           * @default "yui-columnbrowser"
           */
          COLUMNBROWSER: "yui-columnbrowser",

          /**
           * The class name of the container of the items in the ColumnBrowser.
           *
           * @property COLUMNBROWSER_EL
           * @default "yui-columnbrowser-element"
           */
          COLUMNBROWSER_EL: "yui-columnbrowser-element"
      },


      DEFAULT_ERROR_MSG: 'Data unavailable',

      /*
       * Public methods of the Carousel component
       */


      /**
       * Clears the current columns and loads the following urls in order into columns instead.
       *
       * @param urls An array of hierarchial urls to load. (Note root url SHALL be included)
       * @param checkUrlsConsistency If true it will check that the urlPaths follows a
       *        logical order by making sure that the "next" url is present in the
       *        url attribute in one of "previously" loaded children. If not the
       *        loading will stop.
       */
      load: function ColumnBrowser_load(urls, checkUrlsConsistency)
      {
         // If a request is happening stop it
         this._abortRequests();
         this._removeColumns(0);
         this._urlPathUnPaginated = [];

         this._checkLoadUrlsConsistency = checkUrlsConsistency;
         this._loadUrls = urls;
         if (!this._loadUrls || this._loadUrls.length == 0)
         {
            this._loadUrls = [ this.get("rootUrl") ];
         }
         if (this._loadUrls[0].indexOf(this.get("rootUrl")) != 0)
         {
            throw new Error("The column browsers rootUrl doesn't match the beginning of the first url");
         }
         this._urlPathUnPaginated.push(this.get("rootUrl"));
         if (this._loadUrls.length == 1)
         {
            this.fireEvent(itemSelectEvent, null);
         }
         this._loadFromLoadUrls(null);
      },

      /**
       * Makes sure the column specified by columnIndex is shown
       *
       * @param columnIndex
       */
      showColumn: function ColumnBrowser_showColumn(columnIndex)
      {
         // If a request is happening stop it
         this._abortRequests();

         // Let the carousel display the correct column
         this._carousel.scrollTo(columnIndex, false);
      },


      /**
       * Initialize the ColumnBrowser.
       *
       * @method init
       * @public
       * @param el {HTMLElement | String} The html element that represents
       * the ColumnBrowser container.
       * @param attrs {Object} The set of configuration attributes for
       * creating the ColumnBrowser.
       */
      init: function (el, attrs) {
         var columnBrowser = this,
               elId     = el;

         if (!el) {
            YAHOO.log(el + " is neither an HTML element, nor a string",
                  "error", WidgetName);
            return;
         }

         columnBrowser._hasRendered = false;

         YAHOO.log("Component initialization", WidgetName);

         if (JS.isString(el)) {
            el = Dom.get(el);
         } else if (!el.nodeName) {
            YAHOO.log(el + " is neither an HTML element, nor a string",
                  "error", WidgetName);
            return;
         }

         ColumnBrowser.superclass.init.call(columnBrowser, el, attrs);

         if (el) {
            if (!el.id) {   // in case the HTML element is passed
               el.setAttribute("id", Dom.generateId());
            }
         } else {
            el = columnBrowser._createColumnBrowser(elId);
         }
         elId = el.id;

         columnBrowser.initEvents();

         instances[elId] = { object: columnBrowser };

         // Store for easy access
         columnBrowser._id = columnBrowser.get("id");
         columnBrowser._el = columnBrowser.get("element");

         // Create the carousel that houses the columns
         columnBrowser._setupCarousel();
      },

      /**
       * Initialize the configuration attributes used to create the ColumnBrowser.
       *
       * @method initAttributes
       * @public
       * @param attrs {Object} The set of configuration attributes for
       * creating the ColumnBrowser.
       */
      initAttributes: function (attrs) {
         var columnBrowser = this;

         attrs = attrs || {};
         ColumnBrowser.superclass.initAttributes.call(columnBrowser, attrs);

         /**
          * @attribute animationMethod
          * @description The easing method the Carousel component shall use to animat the movement
          * @default YAHOO.util.Easing.easeOut
          * @type Object - A callback object {fn, scope, obj}
          */
         columnBrowser.setAttributeConfig("animationMethod", {
            validator : JS.isFunction,
            value     : attrs.animationMethod || YAHOO.util.Easing.easeOut
         });

         /**
          * @attribute animationSpeed
          * @description The time in seconds that the animation shall take to move the columns
          * @default 0.25
          * @type Number
          */
         columnBrowser.setAttributeConfig("animationSpeed", {
            validator : JS.isNumber,
            value     : attrs.animationSpeed || 0.25
         });

         /**
          * @attribute columnInfoBuilder
          * @description Must  be provided by the user of this component to take a custom data response and
          * use that information to create a columnInfo object. 
          * @default null
          * @type Object - A callback object {fn, scope, obj}
          */
         columnBrowser.setAttributeConfig("columnInfoBuilder", {
            validator : JS.isObject,
            value     : attrs.columnInfoBuilder || null
         });

         /**
          * @attribute pagination
          * @description Can be provided if the response shall be paginated.
          * @default null
          * @type Object { rowsPerPage: int, rowsPerPageParam: String, recordOffsetParam: String }
          */
         columnBrowser.setAttributeConfig("pagination", {
            validator : JS.isObject,
            value     : attrs.pagination || null
         });

         /**
          * @attribute rootUrl
          * @description The required (unpaginated) url used to load the root column.
          * @type String
          */
         columnBrowser.setAttributeConfig("rootUrl", {
            validator : JS.isString,
            value     : attrs.rootUrl
         });

         /**
          * @attribute columnInfos
          * @description An array of the columnInfo objects that contains the data
          * that was used to build the columns
          * @type Number
          */
         columnBrowser.setAttributeConfig("columnInfos", {
            validator : JS.isArray,
            readOnly  : true,
            value     : []
         });

         /**
          * @attribute numVisible
          * @description The number of columns that shall be visible at the same time.
          * @default 3
          * @type Number
          */
         columnBrowser.setAttributeConfig("numVisible", {
            validator : JS.isNumber,
            value     : attrs.numVisible || 3
         });

         /**
          * @attribute requestTimeout
          * @description The time in milliseconds to wait before a conenction attempt is dropped.
          * @default 30000
          * @type Number
          */
         columnBrowser.setAttributeConfig("requestTimeout", {
            validator : JS.isNumber,
            value     : attrs.requestTimeout || 30000
         });

         /**
          * @attribute url
          * @description The url to the root column
          * follow scrolling in the ColumnBrowser.          
          * @type String
          */
         columnBrowser.setAttributeConfig("url", {
            validator : JS.isString,
            value     : attrs.url
         });

         /**
          * @attribute urlPath
          * @description An array describing the path of urls that was requested
          * to populate the ColumnBrowser in its current state
          * @default []
          * @type Array
          */
         columnBrowser.setAttributeConfig("urlPath", {
            readOnly : true,
            getter: this._getUrlPath,
            setter: this._setUrlPath,
            value     : attrs.urlPath || []
         });


      },

      /**                                           
       * Initialize and bind the event handlers.
       *
       * @method initEvents
       * @public
       */
      initEvents: function () {
          var columnBrowser = this,
              cssClass = columnBrowser.CLASSES;
          
      },

      /**
       * Return the string representation of the Carousel.
       *
       * @method toString
       * @public
       * @return {String}
       */
      toString: function () {
          return WidgetName + (this.get ? " (#" + this.get("id") + ")" : "");
      },


      /**
       * Protected methods of the ColumnBrowser component
       */

      /**
       * Create the ColumnBrowser element if it doesn't exist.
       *
       * @method _createColumnBrowser
       * @param elId {String} The id of the element to be created
       * @protected
       */
      _createColumnBrowser: function (elId) {
         var carousel = this,
               cssClass = carousel.CLASSES,
               el       = Dom.get(elId);

         if (!el) {
            el = createElement("DIV", {
               className : cssClass.COLUMNBROWSER,
               id        : elId
            });
         }

         return el;
      },

      /**
       * Will create a carousel to use for scrolling back and forth between the columns
       *
       * @protected
       */
      _setupCarousel: function()
      {
         var columnBrowser = this;

         // Create the carousel with an animation
         columnBrowser._carousel = new YAHOO.widget.Carousel(columnBrowser._id,
         {
            animation: {
               speed:    columnBrowser.get("animationSpeed"),
               effect:   columnBrowser.get("animationMethod")
            },
            numVisible:  columnBrowser.get("numVisible"),
            carouselEl: "UL"
         });

         // Make sure we know when the animation is finished so we know the carousel isn't moving
         columnBrowser._carousel.on("afterScroll", columnBrowser._animationCompleteHandler, null, columnBrowser);

         /**
          * After a column has been added we must add click, hover listeners to all the items
          * (this also where the requests in load are chained)
          */
         columnBrowser._carousel.on("itemAdded", columnBrowser._addClickListeners, null, columnBrowser);

         // Display the carousel
         columnBrowser._carousel.render();
         columnBrowser._carousel.show();

         columnBrowser.carouselElem = Dom.get(columnBrowser._carousel.get("id"));
         columnBrowser.carouselList = Dom.getElementsByClassName("yui-carousel-element", "ul", columnBrowser.carouselElem)[0];

         // Add the column browsers own class
         Dom.addClass(columnBrowser._id, columnBrowser.CLASSES.COLUMNBROWSER);
         if (columnBrowser._carousel.get('numVisible') > 1)
         {
            Dom.addClass(columnBrowser._id, "yui-columnbrowser-multiple");
         }

         // Add escape listeners
         var notOpera = (navigator.userAgent.match(/opera/i) == null);
         var kl = new YAHOO.util.KeyListener
               ( columnBrowser._el,
               { ctrl: notOpera, keys: [37, 38, 39, 40] },
               { fn: this._handleKeypress,
                  scope: this,
                  correctScope: true
               });
         kl.enable();
      },

      /**
       * Asks the service specified by url for columnInfo objects.
       *
       * @param url Url to the service that hosts the columnInfo data
       * @param itemInfo Object that will be passed to the event handlers
       * @param recordOffset (Optional) If pagination is used the parameter will be used as the value for the url parameter named after pagination.recordOffsetParam
       * @protected
       */
      _doRequest: function(url, itemInfo, recordOffset)
      {
         var configuredUrl = url,
            pagination = this.get("pagination");
         if (pagination)
         {
            var separators = configuredUrl.indexOf('?') != -1 ? ['&','&'] : ['?','&'];
            if (configuredUrl.indexOf(pagination.rowsPerPageParam + '=') < 0)
            {
               configuredUrl += separators.pop() + pagination.rowsPerPageParam + '=' + pagination.rowsPerPage;
            }
            if (configuredUrl.indexOf(pagination.recordOffsetParam + '=') < 0)
            {
               configuredUrl += separators.pop() + pagination.recordOffsetParam + '=' + (recordOffset || 0);
            }
         }
         var callback = {
            success:  this._handleSuccess,
            failure:  this._handleFailure,
            scope:    this,
            timeout:  this.get("requestTimeout"),
            argument:
            {
               itemInfo: itemInfo,
               errorMessage: 'Ajax request failed',
               url: configuredUrl
            }
         };
         //this._abortRequests();
         this._request = Con.asyncRequest('GET', configuredUrl, callback);
      },

      /**
       * Aborts the request
       *
       * @protected
       */
      _abortRequests: function()
      {
         if (this._request && Con.isCallInProgress(this._request))
            Con.abort(this._request);
         this._loadUrls = null;
         this._checkLoadUrlsConsistency = false;
      },

      /**
       * Called when the request is finished
       *
       * @param serverResponse
       * @protected
       */
      _handleSuccess: function(serverResponse)
      {
         var columnInfo;
         try {
            // Let the user of the component transform his data to a columnInfo object
            var columnInfoBuilder = this.get("columnInfoBuilder");
            if(columnInfoBuilder)
            {
               columnInfo = columnInfoBuilder.fn.call(columnInfoBuilder.scope ? columnInfoBuilder.scope : this, serverResponse, serverResponse.argument.itemInfo);
            }
         } catch (e) {
            serverResponse.argument.errorMessage = 'Data parsing failed';
            this._handleFailure(serverResponse);
            return;
         }
         this._addColumn(columnInfo, serverResponse.argument.url);
      },

      /**
       * Will handle failures by adding a column with the error message in the header and body
       *
       * @param serverResponse
       * @param suppressEvent
       * @protected
       */
      _handleFailure: function(serverResponse, suppressEvent)
      {
         var errorColumn = {
            header: {
               label:  serverResponse.argument.errorMessage || this.DEFAULT_ERROR_MSG
            },
            body: {
               items: [ { label: serverResponse.argument.errorMessage || this.DEFAULT_ERROR_MSG } ]
            }
         };

         this._addColumn(errorColumn, serverResponse.argument.url, true);
      },

      /**
       * Add a column based on the columnInfo
       *
       * @param columnInfo
       * @param url The url from where the columInfo was requested
       * @param handlingFailure {boolean} True if the function is attempting to add a failure message.
       * @protected
       */
      _addColumn: function(columnInfo, url, handlingFailure) {
         var paneContent;
         try {
            // Create the column from the columnInfo object
            paneContent = this._createColumnEl(columnInfo);
         } catch (e) {
            if (!handlingFailure) {
               this._handleFailure({ argument: { errorMessage: 'Malformed column data' } });
            }
            return;
         }

         // Add the column to the carousel
         if(this._carousel.addItem(paneContent, undefined)) {
            // Update the urlPath attribute
            this._urlPath.push(url);
            this.set("urlPath", this._urlPath);

            // It was added alright now lets see if the carousel needs to scroll or not
            var columnIndex = this._carousel.get("numItems") - 1;
            if (this._shouldScrollNext(this._carousel.getElementForItem(columnIndex))) {

               // Yes it did, scroll it
               this._carousel.scrollForward();
               this._isMoving = true;
            } else {
               if (columnIndex >= 0) {
                  // todo set focus to newly created pane
                  var pane = Dom.get(this._carousel.getItem(columnIndex).id);
                  this._focus(pane);
               }
            }
         }
      },

      /**
       * Create the column DOM elements based on info from the columnInfo object
       *
       * @param columnInfo
       * @protected
       */
      _createColumnEl: function(columnInfo) {
         // The index of the column will be created in
         var columnIndex = this._carousel.get("numItems");
         var columnInfos = this.get("columnInfos");

         /**
          * Cannot add an event lister since the Carousel component will
          * use an innerHTML to add the content.
          * We will add those event listeners after the column has been added,
          * to keep the info about the items it will be stored in a hash
          */
         columnInfos[columnIndex] = columnInfo;

         // Body
         var body = document.createElement('div');
         if(columnInfo.body)
         {
            Dom.addClass(body, "yui-columnbrowser-column-body");
            var items = columnInfo.body.items || [];
            for (var i = 0; i < items.length; i++) {
               columnInfos[columnIndex].body.items[i].columnIndex = columnIndex;
               columnInfos[columnIndex].body.items[i].itemIndex = i;
               body.appendChild(this._createItemEl(columnIndex, i, items[i]));
            }
         }

         // Header
         var header = document.createElement('div');
         if(columnInfo.header) {
            Dom.addClass(header, "yui-columnbrowser-column-header");
            if(columnInfo.header.label) {
               // Create label
               var span = document.createElement('span');
               Dom.addClass(buttons, "yui-columnbrowser-column-header-label");
               span.appendChild(document.createTextNode(columnInfo.header.label.text));
               header.appendChild(span);
            }
            if(columnInfo.header.buttons) {
               // Create buttons
               var buttons = document.createElement('span');
               Dom.addClass(buttons, "yui-columnbrowser-column-header-buttons");
               for(var i = 0; i < columnInfo.header.buttons.length; i++)
               {
                  var button = document.createElement("span");
                  var b = columnInfo.header.buttons[i];
                  button.setAttribute("title", b.title);
                  Dom.addClass(button, b.cssClass);
                  button.innerHTML = "&nbsp;";
                  Dom.setStyle(button, "width", "10px");
                  Dom.setStyle(button, "height", "10px");
                  buttons.appendChild(button);
               }
               header.appendChild(buttons);
            }

         }

         // Footer
         var footer = document.createElement('div');
         Dom.addClass(footer, "yui-columnbrowser-column-footer");
         if(columnInfo.footer && !this.get("pagination"))
         {
            var span = document.createElement('span');
            footer.appendChild(span);
            span.appendChild(document.createTextNode(columnInfo.footer.label));
         }

         // Create column, Note! The Carousel will use the innerHTML instead of the node itself
         var column = document.createElement("div");
         Dom.addClass(column, "yui-columnbrowser-column");
         column.appendChild(header);
         column.appendChild(body);
         column.appendChild(footer);
         return column;
      },

      /**
       * Create a columnItem DOM element
       *
       * @param columnIndex the column the item will get created in
       * @param itemIndex the position this item will have in its column
       * @param itemInfo Describes the item
       * @protected
       */
      _createItemEl: function(columnIndex, itemIndex, itemInfo) {
         // Create item
         var item = document.createElement('a');
         Dom.addClass(item, "yui-columnbrowser-item");
         if(itemInfo.cssClass)
         {
            Dom.addClass(item, itemInfo.cssClass);
         }

         // Create label
         var label = document.createElement('span');
         label.appendChild(document.createTextNode(itemInfo['label']));
         Dom.addClass(label, "yui-columnbrowser-item-label");
         item.appendChild(label);

         item.next = itemInfo['next'];
         if (itemInfo['next'] || (itemInfo['hasNext']))
            Dom.addClass(item, 'yui-columnbrowser-column-has-next');

         // Create buttons
         var buttons = document.createElement('span');
         Dom.addClass(buttons, "yui-columnbrowser-item-buttons");
         for(var i = 0; i < itemInfo.buttons.length; i++)
         {
            var button = document.createElement("span");
            var b = itemInfo.buttons[i];
            button.setAttribute("title", b.title);
            Dom.addClass(button, b.cssClass);
            button.innerHTML = "&nbsp;";
            Dom.setStyle(button, "width", "10px");
            Dom.setStyle(button, "height", "10px");
            buttons.appendChild(button);
         }
         item.appendChild(buttons);

         return item;
      },

      /**
       * Enable key navigation
       *
       * TODO Make this work
       *
       * @param type
       * @param args
       * @param me
       * @protected
       */
      _handleKeypress: function(type, args, me)
      {
         var key = args[0];
         var evt = args[1];
         var target = Event.getTarget(evt);
         var pane = target;
         while (!Dom.hasClass(pane, 'yui-columnbrowser-column') &&
                !Dom.hasClass(pane, 'yui-columnbrowser-noncolumn'))
         {
            pane = pane.parentNode;
         }
         var isNonColumn = Dom.hasClass(pane, 'yui-columnbrowser-noncolumn');
         if (this._isMoving || isNonColumn) {
            Event.stopEvent(evt);
            return;
         }
         if (target.tagName.toLowerCase() != 'a') {
            var items = this._getNodes(this.carouselList.lastChild,
                  this._getItems);
            items[0].focus();
            return;
         }
         switch (key) {
            case 37: // left
               if (this._shouldScrollPrev(pane)) {
                  this._prev(evt);
                  me.carousel.scrollBackward();  // Used to be scrollPrev()
                  this._isMoving = true;
               } else {
                  var prevPane = pane.previousSibling;
                  if (prevPane)
                     this._focus(prevPane);
               }
               break;
            case 38: // up
               if (target.previousSibling)
                  target.previousSibling.focus();
               break;
            case 39: // right
               this._next(evt);
               break;
            case 40: // down
               if (target.nextSibling)
                  target.nextSibling.focus();
               break;
         }
         Event.stopEvent(evt);
      },

      /**
       * Decides if the carousel needs to scroll forward to display the paneEl element
       *
       * @param columnEl The element that shall be visible
       * @protected
       */
      _shouldScrollNext: function(columnEl) {
         var items = this._carousel.getVisibleItems();
         var i;
         for(i = 0; items && i < items.length; i++)
         {
            if(items[i] && items[i].getAttribute("id") == columnEl.getAttribute("id"))
            {
               return false;
            }
         }
         return true;
      },

      /**
       * Decides if the carousel needs to scroll backwards to display the columnEl element
       *
       * @param columnEl The element that shall be visible
       * @protected
       */
      _shouldScrollPrev: function(columnEl) {
         var panes = this._getNodes(this.carouselList,
               this._isChildElement);
         var i = 0;
         for ( ; i < panes.length; i++) {
            if (columnEl == panes[i]) break;
         }
         return i > 0;
      },

      /**
       * Called when a user wants to see whats inside an item
       *
       * @param e A user triggered event
       * @param itemInfo
       * @protected
       */
      _next: function(e, itemInfo) {
         var result = false;

         // Ignore the interaction if the carousel is moving
         if (this._isMoving) {
            Event.stopEvent(e);
            return;
         }

         // Make sure we get the actual item element
         var target = Event.getTarget(e);
         if (target.tagName.toLowerCase() == 'span') {
            target = target.parentNode;
         }

         // Remove columns until this column
         this._removeColumns(itemInfo.columnIndex + 1);

         // Get some info about the item that was clicked
         var url = itemInfo.url;
         this._urlPathUnPaginated.push(url);
         var next = itemInfo.next;

         // Highlight the item
         this._highlight(target);
         
         // Decide how to display the child column items to this the item
         if (next) {
            // The column to th right has already been loaded form the server, use that information
            this._addColumn(next, url);
         }
         else if (url) {
            // Call the server to get data about the column to display
            this._doRequest(url, itemInfo);
         }
         else {
            // It is a leaf, create an empty column
            var columnInfoBuilder = this.get("columnInfoBuilder");
            if(columnInfoBuilder) {
               var columnInfo = columnInfoBuilder.fn.call(columnInfoBuilder.scope ? columnInfoBuilder.scope : this, null, itemInfo);
               if(columnInfo) {
                  this._addColumn(columnInfo, null);
               }
            }
         }

         // Tell users of this component that the item has been clicked
         this.fireEvent(itemSelectEvent, itemInfo);
         Event.stopEvent(e);
         return result;
      },

      /**
       * Remove columns from the right to this index
       *
       * @param columnIndex       
       */
      _removeColumns: function(columnIndex) {
         // Remove from the carousel
         var lastIndex = this._carousel.get("numItems") - 1;
         while (lastIndex >= columnIndex) {
            this._carousel.removeItem(lastIndex);
            lastIndex = this._carousel.get("numItems") - 1;
            this._urlPath.pop();
            this._urlPathUnPaginated.pop();
         }
         // Update the urlPath attribute
         this.set("urlPath", this._urlPath);
      },

      /**
       * Hightlight the item
       *
       * @param el The item element
       * @protected
       */
      _highlight: function(el) {
         var items = this._getNodes(el.parentNode, this._isChildElement);
         for (var i = 0; i < items.length; i++)
            Dom.removeClass(items[i], 'yui-columnbrowser-item-selected');
         Dom.addClass(el, 'yui-columnbrowser-item-selected');
      },

      /**
       * Set the focus on the item
       *
       * @param el The item element
       * @protected
       */
      _focus: function(el) {
         if (Dom.hasClass(el, 'yui-columnbrowser-noncolumn')) {
            if (el.firstChild.focus) el.firstChild.focus();
            else el.focus();
         } else {
            var items = this._getNodes(el, this._getItems);
            for (var i = 0; i < items.length; i++) {
               if (Dom.hasClass(items[i], 'yui-columnbrowser-item-selected')) {
                  items[i].focus();
                  return;
               }
            }
            if (items[0] && Alfresco.util.isVisible(items[0]))
            {
               items[0].focus();
            }
         }
      },

      /**
       * Called when the carousel is finished scrolling/moving the columns.
       * Sets _isMoving to false so we can continue and interact with the user.
       *
       * @param type
       * @param args
       * @param me
       * @protected
       */
      _animationCompleteHandler: function(type, args, me) {
         this._isMoving = false;
      },

      /**
       * Called after a column has been added to the carousel so we can add event
       * listeners to the header, body's item and the footer.
       *
       * @param e
       * @protected
       */
      _addClickListeners: function(e) {
         var columnIndex = this._carousel.get("numItems") - 1;
         var columnInfo = this.get("columnInfos")[columnIndex];
         var column = Dom.get(this._carousel.getItem(columnIndex).id);

         // Add click listener for header label
         var labels = Dom.getElementsByClassName("yui-columnbrowser-column-header-label", "span", column);
         var label = labels ? labels[0] : null;
         if(label && columnInfo.header && columnInfo.header.label && columnInfo.header.label.click) {
            var click = columnInfo.header.label;
            YAHOO.util.Event.addListener(label, "click", function(e, callback)
            {
               callback.fn.call(callback.scope, callback.obj);
            }, { fn: click.fn, scope: click.scope ? click.scope : this, obj: columnInfo}, this);
         }

         // Add click listeners for header buttons
         var buttons = Dom.getElementsByClassName("yui-columnbrowser-column-header-buttons", "span", column);
         buttons = buttons && buttons.length > 0 ? buttons[0] : null;
         if(buttons && columnInfo.header && columnInfo.header.buttons) {
            for(var i = 0; i < columnInfo.header.buttons.length; i++) {
               var b = columnInfo.header.buttons[i];
               if(b.click)
               {
                  // Find all buttons and add a click listener to each
                  var button = Dom.getElementsByClassName(b.cssClass, "span", buttons);
                  YAHOO.util.Event.addListener(button, "click", function(e, callback)
                  {
                     callback.fn.call(callback.scope, callback.obj);
                  },
                  {
                     fn: b.click.fn,
                     scope: b.click.scope ? b.click.scope : this,
                     obj: columnInfo
                  }, this);
               }
            }
         }

         // Add css classes mouseover
         YAHOO.util.Event.addListener(label, "mouseover", function()
         {
            Dom.addClass(this, "yui-columnbrowser-column-header-label-active");
         });
         // Remove css classes on mouseout
         YAHOO.util.Event.addListener(label, "mouseout", function()
         {
            Dom.removeClass(this, "yui-columnbrowser-column-header-label-active");
         });

         // Add listeners for items
         var items = column.getElementsByTagName("a");
         for(var i = 0; i < items.length; i++) {
            var itemInfo = columnInfo.body.items[i];
            var item = items[i];
            // Add click listener
            YAHOO.util.Event.addListener(item, "click", function(e, itemInfo)
            {
               return this._next(e, itemInfo);
            }, itemInfo, this);

            // Add css classes mouseover
            YAHOO.util.Event.addListener(item, "mouseover", function()
            {
               Dom.addClass(this, "yui-columnbrowser-item-active");
               var buttons = Dom.getElementsByClassName("yui-columnbrowser-item-buttons", "span", this)[0];
               Dom.addClass(buttons, "yui-columnbrowser-item-buttons-active");
            });
            // Remove css classes on mouseout
            YAHOO.util.Event.addListener(item, "mouseout", function()
            {
               Dom.removeClass(this, "yui-columnbrowser-item-active");
               var buttons = Dom.getElementsByClassName("yui-columnbrowser-item-buttons", "span", this)[0];
               Dom.removeClass(buttons, "yui-columnbrowser-item-buttons-active");
            });
            // Add button click listeners
            var buttons = Dom.getElementsByClassName("yui-columnbrowser-item-buttons", "span", item);
            buttons = buttons && buttons.length > 0 ? buttons[0] : null;
            if(buttons && itemInfo.buttons) {
               for(var j = 0; j < itemInfo.buttons.length; j++)
               {
                  var b = itemInfo.buttons[j];
                  if(b.click)
                  {
                     // Find all buttons and add a click listener to each
                     var button = Dom.getElementsByClassName(b.cssClass, "span", buttons);
                     YAHOO.util.Event.addListener(button, "click", function(e, callback)
                     {
                        // Stop the event from bubbling so we don't open the columns children
                        YAHOO.util.Event.stopEvent(e);
                        callback.fn.call(callback.scope, callback.obj);
                     },
                     {
                        fn: b.click.fn,
                        scope: b.click.scope ? b.click.scope : this,
                        obj: { obj: b.click.obj, itemInfo: itemInfo, columnInfo: columnInfo }
                     }, this);
                  }
               }
            }

         }

         // Footer
         var footer = Dom.getElementsByClassName("yui-columnbrowser-column-footer", "div", column)[0],
            paginationConfig = this.get("pagination"),
            columnInfoPagination = columnInfo.pagination;
         if (paginationConfig && columnInfoPagination)
         {
            var span = document.createElement('span');
            footer.appendChild(span);
            var p = new YAHOO.widget.Paginator({
               rowsPerPage  : paginationConfig.rowsPerPage,
               totalRecords : columnInfoPagination.totalRecords || 0,
               recordOffset: columnInfoPagination.recordOffset || 0,
               containers   : span,
               firstPageLinkLabel : paginationConfig.firstPageLinkLabel || "<<",
               lastPageLinkLabel : paginationConfig.lastPageLinkLabel || ">>",
               previousPageLinkLabel : paginationConfig.previousPageLinkLabel || "<",
               nextPageLinkLabel : paginationConfig.nextPageLinkLabel || ">",
               pageReportTemplate : paginationConfig.pageReportTemplate || "( {currentPage} of {totalPages} )",
               template: paginationConfig.template || "{FirstPageLink} {PreviousPageLink} {CurrentPageReport} {NextPageLink} {LastPageLink}"
            });
            var onPaginateColumn = function (newState, obj)
            {

               // Save upaginated url for this column, remove column (and url from the list), and restore the url
               var url = this._urlPathUnPaginated[obj.columnIndex];
               this._removeColumns(obj.columnIndex);
               this._urlPathUnPaginated.push(url);

               var columnInfo = obj.columnInfo;
               this._doRequest(url, columnInfo.parent, newState.recordOffset);

               YAHOO.util.Event.stopEvent(e);
            };
            p.subscribe('changeRequest', onPaginateColumn, { columnInfo: columnInfo, columnIndex: columnIndex }, this);
            p.render();
         }

         // If this column was created from a load() call make sure the rest of the urls are requested
         this._loadFromLoadUrls(columnInfo);
      },


      /**
       * Called after a click listeners has been added for the column so we can load more
       * columns if it was requested in the load method.
       *
       * @param columnInfo
       * @protected
       */
      _loadFromLoadUrls: function(columnInfo) {
         var columnIndex = this._carousel.get("numItems") - 1,
            url = this._loadUrls ? this._loadUrls.shift() : null;
         if(url) {
            // Decide if children shall be loaded or not
            var loadUrl = this._checkLoadUrlsConsistency ? false : true;
            var itemInfo = null;
            var items = columnInfo && columnInfo.body && columnInfo.body.items ? columnInfo.body.items : [];
            for(var i = 0;  i < items.length; i++) {
               if(url.indexOf(items[i].url) == 0) {
                  this._urlPathUnPaginated.push(items[i].url);
                  loadUrl = true;
                  itemInfo = items[i];

                  // Highligt parent
                  var column = Dom.get(this._carousel.getItem(columnIndex).id);
                  var itemEl = column.getElementsByTagName("a")[i];
                  this._highlight(itemEl);

                  // Tell other components that the item has been select
                  this.fireEvent(itemSelectEvent, itemInfo);
                  break;
               }
            }
            if(loadUrl || columnIndex == -1) {
               // Load if it is the root or if the url is ok/ignored
               this._doRequest(url, itemInfo);
            }
            else {
               this._abortRequests();
            }
         }
         else {
            this._abortRequests();
         }
      },

      /**
       * Helper method for finding DOM nodes under a root node
       *
       * @param root
       * @param filter
       * @protected
       */
      _getNodes: function(root, filter) {
         var node = root;
         var nodes = [];
         var next;
         var f = filter || function() { return true; }
         while (node != null) {
            if (node.hasChildNodes())
               node = node.firstChild;
            else if (node != root && null != (next = node.nextSibling))
               node = next;
            else {
               next = null;
               for ( ; node != root; node = node.parentNode) {
                  next = node.nextSibling;
                  if (next != null) break;
               }
               node = next;
            }
            if (node != null && f(node, root))
               nodes.push(node);
         }
         return nodes;
      },

      /**
       * Returns true if node is child element to root
       *
       * @param node
       * @param root
       * @protected
       */
      _isChildElement: function(node, root) {
         return (node.nodeType == 1 && node.parentNode == root);
      },

      /**
       * Gets column items under a column
       *
       * @param node
       * @protected
       */
      _getItems: function(node) {
         return (node.nodeType == 1 && node.tagName.toLowerCase() == 'a');
      },

      /**
       * Clones the array when before the urlPath is returned so it can't be modified.
       *
       * @method _getUrlPath
       * @param name the attribute name ("urlPath")
       * @return {Array} The returned/cloned value for/of urlPath
       * @protected
       */
      _getUrlPath: function (name) {
         var copy = [];
         for(var i = 0; this._urlPath && i < this._urlPath.length; i++) {
            copy.push(this._urlPath[i]);
         }
         return copy;
      },

      /**
       * Clones the array before the urlPath is is set so it can't be modified
       *
       * @method _setUrlPath
       * @param val {Array} The value for urlPath
       * @param name the attribute name ("urlPath")
       * @return {Array} The returned/cloned value for/of urlPath
       * @protected
       */
      _setUrlPath: function (val, name) {
         var copy = [];
         for(var i = 0; val && i < val.length; i++) {
            copy.push(val[i]);
         }
         this._urlPath = copy;
         this._urlPathUnPaginated = []; // unpaginated url path is now stale, when load() is called it will be populated.  
         return val;
      }


   });

   /**
    * A bread crumb component to the ColumnBrowser
    *
    * TODO: Move this to become a part of the column browser instead
    *
    * Not documented since it shall be refactored
    */

   YAHOO.extension.ColumnBrowserBreadCrumb = function(id, cfg)
   {
      this._init(id, cfg);
   };

   YAHOO.extension.ColumnBrowserBreadCrumb.prototype =
   {

      _init: function(id, cfg)
      {
         this.id = id;
         this.cfg = cfg;
         this.columnBrowser = this.cfg.columnBrowser;
         this.root = this.cfg.root || "";
         this.separator = this.cfg.separator || " > ";

         this.breadcrumbsEl = document.getElementById(this.id);
         Dom.addClass(this.breadcrumbsEl, "yui-columnbrowser-breadcrumb");

         // Listen for ColumnNav events
         var me = this;
         this.columnBrowser.on("itemSelect", function (itemInfo)
         {            
            if (itemInfo)
            {
               me._addBreadCrumbItem(itemInfo.label, itemInfo.columnIndex, itemInfo.cssClass);
            }
            else
            {
               me._addBreadCrumbItem(null, 0, null);
            }
         }, null, this);

         this._addBreadCrumbRoot(this.root);
      },

      _addBreadCrumbRoot: function (label) {
         this._addBreadCrumb(label, -1, "yui-columnbrowser-breadcrumb-root", null);
      },

      _addBreadCrumbItem: function (label, columnIndex, customCssClass) {
         this._addBreadCrumb(label, columnIndex, "yui-columnbrowser-breadcrumb-item", customCssClass);
      },

      _addBreadCrumb: function (label, columnIndex, cssClass, customCssClass) {
         // Remove previous breadcrumbs
         var items = Dom.getElementsByClassName(cssClass, "span", this.breadcrumbsEl);
         if (items && items.length >= columnIndex && columnIndex >= 0) {
            var numToRemove = (items.length - columnIndex);
            for (var i = 0; i < numToRemove; i++) {
               this.breadcrumbsEl.removeChild(this.breadcrumbsEl.lastChild);
            }
         }

         // Create elements
         if (label)
         {
            var item = document.createElement("span");
            Dom.addClass(item, cssClass);
            if(customCssClass)
            {
               Dom.addClass(item, customCssClass);
            }
            var items = Dom.getElementsByClassName(cssClass, "span", this.breadcrumbsEl);
            for(var i = 0; i < items.length; i++)
            {
               Dom.removeClass(items[i], "yui-columnbrowser-breadcrumb-item-last");
   
            }
            Dom.addClass(item, "yui-columnbrowser-breadcrumb-item-last");
   
            var text = document.createElement("span");
            text.appendChild(document.createTextNode(label));
            YAHOO.util.Event.addListener(text, "click", this._onBreadCrumbClick, { columnIndex: columnIndex }, this);
            Dom.addClass(text, "yui-columnbrowser-breadcrumb-item-text");
            // Add css classes mouseover
            YAHOO.util.Event.addListener(text, "mouseover", function()
            {
               Dom.addClass(this, "yui-columnbrowser-breadcrumb-item-text-active");
            });
            // Remove css classes on mouseout
            YAHOO.util.Event.addListener(text, "mouseout", function()         
            {
               Dom.removeClass(this, "yui-columnbrowser-breadcrumb-item-text-active");
            });
   
            item.appendChild(text);
   
            var separator = document.createElement("span");
            separator.appendChild(document.createTextNode(this.separator));
            Dom.addClass(separator, "yui-columnbrowser-breadcrumb-item-separator");
            item.appendChild(separator);
   
            this.breadcrumbsEl.appendChild(item);
         }
      },

      _onBreadCrumbClick: function (e, itemInfo) {
         this.columnBrowser.showColumn(itemInfo.columnIndex == -1 ? 0 : itemInfo.columnIndex, itemInfo.itemIndex);
      }


   };

})();
//YAHOO.register("columnbrowser", YAHOO.extension.ColumnBrowser, {version: "2.7.0", build: "1799"});