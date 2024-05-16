
// const timeAgo = async (date) => {
//     console.log("Yout shi")
//     const now = new Date();
//     const diff = now - date;
//     const seconds = Math.floor(diff / 1000);
//     const minutes = Math.floor(seconds / 60);
//     const hours = Math.floor(minutes / 60);
//     const days = Math.floor(hours / 24);
//
//     if (seconds < 60) {
//         return `just now`;
//     } else if (minutes < 60) {
//         return `${minutes} minutes${minutes > 1? '' : ''} ago`;
//     } else if (hours < 24) {
//         return `${hours} hours${hours > 1? '' : ''} ago`;
//     } else {
//         return `${days} days${days > 1? '' : ''} ago`;
//     }
// }

const fetchMostTrendyPostWithinOneMonth = async () => {
    const fetchData =  await fetch(`/user/record-user-post-withinOneMonth`);
    const response = await fetchData.json();
    return response;
}

const fetchMostTrendyPostWithinOneYear = async () => {
    const dataFetch = await fetch(`/user/record-user-post-withinOneYear`);
    const response = await dataFetch.json();
    return  response;
}

const fetchMostTrendyPostReactCountWithinOneMonth = async () => {
    const dataList = await fetch(`/user/record-user-trendyReact-post-countWithinOneMonth`);
    const response = await dataList.json();
    return response;
}

const fetchMostTrendyPostReactCountWithinOneYear = async () => {
    const data = await fetch(`/user/record-user-trendyReact-post-countWithOneYear`);
    const response = await data.json();
    return response;
}

const fetchMostTrendyPostCommentCountWithinOneMonth =async () => {
    const commentCount = await fetch(`/user/record-user-trendyComment-post-countWithOneMonth`);
    const response = await commentCount.json();
    return response;
}

const fetchMostTrendyPostCommentCountWithinOneYear =async () => {
    const commentCount = await fetch(`/user/record-user-trendyComment-post-countWithOneYear`);
    const response = await commentCount.json();
    return response;
}


const showOneTrendyPostWithinOneMonth =async () => {
    const postedDate = document.getElementById('unique-oneMonthTrendyPostTime');
    const reactCount = document.getElementById('unique-oneMonthTrendyPostReactCount');
    const commentCount = document.getElementById('unique-oneMonthTrendyPostCommentCount');
    const photo = document.querySelector('.unique-image1');
    const checkUser = await getUserToCheckAdminOrNot();
    if(checkUser.role === 'ADMIN'){
        const post = await getOnlyPostForAdminWithinOneMonth();
        const image = `${post.user.photo}` || '/static/assets/img/card.jpg';
        photo.src = `${image}`;
        const reactSize = await getOnlyPostReactsCountForAdmin(post.id);
        const commentSize = await getOnlyPostCommentsCountForAdmin(post.id);
        const createdDate = new Date(post.createdDate);
        const formattedDate = createdDate.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        postedDate.innerHTML = `Posted Date: ${formattedDate}`;
        reactCount.innerHTML = ` Total React Count : ${ reactSize}`;
        commentCount.innerHTML = ` Total Comment Count : ${ commentSize}`;
        console.log("that is admin")
    }else{
        const post = await fetchMostTrendyPostWithinOneMonth();
        const image = `${post.user.photo}` || '/static/assets/img/card.jpg';
        photo.src = `${image}`;
        const reactSize = await fetchMostTrendyPostReactCountWithinOneMonth();
        const commentSize = await fetchMostTrendyPostCommentCountWithinOneMonth();
        const createdDate = new Date(post.createdDate);
        const formattedDate = createdDate.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        postedDate.innerHTML = `Posted Date: ${formattedDate}`;
        reactCount.innerHTML = ` Total React Count : ${ reactSize}`;
        commentCount.innerHTML = ` Total Comment Count : ${ commentSize}`;
        console.log("that is User")
    }
}

const showOneTrendyPostWithinOneYear =async () => {
    const postedDate = document.getElementById('unique-oneYearTrendyPostTime');
    const reactCount = document.getElementById('unique-oneYearTrendyPostReactCount');
    const commentCount = document.getElementById('unique-oneYearTrendyPostCommentCount');
    const photo = document.querySelector('.unique-image2');
const checkUser = await getUserToCheckAdminOrNot();
 if(checkUser.role === 'ADMIN'){
     const p = await getOnlyPostForAdminWithinOneYear();
     const image = `${p.user.photo}` || '/static/assets/img/card.jpg';
     photo.src = `${image}`;
     const reactSize = await getOnlyPostReactsCountForAdmin(p.id);
     const commentSize = await getOnlyPostCommentsCountForAdmin(p.id);
     const createdDate = new Date(p.createdDate);
     const formattedDate = createdDate.toLocaleDateString('en-US', {
         year: 'numeric',
         month: 'long',
         day: 'numeric',
         hour: '2-digit',
         minute: '2-digit'
     });
     postedDate.innerHTML = ` Posted Date : ${formattedDate}`;
     reactCount.innerHTML = ` Total React Count : ${ reactSize}`;
     commentCount.innerHTML = ` Total Comment Count : ${ commentSize}`;
     console.log("that is admin")
 }else{
     const p = await fetchMostTrendyPostWithinOneYear();
     const image = `${p.user.photo}` || '/static/assets/img/card.jpg';
     photo.src = `${image}`;
     const reactSize = await fetchMostTrendyPostReactCountWithinOneYear();
     const commentSize = await fetchMostTrendyPostCommentCountWithinOneYear();
     const createdDate = new Date(p.createdDate);
     const formattedDate = createdDate.toLocaleDateString('en-US', {
         year: 'numeric',
         month: 'long',
         day: 'numeric',
         hour: '2-digit',
         minute: '2-digit'
     });
     postedDate.innerHTML = ` Posted Date : ${formattedDate}`;
     reactCount.innerHTML = ` Total React Count : ${ reactSize}`;
     commentCount.innerHTML = ` Total Comment Count : ${ commentSize}`;
     console.log("that is user")
 }

}

document.addEventListener('DOMContentLoaded',async () =>{
    await showOneTrendyPostWithinOneMonth();
    await showOneTrendyPostWithinOneYear();
    await showTrendyCommunityWithMostMembers();
    await showCommunityPostsForBoth();
    document.getElementById('postForWithinOneMonth').addEventListener('click', async () => {
        const post = await fetchMostTrendyPostWithinOneMonth();
        console.log('POser',post.id);
    })

    document.getElementById('postForWithinOneYear').addEventListener('click', async () => {
        const post = await fetchMostTrendyPostWithinOneYear();
        console.log('POser',post.id);
    })

    document.querySelectorAll('.groupCommunityTrendy').forEach(g => g.addEventListener('click',async () => {
        const community = await getTrendyCommunity();
        console.log("comuminty",community.id)
    }))
});

 const timeAgo = (createdDate) => {
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


const showTrendyCommunityWithMostMembers = async () => {
    const groupName = document.getElementById('trendyGroupName');
    const groupDescription = document.getElementById('trendyGroupDescription');
    const memberList = document.getElementById('trendyGroupMembers');
    const groupPhoto1 = document.querySelector('.img_1');
    const groupPhoto2 = document.querySelector('.img_2');
    const groupPhoto3 = document.querySelector('.img_3');

    const community = await getTrendyCommunity();
    const members = await getTrendyCommunityMemberList();

    const groupIcon = document.createElement('i');
    const descriptionIcon = document.createElement('i');
    const groupNameIcon = document.createElement('i');

    groupIcon.classList.add('fa-solid', 'fa-people-group');
    descriptionIcon.classList.add('fa-solid', 'fa-audio-description');
    groupNameIcon.classList.add('fa-solid', 'fa-layer-group');

    const photo = community.image || 'static/assets/img/card.jpg';
    groupPhoto1.src = photo;
    groupPhoto2.src = photo;
    groupPhoto3.src = photo;

    groupName.innerHTML = '';
    groupName.appendChild(groupNameIcon);
    groupName.appendChild(document.createTextNode(` ${community.name}`));

    groupDescription.innerHTML = '';
    groupDescription.appendChild(descriptionIcon);
    groupDescription.appendChild(document.createTextNode(`${community.description}`));

    memberList.innerHTML = '';
    memberList.appendChild(groupIcon);
    memberList.appendChild(document.createTextNode(`${members.length} members`));
};

const showCommunityPostsForBoth = async () => {
    const totalPostsForOneMonth = document.getElementById('totalPostsForOneMonth');
    const totalReactsForOneMonth = document.getElementById('totalReactsForOneMonth');
    const totalCommentsForOneMonth = document.getElementById('totalCommentsForOneMonth');
    const totalPostsForOneYear = document.getElementById('totalPostsForOneYear');
    const totalReactsForOneYear = document.getElementById('totalReactsForOneYear');
    const totalCommentsForOneYear = document.getElementById('totalCommentsForOneYear');

    const postSizeForOneMonth = await getTrendYCommunityPostsWithinOneMonth();
    const postSizeForOneYear = await getTrendYCommunityPostsWithinOneYear();
    const reactSizeForOneMonth = await getTotalReactsForCommunityWithinOneMonth();
    const reactSizeForOneYear = await getTotalReactsForCommunityWithinOneYear();
    const commentSizeForOneMonth = await getTotalCommentsForCommunityWithinOneMonth();
    const commentSizeForOneYear = await getTotalCommentsForCommunityWithinOneYear();

    totalPostsForOneMonth.innerHTML = '';
    const postListIconMonth = document.createElement('i');
    postListIconMonth.classList.add('fa-solid', 'fa-signs-post');
    totalPostsForOneMonth.appendChild(postListIconMonth);
    totalPostsForOneMonth.appendChild(document.createTextNode(` Total Posts => ${postSizeForOneMonth.length}`));

    totalPostsForOneYear.innerHTML = '';
    const postListIconYear = document.createElement('i');
    postListIconYear.classList.add('fa-solid', 'fa-signs-post');
    totalPostsForOneYear.appendChild(postListIconYear);
    totalPostsForOneYear.appendChild(document.createTextNode(` Total Posts => ${postSizeForOneYear.length}`));

    totalReactsForOneMonth.innerHTML = '';
    const reactListIconMonth = document.createElement('i');
    reactListIconMonth.classList.add('fa-solid', 'fa-heart');
    totalReactsForOneMonth.appendChild(reactListIconMonth);
    totalReactsForOneMonth.appendChild(document.createTextNode(` Total Reacts => ${reactSizeForOneMonth}`));

    totalReactsForOneYear.innerHTML = '';
    const reactListIconYear = document.createElement('i');
    reactListIconYear.classList.add('fa-solid', 'fa-heart');
    totalReactsForOneYear.appendChild(reactListIconYear);
    totalReactsForOneYear.appendChild(document.createTextNode(` Total Reacts => ${reactSizeForOneYear}`));

    totalCommentsForOneMonth.innerHTML = '';
    const commentListIconMonth = document.createElement('i');
    commentListIconMonth.classList.add('fa-solid', 'fa-comments');
    totalCommentsForOneMonth.appendChild(commentListIconMonth);
    totalCommentsForOneMonth.appendChild(document.createTextNode(` Total Comments => ${commentSizeForOneMonth}`));

    totalCommentsForOneYear.innerHTML = '';
    const commentListIconYear = document.createElement('i');
    commentListIconYear.classList.add('fa-solid', 'fa-comments');
    totalCommentsForOneYear.appendChild(commentListIconYear);
    totalCommentsForOneYear.appendChild(document.createTextNode(` Total Comments => ${commentSizeForOneYear}`));
}



const showTrendyCommunityPostsForReactsCountAndCommentCount = () => {

}

const getTrendyCommunity = async () => {
     const data = await fetch(`/user/record-group-trendyCommunity`);
     const response = await data.json();
     return  response;
}

const getTrendYCommunityPostsWithinOneMonth =async () => {
     const data = await fetch(`/user/record-group-post-withinOneMonth`);
     const response = await data.json();
     return response;
}

const getTrendYCommunityPostsWithinOneYear =async () => {
    const data = await fetch(`/user/record-group-post-withinOneYear`);
    const response = await data.json();
    return response;
}

const getTrendyCommunityMemberList =async () => {
     const data = await fetch(`/user/record-group-trendyCommunityMemberList`);
  const response = await data.json();
  return response;
 }

 const getTotalReactsForCommunityWithinOneMonth = async () => {
    const data = await fetch(`/user/record-groupPostReacts-listForOneMonth`);
    const res = await data.json();
    return res;
}

const getTotalReactsForCommunityWithinOneYear = async () => {
    const data = await fetch(`/user/record-groupPostReacts-listForOneYear`);
    const res = await data.json();
    return res;
}

const getTotalCommentsForCommunityWithinOneMonth = async () => {
    const data = await fetch(`/user/record-groupPostComments-listForOneMonth`);
    const res = await data.json();
    return res;
}

const getTotalCommentsForCommunityWithinOneYear = async () => {
    const data = await fetch(`/user/record-groupPostComments-listForOneYear`);
    const res = await data.json();
    return res;
}

const getUserToCheckAdminOrNot =async () => {
    const data = await fetch(`/user/check-role`);
    const res = await data.json();
    return res;
}

const getOnlyPostForAdminWithinOneMonth = async () => {
    const uniquePost = await fetch(`/user/onlyOne-trendyPost-withinOneMonth`)
   const res = await uniquePost.json();
    return res;
}
const getOnlyPostForAdminWithinOneYear = async () => {
    const uniquePost = await fetch(`/user/onlyOne-trendyPost-withinOneYear`)
    const res = await uniquePost.json();
    return res;
}

const getOnlyPostReactsCountForAdmin = async (id) => {
    const reactCount = await fetch(`/user/onlyOne-trendyPostReacts-withinOneMonth/${id}`);
    const res = await reactCount.json();
    return res;
}

const getOnlyPostCommentsCountForAdmin = async (id) => {
    const reactCount = await fetch(`/user/onlyOne-trendyPostComments-withinOneMonth/${id}`);
    const res = await reactCount.json();
    return res;
}
//for admin to show each activity

const getUsersWithoutAdmin =async () => {
    const users = await fetch(`/user/activeUsers-forAdmin`);
    const res = await users.json();
    return res;
}

const getAllUsersReactsForAdmin =async (id) => {
    const reacts = await fetch(`/user/activeUser-ReactsCount/${id}`);
    const res = await reacts.json();
 return  res;
}
const getAllUsersCommentsForAdmin = async (id) => {
    const comments = await fetch(`/user/activeUser-CommentsCount/${id}`);
    const res = await comments.json();
    return  res;
}
const getPostListForEachUserWithinOneMonth = async (id) => {
    const posts = await fetch(`/user/getPosts-eachUser/month/${id}`);
    const res = await posts.json();
    return res;
}

const getPostListForEachYearWithinOneYear = async (id) => {
    const posts = await fetch(`/user/getPosts-eachUser/year/${id}`);
    const res = await posts.json();
    return res;
}

const getPostListForEachYear = async (id) => {
    const postList = await fetch(`/user/getPosts-eachUser/all/${id}`);
    const res = postList.json();
    return res;
}