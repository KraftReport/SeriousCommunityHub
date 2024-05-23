window.onload = async ()=>{
    let postId = localStorage.getItem('trendPostId')
    let data = await fetch(`/post/fetch-post/${postId}`)
    localStorage.removeItem('trendPostId')
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
    let privateAccessDiv = document.getElementById('privateAccessDiv')
    let publicAccessDiv = document.getElementById('publicAccessDiv')
    let groupDiv = document.getElementById('groupDiv')
    let timeDiv = document.getElementById('timeDiv')
    if(access === 'PUBLIC'){
        document.getElementById('fa-lock').style.display = 'none'
        document.getElementById('fa-lock-open').style.display = 'block'
        privateAccessDiv.style.display = 'none' 
        groupDiv.textContent = '-'
        timeDiv.textContent = time
    }else{
        document.getElementById('fa-lock').style.display = 'none'
        document.getElementById('fa-lock-open').style.display = 'block'
        publicAccessDiv.style.display = 'none' 
        groupDiv.textContent = group
        timeDiv.textContent = time
    }
}

const insertDescription = async (description) => {
    let descDiv = document.getElementById('descDiv')
    descDiv.textContent = description
}

const insertResources = async (resources) => {
    const parentDivForRaw = document.createElement('div');
    parentDivForRaw.classList.add('card','shadow');
    parentDivForRaw.style.marginLeft = '70px'
    parentDivForRaw.style.width = '300px';
    const ul = document.createElement('ul');
    ul.classList.add('list-group', 'list-group-flush');
    for(r of resources){
        if(r.raw===null){
            document.getElementById('parentDivForRaw').classList.add('hidden')
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
        }else {
            document.getElementById('parentDiv').classList.add('hidden') 
            console.log('d ko youk tl naw')
         
                let name = r.description
                console.log('loop pat nay b')
                const li = document.createElement('li');
                li.classList.add('list-group-item','d-flex');
                li.style.maxWidth = '400px'
                li.style.justifyContent = 'space-between' 
        
                const mDiv = document.createElement('div')
                mDiv.textContent = r.description
                mDiv.classList.add('font-monospace')
                
        
                const downloadIcon = document.createElement('i')
                downloadIcon.classList.add('fa-solid','fa-down-long','text-primary')
         
        
                const a = document.createElement('a');
                a.href = r.raw;
                a.classList.add('font-monospace')  
                a.onclick = (event) => downloadFile(event,r.raw,r.description)
                console.log(name) 
        
                
                a.appendChild(downloadIcon)
                li.appendChild(mDiv)
                li.appendChild(a)
                ul.appendChild(li)
        }
        parentDivForRaw.appendChild(ul)
        document.getElementById('parentDivForRaw').appendChild(parentDivForRaw)
    }
}

// const downloadFile = async (event, url, fileName) => {
//     event.preventDefault();
//     try {
//         const response = await fetch(url, {
//             redirect: 'follow' // Follow redirects
//         });
//         if (!response.ok) {
//             throw new Error('Network response was not ok in fetching download file');
//         }
//         const blob = await response.blob();
//         console.log(fileName+' ------> this is filename')
//         const blobUrl = window.URL.createObjectURL(blob);
//         const a = document.createElement('a');
//         a.style.display = 'none';
//         a.href = blobUrl;
//         a.download = fileName; // Ensure the file name is set correctly
//         document.body.appendChild(a);
//         a.click();
//         window.URL.revokeObjectURL(blobUrl);
//         document.body.removeChild(a);
//     } catch (error) {
//         alert('Failed to download the file');
//         console.error('There is a problem downloading the file:', error);
//     }
// };

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
