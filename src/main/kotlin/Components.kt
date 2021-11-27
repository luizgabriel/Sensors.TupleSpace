import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun Dropdown(
    options: List<String>,
    selected: Int = 0,
    onChange: (option: Int) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

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
                    }
                ) {
                    Text(text = option)
                }
            }
        }
    }
}
