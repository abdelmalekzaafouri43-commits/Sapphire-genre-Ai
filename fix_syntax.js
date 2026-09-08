const fs = require('fs');

function fix(filePath) {
    if (!fs.existsSync(filePath)) return;
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // The syntax error is at: ${(layout === 'bullets' || !layout) ? \`
    // We just need to replace \` with ` inside those template literal interpolations
    // Specifically around the PDF generation code
    
    content = content.replace(
        "${(layout === 'bullets' || !layout) ? \\`",
        "${(layout === 'bullets' || !layout) ? `"
    );
    
    content = content.replace(
        "</ul>\\` : ''}",
        "</ul>` : ''}"
    );
    
    content = content.replace(
        "${(slide.bullets || []).map(b => \\`<li class=\"flex items-start gap-4 text-2xl font-medium text-left\">",
        "${(slide.bullets || []).map(b => `<li class=\"flex items-start gap-4 text-2xl font-medium text-left\">"
    );
    
    content = content.replace(
        "</li>\\`).join('')}",
        "</li>`).join('')}"
    );

    // Let's do a regex replace to catch any stray \\` inside ${} if they exist, but the direct replace is safer
    
    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Fixed syntax in ${filePath}`);
}

fix('./app/src/main/assets/index.html');
fix('./index.html');
fix('./public/index.html');
