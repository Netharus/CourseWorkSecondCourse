$(document).ready(function() {
    const modal=new bootstrap.Modal("#modalDialog")
    $(".linkMinus").on("click", function(evt) {
        evt.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;
        if (newQuantity > 0) {
            quantityInput.val(newQuantity);
        } else {
            $("#modalBody").html("Минимальное количество 1")
            modal.show();
        }
    });
    $(".linkPlus").on("click", function(evt) {
        evt.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;
        if (newQuantity <= 10) {
            quantityInput.val(newQuantity);
        } else {
            $("#modalBody").html("Максимальное количество 10")
            modal.show();
        }
    });
});