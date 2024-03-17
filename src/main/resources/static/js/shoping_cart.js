const modal=new bootstrap.Modal("#modalDialog")
$(document).ready(function() {

    $(".linkMinus").on("click", function(evt) {
        evt.preventDefault($(this));
        decreaseQuantity($(this))
    });
    $(".linkPlus").on("click", function(evt) {
        evt.preventDefault($(this));
        increaseQuantity($(this))
    });
    $(".linkRemove").on("click", function(evt) {
        evt.preventDefault($(this));
        removeProduct($(this))

    });
});
function decreaseQuantity(link){
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;
    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId,newQuantity)
    } else {
        $("#modalBody").html("Минимальное количество 1")
        modal.show();
    }
}
function increaseQuantity(link){
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;
    if (newQuantity <= 10) {
        quantityInput.val(newQuantity);
        updateQuantity(productId,newQuantity)
    } else {
        $("#modalBody").html("Максимальное количество 10")
        modal.show();
    }
}
function updateQuantity(productId,quantity){
    url= contextPath+"cart/update/"+productId+"/"+quantity;
    $.ajax({
        type: "POST",
        url:url,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName,csrfValue);
        }
    }).done(function (updatedSubtotal){

        updateSubtotal(updatedSubtotal,productId)
        updateTotal();
    }).fail(function (){
        $("#modalBody").html("Произошла ошибка во время дабавления товара в корзину")
        modal.show();
    });
}
function updateSubtotal(updatedSubtotal,productId){
    //formatedSubtotal=$.number(updatedSubtotal,2);
    $("#subtotal"+productId).html(updatedSubtotal)
}
function updateTotal(){
    newTotal=0.0
    $(".subtotal").each(function(index,element){

        newTotal+=parseFloat(element.innerHTML);

    })
    formatedTotal=$.number(newTotal,2);
    $("#total").html('$'+formatedTotal)
}
function removeProduct(link) {
    url=link.attr("href")
    $.ajax({
        type: "DELETE",
        url:url,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName,csrfValue);
        }
    }).done(function (response){
        alert(response)
    }).fail(function (){
        $("#modalBody").html("Произошла ошибка во время удалила товара в корзину")
        modal.show();
    });
}