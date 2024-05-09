var checkboxes = document.querySelectorAll('#hobbyChangeForm input[type="checkbox"]');
var button = document.querySelector('#hobbyChangeForm button');

// Initialize selectedHobbies variable
var selectedHobbies = '';

// Attach change event listeners to checkboxes
checkboxes.forEach(function(checkbox) {
    checkbox.addEventListener('change', function() {
        if (this.checked) {
            selectedHobbies += this.parentElement.textContent.trim() + ',';
            if (selectedHobbies !== '') {
                button.textContent = 'Next';
            }
        } else {
            selectedHobbies = selectedHobbies.replace(this.parentElement.textContent.trim() + ',', '');
            if (selectedHobbies === '') {
                button.textContent = 'Skip';
            }
        }
    });
});

// Attach click event listener to button
button.addEventListener('click', function() {
    if (selectedHobbies !== '') {
        // Store the selected hobbies as a string in the database
        localStorage.setItem('Hobbies', selectedHobbies.slice(0, -1));
        console.log('Selected hobbies: ' + selectedHobbies.slice(0, -1));
    }
});