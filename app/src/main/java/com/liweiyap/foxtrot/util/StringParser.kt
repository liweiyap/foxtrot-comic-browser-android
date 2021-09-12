package com.liweiyap.foxtrot.util

object StringParser {

    @JvmStatic fun secureWebProtocol(urlString: String): String {
        return urlString.replaceFirst("http://", "https://")
    }

    // https://www.cl.cam.ac.uk/~mgk25/ucs/quotes.html
    @JvmStatic fun standardiseUnicodeApostrophe(string: String): String {
        return string.replace("’", "'")
    }
}