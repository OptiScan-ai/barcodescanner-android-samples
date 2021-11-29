/*
 * Created by OPTISOL ganesh.k on 18/6/21 2:18 PM
 * Copyright (c) 2021.  All rights reserved.
 * Last modified 18/6/21 2:07 PM.
 */

package com.optiscan.demo.barcode.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.optiscan.demo.barcode.utils.DispatcherProvider

class MainViewModel @ViewModelInject constructor(
        private val dispatchers: DispatcherProvider
) : ViewModel() {


}