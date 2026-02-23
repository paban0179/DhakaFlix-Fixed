private void injectGalleryScript() {

    String js =
    "(async function waitForList() {" +

    "let list = document.querySelector('#items');" +
    "let folders = document.querySelectorAll('#items li.item.folder');" +

    "if(!list || folders.length === 0) {" +
    "setTimeout(waitForList, 300);" +
    "return;" +
    "}" +

    // Determine if THIS page is movie-folder level
    "let isMovieLevel = false;" +

    "for (let folder of folders) {" +
    "let link = folder.querySelector('a');" +
    "if(!link) continue;" +

    "try {" +
    "let res = await fetch(link.href);" +
    "let html = await res.text();" +
    "if(html.match(/\\.mp4|\\.mkv|\\.m4v/i)) {" +
    "isMovieLevel = true;" +
    "break;" +
    "}" +
    "} catch(e) {}" +
    "}" +

    "if(!isMovieLevel) return;" +

    // Convert layout to vertical poster grid
    "list.style.display='grid';" +
    "list.style.gridTemplateColumns='repeat(auto-fill,minmax(180px,1fr))';" +
    "list.style.gap='24px';" +
    "list.style.padding='24px';" +
    "list.style.listStyle='none';" +

    "for (let folder of folders) {" +

    "let link = folder.querySelector('a');" +
    "if(!link) continue;" +

    "let folderUrl = link.href;" +
    "let folderName = link.textContent.trim();" +

    "if(!folderName) {" +
    "folderName = folderUrl.split('/').filter(Boolean).pop();" +
    "}" +

    "folder.innerHTML='';" +
    "folder.style.textAlign='center';" +

    "let img = document.createElement('img');" +
    "img.style.width='100%';" +
    "img.style.aspectRatio='2/3';" +
    "img.style.objectFit='contain';" +
    "img.style.borderRadius='12px';" +
    "img.style.background='#111';" +

    // Default placeholder
    "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
    "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
    "<rect width=\"100%\" height=\"100%\" fill=\"#111\"/>" +
    "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#555\" font-size=\"18\">Loading...</text>" +
    "</svg>');" +

    // Fetch folder to get first JPG
    "try {" +
    "let res = await fetch(folderUrl);" +
    "let html = await res.text();" +
    "let parser = new DOMParser();" +
    "let doc = parser.parseFromString(html,'text/html');" +
    "let jpg = doc.querySelector('a[href$=\".jpg\"],a[href$=\".JPG\"]');" +
    "if(jpg) {" +
    "img.src = folderUrl + jpg.getAttribute('href');" +
    "} else {" +
    "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
    "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
    "<rect width=\"100%\" height=\"100%\" fill=\"#111\"/>" +
    "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#555\" font-size=\"18\">No Poster</text>" +
    "</svg>');" +
    "}" +
    "} catch(e) {" +
    "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
    "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
    "<rect width=\"100%\" height=\"100%\" fill=\"#111\"/>" +
    "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#555\" font-size=\"18\">No Poster</text>" +
    "</svg>');" +
    "}" +

    "let title=document.createElement('div');" +
    "title.textContent=folderName;" +
    "title.style.color='white';" +
    "title.style.marginTop='10px';" +
    "title.style.fontSize='14px';" +
    "title.style.wordBreak='break-word';" +

    "folder.appendChild(img);" +
    "folder.appendChild(title);" +

    "folder.setAttribute('tabindex','0');" +
    "folder.onclick=function(){ window.location.href=folderUrl; };" +
    "folder.onkeydown=function(e){ if(e.key==='Enter'){ window.location.href=folderUrl; } };" +

    "}" +

    "document.body.style.background='#000';" +

    "})();";

    webView.evaluateJavascript(js, null);
}
