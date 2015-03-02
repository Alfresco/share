hcalendar = {
    name:'hcalendar',
    root:'vevent',
    fields : [
        'class', 
        'description', 
        'dtend', 
        'dtstamp', 
        'dtstart',
        'duration',
        'location',
        'status',
        'summary', 
        'uid', 
        'last-modified', 
        'url',
        'category'
    ],
    parsers : {
        dtstart : Alfresco.util.fromISO8601,
        dtend : Alfresco.util.fromISO8601,
        /*
         * Parses duration field into an object
         * 
         * @param data {String} duration value in ical format eg PT2H15M
         * @return {Object} Value object containing all elements of 
         * specified duration as a field 
         * {
         *   H: 2,
         *   M: 15
         * }
         */
        duration : function() 
        {
            var durationRE = { //order matters
                'W' : /P.*?([0-9]+)W/,
                'D' : /P.*?([0-9]+)D/,
                'H' : /P.*?T([0-9]+)H/,
                'M' : /P.*?T.*?([0-9]+)M/,
                'S' : /P.*?T.*?([0-9]+)S/
            };
            return function(data) 
            {
                if ( !data )
                {
                    return;
                }
                var durationElements = {};
                for (var re in durationRE)
                {
                    var r = data.match(durationRE[re]);
                    if (r)
                    {
                        durationElements[re] = r[1];
                    } 
                }
                return durationElements;
            };
        }(),
        /**
         * Parses data into an array of tags
         * 
         * @method category
         * @return {Array} Array of tags 
         */
        category : function(data) 
        {
          return data.split(',');
        }
    },
    renderers : {
       /**
        * Renderer method of start date field
        *  
        * @method dtstart
        * @param node {Object} Element to render to
        * @param parsedData {Object} Parsed data to render
        */
       dtstart : function(node,parsedData)
       {
          if (node!==null && parsedData!=null)
          {
            
            node.innerHTML = Alfresco.util.formatDate(parsedData,'HH:MM');
          }

       },
       /**
        * Renderer method of end date field
        *  
        * @method dtend
        * @param node {Object} Element to render to
        * @param parsedData {Object} Parsed data to render
        */
       dtend : function(node,parsedData)
       {           
           if (node!==null && parsedData!=null)
           {
            node.innerHTML = Alfresco.util.formatDate(parsedData,'HH:MM');
           }

       },
       /**
        * Renderer method of duration field
        *  
        * @method duration
        * @param node {Object} Element to render to
        * @param parsedData {Object} Parsed data to render
        */
       duration : function(node,parsedData)
       {
          var renderedData = []; 
          var text = {
             'H' : ['hour','hours'],
             'M' : ['minute','minutes'],
             'D' : ['day','days'],
             'W' : ['week','weeks'],
             'S' : ['second','seconds']
         };
         for (var item in parsedData)
         {
             renderedData.push(parsedData[item]  + ' ' + ( (parsedData[item]>1) ? text[item][1] : text[item][0]));
         }

         if (node)
         {
             node.innerHTML = renderedData.join(', ');
         }
         return renderedData.join(', ');                      
       },
       /**
        * Renderer method of category utilfield
        *  
        * @method category
        * @param node {Object} Element to render to
        * @param parsedData {Object} Parsed data to render
        */
       category : function(node,parsedData)
       {
         if (node && parsedData)
          {
              node.innerHTML = parsedData.join(' ');
              return parsedData.join(' ');
          }
          
       }
    }
};