package net.alanproject.rawg_private.common

import net.alanproject.domain.model.params.ListParams


class Constants{
    companion object {
        //Period
        val TRENDING_PERIOD: String = dateParsing(past = true, ONE_WEEK)
        val RANK_PERIOD: String = dateParsing(past = true, SIX_MONTH)
        val UPCOMING_PERIOD: String = dateParsing(past = false, ONE_MONTH)
        val RELEASE_PERIOD: String = dateParsing(past = true, ONE_MONTH)
        val GENRES_PERIOD: String = dateParsing(past = true, SIX_MONTH)

        //Genres
        val ACTION:String ="2,3,4,5"
        val STRATEGY:String ="10,14"
        val PUZZLE:String ="7,11,28,17"
        val RACING:String ="1,15"



        val TRENDING_PARAMS: ListParams = ListParams(
            period = TRENDING_PERIOD,
            genres = null,
            ordering = null
        )

        val UPCOMING_PARAMS:ListParams = ListParams(
            period = UPCOMING_PERIOD,
            genres = null,
            ordering = null
        )

        val RELEASE_PARAMS:ListParams = ListParams(
            period = RELEASE_PERIOD,
            genres = null,
            ordering = null
        )

        val RANK_PARAMS:ListParams = ListParams(
            period = RANK_PERIOD,
            genres = null,
            ordering = null
        )

        val ACTION_PARAMS:ListParams = ListParams(
            period = GENRES_PERIOD,
            genres = ACTION,
            ordering = null
        )
        val STRATEGY_PARAMS:ListParams = ListParams(
            period = GENRES_PERIOD,
            genres = STRATEGY,
            ordering = null
        )
        val PUZZLE_PARAMS:ListParams = ListParams(
            period = GENRES_PERIOD,
            genres = PUZZLE,
            ordering = null
        )
        val RACING_PARAMS:ListParams = ListParams(
            period = GENRES_PERIOD,
            genres = RACING,
            ordering = null
        )
    }
}



const val BASE_URL = "https://api.rawg.io/api/"
const val PAGE_SIZE = 20
const val HORIZONTAL_GAME_NUMBER = 5
const val ONE_WEEK = 7L
const val ONE_MONTH = 30L
const val TWO_MONTH = 60L
const val THREE_MONTH = 90L
const val SIX_MONTH = 180L
const val ONE_YEAR = 365L

fun mapToPeriod(categoryId:Int):String = when (categoryId) {
    1 -> Constants.TRENDING_PERIOD
    2 -> Constants.RANK_PERIOD
    3 -> Constants.UPCOMING_PERIOD
    else -> {
        Constants.TRENDING_PERIOD
    }
}