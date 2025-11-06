package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.TestUser
import com.lifeleveling.app.ui.theme.AppTextStyles
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.StatsToolTip
import com.lifeleveling.app.ui.components.LevelAndProgress
import com.lifeleveling.app.ui.components.LifeExperienceToolTip
import com.lifeleveling.app.ui.models.StatsUi
import com.lifeleveling.app.ui.models.EditedStats
import kotlinx.coroutines.launch

@Composable
fun StatsScreen(
    uiState: StatsUi,
    onCancel: () -> Unit = {println("Cancel pressed")},
    onConfirm: () -> Unit = { println("Confirm pressed") },
    onCommit: (EditedStats) -> Unit = {}
                ) {
    val progress = (uiState.currentXp.toFloat() / uiState.xpToNext.toFloat()).coerceIn(0f,1f)
    var showHelpDialog = remember { mutableStateOf(false) }
    var showStatsDialog = remember { mutableStateOf(false) }
    var usedPoints by remember { mutableStateOf(uiState.usedLifePoints) }
    var remainingPoints by remember { mutableStateOf(uiState.unusedLifePoints - uiState.usedLifePoints) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppTheme.colors.Background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ){

                LevelAndProgress(
                    modifier = Modifier,
                    showLevelTip = showHelpDialog,
                )
                Column(){
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Text(
                            stringResource(R.string.stats),
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingThree.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),

                            )
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            tint = AppTheme.colors.FadedGray,
                            modifier = Modifier
                                .size(20.dp)
                                .offset(y = 9.74.dp)
                                .clickable {
                                    showStatsDialog.value = !showStatsDialog.value
                                }
                        )

                    }
                    Text(
                        text = stringResource(R.string.LPUsed, usedPoints,uiState.unusedLifePoints),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        ),

                        )

                    Text(
                        text = stringResource(R.string.LPRemaining, remainingPoints),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        ),

                        )
                }



                // Variables were being declared inside of lambda but confirm button is outside of lambda
                val strength = remember { mutableStateOf(uiState.strength) }
                val defense = remember { mutableStateOf(uiState.defense) }
                val intelligence = remember { mutableStateOf(uiState.intelligence) }
                val agility = remember { mutableStateOf(uiState.agility) }
                val health = remember { mutableStateOf(uiState.health) }

                HighlightCard(
                    modifier = Modifier.fillMaxWidth(),
                    outerPadding = 0.dp
                ) {
                    // create baseStats so user is not able to go lower than what is saved
                    val baseStats = mapOf(
                        "Strength" to strength.value,
                        "Defense" to defense.value,
                        "Intelligence" to intelligence.value,
                        "Agility" to agility.value,
                        "Health" to health.value,
                    )
                        //create a list of triples that contain the icon, label, and stat level
                    val statItems = listOf(
                        StatItem((R.drawable.sword), R.string.strength, AppTheme.colors.BrandOne, strength),
                        StatItem((R.drawable.shield), R.string.defense, AppTheme.colors.BrandTwo, defense),
                        StatItem((R.drawable.brain), R.string.intelligence, AppTheme.colors.SecondaryOne,intelligence),
                        StatItem((R.drawable.person_running), R.string.agility, AppTheme.colors.SecondaryTwo,agility),
                        StatItem((R.drawable.heart), R.string.health, AppTheme.colors.SecondaryThree,health)
                    )
                    //create column
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // for each item in the list create a row
                        // track index of list so divider line is not place after last
                        statItems.forEachIndexed { index, (iconRes, labelRes, color, statValue) ->
                            val labelString = stringResource(labelRes)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                //icons on the left and the text
                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(iconRes),
                                        tint = color,
                                        modifier = Modifier.size(38.dp),

                                    )

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(labelRes),
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.HeadingSix
                                    )
                                }

                                // stat controls on the right
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // minus button
                                    Image(
                                        painter = painterResource(id = R.drawable.minus),
                                        contentDescription = "Decrease $labelRes",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.FadedGray),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                                onClick = {
                                                    val base: Int = baseStats[labelString] ?: 0
                                                    if (statValue.value > base) {
                                                        statValue.value -= 1
                                                        usedPoints -= 1
                                                        remainingPoints += 1

                                                        // Adding a guard line
                                                        remainingPoints = (uiState.unusedLifePoints - usedPoints).coerceAtLeast(0)
                                                    }
                                                }
                                            )
                                            .padding(horizontal = 8.dp)
                                    )

                                    // stat value
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = statValue.value.toString(),
                                            color = AppTheme.colors.SecondaryOne,
                                            style = AppTheme.textStyles.HeadingFour,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }

                                    // plus button
                                    Image(
                                        painter = painterResource(id = R.drawable.plus),
                                        contentDescription = "Increase $labelRes",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.SecondaryTwo),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                                onClick = {
                                                    if (remainingPoints > 0) {
                                                        statValue.value += 1
                                                        usedPoints += 1
                                                        remainingPoints -= 1

                                                        // Adding a guard line
                                                        remainingPoints = (uiState.unusedLifePoints - usedPoints).coerceAtLeast(0)
                                                    }
                                                }
                                            )
                                            .padding(horizontal = 8.dp)
                                    )
                                }
                            }

                            // divider lines
                            if (index < statItems.lastIndex) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.separator_line),
                                    tint = AppTheme.colors.FadedGray,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    CustomButton(
                        width = 122.dp,
                        content = { Text(stringResource(R.string.Cancel), style = AppTextStyles.HeadingSix, color = AppTheme.colors.Background) },
                        onClick = { onCancel() },
                        backgroundColor = AppTheme.colors.Error,
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    CustomButton(
                       width = 122.dp,
                        content = { Text(stringResource(R.string.Confrim), style = AppTextStyles.HeadingSix, color = AppTheme.colors.Background) },
                        onClick = {
                            onCommit(
                                EditedStats(
                                    strength = strength.value,
                                    defense = defense.value,
                                    intelligence = intelligence,
                                    agility = agility.value,
                                    health = health.value,
                                    usedPoints = usedPoints,
                                    remainingPoints = remainingPoints
                                )
                            )
                            onConfirm()
                        },
                        backgroundColor = AppTheme.colors.SecondaryTwo,
                    )
                }
            }

            }
        }
        if(showHelpDialog.value){
        LifeExperienceToolTip(showHelpDialog)
        }
        if(showStatsDialog.value){
            StatsToolTip(showStatsDialog)
        }
    }

@Composable
fun StatsScreenRoute(
    repo: com.lifeleveling.app.data.FirestoreRepository = com.lifeleveling.app.data.FirestoreRepository(),
    logger: com.lifeleveling.app.util.ILogger = com.lifeleveling.app.util.ILogger.DEFAULT
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf<com.lifeleveling.app.data.Users?>(null) }

    // Load the current user once
    androidx.compose.runtime.LaunchedEffect(Unit) {
        isLoading = true
        error = null
        user = repo.getCurrentUser(logger)
        isLoading = false
        if (user == null) error = "Could not load user profile."
    }

    when {
        isLoading -> {
            androidx.compose.material3.CircularProgressIndicator()
            return
        }
        error != null -> {
            androidx.compose.material3.Text(error!!)
            return
        }
    }

    val u = user!!

    // Derive display values
    val level = u.level.toInt()
    val currentXp = u.currentXp.toInt()
    // Users calculates xpToNextLevel in init
    val expToLevel = u.xpToNextLevel.toInt()

    // Base stats and points
    val baseStats = u.stats
    val baseUsed = (baseStats.strength + baseStats.defense + baseStats.intelligence + baseStats.agility + baseStats.health).toInt()
    val unusedLifePoints = u.lifePoints.toInt()
    val usedLifePoints = baseUsed // how many are already allocated

    StatsScreen(
        userLevel = level,
        userExperience = currentXp,
        maxExperience = expToLevel,
        usedLifePoints = usedLifePoints,
        unusedLifePoints = unusedLifePoints + usedLifePoints, // your UI expects "total" on the right side (used / total)
        userStrength = baseStats.strength.toInt(),
        userDefense = baseStats.defense.toInt(),
        userIntel = baseStats.intelligence.toInt(),
        userAgility = baseStats.agility.toInt(),
        userHealth = baseStats.health.toInt(),
        onCancel = {
            // reload from server to discard changes
            isLoading = true
            error = null
            // simple re-fetch
            scope.launch {
                user = repo.getCurrentUser(logger)
                isLoading = false
            }
        },
        onConfirm = { newStats, usedPoints, remainingPoints ->
            // Persist chosen stats and life points
            // remainingPoints is what's left after the user’s edits.
            // Our stored "lifePoints" is the remaining/unspent pool.
            val newLifePoints = remainingPoints.toLong()

            scope.launch() {
                val okStats = repo.setStats(newStats, logger)
                val okLP   = repo.setLifePoints(newLifePoints, logger)
                if (okStats && okLP) {
                    // refresh UI from server so progress bar & counts are consistent
                    isLoading = true
                    user = repo.getCurrentUser(logger)
                    isLoading = false
                } else {
                    error = "Failed to save stats."
                }
            }
        }
    )
}



data class StatItem(
    val icon: Int,
    val label: Int,
    val color: androidx.compose.ui.graphics.Color,
    val value: MutableState<Int>
)