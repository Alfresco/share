/**
 * Patch to Menu to allow empty groups to remain in the menu structure.
 * Required by: Sites dynamic drop-down menu.
 * Patches: YUI 2.6.0 to 2.9.0
 * Escalated: Yes, but closed as "by design"
 */
(function()
{
   var Lang = YAHOO.lang,
      Dom = YAHOO.util.Dom,
      _FIRST_OF_TYPE = "first-of-type";

   YAHOO.widget.Menu.prototype._removeItemFromGroupByIndex = function (p_nGroupIndex, p_nItemIndex, p_keepEmptyGroup) {

       var nGroupIndex = Lang.isNumber(p_nGroupIndex) ? p_nGroupIndex : 0,
           aGroup = this._getItemGroup(nGroupIndex),
           aArray,
           oItem,
           oUL;

       if (aGroup) {

           aArray = aGroup.splice(p_nItemIndex, 1);
           oItem = aArray[0];

           if (oItem) {

               // Update the index and className properties of each member
               this._updateItemProperties(nGroupIndex);

               if (aGroup.length === 0 && !p_keepEmptyGroup) {

                   // Remove the UL
                   oUL = this._aListElements[nGroupIndex];

                   if (oUL && oUL.parentNode) {
                       oUL.parentNode.removeChild(oUL);
                   }

                   // Remove the group from the array of items
                   this._aItemGroups.splice(nGroupIndex, 1);

                   // Remove the UL from the array of ULs
                   this._aListElements.splice(nGroupIndex, 1);

                   /*
                        Assign the "first-of-type" class to the new first UL
                        in the collection
                   */
                   oUL = this._aListElements[0];

                   if (oUL) {
                       Dom.addClass(oUL, _FIRST_OF_TYPE);
                   }
               }

               this.itemRemovedEvent.fire(oItem);
               this.changeContentEvent.fire();
           }
       }

      // Return a reference to the item that was removed
      return oItem;
   };

   YAHOO.widget.Menu.prototype._removeItemFromGroupByValue = function (p_nGroupIndex, p_oItem, p_keepEmptyGroup) {

       var aGroup = this._getItemGroup(p_nGroupIndex),
           nItems,
           nItemIndex,
           returnVal,
           i;

       if (aGroup) {
           nItems = aGroup.length;
           nItemIndex = -1;

           if (nItems > 0) {
               i = nItems-1;
               do {
                   if (aGroup[i] == p_oItem) {
                       nItemIndex = i;
                       break;
                   }
               }
               while (i--);

               if (nItemIndex > -1) {
                   returnVal = this._removeItemFromGroupByIndex(p_nGroupIndex, nItemIndex, p_keepEmptyGroup);
               }
           }
       }
       return returnVal;
   };

   YAHOO.widget.Menu.prototype.removeItem = function (p_oObject, p_nGroupIndex, p_keepEmptyGroup) {
       var oItem,
          returnVal;

       if (!Lang.isUndefined(p_oObject)) {
           if (p_oObject instanceof YAHOO.widget.MenuItem) {
               oItem = this._removeItemFromGroupByValue(p_nGroupIndex, p_oObject, p_keepEmptyGroup);
           }
           else if (Lang.isNumber(p_oObject)) {
               oItem = this._removeItemFromGroupByIndex(p_nGroupIndex, p_oObject, p_keepEmptyGroup);
           }

           if (oItem) {
               oItem.destroy();
               returnVal = oItem;
           }
       }

      return returnVal;
   };
})();

/**
 * Patch to Container to prevent IE6 trying to set properties on elements that have been removed from the DOM.
 * This function is called via a setTimer(), so this patch fixes a race condition.
 * Required by: Document List "Loading Document Library..." pop-up.
 * Patches: YUI 2.7.0 to 2.9.0
 */
(function()
{
   /**
   * Adjusts the size of the shadow based on the size of the element.
   * @method sizeUnderlay
   */
   YAHOO.widget.Panel.prototype.sizeUnderlay = function()
   {
       var oUnderlay = this.underlay,
           oElement;

       if (oUnderlay) {
           oElement = this.element;
           if (oElement) {
              oUnderlay.style.width = oElement.offsetWidth + "px";
              oUnderlay.style.height = oElement.offsetHeight + "px";
           }
       }
   };
})();


(function()
{
   /**
    * Drag drop support for ipad (safari) & android (default browser & chrome) making yui's drag n drop classes work out of the box.
    *
    * The trick is to:
    * - Stop listening for mouse events: "mousedown", "mousemove" & "mouseup"
    * - Start listening to touch events: "touchstart", "touchmove" & "touchend"
    * - Make sure all events have with pageX & pageY attributes set so they can be treated as a "mouse" event.
    *
    * Note! Assumes the following when invoked:
    * - the YAHOO.util.DragDropMgr && YAHOO.util.DragDrop classes have been loaded
    * - the YAHOO.util.DragDropMgr have been initialized
    */
   if ((YAHOO.env.ua.ipad || YAHOO.env.ua.android) && YAHOO.util.DragDropMgr && YAHOO.util.DragDrop)
   {
      var Event = YAHOO.util.Event;

      // Fake an object that pretends to be an event so we can set it's pageX & pageY coords
      var createMouseEvent = function(e, preventDefault, stopPropagation)
      {
         var event = {
            type: e.type,
            target: YAHOO.util.Event.getTarget(e),
            pageX: e.pageX,
            pageY: e.pageY,
            which: e.which
         };

         // Make sure the event can stop bubbling
         var orgEvent = e,
            pd = YAHOO.lang.isBoolean(preventDefault) ? preventDefault : true,
            sp = YAHOO.lang.isBoolean(stopPropagation) ? stopPropagation : true;
         if (e.preventDefault)
         {
            event.preventDefault = function()
            {
               if (pd)
               {
                  orgEvent.preventDefault();
               }
            }
         }
         if (e.stopPropagation)
         {
            event.stopPropagation = function()
            {
               if (sp)
               {
               orgEvent.stopPropagation();
               }
            };
         }

         // Android always sets the pageY but the pageX is always 0, pick it from the targetTouches instead
         if (e.targetTouches && e.targetTouches.length > 0)
         {
            var touch = e.targetTouches[e.targetTouches.length - 1];
            event.pageX = touch.pageX;
            event.pageY = touch.pageY;
         }

         // Add it in since the yui classes are looking at the value
         if (e.clientX || e.clientY)
         {
            event.clientX = e.clientX;
            event.clientY = e.clientY;
         }

         // Add it in since the yui classes are looking at the value
         if (e.button)
         {
            event.button = e.button;
         }

         return event;
      };

      // First patch the YAHOO.uti.DragDropMgr (which is an already created singleton object without a prototype)
      var DDM_patch = function()
      {
         // Remove the mouse listeners that was added in DragDropMgr.onLoad
         Event.removeListener(document, "mouseup", this.handleMouseUp);
         Event.removeListener(document, "mousemove", this.handleMouseMove);

         // Add in a "proxy" mousemove listener
         var original_handleMouseMove = this.handleMouseMove;
         this.handleMouseMove = function(e)
         {
            // Create a faked event so that pageX and pageY will be set
            var event = createMouseEvent(e);
            original_handleMouseMove.call(this, event);

            // Make sure to save the touch coords since the "touchend" event always have pageX and pageY set to 0
            this._lastTouchPageX = event.pageX;
            this._lastTouchPageY = event.pageY;
         };

         // Add in a "proxy" mouseup listener
         var original_handleMouseUp = this.handleMouseUp;
         this.handleMouseUp = function(e)
         {
            // Create a faked event so that we can make sure event propagation isn't stopped
            var event = createMouseEvent(e, false, false);
            original_handleMouseUp.call(this, event);
         };

         // Add our own proxy touch listeners
         Event.on(document, "touchend", this.handleMouseUp, this, true);
         Event.on(document, "touchmove", this.handleMouseMove, this, true);

         // Make sure the shim is listening to touch events instead of mouse events
         var original__createShim = this._createShim;
         this._createShim = function()
         {
            original__createShim.call(this);

            // Stop listening to mouse events
            Event.removeListener(s, "mouseup",  this.handleMouseUp);
            Event.removeListener(s, "mouseover",  this.handleMouseMove);

            // Start listening to touch events
            Event.on(s, "touchend",  this.handleMouseUp, this, true);
            Event.on(s, "touchmove", this.handleMouseMove, this, true);
         };

         // Patch the fire events method so we it can treat the "touchend" event as a "mouseup" event (having pageX & pageY)
         var original_fireEvents = this.fireEvents;
         this.fireEvents = function(e, isDrop)
         {
            var event = e;
            if (isDrop)
            {
               // Create a fake event object with all attributes the drag drop classes seem to use
               event = createMouseEvent(e, false, false);
               event.pageX = this._lastTouchPageX;
               event.pageY = this._lastTouchPageY;
            }

            original_fireEvents.call(this, event, isDrop);
         };

      };
      DDM_patch.call(YAHOO.util.DragDropMgr);

      // Now patch the YAHOO.util.DragDrop prototype
      var DD_patch = function()
      {
         var original_init = YAHOO.util.DragDrop.prototype.init;
         YAHOO.util.DragDrop.prototype.init = function(id, sGroup, config)
         {
            original_init.call(this, id, sGroup, config);

            // Stop listening to mouse events
            Event.removeListener(this._domRef || this.id, "mousedown", this.handleMouseDown);

            // Start listening to touch events
            Event.on(this._domRef || this.id, "touchstart", this.handleMouseDown, this, true);
         };

         var original_setOuterHandleElId = YAHOO.util.DragDrop.prototype.setOuterHandleElId;
         YAHOO.util.DragDrop.prototype.setOuterHandleElId = function(id)
         {
            original_setOuterHandleElId.call(this, id);

            // Stop listening to mouse events
            Event.removeListener(id, "mousedown", this.handleMouseDown);

            // Start listening to touch events
            Event.on(id, "touchstart", this.handleMouseDown, this, true);
         };

         var original_handleMouseDown = YAHOO.util.DragDrop.prototype.handleMouseDown;
         YAHOO.util.DragDrop.prototype.handleMouseDown = function(e, oDD)
         {
            // Create a faked event with pageX and pageY attributes to keep yui happy
            var event = createMouseEvent(e, false, false);

            // Make sure to save the touch coords since the "touchend" event always have pageX and pageY set to 0
            this._lastTouchPageX = event.pageX;
            this._lastTouchPageY = event.pageY;

            original_handleMouseDown.call(this, event, oDD);
         };
      };
      DD_patch.call(YAHOO.util.DragDrop);

   }
})();