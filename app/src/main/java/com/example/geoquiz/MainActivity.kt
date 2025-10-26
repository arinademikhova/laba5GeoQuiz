package com.example.geoquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.geoquiz.ui.theme.GeoquizTheme

data class Question(val text: String, val answer: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoquizTheme {
                val questions = listOf(
                    Question("Canberra is the capital of Australia.", true),
                    Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
                    Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
                    Question("The source of the Nile River is in Egypt.", false)
                )
                GeoQuizScreen(questions)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoQuizScreen(questions: List<Question>) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val answered = remember { mutableStateListOf<Boolean>().apply { repeat(questions.size) { add(false) } } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GeoQuiz", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Blue)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = questions[currentIndex].text,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (!answered[currentIndex]) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (!answered[currentIndex]) answered[currentIndex] = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("True", color = Color.White)
                    }
                    Button(
                        onClick = { if (!answered[currentIndex]) answered[currentIndex] = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("False", color = Color.White)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (currentIndex < questions.lastIndex) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { currentIndex++ },
                        enabled = answered[currentIndex],
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text("Next", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeoQuizPreview() {
    GeoquizTheme {
        GeoQuizScreen(
            listOf(
                Question("Canberra is the capital of Australia.", true),
                Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
                Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
                Question("The source of the Nile River is in Egypt.", false)
            )
        )
    }
}
