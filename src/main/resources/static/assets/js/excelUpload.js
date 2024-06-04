document.addEventListener('DOMContentLoaded', function() {
    fetch('/user/current-interval')
        .then(response => response.json())
        .then(intervalInMillis => {
            const intervalInHours = Math.floor(intervalInMillis / (60 * 60 * 1000));
            const intervalInMinutes = Math.floor((intervalInMillis % (60 * 60 * 1000)) / (60 * 1000));

            document.getElementById('hours').value = intervalInHours;
            document.getElementById('minutes').value = intervalInMinutes;
        })
        .catch(error => console.error('Error fetching current interval:', error));
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
document.getElementById('setIntervalButton').addEventListener('click', function(event) {
    const hours = document.getElementById('hours').value;
    const minutes = document.getElementById('minutes').value;
    fetch('/user/set-interval', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `hours=${hours}&minutes=${minutes}`
    })
        .then(response => response.json())
        .then(data => alert('Interval set successfully'))
        .catch(error => console.error('Error:', error));
});
