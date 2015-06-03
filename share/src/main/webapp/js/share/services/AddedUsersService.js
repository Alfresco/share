/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
  /

/*
 * @module share/services/AddedUsersService
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
  "alfresco/core/Core"],
  function(declare, AlfCore) {
    
  return declare([AlfCore], {
     constructor: function share_services_AddedUsersService__constructor(args) {
        declare.safeMixin(this, args);
      },
     
     publishResults: function(data) {
        this.alfPublish("ADDED_USERS_LIST_TOPIC", data);
     }
  });
});