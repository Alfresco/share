<?xml version="1.0" encoding="UTF-8"?>
<root>
   <@markup id="outer">
      ${head}
      <@region id="ext-region1" scope="global" chromeless="true"/>
      <@markup id="inner1">
         <content>base-inner1-markup</content>
      </@>
      <content>base-template-content</content>
      <@markup id="inner2">
        <@region id="ext-region2" scope="global" chromeless="true"/>
      </@>
   </@>
</root>