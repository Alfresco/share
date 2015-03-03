/* Gutter Plugin */
Alfresco.gutter = function(myEditor)
{
   var Dom = YAHOO.util.Dom,
      Anim = YAHOO.util.Anim;
   
   return (
   {
      status: false,
      gutter: null,
         
      createGutter: function()
      {
         this.gutter = new YAHOO.widget.Overlay('gutter1',
         {
            height: '372px',
            width: '260px',
            position: 'absolute',
            visible: false
         });
         
         this.gutter.hideEvent.subscribe(function()
         {
            Dom.setStyle("image_results", "overflow", "hidden");
            Dom.setStyle("gutter1", "visibility", "hidden");
         }, this, true);

         this.gutter.showEvent.subscribe(function()
         {
            this.gutter.cfg.setProperty('context', [Dom.get(myEditor.getContainer()), YAHOO.widget.Overlay.TOP_RIGHT, YAHOO.widget.Overlay.TOP_RIGHT]);
            Dom.setStyle("gutter1", "visibility", "visible");
            Dom.setStyle("image_results", "overflow", "auto");
         }, this, true);
         
         new YAHOO.util.KeyListener(document, {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function()
            {
               if (this.status) this.toggle();
            },
            scope: this,
            correctScope: true
         }).enable();
         
         var libraryTitle = Alfresco.util.message("imagelib.title");
         this.gutter.setBody('<div class="yui-toolbar-container"><div class="yui-toolbar-titlebar"><h2>' + libraryTitle + '</h2></div></div><div id="image_results"></div>');
         this.gutter.render(document.body);
      },
      
      open: function()
      {
         this.gutter.show();
         this.status = true;
      },
      
      close: function()
      {
         this.gutter.hide();
         this.status = false;
      },
      
      toggle: function()
      {
         if (this.status)
         {
            this.close();
         }
         else
         {
            this.open();
         }
      }
   });
};

/**
 * Alfresco top-level util namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.util
 */
Alfresco.util = Alfresco.util || {};

Alfresco.util.createImageEditor = function(id, options)
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
   
   YAHOO.Bubbling.on('editorInitialized', function(e)
   {
      // Create the gutter control
      gutter.createGutter();
   });

   options.setup = function(ed) 
   {
      ed.addButton('alfresco-imagelibrary',
      {
         title: Alfresco.util.message("imagelib.tooltip"),
         icon: 'image',
         onclick: function(ev)
         {
            gutter.toggle.call(gutter);
         }
      });
      
      // Add a button for link selection
      ed.addButton('alfresco-linklibrary',
      {
         title: Alfresco.util.message("linklib.tooltip"),
         icon: 'newdocument',
         onclick: function(ev)
         {
            // When the button is clicked, launch the picker...
            documentPicker.onShowPicker();
         }
      });
      
      YAHOO.Bubbling.on('alfresco-imagelibClick', function(ev, args)
      {
         if (args && args[1].img)
         {
            var html = '<img src="' + args[1].img + '" title="' + ev.title + '"/>';
            ed.execCommand('mceInsertContent', false, html);
         }
         gutter.toggle();
      });
      
      // Handle document selection in the picker...
      YAHOO.Bubbling.on('onDocumentsSelected', function(eventName, payload)
      {
         if (payload && payload[1].items)
         {
            var selText = ed.selection.getContent();
            // Iterate over the list of selected documents and links for them...
            for (var i = 0, j = payload[1].items.length; i < j; i++)
            {
               // Construct the link, the title will be selected text
               // if no selection then document name will be used
               var selectedItem = payload[1].items[i],
                   nodeRef = encodeURIComponent(selectedItem.nodeRef),
                   label = (selText && selText.length > 0) ? selText : selectedItem.name;
               var link = Alfresco.util.siteURL("document-details?nodeRef=" + nodeRef);
               var html = '<a href="' + link + '">' + label + '</a> ';

               // If IE8 focus on the editor so that content can be inserted. This is only
               // required for IE8 and solves ALF-11433.
               if (YAHOO.env.ua.ie == 8)
               {
                  editor.focus();
               }
               
               // Insert the link into the editor...
               ed.execCommand('mceInsertContent', false, html);
               
               // Clear the selections...
               documentPicker.resetSelection();
            }
         }
      });
   };
   var editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR, id, options);
   var gutter = new Alfresco.gutter(editor);
   
   // Construct a new document picker... we need to know the nodeRef of the document library
   // of the site that we are viewing. Make an async request to retrieve this information
   // using the the siteId and when the call returns, construct a new DocumentPicker using the
   // DocLib nodeRef as the starting point for document selection...
   var documentPicker;
   var getDocLibNodeRefUrl = Alfresco.constants.PROXY_URI + "slingshot/doclib/container/" + options.siteId + "/documentlibrary";
   Alfresco.util.Ajax.jsonGet(
   {
      url: getDocLibNodeRefUrl,
      successCallback:
      {
         fn: function(response)
         {
            var nodeRef = response.json.container.nodeRef;
            documentPicker = new Alfresco.module.DocumentPicker(id + '-docPicker', Alfresco.ObjectRenderer);
            documentPicker.setOptions(
            {
               displayMode: "items",
               itemFamily: "node",
               itemType: "cm:content",
               multipleSelectMode: true,
               parentNodeRef: nodeRef,
               restrictParentNavigationToDocLib: true
            });
            documentPicker.onComponentsLoaded(); // Need to force the component loaded call to ensure setup gets completed.
         },
         scope: this
      }
   });
   
   Event.onAvailable('image_results', function()
   {
      Event.on('image_results', 'mousedown', function(ev)
      {
         Event.stopEvent(ev);
         var target = Event.getTarget(ev);
         if (target.tagName.toLowerCase() == 'img')
         {
            var longdesc = target.getAttribute("longdesc");
            if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 8)
            {
               longdesc = target.longdesc;
            }
            if (longdesc)
            {
               title = target.getAttribute('title');
               YAHOO.Bubbling.fire('alfresco-imagelibClick',
               {
                  type: 'alfresco-imagelibClick',
                  img: longdesc,
                  title: title
               });
            }
         }
      }, editor, true);

      // Load the "images"
      Alfresco.util.Ajax.request(
      {
         method: Alfresco.util.Ajax.GET,
         dataObj: { max: "250" },
         url: Alfresco.constants.PROXY_URI + "slingshot/doclib/images/site/" + options.siteId + "/documentLibrary",
         successCallback:
         {
            fn: function(e)
            {
               var result = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
               if (result)
               {
                  var div = Dom.get('image_results'), items = result.items, item, nodeRef, img;
                  for (var i = 0, j = items.length; i < j; i++)
                  {
                     item = items[i];
                     nodeRef = item.nodeRef.replace(":/", "");
                     img = document.createElement("img");
                     img.setAttribute("src", Alfresco.constants.PROXY_URI + "api/node/" + nodeRef + "/content/thumbnails/doclib?c=queue&ph=true");
                     img.setAttribute("longdesc", Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + nodeRef + "/" + $html(item.title));
                     img.setAttribute("title", $html(item.title));
                     div.appendChild(img);
                  }
               } 
            },
            scope: this
         },
         failureCallback:
         {
            fn: function(e)
            {
               return;
            }
         }
      });
   });
   return editor;
};
