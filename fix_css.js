const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // We need to force the sidebar to show up on the preview window width.
    // The previous fix used 'md:static' but the preview window might be smaller than the 'md' breakpoint in Tailwind.
    // Let's completely remove the responsive hidden classes from the sidebar to force it to always be visible,
    // and remove the hamburger menu.

    // 1. Force sidebar to always be static and visible on ALL screen sizes in this environment
    content = content.replace(
        'w-72 md:static md:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full md:translate-x-0 transition-all duration-300',
        'w-72 static glass-card border-r flex flex-col justify-between p-5 z-30 transform translate-x-0 transition-all duration-300'
    );
    // Also catch any variant that might still be using lg:
    content = content.replace(
        'w-72 lg:static lg:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full lg:translate-x-0 transition-all duration-300',
        'w-72 static glass-card border-r flex flex-col justify-between p-5 z-30 transform translate-x-0 transition-all duration-300'
    );
    
    // 2. Hide the top-bar hamburger menu button entirely, as the sidebar is now permanently visible
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">',
        '<button onclick="toggleSidebar()" class="hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">'
    );
    content = content.replace(
        '<button onclick="toggleSidebar()" class="lg:hidden p-2 rounded-xl',
        '<button onclick="toggleSidebar()" class="hidden p-2 rounded-xl'
    );

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Updated ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
