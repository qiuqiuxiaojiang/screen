$(function () {
    $("input[name='checkall']").click(function () {
        if ($(this).is(":checked")) {
            $("input[name='types']").prop("checked", true);
        } else {
            $("input[name='types']").prop("checked", false);
        }
    });
    $("#sub").click(function () {
        $(".res").toggle();
    })
});