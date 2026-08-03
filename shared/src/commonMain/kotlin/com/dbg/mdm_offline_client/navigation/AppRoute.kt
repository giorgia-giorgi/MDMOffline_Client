package com.dbg.mdm_offline_client.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Tutorial : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Help : AppRoute
}

val appNavSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoute.Tutorial::class, AppRoute.Tutorial.serializer())
            subclass(AppRoute.Home::class, AppRoute.Home.serializer())
            subclass(AppRoute.Help::class, AppRoute.Help.serializer())
        }
    }
}
