package ro.smeq.demo.network

import io.reactivex.Single
import retrofit2.http.GET
import ro.smeq.demo.db.Album
import ro.smeq.demo.db.Photo
import ro.smeq.demo.db.Post
import ro.smeq.demo.db.User

interface Api {
    @GET("posts")
    fun posts(): Single<List<Post>>

    @GET("users")
    fun users(): Single<List<User>>

    @GET("albums")
    fun albums(): Single<List<Album>>

    @GET("photos")
    fun photos(): Single<List<Photo>>
}
