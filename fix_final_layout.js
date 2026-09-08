const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // 1. Change sidebar width from w-72 to w-64, and breakpoints from md to sm
    content = content.replace(
        'class="fixed inset-y-0 left-0 w-72 md:static md:w-72 glass-card border-r flex flex-col justify-between p-5 z-30 transform -translate-x-full md:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar"',
        'class="fixed inset-y-0 left-0 w-64 sm:static sm:w-64 flex-shrink-0 glass-card border-r flex flex-col justify-between p-4 z-30 transform -translate-x-full sm:translate-x-0 transition-all duration-300 overflow-y-auto custom-scrollbar"'
    );
    
    // 2. Change hamburger button breakpoint
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-2 rounded-xl',
        '<button onclick="toggleSidebar()" class="sm:hidden p-2 rounded-xl'
    );
    
    // 3. Change internal close button breakpoint
    content = content.replace(
        '<button onclick="toggleSidebar()" class="md:hidden p-1.5 rounded-lg',
        '<button onclick="toggleSidebar()" class="sm:hidden p-1.5 rounded-lg'
    );
    
    // 4. Fix the switchTab auto-collapse logic to match sm breakpoint (640px)
    content = content.replace(
        'if (window.innerWidth < 768) {',
        'if (window.innerWidth < 640) {'
    );

    // 5. Ensure the main container has min-w-0 to prevent flex blowout
    content = content.replace(
        '<main class="flex-1 flex flex-col h-screen overflow-hidden">',
        '<main class="flex-1 flex flex-col h-screen overflow-hidden min-w-0">'
    );

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Updated ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
