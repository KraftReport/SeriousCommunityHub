let selectedHobbies = '';

$('#hobbyChangeForm input[type="checkbox"]').on('change', function() {
    if ($(this).is(':checked')) {
        selectedHobbies += $(this).parent().text().trim() + ',';
        if (selectedHobbies!== '') {
            $('#hobbyChangeForm button').text('Next');
        }
    } else {
        selectedHobbies = selectedHobbies.replace($(this).parent().text().trim() + ',', '');
        if (selectedHobbies === '') {
            $('#hobbyChangeForm button').text('Skip');
        }
    }
});

$('#hobbyChangeForm button').on('click', function() {
    if (selectedHobbies!== '') {
        // Store the selected hobbies as a string in the database
        localStorage.setItem('Hobbies', selectedHobbies.slice(0, -1));
        console.log('Selected hobbies: ' + selectedHobbies.slice(0, -1));
    }
});