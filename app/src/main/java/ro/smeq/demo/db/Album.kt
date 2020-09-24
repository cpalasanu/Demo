package ro.smeq.demo.db

import androidx.room.*

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val title: String
)

data class AlbumWithPhotos(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "id",
        entityColumn = "albumId"
    )
    val photos: List<Photo>
)
