window.onload =  createPostsForSearch

async function searchMethod(){
    let input = await document.getElementById('searchInput').value
    console.log()
    localStorage.setItem('searchInput',encodeURIComponent(input))
    document.getElementById('searchInput').value = localStorage.getItem('searchInput')
    await createPostsForSearch()
    goToEventTab()
    goToPollTab()
    goToUserTab()
    goToCommunityTab()
}

async function goToCommunityTab(){
    let data = await fetch('/api/community/communitySearchMethod/'+localStorage.getItem('searchInput'))
    let response = await data.json()
    let max = 10
    let communityTab = document.getElementById('pills-community-search')
    console.log(response)
    let row = '' 
    if(response.length<1){
        document.getElementById('community-grid').innerHTML =  `
        
 
        <div class="card-body font-monospace fs-4" style="padding:30px; margin-left:100px;" >
        <i class="fa-solid fa-question text-danger "></i> No result here <i class="fa-solid fa-magnifying-glass text-secondary"></i>
        </div>
   
        
        `
        return
    }
        for (let r of response){ 
            let words = r.description;
            if (words.split(' ').length > max) {
                words = words.split(' ').slice(0, max).join(' ') + '....'; // Corrected from maxWords to max
            }
        let num = await getNumberOfMembers(r.id)
        let amount = parseInt(num) > 1 ? 'members' : 'member' 
        let photo = r.image === null ?  '/static/assets/img/profile-img.jpg'   : r.image
        let rows = ''
        rows+= `
        
        <div class="card col-3 m-2 " style="width: 18rem;" onclick="goToCommunityDetail(${r.id})">
  <div class="card-body font-monospace">
    <div class="d-flex">
    <img src="${photo}"  style="width:80px;  height:80px; border-radius:10px; margin-right:30px; margin-bottom:20px; margin-top:15px;">
    <h5 class="card-title font-monospace">${r.name}</h5>
    </div>
    <div class="d-flex">
    <div class="d-block" style="margin-right:50px;"><code><i class="fa-solid fa-crown"></i> Owner</code><p class="text-primary font-monospace" style="width:110px;">${r.ownerName}</p>
    </div>
    <p  class="text-success font-monospace "><i class="fa-solid fa-users"></i>  ${num} ${amount}</p>
    </div>
    
    <h6 class="card-subtitle font-monospace mb-2 text-muted"><i class="fa-solid fa-circle-info"></i> ${words}</h6>
    <p class="card-text"></p>
  </div>
</div>

 

        `

        row+= rows
        }
    document.getElementById('community-grid').innerHTML = row
    
}

async function goToCommunityDetail(id){
    localStorage.setItem('communityIdForDetailPage',id)
    window.location.href = 'api/community/goToCommunityDetail'
}

async function getNumberOfMembers(id){
    let data = await fetch(`/api/community/getNumberOfMembers/${id}`)
    let response = await data.json()
    console.log(response)
    console.log(response[0])
    return response[0]
}

async function goToUserTab(){
    let data = await fetch ('/user/userSearchMethod/'+localStorage.getItem('searchInput'))
    let userTab = document.getElementById('pills-user-search')
    let response = await data.json()
    console.log(response)
    let row = ''
    if(response.length<1){
        userTab.innerHTML  =  `
         
        <div class="card-body font-monospace fs-4" style="padding:30px; margin-left:100px;" >
        <i class="fa-solid fa-question text-danger "></i> No result here <i class="fa-solid fa-magnifying-glass text-secondary"></i>
        </div>
   
        
        `
        return
    }
    response.forEach(r=>{
        let photo = r.photo === null ? '/static/assets/img/profile-img.jpg' : r.photo
        let rows = ''
        rows+=`
      

        <div class="card pt-3 font-monospace  rounded-3" style="width:370px;">
        <div class="card-body d-flex">
        <img style="width:70px" class="rounded-5 m-2 "src="${photo}">
        <div class="d-block ">
        <p class=""> <i class="fa-solid fa-user"></i> ${r.name} </p>
        <code><i class="fa-solid fa-envelope"></i> ${r.email}</code>
        <p class="text-success"><i class="fa-solid fa-building"></i> ${r.dept}</p>
        </div> 
        </div>
      </div>
        
  
  
        `
        row += rows
    })
    userTab.innerHTML = row
}

async function goToEventTab(){
    console.log(localStorage.getItem('searchInput'))
    showSearchEvents(localStorage.getItem('searchInput'))
}

const fetchSizes = async (id) => {
    const reactSize = await fetch(`/like-size/${id}`);
    const reactCount = await reactSize.json();
    return reactCount;
};


const fetchReactType = async (id) => {
    const reactType = await fetch(`/like-type/${id}`);
    const reactTypeData = await reactType.json();
    return reactTypeData;
};

const removeReactionForEvent = async (id) => {
    const cancelType = await fetch(`/remove-like-eventReact-type/${id}`);
    if (!cancelType.ok) {
        alert('something wrong');
    }
    console.log('hehhe')
};

const fetchCommentSizes = async (id) => {
    const commentSize = await fetch(`/comment-size/${id}`);
    const commentCount = await commentSize.json();
    return commentCount;
};

async function timeAgo(createdDate) {
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
document.addEventListener('DOMContentLoaded', () => {
    loginUser = localStorage.getItem('staff_id');
    connect();
    notifyMessage().then();

});

let loginUser = null;
let stompClient = null;
let notificationCount = 0;
const connect = () => {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        stompClient.subscribe(`/user/${loginUser}/like-private-message`, receivedMessageForReact);
        stompClient.subscribe(`/user/all/comment-private-message`, receivedMessageForComment);
        stompClient.subscribe(`/user/${loginUser}/comment-private-message`, receivedMessageForComment);
        stompClient.subscribe(`/user/all/comment-reply-private-message`, receivedMessageForCommentReply);
    });
};


document.getElementById('notiCountDecrease').addEventListener('click', () => {
    notificationCount = 0;
    showNotiCount().then();
});

const showNotiCount = async () => {
    const notiShow = document.getElementById('notiCount');
    notiShow.innerText = notificationCount;
    if (notificationCount === 0) {
        notiShow.innerText = "";
    }
};

const notifyMessage = async () => {
    const showMessage = document.getElementById('notifyMessage');
    const pElement = document.createElement('p');
    pElement.classList.add('noti-p-element');
    if (notificationCount === 0) {
        pElement.style.display = 'block';
        pElement.textContent = 'There is no notification yet!';
    } else {
        pElement.style.display = 'none';
        pElement.textContent = '';
    }
    showMessage.appendChild(pElement);
};

const notifyMessageForReact = async (message, sender, photo, type) => {
    const spanElement = document.getElementById('notiId');
    spanElement.innerText = `You have ${notificationCount} new notifications`;
    const showMessage = document.getElementById('notifyMessage');
    document.querySelector('.noti-p-element').style.display = 'none';
    showMessage.style.whiteSpace = 'nowrap';
    const containerDiv = document.createElement('div');
    const pElement = document.createElement('p');
    const imgElement = document.createElement('img');
    const imgReactElement = document.createElement('img');
    photo = photo || '/static/assets/img/card.jpg';
    imgElement.src = `${photo}`;
    imgElement.width = 40;
    imgElement.height = 40;
    imgElement.style.borderRadius = '50%';
    pElement.textContent = sender + ' ' + message;
    pElement.style.fontWeight = 'bold';
    if (type) {
        imgReactElement.src = `/static/assets/img/${type.toLowerCase()}.png`;
        imgReactElement.width = 20;
        imgReactElement.height = 20;
    }

    containerDiv.style.display = 'flex';
    containerDiv.style.alignItems = 'center';
    // containerDiv.style.border = '1px solid';
    containerDiv.style.borderRadius = '10px';
    containerDiv.style.padding = '10px';
    containerDiv.style.marginBottom = '3px';

    const imgWrapper = document.createElement('div');
    imgWrapper.style.position = 'relative';

    const imgReactWrapper = document.createElement('div');
    imgReactWrapper.style.position = 'relative';
    imgReactWrapper.style.marginLeft = '10px';

    imgReactElement.style.position = 'absolute';
    imgReactElement.style.left = '50%';
    imgReactElement.style.transform = 'translateX(-50%)';
    imgReactElement.style.marginLeft = '-9px';
    imgWrapper.appendChild(imgElement);
    imgReactWrapper.appendChild(imgReactElement);

    containerDiv.appendChild(imgWrapper);
    containerDiv.appendChild(imgReactWrapper);
    containerDiv.appendChild(pElement);

    showMessage.appendChild(containerDiv);
};


const receivedMessageForReact = async (payload) => {
    console.log("Message Received");
    const message = await JSON.parse(payload.body);
    // await welcome();
    if (loginUser === message.staffId) {
        notificationCount = notificationCount + 1;
        await showNotiCount();
        await notifyMessageForReact(message.content, message.sender, message.photo, message.type);
    }
};


const pressedLikeForEvent = async (id, type) => {
    console.log('PostId', id);
    const myObj = {
        postId: id,
        sender: loginUser,
        content: ` reacted to your announcement!`,
        type: type
    };
    stompClient.send('/app/react-event-message', {}, JSON.stringify(myObj));
};

const pressedLike = async (id, type) => {
    console.log('PostId', id);
    const myObj = {
        postId: id,
        sender: loginUser,
        content: ` reacted to your post!`,
        type: type
    };
    stompClient.send('/app/react-message', {}, JSON.stringify(myObj));
};


const getAllComments = async (id) => {
    const fetchComments = await fetch(`/getComment/${id}`);
    if (!fetchComments.ok) {
        alert('There is something wrong in the comment section,Please try again!');
    }
    const getData = await fetchComments.json();
    const chatArea = document.querySelector('#commentMessageText');
    chatArea.innerHTML = '';
    for (const c of getData) {
        let localDateTime = new Date(c.localDateTime);
        await displayMessage(c.user.name, c.content, c.user.photo, c.id, c.post.id, localDateTime, chatArea);
        console.log('data time',localDateTime)
    }
};

const displayMessage = async (sender, content, photo, id, postId,localDateTime,chatArea) => {
    const user = await fetchUserDataByPostedUser(loginUser);
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
    const spanElement = document.createElement('span');
    spanElement.classList.add(`comment-span-container-${id}`);
    spanElement.id = id;
    spanElement.innerHTML = `${content}`;
    // divItem.style.border = '1px solid lightslategrey';
    divItem.style.padding = '5px';
    divItem.style.borderRadius = '30px'
    divItem.style.width = '450px';

    const convertDiv = document.createElement('div');
    convertDiv.classList.add('content-container');
    let createdTime = await timeAgo(new Date(localDateTime))
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

    const likeIconWithoutColor = document.createElement('i');
    likeIconWithoutColor.classList.add('fa-regular', 'fa-thumbs-up');
    likeIconWithoutColor.style.fontSize = '20px';
    likeIconWithoutColor.addEventListener('click', () => {
        if (iconDiv.contains(dislikeIconWithColor)) {
            iconDiv.removeChild(dislikeIconWithColor);
        }
        iconDiv.appendChild(dislikeIconWithoutColor);
        iconDiv.removeChild(likeIconWithoutColor);
        iconDiv.appendChild(likeIconWithColor);
        const myObj = {
            postId: postId,
            commentId: id,
            sender: loginUser,
            content: ` liked  your comment!`,
            type: 'LIKE',
        }
        stompClient.send('/app/like-commentReact-message', {}, JSON.stringify(myObj));
    });
    const likeIconWithColor = document.createElement('i');
    likeIconWithColor.style.fontSize = '20px';
    likeIconWithColor.classList.add('fa-solid', 'fa-thumbs-up');
    likeIconWithColor.addEventListener('click', () => {
        if (iconDiv.contains(dislikeIconWithColor)) {
            iconDiv.removeChild(dislikeIconWithColor);
        }
        iconDiv.appendChild(dislikeIconWithoutColor);
        iconDiv.removeChild(likeIconWithColor);
        iconDiv.appendChild(likeIconWithoutColor);
        const myObj = {
            postId: postId,
            commentId: id,
            sender: loginUser,
            content: ` liked  your comment!`,
            type: null,
        }
        stompClient.send('/app/like-commentReact-message', {}, JSON.stringify(myObj));
    });
    const dislikeIconWithoutColor = document.createElement('i');
    dislikeIconWithoutColor.classList.add('fa-regular', 'fa-thumbs-down');
    dislikeIconWithoutColor.style.fontSize = '20px';
    dislikeIconWithoutColor.style.padding = '10px';
    dislikeIconWithoutColor.addEventListener('click', () => {
        iconDiv.removeChild(dislikeIconWithoutColor);
        iconDiv.appendChild(dislikeIconWithColor);
        if (iconDiv.contains(likeIconWithColor)) {
            iconDiv.removeChild(likeIconWithColor);
        }
        iconDiv.appendChild(likeIconWithoutColor);
        const myObj = {
            postId: postId,
            commentId: id,
            sender: loginUser,
            content: ` disliked  your comment!`,
            type: 'DISLIKE',
        }
        stompClient.send('/app/like-commentReact-message', {}, JSON.stringify(myObj));
    });
    const dislikeIconWithColor = document.createElement('i');
    dislikeIconWithColor.classList.add('fa-solid', 'fa-thumbs-down');
    dislikeIconWithColor.style.fontSize = '20px';
    dislikeIconWithColor.style.padding = '10px';
    dislikeIconWithColor.addEventListener('click', () => {
        iconDiv.removeChild(dislikeIconWithColor);
        iconDiv.appendChild(dislikeIconWithoutColor);
        if (iconDiv.contains(likeIconWithColor)) {
            iconDiv.removeChild(likeIconWithColor);
        }
        iconDiv.appendChild(likeIconWithoutColor);
        const myObj = {
            postId: postId,
            commentId: id,
            sender: loginUser,
            content: ` dislike your comment!`,
            type: null,
        }
        stompClient.send('/app/like-commentReact-message', {}, JSON.stringify(myObj));
    });
    let replyInput = divItem.querySelector('.reply-input');
    let submitButton = divItem.querySelector('.submit-reply-btn');
    let cancelButton = divItem.querySelector('.cancel-reply-btn');

    const replyButton = document.createElement('i');
    replyButton.classList.add('fa-solid', 'fa-reply');
    replyButton.style.padding = '10px';
    replyButton.style.fontSize = '20px';
    replyButton.style.marginLeft = '310px';
    replyButton.id = id;

    replyButton.addEventListener('click', async () => {
        const commentUser = await fetchCommetedUser(id);
        if (!replyInput) {
            replyInput = document.createElement('input');
            replyInput.type = 'text';
            replyInput.placeholder = 'Type your reply...';
            replyInput.classList.add('reply-input');
            replyInput.style.marginTop = '10px';
            replyInput.style.borderRadius = '10px';
            replyInput.value = `@ ${commentUser.user.name} : `;
            replyInput.style.padding = '8px';
            replyInput.readOnly = true;
            divItem.appendChild(replyInput);

            replyInput.addEventListener('focus', function () {
                replyInput.removeAttribute('readOnly');
                replyInput.classList.remove('readonly');
            });
            replyInput.addEventListener('blur', function () {
                replyInput.readOnly = true;
                replyInput.classList.add('readonly');
            });
        } else {
            replyInput.value = `@ ${commentUser.user.name} `;
            replyInput.readOnly = true;
            replyInput.classList.add('readonly');
        }

        replyInput.addEventListener('input', function () {
            if (replyInput.value === `@ ${commentUser.user.name} `) {
                replyInput.classList.add('readonly');
            } else {
                replyInput.classList.remove('readonly');
            }
        });

        if (!submitButton) {
            submitButton = document.createElement('button');
            submitButton.classList.add('btn', 'btn-outline-primary', 'btn-sm', 'submit-reply-btn');
            submitButton.textContent = 'Submit';
            submitButton.addEventListener('click', async () => {
                const replyContent = replyInput.value.trim();
                if (replyContent !== '') {
                    const myObj = {
                        commentId: id,
                        sender: loginUser,
                        content: replyContent
                    };
                    stompClient.send('/app/comment-reply-message', {}, JSON.stringify(myObj));
                    const chatArea = document.querySelector('#commentMessageText');
                    // await displayMessage(user.name, replyContent, user.photo, id, postId, chatArea);
                    //
                    //  const reply = await createReplyElement(replyContent,user.name,user.photo,id,postId);
                    console.log(`Replying to comment ${id}: ${replyContent}`);
                    repliesContainer.style.display = 'block';
                    replyInput.value = '';
                } else {
                    alert('Please enter something that you want to reply.');
                }
            });
            divItem.appendChild(submitButton);
        }

        if (!cancelButton) {
            cancelButton = document.createElement('button');
            cancelButton.innerHTML = 'Cancel';
            cancelButton.classList.add('btn', 'btn-outline-secondary', 'btn-sm', 'cancel-reply-btn');
            cancelButton.addEventListener('click', () => {
                divItem.removeChild(replyInput);
                divItem.removeChild(submitButton);
                divItem.removeChild(cancelButton);
            });
            divItem.appendChild(cancelButton);
        }

        replyInput.focus();
    });
    divItem.appendChild(replyButton);
    const iconDiv = document.createElement('div');
    iconDiv.style.marginLeft = '345px';
    iconDiv.style.marginTop = '-40px';
    const react = await commentReactType(id, user.id, postId);
    if (react !== null && react.type === 'DISLIKE') {
        iconDiv.appendChild(dislikeIconWithColor);
    } else {
        iconDiv.appendChild(dislikeIconWithoutColor);
    }

    if (react !== null && react.type === 'LIKE') {
        iconDiv.appendChild(likeIconWithColor);
    } else {
        iconDiv.appendChild(likeIconWithoutColor);
    }
    divItem.appendChild(iconDiv);
    const replies = await fetchAndDisplayReplies(id);
    const repliesContainer = document.createElement('div');
    repliesContainer.classList.add(`replies-container-${id}`);
    repliesContainer.style.display = 'none';


    for (const reply of replies) {
        repliesContainer.appendChild(reply);
    }
    const dropdownMenu = document.createElement('div');
    dropdownMenu.classList.add('dropdown-menu');

    const dropDownIcon = document.createElement('i');
    dropDownIcon.classList.add('fa-solid', 'fa-ellipsis-vertical');
    dropDownIcon.style.padding = '15px';
    dropDownIcon.style.fontSize = '20px';
    dropDownIcon.addEventListener('click', () => {
        dropdownMenu.style.display = dropdownMenu.style.display === 'none' ? 'block' : 'none';
    });

    const seeMoreButton = document.createElement('i');
    seeMoreButton.classList.add('fa-regular', 'fa-comment');
    seeMoreButton.style.padding = '10px';
    seeMoreButton.addEventListener('click', () => {
        dropdownMenu.style.display = 'none';
        repliesContainer.style.display = repliesContainer.style.display === 'none' ? 'block' : 'none';
    });

    dropdownMenu.appendChild(seeMoreButton);
    divItem.appendChild(dropdownMenu);

    if (sender === user.name) {
        const editIcon = document.createElement('i');
        editIcon.classList.add('fa-regular', 'fa-pen-to-square');
        editIcon.style.padding = '15px';
        editIcon.addEventListener('click', () => {
            dropdownMenu.style.display = 'none';
            let currentContent = null;
            if (convertDiv.contains(spanElement)) {
                currentContent = spanElement.innerHTML;
            }
            console.log('textArea', currentContent);
            const textarea = document.createElement('textarea');
            textarea.style.borderRadius = '10px';
            textarea.style.backgroundColor = 'lightgrey';
            textarea.value = currentContent;
            ddEl.replaceChild(textarea, spanElement);
            const saveButton = document.createElement('button');
            saveButton.textContent = 'Save';
            saveButton.classList.add('btn', 'btn-outline-primary', 'btn-sm', 'save-button');
            saveButton.addEventListener('click', async () => {
                const updatedContent = textarea.value.trim();
                if (updatedContent !== '') {
                    const commentId = spanElement.id;
                    await updateContent(commentId, updatedContent);
                    spanElement.innerHTML = updatedContent;
                    ddEl.removeChild(textarea);
                    divItem.removeChild(saveButton);
                } else {
                    alert('Please enter something for the comment.');
                }
            });
            divItem.appendChild(saveButton);
        });
        dropdownMenu.appendChild(editIcon);

        const trashIcon = document.createElement('i');
        trashIcon.classList.add('fa-solid', 'fa-trash');
        trashIcon.style.padding = '10px';
        trashIcon.addEventListener('click', async () => {
            dropdownMenu.style.display = 'none';
            const commentId = spanElement.id;
            console.log("Span Element id", commentId);
            await deleteComment(commentId);
        });
        dropdownMenu.appendChild(trashIcon);
        divItem.appendChild(dropdownMenu);
    }
    const chDiv = document.createElement('div');
    chDiv.style.marginLeft = '410px';
    chDiv.style.marginTop = '-45px';
    chDiv.appendChild(dropDownIcon);
    divItem.appendChild(chDiv);
    const rpDiv = document.createElement('div');
    rpDiv.appendChild(repliesContainer);
    divItem.appendChild(rpDiv);
};

const onSuccess =async (id) => {
    const reply = await fetchAndDisplayLastReply(id);
    console.log('wow mal ya pr lr')
    await document.querySelector(`.replies-container-${id}`).appendChild(reply);
}

const fetchAndDisplayLastReply = async (id) => {
    const fetchReplies = await fetch(`/getAll-comment/${id}`);
    const fetchDataForReplies = await fetchReplies.json();
    const replyElement = document.createElement('div');
    const lastReplyIndex = fetchDataForReplies.length - 1;
    if (lastReplyIndex >= 0) {
        const reply = fetchDataForReplies[lastReplyIndex];
        replyElement.classList.add(`reply-item-${reply.id}`);
        let createdTimeForReply = await timeAgo(new Date(reply.localDateTime))
        replyElement.setAttribute('data-toggle', 'tooltip');
        replyElement.setAttribute('title', `${createdTimeForReply}`);
        const user = await fetchUserDataByPostedUser(loginUser);
        const userRpImage = document.createElement('img');
        const photo = reply.user.photo || '/static/assets/img/card.jpg';
        userRpImage.src = `${photo}`;
        userRpImage.alt = 'User Photo';
        userRpImage.classList.add('user-photo');
        userRpImage.style.height = '50px';
        userRpImage.style.width = '50px';
        userRpImage.style.marginLeft = '50px';
        userRpImage.style.backgroundColor = '#cccccc';
        replyElement.classList.add('reply-item');
        const replySender = document.createElement('span');
        if (reply.user.name === user.name) {
            replySender.innerHTML = `You : `;
        } else {
            replySender.innerHTML = `${reply.user.name} : `;
        }
        const replyContent = document.createElement('span');
        replyContent.id = reply.id;
        replyContent.innerHTML = reply.content;
        const spElement = document.createElement('span');
        const contentElement = document.createElement('span');
        const divEl = document.createElement('div');
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

        let replyInputForReply = replyElement.querySelector('.reply-input');
        let submitButton = replyElement.querySelector('.submit-reply-btn');
        let cancelButton = replyElement.querySelector('.cancel-reply-btn');
        const replyButton = document.createElement('i');
        replyButton.style.padding = '10px';
        replyButton.style.marginLeft = '300px';
        replyButton.classList.add('fa-solid', 'fa-reply');
        replyButton.addEventListener('click',async () => {
            const replyUser = await fetchRepliedUserForData(reply.id);
            if (!replyInputForReply) {
                replyInputForReply = document.createElement('input');
                replyInputForReply.type = 'text';
                replyInputForReply.placeholder = 'Type your reply...';
                replyInputForReply.classList.add('reply-input');
                replyInputForReply.style.marginTop = '10px';
                replyInputForReply.style.borderRadius = '10px';
                replyInputForReply.value = `@ ${replyUser.user.name} : `;
                replyInputForReply.style.padding = '8px';
                replyInputForReply.readOnly = true;
                replyElement.appendChild(replyInputForReply);

                replyInputForReply.addEventListener('focus', function () {
                    replyInputForReply.removeAttribute('readOnly');
                    replyInputForReply.classList.remove('readonly');
                });
                replyInputForReply.addEventListener('blur', function () {
                    replyInputForReply.readOnly = true;
                    replyInputForReply.classList.add('readonly');
                });
            } else {
                replyInputForReply.value = `@ ${replyUser.user.name} `;
                replyInputForReply.readOnly = true;
                replyInputForReply.classList.add('readonly');
            }

            replyInputForReply.addEventListener('input', function () {
                if (replyInputForReply.value === `@ ${replyUser.user.name} `) {
                    replyInputForReply.classList.add('readonly');
                } else {
                    replyInputForReply.classList.remove('readonly');
                }
            });
            if (!submitButton) {
                submitButton = document.createElement('button');
                submitButton.style.marginTop = '10px';
                submitButton.classList.add('btn', 'btn-outline-primary', 'btn-sm', 'submit-reply-btn');
                submitButton.textContent = 'Submit';
                submitButton.addEventListener('click', async () => {
                    const replyContent = replyInputForReply.value.trim();
                    if (replyContent !== '') {
                        const myObj = {
                            replyId: reply.id,
                            sender: loginUser,
                            content: replyContent
                        };
                        stompClient.send('/app/comment-reply-message', {}, JSON.stringify(myObj));
                        const chatArea = document.querySelector('#commentMessageText');
                        //await displayMessage(user.name, replyContent, user.photo, id, reply.comment.post.id, chatArea);
                        console.log("kyi mal", user.name, replyContent, user.photo, id, reply.comment.post.id);
                        console.log(`Replying to comment ${id}: ${replyContent}`);
                        replyInputForReply.value = '';
                    } else {
                        alert('Please enter something that you want to reply.');
                    }
                });

                replyElement.appendChild(submitButton);
            }
            if (!cancelButton) {
                cancelButton = document.createElement('button');
                cancelButton.innerHTML = 'Cancel';
                cancelButton.style.marginTop = '10px';
                cancelButton.classList.add('btn', 'btn-outline-secondary', 'btn-sm', 'cancel-reply-btn');
                cancelButton.addEventListener('click', () => {
                    replyElement.removeChild(replyInputForReply);
                    replyElement.removeChild(submitButton);
                    replyElement.removeChild(cancelButton);
                });
                replyElement.appendChild(cancelButton);
            }
            replyInputForReply.focus();
        });
        replyElement.appendChild(replyButton);
        const likeIconWithoutColor = document.createElement('i');
        likeIconWithoutColor.classList.add('fa-regular', 'fa-thumbs-up');
        likeIconWithoutColor.style.fontSize = '20px';
        likeIconWithoutColor.addEventListener('click', () => {
            if (iconDiv.contains(dislikeIconWithColor)) {
                iconDiv.removeChild(dislikeIconWithColor);
            }
            iconDiv.appendChild(dislikeIconWithoutColor);
            iconDiv.removeChild(likeIconWithoutColor);
            iconDiv.appendChild(likeIconWithColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` like  your reply!`,
                type: 'LIKE',
            };

            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });

        const likeIconWithColor = document.createElement('i');
        likeIconWithColor.style.fontSize = '20px';
        likeIconWithColor.classList.add('fa-solid', 'fa-thumbs-up');
        likeIconWithColor.addEventListener('click', () => {
            if (iconDiv.contains(dislikeIconWithColor)) {
                iconDiv.removeChild(dislikeIconWithColor);
            }
            iconDiv.appendChild(dislikeIconWithoutColor);
            iconDiv.removeChild(likeIconWithColor);
            iconDiv.appendChild(likeIconWithoutColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` liked  your reply!`,
                type: null,
            }
            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });
        const dislikeIconWithoutColor = document.createElement('i');
        dislikeIconWithoutColor.classList.add('fa-regular', 'fa-thumbs-down');
        dislikeIconWithoutColor.style.fontSize = '20px';
        dislikeIconWithoutColor.style.padding = '10px';
        dislikeIconWithoutColor.addEventListener('click', () => {
            iconDiv.removeChild(dislikeIconWithoutColor);
            iconDiv.appendChild(dislikeIconWithColor);
            if (iconDiv.contains(likeIconWithColor)) {
                iconDiv.removeChild(likeIconWithColor);
            }
            iconDiv.appendChild(likeIconWithoutColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` dislike your reply!`,
                type: 'DISLIKE',
            }
            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });
        const dislikeIconWithColor = document.createElement('i');
        dislikeIconWithColor.classList.add('fa-solid', 'fa-thumbs-down');
        dislikeIconWithColor.style.fontSize = '20px';
        dislikeIconWithColor.style.padding = '10px';
        dislikeIconWithColor.addEventListener('click', () => {
            iconDiv.removeChild(dislikeIconWithColor);
            iconDiv.appendChild(dislikeIconWithoutColor);
            if (iconDiv.contains(likeIconWithColor)) {
                iconDiv.removeChild(likeIconWithColor);
            }
            iconDiv.appendChild(likeIconWithoutColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` dislike your comment!`,
                type: null,
            }
            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });

        const iconDiv = document.createElement('div');
        iconDiv.style.marginLeft = '360px';
        iconDiv.style.marginTop = '-40px';
        const isReact = await replyReactType(id, user.id, reply.id);
        if (isReact !== null && isReact.type === 'DISLIKE') {
            iconDiv.appendChild(dislikeIconWithColor);
        } else {
            iconDiv.appendChild(dislikeIconWithoutColor);
        }

        if (isReact !== null && isReact.type === 'LIKE') {
            iconDiv.appendChild(likeIconWithColor);
        } else {
            iconDiv.appendChild(likeIconWithoutColor);
        }
        const dropdownMenu = document.createElement('div');
        dropdownMenu.classList.add('dropdown-menu');

        const dropDownIcon = document.createElement('i');
        dropDownIcon.classList.add('fa-solid', 'fa-ellipsis-vertical');
        dropDownIcon.style.padding = '15px';
        dropDownIcon.addEventListener('click', () => {
            dropdownMenu.style.display = dropdownMenu.style.display === 'none' ? 'block' : 'none';
        });

        if (reply.user.name === user.name) {
            const editIcon = document.createElement('i');
            editIcon.classList.add('fa-regular', 'fa-pen-to-square');
            editIcon.style.padding = '15px';
            editIcon.addEventListener('click', () => {
                dropdownMenu.style.display = 'none';
                const currentContent = replyContent.innerHTML;
                const textarea = document.createElement('textarea');
                textarea.style.borderRadius = '10px';
                textarea.style.backgroundColor = 'lightgrey';
                textarea.value = currentContent;
                divEl.replaceChild(textarea, replyContent);
                const saveButton = document.createElement('button');
                saveButton.textContent = 'Save';
                saveButton.classList.add('btn', 'btn-outline-primary', 'btn-sm', 'save-button');
                saveButton.addEventListener('click', async () => {
                    const updatedContent = textarea.value.trim();
                    if (updatedContent !== '') {
                        const replyId = reply.id;
                        await updateContentForReply(replyId, updatedContent);
                        replyContent.innerHTML = updatedContent;
                        divEl.removeChild(textarea);
                        replyElement.removeChild(saveButton);
                    } else {
                        alert('Please enter something for the reply.');
                    }
                });
                replyElement.appendChild(saveButton);
            });
            dropdownMenu.appendChild(editIcon);
            replyElement.appendChild(dropdownMenu);

            const trashIcon = document.createElement('i');
            trashIcon.classList.add('fa-solid', 'fa-trash');
            trashIcon.style.padding = '15px';
            trashIcon.addEventListener('click', async () => {
                dropdownMenu.style.display = 'none';
                const replyId = replyContent.id;
                console.log("Span Reply id", replyId);
                await deleteReply(replyId);
            });
            dropdownMenu.appendChild(trashIcon);
            replyElement.appendChild(dropdownMenu);
            replyElement.appendChild(dropDownIcon);
            replyElement.appendChild(iconDiv);
        }
    }
    console.log('Thi chin tal',replyElement)
    return replyElement;
};


const fetchAndDisplayReplies = async (id) => {
    const fetchReplies = await fetch(`/getAll-comment/${id}`);
    const fetchDataForReplies = await fetchReplies.json();

    const replies = [];

    for (const reply of fetchDataForReplies) {
        const user = await fetchUserDataByPostedUser(loginUser);
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
        let createdTimeForReply = await timeAgo(new Date(reply.localDateTime))
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
        replyContent.innerHTML = reply.content;
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

        let replyInputForReply = replyElement.querySelector('.reply-input');
        let submitButton = replyElement.querySelector('.submit-reply-btn');
        let cancelButton = replyElement.querySelector('.cancel-reply-btn');
        const replyButton = document.createElement('i');
        replyButton.style.padding = '10px';
        replyButton.style.marginLeft = '300px';
        replyButton.classList.add('fa-solid', 'fa-reply');
        replyButton.addEventListener('click',async () => {
            const replyUser = await fetchRepliedUserForData(reply.id);
            if (!replyInputForReply) {
                replyInputForReply = document.createElement('input');
                replyInputForReply.type = 'text';
                replyInputForReply.placeholder = 'Type your reply...';
                replyInputForReply.classList.add('reply-input');
                replyInputForReply.style.marginTop = '10px';
                replyInputForReply.style.borderRadius = '10px';
                replyInputForReply.value = `@ ${replyUser.user.name} : `;
                replyInputForReply.style.padding = '8px';
                replyInputForReply.readOnly = true;
                replyElement.appendChild(replyInputForReply);

                replyInputForReply.addEventListener('focus', function () {
                    replyInputForReply.removeAttribute('readOnly');
                    replyInputForReply.classList.remove('readonly');
                });
                replyInputForReply.addEventListener('blur', function () {
                    replyInputForReply.readOnly = true;
                    replyInputForReply.classList.add('readonly');
                });
            } else {
                replyInputForReply.value = `@ ${replyUser.user.name} `;
                replyInputForReply.readOnly = true;
                replyInputForReply.classList.add('readonly');
            }

            replyInputForReply.addEventListener('input', function () {
                if (replyInputForReply.value === `@ ${replyUser.user.name} `) {
                    replyInputForReply.classList.add('readonly');
                } else {
                    replyInputForReply.classList.remove('readonly');
                }
            });
            if (!submitButton) {
                submitButton = document.createElement('button');
                submitButton.style.marginTop = '10px';
                submitButton.classList.add('btn', 'btn-outline-primary', 'btn-sm', 'submit-reply-btn');
                submitButton.textContent = 'Submit';
                submitButton.addEventListener('click', async () => {
                    const replyContent = replyInputForReply.value.trim();
                    if (replyContent !== '') {
                        const myObj = {
                            replyId: reply.id,
                            sender: loginUser,
                            content: replyContent
                        };
                        stompClient.send('/app/comment-reply-message', {}, JSON.stringify(myObj));
                        const chatArea = document.querySelector('#commentMessageText');
                        //  await displayMessage(user.name, replyContent, user.photo, id, reply.comment.post.id, chatArea);
                        console.log("kyi mal", user.name, replyContent, user.photo, id, reply.comment.post.id);
                        console.log(`Replying to comment ${id}: ${replyContent}`);
                        replyInputForReply.value = '';
                    } else {
                        alert('Please enter something that you want to reply.');
                    }
                });

                replyElement.appendChild(submitButton);
            }
            if (!cancelButton) {
                cancelButton = document.createElement('button');
                cancelButton.innerHTML = 'Cancel';
                cancelButton.style.marginTop = '10px';
                cancelButton.classList.add('btn', 'btn-outline-secondary', 'btn-sm', 'cancel-reply-btn');
                cancelButton.addEventListener('click', () => {
                    replyElement.removeChild(replyInputForReply);
                    replyElement.removeChild(submitButton);
                    replyElement.removeChild(cancelButton);
                });
                replyElement.appendChild(cancelButton);
            }
            replyInputForReply.focus();
        });
        replyElement.appendChild(replyButton);
        const likeIconWithoutColor = document.createElement('i');
        likeIconWithoutColor.classList.add('fa-regular', 'fa-thumbs-up');
        likeIconWithoutColor.style.fontSize = '20px';
        likeIconWithoutColor.addEventListener('click', () => {
            if (iconDiv.contains(dislikeIconWithColor)) {
                iconDiv.removeChild(dislikeIconWithColor);
            }
            iconDiv.appendChild(dislikeIconWithoutColor);
            iconDiv.removeChild(likeIconWithoutColor);
            iconDiv.appendChild(likeIconWithColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` like  your reply!`,
                type: 'LIKE',
            };

            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });

        const likeIconWithColor = document.createElement('i');
        likeIconWithColor.style.fontSize = '20px';
        likeIconWithColor.classList.add('fa-solid', 'fa-thumbs-up');
        likeIconWithColor.addEventListener('click', () => {
            if (iconDiv.contains(dislikeIconWithColor)) {
                iconDiv.removeChild(dislikeIconWithColor);
            }
            iconDiv.appendChild(dislikeIconWithoutColor);
            iconDiv.removeChild(likeIconWithColor);
            iconDiv.appendChild(likeIconWithoutColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` liked  your reply!`,
                type: null,
            }
            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });
        const dislikeIconWithoutColor = document.createElement('i');
        dislikeIconWithoutColor.classList.add('fa-regular', 'fa-thumbs-down');
        dislikeIconWithoutColor.style.fontSize = '20px';
        dislikeIconWithoutColor.style.padding = '10px';
        dislikeIconWithoutColor.addEventListener('click', () => {
            iconDiv.removeChild(dislikeIconWithoutColor);
            iconDiv.appendChild(dislikeIconWithColor);
            if (iconDiv.contains(likeIconWithColor)) {
                iconDiv.removeChild(likeIconWithColor);
            }
            iconDiv.appendChild(likeIconWithoutColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` dislike your reply!`,
                type: 'DISLIKE',
            }
            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });
        const dislikeIconWithColor = document.createElement('i');
        dislikeIconWithColor.classList.add('fa-solid', 'fa-thumbs-down');
        dislikeIconWithColor.style.fontSize = '20px';
        dislikeIconWithColor.style.padding = '10px';
        dislikeIconWithColor.addEventListener('click', () => {
            iconDiv.removeChild(dislikeIconWithColor);
            iconDiv.appendChild(dislikeIconWithoutColor);
            if (iconDiv.contains(likeIconWithColor)) {
                iconDiv.removeChild(likeIconWithColor);
            }
            iconDiv.appendChild(likeIconWithoutColor);
            const myObj = {
                postId: reply.id,
                commentId: id,
                sender: loginUser,
                content: ` dislike your comment!`,
                type: null,
            }
            stompClient.send('/app/like-replyReact-message', {}, JSON.stringify(myObj));
        });

        const iconDiv = document.createElement('div');
        iconDiv.style.marginLeft = '360px';
        iconDiv.style.marginTop = '-40px';
        const isReact = await replyReactType(id, user.id, reply.id);
        if (isReact !== null && isReact.type === 'DISLIKE') {
            iconDiv.appendChild(dislikeIconWithColor);
        } else {
            iconDiv.appendChild(dislikeIconWithoutColor);
        }

        if (isReact !== null && isReact.type === 'LIKE') {
            iconDiv.appendChild(likeIconWithColor);
        } else {
            iconDiv.appendChild(likeIconWithoutColor);
        }
        const dropdownMenu = document.createElement('div');
        dropdownMenu.classList.add('dropdown-menu');

        const dropDownIcon = document.createElement('i');
        dropDownIcon.classList.add('fa-solid', 'fa-ellipsis-vertical');
        dropDownIcon.style.padding = '15px';
        dropDownIcon.addEventListener('click', () => {
            dropdownMenu.style.display = dropdownMenu.style.display === 'none' ? 'block' : 'none';
        });

        if (reply.user.name === user.name) {
            const editIcon = document.createElement('i');
            editIcon.classList.add('fa-regular', 'fa-pen-to-square');
            editIcon.style.padding = '15px';
            editIcon.addEventListener('click', () => {
                dropdownMenu.style.display = 'none';
                const currentContent = replyContent.innerHTML;
                const textarea = document.createElement('textarea');
                textarea.style.borderRadius = '10px';
                textarea.style.backgroundColor = 'lightgrey';
                textarea.value = currentContent;
                divEl.replaceChild(textarea, replyContent);
                const saveButton = document.createElement('button');
                saveButton.textContent = 'Save';
                saveButton.classList.add('btn', 'btn-outline-primary', 'btn-sm', 'save-button');
                saveButton.addEventListener('click', async () => {
                    const updatedContent = textarea.value.trim();
                    if (updatedContent !== '') {
                        const replyId = reply.id;
                        await updateContentForReply(replyId, updatedContent);
                        replyContent.innerHTML = updatedContent;
                        divEl.removeChild(textarea);
                        replyElement.removeChild(saveButton);
                    } else {
                        alert('Please enter something for the reply.');
                    }
                });
                replyElement.appendChild(saveButton);
            });
            dropdownMenu.appendChild(editIcon);
            // replyElement.appendChild(dropdownMenu);

            const trashIcon = document.createElement('i');
            trashIcon.classList.add('fa-solid', 'fa-trash');
            trashIcon.style.padding = '15px';
            trashIcon.addEventListener('click', async () => {
                dropdownMenu.style.display = 'none';
                const replyId = replyContent.id;
                console.log("Span Reply id", replyId);
                await deleteReply(replyId);
            });
            dropdownMenu.appendChild(trashIcon);
            replyElement.appendChild(dropdownMenu);
            replyElement.appendChild(dropDownIcon);
        }
        replyElement.appendChild(iconDiv);

        replies.push(replyElement);
    }
    return replies;
};


const replyReactType = async (commentId, userId, replyId) => {
    const fetchTypeForReply = await fetch(`/reply-type-react/${commentId}/${userId}/${replyId}`);
    const response = await fetchTypeForReply.json();
    return response;
}

const commentReactType = async (id, userId, postId) => {
    const fetchType = await fetch(`/comment-type-react/${id}/${userId}/${postId}`);
    const response = await fetchType.json();
    return response;
}

const deleteComment = async (id) => {
    const getData = await fetch(`/delete-comment/${id}`, {
        method: 'DELETE'
    });
    if (!getData.ok) {
        alert('Something wrong while deleting comment');
    }
    const data = await getData.json();
    const postId = data.postId;
    console.log('postId', postId);
    const commentCountSize = await fetchCommentSizes(postId);
    document.getElementById(`commentCountStaticBox-${postId}`).innerHTML = '';
    document.getElementById(`commentCountStaticBox-${postId}`).innerHTML = `comment ${commentCountSize}`;
    const userItem = document.querySelector(`.user-item-${id}`);
    if (!userItem) {
        console.error(`User item with id ${id} not found`);
        return;
    }
    userItem.remove();
    // await getAllComments(postId);
};

const deleteReply = async (id) => {
    const getData = await fetch(`/delete-reply/${id}`, {
        method: 'DELETE'
    });
    if (!getData.ok) {
        alert('Something wrong while deleting reply');
    }
    const data = await getData.json();
    const postId = data.postId;
    console.log('postId', postId);
    const commentCountSize = await fetchCommentSizes(postId);
    document.getElementById(`commentCountStaticBox-${postId}`).innerHTML = '';
    document.getElementById(`commentCountStaticBox-${postId}`).innerHTML = `comment ${commentCountSize}`;
    const replyItem = document.querySelector(`.reply-item-${id}`)
    if (!replyItem) {
        console.error(`User item with id ${id} not found`);
        return;
    }
    replyItem.remove();
    // await getAllComments(postId);
};

const updateContentForReply = async (id, content) => {
    const myObj = {
        id: id,
        content: content
    };
    const updatedData = await fetch(`/reply-update`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(myObj)
    });
    if (!updatedData.ok) {
        alert("Something wrong while updating reply");
    }
    const data = await updatedData.json();
    const postId = data.postId;
    console.log("Want to know postId", postId);
    const divEl = document.querySelector(`.reply-container-div-${id}`);
    const spElement = document.querySelector(`.span-reply-container-${id}`);
    if(spElement){
        divEl.removeChild(spElement);
    }
    const newSpElement = document.createElement('span');
    newSpElement.id = id;
    newSpElement.classList.add(`span-reply-container-${id}`);
    newSpElement.innerHTML = content;
    divEl.appendChild(newSpElement);
    // await getAllComments(postId);
};

const updateContent = async (id, content) => {
    const myObj = {
        id: id,
        content: content
    };
    const upData = await fetch(`/comment-update`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(myObj)
    });
    if (!upData.ok) {
        alert('something went wrong while updating');
    }
    const data = await upData.json();
    const postId = data.postId;
    console.log("Want to know postId", postId)
    const cmtDiv = document.querySelector(`.comment-container-${id}`);
    const spanElement = document.querySelector(`.comment-span-container-${id}`);
    if (spanElement) {
        console.log("shi tal")
        cmtDiv.removeChild(spanElement);
    }
    const newSpanElement = document.createElement('span');
    newSpanElement.id = id;
    newSpanElement.classList.add(`comment-span-container-${id}`);
    newSpanElement.innerHTML = content;
    cmtDiv.appendChild(newSpanElement);
    // await getAllComments(postId);
};

const receivedMessageForComment = async (payload) => {
    console.log('Message Received');
    const message = await JSON.parse(payload.body);
    // const user = await fetchUserDataByPostedUser(loginUser);
    // const postList = await fetchUserPostById(loginUser);
    // console.log("type",typeof postList);
    if (loginUser === message.staffId) {
        notificationCount = notificationCount + 1;
        await showNotiCount();
        console.log(message.photo, message.sender, message.content, message.postId);
        const msg = ' commented to your photo';
        message.photo = message.photo || '/static/assets/img/card.jpg';
        await notifyMessageForReact(msg, message.sender, message.photo, null);
    }
    const commentCountSize = await fetchCommentSizes(message.postId);
    document.getElementById(`commentCountStaticBox-${message.postId}`).innerHTML = '';
    document.getElementById(`commentCountStaticBox-${message.postId}`).innerHTML = `comment ${commentCountSize}`;
    // await welcome();
    message.photo = message.photo || '/static/assets/img/card.jpg';
    const chatArea = document.querySelector('#commentMessageText');
    const localDateTime = new Date().toLocaleString();
    await displayMessage(message.sender, message.content, message.photo, message.commentId, message.postId,localDateTime,chatArea);
};


const receivedMessageForCommentReply = async (payload) => {
    console.log('Message Received');
    const message = await JSON.parse(payload.body);
    console.log('staffid', message.staffId);
    if (loginUser === message.staffId) {
        notificationCount = notificationCount + 1;
        await showNotiCount();
        console.log(message.photo, message.sender, message.content, message.postId);
        const msg = ' replied to your comment';
        await notifyMessageForReact(msg, message.sender, message.photo, null);
    }
    // await welcome();
    const commentCountSize = await fetchCommentSizes(message.postId);
    document.getElementById(`commentCountStaticBox-${message.postId}`).innerHTML = '';
    document.getElementById(`commentCountStaticBox-${message.postId}`).innerHTML = `comment ${commentCountSize}`;
    console.log('Comment ide',message.commentId)
    if(message.commentId != null){
        console.log('haha')
        const reply = await fetchAndDisplayLastReply(message.commentId);
        console.log('wow mal ya pr lr')
        await document.querySelector(`.replies-container-${message.commentId}`).appendChild(reply);
    }
    // const chatArea = document.querySelector('#commentMessageText');
    // await displayMessage(message.sender, message.content, message.photo, message.commentId, message.postId, chatArea);
};

const pressedComment = async (id) => {
    console.log('comment', id);
    await getAllComments(id);
    document.getElementById('sendCommentButton').addEventListener('click', async () => {
        const cmtValue = document.getElementById('commentText').value;
        const myObj = {
            postId: id,
            sender: loginUser,
            content: cmtValue,
        };
        document.getElementById('commentForm').reset();
        //   const user = await fetchUserDataByPostedUser(loginUser);
        //   // console.log("ss",user.name);
        //   const commentCountSize = await fetchCommentSizes(id);
        // let  commentId = commentCountSize.length + 1;
        //    const postedUser = await fetchPostedUser(id);
        //    if(user.staffId === postedUser.staffId) {
        //        const chatArea = document.querySelector('#commentMessageText');
        //        await displayMessage(user.name, cmtValue, user.photo, commentId, id, chatArea);
        //    }
        stompClient.send('/app/comment-message', {}, JSON.stringify(myObj));
    });
};

const resetModalContent = () => {
    const sendCommentButton = document.getElementById('sendCommentButton');
    const newSendCommentButton = sendCommentButton.cloneNode(true);
    sendCommentButton.parentNode.replaceChild(newSendCommentButton, sendCommentButton);
    newSendCommentButton.addEventListener('click', () => {
        const cmtValue = document.getElementById('commentText').value;
        const myObj = {
            postId: id,
            sender: loginUser,
            content: cmtValue,
            date: new Date(),
        };
        document.getElementById('commentForm').reset();
        stompClient.send('/app/comment-message', {}, JSON.stringify(myObj));
    });
};

const commentModal = document.getElementById('commentStaticBox');
commentModal.addEventListener('show.bs.modal', () => {
    console.log('Hello ya p hayy');
    resetModalContent();
});
async function createPostsForSearch(){
    let data = await fetch('/post/searchPost/'+localStorage.getItem('searchInput'))
    let response = await data.json()
    console.log(response)
    let postTab = document.getElementById('pills-post-search')
    let row = ''
    if(response.length < 1){
        postTab.innerHTML = `
        
 
        <div class="card-body font-monospace fs-4" style="padding:30px; margin-left:100px;" >
        <i class="fa-solid fa-question text-danger "></i> No result here <i class="fa-solid fa-magnifying-glass text-secondary"></i>
        </div>
 
        
        `
        return
    }
    for (const p of response) {
        let rows = ''
        let createdTime = await timeAgo(new Date(p.createdDate))
        const reactCount = await fetchSizes(p.id);
        const reactType = await fetchReactType(p.id);
        let likeButtonContent = '';
        if (reactType === "LIKE") {
            likeButtonContent = `<div class="button_icon">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
        <path d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/></svg>
    </div>
            <span  style="color: black;">LIKE ${reactCount.length}</span>`
            ;
        } else if (reactType === "LOVE") {
            likeButtonContent = `<img src="/static/assets/img/love.png" alt="Love" style="width: 25px; height: 25px"/>
               <span>LOVE ${reactCount.length}</span>`;
        } else if (reactType === "CARE") {
            likeButtonContent = `<img src="/static/assets/img/care.png" alt="Care" style="width: 25px; height: 25px" /> 
                      <span>CARE ${reactCount.length}</span>`;
        } else if (reactType === "ANGRY") {
            likeButtonContent = `<img src="/static/assets/img/angry.png" alt="Angry" style="width: 25px; height: 25px" />
                     <span>ANGRY ${reactCount.length}</span>`;
        } else if (reactType === "HAHA") {
            likeButtonContent = `<img src="/static/assets/img/haha.png" alt="Haha" style="width: 25px; height: 25px" />
                  <span>HAHA ${reactCount.length}</span>`;
        } else if (reactType === "SAD") {
            likeButtonContent = `<img src="/static/assets/img/sad.png" alt="Sad" style="width: 25px; height: 25px" /> 
        <span>SAD ${reactCount.length}</span>`;
        } else if (reactType === "WOW") {
            likeButtonContent = `<img src="/static/assets/img/wow.png" alt="Wow" style="width: 25px; height: 25px" /> 
                    <span>WOW ${reactCount.length}</span>`;
        } else {
            likeButtonContent = `<div class="button_icon">
           <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M323.8 34.8c-38.2-10.9-78.1 11.2-89 49.4l-5.7 20c-3.7 13-10.4 25-19.5 35l-51.3 56.4c-8.9 9.8-8.2 25 1.6 33.9s25 8.2 33.9-1.6l51.3-56.4c14.1-15.5 24.4-34 30.1-54.1l5.7-20c3.6-12.7 16.9-20.1 29.7-16.5s20.1 16.9 16.5 29.7l-5.7 20c-5.7 19.9-14.7 38.7-26.6 55.5c-5.2 7.3-5.8 16.9-1.7 24.9s12.3 13 21.3 13L448 224c8.8 0 16 7.2 16 16c0 6.8-4.3 12.7-10.4 15c-7.4 2.8-13 9-14.9 16.7s.1 15.8 5.3 21.7c2.5 2.8 4 6.5 4 10.6c0 7.8-5.6 14.3-13 15.7c-8.2 1.6-15.1 7.3-18 15.2s-1.6 16.7 3.6 23.3c2.1 2.7 3.4 6.1 3.4 9.9c0 6.7-4.2 12.6-10.2 14.9c-11.5 4.5-17.7 16.9-14.4 28.8c.4 1.3 .6 2.8 .6 4.3c0 8.8-7.2 16-16 16H286.5c-12.6 0-25-3.7-35.5-10.7l-61.7-41.1c-11-7.4-25.9-4.4-33.3 6.7s-4.4 25.9 6.7 33.3l61.7 41.1c18.4 12.3 40 18.8 62.1 18.8H384c34.7 0 62.9-27.6 64-62c14.6-11.7 24-29.7 24-50c0-4.5-.5-8.8-1.3-13c15.4-11.7 25.3-30.2 25.3-51c0-6.5-1-12.8-2.8-18.7C504.8 273.7 512 257.7 512 240c0-35.3-28.6-64-64-64l-92.3 0c4.7-10.4 8.7-21.2 11.8-32.2l5.7-20c10.9-38.2-11.2-78.1-49.4-89zM32 192c-17.7 0-32 14.3-32 32V448c0 17.7 14.3 32 32 32H96c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32H32z"/></svg>
            </div>
            <span>Like ${reactCount.length}</span>`
        }
        const commentCountSize = await fetchCommentSizes(p.id);
        rows += `
     
        <div class="post">
        <div class="post-top">
            <div class="dp">
                <img src="${p.user.photo} " alt="">
            </div>
            <div class="post-info">
                <p class="name">${p.user.name} </p>
                <span class="time">${createdTime}</span>
            </div>
                        <div class="dropdown offset-8">
      <a class=" dropdown-toggle" onclick="getPostDetail(${p.id})" href="#"   id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fas fa-ellipsis-h "></i>
            </a>
    
      <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
        <li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#editModalBox"  >Edit</a></li>
        <li><a class="dropdown-item" onclick="deletePost(${p.id})">Delete Post</a></li> 
      </ul>
    </div>
        </div> 
        <div class=" post-content" data-bs-toggle="modal" data-bs-target="#searchPost${p.id}" > 
        ${p.description.replace(/\n/g, '<br>')}
        `
        let oneTag = null
        let oneCloseTag = null
        let twoTag = null
        let twoCloseTag = null
        let threeTag = null
        let threeCloseTag = null
        let fourTag = null
        let fourCloseTag = null
        let fiveTag = null
        let fiveCloseTag = null
        let oneControlAttr = null
        let twoControlAttr = null
        let threeControlAttr = null
        let fourControlAttr = null
        let fiveControlAttr = null
        let one = null
        let two = null
        let three = null
        let four = null
        let five = null
        let six = null

        if(p.resources.length === 1){
            p.resources.forEach((r, index) => {
                if(index === 0 && r.photo !== null){
                    console.log('two')
                    one = r.photo
                    oneTag = 'img'
                    oneCloseTag = ''
                    oneControlAttr = ''
                }else if(index === 0 && r.video !== null){
                    one = r.video
                    oneTag = 'video'
                    oneCloseTag = '</video>'
                    oneControlAttr = 'controls'
                }
                if (one !== null  ) {
                    rows+= `
    <div class="d-flex" > 
    <${oneTag} ${oneControlAttr} src="${one}" class="img-fluid " style="width:500px; border-radius : 5px; height:500px;  " alt="">${oneCloseTag}
    </div>
    `
                }
            })
        }
        if(p.resources.length === 2){
            p.resources.forEach((r, index) => {
                if(index === 0 && r.photo !== null){
                    console.log('two')
                    one = r.photo
                    oneTag = 'img'
                    oneCloseTag = ''
                    oneControlAttr = ''
                }else if(index === 0 && r.video !== null){
                    one = r.video
                    oneTag = 'video'
                    oneCloseTag = '</video>'
                    oneControlAttr = 'controls'
                }
                if(index === 1 && r.photo !== null){
                    two = r.photo
                    twoTag = 'img'
                    twoCloseTag = ''
                    twoControlAttr = ''
                }else if(index === 1 && r.video !== null){
                    two = r.video
                    twoTag = 'video'
                    twoCloseTag = '</video>'
                    twoControlAttr = 'controls'
                }
                if (one !== null && two !== null  ) {
                    rows+= `
    <div class="d-flex" > 
    <${oneTag} ${oneControlAttr} src="${one}" class="img-fluid " style="width:250px; border-radius : 5px; height:400px; margin:2px" alt="">${oneCloseTag}
    <${twoTag} ${twoControlAttr} src="${two}" class="img-fluid " style="width:250px; border-radius : 5px; height:400px; margin:2px" alt="">${twoCloseTag} 
    </div> `
                }
            })
        }
        if(p.resources.length === 3){
            p.resources.forEach((r, index) => {
                if(index === 0 && r.photo !== null){
                    console.log('two')
                    one = r.photo
                    oneTag = 'img'
                    oneCloseTag = ''
                    oneControlAttr = ''
                }else if(index === 0 && r.video !== null){
                    one = r.video
                    oneTag = 'video'
                    oneCloseTag = '</video>'
                    oneControlAttr = 'controls'
                }
                if(index === 1 && r.photo !== null){
                    two = r.photo
                    twoTag = 'img'
                    twoCloseTag = ''
                    twoControlAttr = ''
                }else if(index === 1 && r.video !== null){
                    two = r.video
                    twoTag = 'video'
                    twoCloseTag = '</video>'
                    twoControlAttr = 'controls'
                }
                if(index === 2 && r.photo !== null){
                    three = r.photo
                    threeTag = 'img'
                    threeCloseTag = ''
                    threeControlAttr = ''
                }else if(index === 2 && r.video !== null){
                    three = r.video
                    threeTag = 'video'
                    threeCloseTag = '</video>'
                    threeControlAttr = 'controls'
                }
                if (one !== null && two !== null && three !== null  ) {
                    rows+= `
    <div class="d-flex" > 
    <${oneTag} ${oneControlAttr} src="${one}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${oneCloseTag}
    <${twoTag} ${twoControlAttr} src="${two}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${twoCloseTag} 
    </div>
    <div class="d-flex"> 
    <${threeTag} ${threeControlAttr} src="${three}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin-left:127px" alt="">${threeCloseTag} 
    </div>`
                }
            })
        }
        if(p.resources.length === 4){
            p.resources.forEach((r, index) => {
                console.log(r)

                if(index === 0 && r.photo !== null){
                    console.log('two')
                    one = r.photo
                    oneTag = 'img'
                    oneCloseTag = ''
                    oneControlAttr = ''
                }else if(index === 0 && r.video !== null){
                    one = r.video
                    oneTag = 'video'
                    oneCloseTag = '</video>'
                    oneControlAttr = 'controls'
                }
                if(index === 1 && r.photo !== null){
                    two = r.photo
                    twoTag = 'img'
                    twoCloseTag = ''
                    twoControlAttr = ''
                }else if(index === 1 && r.video !== null){
                    two = r.video
                    twoTag = 'video'
                    twoCloseTag = '</video>'
                    twoControlAttr = 'controls'
                }
                if(index === 2 && r.photo !== null){
                    three = r.photo
                    threeTag = 'img'
                    threeCloseTag = ''
                    threeControlAttr = ''
                }else if(index === 2 && r.video !== null){
                    three = r.video
                    threeTag = 'video'
                    threeCloseTag = '</video>'
                    threeControlAttr = 'controls'
                }
                if(index === 3 && r.photo !== null){
                    four = r.photo
                    fourTag = 'img'
                    fourCloseTag = ''
                    fourControlAttr = ''
                }else if(index === 3 && r.video !== null){
                    four = r.video
                    fourTag = 'video'
                    fourCloseTag = '</video>'
                    fourControlAttr = 'controls'
                }


                if (one !== null && two !== null && three !== null && four !== null) {
                    rows+= `
        <div class="d-flex" > 
        <${oneTag} ${oneControlAttr} src="${one}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${oneCloseTag}
        <${twoTag} ${twoControlAttr} src="${two}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${twoCloseTag} 
        </div>
        <div class="d-flex"> 
        <${threeTag} ${threeControlAttr} src="${three}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${threeCloseTag}
        <${fourTag} ${fourControlAttr} src="${four}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px;  opacity: 20%" alt="">${fourCloseTag}
        </div>`
                }
            })

        }

        if(p.resources.length > 4 ){
            let text = p.resources.length -4
            console.log(text)
            p.resources.forEach((r, index) => {
                if(index === 0 && r.photo !== null){
                    console.log('two')
                    one = r.photo
                    oneTag = 'img'
                    oneCloseTag = ''
                    oneControlAttr = ''
                }else if(index === 0 && r.video !== null){
                    one = r.video
                    oneTag = 'video'
                    oneCloseTag = '</video>'
                    oneControlAttr = 'controls'
                }
                if(index === 1 && r.photo !== null){
                    two = r.photo
                    twoTag = 'img'
                    twoCloseTag = ''
                    twoControlAttr = ''
                }else if(index === 1 && r.video !== null){
                    two = r.video
                    twoTag = 'video'
                    twoCloseTag = '</video>'
                    twoControlAttr = 'controls'
                }
                if(index === 2 && r.photo !== null){
                    three = r.photo
                    threeTag = 'img'
                    threeCloseTag = ''
                    threeControlAttr = ''
                }else if(index === 2 && r.video !== null){
                    three = r.video
                    threeTag = 'video'
                    threeCloseTag = '</video>'
                    threeControlAttr = 'controls'
                }
                if(index === 3 && r.photo !== null){
                    four = r.photo
                    fourTag = 'img'
                    fourCloseTag = ''
                    fourControlAttr = ''
                }else if(index === 3 && r.video !== null){
                    four = r.video
                    fourTag = 'video'
                    fourCloseTag = '</video>'
                    fourControlAttr = 'controls'
                }
                if(index === 4 && r.photo !== null){
                    five = r.photo
                    fiveTag = 'img'
                    fiveCloseTag = ''
                    fiveControlAttr = ''
                }else if(index === 4 && r.video !== null){
                    five = r.video
                    fiveTag = 'video'
                    fiveCloseTag = '</video>'
                    fiveControlAttr = 'controls'
                }

                if(index === 5 ){
                    six = 'hello'
                }

                if (one !== null && two !== null && three !== null && four !== null && five !== null && six === null) {

                    rows+= `
        <div class="d-flex" > 
        <${oneTag} ${oneControlAttr} src="${one}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${oneCloseTag}
        <${twoTag} ${twoControlAttr} src="${two}" class="img-fluid " style="width:250px; border-radius : 5px; height:200px; margin:2px" alt="">${twoCloseTag} 
        </div>
        <div class="d-flex"> 
        <${threeTag} ${threeControlAttr} src="${three}" class="img-fluid " style="width:166px; border-radius : 5px; height:200px; margin:2px" alt="">${threeCloseTag}
        <${fourTag} ${fourControlAttr} src="${four}" class="img-fluid " style="width:166px; border-radius : 5px; height:200px; margin:2px" alt="">${fourCloseTag}
        <div style="position: relative; display: inline-block;">
        <${fiveTag} ${fiveControlAttr} src="${five}" class="img-fluid" style="width:166px; border-radius : 5px; height:200px; margin:2px" alt="">${fiveCloseTag}
        <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); color: white; font-size: 25px;">+${text}</div>
        </div>
        </div>`
                }




            })




        }


        rows+= ` 
        </div>
        <div class="post-bottom">
            <div class="action" style="height: 50px">
    <div class="button_wrapper">
          <div class="all_likes_wrapper">
              <div data-title="LIKE">
                  <img src="/static/assets/img/like.png" alt="Like" />
              </div>
              <div data-title="LOVE">
                  <img src="/static/assets/img/love.png" alt="Love" />
              </div>
              <div data-title="CARE">
                  <img src="/static/assets/img/care.png" alt="Care" />
              </div>
              <div data-title="HAHA">
                  <img src="/static/assets/img/haha.png" alt="Haha" />
              </div>
              <div data-title="WOW">
                  <img src="/static/assets/img/wow.png" alt="Wow" />
              </div>
              <div data-title="SAD">
                  <img src="/static/assets/img/sad.png" alt="Sad" />
              </div>
              <div data-title="ANGRY">
                  <img src="/static/assets/img/angry.png" alt="Angry" />
              </div>
          </div>
        <button class="like_button" id="${p.id}">
          ${likeButtonContent}
        </button>
      </div>
            </div>
            <div class="action">
                <i class="fa-regular fa-comment"></i>
 <span onclick="pressedComment('${p.id}')"  data-bs-toggle="modal" data-bs-target="#commentStaticBox" id="commentCountStaticBox-${p.id}">Comment ${commentCountSize}</span>            </div>
            <div class="action">
                <i class="fa fa-share"></i>
                <span>Share</span>
            </div>
        </div>
    </div>
     
    <div class="modal fade" id="searchPost${p.id}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg"  >
      <div class="modal-content" style=" background-color:transparent;  overflow-y: hidden;"> 
        <div class="modal-body p-0">
          <div id="carouselExampleControlsPostSearch${p.id}" class="carousel slide" data-bs-ride="carousel">
            <div class="carousel-inner">`

        p.resources.forEach((r, index) => {
            let active = index == 0 ? 'active' : ''
            if (r.photo === null && r.video !== null) {
                rows += ` <div   class="carousel-item ${active}" style="object-fit: cover; width:100%; height : 400px;" > 
                <video controls  src="${r.video}" class="d-block  carousel-image " style="object-fit: cover; width:100%; height : 400px;"alt="..."></video>
                <div class="carousel-caption d-none d-md-block"> 
                <p>${r.description.replace(/\n/g, '<br>')}</p>
              </div>
                </div> `
            } else if (r.video === null && r.photo !== null) {
                rows += `<div    class="carousel-item ${active}" style="object-fit: cover; width:100%; height : 400px;"> 
                <img src="${r.photo}"   class="d-block  carousel-image " style="object-fit: cover; width:100%; height : 400px;" alt="...">
                <div class="carousel-caption d-none d-md-block"> 
                <p>${r.description.replace(/\n/g, '<br>')}</p>
              </div>
              </div>`
            } else {
                rows += `<div    class="carousel-item ${active}" style="object-fit: cover; width:100%; height : 400px;"> 
                <video controls src="${r.video}" class="d-block  carousel-image " style="object-fit: cover; width:100%; height : 400px;" alt="..."></video>
                <div class="carousel-caption d-none d-md-block"> 
                <p>${r.description.replace(/\n/g, '<br>')}</p>
              </div>
              </div>`
                rows += `<div    class="carousel-item ${active}" style="object-fit: cover; width:100%; height : 400px;"> 
              <img src="${r.photo}"class="d-block  carousel-image " style="object-fit: cover; width:100%; height : 400px;"alt="...">
              <div class="carousel-caption d-none d-md-block"> 
              <p>${r.description.replace(/\n/g, '<br>')}</p>
            </div>
            </div>
             `
            }
        })
        rows+=`
               
            <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleControlsPostSearch${p.id}" data-bs-slide="prev">
              <span class="carousel-control-prev-icon" aria-hidden="true"></span>
              <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleControlsPostSearch${p.id}" data-bs-slide="next">
              <span class="carousel-control-next-icon" aria-hidden="true"></span>
              <span class="visually-hidden">Next</span>
            </button>
          </div>
        </div> 
      </div>
    </div>
    </div>
    </div>
    
       `
        row += rows
    }
    postTab.innerHTML = row
    const likeButtons = document.querySelectorAll(".like_button");
    likeButtons.forEach(likeButton => {
        likeButton.addEventListener('click', async (event) => {
            const postId = likeButton.id;
            const currentReactType = await fetchReactType(postId);
            console.log('sdd', currentReactType);
            if ((currentReactType !== "OTHER") || (currentReactType === null)) {
                await removeReaction(postId);
                const reactCount = await fetchSizes(postId);
                likeButton.innerHTML = `<div class="button_icon">
           <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M323.8 34.8c-38.2-10.9-78.1 11.2-89 49.4l-5.7 20c-3.7 13-10.4 25-19.5 35l-51.3 56.4c-8.9 9.8-8.2 25 1.6 33.9s25 8.2 33.9-1.6l51.3-56.4c14.1-15.5 24.4-34 30.1-54.1l5.7-20c3.6-12.7 16.9-20.1 29.7-16.5s20.1 16.9 16.5 29.7l-5.7 20c-5.7 19.9-14.7 38.7-26.6 55.5c-5.2 7.3-5.8 16.9-1.7 24.9s12.3 13 21.3 13L448 224c8.8 0 16 7.2 16 16c0 6.8-4.3 12.7-10.4 15c-7.4 2.8-13 9-14.9 16.7s.1 15.8 5.3 21.7c2.5 2.8 4 6.5 4 10.6c0 7.8-5.6 14.3-13 15.7c-8.2 1.6-15.1 7.3-18 15.2s-1.6 16.7 3.6 23.3c2.1 2.7 3.4 6.1 3.4 9.9c0 6.7-4.2 12.6-10.2 14.9c-11.5 4.5-17.7 16.9-14.4 28.8c.4 1.3 .6 2.8 .6 4.3c0 8.8-7.2 16-16 16H286.5c-12.6 0-25-3.7-35.5-10.7l-61.7-41.1c-11-7.4-25.9-4.4-33.3 6.7s-4.4 25.9 6.7 33.3l61.7 41.1c18.4 12.3 40 18.8 62.1 18.8H384c34.7 0 62.9-27.6 64-62c14.6-11.7 24-29.7 24-50c0-4.5-.5-8.8-1.3-13c15.4-11.7 25.3-30.2 25.3-51c0-6.5-1-12.8-2.8-18.7C504.8 273.7 512 257.7 512 240c0-35.3-28.6-64-64-64l-92.3 0c4.7-10.4 8.7-21.2 11.8-32.2l5.7-20c10.9-38.2-11.2-78.1-49.4-89zM32 192c-17.7 0-32 14.3-32 32V448c0 17.7 14.3 32 32 32H96c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32H32z"/></svg>
                </div>
                <span>Like ${reactCount.length}</span>`;
            } else {
                await pressedLike(postId, "LIKE");
                await new Promise(resolve => setTimeout(resolve, 200));
                const reactCount = await fetchSizes(postId);
                likeButton.innerHTML = `<div class="button_icon">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
        <path d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/></svg>
            </div>
            <span>Like ${reactCount.length}</span>`
                likeButton.classList.toggle('active');

                if (likeButton.classList.contains('active')) {
                    likeButton.style.color = "black";
                } else {
                    likeButton.classList.remove('active');
                    likeButton.style.color = "unset";
                }
            }
        });

        likeButton.addEventListener('mouseover', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper").classList.add('active');
        });

        likeButton.addEventListener('mouseout', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper").classList.remove('active');
        });

        likeButton.parentNode.addEventListener('mouseover', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper").classList.add('active');
        });

        likeButton.parentNode.addEventListener('mouseout', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper").classList.remove('active');
        });

        likeButton.parentNode.querySelectorAll('div').forEach((like_image) => {
            like_image.addEventListener('click', async (event) => {
                let dataTitle = event.currentTarget.dataset.title;
                const postId = likeButton.id;
                await pressedLike(postId, dataTitle);
                await new Promise(resolve => setTimeout(resolve, 200));
                const reactCount = await fetchSizes(postId);
                if (dataTitle === "LIKE") {
                    likeButton.innerHTML = `<div class="button_icon">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
        <path d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/></svg>
                        </div>
                        <span>Like ${reactCount.length}</span>`;

                    likeButton.classList.add("active")``
                } else {
                    likeButton.innerHTML = `<img src="/static/assets/img/${dataTitle.toLowerCase()}.png" style="width: 20px; height: 20px" /> ${dataTitle} ${reactCount.length}`;
                }

                if (dataTitle === "LIKE") {
                    likeButton.style.color = "black";
                } else if (dataTitle === "LOVE") {
                    likeButton.style.color = "#EC2D50";
                } else if (dataTitle === "CARE" || dataTitle === 'HAHA' || dataTitle === "WOW" || dataTitle === "SAD") {
                    likeButton.style.color = "#FAC551";
                } else {
                    likeButton.style.color = "#E24E05";
                }

                likeButton.parentNode.querySelector(".all_likes_wrapper").classList.remove("active");
            });
            like_image.addEventListener('click', (event) => {
                event.stopPropagation();
            });
        });
    });
    
    document.getElementById('searchInput').value = localStorage.getItem('searchInput')+''
}

async function dateFormatter(startDate){
    let startDay = startDate.getDate()
    let startMonth = startDate.getMonth()+1
    let startYear = startDate.getFullYear()
    let startHours = startDate.getHours()
    let startMinutes = startDate.getMinutes()
    let formattedStartDate = `${startDay}/${startMonth}/${startYear} ${startHours}:${startMinutes}`;
    return new Promise((resolve) => {
        resolve(formattedStartDate);
    });
}

const fetchReactCountForEvent =async (id) => {
    const sizeData = await fetch(`/event-react-type/${id}`);
    const eventReactCount = await sizeData.json();
    return eventReactCount;
}

const fetchReactTypeForEvent = async (id) => {
    const dataType = await fetch(`/event-user-react-type/${id}`);
    const eventReactType = await dataType.json();
    return eventReactType;
}

const fetchUserDataByPostedUser = async (id) => {
    const fetchUserData = await fetch(`/get-userData/${id}`);
    if (!fetchUserData.ok) {
        alert('Invalid user');
    }
    const userDataForAll = await fetchUserData.json();
    return userDataForAll;
};

async function showSearchEvents(input){
    let data = await fetch('/event/searchEvent/'+input,{
        method : 'GET'
    })
    let response = await data.json()
    if(response.length<1){
        document.getElementById('pills-event-search').innerHTML =  `
        
       
        <div class="card-body font-monospace fs-4" style="padding:30px; margin-left:100px;" >
        <i class="fa-solid fa-question text-danger "></i> No result here <i class="fa-solid fa-magnifying-glass text-secondary"></i>
        </div>
       
        
        `
        return
    }
    console.log(response)
    let row = ''
    for (const r of response) {
        const reactCountForEvent = await fetchReactCountForEvent(r.id);
        console.log('length',reactCountForEvent.length);
        let createdTime = await timeAgo(new Date(r.created_date))
        const reactTypeForEvent = await fetchReactTypeForEvent(r.id);
        let likeButtonContent = '';
        if (reactTypeForEvent === "LIKE") {
            likeButtonContent = `<div class="button_icon1">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
        <path d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/></svg>
    </div>
            <span  style="color: black;">LIKE ${reactCountForEvent.length}</span>`
            ;
        } else if (reactTypeForEvent === "LOVE") {
            likeButtonContent = `<img src="/static/assets/img/love.png" alt="Love" style="width: 20px; height: 20px"/>   
               <span>LOVE ${reactCountForEvent.length}</span>`;
        } else if (reactTypeForEvent === "CARE") {
            likeButtonContent = `<img src="/static/assets/img/care.png" alt="Care" style="width: 20px; height: 20px" /> 
                      <span>CARE ${reactCountForEvent.length}</span>`;
        } else if (reactTypeForEvent === "ANGRY") {
            likeButtonContent = `<img src="/static/assets/img/angry.png" alt="Angry" style="width: 20px; height: 20px" />
                     <span>ANGRY ${reactCountForEvent.length}</span>`;
        } else if (reactTypeForEvent === "HAHA") {
            likeButtonContent = `<img src="/static/assets/img/haha.png" alt="Haha" style="width: 20px; height: 20px" />
                  <span>HAHA ${reactCountForEvent.length}</span>`;
        } else if (reactTypeForEvent === "SAD") {
            likeButtonContent = `<img src="/static/assets/img/sad.png" alt="Sad" style="width: 20px; height: 20px" /> 
        <span>SAD ${reactCountForEvent.length}</span>`;
        } else if (reactTypeForEvent === "WOW") {
            likeButtonContent = `<img src="/static/assets/img/wow.png" alt="Wow" style="width: 20px; height: 20px" /> 
                    <span>WOW ${reactCountForEvent.length}</span>`;
        } else {
            likeButtonContent = `<div class="button_icon1">
           <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M323.8 34.8c-38.2-10.9-78.1 11.2-89 49.4l-5.7 20c-3.7 13-10.4 25-19.5 35l-51.3 56.4c-8.9 9.8-8.2 25 1.6 33.9s25 8.2 33.9-1.6l51.3-56.4c14.1-15.5 24.4-34 30.1-54.1l5.7-20c3.6-12.7 16.9-20.1 29.7-16.5s20.1 16.9 16.5 29.7l-5.7 20c-5.7 19.9-14.7 38.7-26.6 55.5c-5.2 7.3-5.8 16.9-1.7 24.9s12.3 13 21.3 13L448 224c8.8 0 16 7.2 16 16c0 6.8-4.3 12.7-10.4 15c-7.4 2.8-13 9-14.9 16.7s.1 15.8 5.3 21.7c2.5 2.8 4 6.5 4 10.6c0 7.8-5.6 14.3-13 15.7c-8.2 1.6-15.1 7.3-18 15.2s-1.6 16.7 3.6 23.3c2.1 2.7 3.4 6.1 3.4 9.9c0 6.7-4.2 12.6-10.2 14.9c-11.5 4.5-17.7 16.9-14.4 28.8c.4 1.3 .6 2.8 .6 4.3c0 8.8-7.2 16-16 16H286.5c-12.6 0-25-3.7-35.5-10.7l-61.7-41.1c-11-7.4-25.9-4.4-33.3 6.7s-4.4 25.9 6.7 33.3l61.7 41.1c18.4 12.3 40 18.8 62.1 18.8H384c34.7 0 62.9-27.6 64-62c14.6-11.7 24-29.7 24-50c0-4.5-.5-8.8-1.3-13c15.4-11.7 25.3-30.2 25.3-51c0-6.5-1-12.8-2.8-18.7C504.8 273.7 512 257.7 512 240c0-35.3-28.6-64-64-64l-92.3 0c4.7-10.4 8.7-21.2 11.8-32.2l5.7-20c10.9-38.2-11.2-78.1-49.4-89zM32 192c-17.7 0-32 14.3-32 32V448c0 17.7 14.3 32 32 32H96c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32H32z"/></svg>
            </div>
            <span>Like ${reactCountForEvent.length}</span>`
        }
        let expired = ''
        if(new Date()>new Date(r.end_date)){
            expired=`
                
    <div class="alert alert-danger d-flex align-items-center" style=" width:265px; position: absolute; top: 135px;  z-index: 1;" role="alert">
    <svg class="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Danger:"><use xlink:href="#exclamation-triangle-fill"/></svg>
    <div>
    <i class="fa-solid fa-triangle-exclamation"></i>
    EVENT IS EXPIRED
    </div>
    </div>
                
                `
        }
        let photo = r.photo === null ? '/static/assets/img/eventImg.jpg' : r.photo
        let startDate = new Date(r.start_date)
        let startDay = startDate.getDate()
        let startMonth = startDate.getMonth()+1
        let startYear = startDate.getFullYear()
        let startHours = startDate.getHours()
        let startMinutes = startDate.getMinutes()
        let endDate = new Date(r.end_date)
        let endDay = endDate.getDate()
        let endMonth = endDate.getMonth()+1
        let endYear = endDate.getFullYear()
        let endHours = endDate.getHours()
        let endMinutes = endDate.getMinutes()
        let formattedStartDate = `${startDay} / ${startMonth} / ${startYear}  `;
        let formattedEndDate = `${endDay} / ${endMonth} / ${endYear}  `
        let rows =''
        rows += `
        
        <div class="post">
        <div class="post-top">
            <div class="dp">
                <img src="${r.user.photo}" alt="">
            </div>
            <div class="post-info">
                <p class="name">${r.user.name}</p>
                <span class="time">${createdTime}</span>
            </div>
                        <div class="dropdown offset-8">
      <a class=" dropdown-toggle" onclick="getEventDetail(${r.id})" href="#"   id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fas fa-ellipsis-h "></i>
            </a>
    
      <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
        <li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#eventEditModalBox">Edit</a></li>
        <li><a class="dropdown-item" onclick="deleteEvent(${r.id})">Delete Post</a></li> 
      </ul>
    </div>
        </div> 
        <div class=" post-content" data-bs-toggle="modal" data-bs-target="#searchPost" > 
        <div class="card" style="width: 30rem;  margin-left:12px ;"> 
  <div class="card-body">
    <h5 class="card-title">${r.title}</h5> 
    <div class="d-flex align-items-start">
    <div class="nav flex-column nav-pills me-3" id="v-pills-tab" role="tablist" aria-orientation="vertical">
    <button class="nav-link active  " id="v-${r.id}-photo-tab" data-bs-toggle="pill" data-bs-target="#v-${r.id}-photo" type="button" role="tab" aria-controls="v-${r.id}-photo" aria-selected="true"><i class="fa-solid fa-image"></i>  </button>
      <button class="nav-link  " id="v-${r.id}-about-tab" data-bs-toggle="pill" data-bs-target="#v-${r.id}-about" type="button" role="tab" aria-controls="v-${r.id}-about" aria-selected="false"><i class="fa-solid fa-circle-info"></i>  </button>
      <button class="nav-link" id="v-${r.id}-location-tab" data-bs-toggle="pill" data-bs-target="#v-${r.id}-location" type="button" role="tab" aria-controls="v-${r.id}-location" aria-selected="false"><i class="fa-solid fa-location-dot"></i> </button>
      <button class="nav-link" id="v-${r.id}-date-tab" data-bs-toggle="pill" data-bs-target="#v-${r.id}-date" type="button" role="tab" aria-controls="v-${r.id}-date" aria-selected="false"><i class="fa-solid fa-clock"></i></button> 
    </div>
    <div class="tab-content" id="v-${r.id}-tabContent">
      <div class="tab-pane fade" id="v-${r.id}-about" role="tabpanel" aria-labelledby="v-${r.id}-about-tab">
      <div class="text-center lh-sm font-monospace">
      ${r.description}
      </div>
      </div>
      <div class="tab-pane fade" id="v-${r.id}-location" role="tabpanel" aria-labelledby="v-${r.id}-location-tab">
      <div class="text-center fs-5 fw-bold fst-italic font-monospace ml-3">
      <div class="ml-5  mt-5 text-danger">${r.location}</div>
      </div>
      </div>
      <div class="tab-pane fade" id="v-${r.id}-date" role="tabpanel" aria-labelledby="v-${r.id}-date-tab">
      <div class="text-center fs-6 fw-bold mt-3  pl-2 font-monospace">
      <div class="mt-2 ml-5 d-flex"><div class="text-secondary"> START </div> <i class="fa-solid fa-play text-danger mx-1"></i></br></div><div class="fst-italic"> ${formattedStartDate}</div></br>
      <div class="mt-2 ml-5 d-flex"><div class="text-secondary"> END </div> <i class="fa-solid fa-play text-danger mx-1"></i><br></div><div class="fst-italic"> ${formattedEndDate}</div>
       </div>
       </div>
       <div class="tab-pane fade show active" id="v-${r.id}-photo" role="tabpanel" aria-labelledby="v-${r.id}-photo-tab"> 
       ${expired}
       <b class="p-2"><img src="${r.photo}" style="width:250px; height:200px; border-radius:20px; position:relative;">
       </div>  
  </div>
  </div>
</div>
        </div>
         <div class="post-bottom" style="justify-content: center; margin-top: -40px">
        <div class="button_wrapper1">
            <div class="all_likes_wrapper1">
                <div data-title="LIKE">
                    <img src="/static/assets/img/like.png" alt="Like" />
                </div>
                <div data-title="LOVE">
                    <img src="/static/assets/img/love.png" alt="Love" />
                </div>
                <div data-title="CARE">
                    <img src="/static/assets/img/care.png" alt="Care" />
                </div>
                <div data-title="HAHA">
                    <img src="/static/assets/img/haha.png" alt="Haha" />
                </div>
                <div data-title="WOW">
                    <img src="/static/assets/img/wow.png" alt="Wow" />
                </div>
                <div data-title="SAD">
                    <img src="/static/assets/img/sad.png" alt="Sad" />
                </div>
                <div data-title="ANGRY">
                    <img src="/static/assets/img/angry.png" alt="Angry" />
                </div>
            </div>
            <button class="like_button1" id="${r.id}">
                ${likeButtonContent}
            </button>
        </div>
</div>
                </div>`;

        row += rows
    }
    document.getElementById('pills-event-search').innerHTML = row
    const likeButtons = document.querySelectorAll(".like_button1");
    likeButtons.forEach(likeButton => {
        likeButton.addEventListener('click', async (event) => {
            const eventId = likeButton.id;
            const currentReactType = await fetchReactTypeForEvent(eventId);
            console.log('sdd', currentReactType);
            if ((currentReactType !== "OTHER") || (currentReactType === null)) {
                await removeReactionForEvent(eventId);
                const reactCountForButtonEvent = await fetchReactCountForEvent(eventId);
                console.log('FOr other',reactCountForButtonEvent.length)
                console.log("Press previous icon")
                likeButton.innerHTML = `<div class="button_icon1">
           <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M323.8 34.8c-38.2-10.9-78.1 11.2-89 49.4l-5.7 20c-3.7 13-10.4 25-19.5 35l-51.3 56.4c-8.9 9.8-8.2 25 1.6 33.9s25 8.2 33.9-1.6l51.3-56.4c14.1-15.5 24.4-34 30.1-54.1l5.7-20c3.6-12.7 16.9-20.1 29.7-16.5s20.1 16.9 16.5 29.7l-5.7 20c-5.7 19.9-14.7 38.7-26.6 55.5c-5.2 7.3-5.8 16.9-1.7 24.9s12.3 13 21.3 13L448 224c8.8 0 16 7.2 16 16c0 6.8-4.3 12.7-10.4 15c-7.4 2.8-13 9-14.9 16.7s.1 15.8 5.3 21.7c2.5 2.8 4 6.5 4 10.6c0 7.8-5.6 14.3-13 15.7c-8.2 1.6-15.1 7.3-18 15.2s-1.6 16.7 3.6 23.3c2.1 2.7 3.4 6.1 3.4 9.9c0 6.7-4.2 12.6-10.2 14.9c-11.5 4.5-17.7 16.9-14.4 28.8c.4 1.3 .6 2.8 .6 4.3c0 8.8-7.2 16-16 16H286.5c-12.6 0-25-3.7-35.5-10.7l-61.7-41.1c-11-7.4-25.9-4.4-33.3 6.7s-4.4 25.9 6.7 33.3l61.7 41.1c18.4 12.3 40 18.8 62.1 18.8H384c34.7 0 62.9-27.6 64-62c14.6-11.7 24-29.7 24-50c0-4.5-.5-8.8-1.3-13c15.4-11.7 25.3-30.2 25.3-51c0-6.5-1-12.8-2.8-18.7C504.8 273.7 512 257.7 512 240c0-35.3-28.6-64-64-64l-92.3 0c4.7-10.4 8.7-21.2 11.8-32.2l5.7-20c10.9-38.2-11.2-78.1-49.4-89zM32 192c-17.7 0-32 14.3-32 32V448c0 17.7 14.3 32 32 32H96c17.7 0 32-14.3 32-32V224c0-17.7-14.3-32-32-32H32z"/></svg>
                </div>
                <span>Like ${reactCountForButtonEvent.length}</span>`;
            } else {
                await pressedLikeForEvent(eventId, "LIKE");
                await new Promise(resolve => setTimeout(resolve, 200));
                const reactCountForButtonEvent = await fetchReactCountForEvent(eventId);
                console.log('FOr Like',reactCountForButtonEvent.length)
                console.log("Press Like Button for event")
                likeButton.innerHTML = `<div class="button_icon1">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
        <path d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/></svg>
            </div>
            <span>Like ${reactCountForButtonEvent.length}</span>`
                likeButton.classList.toggle('active');

                if (likeButton.classList.contains('active')) {
                    likeButton.style.color = "black";
                } else {
                    likeButton.classList.remove('active');
                    likeButton.style.color = "unset";
                }
            }
        });

        likeButton.addEventListener('mouseover', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper1").classList.add('active');
        });

        likeButton.addEventListener('mouseout', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper1").classList.remove('active');
        });

        likeButton.parentNode.addEventListener('mouseover', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper1").classList.add('active');
        });

        likeButton.parentNode.addEventListener('mouseout', () => {
            likeButton.parentNode.querySelector(".all_likes_wrapper1").classList.remove('active');
        });

        likeButton.parentNode.querySelectorAll('div').forEach((like_image1) => {
            like_image1.addEventListener('click', async (event) => {
                let dataTitle = event.currentTarget.dataset.title;
                const eventId = likeButton.id;
                await pressedLikeForEvent(eventId, dataTitle);
                await new Promise(resolve => setTimeout(resolve, 200));
                const reactCountForButtonEvent = await fetchReactCountForEvent(eventId);
                if (dataTitle === "LIKE") {
                    likeButton.innerHTML = `<div class="button_icon1">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
        <path d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/></svg>
                        </div>
                        <span>Like ${reactCountForButtonEvent.length}</span>`;

                    likeButton.classList.add("active")
                } else {
                    likeButton.innerHTML = `<img src="/static/assets/img/${dataTitle.toLowerCase()}.png" style="width: 20px; height: 20px" /> ${dataTitle} ${reactCountForButtonEvent.length}`;
                }

                if (dataTitle === "LIKE") {
                    likeButton.style.color = "black";
                } else if (dataTitle === "LOVE") {
                    likeButton.style.color = "#EC2D50";
                } else if (dataTitle === "CARE" || dataTitle === 'HAHA' || dataTitle === "WOW" || dataTitle === "SAD") {
                    likeButton.style.color = "#FAC551";
                } else {
                    likeButton.style.color = "#E24E05";
                }

                likeButton.parentNode.querySelector(".all_likes_wrapper1").classList.remove("active");
            });
            like_image1.addEventListener('click', (event) => {
                event.stopPropagation();
            });
        });
    });
}



async function getEventDetail(id){
    let data = await fetch('/event/getEventById/'+id,{
        method : 'GET'
    })
    let response = await data.json()
    let startDate = new Date(response.start_date)
    let startDay = startDate.getDate()
    let startMonth = startDate.getMonth()+1
    let startYear = startDate.getFullYear()
    let startHours = startDate.getHours()
    let startMinutes = startDate.getMinutes()
    let formattedStartDate = `${startDay} : ${startMonth} : ${startYear} `;
    let endDate = new Date(response.end_date)
    let endDay = endDate.getDate()
    let endMonth = endDate.getMonth()+1
    let endYear = endDate.getFullYear()
    let endHours = endDate.getHours()
    let endMinutes = endDate.getMinutes()
    let formattedEndDate = `${endDay} : ${endMonth} : ${endYear} `
    console.log(response)
    let eventEditModal = document.getElementById('eventEditModal')
    let row = ''
    row += `
    <div> 
    <label for="updateEventId"></label>
    <input type="hidden" value="${response.id}" name="updateEventId" id="updateEventId">
    <label for="updateEventTitle"  >Title</label>
    <input type="text"  class="form-control" value="${response.title}" id="updateEventTitle" name="updateEventTitle">
    <label for="updateEventDescription"  >Description</label>
    <textarea type="text" class="form-control" id="updateEventDescription" name="updateEventDescription">${response.description}</textarea>
    <b class="form-control">${formattedStartDate}</b>
    <label for="updateEventStartDate"  >Start Date</label>
    <input class="form-control" type="date" id="updateEventStartDate" name="updateEventStartDate" value=''>
    <b class="form-control">${formattedEndDate}</b>
    <label for="updateEventEndDate"  >End Date</label>
    <input class="form-control" type="date" id="updateEventEndDate" name="updateEventEndDate" value=''>
    <label for="updateEventLocation"  >Location</label>
    <input class="form-control" type="text" id="updateEventLocation" value="${response.location}">
    <label for="updateEventPhoto"  >Edit Photo</label>
    <input class="form-control" type="file" id="updateEventPhoto" name="updateEventPhoto">
    <img class="from-control" src="${response.photo}" style="width:100px; height;100px;" id="${response.id}-event-edit-url">
    <button  id="restoreBtn" class="btn btn-success hidden" onclick="restorePollPhoto(${response.id})">Restore</button>
    <button class="btn btn-danger" id="photoRemoveBtn" onclick="deleteEditEventPhoto(${response.id})">Delete</button> 
    </div>
    <div class="modal-footer">
    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
    <button type="button" onclick="getEventUpdateData()" data-bs-dismiss="modal" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#loadingModalBox" >Update</button>
</div>
    `
    eventEditModal.innerHTML = row
}

async function deleteEditEventPhoto(id){
    document.getElementById(id+'-event-edit-url').src =document.getElementById(id+'-event-edit-url').src+ 'deleted'
    document.getElementById('updateEventPhoto').type = 'hidden'
    document.getElementById('restoreBtn').classList.remove('hidden')
    document.getElementById('photoRemoveBtn').classList.add('hidden')
}

async function deleteEditPollEventPhoto(id){
    document.getElementById(id+'-event-edit-url').src =document.getElementById(id+'-poll-event-edit-url').src+ 'deleted'
    document.getElementById('updatePollEventPhoto').type = 'hidden'
    document.getElementById('restoreBtn').classList.remove('hidden')
    document.getElementById('photoRemoveBtn').classList.add('hidden')
}

async function restorePollPhoto(id){
    let src = document.getElementById(id+'-event-edit-url').src
    console.log(src)
    let li = src.lastIndexOf('deleted')
    let result = src.substring(0,li)
    console.log(result)
    document.getElementById(id+'-event-edit-url').src = result
    console.log(document.getElementById(id+'-event-edit-url').src)
    document.getElementById('photoRemoveBtn').classList.remove('hidden')
    document.getElementById('restoreBtn').classList.add('hidden')
    document.getElementById('updateEventPhoto').type = 'file'
}

async function restorePollPhoto(id){
    let src = document.getElementById(id+'-poll-event-edit-url').src
    console.log(src)
    let li = src.lastIndexOf('deleted')
    let result = src.substring(0,li)
    console.log(result)
    document.getElementById(id+'-poll-event-edit-url').src = result
    console.log(document.getElementById(id+'-poll-event-edit-url').src)
    document.getElementById('photoRemoveBtn').classList.remove('hidden')
    document.getElementById('restoreBtn').classList.add('hidden')
    document.getElementById('updatePollEventPhoto').type = 'file'
}

async function getEventUpdateData(){
    let eventId = document.getElementById('updateEventId').value
    let eventTitle = document.getElementById('updateEventTitle').value
    let updateEventDescription = document.getElementById('updateEventDescription').value
    let start = document.getElementById('updateEventStartDate').value
    let end = document.getElementById('updateEventEndDate').value
    let location = document.getElementById('updateEventLocation').value
    let oldPhoto = document.getElementById(eventId+'-event-edit-url').src
    let newPhoto = document.getElementById('updateEventPhoto').files
    console.log(eventId)
    console.log(eventTitle)
    console.log(updateEventDescription)
    console.log(start)
    console.log(end)
    console.log(location)
    console.log(oldPhoto)
    console.log(newPhoto)

    let data = await fetch('/event/getEventById/'+eventId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    let startDate = new Date(response.start_date)
    let startDay = String(startDate.getDate()).padStart(2, '0')
    let startMonth = String(startDate.getMonth()+1).padStart(2, '0')
    let startYear = startDate.getFullYear()
    let startHours = startDate.getHours()
    let startMinutes = startDate.getMinutes()
    let formattedStartDate = `${startYear}-${startMonth}-${startDay}`;
    let endDate = new Date(response.end_date)
    let endDay = String(endDate.getDate()).padStart(2, '0')
    let endMonth = String(endDate.getMonth()+1).padStart(2, '0')
    let endYear = endDate.getFullYear()
    let endHours = endDate.getHours()
    let endMinutes = endDate.getMinutes()
    let formattedEndDate = ` ${endYear}-${endMonth}-${endDay}`

    if(newPhoto === ''){
        console.log('new photo is empty')
        newPhoto = null
    }
    if(start === ''){
        console.log('new statt is empty')
        start = 'old'
    }
    if(end === ''){
        console.log('new end is empty')
        end = 'old'
    }

    let updateEvent = {
        eventId : eventId,
        title : eventTitle,
        start_date : start,
        end_date : end,
        description : updateEventDescription,
        location : location,
        photo : oldPhoto,
        newPhoto :newPhoto
    }
    let formData = new FormData()
    formData.append('eventId',eventId)
    formData.append('title',eventTitle)
    formData.append('startDate',start)
    formData.append('endDate',end)
    formData.append('description',updateEventDescription)
    formData.append('location',location)
    formData.append('photo',oldPhoto)
    if(newPhoto !== null){
        formData.append('newPhoto',newPhoto[0])
    }
    if(newPhoto === null){
        formData.append('newPhoto',null)
    }

    console.log(Object.fromEntries(formData.entries()))
    console.log(updateEvent)
    console.log(JSON.stringify(updateEvent))
    let send = await fetch('/event/eventUpdate',{
        method : 'POST',
        body : formData
    })
    let res = await send.json()
    console.log(res)
}

async function deleteEvent(id){
    let data = await fetch('/event/deleteAEvent/'+id,{
        method : 'DELETE'
    })
    let result = data.json()
    console.log(result)
}


async function createAVoteOption(){
    let vote = document.getElementById('voteOptionInput').value
    console.log(vote)
    if(vote !== ''){
        let div = document.createElement('div')
        let btn = document.createElement('button')
        let li = document.createElement('li')
        div.style.display = 'flex'
        btn.classList.add('erase-btn')
        btn.textContent = 'X'
        li.classList.add('voteOption')
        let ul = document.getElementById('ulTag')
        li.textContent = vote
        div.appendChild(li)
        div.appendChild(btn)
        ul.appendChild(div)
        if(li){
            document.getElementById('voteOptionInput').value = ''
            btn.addEventListener('click', function() {
                // Remove the corresponding vote option when the button is clicked
                div.remove();
            });
        }
    }


}
async function createAVoteOptionForUpdate(){
    let vote = document.getElementById('voteOptionInputForUpdate').value
    console.log(vote)
    if(vote !== ''){
        let div = document.createElement('div')
        let btn = document.createElement('button')
        let li = document.createElement('li')
        div.style.display = 'flex'
        btn.classList.add('erase-btn')
        btn.textContent = 'X'
        li.classList.add('voteOptionForUpdate')
        let ul = document.getElementById('ulTagForUpdate')
        li.textContent = vote
        div.appendChild(li)
        div.appendChild(btn)
        ul.appendChild(div)
        if(li){
            document.getElementById('voteOptionInputForUpdate').value = ''
            btn.addEventListener('click', function() {
                // Remove the corresponding vote option when the button is clicked
                div.remove();
            });
        }
    }


}

async function createAPollPost(){
    if(validationFails()){
        alert('start date , end date , vote option of two and title is at least require')
    }else{
        let arr = []
        let values = document.querySelectorAll('.voteOption')
        values.forEach(v=>{
            arr.push(v.textContent.trim())
        })
        let data = new FormData(document.getElementById('pollForm'))
        let obj = Object.fromEntries(data.entries())
        console.log(obj)
        console.log(arr)
        data.append('opts',arr)
        data.append('multipartFile',document.getElementById('pollMultipartFile').files[0])
        console.log(data)
        let datas = await fetch('/event/createAVotePost',{
            method : 'POST',
            body : data
        })
        let response = await datas.json()
        console.log(response)
    }
}

// Add an event listener to the modal
document.getElementById('pollCreateBtn').addEventListener('click', async function (event) {
    // Check if validation fails
    if (validationFails()) {
    }else{

    }
})



// Function to check if validation fails
function validationFails() {
    // Add your validation logic here
    let start_date = document.getElementById('poll_start_date').value;
    let end_date = document.getElementById('poll_end_date').value;
    let title = document.getElementById('pollTitle').value;
    let arr = [];
    let values = document.querySelectorAll('.voteOption');
    values.forEach(v=>{
        arr.push(v.textContent.trim());
    });

    return (start_date === '' || end_date === '' || title === '' || arr.length < 2 ||start_date>end_date);
}


async function getAllPollPost(){
    let polls = document.getElementById('polls');
    let pollTab = document.getElementById('polls')

    let data = await fetch('/event/getAllPollPost',{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    let rows = ''
    for (let r of response) {
        let hidden = await checkVoted(r.id) === false ? '' : 'hidden'
        let notHidden = await checkVoted(r.id) === false ? 'hidden' : ''
        let row = ''
        row += `
        <div class="post" id="pollPostDiv-${r.id}">
        <div class="post-top">
            <div class="dp">
                <img src="${r.user.photo}" alt="">
            </div>
            <div class="post-info">
                <p class="name">${r.user.name}</p>
                <span class="time">2 days ago</span>
            </div>
                        <div class="dropdown offset-8">
      <a class=" dropdown-toggle" onclick="getPollEventDetail(${r.id})" href="#"   id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fas fa-ellipsis-h "></i>
            </a>
    
      <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
        <li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#pollEventEditModalBox">Edit</a></li>
        <li><a class="dropdown-item" onclick="deleteEvent(${r.id})">Delete Post</a></li> 
      </ul>
    </div>
        </div> 
        <nav>
        <div class="nav nav-tabs" id="nav-tab" role="tablist">
          <button class="nav-link active" id="nav-about-tab-${r.id}" data-bs-toggle="tab" data-bs-target="#nav-about-${r.id}" type="button" role="tab" aria-controls="nav-about-${r.id}" aria-selected="true"> <i class="fa-solid fa-info"></i>  About</button>
          <button class="nav-link" id="nav-vote-tab-${r.id}" data-bs-toggle="tab" data-bs-target="#nav-vote-${r.id}" type="button" role="tab" aria-controls="nav-vote-${r.id}" aria-selected="false"> <i class="fa-solid fa-check-to-slot"></i> Vote</button> 
        </div>
      </nav>
        <div class=" post-content" data-bs-toggle="modal"  > 


        <div class="card mb-3" style="max-width: 540px;">
        <div class="row g-0">
          <div class="col-md-4">
            <img src="${r.photo}" style="max-width:170px;" class="img-fluid rounded-start" alt="...">
          </div>
          <div class="col-md-8">
            <div class="card-body" style="max-height: 200px; overflow-y: auto;">
            <div class="tab-content" id="nav-tabContent">
  <div class="tab-pane fade show active" id="nav-about-${r.id}" role="tabpanel" aria-labelledby="nav-about-tab-${r.id}">
  
  <h5 class="card-title align-middle font-monospace">${r.title}</h5>
  <p class="card-text font-monospace">${r.description}</p>
  
  </div>
  <div class="tab-pane fade" id="nav-vote-${r.id}" role="tabpanel" aria-labelledby="nav-vote-tab-${r.id}"> 

            
            `
        let totalVotes = 0;
        for (let v of r.voteOptions) {
            let count = await getCount(v.id);
            totalVotes += count;
        }
        for (let v of r.voteOptions) {
            let count = await getCount(v.id);
            let percentage = totalVotes === 0 ? 0 : (count / totalVotes) * 100;
            row += `
        <div class="vote-option d-flex mt-1">
        <div class="d-block">
        <div class="font-monospace ">${v.type}</div>
                <div class="d-flex">
                <div class="progress" style="height: 16px; width: 170px;">
                
                    <div class="progress-bar bg-primary" id="${v.id}-bar" role="progressbar" style="width: 0%" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">${Math.floor(percentage)}%</div>
                </div>`
            row += ` <button id="vote-btn-${r.id}" class="${hidden} give-btn" onclick="voteVoteOption(${v.id},${r.id})"><i class="fa-solid fa-hand" style="width:7px; margin-right:11px;"></i></button> `;

            let yes = await checkVotedMark(v.id)
            let markIcon = 'hidden'
            if(yes === true){
                markIcon = ''
            }
            console.log(yes)


            row += `<div id="mark-id-${v.id}" class="${markIcon}"><i class="fa-regular fa-circle-check"></i></div>
                </div>
                </div>
                 `



            row+=`</div>`

        }

        row += `   <button id="unvote-btn-${r.id}" class="${notHidden} erase-btn" onclick = "unVote(${r.id})"><i class="fa-solid fa-eraser"></i></button> `

        row+=`</div> 
    </div>
          </div>
    </div>
  </div>
</div>
         
        <div class="post-bottom">
            <div class="action" style="height: 50px">
    <div class="button_wrapper">
          <div class="all_likes_wrapper">
              <div data-title="LIKE">
                  <img src="/static/assets/img/like.png" alt="Like" />
              </div>
              <div data-title="LOVE">
                  <img src="/static/assets/img/love.png" alt="Love" />
              </div>
              <div data-title="CARE">
                  <img src="/static/assets/img/care.png" alt="Care" />
              </div>
              <div data-title="HAHA">
                  <img src="/static/assets/img/haha.png" alt="Haha" />
              </div>
              <div data-title="WOW">
                  <img src="/static/assets/img/wow.png" alt="Wow" />
              </div>
              <div data-title="SAD">
                  <img src="/static/assets/img/sad.png" alt="Sad" />
              </div>
              <div data-title="ANGRY">
                  <img src="/static/assets/img/angry.png" alt="Angry" />
              </div>
          </div>
          <button class="like_button" id=" "> 
          </button>
      </div>
            </div>
            <div class="action">
                <i class="fa-regular fa-comment"></i>
                <span onclick="pressedComment()"  data-bs-toggle="modal" data-bs-target="#commentStaticBox">Comment  </span>
            </div>
            <div class="action">
                <i class="fa fa-share"></i>
                <span>Share</span>
            </div>
        </div>
    </div>
    </div>
        `
        rows+=row



    }
    let range = document.createRange();
    let fragment = range.createContextualFragment(rows);
    while (polls.children.length > 0) {
        polls.children[0].remove();
    }
    pollTab.appendChild(fragment)
    if(fragment){
        for(let r of response){
            await updateVotePercentage(r.voteOptions)
        }
    }

}


async function checkVotedMark(voteOptionId){
    let data = await fetch('event/checkVotedMark/'+voteOptionId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    return response
}

async function updateVotePercentage(vo){
    let totalVotes = 0;
    for (let v of vo) {
        let count = await getCount(v.id);
        totalVotes += count;
    }
    for(let v of vo){
        let bar = document.getElementById(v.id+'-bar')
        let count = await getCount(v.id)
        let percentage = totalVotes === 0 ? 0 : (count / totalVotes) * 100;
        bar.style.transition = 'width 1s ease-in-out';
        bar.style.width = percentage + '%';
        document.getElementById(v.id+'-bar').textContent = Math.floor(percentage)+"%"
        if(await checkVotedMark(v.id) === true){
            document.getElementById('mark-id-'+v.id).classList.remove('hidden')
        }else{
            document.getElementById('mark-id-'+v.id).classList.add('hidden')
        }
    }
}

async function voteVoteOption(voteOptionId,eventId){
    console.log(voteOptionId,eventId)
    let data = await fetch('event/giveVote/'+voteOptionId+'/'+eventId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    if(response){
        let data = await fetch('/event/getAllPollPost',{
            method : 'GET'
        })
        let response = await data.json()
        response.forEach(r=>{
            updateVotePercentage(r.voteOptions)
            document.querySelectorAll('#vote-btn-'+eventId).forEach(element => {
                element.classList.add('hidden');
            })
            document.querySelectorAll('#unvote-btn-'+eventId).forEach(element => {
                element.classList.remove('hidden');
            })
        })
    }
}

async function getCount(voteOptionId){
    let data = await fetch('event/getCount/'+voteOptionId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    return response
}

async function checkVoted(eventId){
    let data = await fetch('/event/checkVoted/'+eventId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    return response
}

async function unVote(eventId){
    let data = await fetch('/event/deleteAPoll/'+eventId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    if(response){
        let data = await fetch('/event/getAllPollPost',{
            method : 'GET'
        })
        let response = await data.json()
        response.forEach(r=>{
            updateVotePercentage(r.voteOptions)
            document.querySelectorAll('#vote-btn-'+eventId).forEach(element => {
                element.classList.add('hidden');
            })
            document.querySelectorAll('#vote-btn-'+eventId).forEach(element => {
                element.classList.remove('hidden');
            })
            document.querySelectorAll('#unvote-btn-'+eventId).forEach(element => {
                element.classList.add('hidden');
            })
        })
    }
}


async function getPollEventDetail(id){
    let data = await fetch('/event/getEventById/'+id,{
        method : 'GET'
    })
    let response = await data.json()
    let startDate = new Date(response.start_date)
    let startDay = startDate.getDate()
    let startMonth = startDate.getMonth()+1
    let startYear = startDate.getFullYear()
    let startHours = startDate.getHours()
    let startMinutes = startDate.getMinutes()
    let formattedStartDate = `${startDay} : ${startMonth} : ${startYear} `;
    let endDate = new Date(response.end_date)
    let endDay = endDate.getDate()
    let endMonth = endDate.getMonth()+1
    let endYear = endDate.getFullYear()
    let endHours = endDate.getHours()
    let endMinutes = endDate.getMinutes()
    let formattedEndDate = `${endDay} : ${endMonth} : ${endYear} `
    console.log(response)
    let eventEditModal = document.getElementById('pollEventEditModal')
    let row = ''
    row += `
    <div> 
    <label for="updateEventId"></label>
    <input type="hidden" value="${response.id}" name="updateEventId" id="updatePollEventId">
    <label for="updateEventTitle"  >Title</label>
    <input type="text"  class="form-control" value="${response.title}" id="updatePollEventTitle" name="updateEventTitle">
    <label for="updateEventDescription"  >Description</label>
    <textarea type="text" class="form-control" id="updatePollEventDescription" name="updateEventDescription">${response.description}</textarea>
    <b class="form-control">${formattedStartDate}</b>
    <label for="updateEventStartDate"  >Start Date</label>
    <input class="form-control" type="date" id="updatePollEventStartDate" name="updateEventStartDate" value=''>
    <b class="form-control">${formattedEndDate}</b>
    <label for="updateEventEndDate"  >End Date</label>
    <input class="form-control" type="date" id="updatePollEventEndDate" name="updateEventEndDate" value=''>
    <label for="options">Add vote options </label>
    <div class="d-flex"><input type="text" name="voteOptionInputForUpdate" id="voteOptionInputForUpdate" class="form-control"><button type="button" class="btn btn-parimary " onclick="createAVoteOptionForUpdate()"><i class="fa-solid fa-plus text-white"></i></button></div>
    <div>
        <ul id="ulTagForUpdate">
            
        </ul>
    </div>
    <div>
    <div class="form-check">
     `

    response.voteOptions.forEach(r=>{
        row += `
        <label class="form-check-label">
          <input type="checkbox" class="form-check-input" id="votodelete"   value="${r.id}" checked>
          ${r.type}
        </label> `
    })
    row +=
        `      </div>
    </div>
    <label for="updateEventPhoto"  >Edit Photo</label>
    <input class="form-control" type="file" id="updatePollEventPhoto" name="updateEventPhoto">
    <img class="from-control" src="${response.photo}" style="width:100px; height;100px;" id="${response.id}-poll-event-edit-url">
    <button  id="restoreBtn" class="btn btn-success hidden" onclick="restorePollPhoto(${response.id})">Restore</button>
    <button class="btn btn-danger" id="photoRemoveBtn" onclick="deleteEditPollEventPhoto(${response.id})">Delete</button> 
    </div>
    <div class="modal-footer">
    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
    <button type="button" onclick="getPollEventUpdateData()" data-bs-dismiss="modal" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#loadingModalBox" >Update</button>
</div>
    `
    eventEditModal.innerHTML = row
}

async function getPollEventUpdateData(){
    let eventId = document.getElementById('updatePollEventId').value
    let eventTitle = document.getElementById('updatePollEventTitle').value
    let updateEventDescription = document.getElementById('updatePollEventDescription').value
    let start = document.getElementById('updatePollEventStartDate').value
    let end = document.getElementById('updatePollEventEndDate').value
    let oldPhoto = document.getElementById(eventId+'-poll-event-edit-url').src
    let newPhoto = document.getElementById('updatePollEventPhoto').files
    let votes = []
    let cb = document.querySelectorAll('#votodelete')
    cb.forEach(c=>{
        if(c.checked){
            votes.push(c.value)
        }
    })
    let arr = []
    let values = document.querySelectorAll('.voteOptionForUpdate')
    values.forEach(v=>{
        arr.push(v.textContent.trim())
    })
    console.log(arr)
    console.log(votes)
    console.log(eventId)
    console.log(eventTitle)
    console.log(updateEventDescription)
    console.log(start)
    console.log(end)
    console.log(location)
    console.log(oldPhoto)
    console.log(newPhoto)

    let data = await fetch('/event/getEventById/'+eventId,{
        method : 'GET'
    })
    let response = await data.json()
    console.log(response)
    let startDate = new Date(response.start_date)
    let startDay = String(startDate.getDate()).padStart(2, '0')
    let startMonth = String(startDate.getMonth()+1).padStart(2, '0')
    let startYear = startDate.getFullYear()
    let startHours = startDate.getHours()
    let startMinutes = startDate.getMinutes()
    let formattedStartDate = `${startYear}-${startMonth}-${startDay}`;
    let endDate = new Date(response.end_date)
    let endDay = String(endDate.getDate()).padStart(2, '0')
    let endMonth = String(endDate.getMonth()+1).padStart(2, '0')
    let endYear = endDate.getFullYear()
    let endHours = endDate.getHours()
    let endMinutes = endDate.getMinutes()
    let formattedEndDate = ` ${endYear}-${endMonth}-${endDay}`

    if(newPhoto === ''){
        console.log('new photo is empty')
        newPhoto = null
    }
    if(start === ''){
        console.log('new statt is empty')
        start = 'old'
    }
    if(end === ''){
        console.log('new end is empty')
        end = 'old'
    }

    let updateEvent = {
        eventId : eventId,
        title : eventTitle,
        start_date : start,
        end_date : end,
        description : updateEventDescription,
        location : location,
        photo : oldPhoto,
        newPhoto :newPhoto
    }
    let formData = new FormData()
    formData.append('eventId',eventId)
    formData.append('title',eventTitle)
    formData.append('startDate',start)
    formData.append('endDate',end)
    formData.append('description',updateEventDescription)
    formData.append('location',location)
    formData.append('photo',oldPhoto)
    formData.append('oldOpts',votes)
    formData.append('newOpts',arr)
    if(newPhoto !== null){
        formData.append('newPhoto',newPhoto[0])
    }
    if(newPhoto === null){
        formData.append('newPhoto',null)
    }

    console.log(Object.fromEntries(formData.entries()))
    console.log(updateEvent)
    console.log(JSON.stringify(updateEvent))
    let send = await fetch('/event/pollEventUpdate',{
        method : 'POST',
        body : formData
    })
    let res = await send.json()
    console.log(res)
}



async function goToPollTab(){
    let data = await fetch('/event/searchPolls/'+localStorage.getItem('searchInput'),{
        method : 'GET'
    })
    let response = await data.json()
    if(response.length<1){
        document.getElementById('pills-poll-search').innerHTML =  `
         
        <div class="card-body font-monospace fs-4" style="padding:30px; margin-left:100px;" >
        <i class="fa-solid fa-question text-danger "></i> No result here <i class="fa-solid fa-magnifying-glass text-secondary"></i>
        </div>
     
        `
        return
    }
    let rows = ''
    for (let r of response) {
        let hidden = await checkVoted(r.id) === false ? '' : 'hidden'
        let notHidden = await checkVoted(r.id) === false ? 'hidden' : ''
        let row = ''
        row += `
        <div class="post" id="pollPostDiv-${r.id}">
        <div class="post-top">
            <div class="dp">
                <img src="${r.user.photo}" alt="">
            </div>
            <div class="post-info">
                <p class="name">${r.user.name}</p>
                <span class="time">2 days ago</span>
            </div>
                        <div class="dropdown offset-8">
      <a class=" dropdown-toggle" onclick="getPollEventDetail(${r.id})" href="#"   id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fas fa-ellipsis-h "></i>
            </a>
    
      <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
        <li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#pollEventEditModalBox">Edit</a></li>
        <li><a class="dropdown-item" onclick="deleteEvent(${r.id})">Delete Post</a></li> 
      </ul>
    </div>
        </div> 
        <nav>
        <div class="nav nav-tabs" id="nav-tab" role="tablist">
          <button class="nav-link active" id="nav-about-tab-${r.id}" data-bs-toggle="tab" data-bs-target="#nav-about-${r.id}" type="button" role="tab" aria-controls="nav-about-${r.id}" aria-selected="true"> <i class="fa-solid fa-info"></i>  About</button>
          <button class="nav-link" id="nav-vote-tab-${r.id}" data-bs-toggle="tab" data-bs-target="#nav-vote-${r.id}" type="button" role="tab" aria-controls="nav-vote-${r.id}" aria-selected="false"> <i class="fa-solid fa-check-to-slot"></i> Vote</button> 
        </div>
      </nav>
        <div class=" post-content" data-bs-toggle="modal"  > 


        <div class="card mb-3" style="max-width: 540px;">
        <div class="row g-0">
          <div class="col-md-4">
            <img src="${r.photo}" style="max-width:170px;" class="img-fluid rounded-start" alt="...">
          </div>
          <div class="col-md-8">
            <div class="card-body" style="max-height: 200px; overflow-y: auto;">
            <div class="tab-content" id="nav-tabContent">
  <div class="tab-pane fade show active" id="nav-about-${r.id}" role="tabpanel" aria-labelledby="nav-about-tab-${r.id}">
  
  <h5 class="card-title align-middle font-monospace">${r.title}</h5>
  <p class="card-text font-monospace">${r.description}</p>
  
  </div>
  <div class="tab-pane fade" id="nav-vote-${r.id}" role="tabpanel" aria-labelledby="nav-vote-tab-${r.id}"> 

            
            `
    let totalVotes = 0;
    for (let v of r.voteOptions) {
        let count = await getCount(v.id);
        totalVotes += count;
    }
    for (let v of r.voteOptions) {
        let count = await getCount(v.id);
        let percentage = totalVotes === 0 ? 0 : (count / totalVotes) * 100;
        row += `
        <div class="vote-option d-flex mt-1">
        <div class="d-block">
        <div class="font-monospace ">${v.type}</div>
                <div class="d-flex">
                <div class="progress" style="height: 16px; width: 170px;">
                
                    <div class="progress-bar bg-primary" id="${v.id}-bar" role="progressbar" style="width: 0%" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">${Math.floor(percentage)}%</div>
                </div>`
                row += ` <button id="vote-btn-${r.id}" class="${hidden} give-btn" onclick="voteVoteOption(${v.id},${r.id})"><i class="fa-solid fa-hand" style="width:7px; margin-right:11px;"></i></button> `;

                let yes = await checkVotedMark(v.id)
                let markIcon = 'hidden'
                if(yes === true){
                    markIcon = ''
                }
                console.log(yes)

                
                    row += `<div id="mark-id-${v.id}" class="${markIcon}"><i class="fa-regular fa-circle-check"></i></div>
                </div>
                </div>
                 ` 

                   
              
                row+=`</div>`
               
    }
     
    row += `   <button id="unvote-btn-${r.id}" class="${notHidden} erase-btn" onclick = "unVote(${r.id})"><i class="fa-solid fa-eraser"></i></button> `
     
    row+=`</div> 
    </div>
          </div>
    </div>
  </div>
</div>
         
        <div class="post-bottom">
            <div class="action" style="height: 50px">
    <div class="button_wrapper">
          <div class="all_likes_wrapper">
              <div data-title="LIKE">
                  <img src="/static/assets/img/like.png" alt="Like" />
              </div>
              <div data-title="LOVE">
                  <img src="/static/assets/img/love.png" alt="Love" />
              </div>
              <div data-title="CARE">
                  <img src="/static/assets/img/care.png" alt="Care" />
              </div>
              <div data-title="HAHA">
                  <img src="/static/assets/img/haha.png" alt="Haha" />
              </div>
              <div data-title="WOW">
                  <img src="/static/assets/img/wow.png" alt="Wow" />
              </div>
              <div data-title="SAD">
                  <img src="/static/assets/img/sad.png" alt="Sad" />
              </div>
              <div data-title="ANGRY">
                  <img src="/static/assets/img/angry.png" alt="Angry" />
              </div>
          </div>
          <button class="like_button" id=" "> 
          </button>
      </div>
            </div>
            <div class="action">
                <i class="fa-regular fa-comment"></i>
                <span onclick="pressedComment()"  data-bs-toggle="modal" data-bs-target="#commentStaticBox">Comment  </span>
            </div>
            <div class="action">
                <i class="fa fa-share"></i>
                <span>Share</span>
            </div>
        </div>
    </div>
    </div>
        `
        rows+=row

        
        
    }
    // let range = document.createRange();
    // let fragment = range.createContextualFragment(rows);       
    // while (polls.children.length > 0) {
    //   polls.children[0].remove();
    // }
    document.getElementById('pills-poll-search').innerHTML = rows
    if(rows){
        for(let r of response){
            await updateVotePercentage(r.voteOptions)
        }
    }
}
