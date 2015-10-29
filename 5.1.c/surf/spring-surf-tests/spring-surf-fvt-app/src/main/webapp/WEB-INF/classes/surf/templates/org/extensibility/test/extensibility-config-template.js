var testConfig = config.scoped["TestConfiguration2"];
if (testConfig != null)
{
   controllerConfig = testConfig["controller-config"];
   model.controller_values = (controllerConfig != null) ? controllerConfig.getChildren() : [];
}