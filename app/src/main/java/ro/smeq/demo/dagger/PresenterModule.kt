package ro.smeq.demo.dagger

import dagger.Module
import dagger.Provides
import ro.smeq.demo.repository.Repository
import ro.smeq.demo.ui.detail.DetailPresenter
import ro.smeq.demo.ui.master.MasterPresenter
import javax.inject.Singleton

@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideMasterPresenter(repository: Repository) = MasterPresenter(repository)

    @Provides
    @Singleton
    fun provideDetailPresenter(repository: Repository) = DetailPresenter(repository)
}
