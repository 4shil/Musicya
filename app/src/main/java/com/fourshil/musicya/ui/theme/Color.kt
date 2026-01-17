package com.fourshil.musicya.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
// Neo-Brutalism Professional Color Palette
// Soft, cool colors with small shadows for a clean 60fps experience
// ═══════════════════════════════════════════════════════════════════════════════

// Base Colors
val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFFAFAFA)
val OffBlack = Color(0xFF0F172A)  // Slate-900

// ─────────────────────────────────────────────────────────────────────────────
// Primary Accent Colors - Soft & Professional
// ─────────────────────────────────────────────────────────────────────────────
val NeoCoral = Color(0xFFF97066)       // Primary accent - soft coral
val NeoCoralDark = Color(0xFFDC2626)   // Deeper coral for emphasis
val NeoCoralLight = Color(0xFFFDA4AF)  // Light coral for dark mode

val NeoAmber = Color(0xFFFCD34D)       // Secondary accent - warm amber
val NeoAmberDark = Color(0xFFF59E0B)   // Deeper amber
val NeoAmberLight = Color(0xFFFDE68A)  // Light amber

// ─────────────────────────────────────────────────────────────────────────────
// Cool Accent Colors - For variety and states
// ─────────────────────────────────────────────────────────────────────────────
val NeoTeal = Color(0xFF5EEAD4)        // Success/positive states
val NeoTealDark = Color(0xFF14B8A6)    // Darker teal
val NeoSage = Color(0xFF86EFAC)        // Fresh green accent
val NeoLavender = Color(0xFFC4B5FD)    // Soft purple accent
val NeoSky = Color(0xFF7DD3FC)         // Light blue accent

// ─────────────────────────────────────────────────────────────────────────────
// Slate Scale - Warmer grays for Neo-Brutalism depth
// ─────────────────────────────────────────────────────────────────────────────
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)
val Slate950 = Color(0xFF020617)

// ─────────────────────────────────────────────────────────────────────────────
// Shadow Colors - For Neo-Brutalism depth (small, clean shadows)
// ─────────────────────────────────────────────────────────────────────────────
val NeoShadowLight = Slate700          // Shadow color for light mode
val NeoShadowDark = Slate950           // Shadow color for dark mode
val NeoBorderLight = Slate700          // Border color for light mode
val NeoBorderDark = Slate400           // Border color for dark mode

// ─────────────────────────────────────────────────────────────────────────────
// Semantic Aliases
// ─────────────────────────────────────────────────────────────────────────────
val LightBackground = Slate50
val DarkBackground = Slate900
val LightSurface = PureWhite
val DarkSurface = Slate800
val PrimaryText = Slate900
val DarkPrimaryText = Slate50

// ─────────────────────────────────────────────────────────────────────────────
// Legacy Aliases (for backward compatibility during migration)
// ─────────────────────────────────────────────────────────────────────────────
val MangaRed = NeoCoral
val MangaYellow = NeoAmber
val MangaDarkRed = NeoCoralDark
val MustardYellow = NeoAmberDark
val AccentRed = NeoCoral

// Zinc → Slate migration aliases
val Zinc50 = Slate50
val Zinc100 = Slate100
val Zinc200 = Slate200
val Zinc300 = Slate300
val Zinc700 = Slate700
val Zinc800 = Slate800
val Zinc900 = Slate900
val Zinc950 = Slate950
