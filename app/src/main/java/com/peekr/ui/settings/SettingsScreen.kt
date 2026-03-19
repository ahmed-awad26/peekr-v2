package com.peekr.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.peekr.ui.Screen
import com.peekr.ui.feed.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val isDark by settingsViewModel.isDarkMode.collectAsState()
    val useSystem by settingsViewModel.useSystemTheme.collectAsState()
    val isEnglish by settingsViewModel.isEnglish.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEnglish) "Settings" else "الإعدادات") },
                actions = {
                    // زرار Refresh في الـ TopAppBar
                    IconButton(onClick = { feedViewModel.syncAll() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث الفيد")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ==============================
            // قسم الحسابات والمحتوى
            // ==============================
            SectionHeader(if (isEnglish) "Content" else "المحتوى")

            SettingsItem(
                icon = Icons.Default.AccountCircle,
                title = if (isEnglish) "Connected Accounts" else "ربط الحسابات",
                subtitle = if (isEnglish) "Telegram, YouTube, WhatsApp, Facebook, RSS"
                           else "تليجرام، يوتيوب، واتساب، فيسبوك، RSS",
                onClick = { navController.navigate(Screen.Accounts.route) }
            )
            SettingsItem(
                icon = Icons.Default.Key,
                title = if (isEnglish) "API Keys" else "مفاتيح API",
                subtitle = if (isEnglish) "Manage platform API keys" else "إدارة مفاتيح المنصات المختلفة",
                onClick = { navController.navigate(Screen.ApiKeys.route) }
            )

            Spacer(Modifier.height(4.dp))

            // ==============================
            // قسم المظهر
            // ==============================
            SectionHeader(if (isEnglish) "Appearance" else "المظهر")

            // Dark / Light Mode
            SettingsToggleItem(
                icon = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                title = if (isEnglish) "Theme" else "المظهر",
                subtitle = when {
                    useSystem -> if (isEnglish) "Follow system" else "حسب النظام"
                    isDark    -> if (isEnglish) "Dark mode" else "الوضع الداكن"
                    else      -> if (isEnglish) "Light mode" else "الوضع الفاتح"
                },
                onClick = { showThemeDialog = true }
            )

            // اللغة
            SettingsToggleItem(
                icon = Icons.Default.Language,
                title = if (isEnglish) "Language" else "اللغة",
                subtitle = if (isEnglish) "English" else "العربية",
                onClick = { showLangDialog = true }
            )

            Spacer(Modifier.height(4.dp))

            // ==============================
            // قسم الأمان والنظام
            // ==============================
            SectionHeader(if (isEnglish) "Security & System" else "الأمان والنظام")

            SettingsItem(
                icon = Icons.Default.Lock,
                title = if (isEnglish) "Security & Privacy" else "الأمان والخصوصية",
                subtitle = if (isEnglish) "App lock, PIN, fingerprint" else "قفل التطبيق، PIN، بصمة الإصبع",
                onClick = { navController.navigate(Screen.SecuritySettings.route) }
            )
            SettingsItem(
                icon = Icons.Default.Backup,
                title = if (isEnglish) "Backup" else "النسخ الاحتياطي",
                subtitle = if (isEnglish) "Export, import, Google Drive" else "تصدير، استيراد، جوجل درايف",
                onClick = { navController.navigate(Screen.Backup.route) }
            )
            SettingsItem(
                icon = Icons.Default.Article,
                title = if (isEnglish) "Event Logs" else "سجل الأحداث",
                subtitle = if (isEnglish) "View errors and warnings" else "عرض الأخطاء والتحذيرات",
                onClick = { navController.navigate(Screen.Logs.route) }
            )
        }
    }

    // ==============================
    // Dialog اختيار المظهر
    // ==============================
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(if (isEnglish) "Choose Theme" else "اختر المظهر") },
            text = {
                Column {
                    ThemeOption(
                        label = if (isEnglish) "Follow System" else "حسب النظام",
                        selected = useSystem,
                        onClick = {
                            settingsViewModel.setUseSystemTheme(true)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        label = if (isEnglish) "Light Mode" else "الوضع الفاتح",
                        selected = !useSystem && !isDark,
                        onClick = {
                            settingsViewModel.setDarkMode(false)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        label = if (isEnglish) "Dark Mode" else "الوضع الداكن",
                        selected = !useSystem && isDark,
                        onClick = {
                            settingsViewModel.setDarkMode(true)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(if (isEnglish) "Close" else "إغلاق")
                }
            }
        )
    }

    // ==============================
    // Dialog اختيار اللغة
    // ==============================
    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text("Language / اللغة") },
            text = {
                Column {
                    ThemeOption(
                        label = "العربية",
                        selected = !isEnglish,
                        onClick = {
                            settingsViewModel.setEnglish(false)
                            showLangDialog = false
                        }
                    )
                    ThemeOption(
                        label = "English",
                        selected = isEnglish,
                        onClick = {
                            settingsViewModel.setEnglish(true)
                            showLangDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLangDialog = false }) { Text("OK / موافق") }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        if (selected) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp))
        }
    }
}
