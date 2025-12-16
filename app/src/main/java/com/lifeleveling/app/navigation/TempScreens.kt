package com.lifeleveling.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.ui.components.CustomButton
import kotlinx.coroutines.launch

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

    data class ReminderRow(
        val id: String,
        val title: String,
        val notes: String,
        val isCompleted: Boolean,
        val lastUpdate: com.google.firebase.Timestamp?
    )

    var items by remember { mutableStateOf<List<ReminderRow>>(emptyList()) }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val logger = object : com.lifeleveling.app.util.ILogger {
        override fun d(tag: String, message: String) { android.util.Log.d(tag, message) }
        override fun e(tag: String, message: String) { android.util.Log.e(tag, message) }
        override fun e(tag: String, message: String, throwable: Throwable) { android.util.Log.e(tag, message, throwable) }
        override fun w(tag: String, message: String) { android.util.Log.w(tag, message) }
        override fun i(tag: String, message: String) { android.util.Log.i(tag, message) }
    }

    // Realtime listener: attach when we have a user, detach on dispose
    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    DisposableEffect(uid) {
        if (uid == null) return@DisposableEffect onDispose { }

        val reg = com.google.firebase.ktx.Firebase.firestore
            .collection("users").document(uid)
            .collection("reminders")
            .orderBy("lastUpdate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { qs, e ->
                if (e != null) {
                    android.util.Log.e("DebugPanel", "listen error", e)
                    return@addSnapshotListener
                }
                val list = qs?.documents?.map { d ->
                    ReminderRow(
                        id = d.id,
                        title = d.getString("title") ?: "(no title)",
                        notes = d.getString("notes") ?: "",
                        isCompleted = d.getBoolean("isCompleted") ?: false,
                        lastUpdate = d.getTimestamp("lastUpdate")
                    )
                } ?: emptyList()

                items = list
                // pick a default if none selected or selected disappeared
                if (selectedId == null || list.none { it.id == selectedId }) {
                    selectedId = list.firstOrNull()?.id
                }
            }

        onDispose { reg.remove() }
    }

    val selected = items.firstOrNull { it.id == selectedId }

    Column(Modifier.padding(16.dp)) {
        // Create new reminder
        Button(onClick = {
            scope.launch {
                val id = repo.createReminder(
                    reminders = com.lifeleveling.app.data.Reminders(
                        title = "Hydrate",
                        notes = "Drink water",
                    ),
                    logger = logger
                )
                if (id != null) {
                    // selection will auto-update from the listener; make it the selected one
                    selectedId = id
                }
                android.widget.Toast
                    .makeText(ctx, "Created id=$id", android.widget.Toast.LENGTH_SHORT)
                    .show()
            }
        }) { Text("Create Reminder") }

        Spacer(Modifier.height(12.dp))

        // Picker
        OutlinedButton(onClick = { expanded = true }, enabled = items.isNotEmpty()) {
            Text(
                selected?.let { "Selected: ${it.title} (${it.id})" } ?: "Select a reminder"
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { row ->
                DropdownMenuItem(
                    text = { Text("${row.title} • ${row.id}") },
                    onClick = {
                        selectedId = row.id
                        expanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Live details of selected reminder
        if (selected != null) {
            Text("Title: ${selected.title}", style = MaterialTheme.typography.bodyLarge)
            Text("Notes: ${selected.notes}", style = MaterialTheme.typography.bodyMedium)
            Text("Completed: ${selected.isCompleted}", style = MaterialTheme.typography.bodyMedium)
            Text("Last Update: ${selected.lastUpdate ?: "(null)"}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }

        // Update selected reminder
        Button(
            enabled = selectedId != null,
            onClick = {
                scope.launch {
                    val id = selectedId ?: return@launch
                    val ok = repo.updateReminder(
                        reminderId = id,
                        updates = mapOf(
                            "title" to "Go on a walk",
                            "notes" to "Try to break your record of walking 1 mile in 8 minutes!",
                            "isCompleted" to false,
                            "lastUpdate" to com.google.firebase.Timestamp.now()
                        ),
                        logger = logger
                    )
                    android.widget.Toast
                        .makeText(ctx, "Updated($id) -> $ok", android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) { Text("Update Selected") }

        // Marks the reminder as completed
        Button(
            enabled = selectedId != null,
            onClick = {
                scope.launch {
                    val id = selectedId ?: return@launch
                    val ok = repo.setReminderCompleted(
                        reminderId = id,
                        completed = true,
                        logger = logger
                    )
                    android.widget.Toast
                        .makeText(ctx, "Completed($id) -> $ok", android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) { Text("Complete Reminder") }

        // Marks reminder as un-completed
        Button(
            enabled = selectedId != null,
            onClick = {
                scope.launch {
                    val id = selectedId ?: return@launch
                    val ok = repo.setReminderCompleted(
                        reminderId = id,
                        completed = false,
                        logger = logger
                    )
                    android.widget.Toast
                        .makeText(ctx, "Un-completed($id) -> $ok", android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) { Text("Un-complete Reminder") }

        Spacer(Modifier.height(8.dp))

        // Deletes reminder
        Button(
            enabled = selectedId != null,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            onClick = {
                scope.launch {
                    val id = selectedId ?: return@launch
                    val ok = repo.deleteReminder(
                        reminderId = id,
                        logger = logger
                    )
                    if(ok) {
                        items = items.filterNot { it.id == id }
                        selectedId = items.firstOrNull()?.id
                    }
                    android.widget.Toast
                        .makeText(ctx, "Deleted($id) -> $ok", android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ){Text(text = "Delete Reminder")}

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