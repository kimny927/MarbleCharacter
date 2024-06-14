package ny.marble.character.data.model

data class CharacterApiModel(
    val id: Int,
    val name: String,
    val description: String,
    val thumbnail: ImageApiModel
)
