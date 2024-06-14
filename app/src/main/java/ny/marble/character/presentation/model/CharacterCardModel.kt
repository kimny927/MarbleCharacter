package ny.marble.character.presentation.model

data class CharacterCardModel(
    val id: Int,
    val name: String,
    val thumbnail: String,
    val description: String,
    val favorite : Boolean
)
