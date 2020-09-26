package ro.smeq.demo.ui.master

import io.reactivex.Completable
import io.reactivex.Flowable
import ro.smeq.demo.repository.Repository

class MasterPresenter(private val repository: Repository) {
    fun deletePost(postId: Long): Completable {
        return repository.deletePost(postId)
    }

    fun sync(): Completable {
        return repository.sync()
    }

    fun getPosts(): Flowable<List<ListItem>> {
        return repository.posts()
            .map { it.map { post -> ListItem(post.post.id, post.post.title, post.email) } }
    }
}