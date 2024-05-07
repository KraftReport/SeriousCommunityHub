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
        
        <div class="card col-3 m-2 " style="width: 18rem;">
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
    response.forEach((p,index)=>{
        let rows = ''
        rows += `
     
        <div class="post">
        <div class="post-top">
            <div class="dp">
                <img src="${p.user.photo} " alt="">
            </div>
            <div class="post-info">
                <p class="name">${p.user.name} </p>
                <span class="time">2 days ago</span>
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
    })
    postTab.innerHTML = row
    
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
    response.forEach((r,index)=>{
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
                <span class="time">2 days ago</span>
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
      <div class="tab-pane fade show active" id="v-${r.id}-photo" role="tabpanel" aria-labelledby="v-${r.id}-photo-tab"><b class="p-2"><img src="${r.photo}" style="width:250px; height:200px; border-radius:20px"></div> 
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
    </div>`


        row += rows
    }) 
    document.getElementById('pills-event-search').innerHTML = row
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
