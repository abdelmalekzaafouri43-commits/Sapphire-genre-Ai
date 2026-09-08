const fs = require('fs');
const content = fs.readFileSync('index.html', 'utf-8');
const openDivs = (content.match(/<div/g) || []).length;
const closeDivs = (content.match(/<\/div/g) || []).length;
console.log(`Open Divs: ${openDivs}`);
console.log(`Close Divs: ${closeDivs}`);
