import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberInput(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: String,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value.toString(),
        readOnly = readOnly,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun Dropdown(
    options: List<String>,
    selected: Int = 0,
    onChange: (option: Int) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = options[selected],
            onValueChange = { },
            label = label,
            trailingIcon = {
                Icons.Default.ArrowDropDown
            },
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                setExpanded(it.isFocused)
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setExpanded(false)
            }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    onClick = {
                        onChange(index)
                        setExpanded(false)
                        focusManager.clearFocus()
                    }
                ) {
                    Text(text = option)
                }
            }
        }
    }
}

private fun getEmoji(sensor: SensorEntry) = when (sensor.type ?: SensorType.UNKNOWN) {
    SensorType.UNKNOWN -> "⁉️"
    SensorType.TEMPERATURE -> when (sensor.state) {
        SensorState.NORMAL -> "🌡"
        SensorState.BELLOW_MINIMUM -> "❄️"
        SensorState.ABOVE_MAXIMUM -> "🔥"
    }
    SensorType.HUMIDITY -> when (sensor.state) {
        SensorState.NORMAL -> "💦"
        SensorState.BELLOW_MINIMUM -> "🥵"
        SensorState.ABOVE_MAXIMUM -> "🌊"
    }
    SensorType.VELOCITY -> when (sensor.state) {
        SensorState.NORMAL -> "🚶"
        SensorState.BELLOW_MINIMUM -> "🧎"
        SensorState.ABOVE_MAXIMUM -> "🏃"
    }
}

@Composable
fun SensorEntryView(sensor: SensorEntry) {
    Column {
        Row(modifier = Modifier.height(80.dp).fillMaxWidth()) {
            Column (modifier = Modifier.weight(.8f).fillMaxHeight()) {
                Text(
                    text = sensor.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Text(text = sensor.createdAt.toInstant().toString(), color = Color.LightGray, fontSize = 10.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sensor.value.toString(),
                        fontSize = 25.sp,
                        modifier = Modifier.padding(top = 5.dp, end = 10.dp)
                    )
                    Text(text = sensor.unit, fontSize = 10.sp)
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp).weight(.2f)
            ) {
                Text(text = getEmoji(sensor), fontSize = 30.sp)
            }
        }
        if (sensor.state == SensorState.ABOVE_MAXIMUM)
            Badge(backgroundColor = Color.Red) { Text("Acima do máximo permitido  (${sensor.max} ${sensor.unit})", color = Color.White) }
        else if (sensor.state == SensorState.BELLOW_MINIMUM)
            Badge(backgroundColor = Color.Red) { Text("Abaixo do mínimo permitido (${sensor.min} ${sensor.unit})", color = Color.White) }
    }
}

@Composable
fun SensorLastValues(sensors: List<SensorEntry>) {
    Column(Modifier.padding(top = 10.dp)) {
        Text("Últimas leituras", fontSize = 12.sp)
        Column {
            sensors.take(10).forEach {
                Row(modifier = Modifier.padding(top = 5.dp)) {
                    Text(
                        text = "${it.value} ${it.unit} às ${it.createdAt.toInstant()}",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
