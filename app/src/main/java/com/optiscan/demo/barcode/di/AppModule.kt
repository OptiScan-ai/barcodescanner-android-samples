/*
 * Created by OPTISOL ganesh.k on 18/6/21 2:18 PM
 * Copyright (c) 2021.  All rights reserved.
 * Last modified 18/6/21 2:07 PM.
 */

package com.optiscan.demo.barcode.di

import com.optiscan.demo.barcode.main.DefaultMainRepository
import com.optiscan.demo.barcode.main.MainRepository
import com.optiscan.demo.barcode.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMainRepository(): MainRepository = DefaultMainRepository()

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}