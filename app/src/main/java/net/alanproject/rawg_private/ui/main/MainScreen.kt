package net.alanproject.rawg_private.ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import net.alanproject.domain.model.params.listParamsToJsonString
import net.alanproject.domain.model.response.Game
import net.alanproject.rawg_private.common.Constants.Companion.ACTION
import net.alanproject.rawg_private.common.Constants.Companion.ACTION_PARAMS
import net.alanproject.rawg_private.common.Constants.Companion.PUZZLE_PARAMS
import net.alanproject.rawg_private.common.Constants.Companion.RACING_PARAMS
import net.alanproject.rawg_private.common.Constants.Companion.RELEASE_PARAMS
import net.alanproject.rawg_private.common.Constants.Companion.STRATEGY_PARAMS
import net.alanproject.rawg_private.common.Constants.Companion.UPCOMING_PARAMS
import net.alanproject.rawg_private.common.RetrySection
import net.alanproject.rawg_private.ui.theme.Charcoal200
import net.alanproject.rawg_private.ui.theme.Charcoal500
import net.alanproject.rawg_private.ui.theme.Grey200
import net.alanproject.rawg_private.ui.theme.Yellow200
import net.alanproject.rawg_private.ui.widget.MetaScoreText
import net.alanproject.rawg_private.ui.widget.RatingOnCircle
import net.alanproject.rawg_private.ui.widget.RatingText
import timber.log.Timber


@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {

    val newTrendingList by remember { viewModel.trendingListState }

    val rankList by remember { viewModel.rankListState }

    val upcomingList by remember { viewModel.upcomingListState }
    val newReleaseList by remember { viewModel.newReleaseListState }

    val actionList by remember { viewModel.actionListState }
    val strategyList by remember { viewModel.strategyListState }
    val puzzleList by remember { viewModel.puzzleListState }
    val racingList by remember { viewModel.racingListState }

    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }

    Timber.d("onLoadGames in View")
    viewModel.onLoadGames()

    Scaffold(topBar = { AppBar() }) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp)

        ) {
            TrendingGames(newTrendingList, navController, text = "New & Trending")

            HotGames(
                newReleaseList,
                upcomingList,
                navController,
                gameCnt = null,
                text = "What's Hot Now"
            )

            Ranking(rankList, navController, text = "Ranking")

            PopularGamesByGenre(
                actionList,
                strategyList,
                puzzleList,
                racingList,
                navController,
                text = "Popular",
                gameCnt = null
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Yellow200)
            }
            if (loadError.isNotEmpty()) {
                RetrySection(error = loadError) {
                    viewModel.onLoadGames()
                }
            }
        }

    }


}

@Composable
private fun TrendingGames(
    games: List<Game>?,
    navController: NavHostController?,
    text: String
) {
//    Timber.d("[TrendingGames] games: $games")
    if (!games.isNullOrEmpty()) {
        TopContent(games, navController, text)
    }
}

@Composable
private fun Ranking(
    rankGames: List<Game>,
    navController: NavHostController?,
    text: String
) {
    if (!rankGames.isNullOrEmpty()) {
        MainTitleText(text) { navController?.navigate("list/2") }
        RankGames(rankGames, navController)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewRanking() {
    Ranking(listOf(
        Game(name = "game 1"),
        Game(name = "game 2"),
        Game(name = "game 3"),
        Game(name = "game 4"),
        Game(name = "game 5"),
        Game(name = "game 6")
    ), null, "Test")
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewRankGame() {
    RankGame(null, Game(name = "Halo",))
}

@Composable
private fun HotGames(
    releaseGames: List<Game>?,
    upcomingGames: List<Game>?,
    navController: NavHostController?,
    gameCnt: Int?,
    text: String
) {
    val modifier = Modifier
        .width(240.dp)
        .wrapContentHeight()
        .padding(4.dp)

    if (!releaseGames.isNullOrEmpty() ||!upcomingGames.isNullOrEmpty()){
        MainTitleText(text) { navController?.navigate("list/1") }
    }

    Surface(
        color = Charcoal500,
        elevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (!releaseGames.isNullOrEmpty()) {
                SubTitleText(title = "New Release") {
                    Timber.d("[LoadingError] navigate 1")

                    val jsonString = listParamsToJsonString(RELEASE_PARAMS)
                    navController?.navigate("list/$jsonString")
                }
                HorizontalList(releaseGames, navController, modifier, gameCnt)
            }
            if (!upcomingGames.isNullOrEmpty()) {
                SubTitleText(title = "Upcoming Games") {
                    Timber.d("[LoadingError] navigate 2")
                    val jsonString = listParamsToJsonString(UPCOMING_PARAMS)
                    navController?.navigate("list/$jsonString")
                }
                HorizontalList(upcomingGames, navController, modifier, gameCnt)
            }
        }
    }
}

@Composable
private fun PopularGamesByGenre(
    actionGames: List<Game>?,
    strategyGames: List<Game>?,
    puzzleGames: List<Game>?,
    racingGames: List<Game>?,
    navController: NavHostController?,
    text: String,
    gameCnt: Int?
) {

    val modifier = Modifier
        .width(240.dp)
        .wrapContentHeight()
        .padding(4.dp)
    MainTitleText(text) { navController?.navigate("list/3") }

    Surface(
        color = Charcoal500,
        elevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (!actionGames.isNullOrEmpty()) {
                SubTitleText(title = "Action / Adventure / RPG") {
                    val jsonString = listParamsToJsonString(ACTION_PARAMS)
                    navController?.navigate("list/$jsonString")
                }
                HorizontalList(actionGames, navController, modifier, gameCnt)
            }
            if (!strategyGames.isNullOrEmpty()) {
                SubTitleText(title = "Strategy / Simulation") {
                    val jsonString = listParamsToJsonString(STRATEGY_PARAMS)
                    navController?.navigate("list/$jsonString")
                }
                HorizontalList(strategyGames, navController, modifier, gameCnt)
            }
            if (!puzzleGames.isNullOrEmpty()) {

                SubTitleText(title = "Puzzle / Arcade") {
                    val jsonString = listParamsToJsonString(PUZZLE_PARAMS)
                    navController?.navigate("list/$jsonString")
                }
                HorizontalList(puzzleGames, navController, modifier, gameCnt)
            }

            if (!racingGames.isNullOrEmpty()) {
                SubTitleText(title = "Racing / Sports") {
                    val jsonString = listParamsToJsonString(RACING_PARAMS)
                    navController?.navigate("list/$jsonString")
                }
                HorizontalList(racingGames, navController, modifier, gameCnt)
            }
        }

    }
}

@Composable
fun TopContent(
    topGames: List<Game>?,
    navController: NavHostController?,
    text: String
) {
//Timber.d("[TopContent] topGames: $topGames")
    if (!topGames.isNullOrEmpty()) {
        val displayedGame = topGames.first()
        MainTitleText(text) {}

        Surface(
            color = Charcoal500,
            elevation = 8.dp,

            ) {

            val clickAction: () -> Unit = { navController?.navigate("detail/${displayedGame.id}") }
            Card(
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.Top)
                    .clickable(onClick = { clickAction.invoke() })
                    .padding(8.dp),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                GameScreenWithText(
                    displayedGame, modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@Composable
fun RankGames(
    games: List<Game>,
    navController: NavHostController?
) {
    Surface(
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            games.forEach { game ->
                RankGame(navController, game)
            }
        }
    }
}

@Composable
private fun RankGame(
    navController: NavHostController?,
    game: Game
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 6.dp)
            .clickable(onClick = {
                navController?.navigate("detail/${game.id}")
            })
    ) {
        Row(
            modifier = Modifier
                .background(color = Charcoal200)
        ) {
            Image(
                //loaded asynchronously
                painter = rememberImagePainter(
                    data = game.backgroundImage,
                    builder = {
                        transformations(
                            RoundedCornersTransformation(),
                        )
                    }
                ),
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp),
                contentDescription = "game picture description",
                contentScale = ContentScale.Crop
            )

            GameDescription(
                game,
                modifier = Modifier
                    .height(80.dp)
                    .padding(8.dp),
                style = TextStyle(color = Color.White, fontSize = 16.sp),
            )
        }
    }
}


@Composable
fun HorizontalList(
    games: List<Game>,
    navController: NavHostController?,
    modifier: Modifier,
    gameCnt: Int?
) {

    val displayedGames: List<Game> = games.take(gameCnt ?: games.size)

    LazyRow {
        items(displayedGames) { game ->
            val clickAction: () -> Unit = {
                navController?.navigate("detail/${game.id}")
            }
            Card(

                shape = RoundedCornerShape(15.dp),
                elevation = 2.dp,
                modifier = modifier
                    .clickable(onClick = { clickAction.invoke() })
            ) {

                GameScreenWithText(
                    game,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }
        }


    }
}

@Composable
fun MainTitleText(title: String, clickAction: () -> Unit) {
    Text(
        text = title,
        style = TextStyle(fontSize = 20.sp, color = Yellow200, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
    )
}

@Composable
fun SubTitleText(title: String, clickAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(4.dp)
            .clickable(onClick = { clickAction.invoke() }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {

        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
            Divider(
                color = Yellow200,
                modifier = Modifier
                    .height(20.dp)
                    .width(2.dp)
            )
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "SEE MORE",
            color = Color.LightGray,
            style = TextStyle(
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun GameScreenWithText(game: Game, modifier: Modifier) {
    Box(modifier = modifier) {
        Image(
            //loaded asynchronously
            painter = rememberImagePainter(
                data = game.backgroundImage,
                builder = {
                    transformations(
                        RoundedCornersTransformation(),
                    )
                }
            ),
//            modifier = modifier,
            contentDescription = "game picture description",
            contentScale = ContentScale.Crop
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 300f
                    )
                )
        )
        Text(
            text = game.name,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            style = TextStyle(color = Color.White, fontSize = 16.sp),
        )
        RatingOnCircle(score = game.rating, modifier = Modifier.align(Alignment.TopEnd))
    }

}

@Composable
fun GameDescription(game: Game, modifier: Modifier, style: TextStyle) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingText(game.rating)
            MetaScoreText(game.metacritic)
        }
        Text(
            text = game.name,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Release: ${game.released}",
            style = TextStyle(fontSize = 12.sp),
            color = Grey200,
            maxLines = 1
        )
    }
}

@Composable
fun AppBar() {
    TopAppBar(
        navigationIcon = {
            Icon(
                Icons.Default.Settings,
                contentDescription = "",
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        },
        title = { Text("APPLICATION NAME") }
    )
}





