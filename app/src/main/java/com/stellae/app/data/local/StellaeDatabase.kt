package com.stellae.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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
import com.stellae.app.data.local.entity.AchievementEntity
import com.stellae.app.data.local.entity.CardEntity
import com.stellae.app.data.local.entity.ConfusionPairEntity
import com.stellae.app.data.local.entity.DecanEntity
import com.stellae.app.data.local.entity.DomicileEntity
import com.stellae.app.data.local.entity.ExaltationEntity
import com.stellae.app.data.local.entity.FsrsDataEntity
import com.stellae.app.data.local.entity.LotEntity
import com.stellae.app.data.local.entity.PlanetEntity
import com.stellae.app.data.local.entity.SessionLogEntity
import com.stellae.app.data.local.entity.SignEntity
import com.stellae.app.data.local.entity.TermEntity
import com.stellae.app.data.local.entity.TriplicityEntity
import com.stellae.app.data.local.entity.UserProgressEntity

@Database(
    entities = [
        PlanetEntity::class,
        SignEntity::class,
        DomicileEntity::class,
        ExaltationEntity::class,
        TriplicityEntity::class,
        TermEntity::class,
        DecanEntity::class,
        LotEntity::class,
        CardEntity::class,
        FsrsDataEntity::class,
        UserProgressEntity::class,
        SessionLogEntity::class,
        ConfusionPairEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class StellaeDatabase : RoomDatabase() {

    abstract fun planetDao(): PlanetDao

    abstract fun signDao(): SignDao

    abstract fun dignityDao(): DignityDao

    abstract fun cardDao(): CardDao

    abstract fun fsrsDao(): FsrsDao

    abstract fun userProgressDao(): UserProgressDao

    abstract fun sessionLogDao(): SessionLogDao

    abstract fun lotDao(): LotDao

    abstract fun confusionPairDao(): ConfusionPairDao

    abstract fun achievementDao(): AchievementDao
}
