document.addEventListener('DOMContentLoaded', function() {
    const acceptRoleBtn = document.getElementById('acceptRoleBtn');
    const successModal = new bootstrap.Modal(document.getElementById("successModal"));

    acceptRoleBtn.addEventListener('click', function() {
        updateUserStatusAndRole();
    });


    const submitButton = document.getElementById('submitRemovalReason');

    submitButton.addEventListener('click', function() {
        const removalReason = document.getElementById('removalReason').value;
        rejectAdminRole( removalReason);
        const modal = bootstrap.Modal.getInstance(document.getElementById('removeUserModal'));
        modal.hide();
        document.querySelector('.modal-backdrop').remove();
    });

    function rejectAdminRole(removalReason){
        fetch('/user/rejectAdminRole',{
            method: 'POST',
            headers:{
                'Content-Type' : 'application/json'
            },
            body: JSON.stringify({
                rejectedReason: removalReason
            })
        })
            .then(response => {
                if (response.ok) {
                } else {
                    console.error('Failed to update user status and role');
                }
            })
            .catch(error => {
                console.error('Error updating user status and role:', error);
            });
    }
    function updateUserStatusAndRole() {
        const pendingValue = false
        const doneValue =true;

        fetch(`/user/updateUserStatusAndRole`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                pending: pendingValue,
                done: doneValue,
                removed: false
            })
        })
            .then(response => {
                if (response.ok) {
                    successModal.show();
                    const acceptRoleBtn = document.getElementById('acceptAndLogout');
                    acceptRoleBtn.addEventListener('click', function() {
                        window.location.href = '/logout';
                    });
                } else {
                    console.error('Failed to update user status and role');
                }
            })
            .catch(error => {
                console.error('Error updating user status and role:', error);
            });
    }
});