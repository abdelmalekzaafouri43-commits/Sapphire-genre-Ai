const fs = require('fs');

function fixFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // 1. Make sidebar visible on 'md' instead of 'lg'
    content = content.replace(
        'w-72 lg:static lg:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full lg:translate-x-0',
        'w-72 md:static md:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full md:translate-x-0'
    );
    
    // Header mobile close button (inside sidebar)
    content = content.replace(
        '<button onclick="toggleSidebar()" class="lg:hidden p-1.5',
        '<button onclick="toggleSidebar()" class="md:hidden p-1.5'
    );
    
    // Top bar hamburger button
    content = content.replace(
        '<button onclick="toggleSidebar()" class="p-2 rounded-xl hover:bg-slate-200/50',
        '<button onclick="toggleSidebar()" class="md:hidden p-2 rounded-xl hover:bg-slate-200/50'
    );

    // 2. Move API Key Box from main-content to the left sidebar
    // We will extract the big API key box from the dashboard and insert a compact one in the sidebar.
    // Let's remove the big one first.
    
    // Locate the start of API KEY BOX in main content
    const apiBoxStart = content.indexOf('<!-- API KEY BOX & TESTER -->');
    if (apiBoxStart !== -1) {
        // Find the end of this section (it's a <section> tag)
        const nextSectionStart = content.indexOf('<!-- API Chat Assistant -->', apiBoxStart);
        if (nextSectionStart !== -1) {
            content = content.substring(0, apiBoxStart) + content.substring(nextSectionStart);
        }
    }
    
    // Now inject a compact API Key box in the sidebar, right before <!-- Storage Meter -->
    const compactApiBox = `
            <!-- API Key Input -->
            <div class="p-3 rounded-xl bg-slate-100/70 dark:bg-slate-800/50 text-xs space-y-2">
                <div class="flex justify-between items-center font-semibold mb-1">
                    <span class="flex items-center gap-1"><i data-lucide="key" class="w-3.5 h-3.5 text-blue-600"></i> API Key</span>
                    <span id="api-status-badge" class="text-[9px] px-1.5 py-0.5 rounded bg-amber-200/50 text-amber-700 dark:text-amber-300">Checking...</span>
                </div>
                <div class="relative">
                    <input type="password" id="api-key-input" oninput="onApiKeyInputChanged()" placeholder="Paste Gemini API Key..." 
                        class="w-full px-2 py-1.5 pr-6 text-[10px] rounded border bg-white dark:bg-slate-900 border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-1 focus:ring-blue-500 font-mono">
                    <button onclick="toggleKeyVisibility()" class="absolute right-1.5 top-1.5 text-slate-400 hover:text-slate-600" title="Toggle visibility">
                        <i data-lucide="eye" id="eye-icon" class="w-3 h-3"></i>
                    </button>
                </div>
                <button onclick="testGeminiConnection()" class="w-full py-1.5 rounded bg-blue-600 hover:bg-blue-700 text-white text-[10px] font-bold shadow-sm flex items-center justify-center gap-1">
                    <i data-lucide="zap" class="w-3 h-3"></i> Test Key
                </button>
            </div>
            `;
            
    if (!content.includes('<!-- API Key Input -->')) {
        content = content.replace('<!-- Storage Meter -->', compactApiBox + '\n            <!-- Storage Meter -->');
    }

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log('Fixed layout in ' + filePath);
}

fixFile('./app/src/main/assets/index.html');
fixFile('./index.html');
fixFile('./public/index.html');
