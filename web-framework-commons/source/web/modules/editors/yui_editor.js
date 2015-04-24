/**
 *  Adapter for YUI html editor (http://developer.yahoo.com/yui/editor/).
 * 
 */
Alfresco.util.RichEditorManager.addEditor('YAHOO.widget.SimpleEditor', function(id,config)
{
   var editor;
   return (
   {
      init: function RichEditorManager_yui_init(id,config)
      {
         editor = new YAHOO.widget.SimpleEditor(id, config);
         YAHOO.Bubbling.fire("editorInitialized", this);
         return this;
      },
      
      getEditor: function RichEditorManager_yui_getEditor()
      {
         return editor;
      },

      clear: function RichEditorManager_yui_clear()
      {
         editor.clearEditorDoc();
      },

      render: function RichEditorManager_yui_render()
      {
         editor.render();
      },

      disable: function RichEditorManager_yui_disable()
      {
         editor._disableEditor(true);
      },

      enable: function RichEditorManager_yui_enable()
      {
         editor._disableEditor(false);
      },
      
      focus: function RichEditorManager_yui_focus()
      {
         editor.focus();
      },

      getContent: function RichEditorManager_yui_getContent()
      { 
         return editor.getEditorHTML();
      }, 

      setContent: function RichEditorManager_yui_setContent(html)
      { 
         editor.setEditorHTML(html);
      },

      save: function RichEditorManager_yui_save()
      {
         editor.saveHTML();
      },

      getContainer: function RichEditorManager_yui_getContainer()
      {
         return editor.get('element_cont').get('element');
      },
      
      activateButton: function RichEditorManager_yui_activateButton(buttonId)
      {
         editor.toolbar.selectButton(buttonId);
      },
      
      deactivateButton: function RichEditorManager_yui_deactivateButton(buttonId)
      {
         editor.toolbar.deselectButton(buttonId);
      },
      
      isDirty: function RichEditorManager_yui_isDirty()
      {
         return editor.editorDirty;
      },
      
      clearDirtyFlag: function RichEditorManager_yui_clearDirtyFlag()
      {
         editor.editorDirty = null;
      },

      addPageUnloadBehaviour: function RichEditorManage_yui_addUnloadBehaviour(message)
      {
         // Page unload / unsaved changes behaviour
         window.onbeforeunload = function(e)
         {
            var e = e || window.event;
            if (editor.editorDirty)
            {
               if (e)
               {
                  e.returnValue = message;
               }
               return message;
            }
         };
      }
   });
});