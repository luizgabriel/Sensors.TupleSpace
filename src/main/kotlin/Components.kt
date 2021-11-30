import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import java.lang.NumberFormatException

@Composable
fun NumberInput(value: Int, onValueChange: (value: Int) -> Unit, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE, label: String, readOnly: Boolean = false, modifier: Modifier = Modifier) {
    TextField(
        value = value.toString(),
        readOnly = readOnly,
        onValueChange = {
            try {
                onValueChange(parseInt(it))
            } catch (e: NumberFormatException) {
            }
        },
        label = { Text(label) },
        singleLine = true,
        modifier = modifier.onFocusChanged {
                                           onValueChange(clamp(value, min, max))
        },
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
