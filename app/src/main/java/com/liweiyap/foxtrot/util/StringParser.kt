package com.liweiyap.foxtrot.util

object StringParser {

    @JvmStatic fun secureWebProtocol(urlString: String): String {
        return urlString.replaceFirst("http://", "https://")
    }
}