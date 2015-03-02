/**
 * A microformat parser. Allows export and import to/from DOM of uf data
 * 
 * @constructor
 * @param args {object} Config object specifying micorformat to use and element
 * to parse eg 
 * {
 *   ufSpec : hcalendar,
 *   srcNode : el
 * }
 */

function microformatParser(args){
   var D= YAHOO.util.Dom;
   var E = YAHOO.util.Event;
   var L = YAHOO.lang;
   var data = {
       registry : {},
       renderers : {},
       parsers : {},
       refs : {},
       parsedData : {}
   };

   var srcNode = args.srcNode;
   delete args.srcNode;   
   var ufSpec = args.ufSpec || {};

   delete args.ufSpec;
   //init data to null using spec
   if (!args.data)
   {

       for (var i = 0;i< ufSpec.fields.length;i++)
       {
           data.registry[ufSpec.fields[i]] = null;
       }
   }
   
   //or init data if passed
   else if (args.data)
   {
       for (var item in args.data)
       {

           if ( (item !== 'parsers') || (item !== 'renderers') )
           {
               if (item in ufSpec.fields)
               {
                   data.registry[item] = args[item];
               }
           }
       }
   }
   //init parsers
   if (args.parsers)
   {

       for (var p in args.parsers)
       {
           _addParser(ufSpec.name+'.'+ufSpec.root+'.'+p, args.parsers[p]);
       }
   }

   //init renderers
   if (args.renderers)
   {

       for (var r in args.renderers)
       {
           data.renderers[p] = args.renderers[p];
       }
   }
   
  /**
   * Extracts data from dom elements 
   */ 
  var extractData = function (node) {
      switch (node.nodeName.toLowerCase()) {
          case 'span': 
            return node.title || node.innerHTML;
          case 'img': 
            return (node.alt);
      };
      while (node && node.hasChildNodes())
      {
          node = node.firstChild;
      }
      return node.nodeValue;
  };
  /**
   * Helper function to set text values of nodes
   */ 
  var setText = function (node,value) {
    if (node.innerText) {
      node.innerText = value;
    }
    else {
      while (node.hasChildNodes())
      {
          node=node.firstChild;
          if (node.nodeType == 3)
          {
              node.nodeValue = value;
          }
          else {
              return setText(node,value); 
          }
      };
    }
  };
  /**
   * injects uf field value into DOM
   * 
   */
  var injectData = function(node,value) {
      if (YAHOO.lang.isString(value)) 
      {
          switch (node.nodeName.toLowerCase()) {
             case 'span': 
                 if (node.title)
                 {
                  node.title=value;
                 }
                 else 
                 {
                   node.innerHTML = value
                 }
                 return;
             case 'img': 
                 node.alt = value;
                 return;
           }          
      }
      return setText(node,value);
  };

  /**
   * parses a field datum 
   * @param data {object} value of uf field
   * @param id {string} identifier of field
   * @param ufName {string} name of uf spec to use for parsers 
   */ 
  var parseField = function (id) {
      var parser = data.parsers[id] || ufSpec.parsers[id];
     
      data.parsedData[id] = (parser) ? parser(data.registry[id]) : data.registry[id];
      return null;
  };
  
  /**
   * Update specified field 
   */
  var updateField = function(o,id,value)
  {
      o[id] = value;
      parseField(o,id);
  };
  
  /**
   * Returns parser method for specified field
   * 
   * @method getParser 
   * @param id {String} id of field to return parser for
   * 
   * @return {Function} parser function
   */
  var getParser = function(id)
  {
      return data.parsers[id];
  };
  
  /**
   * adds a parser for specified field
   * 
   * @method _addParser
   * 
   * @param name {String} name of field to add parser for
   * @param value {Function} parser function to add
   */
  function _addParser(name,value)
  {
      data.parsers[name] = value;
  };
  
  /**
   * adds a renderer for specified field
   * 
   * @method _addRenderer
   * 
   * @param name {String} name of field to add renderer for
   * @param value {Function} renderer function to add
   */
  function _addRenderer(name,value)
  {
      data.renderers[name] = value;
  };
  
  /**
   * renders microformat data to DOM
   *  
   * @method _render
   */
  var _render = function() {
      for (var i = 0;i< ufSpec.fields.length;i++)
      {

        var id = ufSpec.fields[i];
        var nodeRef = data.refs[id];
        if (nodeRef)
        {
            injectData(nodeRef,data.registry[id]);            
        }
        var renderer = data.renderers[id] || ufSpec.renderers[id];
        if (renderer){
            renderer(nodeRef,data.parsedData[id]);
        }
      }
  };
  
  /**
   * Updates internals using specified data and optionally renders to DOM too
   * 
   * @method _update
   * @param oData {object} Value object of new data
   * @param doRender {Boolean} Flag to denote whether to render to DOM or not
   */
  function _update(oData,doRender)
  {
    if (oData)
    {
        for (var id in oData)
        {
            if ( id in data.registry ){
                data.registry[id] = oData[id];
                parseField(id);
            }
        }
    }
    if (doRender && doRender===true)
    {
        _render();
    }
    
  }
  
  /**
   * Parse specified node
   * 
   * @method _parse
   * @param node {Object} Element to parse for microformat data
   *  
   */
  function _parse(node){
    var node = node || srcNode;
    var root = ufSpec.root;
    var fields = ufSpec.fields;
    var numFields = fields.length;
    if (D.hasClass(node,ufSpec.root))
    {
        data.refs = {};
        data.parsedData = {};
        var ufData = {};
        data.refs['parentElement'] = node;
        //for every uf field, extract data
        for (var j=0;j<numFields;j++)
        {
            var id = fields[j];
            var fieldNode = D.getElementsByClassName(id,null,node);
            if (fieldNode && fieldNode[0])
            {
                fieldNode = fieldNode[0];

                data.refs[id] = fieldNode;
                var extractedData = extractData(fieldNode);

                if (extractedData)
                {
                    ufData[id] = extractedData;
                }
            }
        }

        _update(ufData,false);
        return data.registry;
    }
    return null;
  }
  return {
      /**
       * Parses specified node into uf data 
       */
      parse : function(node) {
          return _parse(node);
      },
      /**
       * Renders internal state to Dom
       */  
      render : function(){
         return _render();
      },
      /**
       *  Update multiple fields at a time
       */
      update : function(oData,doRender) {
         return _update(oData,doRender);
      },
      getType : function() {
          return ufSpec.name;
      },
      addParser : function(name,value)
      {
             return _addParser(name,value);
      },
      addRenderer : function(name,value)
      {
            return _addRenderer(name,value);
      },
      getAll : function()
      {
          return data;
      },
      get : function(name,parsedValue) {
          var parsedValue = parsedValue || false;
          if (parsedValue)
          {
              return data.parsedData[name] || '';
          }
          else {
              return data.registry[name] || '';
          }
      }
  };
};



