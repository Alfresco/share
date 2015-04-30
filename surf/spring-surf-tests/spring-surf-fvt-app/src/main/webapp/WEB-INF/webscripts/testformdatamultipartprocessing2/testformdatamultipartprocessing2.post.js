var result = (formdata != null);

model.result = result;

// should be a multi-part form
model.isMultiPart = (formdata.getIsMultiPart());

// example of getting a form field via the "args" map - but cannot get "file" field this way
model.args_name = args.name;

model.form_name = "Default";
model.form_filename = "Default";
model.form_title = "Default";
model.form_hiddenvalue = "Default";

model.formFieldLength = formdata.fields.length;

// Parse form attributes - including "file" field
for each (field in formdata.fields)
{
   switch (String(field.name).toLowerCase())
   {
      case "file":
         if (field.isFile)
         {
            model.form_filename = field.filename;
         }
         break;

      case "name":
         model.form_name = field.value;
         break;
      
      case "title":
         model.form_title = field.value;
         break;
      
      case "hiddenvalue":
         model.form_hiddenvalue = field.value;
         break;
      
   }
}