document.addEventListener('DOMContentLoaded', function() {
    const acceptRoleBtn = document.getElementById('acceptRemovedRoleBtn');

    acceptRoleBtn.addEventListener('click', function() {
        removeUserStatusAndRole();
    });
    function removeUserStatusAndRole() {
        fetch(`/user/updateUserStatusAndRole`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                pending: false,
                done: false,
                removed: false // Add the removed status
            })
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/logout';
                } else {
                    console.error('Failed to update user status and role');
                }
            })
            .catch(error => {
                console.error('Error updating user status and role:', error);
            });
    }
});
