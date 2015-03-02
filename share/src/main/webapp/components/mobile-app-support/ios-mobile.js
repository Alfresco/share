/**
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
 * iOS Mobile Application Support
 *
 * @namespace Alfresco
 */
(function()
{
   Alfresco.util.iOSMobileAppLink = function(htmlId)
   {
      function applink(failureURL)
      {
         return function()
         {
            this.parentNode.style.display = "none";
            var clickedAt = new Date;
            // Needs to be > 500ms for iPhone 3GS.
            setTimeout(function()
            {
               if (new Date - clickedAt < 2000)
               {
                  document.location.replace(failureURL);
               }
            }, 500);
         };
      }

      // Helper element for URL manipulation
      var link = document.createElement("a");
      link.href = document.location.href;
      link.hash = "#nomobile";
      var browserURL = encodeURIComponent(link.href);

      // Check for iOS 2-5 only - iOS 6 supports the newer <meta> tags approach
      if (/OS [2-5]_\d(_\d)? like Mac OS X/i.test(navigator.userAgent))
      {
         if (window.location.hash != "#nomobile")
         {
            var mobileDiv = YUIDom.get(htmlId + "-mobile"),
               mobileLink = YUIDom.get(htmlId + "-mobile-link");

            if (mobileDiv && mobileLink)
            {
               mobileLink.href += browserURL;
               mobileLink.onclick = applink("itms-apps://itunes.com/apps/alfresco");
               mobileDiv.style.display = "block";
            }
         }
      }
      else // iOS 6+
      {
         var metas = document.getElementsByTagName("meta");
         for (var index = 0; index < metas.length; index++)
         {
            if (metas[index].name == "apple-itunes-app")
            {
               if (window.location.hash == "#nomobile")
               {
                  metas[index].content = "";
               }
               else
               {
                  metas[index].content += browserURL;
               }
               break;
            }
         }
      }
   }
})();
