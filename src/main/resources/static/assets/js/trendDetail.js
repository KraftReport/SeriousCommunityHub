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
    await insertAccessAndGroupAndTime(access,groupId,formattedDate,postId)
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

const insertAccessAndGroupAndTime = async (access, group, time, postId) => {
    let privateAccessDiv = document.getElementById('privateAccessDiv');
    let publicAccessDiv = document.getElementById('publicAccessDiv');
    let groupDiv = document.getElementById('groupDiv');
    let timeDiv = document.getElementById('timeDiv');
    const reactSize = document.getElementById('reactCount');
    const commentSize = document.getElementById('commentCount');


    const reactCount = await getSizeOfReactForPost(postId);
    const commentCount = await getSizeOfCommentForPost(postId);


    const btn = document.createElement('button');
    btn.classList.add('btn', 'btn-outline-primary');
    btn.setAttribute('data-bs-toggle', 'modal');
    btn.setAttribute('data-bs-target', '#staticBackdropForCommentView');
    btn.textContent = 'Click to see';
    btn.addEventListener('click',async () => {
       await getAllCommentsForSinglePost(postId);
    })

    // Insert content based on access
    if (access === 'PUBLIC') {
        document.getElementById('fa-lock').style.display = 'none';
        document.getElementById('fa-lock-open').style.display = 'block';
        privateAccessDiv.style.display = 'none';
        groupDiv.textContent = '-';
    } else {
        document.getElementById('fa-lock').style.display = 'none';
        document.getElementById('fa-lock-open').style.display = 'block';
        publicAccessDiv.style.display = 'none';
        groupDiv.textContent = group;
    }

    timeDiv.textContent = time;
    reactSize.innerHTML = `${reactCount}`;


    commentSize.innerHTML = '';
    commentSize.textContent = `${commentCount} `;
    commentSize.appendChild(btn);
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

const getSizeOfReactForPost =async (id) => {
    const data = await fetch(`/user/onlyOne-trendyPostReacts-withinOneMonth/${id}`)
    const res = await data.json();
    return res;
}

const getSizeOfCommentForPost = async (id) => {
    const data = await fetch(`/user/onlyOne-trendyPostComments-withinOneMonth/${id}`)
    const res = await data.json();
    return res;
}

//display comment
const getAllCommentsForSinglePost = async (id) => {
    const fetchComments = await fetch(`/getComment/${id}`);
    if (!fetchComments.ok) {
        alert('There is something wrong in the comment section,Please try again!');
    }
    const getData = await fetchComments.json();
    const chatArea = document.querySelector('#commentViewMessage');
    chatArea.innerHTML = '';
    for (const c of getData) {
        let localDateTime = new Date(c.localDateTime);
        await displayMessageForSinglePost(c.user.name, c.content, c.user.photo, c.id, c.post.id, localDateTime, chatArea);
        console.log('data time',localDateTime)
    }
};
// import  { displayMessage } from '/static/assets/js/post.js';

const fetchUserDataByPostedUserForSinglePost = async (id) => {
    const fetchUserData = await fetch(`/get-userData/${id}`);
    if (!fetchUserData.ok) {
        alert('Invalid user');
    }
    const userDataForAll = await fetchUserData.json();
    return userDataForAll;
};
const highlightMentionsForSinglePost = async (description) => {

    const allMembers = await getAllMember();
    const sanitizedMemberNames = allMembers.map(member => member.name.replace(/\s+/g, ''));

    const mentionRegex = /@([a-zA-Z0-9_]+)/g;

    return description.replace(mentionRegex, (match, username) => {
        if (sanitizedMemberNames.includes(username)) {
            return `<span class="mention">${match}</span>`;
        } else {
            return match;
        }
    });
};

async function timeAgoForSinglePost(createdDate) {
    console.log("ddd",createdDate)
    const now = new Date();
    const diff = now - createdDate;
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 60) {
        return `just now`;
    } else if (minutes < 60) {
        return `${minutes} minutes${minutes > 1? '' : ''} ago`;
    } else if (hours < 24) {
        return `${hours} hours${hours > 1? '' : ''} ago`;
    } else {
        return `${days} days${days > 1? '' : ''} ago`;
    }
}

const commentReactTypeForSinglePost = async (id, userId, postId) => {
    const fetchType = await fetch(`/comment-type-react/${id}/${userId}/${postId}`);
    const response = await fetchType.json();
    return response;
}

const displayMessageForSinglePost = async (sender, content, photo, id, postId,localDateTime,chatArea) => {
    const user = await fetchUserDataByPostedUserForSinglePost(loginUser);
    const divItem = document.createElement('div');
    divItem.classList.add(`user-item-${id}`);
    const userImage = document.createElement('img');
    photo = photo || '/static/assets/img/card.jpg';
    userImage.src = `${photo}`;
    userImage.alt = 'User Photo';
    userImage.style.width = '60px';
    userImage.style.height = '60px';
    userImage.classList.add('user-photo');
    userImage.style.border = '2px solid #ccc';
    const spanSender = document.createElement('span');
    if (sender === user.name) {
        spanSender.innerHTML = `You : `;
    } else {
        spanSender.innerHTML = `${sender} : `;
    }
    const formattedContent = await highlightMentionsForSinglePost(content);
    const spanElement = document.createElement('span');
    spanElement.classList.add(`comment-span-container-${id}`);
    spanElement.id = id;
    spanElement.innerHTML = `${formattedContent}`;
    // divItem.style.border = '1px solid lightslategrey';
    divItem.style.padding = '5px';
    divItem.style.borderRadius = '30px'
    divItem.style.width = '450px';

    const convertDiv = document.createElement('div');
    convertDiv.classList.add('content-container');
    let createdTime = await timeAgoForSinglePost(new Date(localDateTime))
    convertDiv.setAttribute('data-toggle', 'tooltip');
    convertDiv.setAttribute('title', `${createdTime}`);
    const spanElementForImg = document.createElement('span');
    spanElementForImg.appendChild(userImage);
    divItem.appendChild(spanElementForImg);
    convertDiv.style.marginLeft = '70px';
    convertDiv.style.marginTop = '-50px';
    convertDiv.style.padding = '25px';
    convertDiv.style.borderBottomRightRadius = '20px';
    convertDiv.style.backgroundColor = 'lightgrey';
    convertDiv.style.borderTopLeftRadius = '10px';
    convertDiv.style.borderBottomLeftRadius = '35px';
    divItem.appendChild(convertDiv);
    const ddEl = document.createElement('div');
    ddEl.classList.add(`comment-container-${id}`);
    ddEl.appendChild(spanSender);
    ddEl.appendChild(spanElement);
    convertDiv.appendChild(ddEl);
    chatArea.appendChild(divItem);
    let replyInput = divItem.querySelector('.reply-input');
    let submitButton = divItem.querySelector('.submit-reply-btn');
    let cancelButton = divItem.querySelector('.cancel-reply-btn');
    const replies = await fetchAndDisplayRepliesForSinglePost(id);
    const repliesContainer = document.createElement('div');
    repliesContainer.classList.add(`replies-container-${id}`);
    repliesContainer.style.display = 'block';
    for (const reply of replies) {
        repliesContainer.appendChild(reply);
    }
    const rpDiv = document.createElement('div');
    rpDiv.appendChild(repliesContainer);
    divItem.appendChild(rpDiv);
};


const fetchAndDisplayRepliesForSinglePost = async (id) => {
    const fetchReplies = await fetch(`/getAll-comment/${id}`);
    const fetchDataForReplies = await fetchReplies.json();

    const replies = [];

    for (const reply of fetchDataForReplies) {
        const user = await fetchUserDataByPostedUserForSinglePost(loginUser);
        const replyElement = document.createElement('div');

        const userRpImage = document.createElement('img');
        const photo = reply.user.photo || '/static/assets/img/card.jpg';
        userRpImage.src = `${photo}`;
        userRpImage.alt = 'User Photo';
        userRpImage.classList.add('user-photo');
        userRpImage.style.height = '50px';
        userRpImage.style.width = '50px';
        userRpImage.style.marginLeft = '50px';
        userRpImage.style.backgroundColor = '#cccccc';
        replyElement.classList.add(`reply-item-${reply.id}`);
        let createdTimeForReply = await timeAgoForSinglePost(new Date(reply.localDateTime))
        replyElement.setAttribute('data-toggle', 'tooltip');
        replyElement.setAttribute('title', `${createdTimeForReply}`);
        const replySender = document.createElement('span');
        if (reply.user.name === user.name) {
            replySender.innerHTML = `You : `;
        } else {
            replySender.innerHTML = `${reply.user.name} : `;
        }
        const replyContent = document.createElement('span');
        replyContent.classList.add(`span-reply-container-${reply.id}`)
        replyContent.id = reply.id;
        const formattedContent = await highlightMentionsForSinglePost(reply.content);
        replyContent.innerHTML =`${formattedContent}`;
        const spElement = document.createElement('span');
        const contentElement = document.createElement('span');
        const divEl = document.createElement('div');
        divEl.classList.add(`reply-container-div-${reply.id}`);
        divEl.style.marginLeft = '110px';
        divEl.style.padding = '20px';
        divEl.style.backgroundColor = 'lightgrey';
        divEl.style.marginTop = '-30px';
        divEl.style.borderTopLeftRadius = '10px';
        divEl.style.borderBottomLeftRadius = '30px';
        divEl.style.borderBottomRightRadius = '15px';
        spElement.appendChild(userRpImage);
        replyElement.appendChild(spElement);
        divEl.appendChild(replySender);
        divEl.appendChild(replyContent);
        contentElement.appendChild(divEl);
        replyElement.appendChild(contentElement);
        replies.push(replyElement);
    }
    return replies;
};
