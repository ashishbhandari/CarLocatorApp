package com.carlocator.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.carlocator.AppExecutors
import com.carlocator.data.network.Resource


/**
 * @author ashish <ashish.bhandari>
 */
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor(
        private val appExecutors: AppExecutors
) {

    /**
     * Final LiveData Result
     */
    private val result = MediatorLiveData<Resource<ResultType?>>()


    init {
        // Send loading state to UI
        result.value = Resource.loading()
        val dbSource = this.loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> setValue(Resource.success(newData)) }
            }
        }
    }

    /**
     * Fetch the data from network and persist into DB and then
     * send it back to UI.
     */
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        result.addSource(dbSource) { result.setValue(Resource.loading()) }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            response?.apply {
                if (status.isSuccessful()) {
                    saveResultAndReInit(response)
                } else {
                    onFetchFailed()
                    result.addSource(dbSource) {
                        result.setValue(Resource.error(message))
                    }
                }
            }
        }
    }

    private fun saveResultAndReInit(response: Resource<RequestType>) {
        appExecutors.diskIO().execute {
            processResponse(response)?.let { requestType -> saveCallResult(requestType) }
            appExecutors.mainThread().execute {
                result.addSource(loadFromDb()) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType?>) {
        if (result.value != newValue) result.value = newValue
    }


    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    fun asLiveData(): LiveData<Resource<ResultType?>> {
        return result
    }

    @WorkerThread
    private fun processResponse(response: Resource<RequestType>): RequestType? {
        return response.data
    }

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    // Called to get the cached data from the database
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<Resource<RequestType>>

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected fun onFetchFailed() {
    }
}