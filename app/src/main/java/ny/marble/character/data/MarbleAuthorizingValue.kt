package ny.marble.character.data

data class MarbleAuthorizingValue(
    val timeStamp: Long,
    val publicKey: String,
    val hashValue: String
)
