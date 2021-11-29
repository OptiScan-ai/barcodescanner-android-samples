/*
 * Created by OPTISOL ganesh.k on 18/6/21 2:18 PM
 * Copyright (c) 2021.  All rights reserved.
 * Last modified 18/6/21 2:07 PM.
 */

package com.optiscan.demo.barcode.utils

sealed class Resource<T>(val data: T?, val message: String?) {

    class Success<T>(data: T) : Resource<T>(data, null)

    class Error<T>(message: String) : Resource<T>(null, message)

}