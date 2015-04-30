(function()
{
   /**
    * Preview Web Asset
    *
    * @method onPreviewWebAsset
    * @param record {object} Object literal representing one file or folder to be actioned
    */
   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onActionPreviewWebAsset",
      fn: function WCMQS_onActionPreviewWebAsset(record)
      {
         var nodeRef = new Alfresco.util.NodeRef(record.nodeRef);
         
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function WCMQS_onPreviewWebAsset_success(data)
                  {
                     var url = data.json.url,
                        win = window.open(url, "webpreview");
                     if (!win)
                     {
                        // A popup blocker kicked-in. Offer a manual option.
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                           title: this.msg("message.popup-blocked.title"),
                           text: this.msg("message.popup-blocked.text"),
                           buttons: [
                           {
                              text: this.msg("button.ok"),
                              handler: function WCMQS_onPreviewWebAsset_success_ok()
                              {
                                 window.open(url, "webpreview");
                                 this.destroy();
                              },
                              isDefault: true
                           },
                           {
                              text: this.msg("button.cancel"),
                              handler: function WCMQS_onPreviewWebAsset_success_cancel()
                              {
                                 this.destroy();
                              }
                           }]
                        });
                     }
                  },
                  scope: this
               }
            },
            failure:
            {
               message: "Unable to preview web asset"
            },
            webscript:
            {
               stem: Alfresco.constants.PROXY_URI,
               name: "api/webassetpreviewer/{id}",
               method: Alfresco.util.Ajax.GET,
               params:
               {
                  id: nodeRef.id
               }
            }
         });
      }
   });
})();