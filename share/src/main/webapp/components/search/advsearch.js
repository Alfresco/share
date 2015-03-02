/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * Advanced Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AdvancedSearch
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Bubbling = YAHOO.Bubbling;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Advanced Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AdvancedSearch} The new AdvancedSearch instance
    * @constructor
    */
   Alfresco.AdvancedSearch = function(htmlId)
   {
      Alfresco.AdvancedSearch.superclass.constructor.call(this, "Alfresco.AdvancedSearch", htmlId, ["button", "container"]);
      
      Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      Bubbling.on("afterFormRuntimeInit", this.onAfterFormRuntimeInit, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.AdvancedSearch, Alfresco.component.Base,
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
          * Current siteId
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Search Form objects, for example:
          * {
          *    id: "advanced-search",
          *    type: "cm:content",
          *    label: "Content",
          *    description: "All types of content"
          * }
          * 
          * @property searchForms
          * @type Array
          */
         searchForms: [],
         
         /**
          * Previously saved query, if any
          * 
          * @property savedQuery
          * @type string
          */
         savedQuery: "",
         
         /**
          * It is possible to disable searching entire repo via config
          * 
          * @property searchScope - "repo" or "all_sites" or a siteid
          * @type string
          */
         searchScope: ""
      },
      
      /**
       * Currently visible Search Form object
       */
      currentForm: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ADVSearch_onReady()
      {
         var me = this,
            domId = this.id + "-form-list",
            elList = Dom.get(domId);
         
         // see if a saved query json string is provided
         var defaultForm = this.options.searchForms[0];
         if (this.options.savedQuery.length !== 0)
         {
            var savedQuery = YAHOO.lang.JSON.parse(this.options.savedQuery);
            if (savedQuery.datatype)
            {
               for (var f in this.options.searchForms)
               {
                  var form = this.options.searchForms[f];
                  if (form.type === savedQuery.datatype)
                  {
                     // found previous form datatype - use as first form to display
                     defaultForm = form;
                     break;
                  }
               }
            }
         }
         
         // search YUI button and menus
         this.widgets.searchButton1 = Alfresco.util.createYUIButton(this, "search-button-1", this.onSearchClick);
         this.widgets.searchButton2 = Alfresco.util.createYUIButton(this, "search-button-2", this.onSearchClick);

         this.widgets.formButton = Alfresco.util.createYUIButton(this, "selected-form-button", function(p_sType, p_aArgs)
         {
            // update selected item menu button label
            var form = this.options.searchForms[p_aArgs[1].index];
            this.widgets.formButton.set("label", form.label + " " + Alfresco.constants.MENU_ARROW_SYMBOL);
            this.widgets.formButton.set("title", form.description);

            // render the appropriate form template
            this.renderFormTemplate(form);
         },
         {
            label: defaultForm.label + " " + Alfresco.constants.MENU_ARROW_SYMBOL,
            title: defaultForm.description,
            type: "menu",
            menu: "selected-form-list"
         });

         // render initial form template
         this.renderFormTemplate(defaultForm, true);
         
         // register the "enter" event on the search text field
         var queryInput = Dom.get(this.id + "-search-text");
         this.widgets.enterListener = new YAHOO.util.KeyListener(queryInput, 
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, 
         {
            fn: me._searchEnterHandler,
            scope: this,
            correctScope: true
         }, "keydown").enable();
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      /**
       * DEFAULT ACTION EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Loads or retrieves from cache the Form template for a given content type
       * 
       * @method renderFormTemplate
       * @param form {Object} Form descriptor to render template for
       * @param repopulate {boolean} If true, repopulate form instance based on supplied data
       */
      renderFormTemplate: function ADVSearch_renderFormTemplate(form, repopulate)
      {
         // update current form state
         this.currentForm = form;
         this.currentForm.repopulate = repopulate;
         
         var containerDiv = Dom.get(this.id + "-forms");
         
         var visibleFormFn = function()
         {
            // hide visible form if any
            for (var i=0, c=containerDiv.children; i<c.length; i++)
            {
               if (!Dom.hasClass(c[i], "hidden"))
               {
                  Dom.addClass(c[i], "hidden");
                  break;
               }
            }
            
            // display cached form element
            Dom.removeClass(form.htmlid, "hidden");
            
            // reset focus to search input textbox
            Dom.get(this.id + "-search-text").focus();
         };
         
         if (!form.htmlid)
         {
            // generate child container div for this form
            var htmlid = this.id + "_" + containerDiv.children.length;
            var formDiv = document.createElement("div");
            formDiv.id = htmlid;
            Dom.addClass(formDiv, "hidden");
            Dom.addClass(formDiv, "share-form");
            
            // cache htmlid so we know the form is present on the form
            form.htmlid = htmlid;
            
            // load the form component for the appropriate type
            var formUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind=type&itemId={itemId}&formId={formId}&mode=edit&showSubmitButton=false&showCancelButton=false",
            {
               itemId: form.type,
               formId: form.id
            });
            var formData =
            {
               htmlid: htmlid
            };
            Alfresco.util.Ajax.request(
            {
               url: formUrl,
               dataObj: formData,
               successCallback:
               {
                  fn: function ADVSearch_onFormTemplateLoaded(response)
                  {
                     // Inject the template from the XHR request into the child container div
                     formDiv.innerHTML = response.serverResponse.responseText;
                     containerDiv.appendChild(formDiv);
                     
                     visibleFormFn.call(this);
                  },
                  scope: this
               },
               failureMessage: "Could not load form component '" + formUrl + "'.",
               scope: this,
               execScripts: true
            });
         }
         else
         {
            visibleFormFn.call(this);
         }
      },
      
      /**
       * Repopulate currently displayed Form fields based on saved query data
       *
       * @method repopulateCurrentForm
       */
      repopulateCurrentForm: function ADVSearch_repopulateCurrentForm()
      {
         if (this.options.savedQuery.length !== 0)
         {
            var savedQuery = YAHOO.lang.JSON.parse(this.options.savedQuery);
            var elForm = Dom.get(this.currentForm.runtime.formId);
            
            for (var i = 0, j = elForm.elements.length; i < j; i++)
            {
               var element = elForm.elements[i];
               var name = element.name;
               if (name != undefined && name !== "-")
               {
                  var savedValue = savedQuery[name];
                  if (savedValue !== undefined)
                  {
                     if (element.type === "checkbox" || element.type === "radio")
                     {
                        element.checked = (savedValue === "true");
                     }
                     else if (name.match("-range$") == "-range")
                     {
                        // found number range?
                        var cntrl = Dom.get(element.id + "-cntrl-min");
                        if (cntrl)
                        {
                           // populate number range elements
                           cntrl.value = savedValue.substring(0, savedValue.indexOf("|"));
                           cntrl = Dom.get(element.id + "-cntrl-max");
                           cntrl.value = savedValue.substring(savedValue.indexOf("|") + 1, savedValue.length);
                           // set range value to the input hidden field
                           cntrl = Dom.get(element.id);
                           cntrl.value = savedValue;                           
                        }
                        else
                        {
                           // probably date range - just set value and control will handle it
                           element.value = savedValue;
                        }
                     }
                     else
                     {
                        // standard html control
                        element.value = savedValue;
                     }
                     
                     // reverse value setting doesn't work with checkboxes or multi-select boxes because of the 
                     // hidden field used to store the underlying field value
                     if (element.type === "hidden")
                     {
                     	// hidden fields could be a part of a checkbox or similar in the Forms runtime
                     	// so look if there is a entry element attached this hidden field and set the value
                     	var cntrl = Dom.get(element.id + "-entry");
                     	if (cntrl)
                     	{
                     	   switch (cntrl.type)
                     	   {
                     	      case "checkbox":
                           	   cntrl.checked = (savedValue === "true");
                           	   break;
                           	default: // "select-multiple" - and potentially others following the same pattern
                           	   cntrl.value = savedValue;
                           	   break;
                        	}
                        }
                     }
                  }
               }
            }
            
            Bubbling.fire("formContentsUpdated");
         }
      },
      
      /**
       * Event handler that gets fired when user clicks the Search button.
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       */
      onSearchClick: function ADVSearch_onSearchClick(e, obj)
      {
         // retrieve form data structure directly from the runtime
         var formData = this.currentForm.runtime.getFormData();
         
         // add DD type to form data structure
         formData.datatype = this.currentForm.type;
         
         // build and execute url for search page
         // 'this.options.searchPath' contains {variable} replacement points for substitution
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + this.options.searchPath,
         {
            site: (this.options.siteId.length !== 0 ? ("site/" + this.options.siteId + "/") : ""),
            terms: encodeURIComponent(Dom.get(this.id + "-search-text").value),
            query: encodeURIComponent(YAHOO.lang.JSON.stringify(formData)),
            scope: this.options.searchScope.toString()
         });
         
         window.location.href = url;
      },
      
      /**
       * Event handler called when the "beforeFormRuntimeInit" event is received
       */
      onBeforeFormRuntimeInit: function ADVSearch_onBeforeFormRuntimeInit(layer, args)
      {
         // extract the current form runtime - so we can reference it later
         this.currentForm.runtime = args[1].runtime;
         
         // remove Forms Runtime validators on the advanced search form
         this.currentForm.runtime.validations = {};
         var nodes = this.currentForm.runtime.getFieldsByType("select");
         for (var i = 0, ii = nodes.length; i < ii; i++)
         {	    
            if (nodes[i].classList.contains("non-tokenised"))
            {
               var options = nodes[i].options;
               for (var j = 0, jj = options.length; j < jj; j++)
               {
                  if (options[j].value != "")
                  {
                     options[j].value = '\"' + options[j].value + '\"';
                  }
               }
            }
         }
         
         // Repopulate current form from url query data?
         if (this.currentForm.repopulate)
         {
            this.currentForm.repopulate = false;
            this.repopulateCurrentForm();
         }
      },

      /**
       * Event handler called when the "afterFormRuntimeInit" event is received
       */
      onAfterFormRuntimeInit: function ADVSearch_onAfterFormRuntimeInit(layer, args)
      {
         // extract the current form runtime - so we can reference it later
         this.currentForm.runtime = args[1].runtime;
         var form = (Dom.get(this.currentForm.runtime.formId));
         Event.removeListener(form, "submit");
         form.setAttribute("onsubmit", "return false;");
      },

      /**
       * Search text box ENTER key event handler
       * 
       * @method _searchEnterHandler
       */
      _searchEnterHandler: function ADVSearch__searchEnterHandler(e, args)
      {
         this.onSearchClick(e, args);
      }
   });
})();