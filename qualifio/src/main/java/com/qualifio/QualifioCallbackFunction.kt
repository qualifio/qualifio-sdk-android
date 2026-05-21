package com.qualifio

public interface QualifioCallbackFunction<A, T> {
    public fun invoke(
        argument: A,
        callback: QualifioCallback<T>
    )
}