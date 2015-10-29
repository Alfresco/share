<import resource="/org/springframework/extensions/surf/api.lib.js">

var objectTypeId = url.templateArgs["type"];

model.results = sitedata.getObjects(objectTypeId);
model.code = "OK";
model.message = "";
