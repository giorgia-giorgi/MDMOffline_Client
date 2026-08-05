package com.dbg.mdm_offline_client.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    ITALIAN("it");

    companion object {
        /** Uses the language part of a locale tag (e.g. "it-IT" → Italian). Falls back to English. */
        fun fromLocaleTag(tag: String): AppLanguage {
            val language = tag
                .substringBefore('-')
                .substringBefore('_')
                .lowercase()
            return entries.firstOrNull { it.code == language } ?: ENGLISH
        }
    }
}

enum class ConnectionPhase {
    Idle,
    Discovering,
    Registering,
    Connected,
    Error,
}
