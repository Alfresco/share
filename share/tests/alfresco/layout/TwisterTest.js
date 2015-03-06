/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Richard Smith
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, assert, require, TestCommon, keys) {

   registerSuite({
      name: 'Twister Tests',
      'Twister mouse tests': function () {

         var browser = this.remote;
         var testname = "Twister mouse tests";
         return TestCommon.loadTestWebScript(this.remote, "/Twister", testname)

         // Click button 1
         .findById("BUTTON_1")
            .click()
            .end()

         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister renders correctly");
               expect(text).to.equal("Twister with heading level", "The first twister did not render correctly");
            })
            .end()

         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
            .then(function (rows) {
               TestCommon.log(testname,"Check facets are shown after clicking button 1");
               expect(rows).to.have.length.above(0, "There should some facets shown");
            })
            .end()

         .findByCssSelector("#TWISTER_NO_HEADING_LEVEL > div.alfresco-twister")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the second twister renders correctly");
               expect(text).to.equal("Twister with no heading level", "The second twister did not render correctly");
            })
            .end()

         .findByCssSelector("#TWISTER_BAD_HEADING_LEVEL > div.alfresco-twister")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the third twister renders correctly");
               expect(text).to.equal("", "The third twister did not render correctly");
            })
            .end()

         .findByXpath(TestCommon.consoleXpathSelector("A heading must have a numeric level from 1 to 6 and must have a label"))
            .then(
               function(element){TestCommon.log(testname,"Check the console logged the faulty twister");},
               function(err){assert(false, "The console did not log the faulty twister at xpath: " + TestCommon.consoleXpathSelector("A heading must have a numeric level from 1 to 6 and must have a label"));}
            )
            .end()

         // Click the title of twister 1
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .click()
            .end()

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is still visible");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible");
            })
            .end()

         .findByCssSelector("#TWISTER_HEADING_LEVEL > ul")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the facets have hidden");
               expect(text).to.equal("", "The facets should be hidden");
            })
            .end()

         // Click the title of twister 1
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .click()
            .end()

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is still visible");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible");
            })
            .end()

         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
            .then(function (rows) {
               TestCommon.log(testname,"Check facets are shown after clicking the title again");
               expect(rows).to.have.length.above(0, "There should be some facets shown");
            })
            .end()

         .alfPostCoverageResults(browser);
      },

      'Twister keyboard tests': function () {

         var browser = this.remote;
         var testname = "Twister keyboard tests";
         return TestCommon.loadTestWebScript(this.remote, "/Twister", testname)

         // Click button 1
         .findById("BUTTON_1")
            .click()
            .end()

         // Focus the title of twister 1
         .pressKeys(keys.TAB)
         
         // 'Click' the title with the return key
         .pressKeys(keys.RETURN)

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is visible after keyboard [RETURN]");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible after keyboard [RETURN]");
            })
            .end()

         // Facets should be hidden
         .findByCssSelector("#TWISTER_HEADING_LEVEL > ul")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the facets have hidden after keyboard [RETURN]");
               expect(text).to.equal("", "The facets should be hidden after keyboard [RETURN]");
            })
            .end()

         // 'Click' the title with the return key again
         .pressKeys(keys.RETURN)

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is visible after re-pressing keyboard [RETURN]");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible after re-pressing keyboard [RETURN]");
            })
            .end()

         // Facets should not be hidden
         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
            .then(function (rows) {
               TestCommon.log(testname,"Check facets are shown after re-pressing keyboard [RETURN]");
               expect(rows).to.have.length.above(0, "There should be some facets shown after re-pressing keyboard [RETURN]");
            })
            .end()

         // 'Click' the title with the space key
         .pressKeys(keys.SPACE)

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is visible after keyboard [SPACE]");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible after keyboard [SPACE]");
            })
            .end()

         // Facets should be hidden
         .findByCssSelector("#TWISTER_HEADING_LEVEL > ul")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the facets have hidden after keyboard [SPACE]");
               expect(text).to.equal("", "The facets should be hidden after keyboard [SPACE]");
            })
            .end()

         // 'Click' the title with the space key again
         .pressKeys(keys.SPACE)

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is visible after re-pressing keyboard [SPACE]");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible after re-pressing keyboard [SPACE]");
            })
            .end()

         // Facets should not be hidden
         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
            .then(function (rows) {
               TestCommon.log(testname,"Check facets are shown after re-pressing keyboard [SPACE]");
               expect(rows).to.have.length.above(0, "There should be some facets shown after re-pressing keyboard [SPACE]");
            })
            .end()

         // 'Click' the title with the 'k' key
         .pressKeys('k')

         // Title should still be visible
         .findByCssSelector("#TWISTER_HEADING_LEVEL > div.alfresco-twister > h3")
            .getVisibleText()
            .then(function (text) {
               TestCommon.log(testname,"Check the first twister title is visible after re-pressing keyboard [k]");
               expect(text).to.equal("Twister with heading level", "The first twister title is not visible after re-pressing keyboard [k]");
            })
            .end()

         // Facets should not be hidden
         .findAllByCssSelector(".alfresco-search-FacetFilter:not(.hidden)")
            .then(function (rows) {
               TestCommon.log(testname,"Check facets are still shown after pressing keyboard [k]");
               expect(rows).to.have.length.above(0, "There should be some facets shown after pressing keyboard [k]");
            })
            .end()

         .alfPostCoverageResults(browser);
      }
   });
});