$(document).ready(function() {
    const $modal = $("#changePasswordModal");
    const $btn = $("#changePasswordBtn");
    const $restartBtn = $("#restartBtn");
    const $saveBtn = $("#saveBtn");
    const $checkPasswordBtn = $("#checkPasswordBtn");
    const $confirmPasswordMessage = $('#confirmChangePasswordMessage');
    const $confirmPasswordInput = $('#confirmChangePassword');
    const $newPasswordInput = $('#changePassword');
    const $newPasswordMessage = $('#changePasswordMessage');
    const $changePasswordFields = $("#changePasswordFields");
    const $currentPassword = $("#currentPassword");

    $changePasswordFields.hide();
    $currentPassword.removeClass('is-invalid is-valid');

    $btn.on("click", function() {
        $modal.modal('show');
    });

    $modal.on('hidden.bs.modal', function () {
        $currentPassword.val('');
        $newPasswordInput.val('');
        $confirmPasswordInput.val('');
        $changePasswordFields.hide();
        $currentPassword.removeClass('is-invalid is-valid');
        $newPasswordInput.removeClass('is-invalid is-valid');
        $confirmPasswordInput.removeClass('is-invalid is-valid');
    });

    $restartBtn.on("click", function () {
        $currentPassword.val('');
        $newPasswordInput.val('');
        $confirmPasswordInput.val('');
        $changePasswordFields.hide();
        $currentPassword.removeClass('is-invalid is-valid');
        $newPasswordInput.removeClass('is-invalid is-valid');
        $confirmPasswordInput.removeClass('is-invalid is-valid');
    });

    $checkPasswordBtn.on("click", function () {
        const currentPassword = $currentPassword.val();
        fetch('/user/checkPassword', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ password: currentPassword })
        })
            .then(response => {
                if (response.ok) {
                    $changePasswordFields.show();
                    $currentPassword.removeClass('is-invalid').addClass('is-valid');
                } else {
                    alert("Incorrect current password. Please try again.");
                    $currentPassword.removeClass('is-valid').addClass('is-invalid');
                }
            })
            .catch(error => {
                console.error('Error verifying password:', error);
            });
    });

    function validatingPassword() {
        const password = $newPasswordInput.val();
        const hasLowerCase = /[a-z]/.test(password);
        const hasUpperCase = /[A-Z]/.test(password);
        const hasNumber = /\d/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        const hasEightCharacters = password.length >= 8;

        if (hasLowerCase && hasUpperCase && hasNumber && hasSpecialChar && hasEightCharacters) {
            $newPasswordInput.removeClass('is-invalid').addClass('is-valid');
            $newPasswordMessage.hide();
        } else {
            $newPasswordInput.removeClass('is-valid').addClass('is-invalid');
            $newPasswordMessage.show();
        }

        // Display password strength message
        if (hasLowerCase && hasUpperCase && hasNumber && hasEightCharacters) {
            if (hasSpecialChar) {
                $newPasswordMessage.text('Your password is strong.')
                    .removeClass('text-danger')
                    .addClass('text-success');
            } else {
                $newPasswordMessage.text('Your password is normal.')
                    .removeClass('text-danger')
                    .addClass('text-warning');
            }
        } else {
            $newPasswordMessage.text('Your password is weak.')
                .removeClass('text-success')
                .addClass('text-danger');
        }
    }

    function checkPasswordsMatch() {
        const newPassword = $newPasswordInput.val();
        const confirmPassword = $confirmPasswordInput.val();

        if (newPassword === confirmPassword && newPassword.length > 0) {
            $confirmPasswordInput.removeClass('is-invalid').addClass('is-valid');
            $confirmPasswordMessage.hide();
        } else {
            $confirmPasswordInput.removeClass('is-valid').addClass('is-invalid');
            $confirmPasswordMessage.show();
        }
    }

    $newPasswordInput.on('input', function () {
        validatingPassword();
    });

    $confirmPasswordInput.on('input', function () {
        checkPasswordsMatch();
    });

    $saveBtn.on("click", function () {
        const newPassword = $newPasswordInput.val();
        const confirmPassword = $confirmPasswordInput.val();
        console.log("skill is saving.....")
        if (newPassword && confirmPassword && newPassword === confirmPassword) {
            fetch('/user/saveNewPassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ password: newPassword })
            })
                .then(response => {
                    if (response.ok) {
                        $modal.modal('hide');
                    } else {
                        console.error('Failed to save new password.');
                    }
                })
                .catch(error => {
                    console.error('Error saving new password:', error);
                });
        }
    });
})