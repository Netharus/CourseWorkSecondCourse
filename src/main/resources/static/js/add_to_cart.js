$(document).ready(function(){
    $("#buttonAdd2Cart").on("click",function(evt){
        addToCart();
    });
});
function addToCart(){
    const modal=new bootstrap.Modal("#modalDialog")
    quantity=$("#quantity"+productId).val();
    url= contextPath+"cart/add/"+productId+"/"+quantity;
    $.ajax({
        type: "POST",
        url:url,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName,csrfValue);
        }
    }).done(function (p){

        $("#modalBody").html(response)
        modal.show();
    }).fail(function (){
        $("#modalBody").html("Произошла ошибка во время дабавления товара в корзину")
        modal.show();
    });
}