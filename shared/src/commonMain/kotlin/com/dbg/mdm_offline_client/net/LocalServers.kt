package com.dbg.mdm_offline_client.net

/** Starts the always-on client TCP (`/ping`) and UDP (discover) servers. */
fun startLocalServers(tcpServer: TcpServer = TcpServer, udpServer: UdpServer = UdpServer) {
    tcpServer.start()
    udpServer.start()
}
