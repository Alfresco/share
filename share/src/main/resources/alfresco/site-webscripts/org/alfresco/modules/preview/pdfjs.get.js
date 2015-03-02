function main()
{
    model.toolbarItems = [
         {
             id: "previous",
             type: "button",
             icon: "components/images/back-arrow.png",
             label: msg.get("button.previous"),
             disabled: true
         },
         {
             id: "next",
             type: "button",
             icon: "components/images/forward-arrow-16.png",
             label: msg.get("button.next"),
             disabled: true
         },
         {
             id: "pageNumber",
             type: "number",
             disabled: true
         },
         {
             type: "span",
             text: "/"
         },
         {
             id: "numPages",
             type: "span",
             className: "numPages",
             text: "--"
         },
         {
             type: "separator"
         },
         {
             id: "zoomOut",
             type: "button",
             icon: "components/preview/images/zoom-out-16.png",
             title: msg.get("button.zoomout"),
             disabled: true
         },
         {
             id: "zoomIn",
             type: "button",
             icon: "components/preview/images/zoom-in-16.png",
             title: msg.get("button.zoomin"),
             disabled: true
         },
         {
             id: "scaleSelect",
             type: "select",
             icon: "components/images/forward-arrow.png",
             label: msg.get("button.next"),
             options: [
                 {
                     label: "25%",
                     value: "0.25"
                 },
                 {
                     label: "50%",
                     value: "0.5"
                 },
                 {
                     label: "75%",
                     value: "0.75"
                 },
                 {
                     label: "100%",
                     value: "1"
                 },
                 {
                     label: "125%",
                     value: "1.25"
                 },
                 {
                     label: "150%",
                     value: "1.5"
                 },
                 {
                     label: "200%",
                     value: "2"
                 },
                 {
                     label: "400%",
                     value: "4"
                 },
                 {
                     label: msg.get("select.pagewidth"),
                     value: "page-width"
                 },
                 {
                     label: msg.get("select.twopagewidth"),
                     value: "two-page-width"
                 },
                 {
                     label: msg.get("select.pagefit"),
                     value: "page-fit"
                 },
                 {
                     label: msg.get("select.twopagefit"),
                     value: "two-page-fit"
                 },
                 {
                     label: msg.get("select.auto"),
                     value: "auto"
                 }
             ],
             disabled: true
         },
         {
             type: "separator"
         },
         {
             id: "fullpage",
             title: "",
             type: "button",
             label: msg.get("button.maximize"),
             useWrapper: true,
             wrapperClassName: "maximizebutton"
         },
         {
             id: "present",
             title: "",
             type: "button",
             icon: "components/preview/images/present-16.png",
             label: msg.get("button.present"),
             useWrapper: true,
             wrapperClassName: "presentbutton"
         },
         {
             type: "separator",
             useWrapper: true,
             wrapperClassName: "maximizebuttonSep"
         },
         {
             id: "download",
             type: "button",
             icon: "components/documentlibrary/actions/document-download-16.png",
             label: msg.get("button.download"),
             title: msg.get("button.download")
         },
         {
             id: "link",
             type: "button",
             icon: "components/images/link-16.png",
             title: msg.get("button.link"),
             useWrapper: true,
             wrapperClassName: "linkbutton"
         },
         {
             type: "separator",
             useWrapper: true,
             wrapperClassName: "searchBarToggle"
         },
         {
             id: "searchBarToggle",
             type: "button",
             icon: "components/images/search-16.png",
             useWrapper: true,
             wrapperClassName: "searchBarToggle",
             disabled: true
         }
    ];
}

main();