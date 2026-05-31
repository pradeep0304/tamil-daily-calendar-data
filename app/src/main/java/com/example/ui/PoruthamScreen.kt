package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.t

@Composable
fun PoruthamScreen(viewModel: PoruthamViewModel, isTamil: Boolean) {
    val state by viewModel.currentState.collectAsState()

    Crossfade(targetState = state, label = "PoruthamTransition") { currentState ->
        when (currentState) {
            PoruthamState.INPUT -> PoruthamInputScreen(viewModel, isTamil)
            PoruthamState.LOADING -> PoruthamLoadingScreen(viewModel, isTamil)
            PoruthamState.RESULT -> PoruthamResultScreen(viewModel, isTamil)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoruthamInputScreen(viewModel: PoruthamViewModel, isTamil: Boolean) {
    val sdfDate = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()) }
    val sdfTime = remember { java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()) }
    val currentDate = remember { sdfDate.format(java.util.Date()) }
    val currentTime = remember { sdfTime.format(java.util.Date()) }

    var groomName by remember { mutableStateOf("") }
    var groomDob by remember { mutableStateOf(currentDate) }
    var groomTob by remember { mutableStateOf(currentTime) }
    var groomPlace by remember { mutableStateOf("") }

    var brideName by remember { mutableStateOf("") }
    var brideDob by remember { mutableStateOf(currentDate) }
    var brideTob by remember { mutableStateOf(currentTime) }
    var bridePlace by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = t("Marriage Compatibility", isTamil),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = t("Enter birth details to view compatibility insights.", isTamil),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        // Image upload placeholder
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = t("Upload PDF/Image (Auto-fill)", isTamil), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            PersonDetailsCard(
                title = t("Groom Details", isTamil),
                name = groomName, onNameChange = { groomName = it },
                dob = groomDob, onDobChange = { groomDob = it },
                tob = groomTob, onTobChange = { groomTob = it },
                place = groomPlace, onPlaceChange = { groomPlace = it },
                isTamil = isTamil
            )
        }

        item {
            PersonDetailsCard(
                title = t("Bride Details", isTamil),
                name = brideName, onNameChange = { brideName = it },
                dob = brideDob, onDobChange = { brideDob = it },
                tob = brideTob, onTobChange = { brideTob = it },
                place = bridePlace, onPlaceChange = { bridePlace = it },
                isTamil = isTamil
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Button(
                    onClick = { 
                        viewModel.updateGroomName(groomName)
                        viewModel.updateBrideName(brideName)
                        viewModel.checkCompatibility(groomName, brideName)
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = t("Check Compatibility", isTamil),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            com.example.AdArea()
            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav & ad
        }
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun PoruthamLocalActivity(): Activity? {
    return androidx.compose.ui.platform.LocalContext.current.findActivity()
}

@Composable
fun PersonDetailsCard(
    title: String,
    name: String, onNameChange: (String) -> Unit,
    dob: String, onDobChange: (String) -> Unit,
    tob: String, onTobChange: (String) -> Unit,
    place: String, onPlaceChange: (String) -> Unit,
    isTamil: Boolean
) {
    val activity = PoruthamLocalActivity()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = name, onValueChange = onNameChange,
                label = { Text(t("Full Name", isTamil)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dob, onValueChange = onDobChange,
                    label = { Text(t("Date of Birth", isTamil)) },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = java.util.Calendar.getInstance()
                            if (activity != null) {
                                android.app.DatePickerDialog(
                                    activity,
                                    { _, year, month, day ->
                                        onDobChange(String.format("%02d/%02d/%04d", day, month + 1, year))
                                    },
                                    calendar.get(java.util.Calendar.YEAR),
                                    calendar.get(java.util.Calendar.MONTH),
                                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        }) {
                            Icon(Icons.Default.EditCalendar, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = tob, onValueChange = onTobChange,
                    label = { Text(t("Time (Optional)", isTamil)) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = java.util.Calendar.getInstance()
                            if (activity != null) {
                                android.app.TimePickerDialog(
                                    activity,
                                    { _, hourOfDay, minute ->
                                        val isPm = hourOfDay >= 12
                                        val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                                        val amPm = if (isPm) "PM" else "AM"
                                        onTobChange(String.format("%02d:%02d %s", hour, minute, amPm))
                                    },
                                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                    calendar.get(java.util.Calendar.MINUTE),
                                    false
                                ).show()
                            }
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Select Time")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = place, onValueChange = onPlaceChange,
                label = { Text(t("Birth Place", isTamil)) },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun PoruthamLoadingScreen(viewModel: PoruthamViewModel, isTamil: Boolean) {
    val message by viewModel.loadingMessage.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinAnimation"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
            Icon(
                imageVector = Icons.Default.SettingsSuggest, // Placeholder for astrology wheel
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = t(message, isTamil),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PoruthamResultScreen(viewModel: PoruthamViewModel, isTamil: Boolean) {
    val score by viewModel.harmonyScore.collectAsState()
    val scoreCategory = when {
        score >= 95 -> t("Exceptional Alignment", isTamil)
        score >= 85 -> t("Excellent Alignment", isTamil)
        score >= 75 -> t("Strong Alignment", isTamil)
        score >= 60 -> t("Balanced Alignment", isTamil)
        else -> t("Detailed Consultation Suggested", isTamil)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.reset() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = t("Compatibility Summary", isTamil),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(t("Harmony Score", isTamil), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            strokeWidth = 12.dp,
                            trackColor = Color.Transparent,
                        )
                        val ringColor = when {
                            score > 85 -> Color(0xFF1565C0) // Royal Blue
                            score > 75 -> Color(0xFFD4AF37) // Gold
                            else -> Color(0xFFB71C1C) // Deep Red
                        }
                        CircularProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = ringColor,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round,
                            trackColor = Color.Transparent,
                        )
                        Text(
                            text = "$score%",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = scoreCategory,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Text(
                text = t("Porutham Analysis", isTamil),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val poruthams = listOf(
            "Dina Porutham", "Gana Porutham", "Mahendra Porutham", "Sthree Dheerga Porutham",
            "Yoni Porutham", "Rasi Porutham", "Rasi Adhipathi Porutham", "Vasya Porutham",
            "Rajju Porutham", "Vedha Porutham"
        )

        items(poruthams.size) { index ->
            val pName = poruthams[index]
            val status = if (score > 85) t("Excellent Alignment", isTamil) else if (index % 3 == 0) t("Strong Alignment", isTamil) else t("Good Alignment", isTamil)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = t(pName, isTamil), fontWeight = FontWeight.SemiBold)
                    Text(text = status, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Text(
                text = t("AI Insights", isTamil),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InsightRow(t("Strong indicators of mutual understanding.", isTamil))
                    InsightRow(t("Positive family harmony patterns.", isTamil))
                    InsightRow(t("Balanced relationship dynamics.", isTamil))
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val context = androidx.compose.ui.platform.LocalContext.current
                OutlinedButton(
                    onClick = { 
                        viewModel.saveReport()
                        android.widget.Toast.makeText(context, t("Report Saved", isTamil), android.widget.Toast.LENGTH_SHORT).show()
                    }, 
                    modifier = Modifier.weight(1f)
                ) {
                    Text(t("Save Report", isTamil))
                }
                Button(onClick = { /* Share */ }, modifier = Modifier.weight(1f)) {
                    Text(t("Share", isTamil))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            com.example.AdArea()
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun InsightRow(text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}
