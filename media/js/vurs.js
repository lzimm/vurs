(function() {
    $(window).ready(function() {
        $("input, textarea").each(function() {
            $(this).attr("background", $(this).css("background"));
            $(this).focus(function() {
                $(this).css("background", "transparent");
            }).blur(function() {
                if (!$(this).val()) {
                    $(this).css("background", $(this).attr("background"));
                }
            });
            
            if ($(this).val()) {
                $(this).css("background", "transparent");
            }
        });
    });
})();