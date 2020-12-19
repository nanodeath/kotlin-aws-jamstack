package org.jamstack.engine

import org.koin.core.Koin
import kotlin.reflect.KClass

sealed class Container<out T : Any> {
    class Type<T : Any>(val type: KClass<T>) : Container<T>() {
        override fun toString() = "Type($type)"
    }
    class Instance<T : Any>(val instance: T) : Container<T>() {
        override fun toString() = "Instance<${instance.javaClass}>($instance)"
    }
}

fun <T : Any> Container<T>.getInstance(koin: Koin): T = when (this) {
    is Container.Type -> koin.get(this.type)
    is Container.Instance -> this.instance
}