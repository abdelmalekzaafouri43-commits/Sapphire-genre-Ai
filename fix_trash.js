const fs = require('fs');

function addTrashIcon(filePath) {
    let content = fs.readFileSync(filePath, 'utf-8');
    
    const oldHtml = `<button onclick="toggleKeyVisibility()" class="absolute right-1.5 top-1.5 text-slate-400 hover:text-slate-600" title="Toggle visibility">
                        <i data-lucide="eye" id="eye-icon" class="w-3 h-3"></i>
                    </button>`;
                    
    const newHtml = `<div class="absolute right-1.5 top-1.5 flex items-center gap-1">
                        <button onclick="toggleKeyVisibility()" class="text-slate-400 hover:text-slate-600" title="Toggle visibility">
                            <i data-lucide="eye" id="eye-icon" class="w-3 h-3"></i>
                        </button>
                        <button onclick="clearSavedApiKey()" class="text-slate-400 hover:text-rose-600" title="Clear key">
                            <i data-lucide="trash-2" class="w-3 h-3"></i>
                        </button>
                    </div>`;
                    
    content = content.replace(oldHtml, newHtml);
    
    fs.writeFileSync(filePath, content, 'utf-8');
    console.log('Added trash icon ' + filePath);
}

addTrashIcon('./app/src/main/assets/index.html');
addTrashIcon('./index.html');
addTrashIcon('./public/index.html');
