package com.liweiyap.foxtrot

import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T> initOnce(): ReadWriteProperty<Any, T> = InitOnceProperty()

/**
 * https://stackoverflow.com/questions/48443167/kotlin-lateinit-to-val-or-alternatively-a-var-that-can-set-once
 */
class InitOnceProperty<T> : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (valueRef.get() == null) {
            throw IllegalStateException("InitOnceProperty::getValue(): value is not already initialized.")
        }
        return valueRef.get() as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        // compares valueRef with expected (null);
        // if equal to expected (null), then replaces it with new value and returns TRUE.
        if (valueRef.compareAndSet(null, value)) {
            return
        }
        throw IllegalStateException("InitOnceProperty::setValue(): value is already initialized.")
    }

    private val valueRef = AtomicReference<T>()
}