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
 * Document Library "Details" module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibAspects
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;


   Alfresco.module.DoclibAspects = function(htmlId)
   {
      Alfresco.module.DoclibAspects.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable"]);

      this.eventGroup = htmlId;
      this.currentValues = [];
      this.selectedValues = {};

      return this;
   };
   
   YAHOO.extend(Alfresco.module.DoclibAspects, Alfresco.module.SimpleDialog,
   {
      /**
       * Those that are currently applied to the object in the repository.
       * 
       * @property currentValues
       * @type object
       */
      currentValues: null,

      /**
       * Keeps a list of selected values for evaluating added and removed values.
       * 
       * @property selectedValues
       * @type object
       */
      selectedValues: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocListToolbar} returns 'this' for method chaining
       */
      setOptions: function DA_setOptions(obj)
      {
         Alfresco.module.DoclibAspects.superclass.setOptions.call(this,
         {
            width: "56em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/aspects",
            doBeforeDialogShow:
            {
               fn: this.doBeforeDialogShow,
               obj: null,
               scope: this
            },
            doBeforeAjaxRequest:
            {
               fn: this.doBeforeAjaxRequest,
               obj: null,
               scope: this
            }
         });

         this.options = YAHOO.lang.merge(this.options, obj);
         
         return this;
      },

      /**
       * Render item using a passed-in template
       *
       * @method renderItem
       * @param item {object} Item object literal
       * @param template {string} String with "{parameter}" style placeholders
       */
      renderItem: function DA_renderItem(item, template)
      {
         var renderHelper = function(p_key, p_value, p_metadata)
         {
            var html = "";
            
            if (p_key.toLowerCase() == "icon")
            {
               // Look for extra metadata to specify width x height, e.g. "{icon 16 16}"
               var width = "", height = "", arrDims;
               if (p_metadata && p_metadata.length > 0)
               {
                  arrDims = p_metadata.split(" ");
                  width = ' width="' + arrDims[0] + '"';
                  if (arrDims.length > 1)
                  {
                     height = ' height="' + arrDims[1] + '"';
                  }
               }
               html = '<img src="' + p_value + '"' + width + height + ' alt="' + $html(item.name) + '" title="' + $html(item.name) + '" />'; 
            }
            else
            {
               html = $html(p_value);
            }
            
            return html;
         };
         
         return YAHOO.lang.substitute(template, item, renderHelper);
      },
      
      /**
       * Return i18n string for given aspect
       *
       * @method i18n
       * @param aspect {string} The aspect qName
       * @param scope {object} Optional - Scope if 'this' is not the component instance
       * @return {string} The custom message
       */
      i18n: function DA_i18n(aspect, scope)
      {
         var key = "aspect." + aspect.replace(":", "_"),
             msg = this.msg(key);
         return (msg !== key ? msg : this.options.labels[aspect]) + " (" + aspect + ")";
      },
      
      /**
       * Interceptor just before dialog is shown
       *
       * @method doBeforeDialogShow
       * @param p_form {object} The forms runtime instance
       * @param p_this {object} Caller scope
       * @param p_obj {object} Optional - arbitrary object passed through
       */
      doBeforeDialogShow: function DA_doBeforeDialogShow(p_form, p_this, p_obj)
      {
         // Dialog title
         var fileSpan = '<span class="light">' + $html(this.options.file.displayName) + '</span>';
         Dom.get(this.id + "-title").innerHTML = this.msg("title", fileSpan);

         // DocLib Actions module
         if (!this.modules.actions)
         {
            // This module does not rely on Site scope, so can use the DoclibActions module in Repository mode all the time.
            this.modules.actions = new Alfresco.module.DoclibActions(Alfresco.doclib.MODE_REPOSITORY);
         }
         
         this._createAspectsControls();
         this._requestAspectData();

         // Enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.okButton.addClass("alf-primary-button");
         this.widgets.cancelButton.set("disabled", false);
      },
      
      /**
       * Interceptor just before Ajax request is sent
       *
       * @method doBeforeAjaxRequest
       * @param p_config {object} Object literal containing request config
       * @return {boolean} True to continue sending form, False to prevent it
       */
      doBeforeAjaxRequest: function DA_doBeforeAjaxRequest(p_config)
      {
         // Success callback function
         var fnSuccess = function DA_dBAR_success(p_data)
         {
            this.hide();

            // Did the operation succeed?
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg(p_data.json.overallSuccess ? "message.aspects.success" : "message.aspects.failure")
            });
            
            if (p_data.json.results[0].tagScope)
            {
               // TODO: Call a (non-existent) REST API to refresh the tag scope, then fire tagRefresh upon it's return
               // YAHOO.Bubbling.fire("tagRefresh");
            }
         };

         // Failure callback function
         var fnFailure = function DA_dBAR_failure(p_data)
         {
            this.hide();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.aspects.failure")
            });
         };

         // Construct generic action call
         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh",
                  obj:
                  {
                     highlightFile: this.options.file.name
                  }
               },
               callback:
               {
                  fn: fnSuccess,
                  scope: this
               }
            },
            failure:
            {
               callback:
               {
                  fn: fnFailure,
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "aspects/node/{nodeRef}",
               params:
               {
                  nodeRef: this.options.file.jsNode.nodeRef.uri
               }
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  added: this.getAddedValues(),
                  removed: this.getRemovedValues()
               }
            }
         });

         // Return false - we'll be using our own Ajax request
         return false;
      },

      /**
       * Returns an array of values that have been added to the current values
       *
       * @method getAddedValues
       * @return {array}
       */
      getAddedValues: function DA_getAddedValues()
      {
         var addedValues = [],
            currentValues = Alfresco.util.arrayToObject(this.currentValues);
         
         for (var value in this.selectedValues)
         {
            if (this.selectedValues.hasOwnProperty(value))
            {
               if (!(value in currentValues))
               {
                  addedValues.push(value);
               }
            }
         }
         return addedValues;
      },

      /**
       * Returns an array of values that have been removed from the current values
       *
       * @method getRemovedValues
       * @return {array}
       */
      getRemovedValues: function DA_getRemovedValues()
      {
         var removedValues = [],
            currentValues = Alfresco.util.arrayToObject(this.currentValues);
         
         for (var value in currentValues)
         {
            if (currentValues.hasOwnProperty(value))
            {
               if (!(value in this.selectedValues))
               {
                  removedValues.push(value);
               }
            }
         }
         return removedValues;
      },


      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Creates UI controls to support Aspect picker.
       *
       * NOTE: This function has "refactor" written all over it. It's on the TODO list...
       *
       * @method _createAspectsControls
       * @private
       */
      _createAspectsControls: function DA__createAspectsControls()
      {
         var me = this;

         /**
          * Icon datacell formatter
          */
         var renderCellIcon = function renderCellIcon(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            elCell.innerHTML = me.renderItem(oRecord.getData(), '<div>{icon 16 16}</div>');
         };

         /**
          * Name datacell formatter
          */
         var renderCellName = function renderCellName(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = me.renderItem(oRecord.getData(), '<h4 class="name">{name}</h4>');
         };

         /**
          * Add button datacell formatter
          */
         var renderCellAdd = function renderCellAdd(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            if (oRecord.getData("canAdd"))
            {
               elCell.innerHTML = '<a href="#" class="add-item add-' + me.eventGroup + '" title="' + me.msg("button.add") + '"><span class="addIcon">&nbsp;</span></a>';
            }
         };

         /**
          * Remove item datacell formatter
          */
         var renderCellRemove = function renderCellRemove(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            if (oRecord.getData("canRemove"))
            {
               elCell.innerHTML = '<a href="#" class="remove-item remove-' + me.eventGroup + '" title="' + me.msg("button.remove") + '"><span class="removeIcon">&nbsp;</span></a>';
            }
         };

         /**
          * Addable values list (left-hand side)
          */
         // DataSource
         this.widgets.dataSourceLeft = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         }); 

         // DataTable
         var columnDefinitionsLeft =
         [
            { key: "icon", label: "icon", sortable: false, formatter: renderCellIcon, width: 10 },
            { key: "name", label: "name", sortable: false, formatter: renderCellName },
            { key: "id", label: "add", sortable: false, formatter: renderCellAdd, width: 16 }
         ];
         this.widgets.dataTableLeft = new YAHOO.widget.DataTable(this.id + "-left", columnDefinitionsLeft, this.widgets.dataSourceLeft,
         {
            MSG_EMPTY: this.msg("label.loading")
         });

         // Hook action click events
         var fnAddHandler = function fnAddItemHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target = args[1].target,
                  rowId = target.offsetParent,
                  record = me.widgets.dataTableLeft.getRecord(rowId);

               if (record)
               {
                  me.widgets.dataTableRight.addRow(record.getData());
                  me.selectedValues[record.getData("id")] = record;
                  me.widgets.dataTableLeft.deleteRow(rowId);
               }
            }
            return true;
         };
         // Force the new action as "me" object may have changed.
         // See MNT-10286
         YAHOO.Bubbling.addDefaultAction("add-" + this.eventGroup, fnAddHandler, true);

         /**
          * Selected values list (right-hand side)
          */
         this.widgets.dataSourceRight = new YAHOO.util.DataSource([],
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         }); 
         var columnDefinitionsRight =
         [
            { key: "icon", label: "icon", sortable: false, formatter: renderCellIcon, width: 10 },
            { key: "name", label: "name", sortable: false, formatter: renderCellName },
            { key: "id", label: "remove", sortable: false, formatter: renderCellRemove, width: 16 }
         ];
         this.widgets.dataTableRight = new YAHOO.widget.DataTable(this.id + "-right", columnDefinitionsRight, this.widgets.dataSourceRight,
         {
            MSG_EMPTY: this.msg("label.loading")
         });

         // Hook action click events
         var fnRemoveHandler = function fnRemoveHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var target = args[1].target,
                  rowId = target.offsetParent,
                  record = me.widgets.dataTableRight.getRecord(rowId);

               if (record)
               {
                  me.widgets.dataTableLeft.addRow(record.getData());
                  delete me.selectedValues[record.getData("id")];
                  me.widgets.dataTableRight.deleteRow(rowId);
               }
            }
            return true;
         };
         // Force the new action as "me" object may have changed.
         // See MNT-10286
         YAHOO.Bubbling.addDefaultAction("remove-" + this.eventGroup, fnRemoveHandler, true);
      },
      
      /**
       * Gets current aspect values from the Repository
       *
       * @method _requestAspectData
       * @private
       */
      _requestAspectData: function DA__requestAspectData()
      {
         this.selectedValues = {};
         
         Alfresco.util.Ajax.request(
         {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/aspects/node/' + this.options.file.jsNode.nodeRef.uri,
            successCallback: 
            { 
               fn: this._requestAspectDataSuccess, 
               scope: this 
            },
            failureCallback: 
            { 
               fn: this._requestAspectDataFailure, 
               scope: this 
            }
         });
      },

      /**
       * Failure handler for aspect data request
       *
       * @method _requestAspectDataFailure
       * @private
       */
      _requestAspectDataFailure: function DA__requestAspectDataFailure()
      {
         this.widgets.dataTableLeft.set("MSG_EMPTY", this.msg("label.load-failure"));
         this.widgets.dataTableRight.set("MSG_EMPTY", this.msg("label.load-failure"));
      },
      
      /**
       * Success handler for aspect data request
       *
       * @method _requestAspectDataSuccess
       * @param response {object} Object literal containing response data
       * @private
       */
      _requestAspectDataSuccess: function DA__requestAspectDataSuccess(response)
      {
         this.currentValues = {};
         
         if (typeof response.json != "undefined")
         {
            var currentArr = response.json.current,
               currentObj = Alfresco.util.arrayToObject(currentArr),
               visibleArr = this.options.visible,
               visibleObj = Alfresco.util.arrayToObject(visibleArr),
               addableArr = this.options.addable,
               removeableArr = this.options.removeable,
               i, ii;

            this.currentValues = currentArr;

            if (addableArr.length === 0)
            {
               addableArr = visibleArr.slice(0);
            }
            
            if (removeableArr.length === 0)
            {
               removeableArr = visibleArr.slice(0);
            }
            var addableObj = Alfresco.util.arrayToObject(addableArr),
               removeableObj = Alfresco.util.arrayToObject(removeableArr);

            var current, addable, record;
            // Current Values into right-hand table
            for (i = 0, ii = currentArr.length; i < ii; i++)
            {
               current = currentArr[i];
               record =
               {
                  id: current,
                  icon: Alfresco.constants.URL_RESCONTEXT + "components/images/aspect-16.png",
                  name: this.i18n(current),
                  canAdd: current in addableObj,
                  canRemove: current in removeableObj
               };
               if (current in visibleObj)
               {
                  this.widgets.dataTableRight.addRow(record);
               }
               this.selectedValues[current] = record;
            }
            
            // Addable values into left-hand table
            for (i = 0, ii = addableArr.length; i < ii; i++)
            {
               addable = addableArr[i];
               if ((addable in visibleObj) && !(addable in currentObj))
               {
                  this.widgets.dataTableLeft.addRow(
                  {
                     id: addable,
                     icon: Alfresco.constants.URL_RESCONTEXT + "components/images/aspect-16.png",
                     name: this.i18n(addable),
                     canAdd: true,
                     canRemove: true
                  });
               }
            }

            this.widgets.dataTableLeft.set("MSG_EMPTY", this.msg("label.no-addable"));
            this.widgets.dataTableRight.set("MSG_EMPTY", this.msg("label.no-current"));
            this.widgets.dataTableLeft.render();
            this.widgets.dataTableRight.render();
         }
      }
   });
})();
/**
 * Dummy instance to load optional YUI components early.
 * Use fake "null" id, which is tested later in onComponentsLoaded()
*/
var doclibAspects = new Alfresco.module.DoclibAspects("null");