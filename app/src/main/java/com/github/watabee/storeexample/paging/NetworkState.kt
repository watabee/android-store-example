package com.github.watabee.storeexample.paging

sealed class NetworkState {
    class Loading(val isInitial: Boolean) : NetworkState()
    class Loaded(val isInitial: Boolean) : NetworkState()
    class Error(val isInitial: Boolean, val message: String): NetworkState()
}