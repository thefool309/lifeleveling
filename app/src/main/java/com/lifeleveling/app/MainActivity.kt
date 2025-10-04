package com.lifeleveling.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.LifelevelingTheme


// Temp Check to ensure firebase connection
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifelevelingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // ==== TEMP healthcheck: sign in anonymously, then write a doc ====
        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnSuccessListener {
                Log.d("FB", "Anon sign-in OK: uid=${it.user?.uid}")
                Firebase.firestore.collection("healthchecks")
                    .add(mapOf("ts" to Timestamp.now(), "source" to "android"))
                    .addOnSuccessListener { docRef ->
                        Log.d("FB", "Healthcheck doc: ${docRef.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FB", "Healthcheck failed", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FB", "Anon sign-in failed", e)
            }
        // ==== END TEMP ====
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        color = AppTheme.colors.BrandOne,
        style = AppTheme.textStyles.HeadingThree,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LifelevelingTheme {
        Greeting("Android")
    }

}