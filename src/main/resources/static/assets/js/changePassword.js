const modal = document.getElementById("changePasswordModal");
const btn = document.getElementById("changePasswordBtn");
const span = document.getElementsByClassName("close")[0];
const restartBtn = document.getElementById("restartBtn");
const saveBtn = document.getElementById("saveBtn");
const checkPasswordBtn = document.getElementById("checkPasswordBtn");
const confirmPasswordMessage = document.getElementById('confirmChangePasswordMessage');
const confirmPasswordInput = document.getElementById('confirmChangePassword');
const newPasswordInput = document.getElementById('changePassword');
const newPasswordMessage = document.getElementById('changePasswordMessage');
document.getElementById("changePasswordFields").style.display ="none";
document.getElementById("currentPassword").classList.remove('is-invalid');
document.getElementById("currentPassword").classList.remove('is-valid');


btn.onclick = function() {
    modal.style.display = "block";
}

span.onclick = function() {
    modal.style.display = "none";
    document.getElementById("currentPassword").value = "";
    document.getElementById("changePassword").value = "";
    document.getElementById("confirmChangePassword").value = "";
    document.getElementById("changePasswordFields").style.display ="none";
    document.getElementById("currentPassword").classList.remove('is-invalid');
    document.getElementById("currentPassword").classList.remove('is-valid');
    newPasswordInput.classList.remove('is-invalid');
    newPasswordInput.classList.remove('is-valid');
    confirmPasswordInput.classList.remove('is-invalid');
    confirmPasswordInput.classList.remove('is-valid');
}

restartBtn.onclick = function() {
    document.getElementById("currentPassword").value = "";
    document.getElementById("changePassword").value = "";
    document.getElementById("confirmChangePassword").value = "";
    document.getElementById("changePasswordFields").style.display ="none";
    document.getElementById("currentPassword").classList.remove('is-invalid');
    document.getElementById("currentPassword").classList.remove('is-valid');
    newPasswordInput.classList.remove('is-invalid');
    newPasswordInput.classList.remove('is-valid');
    confirmPasswordInput.classList.remove('is-invalid');
    confirmPasswordInput.classList.remove('is-valid');
}
checkPasswordBtn.onclick = function() {
    const currentPassword = document.getElementById("currentPassword").value;
    fetch('/user/checkPassword', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: currentPassword
    })
        .then(response => {
            if (response.ok) {
                document.getElementById("changePasswordFields").style.display ="block";
                document.getElementById("currentPassword").classList.remove('is-invalid');
                document.getElementById("currentPassword").classList.add('is-valid');
            } else {
                alert("Incorrect current password. Please try again.");
                document.getElementById("currentPassword").classList.remove('is-valid');
                document.getElementById("currentPassword").classList.add('is-invalid');
            }
        })
        .catch(error => {
            console.error('Error verifying password:', error);
        });

    function validatingPassword() {
        const password = newPasswordInput.value;
        const HasLowerCase = /[a-z]/.test(password);
        const HasUpperCase = /[A-Z]/.test(password);
        const HasNumber = /\d/.test(password);
        const HasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        const HasEightCharacters = password.length >= 8;

        if (HasLowerCase && HasUpperCase && HasNumber && HasSpecialChar && HasEightCharacters) {
            newPasswordInput.classList.remove('is-invalid');
            newPasswordInput.classList.add('is-valid');
            newPasswordMessage.style.display = 'none';
        } else {
            newPasswordInput.classList.remove('is-valid');
            newPasswordInput.classList.add('is-invalid');
            newPasswordMessage.style.display = 'block';
        }

        // Display password strength message
        if (HasLowerCase && HasUpperCase && HasNumber && HasEightCharacters) {
            if (HasSpecialChar) {
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


    newPasswordInput.addEventListener('focus', function() {
        document.getElementById('newPasswordMessage').style.display = 'block';
        validatingPassword();
    });


    newPasswordInput.addEventListener('blur', function() {
        validatingPassword();
    });


    newPasswordInput.addEventListener('input', function() {
        validatingPassword();
    });

    confirmPasswordInput.addEventListener('input', function() {
        checkPasswordsMatch();
    });


    function checkPasswordsMatch() {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (newPassword === confirmPassword && newPassword.length > 0) {
            confirmPasswordInput.classList.remove('is-invalid');
            confirmPasswordInput.classList.add('is-valid');
            confirmPasswordMessage.style.display = 'none';

        } else {
            confirmPasswordInput.classList.remove('is-valid');
            confirmPasswordInput.classList.add('is-invalid');
            confirmPasswordMessage.style.display = 'block';

        }
    }

    saveBtn.addEventListener('click', function() {
        const staffId = localStorage.getItem('staffId');
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (staffId && newPassword && confirmPassword && newPassword === confirmPassword) {
            fetch('/saveNewPassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ staffId: staffId, password: newPassword })
            })
                .then(response => {
                    if (response.ok) {
                        modal.style.display = "none";
                    } else {

                    }
                })
                .catch(error => {
                    console.error('Error saving new password:', error);
                });
        } else {

        }
    });
}