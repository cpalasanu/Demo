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
    @Query("SELECT posts.*, users.email FROM posts INNER JOIN users ON posts.userId = users.id WHERE posts.title LIKE :searchStr")
    fun getAllPostsContaining(searchStr: String?): Flowable<List<PostWithUserEmail>>

    @Query("SELECT * FROM posts WHERE posts.id = :postId")
    fun getPost(postId: Long): Flowable<Post>

    @Query("DELETE FROM posts WHERE posts.id = :postId")
    fun deletePost(postId: Long): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: List<Post>): Completable
}
