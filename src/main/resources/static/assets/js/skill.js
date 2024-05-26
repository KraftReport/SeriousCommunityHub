document.addEventListener('DOMContentLoaded', () => {

    async function addSkillToDropdown(id, name, experience) {
        const skillsDropdown = document.getElementById('skillsDropdown');
        skillsDropdown.style.display = 'flex';
        skillsDropdown.classList.add(`whole-div-${id}`);
        skillsDropdown.style.flexWrap = 'wrap';

        const skillItem = document.createElement('div');
        skillItem.style.display = 'flex';
        skillItem.id = `skill-div-${id}`;
        skillItem.style.alignItems = 'right';
        skillItem.style.marginRight = '25px';
        skillItem.style.marginBottom = '15px';
        skillItem.setAttribute('data-toggle', 'tooltip');
        skillItem.setAttribute('title', `${experience} years`);

        const p = document.createElement('p');
        p.textContent = name;
        p.id = `p-tag-${id}`;
        p.style.margin = '0';
        p.style.marginRight = '10px';
        p.dataset.experience = experience;
        p.dataset.id = id;

        const updateBtn = document.createElement('i');
        updateBtn.classList.add('fa-solid', 'fa-pen-to-square');
        updateBtn.style.color = 'blue';
        updateBtn.id = `update-div-${id}`;
        updateBtn.style.cursor = 'pointer';
        updateBtn.style.marginRight = '10px';
        updateBtn.addEventListener('click', async () => {
            await showUpdateForm(id, name, experience);
        });

        const deleteBtn = document.createElement('i');
        deleteBtn.classList.add('fa-solid', 'fa-trash-can');
        deleteBtn.style.color = 'red';
        deleteBtn.id = `delete-div-${id}`;
        deleteBtn.style.cursor = 'pointer';
        deleteBtn.addEventListener('click', async () => {
            const skillId = p.dataset.id;
            skillItem.remove();
            await deleteSkill(skillId);
        });

        skillItem.appendChild(p);
        skillItem.appendChild(updateBtn);
        skillItem.appendChild(deleteBtn);
        skillsDropdown.appendChild(skillItem);
    }

    async function deleteSkill(id) {
        const fetchData = await fetch(`/user/deleteSkill/${id}`, { method: 'GET' });
        if (!fetchData.ok) {
            console.log("Error occurred!");
        }
    }

    async function updateSkill(id, name, experience) {
        const response = await fetch(`/user/updateSkill/${id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, experience })
        });
        if (!response.ok) {
            console.log("Error occurred!");
        } else {
            const res = await response.json();
            const skillDiv = document.getElementById(`skill-div-${id}`);
            const pElement = document.getElementById(`p-tag-${id}`);
            if (pElement) {
                pElement.textContent = res.name;
                pElement.dataset.experience = res.experience;
                skillDiv.setAttribute('title', `${res.experience} years`);
            }

            console.log("Update successful!");
        }
    }

    async function showUpdateForm(id, name, experience) {
        newSkillInput.style.display = 'block';
        addNewSkillBtn.style.display = 'none';

        const nameInput = document.getElementById('newSkillNameInput');
        const experienceInput = document.getElementById('newSkillExperienceInput');

        nameInput.value = name;
        experienceInput.value = experience;

        nameInput.readOnly = true;

        const saveSkillBtn = document.getElementById('saveSkillBtn');
        if (saveSkillBtn) {
            saveSkillBtn.remove();
        }

        const buttonsContainer = document.createElement('div');
        buttonsContainer.style.marginTop = '10px';
        buttonsContainer.style.display = 'flex';

        const cancelBtn = document.createElement('button');
        cancelBtn.type = 'button';
        cancelBtn.classList.add('btn', 'btn-primary', 'ml-2');
        cancelBtn.innerHTML = 'Cancel';
        cancelBtn.addEventListener('click', () => {
            newSkillInput.style.display = 'none';
            addNewSkillBtn.style.display = 'block';
            nameInput.value = '';
            experienceInput.value = '';
            nameInput.readOnly = false;
            cancelBtn.remove();
            updateBtn.remove();
        });

        const updateBtn = document.createElement('button');
        updateBtn.type = 'button';
        updateBtn.classList.add('btn', 'btn-primary');
        updateBtn.innerHTML = 'Edit';
        updateBtn.addEventListener('click', async () => {
            const newName = nameInput.value;
            const newExperience = experienceInput.value;
            await updateSkill(id, newName, newExperience);
            newSkillInput.style.display = 'none';
            addNewSkillBtn.style.display = 'block';
            nameInput.value = '';
            experienceInput.value = '';
            nameInput.readOnly = false;
            updateBtn.remove();
            cancelBtn.remove();
        });

        buttonsContainer.appendChild(cancelBtn);
        buttonsContainer.appendChild(updateBtn);
        addBtnContainer.appendChild(buttonsContainer);
    }
    function saveSkillsToLocalStorage() {
        const newSkillName = document.getElementById('newSkillNameInput').value;
        const newSkillExperience = document.getElementById('newSkillExperienceInput').value;

        if (newSkillName && newSkillExperience) {
            localStorage.setItem('Experience', newSkillExperience);
            localStorage.setItem('selectedSkills', newSkillName);
        }
    }

    function displaySkillInput(show) {
        if (show) {
            const addBtn = document.createElement('button');
            addBtn.type = 'button';
            addBtn.id = 'saveSkillBtn';
            addBtn.classList.add('btn', 'btn-primary');
            addBtn.style.marginTop = '10px';
            addBtn.innerHTML = 'Add';
            addBtn.addEventListener('click', async () => {
                const skillsInput = document.getElementById('newSkillNameInput');
                const experienceInput = document.getElementById('newSkillExperienceInput');
                const skills = skillsInput.value.trim();
                const experience = experienceInput.value.trim();

                if (!skills || !experience) {
                    const error = document.createElement('span');
                    error.id = 'errorSpan';
                    error.style.color = 'red';
                    error.textContent = 'Please enter both skill name and experience.';
                    newSkillInput.appendChild(error);
                    return;
                }

                saveSkillsToLocalStorage();
                await svgSkill(skills, experience);
            });

            addBtnContainer.appendChild(addBtn);
        } else {
            const errorSpan = document.getElementById('errorSpan');
            if (errorSpan) {
                errorSpan.remove();
            }

            const addBtn = document.getElementById('saveSkillBtn');
            if (addBtn) {
                addBtn.remove();
            }
        }
        newSkillInput.style.display = show ? 'block' : 'none';
    }

    const skillFunction = async () => {
        const data = await fetch(`/user/getSkills`);
        if (!data.ok) {
            alert('Something went wrong, please try again');
            return;
        }
        const res = await data.json();
        console.log('Skills loaded', res);
        const skillsDropdown = document.getElementById('skillsDropdown');
        for (const skill of res) {
            console.log(skill.experience);
            await addSkillToDropdown(skill.id, skill.name, skill.experience);
        }
    };

    skillFunction().then();

    const addNewSkillBtn = document.getElementById('addNewSkillBtn');
    const newSkillInput = document.getElementById('newSkillInput');
    const addBtnContainer = document.getElementById('forSkillAddButton');

    addNewSkillBtn.addEventListener('click', () => {
        if (addNewSkillBtn.innerHTML === 'Add New Skill') {
            addNewSkillBtn.innerHTML = 'Cancel';
            displaySkillInput(true);
        } else {
            addNewSkillBtn.innerHTML = 'Add New Skill';
            displaySkillInput(false);
        }
    });

    const svgSkill = async (skills, experience) => {
        const skillDto = { name: skills, experience: experience };
        const data = await fetch(`/user/saveSkillToDb`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(skillDto)
        });
        if (!data.ok) {
            alert('Something went wrong, please try again!');
            return;
        }
        const res = await data.json();
        await addSkillToDropdown(res.id, res.name, res.experience);
        localStorage.removeItem('selectedSkills');
        localStorage.removeItem('Experience');
        document.getElementById('newSkillNameInput').value = '';
        document.getElementById('newSkillExperienceInput').value = '';
        addNewSkillBtn.click();
        console.log('Skill saved successfully');
    };

    document.querySelector('#skillChangeForm button.next').addEventListener("click", function (event) {
        const newSkillName = document.getElementById('newSkillNameInput').value;
        const newSkillExperience = document.getElementById('newSkillExperienceInput').value;

        if (newSkillName && newSkillExperience) {
            const newSkill = { id: Date.now(), name: newSkillName, experience: newSkillExperience };

            const p = document.createElement('p');
            p.textContent = newSkill.name;
            p.classList.add('form-check');
            const skillsDropdown = document.getElementById('skillsDropdown');
            skillsDropdown.appendChild(p);

            saveSkillsToLocalStorage();
        }
    });

});
