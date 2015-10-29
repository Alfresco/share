// Ensure Alfresco.forms and validation objects exist
Alfresco.forms = Alfresco.forms || {};
Alfresco.forms.validation = Alfresco.forms.validation || {};

/**
 * Class to represent the forms runtime.
 * 
 * @namespace Alfresco.forms
 * @class Alfresco.forms.Form
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Constructor for a form.
    * 
    * @param {String} formId The HTML id of the form to be managed
    * @return {Alfresco.forms.Form} The new Form instance
    * @constructor
    */
   Alfresco.forms.Form = function(formId)
   {
      this.formId = formId;
      this.showSubmitStateDynamically = false;
      this.reusable = true;
      this.submitAsJSON = false;
      this.submitElements = [];
      this.validations = [];
      this.tooltips = {};
      this._valid = null;
      this._fieldEvents = {};
      this._visitedFields = {};
      this.ajaxSubmit = false;
      this.ajaxSubmitMethod = "POST";
      this.ajaxNoReloadOnAuthFailure = null;
      this.errorContainer = "tooltip";
      this.showMultipleErrors = true;

      // Make sure to hide error containers (i.e. tooltips) when overlays and panels are closed

      YAHOO.Bubbling.on("showPanel", this.hideErrorContainer, this);
      YAHOO.Bubbling.on("hidePanel", this.hideErrorContainer, this);
      YAHOO.Bubbling.on("showOverlay", this.hideErrorContainer, this);
      YAHOO.Bubbling.on("hideOverlay", this.hideErrorContainer, this);

      return this;
   };

   YAHOO.lang.augmentObject(Alfresco.forms.Form,
   {
      NOTIFICATION_LEVEL_NONE: 1,

      /**
       * Applies css classes against the fields
       */
      NOTIFICATION_LEVEL_FIELD: 2,

      /**
       * Invokes the error containers
       */
      NOTIFICATION_LEVEL_CONTAINER: 3
   });

   Alfresco.forms.Form.prototype =
   {

      /**
       * HTML id of the form being represented.
       * 
       * @property formId
       * @type string
       */
      formId: null,

      /**
       * List of ids and/or elements being used to submit the form.
       * 
       * @property submitElements
       * @type object[]
       */
      submitElements: null,
      
      /**
       * Flag to determine whether the submit elements dynamically update
       * their state depending on the current values in the form.
       * 
       * @property showSubmitStateDynamically
       * @type boolean
       */
      showSubmitStateDynamically: null,

      /**
       * Flag to determine whether the submit elements shall be enabled again after a successful form submission.
       *
       * @property reusable
       * @type boolean
       * @default false
       */
      reusable: false,

      /**
       * Flag to determine whether the form will be submitted using an AJAX request.
       * 
       * @property ajaxSubmit
       * @type boolean
       */
      ajaxSubmit: null,
      
      /**
       * String representing where errors should be displayed. 
       * If the value is not "alert" it's presumed the string is the id of an 
       * HTML object to be used as the error container.
       * 
       * @property errorContainer
       * @type string
       */
      errorContainer: null,

       /**
       * Boolean deciding if on eor many errors shall be displayed at a time.
       *
       * @property showMultipleErrors
       * @type boolean
       */
      showMultipleErrors: false,
      
      /**
       * Object literal containing the abstract function for pre-submission form processing.
       *   fn: function, // The override function.
       *   obj: object, // An object to pass back to the function.
       *   scope: object // The object to use for the scope of the function.
       * 
       * @property doBeforeFormSubmit
       * @type object
       */
      doBeforeFormSubmit:
      {
         fn: function(form, obj){},
         obj: null,
         scope: this
      },
      
      /**
       * Object literal containing the abstract function for intercepting AJAX form submission.
       * Returning false from the override will prevent the Forms Runtime from submitting the data.
       *   fn: function, // The override function.
       *   obj: object, // An object to pass back to the function.
       *   scope: object // The object to use for the scope of the function.
       * 
       * @property doBeforeAjaxRequest
       * @type object
       */
      doBeforeAjaxRequest:
      {
         fn: function(form, obj)
         {
            return true;
         },
         obj: null,
         scope: this
      },
      
      /**
       * Object holding the callback handlers and messages for AJAX submissions.
       * The callback handlers are themselves an object of the form:
       *   fn: function, // The handler to call when the event fires.
       *   obj: object, // An object to pass back to the handler.
       *   scope: object // The object to use for the scope of the handler.
       * 
       * @property ajaxSubmitHandlers
       * @type object
       */
      ajaxSubmitHandlers: null,
      
      /**
       * String representing the http method to be used for the
       * ajax call. Default is POST.
       * 
       * @property ajaxSubmitMethod
       * @type String
       */
      ajaxSubmitMethod: null,

      /**
       * Decides if the browser shall try and reload the current page if a form submission fails due to auth failure.
       *
       * Possible values:
       * true - browser will NOT reload current page
       * false - browser will reload current page
       * null - Will use the default value of noReloadOnAuthFailure in Alfresco.util.Ajax.request
       *
       * @property ajaxNoReloadOnAuthFailure
       * @type String
       * @default null
       */
      ajaxNoReloadOnAuthFailure: null,
      
      /**
       * Flag to determine whether the form data should be submitted 
       * represented by a JSON structure.
       * 
       * @property submitAsJSON
       * @type boolean
       */
      submitAsJSON: null,
      
      /**
       * List of validations to execute when the form is submitted.
       * 
       * @property validations
       * @type object[]
       */
      validations: null,

      /**
       * The tooltips used to display an error when errorContainer is set to "tooltip"
       *
       * @property tooltips
       * @type object
       */
      tooltips: null,

      /**
       * The fields and events that already have a listener attached to them.
       */
      _fieldEvents: {},

      _FIELD_EVENT_UNDEFINED: undefined,
      _FIELD_EVENT_LISTEN: 1,
      _FIELD_EVENT_VALIDATE: 2,

      /**
       * Name of the attribute to be applied to HTML elements where a HTML validation message must be stored
       * temporarily
       */
      _VALIDATION_MSG_ATTR: "alf-validation-msg",

      /**
       * The fields that are considered to have been visited by the user.
       * Meaning that they will display balloon and red color if users tabs out of them.
       */
      _visitedFields: null,

      /**
       * Indicates if the field is valid, will be updated by _runValidations
       */
      _valid: null,

      /**
       * Sets up the required event handlers and prepares the form for use.
       * NOTE: This must be called after all other setup methods.
       * 
       * @method init
       */
      init: function()
      {
         var form = Dom.get(this.formId);
         if (form !== null)
         {
            if (form.getAttribute("forms-runtime") != "listening")
            {
               // add the event to the form and make the scope of the handler this form.
               Event.addListener(form, "submit", this._submitInvoked, this, true);
               form.setAttribute("forms-runtime", "listening");
               if (this.ajaxSubmit)
               {
                  form.setAttribute("onsubmit", "return false;");
               }
            }
            
            // determine if the AJAX and JSON submission should be enabled
            if (form.getAttribute("enctype") && form.getAttribute("enctype") == "application/json")
            {
               this.ajaxSubmit = true;
               this.submitAsJSON = true;
            }
            
            // find the default submit elements if there are no submitIds set
            if (this.submitElements && this.submitElements.length == 0)
            {
               // use a selector to find any submit elements for the form
               var nodes = Selector.query('#' + this.formId + ' > input[type="submit"]');
               for (var x = 0, xx = nodes.length; x < xx; x++)
               {
                  var elem = nodes[x];
                  this.submitElements.push(elem.id);
               }
            }

            // make sure the submit elements start in the correct state
            this._runValidations(null, null, Alfresco.forms.Form.NOTIFICATION_LEVEL_FIELD);

            // Set up an escape listener so we can close 3rd party widgets such as ballons when escape is clicked
            var escapeListener = new KeyListener(document,
            {
               keys: KeyListener.KEY.ESCAPE
            },
            {
               fn: function(id, keyEvent)
               {
                  for (var id in this.tooltips)
                  {
                     if (this.tooltips.hasOwnProperty(id))
                     {
                        this.tooltips[id].hide();
                     }
                  }
               },
               scope: this,
               correctScope: true
            });
            escapeListener.enable();
         }
         else
         {
            this._showInternalError("form with id of '" + this.formId + 
                  "' could not be located, ensure the form is created after the form element is available.");
         }
      },

      /**
       * If the form has been tested a boolean is returned to indicate if the form is valid.
       * If it hasn't been tested null is returned,
       *
       * @return {null|Boolean}
       */
      isValid: function()
      {
         return this._valid;
      },

      /**
       * Deprecated!
       *
       * All forms are always validated before submit
       *
       * Enables or disables validation when the form is submitted.
       * 
       * @method setValidateOnSubmit
       * @param validate {boolean} true to validate on submission, false
       *        to avoid validation
       */
      setValidateOnSubmit: function(validate)
      {
      },
      
      /**
       * Deprecated!
       *
       * All fields are now always validated.
       *
       * Sets whether all fields are validated when the form is submitted.
       * 
       * @method setValidateAllOnSubmit
       * @param validate {boolean} true to validate all fields on submission, false
       *        to stop after the first validation failure
       */
      setValidateAllOnSubmit: function(validateAll)
      {
      },
      
      /**
       * Sets the list of ids and/or elements being used to submit the form.
       * By default the forms runtime will look for and use the first
       * input field of type submit found in the form being managed.
       * 
       * @method setSubmitElements
       * @param submitElements {object | object[]} Single object or array of objects
       */
      setSubmitElements: function(submitElements)
      {
         if (!YAHOO.lang.isArray(submitElements))
         {
            this.submitElements[0] = submitElements;
         }
         else
         {
            this.submitElements = submitElements;
         }
      },
      
      /**
       * Sets the container where errors will be displayed.
       * 
       * @method setErrorContainer
       * @param container {string} String representing where errors should
       *        be displayed. If the value is not "alert" it's presumed the 
       *        string is the id of an HTML object to be used as the error 
       *        container
       */
      setErrorContainer: function(container)
      {
         this.errorContainer = container;
      },

      /**
       * Dictates if the containers will display one or many errors at a time.
       *
       * @method setShowMultipleErrors
       * @param showMultipleErrors {boolean} Set to true to display multiple errors
       */
      setShowMultipleErrors: function(showMultipleErrors)
      {
         this.showMultipleErrors = showMultipleErrors;
      },

      /**
       * Sets a field as being repeatable, this adds a 'plus' sign after the field
       * thus allowing multiple values to be entered.
       *
       * @method setRepeatable
       * @param fieldId {string} Id of the field the validation is for
       * @param containerId {string} Id of the element representing the
       *        field 'prototype' i.e. the item that will get cloned.
       */
      setRepeatable: function(fieldId, containerId)
      {
         alert("not implemented yet");
      },

      /**
       * Sets whether the submit elements dynamically update
       * their state depending on the current values in the form.
       * The visibility of errors can be controlled via the
       * showErrors parameter.
       * 
       * @method setShowSubmitStateDynamically
       * @param showState {boolean} true to have the elements update dynamically
       */
      setShowSubmitStateDynamically: function(showState)
      {
         this.showSubmitStateDynamically = showState;
      },

      /**
       * The forms submit elements are automatically disabled during form submission (and enabled if the form submission fails).
       * Sets this to true if submit elements shall be enabled after a successful form submission as well.
       * Thus making the form "reusable".
       *
       * @param reusable {boolean}
       */
      setAsReusable: function(reusable)
      {
         this.reusable = reusable;
      },

      /**
       * Enables or disables whether the form submits via an AJAX call.
       * 
       * @method enableAJAXSubmit
       * @param ajaxSubmit {boolean} true to submit using AJAX, false to submit
       *        using the browser's default behaviour
       * @param callbacks {object} Optional object representing callback handlers 
       *        or messages to use, for example
       *        { 
       *           successCallback: yourHandlerObject,
       *           failureCallback: yourHandlerObject,
       *           successMessage: yourMessage,
       *           failureMessage: yourMessage
       *        }
       *        Callback handler objects are of the form:
       *        { 
       *           fn: function, // The handler to call when the event fires.
       *           obj: object, // An object to pass back to the handler.
       *           scope: object // The object to use for the scope of the handler.
       *        }
       */
      setAJAXSubmit: function(ajaxSubmit, callbacks)
      {
         this.ajaxSubmit = ajaxSubmit;
         this.ajaxSubmitHandlers = callbacks;
      },
      
      /**
       * Enables or disables submitting the form data in JSON format.
       * Setting the enctype attribute of the form to "application/json"
       * in Firefox will achieve the same result.
       * 
       * @method setSubmitAsJSON
       * @param submitAsJSON {boolean} true to submit the form data as JSON, 
       *        false to submit one of the standard types "multipart/form-data"
       *        or "application/x-www-form-urlencoded" depending on the enctype
       *        attribute on the form
       */
      setSubmitAsJSON: function(submitAsJSON)
      {
         this.submitAsJSON = submitAsJSON;
      },

      /**
       * Set the http method to use for the AJAX call.
       * 
       * @method setAjaxSubmitMethod
       * @param ajaxSubmitMethod {string} the http method to use for the AJAX call
       */
      setAjaxSubmitMethod: function(ajaxSubmitMethod)
      {
         this.ajaxSubmitMethod = ajaxSubmitMethod;
      },

      /**
       * Decides if the browser shall try and reload the current page if a form submission fails due to auth failure.
       *
       * Possible values:
       * true - browser will NOT reload current page
       * false - browser will reload current page
       * null - Will use the default value of noReloadOnAuthFailure in Alfresco.util.Ajax.request
       *
       * @method setAjaxNoReloadOnAuthFailure
       */
      setAjaxNoReloadOnAuthFailure: function(noReloadOnAuthFailure)
      {
         this.ajaxNoReloadOnAuthFailure = noReloadOnAuthFailure;
      },
      
      /**
       * Adds validation for a specific field on the form.
       * Use the when parameter to define when validation shall be triggered.
       * ALL added validators will be called in the order they were added every time validation is triggered.
       * Validation can also be manually triggered by calling validate().
       *
       * @method addValidation
       * @param fieldId {string} Id of the field the validation is for
       * @param validationHandler {function} Function to call to handle the 
       *        actual validation
       * @param validationArgs {object} Optional object representing the 
       *        arguments to pass to the validation handler function
       * @param when {string} Name of the event when validation shall be triggered, i.e. "keyup", "blur"
       *        If null, the validation will
       * @param message {string|object|function|null} Message to be displayed to the user when validation fails,
       *        if a function it will be invoked and shall return a string, which may contain HTML,
       *        if an object different values may be provided for rendering a HTML or text message as necessary 
       *        in the 'html' and 'text' property values, which may themselves be strings, functions or null,
       *        if omitted or null the default message in the handler is used
       * @param config {object|function} Contains advanced instructions for the form, i.e. tooltip behaviour when widgets are used
       * @param config.validationType {string} Set to "mandatory" if a custom mandatory validator is used (rather than Alfresco.forms.mandatory)
       * @param config.errorField {string|HTMLElement} The element that reflects the error (if not provided fieldId will be used)
       */
      addValidation: function(fieldId, validationHandler, validationArgs, when, message, config)
      {
         var field = Dom.get(fieldId);
         if (field == null)
         {
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Ignoring validation for field with id of '" + fieldId + "' as it could not be located.");
            
            return;
         }
         else
         {
            // Make sure every validated element has an id set.
            if (!field.id)
            {
               Alfresco.util.generateDomId(field, "form-field-")
            }
         }
         
         if (validationHandler === undefined || validationHandler === null)
         {
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Ignoring validation for field with id of '" + fieldId + "' as a validationHandler was not provided.");
            
            return;
         }
         
         if (message === undefined)
         {
            message = null;
         }
         
         // create object representation of validation
         var validation =
         {
            fieldId: field.id,
            args: validationArgs,
            handler: validationHandler,
            message: message,
            originalTitle: field.getAttribute("title") || "",
            event: when,
            config: config
         };
         
         // add to list of validations
         this.validations.push(validation);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Added submit validation for field: " + fieldId +
                                  ", using handler: " + 
                                  (validationHandler.name || YAHOO.lang.dump(validationHandler)) + 
                                  ", args: " + YAHOO.lang.dump(validationArgs));
      
         // if an event has been specified attach an event handler
         if (when && when.length > 0)
         {
            // Add blur listener so we can keep track of visited fields and know when to hide error containers
            if (this._fieldEvents[field.id + ":" + "blur"] == this._FIELD_EVENT_UNDEFINED)
            {
               Event.addListener(field, "blur", this._validationEventFired, field.id, this);
               this._fieldEvents[field.id + ":" + "blur"] = this._FIELD_EVENT_LISTEN;
            }

            if (this._fieldEvents[field.id + ":" + when] == this._FIELD_EVENT_UNDEFINED)
            {
               // Add a listener to the field event (unless one already had been added for that event)

               // YAHOO doesn't seem to process the propertychange via the addListener so we need to take
               // matters into our own hands. Listening for the "propertychange" event is required so that
               // the forms runtime can validate when autocomplete is used in IE.
               if (YAHOO.env.ua.ie && YAHOO.env.ua.ie < 11 && when == "propertychange")
               {
                  var _this = this, _fieldId = field.id;
                  field.attachEvent("onpropertychange", function(e) {
                     _this._validationEventFired(e, _fieldId);
                  });
               }
               else
               {
                  Event.addListener(field, when, this._validationEventFired, field.id, this);
               }
            }
            if (this._fieldEvents[field.id + ":" + when] != this._FIELD_EVENT_VALIDATE)
            {
               // Now we will validate on the event
               this._fieldEvents[field.id + ":" + when] = this._FIELD_EVENT_VALIDATE;
            }

            // Add focus listener so we can keep track of visited fields andknow when to show error containers
            if (this._fieldEvents[field.id + ":" + "focus"] == this._FIELD_EVENT_UNDEFINED)
            {
               Event.addListener(field, "focus", this._validationEventFired, field.id, this);
               this._fieldEvents[field.id + ":" + "focus"] = this._FIELD_EVENT_LISTEN;
            }

            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Added field validation for field: " + fieldId +
                         ", using handler: " +
                         (validationHandler.name || YAHOO.lang.dump(validationHandler)) +
                         ", args: " + YAHOO.lang.dump(validationArgs) +
                         ", on event: " + when);
         }
      },
      
      /**
       * NOTE! Deprecated. The form runtime now handles error displays by itself.
       * Kept to make sure custom validation handlers calling this method won't break.
       *
       * Adds an error to the form.
       * 
       * @method addError
       * @param msg {string} The error message to display
       * @param field {object} The element representing the field the error occurred on
       */
      addError: function(msg, field)
      {

      },
      
      /**
       * Adds the given submitElement to the list of submit elements
       * for the form.
       * 
       * @method addSubmitElement
       * @param submitElement Object or string representing the submit element
       */
      addSubmitElement: function(submitElement)
      {
         if (submitElement !== null)
         {
            // add the new element to the list
            this.submitElements.push(submitElement);

            if (this.showSubmitStateDynamically)
            {
               // force a refresh of the submit state
               this.validate();
            }
         }
      },
      
      /**
       * Retrieves the label text for a field
       * 
       * @method getFieldLabel
       * @param fieldId {string} The id of the field to get the label for
       * @return {string} The label for the field or the fieldId if a label could not be found
       */
      getFieldLabel: function(fieldId)
      {
         var label = null;
         
         // lookup the label using the "for" attribute (use the first if multiple found)
         var nodes = Selector.query('label');
         // NOTE: there seems to be a bug in getting label using 'for' or 'htmlFor'
         //       for now get all labels and find the one we want
         if (nodes.length > 0)
         {
            for (var x = 0, xx = nodes.length; x < xx; x++)
            {
               var elem = nodes[x];
               if ((elem["htmlFor"] == fieldId) || (elem["htmlFor"] == fieldId+"-cntrl"))
               {
                  // get the text for the label
                  label = elem.firstChild.nodeValue;
               }
            }
         }
         
         // default to the field id if the label element was not found
         if (label == null)
         {
            label = fieldId;
         }
         
         return label;
      },
      
      /**
       * Retrieves fields by type
       * 
       * @method getFieldsByType
       * @param type {string} The type
       * @return {string} fields by type
       */	  
      getFieldsByType: function(type)
      {
         var nodes = Selector.query(type);
         return nodes;
      },
	  
      /**
       * Retrieves the data currently held by the form.
       * 
       * @method getFormData
       * @return An object representing the form data
       */
      getFormData: function()
      {
         // get the form element
         var form = Dom.get(this.formId);
         
         // build object representation of the form data
         return this._buildAjaxForSubmit(form);
      },
      
      /**
       * Applies a Key Listener to input fields to ensure tabbing only targets elements
       * that specifically set a "tabindex" attribute.
       * This has only been seen as an issue with the Firefox web browser, so shouldn't be applied otherwise.
       *
       * @method applyTabFix
       */
      applyTabFix: function()
      {
         if (YAHOO.env.ua.gecko > 0)
         {
            /**
             * Ensure the Tab key only focusses relevant fields
             */
            var form = Dom.get(this.formId);
            
            var fnTabFix = function(id, keyEvent)
            {
               var event = keyEvent[1];
               var target = event.target;
               if (!target.hasAttribute("tabindex"))
               {
                  Event.stopEvent(event);
                  var el = Selector.query("[tabindex]", form)[0];
                  if (Alfresco.util.isVisible(el))
                  {
                     el.focus();
                  }
               }
            };
            
            var tabListener = new KeyListener(form,
            {
               keys: KeyListener.KEY.TAB
            },
            fnTabFix, "keyup");
            tabListener.enable();
         }
      },

      /**
       * Updates the state of all submit elements.
       *
       * @method updateSubmitElements
       */
      updateSubmitElements: function()
      {
         // To support old forms updating the
         return this.validate(Alfresco.forms.Form.NOTIFICATION_LEVEL_FIELD);
      },

      /**
       * Validates all the fields in the form.
       * Updates the state of all submit elements if the "showSubmitStateDynamically" option was set to true.
       *
       * @method validateField
       * @param notificationLevel {Object|null} (Optional) The Event that triggered the validation, shall have a type attribute
       */
      validate: function(notificationLevel)
      {
         notificationLevel = notificationLevel || Alfresco.forms.Form.NOTIFICATION_LEVEL_FIELD;
         return this._runValidations({}, false, notificationLevel);
      },

      /**
       * Validates all the fields in the form but indicates it was triggered from a specific event and field.
       * Updates the state of all submit elements if the "showSubmitStateDynamically" option was set to true.
       *
       * @method validateField
       * @param event {Object|null} (Optional) The Event that triggered the validation, shall have a type attribute
       * @param fieldId {String|null} (Optional) The id
       */
      validateField: function(event, fieldId)
      {
         this._validationEventFired(event, fieldId);
      },

      /**
       * Toggles the state of all submit elements.
       *
       * @method _toggleSubmitElements
       * @parameter enabled {boolean} true if the elements shall be enabled
       */
      _toggleSubmitElements: function(enabled)
      {
         // make sure all submit elements show correct state
         for (var x = 0, xx = this.submitElements.length; x < xx; x++)
         {
            var currentItem = this.submitElements[x];
            if (currentItem)
            {
              if (typeof currentItem == "string")
              {
                 // get the element with the id and set the disabled attribute
                 Dom.get(currentItem).disabled = !enabled;
              }
              else
              {
                 // TODO: for now if an object is passed presume it's a YUI button
                 currentItem.set("disabled", !enabled);
              }              
            }
         }
      },
      
      /**
       * Event handler called when a validation event is fired by any registered field.
       * 
       * @method _validationEventFired
       * @param event {object} The event
       * @param fieldId {String}
       * @private
       */
      _validationEventFired: function(event, fieldId)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Event has been fired for field: " + fieldId);

         if (event)
         {
            if (event.type == "blur")
            {
               // Mark field as visited when we leave it
               if (!this._isFieldVisited(fieldId))
               {
                  this._setFieldAsVisited(fieldId);
                  this._runValidations(event, fieldId, Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER);
               }
               this._setFieldAsVisited(fieldId);
               if (this.tooltips[fieldId])
               {
                  this.hideErrorContainer();
               }
            }
            else if (event.type == "focus")
            {
               if (!this.tooltips[fieldId])
               {
                  this.tooltips[fieldId] = Alfresco.util.createBalloon(this.getFieldValidationEl(fieldId), {
                     effectType: null,
                     effectDuration: 0
                  });
               }

               if (YAHOO.util.Dom.hasClass(fieldId, "invalid"))
               {
                  if (this._fieldEvents[fieldId + ":" + "focus"] != this._FIELD_EVENT_VALIDATE)
                  {
                     // Display error if "focus" event hadn't been added to trigger validation
                     var message = Dom.get(fieldId).getAttribute(this._VALIDATION_MSG_ATTR);
                     this.tooltips[fieldId].html(message);
                     this.tooltips[fieldId].show();
                  }
               }
            }
            else if (event.type == "propertychange" && !Alfresco.util.arrayContains(["value", "checked"], event.propertyName))
            {
               // "propertychange" events are fired for any attribute change, i.e. "className" & "title",
               // lets make sure we only validate when necessary
               return;
            }
         }


         // Ignore tab, shift, enter and escape clicks
         if (!event || (event.keyCode != 9 && event.keyCode != 16 && event.keyCode != 13 && event.keyCode != 27))
         {
            if (event && this._fieldEvents[fieldId + ":" + event.type] == this._FIELD_EVENT_VALIDATE)
            {
               // Make sure to run the validations anyhow so the tooltips and css error classes are updated & displayed
               this._runValidations(event, fieldId, Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER);
            }
         }
      },

      /**
       * Helper method that inspects the url and adds or refreshes the CSRF token parameter
       *
       * @param url
       * @return {*}
       * @private
       */
      _setCSRFParameter: function(url)
      {
         // Make sure there is a CSRF parameter with the token present in submission
         var pathAndParams = url.split("?"),
               tokenParam = Alfresco.util.CSRFPolicy.getParameter() + "=" + encodeURIComponent(Alfresco.util.CSRFPolicy.getToken());
         if (pathAndParams.length == 1)
         {
            url += "?" + tokenParam;
         }
         else
         {
            var params = pathAndParams[1].split("&"),
                  pi = 0,
                  nameAndValue,
                  newParams = "",
                  firstParam = true;
            for (; pi < params.length; pi++)
            {
               nameAndValue = params[pi].split("=");
               if (nameAndValue.length > 0 && nameAndValue[0] == Alfresco.util.CSRFPolicy.getParameter())
               {
                  // Don't use the old token param, add a new one after the loop instead
               }
               else
               {
                  // Pass on parameter
                  newParams += firstParam ? "?" : "&";
                  newParams += params[pi];
                  firstParam = false;
               }
            }
            if (pi == params.length)
            {
               newParams += firstParam ? "?" : "&";
               newParams += tokenParam;
            }
            url = pathAndParams[0] + newParams;
         }
         return url;
      },

      /**
       * Event handler called when the form is submitted.
       * 
       * @method _submitInvoked
       * @param event {object} The event
       * @private
       */
      _submitInvoked: function(event)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Submit invoked on formId: ", this.formId);

         this._setAllFieldsAsVisited();

         // Run all validators
         if (this._runValidations(event, null, Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER))
         {
            // validation was successful

            // get the form element
            var form = Dom.get(this.formId);

            // call the pre-submit function, passing the form for last-chance processing
            this.doBeforeFormSubmit.fn.call(this.doBeforeFormSubmit.scope, form, this.doBeforeFormSubmit.obj);

            // should submission be done using AJAX, or let
            // the browser do the submit?
            if (this.ajaxSubmit)
            {
               // stop the browser from submitting the form
               if (typeof event.cancelBubble !== 'unknown' && typeof event.returnValue !== 'unknown')
               {
                  Event.stopEvent(event);
               }

               // get the form's action URL
               var submitUrl = form.attributes.action.nodeValue;

               if (Alfresco.logger.isDebugEnabled())
               {
                  Alfresco.logger.debug("Performing AJAX submission to url: ", submitUrl);
               }

               // determine how to submit the form, if the enctype
               // on the form is set to "application/json" then
               // package the form data as an AJAX string and post
               if (form.enctype && form.enctype == "multipart/form-data")
               {
                  var d = form.ownerDocument;
                  var iframe = d.createElement("iframe");
                  iframe.style.display = "none";
                  Dom.generateId(iframe, "formAjaxSubmit");
                  iframe.name = iframe.id;
                  document.body.appendChild(iframe);

                  // makes it possible to target the frame properly in IE.
                  window.frames[iframe.name].name = iframe.name;

                  // Pass the CSRF token if the CSRF token filter is enabled
                  if (Alfresco.util.CSRFPolicy.isFilterEnabled())
                  {
                     form.attributes.action.nodeValue = this._setCSRFParameter(submitUrl);
                  }

                  form.target = iframe.name;
                  form.submit();
                  return;
               }
               else
               {
                  // Only disable submit elements for XMLHttpRequests since we then have a chance of enabling them on failure
                  this._toggleSubmitElements(false);
               }

               // create config object to pass to request helper
               var config =
               {
                  method: this.ajaxSubmitMethod,
                  url: submitUrl
               };

               if (this.ajaxSubmitHandlers)
               {
                  this.ajaxSubmitHandlers = this.ajaxSubmitHandlers || {};
                  config.successMessage = this.ajaxSubmitHandlers.successMessage;
                  config.successCallback = {
                     fn: function(response, successCallback)
                     {
                        if (Dom.get(this.formId))
                        {
                           // The form still exists
                           this._resetAllVisitedFields();
                           if (this.reusable)
                           {
                              if (this.showSubmitStateDynamically)
                              {
                                 // Update using validation
                                 this.updateSubmitElements();
                              }
                              else
                              {
                                 // Enable submit buttons
                                 this._toggleSubmitElements(true);
                              }
                           }
                        }
                        if (successCallback.fn)
                        {
                           successCallback.fn.call(successCallback.scope || this, response, successCallback.obj);
                        }
                     },
                     obj: this.ajaxSubmitHandlers.successCallback || {},
                     scope: this
                  };
                  config.failureMessage = this.ajaxSubmitHandlers.failureMessage;
                  config.failureCallback = {
                     fn: function(response, failureCallback)
                     {
                        if (Dom.get(this.formId))
                        {
                           // The form still exists
                           this._resetAllVisitedFields();
                           if (this.showSubmitStateDynamically)
                           {
                              // Update using validation
                              this.updateSubmitElements();
                           }
                           else
                           {
                              // Enable submit buttons
                              this._toggleSubmitElements(true);
                           }
                        }
                        if (failureCallback.fn)
                        {
                           failureCallback.fn.call(failureCallback.scope || this, response, failureCallback.obj);
                        }
                     },
                     obj: this.ajaxSubmitHandlers.failureCallback || {},
                     scope: this
                  };
               }
               if (this.submitAsJSON)
               {
                  var jsonData = this._buildAjaxForSubmit(form);

                  // set up specific config
                  config.dataObj = jsonData;
                  config.noReloadOnAuthFailure = this.ajaxNoReloadOnAuthFailure;

                  // call the pre-request function, passing the config object for last-chance processing
                  if (this.doBeforeAjaxRequest.fn.call(this.doBeforeAjaxRequest.scope, config, this.doBeforeAjaxRequest.obj))
                  {
                     if (Alfresco.logger.isDebugEnabled())
                        Alfresco.logger.debug("Submitting JSON data: ", config.dataObj);

                     Alfresco.util.Ajax.jsonRequest(config);
                  }
                  else
                  {
                     if (Alfresco.logger.isDebugEnabled())
                        Alfresco.logger.debug("JSON data request cancelled in doBeforeAjaxRequest()");
                  }
                  this._toggleSubmitElements(true);
               }
               else
               {
                  if (Alfresco.logger.isDebugEnabled())
                     Alfresco.logger.debug("Submitting data in form: ", form.enctype);

                  // set up specific config
                  config.dataForm = form;
                  Alfresco.util.Ajax.request(config);
               }
            }
            else
            {
               if (form.enctype && form.enctype == "multipart/form-data" && Alfresco.util.CSRFPolicy.isFilterEnabled())
               {
                  // We are submitting the form as  multipart/form-data and leaving the page, make sure the CSRF token is set
                  form.attributes.action.nodeValue = this._setCSRFParameter(form.attributes.action.nodeValue);
               }
            }
         }
         else
         {
            // stop the event from continuing and sending the form.
            Event.stopEvent(event);

            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Submission prevented as validation failed");
            
            // Enable submit buttons
            this._toggleSubmitElements(true);
         }
      },
      
      /**
       * Builds a JSON representation of the current form
       * 
       * @method _buildAjaxForSubmit
       * @param form {object} The form object to build the JSON for
       * @private
       */
      _buildAjaxForSubmit: function(form)
      {
         if (form !== null)
         {
            var formData = {},
               length = form.elements.length;

            for (var i = 0; i < length; i++)
            {
               var element = form.elements[i],
                  name = element.name;

               if (name == "-" || element.disabled || element.type === "button")
               {
                  continue;
               }
               if (name == undefined || name == "")
               {
                  name = element.id;
               }
               var value = (element.type === "textarea") ? element.value : YAHOO.lang.trim(element.value);
               if (name)
               {
                  // check whether the input element is an array value
                  if ((name.length > 2) && (name.substring(name.length - 2) == '[]'))
                  {
                     name = name.substring(0, name.length - 2);
                     if (formData[name] === undefined)
                     {
                        formData[name] = new Array();
                     }
                     formData[name].push(value);
                  }
                  // check whether the input element is an object literal value
                  else if (name.indexOf(".") > 0)
                  {
                     var names = name.split(".");
                     var obj = formData;
                     var index;
                     for (var j = 0, k = names.length - 1; j < k; j++)
                     {
                        index = names[j];
                        if (obj[index] === undefined)
                        {
                           obj[index] = {};
                        }
                        obj = obj[index];
                     }
                     obj[names[j]] = value;
                  }
                  else if (!((element.type === "checkbox" || element.type === "radio") && !element.checked))
                  {
                     if (element.type == "select-multiple")
                     {
                        for (var j = 0, jj = element.options.length; j < jj; j++)
                        {
                           if (element.options[j].selected)
                           {
                              if (formData[name] == undefined)
                              {
                                 formData[name] = new Array();
                              }
                              formData[name].push(element.options[j].value);
                           }
                        }
                     }
                     else
                     {
                        formData[name] = value;
                     }
                  }
               }
            }
            
            return formData;
         }
      },

      /**
       * Checks if a field has been visited
       *
       * @function _isFieldVisited
       * @param fieldId THe fieldId to mark as edited
       * @return {boolean} true of the field has been edited (or the user has tried to submit the form)
       */
      _isFieldVisited: function(fieldId)
      {
         return (YAHOO.lang.isBoolean(this._visitedFields) && this._visitedFields) ||
               (YAHOO.lang.isObject(this._visitedFields) && this._visitedFields[fieldId]);
      },

      /**
       * Marks a field as visited
       *
       * @function _setFieldAsVisited
       * @param fieldId {string}
       */
      _setFieldAsVisited: function(fieldId)
      {
         if (!YAHOO.lang.isBoolean(this._visitedFields) && YAHOO.lang.isObject(this._visitedFields))
         {
            return this._visitedFields[fieldId] = true;
         }
      },

      /**
       * Marks all fields as visited
       *
       * @function _setAllFieldsEdited
       */
      _setAllFieldsAsVisited: function()
      {
         this._visitedFields = true;
      },

      _resetAllVisitedFields: function()
      {
         this._visitedFields = {};
      },


      _getConfig: function(config, configName, defaultValue)
      {
         var c = config || {};
         if (YAHOO.lang.isFunction(config))
         {
            c = config();
         }
         var result = Alfresco.util.findValueByDotNotation(c, configName);
         return (result != undefined || result != null) ? result : defaultValue;
      },

      _isMandatoryValidator: function(val)
      {
         return val.handler == Alfresco.forms.validation.mandatory || (this._getConfig(val.config, "validationType", "invalid") == "mandatory");
      },

      /**
       * Executes all registered validations and returns result.
       *
       * @method _runValidations
       * @parameter event {Object|null} The event that triggered the validation OR null if validation was programmatically called
       * @parameter fieldId {string|null|boolean} The current el that triggered the event OR null if validation was programmatically called
       * @private
       */
      _runValidations: function(event, fieldId, notificationLevel)
      {
         var allErrors = [],
            errorsByField = {},
            errorsByFieldText = {},
            allWarnings = [],
            warningsByField = {},
            fieldMsg,
            message,
            textMessage,
            validationType,
            MANDATORY = "mandatory",
            INVALID = "invalid",
            field,
            valFieldId,
            val,
            suppressValidationNotification,
            primaryErrorFieldId = null;

         // Iterate through all validations (for all enabled fields) so we can tell if the complete form is valid or not
         for (var x = 0, xx = this.validations.length; x < xx; x++)
         {
            val = this.validations[x];

            field = Dom.get(val.fieldId);
            if (field !== null && !field.disabled)
            {
               textMessage = null;
               valFieldId = field.getAttribute("id");
               errorsByField[valFieldId] = errorsByField[valFieldId] || [];
               errorsByFieldText[valFieldId] = errorsByFieldText[valFieldId] || [];
               warningsByField[valFieldId] = warningsByField[valFieldId] || [];

               // Make sure invalid fields are showed as mandatory until the user has had a chance of changing the value
               validationType = this._isMandatoryValidator(val) && !this._isFieldVisited(valFieldId) ? MANDATORY : INVALID;
               if (typeof val.message === "object" && val.message != null)
               {
                  message = val.message.html || null;
                  textMessage = val.message.text || null;
               }
               else
               {
                  message = val.message || null;
               }
               if (message)
               {
                  if (YAHOO.lang.isFunction(message))
                  {
                     // Validator wanted to create error message manually
                     message = message.call(this, val.args);
                  }
                  else
                  {
                     // Make sure message parameters based on validator args are resolved
                     message = YAHOO.lang.substitute(message, val.args);
                  }
               }
               else
               {
                  // Message is missing, use these as a last resort
                  var key;
                  if (this._isMandatoryValidator(val))
                  {
                     key = "Alfresco.forms.validation.mandatory.message";
                  }
                  else
                  {
                     key = "Alfresco.forms.validation.invalid.message";
                  }
                  message = Alfresco.util.message(key);
               }
               if (textMessage)
               {
                  if (YAHOO.lang.isFunction(textMessage))
                  {
                     // Validator wanted to create error message manually
                     textMessage = textMessage.call(this, val.args);
                  }
                  else
                  {
                     // Make sure message parameters based on validator args are resolved
                     textMessage = YAHOO.lang.substitute(textMessage, val.args);
                  }
               }
               else
               {
                  // Last resort - strip HTML tags from the HTML message
                  textMessage = message.replace(/<\/?[^>]+(>|$)/g, "");
               }

               // Make sure that validation notifications aren't triggered by events from other validations for the same field
               suppressValidationNotification = (validationType == INVALID) &&
                     (event != null && event.type != "blur" && event.type != val.event && event.keyCode != 13) &&
                     (fieldId == valFieldId);

               if (!val.handler(field, val.args, null, this, true, message))
               {
                  // The field is invalid

                  // Lets see if the validator wants to use another Dom id as context for the error container.
                  var errorField = Dom.get(this._getConfig(val.config, "errorField", field));
                  if (!errorField.id)
                  {
                     // All fields must have a dom id
                     Alfresco.util.generateDomId(errorField);
                  }
                  if (errorField.id != fieldId && validationType == INVALID)
                  {
                     // Make sure that error containers are created for custom fields as well
                     errorsByField[errorField.id] = errorsByField[errorField.id] || [];
                     errorsByFieldText[errorField.id] = errorsByFieldText[errorField.id] || [];
                     if (errorsByField[errorField.id].indexOf(message) == -1)
                     {
                        errorsByField[errorField.id].push(message);
                     }
                     if (errorsByFieldText[errorField.id].indexOf(textMessage) == -1)
                     {
                        errorsByFieldText[errorField.id].push(textMessage);
                     }
                  }

                  // Mark the first field with an error as the primary field (will be used if user tried to submit form)
                  if (primaryErrorFieldId == null)
                  {
                     primaryErrorFieldId = errorField.id;
                  }

                  if (suppressValidationNotification)
                  {
                     // There was a validation error for this field but it was configured to get displayed on another event
                     continue;
                  }

                  // Prepare error message including field name (the label in the dom)
                  fieldMsg = YAHOO.lang.trim(this.getFieldLabel(valFieldId)) || "";
                  if (fieldMsg.length > 0)
                  {
                     if (fieldMsg.indexOf(":") < 0)
                     {
                        fieldMsg += ":";
                     }
                     fieldMsg += " ";
                  }
                  fieldMsg += message;

                  // Update error indication css classes
                  if (validationType == INVALID)
                  {
		    
                     allErrors.push({ id: valFieldId, msg: fieldMsg });
                     // we don't want to add the same text
                     if (errorsByField[valFieldId].indexOf(message) == -1)
                     {
                        errorsByField[valFieldId].push(message);
                     }
                     // we don't want to add the same text
                     if (errorsByFieldText[valFieldId].indexOf(textMessage) == -1)
                     {
                        errorsByFieldText[valFieldId].push(textMessage);
                     }
                     
                     // Remove the mandatory class since it now is invalid
                     if (notificationLevel >= Alfresco.forms.Form.NOTIFICATION_LEVEL_FIELD)
                     {
                        YAHOO.util.Dom.removeClass(field, MANDATORY);
                        if (field == errorField)
                        {
                           YAHOO.util.Dom.addClass(field, INVALID);
                        }
                        else
                        {
                           YAHOO.util.Dom.removeClass(field, INVALID);
                        }

                        // Update message with the first validation failure (ALF-19012)
                        if (errorsByField[valFieldId].length == 0)
                        {
                           field.setAttribute("title", textMessage);
                           field.setAttribute(this._VALIDATION_MSG_ATTR, message);
                        }
                        else
                        {
                           field.setAttribute("title", errorsByFieldText[valFieldId][0]);
                           var validationMsgAttr = '';
                           for (var mess = 0; mess < errorsByField[valFieldId].length; mess++)
                           {
                              if (mess > 0 && !this.showMultipleErrors)
                              {
                                 break;
                              }
                              validationMsgAttr += '<div>' + errorsByField[valFieldId][mess] + '</div>';
                           }
                           field.setAttribute(this._VALIDATION_MSG_ATTR, validationMsgAttr);
                        }
                     }
                     
                  }
                  else
                  {
                     // Actual errors have higher priority than a "mandatory error"
                     if (errorsByField[valFieldId].length == 0)
                     {
                        if (notificationLevel >= Alfresco.forms.Form.NOTIFICATION_LEVEL_FIELD)
                        {
                           YAHOO.util.Dom.removeClass(field, INVALID);
                           if (field == errorField)
                           {
                              YAHOO.util.Dom.addClass(field, MANDATORY);
                           }
                           else
                           {
                              YAHOO.util.Dom.removeClass(field, INVALID);
                           }
                           field.setAttribute("title", message);
                        }
                        field.setAttribute(this._VALIDATION_MSG_ATTR, message);
                     }
                     warningsByField[valFieldId].push(message);
                     allWarnings.push({ id: valFieldId, msg: fieldMsg });
                  }

                  YAHOO.Bubbling.fire("formValidationError", {
                     msg: fieldMsg,
                     field: field,
                     type: validationType
                  });
               }
               else
               {
                  if (suppressValidationNotification)
                  {
                     // There was a validation error, but it was configured to be displayed on another event
                     continue;
                  }

                  if (notificationLevel >= Alfresco.forms.Form.NOTIFICATION_LEVEL_FIELD)
                  {

                     if (errorsByField[valFieldId].length == 0)
                     {
                        YAHOO.util.Dom.removeClass(field, INVALID);
                     }
                     if (warningsByField[valFieldId].length == 0)
                     {
                        YAHOO.util.Dom.removeClass(field, MANDATORY);
                     }
                     if (errorsByField[valFieldId].length == 0 && warningsByField[valFieldId].length == 0)
                     {
                        if (field.getAttribute("title") != val.originalTitle)
                        {
                           field.setAttribute("title", val.originalTitle);
                        }
                        field.setAttribute(this._VALIDATION_MSG_ATTR, "");
                        if (this.tooltips[valFieldId])
                        {
                           this.tooltips[valFieldId].html("");
                           this.tooltips[valFieldId].hide();
                        }
                     }
                  }
               }
            }
         }

         // Use focused field as context for the error container ...
         var errorFieldId = (document.activeElement && document.activeElement.id ? document.activeElement.id : null);
         if (notificationLevel >= Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER)
         {
            if ((event == null || event.type == "submit") && primaryErrorFieldId &&
                  (!warningsByField[errorFieldId] || warningsByField[errorFieldId].length == 0) &&
                  (!errorsByField[errorFieldId] || errorsByField[errorFieldId].length == 0))
            {
               // ... unless the user tried to submit the form, then use the forst field with an error
               errorFieldId = primaryErrorFieldId;
               var el = this.getFieldValidationEl(Dom.get(errorFieldId));
               if (Alfresco.util.isVisible(el))
               {
                  if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 9)
                  {
                     // No action - this causes a script error on IE8 and below.
                  }
                  else if (YAHOO.lang.isFunction(el.focus))
                  {
                     el.focus();
                  }
               }
            }

            if (this.errorContainer !== null && errorFieldId && errorsByField[errorFieldId])
            {
               if (this.errorContainer === "tooltip")
               {
                  /*
                  for (var key in this.tooltips)
                  {
                     if (this.tooltips.hasOwnProperty(key) && key != errorFieldId)
                     {
                        this.tooltips[key].hide();
                     }
                  }
                  */
                  if (errorsByField[errorFieldId].length > 0)
                  {
                     if (!this.tooltips[errorFieldId])
                     {
                        this.tooltips[errorFieldId] = Alfresco.util.createBalloon(this.getFieldValidationEl(errorFieldId), {
                           effectType: null,
                           effectDuration: 0
                        });
                     }

                     var html = '';
                     for (var i = 0; i < errorsByField[errorFieldId].length; i++)
                     {
                        if (i > 0 && !this.showMultipleErrors)
                        {
                           break;
                        }
                        html += '<div>' + errorsByField[errorFieldId][i] + '</div>';
                     }
                     this.tooltips[errorFieldId].html(html);
                     this.tooltips[errorFieldId].show();
                  }
                  else if (this.tooltips[errorFieldId])
                  {
                     this.tooltips[errorFieldId].html("");
                     this.tooltips[errorFieldId].hide();
                  }
               }
               else if (this.errorContainer === "alert" && allErrors.length > 0)
               {
                  var msg = '';
                  for (var i = 0; i < allErrors.length; i++)
                  {
                     if (i > 0 && !this.showMultipleErrors)
                     {
                        break;
                     }
                     msg += allErrors[i] + '\n';
                  }
                  alert(msg);
               }
               else if (YAHOO.lang.isString(this.errorContainer))
               {
                  var htmlNode = Dom.get(this.errorContainer);
                  if (htmlNode !== null)
                  {
                     var html = '';
                     for (var i = 0; i < allErrors.length; i++)
                     {
                        if (i > 0 && !this.showMultipleErrors)
                        {
                           break;
                        }
                        html += '<div><label for="' + allErrors[i].id + '">' + allErrors[i].msg + '</label></div>';
                     }
                     htmlNode.style.display = "block";
                     htmlNode.innerHTML = html;
                  }
               }
            }
         }

         this._valid = primaryErrorFieldId == null;

         // update submit elements state, if required
         if (this.showSubmitStateDynamically)
         {
            this._toggleSubmitElements(this._valid);
         }

         return this._valid;
      },

      getFieldValidationEl: function(fieldId)
      {
         var el = Dom.get(fieldId);
         if ((el.tagName.toLowerCase() == "input" && el.type == "hidden") || !Alfresco.util.isVisible(el))
         {
            return el.parentNode;
         }
         return el;
      },

      hideErrorContainer: function()
      {
         if (this.errorContainer === "tooltip")
         {
            for (var key in this.tooltips)
            {
               if (this.tooltips.hasOwnProperty(key))
               {
                  this.tooltips[key].hide();
               }
            }
         }
         else if (this.errorContainer === "alert")
         {
            // Not applicable
         }
         else if (YAHOO.lang.isString(this.errorContainer))
         {
            // todo Filter out errors for the field
         }
      },

      /**
       * Displays an internal form error message.
       * 
       * @method _showInternalError
       * @param msg {string} The error message to display
       * @param field {object} The element representing the field the error occurred on
       * @private 
       */
      _showInternalError: function(msg, field)
      {
         this.addError("Internal Form Error: " + msg, field);
      },

      reset: function()
      {
         this._resetAllVisitedFields();
         Dom.get(this.formId).reset();
         this.validate();
      }
   };
})();

(function()
{

   /**
    * Mandatory validation handler, tests that the given field has a value.
    *
    * @method mandatory
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.mandatory = function mandatory(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating mandatory state of field '" + field.id + "'");

      var valid = true;

      if (field.type && field.type == "radio")
      {
         //       wouldn't a radio button normally have a default
         //       'checked' option?

         var formElem = Dom.get(form.formId),
            radios = formElem[field.name],
            anyChecked = false;
         for (var x = 0, xx = radios.length; x < xx; x++)
         {
            if (radios[x].checked)
            {
               anyChecked = true;
               break;
            }

         }

         valid = anyChecked;
      }
      else if (field.type && field.type == "checkbox")
      {
         valid = field.checked;
      }
      else
      {
         valid = YAHOO.lang.trim(field.value).length !== 0;
      }

      return valid;
   };


   /**
    * Password match validation handler, tests that the given field's value matches another password field.
    *
    * @method passwordMatch
    * @param field {object} The element representing the field the validation is for
    * @param args {object}
    * @param args.el {HTMLElement|String} The input element that contains the element to coimpare against.
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.passwordMatch = function passwordMatch(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating password match of field '" + field.id + "' using args: " + YAHOO.lang.dump(args));
      if (YAHOO.lang.trim(field.value).length == 0)
      {
         return true;
      }

      var valid = false;
      var myArgs = YAHOO.lang.merge(
      {
         el: null
      }, args);

      if (myArgs.el)
      {
         var el = YAHOO.util.Dom.get(myArgs.el);
         if (el.value == field.value)
         {
            valid = true;
         }
      }

      return valid;
   };

   /**
    * Validate that the content of a password adheres to the policy provided in the supplied configuration
    * 
    * @method passwordContent
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Object providing the password content policy
    *        {
    *           minUpper: 1, // Minimum number of uppercase characters
    *           minLower: 1, // Minimum number of lowercase characters
    *           minNumeric: 1, // Minimum number of numeric characters
    *           minSymbols: 1 // Minimum number of special characters (!,@,#,$,%,^,&,*,?,_,~)
    *        }
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    * @returns {Boolean}   True if the password adheres to policy, false otherwise
    */
   Alfresco.forms.validation.passwordContent = function passwordContent(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating password content of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
      // Empty input elements should not be marked as invalid, leave this to the mandatory check
      if (YAHOO.lang.trim(field.value).length == 0)
      {
         return true;
      }
      var myArgs = YAHOO.lang.merge(
         {
            minUpper: 0,
            minLower: 0,
            minNumeric: 0,
            minSymbols: 0
         }, args),
         password = field.value, 
         DIGITS = /\d/g,
         SPECIAL_CHARS = /([!,@,#,$,%,^,&,*,?,_,~])/g,
         numeric = (password.match(DIGITS) || []).length,
         symbols = (password.match(SPECIAL_CHARS) || []).length,
         upper = 0, lower = 0, ch;
      for (var i = 0; i < password.length; i++)
      {
         ch = password.charAt(i);
         if (ch.toUpperCase() != ch.toLowerCase())
         {
            // Ok now we now it is an actual character (can't use regexp since they dont handle foreign characters)
            if (ch.toUpperCase() == ch)
            {
               upper++;
            }
            if (ch.toLowerCase() == ch)
            {
               lower++;
            }
         }
      }
      return myArgs.minUpper <= upper && myArgs.minLower <= lower && myArgs.minNumeric <= numeric && myArgs.minSymbols <= symbols;
   };

   /**
    * Length validation handler, tests that the given field's value has either
    * a minimum and/or maximum length.
    *
    * @method length
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Object representing the minimum and maximum length, and whether to crop content
    *        {
    *           min: 3,
    *           max: 10,
    *           crop: true
    *        }
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.length = function length(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating length of field '" + field.id +
                               "' using args: " + YAHOO.lang.dump(args));
      var valid = true;
      var myArgs = YAHOO.lang.merge(
      {
         min: -1,
         max: -1,
         crop: false,
         includeWhitespace: true,
         ignoreEmpty: false
      }, args);
      
      if (YAHOO.lang.trim(field.value).length == 0 && myArgs.ignoreEmpty)
      {
         return true;
      }

      if (myArgs.minLength)
      {
         myArgs.min = myArgs.minLength;
      }

      if (myArgs.maxLength)
      {
         myArgs.max = myArgs.maxLength;
      }

      var length = myArgs.includeWhitespace ? field.value.length : YAHOO.lang.trim(field.value).length;

      if (myArgs.min != -1 && length < myArgs.min)
      {
         valid = false;
      }

      if (myArgs.max != -1 && length > myArgs.max)
      {
         valid = false;
         if (myArgs.crop)
         {
            if (myArgs.includeWhitespace)
            {
               field.value = YAHOO.lang.trim(field.value);
            }
            if (field.value.length > myArgs.max)
            {
               field.value = field.value.substring(0, myArgs.max);
            }
            if (field.type && field.type == "textarea")
            {
               field.scrollTop = field.scrollHeight;
            }
            valid = true;
         }
      }

      return valid;
   };

   /**
    * Number validation handler, tests that the given field's value is a number.
    *
    * @method number
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Optional object containing a "repeating" flag
    *        {
    *           repeating: true
    *        }
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.number = function number(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a number");

      var repeating = false;

      // determine if field has repeating values
      if (args !== null && args.repeating)
      {
         repeating = true;
      }

      var valid = true;
      if (repeating)
      {
         // as it's repeating there could be multiple comma separated values
         var values = field.value.split(",");
         for (var i = 0; i < values.length; i++)
         {
            valid = (isNaN(values[i]) == false);

            if (!valid)
            {
               // stop as soon as we find an invalid value
               break;
            }
         }
      }
      else
      {
         valid = (isNaN(field.value) == false);
      }

      return valid;
   };

   /**
    * Number range validation handler, tests that the given field's value has either
    * a minimum and/or maximum value.
    *
    * @method numberRange
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Object representing the minimum and maximum value, and optionally
    *         a "repeating" flag, for example
    *        {
    *           min: 18,
    *           max: 30,
    *           repeating: true
    *        }
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.numberRange = function numberRange(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled()) {
         Alfresco.logger.debug("Validating number range of field '" + field.id + "' using args: " + YAHOO.lang.dump(args));
      }

      var valid = true;
      var value = field.value.toString();

      if (value.length > 0)
      {
         if (!Alfresco.forms.validation.number(field, args, null, null, true, null))
         {
            valid = false;
         }
         else
         {
            // ACE-3690: NaN instead of -1 to indicate a non-present value
            var min = NaN;
            var max = NaN;
            var repeating = false;

            if (args.min !== undefined)
            {
               min = parseInt(args.min);
            }

            if (args.minValue !== undefined)
            {
               min = parseInt(args.minValue);
            }

            if (args.max !== undefined)
            {
               max = parseInt(args.max);
            }

            if (args.maxValue !== undefined)
            {
               max = parseInt(args.maxValue);
            }

            // determine if field has repeating values
            if (args !== null && args.repeating)
            {
               repeating = true;
            }

            var valid = true;
            if (repeating)
            {
               // as it's repeating there could be multiple comma separated values
               var values = value.split(",");
               for (var i = 0; i < values.length; i++)
               {
                  if (min != -1 && values[i] < min)
                  {
                     valid = false;
                  }

                  if (max != -1 && values[i] > max)
                  {
                     valid = false;
                  }

                  if (!valid)
                  {
                     // stop as soon as we find an invalid value
                     break;
                  }
               }
            }
            else
            {
            if (!isNaN(min) && value < min)
               {
                  valid = false;
               }

            if (!isNaN(max) && value > max)
               {
                  valid = false;
               }
            }
         }
      }

      return valid;
   };

   /**
    * Node name validation handler, tests that the given field's value is a valid
    * name for a node in the repository.
    *
    * @method nodeName
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.nodeName = function nodeName(field, args, event, form)
   {
	   if (Alfresco.logger.isDebugEnabled())
		   Alfresco.logger.debug("Validating field '" + field.id + "' is a valid node name");
	   
	   return Alfresco.forms.validation.name(field, args, event, form);
   };
  
   /**
    * File name validation handler, tests that the given field's value is a valid
    * name for a node in the repository.
    *
    * @method fileName
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @param silent {boolean} Determines whether the user should be informed upon failure
    * @param message {string} Message to display when validation fails, maybe null
    * @static
    */
   Alfresco.forms.validation.fileName = function fileName(field, args, event, form)
   {
	   if (Alfresco.logger.isDebugEnabled())
		   Alfresco.logger.debug("Validating field '" + field.id + "' is a valid file name");
	   
	   return Alfresco.forms.validation.name(field, args, event, form);
   };
   
   
   /**
    * Name validation handler, tests that the given field's value is a valid
    * name for a node name or file name in the repository.
    *
    * @method nodeName
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.name = function name(field, args, event, form)
   {
	   if (!args)
	   {
		   args = {};
	   }
	   
	   /**
	    * Pattern for disallowing leading and trailing spaces. See CHK-6614
	    * args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)|(^[ \t]+|[ \t]+$)/;
	    */
	   
	   args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)|(^[ \t]+|[ \t]+$)/;
	   args.match = false;
	   
	   return Alfresco.forms.validation.regexMatch(field, args, event, form);
   };

   /**
    * Wiki page title validation handler, tests that the given field's value is a valid
    * name for a node in the repository.
    *
    * @method nodeName
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @param silent {boolean} Determines whether the user should be informed upon failure
    * @param message {string} Message to display when validation fails, maybe null
    * @static
    */
   Alfresco.forms.validation.wikiTitle = function wikiTitle(field, args, event, form, silent, message)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a valid node name");

      if (!args)
      {
         args = {};
      }
      
      args.pattern = /([#\\\?\/\|]+)|([\.]?[\.]+$)/;
      args.match = false;

      return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
   };

   /**
    * NodeRef validation handler, tests that the given field's value is a valid
    * nodeRef identifier for a node in the repository.
    *
    * @method nodeRef
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.nodeRef = function nodeRef(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a valid noderef");

      if (!args)
      {
         args = {};
      }

      args.pattern = /^[^\:^ ]+\:\/\/[^\:^ ]+\/[^ ]+$/;

      return Alfresco.forms.validation.regexMatch(field, args, event, form);
   };


   /**
    * Email validation handler, tests that the given field's value is a valid
    * email address.
    *
    * @method email
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.email = function email(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a valid email address");

      if (!args)
      {
         args = {};
      }

      var valid = true;
      if (field.value && field.value.length > 0)
      {
         //Strip out the name and surrounding angle brackets, if present.
         var start = field.value.indexOf("<");
         if (start >= 0)
         {
            start = start + 1;
            var end = field.value.lastIndexOf(">");
            if (end > start)
            {
               field.value = field.value.substr(start, (end -start));
            }
         }

         var emailFirstCharIndex = field.value.indexOf('"');

          /* MNT-7031 fix. Added validation according RFC 2822.
           If email local part starts with " (double quote) pattern for special characters space and
           "(),:;<>@[\] (ASCII: 32, 34, 40, 41, 44, 58, 59, 60, 62, 64, 91–93) with restrictions is used.
           Otherwise pattern for uppercase and lowercase English letters, digits,
           characters  !#$%&'*+-/=?^_`{|}~  ((ASCII: 33, 35–39, 42, 43, 45, 47, 61, 63, 94–96, 123–126)) , dot is used
           */
         if (emailFirstCharIndex == 0)
         {
         	args.pattern = /^("[-A-Za-z0-9\xc0-\xff\(\)<>\[\]\\:,;@\".\" *"!#$%&'*+\/=?^_`{}~|]*")+@([\.A-Za-z0-9_-])*[A-Za-z0-9_-]$/;
         }
         else 
         {
         	args.pattern = /^([-A-Za-z0-9\xc0-\xff!#$%&'*+\/=?^_`{}~|]+\.)*[-A-Za-z0-9\xc0-\xff!#$%&'*+\/=?^_`{}|~_]+@([\.A-Za-z0-9_-])*[A-Za-z0-9_-]$/;
         }
         
         args.match = true;

         valid = Alfresco.forms.validation.regexMatch(field, args, event, form);
      }
      return valid;
   };
   
   /**
    * Phone validation handler
    *
    * @method phone
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @param silent {boolean} Determines whether the user should be informed upon failure
    * @param message {string} Message to display when validation fails, maybe null
    * @static
    */
   Alfresco.forms.validation.phone = function phone(field, args, event, form, silent, message)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a valid phone number");

      if (!args)
      {
         args = {};
      }

      args.pattern = /^[0-9\(\)\[\]\-\+\*#\\:\/,; ]+$/;
      args.match = true;

      return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
   };


   /**
    * Time validation handler, tests that the given field's value is a valid time value.
    *
    * @method time
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.time = function time(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a valid time value");

      if (!args)
      {
         args = {};
      }

      args.pattern = /^([0-1]\d|2[0-3]):[0-5]\d(:[0-5]\d)?$/;
      args.match = true;

      return Alfresco.forms.validation.regexMatch(field, args, event, form);
   };

   /**
    * URL validation handler, tests that the given field's value is a valid URL
    *
    * @method url
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.url = function url(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a valid URL");

      var expression = /(ftp|http|https):\/\/[\w\-_]+(\.[\w\-_]+)*([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/,
         valid = true;

      if (field.value.length > 0)
      {
         // Check an empty string replacement returns an empty string
         var pattern = new RegExp(expression);
         valid = field.value.replace(pattern, "") === "";
      }

      return valid;
   };


   /**
    * Regular expression validation handler, tests that the given field's value matches
    * the supplied regular expression.
    *
    * @method regexMatch
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Object representing the expression.
    * The args object should have the form of:
    * {
    *    pattern: {regexp}, // A regular expression
    *    match: {boolean}   // set to false if the regexp should NOT match the input, default is true
    * }
    * An example to validate a field represents an email address can look like:
    * {
    *    pattern: /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/
    * }
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.regexMatch = function regexMatch(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating regular expression of field '" + field.id +
                               "' using args: " + YAHOO.lang.dump(args));
      var valid = true;

      if (field.value && field.value.length > 0)
      {
         // The pattern SHOULD match by default
         if (args.match === undefined)
         {
             args.match = true;
         }

         // Check if the patterns match
         var pattern = new RegExp(args.pattern);
         valid = pattern.test(field.value);

         // Adjust the result if the test wasn't intended to match
         if (!args.match)
         {
            valid = !valid;
         }
      }

      return valid;
   };


   /**
    * Repository regular expression handler, simply used as a pass through to the
    * standard regexMatch handler after converting the paramater names.
    *
    * @method repoRegexMatch
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.repoRegexMatch = function repoRegexMatch(field, args, event, form)
   {
      // convert parameters
      args.pattern = args.expression;
      args.match = args.requiresMatch;

      // call the standard regex handler
      return Alfresco.forms.validation.regexMatch(field, args, event, form);
   };

   /**
    * Validation handler for a valid date and time, currently this simply looks for the
    * presence of the 'invalid' class applied to the relevant field. This implies that this
    * validation handler must be added after any other handlers that determine validity.
    *
    * @method validDateTime
    * @param field {object} The element representing the field the validation is for
    * @param args {object} Not used
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.validDateTime = function validDateTime(field, args, event, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' has a valid date and time");

      return !YAHOO.util.Dom.hasClass(field, "invalid");
   };

   /**
    * Validation handler for the repository 'list of values' constraint. As the UI
    * handles this by displaying the list of allowable values this handler is a dummy
    * placeholder.
    *
    * @method listOfValues
    * @param field {object} The element representing the field the validation is for
    * @param args {object} The list of allowable values
    * @param event {object} The event that caused this handler to be called, maybe null
    * @param form {object} The forms runtime class instance the field is being managed by
    * @static
    */
   Alfresco.forms.validation.inList = function inList(field, args, event, form)
   {
      return true;
   };

})();
