/**
 * Selects all the options in the HTML select element passed as an argument. This is provided so that
 * all the options in a list are posted as request parameters on the form submit. This also modifies 
 * the element to ensure that multiple options can be selected (this function should only be called
 * prior to submitting the page so changing the default behaviour of the element shouldn't pose a problem).
 * 
 * @param selectElement
 */
function selectAll(selectElement)
{
   selectElement.multiple = "true";  // Make sure that we really can select all the elements
   var i;
   for (i=0; i<selectElement.options.length; i++) 
   {
      selectElement.options[i].selected = true;
   }
}

/**
 * Moves an option from one select element to another ensuring that the label, value and onclick behaviour
 * is retained. This function should be called when moving modules from the undeployed list to the deployed
 * list (or vice versa). 
 * 
 * @param moveFrom
 * @param moveTo
 */
function deploymentAction(moveFrom, moveTo)
{
   var i;
   for(i=0; i<moveFrom.length; i++)
   {
      if(moveFrom.options[i].selected)
      {
         var option = new Option(moveFrom.options[i].text, moveFrom.options[i].value);
         option.onclick = moveFrom.options[i].onclick;
         moveTo.options[moveTo.length] = option;
         moveFrom.options[i] = null;
      }
   }
}

/**
 * Moves the selected option in the supplied select element down a place. This function should be called
 * by a button that moves a deployed module down the process order list. If the selected option is at the 
 * bottom of the list then the order will not change.
 * 
 * @param selectElement
 */
function moveDown(selectElement)
{
   var optionElement = findSelected(selectElement);
   var index = optionElement.index;
   if (index < selectElement.length - 1)
   {
       var tmp = selectElement.options[index + 1];
       selectElement.options[index] = new Option(tmp.text, tmp.value);
       var newSelectedOption = new Option(optionElement.text, optionElement.value);
       selectElement.options[index + 1] = newSelectedOption;
       newSelectedOption.selected = true;
    }
}
  
/**
 * Moves the selected option in the supplied select element up a place. This function should be called 
 * by a button that moves a deployed module up the process order list. If the selected option is already
 * at the top of the list then the order will not change.
 * 
 * @param selectElement
 */
function moveUp(selectElement)
{
   var optionElement = findSelected(selectElement);
   var index = optionElement.index;
   if (index > 0)
   {
      var tmp = selectElement.options[index - 1];
      var newSelectedOption = new Option(optionElement.text, optionElement.value);
      selectElement.options[index - 1] = newSelectedOption;
      newSelectedOption.selected = true;
      selectElement.options[index] = new Option(tmp.text, tmp.value);
      selectElement.options
   }
}

/**
 * Returns the selected option in the supplied select element.
 * @param selectElement
 * @returns
 */
function findSelected(selectElement)
{
   for (var i = 0; i < selectElement.length ; i++)
   {
      if(selectElement.options[i].selected)
      {
         return selectElement.options[i];
      }
   }
}
  
/**
 * Writes out the id of the supplied module. 
 * @param mod
 */
function getModuleId(mod)
{
   document.write(mod.id + " (Version: " + (mod.version != null ? mod.version : "Not Defined") + ")");
}

/**
 * Writes out the id of the supplied module. 
 * @param mod
 */
function getEvaluatorId(mod)
{
   document.write(mod.id);
}
  
  
var currentOption;
var currentModule;
var evalPropDiv; // Global so we don't need to retrieve it each time

/**
 * Creates HTML elements to display the supplied module. These elements will be written
 * to a specific section of the page which ties this function to the ModuleDeployment
 * WebScripts.
 * 
 * @param option
 * @param reset
 */
function showSelectedEvaluator(option, reset)
{
   document.getElementById("moduleInfo").className = "alignTop"; // Ensure that the module info is shown...
   
   if (evalPropDiv == null)
   {
      evalPropDiv = document.getElementById("evaluatorPropertyOverrides");
   }
     
   currentModule = YAHOO.lang.JSON.parse(option.value);
   currentOption = option;
   
   document.getElementById("selectedModule").innerHTML = currentModule.id;

   if (reset || currentModule.evaluatorOverrideId === undefined)
   {
      if (currentModule.evaluatorId === undefined)
      {
         selectEvaluator("");
      }
      else
      {
         selectEvaluator(currentModule.evaluatorId);
      }
   }
   else
   {
      selectEvaluator(currentModule.evaluatorOverrideId);
   }
   
   // Clear the current properties...
   clearEvaluatorProperties();
   
   // Determine whether or not to display default evaluator properties or property overrides...
   var evalProps;
   if (reset || currentModule.evaluatorPropertyOverrides === undefined)
   {
      evalProps = currentModule.evaluatorProperties;
   }
   else
   {
      evalProps = currentModule.evaluatorPropertyOverrides;
   }

   // Add the property information...
   for (var key in evalProps)
   {
      addProperty(key, evalProps[key]);
   }
}
  
/**
 * Selects the evaluator with the supplied id.
 * @param evaluatorId
 */
function selectEvaluator(evaluatorId)
{
   var evaluatorSelect = document.getElementById("evaluator");
   for (var i = 0; i < evaluatorSelect.length ; i++)
   {
      var currEvaluator = YAHOO.lang.JSON.parse(evaluatorSelect.options[i].value);
      if(currEvaluator.id == evaluatorId)
      {
         evaluatorSelect.options[i].selected = true;
         break;
      }
   }
}

/**
 * 
 */
function clearEvaluatorProperties()
{
   if (evalPropDiv == null)
   {
      evalPropDiv = document.getElementById("evaluatorPropertyOverrides");
   }
   if (evalPropDiv.hasChildNodes())
   {
      while (evalPropDiv.childNodes.length > 0)
      {
         evalPropDiv.removeChild(evalPropDiv.firstChild);
      }
   }
}

/**
 * Deletes the elements that specify a single evaluator property name/value pair. This doesn't actually
 * change the configuration unless the module is saved.
 *  
 * @param propRow
 */
function deleteProperty(propRow)
{
   evalPropDiv.removeChild(propRow);
}
  
/**
 * Adds the HTML elements that specify a single evaluator property name/value pair.
 * @param name
 * @param value
 */
function addProperty(name, value)
{
   var propRow = document.createElement("div");
   var keyLabel = document.createElement("span");
   keyLabel.innerHTML = "Key: ";
   var keyValue = document.createElement("input");
   keyValue.type = "text";
   keyValue.value = name;
   var valueLabel = document.createElement("span");
   valueLabel.innerHTML = "Value: ";
   var valueValue = document.createElement("input");
   valueValue.type = "text";
   valueValue.value = value;
   var deleteButton = document.createElement("input");
   deleteButton.type = "button";
   deleteButton.value = "Delete";
   deleteButton.onclick = function() {
      evalPropDiv.removeChild(this.parentNode);
   };
   propRow.appendChild(keyLabel);
   propRow.appendChild(keyValue);
   propRow.appendChild(valueLabel);
   propRow.appendChild(valueValue);
   propRow.appendChild(deleteButton);
   evalPropDiv.appendChild(propRow);
}
  
/**
 * Calls the showSelectedEvaluator function ensuring that the default evaluator properties are
 * displayed (this will remove any custom configured properties although the configuration will
 * not change unless saved). 
 */
function resetProperties()
{
   showSelectedEvaluator(currentOption, true);
}
  
function showRequiredEvaluatorProps(value)
{
   var evaluatorObj = YAHOO.lang.JSON.parse(value);
   clearEvaluatorProperties();
   for (var i=0;i<evaluatorObj.requiredProps.length; i++)
   {
      addProperty(evaluatorObj.requiredProps[i], "");
   }
}

/**
 * Saves the currently displayed module locally (this will not result in server side changes). Essentially
 * this means that another module can be displayed and the changes won't be lost prior to a form submit.
 */
function saveModule()
{
   var evaluatorSelect = document.getElementById("evaluator");
   var selectedEvaluator = YAHOO.lang.JSON.parse(evaluatorSelect.value).id;
   currentModule.evaluatorOverrideId = selectedEvaluator;
   var evaluatorPropertyOverides = {};
   var i;
   for (i=0; i<evalPropDiv.childNodes.length; i++)
   {
      var key = evalPropDiv.childNodes[i].childNodes[1].value;
      var value = evalPropDiv.childNodes[i].childNodes[3].value;
      evaluatorPropertyOverides[key] = value; 
   } 
   currentModule.evaluatorPropertyOverrides = evaluatorPropertyOverides;
   var updatedJSON = YAHOO.lang.JSON.stringify(currentModule);
   currentOption.value = updatedJSON;
}
