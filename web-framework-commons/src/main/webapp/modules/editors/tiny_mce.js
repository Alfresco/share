/**
 *  Adapter for tinyMCE html editor (http://tinymce.moxiecode.com).
 */
Alfresco.util.RichEditorManager.addEditor('tinyMCE', function(id, config)
{
   var editor;
   
   return (
   {
      init: function RichEditorManager_tinyMCE_init(id, config)
      {
         config.theme = "modern";
         if (!config.toolbar)
         {
            config.toolbar = "styleselect | bold italic | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | print preview fullscreen";
         }
         if (!config.menu)
         {
            config.menu = {
               // TODO: I18N
               file   : {title : 'File'  , items : 'newdocument | print'},
               edit   : {title : 'Edit'  , items : 'undo redo | cut copy paste pastetext | selectall | searchreplace'},
               insert : {title : 'Insert', items : 'link image | charmap hr anchor pagebreak inserttime nonbreaking'},
               view   : {title : 'View'  , items : 'fullscreen preview visualblocks code'},
               format : {title : 'Format', items : 'bold italic underline strikethrough superscript subscript | formats | removeformat'},
               table  : {title : 'Table' , items : 'inserttable tableprops deletetable | cell row column'},
            };
         }
         config.plugins = [
            "advlist autolink link image lists charmap print preview hr anchor pagebreak",
            "searchreplace code fullscreen insertdatetime nonbreaking",
            "table contextmenu paste textcolor visualblocks"
         ];
         config.relative_urls = true;
         config.convert_urls = false;
         if (!config.init_instance_callback) 
         {
            config.init_instance_callback = function(o)
            {
               return function(inst)
               {
                  YAHOO.Bubbling.fire("editorInitialized", o);
               };
            }(this);
         }
         
         editor = new tinymce.Editor(id, config, tinymce.EditorManager);
         
         // Allow back the 'embed' tag as TinyMCE now removes it - this is allowed by our editors
         // if the HTML stripping is disabled via the 'allowUnfilteredHTML' config attribute
         var extValidElements = config.extended_valid_elements;
         extValidElements = (extValidElements && extValidElements != "") ? (extValidElements = extValidElements + ",") : "";
         config.extended_valid_elements = extValidElements + "embed[src|type|width|height|flashvars|wmode]";
         
         return this;
      },

      getEditor: function RichEditorManager_tinyMCE_getEditor()
      {
         return editor;
      },

      clear: function RichEditorManager_tinyMCE_clear() 
      {
         YAHOO.util.Dom.get(editor.id).value = '';
         editor.setContent('');
      },

      render: function RichEditorManager_tinyMCE_render() 
      {
         editor.render();
      },

      execCommand: 'execCommand',

      disable: function RichEditorManager_tinyMCE_disable()
      {
         editor.hide();
      },

      enable: function RichEditorManager_tinyMCE_enable()
      {
         editor.show();
      },
      
      focus: function RichEditorManager_tinyMCE_focus()
      {
         editor.focus();
      },

      getContent: function RichEditorManager_tinyMCE_getContent() 
      { 
         return editor.getContent();
      }, 

      setContent: function RichEditorManager_tinyMCE_setContent(html) 
      { 
         editor.setContent(html);
      }, 

      save: function RichEditorManager_tinyMCE_save()
      {
         editor.save();
      },

      getContainer: function RichEditorManager_tinyMCE_getContainer()
      {
         return editor.getContainer();
      },
      
      activateButton: function RichEditorManager_tinyMCE_activateButton(buttonId)
      {
         editor.controlManager.setActive(buttonId, true);
      },
      
      deactivateButton: function RichEditorManager_tinyMCE_deactivateButton(buttonId)
      {
         editor.controlManager.setActive(buttonId, false);
      },

      isDirty: function RichEditorManager_tinyMCE_isDirty()
      {
         return editor.isDirty();
      },

      clearDirtyFlag: function RichEditorManager_tinyMCE_clearDirtyFlag()
      {
         editor.isNotDirty = 1;
      },
      
      addPageUnloadBehaviour: function RichEditorManager_tinyMCE_addUnloadBehaviour(message, callback)
      {
         // Page unload / unsaved changes behaviour
         window.onbeforeunload = function(e)
         {
            if (YAHOO.lang.isFunction(callback) && callback())
            {
               var e = e || window.event;
               if (editor.isDirty())
               {
                  if (e)
                  {
                     e.returnValue = message;
                  }
                  return message;
               }
            }
         };
      },
      
      addSaveKeyBehaviour: function RichEditorManager_tinyMCE_addSaveKeyBehaviour(fn)
      {
         // iframe editor document save key event - and also stop the outer document event
         new YAHOO.util.KeyListener(editor.getDoc(), { keys: 83, ctrl: true }, {
            fn: fn,
            scope: this,
            correctScope: true
         }).enable();
         new YAHOO.util.KeyListener(document, { keys: 83, ctrl: true }, {
            fn: function(id, e) {
               Event.stopEvent(e[1]);
            },
            scope: this,
            correctScope: true
         }).enable();
      }
   });
});
