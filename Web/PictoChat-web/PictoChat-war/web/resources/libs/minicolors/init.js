(function ($){
    $(document).ready(function(){

        //Change defaults
        $.minicolors.defaults = $.extend($.minicolors.defaults, {
            changeDelay: 200,
            letterCase: 'uppercase',
            control: 'wheel',
            theme: 'bootstrap',
            defaultValue: '#ffffff',
            position: 'top right',
            swatches: ['#f00', '#ff9900', '#ff0', '#0f0', '#00bfff', '#00f', '#8000ff']
        });

    });
}(jQuery));