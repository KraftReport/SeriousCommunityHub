document.getElementById('inputImage').addEventListener('change', function() {
    const file = this.files[0]; // Get the selected file
    const croppedImagePreview = document.getElementById('croppedImagePreview');
    let selectedArea = null; // Variable to store the selected area coordinates

    const reader = new FileReader();
    reader.onload = function(e) {
        const image = new Image();
        image.onload = function() {
            // Resize the image to a fixed size (e.g., 200x200) before displaying it
            const resizedImage = resizeImage(image, 250, 250); // Adjust the dimensions as needed

            // Display the resized image
            croppedImagePreview.innerHTML = '';
            croppedImagePreview.style.display = 'block';
            croppedImagePreview.style.display = 'flex';
            croppedImagePreview.style.alignItems = 'center';
            croppedImagePreview.style.justifyContent = 'center';
            croppedImagePreview.appendChild(resizedImage);

            // Initialize Jcrop
            $(resizedImage).Jcrop({
                aspectRatio: 1, // Adjust this as needed for your application
                onSelect: function(c) {
                    // Update the selected area coordinates
                    selectedArea = c;

                }
            });
        };
        image.src = e.target.result;
    };
    reader.readAsDataURL(file);

    // Function to resize the image to the specified dimensions
    function resizeImage(image, width, height) {
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        canvas.width = width;
        canvas.height = height;
        context.drawImage(image, 0, 0, width, height);
        const resizedImage = new Image();
        resizedImage.src = canvas.toDataURL('image/jpeg');
        return resizedImage;
    }

    // Add event listener to the "Next" button for cropping
    document.querySelector('#photoChangeForm button.next').addEventListener("click", function(event) {
        event.preventDefault(); // Prevent form submission
    });

    // Update the button text based on the selected file
    if (file) {
        document.querySelector('#photoChangeForm button.next').textContent = 'Next';
    } else {
        document.querySelector('#photoChangeForm button.next').textContent = 'Skip';
    }
    function saveRawImageToLocalStorage() {
        const file = document.getElementById('inputImage').files[0]; // Get the selected file
        const reader = new FileReader();
        reader.onload = function(e) {
            localStorage.setItem('rawImage', e.target.result);
        };
        reader.readAsDataURL(file);
    }
// Add an event listener to the next button to save the raw image when clicked
    document.querySelector('#photoChangeForm button.next').addEventListener("click", function(event) {
        event.preventDefault(); // Prevent form submission
        saveRawImageToLocalStorage();
    });
});