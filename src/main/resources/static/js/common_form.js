const image_input=document.querySelector("#fileImage");

image_input.addEventListener("change",function(){
    const reader=new FileReader();
    reader.addEventListener("load",() =>{
        document.querySelector("#thumbnail").src=reader.result;
    })
    reader.readAsDataURL(this.files[0]);
})