const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // Restore sidebar classes
    content = content.replace(
        '<aside id="sidebar-container" class="w-72 min-w-[288px] max-w-[288px] flex-shrink-0 h-full glass-card border-r flex flex-col justify-between p-5 z-30 overflow-y-auto custom-scrollbar">',
        '<aside id="sidebar-container" class="fixed inset-y-0 left-0 w-72 lg:static lg:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full lg:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar">'
    );
    
    // Restore close button inside sidebar
    // Currently lines 208-211 look like:
    //                 </div>
    //                 <!-- Close menu button for mobile inside sidebar -->
    //                 
    //             </div>
    content = content.replace(
        '<!-- Close menu button for mobile inside sidebar -->\n                \n            </div>',
        `<!-- Close menu button for mobile inside sidebar -->
                <button onclick="toggleSidebar()" class="lg:hidden p-1.5 rounded-lg hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-500 active:scale-95 transition-transform">
                    <i data-lucide="chevron-left" class="w-5 h-5"></i>
                </button>
            </div>`
    );

    // Restore hamburger menu in top bar
    // Currently lines 292-298 look like:
    //         <header class="h-16 border-b glass-card flex items-center justify-between px-6 z-10">
    //             <div class="flex items-center gap-3">
    //                 
    //                     <i data-lucide="menu" class="w-5 h-5"></i>
    //                 </button>
    //                 <h2 id="page-title" class="font-bold text-lg tracking-tight">Dashboard</h2>
    //             </div>
    content = content.replace(
        `<header class="h-16 border-b glass-card flex items-center justify-between px-6 z-10">\n            <div class="flex items-center gap-3">\n                \n                    <i data-lucide="menu" class="w-5 h-5"></i>\n                </button>\n                <h2 id="page-title" class="font-bold text-lg tracking-tight">Dashboard</h2>\n            </div>`,
        `<header class="h-16 border-b glass-card flex items-center justify-between px-6 z-10">
            <div class="flex items-center gap-3">
                <button onclick="toggleSidebar()" class="lg:hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">
                    <i data-lucide="menu" class="w-5 h-5"></i>
                </button>
                <h2 id="page-title" class="font-bold text-lg tracking-tight">Dashboard</h2>
            </div>`
    );

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Restored ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
