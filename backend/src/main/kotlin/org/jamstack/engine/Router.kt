package org.jamstack.engine

import kotlin.reflect.KClass

class Router {
    private val routes = mutableMapOf<String, RouteConfiguration>()

    /**
     * Adds a mapping so that when a request comes in for the given [url], [controller] will be invoked.
     *
     * @param url URL fragment to match. No leading / required. Ex: "foo", "foo/bar".
     * @param controller controller to handle this request (instantiated using Koin).
     * @param cb callback to apply additional configuration.
     */
    fun add(url: String, controller: KClass<out Controller>, cb: MutableRouteConfiguration.() -> Unit = {}) {
        routes.putIfAbsent(url, MutableRouteConfiguration(controller).apply(cb).toRouteConfiguration())
    }

    fun resolve(url: String): RouteConfiguration? = routes[url]
}

/**
 * Adds a mapping so that when a request comes in for the given [url], the [controller][T] will be invoked.
 *
 * @param url URL fragment to match. No leading / required. Ex: "foo", "foo/bar".
 * @param T type of the controller to handle this request (instantiated using Koin).
 * @param cb callback to apply additional configuration.
 */
inline fun <reified T : Controller> Router.add(url: String, noinline cb: MutableRouteConfiguration.() -> Unit = {}) =
    add(url, T::class, cb)

class MutableRouteConfiguration(
    var controller: KClass<out Controller>,
    val interceptors: MutableList<Container<Interceptor>> = mutableListOf()
) {
    fun toRouteConfiguration() = RouteConfiguration(controller, interceptors.toList())
}

/**
 * Add the given [interceptor][type] to the list of interceptors to be applied. Interceptors are applied in FIFO
 * order. If you add interceptors in order A, B, C, they'll be invoked in order A, then B, then C.
 *
 * [type] will be instantiated using Koin.
 */
fun <T : Interceptor> MutableRouteConfiguration.addInterceptor(type: KClass<T>) {
    interceptors.add(Container.Type(type))
}

/**
 * Add the given [interceptor][instance] to the list of interceptors to be applied. Interceptors are applied in FIFO
 * order. If you add interceptors in order A, B, C, they'll be invoked in order A, then B, then C.
 */
fun MutableRouteConfiguration.addInterceptor(instance: Interceptor) {
    interceptors.add(Container.Instance(instance))
}

data class RouteConfiguration(
    val controller: KClass<out Controller>,
    val interceptors: List<Container<Interceptor>> = emptyList()
)
