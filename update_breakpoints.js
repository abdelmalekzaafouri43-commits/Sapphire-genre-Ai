const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // Change lg to md in sidebar
    content = content.replace(
        'class="fixed inset-y-0 left-0 w-72 lg:static lg:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full lg:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar"',
        'class="fixed inset-y-0 left-0 w-72 md:static md:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full md:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar"'
    );
    
    // Change lg to md in close button
    content = content.replace(
        '<button onclick="toggleSidebar()" class="lg:hidden p-1.5 rounded-lg hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-500 active:scale-95 transition-transform">',
        '<button onclick="toggleSidebar()" class="md:hidden p-1.5 rounded-lg hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-500 active:scale-95 transition-transform">'
    );
    
    // Change lg to md in hamburger button
    content = content.replace(
        '<button onclick="toggleSidebar()" class="lg:hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">',
        '<button onclick="toggleSidebar()" class="md:hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">'
    );

    // Update JS toggle function to use md breakpoint
    content = content.replace(
        'if (window.innerWidth < 1024) {',
        'if (window.innerWidth < 768) {'
    );

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Updated breakpoints in ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
