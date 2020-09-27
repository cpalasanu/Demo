package ro.smeq.demo.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ro.smeq.demo.db.*
import ro.smeq.demo.network.Api
import timber.log.Timber

class Repository(private val api: Api, private val db: MyDatabase) {
    fun sync(): Completable {
        return Completable.mergeArray(
            api.posts().flatMapCompletable { db.postDao().insertPosts(it) },
            api.albums().flatMapCompletable { db.albumDao().insertAlbums(it) },
            api.photos().flatMapCompletable { db.photoDao().insertPhotos(it) },
            api.users().flatMapCompletable { db.userDao().insertUsers(it) },
        )
    }

    fun posts(): Flowable<List<PostWithUserEmail>> {
        return db.postDao().getAllPosts()
    }

    fun posts(searchStr: String?): Flowable<List<PostWithUserEmail>> {
        return db.postDao().getAllPostsContaining("%$searchStr%")
    }

    fun post(postId: Long): Flowable<PostWithRelatedAlbums> {
        return db.postDao().getPost(postId)
            .flatMap { post ->
                db.albumDao().getAlbumsForUser(post.userId)
                    .map { PostWithRelatedAlbums(post, it) }
            }
    }

    fun deletePost(postId: Long): Completable {
        return db.postDao().deletePost(postId)
    }
}

data class PostWithRelatedAlbums(
    val post: Post,
    val albumsWithPhotos: List<AlbumWithPhotos>
)