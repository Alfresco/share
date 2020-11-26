<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/data/surf-doclist.lib.js">
if(typeof stripLinkedNodeProperties === 'undefined'){
    surfDoclist_main(args["includeThumbnails"] == "true", null);
}else{
    surfDoclist_main(args["includeThumbnails"] == "true", stripLinkedNodeProperties);
}
