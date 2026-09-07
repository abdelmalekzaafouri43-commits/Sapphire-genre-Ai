package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.ui.components.GlassCard
import com.example.ui.theme.SapphirePrimary

@Composable
fun MyDocumentsScreen(
    docs: List<GeneratedDoc>,
    onSelectDoc: (GeneratedDoc) -> Unit,
    onToggleFavorite: (GeneratedDoc) -> Unit,
    onDeleteDoc: (GeneratedDoc) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, WORKSHEETS, PRESENTATIONS, FAVORITES

    val filteredDocs = docs.filter { doc ->
        val matchesSearch = doc.title.contains(searchQuery, ignoreCase = true) ||
                doc.subjectOrTopic.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "WORKSHEETS" -> doc.docType == DocType.WORKSHEET
            "PRESENTATIONS" -> doc.docType == DocType.PRESENTATION
            "FAVORITES" -> doc.isFavorite
            else -> true
        }
        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Filter Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "My Generated Documents",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SapphirePrimary)
                    },
                    placeholder = { Text("Search by topic, subject, or title...") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SapphirePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "WORKSHEETS", "PRESENTATIONS", "FAVORITES").forEach { filterTag ->
                        val isSelected = selectedFilter == filterTag
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SapphirePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedFilter = filterTag }
                        ) {
                            Text(
                                text = filterTag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // List
        if (filteredDocs.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    padding = 24.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = SapphirePrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No documents found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Try adjusting your search or filters, or generate new content.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredDocs) { doc ->
                DocHistoryItem(
                    doc = doc,
                    onSelect = { onSelectDoc(doc) },
                    onToggleFav = { onToggleFavorite(doc) },
                    onDelete = { onDeleteDoc(doc) }
                )
            }
        }
    }
}
