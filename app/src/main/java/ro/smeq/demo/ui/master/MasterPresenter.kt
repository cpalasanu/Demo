package ro.smeq.demo.ui.master

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ro.smeq.demo.db.PostWithUserEmail
import ro.smeq.demo.repository.Repository

class MasterPresenter(private val repository: Repository) {
    private val posts = repository.posts()
        .map { it.map { post -> toListItem(post) } }
        .replay(1).autoConnect()

    fun deletePost(postId: Long): Completable {
        return repository.deletePost(postId)
    }

    fun sync(): Completable {
        return repository.sync()
    }

    fun getPosts(): Flowable<List<ListItem>> {
        return posts
    }

    fun searchInPosts(searchStr: String?): Single<List<ListItem>> {
        return if (searchStr.isNullOrBlank()) {
            repository.posts()
                .map { it.map { post -> toListItem(post) } }
                .toObservable().firstOrError()
        } else {
            repository.posts(searchStr)
                .map { it.map { post -> toListItem(post) } }
                .toObservable().firstOrError()
        }
    }

    private fun toListItem(post: PostWithUserEmail) = ListItem(post.post.id, post.post.title, post.email)
}
