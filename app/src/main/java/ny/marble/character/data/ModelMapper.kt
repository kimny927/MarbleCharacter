package ny.marble.character.data

import ny.marble.character.data.model.CharacterDataWrapperApiModel
import ny.marble.character.presentation.model.CharacterCardModel
import ny.marble.character.presentation.model.CharacterCardWrapperModel

fun CharacterDataWrapperApiModel.toModel(): CharacterCardWrapperModel {
    val list =
        this.data.results.map {
            CharacterCardModel(
                id = it.id,
                name = it.name,
                thumbnail = "${it.thumbnail.path}.${it.thumbnail.extension}",
                description = it.description,
                favorite = false
            )
        }

    return CharacterCardWrapperModel(
        offset = this.data.offset,
        cardList = list,
        totalCount = this.data.total
    )
}
