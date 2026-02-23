private void injectGalleryScript() {

    String js =
    "(function waitForList() {" +

    "let list = document.querySelector('#items');" +
    "let folders = document.querySelectorAll('#items li.item.folder');" +

    "if(!list || folders.length === 0) {" +
    "setTimeout(waitForList, 300);" +
    "return;" +
    "}" +

    // If page contains video files, DO NOT transform
    "let videos = document.querySelectorAll('#items li.item.file a[href$=\".mp4\"], #items li.item.file a[href$=\".mkv\"], #items li.item.file a[href$=\".m4v\"]');" +
    "if(videos.length > 0) return;" +

    // Transform into grid
    "list.style.display='grid';" +
    "list.style.gridTemplateColumns='repeat(auto-fill,minmax(160px,1fr))';" +
    "list.style.gap='18px';" +
    "list.style.padding='20px';" +
    "list.style.listStyle='none';" +

    "folders.forEach(function(folder) {" +

    "let link = folder.querySelector('a');" +
    "if(!link) return;" +

    "let folderUrl = link.href;" +
    "let folderName = link.textContent.trim();" +

    "folder.innerHTML='';" +
    "folder.style.textAlign='center';" +

    "let img = document.createElement('img');" +
    "img.style.width='100%';" +
    "img.style.height='240px';" +
    "img.style.objectFit='cover';" +
    "img.style.borderRadius='10px';" +
    "img.style.background='#222';" +

    // Placeholder first
    "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
    "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
    "<rect width=\"100%\" height=\"100%\" fill=\"#222\"/>" +
    "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#666\" font-size=\"18\">Loading...</text>" +
    "</svg>');" +

    // Try direct poster guess first (fast path)
    "let testImg = new Image();" +
    "testImg.onload=function(){ img.src=this.src; };" +
    "testImg.onerror=function(){" +

        // If direct guess fails, fetch folder HTML once
        "fetch(folderUrl)" +
        ".then(r=>r.text())" +
        ".then(html=>{" +
        "let parser=new DOMParser();" +
        "let doc=parser.parseFromString(html,'text/html');" +
        "let jpg=doc.querySelector('a[href$=\".jpg\"],a[href$=\".JPG\"]');" +
        "if(jpg){" +
        "img.src=folderUrl + jpg.getAttribute('href');" +
        "}else{" +
        "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
        "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
        "<rect width=\"100%\" height=\"100%\" fill=\"#222\"/>" +
        "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#666\" font-size=\"18\">No Poster</text>" +
        "</svg>');" +
        "}" +
        "});" +
    "};" +

    "testImg.src=folderUrl + 'a_AL_.jpg';" +

    "let title=document.createElement('div');" +
    "title.textContent=folderName;" +
    "title.style.color='white';" +
    "title.style.marginTop='8px';" +
    "title.style.fontSize='14px';" +

    "folder.appendChild(img);" +
    "folder.appendChild(title);" +

    "folder.setAttribute('tabindex','0');" +
    "folder.onclick=function(){ window.location.href=folderUrl; };" +
    "folder.onkeydown=function(e){ if(e.key==='Enter'){ window.location.href=folderUrl; } };" +

    "});" +

    "document.body.style.background='#111';" +

    "})();";

    webView.evaluateJavascript(js, null);
}
