const fs = require('fs');

function fix(filePath) {
    if (!fs.existsSync(filePath)) return;
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // Fix the backticks in the layout logic
    content = content.replace(
        "${layout === 'two-column' ? \\`",
        "${layout === 'two-column' ? `"
    );
    
    content = content.replace(
        "${(slide.bullets || []).filter((_,i) => i%2===0).map(b => \\`<li class=\"flex items-start gap-4 text-xl font-medium text-left\"><span class=\"mt-2 flex-shrink-0 w-2 h-2 rounded-full bg-white/60\"></span><span>${b}</span></li>\\`).join('')}",
        "${(slide.bullets || []).filter((_,i) => i%2===0).map(b => `<li class=\"flex items-start gap-4 text-xl font-medium text-left\"><span class=\"mt-2 flex-shrink-0 w-2 h-2 rounded-full bg-white/60\"></span><span>${b}</span></li>`).join('')}"
    );

    content = content.replace(
        "${(slide.bullets || []).filter((_,i) => i%2!==0).map(b => \\`<li class=\"flex items-start gap-4 text-xl font-medium text-left\"><span class=\"mt-2 flex-shrink-0 w-2 h-2 rounded-full bg-white/60\"></span><span>${b}</span></li>\\`).join('')}",
        "${(slide.bullets || []).filter((_,i) => i%2!==0).map(b => `<li class=\"flex items-start gap-4 text-xl font-medium text-left\"><span class=\"mt-2 flex-shrink-0 w-2 h-2 rounded-full bg-white/60\"></span><span>${b}</span></li>`).join('')}"
    );

    content = content.replace(
        "</div>\\` : ''}",
        "</div>` : ''}"
    );
    
    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Fixed syntax in ${filePath}`);
}

fix('./app/src/main/assets/index.html');
fix('./index.html');
fix('./public/index.html');
