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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.geoquiz.ui.theme.GeoquizTheme
import kotlinx.coroutines.launch

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
                    Question("The source of the Nile River is in Egypt.", false),
                    Question("The Amazon River is the longest river in the Americas.", true),
                    Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
                )
                GeoQuizScreen(questions)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoQuizScreen(questions: List<Question>) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    val answered = remember { mutableStateListOf<Boolean>().apply { repeat(questions.size) { add(false) } } }
    var correctCount by remember { mutableIntStateOf(0) }
    // локальное состояние выбранного варианта (null — ничего не выбрано)
    var selectedOption by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GeoQuiz", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Blue)
            )
        },
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = questions[currentIndex].text,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            // радиоварианты — показываем только если вопрос ещё не отвечен
            if (!answered[currentIndex]) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // True option
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption == true,
                            onClick = {
                                if (!answered[currentIndex]) {
                                    selectedOption = true
                                    // зафиксировать ответ
                                    answered[currentIndex] = true
                                    if (questions[currentIndex].answer) correctCount++
                                    // если это последний — показать результат
                                    if (currentIndex == questions.lastIndex) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Результат: $correctCount / ${questions.size}")
                                        }
                                    }
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Blue,
                                unselectedColor = Color.Blue.copy(alpha = 0.6f)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "True", modifier = Modifier.padding(start = 4.dp))
                    }

                    // False option
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption == false,
                            onClick = {
                                if (!answered[currentIndex]) {
                                    selectedOption = false
                                    answered[currentIndex] = true
                                    if (!questions[currentIndex].answer) correctCount++
                                    if (currentIndex == questions.lastIndex) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Результат: $correctCount / ${questions.size}")
                                        }
                                    }
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Blue,
                                unselectedColor = Color.Blue.copy(alpha = 0.6f)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "False", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            } else {
                // очистим локальный selection при переходе на следующий вопрос,
                // но делаем это только визуально — при отображении следующего вопроса we will reset selectedOption
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Next — видна только если есть следующий вопрос
            if (currentIndex < questions.lastIndex) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // при переходе к следующему вопросу сбрасываем локальный выбор
                            selectedOption = null
                            currentIndex++
                        },
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
                Question("The source of the Nile River is in Egypt.", false),
                Question("The Amazon River is the longest river in the Americas.", true),
                Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
            )
        )
    }
}
