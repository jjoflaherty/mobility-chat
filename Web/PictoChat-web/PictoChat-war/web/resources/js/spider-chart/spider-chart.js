var spiderChart = function() {
    (function($) {
        $('.spider-chart').each(function(){
            var value = $(this).children('.spider-chart-data').first().text();
            var data = $.parseJSON(value);
            new Chart($(this).children('canvas').first().get(0).getContext("2d")).Radar(data, {
                responsive: true
            });
        });
    })(jQuery);
};

(function($) {
    $(document).ready(function(){
        spiderChart();
    });

    spiderChart();
})(jQuery);