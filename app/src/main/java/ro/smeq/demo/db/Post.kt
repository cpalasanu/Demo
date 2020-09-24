package ro.smeq.demo.db

import androidx.room.*

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String
)

data class PostWithUserEmail(
    @Embedded
    val post: Post,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id",
        entity = User::class,
        projection = ["email"]
    )
    val email: String
)
