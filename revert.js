const fs = require('fs');

function revertLayout(filePath) {
    if (!fs.existsSync(filePath)) return;
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // 1. Sidebar sizing and visibility (restore to lg:)
    content = content.replace(
        'class="fixed inset-y-0 left-0 w-64 sm:static sm:w-64 flex-shrink-0 glass-card border-r flex flex-col justify-between p-4 z-30 transform -translate-x-full sm:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar"',
        'class="fixed inset-y-0 left-0 w-72 lg:static lg:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full lg:translate-x-0 transition-all duration-300"'
    );
    // Also catch any other variants that might have lingered
    content = content.replace(
        'class="fixed inset-y-0 left-0 w-72 md:static md:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full md:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar"',
        'class="fixed inset-y-0 left-0 w-72 lg:static lg:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full lg:translate-x-0 transition-all duration-300"'
    );

    // 2. Hamburger buttons (restore to lg:hidden)
    content = content.replace(
        '<button onclick="toggleSidebar()" class="sm:hidden p-2 rounded-xl',
        '<button onclick="toggleSidebar()" class="lg:hidden p-2 rounded-xl'
    );
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-2 rounded-xl',
        '<button onclick="toggleSidebar()" class="lg:hidden p-2 rounded-xl'
    );
    
    content = content.replace(
        '<button onclick="toggleSidebar()" class="sm:hidden p-1.5 rounded-lg',
        '<button onclick="toggleSidebar()" class="lg:hidden p-1.5 rounded-lg'
    );
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-1.5 rounded-lg',
        '<button onclick="toggleSidebar()" class="lg:hidden p-1.5 rounded-lg'
    );

    // 3. Switch tab logic (restore to 1024)
    content = content.replace(
        'if (window.innerWidth < 640) {',
        'if (window.innerWidth < 1024) {'
    );
    content = content.replace(
        'if (window.innerWidth < 768) {',
        'if (window.innerWidth < 1024) {'
    );

    // 4. Remove compact API key box from sidebar
    const compactApiBoxStart = content.indexOf('<!-- API Key Input -->');
    if (compactApiBoxStart !== -1) {
        const compactApiBoxEnd = content.indexOf('<!-- Storage Meter -->');
        if (compactApiBoxEnd !== -1) {
            content = content.substring(0, compactApiBoxStart) + content.substring(compactApiBoxEnd);
        }
    }

    // 5. Inject large API key box after THEME SELECTOR CARD
    const bigApiBoxCode = `
            <!-- API KEY BOX & TESTER -->
            <section class="glass-card rounded-2xl p-5 space-y-4">
                <div class="flex items-center justify-between flex-wrap gap-2">
                    <div class="flex items-center gap-3">
                        <div class="w-9 h-9 rounded-xl bg-blue-600/10 text-blue-600 flex items-center justify-center">
                            <i data-lucide="key" class="w-5 h-5"></i>
                        </div>
                        <div>
                            <h3 class="font-bold text-base">Gemini API Key Settings</h3>
                            <p class="text-xs text-slate-500">Auto-detected from Secrets or enter custom key</p>
                        </div>
                    </div>
                    <span id="api-status-badge" class="px-2.5 py-1 rounded-md text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-950/60 dark:text-amber-300 flex items-center gap-1">
                        <i data-lucide="shield-alert" class="w-3 h-3"></i> Checking Key...
                    </span>
                </div>

                <div class="space-y-3">
                    <div class="relative">
                        <input type="password" id="api-key-input" oninput="onApiKeyInputChanged()" placeholder="Paste Google Gemini Key (e.g. AIzaSy...)" 
                            class="w-full px-4 py-3 pl-10 pr-20 text-sm rounded-xl border bg-slate-50 dark:bg-slate-900 border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono">
                        <i data-lucide="key" class="w-4 h-4 absolute left-3 top-3.5 text-slate-400"></i>
                        <div class="absolute right-3 top-2.5 flex items-center gap-1.5">
                            <button onclick="toggleKeyVisibility()" class="p-1 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200" title="Toggle visibility">
                                <i data-lucide="eye" id="eye-icon" class="w-4 h-4"></i>
                            </button>
                            <button onclick="clearSavedApiKey()" class="p-1 text-slate-400 hover:text-rose-600" title="Clear key">
                                <i data-lucide="trash-2" class="w-4 h-4"></i>
                            </button>
                        </div>
                    </div>
                    
                    <div class="flex gap-2 items-center">
                        <button onclick="testApiKey()" class="flex-1 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold shadow-md shadow-blue-500/20 flex items-center justify-center gap-2 transition-all">
                            <i data-lucide="zap" class="w-4 h-4"></i> Test Connection
                        </button>
                        <button onclick="saveApiKeyManual()" class="px-4 py-2.5 rounded-xl bg-slate-200 hover:bg-slate-300 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 font-bold transition-all">
                            Save
                        </button>
                    </div>

                    <div id="api-key-result" class="hidden p-3 rounded-xl text-xs font-semibold"></div>
                </div>
            </section>
`;

    if (content.indexOf('<!-- API KEY BOX & TESTER -->') === -1) {
        const insertTarget = '</section>';
        const themeSectionEnd = content.indexOf(insertTarget, content.indexOf('<!-- THEME SELECTOR CARD -->'));
        if (themeSectionEnd !== -1) {
            content = content.substring(0, themeSectionEnd + insertTarget.length) + '\n' + bigApiBoxCode + content.substring(themeSectionEnd + insertTarget.length);
        }
    }

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Reverted layout in ${filePath}`);
}

revertLayout('./app/src/main/assets/index.html');
revertLayout('./index.html');
revertLayout('./public/index.html');
