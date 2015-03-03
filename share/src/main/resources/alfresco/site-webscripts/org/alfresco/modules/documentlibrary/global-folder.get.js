function main()
{
   var showRepositoryLink = "";
   if (config.scoped["RepositoryLibrary"] &&
       config.scoped["RepositoryLibrary"]["visible"])
   {
      showRepositoryLink = config.scoped["RepositoryLibrary"]["visible"].getValue();
   }
   model.showRepositoryLink = user.isAdmin || showRepositoryLink == "true";
}

main();