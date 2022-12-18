$(document).ready(function (){
    $("#logoutLink").on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    });
})

function customizeDropDownMenu(){
    $(".dropdown>a").click(function (){
        location.href=this.href;
    });
}