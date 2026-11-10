package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.model.RestCategory
import com.example.data.model.RestLocation
import com.example.data.repository.AuthRepository
import com.example.data.repository.BookingRepository
import com.example.data.repository.LocationsRepository
import com.example.data.repository.RealPlacesRepository
import com.example.data.repository.SearchHistoryRepository
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookingDialog
import com.example.ui.screens.BookingsHistoryScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.RadarScreen
import com.example.ui.screens.SettingsScreen
import com.example.util.LocationTracker
import com.example.util.NotificationHelper
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    EXPLORE("Explore", Icons.Default.Explore, "tab_explore"),
    RADAR("Radar", Icons.Default.Radar, "tab_radar"),
    BOOKINGS("Bookings", Icons.Default.BookOnline, "tab_bookings"),
    SETTINGS("Settings", Icons.Default.Settings, "tab_settings")
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize Database & Repositories
    val database = remember { AppDatabase.getInstance(context) }
    val authRepo = remember { AuthRepository(database.appDao()) }
    val bookingRepo = remember { BookingRepository(database.appDao()) }
    val searchHistoryRepo = remember { SearchHistoryRepository(database.appDao()) }
    val locationTracker = remember { LocationTracker(context) }
    val notificationHelper = remember { NotificationHelper(context) }
    val realPlacesRepo = remember { RealPlacesRepository(context) }

    val currentUser by authRepo.currentUser.collectAsState()
    val locationState by locationTracker.locationState.collectAsState()
    val allBookings by bookingRepo.getAllBookings().collectAsState(initial = emptyList())
    val searchHistory by searchHistoryRepo.getRecentSearches().collectAsState(initial = emptyList())
    val livePlaces by realPlacesRepo.cachedPlaces.collectAsState()
    val isFetchingLive by realPlacesRepo.isFetchingLive.collectAsState()
    val lastDataSource by realPlacesRepo.lastDataSource.collectAsState()

    // Permissions Request
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            locationTracker.requestLocationUpdate()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        authRepo.initialize()
        bookingRepo.seedSampleBookingIfEmpty()

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Automatically query real places when user's location changes (stabilized coordinate key to prevent rapid micro-jitter restarts)
    val locationKey = remember(locationState.latitude, locationState.longitude, locationState.townName) {
        val latRound = Math.round(locationState.latitude * 1000.0) / 1000.0
        val lngRound = Math.round(locationState.longitude * 1000.0) / 1000.0
        "${locationState.townName}-$latRound-$lngRound"
    }

    LaunchedEffect(locationKey) {
        realPlacesRepo.fetchRealPlaces(
            locationState.latitude,
            locationState.longitude,
            locationState.townName
        )
    }

    // App Navigation State
    var currentTab by remember { mutableStateOf(AppTab.EXPLORE) }
    var selectedCategory by remember { mutableStateOf(RestCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDetailLocation by remember { mutableStateOf<RestLocation?>(null) }
    var bookingLocationDialog by remember { mutableStateOf<RestLocation?>(null) }
    var isAuthModalOpen by remember { mutableStateOf(false) }

    // Dynamic filtered real places
    val filteredLocations = remember(searchQuery, selectedCategory, livePlaces, locationState.latitude, locationState.longitude) {
        val baseList = if (livePlaces.isNotEmpty()) livePlaces
        else LocationsRepository.getNearestPlacesForCoordinates(locationState.latitude, locationState.longitude, locationState.townName)

        baseList.filter { loc ->
            val matchCat = selectedCategory == RestCategory.ALL || loc.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    loc.name.contains(searchQuery, ignoreCase = true) ||
                    loc.address.contains(searchQuery, ignoreCase = true) ||
                    loc.town.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
        }.sortedBy {
            LocationsRepository.calculateDistanceKm(locationState.latitude, locationState.longitude, it.latitude, it.longitude)
        }
    }

    // Back handling
    if (selectedDetailLocation != null) {
        BackHandler {
            selectedDetailLocation = null
        }
    } else if (isAuthModalOpen) {
        BackHandler {
            isAuthModalOpen = false
        }
    } else if (currentTab != AppTab.EXPLORE) {
        BackHandler {
            currentTab = AppTab.EXPLORE
        }
    }

    // Active Booking Dialog
    bookingLocationDialog?.let { loc ->
        BookingDialog(
            location = loc,
            userId = currentUser?.id ?: "guest-user",
            onDismiss = { bookingLocationDialog = null },
            onBookingConfirmed = { uId, pId, pName, pCat, pAddr, pImg, date, time, guests, optTitle, total, method ->
                coroutineScope.launch {
                    val newBooking = bookingRepo.createBooking(
                        userId = uId,
                        placeId = pId,
                        placeName = pName,
                        placeCategory = pCat,
                        placeAddress = pAddr,
                        placeImageUrl = pImg,
                        checkInDate = date,
                        checkInTime = time,
                        guests = guests,
                        optionTitle = optTitle,
                        totalPrice = total,
                        paymentMethod = method
                    )
                    // Push notification alert for upcoming booking
                    notificationHelper.sendBookingConfirmedNotification(
                        bookingRef = newBooking.bookingReference,
                        placeName = pName,
                        date = date,
                        time = time
                    )
                }
            }
        )
    }

    // Auth Screen Modal
    if (isAuthModalOpen) {
        AuthScreen(
            onSignIn = { email, pass -> authRepo.signIn(email, pass) },
            onSignUp = { name, email, pass, phone -> authRepo.signUp(name, email, pass, phone) },
            onGoogleSignIn = { authRepo.signInWithGoogle() },
            onSuccess = { isAuthModalOpen = false }
        )
        return
    }

    // Detail Screen
    selectedDetailLocation?.let { loc ->
        val dist = LocationsRepository.calculateDistanceKm(
            locationState.latitude,
            locationState.longitude,
            loc.latitude,
            loc.longitude
        )
        DetailScreen(
            location = loc,
            distanceKm = dist,
            onBackClick = { selectedDetailLocation = null },
            onBookClick = {
                bookingLocationDialog = loc
            }
        )
        return
    }

    // Responsive Layout: Use NavigationRail on Tablet (width >= 600dp), BottomNavigationBar on Mobile
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp

        if (isTablet) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🏨",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTab.values().forEach { tab ->
                        NavigationRailItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    RenderTabContent(
                        tab = currentTab,
                        locationState = locationState,
                        filteredLocations = filteredLocations,
                        searchHistory = searchHistory,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        allBookings = allBookings,
                        currentUser = currentUser,
                        isFetchingLive = isFetchingLive,
                        lastDataSource = lastDataSource,
                        currentApiKey = realPlacesRepo.getGooglePlacesApiKey(),
                        onSaveApiKey = { newKey ->
                            realPlacesRepo.saveCustomApiKey(newKey)
                            coroutineScope.launch {
                                realPlacesRepo.fetchRealPlaces(
                                    locationState.latitude,
                                    locationState.longitude,
                                    locationState.townName
                                )
                            }
                        },
                        onRefreshLivePlaces = {
                            coroutineScope.launch {
                                realPlacesRepo.fetchRealPlaces(
                                    locationState.latitude,
                                    locationState.longitude,
                                    locationState.townName
                                )
                            }
                        },
                        onCategorySelected = { selectedCategory = it },
                        onSearchQueryChange = {
                            searchQuery = it
                            if (it.length >= 3) {
                                coroutineScope.launch {
                                    searchHistoryRepo.addSearch(it, locationState.townName)
                                }
                            }
                        },
                        onManualLocationSelected = { town ->
                            locationTracker.setManualLocation(town)
                        },
                        onRequestGpsLocation = {
                            locationTracker.requestLocationUpdate()
                        },
                        onPlaceClick = { selectedDetailLocation = it },
                        onBookPlaceClick = { bookingLocationDialog = it },
                        onCancelBooking = { bkgId ->
                            coroutineScope.launch { bookingRepo.cancelBooking(bkgId) }
                        },
                        onNavigateToAuth = { isAuthModalOpen = true },
                        onSignOut = { coroutineScope.launch { authRepo.signOut() } },
                        onSendTestBookingNotification = {
                            notificationHelper.sendUpcomingAppointmentReminder(
                                placeName = "Sunset Eco-Cabanas & Beach Shacks",
                                scheduledTime = "Tomorrow at 02:00 PM"
                            )
                        },
                        onSendTestPromoNotification = {
                            notificationHelper.sendPromotionalOfferNotification(
                                title = "Waves IT Weekend Escape",
                                code = "WAVES30"
                            )
                        },
                        onExploreClick = { currentTab = AppTab.EXPLORE }
                    )
                }
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        AppTab.values().forEach { tab ->
                            NavigationBarItem(
                                selected = currentTab == tab,
                                onClick = { currentTab = tab },
                                icon = { Icon(tab.icon, contentDescription = tab.title) },
                                label = { Text(tab.title, fontSize = 11.sp) },
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    RenderTabContent(
                        tab = currentTab,
                        locationState = locationState,
                        filteredLocations = filteredLocations,
                        searchHistory = searchHistory,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        allBookings = allBookings,
                        currentUser = currentUser,
                        isFetchingLive = isFetchingLive,
                        lastDataSource = lastDataSource,
                        currentApiKey = realPlacesRepo.getGooglePlacesApiKey(),
                        onSaveApiKey = { newKey ->
                            realPlacesRepo.saveCustomApiKey(newKey)
                            coroutineScope.launch {
                                realPlacesRepo.fetchRealPlaces(
                                    locationState.latitude,
                                    locationState.longitude,
                                    locationState.townName
                                )
                            }
                        },
                        onRefreshLivePlaces = {
                            coroutineScope.launch {
                                realPlacesRepo.fetchRealPlaces(
                                    locationState.latitude,
                                    locationState.longitude,
                                    locationState.townName
                                )
                            }
                        },
                        onCategorySelected = { selectedCategory = it },
                        onSearchQueryChange = {
                            searchQuery = it
                            if (it.length >= 3) {
                                coroutineScope.launch {
                                    searchHistoryRepo.addSearch(it, locationState.townName)
                                }
                            }
                        },
                        onManualLocationSelected = { town ->
                            locationTracker.setManualLocation(town)
                        },
                        onRequestGpsLocation = {
                            locationTracker.requestLocationUpdate()
                        },
                        onPlaceClick = { selectedDetailLocation = it },
                        onBookPlaceClick = { bookingLocationDialog = it },
                        onCancelBooking = { bkgId ->
                            coroutineScope.launch { bookingRepo.cancelBooking(bkgId) }
                        },
                        onNavigateToAuth = { isAuthModalOpen = true },
                        onSignOut = { coroutineScope.launch { authRepo.signOut() } },
                        onSendTestBookingNotification = {
                            notificationHelper.sendUpcomingAppointmentReminder(
                                placeName = "Sunset Eco-Cabanas & Beach Shacks",
                                scheduledTime = "Tomorrow at 02:00 PM"
                            )
                        },
                        onSendTestPromoNotification = {
                            notificationHelper.sendPromotionalOfferNotification(
                                title = "Waves IT Weekend Escape",
                                code = "WAVES30"
                            )
                        },
                        onExploreClick = { currentTab = AppTab.EXPLORE }
                    )
                }
            }
        }
    }
}

@Composable
fun RenderTabContent(
    tab: AppTab,
    locationState: com.example.util.UserLocationState,
    filteredLocations: List<RestLocation>,
    searchHistory: List<com.example.data.local.SearchHistoryEntity>,
    selectedCategory: RestCategory,
    searchQuery: String,
    allBookings: List<com.example.data.local.BookingEntity>,
    currentUser: com.example.data.model.UserProfile?,
    isFetchingLive: Boolean,
    lastDataSource: String,
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onRefreshLivePlaces: () -> Unit,
    onCategorySelected: (RestCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onManualLocationSelected: (String) -> Unit,
    onRequestGpsLocation: () -> Unit,
    onPlaceClick: (RestLocation) -> Unit,
    onBookPlaceClick: (RestLocation) -> Unit,
    onCancelBooking: (String) -> Unit,
    onNavigateToAuth: () -> Unit,
    onSignOut: () -> Unit,
    onSendTestBookingNotification: () -> Unit,
    onSendTestPromoNotification: () -> Unit,
    onExploreClick: () -> Unit
) {
    when (tab) {
        AppTab.EXPLORE -> {
            ExploreScreen(
                locationState = locationState,
                locations = filteredLocations,
                searchHistory = searchHistory,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                isFetchingLive = isFetchingLive,
                lastDataSource = lastDataSource,
                onRefreshLivePlaces = onRefreshLivePlaces,
                onCategorySelected = onCategorySelected,
                onSearchQueryChange = onSearchQueryChange,
                onManualLocationSelected = onManualLocationSelected,
                onRequestGpsLocation = onRequestGpsLocation,
                onPlaceClick = onPlaceClick,
                onBookPlaceClick = onBookPlaceClick
            )
        }
        AppTab.RADAR -> {
            RadarScreen(
                locationState = locationState,
                locations = filteredLocations,
                selectedCategory = selectedCategory,
                isFetchingLive = isFetchingLive,
                lastDataSource = lastDataSource,
                onRefreshLivePlaces = onRefreshLivePlaces,
                onCategorySelected = onCategorySelected,
                onRecenterGps = onRequestGpsLocation,
                onPlaceSelected = onPlaceClick,
                onBookPlaceClick = onBookPlaceClick
            )
        }
        AppTab.BOOKINGS -> {
            BookingsHistoryScreen(
                bookings = allBookings,
                onCancelBooking = onCancelBooking,
                onExploreClick = onExploreClick
            )
        }
        AppTab.SETTINGS -> {
            SettingsScreen(
                user = currentUser,
                currentApiKey = currentApiKey,
                lastDataSource = lastDataSource,
                onSaveApiKey = onSaveApiKey,
                onSignOut = onSignOut,
                onNavigateToAuth = onNavigateToAuth,
                onSendTestBookingNotification = onSendTestBookingNotification,
                onSendTestPromoNotification = onSendTestPromoNotification
            )
        }
    }
}
