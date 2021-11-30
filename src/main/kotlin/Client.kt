import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState


@Composable
fun Client() {
    val (allTopics, setAllTopics) = remember { mutableStateOf(setOf<String>()) }
    val (subscribedTopics, setSubscribedTopics) = remember { mutableStateOf(setOf<String>()) }
    val (messages, setMessages) = remember {
        mutableStateOf(
            listOf<SensorEntry>()
        )
    }
    val (service, setService) = remember { mutableStateOf<NotificationService?>(null) }

    LaunchedEffect(true) {
        setService(TupleSpaceNotificationService.lookupForSpace())

        while (service != null) {
            service
                .getAvailableTopics()
                .apply(setAllTopics)
            delay(5000)
        }
    }

    LaunchedEffect(service) {
        while (service != null) {
            service
                .getAvailableTopics()
                .apply(setAllTopics)
            delay(5000)
        }
    }

    LaunchedEffect(service, subscribedTopics) {
        while (service != null) {
            subscribedTopics
                .map { topic -> service.getAllNotificationsByTopic(topic) }
                .reduceOrNull { acc, cur -> acc + cur }
                ?.sortedByDescending { it.createdAt }
                ?.apply(setMessages)
            delay(5000)
        }
    }

    val onClickSelectTopic = { topicName: String ->
        { checked: Boolean ->
            setSubscribedTopics(
                if (checked) {
                    subscribedTopics + setOf(topicName)
                } else {
                    subscribedTopics - setOf(topicName)
                }
            )
        }
    }

    MaterialTheme {
        Row {
            Column(Modifier.padding(20.dp).weight(0.2f)) {
                Text("Sensores", fontSize = 20.sp)
                LazyColumn(modifier = Modifier.weight(0.4f)) {
                    items(allTopics.toList()) { topic ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(subscribedTopics.contains(topic), onCheckedChange = onClickSelectTopic(topic))
                            Text(text = topic, fontSize = 15.sp)
                        }
                    }
                }
            }

            Row(modifier = Modifier.weight(0.8f).fillMaxHeight().background(Color.LightGray).padding(20.dp)) {
                messages.groupBy { it.name }.forEach { (_, sensors) ->
                    val last = sensors.maxByOrNull { it.createdAt }

                    Card(shape = RoundedCornerShape(3.dp), modifier = Modifier.padding(10.dp).width(260.dp)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            SensorEntryView(last!!)
                            SensorLastValues(sensors)
                        }
                    }
                }
            }
        }
    }
}



fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Cliente", state = rememberWindowState(width = 1200.dp, height = 500.dp)) {
        Client()
    }
}
