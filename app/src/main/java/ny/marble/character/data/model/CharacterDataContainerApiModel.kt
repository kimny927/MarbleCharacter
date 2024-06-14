package ny.marble.character.data.model

data class CharacterDataContainerApiModel(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<CharacterApiModel>
)
