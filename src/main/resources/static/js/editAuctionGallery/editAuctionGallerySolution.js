/* 
 * Atia & Tiger technology 2019.
 */

function startApp() {

    (() => {

        $('.my-ajax-getpics').on('click', getAuctionItemPictures);
        $(".prevent-default-form").submit(function (event) {
            event.preventDefault();
        });
    })();

    function getAuctionItemPictures(event) {
        let elm = $(event.target);
        let arr = elm.attr('action').split(" ");
        let url = arr[0];
        let actionId = arr[1];
        let baseURL = arr[2];
        let auctionItemId = arr[3];
        let divPictureReciver = $($(".my-ajax-reciver")[0]);



        let body = $($(elm.parents(".card")).find("div.row"));
        if (body.children().length !== 0) {
            return;
        }

        requester.get(url, successFunc, errorFunc);

        function successFunc(data) {
            for (let itemPic of data) {
                let isPictureAllreadyAddedToAuction = $('#'+itemPic.picturePath).length!==0;
                let html =
                        `<div class="col-md-7">
                                <a href="${itemPic.picturePath}">
                                    <img class="img-fluid rounded mb-3 mb-md-0" src="${itemPic.picturePath}" alt=""/>
                                </a>
                            </div>
                            <div class="col-md-5">
                                <form class="mx-auto w-100 prevent-default-form" action="/seller/auctions/gallery/edit/${actionId}/${itemPic.id}" method="update" id="formadd${itemPic.id}">
                                    <label class="control-label h3 mb-2" for="pictureDescription">Picture description</label>
                                    <textarea name="pictureDescription" placeholder="Picture Description..." class="form-control" style="resize: none;" cols="15" rows="3" disabled="true">${itemPic.description}</textarea>
                                    <br/>
                                    
                                `;
                if(isPictureAllreadyAddedToAuction){
                    html+=
                            `<div class="control-group">
                                    <div class="controls">
                                        <button class="btn btn-lg btn-primary btn-block">Add to auction</button>
                                    </div>
                            </div>`;
                }else{
                    html+=
                            `<div class="control-group">
                                    <div class="controls">
                                        <button class="btn btn-lg btn-danger btn-block">Delete from auction</button>
                                    </div>
                                </div>`;
                }
                html+=
                        `</form>
                            <br/>
                            </div>`;
                let picElm = $(html);
                $(picElm.find(".prevent-default-form")).submit(function (event) {
                    event.preventDefault();
                });
                picElm.find(".btn-primary").click(function(event){
                    addPicture(actionId, auctionItemId, itemPic.pictureId, itemPic.description, event);
                });
                picElm.find(".btn-danger").click(function(event){
                    deletePicture(baseURL + actionId + "/" +auctionItemId+"/"+ itemPic.id, event);
                });
                body.append(picElm);
            }
        }

        function errorFunc() {
            showError();
        }

        function addPicture(actionId, auctionItemId,  itemPicId, itemPicDescription, event) {
            let addUlr = baseURL + actionId + "/" +auctionItemId+"/"+ itemPicId;
            let obj = {"description":itemPicDescription};
            
            function successFunc(newPicData) {
                let newPicHTML = `<div class="col-md-6 col-lg-4 item zoom-on-hover">
                                    <a class="lightbox" href="${newPicData.picturePath}">
                                        <img class="img-fluid image" src="${newPicData.picturePath}"/>
                                        <span class="description">
                                            <span class="description-body" text="${newPicData.description}"/>
                                        </span>
                                    </a>
                                </div>`;
                let newPicElm = $(newPicHTML);
                divPictureReciver.append(newPicElm);
                let delBtnHtml=`<div class="control-group">
                                    <div class="controls">
                                        <button class="btn btn-lg btn-danger btn-block">Delete from auction</button>
                                    </div>
                                </div>`;
                
                let delBtnElm=$(delBtnHtml);
                let addBtnElm = $(event.target);
                let cardDiv = $(addBtnElm.parents(".col-md-5"));
                cardDiv.append(delBtnElm);
                delBtnElm.click(function(event){
                    deletePicture(baseURL + actionId + "/" +auctionItemId+"/"+ newPicData.id, event);
                });
                showInfo();
            }

            function errorFunc() {
                showError();
            }

            requester.post(addUlr, successFunc, errorFunc, obj);

        }


    }

    function deletePicture(url, event) {
        

        function successFunc(event) {
            $(event.target).remove();
            showInfo();
        }

        function errorFunc() {
            handleError();
        }

        requester.remove(url, function(){
            successFunc(event);
        }, errorFunc);
    }




}


