# Contributing guidelines

We are happy to accept your patches! For this thing to happen you have to follow some rules. Read this document to find out what you have to do to contribute to Share's code.

If you are interested in making a large contribution, please reach out to us before starting work so that we can discuss the likelihood of our accepting the contribution.

Share is made available under the terms of LGPLv3.

## Get Share ready for patching
1. Fork the project.
2. Clone the project to your local machine using: `git clone git@github.com:Alfresco/share.git` and add a new remote for the original repository (upstream).
3. Setup your environment by installing the following: jdk 1.8, maven 3.5, postgreSQL 9.6. After that, don't forget to edit your environment variables: JAVA_HOME and path.
4. To check that everything is alright, build and run Share by following the instructions:
    * build Share; in the folder were the project resides run: `mvn clean install -Penterprise`;
    * create a database and a user for it, both with the name `alfresco`; 
    * run the platform; go to alfresco subfolder and run: `mvn install -Prun`;
    * run the Share server; in the share subfolder run: `mvn install -Prun`.
	
## Make your change
For consistency, please follow these guidelines.

1. First of all, you need to raise a Jira ticket on the [ALF project](https://issues.alfresco.com/jira/projects/ALF/issues/ALF-21766?filter=allopenissues) for the issue that you want to address(contribution or bug). Make sure that the ticket is well documented according to it's type and it has all the information that is required. More about reporting an issue [here](https://community.alfresco.com/docs/DOC-6263-reporting-an-issue).
2. Create a new branch for the work that you're about to do. If you're fixing a bug, we recommend to name your branch following the pattern: `fix/<ticket-id>_<ShortDescription>`. The pattern for adding a new feature is: `feature/<ticket-id>_<ShortDescription>`.
3. When writing your code, be sure to follow the [Coding Standards for Alfresco Content Services](https://community.alfresco.com/docs/DOC-4658-coding-standards). There are some files that can be uploaded into the IDE and which take care of the formatting. You can find them in the `ide-config` folder. Also, we kindly ask you to configure your IDE to use for line endings the characters associated to Windows (CRLF).
4. Another important thing is to make sure that the licenses of the libraries that you use in your implementation are compatible with LGPLv3. 
5. When committing your work, please add a clear commit message (e.g. [Pull Request Commit Messages](https://community.alfresco.com/docs/DOC-6269-submitting-contributions#jive_content_id_Pull_Request_Commit_Messages)).
6. Include basic and clear documentation and modify or add unit tests to cover your contribution. When writing the unit tests be very careful to respect the folowing:
    * keep the test short (it should finish under 2 seconds);
    * use mocks to ensure good performance;
    * in the tests that do require an application context, do not use static references to the application context; use instance variables for the application context, and then, to get the desired context reference, in the setup method of the test use:<br />
    ``` 
    applicationContext = ApplicationContextHelper.getApplicationContect();
    ```
    * clean up after the tests - ensure that the cleanup happens even if the test fails;
    * don't use the `sleep()` method;
    * consider side effects on other tests/test classes;
    * keep the tests classes and methods focused on testing a certain functionality in a similar manner;
    * keep/add  proper tests in the correct project so that there would not be a need to run other project's tests to validate the code/functionality from another project.
7. After you finished working, run all the tests and make sure that they all pass.
8. If you changed a string in a view, let us know. The UX team has to validate it and also we have to take care of the localization.

## Share your contribution
1. Create a pull request against the original repository (upstream) on GitHub. Give the pull request a name that respects the pattern: `[<ticket-name>] - <Short description>`. Add in the description of the pull request what the code does and the link to the Jira ticket if it exists. At this stept you will have to accept the terms in the [Alfresco Contribution Agreement](https://community.alfresco.com/docs/DOC-7070-alfresco-contribution-agreement).
2. Keep track of your Jira issue in case further discussion is required and of your pull request to make possible changes requested. After the code review proccess is made and all the builds pass, your contribution will be merged into the original repository.
3. Finally, let everyone know you're an Alfresco Contributor by listing your contribution on the [Featured Contributions](https://community.alfresco.com/docs/DOC-5279-featured-contributions) page.