package com.lifeleveling.app.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.data.Reminders
import com.lifeleveling.app.ui.theme.CustomButton
import com.lifeleveling.app.util.ILogger
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TempCalendarScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.calendar),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Calendar screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempStatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.bar_graph),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Stats screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.person),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Home screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempStreaksScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.award_ribbon),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Streaks screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

// Temporary testing logic for the REMINDER CRUD
@Composable
fun DebugRemindersPanel(repo: FirestoreRepository = FirestoreRepository()) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Uses the last created id
    // var lastId by remember { mutableStateOf<String?>(null) }

    // simple in-memory list of reminders (id + title)
    var items by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val logger = object : ILogger {
        override fun d(tag: String, message: String) {
            Log.d(tag, message) }
        override fun e(tag: String, message: String) {
            Log.e(tag, message)}
        override fun e(tag: String, message: String, throwable: Throwable) {
            Log.e(tag, message, throwable)}
        override fun w(tag: String, message: String) {
            Log.w(tag, message)}
        override fun i(tag: String, message: String)  {
            Log.i(tag, message)}
    }

    // Load reminders once when the panel appears
    LaunchedEffect(Unit) {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val snap = com.google.firebase.ktx.Firebase.firestore
                .collection("users").document(uid)
                .collection("reminders")
                .get().await()
            items = snap.documents.map { it.id to (it.getString("title") ?: "(no title)") }
            selectedId = items.firstOrNull()?.first
        }
    }

    Column(Modifier.padding(16.dp)) {
        // Create new reminder
        Button(onClick = {
            scope.launch {
                val id = repo.createReminder(
                    reminders = Reminders(title = "Hydrate", notes = "Drink water"),
                    logger = logger
                )
                if (id != null) {
                    // refresh list
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        val snap = com.google.firebase.ktx.Firebase.firestore
                            .collection("users").document(uid)
                            .collection("reminders")
                            .get().await()
                        items = snap.documents.map { it.id to (it.getString("title") ?: "(no title)") }
                        selectedId = id
                    }
                }
                android.widget.Toast.makeText(ctx, "Created id=$id", android.widget.Toast.LENGTH_SHORT).show()
            }
        }) { Text("Create Reminder") }

        Spacer(Modifier.height(12.dp))

        // Picker: choose which reminder to update
        OutlinedButton(onClick = { expanded = true }, enabled = items.isNotEmpty()) {
            Text(selectedId?.let { id ->
                val title = items.find { it.first == id }?.second ?: ""
                "Selected: $title ($id)"
            } ?: "Select a reminder")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (id, title) ->
                DropdownMenuItem(
                    text = { Text("$title  •  $id") },
                    onClick = {
                        selectedId = id
                        expanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Button to update a reminder that was created(U)
        Button(
            enabled = selectedId != null,
            onClick = {
                scope.launch {
                    val id = selectedId ?: return@launch
                    val ok = repo.updateReminder(
                        reminderId = id,
                        updates = mapOf(
                        "title" to "Go on a walk",
                        "notes" to "Try to break your record of walking 1 in 8 minutes!"
                    ),
                    logger = logger
                )
                Toast.makeText(ctx, "Updated($id) -> $ok", Toast.LENGTH_SHORT).show()
            }
        }) { Text("Update Reminder") }
    }
}




@Composable
fun TempSettingsScreen(
    onSignOut: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CustomButton(
            onClick = onSignOut,
            backgroundColor = AppTheme.colors.Error75
        ) {
            Text(
                "Sign Out",
                color = AppTheme.colors.DarkerBackground,
                style = AppTheme.textStyles.HeadingSix,
            )
        }

        // Temp Panel
        Spacer(Modifier.height(16.dp))
        DebugRemindersPanel()

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.settings_icon),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Settings screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}