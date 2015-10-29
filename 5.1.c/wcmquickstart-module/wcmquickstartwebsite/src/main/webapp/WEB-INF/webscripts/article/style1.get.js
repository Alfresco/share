var title = asset.title
if ((title==null) || (title.length() == 0))
{
    title = asset.name
}
model.title = title;
