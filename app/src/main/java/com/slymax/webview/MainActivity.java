private void removePanelsIfNotHome() {

    String js =
        "(function() {" +

        "if(window.location.pathname !== '/' && window.location.pathname !== '') {" +

        // Inject permanent CSS to hide panels
        "if(!document.getElementById('tv-mode-style')) {" +
        "   var style = document.createElement('style');" +
        "   style.id = 'tv-mode-style';" +
        "   style.innerHTML = '" +

        // Hide header
        "header, #topbar, .topbar, .header { display:none !important; }" +

        // Hide sidebar / tree / nav
        "#sidebar, #tree, .sidebar, .tree, nav { display:none !important; }" +

        // Expand main content to full width
        "main, .content, #content { width:100% !important; margin:0 !important; }" +

        "';" +
        "   document.head.appendChild(style);" +
        "}" +

        "}" +

        // Force focus into file list
        "setTimeout(function(){" +
        "   var first = document.querySelector('.item a, tr td.name a');" +
        "   if(first) {" +
        "       first.focus();" +
        "       first.scrollIntoView({block:'center'});" +
        "   }" +
        "}, 200);" +

        // Fullscreen poster image
        "if(document.images.length === 1) {" +
        "   var img = document.images[0];" +
        "   document.body.style.margin='0';" +
        "   img.style.height='100vh';" +
        "   img.style.width='auto';" +
        "   img.style.display='block';" +
        "   img.style.margin='0 auto';" +
        "}" +

        "})();";

    webView.evaluateJavascript(js, null);
}
