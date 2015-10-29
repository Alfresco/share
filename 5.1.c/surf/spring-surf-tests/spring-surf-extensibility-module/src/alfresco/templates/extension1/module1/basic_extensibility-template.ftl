This shouldn't get rendered before extension regions
<@region target="region-to-remove" id="remove-region" action="remove"/>
<@region target="region-to-replace" id="replacement-region" scope="global" action="replace" />
<@region target="region-to-stay" id="extra-region" scope="global" action="after" />
This shouldn't get rendered after extension regions 