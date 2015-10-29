var licenseHolder = context.properties["editionInfo"].holder,
    footerConfig = config.scoped["Edition"]["footer"],
    footerCopyRight = footerConfig.getChildValue("label"),
    footerCssClass = footerConfig.getChildValue("css-class"),
    footerLogo = footerConfig.getChildValue("logo"),
    footerLogoAltText = footerConfig.getChildValue("alt-text");

// Returns supplied services and widgets with sticky footer wrapper to create sticky footer beneath those supplied elements
function getFooterModel(services, widgets)
{
   return {
      services: services,
      widgets: [
         {
            id: "ALF_STICKY_FOOTER",
            name: "alfresco/footer/AlfStickyFooter",
            config: {
               widgets: [
                  {
                     id: "SHARE_VERTICAL_LAYOUT",
                     name: "alfresco/layout/VerticalWidgets",
                     config: 
                     {
                        widgets: widgets
                     }
                  }
               ],
               widgetsForFooter: [
                  {
                     id: "ALF_SHARE_FOOTER",
                     name: "alfresco/footer/AlfShareFooter",
                     config: {
                        semanticWrapper: "footer",
                        licenseLabel: licenseHolder,
                        copyrightLabel: footerCopyRight,
                        altText: footerLogoAltText,
                        logoImageSrc: footerLogo,
                        cssClass: footerCssClass
                     }
                  }
               ]
            }
         }
      ]
   };

}