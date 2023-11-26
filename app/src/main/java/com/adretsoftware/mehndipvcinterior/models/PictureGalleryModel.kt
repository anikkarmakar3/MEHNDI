package com.adretsoftware.mehndipvcinterior.models

class PictureGalleryModel(
    user_id: String = "",
    cat_id: String = "",
    picture_image: String = "",
          //  picture_name :String=""
) {
    var user_id = ""
    var cat_id = ""
    var picture_image = ""
 //   var picture_name = ""
    init {
        this.user_id = user_id
        this.cat_id = cat_id
        this.picture_image = picture_image
      //  this.picture_name = picture_name
    }
}

// user_id
// cat_id
// image
