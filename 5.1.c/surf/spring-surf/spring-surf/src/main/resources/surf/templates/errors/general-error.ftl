<div>
    <p>This is error page has not yet been translated yet!</p>
    
	<h1>An unexpected error occurred</h1>
	
	<#-- An error title should be a short piece of text identifying the problem --> 
	<#if context.properties[errorTitle]??>
	  <h3>${context.properties[errorTitle]}</h3>
	</#if>
	
	<#-- An error description should be a longer piece of text explaining the the
	     possible causes of the problem and (if possible) the ways in which it might
	     be corrected. -->
	<#if context.properties[errorDescription]??>
	  <p>${context.properties[errorDescription]}</p>
	</#if>     
	     
	<#-- If an exception was thrown then it is useful to display the stacktrace to
	     assist the debugging process. -->     
	<#if context.properties[stacktrace]??>
	    <p>The following stack trace was generated: </p>
	    <p>${context.properties[stacktrace]}</p>
	<#else>
	    <p>No stack trace is available.</p>
	</#if>
</div>