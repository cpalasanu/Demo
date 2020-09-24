package ro.smeq.demo.dagger

import dagger.Component
import ro.smeq.demo.network.Api
import ro.smeq.demo.repository.Repository
import ro.smeq.demo.ui.MainActivity
import ro.smeq.demo.ui.detail.DetailFragment
import ro.smeq.demo.ui.master.MasterFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(masterFragment: MasterFragment)
    fun inject(detailFragment: DetailFragment)

    fun provideRepository(): Repository
}
