document.addEventListener('DOMContentLoaded', function() {
    const uploadModal = document.getElementById('uploadModal');
    const uploadButton = document.getElementById('uploadButton');
    const fileInput = document.getElementById('fileInput');

    uploadButton.addEventListener('click', function() {
        const formData = new FormData();
        formData.append('file', fileInput.files[0]);

        fetch('/user/upload-data', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('Failed to upload data');
                }
            })
            .then(message => {

                $(uploadModal).modal('show');
            })
            .catch(error => {
                console.error('Error:', error);

            });
    });

});