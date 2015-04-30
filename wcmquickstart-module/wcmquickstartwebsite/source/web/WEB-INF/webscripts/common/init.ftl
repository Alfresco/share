<@endTemplate />

<script type="text/javascript">
<#-- Make whole of list/narrow clickable -->
$(document).ready(function(){                      
    $(".ln-list li").click(function(){//makes entire area "ln-list li" clickable
        window.location=$(this).find("a").attr("href");return false;
    });
    
    $(".brochure").click(function(){//makes entire area "brochure" clickable
        window.location=$(this).find("a").attr("href");return false;
    });     
});

<#-- Make search field blank on focus gained -->
(function() 
 {
    if (window.addEventListener) window.addEventListener("load", init, false);
    else if (window.attachEvent) window.attachEvent("onload", init);
    
    function init() 
    {
        var field = document.getElementById("search-phrase");
        field.onfocus=function() 
        {
            if (this.value == "search") this.value = "";
        };
    }
})();        
</script>
