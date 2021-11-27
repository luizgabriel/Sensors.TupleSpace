import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlin.random.Random


@Composable
@Preview
fun Sensor() {
    val (name, setName) = remember { mutableStateOf("") }
    val (value, setValue) = remember { mutableStateOf(Random.nextInt(-100, 200).toString()) }
    val (min, setMin) = remember { mutableStateOf(Random.nextInt(-100, 100).toString()) }
    val (max, setMax) = remember { mutableStateOf(Random.nextInt(100, 200).toString()) }
    val (selectedUnit, setSelectedUnit) = remember { mutableStateOf(0) }
    val items = listOf("Temperatura", "Umidade", "Velocidade")

    MaterialTheme {
        Row (Modifier.fillMaxWidth()) {
            Column (Modifier.padding(20.dp).fillMaxWidth().weight(1f)) {
                Text("Sensor", fontSize = 20.sp, modifier = Modifier.padding(bottom = 10.dp))

                TextField(name, onValueChange = setName, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth().padding(end = 2.dp))

                Row (Modifier.fillMaxWidth().padding(bottom = 20.dp, top = 10.dp)) {
                    TextField(value, onValueChange = setValue, label = { Text("Valor de Leitura do Sensor") }, modifier = Modifier.padding(end = 2.dp).weight(.5f))
                    Dropdown(options = items, selected = selectedUnit, onChange = setSelectedUnit, label = { Text("Tipo") }, modifier = Modifier.weight(.5f))
                }


                Text("Alarme", fontSize = 15.sp, modifier = Modifier.padding(bottom = 10.dp))
                Row (Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    TextField(min, onValueChange = setMin, label = { Text("Mínimo") }, modifier = Modifier.padding(end = 2.dp).weight(.5f))
                    TextField(
                        max,
                        onValueChange = setMax,
                        label = { Text("Máximo") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(.5f)
                    )
                }
                Button(enabled = name.isNotBlank(), onClick = {
                    setName("Hello, Desktop!")
                }) {
                    Text("Enviar")
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Sensor", state = WindowState(width = 500.dp, height = 400.dp)) {
        Sensor()
    }
}
