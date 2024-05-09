
document.addEventListener('DOMContentLoaded', function() {
    const input = document.querySelector("#phoneInput");
    const iti = window.intlTelInput(input, {
        initialCountry: "mm",
        preferredCountries: ["mm", "us", "cn", "in"],
        separateDialCode: true
    });


    document.getElementById('phoneInput').addEventListener('input', function() {
        const phoneInput = document.getElementById('phoneInput');
        const phoneNumber = phoneInput.value;
        const isValid = phoneNumber.length === 9;

        if (isValid) {
            phoneInput.classList.remove('is-invalid');
            phoneInput.classList.add('is-valid');
        } else {
            phoneInput.classList.remove('is-valid');
            phoneInput.classList.add('is-invalid');
        }
    });

    document.getElementById('dobInput').addEventListener('change', function() {
        const dobInput = document.getElementById('dobInput');
        const dob = new Date(dobInput.value);
        const today = new Date();
        const age = today.getFullYear() - dob.getFullYear();
        const isOldEnough = age >= 18;

        if (isOldEnough) {
            document.querySelector('#phoneChangeForm button.next').style.display = 'block';
            dobInput.classList.remove('is-invalid');
            dobInput.classList.add('is-valid');
        } else {
            document.querySelector('#phoneChangeForm button.next').style.display = 'none';
            dobInput.classList.remove('is-valid');
            dobInput.classList.add('is-invalid');
            alert('You are not old enough to use this website.');
        }
    });

    document.getElementById('genderInput').addEventListener('change', function() {
        const genderInput = document.getElementById('genderInput');
        const gender = genderInput.value;

        if (gender) {
            genderInput.classList.remove('is-invalid');
            genderInput.classList.add('is-valid');
        } else {
            genderInput.classList.remove('is-valid');
            genderInput.classList.add('is-invalid');
        }
    });

    // Function to save the form data to local storage
    function saveFormDataToLocalStorage() {
        if (!window.localStorage) {
            console.error('Local storage is not available.');
            return;
        }

        const phoneNumber = document.getElementById('phoneInput').value;
        const dob = document.getElementById('dobInput').value;
        const gender = document.querySelector('input[name="gender"]:checked').value;

        localStorage.setItem('phoneNumber', phoneNumber);
        localStorage.setItem('dob', dob);
        localStorage.setItem('gender', gender);
    }

    // Add an event listener to the next button to save the form data when clicked
    document.querySelector('#phoneChangeForm button.next').addEventListener("click", function(event) {
        event.preventDefault(); // Prevent form submission
        saveFormDataToLocalStorage();
    });
});