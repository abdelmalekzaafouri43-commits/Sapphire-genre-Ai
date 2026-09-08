const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // We need to completely remove the absolute positioning 'fixed inset-y-0 left-0' from the sidebar
    // so it naturally flows in the flexbox next to the main content.
    content = content.replace(
        'class="fixed inset-y-0 left-0 w-72 static glass-card',
        'class="w-72 glass-card'
    );
    
    // Remove the hamburger menu from top bar
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-2 rounded-xl hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-300 active:scale-95 transition-transform" aria-label="Toggle Navigation Sidebar">',
        '<!-- hamburger hidden -->'
    );

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Updated ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
