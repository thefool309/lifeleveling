package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.SlidingSwitch
import com.lifeleveling.app.ui.theme.AppTheme
import kotlinx.datetime.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.ExperimentalTime

@Preview
@OptIn(ExperimentalTime::class)
@Composable
fun CalendarScreen() {
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(color = AppTheme.colors.Background)
        .padding(16.dp),

    ) {

        val currentMonth = remember { YearMonth.now() }
        val startMonth = remember { currentMonth.minusMonths(100) }
        val endMonth = remember { currentMonth.plusMonths(100) }
        val daysOfWeek = remember{daysOfWeek()}
        val isMonthView = remember { mutableStateOf(true) }
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = daysOfWeek.first(),
            outDateStyle = OutDateStyle.EndOfGrid
        )
         Box(
             modifier = Modifier
                 .fillMaxSize()
                 .background(color = AppTheme.colors.Background),
             contentAlignment = Alignment.Center
         ){
             Column(modifier = Modifier
                 .fillMaxSize()
                 .padding(top = 32.dp),
                 verticalArrangement = Arrangement.spacedBy(16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Box(
                    modifier = Modifier
                        .fillMaxWidth(),

                 ){
                     SlidingSwitch(
                         modifier = Modifier
                             .align(Alignment.Center),
                         options = listOf("Month", "Day"),
                         selectedIndex = if (isMonthView.value) 0 else 1,
                         onOptionSelected = { index -> isMonthView.value = (index == 0) },
                         horizontalPadding = 12.dp,
                         verticalPadding = 8.dp,
                         backgroundColor = AppTheme.colors.DarkerBackground,
                         selectedColor = AppTheme.colors.BrandOne,
                         unselectedColor = AppTheme.colors.Gray,
                         cornerRadius = 32.dp,
                         textStyle = AppTheme.textStyles.HeadingFour,
                         insetAmount = 4.dp,
                         extraWidth = 64.dp,
                     )
                    ShadowedIcon(
                        imageVector = ImageVector.vectorResource(R.drawable.info),
                        contentDescription = null,
                        tint = AppTheme.colors.FadedGray,
                        modifier = Modifier

                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .clickable {
                                // Todo add info i click action
                            },

                    )
                 }

                 HighlightCard(
                     modifier = Modifier,
                         //.weight(1f),
                     innerPadding = 0.dp,
                     outerPadding = 0.dp,
                     //height = ((screenHeight/4)*3)

                 ){
                     Column(
                         modifier = Modifier
                     ){
                         if (isMonthView.value){
                             HorizontalCalendar(
                                 modifier = Modifier
                                     .background(color = Color.Transparent),
                                 state = state,
                                 dayContent = { Day(it) },
                                 monthHeader = {
                                    month ->
                                     Column(
                                         modifier = Modifier
                                             .fillMaxWidth()
                                             .background(Color.Transparent)
                                     ){
                                         val monthName = month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                                         val year = month.yearMonth.year
                                         Text(
                                             text = "$monthName $year",
                                             style = AppTheme.textStyles.HeadingFour,
                                             color = AppTheme.colors.Gray,
                                             modifier = Modifier
                                                 .fillMaxWidth()
                                                 .padding(vertical = 8.dp),
                                             textAlign = TextAlign.Center
                                         )
                                         DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                                     }
                                 },
                             )
                         }else{
                             var today by remember {mutableStateOf(LocalDate.now())}
                             val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                             val monthName = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                             Box(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .background(AppTheme.colors.DarkerBackground)
                                     .border(0.5.dp, AppTheme.colors.Gray),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Row(
                                     verticalAlignment = Alignment.CenterVertically,
                                     horizontalArrangement = Arrangement.Center,
                                 ){
                                     ShadowedIcon(
                                         imageVector = ImageVector.vectorResource(R.drawable.left_arrow),
                                         contentDescription = null,
                                         tint = AppTheme.colors.BrandTwo,
                                         modifier = Modifier
                                             .size(28.dp)
                                             .clickable { today = today.minusDays(1) },
                                     )
                                     Column(modifier = Modifier.padding(24.dp),
                                         horizontalAlignment = Alignment.CenterHorizontally){
                                         Text(
                                             text = "Today",
                                             style = AppTheme.textStyles.Default,
                                             color = AppTheme.colors.Gray,
                                             textAlign = TextAlign.Center
                                         )
                                         Text(
                                             text = "$dayName, $monthName ${today.dayOfMonth}",
                                             style = AppTheme.textStyles.HeadingSix,
                                             color = AppTheme.colors.BrandOne,
                                             textAlign = TextAlign.Center
                                         )
                                     }
                                     ShadowedIcon(
                                         imageVector = ImageVector.vectorResource(R.drawable.right_arrow),
                                         contentDescription = null,
                                         tint = AppTheme.colors.BrandTwo,
                                         modifier = Modifier
                                             .size(28.dp)
                                             .clickable { today = today.plusDays(1) }
                                     )
                                 }
                             }
                         }
                     }
                 }
                 Column(
                     modifier = Modifier
                         .fillMaxWidth(),
                     verticalArrangement = Arrangement.spacedBy(24.dp),

                 ){
                     Row(
                         modifier = Modifier
                             .align(Alignment.Start)
                             .clickable {
                                 // Todo add way to add reminders
                             },
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.spacedBy(8.dp),
                     ){
                         ShadowedIcon(
                             imageVector = ImageVector.vectorResource(R.drawable.plus),
                             contentDescription = null,
                             tint = AppTheme.colors.SecondaryThree,
                             modifier = Modifier
                                 .size(24.dp)


                         )

                         Text(
                             text = "Add Reminder",
                             color = AppTheme.colors.SecondaryThree,
                             style = AppTheme.textStyles.DefaultUnderlined.copy(
                                 shadow = Shadow(
                                     color = AppTheme.colors.DropShadow,
                                     offset = Offset(3f, 4f),
                                     blurRadius = 6f,
                                 )
                             ),
                         )
                     }

                     Row(
                         modifier = Modifier
                             .align(Alignment.Start)
                             .clickable {
                                 // Todo add all reminder list click > might be handled in Days list cause how they are created
                             },
                         verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.spacedBy(8.dp),
                     ){
                         ShadowedIcon(
                             imageVector = ImageVector.vectorResource(R.drawable.bars_solid_full),
                             contentDescription = null,
                             tint = AppTheme.colors.SecondaryThree,
                             modifier = Modifier
                                 .size(24.dp)


                         )

                         Text(
                             text = "My Reminders",
                             color = AppTheme.colors.SecondaryThree,
                             style = AppTheme.textStyles.DefaultUnderlined.copy(
                                 shadow = Shadow(
                                     color = AppTheme.colors.DropShadow,
                                     offset = Offset(3f, 4f),
                                     blurRadius = 6f,
                                 )
                             ),
                         )
                     }
                 }
             }
         }
    }
}

@Composable
fun Day(day: CalendarDay) {
    val isOutDate = day.position != DayPosition.MonthDate
    Box(
        modifier = Modifier
            .border(
                color = AppTheme.colors.Gray, shape = RectangleShape,
                width = 0.2.dp
            )
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                // Todo add click to add events to days > might be handled in Add reminders
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(text = day.date.dayOfMonth.toString(),
            color = if(isOutDate){
                AppTheme.colors.FadedGray
            }else {
                AppTheme.colors.Gray
            }
        )
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    //val backgroundColor = AppTheme.colors.Background
    val topLine = AppTheme.colors.SecondaryTwo
    val grayLine = AppTheme.colors.Gray
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.Transparent)
        .drawBehind {
            val strokeWidth = 2.dp.toPx()
            // Top border
            drawLine(
                color = topLine,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth
            )
            val thinStroke = 1.dp.toPx()
            // Bottom border
            drawLine(
                color = grayLine,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = thinStroke
            )
            // Right border
            drawLine(
                color = grayLine,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = thinStroke
            )
            // Left border
            drawLine(
                color = grayLine,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = thinStroke
            )
        }
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()).toString(),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = AppTheme.colors.Gray
            )
        }
    }
}

private fun DayOfWeek.getDisplayName(short: Any, default: Any) {}
