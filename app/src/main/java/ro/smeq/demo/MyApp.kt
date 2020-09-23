package ro.smeq.demo

import android.app.Application
import ro.smeq.demo.dagger.ApplicationComponent
import ro.smeq.demo.dagger.DaggerApplicationComponent
import ro.smeq.demo.dagger.NetworkModule
import timber.log.Timber

class MyApp: Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
            .networkModule(NetworkModule(BuildConfig.BASE_URL))
            .build()
    }
}
