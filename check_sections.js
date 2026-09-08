const fs = require('fs');
const content = fs.readFileSync('index.html', 'utf-8');
const open = (content.match(/<section/g) || []).length;
const close = (content.match(/<\/section>/g) || []).length;
console.log(`Open Sections: ${open}`);
console.log(`Close Sections: ${close}`);
