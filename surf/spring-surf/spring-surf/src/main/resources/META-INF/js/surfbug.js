var SurfBug;
if (!SurfBug) SurfBug = {};
    
SurfBug.showPopup = function(event)
{
    event.target.children[0].style.display = "block";
}

SurfBug.hidePopup = function(event, node)
{
    node.parentNode.style.display = "none";
    event.stopPropagation();
}

// This function should create a new absolutely positioned highlight <div>
// element over the first child in the supplied node...
SurfBug.createHighlight = function(id)
{
   if (id != null && id != "")
   {
      var chromeElement = document.getElementById(id);
      if (chromeElement != null)
      {
         // Find the first element within the chrome (this will be whatever has been 
         // provided by the call to <@componentInclude>
         for (var i=0; i<chromeElement.children.length; i++)
         {
            if (chromeElement.children[i].tagName.toLowerCase() != "script" &&
                chromeElement.children[i].type != "hidden" &&
                (chromeElement.children[i].style != null && chromeElement.children[i].style.display != "none"))
            {
               var elementToHighlight = chromeElement.children[i];
               if (elementToHighlight != null)
               {
                  // Create the new element that will highlight the element...
                  var highlightElement = document.createElement("div");
                  highlightElement.style.position = "absolute";
                  highlightElement.className = "surfbug_highlight";
                  
                  // Ensure that there is always *something* to click in for the Surf Bug popup...
                  var width = elementToHighlight.offsetWidth;
                  if (width == 0)
                  {
                     width = 20;
                  }
                  var height = elementToHighlight.offsetHeight;
                  if (height == 0)
                  {
                     height = 20;
                  }
                  
                  highlightElement.style.width = (width) + "px";
                  highlightElement.style.height = (height) + "px";
                  highlightElement.style.top = (elementToHighlight.offsetTop) + "px";
                  highlightElement.style.left = (elementToHighlight.offsetLeft) + "px";
                  
                  // Attach a listener to show the popup when it is clicked on...
                  highlightElement.addEventListener('click', SurfBug.showPopup, false);
                  chromeElement.appendChild(highlightElement);
                  
                  // Sub-Component Chrome should have created an element containing all the 
                  // debug data (if SurfBug has been enabled). But this element needs to be 
                  // moved into the new highlight element.
                  var surfBugDataElement = document.getElementById(id + "-SurfBug-Popup");
                  if (surfBugDataElement != null)
                  {
                     highlightElement.appendChild(surfBugDataElement);
                  }
               }
               break;
            }
         }
      }
   }
}

