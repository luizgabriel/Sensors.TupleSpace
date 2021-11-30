import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


fun String.toSensorType(): SensorType {
    return when (this) {
        "Temperatura" -> SensorType.TEMPERATURE
        "Umidade" -> SensorType.HUMIDITY
        "Velocidade" -> SensorType.VELOCITY
        else -> SensorType.UNKNOWN
    }
}

@Composable
fun Sensor() {
    val (registered, setRegistered) = remember { mutableStateOf(false) }
    val (started, setStarted) = remember { mutableStateOf(false) }
    val (name, setName) = remember { mutableStateOf("") }
    val (value, setValue) = remember { mutableStateOf(Random.nextInt(-100, 200)) }
    val (min, setMin) = remember { mutableStateOf(Random.nextInt(-100, 100)) }
    val (max, setMax) = remember { mutableStateOf(Random.nextInt(100, 200)) }
    val (selectedUnit, setSelectedUnit) = remember { mutableStateOf(0) }
    val items = listOf("Temperatura", "Umidade", "Velocidade")
    val service = remember<NotificationService> { TupleSpaceNotificationService() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(true) {
        onDispose {
            service.terminate()
        }
    }

    LaunchedEffect(started, min, max) {
        while (started) {
            setValue(Random.nextInt((min * 1.1).toInt(), (max * 1.1).toInt()))
            delay(1000)
        }
    }

    LaunchedEffect(service, registered, value, min, max) {
        if (!registered) return@LaunchedEffect

        val type = items[selectedUnit].toSensorType()
        val message = if (value > max) {
            "Value above maximum allowed. (${value} > ${max})"
        } else {
            "Value bellow minimum allowed. (${value} < ${min})"
        }

        val shouldLaunchWarning = value < min || value > max

        coroutineScope.launch(Dispatchers.IO) {
            if (shouldLaunchWarning)
                service.notifySensorWarning(type, name, message)

            println((service as TupleSpaceNotificationService).readEntriesByType(SensorType.VELOCITY))
        }
    }

    MaterialTheme {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp).fillMaxWidth().weight(1f)) {
                Text("Sensor", fontSize = 20.sp, modifier = Modifier.padding(bottom = 10.dp))

                TextField(
                    name,
                    onValueChange = setName,
                    singleLine = true,
                    label = { Text("Nome") },
                    readOnly = registered,
                    modifier = Modifier.fillMaxWidth().padding(end = 2.dp)
                )

                Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    NumberInput(
                        value,
                        onValueChange = setValue,
                        readOnly = started,
                        label = "Valor de Leitura do Sensor",
                        modifier = Modifier.padding(end = 2.dp).weight(.5f)
                    )
                    Dropdown(
                        options = items,
                        selected = selectedUnit,
                        onChange = setSelectedUnit,
                        label = { Text("Tipo") },
                        modifier = Modifier.weight(.5f)
                    )
                }

                Text("Alarme", fontSize = 15.sp, modifier = Modifier.padding(bottom = 10.dp, top = 20.dp))
                Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    NumberInput(
                        min,
                        onValueChange = setMin,
                        label = "Mínimo",
                        modifier = Modifier.padding(end = 2.dp).weight(.5f),
                    )
                    NumberInput(
                        max,
                        onValueChange = setMax,
                        label = "Máximo",
                        modifier = Modifier.weight(.5f)
                    )
                }

                Text("Controle", fontSize = 15.sp, modifier = Modifier.padding(bottom = 5.dp, top = 10.dp))
                Row {
                    Button(
                        enabled = name.isNotBlank() && !registered,
                        modifier = Modifier.padding(end = 5.dp),
                        onClick = {
                            setRegistered(true)
                        }) {
                        Text("Cadastrar Sensor")
                    }

                    Button(enabled = registered, onClick = {
                        setStarted(!started)
                    }) {
                        Text(
                            text = when (started) {
                                true -> "Parar Sensor Automático"
                                else -> "Iniciar Sensor Automático"
                            }
                        )
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sensor",
        state = rememberWindowState(width = 500.dp, height = 430.dp)
    ) {
        Sensor()
    }
}
