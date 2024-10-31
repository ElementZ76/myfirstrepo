var cta = document.querySelector(".cta");
var check = 0;

cta.addEventListener('click', function(e){
    var text = e.target.nextElementSibling;
    var loginText = e.target.parentElement;
    text.classList.toggle('show-hide');
    loginText.classList.toggle('expand');
    if(check == 0)
    {
        cta.innerHTML = "<span style='font-size: 30px;'>&#11015;</span>";
        check++;
    }
    else
    {
        cta.innerHTML = "<span style='font-size: 30px;'>&#11015;</span>";
        check = 0;
    }
})