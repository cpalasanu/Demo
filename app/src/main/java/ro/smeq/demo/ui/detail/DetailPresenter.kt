package ro.smeq.demo.ui.detail

import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.flowables.ConnectableFlowable
import ro.smeq.demo.repository.Repository

class DetailPresenter(private val repository: Repository) {
    private var postId: Long? = null
    private var postFlowable: ConnectableFlowable<List<ListItem>>? = null
    private var disposable = CompositeDisposable()

    fun getPost(postId: Long): Flowable<List<ListItem>> {
        if (this.postId != postId) {
            // clear previous subscriptions
            disposable.clear()

            this.postId = postId
            postFlowable = createPostFlowable(postId)

            disposable.add(postFlowable!!.connect())
        }

        return postFlowable!!
    }

    private fun createPostFlowable(postId: Long): ConnectableFlowable<List<ListItem>> {
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

                return@map adapterList as List<ListItem>
            }
            .replay(1)
    }
}
