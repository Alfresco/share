var resultsToSkip = url.args.resultsToSkip == null ? 0 : url.args.resultsToSkip;
var maxResults = 10;

var articles = collectionService.getCollection(context.properties.section.id, args.collection, resultsToSkip, maxResults);

model.articles = articles;
model.pageNumber = articles.query.resultsToSkip / articles.query.maxResults + 1;
model.totalPages = Math.ceil(articles.totalSize / articles.query.maxResults); 
model.prevSkip = articles.query.resultsToSkip - articles.query.maxResults;
model.nextSkip = articles.query.resultsToSkip + articles.query.maxResults;




