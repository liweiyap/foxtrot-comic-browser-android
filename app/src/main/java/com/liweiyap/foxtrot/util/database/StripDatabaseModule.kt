package com.liweiyap.foxtrot.util.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StripDatabaseModule {

    @Singleton
    @Provides
    fun provideStripDatabase(@ApplicationContext context: Context) = StripDatabase(context)

    @Provides
    fun provideStripDao(db: StripDatabase): StripDao = db.getStripDao()
}