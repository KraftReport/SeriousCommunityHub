// Insert terms and conditions paragraph into the container
document.getElementById("termsContainer").innerHTML = "<p>Vision\n" +
    "\n" +
    "DIR-ACE Technology aims to deliver top-tier IT solutions," +
    " positioning Myanmar’s ICT industry at the forefront of " +
    "technological innovation. With a team of highly skilled and " +
    "experienced engineers, we are committed to developing and " +
    "delivering cutting-edge IT systems and services that exceed standards." +
    " By creating rewarding career opportunities and driving growth and" +
    " innovation in the country’s IT sector, we strive to operate and e" +
    "stablish one of the most mission critical system of Stock Exchange " +
    "and one of leading provider of exceptional IT solutions in the region " +
    "and beyond.\n" +
    "Mission\n" +
    "\n" +
    "DIR-ACE Technology is to empower Myanmar’s IT industry " +
    "by developing highly skilled and innovative IT engineers, " +
    "delivering world-class IT systems and services, and contributing " +
    "to the country’s socio-economic development through software " +
    "and service exports. We are committed to providing our clien" +
    "ts with the most reliable and cost-effective solutions while" +
    " continually innovating and adapting to the latest technological " +
    "advancements, all with integrity, professionalism, and a commitment " +
    "to excellence.</p>";

// Enable the finished button when the checkbox is checked
document.getElementById("acceptTerms").addEventListener("change", function() {
    if(this.checked) {
        document.getElementById("finishedButton").disabled = false;
    } else {
        document.getElementById("finishedButton").disabled = true;
    }
});
const finishedButton = document.getElementById('finishedButton');

// Add an event listener to the finished button
finishedButton.addEventListener('click', function() {
    // Retrieve the data from local storage
    const savedPassword = localStorage.getItem('savedPassword');
    const rawImage = localStorage.getItem('rawImage');
    const selectedHobbies = localStorage.getItem('Hobbies');
    const phoneNumber = localStorage.getItem('phoneNumber');
    const dob = localStorage.getItem('dob');
    const gender = localStorage.getItem('gender');
    const skills=localStorage.getItem('selectedSkills');
    const experience=localStorage.getItem('Experience');

    // Send the data to the server
    fetch('/user/savePassword', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ password: savedPassword , phoneNumber, dob, gender ,hobbies: selectedHobbies })
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
    localStorage.clear();
});