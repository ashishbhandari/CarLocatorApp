package com.carlocator.data.network


/**
 * @author ashish <ashish.bhandari>
 *
 *this class expose network status to encapsulate both the data and its state
 */
data class Resource<ResultType>(var status: Status, var data: ResultType? = null, var message: String? = null) {

    companion object {

        fun <ResultType> success(data: ResultType): Resource<ResultType> {
            return Resource(Status.SUCCESS, data)
        }

        fun <ResultType> error(message: String?): Resource<ResultType> {
            return Resource(Status.ERROR, message = message)
        }


        fun <ResultType> loading(): Resource<ResultType> {
            return Resource(Status.LOADING)
        }

    }

}