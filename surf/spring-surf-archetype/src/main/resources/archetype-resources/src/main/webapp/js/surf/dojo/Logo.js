define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Logo.html"], 
        function(declare, _Widget, _Templated, template) {
   
   return declare([_Widget, _Templated], {
      
      cssRequirements: [{cssFile:"./css/Logo.css", mediaType:"screen"}],
      
      // This should be set to the CSS classes required for the logo
      logoClasses: "",
      
      templateString: template,
      
      postCreate: function() {
         this.inherited(arguments);
      },
      
      startup: function() {
         this.inherited(arguments);
      }
   });
});