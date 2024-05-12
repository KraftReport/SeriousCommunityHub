// Fetch policy content from the server
fetch('/user/getAllPolicies')
    .then(response => response.json())
    .then(data => {
        // Assuming data is an array of policy objects
        data.forEach(policy => {
            const policyContainer = document.createElement('div');
            const policyElement = document.createElement('p');
            policyElement.innerHTML = policy.rule;
            policyContainer.appendChild(policyElement);
            const userSpan = document.createElement('span');
            userSpan.innerHTML = ` written by ${policy.user.name}(ADMIN)`;
            userSpan.style.fontSize = 'smaller';
            userSpan.style.color = 'blue';
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

    const savedPassword = localStorage.getItem('savedPassword');
    const rawImage = localStorage.getItem('rawImage');
    const selectedHobbies = localStorage.getItem('Hobbies');
    const phoneNumber = localStorage.getItem('phoneNumber');
    const dob = localStorage.getItem('dob');
    const gender = localStorage.getItem('gender');
    const skills = localStorage.getItem('selectedSkills');
    const experience = localStorage.getItem('Experience');



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
            saveOtherDataAndClearLocalStorage();
            clearLocalStorage();
        })
        .catch(error => {
            console.error('Error saving image:', error);
        });

    function saveOtherDataAndClearLocalStorage() {
        // Send requests to save password and skills
        Promise.all([
            fetch('/user/savePassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({password: savedPassword, phoneNumber, dob, gender, hobbies: selectedHobbies})
            }),
            fetch('/user/saveSkill', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({selectedSkills: skills, experience})
            })
        ])
            .then(responses => Promise.all(responses.map(response => response.json())))
            .then(data => {
                console.log('Password saved:', data[0]);
                console.log('Skills saved:', data[1]);


            })
            .catch(error => {
                console.error('Error saving password or skills:', error);
            });
    }
    const finishedModal = new bootstrap.Modal(document.getElementById("finishedModal"), {
        backdrop: 'static'
    });
    finishedModal.show();
    function clearLocalStorage() {
        console.log("it reach here.")
        localStorage.removeItem('savedPassword');
        localStorage.removeItem('phoneNumber');
        localStorage.removeItem('rawImage');
        localStorage.removeItem('Hobbies');
        localStorage.removeItem('dob');
        localStorage.removeItem('gender');
        localStorage.removeItem('selectedSkills');
        localStorage.removeItem('Experience');
    }
});