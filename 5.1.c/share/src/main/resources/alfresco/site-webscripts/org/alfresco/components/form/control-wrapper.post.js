/**
 * Supported Forms controls
 */
var supportedControlTypes =
{
   "association": true,
   "category": true,
   "checkbox": false,
   "content": false,
   "date": true,
   "encoding": false,
   "mimetype": false,
   "period": false,
   "readonly": false,
   "richtext": false,
   "selectmany": false,
   "selectone": false,
   "size": false,
   "tag": true,
   "textarea": false,
   "textfield": false
};

/**
 * Apply all properties of supplier to receiver object
 *
 * @method merge
 * @param r {Object} Receiver object
 * @param s {Object} Supplier object
 * @return {Object} Augmented object
 */
function merge(r, s)
{
   if (s && r)
   {
      for (var p in s)
      { 
         r[p] = s[p];
      }
   }
   return r;
}

/**
 * Main entrypoint
 *
 * @method main
 */
function main()
{
   // Input arguments
   var type = args.type || decodeURIComponent(page.url.args.type),
      name = args.name || ("wrapped-" + type);

   // Output variables
   var field =
   {
      configName: name,
      disabled: false,
      id: name,
      name: name,
      label: args.label || "",
      mandatory: (args.mandatory || "false") == "true",
      control:
      {
         template: "controls/" + type + ".ftl",
         params: jsonUtils.toObject(args.controlParams || "{}")
      },
      value: args.value || ""
   };
   
   var fieldArgs = args.field;
   if (fieldArgs != null)
   {
      field = merge(field, jsonUtils.toObject(fieldArgs));
   }
   
   var form =
   {
      mode: args.mode || "edit",
      data: jsonUtils.toObject(args.formData || "{}")
   };

   switch (String(type).toLowerCase())
   {
      case "association":
         if (field.endpointType == null)
         {
            field.endpointType = "cm:cmobject";
         }
         if (field.endpointMany == null)
         {
            field.endpointMany = true;
         }
         break;

      case "category":
         if (field.control.params.compactMode == null)
         {
            field.control.params.compactMode = true;
         }
         break;

      case "tag":
         field.control.template = "controls/category.ftl";
         if (field.control.params.compactMode == null)
         {
            field.control.params.compactMode = true;
         }
         field.control.params.params = "aspect=cm:taggable";
         break;
   }

   model.form = form;
   model.field = field;
}

main();