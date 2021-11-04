package com.example.pokersimulator.domain_object

/**
 * Representation of one single poker card
 */
data class CardData(
    val cardType: CardType,
    val cardNumber: Int,
) {
    constructor() : this(CardType.JOKER, -1)
    var faceUp = false

    override fun toString(): String {
        return "${cardType.name.lowercase()}_$cardNumber"
    }

    companion object{
        val comparatorByType: Comparator<CardData> = Comparator { card1, card2 ->
            if (card1.cardType != card2.cardType)
                card1.cardType.compareTo(card2.cardType)
            else
                comparatorByNumber.compare(card1, card2)
        }

        val comparatorByNumber: Comparator<CardData> = Comparator { card1, card2 ->
            card1.cardNumber - card2.cardNumber
        }
    }
}
