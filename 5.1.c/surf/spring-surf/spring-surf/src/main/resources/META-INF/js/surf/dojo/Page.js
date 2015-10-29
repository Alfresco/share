define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!surf/dojo/templates/Page.html",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare, _Widget, _Templated, template, domConstruct, array) {
   
   return declare([_Widget, _Templated], {
      
      templateString: template,

      postCreate: function() {
         
         if (this.services)
         {
            this.processServices(this.services);
         }
         
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      },
      
      processWidgets: function(widgets, rootNode) {
         // There are two options for providing configuration, either via a JSON object or
         // via a URL to asynchronously retrieve the configuration. The configuration object
         // takes precedence as it will be faster by virtue of not requiring a remote call.
         
         // For the moment we'll just ignore handling the configUrl...
         var _this = this;
         if (widgets)
         {
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(widgets, function(entry, i) {
               var domNode = _this.createWidgetDomNode(entry, rootNode, entry.className);
               _this.createWidget(entry.name, entry.config, domNode);
            });
         }
      },
      
      processServices: function(services) {
         var _this = this;
         if (services)
         {
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(services, function(entry, i) {
               var requires = [entry];
               require(requires, function(ServiceType) {
                  new ServiceType();
               });
            });
         }
      },
      
      createWidgetDomNode: function(widget, rootNode, rootClassName) {
         // Add a new <div> element to the "main" domNode (defined by the "data-dojo-attach-point"
         // in the HTML template)...
         var className = (rootClassName) ? rootClassName : "";
         var outerDiv = domConstruct.create("div", { className: className}, rootNode);
         var innerDiv = domConstruct.create("div", {}, outerDiv);
         return innerDiv;
      },
      
      createWidget: function(type, config, domNode, callback, callbackArgs) {
         // Make sure we have an instantiation args object...
         var initArgs = (config && (typeof config === 'object')) ? config : {};
         
         // Dynamically require the specified widget
         // The use of indirection is done so modules will not rolled into a build (should we do one)
         var requires = [type];
         require(requires, function(WidgetType) {
            // Instantiate the new widget
            
            // This is an asynchronous response so we need a callback method...
            var widget = new WidgetType(initArgs, domNode);
            if (callback)
            {
               callback(widget, callbackArgs);
            }
         });
      }
   });
});