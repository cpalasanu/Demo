package ro.smeq.demo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE albumId = :albumId")
    fun getPhotosInAlbum(albumId: Long): Flowable<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(photos: List<Photo>): Completable
}
