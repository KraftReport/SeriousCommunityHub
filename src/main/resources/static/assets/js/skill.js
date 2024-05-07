document.addEventListener('DOMContentLoaded', function() {
    fetch("/user/getSkills")
        .then(response => response.json())
        .then(data => {
            const skillsDropdown = document.getElementById('skillsDropdown');
            data.forEach(skill => {
                const checkbox = document.createElement('input');
                console.log('skdfdsf', skill.name)
                checkbox.type = 'checkbox';
                checkbox.name = 'skills';
                checkbox.value = skill.id;
                checkbox.addEventListener('change', function() {
                    saveSkillsToLocalStorage();
                });
                const label = document.createElement('label');
                label.htmlFor = 'skill_' + skill.id;
                label.appendChild(document.createTextNode(skill.name));
                const div = document.createElement('div');
                div.classList.add('form-check');
                div.appendChild(checkbox);
                div.appendChild(label);
                skillsDropdown.appendChild(div);
            });
        });



    document.getElementById('addNewSkillBtn').addEventListener('click', function() {
        const newSkillInput = document.getElementById('newSkillInput');
        newSkillInput.style.display = (newSkillInput.style.display === 'none')? 'block' : 'none';
    });
    document.querySelector('#skillChangeForm button.next').addEventListener("click", function(event) {
        const newSkillName = document.getElementById('newSkillNameInput').value;
        const newSkillExperience = document.getElementById('newSkillExperienceInput').value;
        console.log(newSkillName);
        console.log(newSkillExperience);
        if (newSkillName && newSkillExperience) {
            const newSkill = {
                id: Date.now(),
                name: newSkillName,
                experience: newSkillExperience
            };
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.name = 'skills';
            checkbox.value = newSkill.id;
            checkbox.addEventListener('change', function() {
                saveSkillsToLocalStorage();
            });
            const label = document.createElement('label');
            label.htmlFor = 'skill_' + newSkill.id;
            label.appendChild(document.createTextNode(newSkill.name));
            const div = document.createElement('div');
            div.classList.add('form-check');
            div.appendChild(checkbox);
            div.appendChild(label);
            const skillsDropdown = document.getElementById('skillsDropdown');
            skillsDropdown.appendChild(div);
            saveSkillsToLocalStorage();
        }
    });

    function saveSkillsToLocalStorage() {
        const selectedSkills = Array.from(document.querySelectorAll('#skillsDropdown input[type="checkbox"]:checked'))
            .map(checkbox => checkbox.parentElement.querySelector('label').innerText);
        const newSkillName = document.getElementById('newSkillNameInput').value;
        const newSkillExperience = document.getElementById('newSkillExperienceInput').value;
        if (newSkillName && newSkillExperience) {
            selectedSkills.push(newSkillName);
            localStorage.setItem('Experience', newSkillExperience);
        }
        localStorage.setItem('selectedSkills', selectedSkills.join(','));
    }

});