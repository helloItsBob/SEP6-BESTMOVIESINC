//*************************  OPEN REGISTRATION FORM  *************************//

const registrationText = document.getElementById("registerButton");
const registrationModal = document.getElementById("registrationPopup");

registrationText.addEventListener("click", function () {
    registrationModal.style.display = "block";
});

// close on clicking outside the box
window.addEventListener("click", function (event) {
    if (event.target === registrationModal) {
        registrationModal.style.display = "none";
    }
});