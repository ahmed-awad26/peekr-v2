package com.peekr.ui.settings.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.peekr.data.local.entities.AccountEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoutubeLoginScreen(
    navController: NavController,
    viewModel: YoutubeViewModel = hiltViewModel()
) {
    var channelUrl by remember { mutableStateOf("") }
    var editingChannel by remember { mutableStateOf<AccountEntity?>(null) }
    var editText by remember { mutableStateOf("") }
    val channels by viewModel.channels.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ربط يوتيوب") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // أيقونة وعنوان
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = Color(0xFFFF0000).copy(alpha = 0.15f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PlayCircle, null, tint = Color(0xFFFF0000), modifier = Modifier.size(40.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("إضافة قنوات يوتيوب", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "أضف رابط القناة اللي عاوز تتابعها — التغييرات بتتحفظ تلقائياً",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // حقل الإدخال
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = channelUrl,
                        onValueChange = { channelUrl = it },
                        label = { Text("رابط أو اسم القناة") },
                        placeholder = { Text("youtube.com/c/channelname") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            viewModel.addChannel(channelUrl)
                            channelUrl = ""
                        },
                        enabled = channelUrl.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
                    ) { Text("إضافة") }
                }
            }

            // عدد القنوات
            if (channels.isNotEmpty()) {
                item {
                    Text(
                        "القنوات المضافة (${channels.size}):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // قائمة القنوات من DB
            items(channels, key = { it.id }) { channel ->
                if (editingChannel?.id == channel.id) {
                    // وضع التعديل
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editText,
                                onValueChange = { editText = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("تعديل الرابط") }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    viewModel.updateChannel(channel, editText)
                                    editingChannel = null
                                }, modifier = Modifier.weight(1f)) { Text("حفظ") }
                                OutlinedButton(onClick = { editingChannel = null }, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                            }
                        }
                    }
                } else {
                    ChannelRow(
                        url = channel.accountName,
                        onEdit = { editingChannel = channel; editText = channel.accountName },
                        onDelete = { viewModel.removeChannel(channel) }
                    )
                }
            }

            // زر رجوع لما خلصنا
            if (channels.isNotEmpty()) {
                item {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("تم ✓") }
                }
            }
        }
    }
}

@Composable
private fun ChannelRow(url: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PlayCircle, null, tint = Color(0xFFFF0000), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(url, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
