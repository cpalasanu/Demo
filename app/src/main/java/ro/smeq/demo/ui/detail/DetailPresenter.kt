package ro.smeq.demo.ui.detail

import io.reactivex.Flowable
import ro.smeq.demo.repository.Repository

class DetailPresenter(private val repository: Repository) {
    fun getPost(postId: Long): Flowable<List<ListItem>> {
        return repository.post(postId)
            .map { postWithAlbums ->
                val adapterList = ArrayList<ListItem>()
                adapterList.add(
                    HeaderListItem(
                        postWithAlbums.post.id,
                        postWithAlbums.post.title,
                        postWithAlbums.post.body
                    )
                )
                postWithAlbums.albumsWithPhotos.forEach { albumWithPhotos ->
                    adapterList.add(
                        AlbumListItem(
                            albumWithPhotos.album.id,
                            albumWithPhotos.album.title
                        )
                    )
                    albumWithPhotos.photos.forEach { photo ->
                        adapterList.add(PhotoListItem(photo.id, photo.title, photo.url))
                    }
                }

                return@map adapterList
            }
    }
}
