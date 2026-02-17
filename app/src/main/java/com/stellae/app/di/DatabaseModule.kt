package com.stellae.app.di

import android.content.Context
import androidx.room.Room
import com.stellae.app.data.local.PrepopulateCallback
import com.stellae.app.data.local.StellaeDatabase
import com.stellae.app.data.local.dao.AchievementDao
import com.stellae.app.data.local.dao.CardDao
import com.stellae.app.data.local.dao.ConfusionPairDao
import com.stellae.app.data.local.dao.DignityDao
import com.stellae.app.data.local.dao.FsrsDao
import com.stellae.app.data.local.dao.LotDao
import com.stellae.app.data.local.dao.PlanetDao
import com.stellae.app.data.local.dao.SessionLogDao
import com.stellae.app.data.local.dao.SignDao
import com.stellae.app.data.local.dao.UserProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStellaeDatabase(
        @ApplicationContext context: Context,
        // Provider<StellaeDatabase> breaks the circular dependency between the database
        // instance and the callback that needs the database's DAOs.
        databaseProvider: Provider<StellaeDatabase>
    ): StellaeDatabase {
        return Room.databaseBuilder(
            context,
            StellaeDatabase::class.java,
            "stellae.db"
        )
            .addCallback(PrepopulateCallback(databaseProvider))
            .build()
    }

    @Provides
    fun providePlanetDao(db: StellaeDatabase): PlanetDao = db.planetDao()

    @Provides
    fun provideSignDao(db: StellaeDatabase): SignDao = db.signDao()

    @Provides
    fun provideDignityDao(db: StellaeDatabase): DignityDao = db.dignityDao()

    @Provides
    fun provideCardDao(db: StellaeDatabase): CardDao = db.cardDao()

    @Provides
    fun provideFsrsDao(db: StellaeDatabase): FsrsDao = db.fsrsDao()

    @Provides
    fun provideUserProgressDao(db: StellaeDatabase): UserProgressDao = db.userProgressDao()

    @Provides
    fun provideSessionLogDao(db: StellaeDatabase): SessionLogDao = db.sessionLogDao()

    @Provides
    fun provideLotDao(db: StellaeDatabase): LotDao = db.lotDao()

    @Provides
    fun provideConfusionPairDao(db: StellaeDatabase): ConfusionPairDao = db.confusionPairDao()

    @Provides
    fun provideAchievementDao(db: StellaeDatabase): AchievementDao = db.achievementDao()
}
