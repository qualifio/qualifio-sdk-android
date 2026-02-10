package com.qualifio

/**
 * A callback interface for handling asynchronous operations in the Qualifio SDK.
 *
 * This interface defines methods to receive the result or an error from asynchronous operations.
 *
 * @param T The type of the result expected on a successful operation.
 */
public interface QualifioCallback<T> {
    /**
     * Called when the asynchronous operation completes successfully.
     *
     * @param result The result of the successful operation of type [T].
     */
    public fun onSuccess(result: T)

    /**
     * Called when the asynchronous operation fails with an exception.
     *
     * @param e The exception detailing the reason for the failure.
     */
    public fun onFailure(e: Exception)
}
