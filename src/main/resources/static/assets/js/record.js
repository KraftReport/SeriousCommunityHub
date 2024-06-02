
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



async function goToCommunityDetail(id){
    let data = await fetch(`/user/checkIfUserIsAMemberOrOwnerOrAdminOfAGroup/${id}`)
    let response = await data.json()
    if(response[0] === 'VISITOR'){
        alert('you have not access to view this group')
    }else{
        localStorage.setItem('communityIdForDetailPage',id)
        window.location.href = 'api/community/goToCommunityDetail'
    }
}


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
        // const image = `${post.user.photo}` || 'static/assets/img/default-logo.png';
        const image = post.user.photo ? post.user.photo : '/static/assets/img/default-logo.png';
        photo.src = image;
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
        // const image = `${post.user.photo}` || 'static/assets/img/default-logo.png';
        const image = post.user.photo ? post.user.photo : '/static/assets/img/default-logo.png';
        photo.src = image;
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
     // const image = `${p.user.photo}` || 'static/assets/img/default-logo.png';
     const image = p.user.photo ? p.user.photo : '/static/assets/img/default-logo.png';
     photo.src = image;
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
     const image = p.user.photo ? p.user.photo : '/static/assets/img/default-logo.png';
     photo.src = image;
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
    // await showDetailsForEachPost();
    document.getElementById('postForWithinOneMonth').addEventListener('click', async () => {
        console.log("it clicked!!!!")
        const checkUser = await getUserToCheckAdminOrNot();
        if(checkUser.role === 'ADMIN'){
            const post = await getOnlyPostForAdminWithinOneMonth();
            console.log('POser',post.id);
            if(post){
                localStorage.setItem('trendPostId',post.id)
                window.location.href = `/trendingPostDetailPage`
            }
        }else{
            const post = await fetchMostTrendyPostWithinOneMonth();
            console.log('POser',post.id);
            if(post){
                localStorage.setItem('trendPostId',post.id)
                window.location.href = `/trendingPostDetailPage`
            }
        }
    })
    document.getElementById('postForWithinOneYear').addEventListener('click', async () => {
        const checkUser = await getUserToCheckAdminOrNot();
        if(checkUser.role === 'ADMIN'){
            const p = await getOnlyPostForAdminWithinOneYear();
            localStorage.setItem('trendPostId',p.id)
            window.location.href = `/trendingPostDetailPage`
        }else{
            const post = await fetchMostTrendyPostWithinOneYear();
            console.log('POser',post.id);
            localStorage.setItem('trendPostId',post.id)
            window.location.href = `/trendingPostDetailPage`
        }

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

    const photo = community.image || 'static/assets/img/default-logo.png';
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

//for pie chart

document.addEventListener("DOMContentLoaded", function() {
    const pieChartContainer = document.getElementById('pieChartContainer');

    fetch('/user/data')
        .then(response => response.json())
        .then(data => {
            for (const [userId, userDetail] of Object.entries(data)) {
                Promise.all([
                    fetch(`/user/getPosts-eachUser/month/${userId}`).then(response => response.ok ? response.json() : []),
                    fetch(`/user/getPosts-eachUser/year/${userId}`).then(response => response.ok ? response.json() : []),
                    fetch(`/user/getPosts-eachUser/all/${userId}`).then(response => response.ok ? response.json() : []),
                    fetch(`/user/activeUser-ReactsCount/${userId}`).then(response => response.ok ? response.json() : 0),
                    fetch(`/user/activeUser-CommentsCount/${userId}`).then(response => response.ok ? response.json() : 0)
                ])
                    .then(responses => Promise.all(responses.map(response => {
                        // Check if the response is a number or an array
                        if (Array.isArray(response)) {
                            return response;
                        } else if (typeof response === 'number') {
                            return response;
                        } else {
                            return 0;
                        }
                    })))
                    .then(([postsWithinMonth, postsWithinYear, totalPosts, totalReacts, totalComments]) => {
                        const chartContainer = document.createElement('div');
                        chartContainer.className = 'piechart-container';
                        chartContainer.dataset.username = userDetail.name.toLowerCase();

                        const pieCanvas = document.createElement('canvas');
                        pieCanvas.id = `pieChart-${userId}`;

                        const pieChartDiv = document.createElement('div');
                        pieChartDiv.className = 'piechart';
                        pieChartDiv.appendChild(pieCanvas);

                        chartContainer.appendChild(pieChartDiv);
                        pieChartContainer.appendChild(chartContainer);

                        const ctx = pieCanvas.getContext('2d');

                        const labels = ['Posts within one month', 'Posts within one year', 'Total Posts', 'Total Reacts', 'Total Comments'];
                        const counts = [
                            Array.isArray(postsWithinMonth) ? postsWithinMonth.length : 0,
                            Array.isArray(postsWithinYear) ? postsWithinYear.length : 0,
                            Array.isArray(totalPosts) ? totalPosts.length : 0,
                            typeof totalReacts === 'number' ? totalReacts : 0,
                            typeof totalComments === 'number' ? totalComments : 0
                        ];
                        const colors = ['red', 'green', 'blue', 'orange', 'purple'];

                        const isActive = counts.some(count => count > 0);
                        const titleText = isActive ?
                            `Data Distribution for User ${userDetail.staffId} (${userDetail.name})` :
                            `Data Distribution for User ${userDetail.staffId} (${userDetail.name}) - No active yet`;

                        new Chart(ctx, {
                            type: 'pie',
                            data: {
                                labels: labels,
                                datasets: [{
                                    label:'Counts',
                                    data: counts,
                                    backgroundColor: colors,
                                }],

                            },
                            options: {
                                responsive: true,
                                plugins: {
                                    title: {
                                        display: true,
                                        text: titleText
                                    },
                                    legend: {
                                        position: 'top',
                                    },
                                    tooltip: {
                                        callbacks: {
                                            label: function(context) {
                                                const label = labels[context.dataIndex] || '';
                                                const value = context.raw;
                                                return `${label}: ${value}`;
                                            }
                                        }
                                    },
                                }
                            },
                        });
                    })
                    .catch(error => console.error('Error fetching user data:', error));
            }
        })
        .catch(error => console.error('Error fetching data:', error));

    document.getElementById('userSearch').addEventListener('input', function() {
        const searchValue = this.value.toLowerCase();
        const allUsers = document.querySelectorAll('.piechart-container');

        allUsers.forEach(userContainer => {
            const userName = userContainer.dataset.username;
            if (userName.includes(searchValue)) {
                userContainer.style.display = 'block';
            } else {
                userContainer.style.display = 'none';
            }
        });
    });
});


//for piechart for eachMonth
document.addEventListener("DOMContentLoaded", async function() {
    const yearData = document.getElementById('searchWithYear');
    const monthData = document.getElementById('searchWithMonth');
    const pieChartContainer = document.getElementById('pieChartContainerForEach');

    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const currentMonth = currentDate.getMonth() + 1;

    yearData.value = currentYear;
    monthData.value = currentMonth;

    const disableFutureYears = () => {
        const yearOptions = yearData.options;

        for (let i = 0; i < yearOptions.length; i++) {
            const yearValue = parseInt(yearOptions[i].value);
            if (yearValue > currentYear) {
                yearOptions[i].disabled = true;
            } else {
                yearOptions[i].disabled = false;
            }
        }
    };

    const disableFutureMonths = () => {
        const selectedYear = parseInt(yearData.value);
        const monthOptions = monthData.options;

        for (let i = 0; i < monthOptions.length; i++) {
            const monthValue = parseInt(monthOptions[i].value);
            if (selectedYear > currentYear || (selectedYear === currentYear && monthValue > currentMonth)) {
                monthOptions[i].disabled = true;
            } else {
                monthOptions[i].disabled = false;
            }
        }
    };

    disableFutureYears();
    disableFutureMonths();

    yearData.addEventListener('change', () => {
        disableFutureYears();
        disableFutureMonths();
    });

    monthData.addEventListener('change', () => {
        disableFutureMonths();
    });

    const showDetailsForEachPost = async () => {
        const users = await getUsersWithoutAdmin();

        const renderChartForUser = async (user, yearValue, monthValue) => {
            try {
                const data = await fetch(`/user/dataPerMonth/${user.id}/${yearValue}/${monthValue}`).then(response => response.json());
                const userDetail = data[user.id];

                const postsWithinMonth = await fetch(`/user/posts-perMonth-forAdmin/${user.id}/${yearValue}/${monthValue}`).then(response => response.ok ? response.json() : []);
                const totalReacts = await fetch(`/user/posts-perMonth-reactSize-forAdmin/${user.id}/${yearValue}/${monthValue}`).then(response => response.ok ? response.json() : 0);
                const totalComments = await fetch(`/user/posts-perMonth-commentSize-forAdmin/${user.id}/${yearValue}/${monthValue}`).then(response => response.ok ? response.json() : 0);

                const chartContainer = document.createElement('div');
                chartContainer.className = 'piechart-containerForPost';
                chartContainer.dataset.username = userDetail.name.toLowerCase();

                const pieCanvas = document.createElement('canvas');
                pieCanvas.id = `pieChart-${user.id}`;

                const pieChartDiv = document.createElement('div');
                pieChartDiv.className = 'piechart';
                pieChartDiv.appendChild(pieCanvas);

                chartContainer.appendChild(pieChartDiv);
                pieChartContainer.appendChild(chartContainer);

                const ctx = pieCanvas.getContext('2d');

                const labels = ['Posts within one month', 'Total Reacts', 'Total Comments'];
                const counts = [
                    Array.isArray(postsWithinMonth) ? postsWithinMonth.length : 0,
                    typeof totalReacts === 'number' ? totalReacts : 0,
                    typeof totalComments === 'number' ? totalComments : 0
                ];
                const colors = ['red', 'green', 'blue'];

                const isActive = counts.some(count => count > 0);
                const titleText = isActive ?
                    `Data Distribution for User ${userDetail.staffId} (${userDetail.name})` :
                    `Data Distribution for User ${userDetail.staffId} (${userDetail.name}) - No activity yet`;

                new Chart(ctx, {
                    type: 'pie',
                    data: {
                        datasets: [{
                            data: counts,
                            backgroundColor: colors,
                        }],
                        labels: labels
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            title: {
                                display: true,
                                text: titleText
                            },
                            legend: {
                                position: 'top',
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = labels[context.dataIndex] || '';
                                        const value = context.raw;
                                        return `${label}: ${value}`;
                                    }
                                }
                            },
                        }
                    },
                });
            } catch (error) {
                console.error('Error fetching user data:', error);
            }
        };

        const handleClick = async () => {
            const yearValue = yearData.value;
            const monthValue = monthData.value;

            pieChartContainer.innerHTML = '';

            for (const user of users) {
                await renderChartForUser(user, yearValue, monthValue);
            }
        };

        yearData.addEventListener('change', handleClick);
        monthData.addEventListener('change', handleClick);

        document.getElementById('userSearchForPost').addEventListener('input', function() {
            const searchValue = this.value.toLowerCase();
            const allUsers = document.querySelectorAll('.piechart-containerForPost');

            allUsers.forEach(userContainer => {
                const userName = userContainer.dataset.username;
                if (userName.includes(searchValue)) {
                    userContainer.style.display = 'block';
                } else {
                    userContainer.style.display = 'none';
                }
            });
        });

        await handleClick();
    };

    await showDetailsForEachPost();
});


//for barChart
document.addEventListener("DOMContentLoaded", function() {
    const pieChartContainer = document.getElementById('pieChartContainer');

    fetch('/user/barchartdata')
        .then(response => response.json())
        .then(data => {
            console.log(data);
            for (const [userId, userDetail] of Object.entries(data)) {
                const userData = userDetail.data;
                const chartContainer = document.createElement('div');
                chartContainer.className = 'chart-container';
                chartContainer.dataset.username = userDetail.name.toLowerCase();

                const canvas = document.createElement('canvas');
                canvas.id = `chart-${userId}`;

                const chartDiv = document.createElement('div');
                chartDiv.className = 'chart';
                chartDiv.appendChild(canvas);

                chartContainer.appendChild(chartDiv);
                pieChartContainer.appendChild(chartContainer);

                const ctx = canvas.getContext('2d');
                const labels = ['Posts within one month', 'Posts within one year', 'Total Posts', 'Total Reacts', 'Total Comments'];
                const counts = [
                    userData.within_one_month,
                    userData.within_one_year,
                    userData.total_posts,
                    userData.total_reacts,
                    userData.total_comments
                ];
                const colors = ['red', 'green', 'blue', 'orange', 'purple'];
                if (userDetail.role === 'USER') {
                    const myChart = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Counts',
                                data: counts,
                                backgroundColor: colors,
                            }],
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                title: {
                                    display: true,

                                },
                                legend: {
                                    display: false,
                                },
                                tooltip: {
                                    callbacks: {
                                        label: function(context) {
                                            const label = context.dataset.label || '';
                                            const value = context.raw;
                                            return `${label}: ${value}`;
                                        }
                                    }
                                }
                            },
                            scales: {
                                y: {
                                    beginAtZero: true
                                }
                            }
                        },
                    });
                }
            }
        })
        .catch(error => console.error('Error fetching data:', error));
});


//for lineChart each month
// document.addEventListener("DOMContentLoaded", async function() {

const showPostsPerMonth = async () => {
    const yearData = document.getElementById('searchYear');
    const lineChartContainerForEachMonth = document.getElementById('lineChartContainerForEachMonth');
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    yearData.value = currentYear;

    const disableFutureYears = () => {
        const yearOptions = yearData.options;

        for (let i = 0; i < yearOptions.length; i++) {
            const yearValue = parseInt(yearOptions[i].value);
            if (yearValue > currentYear) {
                yearOptions[i].disabled = true;
            } else {
                yearOptions[i].disabled = false;
            }
        }
    };

    disableFutureYears();

    yearData.addEventListener('change', () => {
        disableFutureYears();

    });

    const renderLineChartForUser = async (user, yearValue) => {
        try {
            const response = await fetch(`/user/postCountsPerMonth/${user.id}/${yearValue}`);
            const data = await response.json();
            const userDetail = data[user.id];

            const chartContainer = document.createElement('div');
            chartContainer.className = 'linechart-containerForPost';
            chartContainer.dataset.username = userDetail.name.toLowerCase();

            const lineCanvas = document.createElement('canvas');
            lineCanvas.id = `lineChart-${user.id}`;

            const lineChartDiv = document.createElement('div');
            lineChartDiv.className = 'linechart';
            lineChartDiv.appendChild(lineCanvas);

            chartContainer.appendChild(lineChartDiv);
            lineChartContainerForEachMonth.appendChild(chartContainer);

            const ctx = lineCanvas.getContext('2d');

            const labels = Array.from({ length: 12 }, (_, i) => (i + 1).toString()); // Months 1 to 12

            const dataValues = Object.values(userDetail);

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Post Counts',
                        data: dataValues,
                        fill: false,
                        borderColor: 'rgb(75, 192, 192)',
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        title: {
                            display: true,
                            text: `Post Counts for User ${userDetail.staffId} (${userDetail.name})`
                        },
                        legend: {
                            display: false
                        }
                    }
                }
            });
        } catch (error) {
            console.error('Error fetching user data:', error);
        }
    };

    document.getElementById('userSearchForPostMonth').addEventListener('input', function() {
        const searchValue = this.value.toLowerCase();
        const allUsers = document.querySelectorAll('.linechart-containerForPost');

        allUsers.forEach(userContainer => {
            const userName = userContainer.dataset.username;
            userContainer.style.display = userName.includes(searchValue) ? 'block' : 'none';
        });
    });

    const handleClick = async () => {
        const yearValue = yearData.value;

        // Clear previous charts
        lineChartContainerForEachMonth.innerHTML = '';

        const users = await getUsersWithoutAdmin();

        for (const user of users) {
            await renderLineChartForUser(user, yearValue);
        }
    };

    yearData.addEventListener('change', handleClick);

    // Trigger the initial render
    await handleClick();
};

// });




const showDetailsForEachPost =async () => {
    const users = await getUsersWithoutAdmin();
    const yearData = document.getElementById('searchWithYear');
    const monthData = document.getElementById('searchWithMonth');
    users.forEach(user => {
        yearData.addEventListener('click',async () => {
            console.log("USERID=====>",user.id)
         const yearValue = yearData.value;
         const monthValue = monthData.value;
           await showAllPostsForEachMonth(user.id,yearValue,monthValue);
           await showAllPostsReactSizeForEachMonth(user.id,yearValue,monthValue);
           await showAllPostsCommentSizeForEachMonth(user.id,yearValue,monthValue);
        });

        monthData.addEventListener('click',async () => {
            const yearValue = yearData.value;
            const monthValue = monthData.value;
            await showAllPostsForEachMonth(user.id,yearValue,monthValue);
            await showAllPostsReactSizeForEachMonth(user.id,yearValue,monthValue);
            await showAllPostsCommentSizeForEachMonth(user.id,yearValue,monthValue);
        });
    });
}

const showAllPostsForEachMonth =async (id,year,month) =>{
    const data = await fetch(`/user/posts-perMonth-forAdmin/${id}/${year}/${month}`);
    if(!data.ok){
       alert('There is no posts in this month!');
    }
    const res = await data.json();
    console.log("postSize===========>",res.length);
}

const showAllPostsReactSizeForEachMonth =async (id,year,month) =>{
    const data = await fetch(`/user/posts-perMonth-reactSize-forAdmin/${id}/${year}/${month}`);
    if(!data.ok){
        alert('There is no react in this month!');
    }
    const res = await data.json();
    console.log("React size=======>",res);
}

const showAllPostsCommentSizeForEachMonth =async (id,year,month) =>{
    const data = await fetch(`/user/posts-perMonth-commentSize-forAdmin/${id}/${year}/${month}`);
    if(!data.ok){
        alert('There is no comment in this month!');
    }
    const res = await data.json();
    console.log("Comment size========>",res);
}

const clickSeeMore = async () => {
    let gp = await getTrendyCommunity()
    console.log('wow')
    await goToCommunityDetail(gp.id)
}

