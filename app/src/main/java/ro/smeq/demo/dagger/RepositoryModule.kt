package ro.smeq.demo.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import ro.smeq.demo.db.MyDatabase
import ro.smeq.demo.network.Api
import ro.smeq.demo.repository.Repository
import javax.inject.Singleton

@Module
class RepositoryModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideDB() = MyDatabase.buildDatabase(context)

    @Provides
    @Singleton
    fun provideRepository(db: MyDatabase, api: Api) = Repository(api, db)
}
