/* This piece of controller code checks to see whether or not a "name" argument has been provided (this
 * could be done either directly as a request parameter or hopefully via a URI template argument
 */
var personToWelcome = page.url.templateArgs["name"];
model.displayWelcome = false; 
if (personToWelcome != null)
{
    model.displayWelcome = true;
    model.name = personToWelcome;
}
