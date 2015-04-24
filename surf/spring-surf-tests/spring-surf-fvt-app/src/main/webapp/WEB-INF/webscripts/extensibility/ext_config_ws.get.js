var testConfig = config.scoped["TestConfiguration4"];
if (testConfig != null)
{
   controllerConfig = testConfig["ws-controller-config"];
   model.controller_values = (controllerConfig != null) ? controllerConfig.getChildren(): [];
}