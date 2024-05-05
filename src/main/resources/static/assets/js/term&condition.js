// Fetch policy content from the server
fetch('/user/getAllPolicies')
    .then(response => response.json())
    .then(data => {
        // Assuming data is an array of policy objects
        data.forEach(policy => {
            const policyContainer = document.createElement('div');

            // Create a paragraph element for the policy content
            const policyElement = document.createElement('p');
            policyElement.innerHTML = policy.rule;
            policyContainer.appendChild(policyElement);

            // Create a span element for the user name
            const userSpan = document.createElement('span');
            userSpan.innerHTML = ` written by ${policy.user.name}(ADMIN)`;
            userSpan.style.fontSize = 'smaller'; // Make user name smaller
            userSpan.style.color = 'blue'; // Make user name blue
            policyContainer.appendChild(userSpan);


            // Add a gray line between each policy
            policyContainer.style.borderBottom = '1px solid #ddd';

            // Append the container to the termsContainer
            document.getElementById("termsContainer").appendChild(policyContainer);
        });
    })
    .catch(error => console.error('Error fetching policies:', error));

// Enable the finished button when the checkbox is checked
document.getElementById("acceptTerms").addEventListener("change", function () {
    if (this.checked) {
        document.getElementById("finishedButton").disabled = false;
    } else {
        document.getElementById("finishedButton").disabled = true;
    }
});

const finishedButton = document.getElementById('finishedButton');

// Add an event listener to the finished button
finishedButton.addEventListener('click', function () {
    // Retrieve the data from local storage
    const savedPassword = localStorage.getItem('savedPassword');
    const rawImage = localStorage.getItem('rawImage');
    const selectedHobbies = localStorage.getItem('Hobbies');
    const phoneNumber = localStorage.getItem('phoneNumber');
    const dob = localStorage.getItem('dob');
    const gender = localStorage.getItem('gender');
    const skills = localStorage.getItem('selectedSkills');
    const experience = localStorage.getItem('Experience');

    // Send the data to the server
    fetch('/user/savePassword', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({password: savedPassword, phoneNumber, dob, gender, hobbies: selectedHobbies})
    })
        .then(response => response.json())
        .then(data => {
            console.log('Password saved:', data);
        })
        .catch(error => {
            console.error('Error saving password:', error);
        });
    fetch('/user/saveSkill', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({selectedSkills: skills, experience})
    })
        .then(response => response.json())
        .then(data => {
            console.log('Skills saved:', data);
        })
        .catch(error => {
            console.error('Error saving skills:', error);
        });
    fetch('/user/saveImage', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/octet-stream'
        },
        body: rawImage
    })
        .then(response => response.json())
        .then(data => {
            console.log('Image saved:', data);
        })
        .catch(error => {
            console.error('Error saving image:', error);
        });
    // Clear the local storage
    localStorage.removeItem('savedPassword')
    localStorage.removeItem('phoneNumber')
    localStorage.removeItem('rawImage')
    localStorage.removeItem('Hobbies')
    localStorage.removeItem('dob')
    localStorage.removeItem('gender')
    localStorage.removeItem('selectedSkills')
    localStorage.removeItem('Experience')
});