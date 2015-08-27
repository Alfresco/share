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
 * Alfresco.EventInfo
 */

// TODO: Event element IDs need cleaning up.


(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener,
      $combine = Alfresco.util.combinePaths,
      $html = Alfresco.util.encodeHTML,
      fromISO8601 = Alfresco.util.fromISO8601,
      toISO8601 = Alfresco.util.toISO8601,
      formatDate = Alfresco.util.formatDate,
      // These help with the confusing scope levels here, to be defined later.
      EventInfo,
      EditDialog,
      CalendarView;

   Alfresco.EventInfo = function(containerId)
   {
      this.name = "Alfresco.EventInfo";
      this.id = containerId;

      this.panel = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.EventInfo.prototype =
   {
      /**
       * EventInfo instance.
       *
       * @property panel
       * @type Alfresco.EventInfo
       */
      panel: null,
      
      /**
       * A reference to the current event. 
       *
       * @property event
       * @type object
       */
      event: null,
      
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Callback called when the event info panel is closed.
          *
          * @property onClose callback object with fn, scopt & obj attributes
          * @type {object}
          */
         onClose: null,
         eventUri: null,
         displayDate: null
      },      
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function EventInfo_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function EventInfo_onComponentsLoaded()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Renders the event info panel. 
       *
       * @method show
       * @param event {object} JavaScript object representing an event
       */
      show: function EventInfo_show(event)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/info",
            dataObj:
            {
               "htmlid": this.id,
               "uri": event.uri 
            },
            
            successCallback:
            {
               fn: this.templateLoaded,
               scope: this
            },
            failureMessage: "Could not load event info panel",
            execScripts: true
         });

         this.event = event;
      },

      /**
       * Fired when the event info panel has loaded successfully.
       *
       * @method templateLoaded
       * @param response {object} DomEvent
       */
      templateLoaded: function EventInfo_templateLoaded(response)
      {
         var div = Dom.get("eventInfoPanel");
         div.innerHTML = response.serverResponse.responseText;

         this.panel = Alfresco.util.createYUIPanel(div,
         {
            width: "35em",
            zIndex: 10
         });
         this.widgets = this.widgets || {};
         // Buttons
         this.widgets.deleteButton = Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick);
         this.widgets.editButton = Alfresco.util.createYUIButton(this, "edit-button", this.onEditClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);
         this.widgets.escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelClick();
            },
            scope: this,
            correctScope: true
         });
         this.widgets.escapeListener.enable();
         if (Dom.get(this.id+"-edit-available") == null)
         {
            if (this.widgets.deleteButton)
            {
               this.widgets.deleteButton.set("disabled", true);
            }
            if (this.widgets.editButton)
            {
               this.widgets.editButton.set("disabled", true);
            }

         }
         //convert iso date to readable human text
         var dateElIds = [this.id+'-startdate',this.id+'-enddate'];
         for (var i=0,len=dateElIds.length;i<len;i++)
         {
            var dateTextEl = Dom.get(dateElIds[i]);
            var date = fromISO8601(dateTextEl.innerHTML);
            //only show date for allday events otherwise show time too
            if (Dom.hasClass(dateTextEl, "allDayEvent"))
            {
               dateTextEl.innerHTML = formatDate(date, Alfresco.util.message("date-format.fullDate"));
            }
            else 
            {
               dateTextEl.innerHTML = formatDate(date, Alfresco.util.message("date-format.fullDateTime"));
            }
         }
         //decode html for text values of event
         var textData = Selector.query('.yui-gd .yui-u', div);
         for (var i=1;i<6;i+=2)
         {
            textData[i].innerHTML = Alfresco.util.decodeHTML(textData[i].innerHTML);
         }
         // Display the panel
         this.panel.show();
      },
      
      /**
       * Fired when the use selected the "Cancel" button.
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       */
      onCancelClick: function EventInfo_onCancelClick(e)
      {
         this._hide();
      },
      
      /**
       * Fired when the user selects the "Edit" button.
       *
       * @method onEventClick
       * @param e {object} DomEvent
       */
      onEditClick: function(e)
      {
         if (this.isShowing) 
         {
            this._hide();
         }
         this.eventDialog = this.initEditDialog();
         this.eventDialog.show();
      },

      /**
       * Called when an event edit API call returns successfully.
       *
       * @method onEdited
       * @param o {object} response from server
       */
      onEdited: function(o)
      {
         // Check that there isn't an error in the response body
         if (!o.json.error)
         {
            YAHOO.Bubbling.fire('eventEdited',
            {
               id: this.options.event, // so we know which event we are dealing with
               data: o.json.data
            });
         }
         // if there is an error, it didn't work, despite the 200 response.
         else
         {
            this.onEditFailed(o)
         }

         // Tidy up after ourselves.
         if (this.panel)
         {
            this.panel.hide();
            this.panel.destroy();
         }
         this.eventDialog.dialog.destroy();
      },

      /**
       * Called when an event could not be edited.
       *
       * @method onEditFailed
       * @param o
       */
      onEditFailed: function(o)
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message('message.edited.failure','Alfresco.CalendarView')
         });
      },

      /**
       * Fired when the delete is clicked. Kicks off a DELETE request
       * to the Alfresco repo to remove an event.
       * 
       * Also triggered by the delete action link in the Agenda DataTable.
       *
       * @method onDeleteClick
       * @param e {object} DomEvent
       */
      onDeleteClick: function EventInfo_onDeleteClick(e)
      {
         var me = this,
            displayDate = formatDate(fromISO8601(this.event.startAt.iso8601), this._msg("date-format.fullDate"));

         Alfresco.util.PopupManager.displayPrompt(
         {
            noEscape: true,
            title: this._msg("message.confirm.delete.title"),
            text: this._msg("message.confirm.delete", $html(this.event.title), displayDate),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function EventInfo_onActionDelete_delete()
               {
                  this.destroy();
                  me._onDeleteConfirm.call(me);
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function EventInfo_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });

         var elements = Dom.getElementsByClassName('yui-button', 'span', 'prompt');
         Dom.addClass(elements[0], 'alf-primary-button');
      },

      /**
       * Delete Event confirmed.
       * Kicks off a DELETE request to the Alfresco repo to remove an event.
       *
       * @method _onDeleteConfirm
       * @private
       */
      _onDeleteConfirm: function EventInfo_onDeleteConfirm()
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + this.event.uri + "&page=calendar",
            successCallback:
            {
               fn: this.onDeleted,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure", this.event.title)
         });
      },

      /**
       * Called when an event is successfully deleted.
       *
       * @method onDeleted
       * @param e {object} DomEvent
       */
      onDeleted: function EventInfo_onDeleted(e)
      {
         this._hide();
         Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.delete.success", this.event.title)
         });
         YAHOO.Bubbling.fire('eventDeleted',
         {
            id: this.options.event // so we know which event we are dealing with
         });
         if (this.panel)
         {
            this.panel.destroy();
         }
      },

      /**
       * Creates the Edit dialog
       * Notes: This used to be spread over two functions and part of Alfresco.util.DialogManager
       *
       * @method initEditDialog
       * @param optionOverrides {object} overrides config options
       * @return {object} Alfresco.module.SimpleDialog object
       */
      initEditDialog: function EventInfo_createEditDialog(optionOverrides)
      {

         EventInfo = this;
         EditDialog = new Alfresco.module.SimpleDialog();
         CalendarView = Alfresco.util.ComponentManager.findFirst("Alfresco.CalendarView");

         // Sets Default Options
         EditDialog.setOptions(
         {
            site: EventInfo.options.siteId,
            displayDate:EventInfo.options.displayDate,
            width: "42em",
            actionUrl: Alfresco.constants.PROXY_URI + EventInfo.options.eventUri + "&page=calendar",
            ajaxSubmitMethod: Alfresco.util.Ajax.PUT,
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
            templateRequestParams: {
               site: EventInfo.options.siteId,
               uri: '/'+EventInfo.options.eventUri || ""
            },
            doBeforeFormSubmit:
            {
               fn: function EditDialog_doBeforeFormSubmit(form, obj)
               {
                  // Update the tags set in the form
                  EditDialog.tagLibrary.updateForm(EditDialog.id + "-form", "tags");

                  // Avoid submitting the input field used for entering tags
                  var tagInputElem = Dom.get(EditDialog.id + "-tag-input-field");
                  if (tagInputElem)
                  {
                     tagInputElem.disabled = true;
                  }
                  var errorEls = Dom.getElementsByClassName('error',null,Dom.get(EditDialog.id + "-form"));

                  for (var i = 0; i <errorEls.length;i++)
                  {
                     Dom.removeClass(errorEls[i],'error');
                  }
                  Dom.get(EditDialog.id+'-title').disabled = false;
                  Dom.get(EditDialog.id+'-location').disabled = false;

                  // Disable buttons before submit to avoid double submits
                  EditDialog.widgets.okButton.set("disabled", true);
                  EditDialog.widgets.cancelButton.set("disabled", true);

                  // Update time in submission fields
                  // NOTE: this uses the user's current timezone and stores it as a UTC offset.
                  var startTimeEl = document.getElementsByName("start")[0],
                     startTime = Alfresco.util.parseTime(startTimeEl.value),
                     endTimeEl = document.getElementsByName("end")[0],
                     endTime = Alfresco.util.parseTime(endTimeEl.value),
                     startAtEl = document.getElementsByName("startAt")[0],
                     endAtEl = document.getElementsByName("endAt")[0],
                     startDate = fromISO8601(startAtEl.value),
                     endDate = fromISO8601(endAtEl.value);

                  startDate.setHours(startTime.getHours(), startTime.getMinutes());
                  endDate.setHours(endTime.getHours(), endTime.getMinutes());

                  Dom.setAttribute(startAtEl, "value", toISO8601(startDate))
                  Dom.setAttribute(endAtEl, "value", toISO8601(endDate))

                  // Make sure form submit method is correct:
                  EditDialog.form.setAjaxSubmitMethod(EditDialog.options.ajaxSubmitMethod);

               },
               scope:EditDialog
            },
            doBeforeAjaxRequest:
            {
               fn: function EditDialog_doBeforeAjaxRequest(p_config, p_obj)
               {
                  var isAllDay = document.getElementsByName('allday').checked===true;
                  var startEl = document.getElementsByName('start')[0];
                  var endEl = document.getElementsByName('end')[0];

                  if (p_config.dataObj.tags)
                  {
                     p_config.dataObj.tags = p_config.dataObj.tags.join();
                  }

                  //all day
                  if (YAHOO.lang.isUndefined(p_config.dataObj.start))
                  {
                     p_config.dataObj.start = startEl.value;
                     p_config.dataObj.end = endEl.value;
                  }
                  // if times start and end at 00:00 and not allday then add 1 hour
                  if (!isAllDay && (p_config.dataObj.start == '00:00' && p_config.dataObj.end =='00:00') )
                  {
                     p_config.dataObj.end = '01:00';
                  }

                  // Check 

                  return true;
               },
               scope: EditDialog
            },
            doBeforeDialogShow:
            {
               fn: function EditDialog_doBeforeDialogShow()
               {

                  var editEvent = EventInfo.event,
                     startDate  = (editEvent)? fromISO8601(editEvent.startAt.iso8601) : this.options.displayDate,
                     endDate  = (editEvent)? fromISO8601(editEvent.endAt.iso8601) : this.options.displayDate;
                  
                  // Pretty formatting
                  Alfresco.CalendarHelper.writeDateToField(startDate, Dom.get("fd"));
                  Alfresco.CalendarHelper.writeDateToField(endDate, Dom.get("td"));
                  Dom.get(EditDialog.id+"-startAt").value =  toISO8601(startDate);
                  Dom.get(EditDialog.id+"-endAt").value = toISO8601(endDate);

                  //init taglib
                  var tagInputEl = Dom.get(EditDialog.id + "-tag-input-field");

                  tagInputEl.disabled=false;
                  tagInputEl.tabIndex = 9;
                  Dom.get(EditDialog.id + "-add-tag-button").tabIndex = 10;

                  var tags = tagInputEl.value;
                  tagInputEl.value = '';
                  EditDialog.tagLibrary.setTags(tags.split(","));

                  // Reset errors
                  EditDialog.form.errorContainer = null;

                  // Time boxes should be hidden if it's not an all day event
                  var startTimeEl = document.getElementsByName('start')[0],
                     endTimeEl = document.getElementsByName('end')[0];
                  if (document.getElementsByName('allday')[0].checked===true)
                  {
                     // hide time boxes if they're not relevant.
                     Dom.addClass(startTimeEl.parentNode, "hidden")
                     Dom.addClass(endTimeEl.parentNode, "hidden")
                  } else
                  {
                     // show them if they are.
                     Dom.removeClass(startTimeEl.parentNode, "hidden")
                     Dom.removeClass(endTimeEl.parentNode, "hidden")
                  }

                  // Make sure the time is correct if we're editing an event.
                  if (editEvent)
                  {
                     Dom.setAttribute(startTimeEl, "value", formatDate(startDate, this.msg("date-format.shortTime")));
                     Dom.setAttribute(endTimeEl, "value", formatDate(endDate, this.msg("date-format.shortTime")));
                  }
                  else
                  {
                     var defaultStartTime = Alfresco.util.parseTime(startTimeEl.value),
                     defaultEndTime = Alfresco.util.parseTime(endTimeEl.value);
                     Dom.setAttribute(startTimeEl, "value", formatDate(defaultStartTime, this.msg("date-format.shortTime")));
                     Dom.setAttribute(endTimeEl, "value", formatDate(defaultEndTime, this.msg("date-format.shortTime")));
                  }

                  // hide mini-cal
                  EditDialog.dialog.hideEvent.subscribe(function()
                  {
                     if (CalendarView && CalendarView.oCalendar)
                     {
                        CalendarView.oCalendar.hide();
                     }
                  }, EditDialog, true);

                  // Decode strings in all text based fields
                  var a = ['what','where','desc'];
                  for (var i=0;i<a.length;i++)
                  {
                     var el = document.getElementsByName(a[i])[0];
                     el.value = Alfresco.util.decodeHTML(el.value);
                  }
                  
                  // Style button
                  Dom.addClass(this.widgets.okButton._button.parentElement.parentElement, "alf-primary-button");
                  this.widgets.okButton._button.innerHTML = this.msg("button.save");
               },
               scope: EditDialog
            },
            doSetupFormsValidation:
            {
               fn: function EditDialog_doSetupFormsValidation(form)
               {

                  // Validation Regular Expressions
                  // TODO: This should use validation from Forms service
                  // validate text fields
                  var validateTextRegExp =
                  {
                     pattern: /({|})/,
                     match: false
                  };

                  var textElements = [EditDialog.id + "-title", EditDialog.id + "-location", EditDialog.id + "-description"];
                  form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "blur");
                  form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "keyup");

                  for (var i = 0; i < textElements.length; i++)
                  {
                     form.addValidation(textElements[i], Alfresco.forms.validation.regexMatch, validateTextRegExp, "blur");
                     form.addValidation(textElements[i], Alfresco.forms.validation.regexMatch, validateTextRegExp, "keyup");
                  }

                  EditDialog.tagLibrary.initialize(form);

                  var dateElements = ["td", "fd", EditDialog.id + "-start", EditDialog.id + "-end"];
                  for (var i = 0; i < dateElements.length; i++)
                  {
                     form.addValidation(dateElements[i], EventInfo._onDateValidation,
                     {
                        "obj": EditDialog
                     }, "blur");
                  }

                  // Setup date validation
                  form.addValidation("td", EventInfo._onDateValidation,
                  {
                     "obj": EditDialog
                  }, "focus");
                  form.addValidation("fd", EventInfo._onDateValidation,
                  {
                     "obj": EditDialog
                  }, "focus");

                  form.setSubmitElements(EditDialog.widgets.okButton);

                  /**
                   * keyboard handler for popup calendar button. Requried as YUI button's click
                   * event doesn't fire in firefox
                   */
                  var buttonKeypressHandler = function()
                  {
                     return function(e)
                     {
                        if (e.keyCode===KeyListener.KEY['ENTER'])
                        {
                           EventInfo.onDateSelectButton.apply(EditDialog,arguments);
                           return false;
                        }
                     };
                  }();

                  var browseButton = Alfresco.util.createYUIButton(EditDialog, "browse-button", function()
                  {
                     if (EditDialog.browsePanel)
                     {
                        delete EditDialog.browsePanel;
                     }
                     EditDialog.hide();

                     // Show site title, if possible:
                     var titleModule = Alfresco.util.ComponentManager.findFirst("Alfresco.CollaborationTitle"),
                        siteTitle = (titleModule && titleModule.options) ? titleModule.options.siteTitle : EditDialog.options.site ;

                     Alfresco.util.Ajax.request(
                     {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/browse-docfolder",
                        dataObj: {
                           site: siteTitle
                        },
                        successCallback:
                        {
                           fn: function(response)
                           {

                              var containerDiv = document.createElement("div");
                              containerDiv.innerHTML = response.serverResponse.responseText;
                              var panelDiv = Dom.getFirstChild(containerDiv);
                              EditDialog.browsePanel = Alfresco.util.createYUIPanel(panelDiv);
                              var selectedDocfolder = Dom.get(EditDialog.id + "-docfolder").value;

                              Alfresco.util.createYUIButton(EditDialog.browsePanel, "ok", function()
                              {
                                 if (selectedDocfolder.charAt(selectedDocfolder.length - 1) == '/')
                                 {
                                    selectedDocfolder = selectedDocfolder.substring(0, selectedDocfolder.length - 1);
                                 }
                                 Dom.get(EditDialog.id + "-docfolder").value = selectedDocfolder;
                                 EditDialog.browsePanel.hide();
                                 EditDialog.dialog.show();
                              });

                              Alfresco.util.createYUIButton(EditDialog.browsePanel, "cancel", function()
                              {
                                 EditDialog.browsePanel.hide();
                                 EditDialog.dialog.show();
                              });

                              Alfresco.util.createTwister("twister");
                              var tree = new YAHOO.widget.TreeView("treeview");
                              tree.setDynamicLoad(function(node, fnLoadComplete)
                              {
                                 var nodePath = node.data.path;
                                 var uri = Alfresco.constants.PROXY_URI + "slingshot/doclib/treenode/site/" + $combine(encodeURIComponent(CalendarView.options.siteId), encodeURIComponent("documentLibrary"), Alfresco.util.encodeURIPath(nodePath));
                                 var callback =
                                 {
                                    success: function(oResponse)
                                    {
                                       var results = YAHOO.lang.JSON.parse(oResponse.responseText), item, treeNode;
                                       if (results.items)
                                       {
                                          for (var i = 0, j = results.items.length; i < j; i++)
                                          {
                                             item = results.items[i];
                                             item.path = $combine(nodePath, item.name);
                                             treeNode = EventInfo._buildTreeNode(item, node, false);
                                             if (!item.hasChildren)
                                             {
                                                treeNode.isLeaf = true;
                                             }
                                          }
                                       }
                                       oResponse.argument.fnLoadComplete();
                                    },

                                    failure: function(oResponse)
                                    {
                                       Alfresco.logger.error("", oResponse);
                                    },

                                    argument:
                                    {
                                       "node": node,
                                       "fnLoadComplete": fnLoadComplete
                                    },

                                    scope: EditDialog
                                 };

                                 YAHOO.util.Connect.asyncRequest('GET', uri, callback);
                              });

                              var selectDocfolder = function DocFolder_selectDocfolder(path)
                              {
                                 selectedDocfolder = "documentLibrary" + path;
                              }

                              tree.subscribe("expand", function(node)
                              {
                                 selectDocfolder(node.data.path);
                              })

                              tree.subscribe("clickEvent", function(args)
                              {
                                 selectDocfolder(args.node.data.path);
                              });

                              tree.subscribe("collapseComplete", function(node)
                              {
                                 selectDocfolder(node.data.path);
                              });


                              var tempNode = EventInfo._buildTreeNode(
                              {
                                 name: "documentLibrary",
                                 path: "/",
                                 nodeRef: ""
                              }, tree.getRoot(), false);

                              tree.render();
                              EditDialog.browsePanel.setFirstLastFocusable();
                              EditDialog.browsePanel.show();

                           },
                           scope: EditDialog
                        }
                     });
                  });

                  /**
                   * Button declarations that, when clicked, display the calendar date
                   * picker widget.
                   */
                  if (!EditDialog.startButton)
                  {
                     EditDialog.startButton = new YAHOO.widget.Button(
                     {
                        type: "link",
                        id: "calendarpicker",
                        label: '',
                        href: '',
                        tabindex: 6,
                        container: EditDialog.id + "-startdate"
                     });

                     EditDialog.startButton.on("click", EventInfo.onDateSelectButton, {}, EditDialog);
                     Event.on(Dom.get("fd") , "click" , EventInfo.onDateSelectButton , {} , EditDialog)
                     EditDialog.startButton.on("keypress", buttonKeypressHandler);
                  }
                  if (!EditDialog.endButton)
                  {
                     EditDialog.endButton = new YAHOO.widget.Button(
                     {
                        type: "link",
                        id: "calendarendpicker",
                        label: '',
                        href: '',
                        tabindex: 8,
                        container: EditDialog.id + "-enddate"
                     });

                     EditDialog.endButton.on("click", EventInfo.onDateSelectButton, {}, EditDialog);
                     Event.on(Dom.get("td") , "click" , EventInfo.onDateSelectButton , {} , EditDialog)
                     EditDialog.endButton.on("keypress", buttonKeypressHandler);
                  }

                  /* disable time fields if all day is selected */
                  Event.addListener(document.getElementsByName('allday')[0], 'click', function(e)
                  {
                     if (Event.getTarget(e).checked===true)
                     {
                        // hide time boxes if they're not relevent.
                        Dom.addClass(document.getElementsByName('start')[0].parentNode, "hidden")
                        Dom.addClass(document.getElementsByName('end')[0].parentNode, "hidden")
                     } else
                     {
                        // show them if they are.
                        Dom.removeClass(document.getElementsByName('start')[0].parentNode, "hidden")
                        Dom.removeClass(document.getElementsByName('end')[0].parentNode, "hidden")
						
                        //Set default start/end dates to prevent AllDay event(see MNT-9498)
                        //Format date by pattern (see MNT-12811)
                        var tmpDate = new Date();
                        tmpDate.setHours(12);
                        tmpDate.setMinutes(0);
                        document.getElementsByName('start')[0].value=Alfresco.util.formatDate(tmpDate, Alfresco.messages.global["date-format.shortTime"]);
                        tmpDate.setHours(13);
                        document.getElementsByName('end')[0].value=Alfresco.util.formatDate(tmpDate, Alfresco.messages.global["date-format.shortTime"]);
                     }
                  });

               },
               scope: EditDialog
            },
            onSuccess: {
               fn: EventInfo.onEdited,
               scope: EventInfo
            },
            onFailure: {
               fn: EventInfo.onEditFailed,
              scope: EventInfo
           }
         });

         // Overrides Options with config params (if there are any)
         if (optionOverrides)
         {
            EditDialog.setOptions(optionOverrides);
         }
         
         EditDialog.id = "eventEditPanel";
         EditDialog.event = EventInfo.event;

         // add the tags that are already set on the post
         if (EditDialog.tagLibrary == undefined)
         {
            // If there is an existing TagLibrary component on the page, use that, otherwise create a new one.
            var existingTagLibComponent = Alfresco.util.ComponentManager.find({name: "Alfresco.module.TagLibrary"});
            if (existingTagLibComponent.length > 0)
            {
               EditDialog.tagLibrary = existingTagLibComponent[0];
            }
            else
            {
               EditDialog.tagLibrary = new Alfresco.module.TagLibrary( EditDialog.id);
               EditDialog.tagLibrary.setOptions({ siteId: EditDialog.options.siteId });
            }
         }
         EditDialog.tags = [];
         YAHOO.Bubbling.on('onTagLibraryTagsChanged',function(e,o)
         {
            EditDialog.tags=o[1].tags;
         }, EditDialog);

         return EditDialog;

      },

      /**
       * EDIT DIALOGUE FUNCTIONS
       */

      /**
       * Event handler that gets fired when a user clicks on the date selection
       * button in the event creation form. Displays a mini YUI calendar. Gets
       * called for both the start and end date buttons.
       *
       * @method onDateSelectButton
       * @param e {object} DomEvent
       */
      onDateSelectButton: function onDateSelectButton(e)
      {
         Event.stopEvent(e);
         var o = CalendarView,
            targetEl = Event.getTarget(e),
            isStart = (targetEl.id === "calendarpicker-button" || targetEl.id === "fd" ) ? true : false;

         o.oCalendarMenu = new YAHOO.widget.Overlay("calendarmenu",
         {
            context: [targetEl, 'tr', 'br'],
            fixedcenter: true
         });
         o.oCalendarMenu.setBody("&#32;");
         o.oCalendarMenu.body.id = "calendarcontainer";

         o.oCalendarMenu.render(Dom.getAncestorByTagName(targetEl.id, 'div'));

         var domEl = (isStart) ? Dom.get('fd') : Dom.get('td');
         var d =  Alfresco.CalendarHelper.getDateFromField(domEl)

         var pagedate = Alfresco.CalendarHelper.padZeros(d.getMonth() + 1) + '/' + d.getFullYear();

         var startOptions =
         {
            pagedate: pagedate,
            close: true

         },
         endOptions =
         {
            pagedate: pagedate,
            close: true,
            mindate: fromISO8601(Dom.get('fd').title)
         };

         var options = (isStart)? startOptions : endOptions;

         o.oCalendar = new YAHOO.widget.Calendar("buttoncalendar", o.oCalendarMenu.body.id, options);
         Alfresco.util.calI18nParams(o.oCalendar);
         o.oCalendar.render();
         o.oCalendar.selectEvent.subscribe(function(type, args)
         {
            var date;
            if (args)
            {

               date = args[0][0];
               var selectedDate = new Date(date[0], (date[1] - 1), date[2]);
               Alfresco.CalendarHelper.writeDateToField(selectedDate, domEl);

               var toDate;
               if (isStart)
               {
                  // If a new fromDate was selected
                  toDate = Alfresco.CalendarHelper.getDateFromField(Dom.get('td'));
                  if (YAHOO.widget.DateMath.before(toDate, selectedDate))
                  {
                     //...adjust the toDate if toDate is earlier than the new fromDate
                     var tdEl = Dom.get("td");
                     Alfresco.CalendarHelper.writeDateToField(selectedDate, tdEl)
                     document.getElementsByName('endAt')[0].value = toISO8601(selectedDate);
                  }
                  document.getElementsByName('startAt')[0].value = toISO8601(selectedDate);
               }
               else
               {
                  toDate = Alfresco.CalendarHelper.getDateFromField(domEl);
                  document.getElementsByName('endAt')[0].value = toISO8601(toDate);
               }
            }
            o.oCalendarMenu.hide();
            (isStart) ? Dom.get('calendarpicker-button').focus() : Dom.get('calendarendpicker-button').focus();
         }, o, true);
         o.oCalendarMenu.body.tabIndex = -1;
         o.oCalendar.oDomContainer.tabIndex = -1;
         o.oCalendarMenu.show();
         o.oCalendar.show();
         o.oCalendarMenu.body.focus();
         return false;
      },

      _onDateValidation: function _onDateValidation(field, args, event, form, silent)
      {
         var fromTime = Alfresco.util.parseTime(Dom.get(args.obj.id + "-start").value);
         var toTime = Alfresco.util.parseTime(Dom.get(args.obj.id + "-end").value);

         if (fromTime === null || toTime === null)
         {
            return false;
         }

         // Check that the end date is after the start date
         var startDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("fd"));
         startDate.setHours(fromTime.getHours(), fromTime.getMinutes());

         var toDate = Alfresco.CalendarHelper.getDateFromField(Dom.get("td"));
         toDate.setHours(toTime.getHours(), toTime.getMinutes());

         //allday events; the date and time can be exactly the same so test for this too
         if (startDate.getTime() === toDate.getTime())
         {
            return true;
         }
         var after = YAHOO.widget.DateMath.after(toDate, startDate);

         if (Alfresco.logger.isDebugEnabled())
         {
            Alfresco.logger.debug("Current start date: " + startDate + " " + Dom.get(args.obj.id + "-start").value);
            Alfresco.logger.debug("Current end date: " + toDate + " " + Dom.get(args.obj.id + "-end").value);
            Alfresco.logger.debug("End date is after start date: " + after);
         }

         if (!after && !silent)
         {
            form.addError(Alfresco.util.message('message.invalid-date', 'Alfresco.CalendarView'), field);
         }
         return after;
      },

      /**
       * PRIVATE FUNCTIONS
       */

       /**
        * Gets a custom message
        *
        * @method _msg
        * @param messageId {string} The messageId to retrieve
        * @return {string} The custom message
        * @private
        */
       _msg: function EventInfo__msg(messageId)
       {
          return Alfresco.util.message.call(this, messageId, "Alfresco.EventInfo", Array.prototype.slice.call(arguments).slice(1));
       },

      /**
       * Hides the panel and calls onClose callback if present
       *
       * @method _hide
       * @private
       */
      _hide: function EventInfo__hide()
      {
         if (this.widgets && this.widgets.escapeListener)
         {
            this.widgets.escapeListener.disable();
         }
         if (this.panel) 
         {
            this.panel.hide();
         }
         var callback = this.options.onClose;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onClose callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), callback.obj);
         }
      },
      /**
       * Build a tree node using passed-in data
       *
       * @method _buildTreeNode
       * @param p_oData {object} Object literal containing required data for new node
       * @param p_oParent {object} Optional parent node
       * @param p_expanded {object} Optional expanded/collaped state flag
       * @return {YAHOO.widget.TextNode} The new tree node
      */
      _buildTreeNode: function EventInfo__buildTreeNode(p_oData, p_oParent, p_expanded)
      {
         return new YAHOO.widget.TextNode(
         {
            label: Alfresco.util.encodeHTML(p_oData.name),
            path: p_oData.path,
            nodeRef: p_oData.nodeRef,
            description: p_oData.description
         }, p_oParent, p_expanded);
      }
   };
})();