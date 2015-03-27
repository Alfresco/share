<#assign el=args.htmlid?html>
<#assign aboutConfig=config.scoped["Edition"]["about"]>
<div id="${el}-dialog" class="about-share">
   <div class="bd">
      <div id="${el}-logo" class="${aboutConfig.getChildValue("css-class")!logo-com} logo">
         <div class="about">
            <#assign split=serverVersion?index_of(" ")>
            <div class="header">Alfresco ${serverEdition?html} v${serverVersion?substring(0, split)?html}</div>
            <div>${serverVersion?substring(split+1)?html} schema ${serverSchema?html}</div>
            <#assign split=server.version?index_of(" ")>
            <div class="header">Spring Surf and ${server.edition?html} v${server.version?substring(0, split)?html}</div>
            <div>${server.version?substring(split+1)?html}</div>
            <div class="licenseHolder"><#if licenseHolder != "" && licenseHolder != "UNKNOWN"><span>${msg("label.licensedTo")}</span> ${licenseHolder}<#else>&nbsp;</#if></div>
            <div class="contributions-bg"></div>
            <div class="contributions-wrapper">
               <div id="${el}-contributions" class="contributions">
Alfresco Contributors...
<br/><br/>
Igor Blanco<br/>
Sylvain Chambon<br/>
Philippe Dubois<br/>
Dave Gillen<br/>
Romain Guinot<br/>
Antti Jokipii<br/>
Markus Konrad<br/>
Michael Kriske<br/>
Carina Lansing<br/>
Peter Lofgren<br/>
Sebastian Lorenz<br/>
Marlin Manowski<br/>
Jesper Steen M&oslash;ller<br/>
Peter Monks<br/>
Paolo Nacci<br/>
Guillaume Nodet<br/>
Ian Norton<br/>
Jan Pfitzner<br/>
Noel Sharpe<br/>
Alfresco Engineering
<br/><br/>
Atol Conseils et D&eacute;veloppements<br/>
CEC<br/>
DMC.de<br/>
IP Tech<br/>
Optaros<br/>
Zia Consulting<br/>
Zaizi<br/>
               </div>
            </div>
            <div class="copy">
               <span>&copy; 2005-2015 Alfresco Software Inc. All rights reserved.</span>
               <a href="http://www.alfresco.com" target="new">www.alfresco.com</a>
               <a href="http://www.alfresco.com/legal/agreements/" target="new">Legal and License</a>
            </div>
         </div>
      </div>
   </div>
</div>