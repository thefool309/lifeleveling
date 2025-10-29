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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTextStyles
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.CustomButton
import com.lifeleveling.app.ui.theme.HighlightCard
import com.lifeleveling.app.ui.theme.LevelAndProgress
import com.lifeleveling.app.ui.theme.ShadowedIcon

@Preview
@Composable
fun StatsScreen(
    userLevel: Int = TestUser.level,
    userExperience: Int = TestUser.currentExp,
    maxExperience: Int = TestUser.expToLevel,
    usedLifePoints: Int = TestUser.LifePointsUsed,
    unusedLifePoints: Int = TestUser.UnusedLifePoints,
    userStrength: Int = TestUser.StatStrength,
    userDefense: Int = TestUser.StatDefense,
    userIntel: Int = TestUser.StatIntelligence,
    userAgility: Int = TestUser.StatAgility,
    userHealth: Int = TestUser.StatHealth,
    onConfirm: () -> Unit = {println("Confirm pressed")},
    onCancel: () -> Unit = {println("Cancel pressed")},
                ) {
    val progress = (userExperience.toFloat() / maxExperience.toFloat()).coerceIn(0f,1f)
    var showHelpDialog = remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var usedPoints by remember { mutableStateOf(usedLifePoints) }
    var remainingPoints by remember { mutableStateOf(unusedLifePoints - usedLifePoints) }

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
                            "Stats",
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
                                    showStatsDialog = showStatsDialog.not()
                                }
                        )

                    }
                    Text(
                        text = "Life Points Used: $usedPoints / $unusedLifePoints",
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
                        text = "Life Points remaining to use: $remainingPoints",
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



                HighlightCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    outerPadding = 0.dp
                ) {
                    val strength = remember { mutableStateOf(userStrength) }
                    val defense = remember { mutableStateOf(userDefense) }
                    val intelligence = remember { mutableStateOf(userIntel) }
                    val agility = remember { mutableStateOf(userAgility) }
                    val health = remember { mutableStateOf(userHealth) }
                    // create baseStats so user is not able to go lower than what is saved
                    val baseStats = mapOf(
                        "Strength" to userStrength,
                        "Defense" to userDefense,
                        "Intelligence" to userIntel,
                        "Agility" to userAgility,
                        "Health" to userHealth
                    )
                        //create a list of triples that contain the icon, label, and stat level
                    val statItems = listOf(
                        StatItem((R.drawable.sword), "Strength", AppTheme.colors.BrandOne, strength),
                        StatItem((R.drawable.shield), "Defense", AppTheme.colors.BrandTwo, defense),
                        StatItem((R.drawable.brain), "Intelligence", AppTheme.colors.SecondaryOne,intelligence),
                        StatItem((R.drawable.person_running), "Agility", AppTheme.colors.SecondaryTwo,agility),
                        StatItem((R.drawable.heart), "Health", AppTheme.colors.SecondaryThree,health)
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
                        statItems.forEachIndexed { index, (iconRes, label, color, statValue) ->
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
                                        text = label,
                                        color = AppTheme.colors.Gray,
                                        style = AppTheme.textStyles.HeadingSix
                                    )
                                }

                                // stat controls on the right
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // minus button
                                    Image(
                                        painter = painterResource(id = R.drawable.minus),
                                        contentDescription = "Decrease $label",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.FadedGray),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                                onClick = {
                                                    val base: Int = baseStats[label] ?: 0
                                                    if (statValue.value > base) {
                                                        statValue.value -= 1
                                                        usedPoints -= 1
                                                        remainingPoints += 1
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
                                        contentDescription = "Increase $label",
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
                        content = { Text("Cancel", style = AppTextStyles.HeadingSix, color = AppTheme.colors.Background) },
                        onClick = { onCancel() },
                        backgroundColor = AppTheme.colors.Error,
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    CustomButton(
                       width = 122.dp,
                        content = { Text("Confirm", style = AppTextStyles.HeadingSix, color = AppTheme.colors.Background) },
                        onClick = { onConfirm() },
                        backgroundColor = AppTheme.colors.SecondaryTwo,
                    )
                }
            }
            // and here
//            if(showHelpDialog){
//                PopupCard {
//                    Column(
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                    ){
//                        Text("This is your current level.\n" +
//                                "The bar shows your experience progress toward your next level.\n" +
//                                "You earn experience idly while your character is fighting.\n" +
//                                "Experience earned is based on your stats."
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        CustomButton(
//                            content = { Text("Understood!") },
//                            onClick = {showHelpDialog = false},
//                        )
//                    }
//
//                }
//            }
//                if(showStatsDialog){
//                PopupCard {
//                     Column(
//                     modifier = Modifier
//                         .padding(horizontal = 12.dp),
//                     horizontalAlignment = Alignment.CenterHorizontally,
//                    ){
//                        Text("Your stats effect how much experience and coins you get from fighting.\n"+
//                             "Life Points can be spent to increase your stats.\n" +
//                             "Strength and Defense effect how much experience you gain while fighting.\n" +
//                             "Intelligence and Agility effect how many coins you earn when fighting.\n" +
//                             "Health effects how long your character will fight for. Your character will go through one health every minute. You start with 60 health and every Life Point you put in gives you 5 more health. 100 Life Points is the max you can put into this stat."
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                         CustomButton(
//                         content = { Text("Understood!") },
//                         onClick = {showStatsDialog = false},
//                        )
//                    }
//
//                }
            }
        }
    }


data class StatItem(
    val icon: Int,
    val label: String,
    val color: androidx.compose.ui.graphics.Color,
    val value: MutableState<Int>
)