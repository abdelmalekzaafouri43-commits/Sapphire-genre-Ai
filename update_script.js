const fs = require('fs');

function updateFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    // 1. Add PptxGenJS script
    if (!content.includes('pptxgen.bundle.js')) {
        content = content.replace(
            '<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>',
            '<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>\n    <script src="https://cdn.jsdelivr.net/gh/gitbrent/pptxgenjs@3.12.0/dist/pptxgen.bundle.js"></script>'
        );
    }
    
    // 2. Change Target Audience to Select Dropdown
    const oldAudience = `<input type="text" id="ppt-audience" value="Secondary & High School Students" class="w-full px-3 py-2 text-sm rounded-lg border bg-slate-50 dark:bg-slate-900 border-slate-300 dark:border-slate-700">`;
    const newAudience = `<select id="ppt-audience" class="w-full px-3 py-2 text-sm rounded-lg border bg-slate-50 dark:bg-slate-900 border-slate-300 dark:border-slate-700 font-semibold">
                                <option value="Elementary School">Elementary School / Primaire</option>
                                <option value="Middle School">Middle School / Collège</option>
                                <option value="Secondary & High School Students" selected>High School / Lycée</option>
                                <option value="University">University / Université</option>
                                <option value="Corporate / Professional">Corporate / Professionnel</option>
                            </select>`;
    content = content.replace(oldAudience, newAudience);
    content = content.replace('<label class="block text-xs font-semibold mb-1">Target Audience</label>', '<label class="block text-xs font-semibold mb-1">Target Audience / Public Cible</label>');

    // 3. Add Export .pptx button
    const oldButtons = `<button onclick="toggleSpeakerNotes()" class="p-2 rounded-xl border hover:bg-slate-100 dark:hover:bg-slate-800 text-xs font-semibold flex items-center gap-1">`;
    const newButtons = `<button onclick="downloadPPTX()" class="p-2 rounded-xl border border-green-500/30 text-green-700 bg-green-50 hover:bg-green-100 dark:bg-green-900/30 dark:text-green-400 dark:hover:bg-green-900/50 text-xs font-semibold flex items-center gap-1 transition-all">
                                <i data-lucide="download" class="w-4 h-4"></i> .pptx
                            </button>
                            <button onclick="toggleSpeakerNotes()" class="p-2 rounded-xl border hover:bg-slate-100 dark:hover:bg-slate-800 text-xs font-semibold flex items-center gap-1">`;
    content = content.replace(oldButtons, newButtons);

    // 4. Update generatePPTDeck prompt
    const oldPrompt = `const promptText = \`Generate a presentation deck with exactly \${count} slides on the topic of: '\${topic}' targeted for '\${audience}'. The presentation MUST be written strictly in \${lang === 'fr' ? 'French (Français)' : 'English'}. Return ONLY a JSON array of slide objects. Each slide object MUST have: 'number' (number), 'tag' (string like 'OVERVIEW', 'CORE CONCEPT', etc.), 'title' (string), 'subtitle' (string), 'bullets' (array of 3 strings), and 'notes' (string of speaker notes). Do NOT include any markdown formatting like \\\`\\\`\\\`json or \\\`\\\`\\\`, just return the raw JSON array string. Ensure no trailing commas. Choose a dynamic visual background gradient representation for each slide from standard options like 'from-blue-600 to-indigo-800', 'from-purple-600 to-indigo-900', 'from-emerald-600 to-teal-900', 'from-amber-600 to-orange-800', 'from-fuchsia-600 to-pink-800' as 'bgGradient' property.\`;`;
    const newPrompt = `const promptText = \`Generate a highly detailed presentation deck with exactly \${count} slides on the topic of: '\${topic}'. Ensure the vocabulary and complexity are specifically tailored for this audience level: '\${audience}'. The presentation MUST be written strictly in \${lang === 'fr' ? 'French (Français)' : 'English'}. 
Return ONLY a JSON array of slide objects. Each slide object MUST have:
- 'number' (integer)
- 'layout' (string, MUST be one of: 'title', 'bullets', 'two-column', 'quote')
- 'tag' (short label like 'OVERVIEW', 'CORE CONCEPT')
- 'title' (string, main slide header)
- 'subtitle' (string, optional subtitle or quote author if layout is quote)
- 'bullets' (array of 3 to 5 strings, use for 'bullets' and 'two-column' layouts)
- 'imagePrompt' (string, a highly detailed prompt describing an ideal image or diagram to accompany this slide)
- 'notes' (string, detailed speaker notes and script for the presenter)
- 'bgGradient' (string, choose one: 'from-blue-600 to-indigo-800', 'from-purple-600 to-indigo-900', 'from-emerald-600 to-teal-900', 'from-amber-600 to-orange-800', 'from-fuchsia-600 to-pink-800').
Do NOT include any markdown formatting like \\\`\\\`\\\`json or \\\`\\\`\\\`, just return the raw JSON array string. Ensure valid JSON syntax with no trailing commas.\`;`;
    content = content.replace(oldPrompt, newPrompt);
    
    // 5. Update render3DSlider to handle layout & imagePrompt visually
    const oldRenderSliderCore = `                    <div class="h-full flex flex-col p-8">
                        <div class="mb-4">
                            <span class="px-2 py-1 bg-white/20 rounded text-[10px] font-bold uppercase tracking-wider">\${slide.tag}</span>
                        </div>
                        <h2 class="text-3xl font-extrabold mb-2 leading-tight">\${slide.title}</h2>
                        <h3 class="text-lg font-medium opacity-90 mb-6">\${slide.subtitle}</h3>
                        
                        <div class="flex-1 overflow-hidden flex flex-col justify-center">
                            <ul class="space-y-4">
                                \${slide.bullets.map(b => \`<li class="flex items-start gap-3 text-sm md:text-base font-medium">
                                    <span class="mt-1 flex-shrink-0 w-2 h-2 rounded-full bg-white/60"></span>
                                    <span>\${b}</span>
                                </li>\`).join('')}
                            </ul>
                        </div>
                        
                        <div class="mt-auto pt-4 flex justify-between items-center text-xs opacity-70 border-t border-white/20">
                            <span>\${topic}</span>
                            <span class="font-bold">\${slide.number}</span>
                        </div>
                    </div>`;

    const newRenderSliderCore = `                    <div class="h-full flex flex-col p-8">
                        <div class="mb-4">
                            <span class="px-2 py-1 bg-white/20 rounded text-[10px] font-bold uppercase tracking-wider">\${slide.tag || 'SLIDE'} - \${(slide.layout || 'bullets').toUpperCase()}</span>
                        </div>
                        
                        <div class="flex-1 overflow-hidden flex flex-col \${slide.layout === 'title' || slide.layout === 'quote' ? 'justify-center text-center items-center' : 'justify-start'}">
                            <h2 class="\${slide.layout === 'title' ? 'text-4xl' : slide.layout === 'quote' ? 'text-3xl italic' : 'text-3xl'} font-extrabold mb-2 leading-tight">\${slide.layout === 'quote' ? '"'+slide.title+'"' : slide.title}</h2>
                            <h3 class="\${slide.layout === 'quote' ? 'text-xl font-medium opacity-80 mb-6 mt-4' : 'text-lg font-medium opacity-90 mb-6'}">\${slide.layout === 'quote' && slide.subtitle ? '— ' + slide.subtitle : slide.subtitle || ''}</h3>
                            
                            \${(slide.layout === 'bullets' || !slide.layout) ? \`
                            <ul class="space-y-4 mt-2">
                                \${(slide.bullets || []).map(b => \`<li class="flex items-start gap-3 text-sm md:text-base font-medium text-left">
                                    <span class="mt-1 flex-shrink-0 w-2 h-2 rounded-full bg-white/60"></span>
                                    <span>\${b}</span>
                                </li>\`).join('')}
                            </ul>\` : ''}
                            
                            \${slide.layout === 'two-column' ? \`
                            <div class="grid grid-cols-2 gap-4 mt-2">
                                <ul class="space-y-3">
                                    \${(slide.bullets || []).filter((_,i) => i%2===0).map(b => \`<li class="flex items-start gap-2 text-xs md:text-sm font-medium text-left"><span class="mt-1 flex-shrink-0 w-1.5 h-1.5 rounded-full bg-white/60"></span><span>\${b}</span></li>\`).join('')}
                                </ul>
                                <ul class="space-y-3">
                                    \${(slide.bullets || []).filter((_,i) => i%2!==0).map(b => \`<li class="flex items-start gap-2 text-xs md:text-sm font-medium text-left"><span class="mt-1 flex-shrink-0 w-1.5 h-1.5 rounded-full bg-white/60"></span><span>\${b}</span></li>\`).join('')}
                                </ul>
                            </div>\` : ''}
                        </div>
                        
                        <div class="mt-auto pt-4 flex justify-between items-center text-xs opacity-70 border-t border-white/20">
                            <span>\${topic}</span>
                            <span class="font-bold">\${slide.number}</span>
                        </div>
                    </div>`;
                    
    content = content.replace(oldRenderSliderCore, newRenderSliderCore);
    
    // Update speaker notes display to include imagePrompt
    const oldNotesBox = `document.getElementById('speaker-notes-content').innerHTML = currentSlide.notes || "No notes for this slide.";`;
    const newNotesBox = `document.getElementById('speaker-notes-content').innerHTML = 
                (currentSlide.notes || "No notes for this slide.") + 
                (currentSlide.imagePrompt ? \`<div class="mt-4 pt-3 border-t border-slate-200 dark:border-slate-700"><strong class="text-purple-600 dark:text-purple-400">Suggested Visuals:</strong><br><span class="opacity-80 italic">\${currentSlide.imagePrompt}</span></div>\` : "");`;
    content = content.replace(oldNotesBox, newNotesBox);

    // 6. Add downloadPPTX function
    const downloadFunc = `
        function downloadPPTX() {
            if (!slidesData || slidesData.length === 0) {
                showToast("No presentation generated yet.", "error");
                return;
            }
            
            showToast("Generating .pptx file...");
            let pptx = new PptxGenJS();
            
            pptx.layout = 'LAYOUT_16x9';
            pptx.author = 'Sapphire AI';
            pptx.company = 'Automated Content Generator';
            const rawTopic = document.getElementById('ppt-topic').value || 'AI Presentation';
            pptx.title = rawTopic;

            slidesData.forEach(slideInfo => {
                let slide = pptx.addSlide();
                
                // Add a subtle off-white background
                slide.background = { color: "F8FAFC" };

                // Handle Different Layouts
                if (slideInfo.layout === 'title') {
                    slide.addText(slideInfo.title || "", { x:1, y:2, w:8, h:1.5, fontSize:44, bold:true, align:'center', color:'0F172A' });
                    if (slideInfo.subtitle) slide.addText(slideInfo.subtitle, { x:1, y:3.8, w:8, h:1, fontSize:24, align:'center', color:'475569' });
                } else if (slideInfo.layout === 'quote') {
                    slide.addText(\`"\${slideInfo.title || ""}"\`, { x:1, y:2, w:8, h:2, fontSize:36, italic:true, align:'center', color:'0F172A' });
                    if (slideInfo.subtitle) slide.addText(\`— \${slideInfo.subtitle}\`, { x:1, y:4.5, w:8, h:1, fontSize:20, align:'right', color:'475569' });
                } else if (slideInfo.layout === 'two-column') {
                    slide.addText(slideInfo.title || "", { x:0.5, y:0.5, w:9, h:1, fontSize:32, bold:true, color:'0F172A' });
                    if (slideInfo.subtitle) slide.addText(slideInfo.subtitle, { x:0.5, y:1.3, w:9, h:0.5, fontSize:18, color:'64748B' });
                    
                    let col1 = [];
                    let col2 = [];
                    (slideInfo.bullets || []).forEach((b, idx) => {
                        if (idx % 2 === 0) col1.push({ text: b, options: { bullet:true }});
                        else col2.push({ text: b, options: { bullet:true }});
                    });
                    
                    if (col1.length > 0) slide.addText(col1, { x:0.5, y:2.2, w:4.2, h:4, fontSize:18, color:'334155', align:'left', valign:'top' });
                    if (col2.length > 0) slide.addText(col2, { x:5.2, y:2.2, w:4.2, h:4, fontSize:18, color:'334155', align:'left', valign:'top' });
                } else {
                    // Default to bullets layout
                    slide.addText(slideInfo.title || "", { x:0.5, y:0.5, w:9, h:1, fontSize:32, bold:true, color:'0F172A' });
                    if (slideInfo.subtitle) slide.addText(slideInfo.subtitle, { x:0.5, y:1.3, w:9, h:0.5, fontSize:18, color:'64748B' });
                    
                    let formattedBullets = (slideInfo.bullets || []).map(b => ({ text: b, options: { bullet:true } }));
                    if (formattedBullets.length > 0) slide.addText(formattedBullets, { x:0.5, y:2.2, w:8.5, h:4, fontSize:20, color:'334155', align:'left', valign:'top' });
                }

                // Add Speaker Notes
                if (slideInfo.notes || slideInfo.imagePrompt) {
                    slide.addNotes((slideInfo.notes || "") + (slideInfo.imagePrompt ? \`\\n\\n[Suggested Visuals]: \${slideInfo.imagePrompt}\` : ""));
                }
            });

            const fileName = rawTopic.substring(0, 30).replace(/[^a-zA-Z0-9 ]/g, "") + ".pptx";
            pptx.writeFile({ fileName: fileName }).then(() => {
                showToast("PPTX Download Complete!", "success");
            });
        }
`;
    
    if (!content.includes('function downloadPPTX()')) {
        content = content.replace('async function generatePPTDeck() {', downloadFunc + '\n        async function generatePPTDeck() {');
    }

    fs.writeFileSync(filePath, content, 'utf-8');
    console.log(`Updated ${filePath}`);
}

updateFile('./app/src/main/assets/index.html');
updateFile('./index.html');
updateFile('./public/index.html');
