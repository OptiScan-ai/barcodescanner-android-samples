/*
 * Created by OPTISOL ganesh.k on 18/6/21 2:18 PM
 * Copyright (c) 2021.  All rights reserved.
 * Last modified 18/6/21 2:07 PM.
 */

package com.optiscan.demo.barcode.utils

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}