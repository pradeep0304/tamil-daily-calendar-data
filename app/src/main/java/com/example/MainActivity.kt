package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.PoruthamRepository
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.CalendarDayInfo
import com.example.data.CalendarModel
import com.example.ui.CalendarTab
import com.example.ui.CalendarViewModel
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.GoldHighlight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OrangeFlame
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

@Composable
fun LocationWeatherHandler(viewModel: CalendarViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            viewModel.fetchWeather(location.latitude, location.longitude)
                        } else {
                            viewModel.fetchWeather(13.0827, 80.2707) // Default fallback
                        }
                    }.addOnFailureListener {
                        viewModel.fetchWeather(13.0827, 80.2707)
                    }
                } catch (e: SecurityException) {}
            }
            else -> {
                // Permission denied, handle default location
                viewModel.fetchWeather(13.0827, 80.2707)
            }
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.fetchWeather(location.latitude, location.longitude)
                    } else {
                        viewModel.fetchWeather(13.0827, 80.2707) // Default to Chennai
                    }
                }.addOnFailureListener {
                    viewModel.fetchWeather(13.0827, 80.2707)
                }
            } catch (e: SecurityException) {}
            
            // Still request notification if not granted
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    locationPermissionRequest.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                }
            }
        } else {
            locationPermissionRequest.launch(permissionsToRequest.toTypedArray())
        }
    }
}

class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        com.example.scheduleMorningNotification(this)
        enableEdgeToEdge()
        val appContext = applicationContext
        val db = Room.databaseBuilder(appContext, AppDatabase::class.java, "porutham_db").build()
        val poruthamRepository = PoruthamRepository(db.poruthamDao())

        setContent {
            val viewModel: CalendarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val poruthamViewModel: com.example.ui.PoruthamViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.example.ui.PoruthamViewModelFactory(poruthamRepository)
            )
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1800) // Beautiful cinematic entry duration
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen(onSkip = { showSplash = false })
                } else {
                    MainAppContent(poruthamViewModel)
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSkip: () -> Unit) {
    var animateStart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateStart = true
    }

    // Interactive scale animations
    val scale by animateFloatAsState(
        targetValue = if (animateStart) 1f else 0.7f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "LogoScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                // Tactical, beautiful heritage red/gold dotted background layer
                val dotSize = 2.dp.toPx()
                val gap = maxOf(40, 30.dp.toPx().toInt())
                for (x in 0..maxOf(0, size.width.toInt()) step gap) {
                    for (y in 0..maxOf(0, size.height.toInt()) step gap) {
                        drawCircle(
                            color = BrandPrimaryLight.copy(alpha = 0.05f),
                            radius = dotSize,
                            center = Offset(x.toFloat(), y.toFloat())
                        )
                    }
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Fast skip tap
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onSkip() }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top aesthetics decor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                )
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                )
            }

            // Central logo & text blocks
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Drawing central spiritual golden temple calendar dome emblem
                        val center = Offset(size.width / 2, size.height / 2)
                        drawCircle(
                            color = OrangeFlame.copy(alpha = 0.15f),
                            radius = (size.width / 2) * scale,
                            center = center
                        )
                        drawCircle(
                            color = GoldHighlight,
                            radius = (size.width / 3.5f) * scale,
                            center = center
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = animateStart,
                    enter = fadeIn(tween(800, delayMillis = 300)) + slideInVertically(
                        tween(800, delayMillis = 300),
                        initialOffsetY = { 50 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "தமிழ் தினசரி காலண்டர் ப்ரோ",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tamil Daily Calendar Pro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "உங்கள் பாரம்பரியம் மற்றும் வழிபாடுகளுக்கான தினசரி வழிகாட்டி\nYour Daily Guide to Tradition & Timings",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Loading / Footer section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "உடன் படைப்போம், வளமுடன் வாழ்வோம்",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Developed by Pradeep M",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Publisher ID: pub-3244378896899982",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Light
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        val animDelay = index * 200
                        var isPinging by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(animDelay.toLong())
                            while (true) {
                                isPinging = true
                                delay(600)
                                isPinging = false
                                delay(600)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPinging) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(poruthamViewModel: com.example.ui.PoruthamViewModel) {
    val viewModel: CalendarViewModel = viewModel()
    val activeTab by viewModel.activeTab.collectAsState()
    val isTamil by viewModel.isTamilLanguage.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val dayInfo = viewModel.getSelectedDayInfo()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            TopAppHeader(viewModel = viewModel, isTamil = isTamil, dayInfo = dayInfo)
        },
        bottomBar = {
            BottomNavBar(viewModel = viewModel, activeTab = activeTab, isTamil = isTamil)
        },
        floatingActionButton = {
            if (activeTab == CalendarTab.DAILY) {
                FloatingActionButton(
                    onClick = { viewModel.nextDay() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("next_day_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = t("Next Day", isTamil)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .drawBehind {
                    // Tactile Paper Texture Dots
                    val dotSize = 1.5.dp.toPx()
                    val gap = maxOf(40, 24.dp.toPx().toInt())
                    for (x in 0..maxOf(0, size.width.toInt()) step gap) {
                        for (y in 0..maxOf(0, size.height.toInt()) step gap) {
                            drawCircle(
                                color = BrandPrimaryLight.copy(alpha = 0.04f),
                                radius = dotSize,
                                center = Offset(x.toFloat(), y.toFloat())
                            )
                        }
                    }
                }
                .padding(paddingValues)
        ) {
            when (activeTab) {
                CalendarTab.DAILY -> DailyScreen(viewModel = viewModel, isTamil = isTamil, dayInfo = dayInfo)
                CalendarTab.MONTHLY -> MonthlyScreen(viewModel = viewModel, isTamil = isTamil, selectedDate = selectedDate)
                CalendarTab.FESTIVALS -> FestivalsScreen(viewModel = viewModel, isTamil = isTamil)
                CalendarTab.PORUTHAM -> com.example.ui.PoruthamScreen(viewModel = poruthamViewModel, isTamil = isTamil)
                CalendarTab.SETTINGS -> SettingsScreen(viewModel = viewModel, isTamil = isTamil)
            }
        }
    }
}

@Composable
fun TopAppHeader(viewModel: CalendarViewModel, isTamil: Boolean, dayInfo: CalendarDayInfo) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isSearchExpanded) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text(t("Search Festivals", isTamil)) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("header_search_input"),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.updateSearchQuery("")
                            isSearchExpanded = false
                            focusManager.clearFocus()
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                    })
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Temple Kalasam",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = t("Tamil Daily Calendar Pro", isTamil),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = if (isTamil) "வைகாசி - சித்திரை நற்பொழுது" else "Traditional Almanac",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.setActiveTab(CalendarTab.FESTIVALS)
                        isSearchExpanded = true
                    },
                    modifier = Modifier.testTag("search_trigger_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(viewModel: CalendarViewModel, activeTab: CalendarTab, isTamil: Boolean) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        windowInsets = WindowInsets.navigationBars
    ) {
        val items = listOf(
            Triple(CalendarTab.DAILY, Icons.Default.Event, "Daily"),
            Triple(CalendarTab.MONTHLY, Icons.Default.CalendarMonth, "Monthly"),
            Triple(CalendarTab.FESTIVALS, Icons.Default.AutoAwesome, "Festivals"),
            Triple(CalendarTab.PORUTHAM, Icons.Default.Favorite, "Porutham"),
            Triple(CalendarTab.SETTINGS, Icons.Default.Settings, "Settings")
        )

        items.forEach { (tab, icon, label) ->
            val labelText = t(label, isTamil)
            NavigationBarItem(
                selected = activeTab == tab,
                onClick = { viewModel.setActiveTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = labelText
                    )
                },
                label = {
                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun DailyScreen(viewModel: CalendarViewModel, isTamil: Boolean, dayInfo: CalendarDayInfo) {
    LocationWeatherHandler(viewModel)
    val weatherSummary by viewModel.weatherSummary.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. Giant Gregorian Date Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectToday() }
                    .testTag("gregorian_date_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val monthYear = "${t(dayInfo.gregorianDate.month.name, isTamil)} ${dayInfo.gregorianDate.year}"
                    Text(
                        text = monthYear.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = dayInfo.gregorianDate.dayOfMonth.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = t(dayInfo.gregorianDate.dayOfWeek.name, isTamil),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    
                    if (weatherSummary != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = weatherSummary!!,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Tamil Date details Row (Left Thick Border)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tamil_date_row_card"),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            // Thick crimson border left
                            drawRect(
                                color = BrandPrimaryLight,
                                topLeft = Offset(0f, 0f),
                                size = size.copy(width = 8.dp.toPx())
                            )
                        }
                        .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = t("Tamil Date", isTamil),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "${dayInfo.tamilMonth} ${dayInfo.tamilDate}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${t("Rasi", isTamil)}: ${dayInfo.rasi} | ${t("Nakshatram", isTamil)}: ${dayInfo.nakshatram}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = t("Tithi", isTamil),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = dayInfo.tithi,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Auspicious Bento structure block
        item {
            AuspiciousBentoBlock(dayInfo = dayInfo, isTamil = isTamil)
        }

        // 4. Festivals check banner
        dayInfo.festivalName?.let { fest ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("festival_banner_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = borderStroke(MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = "Celebration",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = fest,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center
                        )

                        dayInfo.festivalDetail?.let { detail ->
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (dayInfo.isGovernmentHoliday) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = t("Government Holiday", isTamil),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // AdMob Placeholder
        item {
            AdArea()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AuspiciousBentoBlock(dayInfo: CalendarDayInfo, isTamil: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("auspicious_bento_card"),
        shape = RoundedCornerShape(12.dp),
        border = borderStroke(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // First Row: Nalla Neram and Gowri Nalla Neram Side-by-side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Horizontal divider line
                        drawLine(
                            color = BrandPrimaryLight.copy(alpha = 0.2f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                // Left box - Nalla Neram
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .drawBehind {
                            // Middle vertical divider
                            drawLine(
                                color = BrandPrimaryLight.copy(alpha = 0.2f),
                                start = Offset(size.width, 0f),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t("Nalla Neram", isTamil),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${t("Morning", isTamil)}: ${dayInfo.nallaNeramMorning}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${t("Evening", isTamil)}: ${dayInfo.nallaNeramEvening}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Right box - Gowri Nalla Neram
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t("Gowri Nalla Neram", isTamil),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${t("Morning", isTamil)}: ${dayInfo.gowriNallaNeramMorning}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${t("Evening", isTamil)}: ${dayInfo.gowriNallaNeramEvening}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Row 2: 4-Column Astro grid (Rahu, Kuligai, Yamagandam, Sani/Sun)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Bottom line
                        drawLine(
                            color = BrandPrimaryLight.copy(alpha = 0.2f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                val items = listOf(
                    Pair(t("Rahu", isTamil), dayInfo.rahuKaalam),
                    Pair(t("Kuligai", isTamil), dayInfo.kuligai),
                    Pair(t("Yemagandam", isTamil), dayInfo.yemagandam),
                    Pair(t("SaniRahu", isTamil), dayInfo.saniRahu)
                )

                items.forEachIndexed { index, (label, valStr) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .drawBehind {
                                if (index < 3) {
                                    drawLine(
                                        color = BrandPrimaryLight.copy(alpha = 0.2f),
                                        start = Offset(size.width, 0f),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                            }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = valStr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Bottom section: rows of Karanam Soolam
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ValRow(label = t("Soolam", isTamil), value = dayInfo.soolam, isTamil = isTamil)
                ValRow(label = t("Parigaram", isTamil), value = dayInfo.parigaram, isTamil = isTamil)
                ValRow(label = t("Karanam", isTamil), value = dayInfo.karanam, isTamil = isTamil)
            }
        }
    }
}

@Composable
fun ValRow(label: String, value: String, isTamil: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Subtle bottom border line
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label :",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun MonthlyScreen(viewModel: CalendarViewModel, isTamil: Boolean, selectedDate: LocalDate) {
    val focusManager = LocalFocusManager.current
    val dayOfWeekHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    // Local state for shown month in calendar grid
    var shownMonth by remember(selectedDate) { mutableStateOf(selectedDate) }

    val daysInMonth = shownMonth.lengthOfMonth()
    val firstOfMonth = LocalDate.of(shownMonth.year, shownMonth.month, 1)
    val emptyStartCells = firstOfMonth.dayOfWeek.value % 7 // offset day indices

    val totalCells = daysInMonth + emptyStartCells

    // Selected day particulars for bottom view
    val selectedDayInfo = CalendarModel.getTamilDayInfo(selectedDate)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 1. Month Navigator Controller
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    shownMonth = shownMonth.minusMonths(1)
                }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Prev Month")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${t(shownMonth.month.name, isTamil).uppercase(Locale.getDefault())} ${shownMonth.year}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = if (isTamil) "சித்திரை - வைகாசி கார்த்திகை" else "Auspicious Heritage Range",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                IconButton(onClick = {
                    shownMonth = shownMonth.plusMonths(1)
                }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Month")
                }
            }
        }

        // 2. Calendar Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monthly_grid_card"),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Headings
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(vertical = 8.dp)
                    ) {
                        dayOfWeekHeaders.forEach { header ->
                            Text(
                                text = header,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (header == "Sun") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Days grid
                    val rowCount = (totalCells + 6) / 7
                    for (row in 0 until rowCount) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - emptyStartCells + 1

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .drawBehind {
                                            // Soft borders on each grid cells
                                            drawLine(
                                                color = Color.LightGray.copy(alpha = 0.15f),
                                                start = Offset(size.width, 0f),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = 0.5.dp.toPx()
                                            )
                                            drawLine(
                                                color = Color.LightGray.copy(alpha = 0.15f),
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = 0.5.dp.toPx()
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayNum in 1..daysInMonth) {
                                        val cellDate = LocalDate.of(shownMonth.year, shownMonth.month, dayNum)
                                        val isSelected = cellDate == selectedDate
                                        val isToday = cellDate == LocalDate.now()

                                        val cellInfo = CalendarModel.getTamilDayInfo(cellDate)
                                        val hasFestival = cellInfo.festivalName != null

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary
                                                        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable {
                                                    viewModel.selectDate(cellDate)
                                                }
                                                .testTag("day_cell_${dayNum}"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dayNum.toString(),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = when {
                                                            isSelected -> Color.White
                                                            isToday -> MaterialTheme.colorScheme.primary
                                                            col == 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                )
                                                if (hasFestival) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) Color.White else MaterialTheme.colorScheme.secondary
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Empty cell inside grid
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Gray.copy(alpha = 0.03f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Selected Day Particulars below Grid
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(24.dp)
                            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${t("Selected Day", isTamil)}: ${t(selectedDayInfo.gregorianDate.month.name, isTamil)} ${selectedDayInfo.gregorianDate.dayOfMonth}, ${selectedDayInfo.gregorianDate.year}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Celebrations Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)),
                        border = borderStroke(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = t("Festivals", isTamil),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedDayInfo.festivalName ?: t("Auspicious Day", isTamil),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 3
                            )
                        }
                    }

                    // Auspicious timers Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)),
                        border = borderStroke(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = t("Panchangam Info", isTamil),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${t("Tamil Date", isTamil)}: ${selectedDayInfo.tamilMonth} ${selectedDayInfo.tamilDate}",
                                style = MaterialTheme.typography.bodySmall,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = "Nalla Neram: ${selectedDayInfo.nallaNeramMorning}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                fontSize = 11.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = "Rahu: ${selectedDayInfo.rahuKaalam}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 4. Quotations Wisdom from Tirukkural
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(borderStroke())
            ) {
                // Background visual gopuram temple illustration
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAteJ8QDBNRGx3YKE9JqSxOtLdwJt2W3dDb4qPYjQn2WJyh6rxrEzgdTXiEzziR2K2TrmQSUiWKviw3hTXC6MnvoAOr1rP3-xqXjV6Te36EdrlGWKZC2W27aHyZRcobcfaUC87LOP-zdElbc8c7nRV3pESs5Go9FBo9Cj8NYSwd6ZQKAOxMF8WxggM3gKhoiORZ38oVxsHUBv6ptmyBoLJXCp7ybeEfpMoBvOl4BbB-Cp1Vf2Ob7tbWQtkYxmE15eAIYyEL4TDgNgE",
                    contentDescription = "Temple gopuram",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                )

                // Content Text
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = t("Monthly Wisdom", isTamil).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldHighlight
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isTamil) "“மெய்ப்பொருள் காண்ப தறிவு.” - குறள் 355\n(அறிவு என்பது எப்பொருளின் மெய்யான உண்மையை காண்பதே ஆகும்.)"
                        else "“Action is better than inaction.” — Tirukkural",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Ad space
        item {
            AdArea()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FestivalsScreen(viewModel: CalendarViewModel, isTamil: Boolean) {
    val festivals by viewModel.filteredFestivals.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Screen Intro Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = t("Festivals 2024 - 2025", isTamil),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = t("Auspicious cultural dates aligned with standard almanac", isTamil),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Tune",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        // Live Search Input (Inside screen as well)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text(t("Search Festivals", isTamil)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("festivals_page_search"),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (festivals.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Not found",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = t("No matching festivals found", isTamil),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            } else {
                items(festivals) { item ->
                    FestivalRowItem(item = item, isTamil = isTamil, onSelect = {
                        viewModel.selectDate(item.gregorianDate)
                        viewModel.setActiveTab(CalendarTab.DAILY)
                    })
                }
            }

            // Sponsored Ad break
            item {
                Spacer(modifier = Modifier.height(8.dp))
                AdAreaHighlight(isTamil = isTamil)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FestivalRowItem(item: CalendarDayInfo, isTamil: Boolean, onSelect: () -> Unit) {
    val isPrimaryMajor = item.festivalName?.contains("பொங்கல்") == true || item.festivalName?.contains("தீபாவளி") == true || item.festivalName?.contains("புத்தாண்டு") == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("festival_item_${item.gregorianDate}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimaryMajor) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = borderStroke(
            if (isPrimaryMajor) MaterialTheme.colorScheme.primary
            else Color.LightGray.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Date Badge Block
            Column(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.gregorianDate.month.name.take(3),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Text(
                    text = item.gregorianDate.dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.tamilMonth} ${item.tamilDate} • ${t(item.gregorianDate.dayOfWeek.name, isTamil)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.festivalName ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                item.festivalDetail?.let { detail ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (item.isGovernmentHoliday) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Holiday",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: CalendarViewModel, isTamil: Boolean) {
    val showAstroAlerter by viewModel.showAstroAlerter.collectAsState()
    val isTamilLang by viewModel.isTamilLanguage.collectAsState()
    val location by viewModel.selectedLocation.collectAsState()

    var showPromoAlert by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Text(
                text = t("Settings", isTamil),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = t("Customize calendar rules and layout preferences", isTamil),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // 1. Language Toggle setting
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = t("Language", isTamil),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isTamilLang) "தற்போது தமிழ் மொழியில் உள்ளது" else "Currently English active",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    Switch(
                        checked = isTamilLang,
                        onCheckedChange = { viewModel.toggleLanguage() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("language_switch")
                    )
                }
            }
        }

        // 1.5 Dark Mode Toggle setting
        item {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = t("Dark Mode", isTamil),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isDarkMode) t("Dark visual theme active", isTamil) else t("Light visual theme active", isTamil),
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }
        }

        // 2. Location Astro Setter
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = t("Panchangam Location", isTamil),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Simulated selection buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Chennai", "Madurai", "Coimbatore", "Singapore").forEach { loc ->
                            Button(
                                onClick = { viewModel.updateLocation("$loc, Traditional Astro") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (location.startsWith(loc)) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (location.startsWith(loc)) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("location_button_${loc.lowercase()}")
                            ) {
                                Text(
                                    text = loc,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Alerts switches
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = t("Panchangam Alerter", isTamil),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = t("Receive timely alerts for Rahu & Nalla Neram", isTamil),
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    Switch(
                        checked = showAstroAlerter,
                        onCheckedChange = { viewModel.toggleAstroAlerter() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }

        // 4. Premium purchase Promo Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPromoAlert = true }
                    .testTag("premium_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = borderStroke(MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = t("Go Premium Pro", isTamil),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = t("Remove all advertisements & unlock offline warnings alarms.", isTamil),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(
                        onClick = { showPromoAlert = true },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .testTag("premium_arrow_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Buy",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Virtual Alert dialogue
        if (showPromoAlert) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = t("Premium Alerter Simulation", isTamil),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                        Text(
                            text = if (isTamilLang) "வாழ்த்துகள்! விளம்பரமில்லா பிரீமியம் தற்காலிகமாக இயக்கப்பட்டது."
                            else "Excellent choice! Ad-free premium simulation successfully activated.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Button(
                            onClick = { showPromoAlert = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onErrorContainer)
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "App Info: Developed by Pradeep M",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun AdArea() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("admob_placeholder"),
        shape = RoundedCornerShape(4.dp),
        border = borderStroke(Color.LightGray.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    // Test Ad Unit ID for banner
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Composable
fun AdAreaHighlight(isTamil: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("highlighted_ad"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = borderStroke(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = t("Sponsored Background", isTamil).uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        // Test Ad Unit ID for banner
                        adUnitId = "ca-app-pub-3940256099942544/6300978111"
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}

@Composable
fun borderStroke(color: Color = Color.LightGray.copy(alpha = 0.3f)) =
    androidx.compose.foundation.BorderStroke(1.dp, color)

// Simple clean bilingual dictionary lookup
fun t(key: String, isTamil: Boolean): String {
    if (!isTamil) return key
    return when (key) {
        "Daily" -> "தினசரி"
        "Monthly" -> "மாதாந்திரம்"
        "Festivals" -> "திருவிழாக்கள்"
        "Porutham" -> "பொருத்தம்"
        "Settings" -> "அமைப்புகள்"
        "Dark Mode" -> "இருண்ட பயன்முறை"
        "Dark visual theme active" -> "இருண்ட தீம் செயல்படுகிறது"
        "Light visual theme active" -> "ஒளி தீம் செயல்படுகிறது"
        "Marriage Compatibility" -> "ஜாதக பொருத்தம்"
        "Enter birth details to view compatibility insights." -> "பொருத்த விவரங்களைக் காண பிறந்த விவரங்களை உள்ளிடவும்."
        "Groom Details" -> "மாப்பிள்ளை விவரங்கள்"
        "Bride Details" -> "மணமகள் விவரங்கள்"
        "Full Name" -> "முழு பெயர்"
        "Date of Birth" -> "பிறந்த தேதி"
        "Time (Optional)" -> "நேரம் (விருப்பம்)"
        "Birth Place" -> "பிறந்த இடம்"
        "Check Compatibility" -> "பொருத்தம் பார்க்க"
        "Upload PDF/Image (Auto-fill)" -> "ஜாதகம் பதிவேற்ற (Auto-fill)"
        "Preparing Horoscope" -> "ஜாதகம் தயாராகிறது..."
        "Calculating Nakshatra" -> "நட்சத்திரம் கணக்கிடப்படுகிறது..."
        "Calculating Rasi" -> "ராசி கணக்கிடப்படுகிறது..."
        "Checking Compatibility" -> "பொருத்தம் சரிபார்க்கப்படுகிறது..."
        "Generating Report" -> "அறிக்கை உருவாக்கப்படுகிறது..."
        "Harmony Score" -> "பொருத்தமான மதிப்பெண்"
        "Exceptional Alignment" -> "மிகவும் சிறப்பான பொருத்தம்"
        "Excellent Alignment" -> "சிறப்பான பொருத்தம்"
        "Strong Alignment" -> "நல்ல பொருத்தம்"
        "Balanced Alignment" -> "சுமாரான பொருத்தம்"
        "Detailed Consultation Suggested" -> "ஜோதிடரை அணுகவும்"
        "Compatibility Summary" -> "பொருத்தச் சுருக்கம்"
        "Porutham Analysis" -> "பொருத்த பகுப்பாய்வு"
        "Dina Porutham" -> "தினப் பொருத்தம்"
        "Gana Porutham" -> "கணப் பொருத்தம்"
        "Mahendra Porutham" -> "மாகேந்திரப் பொருத்தம்"
        "Sthree Dheerga Porutham" -> "ஸ்திரீ தீர்க்கப் பொருத்தம்"
        "Yoni Porutham" -> "யோனிப் பொருத்தம்"
        "Rasi Porutham" -> "ராசிப் பொருத்தம்"
        "Rasi Adhipathi Porutham" -> "ராசியாதிபதி பொருத்தம்"
        "Vasya Porutham" -> "வசியப் பொருத்தம்"
        "Rajju Porutham" -> "ரஜ்ஜுப் பொருத்தம்"
        "Vedha Porutham" -> "வேதைப் பொருத்தம்"
        "Good Alignment" -> "பொருத்தம் உண்டு"
        "AI Insights" -> "செயற்கை நுண்ணறிவு கணிப்பு"
        "Strong indicators of mutual understanding." -> "பரஸ்பர புரிதலுக்கான வலுவான அறிகுறிகள் உள்ளன."
        "Positive family harmony patterns." -> "நேர்மறையான குடும்ப ஒற்றுமை அம்சங்கள் உள்ளன."
        "Balanced relationship dynamics." -> "சமநிலையான உறவுமுறை காணப்படுகிறது."
        "Save Report" -> "அறிக்கையைச் சேமி"
        "Report Saved" -> "அறிக்கை சேமிக்கப்பட்டது"
        "Share" -> "பகிர்"
        "Tamil Date" -> "தமிழ் தேதி"
        "Tithi" -> "திதி"
        "Nakshatram" -> "நட்சத்திரம்"
        "Rasi" -> "ராசி"
        "Nalla Neram" -> "நல்ல நேரம்"
        "Gowri Nalla Neram" -> "கௌரி நல்ல நேரம்"
        "Morning" -> "காலை"
        "Evening" -> "மாலை"
        "Rahu" -> "ராகு"
        "Kuligai" -> "குளிகை"
        "Yemagandam" -> "எமகண்டம்"
        "SaniRahu" -> "சனி / ர"
        "Soolam" -> "சூலம்"
        "Parigaram" -> "பரிகாரம்"
        "Karanam" -> "கரணம்"
        "Wisdom" -> "திருக்குறள்"
        "Auspicious Day" -> "மங்கல பொழுது"
        "Panchangam Info" -> "பஞ்சாங்க விவரம்"
        "Selected Day" -> "தேர்ந்தெடுக்கப்பட்ட நாள்"
        "Auspicious cultural dates aligned with standard almanac" -> "பாரம்பரிய பஞ்சாங்கத்தின்படி கணிக்கப்பட்ட திருவிழாக்கள்"
        "Search Festivals" -> "திருவிழாக்களைத் தேடுக..."
        "No matching festivals found" -> "பொருந்தக்கூடிய திருவிழாக்கள் எதுவுமில்லை"
        "Panchangam Location" -> "பஞ்சாங்க கணிப்பு இடம்"
        "Panchangam Alerter" -> "நேர எச்சரிக்கை மணி"
        "Receive timely alerts for Rahu & Nalla Neram" -> "நல்ல நேரம், ராகு கால தொடக்கத்தின் போது அறிவிப்புகளைப் பெறுக"
        "Go Premium Pro" -> "பிரீமியம் ப்ரோ பெறுக"
        "Remove all advertisements & unlock offline warnings alarms." -> "அனைத்து விளம்பரங்களையும் நீக்கி, எச்சரிக்கை மணிகளை இயக்குக."
        "Premium Alerter Simulation" -> "பிரீமியம் கணிப்பு சிமுலேஷன்"
        "Government Holiday" -> "அரசு விடுமுறை நாள்"
        "Language" -> "மொழி (Language Select)"
        "Customize calendar rules and layout preferences" -> "பஞ்சாங்க விதிகள் மற்றும் விருப்பங்களை மாற்றுக"
        "Auspicious Times" -> "சுப நேரங்கள்"
        "Monthly Wisdom" -> "மாதத்தின் ஞானக்குறள்"
        "Unlock Premium Almanac Functions" -> "முழுமையான விஐபி பஞ்சாங்கம்"
        "Receive immediate notification for fasts and eclipse timing calculations." -> "நோன்பு மற்றும் கிரகண நேர துல்லிய கணிப்புகளை உடனுக்குடன் பெறுக"
        "Sponsored Background" -> "விளம்பரம்"
        "Tamil Daily Calendar Pro" -> "தமிழ் தினசரி காலண்டர் ப்ரோ"
        "Next Day" -> "அடுத்த நாள்"
        "SUNDAY" -> "ஞாயிற்றுக்கிழமை"
        "MONDAY" -> "திங்கள்கிழமை"
        "TUESDAY" -> "செவ்வாய்க்கிழமை"
        "WEDNESDAY" -> "புதன்கிழமை"
        "THURSDAY" -> "வியாழக்கிழமை"
        "FRIDAY" -> "வெள்ளிக்கிழமை"
        "SATURDAY" -> "சனிக்கிழமை"
        "JANUARY" -> "ஜனவரி"
        "FEBRUARY" -> "பிப்ரவரி"
        "MARCH" -> "மார்ச்"
        "APRIL" -> "ஏப்ரல்"
        "MAY" -> "மே"
        "JUNE" -> "ஜூன்"
        "JULY" -> "ஜூலை"
        "AUGUST" -> "ஆகஸ்ட்"
        "SEPTEMBER" -> "செப்டம்பர்"
        "OCTOBER" -> "அக்டோபர்"
        "NOVEMBER" -> "நவம்பர்"
        "DECEMBER" -> "டிசம்பர்"
        else -> key
    }
}
