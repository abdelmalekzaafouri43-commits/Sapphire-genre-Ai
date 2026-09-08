const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // The issue is likely that removing 'fixed inset-y-0 left-0' but keeping 'transform translate-x-0 transition-all duration-300' 
    // without flex constraints is causing the sidebar to either squish or break out of bounds.
    // Let's lock the width strictly using flex shrink constraints.

    content = content.replace(
        '<aside id="sidebar-container" class="w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform translate-x-0 transition-all duration-300">',
        '<aside id="sidebar-container" class="w-72 min-w-[288px] max-w-[288px] flex-shrink-0 h-full glass-card border-r flex flex-col justify-between p-5 z-30 overflow-y-auto custom-scrollbar">'
    );
    
    // Remove the hamburger menu from top bar, let's make sure it's completely gone to avoid breaking layout
    content = content.replace(
        '<!-- hamburger hidden -->',
        ''
    );
    
    content = content.replace(
        '<button onclick="toggleSidebar()" class="hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">',
        ''
    );
    
    // Remove the inner close button just in case
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-1.5 rounded-lg hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-500 active:scale-95 transition-transform">\n                    <i data-lucide="chevron-left" class="w-5 h-5"></i>\n                </button>',
        ''
    );

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Updated ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
