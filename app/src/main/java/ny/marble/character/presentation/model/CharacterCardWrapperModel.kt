package ny.marble.character.presentation.model

data class CharacterCardWrapperModel(
    val offset : Int,
    val totalCount: Int,
    val cardList : List<CharacterCardModel>
)
