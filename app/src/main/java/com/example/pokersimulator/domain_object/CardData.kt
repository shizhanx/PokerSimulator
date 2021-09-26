package com.example.pokersimulator.domain_object

/**
 * Representation of one single poker card
 */
data class CardData(
    val cardType: CardType,
    val cardNumber: Int,
) {
    // TODO serialize and deserialize a card to and form a String
    override fun toString(): String {
        return cardType.name + cardNumber
    }
}
