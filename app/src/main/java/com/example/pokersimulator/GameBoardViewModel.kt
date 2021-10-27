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
    fun shuffleDrawPile() {
        drawPileLiveData.value = drawPileLiveData.value!!.shuffled().toMutableList()
    }

    /**
     * Draw one card from the draw pile to your hand.
     * The caller should be responsible to ensure the draw pile being non-empty
     */
    fun drawCard() {
        val drawPile = drawPileLiveData.value!!
        val yourHand = yourHandLiveData.value!!
        val card = drawPile.removeLast()
        card.faceUp = true
        yourHand.add(card)
        drawPileLiveData.value = drawPile
        yourHandLiveData.value = yourHand
    }

    /**
     * Put the selected card back to the top of the draw pile
     */
    fun undoDraw(position: Int) {
        val drawPile = drawPileLiveData.value!!
        val yourHand = yourHandLiveData.value!!
        val card = yourHand.removeAt(position)
        card.faceUp = false
        drawPile.add(card)
        drawPileLiveData.value = drawPile
        yourHandLiveData.value = yourHand
    }

    /**
     * Play the selected card to your played pile from your hand
     */
    fun play(position: Int) {
        val yourHand = yourHandLiveData.value!!
        val yourPlayedPile = yourPlayedPileLiveData.value!!
        yourPlayedPile.add(yourHand.removeAt(position))
        yourHandLiveData.value = yourHand
        yourPlayedPileLiveData.value = yourPlayedPile
    }

    /**
     * Retrieve the selected card from the played pile to your hand
     */
    fun undoPlay(position: Int) {
        val yourHand = yourHandLiveData.value!!
        val yourPlayedPile = yourPlayedPileLiveData.value!!
        yourHand.add(yourPlayedPile.removeAt(position))
        yourHandLiveData.value = yourHand
        yourPlayedPileLiveData.value = yourPlayedPile
    }

    /**
     * Flip a card in a pile other than the opponent's played pile.
     * If the pile is the draw pile, only flip the top card. Otherwise flip the card at the
     * specified position.
     */
    fun flipCard(pileId: Int, position: Int) {
        when (pileId) {
            R.id.draw_pile -> {
                val changePile = drawPileLiveData.value!!
                changePile.last().faceUp = !changePile.last().faceUp
                drawPileLiveData.value = changePile
            }
            R.id.your_played_pile -> {
                val changePile = yourPlayedPileLiveData.value!!
                changePile[position].faceUp = !changePile[position].faceUp
                yourPlayedPileLiveData.value = changePile
            }
            R.id.your_hand -> {
                val changePile = yourHandLiveData.value!!
                changePile[position].faceUp = !changePile[position].faceUp
                yourHandLiveData.value = changePile
            }
        }
    }
}
