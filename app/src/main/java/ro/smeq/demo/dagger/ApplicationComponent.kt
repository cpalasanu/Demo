package ro.smeq.demo.dagger

import dagger.Component
import ro.smeq.demo.network.Api
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun provideApi(): Api
}
