package com.dbg.mdm_offline_client.net

import com.dbg.mdm_offline_client.protocol.ProtocolConstants
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.concurrent.atomic.AtomicBoolean

actual object TcpServer {
    private val started = AtomicBoolean(false)

    actual fun start() {
        if (!started.compareAndSet(false, true)) return
        embeddedServer(CIO, port = ProtocolConstants.CLIENT_HTTP_PORT) {
            routing {
                get("/ping") {
                    call.respondText("pong")
                }
            }
        }.start(wait = false)
    }
}
