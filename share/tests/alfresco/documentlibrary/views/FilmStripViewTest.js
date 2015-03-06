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
 * The purpose of this test is to ensure that keyboard accessibility is possible between the header and the 
 * main table. It should be possible to use the tab/shift-tab keys to navigate along the headers (and the enter/space key
 * to make requests for sorting) and then the cursor keys to navigate around the table itself.
 * 
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!assert",
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, assert, expect, require, TestCommon, keys) {

   var pause = 1500;

   // Setup some selectors for re-use...
   var previewCarouselSelector = "#FILMSTRIP_VIEW > div.preview";
   var thumbnailsCarouselSelector = "#FILMSTRIP_VIEW > div.items";

   var previewControlsSelector = previewCarouselSelector + " .controls";
   var prevPreviewControlSelector = previewControlsSelector + " > div.prev > img";
   var nextPreviewControlSelector = previewControlsSelector + " > div.next > img";
   var previewFrameSelector = previewCarouselSelector + " .frame > ol";
   var previewFrameItemsSelector = previewFrameSelector + " > li";

   var previewImgSelectorSuffix = " > div:nth-child(2) span > img";
   var thumbnailImgSelectorSuffix = " > div:nth-child(1) img";

   var thumbnailControlsSelector = thumbnailsCarouselSelector + " .controls";
   var prevThumbnailControlSelector = thumbnailControlsSelector + " > div.prev > img";
   var nextThumbnailControlSelector = thumbnailControlsSelector + " > div.next > img";
   var thumbnailFrameSelector = thumbnailsCarouselSelector + " .frame > ol";
   var thumbnailFrameItemsSelector = thumbnailFrameSelector + " > li";

         
   registerSuite({
      name: 'FilmStrip View Test',
      'Basic Test': function () {

         // var browser = this.remote;
         var testname = "FilmStripView Test";
         return TestCommon.loadTestWebScript(this.remote, "/FilmStripView", testname)

            // Wait for view to be created...

            // Check that the "Previous Preview" control is hidden...
            .findByCssSelector(prevPreviewControlSelector)
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Check that PREVIOUS preview control is NOT displayed...");
                  assert(result === false, "Test #1a - The previous preview control should not be displayed");
               })
            .end()

            // Check that the "Next Preview" control is displayed...
            .findByCssSelector(nextPreviewControlSelector)
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Check that next preview control IS displayed...");
                  assert(result === true, "Test #1b - The previous preview control should have been displayed");
               })
            .end()

            // Check that the "Previous Thumbnails" control is hidden...
            .findByCssSelector(prevThumbnailControlSelector)
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Check that PREVIOUS thumbnails control is NOT displayed...");
                  assert(result === false, "Test #1c - The previous thumbnails control should not be displayed");
               })
            .end()

            // Check that the "Next Thumbnails" control is hidden...
            .findByCssSelector(nextThumbnailControlSelector)
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Check that NEXT thumbnails control is NOT displayed...");
                  assert(result === false, "Test #1d - The next thumbnails control should not be displayed");
               })
            .end()

            // Count the number of preview items...
            .findAllByCssSelector(previewFrameItemsSelector)
               .then(function(elements) {
                  TestCommon.log(testname, "Searching for preview items using: " + previewFrameItemsSelector);
                  assert(elements.length === 4, "Test #2a - Expected 2 preview items, found: " + elements.length);
               })
            .end()

            // Check the first preview is displayed and the second is hidden...
            .findByCssSelector(previewFrameItemsSelector + ":nth-child(1)")
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Check that first preview is displayed...");
                  assert(result === true, "Test #2b - The first preview item should be displayed");
               })
            .end()

            .findByCssSelector(previewFrameItemsSelector + ":nth-child(2)")
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Check that second preview is hidden...");
                  assert(result === false, "Test #2c - The second preview item should be hidden");
               })
            .end();
      },
      'Test Next/Previous Preview': function () {
         // Wait for the data to load and the page to draw - this is currently slow and the rendering needs to be
         var browser = this.remote;
         var testname = "Filmstrip View - Previous/Next";
         
         // Click on the next preview item to scroll along...
         return browser
            .then(function() {
                  TestCommon.log(testname, "Starting preview/next test");
            })
            .findByCssSelector(nextPreviewControlSelector)
               .click()
            .end()

            .sleep(pause) // Wait for just over a second for the animation to complete...

            // Check the first preview is now hidden and the second is visible...
            // NOTE: Not easy to test that the first preview is off the screen at the moment (the widget may
            //       need updating to support the test!)
            // .findByCssSelector(previewFrameItemsSelector + ":nth-child(1)")
            //    .isVisible()
            //    .then(function(result) {
            //       assert(result === false, "Test #3a - The first preview should now be hidden");
            //    })
            //    .end()

            .findByCssSelector(previewFrameItemsSelector + ":nth-child(2)")
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Checking that the second preview item is displayed");
                  assert(result === true, "Test #3b - The second preview item should now be displayed");
               })
            .end()

            // Click previous...
            .findByCssSelector(prevPreviewControlSelector)
               .then(function() {
                  TestCommon.log(testname, "Clicking previous button: " + prevPreviewControlSelector);
               })
               .isVisible()
               .then(function(result) {
                  TestCommon.log(testname, "Checking that the previous button is displayed");
                  assert(result === true, "Test #3c - The previous button is not displayed");
               })
               .click()
            .end()
            .sleep(pause) // Wait for just over a second for the animation to complete...

            // Click the preview image to load folder...
            .findByCssSelector(previewFrameItemsSelector + ":nth-child(1)" + previewImgSelectorSuffix)
               .then(function(result) {
                  TestCommon.log(testname, "Clicking the preview image to load the folder, " + previewFrameItemsSelector + ":nth-child(1)" + previewImgSelectorSuffix);
               })
               .click()
            .end()

            .sleep(pause) // Wait for folder items to load...

            // Count the number of preview items...
            .findAllByCssSelector(previewFrameItemsSelector)
               .then(function(elements) {
                  assert(elements.length === 16, "Test #2a - Expected 14 preview items (+ 2 debug items), found: " + elements.length);
               })
            .end();
      },
      'Test Thumbnail Scrolls With Preview': function () {
         // Wait for the data to load and the page to draw - this is currently slow and the rendering needs to be
         var browser = this.remote;
         
         var testname = "Filmstrip View - Thumbnail scrolling";

         // Click the next preview selector 3 times (to check that the thumbnail frame scrolls)...
         return browser
            .then(function() {
               TestCommon.log(testname, "Starting thumbnail scrolls tests...");
               TestCommon.log(testname, "Clicking next preview: " + nextPreviewControlSelector);
            })
            .findByCssSelector(nextPreviewControlSelector)
            .then(function() {
               TestCommon.log(testname, "Click 1...");
            })
            .click()
            .sleep(pause)
            .then(function() {
               TestCommon.log(testname, "Click 2...");
            })
            .click()
            .sleep(pause)
            .then(function() {
               TestCommon.log(testname, "Click 3...");
            })
            .click()
            .sleep(pause)
            .then(function() {
               TestCommon.log(testname, "Click 4...");
            })
            .click()
            .sleep(pause)
         .end();
         // TODO: Check that 2nd frame of thumbnails is displayed...
      },
      'Test Preview Scrolls With Thumbnail Selection': function () {
         // Wait for the data to load and the page to draw - this is currently slow and the rendering needs to be
         var browser = this.remote;
         var testname = "Filmstrip View - Preview scrolling via thumbnail selection";

         // Move to the 3rd selection of thumbnails...
         return browser
            .then(function() {
                  TestCommon.log(testname, "Starting tests...");
            })
            .findByCssSelector(nextThumbnailControlSelector)
               .click()
               .sleep(pause)
            .end()

            // TODO: Check that 3rd frame of thumbnails is displayed...

            .findByCssSelector(thumbnailFrameItemsSelector + ":nth-child(10)" + thumbnailImgSelectorSuffix)
               .click()
               .sleep(pause)
            .end()

            .sleep(pause)

            // TODO: Check that 10th preview is displayed...

            .alfPostCoverageResults(browser);
      }
   });
});