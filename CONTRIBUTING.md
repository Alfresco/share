# Contributing guidelines

We are happy to accept your patches! This document explains what you have to do to contribute to Share's code.

If you are interested in making a large contribution, please reach out to us before starting work so that we can discuss the likelihood of our accepting the contribution.

Share is made available under the terms of LGPLv3.

## Get Share ready for patching
1. Fork the project.
2. Clone the project to your local machine using: `git clone git@github.com:Alfresco/share.git` and add a new remote for the original repository (upstream).
3. Setup your environment by installing the following: jdk 1.8, maven 3.5, postgreSQL 9.6. Don't forget to edit your environment variables: JAVA_HOME and path.
4. To check that your environment is functional, build and run Share by following these instructions:
    * Build Share by running this command in the folder where the project resides: `mvn clean install -Penterprise`;
    * Create a database named `alfresco` and a database user named `alfresco`;
    * Run the Content Repository by running this command from the `alfresco` subfolder: `mvn install -Prun`;
    * Run the Share web application by running this command from the `share` subfolder: `mvn install -Prun`.
	
## Make your change
For consistency, please follow these guidelines.

1. Raise a ticket in the [ALF project at issues.alfresco.com](https://issues.alfresco.com/jira/projects/ALF/issues/ALF-21766?filter=allopenissues) for the issue that you want to address (whether a contribution or a bug). As part of raising a ticket, you will have to accept the [Alfresco Contribution Agreement](https://community.alfresco.com/docs/DOC-7070-alfresco-contribution-agreement). Make sure that the ticket is well documented according to its type and it has all the information that is required. There is more information about reporting an ALF issue [here](https://community.alfresco.com/docs/DOC-6263-reporting-an-issue).
2. Create a new branch for the work that you're about to do. If you're fixing a bug, we recommend that you name your branch following the pattern: `fix/<ticket-id>_<ShortDescription>`. The pattern for adding a new feature is: `feature/<ticket-id>_<ShortDescription>`.
3. When writing your code, be sure to follow the [Coding Standards for Alfresco Content Services](https://community.alfresco.com/docs/DOC-4658-coding-standards). The `ide-config` folder in this project contains files that can be uploaded into your IDE to standardize the formatting. You must use Windows line ending characters (CRLF).
4. Make sure that the licenses of any libraries that you use in your implementation are compatible with LGPLv3.
5. When committing your work, please add a clear commit message (e.g. [Pull Request Commit Messages](https://community.alfresco.com/docs/DOC-6269-submitting-contributions#jive_content_id_Pull_Request_Commit_Messages)).
6. Include basic and clear documentation for your contribution.
7. Modify or add unit tests to cover your contribution. When writing the unit tests be very careful to respect the following:
    * keep the test short (it should finish in less than 2 seconds);
    * use mocks to ensure good performance;
    * if a test requires an application context, do not use static references. Instead, use instance variables for the application context, then get the desired context reference in the setup method of the test with the following:<br />
    ``` 
    applicationContext = ApplicationContextHelper.getApplicationContect();
    ```
    * clean up after the tests, and ensure that the cleanup happens even if the test fails;
    * don't use the `sleep()` method;
    * consider side effects on other tests and test classes;
    * keep the tests classes and methods focused on testing a specific functionality in a similar manner;
    * keep/add proper tests in the correct project so that it is not necessary to run another project's tests to validate the code or functionality.
7. After you finish working, run all the tests and make sure that they all pass.
8. If you change a string that appears in the user interface, highlight it in your merge request. We will have our UX team review it and we will take care of the localization.

## Share your contribution
1. Create a pull request against the original repository (upstream) on GitHub. Give the pull request a name that respects the pattern: `[<ticket-name>] - <Short description>`. In the pull request description, include what the code does and the link to the ALF project ticket.
2. Keep track of your ALF project ticket and pull request in case further discussion is required.
3. In our next triage session, we will schedule a time to review your contribution. If we accept your contribution, we will merged it into the main repository.
4. Finally, let everyone know you're an Alfresco Contributor by listing your contribution on the [Featured Contributions](https://community.alfresco.com/docs/DOC-5279-featured-contributions) page.