var tristate = function() {
    (function($) {
        $('.tri-state').each(function(){
            if (!$(this).hasClass('hide-radios')) {
                $(this).addClass('hide-radios');

                $(this).find('label').each(function(){
                    $(this).click(function(){

                        if ($(this).find('span').first().hasClass('yes')) {
                            $(this).closest('tr').find('span.no').closest('td').find('input').prop('checked', true);
                        } else {
                            $(this).closest('tr').find('span.yes').closest('td').find('input').prop('checked', true);
                        }

                        return false;

                    });
                });
            }
        });
    })(jQuery);
};

(function($) {
    $(document).ready(function(){
        tristate();
    });

    tristate();
})(jQuery);