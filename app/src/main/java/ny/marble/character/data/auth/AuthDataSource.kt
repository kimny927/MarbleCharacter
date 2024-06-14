package ny.marble.character.data.auth

import ny.marble.character.data.MarbleAuthorizingValue
import java.security.MessageDigest

class AuthDataSource {

    companion object {
        init {
            System.loadLibrary("key")
        }
    }

    private external fun getPublicKey(): String

    private external fun getPrivateKey(): String

    fun getMarbleAuthorizingValue() : MarbleAuthorizingValue {
        val timeStamp = System.currentTimeMillis()
        val publicKey = getPublicKey()
        val privateKey = getPrivateKey()
        return MarbleAuthorizingValue(
            timeStamp = timeStamp,
            publicKey = publicKey,
            hashValue = (timeStamp.toString() + privateKey + publicKey).md5()
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(this.toByteArray())
        return digest.toHexString()
    }
}