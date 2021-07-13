package com.github.bun133.flylib2.resourcepack

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.bukkit.entity.Player
import java.io.File
import java.net.InetSocketAddress
import java.io.OutputStream


class ResourcePackDistributor(var address: String, var hash: String) {
    fun submit(p: Player) {
        p.setResourcePack(address, hash)
    }
}

class SimpleFileHTTPServer(val port: Int, val file: File) {
    val server = HttpServer.create(InetSocketAddress(port), 0)
    fun start() {
        server.createContext("/${file.name}") {
            val data: ByteArray = file.inputStream().readAllBytes()
            it.sendResponseHeaders(200, data.size.toLong())
            val os: OutputStream = it.responseBody
            os.write(data)
            os.close()
        }
    }
}

//class FileHandler(val file:File): HttpExchange() {
//
//}