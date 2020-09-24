package ro.smeq.demo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums INNER JOIN photos ON albums.id = photos.albumId WHERE userId = :userId")
    fun getAlbumsForUser(userId: Long): Flowable<List<AlbumWithPhotos>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbums(posts: List<Album>): Completable
}
