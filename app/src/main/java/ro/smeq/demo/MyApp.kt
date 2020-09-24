package ro.smeq.demo

import android.app.Application
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ro.smeq.demo.dagger.ApplicationComponent
import ro.smeq.demo.dagger.DaggerApplicationComponent
import ro.smeq.demo.dagger.NetworkModule
import ro.smeq.demo.dagger.RepositoryModule
import timber.log.Timber

class MyApp : Application() {
    lateinit var applicationComponent: ApplicationComponent
    private val disposable = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationComponent = DaggerApplicationComponent.builder()
            .networkModule(NetworkModule(BuildConfig.BASE_URL))
            .repositoryModule(RepositoryModule(applicationContext))
            .build()

        disposable.add(
            applicationComponent.provideRepository().sync()
                .subscribeOn(Schedulers.io())
                .subscribe({ Timber.d("Sync complete!") }, Timber::e)
        )
    }
}
