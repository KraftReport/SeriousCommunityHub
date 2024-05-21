document.addEventListener('DOMContentLoaded', function () {
    const addNewSkillBtn = document.getElementById('addNewSkillBtn');
    const newSkillInput = document.getElementById('newSkillInput');
    const addbtn = document.getElementById('addbtn');
    const skillsDiv = document.getElementById('skills');
    const nextBtn = document.getElementById('nextBtn');
    const newSkillNameInput = document.getElementById('newSkillNameInput');
    const suggestionsSkills = document.getElementById('suggestionsSkills');
    let availableSkills = [];

    // Fetch skills from the server
    fetch('/user/getAllSkills')
        .then(response => response.json())
        .then(data => {
            availableSkills = data;
        })
        .catch(error => console.error('Error fetching skills:', error));

    addNewSkillBtn.addEventListener('click', function () {
        newSkillInput.style.display = 'block';
    });

    addbtn.addEventListener('click', function () {
        const skillName = document.getElementById('newSkillNameInput').value;
        const experience = document.getElementById('newSkillExperienceInput').value;

        if (skillName && experience) {
            const skillRow = document.createElement('div');
            skillRow.classList.add('skill-row', 'd-flex', 'justify-content-between', 'align-items-center', 'mb-2');

            const skillText = document.createElement('span');
            skillText.textContent = `${skillName} ${experience} year(s)`;

            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.classList.add('btn', 'btn-danger', 'btn-sm', 'ml-2');
            removeBtn.innerHTML = '<i class="fas fa-trash-alt"></i>'; // Trash icon from Font Awesome

            removeBtn.addEventListener('click', function () {
                skillsDiv.removeChild(skillRow);
            });

            skillRow.appendChild(skillText);
            skillRow.appendChild(removeBtn);
            skillsDiv.appendChild(skillRow);

            // Clear input fields
            document.getElementById('newSkillNameInput').value = '';
            document.getElementById('newSkillExperienceInput').value = '';

            // Hide new skill input
            newSkillInput.style.display = 'none';
        } else {
            alert('Please enter both skill name and experience.');
        }
    });

    nextBtn.addEventListener('click', function () {
        const skills = [];
        const experiences = [];
        console.log("nextBtn clicked")
        skillsDiv.querySelectorAll('.skill-row').forEach(skillRow => {
            const skillText = skillRow.querySelector('span').textContent;
            const [skillName, experienceText] = skillText.split(' ');
            const experience = experienceText.split(' ')[0];

            skills.push(skillName);
            experiences.push(experience);
        });
        localStorage.setItem('Skills', skills.join(','));
        localStorage.setItem('Experience', experiences.join(','));
    });

    newSkillNameInput.addEventListener('focus', function () {
        const inputValue = newSkillNameInput.value.toLowerCase();
        const filteredSkills = availableSkills.filter(skill => skill.toLowerCase().includes(inputValue));
        console.log(availableSkills);
        displaySuggestions(filteredSkills);
    });

    function displaySuggestions(skills) {
        suggestionsSkills.innerHTML = ''; // Clear previous suggestions

        // Create a container div for the skill boxes
        const skillBoxContainer = document.createElement('div');
        skillBoxContainer.classList.add('skill-box-container');

        skills.forEach(skill => {
            // Create a box for each skill
            const skillBox = document.createElement('div');
            skillBox.classList.add('skill-box');
            skillBox.textContent = skill;

            // Add click event listener to select the skill
            skillBox.addEventListener('click', function () {
                newSkillNameInput.value = skill;
                suggestionsSkills.innerHTML = ''; // Clear suggestions after selecting
            });
            addbtn.addEventListener('click', function () {
                suggestionsSkills.innerHTML = '';
            });
            // Append the skill box to the container
            skillBoxContainer.appendChild(skillBox);
        });

        // Append the container to the suggestions div
        suggestionsSkills.appendChild(skillBoxContainer);
    }

});