package com.example.pokersimulator.domain_object

data class CardData(
    val cardType: CardType,
    val cardNumber: Int,
) {
    override fun toString(): String {
        return cardType.name + cardNumber
    }
}
