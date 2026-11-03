package com.example.equalizer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.audiofx.AudioEffect
import android.media.audiofx.DynamicsProcessing
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

class MainActivity : ComponentActivity() {
    private var dynamicsProcessing: DynamicsProcessing? = null
    private var currentSessionId = 0

    private val audioSessionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val sessionId = intent?.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, 0) ?: 0
            
            if (sessionId != 0 && sessionId != currentSessionId) {
                currentSessionId = sessionId
                initializeDSP(sessionId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Register for audio session updates from other apps
        val filter = IntentFilter()
        filter.addAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
        filter.addAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        registerReceiver(audioSessionReceiver, filter, RECEIVER_NOT_EXPORTED)

        initializeDSP(0) // Default to global session

        setContent {
            MaterialTheme {
                EqualizerApp(dynamicsProcessing)
            }
        }
    }

    private fun initializeDSP(sessionId: Int = 0) {
        try {
            dynamicsProcessing?.release() // Release old one if exists

            val bandsCount = 9
            val mbcBandsCount = 3
            
            val builder = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                2, // channel count (Stereo for A52)
                false, 0, // pre-EQ
                true, mbcBandsCount, // MBC
                true, bandsCount, // post-EQ
                true // Limiter
            )
            
            val config = builder.build()
            dynamicsProcessing = DynamicsProcessing(0, sessionId, config)
            
            // Configure MBC for Galaxy A52 Speakers
            val mbcCutoffs = floatArrayOf(250f, 4000f)
            for (i in 0 until mbcBandsCount) {
                val cutoff = if (i < mbcCutoffs.size) mbcCutoffs[i] else 20000f
                val mbcBand = DynamicsProcessing.MbcBand(
                    true, cutoff, 50f, 100f, 2f, -20f, 6f, -80f, 1f, 0f, 0f
                )
                dynamicsProcessing?.setMbcBandAllChannelsTo(i, mbcBand)
            }

            // Configure Post-EQ
            val bandFreqs = floatArrayOf(63f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
            for (i in 0 until bandsCount) {
                val eqBand = DynamicsProcessing.EqBand(true, bandFreqs[i], 0f)
                dynamicsProcessing?.setPostEqBandAllChannelsTo(i, eqBand)
            }
            
            val limiter = DynamicsProcessing.Limiter(true, true, 0, 10f, 100f, 2f, -1f, 0f)
            dynamicsProcessing?.setLimiterAllChannelsTo(limiter)
            
            dynamicsProcessing?.enabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(audioSessionReceiver)
        dynamicsProcessing?.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerApp(dsp: DynamicsProcessing?) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val surfaceColor = if (isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFF8F9FB)
    val primaryText = if (isDarkTheme) Color.White else Color(0xFF1D1D1F)
    val secondaryText = Color.Gray
    val accentColor = Color(0xFF4C84FF)
    val dividerColor = if (isDarkTheme) Color(0xFF38383A) else Color(0xFFE5E5E5)
    val trackColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF2F2F2)

    var isEnabled by remember { mutableStateOf(true) }
    var masterBoost by remember { mutableFloatStateOf(0f) }
    var highEndSparkle by remember { mutableFloatStateOf(0f) }
    
    val bands = remember { listOf(63, 125, 250, 500, 1000, 2000, 4000, 8000, 16000) }
    val bandLevels = remember { mutableStateListOf<Float>().apply { repeat(bands.size) { add(0f) } } }
    val customLevels = remember { mutableStateListOf<Float>().apply { repeat(bands.size) { add(0f) } } }
    
    var selectedPreset by remember { mutableIntStateOf(0) }
    val minLevel = -12f
    val maxLevel = 12f

    val presets = remember { listOf("Normal", "B&O Signature", "Vocal Clarity", "Custom") }
    val boLevels = listOf(0f, 4.5f, 3.5f, 2.5f, 1.5f, 3.0f, 5.0f, 4.0f, 7.0f)
    val vocalLevels = listOf(-2.0f, -1.0f, 0f, 2.0f, 5.0f, 4.0f, 2.0f, 0f, -2.0f)

    LaunchedEffect(dsp, isEnabled) {
        dsp?.enabled = isEnabled
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DSP Pro", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = primaryText) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(0.9f)) {
                PresetCard(presets, selectedPreset, isEnabled, surfaceColor, primaryText, secondaryText, accentColor, dividerColor) { index ->
                    if (selectedPreset == index) return@PresetCard
                    selectedPreset = index
                    highEndSparkle = 0f
                    val targetLevels = when (index) {
                        0 -> List(bands.size) { 0f }
                        1 -> boLevels
                        2 -> vocalLevels
                        else -> customLevels.toList()
                    }
                    for (i in bands.indices) {
                        val level = targetLevels[i]
                        bandLevels[i] = level
                        val eqBand = dsp?.getPostEqBandByChannelIndex(0, i)
                        if (eqBand != null) {
                            eqBand.gain = level
                            dsp.setPostEqBandAllChannelsTo(i, eqBand)
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                EqualizerSection(bands, bandLevels, minLevel, maxLevel, isEnabled, accentColor, secondaryText, trackColor) { index, value ->
                    if (selectedPreset != 3) {
                        for (i in bands.indices) customLevels[i] = bandLevels[i]
                        selectedPreset = 3
                        highEndSparkle = 0f
                    }
                    bandLevels[index] = value
                    customLevels[index] = value
                    val eqBand = dsp?.getPostEqBandByChannelIndex(0, index)
                    if (eqBand != null) {
                        eqBand.gain = value
                        dsp.setPostEqBandAllChannelsTo(index, eqBand)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val isSparkleEnabled = isEnabled && selectedPreset == 1
                        Text("High-End Sparkle", fontSize = 13.sp, color = if (isSparkleEnabled) primaryText else secondaryText)
                        Text("${highEndSparkle.toInt()} dB", fontSize = 13.sp, color = if (isSparkleEnabled) accentColor else secondaryText)
                    }
                    Slider(
                        value = highEndSparkle,
                        onValueChange = { sparkle ->
                            highEndSparkle = sparkle
                            listOf(6, 7, 8).forEach { idx ->
                                val base = boLevels[idx]
                                val newLevel = (base + sparkle).coerceIn(minLevel, maxLevel)
                                bandLevels[idx] = newLevel
                                val eqBand = dsp?.getPostEqBandByChannelIndex(0, idx)
                                if (eqBand != null) {
                                    eqBand.gain = newLevel
                                    dsp.setPostEqBandAllChannelsTo(idx, eqBand)
                                }
                            }
                        },
                        valueRange = -6f..6f,
                        enabled = isEnabled && selectedPreset == 1,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Speaker Master Boost", fontSize = 13.sp, color = primaryText)
                        Text("${masterBoost.toInt()} dB", fontSize = 13.sp, color = accentColor)
                    }
                    Slider(
                        value = masterBoost,
                        onValueChange = { 
                            masterBoost = it
                            dsp?.setInputGainAllChannelsTo(it)
                        },
                        valueRange = 0f..12f,
                        enabled = isEnabled,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Enable advanced effects", fontSize = 16.sp, color = primaryText)
                Switch(checked = isEnabled, onCheckedChange = { isEnabled = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor))
            }
        }
    }
}

@Composable
fun EqualizerSection(bands: List<Int>, levels: List<Float>, min: Float, max: Float, isEnabled: Boolean, accentColor: Color, secondaryText: Color, trackColor: Color, onValueChange: (Int, Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 35.dp, bottom = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            levels.forEach { level ->
                val db = level.toInt()
                Text(text = if (db > 0) "+$db" else db.toString(), fontSize = 9.sp, color = if (isEnabled) accentColor else secondaryText, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text("12", fontSize = 9.sp, color = secondaryText)
                Text("0", fontSize = 9.sp, color = secondaryText)
                Text("-12", fontSize = 9.sp, color = secondaryText)
            }
            Row(modifier = Modifier.fillMaxSize().padding(start = 35.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                bands.forEachIndexed { index, _ ->
                    CustomVerticalSlider(value = levels[index], min = min, max = max, isEnabled = isEnabled, accentColor = accentColor, trackColor = trackColor, onValueChange = { onValueChange(index, it) })
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(start = 35.dp, top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            bands.forEach { freq ->
                Text(text = if (freq >= 1000) "${freq / 1000}k" else freq.toString(), fontSize = 9.sp, color = secondaryText, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
            }
        }
    }
}

@Composable
fun CustomVerticalSlider(value: Float, min: Float, max: Float, isEnabled: Boolean, accentColor: Color, trackColor: Color, onValueChange: (Float) -> Unit) {
    Box(modifier = Modifier.fillMaxHeight().width(28.dp).pointerInput(isEnabled) {
        if (!isEnabled) return@pointerInput
        detectTapGestures { offset ->
            val newValue = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
            onValueChange(min + newValue * (max - min))
        }
    }.pointerInput(isEnabled) {
        if (!isEnabled) return@pointerInput
        detectDragGestures { change, _ ->
            val newValue = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
            onValueChange(min + newValue * (max - min))
        }
    }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val thumbRadius = 6.dp.toPx()
            drawLine(color = trackColor, start = Offset(centerX, 0f), end = Offset(centerX, size.height), strokeWidth = 1.dp.toPx())
            val currentY = size.height * (1f - (value - min) / (max - min))
            if (isEnabled) drawLine(color = accentColor, start = Offset(centerX, size.height), end = Offset(centerX, currentY), strokeWidth = 1.5.dp.toPx())
            drawCircle(color = if (isEnabled) accentColor else Color.LightGray, radius = thumbRadius, center = Offset(centerX, currentY))
        }
    }
}

@Composable
fun PresetCard(presets: List<String>, selectedIndex: Int, isEnabled: Boolean, surfaceColor: Color, primaryText: Color, secondaryText: Color, accentColor: Color, dividerColor: Color, onSelect: (Int) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = surfaceColor), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 4.dp).fillMaxHeight()) {
            presets.forEachIndexed { index, name ->
                Row(modifier = Modifier.fillMaxWidth().weight(1f).pointerInput(isEnabled) { detectTapGestures { if (isEnabled) onSelect(index) } }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedIndex == index, onClick = { if (isEnabled) onSelect(index) }, enabled = isEnabled, colors = RadioButtonDefaults.colors(selectedColor = accentColor), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = name, fontSize = 15.sp, color = if (isEnabled) primaryText else secondaryText)
                }
                if (index < presets.size - 1) HorizontalDivider(modifier = Modifier.padding(start = 56.dp, end = 16.dp), thickness = 0.5.dp, color = dividerColor)
            }
        }
    }
}
