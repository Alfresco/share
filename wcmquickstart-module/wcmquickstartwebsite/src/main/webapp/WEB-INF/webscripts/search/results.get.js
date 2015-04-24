model.pageNumber = results.query.resultsToSkip / results.query.maxResults + 1;
model.totalPages = Math.ceil(results.totalSize / results.query.maxResults); 
model.prevSkip = results.query.resultsToSkip - results.query.maxResults;
model.nextSkip = results.query.resultsToSkip + results.query.maxResults;



