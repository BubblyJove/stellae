package com.stellae.app.di

import com.stellae.app.domain.fsrs.ConfusionDetector
import com.stellae.app.domain.fsrs.FsrsScheduler
import com.stellae.app.domain.gamification.RankSystem
import com.stellae.app.domain.gamification.StreakManager
import com.stellae.app.domain.gamification.XpCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing domain-layer singletons that have no Room or Android
 * framework dependencies, making them straightforward to construct.
 *
 * These are pure Kotlin classes that could alternatively be annotated with
 * [@Inject constructor] and [@Singleton]; they are wired here explicitly so
 * that construction arguments (e.g. [FsrsScheduler.desiredRetention]) remain
 * configurable from one place.
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    /** FSRS-5 spaced-repetition scheduler at the default 90 % retention target. */
    @Provides
    @Singleton
    fun provideFsrsScheduler(): FsrsScheduler = FsrsScheduler()

    /** XP award calculator — stateless, safe to share across the whole app. */
    @Provides
    @Singleton
    fun provideXpCalculator(): XpCalculator = XpCalculator()

    /** Daily streak evaluator — stateless, no mutable state inside the class. */
    @Provides
    @Singleton
    fun provideStreakManager(): StreakManager = StreakManager()

    /** Rank/progress calculator derived purely from accumulated XP. */
    @Provides
    @Singleton
    fun provideRankSystem(): RankSystem = RankSystem()

    /**
     * In-memory confusion pattern tracker.
     *
     * The detector accumulates wrong-answer pairs for the lifetime of the
     * process; data is intentionally NOT persisted between app restarts so
     * the learner always gets fresh pattern detection each session.
     */
    @Provides
    @Singleton
    fun provideConfusionDetector(): ConfusionDetector = ConfusionDetector()
}
