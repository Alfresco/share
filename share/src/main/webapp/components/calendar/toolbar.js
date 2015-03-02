/*
 *** Alfresco.CalendarToolbar
*/
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.CalendarToolbar = function(containerId, enabledViews, defaultView)
   {
      this.name = "Alfresco.CalendarToolbar";
      this.id = containerId;
		this.enabledViews = enabledViews;
		this.defaultView = defaultView;

      this.navButtonGroup = null;
      this.nextButton = null;
      this.prevButton = null;
      this.todayButton = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      Alfresco.util.ComponentManager.register(this);

      return this;
   };

   Alfresco.CalendarToolbar.prototype =
   {
      /**
       * Sets the current site for this component.
       * 
       * @property siteId
       * @type string
       */
      setSiteId: function(siteId)
      {
         this.siteId = siteId;
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function()
      {
         Event.onContentReady(this.id, this.init, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method init
       */
      init: function()
      {
         /* Add Event Button */
         if (Dom.get(this.id + "-addEvent-button"))
         {
            Alfresco.util.createYUIButton(this, "addEvent-button", this.onButtonClick);
         }
         Alfresco.util.createYUIButton(this, "publishEvents-button", null,
         {
            type: "link"
         });
         this.nextButton = Alfresco.util.createYUIButton(this, "next-button", this.onNextNav);
         this.nextButton.addClass("next-button");
         this.prevButton = Alfresco.util.createYUIButton(this, "prev-button", this.onPrevNav);
         this.prevButton.addClass("prev-button");
         this.todayButton = Alfresco.util.createYUIButton(this, "today-button", this.onTodayNav);

         this.workHoursButton = Alfresco.util.createYUIButton(this, "workHours-button", this.onToggleWorkHours,
         {
            type: "checkbox",
            checked:false
         });
         this.workHoursButton.set("title", Alfresco.util.message(this.workHoursButton.get("checked") ? "button.work-hours.all" : "button.work-hours.working", 'Alfresco.CalendarToolbar'));
         this.navButtonGroup = new YAHOO.widget.ButtonGroup(this.id + "-navigation");
         if (typeof(this.navButtonGroup) != "undefined" && this.navButtonGroup._buttons != null ) // Will be undefined / null if navigation is hidden serverside (e.g. only one view enabled)
         {

            // The view will either be the booked marked value (from the hash with the "view=" stripped off), or on the query params, or the default.
            var hash = window.location.hash,
               view = hash.substring(hash.indexOf("view=") + 5).split("&")[0] || Alfresco.util.getQueryStringParameter('view') || this.defaultView;

            for (var i = 0; i < this.navButtonGroup._buttons.length; i++)
            {
               if (this.navButtonGroup._buttons[i]._button.id.match(view))
               {
                  this.navButtonGroup.check(i);
                  this.disableButtons(i);
                  break;
               }
            }
            this.navButtonGroup.on("checkedButtonChange", this.onNavigation, this.navButtonGroup, this);
         }
      },

      onNextNav: function(e)
      {
         this._fireEvent("nextNav");
      },

      onPrevNav: function(e)
      {
         this._fireEvent("prevNav");         
      },

      onTodayNav: function(e)
      {
         this._fireEvent("todayNav");
      },

      onToggleWorkHours: function(e)
      {
         // Note the title is reversed since it reflects what will happen if the user clicks the button.
         this.workHoursButton.set("title", Alfresco.util.message(this.workHoursButton.get("checked") ? "button.work-hours.all" : "button.work-hours.working", 'Alfresco.CalendarToolbar'));
         this._fireEvent("toggleWorkHours");
      },

      onNavigation: function(e)
      {
         this.disableButtons(e.newValue.index);

         YAHOO.Bubbling.fire("viewChanged",
         {
            activeView: e.newValue.index
         });
      },
      disableButtons : function(butIndex) 
      {
         var selectedButton = this.navButtonGroup.getButtons()[butIndex];
         if (this.todayButton != null) // Note: Today button will be null if elements are hidden serverside
         {
            // Disable Nav for Agenda view which uses a different navigation model
            if (selectedButton.get('label') === Alfresco.util.message('label.agenda', 'Alfresco.CalendarView'))
            {
               this.todayButton.set('disabled', true);
               this.nextButton.set('disabled', true);
               this.prevButton.set('disabled', true);
            }
            else 
            {
               this.todayButton.set('disabled', false);
               this.nextButton.set('disabled', false);
               this.prevButton.set('disabled', false);
            }

            // Work Hours button needs disabling in both month and agenda views.
            if (selectedButton.get('label') === Alfresco.util.message('label.month', 'Alfresco.CalendarView') || selectedButton.get('label') === Alfresco.util.message('label.agenda', 'Alfresco.CalendarView'))
            {
               this.workHoursButton.set('disabled', true);
            }
            else
            {
               this.workHoursButton.set('disabled', false);
            }

         }
      },
      _fireEvent: function(type)
      {
         YAHOO.Bubbling.fire(type, 
         {
            source: this
         });
      },

      /**
       * Fired when the "Add Event" button is clicked.
       * Displays the event creation form. Initialises the 
       * form if it hasn't been initialised. 
       *
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       * @method  onButtonClick
       */     
      onButtonClick: function(e)
      {
         var obj = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarView");
         if (obj)
         {
             obj.showAddDialog();
         }
      }
   };
})();