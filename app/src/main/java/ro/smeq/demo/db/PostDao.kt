package ro.smeq.demo.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface PostDao {
    @Transaction
    @Query("SELECT posts.*, users.email FROM posts INNER JOIN users ON posts.userId = users.id")
    fun getAllPosts(): Flowable<List<PostWithUserEmail>>

    @Transaction
    @Query("SELECT * FROM posts WHERE posts.id = :postId")
    fun getPost(postId: Long): Flowable<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: List<Post>): Completable
}
