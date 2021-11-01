package com.example.pokersimulator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokersimulator.domain_object.CardData
import com.example.pokersimulator.domain_object.CardType

class GameBoardViewModel: ViewModel() {
    val drawPileLiveData: MutableLiveData<MutableList<CardData>> by lazy {
        MutableLiveData(initDrawPile())
    }
    val yourHandLiveData = MutableLiveData<MutableList<CardData>>(mutableListOf())
    val yourPlayedPileLiveData = MutableLiveData<MutableList<CardData>>(mutableListOf())
    val opponentPlayedPileLiveData = MutableLiveData<MutableList<CardData>>(mutableListOf())
    // The name of the current turn's owner. Empty string means no one is acting now
    var currentPlayerLiveData = MutableLiveData("")

    private fun initDrawPile(): MutableList<CardData> {
        val drawPile = mutableListOf<CardData>()
        for (i in 1..13) {
            for (j in CardType.values()) {
                if (j == CardType.JOKER) break
                drawPile.add(CardData(j, i))
            }
        }
        drawPile.add(CardData(CardType.JOKER, 1))
        drawPile.add(CardData(CardType.JOKER, 2))
        return drawPile
    }

    /**
     * Shuffle the current draw pile regardless of its emptiness
     */
    fun shuffleDrawPile(): String {
        drawPileLiveData.value = drawPileLiveData.value!!.shuffled().toMutableList()
        return " shuffled the draw pile\n"
    }

    /**
     * Draw one card from the draw pile to your hand.
     * The caller should be responsible to ensure the draw pile being non-empty
     */
    fun drawCard(): String {
        val drawPile = drawPileLiveData.value!!
        val yourHand = yourHandLiveData.value!!
        val card = drawPile.removeLast()
        yourHand.add(card)
        drawPileLiveData.value = drawPile
        yourHandLiveData.value = yourHand
        return " drew a card\n"
    }

    /**
     * Put the selected card back to the top of the draw pile
     */
    fun undoDraw(position: Int): String {
        val drawPile = drawPileLiveData.value!!
        val yourHand = yourHandLiveData.value!!
        val card = yourHand.removeAt(position)
        drawPile.add(card)
        drawPileLiveData.value = drawPile
        yourHandLiveData.value = yourHand
        return " put a card back to the draw pile\n"
    }

    /**
     * Play the selected card to your played pile from your hand
     */
    fun play(position: Int): String {
        val yourHand = yourHandLiveData.value!!
        val yourPlayedPile = yourPlayedPileLiveData.value!!
        val card = yourHand.removeAt(position)
        yourPlayedPile.add(card)
        yourHandLiveData.value = yourHand
        yourPlayedPileLiveData.value = yourPlayedPile
        return if (card.faceUp) " played $card\n"
        else " played a flipped card\n"
    }

    /**
     * Retrieve the selected card from the played pile to your hand
     */
    fun undoPlay(position: Int): String {
        val yourHand = yourHandLiveData.value!!
        val yourPlayedPile = yourPlayedPileLiveData.value!!
        val card = yourPlayedPile.removeAt(position)
        yourHand.add(card)
        yourHandLiveData.value = yourHand
        yourPlayedPileLiveData.value = yourPlayedPile
        return if (card.faceUp) " put $card back to his hand\n"
        else " put a flipped card back to his hand\n"
    }

    /**
     * Flip a card in a pile other than the opponent's played pile.
     * If the pile is the draw pile, only flip the top card. Otherwise flip the card at the
     * specified position.
     */
    fun flipCard(pileId: Int, position: Int): String {
        when (pileId) {
            R.id.drawPile -> {
                val changePile = drawPileLiveData.value!!
                changePile.last().faceUp = !changePile.last().faceUp
                drawPileLiveData.value = changePile
                return " flipped the top most card in the draw pile\n"
            }
            R.id.yourPlayedPile -> {
                val changePile = yourPlayedPileLiveData.value!!
                changePile[position].faceUp = !changePile[position].faceUp
                yourPlayedPileLiveData.value = changePile
                return " flipped a card in his played pile\n"
            }
            R.id.yourHand -> {
                val changePile = yourHandLiveData.value!!
                changePile[position].faceUp = !changePile[position].faceUp
                yourHandLiveData.value = changePile
                return ""
            }
        }
        return ""
    }

    /**
     * Sort the cards in your hand with the given comparator
     */
    fun sortYourHand(comparator: Comparator<CardData>) {
        val yourHand = yourHandLiveData.value!!
        yourHand.sortWith(comparator)
        yourHandLiveData.value = yourHand
    }
}
