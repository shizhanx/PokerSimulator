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
        yourHand.add(drawPile.removeLast())
        drawPileLiveData.value = drawPile
        yourHandLiveData.value = yourHand
    }

    fun undoDraw(position: Int) {
        val drawPile = drawPileLiveData.value!!
        val yourHand = yourHandLiveData.value!!
        drawPile.add(yourHand.removeAt(position))
        drawPileLiveData.value = drawPile
        yourHandLiveData.value = yourHand
    }
}
