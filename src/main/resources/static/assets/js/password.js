document.addEventListener('DOMContentLoaded', function() {
    const newPasswordInput = document.getElementById('newPassword');
    const newPasswordMessage = document.getElementById('newPasswordMessage');
    const nextButton = document.querySelector('.next');
    const confirmPasswordMessage = document.getElementById('confirmPasswordMessage');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    // Function to validate the password and display the error message if invalid
    function validatePassword() {
        const password = newPasswordInput.value;
        const hasLowerCase = /[a-z]/.test(password);
        const hasUpperCase = /[A-Z]/.test(password);
        const hasNumber = /\d/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        const hasEightCharacters = password.length >= 8;

        if (hasLowerCase && hasUpperCase && hasNumber && hasSpecialChar && hasEightCharacters) {
            newPasswordInput.classList.remove('is-invalid');
            newPasswordInput.classList.add('is-valid');
            newPasswordMessage.style.display = 'none';
            nextButton.style.display = 'block';
        } else {
            newPasswordInput.classList.remove('is-valid');
            newPasswordInput.classList.add('is-invalid');
            newPasswordMessage.style.display = 'block';
            nextButton.style.display = 'none';
        }

        // Display password strength message
        if (hasLowerCase && hasUpperCase && hasNumber && hasEightCharacters) {
            if (hasSpecialChar) {
                newPasswordMessage.textContent = 'Your password is strong.';
                newPasswordMessage.classList.remove('text-danger');
                newPasswordMessage.classList.add('text-success');
            } else {
                newPasswordMessage.textContent = 'Your password is normal.';
                newPasswordMessage.classList.remove('text-danger');
                newPasswordMessage.classList.add('text-warning');
            }
        } else {
            newPasswordMessage.textContent = 'Your password is weak.';
            newPasswordMessage.classList.remove('text-success');
            newPasswordMessage.classList.add('text-danger');
        }
    }

    // Show the message and validate the password when the input field gains focus
    newPasswordInput.addEventListener('focus', function() {
        document.getElementById('newPasswordMessage').style.display = 'block';
        validatePassword();
    });

    // Validate the password when the input field loses focus
    newPasswordInput.addEventListener('blur', function() {
        validatePassword();
    });

    // Validate the password when the input field changes
    newPasswordInput.addEventListener('input', function() {
        validatePassword();
    });

    confirmPasswordInput.addEventListener('input', function() {
        checkPasswordsMatch();
    });

    // Function to check if passwords match and enable/disable the next button
    function checkPasswordsMatch() {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (newPassword === confirmPassword && newPassword.length > 0) {
            confirmPasswordInput.classList.remove('is-invalid');
            confirmPasswordInput.classList.add('is-valid');
            confirmPasswordMessage.style.display = 'none';
            nextButton.style.display = 'block';
        } else {
            confirmPasswordInput.classList.remove('is-valid');
            confirmPasswordInput.classList.add('is-invalid');
            confirmPasswordMessage.style.display = 'block';
            nextButton.style.display = 'none';
        }
    }
    function savePasswordToLocalStorage() {
        const newPassword = newPasswordInput.value;
        localStorage.setItem('savedPassword', newPassword);
    }
    nextButton.addEventListener('click', function() {
        savePasswordToLocalStorage();
    });
    confirmPasswordInput.addEventListener('input', function() {
        if (confirmPasswordInput.value.length === 0) {
            confirmPasswordMessage.style.display = 'none';
        }
    });
});