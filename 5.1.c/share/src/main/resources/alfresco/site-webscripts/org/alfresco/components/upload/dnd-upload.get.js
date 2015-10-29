function main()
{
   model.contentTypes = 
   [{
      id: "cm:content",
      value: "cm_content"
   }];
   
   
   //Widget instantiation metadata...
   var dndUpload = {
      id : "DNDUpload", 
      name : "Alfresco.DNDUpload",
      assignTo : "dndUpload"
   };
   model.widgets = [dndUpload];

   // Get the file-upload settings...
   var _inMemoryLimit, _maximumFileSizeLimit;
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var fileUpload = docLibConfig["file-upload"];
      if (fileUpload != null)
      {
         _inMemoryLimit = fileUpload.getChildValue("in-memory-limit");
         _maximumFileSizeLimit = fileUpload.getChildValue("maximum-file-size-limit");
      }
   }
   model.inMemoryLimit = (_inMemoryLimit != null) ? _inMemoryLimit : "262144000";
   model.fileUploadSizeLimit = (_maximumFileSizeLimit != null) ? _maximumFileSizeLimit : "0";
}

main();