package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.DocPreviewDialog
import com.example.ui.components.NavDestination
import com.example.ui.components.SidebarContent
import com.example.ui.components.TopBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MyDocumentsScreen
import com.example.ui.screens.PptGeneratorScreen
import com.example.ui.screens.WorksheetGeneratorScreen
import com.example.ui.theme.SapphireAITheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentTheme by viewModel.currentTheme.collectAsState()

            SapphireAITheme(
                appTheme = currentTheme,
                darkTheme = isDarkMode
            ) {
                SapphireAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SapphireAppContent(viewModel: MainViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val docs by viewModel.docs.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    val selectedDocForPreview by viewModel.selectedDocForPreview.collectAsState()
    val selectedWorksheetData by viewModel.selectedWorksheetData.collectAsState()
    val selectedPresentationData by viewModel.selectedPresentationData.collectAsState()

    val lastGeneratedDoc by viewModel.lastGeneratedDoc.collectAsState()
    val lastWorksheetData by viewModel.lastWorksheetData.collectAsState()
    val lastPresentationData by viewModel.lastPresentationData.collectAsState()

    val currentTheme by viewModel.currentTheme.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val isTestingKey by viewModel.isTestingKey.collectAsState()
    val apiKeyTestResult by viewModel.apiKeyTestResult.collectAsState()
    val isKeyValid by viewModel.isKeyValid.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 720.dp

        if (isExpandedScreen) {
            // Desktop / Wide Screen Layout: Left Sidebar fixed on left
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Fixed Left Sidebar
                SidebarContent(
                    currentDestination = currentDestination,
                    onNavigate = { dest ->
                        viewModel.navigateTo(dest)
                    },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                    currentTheme = currentTheme
                )

                // Main Content Workspace Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TopBar(
                        title = currentDestination.label,
                        showMenuButton = false,
                        onMenuClick = {},
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode(!isDarkMode) }
                    )

                    MainScreenWorkspace(
                        currentDestination = currentDestination,
                        viewModel = viewModel,
                        docs = docs,
                        isGenerating = isGenerating,
                        lastGeneratedDoc = lastGeneratedDoc,
                        lastWorksheetData = lastWorksheetData,
                        lastPresentationData = lastPresentationData
                    )
                }
            }
        } else {
            // Mobile Layout: Collapsible Navigation Drawer on left
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.width(280.dp)
                    ) {
                        SidebarContent(
                            currentDestination = currentDestination,
                            onNavigate = { dest ->
                                viewModel.navigateTo(dest)
                                coroutineScope.launch { drawerState.close() }
                            },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                            currentTheme = currentTheme
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopBar(
                            title = currentDestination.label,
                            showMenuButton = true,
                            onMenuClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode(!isDarkMode) }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MainScreenWorkspace(
                            currentDestination = currentDestination,
                            viewModel = viewModel,
                            docs = docs,
                            isGenerating = isGenerating,
                            lastGeneratedDoc = lastGeneratedDoc,
                            lastWorksheetData = lastWorksheetData,
                            lastPresentationData = lastPresentationData
                        )
                    }
                }
            }
        }

        // Fullscreen Modal Preview for Generated Documents
        selectedDocForPreview?.let { doc ->
            DocPreviewDialog(
                doc = doc,
                worksheetData = selectedWorksheetData,
                presentationData = selectedPresentationData,
                onDismiss = { viewModel.closeDocPreview() }
            )
        }
    }
}

@Composable
private fun MainScreenWorkspace(
    currentDestination: NavDestination,
    viewModel: MainViewModel,
    docs: List<com.example.data.model.GeneratedDoc>,
    isGenerating: Boolean,
    lastGeneratedDoc: com.example.data.model.GeneratedDoc?,
    lastWorksheetData: com.example.data.model.WorksheetData?,
    lastPresentationData: com.example.data.model.PresentationData?
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val isTestingKey by viewModel.isTestingKey.collectAsState()
    val apiKeyTestResult by viewModel.apiKeyTestResult.collectAsState()
    val isKeyValid by viewModel.isKeyValid.collectAsState()

    Crossfade(targetState = currentDestination, label = "ScreenTransition") { destination ->
        when (destination) {
            NavDestination.DASHBOARD -> DashboardScreen(
                recentDocs = docs,
                apiKey = apiKey,
                isTestingKey = isTestingKey,
                apiKeyTestResult = apiKeyTestResult,
                isKeyValid = isKeyValid,
                onApiKeyChange = { key -> viewModel.updateApiKey(key) },
                onTestKey = { key -> viewModel.testApiKey(key) },
                currentTheme = currentTheme,
                onSelectTheme = { theme -> viewModel.selectTheme(theme) },
                onNavigate = { dest -> viewModel.navigateTo(dest) },
                onSelectDoc = { doc -> viewModel.openDocPreview(doc) },
                onToggleFavorite = { doc -> viewModel.toggleFavorite(doc) },
                onDeleteDoc = { doc -> viewModel.deleteDoc(doc) }
            )

            NavDestination.WORKSHEET -> WorksheetGeneratorScreen(
                isGenerating = isGenerating,
                lastGeneratedDoc = lastGeneratedDoc,
                lastWorksheetData = lastWorksheetData,
                onGenerateWorksheet = { subject, grade, topic, types, count, diff ->
                    viewModel.generateWorksheet(subject, grade, topic, types, count, diff)
                },
                onOpenPreview = { doc -> viewModel.openDocPreview(doc) }
            )

            NavDestination.PPT -> PptGeneratorScreen(
                isGenerating = isGenerating,
                lastGeneratedDoc = lastGeneratedDoc,
                lastPresentationData = lastPresentationData,
                onGeneratePresentation = { topic, audience, count, style, tone ->
                    viewModel.generatePresentation(topic, audience, count, style, tone)
                },
                onOpenPreview = { doc -> viewModel.openDocPreview(doc) }
            )

            NavDestination.DOCUMENTS -> MyDocumentsScreen(
                docs = docs,
                onSelectDoc = { doc -> viewModel.openDocPreview(doc) },
                onToggleFavorite = { doc -> viewModel.toggleFavorite(doc) },
                onDeleteDoc = { doc -> viewModel.deleteDoc(doc) }
            )

            NavDestination.ANALYTICS -> AnalyticsScreen(
                docs = docs,
                apiKey = apiKey,
                isTestingKey = isTestingKey,
                apiKeyTestResult = apiKeyTestResult,
                isKeyValid = isKeyValid,
                onApiKeyChange = { key -> viewModel.updateApiKey(key) },
                onTestKey = { key -> viewModel.testApiKey(key) },
                currentTheme = currentTheme,
                onSelectTheme = { theme -> viewModel.selectTheme(theme) }
            )
        }
    }
}
