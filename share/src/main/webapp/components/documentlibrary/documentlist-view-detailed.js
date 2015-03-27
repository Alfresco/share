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
 * DocumentListViewRenderer component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentListViewRenderer
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
   var $html = Alfresco.util.encodeHTML,
      $isValueSet = Alfresco.util.isValueSet;
   
   /**
    * ViewRenderer constructor.
    *
    * @param name {String} The name of the viewRenderer
    * @return {Alfresco.DocumentListViewRenderer} The new ViewRenderer instance
    * @constructor
    */
   Alfresco.DocumentListViewRenderer = function(name, parentDocumentList)
   {
      /*
       * Initialise prototype properties
       */
      this.name = name;
      this.parentDocumentList = parentDocumentList;
      this.parentElementIdSuffix = "-documents";
      this.rowClassName = "yui-dt-rec";
      this.actionsCssClassName = this.name;
      this.actionsColumnWidth = 200;
      this.actionsSplitAtModifier = 1;
      this.thumbnailColumnWidth = 100;
      this.buttonElementIdSuffix = "-" + this.name + "View";
      this.buttonCssClass = this.name + "-view";
      this.metadataBannerViewName = this.name;
      this.metadataLineViewName = this.name;
      
      return this;
   };
   
   Alfresco.DocumentListViewRenderer.prototype =
   {

      /**
       * Performs any setup needed immediately after registration
       *
       * @method setupRenderer
       * @param scope {object} The DocumentList object
       */
      setupRenderer: function DL_VR_setupRenderer(scope)
      {
         Dom.addClass(scope.id + this.buttonElementIdSuffix, this.buttonCssClass);
      },
   
      /**
       * Render the view using the given scope (documentList), request and response.
       *
       * @method renderView
       * @param scope {object} The DocumentList object
       * @param sRequest {string} Original request
       * @param oResponse {object} Response object
       * @param oPayload {MIXED} (optional) Additional argument(s)
       */
      renderView: function DL_VR_renderView(scope, sRequest, oResponse, oPayload)
      {
         YAHOO.util.Dom.setStyle(scope.id + this.parentElementIdSuffix, 'display', '');
         scope.widgets.dataTable.onDataReturnInitializeTable.call(scope.widgets.dataTable, sRequest, oResponse, oPayload);
      },
   
      /**
       * Performs any teardown or visual changes to deselect this view in the interface
       *
       * @method destroyView
       * @param scope {object} The DocumentList object
       * @param sRequest {string} Original request
       * @param oResponse {object} Response object
       * @param oPayload {MIXED} (optional) Additional argument(s)
       */
      destroyView: function DL_VR_destroyView(scope, sRequest, oResponse, oPayload)
      {
         YAHOO.util.Dom.setStyle(scope.id + this.parentElementIdSuffix, 'display', 'none');
      },

      /**
       * Selector custom datacell formatter
       *
       * @method renderCellSelected
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellSelected: function DL_VR_renderCellSelected(scope, elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         
         var jsNode = oRecord.getData("jsNode"),
             nodeRef = jsNode.nodeRef,
             name = oRecord.getData("displayName"),
             checkbox = document.createElement("input"),
             label = document.createElement("label");
         checkbox.id = "checkbox-" + oRecord.getId();
         checkbox.type = "checkbox";
         checkbox.name = "fileChecked";
         checkbox.value = nodeRef;
         checkbox.checked = scope.selectedFiles[nodeRef] ? true : false;
         
         label.id = "label_for_" + checkbox.id;
         label.style.fontSize="0em";
         label.innerHTML = (checkbox.checked ? scope.msg("checkbox.uncheck") : scope.msg("checkbox.check")) + " " + name;
         label.setAttribute("for", checkbox.id);
         elCell.innerHTML = '';
         elCell.appendChild(label);
         elCell.appendChild(checkbox);
         Event.addListener(checkbox, "click", function(e)
         {
            label.innerHTML = (checkbox.checked ? scope.msg("checkbox.uncheck") : scope.msg("checkbox.check")) + " " + name;
         }, checkbox, true);

         // MNT-12522
         var row = Dom.getAncestorByTagName(elCell, "tr");
         Event.addListener(checkbox, "focus", function()
         {
            _unhighlightRows(this);
            this.onEventHighlightRow({target : row});
         }, this.parentDocumentList, true);

         new YAHOO.util.KeyListener(checkbox,
         {
            keys : YAHOO.util.KeyListener.KEY.TAB,
            shift : true
         },
         {
            fn : function()
            {
               _unhighlightRows(this);
               var previous = Dom.getPreviousSibling(row);
               if (previous !== null)
               {
                  this.onEventHighlightRow({target : previous});
               }
            },
            scope : this.parentDocumentList,
            correctScope : true
         }, "keydown").enable();

         function _unhighlightRows(scope)
         {
            var highlightedRows = Dom.getElementsByClassName("yui-dt-highlighted", "tr", Dom.getAncestorByTagName(row, "tbody"));
            for (var i = 0; i < highlightedRows.length; i++)
            {
               scope.onEventUnhighlightRow({target : highlightedRows[i]});
            }
         }
      },
      
      /**
       * Status custom datacell formatter
       *
       * @method renderCellStatus
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellStatus: function DL_VR_renderCellStatus(scope, elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

         var record = oRecord.getData(),
            node = record.jsNode,
            indicators = record.indicators,
            indicator, label, desc = "";

         if (indicators && indicators.length > 0)
         {
            for (var i = 0, ii = indicators.length; i < ii; i++)
            {
               indicator = indicators[i];
               // Note: deliberate bypass of scope.msg() function
               label = Alfresco.util.message(indicator.label, scope.name, indicator.labelParams);
               label = Alfresco.util.substituteDotNotation(label, record);

               desc += '<div class="status">';
               
               if (indicator.action)
               {
                  desc += '<a class="indicator-action" data-action="' + indicator.action + '">';
               }
               
               desc += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/indicators/' + indicator.icon + '" title="' + label + '" alt="' + indicator.id + '" />';
               if (indicator.action)
               {
                  desc += '</a>';
               }
               desc += '</div>';
            }
         }

         elCell.innerHTML = desc;
      },
      
      /**
       * Render the thumbnail cell
       *
       * @method renderCellThumbnail
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellThumbnail: function DL_VR_renderCellThumbnail(scope, elCell, oRecord, oColumn, oData)
      {
         var record = oRecord.getData(),
            node = record.jsNode,
            properties = node.properties,
            name = record.displayName,
            isContainer = node.isContainer,
            isLink = node.isLink,
            extn = name.substring(name.lastIndexOf(".")),
            imgId = node.nodeRef.nodeRef; // DD added
         
         var containerTarget; // This will only get set if thumbnail represents a container
         
         if (window.location.href.search(/\/sharedfiles/) != -1 && record.location.path.search("/Shared") == 0)
         {
            record.location.path = record.location.path.substring(7);
         }
         else
         {
            if (window.location.href.search(/\/myfiles/) != -1 && record.location.path.search("/User Homes") == 0)
            {
               record.location.path = "/" + record.location.path.split("/").slice(3).join("/");
            }
         }
         
         oColumn.width = this.thumbnailColumnWidth;
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
      
         if (isContainer || (isLink && node.linkedNode.isContainer))
         {
            elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + (scope.dragAndDropEnabled ? '<span class="droppable"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-64.png" /></a>';
            containerTarget = new YAHOO.util.DDTarget(imgId); // Make the folder a target
         }
         else
         {
            elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.DocumentList.generateThumbnailUrl(record) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
         }
         var dnd = new Alfresco.DnD(imgId, scope);
      },

      /**
       * Description/detail custom datacell formatter
       *
       * @method renderCellDescription
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellDescription: function DL_VR_renderCellDescription(scope, elCell, oRecord, oColumn, oData)
      {
         var desc = "", i, j,
            record = oRecord.getData(),
            jsNode = record.jsNode,
            properties = jsNode.properties,
            isContainer = jsNode.isContainer,
            isLink = jsNode.isLink,
            title = "",
            titleHTML = "",
            version = "",
            canComment = jsNode.permissions.user.CreateChildren;

         if (jsNode.isLink)
         {
            // Link handling
            // MNT-11988: Renaming links is not working correctly
            oRecord.setData("displayName", record.fileName.replace(/(.url)$/,""));
         }
         else if (properties.title && properties.title !== record.displayName && scope.options.useTitle)
         {
            // Use title property if it's available. Supressed for links.
            titleHTML = '<span class="title">(' + $html(properties.title) + ')</span>';
         }

         // Version display
         if (!jsNode.hasAspect("cm:workingcopy") && $isValueSet(record.version) && !jsNode.isContainer && !jsNode.isLink)
         {
            version = '<span class="document-version">' + $html(record.version) + '</span>';
         }

         /**
          *  Render using metadata template
          */
         record._filenameId = Alfresco.util.generateDomId();

         var metadataTemplate = record.metadataTemplate;
         if (metadataTemplate)
         {
            /* Banner */
            if (YAHOO.lang.isArray(metadataTemplate.banners))
            {
               var fnRenderBanner = function fnRenderBanner_substitute(p_key, p_value, p_meta)
               {
                  var label = (p_meta !== null ? scope.msg(p_meta) + ': ': ''),
                     value = "";
                      
                  // render value from properties or custom renderer
                  if (scope.renderers.hasOwnProperty(p_key) && typeof scope.renderers[p_key] === "function")
                  {
                     value = scope.renderers[p_key].call(scope, record, label);
                  }
                  else
                  {
                     if (jsNode.hasProperty(p_key))
                     {
                        value = '<span class="item">' + label + scope.renderProperty(jsNode.properties[p_key]) + '</span>';
                     }
                  }

                  return value;
               };

               var html, banner;
               for (i = 0, j = metadataTemplate.banners.length; i < j; i++)
               {
                  banner = metadataTemplate.banners[i];
                  if (!$isValueSet(banner.view) || banner.view == this.metadataBannerViewName)
                  {
                     html = YAHOO.lang.substitute(banner.template, scope.renderers, fnRenderBanner);
                     if ($isValueSet(html))
                     {
                        desc += '<div class="info-banner">' + html + '</div>';
                     }
                  }
               }
            }

            /* Title */
            if (YAHOO.lang.isString(metadataTemplate.title))
            {
               var fnRenderTitle = function fnRenderTitle_substitute(p_key, p_value, p_meta)
               {
                  var label = (p_meta !== null ? '<em>' + scope.msg(p_meta) + '</em>: ': ''),
                     value = "";
                      
                  // render value from properties or custom renderer
                  if (scope.renderers.hasOwnProperty(p_key) && typeof scope.renderers[p_key] === "function")
                  {
                     value = scope.renderers[p_key].call(scope, record, label);
                  }
                  else
                  {
                     if (jsNode.hasProperty(p_key))
                     {
                        value = '<div class="filename">' + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record);
                        value += label + scope.renderProperty(jsNode.properties[p_key]) + '</a></span></div>';
                     }
                  }

                  return value;
               };

               desc += YAHOO.lang.substitute(metadataTemplate.title, scope.renderers, fnRenderTitle);
            }
            else
            {
               // Insitu editing for title (filename)
               if (jsNode.hasPermission("Write") && !jsNode.isLocked && !jsNode.hasAspect("cm:workingcopy"))
               {
                  scope.insituEditors.push(
                  {
                     context: record._filenameId,
                     params:
                     {
                        type: "textBox",
                        nodeRef: jsNode.nodeRef.toString(),
                        name: "prop_cm_name",
                        value: record.fileName,
                        fnSelect: function fnSelect(elInput, value)
                        {
                           // If the file has an extension, omit it from the edit selection
                           var extnPos = value.lastIndexOf(Alfresco.util.getFileExtension(value)) - 1;
                           if (extnPos > 0)
                           {
                              Alfresco.util.selectText(elInput, 0, extnPos);
                           }
                           else
                           {
                              elInput.select();
                           }
                        },
                        validations: [
                        {
                           type: Alfresco.forms.validation.length,
                           args: { min: 1, max: 255, crop: true },
                           when: "keyup",
                           message: scope.msg("validation-hint.length.min.max", 1, 255)
                        },
                        {
                           type: Alfresco.forms.validation.nodeName,
                           when: "keyup",
                           message: scope.msg("validation-hint.nodeName")
                        }],
                        title: scope.msg("tip.insitu-rename"),
                        errorMessage: scope.msg("message.insitu-edit.name.failure")
                     },
                     callback:
                     {
                        fn: scope._insituCallback,
                        scope: scope,
                        obj: record
                     }
                  });
               }

               desc += '<h3 class="filename"><span id="' + record._filenameId + '">' + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record);
               desc += $html(record.displayName) + '</a></span>' + titleHTML + version + '</h3>';
            }

            if (YAHOO.lang.isArray(metadataTemplate.lines))
            {
               var fnRenderTemplate = function fnRenderTemplate_substitute(p_key, p_value, p_meta)
               {
                  var label = (p_meta !== null ? '<em>' + scope.msg(p_meta) + '</em>: ': ''),
                     value = "";
                      
                  // render value from properties or custom renderer
                  if (scope.renderers.hasOwnProperty(p_key) && typeof scope.renderers[p_key] === "function")
                  {
                     value = scope.renderers[p_key].call(scope, record, label);
                  }
                  else
                  {
                     if (jsNode.hasProperty(p_key))
                     {
                        value = '<span class="item">' + label + scope.renderProperty(jsNode.properties[p_key]) + '</span>';
                     }
                  }

                  return value;
               };

               var html, line;
               for (i = 0, j = metadataTemplate.lines.length; i < j; i++)
               {
                  line = metadataTemplate.lines[i];
                  if (!$isValueSet(line.view) || line.view == this.metadataLineViewName)
                  {
                     html = YAHOO.lang.substitute(line.template, scope.renderers, fnRenderTemplate);
                     if ($isValueSet(html))
                     {
                        desc += '<div class="detail">' + html + '</div>';
                     }
                  }
               }
            }
         }

         elCell.innerHTML = desc;

         Event.on(Dom.getElementsByClassName("banner-more-info-link", "span", elCell), "click", function showMoreInfoLinkClick(event)
         {
            scope.onCloudSyncIndicatorAction(record, Event.getTarget(event))
         }, {}, scope);
      },
      
      /**
       * Actions custom datacell formatter
       *
       * @method renderCellActions
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellActions: function DL_VR_renderCellActions(scope, elCell, oRecord, oColumn, oData)
      {
         oColumn.width = this.actionsColumnWidth;
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         Dom.addClass(elCell.parentNode, oRecord.getData("type"));

         elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
      },
      
      /**
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellProperty
       */
      fnRenderCellProperty: function DL_fnRenderCellProperty()
      {
         var scope = this;

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellProperty(elCell, oRecord, oColumn, oData)
         {
            scope.renderCellProperty(scope, elCell, oRecord, oColumn, oData);
         };
      },
      
      /**
       * Actions custom datacell formatter
       *
       * @method renderCellActions
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellProperty: function DL_VR_renderCellProperty(scope, elCell, oRecord, oColumn, oData)
      {
         // this.renderers[propertyName] = renderer
         // this.parentDocumentList.renderers
         
         if (typeof this.parentDocumentList.renderers[oColumn.field] === "function")
         {
            elCell.innerHTML = this.parentDocumentList.renderers[oColumn.field].call(this.parentDocumentList, oRecord.getData(), "");
         }
         else 
         {
            // IMPLEMENTATION NOTES:
            // It is possible to check for renderers mapped to a property without the namespace (e.g. "description" instead of
            // "cm:description"). However, the renderers were intended to work with metadata line templates which have been registered
            // with names such as "tags" rather than "cm:taggable". Therefore this function supports either genuine data properties
            // (such as "cm:name") or template renderers (such as "tags" and "size") but makes no attempt to map between the two.
            // However, example code is commented out below in case it is decided that this would actually be desirable...
//            var namespaceLessProp = oColumn.field.substring(oColumn.field.indexOf(":") + 1);
//            if (typeof this.parentDocumentList.renderers[namespaceLessProp] === "function")
//            {
//               elCell.innerHTML = this.parentDocumentList.renderers[namespaceLessProp].call(this.parentDocumentList, oRecord.getData(), "");
//            }
            var record = oRecord.getData(),
                node = record.jsNode,
                properties = node.properties,
                propertyValue = properties[oColumn.field];
            if (propertyValue != null)
            {
               elCell.innerHTML = '<span class="alf-generic-property">' + this.parentDocumentList.renderProperty(propertyValue) + '</span>';
            }
         }
      },
      
      /**
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellLinkProperty
       */
      fnRenderCellLinkProperty: function DL_fnRenderCellLinkProperty()
      {
         var scope = this;

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellLinkProperty
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellLinkProperty(elCell, oRecord, oColumn, oData)
         {
            scope.renderCellLinkProperty(scope, elCell, oRecord, oColumn, oData);
         };
      },
      
      /**
       * Actions custom datacell formatter
       *
       * @method renderCellLinkProperty
       * @param scope {object} The DocumentList object
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellLinkProperty: function DL_VR_renderCellLinkProperty(scope, elCell, oRecord, oColumn, oData)
      {
         var record = oRecord.getData(),
            node = record.jsNode,
            properties = node.properties,
            propertyValue = properties[oColumn.field];
         if (propertyValue != null)
         {
            elCell.innerHTML = '<span class="link">' + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope.parentDocumentList, record) + this.parentDocumentList.renderProperty(propertyValue) + '</a></span>'
         }
      },
      
      /**
       * Get the dataTable record identifier, i.e. yui-recXX, from the given row element.
       *
       * @method getDataTableRecordIdFromRowElement
       * @param scope {object} The DocumentList object
       * @param rowElement {HTMLElement} row element.
       * @return {String} the dataTable recordId
       */
      getDataTableRecordIdFromRowElement: function DL_VR_getDataTableRecordIdFromRowElement(scope, rowElement)
      {
         if (scope != null && rowElement != null)
         {
            var element = rowElement;
            if (!Dom.hasClass(rowElement, this.rowClassName))
            {
               element = Dom.getAncestorByClassName(rowElement, this.rowClassName);
            }
            return element !== null ? element.id : null;
         }
      },
      
      /**
       * Get the row element from the given dataTable record.
       *
       * @method getRowElementFromDataTableRecordId
       * @param scope {object} The DocumentList object
       * @param oRecord {object}
       * @return {HTMLElement} row element
       */
      getRowElementFromDataTableRecord: function DL_VR_getRowElementFromDataTableRecordId(scope, oRecord)
      {
         if (scope != null && oRecord != null)
         {
            return scope.widgets.dataTable.getTrEl(oRecord);
         }
      },
      
      /**
       * Get the row's selection element, i.e. checkbox, from the given dataTable record.
       *
       * @method getRowSelectElementFromDataTableRecord
       * @param scope {object} The DocumentList object
       * @param oRecord {object}
       * @return {HTMLElement} row element
       */
      getRowSelectElementFromDataTableRecord: function DL_VR_getRowSelectElementFromDataTableRecord(scope, oRecord)
      {
         if (scope != null && oRecord != null)
         {
            return Dom.get("checkbox-" + oRecord.getId());
         }
      },
      
      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param scope {object} The DocumentList object
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       * @param rowElement {HTMLElement} row element, optionally force a target other than event's
       */
      onEventHighlightRow: function DL_VR_onEventHighlightRow(scope, oArgs, rowElement)
      {
         // Call through to get the row highlighted by YUI
         scope.widgets.dataTable.onEventHighlightRow.call(scope.widgets.dataTable, oArgs);
         
         var targetElement;
         if (rowElement)
         {
            targetElement = rowElement;
         }
         else
         {
            targetElement = oArgs.target;
         }

         // elActions is the element id of the active table cell where we'll inject the actions
         var elActions = Dom.get(scope.id + "-actions-" + targetElement.id);

         // Inject the correct action elements into the actionsId element
         if (elActions && elActions.firstChild === null)
         {
            // Retrieve the actionSet for this record
            var oRecord = scope.widgets.dataTable.getRecord(this.getDataTableRecordIdFromRowElement(scope, targetElement));
            if (oRecord !== null)
            {
               var record = oRecord.getData(),
                  jsNode = record.jsNode,
                  actions = record.actions,
                  actionsEl = document.createElement("div"),
                  actionHTML = "",
                  actionsSel;
   
               record.actionParams = {};
               for (var i = 0, ii = actions.length; i < ii; i++)
               {
                  actionHTML += scope.renderAction(actions[i], record);
               }
   
               // Token replacement - action Urls
               actionsEl.innerHTML = YAHOO.lang.substitute(actionHTML, scope.getActionUrls(record));
   
               // Simple or detailed view
               Dom.addClass(actionsEl, "action-set");
               Dom.addClass(actionsEl, this.actionsCssClassName);
   
               // Need the "More >" container?
               actionsSel = YAHOO.util.Selector.query("div", actionsEl);
               if (actionsSel.length > scope.options.actionsSplitAt + this.actionsSplitAtModifier)
               {
                  var moreContainer = Dom.get(scope.id + "-moreActions").cloneNode(true),
                     containerDivs = YAHOO.util.Selector.query("div", moreContainer);
   
                  // Insert the two necessary DIVs before the third action item
                  Dom.insertBefore(containerDivs[0], actionsSel[scope.options.actionsSplitAt]);
                  Dom.insertBefore(containerDivs[1], actionsSel[scope.options.actionsSplitAt]);
   
                  // Now make action items three onwards children of the 2nd DIV
                  var index, moreActions = actionsSel.slice(scope.options.actionsSplitAt);
                  for (index in moreActions)
                  {
                     if (moreActions.hasOwnProperty(index))
                     {
                        containerDivs[1].appendChild(moreActions[index]);
                     }
                  }
               }
   
               elActions.appendChild(actionsEl);
            }
         }

         if (!Dom.hasClass(document.body, "masked"))
         {
            scope.currentActionsMenu = elActions;
            // Show the actions
            Dom.removeClass(elActions, "hidden");
         }
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param scope {object} The DocumentList object
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       * @param rowElement {HTMLElement} row element, optionally force a target other than event's
       */
      onEventUnhighlightRow: function DL_VR_onEventUnhighlightRow(scope, oArgs, rowElement)
      {
         // Call through to get the row unhighlighted by YUI
         scope.widgets.dataTable.onEventUnhighlightRow.call(scope.widgets.dataTable, oArgs);
         
         var targetElement;
         if (rowElement)
         {
            targetElement = rowElement;
         }
         else
         {
            targetElement = oArgs.target;
         }

         var elActions = Dom.get(scope.id + "-actions-" + (targetElement.id));

         // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
         if (elActions || Dom.hasClass(document.body, "masked"))
         {
            if (scope.hideMoreActionsFn)
            {
               scope.hideMoreActionsFn.call(this);
            }
            // Just hide the action links, rather than removing them from the DOM
            Dom.addClass(elActions, "hidden");
         }
      },

      /**
       * Show more actions pop-up.
       *
       * @method onActionShowMore
       * @param scope {object} The DocumentList object
       * @param record {object} Object literal representing file or folder to be actioned
       * @param elMore {element} DOM Element of "More Actions" link
       */
      onActionShowMore: function DL_VR_onActionShowMore(scope, record, elMore)
      {
         // Fix "More Actions" hover style
         Dom.addClass(elMore.firstChild, "highlighted");

         // Get the pop-up div, sibling of the "More Actions" link
         var elMoreActions = Dom.getNextSibling(elMore);
         
         // MNT-11703, MNT-12137 Menus disappearing off bottom of screen when clicking on more in list view
         var scrollY = window.scrollY;
         if (scrollY === undefined)
         {
            scrollY = document.documentElement.scrollTop;
         }
         
         var visibleHeight = Dom.getViewportHeight() - (Dom.getY(elMore) - scrollY + elMore.offsetHeight);
         
         Dom.removeClass(elMoreActions, "hidden");
         
         if (elMoreActions.offsetHeight > visibleHeight)
         {
            Dom.setStyle(elMoreActions , "margin-top" , - (elMore.offsetHeight + elMoreActions.offsetHeight + 1) + "px" );
         }
         
         scope.hideMoreActionsFn = function DL_oASM_fnHidePopup()
         {
            scope.hideMoreActionsFn = null;
            
            Dom.setStyle(elMoreActions , "margin-top" , "" );
            Dom.removeClass(elMore.firstChild, "highlighted");
            Dom.addClass(elMoreActions, "hidden");
         };
      },
      
      /**
       * File or folder renamed event handler
       *
       * @method onFileRenamed
       * @param scope {object} The DocumentList object
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileRenamed: function DL_VR_onFileRenamed(scope, layer, args)
      {
         var obj = args[1];
         if (obj && (obj.file !== null))
         {
            var recordFound = scope._findRecordByParameter(obj.file.node.nodeRef, "nodeRef");
            if (recordFound !== null)
            {
               scope.widgets.dataTable.updateRow(recordFound, obj.file);
               var el = scope.widgets.dataTable.getTrEl(recordFound);
               Alfresco.util.Anim.pulse(el);
            }
         }
      },
      
      /**
       * Highlight file event handler
       * Used when a component (including the DocList itself on loading) wants to scroll to and highlight a file
       *
       * @method onHighlightFile
       * @param scope {object} The DocumentList object
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (filename to be highlighted)
       */
      onHighlightFile: function DL_VR_onHighlightFile(scope, layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && ($isValueSet(obj.fileName)))
         {
            Alfresco.logger.debug("DL_VR_onHighlightFile: ", obj.fileName);
            var recordFound = scope._findRecordByParameter(obj.fileName, "displayName");
            if (recordFound !== null)
            {
               // Scroll the record into view and highlight it
               var el = this.getRowElementFromDataTableRecord(scope, recordFound),
                  yPos = Dom.getY(el);

               if (YAHOO.env.ua.ie > 0)
               {
                  yPos = yPos - (document.body.clientHeight / 3);
               }
               else
               {
                  yPos = yPos - (window.innerHeight / 3);
               }
               window.scrollTo(0, yPos);
               Alfresco.util.Anim.pulse(el);
               scope.options.highlightFile = null;

               // Select the file
               var rowSelectEl = this.getRowSelectElementFromDataTableRecord(scope, recordFound);
               rowSelectEl.checked = true;
               scope.selectedFiles[recordFound.getData("nodeRef")] = true;
               YAHOO.Bubbling.fire("selectedFilesChanged");
            }
         }
      },
      
      /**
       * Sets the given message HTML as the text string of the YUI datatable
       *
       * @method _setEmptyDataSourceMessage
       * @param scope {object} The DocumentList object
       * @param messageHtml {string} the message HTML
       */
      _setEmptyDataSourceMessage: function DL_VR_setEmptyDataSourceMessage(scope, messageHtml)
      {
         scope.widgets.dataTable.set("MSG_EMPTY", messageHtml);
      },
      
      /**
       * Constructs the display of upload indicators and instructions for empty spaces.
       *
       * @method renderEmptyDataSourceHtml
       * @param scope {object} The DocumentList object
       * @param permissions {object} the current user's permissions
       */
      renderEmptyDataSourceHtml: function DL_VR_renderEmptyDataSourceHtml(scope, permissions)
      {
         var me = scope;
         
         // Work out the current status of the document list (this will be used to determine what user assistance
         // is provided if the doc list is empty, or appears as empty)...
         var itemCounts = me.doclistMetadata.itemCounts,
            empty = (itemCounts.documents === 0  && itemCounts.folders === 0),
            hiddenFolders = (itemCounts.documents === 0 && !me.options.showFolders && itemCounts.folders > 0);

         // Define a re-usable function for seting IDs...
         //   Get the children of the supplied node and append "-instance" to any child nodes that have an
         // "id" attribute in the template. This ensures that the clone has a unique ID within the
         // page and can be accurately targeted later (i.e. to add event listeners to).
         var updateIDs = function DL_updateIDs(node)
         {
            var children = Dom.getChildren(node);
            for (var i = 0, ii = children.length; i < ii; i++)
            {
               if (children[i].id !== null && children[i].id !== "")
               {
                  children[i].id += "-instance";
               }
            }
         };

         // In documentlist.lib.ftl there are a number of DOM structures that are not displayed, these can
         // cloned to display the relevant information to the user based on content, display options, site
         // ownership and access rights. All of theses DOM "snippets" need to be added to a main container
         // which controls the overall display (of borders, etc).
         var template = Dom.get(me.id + "-main-template"),
            main = template.cloneNode(true),
            container = Dom.getFirstChild(main),
            templateInstance = null,
            elements = null;

         if (permissions)
         {
            me._userCanUpload = me.doclistMetadata.parent.permissions.user.CreateChildren && YAHOO.env.ua.mobile === null;
            
            // Only allow drag and drop behaviour if the filter is changed to an actual
            // path (if the filter is anything else such as tags then there won't be a specific
            // location to upload to!)...
            me._removeDragAndDrop();
            if (me.currentFilter.filterId === "path" || me.currentFilter.filterId === "favourites")
            {
               me._addDragAndDrop();
            }
            
            if (me._userCanUpload && me.dragAndDropEnabled)
            {
               Dom.addClass(container, "docListInstructionsWithDND");
            }
            else
            {
               Dom.addClass(container, "docListInstructions");
            }

            // Work out what to display based on the boolean values calculated earlier...
            if (empty && !me._userCanUpload)
            {
               // If folder is empty, there are no hidden folders and the user cannot upload, then show the no items info...
               template = Dom.get(me.id + "-no-items-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               container.appendChild(templateInstance);
            }
            else if (hiddenFolders)
            {
               // ...or, if there are hidden subfolders then show the option to reveal them...
               template = Dom.get(me.id + "-hidden-subfolders-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               updateIDs(templateInstance);
               elements = Dom.getElementsByClassName("docListLinkedInstruction", "a", templateInstance);
               if (elements.length == 1)
               {
                  elements[0].innerHTML = me.msg("show.folders", itemCounts.folders);
               }
               container.appendChild(templateInstance);
            }
            else if (empty && me._userCanUpload && me.dragAndDropEnabled)
            {
               // ...or, if the folder is empty, there are no hidden folders, the user can upload AND the browser supports
               // the DND process, show the HTML5 DND instructions...
               template = Dom.get(me.id + "-dnd-instructions-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               container.appendChild(templateInstance);
            }
            else if (empty && me._userCanUpload && !me.dragAndDropEnabled)
            {
               // ...but if the folder is empty, there are no hidden folders, the user can upload BUT the browser does
               // NOT support the DND process then just show the standard upload instructions...
               template = Dom.get(me.id + "-upload-instructions-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               updateIDs(templateInstance);
               container.appendChild(templateInstance);
            }

            if (empty && me._userCanUpload && me.dragAndDropEnabled)
            {
               // Clone the other options node...
               template = Dom.get(me.id + "-other-options-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               container.appendChild(templateInstance);

               if (empty && me._userCanUpload)
               {
                  if (me.dragAndDropEnabled)
                  {
                     // Show the standard upload other options node...
                     template = Dom.get(me.id + "-standard-upload-template");
                     templateInstance = template.cloneNode(true);
                     Dom.removeClass(templateInstance, "hidden");
                     updateIDs(templateInstance);
                     container.appendChild(templateInstance);
                  }
                  
                  // Show the New Folder other options node...
                  template = Dom.get(me.id + "-new-folder-template");
                  templateInstance = template.cloneNode(true);
                  Dom.removeClass(templateInstance, "hidden");
                  updateIDs(templateInstance);
                  container.appendChild(templateInstance);
               }
            }
         }
         else
         {
            Dom.addClass(container, "docListInstructions");

            // Work out what to display based on the boolean values calculated earlier...
            if (hiddenFolders)
            {
               // If there are hidden subfolders then show the option to reveal them...
               template = Dom.get(me.id + "-hidden-subfolders-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               updateIDs(templateInstance);
               elements = Dom.getElementsByClassName("docListLinkedInstruction", "a", templateInstance);
               if (elements.length == 1)
               {
                  elements[0].innerHTML = me.msg("show.folders", itemCounts.folders);
               }
               container.appendChild(templateInstance);
            }
            else
            {
               // Show the no items info...
               template = Dom.get(me.id + "-no-items-template");
               templateInstance = template.cloneNode(true);
               Dom.removeClass(templateInstance, "hidden");
               container.appendChild(templateInstance);
            }
         }

         // Add a node in with a style of "clear" set to both to ensure that the main div is given
         // a height to accomodate the floated content...
         var clearingNode = document.createElement("div");
         Dom.setStyle(clearingNode, "clear", "both");
         container.appendChild(clearingNode);

         this._setEmptyDataSourceMessage(me, main.innerHTML);
      }
      
   };
   
})();