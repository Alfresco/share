
<!--
<script type="text/javascript" src="/share/res/ctools/jquery.js"></script>
<script type="text/javascript" src="/share/res/ctools/protovis.js"></script>
<script type="text/javascript" src="/share/res/ctools/protovis-msie.js"></script>
<script type="text/javascript" src="/share/res/ctools/jquery.tipsy.js"></script>
<script type="text/javascript" src="/share/res/ctools/tipsy.js"></script>
<link type="text/css" href="/share/res/ctools/tipsy.css" rel="stylesheet"/>
<script type="text/javascript" src="/share/res/ctools/def.js"></script>
<script type="text/javascript" src="/share/res/ctools/pvc.js"></script>
-->

<@script type="text/javascript" src="/share/res/ctools/jquery.js"></@script>
<@script type="text/javascript" src="/share/res/ctools/protovis.js"></@script>
<@script type="text/javascript" src="/share/res/ctools/protovis-msie.js"></@script>
<@script type="text/javascript" src="/share/res/ctools/jquery.tipsy.js"></@script>
<@script type="text/javascript" src="/share/res/ctools/tipsy.js"></@script>
<@link type="text/css" href="/share/res/ctools/tipsy.css" rel="stylesheet"/>
<@script type="text/javascript" src="/share/res/ctools/def.js"></@script>
<@script type="text/javascript" src="/share/res/ctools/pvc.js"></@script>


<div id="pvcPie3"></div>
<script type="text/javascript">
   alert('$: ' + (typeof $) +
   ', pv: ' + pv +
   ', pv.have_SVG: ' + pv.have_SVG +
   ', jquery.tipsy: ' + (typeof $.fn.tipsy) +
   ', pv.Behavior.tipsy: ' + (typeof pv.Behavior.tipsy) +
   ', def: ' + def +
   ', pvc: ' + pvc);

   var pie = new pvc.PieChart({
      canvas: "pvcPie3",
      width: 600,
      height: 400,
      title: "Sample Donut!",
      titlePosition: "bottom",
      legend: false,
      tooltipEnabled: true,
      explodedSliceRadius: 15,
      valuesVisible: true,
      selectable: true,
      hoverable:  true,
      extensionPoints: {
         //slice_innerRadius: 30,
         slice_innerRadiusEx: '60%',
         titleLabel_font: "18px sans-serif"
      }
   });

   pie.setData(
         {
            "resultset": [
               ["London", 74],
               ["Paris", 48],
               ["New York", 37],
               ["Prague", 27],
               ["Stockholm", 22],
               ["Sydney", 19],
               ["Madrid", 18],
               ["Lisbon", 41],
               ["Pequim", 7],
               ["Rome", 48],
               ["Athens", 27],
               ["Luanda", 76],
               ["Ottawa", 21],
               ["Berlin", 30],
               ["Brasilia", 50],
               ["Beijing", 41]
            ],
            "metadata": [{
               "colIndex": 0,
               "colType": "String",
               "colName": "City"
            }, {
               "colIndex": 1,
               "colType": "Numeric",
               "colName": "Value"
            }]
         },
         {
            crosstabMode: false,
            seriesInRows: false
         });

   pie.render();



</script>
