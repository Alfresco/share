model.title = args.title;

var assetSize = asset.properties['cmis:contentStreamLength'];
if (assetSize >= 1073741824) 
{
	var gb = assetSize/1073741824;
	model.size = gb.toFixed(2) + ' GB';
} 
else if (assetSize >= 1048576) 
{
	var mb = assetSize/1048576;
	model.size = mb.toFixed(2) + ' MB';
} 
else if (assetSize >= 1024) 
{
	var kb = assetSize/1024;
	model.size = kb.toFixed(2) + ' KB';
} 
else 
{
    model.size = assetSize + ' bytes';
}