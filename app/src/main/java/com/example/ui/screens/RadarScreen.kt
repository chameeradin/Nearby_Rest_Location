package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RestCategory
import com.example.data.model.RestLocation
import com.example.data.repository.LocationsRepository
import com.example.ui.components.CategoryBadge
import com.example.ui.components.RatingBadge
import com.example.ui.theme.CabanaTeal
import com.example.ui.theme.CafeAmber
import com.example.ui.theme.HotelBlue
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RestaurantOrange
import com.example.ui.theme.TealAccent
import com.example.util.UserLocationState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarScreen(
    locationState: UserLocationState,
    locations: List<RestLocation>,
    selectedCategory: RestCategory,
    isFetchingLive: Boolean = false,
    lastDataSource: String = "Google Maps & Places Engine",
    onRefreshLivePlaces: () -> Unit = {},
    onCategorySelected: (RestCategory) -> Unit,
    onRecenterGps: () -> Unit,
    onPlaceSelected: (RestLocation) -> Unit,
    onBookPlaceClick: (RestLocation) -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf(locations.firstOrNull()) }
    var radarRangeKm by remember { mutableFloatStateOf(5.0f) } // 2km, 5km, 10km, 25km

    // Radar scan rotation animation (360 degrees)
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    val filteredLocations = remember(locations, selectedCategory) {
        if (selectedCategory == RestCategory.ALL) locations
        else locations.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // App Top Title & Radar Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Nearby Rest Radar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Live 360° Rest Place Scanner • ${locationState.townName}",
                            fontSize = 11.sp,
                            color = TealAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Open Full Google Maps App
                    OutlinedButton(
                        onClick = {
                            try {
                                val gmmIntentUri = Uri.parse(
                                    "geo:${locationState.latitude},${locationState.longitude}?q=hotels+restaurants+near+me"
                                )
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val webUri = Uri.parse("https://www.google.com/maps/search/hotels+restaurants+near+me/@${locationState.latitude},${locationState.longitude},14z")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                }
                            } catch (_: Exception) {
                                Toast.makeText(context, "Opening Google Maps...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("open_google_maps_radar")
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Google Maps", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(RestCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelected(cat) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(cat.iconEmoji)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(cat.displayName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Radar Range Selector & GPS Recenter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(2.0f to "2 km", 5.0f to "5 km", 10.0f to "10 km").forEach { (rng, label) ->
                    val isSelected = radarRangeKm == rng
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { radarRangeKm = rng }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = TealAccent.copy(alpha = 0.15f),
                modifier = Modifier.clickable { onRecenterGps() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GpsFixed, contentDescription = null, tint = TealAccent, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Recenter GPS", fontSize = 11.sp, color = TealAccent, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // THE RADAR CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF07111D))
            ) {
                val canvasDim = minOf(maxWidth, maxHeight)

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(filteredLocations) {
                            detectTapGestures { tapOffset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val maxRadius = (size.width / 2f) * 0.85f

                                // Check which pin was tapped
                                filteredLocations.forEach { loc ->
                                    val distKm = LocationsRepository.calculateDistanceKm(
                                        locationState.latitude, locationState.longitude,
                                        loc.latitude, loc.longitude
                                    ).toFloat()

                                    val dLat = Math.toRadians(loc.latitude - locationState.latitude)
                                    val dLon = Math.toRadians(loc.longitude - locationState.longitude)
                                    val y = sin(dLon) * cos(Math.toRadians(loc.latitude))
                                    val x = cos(Math.toRadians(locationState.latitude)) * sin(Math.toRadians(loc.latitude)) -
                                            sin(Math.toRadians(locationState.latitude)) * cos(Math.toRadians(loc.latitude)) * cos(dLon)
                                    val bearing = (Math.toDegrees(kotlin.math.atan2(y, x)) + 360.0) % 360.0

                                    val rRatio = (distKm / radarRangeKm).coerceIn(0.12f, 0.95f)
                                    val pinRadius = maxRadius * rRatio
                                    val angleRad = Math.toRadians(bearing - 90.0)
                                    val pinX = center.x + (pinRadius * cos(angleRad)).toFloat()
                                    val pinY = center.y + (pinRadius * sin(angleRad)).toFloat()

                                    val touchDist = kotlin.math.hypot(tapOffset.x - pinX, tapOffset.y - pinY)
                                    if (touchDist <= 48f) {
                                        selectedLocation = loc
                                    }
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w / 2f, h / 2f)
                    val maxRadius = (w / 2f) * 0.85f

                    // 1. Concentric Distance Rings
                    val ringSteps = listOf(0.25f to "1 km", 0.5f to "2.5 km", 0.75f to "4 km", 1.0f to "${radarRangeKm.toInt()} km")
                    ringSteps.forEach { (fraction, label) ->
                        val r = maxRadius * fraction
                        drawCircle(
                            color = Color(0xFF1E3A5F),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                        )

                        // Distance label
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint().apply {
                                isAntiAlias = true
                                textSize = 24f
                                color = android.graphics.Color.parseColor("#4A6B8F")
                                isFakeBoldText = true
                            }
                            canvas.nativeCanvas.drawText(label, center.x + 8f, center.y - r + 24f, paint)
                        }
                    }

                    // 2. Crosshair Grid Lines
                    drawLine(
                        color = Color(0xFF1E3A5F),
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0xFF1E3A5F),
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1.5f
                    )

                    // Compass Indicators (N, S, E, W)
                    drawIntoCanvas { canvas ->
                        val textPaint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 28f
                            color = android.graphics.Color.parseColor("#E0A96D")
                            isFakeBoldText = true
                        }
                        canvas.nativeCanvas.drawText("N", center.x - 8f, center.y - maxRadius - 10f, textPaint)
                        canvas.nativeCanvas.drawText("S", center.x - 8f, center.y + maxRadius + 30f, textPaint)
                        canvas.nativeCanvas.drawText("E", center.x + maxRadius + 14f, center.y + 10f, textPaint)
                        canvas.nativeCanvas.drawText("W", center.x - maxRadius - 34f, center.y + 10f, textPaint)
                    }

                    // 3. Rotating Radar Scanner Beam
                    val sweepRad = Math.toRadians(sweepAngle.toDouble() - 90.0)
                    val sweepEndX = center.x + (maxRadius * cos(sweepRad)).toFloat()
                    val sweepEndY = center.y + (maxRadius * sin(sweepRad)).toFloat()

                    // Beam line
                    drawLine(
                        color = Color(0xFF2A9D8F),
                        start = center,
                        end = Offset(sweepEndX, sweepEndY),
                        strokeWidth = 3f
                    )

                    // 4. Center: User GPS Icon
                    drawCircle(color = Color(0xFF00C48C).copy(alpha = 0.25f), radius = 24f, center = center)
                    drawCircle(color = Color.White, radius = 10f, center = center)
                    drawCircle(color = Color(0xFF2563EB), radius = 7f, center = center)

                    // 5. Draw Nearest Places Markers on Radar
                    filteredLocations.forEach { loc ->
                        val distKm = LocationsRepository.calculateDistanceKm(
                            locationState.latitude, locationState.longitude,
                            loc.latitude, loc.longitude
                        ).toFloat()

                        val dLat = Math.toRadians(loc.latitude - locationState.latitude)
                        val dLon = Math.toRadians(loc.longitude - locationState.longitude)
                        val y = sin(dLon) * cos(Math.toRadians(loc.latitude))
                        val x = cos(Math.toRadians(locationState.latitude)) * sin(Math.toRadians(loc.latitude)) -
                                sin(Math.toRadians(locationState.latitude)) * cos(Math.toRadians(loc.latitude)) * cos(dLon)
                        val bearing = (Math.toDegrees(kotlin.math.atan2(y, x)) + 360.0) % 360.0

                        val rRatio = (distKm / radarRangeKm).coerceIn(0.14f, 0.94f)
                        val pinRadius = maxRadius * rRatio
                        val angleRad = Math.toRadians(bearing - 90.0)
                        val pinX = center.x + (pinRadius * cos(angleRad)).toFloat()
                        val pinY = center.y + (pinRadius * sin(angleRad)).toFloat()

                        val isSelected = selectedLocation?.id == loc.id
                        val pinColor = when (loc.category) {
                            RestCategory.HOTEL -> HotelBlue
                            RestCategory.RESTAURANT -> RestaurantOrange
                            RestCategory.CABANA -> CabanaTeal
                            RestCategory.CAFE -> CafeAmber
                            RestCategory.ALL -> Color(0xFF2A9D8F)
                        }

                        // Selected place target reticle
                        if (isSelected) {
                            drawCircle(
                                color = Color(0xFFE0A96D),
                                radius = 24f,
                                center = Offset(pinX, pinY),
                                style = Stroke(width = 3f)
                            )
                        }

                        // Marker outer & inner
                        drawCircle(color = Color.White, radius = if (isSelected) 16f else 12f, center = Offset(pinX, pinY))
                        drawCircle(color = pinColor, radius = if (isSelected) 12f else 8f, center = Offset(pinX, pinY))

                        // Small Name tag
                        drawIntoCanvas { canvas ->
                            val labelPaint = Paint().asFrameworkPaint().apply {
                                isAntiAlias = true
                                textSize = if (isSelected) 26f else 22f
                                color = if (isSelected) android.graphics.Color.WHITE else android.graphics.Color.LTGRAY
                                isFakeBoldText = isSelected
                            }
                            val shortName = loc.name.take(12)
                            canvas.nativeCanvas.drawText(shortName, pinX + 16f, pinY + 6f, labelPaint)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Place Card
        selectedLocation?.let { loc ->
            val dist = LocationsRepository.calculateDistanceKm(
                locationState.latitude,
                locationState.longitude,
                loc.latitude,
                loc.longitude
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("radar_selected_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = loc.imageUrl,
                                contentDescription = loc.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryBadge(category = loc.category)
                                RatingBadge(rating = loc.rating)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = loc.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "🎯 $dist km away • ${loc.address}",
                                fontSize = 12.sp,
                                color = TealAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${loc.priceAmount.toInt()} / ${loc.priceUnit}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Google Maps Direction Button
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode("${loc.name}, ${loc.address}"))
                                        context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                                    } catch (_: Exception) {
                                        Toast.makeText(context, loc.address, Toast.LENGTH_LONG).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Map", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onPlaceSelected(loc) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Details", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Button(
                                onClick = { onBookPlaceClick(loc) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.BookOnline, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nearest Radar Feed: Places sorted strictly by distance
        Text(
            text = "Nearest Detected Rest Places (${filteredLocations.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredLocations) { loc ->
                val dist = LocationsRepository.calculateDistanceKm(
                    locationState.latitude, locationState.longitude,
                    loc.latitude, loc.longitude
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .width(220.dp)
                        .clickable { selectedLocation = loc }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = loc.imageUrl,
                                contentDescription = loc.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = "$dist km",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = loc.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = loc.category.displayName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
