const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // 1. Add the PDF button
    const searchStr = `<button onclick="downloadPPTX()"`;
    
    if (content.includes(searchStr) && !content.includes('downloadDeckPDF()')) {
        const pdfButton = `<button onclick="downloadDeckPDF()" class="p-2 rounded-xl border border-red-500/30 text-red-700 bg-red-50 hover:bg-red-100 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-900/50 text-xs font-semibold flex items-center gap-1 transition-all">
                                <i data-lucide="file-text" class="w-4 h-4"></i> .pdf
                            </button>\n                            `;
        content = content.replace(searchStr, pdfButton + searchStr);
    }

    // 2. Add downloadDeckPDF() function
    const funcDefinition = `
        function downloadDeckPDF() {
            if (!slidesData || slidesData.length === 0) {
                showToast("No presentation generated yet.", "error");
                return;
            }

            showToast("Generating A4 Landscape PDF...");

            const container = document.createElement('div');
            container.id = 'pdf-export-wrapper';
            container.style.position = 'absolute';
            container.style.left = '-9999px';
            container.style.top = '0';
            container.style.width = '1123px';
            container.style.zIndex = '-1';

            let htmlContent = '';
            const presentationTopic = document.getElementById('ppt-topic').value || 'Presentation';

            slidesData.forEach((slide, index) => {
                const isLast = index === slidesData.length - 1;
                const pageBreak = isLast ? '' : 'page-break-after: always;';

                const layout = slide.layout || 'bullets';
                const bgClass = slide.bgGradient || 'from-slate-800 to-slate-900';

                htmlContent += \`
                <div style="width: 1123px; height: 794px; \${pageBreak} position: relative; overflow: hidden; box-sizing: border-box;" class="bg-gradient-to-br \${bgClass} text-white font-sans">
                    <div class="h-full flex flex-col p-16 box-border">
                        <div class="mb-8">
                            <span class="px-3 py-1 bg-white/20 rounded text-sm font-bold uppercase tracking-wider">\${slide.tag || 'SLIDE'} - \${layout.toUpperCase()}</span>
                        </div>

                        <div class="flex-1 overflow-hidden flex flex-col \${layout === 'title' || layout === 'quote' ? 'justify-center text-center items-center' : 'justify-start'}">
                            <h2 class="\${layout === 'title' ? 'text-6xl' : layout === 'quote' ? 'text-5xl italic' : 'text-5xl'} font-extrabold mb-4 leading-tight">\${layout === 'quote' ? '"'+(slide.title||'')+'"' : (slide.title||'')}</h2>
                            <h3 class="\${layout === 'quote' ? 'text-3xl font-medium opacity-80 mb-8 mt-6' : 'text-2xl font-medium opacity-90 mb-8'}">\${layout === 'quote' && slide.subtitle ? '— ' + slide.subtitle : slide.subtitle || ''}</h3>

                            \${(layout === 'bullets' || !layout) ? \\\`
                            <ul class="space-y-6 mt-4">
                                \${(slide.bullets || []).map(b => \\\`<li class="flex items-start gap-4 text-2xl font-medium text-left">
                                    <span class="mt-2 flex-shrink-0 w-3 h-3 rounded-full bg-white/60"></span>
                                    <span>\${b}</span>
                                </li>\\\`).join('')}
                            </ul>\\\` : ''}

                            \${layout === 'two-column' ? \\\`
                            <div class="grid grid-cols-2 gap-8 mt-4 w-full">
                                <ul class="space-y-6">
                                    \${(slide.bullets || []).filter((_,i) => i%2===0).map(b => \\\`<li class="flex items-start gap-4 text-xl font-medium text-left"><span class="mt-2 flex-shrink-0 w-2 h-2 rounded-full bg-white/60"></span><span>\${b}</span></li>\\\`).join('')}
                                </ul>
                                <ul class="space-y-6">
                                    \${(slide.bullets || []).filter((_,i) => i%2!==0).map(b => \\\`<li class="flex items-start gap-4 text-xl font-medium text-left"><span class="mt-2 flex-shrink-0 w-2 h-2 rounded-full bg-white/60"></span><span>\${b}</span></li>\\\`).join('')}
                                </ul>
                            </div>\\\` : ''}
                        </div>

                        <div class="mt-auto pt-6 flex justify-between items-center text-lg opacity-70 border-t border-white/20">
                            <span>\${presentationTopic}</span>
                            <span class="font-bold">\${slide.number || (index+1)}</span>
                        </div>
                    </div>
                </div>
                \`;
            });

            container.innerHTML = htmlContent;
            document.body.appendChild(container);

            const opt = {
                margin:       0,
                filename:     presentationTopic.substring(0, 30).replace(/[^a-zA-Z0-9 ]/g, "") + '.pdf',
                image:        { type: 'jpeg', quality: 0.98 },
                html2canvas:  { scale: 2, useCORS: true, windowWidth: 1123, logging: false },
                jsPDF:        { unit: 'pt', format: 'a4', orientation: 'landscape' }
            };

            html2pdf().set(opt).from(container).save().then(() => {
                document.body.removeChild(container);
                showToast("PDF Download Complete!", "success");
            }).catch(err => {
                console.error(err);
                if(document.body.contains(container)) document.body.removeChild(container);
                showToast("Error generating PDF.", "error");
            });
        }
`;

    if (!content.includes('function downloadDeckPDF()')) {
        content = content.replace('function downloadPPTX() {', funcDefinition + '\n        function downloadPPTX() {');
    }
    
    // Add lucide icons re-render to inject the icon on the new button
    content = content.replace('content = content.replace(searchStr, pdfButton + searchStr);', 'content = content.replace(searchStr, pdfButton + searchStr);\n        setTimeout(() => lucide.createIcons(), 100); // refresh icons');
    
    // Update the icon refresh in actual script
    const initIcons = "lucide.createIcons();";
    if (content.includes("lucide.createIcons();") && !content.includes("setInterval(() => lucide.createIcons(), 2000)")) {
         content = content.replace("lucide.createIcons();", "lucide.createIcons();\n        // Periodically refresh icons to catch dynamic additions like PDF/PPTX buttons\n        setInterval(() => lucide.createIcons(), 2000);");
    }

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log('Updated ' + filePath);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
