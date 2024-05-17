window.onload = async ()=>{
    let postId = localStorage.getItem('trendPostId')
    let data = await fetch(`/post/fetch-post/${postId}`)
    let response = await data.json()
    console.log(response)
    let description = response.description
    let group = response.userGroup !== null ? response.userGroup : null
    let community = group !== null ? group.community : null
    let groupId = community !== null ? community.id : '-'
    let user = response.user
    let resources = response.resources !== null ? response.resources : null
    let userPhoto = user.photo === null ? "/static/assets/img/wowIt'sme.jpg" : user.photo
    let userName = user.name
    let department = user.dept === null ? 'no dept' : user.dept
    let email = user.email
    let access = response.access 
    let createdDate = new Date(response.createdDate)
    let startDay = createdDate.getDate();
    let startMonth = createdDate.getMonth() + 1;
    let startYear = createdDate.getFullYear();
    let formattedDate = `${startYear} - ${startMonth} - ${startDay}`
    await insertPhoto(userPhoto)
    await insertNameAndDepartmentAndEmail(userName,department,email)
    await insertAccessAndGroupAndTime(access,groupId,formattedDate)
    await insertDescription(description)
    if(resources !== null){
        await insertResources(resources)
    }
}


const insertPhoto = async ( photoUrl ) => {
    let imgDiv = document.getElementById('userPhotoDiv')
    imgDiv.src = photoUrl 
}

const insertNameAndDepartmentAndEmail = async (userName,department,email) => {
    let nameDiv = document.getElementById('userNameDiv')
    let departmentDiv = document.getElementById('userDepartmentDiv')
    let emailDiv = document.getElementById('userEmailDiv')
    nameDiv.textContent = userName
    departmentDiv.textContent = department
    emailDiv.textContent = email
}

const insertAccessAndGroupAndTime = async (access,group,time) => {
    let accessDiv = document.getElementById('accessDiv')
    let groupDiv = document.getElementById('groupDiv')
    let timeDiv = document.getElementById('timeDiv')
    accessDiv.textContent = access
    groupDiv.textContent = group
    timeDiv.textContent = time
}

const insertDescription = async (description) => {
    let descDiv = document.getElementById('descDiv')
    descDiv.textContent = description
}

const insertResources = async (resources) => {
    for(r of resources){
        let parentDiv = document.getElementById('parentDiv')
        let mainDiv = document.createElement('div')
        let captionDiv = document.createElement('div')
        let caption = document.createElement('p')
        let resourceDiv = document.createElement('div')
        let resource = null
        if(r.photo === null){
            resource = document.createElement('video')
            resource.src = r.video
        }
        if(r.video === null){
            resource = document.createElement('img')
            resource.src = r.photo
        } 
        captionDiv.classList.add('font-monospace')
        captionDiv.classList.add('m-2')
        resource.style.width = '400px'
        resource.style.marginLeft = '200px'
        caption.textContent = r.description
        captionDiv.appendChild(caption) 
        resourceDiv.appendChild(resource)
        mainDiv.appendChild(captionDiv)
        mainDiv.appendChild(resourceDiv)
        parentDiv.appendChild(mainDiv) 
    }
}

const makeSeeMore = async (text,btn) => {
    let max = 20
    let content = text.textContent
    let button = btn
    let words = content.split(' ')
    if(words.length > max){
        let show = words.slice(0,max).join(' ')
        let hide = words.slice(max).join(' ')
        content.innerHTML = `${show}<span id="hiddenSpan" class="hidden"> ${hide}</span>`;
        button.addEventListener('click',async () => {
            let span = document.getElementById('hiddenSpan')
            if(span.classList.contains('hidden')){
                span.classList.remove('hidden')
                button.textContent = 'see less'
            }else{
                span.classList.add('hidden')
                button.textContent = 'see more'
            }
        })
    }else{
        button.style.display = 'none'
    }
}
