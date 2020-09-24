package ro.smeq.demo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getUsers(): Flowable<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flowable<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<User>): Completable
}
